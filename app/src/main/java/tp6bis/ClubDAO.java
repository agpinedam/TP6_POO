package tp6bis;

import java.sql.PreparedStatement;
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
        /* *
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");*/
        System.out.println("hi");
        return obj;
    }

    @Override
    public void delete(Club obj) {
        System.out.println("hi");
    }

    @Override
    public Club find(long id) {
        /* 
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'find'");
        */
        System.out.println("Hi");
        return null;
    }
    
}
