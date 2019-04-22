package es.ucm.fdi.iw.control;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import es.ucm.fdi.iw.model.Friend;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.service.UserService;
import es.ucm.fdi.iw.util.StringUtil;

@Controller()
@RequestMapping("user")
public class FriendController {
	
	private static final Logger log = LogManager.getLogger(FriendController.class);
	
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
	
	
	@PostMapping("/addFriendRequest")
	public ModelAndView addFriendRequest(ModelAndView modelAndView, HttpSession session, 
			@ModelAttribute ("userId") String userId, @ModelAttribute ("message") String message) {
		String err = "User not found";
		User userLogged = null;
		User friendRequestUser = null;
		
		if(userId != null && !userId.isEmpty()) {
			Long userIdLong = Long.valueOf(userId);
			
			if(userIdLong != null) {
				userLogged = (User)session.getAttribute("u");
				
				if(userLogged != null && userLogged.getId() != userIdLong) {
					friendRequestUser = userService.findById(userIdLong);
					
					if(friendRequestUser != null && friendRequestUser.isActive()) {
						User userLoggedDatabase = userService.findById(userLogged.getId());
						
						if(userLoggedDatabase != null && userLoggedDatabase.isActive()) {
							err = null;
							List<Friend> friends = friendRequestUser.getFriends();
							boolean found = false;
							int i = 0;
							
							if(friends == null) {
								friends = new ArrayList<Friend>();
							}
							else {
								while(i < friends.size()) {
									found = (friends.get(i).getTargetUser().getId() == userLoggedDatabase.getId());
									i++;
								}
							}
							
							if(!found) {
								friends.add(new Friend(userLoggedDatabase, message));
								friendRequestUser.setFriends(friends);
								
								friendRequestUser = userService.save(friendRequestUser);
							}
							else {
								if(friends.get(i-1).isAccepted())
									err = "The user "+friendRequestUser.getNickname()+"is already your friend";
								else
									err = "You sended a friend request to "+friendRequestUser.getNickname()+" before, please wait for an answer";
							}
						}
					}
				}
			}
		}
		
		if(err != null) {
			modelAndView.setViewName("redirect:/");
			this.notifyModal(modelAndView, "Error", err);
		}
		else if(friendRequestUser != null){
			modelAndView.setViewName("redirect:/user/profile");
			modelAndView.addObject("userId", userId);
			this.notifyModal(modelAndView, "Friend request sended", "Yo have send a friend request to user " + friendRequestUser.getNickname());
		}
		
		return modelAndView;
	}
	
