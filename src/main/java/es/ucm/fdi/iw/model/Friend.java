package es.ucm.fdi.iw.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

@Entity
@NamedQueries({
	@NamedQuery(name="readFriendshipsOfUser", query="SELECT f "
			+ " FROM Friend f "
			+ " WHERE f.firstUser.id = :userId OR f.secondUser.id = :userId"),
	@NamedQuery(name="readFriendshipIdsOfUser", query="SELECT f.firstUser.id, f.secondUser.id "
			+ " FROM Friend f "
			+ " WHERE f.firstUser.id = :userId OR f.secondUser.id = :userId"),
	@NamedQuery(name="readFriendship", query="SELECT f "
			+ " FROM Friend f "
			+ " WHERE f.firstUser.id = :firstUserId AND f.secondUser.id = :secondUserId"
			+ "	OR f.secondUser.id = :firstUserId AND f.firstUser.id = :secondUserId"),
})
public class Friend {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@OneToOne(targetEntity=User.class)
	private User firstUser;
	
	@OneToOne(targetEntity=User.class)
	private User secondUser;
	
	private boolean accepted;
	
	private String message;
	
	public Friend() { }

	public Friend(User firstUser, User secondUser, String message) {
		this.firstUser = firstUser;
		this.secondUser = secondUser;
		this.message = message;
		this.accepted = false;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public User getFirstUser() {
		return firstUser;
	}

	public void setFirstUser(User firstUser) {
		this.firstUser = firstUser;
	}
	
	public User getSecondUser() {
		return secondUser;
	}

	public void setSecondUser(User secondUser) {
		this.secondUser = secondUser;
	}

	public boolean isAccepted() {
		return accepted;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
