package tp6bis;

public class Main {
    public static void main(String[] args) {
        Club club = new Club();
        club.setFabricat("Fabricant20");
        club.setPoids(14.5);
        DAO<Club> clubDao = new ClubDAO();
        clubDao.create(club);
    }
}