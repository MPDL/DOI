package de.mpg.mpdl.doi.view;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import de.mpg.mpdl.doi.rest.JerseyApplicationConfig;
import de.mpg.mpdl.doi.security.DoxiUser;

public class ViewUserDB {

	public List<DoxiUser> userList;
	
	
	public ViewUserDB()
	{
		EntityManager manager = JerseyApplicationConfig.emf.createEntityManager();
		TypedQuery<DoxiUser> query = manager.createQuery("select u from users u", DoxiUser.class);
		
		setUserList(query.getResultList());
		manager.close();
	}

	public List<DoxiUser> getUserList() {
		return userList;
	}

	public void setUserList(List<DoxiUser> userList) {
		this.userList = userList;
	}
	
}
