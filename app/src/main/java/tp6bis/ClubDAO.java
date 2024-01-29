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
                                                    "INSERT INTO club (version, fabricant, poids) VALUES(? , ? , ?)"
                                                );
                prepare.setInt(1, 0);
                prepare.setString(2, obj.getFabricat());
                prepare.setDouble(3, obj.getPoids());
                
                prepare.executeUpdate();
        } catch (SQLException e) {
                e.printStackTrace();
        }
        System.out.println("Finito");
        return obj;
    }

    @Override
    public Club update(Club obj) {
        System.out.println("hi");
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
                                                "SELECT id, version, fabricant, poids\n FROM club" 
                                             );
            System.out.println(result);
            if(result.first())
                    club = new Club(
                                        id, 
                                        result.getString("fabricant") 
                                    );
            
        } catch (SQLException e) {
                    e.printStackTrace();
        }
    return club;
    }   
}