	@PostMapping("/resolveFriendRequest")
	public ModelAndView resolveFriendRequest(ModelAndView modelAndView, HttpSession session, 
			@ModelAttribute ("friendUserId") String userId, @ModelAttribute ("accept") String accept) {
		String err = "User not found";
		User userLogged = null;
		User friendRequestUser = null;
		
		if(userId != null && !userId.isEmpty() && accept != null && !accept.isEmpty()) {
			Long userIdLong = Long.valueOf(userId);
			
			if(userIdLong != null) {
				userLogged = (User)session.getAttribute("u");
				
				if(userLogged != null && userLogged.getId() != userIdLong) {
					friendRequestUser = userService.findById(userIdLong);
					
					if(friendRequestUser != null && friendRequestUser.isActive()) {
						User userLoggedDatabase = userService.findById(userLogged.getId());
						
						if(userLoggedDatabase != null && userLoggedDatabase.isActive()) {
							err = null;
							List<Friend> friends = friendRequestUser.getFriends();
							boolean found = false;
							
							if(friends != null && friends.size() > 0) {
								int i = 0;
								while(i < friends.size()) {
									found = (friends.get(i).getTargetUser().getId() == userLoggedDatabase.getId());
									i++;
								}
								
								if(found) {
									if(accept.equalsIgnoreCase("true")) {
										friends.get(i-1).setAccepted(true);
										
										List<Friend> friendsUserLogged = userLoggedDatabase.getFriends();
										found = false;
										i = 0;
										while(i < friendsUserLogged.size()) {
											found = (friendsUserLogged.get(i).getTargetUser().getId() == friendRequestUser.getId());
											i++;
										}
										
										if(found) {
											friendsUserLogged.get(i-1).setAccepted(true);
											
											friendRequestUser.setFriends(friends);
											userLoggedDatabase.setFriends(friendsUserLogged);
											
											friendRequestUser = userService.save(friendRequestUser);
											userLoggedDatabase = userService.save(userLoggedDatabase);
										}
									}
									else if(accept.equalsIgnoreCase("false")) {
										friends.remove(i-1);
										friendRequestUser.setFriends(friends);
										friendRequestUser = userService.save(friendRequestUser);
									}
								}
								else
									err = "The user "+friendRequestUser.getNickname()+"is already your friend";
							}
						}
					}
				}
			}
		}
		
		modelAndView.setViewName("redirect:/user/friends");
		if(err != null) {
			this.notifyModal(modelAndView, "Error", err);
		}
		
		return modelAndView;
	}
	
	
	@PostMapping("/removeFriend")
	public ModelAndView addFriend(ModelAndView modelAndView, HttpSession session, @ModelAttribute ("userId") String userId) {
		String err = "User not found";
		User userLogged = null;
		User friendRequestUser = null;
		
		if(userId != null && !userId.isEmpty()) {
			Long userIdLong = Long.valueOf(userId);
			
			if(userIdLong != null) {
				userLogged = (User)session.getAttribute("u");
				
				if(userLogged != null && userLogged.getId() != userIdLong) {
					friendRequestUser = userService.findById(userIdLong);
					
					if(friendRequestUser != null && friendRequestUser.isActive()) {
						User userLoggedDatabase = userService.findById(userLogged.getId());
						
						if(userLoggedDatabase != null && userLoggedDatabase.isActive()) {
							err = null;
							List<Friend> friendsTargetUser = friendRequestUser.getFriends();
							List<Friend> friends = userLoggedDatabase.getFriends();
							
							if(friendsTargetUser != null && friends != null) {
								boolean found = false;
								int i = 0;
								while(i < friendsTargetUser.size()) {
									found = (friendsTargetUser.get(i).getTargetUser().getId() == userLoggedDatabase.getId());
									i++;
								}
								
								if(found) {
									int targetUserIndex = i-1;
									
									found = false;
									i = 0;
									while(i < friends.size()) {
										found = (friends.get(i).getTargetUser().getId() == friendRequestUser.getId());
										i++;
									}
									
									if(found) {
										friendsTargetUser.remove(targetUserIndex);
										friendRequestUser.setFriends(friendsTargetUser);
										
										friends.remove(i-1);
										userLoggedDatabase.setFriends(friends);
										
										friendRequestUser = userService.save(friendRequestUser);
										userLoggedDatabase = userService.save(userLoggedDatabase);
									}
									else
										err = "There was a error removing the user" + friendRequestUser.getNickname();
								}
								else
									err = "The user "+friendRequestUser.getNickname()+"is not your friend";
							}
						}
					}
				}
			}
		}
		
		modelAndView.setViewName("redirect:/user/friends");
		if(err != null) {
			this.notifyModal(modelAndView, "Error", err);
		}
		else if(friendRequestUser != null){
			this.notifyModal(modelAndView, "Friend removed", "You have removed " + friendRequestUser.getNickname() + " from your friends");
		}
		
		return modelAndView;
	}
	
	@GetMapping("/friends")
	public ModelAndView friends(ModelAndView modelAndView, HttpSession session) {
		
		User userLogged = (User)session.getAttribute("u");
		
		if(userLogged != null) {
			modelAndView.addObject("friends", userLogged.getFriends());
		}
		modelAndView.setViewName("friends");
		
		return modelAndView;
	}

}
