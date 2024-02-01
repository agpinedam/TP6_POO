package tp6bis;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;

class AppTest {
    @Test
    public void testPersistence() {
        Club club = new Club();
        club.setFabricant("Fabricant");
        club.setPoids(10.3);
        EntityManagerImpl em = new EntityManagerImpl();
        em.createTable(club);
        Club createdClub = em.persist(club);
        assertEquals(createdClub.getFabricant(), club.getFabricant());
        assertEquals(createdClub.getPoids(), club.getPoids());
        assertEquals(createdClub.getVersion(), 0);
    }

    @Test
    public void testFind() throws SQLException {
        Club club = new Club();
        club.setFabricant("FabricantTest");
        club.setPoids(10.5);
        EntityManagerImpl em = new EntityManagerImpl();
        Club createdClub = em.persist(club);
        Club trouve = em.<Club> find(Club.class, createdClub.getId());
        assertEquals(createdClub.getFabricant(), trouve.getFabricant());
    }

    @Test
    public void testUpdate() throws SQLException, IllegalAccessException, NoSuchFieldException{
        Club club = new Club();
        club.setFabricant("FabricantUpdate");
        club.setPoids(10.5);
        EntityManagerImpl em = new EntityManagerImpl();
        Club createdClub = em.persist(club);
        createdClub.setFabricant("NewName");
        createdClub.setPoids(10.5);
        Club updatedClub = em.update(createdClub);
        assertEquals(createdClub.getFabricant(), updatedClub.getFabricant());
        assertEquals(createdClub.getPoids(), updatedClub.getPoids());
        assertEquals(updatedClub.getVersion(), 1);
    }

    @Test
    public void testDelete(){
        Club club = new Club();
        club.setFabricant("FabricantDeleted");
        club.setPoids(10.5);
        EntityManagerImpl em = new EntityManagerImpl();
        Club createdClub = em.persist(club);
        em.delete(createdClub);
        Club findedClub = em.find(Club.class, createdClub.getId());
        assertEquals(findedClub, null);
    }
}
