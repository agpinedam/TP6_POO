package tp6bis;

public class Main {
    public static void main(String[] args) {
        Club club = new Club();
        Club club2 = new Club();
        club.setFabricat("Fabricant21");
        club.setPoids(14.5);
        DAO<Club> clubDao = new ClubDAO();
        //clubDao.create(club);
        club2.setId(12);
        clubDao.delete(club2);
    }
}