package es.ucm.fdi.iw.control;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import es.ucm.fdi.iw.LocalData;
import es.ucm.fdi.iw.model.CFile;
import es.ucm.fdi.iw.model.Tag;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.UserFile;
import es.ucm.fdi.iw.model.UserType;
import es.ucm.fdi.iw.parser.UserParser;
import es.ucm.fdi.iw.serializer.UserSerializer;
import es.ucm.fdi.iw.service.FileService;
import es.ucm.fdi.iw.service.FriendService;
import es.ucm.fdi.iw.service.UserService;
import es.ucm.fdi.iw.transfer.UserTransfer;
import es.ucm.fdi.iw.util.DateUtil;
import es.ucm.fdi.iw.util.StringUtil;

@Controller()
@RequestMapping("user")
public class UserController {
	
	private static final Logger log = LogManager.getLogger(UserController.class);
	
	@Autowired
	private FileService fileService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private FriendService friendService;
	
	@Autowired
	private EntityManager entityManager;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private LocalData localData;
		
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
	public String index(Model model, HttpSession session) {	
		Long id_currentUser = ((User) session.getAttribute("u")).getId();
		List<CFile> files_currentUser = entityManager.createNamedQuery("FilesUser", CFile.class).setParameter("id_currentUser", id_currentUser).getResultList();
		List<Tag> tags = entityManager.createNamedQuery("readTagsByUser", Tag.class).setParameter("userId", id_currentUser).getResultList();
		
		System.out.println(files_currentUser);
		//String json = new Gson().toJson(files_currentUser);
		
		model.addAttribute("files", files_currentUser);
		model.addAttribute("tags", tags);
		model.addAttribute("parentTag", null);
		model.addAttribute("currentTag", null);
		
		if(!files_currentUser.isEmpty()) {
			System.out.println(files_currentUser.get(0).getMetadata());
		}

		return "index";
	}
	
	@GetMapping("/search")
	public ModelAndView searchUser(ModelAndView modelAndView, HttpSession session, HttpServletRequest request,
			@RequestParam("searchText") String searchText) {

		User userLogged = (User)session.getAttribute("u");
		
		if(searchText != null && !searchText.isEmpty()) {
			List<User> users = userService.findByEmailOrNicknameOrNameOrLastName(searchText);
			
			if(users != null && users.size() > 0) {
				modelAndView.addObject("users", users);
			}
			
			if(userLogged != null) {
				session.setAttribute("friendIds", friendService.getFriendIdsFromUser(userLogged.getId()));
			}
		}
		
		modelAndView.setViewName("results");
		return modelAndView;
	}
	
	@GetMapping("/profile")
	public ModelAndView profile(ModelAndView modelAndView, HttpSession session, SessionStatus status, @ModelAttribute ("userId") Long userId) {
		
		String err = "User not found";

		if(userId != null) {
			User user = userService.findById(userId);
			if(user != null && user.isActive()) {
				err = null;
				modelAndView.addObject("user", user);
				
				session.setAttribute("friendIds", friendService.getFriendIdsFromUser(userId));
			}
		}
		
		if(err != null) {
			this.notifyModal(modelAndView, "Error", err);
		}
		
		modelAndView.setViewName("profile");
		return modelAndView;
	}
	
	@GetMapping("/avatar/{id}")
	public StreamingResponseBody getFile(@PathVariable long id, Model model, HttpSession session) throws IOException {
		User user = userService.findById(id);
		String sha256 = user.getAvatar();
		
		File f = localData.getFile("files", sha256);
		InputStream in;
			
		in = new BufferedInputStream(new FileInputStream(f));

		return new StreamingResponseBody() {
			@Override
			public void writeTo(OutputStream os) throws IOException {
				FileCopyUtils.copy(in, os);
			}
		};
	}
	
