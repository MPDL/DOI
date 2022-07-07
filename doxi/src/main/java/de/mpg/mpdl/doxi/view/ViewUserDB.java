package de.mpg.mpdl.doxi.view;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import de.mpg.mpdl.doxi.security.DoxiUser;
import de.mpg.mpdl.doxi.util.EMF;

public class ViewUserDB {

  public List<DoxiUser> userList;

  public ViewUserDB() {
    EntityManager em = EMF.emf.createEntityManager();
    TypedQuery<DoxiUser> query = em.createQuery("select u from users u", DoxiUser.class);

    setUserList(query.getResultList());
    
    em.close();
  }

  public List<DoxiUser> getUserList() {
    return userList;
  }

  public void setUserList(List<DoxiUser> userList) {
    this.userList = userList;
  }
}
