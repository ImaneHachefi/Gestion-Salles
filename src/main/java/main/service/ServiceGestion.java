package main.service;

import main.dao.SalleDao;
import main.dao.UserDao;
import main.entities.Salle;
import main.entities.User;
import main.exception.ServiceException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Map;

public class ServiceGestion {

    private final EntityManagerFactory emf;
    private final EntityManager em;
    private final SalleDao salleDao;
    private final UserDao userDao;

    public ServiceGestion() {
        this(null);
    }

    public ServiceGestion(Map<String, Object> properties) {
        try {
            emf = Persistence.createEntityManagerFactory("gestionPU", properties);
            em = emf.createEntityManager();
        } catch (PersistenceException e) {
            throw new ServiceException("Impossible d'initialiser la connexion à la base de données", e);
        }
        salleDao = new SalleDao(em);
        userDao = new UserDao(em);
    }


    public void fermer() {
        if (em != null && em.isOpen()) em.close();
        if (emf != null && emf.isOpen()) emf.close();
    }

    public void addSalle(Salle salle) {
        if (salle == null) {
            throw new IllegalArgumentException("La salle ne peut pas être nulle");
        }
        if (salle.getNom() == null || salle.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la salle est obligatoire");
        }
        if (salle.getCapacite() <= 0) {
            throw new IllegalArgumentException("La capacité doit être strictement positive");
        }
        try {
            salleDao.create(salle);
        } catch (PersistenceException e) {
            em.clear();
            throw new ServiceException("Échec de l'ajout de la salle '" + salle.getNom()
                    + "' (nom peut-être déjà utilisé)", e);
        }
    }

    public void addUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("L'utilisateur ne peut pas être nul");
        }
        if (user.getNom() == null || user.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'utilisateur est obligatoire");
        }
        if (user.getEmail() == null || !user.getEmail().contains("@")) {

            throw new IllegalArgumentException("Email invalide");
        }
        try {
            userDao.create(user);
        } catch (PersistenceException e) {
            em.clear();
            throw new ServiceException("Échec de l'ajout de l'utilisateur '" + user.getEmail()
                    + "' (email peut-être déjà utilisé)", e);
        }
    }

    public List<Salle> listerSalles() {
        try {
            return salleDao.findAll();
        } catch (PersistenceException e) {
            throw new ServiceException("Impossible de lister les salles", e);
        }
    }

    public List<User> listerUsers() {
        try {
            return userDao.findAll();
        } catch (PersistenceException e) {
            throw new ServiceException("Impossible de lister les utilisateurs", e);
        }
    }

    public List<User> usersParSalle(String nomSalle) {
        if (nomSalle == null || nomSalle.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la salle est obligatoire");
        }
        try {

            return userDao.findBySalleNom(nomSalle);
        } catch (PersistenceException e) {
            throw new ServiceException("Impossible de rechercher les utilisateurs de la salle '" + nomSalle + "'", e);
        }
    }

    public List<Salle> sallesParCapaciteSuperieure(int minCapacite) {
        try {
            return salleDao.findByCapaciteSuperieureA(minCapacite);
        } catch (PersistenceException e) {
            throw new ServiceException("Impossible de rechercher les salles par capacité", e);
        }
    }

    public List<User> usersParNomContenant(String motif) {
        if (motif == null || motif.trim().isEmpty()) {
            throw new IllegalArgumentException("Le motif de recherche est obligatoire");
        }
        try {
            return userDao.findByNomContient(motif);
        } catch (PersistenceException e) {
            throw new ServiceException("Impossible de rechercher les utilisateurs par nom", e);
        }
    }
}