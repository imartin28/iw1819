package es.ucm.fdi.iw.control;

import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.ucm.fdi.iw.model.CGroup;
import es.ucm.fdi.iw.model.User;

@Controller()
@RequestMapping("group")
public class GroupController {

	
	@Autowired
	private EntityManager entityManager;
	
	@GetMapping("/")
	public String groups(Model model, HttpSession session) {
		List<CGroup> groups =  entityManager.createNamedQuery("readAllGroupsOfUser", CGroup.class).setParameter("userId", session.getAttribute("u") ).getResultList();
		model.addAttribute("groups", groups);
		return "groups";
	}
	
	
	
	@PostMapping("/newGroup")
	@Transactional
	public String postNewGroup(Model model, HttpSession session, @RequestParam("groupName") String name) {
		
		CGroup group = new CGroup(name);
		
		group.getUsers().add((User) session.getAttribute("u"));
		
		entityManager.persist(group);
		
		

		return "redirect:/group/";
	}
	
}
