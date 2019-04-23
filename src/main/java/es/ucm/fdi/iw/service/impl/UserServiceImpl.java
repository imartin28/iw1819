package es.ucm.fdi.iw.service.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import es.ucm.fdi.iw.integration.UserRepository;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private EntityManager entityManager;
	
	@Transactional
    @Override
    public User create(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setActive(true);
		return userRepo.save(user);
	}
	
	@Transactional
    @Override
    public User delete(User user) {
		user.setActive(false);
		return userRepo.save(user);
	}
	
	@Override
	public User save(User user) {
        if(user != null)
        	user = userRepo.save(user);
        return user;
	}
	
	@Override
	public List<User> getAll() {
		return userRepo.findAll();
	}
	
	@Override
	public User findByEmail(String email) {
		return userRepo.findByEmail(email);
	}
	
	@Override
	public User findByNickname(String nickname) {
		return userRepo.findByNickname(nickname);
	}

	@Override
	public User findById(long id) {
		return userRepo.findById(id);
	}

	@Override
	public User findByEmailOrNickname(String email, String nickname) {
		User u = null;
		
		if(email != null && nickname != null && !email.equalsIgnoreCase("") && !nickname.equalsIgnoreCase("")) {
			u = entityManager.createNamedQuery("User.byEmailOrNickname", User.class)
	                .setParameter("email", email)
	                .setParameter("nickname", nickname)
	                .getSingleResult();
		}
		
		return u;
	}
	
	@Override
	public User findByEmailAndPasswordAndActive(String email, String password) {
		User u = null;
		
		if(email != null && password != null && !email.equalsIgnoreCase("") && !password.equalsIgnoreCase("")) {
			u = entityManager.createNamedQuery("User.byEmailAndPassword", User.class)
	                .setParameter("userEmail", email)
	                .setParameter("userPassword", password)
	                .getSingleResult();
		}
		
		return u;
	}
	
	@Override
	public List<User> findByEmailOrNicknameOrNameOrLastName(String searchText) {
		List<User> users = null;
		
		if(searchText != null && !searchText.isEmpty()) {
			searchText = searchText.toLowerCase().trim();
			users = entityManager.createNamedQuery("User.findUser", User.class)
					.setParameter(1, searchText)
					.setParameter(2, '%'+searchText+'%')
					.getResultList();
		}
		
		return users;
	}
	
	@Override
	public int adminCount() {
		return entityManager.createNamedQuery("User.listAdmin", User.class).getResultList().size();
	}
	
	
}
