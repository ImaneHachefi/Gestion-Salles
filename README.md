# Gestion-Salles

TP de persistance avec **JPA / Hibernate** et une base **H2 en mémoire** : gestion de salles et d'utilisateurs affectés à une salle.

## Objectifs

- Mapper des entités Java vers des tables avec JPA
- Mettre en place une relation `@ManyToOne` (Utilisateur → Salle)
- Utiliser un DAO générique et des DAO spécialisés
- Écrire des requêtes JPQL avec paramètres nommés
- Gérer les exceptions (validation, doublons, erreurs de persistance)
- Tester la couche service avec JUnit 4

## Technologies

| Outil | Version |
|-------|---------|
| Java | 17 |
| Maven | 3.x |
| Hibernate (hibernate-entitymanager) | 5.6.15.Final |
| H2 Database | 2.1.214 |
| JUnit | 4.13.2 |

## Structure du projet

```
src/
├── main/
│   ├── java/main/
│   │   ├── dao/          GenericDao, SalleDao, UserDao
│   │   ├── entities/     Salle, User
│   │   ├── exception/    ServiceException
│   │   ├── service/      ServiceGestion
│   │   └── Main.java
│   └── resources/META-INF/persistence.xml
└── test/java/main/service/ServiceGestionTest.java
```

## Modèle de données

- **Salle** : `id`, `nom` (unique), `capacite`
- **Utilisateur** (entité `User`) : `id`, `name`, `email` (unique), `salle_id` (clé étrangère vers Salle)

> La table s'appelle `utilisateur` car `USER` est un mot réservé dans H2 .

## Fonctionnalités

- Ajouter et lister des salles
- Ajouter et lister des utilisateurs
- Rechercher les utilisateurs d'une salle
- Rechercher les salles dont la capacité dépasse une valeur
- Rechercher les utilisateurs dont le nom contient une chaîne

### Exemples de requêtes JPQL

```java
// Salles dont la capacité est supérieure à un minimum
em.createQuery("SELECT s FROM Salle s WHERE s.capacite > :minCapacite ORDER BY s.capacite", Salle.class)
  .setParameter("minCapacite", 20)
  .getResultList();

// Utilisateurs dont le nom contient un motif
em.createQuery("SELECT u FROM User u WHERE LOWER(u.name) LIKE :pattern", User.class)
  .setParameter("pattern", "%jean%")
  .getResultList();
```

## Gestion des exceptions

- `IllegalArgumentException` : données invalides (nom vide, capacité négative, email incorrect)
- `ServiceException` (personnalisée) : erreurs de persistance (doublon de nom ou d'email, connexion impossible)
- Rollback automatique de la transaction dans le DAO en cas d'erreur

## Lancer le projet

```bash
git clone https://github.com/ImaneHachefi/Gestion-Salles.git
cd Gestion-Salles
mvn compile exec:java -Dexec.mainClass="main.Main"
```

Le `Main` démarre la console H2 et attend une touche avant de s'arrêter.

### Accès à la console H2

- URL : http://localhost:8082
- JDBC URL : `jdbc:h2:mem:testdb`
- User : `sa`
- Mot de passe : *(vide)*

La base étant en mémoire, elle n'existe que pendant l'exécution du `Main`.

## Exécution du `Main`

<img width="652" height="568" alt="image" src="https://github.com/user-attachments/assets/26745c45-cad7-4e87-8a91-b9e376e598f1" />
<img width="1234" height="691" alt="image" src="https://github.com/user-attachments/assets/8a65e06b-5e78-4b64-ab74-f708e3852285" />
<img width="1108" height="670" alt="image" src="https://github.com/user-attachments/assets/f06fa1b0-bf74-4f80-96f0-d7ab18273311" />
<img width="1171" height="646" alt="image" src="https://github.com/user-attachments/assets/9dcb83d1-0c28-4dc3-860e-f274938f5c6f" />
<img width="1238" height="719" alt="image" src="https://github.com/user-attachments/assets/53d129b1-df25-46a9-84ff-eb7cff40f2df" />
<img width="802" height="841" alt="image" src="https://github.com/user-attachments/assets/eee65363-e2cd-4338-96b2-9dbade89caeb" />
<img width="840" height="532" alt="image" src="https://github.com/user-attachments/assets/0c29e60e-e6a1-4307-a3cf-1f56818ed29f" />

## Vérification de la base dans la console H2

### Connexion à la console

<img width="1919" height="1079" alt="Capture d&#39;écran 2026-10-14 031042" src="https://github.com/user-attachments/assets/ad8cb050-1315-45fc-819e-ce39e797f359" />

### Table `SALLE`

```sql
SELECT * FROM SALLE;
```
<img width="1338" height="635" alt="Capture d&#39;écran 2026-10-14 031200" src="https://github.com/user-attachments/assets/eabe29a1-b8a6-4af7-bb3d-07eddc6213d9" />

### Table `UTILISATEUR`

```sql
SELECT * FROM UTILISATEUR;
```
<img width="1279" height="666" alt="Capture d&#39;écran 2026-10-14 031223" src="https://github.com/user-attachments/assets/e119d2a4-85d5-4755-8a40-a0b871b422fe" />

### Jointure utilisateurs / salles

```sql
SELECT u.id, u.name, u.email, s.nom AS salle
FROM UTILISATEUR u LEFT JOIN SALLE s ON u.salle_id = s.id;
```

<img width="1273" height="660" alt="Capture d&#39;écran 2026-10-14 031255" src="https://github.com/user-attachments/assets/52848956-d177-466d-9856-66313fec51b1" />

### Salles de capacité supérieure à 30

```sql
SELECT * FROM SALLE WHERE CAPACITE > 30;
```
<img width="1289" height="649" alt="Capture d&#39;écran 2026-10-14 031319" src="https://github.com/user-attachments/assets/44b345f3-7492-47b6-87ab-9bc6da8fe31b" />

### Nombre d'utilisateurs par salle

```sql
SELECT s.nom, COUNT(u.id) AS nb_utilisateurs
FROM SALLE s LEFT JOIN UTILISATEUR u ON u.salle_id = s.id
GROUP BY s.nom;
```
<img width="1288" height="667" alt="Capture d&#39;écran 2026-10-14 031403" src="https://github.com/user-attachments/assets/997eac3d-79e4-4894-a278-13a432d4f2fc" />

## Tests unitaires

Lancer les tests :

```bash
mvn test
```

Chaque test utilise une base H2 distincte pour être indépendant des autres.

| Test | Objectif |
|------|----------|
| `testCreationEtRechercheSalle` | Création et lecture d'une salle |
| `testCreationUserAvecSalle` | Création d'un utilisateur lié à une salle |
| `testRechercheUsersParSalle` | Recherche des utilisateurs d'une salle |
| `testSallesParCapaciteSuperieure` | Requête JPQL sur la capacité |
| `testUsersParNomContenant` | Requête JPQL avec `LIKE` |
| `testEmailDuplique` | Exception sur email déjà utilisé |
| `testCapaciteInvalide` | Validation de la capacité |

<img width="1913" height="1079" alt="Capture d&#39;écran 2026-10-14 030214" src="https://github.com/user-attachments/assets/7d3198cb-e2f4-46f1-94b2-ab1012329bba" />
