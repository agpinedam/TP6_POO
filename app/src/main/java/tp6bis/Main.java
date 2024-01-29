package tp6bis;

public class Main {
    public static void main(String[] args) {
        Club club = new Club();
        club.setFabricat("Fabricant2");
        club.setPoids(14.5);
        DAO<Club> clubDao = new ClubDAO();
        clubDao.find(1);
    }
}