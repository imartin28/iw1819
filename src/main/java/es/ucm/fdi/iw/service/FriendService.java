package es.ucm.fdi.iw.service;

import java.util.List;

public interface FriendService {
	
	List<Long> getFriendIdsFromUser(Long userLoggedId);

}