	@PostMapping("/delete")
	public ModelAndView deleteUser(ModelAndView modelAndView, HttpSession session, SessionStatus status, @ModelAttribute ("userId") Long userId) {
		String err = null;
		String viewName = null;
		
		User userLogged = (User)session.getAttribute("u");
		
		//If the loggedUser is deactivating himself
		if(userLogged != null && userLogged.getId() == userId) {
			viewName = "redirect:/logout";
		}
		else {
			//If admin is deactivating a user
			if(userLogged != null && userLogged.hasRole(UserType.Administrator.getKeyName())) {
				viewName = "redirect:/admin/";
			}			
			//One user is deactivating another
			else {
				err = "You are not allowed to delete this user";
				viewName = "redirect:/user/";
			}
		}
		
		if(err == null) {
			err = "User not found";
			if(userId != null) {
				User user = userService.findById(userId);
				
				if(user != null) {
					if(user.isActive()) {
						boolean delete = true;
						
						if(user.hasRole(UserType.Administrator.getKeyName())) {
							int adminCount = userService.adminCount();
							if(adminCount <= 1) {
								delete = false;
								err = "You cant deactivate yourselve because you are the last admin";
							}								
						}
						
						if(delete) {
							user = userService.delete(user);
							err = null;
							String msg = "User "+user.getName()+" ("+user.getEmail()+")"+
											" with id: "+userId+", has been deactivated";
							this.notifyModal(modelAndView, "User notification", msg);
						}
					}
					else
						err = "The user with id: "+userId+" is already deactivated";
				}
			}
		}

		if(err != null) {
			modelAndView.setViewName("redirect:/");
			this.notifyModal(modelAndView, "Error", err);
		}
		else {
			modelAndView.setViewName(viewName);
		}
		
		return modelAndView;
	}
	
	@PostMapping("/activate")
	public ModelAndView activateUser(ModelAndView modelAndView, HttpSession session, @ModelAttribute ("userId") Long userId) {
		String err = null;
		String viewName = null;
		
		User userLogged = (User)session.getAttribute("u");
		
		if(userLogged != null && userLogged.hasRole(UserType.Administrator.getKeyName())) {
			viewName = "redirect:/admin/";
		}
		else {
			err = "You are not allowed to activate this user";
			viewName = "redirect:/";
		}
		
		if(err == null) {
			err = "User not found";
			if(userId != null) {
				User user = userService.findById(userId);
				if(user != null) {
					if(!user.isActive()) {
						user.setActive(true);
						user = userService.save(user);
						if(user != null && user.isActive()) {
							err = null;
							String msg = "User "+user.getName()+" ("+user.getEmail()+")"+
											" with id: "+userId+", has been activated";
							this.notifyModal(modelAndView, "User notification", msg);
						}
					}
					else {
						err = "The user with id: "+userId+" is already activated";
					}
				}
			}
		}

		if(err != null) {
			this.notifyModal(modelAndView, "Error", err);
		}
		else {
			modelAndView.setViewName(viewName);
		}
		
		return modelAndView;
	}
	
	@GetMapping("/modifyProfile")
	public ModelAndView modifyProfileGet(ModelAndView modelAndView, HttpSession session, SessionStatus status, @ModelAttribute ("userId") Long userId) {
		User user = null;
		String err = "User not found";
		
		if(userId != null) {
			User userLogged = (User)session.getAttribute("u");
			
			if(userLogged != null && userLogged.getId() == userId) {
				user = userService.findById(userId);
				if(user != null && user.isActive()) {
					err = null;
					modelAndView.addObject("user", UserSerializer.domainObjToUserTransfer(user));
				}
			}
			else {
				err= "You dont have the persimission to modify this user";
			}
		}
		
		if(err != null) {
			this.notifyModal(modelAndView, "Error", err);
			modelAndView.setViewName("profile");
		}
		else {
			modelAndView.addObject("user", user);
			modelAndView.addObject("userTransfer", new UserTransfer());
			modelAndView.setViewName("modifyProfile");
		}
		
		return modelAndView;
	}
	
