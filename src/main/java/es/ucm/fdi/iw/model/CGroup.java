package es.ucm.fdi.iw.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;


@Entity
@NamedQueries({
@NamedQuery(name="readAllGroupsOfUser", query="SELECT g "
		+ " FROM CGroup g" 
		+ " WHERE g.id IN (SELECT cgu.group.id "
		+ " FROM CGroupUser cgu"
		+ " WHERE cgu.group.id = g.id AND cgu.user.id = :userId)"),
@NamedQuery(name="findAllGroupsById", query="SELECT group "
		+ " FROM CGroup group"
		+ " WHERE group.id IN :ids "),
@NamedQuery(name="findGroupById", query="SELECT group"
		+ " FROM CGroup group"
		+ " WHERE group.id = :id "),
@NamedQuery(name="findGroupByLetter", query="SELECT group"
		+ " FROM CGroup group"
		+ " WHERE group.name LIKE :name" )

})
public class CGroup {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	private String name;
	
	
	@OneToMany(targetEntity=CGroupUser.class, mappedBy="group", cascade=CascadeType.REMOVE)
	private List<CGroupUser> users;

	@ManyToMany(targetEntity=CFile.class)
	private List<CFile> files;

	@OneToMany(targetEntity=Message.class, mappedBy="groupReceiver")
	private List<Message> messages;
	
	public CGroup() {}

	
	public CGroup(String name) {
		this.name = name;
		this.users = new ArrayList<>();
		this.files = new ArrayList<>();
		this.messages = new ArrayList<>();

	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

	public List<CFile> getFiles() {
		return files;
	}

	public void setFiles(List<CFile> files) {
		this.files = files;
	}


	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public List<CGroupUser> getUsers() {
		return users;
	}


	public void setUsers(List<CGroupUser> users) {
		this.users = users;
	}
	

}
