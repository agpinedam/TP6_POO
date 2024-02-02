# ORM de base inspiré d'Hibernate

Ce projet vise à créer un ORM de base, en s'inspirant d'Hibernate.

## Prérequis

### Installation de Postgres sur Linux

```bash
sudo apt install postgresql
```

Ensuite, démarrez le service PostgreSQL :

```bash
sudo systemctl start postgresql
```

Vérifiez le statut de PostgreSQL avec :

```bash
sudo systemctl status postgresql
```

Si nécessaire, changez le mot de passe de PostgreSQL en exécutant :

```bash
sudo -u postgres psql -c "ALTER ROLE postgres WITH PASSWORD 'your_new_password'"
```

### Configuration de la base de données

Créez la base de données "GolfClub" sur le port 5432 :

```bash
createdb -p 5432 GolfClub
```

Assurez-vous que la base de données est sur le port 5432.
Modèle Java

## Considérations sur le modèle

* Tous les objets du modèle ont un attribut "id" qui agit comme une clé primaire et est automatiquement incrémenté.
    
* Chaque objet du modèle possède une propriété appelée "version" qui est incrémentée à chaque fois qu'une modification est apportée à cet objet.

Exemple d'utilisation :

```java

// Exemple d'une entité simple
public class EntityExample {
    private Long id ;
    private int version ;
    // Autres attributs et méthodes de l'entité
}
```

## Test

Pour vérifier le fonctionnement du modèle, des tests ont été ajoutés pour chacune des méthodes de la classe EntiyManagerImpl. De plus, pour rendre les tests indépendants, nous avons créé une classe qui crée des données aléatoires.

````java
// Exemple de test
@Test
    public void testPersistence() {
        Club club = new Club() ;
        club.setFabricant(createRandomData.createRandomCompanyName()) ;
        club.setPoids(createRandomData.createRandomPoids()) ;
        EntityManagerImpl em = new EntityManagerImpl() ;
        em.createTable(club) ;
        Club createdClub = em.persist(club) ;
        assertEquals(createdClub.getFabricant(), club.getFabricant()) ;
        assertEquals(createdClub.getPoids(), club.getPoids()) ;
        assertEquals(createdClub.getVersion(), 0) ;
    }
```