	@PostMapping("/modifyProfile")
	public ModelAndView modifyProfilePost(ModelAndView modelAndView, HttpSession session, @ModelAttribute ("userTransfer") UserTransfer userTransfer)  {
		String err = "User not found";

		User userDatabase = null;
		User userLogged = (User)session.getAttribute("u");
		
		if(userTransfer != null) {
			if(userLogged != null && userLogged.getId() == userTransfer.getId()) {
				userDatabase = userService.findById(userTransfer.getId());
				if(userDatabase != null && userDatabase.isActive()) {
					
					if(UserParser.getInstance().parseUserModify(modelAndView, userTransfer)) {
						
						boolean emailCorrect = true;
						
						if(!userDatabase.getEmail().equalsIgnoreCase(userTransfer.getEmail())) {
							User userSameEmail = userService.findByEmail(userTransfer.getEmail());
							
							emailCorrect = (userSameEmail == null || (userSameEmail.getId() != userTransfer.getId() && !userSameEmail.getEmail().equalsIgnoreCase(userTransfer.getEmail())));
							//users with same emails and different ids
							if(!emailCorrect) {
								err = "The email "+userTransfer.getEmail()+" is already registered";
							}
						}
						
						if(emailCorrect) {
							userDatabase.setEmail(userTransfer.getEmail());
							userDatabase.setName(userTransfer.getName());
							userDatabase.setLastName(userTransfer.getLastName());
							userDatabase.setBirthdate(DateUtil.getDateWithoutHour(userTransfer.getBirthdateStr()));
							userDatabase.setDescription(userTransfer.getDescription());
							
							User userSaved = userService.save(userDatabase);
							if(userSaved != null) {
								err = null;
							}
						}
					}
					else {
						err = "";
					}
				}
			}
			else {
				err= "You dont have the persimission to modify this user";
			}
		}

		if(err != null) {
			modelAndView.setViewName("modifyProfile");
			modelAndView.addObject("user", UserSerializer.userTransferToDomainObj(userTransfer));
			modelAndView.addObject("userTransfer", userTransfer);
			modelAndView.addObject("userId", userTransfer.getId());
			this.notifyModal(modelAndView, "Error", err);
		}
		else {
			Long userId = null;
			if(userDatabase != null) {
				userId = userDatabase.getId();
				modelAndView.setViewName("redirect:/user/profile");
				modelAndView.addObject("userId", userId);
			}
		}
		
		return modelAndView;
	}
	
	@PostMapping("/modifyAvatar")
	@Transactional
	public ModelAndView modifyAvatar(ModelAndView modelAndView, HttpSession session,
			@RequestParam("id") Long id, @RequestParam("avatar") String avatar,
			@RequestParam MultipartFile file) {
		
		User user = userService.findById(id);
		User userLogged = (User) session.getAttribute("u");
		String err = null;
		
		String sha256 = "custom-avatar-" + id;
		List<CFile> current =  fileService.findAllBysha256(sha256);
		
		if (!current.isEmpty()) {
			for(Tag tag : current.get(0).getTags())
				tag.getFiles().remove(current.get(0));
			
			entityManager.remove(current.get(0));
		}
		
		if (user != null && user.isActive()) {
			if (!avatar.equals("")) {
				user.setAvatar(avatar);
				userService.save(user);
				userLogged.setAvatar(avatar);
				session.setAttribute("u", userLogged);
			}
			else {
				log.info("Uploading file for user {}", user.getId());
				File folder = localData.getFolder("files");
				
				if (file.getContentType().contains("image/")) {
					CFile fileToPersist = new CFile(sha256, file.getOriginalFilename(), file.getSize(), file.getContentType());
					String path = fileToPersist.getSha256() + "." + fileToPersist.getExtension();
					
					fileToPersist.setPath(folder.getAbsolutePath() + "/" + path);
					entityManager.persist(fileToPersist);
					
					UserFile userFile = new UserFile(user, fileToPersist, "rw");
					entityManager.persist(userFile);
					
					uploadAvatar(sha256, file, fileToPersist, user);
					
					user.setAvatar(path);
					userService.save(user);
					userLogged.setAvatar(path);
					session.setAttribute("u", userLogged);
				}
				else
					err = "Selected file is not an image";
			}
		}
		else
			err = "User not found";
		
		modelAndView.setViewName("profile");
		modelAndView.addObject("userId", id);
		modelAndView.addObject("user", user);
		modelAndView.addObject("userTransfer", user);
		
		if(err != null)
			this.notifyModal(modelAndView, "Error", err);
		else
			this.notifyModal(modelAndView, "Avatar changed", "You have successfully update your avatar");
		
		return modelAndView;
	}
	
