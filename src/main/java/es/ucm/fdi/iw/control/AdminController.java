package es.ucm.fdi.iw.control;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import es.ucm.fdi.iw.model.CFile;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.UserType;
import es.ucm.fdi.iw.service.FileService;
import es.ucm.fdi.iw.service.UserService;
import es.ucm.fdi.iw.transfer.UserTransfer;
import es.ucm.fdi.iw.util.StringUtil;

@Controller()
@RequestMapping("admin")
public class AdminController {
	
	private static final Logger log = LogManager.getLogger(AdminController.class);
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private UserService userService;

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
	public String index(ModelAndView modelAndView, HttpSession session) {
		return "redirect:/admin/users";
	}
	
	@GetMapping("/users")
	public ModelAndView usersGet(ModelAndView modelAndView) {
		List<User> users = userService.getAll();

		modelAndView.addObject("users", users);
		modelAndView.setViewName("users");
		return modelAndView;
	}
	
	@PostMapping("/users")
	public @ResponseBody String usersPost(ModelAndView modelAndView, HttpServletResponse response, @RequestBody String userIdsAndStates) {
		String err = "";
		
		JSONObject userIdsAndStatesJSON = null;
		try {
			userIdsAndStatesJSON = new JSONObject(userIdsAndStates);
			
			if(userIdsAndStates != null) {
				Iterator<String> keys = userIdsAndStatesJSON.keys();
				
				while(keys.hasNext()) {
					String errUserId = "";
					
					String key = keys.next();
					Long userId = Long.parseLong(key.replace("user", ""));
					
					if(userId != null) {
						User user = userService.findById(userId);
						
						if(user != null) {
							Boolean active = Boolean.parseBoolean((String)userIdsAndStatesJSON.get(key));
							if(active != null && (active != user.isActive())) {
								user.setActive(active);
								user = userService.save(user);
							}
						}
						else
							errUserId += "User with id: "+userId+", not found";
					}
					else {
						errUserId += "Error with user id";
					}
					
					err += errUserId + '\n';
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			err = "Error while parsing ids";
		}

		err = err.trim();
		if(err != null && !err.equalsIgnoreCase("")) {
			this.notifyModal(modelAndView, "Error", err);
		}
		else {
			err = "";
		}
		
		return "{ \"err\": \""+err+"\" }";
	}
	
	@GetMapping("/files")
	public ModelAndView filesGet(ModelAndView modelAndView) {
		List<CFile> files = fileService.getAll();

		modelAndView.addObject("files", files);
		modelAndView.setViewName("files");
		return modelAndView;
	}
	
	@GetMapping("/addAdmin")
	public ModelAndView addAdminGet(ModelAndView modelAndView) {
		modelAndView.setViewName("addAdmin");
		UserTransfer user = new UserTransfer();
		user.setType(UserType.Administrator.getKeyName());
		modelAndView.addObject("user", user);
		return modelAndView;
	}
	
}
