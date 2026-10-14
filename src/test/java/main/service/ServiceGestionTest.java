package main.service;

import main.entities.Salle;
import main.entities.User;
import main.exception.ServiceException;
import main.service.ServiceGestion;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;

public class ServiceGestionTest {

    private ServiceGestion service;

    @Before
    public void setUp() {
        Map<String, Object> props = new HashMap<>();
        props.put("javax.persistence.jdbc.url",
                "jdbc:h2:mem:test_" + UUID.randomUUID().toString().replace("-", "") + ";DB_CLOSE_DELAY=-1");
        service = new ServiceGestion(props);
    }

    @After
    public void tearDown() {
        service.fermer();

    }

    @Test
    public void testCreationEtRechercheSalle() {
        service.addSalle(new Salle("Salle Test", 20));

        List<Salle> salles = service.listerSalles();
        assertEquals(1, salles.size());
        assertEquals("Salle Test", salles.get(0).getNom());
    }

    @Test
    public void testCreationUserAvecSalle() {
        Salle salle = new Salle("Salle B", 15);
        service.addSalle(salle);
        service.addUser(new User("Alice", "alice@example.com", salle));

        List<User> users = service.listerUsers();
        assertEquals(1, users.size());
        assertEquals("Alice", users.get(0).getNom());
        assertEquals("Salle B", users.get(0).getSalle().getNom());
    }

    @Test
    public void testRechercheUsersParSalle() {
        Salle salle = new Salle("Salle C", 30);
        service.addSalle(salle);
        service.addUser(new User("Bob", "bob@example.com", salle));
        service.addUser(new User("Carol", "carol@example.com", salle));

        assertEquals(2, service.usersParSalle("Salle C").size());
    }


    @Test
    public void testSallesParCapaciteSuperieure() {
        service.addSalle(new Salle("Petite", 10));
        service.addSalle(new Salle("Moyenne", 25));
        service.addSalle(new Salle("Grande", 50));

        List<Salle> result = service.sallesParCapaciteSuperieure(20);
        assertEquals(2, result.size());
        assertEquals("Moyenne", result.get(0).getNom());
    }

    @Test
    public void testUsersParNomContenant() {
        Salle salle = new Salle("Salle D", 20);
        service.addSalle(salle);
        service.addUser(new User("Jean Dupont", "jean@example.com", salle));
        service.addUser(new User("Marie Curie", "marie@example.com", salle));

        List<User> result = service.usersParNomContenant("jean");
        assertEquals(1, result.size());
        assertEquals("Jean Dupont", result.get(0).getNom());
    }

    @Test(expected = ServiceException.class)
    public void testEmailDuplique() {
        Salle salle = new Salle("Salle E", 20);
        service.addSalle(salle);
        service.addUser(new User("A", "meme@example.com", salle));
        service.addUser(new User("B", "meme@example.com", salle));
    }


    @Test(expected = IllegalArgumentException.class)
    public void testCapaciteInvalide() {
        service.addSalle(new Salle("Salle F", -5));
    }
}