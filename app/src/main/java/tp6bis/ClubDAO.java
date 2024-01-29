package tp6bis;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClubDAO extends DAO<Club>{
    @Override
    public Club create(Club obj) {
        try {
            PreparedStatement prepare = this    .connect
                                                    .prepareStatement(
                                                        "INSERT INTO club (fabricant, poids, version) VALUES(?, ?, ?)"
                                                    );
                prepare.setString(1, obj.getFabricat());
                prepare.setDouble(2, obj.getPoids());
                prepare.setInt(3,0);
                prepare.executeUpdate();    
                
        } catch (SQLException e) {
                e.printStackTrace();
        }
        return obj;
    }

    @Override
    public Club update(Club obj) {
        Club club = new Club();
        club = find(obj.getId());
        int version = club.getVersion() + 1 ;
        try {
            this .connect    
                 .createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE, 
                    ResultSet.CONCUR_UPDATABLE
                 ).executeUpdate(
                    "UPDATE club SET fabricant = '" + obj.getFabricat() + "',"+
                    " poids = " + obj.getPoids() + ","+
                    " version = " + version + " "+
                    " WHERE id = " + obj.getId()
                 );
        
        obj = this.find(obj.getId());
        } catch (SQLException e) {
                e.printStackTrace();
        }
    return obj;
    }

    @Override
    public void delete(Club obj) {
        System.out.println("hi");
    }

    @Override
    public Club find(long id) {
        Club club = new Club();
        try {
            ResultSet result = this .connect
                                    .createStatement(
                                                ResultSet.TYPE_SCROLL_INSENSITIVE, 
                                                ResultSet.CONCUR_UPDATABLE
                                             ).executeQuery(
                                                "SELECT * FROM club WHERE id = " + id
                                             );
            if(result.first())
                    club = new Club(
                                        id, 
                                        result.getString("fabricant") 
                                    );
            club.setVersion(result.getInt("version"));
            club.setPoids(result.getDouble("poids"));
            } catch (SQLException e) {
                    e.printStackTrace();
            }
    return club;
    }   
}
