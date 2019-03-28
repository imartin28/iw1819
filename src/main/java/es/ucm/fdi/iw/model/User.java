package es.ucm.fdi.iw.model;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

@Entity
@NamedQueries({
	@NamedQuery(name="User.byEmailOrNicknameAndActive",
			query="SELECT u FROM User u "
					+ "WHERE (u.email = :userLogin OR u.nickname = :userLogin) AND u.active = 1"),
	@NamedQuery(name="User.byEmailOrNickname",
	query="SELECT u FROM User u "
			+ "WHERE (u.email = :userLogin OR u.nickname = :userLogin)"),
	@NamedQuery(name="User.byEmailAndPassword",
	query="SELECT u FROM User u "
			+ "WHERE (u.email = :userEmail AND u.password = :userPassword AND u.active = 1)"),
	
	//Admin
	@NamedQuery(name="User.listAdmin",
	query="SELECT u FROM User u "
			+ "WHERE (u.active = 1 AND LOCATE(u.roles, 'admin') > 0)")
})
public class User {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	@Column(unique=true)
	private String nickname;
	
	private String name;
	
	private String lastName;
	
	@Column(unique=true)
	private String email;
	
	private String password;
	
	private Date birthdate;

	private String description;
	
	private boolean active;
	
	private String roles;
	
	@ManyToOne(targetEntity=User.class)
	private List<Friend> friends;
	
	@OneToMany(targetEntity=Notification.class, mappedBy="user")
	private List<Notification> notifications;
	
	
	@OneToMany(targetEntity=UserFile.class, mappedBy="user")
	private List<UserFile> files;
	
	
	@ManyToMany(targetEntity=CGroup.class, mappedBy="users")
	private List<CGroup> groups;
	
	
	@OneToMany(targetEntity=Message.class, mappedBy="sender")
	private List<Message> sentMessages;
	
	@OneToMany(targetEntity=Message.class, mappedBy="receiver")
	private List<Message> receivedMessages;

	@OneToMany(targetEntity=Tag.class, mappedBy="user")
	private List<Tag> tags;


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(Date birthdate) {
		this.birthdate = birthdate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<Friend> getFriends() {
		return friends;
	}

	public void setFriends(List<Friend> friends) {
		this.friends = friends;
	}

	public List<Notification> getNotifications() {
		return notifications;
	}

	public void setNotifications(List<Notification> notifications) {
		this.notifications = notifications;
	}

	public List<UserFile> getFiles() {
		return files;
	}

	public void setFiles(List<UserFile> files) {
		this.files = files;
	}

	public List<CGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<CGroup> groups) {
		this.groups = groups;
	}

	public List<Message> getSentMessages() {
		return sentMessages;
	}

	public void setSentMessages(List<Message> sentMessages) {
		this.sentMessages = sentMessages;
	}

	public List<Message> getReceivedMessages() {
		return receivedMessages;
	}

	public void setReceivedMessages(List<Message> receivedMessages) {
		this.receivedMessages = receivedMessages;
	}
	
	public String getRoles() {
		return this.roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}
	
	public boolean hasRole(String roleName) {
		if(roles == null)
			return false;
		return Arrays.stream(this.roles.split(","))
				.anyMatch(r -> r.equalsIgnoreCase(roleName));
	}
	
	public void addRole(String roleName) {
		this.roles += ','+roleName;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", email=" + email + ", nickname=" + nickname + ", name=" + name + ", lastName=" + lastName + 
					", password=" + password + ", birthdate=" + birthdate + ", description=" + description + ", roles=" + roles + ", active=" + active + "]";
	}
}