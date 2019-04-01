package es.ucm.fdi.iw.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import es.ucm.fdi.iw.LocalData;
import es.ucm.fdi.iw.model.CFile;
import es.ucm.fdi.iw.model.FileType;
import es.ucm.fdi.iw.model.Tag;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.UserFile;
import es.ucm.fdi.iw.service.FileService;
import es.ucm.fdi.iw.service.UserService;
import es.ucm.fdi.iw.util.MediaTypeUtils;
import es.ucm.fdi.iw.util.StringUtil;

@Controller()
@RequestMapping("file")
public class FileController {
	
	private static final Logger log = LogManager.getLogger(FileController.class);

	private static final String APPLICATION_PDF = "application/pdf";
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private EntityManager entityManager;
	
	 
    @Autowired
    private ServletContext servletContext;
	
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
	
	

	/*@RequestMapping(value = "/download/{id}", method=RequestMethod.GET)
	@ResponseBody
	public FileSystemResource downloadFile(@PathVariable Long id, HttpServletResponse response) {
		CFile file = fileService.findById(id);
		
		//response.setContentType(APPLICATION_PDF);
		response.setHeader("Content-Disposition", "inline; filename=" + file.getName());
		
	    return new FileSystemResource(new File(file.getPath()));
	}*/
	
	
	@RequestMapping(value = "/download/{id}", method=RequestMethod.GET)
	public ResponseEntity<InputStreamResource> downloadFile(@PathVariable Long id) throws IOException {
		
		CFile file = fileService.findById(id);
        MediaType mediaType = MediaTypeUtils.getMediaTypeForFileName(this.servletContext, file.getName());
        System.out.println("fileName: " + file.getName());
        System.out.println("mediaType: " + mediaType);
 
        File f = new File(file.getPath());
        InputStreamResource resource = new InputStreamResource(new FileInputStream(f));
 
        return ResponseEntity.ok()
                // Content-Disposition
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
                // Content-Type
                .contentType(mediaType)
                // Contet-Length
                .contentLength(f.length()) //
                .body(resource);
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
	
	
	@PostMapping("/modifyFileName")
	@Transactional
	public String postModifyFileName(@RequestParam("idFile") Long id, @RequestParam("fileName") String name) {
		
		
		CFile file = fileService.findById(id);	
		
		
		file.setName(name);
		
		return "redirect:/user/";
	}
	
	
	@PostMapping("/deleteFiles")
	@Transactional
	@ResponseBody
	public String deleteFiles(@RequestBody List<Long> ids, HttpServletResponse response) {
		List<CFile> files = entityManager.createNamedQuery("findAllById", CFile.class)
				.setParameter("ids", ids)
				.getResultList();
		
		
		response.setStatus(200);
		// Hay que borrar los ficheros del disco duro
		
		fileService.deleteFiles(files);
		return "{}";
	}
	
	
	
	
	
	
	@PostMapping("/nestFileInTag")
	@Transactional
	public String nestFileInTag( @RequestParam("id_tag") Long idTag, @RequestParam("id_file") Long idFile) {
		
		CFile file = fileService.findById(idFile);
		
		Tag tag = (Tag) entityManager.createNamedQuery("findById", Tag.class).setParameter("id", idTag).getSingleResult();
		
	
		file.getTags().add(tag);
		
		
		
		
		return "redirect:/user/";
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
	/*public ModelAndView postTag(Model model, HttpSession session, @RequestParam("tagName") String name, @RequestParam("tagColor") String color, @RequestParam("parentId") Long parentId, BindingResult bindingResult) {
		
		ModelAndView modelAndView = null;
		Tag tagWithSameName = entityManager.createNamedQuery("findByName", Tag.class).setParameter("name", name).getSingleResult();
	    if(tagWithSameName == null) {
	    	Tag parentTag = null;
 			if(parentId != null) {
 				parentTag = (Tag) entityManager.createNamedQuery("findById", Tag.class).setParameter("id", parentId).getSingleResult();
 			}
 			Tag tag = new Tag(name, color, parentTag, (User)session.getAttribute("u"));
 		
 			entityManager.persist(tag);
 			entityManager.flush();
 			
 			modelAndView = new ModelAndView("redirect:/user/");
	     } else {
	    	 bindingResult.rejectValue("tag_name", "Another tag with that name already exists");
	    	 
	    	 modelAndView = new ModelAndView("index", bindingResult.getModel());
	    	 
	     }
		
		
		
		
		return modelAndView;
	}*/
	
	
	
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
