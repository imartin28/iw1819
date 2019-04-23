package es.ucm.fdi.iw.integration;


import java.util.List;
import es.ucm.fdi.iw.model.Friend;


public interface DAOFriend {
	public List<Friend> getFriendsOfUser(Long userId);
}
