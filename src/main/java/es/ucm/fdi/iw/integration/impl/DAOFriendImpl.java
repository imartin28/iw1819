package es.ucm.fdi.iw.integration.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import es.ucm.fdi.iw.model.Friend;

@Repository
public class DAOFriendImpl {
	protected EntityManager entityManager;
    
    public EntityManager getEntityManager() {
        return entityManager;
    }
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
	
	@Transactional
	public List<Friend> getFriendsOfUser(Long userId) {

		List<Friend> friends = getEntityManager().createNamedQuery("readFriendsOfUser", Friend.class).setParameter("userId", userId).getResultList();
	
		
		return friends;
	}
}
