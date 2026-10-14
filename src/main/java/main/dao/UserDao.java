package main.dao;;

import main.entities.User;
import javax.persistence.EntityManager;
import java.util.List;

public class UserDao extends GenericDao<User> {

    public UserDao(EntityManager em) {
        super(User.class, em);
    }

    public List<User> findBySalleNom(String nomSalle) {
        return em.createQuery("SELECT u FROM User u WHERE u.salle.nom = :nomSalle", User.class)
                .setParameter("nomSalle", nomSalle)
                .getResultList();
    }

    public List<User> findByNomContient(String motif) {
        return em.createQuery(
                        "SELECT u FROM User u WHERE LOWER(u.name) LIKE :pattern", User.class)
                .setParameter("pattern", "%" + motif.toLowerCase() + "%")
                .getResultList();
    }
}
