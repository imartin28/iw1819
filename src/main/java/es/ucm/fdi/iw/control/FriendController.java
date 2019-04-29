package es.ucm.fdi.iw.control;

import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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
	
	
	@Autowired
	private EntityManager entityManager;
	
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
	@Transactional
	public ModelAndView addFriendRequest(ModelAndView modelAndView, HttpSession session, 
			@ModelAttribute ("userId") Long userId, @ModelAttribute ("message") String message) {
		String err = "User not found";
		User friendRequestUser = userService.findById(userId);
		User userLogged = (User) session.getAttribute("u");
		
		err = null;
		
		List<User> friends = entityManager.createNamedQuery("readFriendsOfUser", User.class).setParameter("userId", userId).getResultList();
		List<Friend> friendships = entityManager.createNamedQuery("readFriendship", Friend.class)
				.setParameter("firstUserId", userId).setParameter("secondUserId", friendRequestUser.getId())
				.getResultList();
		Friend friendship = friendships.isEmpty() ? null : friendships.get(0);
		
		if(!usersAreAlreadyFriends(friendRequestUser, friends, friendship)) {
			Friend friend = new Friend(userLogged, friendRequestUser, message);
			entityManager.persist(friend);
		} else {
			if(friendship.isAccepted())
				err = "The user " + friendRequestUser.getNickname() + "is already your friend";
			else
				err = "You have already sent a friend request to " + friendRequestUser.getNickname() +" before, please wait for an answer";
		}
		
		
		if(err != null) {
			modelAndView.setViewName("redirect:/");
			this.notifyModal(modelAndView, "Error", err);
		}
		else if(friendRequestUser != null){
			modelAndView.setViewName("redirect:/user/profile");
			modelAndView.addObject("userId", userId);
			this.notifyModal(modelAndView, "Friend request sent", "Yo have sent a friend request to user " + friendRequestUser.getNickname());
		}
		
		return modelAndView;
	}
	
	@PostMapping("/resolveFriendRequest")
	@Transactional
	public ModelAndView resolveFriendRequest(ModelAndView modelAndView, HttpSession session, 
			@ModelAttribute ("friendUserId") Long userId, @ModelAttribute ("accept") String accept) {
		String err = "User not found";
		User userLogged = null;
		User friendRequestUser = null;
		
		userLogged = (User)session.getAttribute("u");
		
		if(userLogged != null && userLogged.getId() != userId) {
			friendRequestUser = userService.findById(userId);
			
			if(friendRequestUser != null && friendRequestUser.isActive()) {
				err = null;
				List<User> friends = entityManager.createNamedQuery("readFriendsOfUser", User.class)
						.setParameter("userId", friendRequestUser.getId()).getResultList();
				List<Friend> friendships = entityManager.createNamedQuery("readFriendshipsOfUser", Friend.class)
						.setParameter("userId", friendRequestUser.getId()).getResultList();
				Friend friendship = friendships.isEmpty() ? null : friendships.get(0);
				
				if(!usersAreAlreadyFriends(friendRequestUser, friends, friendship)) {
					if(accept != null && !accept.isEmpty() && accept.equalsIgnoreCase("true"))
						friendship.setAccepted(true);
					else
						entityManager.remove(friendship);
				}
				else
					err = "The user "+friendRequestUser.getNickname()+" is already your friend";
			}
		}
		
		modelAndView.setViewName("redirect:/user/friends");
		
		if(err != null) {
			this.notifyModal(modelAndView, "Error", err);
		}
		
		return modelAndView;
	}
	
	@PostMapping("/removeFriend")
	@Transactional
	public ModelAndView addFriend(ModelAndView modelAndView, HttpSession session, @ModelAttribute ("userId") Long userId) {
		String err = "User not found";
		User userLogged = null;
		User friendRequestUser = null;
		
		if(userId != null) {
			userLogged = (User)session.getAttribute("u");
			
			if(userLogged != null && userLogged.getId() != userId) {
				friendRequestUser = userService.findById(userId);
				
				if(friendRequestUser != null && friendRequestUser.isActive()) {
					User userLoggedDatabase = userService.findById(userLogged.getId());
					
					if(userLoggedDatabase != null && userLoggedDatabase.isActive()) {
						err = null;
						
						List<User> friends = entityManager.createNamedQuery("readFriendsOfUser", User.class)
								.setParameter("userId", userLogged.getId()).getResultList();
						List<Friend> friendships = entityManager.createNamedQuery("readFriendshipsOfUser", Friend.class)
								.setParameter("userId", friendRequestUser.getId()).getResultList();
						Friend friendship = friendships.isEmpty() ? null : friendships.get(0);
						
						if(usersAreAlreadyFriends(friendRequestUser, friends, friendship)) {
							entityManager.remove(friendship);
						}
						else
							err = "The user "+friendRequestUser.getNickname()+" is not your friend";
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
			List<Friend> friendships = entityManager.createNamedQuery("readFriendshipsOfUser", Friend.class).setParameter("userId", userLogged.getId()).getResultList();
			modelAndView.addObject("friends", friendships);
		}
		modelAndView.setViewName("friends");
		
		return modelAndView;
	}
	
	private boolean usersAreAlreadyFriends(User secondUser, List<User> friendsOfFirstUser, Friend friendship) {
		
		boolean found = false;
		int i = 0;
		
		if(friendsOfFirstUser != null && friendsOfFirstUser.size() > 0 && friendship != null) {
			// Check if they are already friends
			while(!found && i < friendsOfFirstUser.size()) {
				found = friendsOfFirstUser.get(i).getId() == secondUser.getId() && friendship.isAccepted();
				i++;
			}
		}
		
		return found;
	}

}


