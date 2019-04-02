package es.ucm.fdi.iw.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

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
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
import es.ucm.fdi.iw.util.JSONUtil;
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
	
	


	
	
	
	
	@GetMapping("/{id}")
	public ModelAndView getFile(ModelAndView modelAndView, HttpSession session, @PathVariable("id") Long fileId) throws IOException {
		
		String err = "File not found";
		
		CFile file = fileService.findById(fileId);
		User currentUser = (User) session.getAttribute("u");
		
		if (file != null && currentUser != null) {
			err = null;
			modelAndView.addObject("filename", file.getName());
			
			String url = "/file/user" + currentUser.getId() + "/" + fileId;
			modelAndView.addObject("fileurl", url);
			
			
			String mimetype = file.getMimetype().split("/")[0];
			
			if(mimetype != null && !mimetype.equalsIgnoreCase("")) {
				modelAndView.addObject("mimetype", mimetype);
			}
			else {
				err = "File has invalid MIME type";
			}
			
			modelAndView.addObject("tags", file.tagNameList());
			
			try {
				JSONObject metadataJSON = new JSONObject(file.getMetadata());
				Map<String, Object> metadataMapObject = JSONUtil.toMap(metadataJSON);
				modelAndView.addObject("metadata", metadataMapObject);
			} catch(JSONException e) {
				e.printStackTrace();
			}
		}

		String viewName = "redirect:/user/";
		if (err == null)
			viewName = "file";
		
		modelAndView.setViewName(viewName);
		
		return modelAndView;
	}
	
	
	@PostMapping("/{id}")
	@Transactional
	public String postFile(@RequestParam("file") MultipartFile file, @PathVariable("id") Long id, Model model, HttpSession session) {
		
		try {
		User target = userService.findById(id);
		model.addAttribute("user", target);
		
		User requester = (User) session.getAttribute("u");
		if (requester.getId() != target.getId()) {
			return "user";
		}
		
		
		
		log.info("Uploading photo for user {}", id);
		
		
		
		/* Comprobar que existen el directorio del usuario y el fichero, y crearlos en caso contrario */
		
		File f = null;
		File folder = localData.getFolder("user" + id);
		String metadata = "{\", \"size\" : \"" + file.getSize() + "\"}";
		CFile fileToPersist = new CFile(file.getOriginalFilename(), folder.getAbsolutePath(), metadata);			
		entityManager.persist(fileToPersist);
		
		
		fileToPersist.setPath(fileToPersist.getPath() + "/" + fileToPersist.getId());
		
		String mimetype = file.getContentType();
		fileToPersist.setMimetype(mimetype);
		f = new File(folder.getAbsolutePath() + "/" + fileToPersist.getId());
		
		try {
			if (f.createNewFile()) {
		        System.out.println("File is created!");
			} else {
		        System.out.println("File already exists.");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		

		
		if (file.isEmpty()) {
			log.info("failed to upload file : empty file?");
		} else {
			try {
				FileOutputStream f1 = new FileOutputStream(f);
				byte[] bytes = file.getBytes();
				f1.write(bytes);
				f1.close();
			
				User currentUser = (User) session.getAttribute("u");
				
				UserFile userFile = new UserFile(currentUser, fileToPersist, "rw");
				entityManager.persist(userFile);				
				
				entityManager.flush();
				
				log.info("Succesfully uploaded file for user {} into {}", id, f.getAbsolutePath());
			} catch (Exception e) {
				System.out.println("Error uploading file of user " + id + " " + e);
			}
		}
		
		}catch(Exception e) {
			log.warn("ERROR DESCONOCIDO" , e);
		}
		return "redirect:/user/";
	}
	
	
	
	

	@RequestMapping(value = "/download/{id}", method=RequestMethod.GET)
	public ResponseEntity<InputStreamResource> downloadFile(@PathVariable Long id) throws IOException {
		
		CFile file = fileService.findById(id);
        MediaType mediaType = MediaTypeUtils.getMediaTypeForFileName(this.servletContext, file.getName());
  
 
        File f = new File(file.getPath() + "/" + file.getId());
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
	
	
	
	@PostMapping("/modifyFileName")
	@Transactional
	public String postModifyFileName(@RequestParam("idFile") Long id, @RequestParam("fileName") String name) {
		
		CFile file = fileService.findById(id);	
		file.setName(name);
		return "redirect:/user/";
	}
	

	@PostMapping("/deleteFile")
	@Transactional
	public String postDeleteFile(@RequestParam("idFile") Long id) {
		CFile file = fileService.findById(id);		
		entityManager.remove(file);
		
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
	public String postDeleteTag(@RequestParam("idTag") Long tagId, HttpSession session) {
		
		entityManager.createQuery("DELETE FROM CFile_Tags WHERE tags_id = :tagId").setParameter("tagId", tagId).executeUpdate();
		Tag tag = (Tag) entityManager.createNamedQuery("findById", Tag.class).setParameter("id", tagId).getSingleResult();		
		entityManager.remove(tag);
		
		return "redirect:/user/";
	}
	
	
}
