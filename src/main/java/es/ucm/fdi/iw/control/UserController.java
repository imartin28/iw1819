package es.ucm.fdi.iw.control;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import es.ucm.fdi.iw.LocalData;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.parser.UserParser;
import es.ucm.fdi.iw.serializer.UserSerializer;
import es.ucm.fdi.iw.service.UserService;
import es.ucm.fdi.iw.transfer.UserTransfer;
import es.ucm.fdi.iw.util.DateUtil;
import es.ucm.fdi.iw.util.StringUtil;

@Controller()
@RequestMapping("user")
public class UserController {
	
	private static final Logger log = LogManager.getLogger(UserController.class);
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private LocalData localData;
	
	
	
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
	public String index(ModelAndView modelAndView, HttpSession session/*, @RequestParam("nickname") String nickname*/) {
		//modelAndView.addObject("user", session.getAttribute("u"));
		
		return "index";
	}
	
	
	
	@PostMapping("/{id}/file")
	public String postFile(@RequestParam("file") MultipartFile file, @PathVariable("id") Long id, Model model, HttpSession session) {
		
		User target = userService.findById(id);
		model.addAttribute("user", target);
		
		User requester = (User) session.getAttribute("u");
		if (requester.getId() != target.getId()) {
			return "user";
		}
		
		log.info("Uploading photo for user {}", id);
		
		
		localData.getFolder("user" + id);  //crea la carpeta del usuario actual si no estaba creada
		
		File f = localData.getFile("user" + id, file.getName());
		
		if (file.isEmpty()) {
			log.info("failed to upload file : empty file?");
		} else {
			try {
				BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(f));
				byte[] bytes = file.getBytes();
				stream.write(bytes);
			} catch (Exception e) {
				System.out.println("Error uploading file of user " + id + " " + e);
			}
			
			log.info("Succesfully uploaded file for user {} into {}", id, f.getAbsolutePath());
		}
		
		
		return "user";
	}
	
	
	@GetMapping("/profile")
	public ModelAndView profile(ModelAndView modelAndView, HttpSession session, SessionStatus status, @ModelAttribute ("userId") Long userId) {
		
		String err = "User not found";

		if(userId != null) {
			User user = userService.findById(userId);
			if(user != null && user.isActive()) {
				err = null;
				modelAndView.addObject("user", user);
			}
		}
		
		if(err != null) {
			this.notifyModal(modelAndView, "Error", err);
		}
		
		modelAndView.setViewName("profile");
		return modelAndView;
	}
	
	@RequestMapping(value= "/delete", method = RequestMethod.POST)
	public ModelAndView deleteUser(ModelAndView modelAndView, HttpSession session, SessionStatus status, @ModelAttribute ("userId") Long userId) {
		String err = "User not found";

		if(userId != null) {
			User user = userService.findById(userId);
			if(user != null) {
				if(user.isActive()) {
					user = userService.delete(user);
					if(user != null && !user.isActive()) {
						err = null;
						String msg = "User "+user.getName()+" ("+user.getEmail()+")"+
										" with id: "+userId+", has been deactivated";
						this.notifyModal(modelAndView, "User notification", msg);
					}
				}
				else {
					err = "The user with id: "+userId+" is already deactivated";
				}
			}
		}

		if(err != null) {
			this.notifyModal(modelAndView, "Error", err);
		}
		//If redirect to users the modal wont be rendered
		modelAndView.setViewName("redirect:/");
		
		return modelAndView;
	}
	
	@GetMapping("/modifyProfile")
	public ModelAndView modifyProfileGet(ModelAndView modelAndView, HttpSession session, SessionStatus status, @ModelAttribute ("userId") Long userId) {
		
		String err = "User not found";

		User user = null;
		if(userId != null) {
			user = userService.findById(userId);
			if(user != null && user.isActive()) {
				err = null;
				modelAndView.addObject("user", UserSerializer.domainObjToUserTransfer(user));
			}
		}
		
		if(err != null) {
			this.notifyModal(modelAndView, "Error", err);
			modelAndView.setViewName("profile");
		}
		else {
			modelAndView.addObject("user", user);
			modelAndView.addObject("userTransfer", new UserTransfer());
			modelAndView.setViewName("modifyProfile");
		}
		
		return modelAndView;
	}
	
	@RequestMapping("/modifyProfile")
	public ModelAndView modifyProfilePost(ModelAndView modelAndView, HttpSession session, SessionStatus status, @ModelAttribute ("userTransfer") UserTransfer userTransfer)  {
		String err = "User not found";

		User userDatabase = null;
		if(userTransfer != null) {
			userDatabase = userService.findById(userTransfer.getId());
			if(userDatabase != null && userDatabase.isActive()) {
				
				if(UserParser.getInstance().parseUserModify(modelAndView, userTransfer)) {
					
					boolean emailCorrect = true;
					
					if(!userDatabase.getEmail().equalsIgnoreCase(userTransfer.getEmail())) {
						User userSameEmail = userService.findByEmail(userTransfer.getEmail());
						
						emailCorrect = (userSameEmail == null || (userSameEmail.getId() != userTransfer.getId() && !userSameEmail.getEmail().equalsIgnoreCase(userTransfer.getEmail())));
						//users with same emails and different ids
						if(!emailCorrect) {
							err = "The email "+userTransfer.getEmail()+" is already registered";
						}
					}
					
					if(emailCorrect) {
						userDatabase.setEmail(userTransfer.getEmail());
						userDatabase.setName(userTransfer.getName());
						userDatabase.setLastName(userTransfer.getLastName());
						userDatabase.setBirthdate(DateUtil.getDateWithoutHour(userTransfer.getBirthdateStr()));
						userDatabase.setDescription(userTransfer.getDescription());
						
						User userSaved = userService.save(userDatabase);
						if(userSaved != null) {
							err = null;
						}
					}
				}
				else {
					err = null;
				}
			}
		}

		if(err != null) {
			modelAndView.setViewName("modifyProfile");
			modelAndView.addObject("user", UserSerializer.userTransferToDomainObj(userTransfer));
			modelAndView.addObject("userTransfer", userTransfer);
			modelAndView.addObject("userId", userTransfer.getId());
			this.notifyModal(modelAndView, "Error", err);
		}
		else {
			Long userId = null;
			if(userDatabase != null) {
				userId = userDatabase.getId();
				modelAndView.setViewName("redirect:/user/profile");
				modelAndView.addObject("userId", userId);
			}
		}
		
		return modelAndView;
	}

}
