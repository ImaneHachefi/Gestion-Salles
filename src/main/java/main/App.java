package main;

import main.entities.Salle;
import main.entities.User;
import main.exception.ServiceException;
import main.service.ServiceGestion;
import org.h2.tools.Server;

import java.sql.SQLException;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        Server console = null;
        ServiceGestion service = null;
        try {
            // Console web H2
            console = Server.createWebServer("-webPort", "8082").start();
            System.out.println("[OK] Console H2 démarrée : " + console.getURL());
            System.out.println("     JDBC URL : jdbc:h2:mem:testdb | User : sa | Mot de passe : (vide)");

            // Initialisation JPA + création des tables
            service = new ServiceGestion();
            System.out.println("[OK] Connexion établie et tables créées");

            // Salles
            Salle salleA = new Salle("Salle A", 30);
            Salle salleB = new Salle("Salle B", 50);
            service.addSalle(salleA);
            System.out.println("[OK] Salle ajoutée avec succès : " + salleA);
            service.addSalle(salleB);

            System.out.println("[OK] Salle ajoutée avec succès : " + salleB);

            // Utilisateurs
            User[] users = {
                    new User("Imane", "imane@mail.com", salleA),
                    new User("Nisrine", "nisrine@mail.com", salleA),
                    new User("Meryem", "meryem@mail.com", salleB)
            };
            for (User u : users) {
                service.addUser(u);
                System.out.println("[OK] Utilisateur ajouté avec succès : " + u);
            }

            // Test d'erreur : email dupliqué
            try {
                service.addUser(new User("Doublon", "imane@mail.com", salleB));
            } catch (ServiceException e) {
                System.out.println("[OK] Erreur attendue interceptée : " + e.getMessage());
            }

            // Test d'erreur : validation
            try {
                service.addSalle(new Salle("Salle C", -5));
            } catch (IllegalArgumentException e) {
                System.out.println("[OK] Validation interceptée : " + e.getMessage());
            }

            // Requêtes JPQL
            System.out.println("\n=== Toutes les salles ===");
            service.listerSalles().forEach(System.out::println);

            System.out.println("\n=== Tous les utilisateurs ===");

            service.listerUsers().forEach(System.out::println);

            System.out.println("\n=== Utilisateurs de la Salle A ===");
            service.usersParSalle("Salle A").forEach(System.out::println);

            System.out.println("\n=== Salles de capacité > 30 ===");
            service.sallesParCapaciteSuperieure(30).forEach(System.out::println);

            System.out.println("\n=== Utilisateurs dont le nom contient 'an' ===");
            service.usersParNomContenant("an").forEach(System.out::println);

            System.out.println("\n[OK] Toutes les opérations ont réussi.");
            System.out.println(">>> Ouvre " + console.getURL() + " pour vérifier la base.");
            System.out.println(">>> Appuie sur Entrée pour arrêter le programme (la base mémoire sera supprimée).");
            new Scanner(System.in).nextLine();

        } catch (SQLException e) {
            System.err.println("Erreur lors du démarrage de la console H2 : " + e.getMessage());
        } catch (ServiceException e) {
            System.err.println("Erreur service : " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("Cause : " + e.getCause().getMessage());
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Données invalides : " + e.getMessage());
        } finally {
            if (service != null) service.fermer();
            if (console != null) console.stop();
        }
    }
}