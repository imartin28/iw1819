package es.ucm.fdi.iw.control;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import es.ucm.fdi.iw.LocalData;
import es.ucm.fdi.iw.model.CFile;
import es.ucm.fdi.iw.model.FileType;
import es.ucm.fdi.iw.model.Tag;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.service.FileService;
import es.ucm.fdi.iw.util.StringUtil;

@Controller()
@RequestMapping("file")
public class FileController {
	
	private static final Logger log = LogManager.getLogger(FileController.class);
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private EntityManager entityManager;
	
	private LocalData localData = new LocalData();
	
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
		return "index";
	}
	
	@GetMapping("/{id}")
	public String getFile(ModelAndView modelAndView, HttpSession session, @PathVariable("id") Long id, @ModelAttribute ("userId") Long userId) throws IOException {
		
		String err = "File not found";
		
		CFile file = fileService.findById(id);
		
		if (file != null) {
			err = null;
			modelAndView.addObject("filename", id);
			
			String p = "/user" + userId + "/" + id;
			modelAndView.addObject("fileurl", p);
			
			Path path = Paths.get(p);
			String mimetype = Files.probeContentType(path);
			
			String type = FileType.getKeyName(mimetype);
			
			if(type != null) {
				modelAndView.addObject("mimetype", type);
			}
			else {
				err = "File has invalid MIME type";
			}
			
			modelAndView.addObject("tags", file.tagNameList());
			
			try {
				JSONObject metadataJSON = new JSONObject(file.getMetadata());
				modelAndView.addObject("metadata", metadataJSON);
			} catch(JSONException e) {
				e.printStackTrace();
			}
		}

		if (err != null)
			return "redirect:/user/";
		else return "file";
	}
	
	

	@PostMapping("/newTag")
	@Transactional
	public String postTag(Model model, HttpSession session, @RequestParam("tagName") String name, @RequestParam("parentId") Long parentId) {
		
		
		Tag parentTag = null;
		if(parentId != null) {
			parentTag = (Tag) entityManager.createNamedQuery("findById", Tag.class).setParameter("id", parentId).getSingleResult();
		}
		Tag tag = new Tag(name, null, parentTag, (User)session.getAttribute("u"));
		
		entityManager.persist(tag);
		entityManager.flush();
		
		
		return "redirect:/user/";
	}
	
	
	
	@PostMapping("/modifyTag")
	@Transactional
	public String postModifyTag(@RequestParam("colorTag") String color, @RequestParam("idTag") Long id, @RequestParam("tagName") String name) {
		
		
		
		Tag tag = (Tag) entityManager.createNamedQuery("findById", Tag.class).setParameter("id", id).getSingleResult();		
		tag.setColor(color);
		tag.setName(name);
		
		return "redirect:/user/";
	}
	
	

	@PostMapping("/deleteTag")
	@Transactional
	public String postDeleteTag(@RequestParam("idTag") Long id) {
		
		
		
		Tag tag = (Tag) entityManager.createNamedQuery("findById", Tag.class).setParameter("id", id).getSingleResult();		
		entityManager.remove(tag);
		
		return "redirect:/user/";
	}
	
}
