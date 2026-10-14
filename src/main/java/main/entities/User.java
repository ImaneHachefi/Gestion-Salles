package main.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "utilisateur")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @ManyToOne
    @JoinColumn(name = "salle_id")
    private Salle salle;

    // Constructeurs
    public User() {}

    public User(String nom, String email, Salle salle) {
        this.name = nom;
        this.email = email;
        this.salle = salle;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public String getNom() {
        return name;
    }

    public void setNom(String nom) {
        this.name = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Salle getSalle() {
        return salle;
    }

    public void setSalle(Salle salle) {
        this.salle = salle;
    }

    @Override
    public String toString() {
        return "Utilisateur [id=" + id + ", nom=" + name + ", email=" + email + ", salle=" + (salle != null ? salle.getNom() : "aucune") + "]";
    }
}