package es.ucm.fdi.iw.serializer;

import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.transfer.UserTransfer;
import es.ucm.fdi.iw.util.DateUtil;


public class UserSerializer {
	
	public static User userTransferToDomainObj(UserTransfer usertr) {
		User user = null;
		
		if(usertr == null)
			return user;
		
		if(usertr.getBirthdate() == null && usertr.getBirthdateStr() != null) {
			usertr.setBirthdate(DateUtil.getDateWithoutHour(usertr.getBirthdateStr()));
		}
		/*
		user = new User(
			usertr.getEmail(),
			usertr.getNickname(),
			usertr.getName(), 
			usertr.getLastName(),
			usertr.getBirthdate(), 
			usertr.getDescription()
		);
			*/
		user = new User();
		user.setEmail(usertr.getEmail());
		user.setNickname(usertr.getNickname());
		user.setName(usertr.getName());
		user.setLastName(usertr.getLastName());
		user.setBirthdate(usertr.getBirthdate());
		user.setDescription(usertr.getDescription());
		user.setPassword(usertr.getPassword());
		
		if(user != null && usertr.getId() > 0) {
			user.setId(usertr.getId());
		}
		
		return user;
	}

	public static UserTransfer domainObjToUserTransfer(User userobj) {
		UserTransfer usertr = null;
		
		if(userobj == null)
			return usertr;
		
		usertr = new UserTransfer(
			userobj.getEmail(),
			userobj.getNickname(),
			userobj.getName(),
			userobj.getLastName(),
			userobj.getBirthdate(),
			userobj.getDescription()
		);
		usertr.setPassword(userobj.getPassword());
		
		if(usertr != null && userobj.getId() > 0) {
			usertr.setId(userobj.getId());
		}
		
		return usertr;
	}

}
