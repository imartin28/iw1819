package es.ucm.fdi.iw.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.ucm.fdi.iw.service.FriendService;

@Service
public class FriendServiceImpl implements FriendService {

	@Autowired
	private EntityManager entityManager;
	
	public List<Long> getFriendIdsFromUser(Long userLoggedId) {
		
		List<Object[]> friendshipObjIds = entityManager.createNamedQuery("readFriendshipIdsOfUser", Object[].class).setParameter("userId", userLoggedId).getResultList();
		List<Long> friendshipIds = new ArrayList<Long>();
		
		for(Object[] obj : friendshipObjIds) {
			Long firstUserId = ((Number)obj[0]).longValue();
			Long secondUserId = ((Number)obj[1]).longValue();
			if(firstUserId != null && !friendshipIds.contains(firstUserId))
				friendshipIds.add(firstUserId);
			if(secondUserId != null && !friendshipIds.contains(secondUserId))
				friendshipIds.add(secondUserId);
		}
		
		return friendshipIds;
	}
	
}
