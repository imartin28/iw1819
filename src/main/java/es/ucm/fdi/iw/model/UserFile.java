package es.ucm.fdi.iw.model;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

@Entity
public class UserFile {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
	private User user;
	
	
	@ManyToOne
	private CFile file;
	
	private String permission;
	
	public UserFile(User user, CFile file, String permission ){
		
		this.user = user;
		this.file = file;
		this.permission = permission;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public CFile getFile() {
		return file;
	}

	public void setFile(CFile file) {
		this.file = file;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}
}
