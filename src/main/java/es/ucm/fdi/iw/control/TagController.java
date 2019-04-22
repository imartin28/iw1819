package es.ucm.fdi.iw.control;

import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.ucm.fdi.iw.model.CFile;
import es.ucm.fdi.iw.model.Tag;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.service.FileService;
import es.ucm.fdi.iw.service.UserService;
import es.ucm.fdi.iw.util.PostDeleteTagFromFile;
import es.ucm.fdi.iw.util.PostTagFile;

@Controller()
@RequestMapping("tag")
public class TagController {

	@Autowired
	private FileService fileService;

	@Autowired
	private EntityManager entityManager;
	

	@GetMapping("/validateTagName")
	public @ResponseBody String validateTagName(@RequestParam String name, @RequestParam Long tagId,
			HttpSession session) {
		List<Tag> tags = entityManager.createNamedQuery("findByName", Tag.class).setParameter("name", name)
				.getResultList();
		Long userId = ((User) session.getAttribute("u")).getId();

		if (containsTagWithSameNameAndSameUserOrDifferentTagId(tags, name, tagId, userId)) {
			return "A tag with the name " + name + " already exists.";
		} else {
			return null;
		}
	}

	private boolean containsTagWithSameNameAndSameUserOrDifferentTagId(List<Tag> tags, String name, Long tagId,
			Long userId) {
		boolean found = false;
		Iterator<Tag> it = tags.iterator();

		while (!found && it.hasNext()) {
			Tag tag = it.next();

			if (tag.getName().equals(name) && tag.getUser().getId() == userId
					&& (tagId == null || tag.getId() != tagId)) {
				found = true;
			}
		}

		return found;
	}
	

	
	@RequestMapping(value = "/addTagsToFile", method = RequestMethod.POST,  consumes=MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public String addTagsToFile(@RequestBody PostTagFile postInfo, HttpServletResponse response) {
		CFile file = fileService.findById(postInfo.getFileId());
		
		for (Long tagId : postInfo.getTagsIds()) {
			Tag tag = (Tag) entityManager.createNamedQuery("findById", Tag.class).setParameter("id", tagId)
					.getSingleResult();
			
			tag.getFiles().add(file);
		}

		
		response.setStatus(200);
		return "redirect:/user/";
	}
	
	@PostMapping("/removeTagFromFile")
	@Transactional
	public String removeTagFromFile(@RequestBody PostDeleteTagFromFile postInfo) {
		CFile file = fileService.findById(postInfo.getFileId());
		Tag tag = (Tag) entityManager.createNamedQuery("findById", Tag.class).setParameter("id", postInfo.getTagId())
				.getSingleResult();
		
		tag.getFiles().remove(file);
		
		return "redirect:/user/";
	}

	@PostMapping("/newTag")
	@Transactional
	public String postTag(Model model, HttpSession session, @RequestParam("tagName") String name,
			@RequestParam("tagColor") String color, @RequestParam("parentId") Long parentId) {
		Tag parentTag = null;
		if (parentId != null) {
			parentTag = (Tag) entityManager.createNamedQuery("findById", Tag.class).setParameter("id", parentId)
					.getSingleResult();
		}
		Tag tag = new Tag(name.trim(), color, parentTag, (User) session.getAttribute("u"));

		entityManager.persist(tag);
		entityManager.flush();

		return "redirect:/user/";
	}

	@PostMapping("/modifyTag")
	@Transactional
	public String postModifyTag(@RequestParam("colorTag") String color, @RequestParam("idTag") Long id,
			@RequestParam("tagName") String name) {

		Tag tag = (Tag) entityManager.createNamedQuery("findById", Tag.class).setParameter("id", id).getSingleResult();
		tag.setColor(color);
		tag.setName(name);

		return "redirect:/user/";
	}

	@PostMapping("/deleteTag")
	@Transactional
	public String postDeleteTag(@RequestParam("idTag") Long tagId, HttpSession session) {

		Tag tag = (Tag) entityManager.createNamedQuery("findById", Tag.class).setParameter("id", tagId)
				.getSingleResult();
		tag.getFiles().removeAll(tag.getFiles());
		entityManager.remove(tag);

		return "redirect:/user/";
	}

}
