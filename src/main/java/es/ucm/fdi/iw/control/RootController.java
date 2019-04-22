package es.ucm.fdi.iw.control;

import javax.persistence.EntityManager;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.UserType;
import es.ucm.fdi.iw.parser.UserParser;
import es.ucm.fdi.iw.serializer.UserSerializer;
import es.ucm.fdi.iw.service.UserService;
import es.ucm.fdi.iw.transfer.UserTransfer;
import es.ucm.fdi.iw.util.StringUtil;


@Controller
public class RootController {
	
	private static final Logger log = LogManager.getLogger(RootController.class);
	
	@Autowired
	private ServletContext context;
	
	@Autowired
	private Environment env;
	
	@Autowired
	private IwSocketHandler iwSocketHandler;

	@Autowired 
	private EntityManager entityManager;
	
	@Autowired 
	private AuthenticationManager authenticationManager;
	
	@Autowired
	UserService userService;
	
	/**
	 * Function to notify the current user a message from server
	 * the modal is located on nav.html in order to display a message in any view
	 * @param model
	 * @param errorTitle
	 * @param errorMsg
	 */
	private void notifyModal(ModelAndView modelAndView, String title, String msg) {
		if(title != null && title != "" && msg != null && msg != "") {
			modelAndView.addObject("title", StringUtil.convertToTitleCaseSplitting(title));
			modelAndView.addObject("msg", msg);
		}
	}
	
	@GetMapping("/")
	public ModelAndView index(ModelAndView modelAndView, HttpSession session) {
		String viewName = "redirect:/login";
		User userLogged = (User)session.getAttribute("u");
		
		if(userLogged != null) {
			if(userLogged.hasRole(UserType.Administrator.getKeyName())) {
				viewName = "redirect:/admin/";
			}
			else {
				viewName = "redirect:/user/";
			}
		}
		modelAndView.setViewName(viewName);
		
		return modelAndView;
	}

	@GetMapping("/login")
	public ModelAndView login(ModelAndView modelAndView, 
			@RequestParam(value = "error", required = false) String error, 
			@RequestParam(value = "logout", required = false) String logout
	) {
		
		if(error != null) {
			modelAndView.addObject("error", true);
		}
		if(logout != null) {
			notifyModal(modelAndView, "logout", "You have been logged out");
		}
		
		modelAndView.setViewName("login");
		modelAndView.addObject("userRegister", new UserTransfer());
		
		return modelAndView;
	}
	
	@GetMapping("/logout")
	public String logoutPage (HttpServletRequest request, HttpServletResponse response) {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth != null){    
	        new SecurityContextLogoutHandler().logout(request, response, auth);
	    }
	    return "redirect:/login?logout";
	}
	
	@PostMapping("/register")
	public ModelAndView register(ModelAndView modelAndView, HttpSession session, HttpServletRequest request,
			@ModelAttribute ("userRegister") UserTransfer userTransfer) {
		String err = "Please fill the fields";

		User userLogged = (User)session.getAttribute("u");
		
		if(userTransfer != null) {
			err = null;
			if(UserParser.getInstance().parseUserRegister(modelAndView, userTransfer)) {
				User userEmail = userService.findByEmail(userTransfer.getEmail());
				User userNickname = userService.findByNickname(userTransfer.getNickname());
				if(userEmail == null && userNickname == null) {
					User user = UserSerializer.userTransferToDomainObj(userTransfer);
					
					boolean hasPermission = false;
					UserType userType = UserType.getUserType(userTransfer.getType());
					//Boolean debug = (Boolean)context.getAttribute("debug");
					if(userType != null) {
						hasPermission = (userType == UserType.Administrator && userLogged != null && userLogged.hasRole(UserType.Administrator.getKeyName()));
					}
					else {
						user.addRole("user");
						hasPermission = true;
					}
					
					if(hasPermission) {
						user = userService.create(user);
						if(user != null) {
							err = null;
							userTransfer.setId(user.getId());
							
							doAutoLogin(userTransfer.getEmail(), userTransfer.getPassword(), request);	  
					        log.info("Created & logged user {}, with ID {} and password {}", userTransfer.getEmail(), userTransfer.getId(), userTransfer.getPassword());
					        
					        // add 'u' session attribute
					        session.setAttribute("u", user);
							// add a 'ws' session variable
							session.setAttribute("ws", request.getRequestURL().toString()
									.replaceFirst("[^:]*", "ws")		// http[s]://... => ws://...
									.replaceFirst("/[^/]*$", "/ws"));
							
						}
						else {
							err = "Error while trying to create the user";
						}
					}
					else {
						err = "You have not the permission to register an andmin";
					}
				}
				else {
					err = "User with "+(userEmail != null ? "email " + userTransfer.getEmail() : "")+
							(userEmail != null && userNickname != null ? " and " : "")+
							(userNickname != null ? "nickname " + userTransfer.getNickname() : "")+
							" already exists";
				}
			}
		}

		if(err != null) {
			modelAndView.setViewName("login");
			modelAndView.addObject("user", userTransfer);
			this.notifyModal(modelAndView, "Error", err);
		}
		else {
			modelAndView.setViewName("redirect:/user/profile");
			modelAndView.addObject("userId", userTransfer.getId());
		}
		
		return modelAndView;
	}
	
	/**
	 * Non-interactive authentication; user and password must already exist
	 * @param username
	 * @param password
	 * @param request
	 */
	private void doAutoLogin(String username, String password, HttpServletRequest request) {
	    try {
	        // Must be called from request filtered by Spring Security, otherwise SecurityContextHolder is not updated
	        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
	        token.setDetails(new WebAuthenticationDetails(request));
	        Authentication authentication = authenticationManager.authenticate(token);
	        log.debug("Logging in with [{}]", authentication.getPrincipal());
	        SecurityContextHolder.getContext().setAuthentication(authentication);
	    } catch (Exception e) {
	        SecurityContextHolder.getContext().setAuthentication(null);
	        log.error("Failure in autoLogin", e);
	    }
	}
	
	@GetMapping("/chats")
	public String chats(Model model) {
		return "chats";
	}
	
	@GetMapping("/file")
	public String file(Model model) {
		return "file";
	}
	
	/*
	@GetMapping("/admin")
	public String admin(Model model, Principal principal) {
		model.addAttribute("activeProfiles", env.getActiveProfiles());
		model.addAttribute("basePath", env.getProperty("es.ucm.fdi.base-path"));
		
		log.info("let us all welcome this admin, {}", principal.getName());
		
		return "index";
	}
	
	@GetMapping("/chat")
	public String chat(Model model, HttpServletRequest request) {
		model.addAttribute("socketUrl", request.getRequestURL().toString()
				.replaceFirst("[^:]*", "ws")
				.replace("chat", "ws"));
		return "chat";
	}
	*/
}
