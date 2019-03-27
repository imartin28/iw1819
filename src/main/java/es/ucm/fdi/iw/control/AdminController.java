package es.ucm.fdi.iw.control;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.service.UserService;
import es.ucm.fdi.iw.transfer.UserTransfer;
import es.ucm.fdi.iw.util.StringUtil;

@Controller()
@RequestMapping("admin")
public class AdminController {
	
	private static final Logger log = LogManager.getLogger(AdminController.class);
	
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
	
	@GetMapping("/addAdmin")
	public ModelAndView addAdminGet(ModelAndView modelAndView) {
		modelAndView.setViewName("addAdmin");
		modelAndView.addObject("user", new UserTransfer());
		return modelAndView;
	}
	
	@PostMapping("/delete-users")
	public ModelAndView deleteUsers(ModelAndView modelAndView, HttpSession session, SessionStatus status, @RequestBody List<Long> userIdsToDelete) {
		String err = "";
		
		if(userIdsToDelete != null) {
			
			for(int i = 0; i < userIdsToDelete.size(); i++) {
				String errUserId = "";
				
				Long userId = userIdsToDelete.get(i);
				
				if(userId != null) {
					User user = userService.findById(userId);
	
					if(user != null) {
						if(user.isActive()) {
							user = userService.delete(user);
							if(user != null && !user.isActive()) {
								errUserId = "";
							}
						}
						else {
							errUserId = "The user with id: "+userId+" is already deactivated";
						}
					}
					else {
						errUserId += "User with id: "+userId+", not found";
					}
				}
				else {
					errUserId += "Error with user id";
				}
				
				err += errUserId + '\n';
			}
		}
		
		err = err.trim();

		modelAndView.setViewName("redirect:/admin/");
		if(err != null && !err.equalsIgnoreCase("")) {
			this.notifyModal(modelAndView, "Error", err);
		}
		
		return modelAndView;
	}
	
}
