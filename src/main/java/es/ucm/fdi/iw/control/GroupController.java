package es.ucm.fdi.iw.control;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.ucm.fdi.iw.model.CFile;
import es.ucm.fdi.iw.model.CGroup;
import es.ucm.fdi.iw.model.CGroupUser;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.transfer.GroupTransfer;
import es.ucm.fdi.iw.transfer.UserTransfer;
import es.ucm.fdi.iw.util.PostChangePermissionInfo;
import es.ucm.fdi.iw.util.PostDeleteMemberInfo;

@Controller()
@RequestMapping("groups")
public class GroupController {

	
	@Autowired
	private EntityManager entityManager;
	
	@GetMapping("/")
	public String groups(Model model, HttpSession session) {
		Long userId = ((User) session.getAttribute("u")).getId();
		List<CGroup> groups =  entityManager.createNamedQuery("readAllGroupsOfUser", CGroup.class).setParameter("userId", userId).getResultList();
		model.addAttribute("groups", groups);
		return "groups";
	}
	
	@GetMapping("/{groupId}")
	public String group(Model model, HttpSession session, @PathVariable Long groupId) {
		
		CGroup group = entityManager.createNamedQuery("findGroupById", CGroup.class).setParameter("id", groupId).getSingleResult();
		User user = ((User) session.getAttribute("u"));
		List<User> friends = entityManager.createNamedQuery("readFriendsOfUser", User.class).setParameter("userId", user.getId()).getResultList();
		
		String userLoggedPermission = group.getUsers().get(group.getUsers().indexOf(new CGroupUser(user, group, ""))).getPermission();
					
		List<User> friendsNotInGroup = new ArrayList<>();
		for (User friend : friends) {
			if (!group.getUsers().contains(new CGroupUser(friend, group, ""))) {
				friendsNotInGroup.add(friend);
			}
		}
		
		model.addAttribute("group", group);
		model.addAttribute("friendsNotInGroup", friendsNotInGroup);
		model.addAttribute("userLoggedPermission", userLoggedPermission);
		
		return "group";
		
	}
	
	
	
	
	@PostMapping("/newGroup")
	@Transactional
	public String postNewGroup(Model model, HttpSession session, @RequestParam("groupName") String name) {
		User user = (User) session.getAttribute("u");
		CGroup group = new CGroup(name);
		CGroupUser groupUser = new CGroupUser(user, group, "admin");
		
		group.getUsers().add(groupUser);
		
		entityManager.persist(group);
		entityManager.persist(groupUser);
		return "redirect:/groups/";
	}
	
	@PostMapping("/modifyGroup")
	@Transactional
	public String modifyGroup(@RequestParam("groupName") String name, @RequestParam("groupId") Long id) {
		
		
		CGroup group = entityManager.createNamedQuery("findGroupById", CGroup.class).setParameter("id", id).getSingleResult();
		
		group.setName(name);
		
		return "redirect:/groups/";
	}
	
	
	
	@PostMapping("/deleteGroups")
	@Transactional
	@ResponseBody
	public String deleteGroups(@RequestBody List<Long> ids, HttpServletResponse response) {
		List<CGroup> groups = entityManager.createNamedQuery("findAllGroupsById", CGroup.class).setParameter("ids", ids)
				.getResultList();

		for (CGroup group : groups) {
			entityManager.remove(group);
		}

		response.setStatus(200);
		
		return "{}";
	}
	
	
	@PostMapping("/addMembers")
	@Transactional
	public String addMember(@RequestBody List<Long> idsOfUsers, HttpServletResponse response) {
		//se saca el ultimo elemento de la lista, que contiene el id del grupo.
		Long idGroup = idsOfUsers.get(idsOfUsers.size()-1);
		CGroup group = entityManager.createNamedQuery("findGroupById", CGroup.class).setParameter("id", idGroup).getSingleResult();
		for(int i = 0; i < idsOfUsers.size() - 1; ++i) {
			User user = entityManager.createNamedQuery("findUserById", User.class).setParameter("idUser", idsOfUsers.get(i)).getSingleResult();
			CGroupUser cgroupUser = new CGroupUser(user, group, "user");
			entityManager.persist(cgroupUser);
		}
		
		response.setStatus(200);
		
		return "{}";
		
	}
	
	@PostMapping("/deleteMember")
	@Transactional
	public @ResponseBody String deleteMember(@RequestBody PostDeleteMemberInfo postInfo, HttpServletResponse response) {
		CGroupUser cGroupUser = entityManager.createNamedQuery("findCGroupUserByUserIdAndGroupId", CGroupUser.class).
				setParameter("userId", postInfo.getUserId()).
				setParameter("groupId", postInfo.getGroupId()).
				getSingleResult();
		
		
		
		// Si el miembro a borrar es el último del grupo entonces se elimina 
		// de la base de datos también el grupo
		if (cGroupUser.getGroup().getUsers().size() == 1) {
			entityManager.remove(cGroupUser.getGroup());
		} else if (cGroupUser.getGroup().getUsers().size() == 2) {
			// Si sólo se queda un usuario en el grupo se le pone como admin
			for (CGroupUser cGroupUser2 : cGroupUser.getGroup().getUsers()) {
				if (cGroupUser2.getId() != cGroupUser.getId()) {
					cGroupUser2.setPermission("admin");
				}
			}
		}
		
		entityManager.remove(cGroupUser);
		response.setStatus(200);
		return "Todo correcto";
	}
	
	@PostMapping("/changePermission")
	@Transactional
	public @ResponseBody String changePermission(@RequestBody PostChangePermissionInfo postInfo, HttpServletResponse response) {
		CGroupUser cGroupUser = entityManager.createNamedQuery("findCGroupUserByUserIdAndGroupId", CGroupUser.class).
				setParameter("userId", postInfo.getUserId()).
				setParameter("groupId", postInfo.getGroupId()).
				getSingleResult();
		
		cGroupUser.setPermission(postInfo.getPermission());
		response.setStatus(200);
		return "Todo correcto";
	}
	
	
	
	
	@GetMapping("/viewMembers/{groupId}")
	public @ResponseBody List<UserTransfer> members(Model model, HttpSession session, @PathVariable Long groupId) {
		
		CGroup group = entityManager.createNamedQuery("findGroupById", CGroup.class).setParameter("id", groupId).getSingleResult();
		
		List<CGroupUser> listCGroupUser = group.getUsers();
		
		List<UserTransfer> listaMembers = new ArrayList<>();
		
		for(CGroupUser user : listCGroupUser) {
			listaMembers.add(new UserTransfer(user.getUser()));
		}
		
		
		return listaMembers;
		
	}
	
	
	@GetMapping("/searchGroup")
	public @ResponseBody List<GroupTransfer> searchGroup(Model model, HttpSession session, @RequestParam String groupName) {
		
		List<CGroup> groups = entityManager.createNamedQuery("findGroupByLetter", CGroup.class).setParameter("name", "%" + groupName + "%").getResultList(); 
		List<GroupTransfer> listGroups = new ArrayList<>();
		
		if(groups.size() > 0) {
			for(CGroup group : groups) {
				listGroups.add(new GroupTransfer(group));
			}
		}else {
			Long userId = ((User) session.getAttribute("u")).getId();
			List<CGroup> allGroups = entityManager.createNamedQuery("readAllGroupsOfUser", CGroup.class).setParameter("userId", userId).getResultList();
			for(CGroup group : allGroups) {
				listGroups.add(new GroupTransfer(group));
			}
		}
		
		
		return listGroups;
	}
	
}
