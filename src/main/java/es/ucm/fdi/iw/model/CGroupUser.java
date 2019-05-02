package es.ucm.fdi.iw.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class CGroupUser {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne(targetEntity=User.class)
	private User user;
	
	
	@ManyToOne(targetEntity=CGroup.class)
	private CGroup group;
	
	private String permission;
	
	public CGroupUser() {}
		
	
	
	public CGroupUser(User user, CGroup group, String permission ){
		
		this.user = user;
		this.group = group;
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

	public CGroup getGroup() {
		return group;
	}

	public void setGroup(CGroup group) {
		this.group = group;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}
}