	private void uploadAvatar(String sha256, MultipartFile file, CFile fileToPersist, User user) {
		try {
			
			File f = new File(fileToPersist.getPath());

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
					// Guardar fichero en el disco
					InputStream fileStream = file.getInputStream();
					FileUtils.copyInputStreamToFile(fileStream, f);

					// Guardar datos del fichero en la base de datos

					log.info("Succesfully uploaded file for user {} into {}", user.getId(), f.getAbsolutePath());
				} catch (Exception e) {
					System.out.println("Error uploading file of user " + user.getId() + " " + e);
				}
			}

		} catch (Exception e) {
			log.warn("ERROR DESCONOCIDO", e);
		}

	}
	
	@PostMapping("/modifyPassword")
	public ModelAndView modifyPassword(ModelAndView modelAndView, HttpSession session, @ModelAttribute ("userTransfer") UserTransfer userTransfer) {
		User user = null;
		String err = "User not found";
		User userLogged = null;
		
		if(userTransfer != null) {
			userLogged = (User)session.getAttribute("u");
			
			if(userLogged != null && userLogged.getId() == userTransfer.getId()) {
				User userDatabase = userService.findById(userTransfer.getId());
				
				if(userDatabase != null && userDatabase.isActive()) {
					if(UserParser.getInstance().parseUserModifyPassword(modelAndView, userTransfer)) {
						String oldPassword = passwordEncoder.encode(userTransfer.getPassword());
						String newPassword = passwordEncoder.encode(userTransfer.getSamePassword());
						
						if(userDatabase.getPassword().equalsIgnoreCase(oldPassword)) {
							if(!userDatabase.getPassword().equalsIgnoreCase(newPassword)) {
								err = null;
								userDatabase.setPassword(newPassword);
								userService.save(user);
							}
							else
								err = "The new password must be different from the old one";
						}
						else
							err = "Incorrect password";
					}
					else
						err = "";
				}
			}
			else
				err= "You dont have the persimission to modify this user";
		}
		
		if(err != null) {
			modelAndView.setViewName("modifyProfile");
			modelAndView.addObject("user", userLogged);
			modelAndView.addObject("userTransfer", userTransfer);
			modelAndView.addObject("userId", userLogged.getId());
			this.notifyModal(modelAndView, "Error", err);
		}
		else {
			modelAndView.setViewName("redirect:/user/profile");
			modelAndView.addObject("userId", userTransfer.getId());
			this.notifyModal(modelAndView, "Password changed", "You have successfully update your password");
		}
		
		return modelAndView;
	}
	
	@GetMapping("/showContent/{tagId}")
	public String showContentOfTag(Model model, @PathVariable long tagId,
			HttpSession session) {
		Tag tag = entityManager.createNamedQuery("findById", Tag.class).setParameter("id", tagId)
				.getSingleResult();
		Long userId = ((User) session.getAttribute("u")).getId();
		List<Tag> tags = entityManager.createNamedQuery("readTagsByUser", Tag.class).setParameter("userId", userId).getResultList();
		LinkedList<Tag> tagsHierarchy = new LinkedList<>();
		
		model.addAttribute("files", tag.getFiles());
		model.addAttribute("tags", tags);
		model.addAttribute("currentTag", tag);
		
		// Leer todos los tags que estén por encima en la jerarquía
		while (tag.getParent() != null) {
			tagsHierarchy.addFirst(tag);
			tag = entityManager.createNamedQuery("readParentTag", Tag.class).setParameter("parentId", tag.getParent().getId())
					.getSingleResult();
		}
		
		
		model.addAttribute("parentTag", tag);
		model.addAttribute("tagsHierarchy", tagsHierarchy);
		
		return "index";
		
	}
	
	@GetMapping("/history")
	public String history(Model model) {
		return "history";
	}
	
}
