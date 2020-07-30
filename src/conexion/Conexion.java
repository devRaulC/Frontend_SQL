package conexion;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;
/**
 *
 * @author Raúl
 */

public class Conexion {
    private static Connection conn = null;
     private static final String url = "jdbc:mysql://localhost";
     private static final String port = "3306";
     private static final String user = "root";
     private static final String passwd = "";
     private static final String db = "frontend";
     
      public static void AbrirCone() {
        try {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            String sUrl = url + ":" + port + "/" + db + "?zeroDateTimeBehavior=convertToNull";
            conn = DriverManager.getConnection(sUrl, user, passwd);
            System.out.println(sUrl);

        } catch (SQLException ex) {
            conn = null;
            throw new RuntimeException("Error con la conexión!!!");
        }
    }
       public static Connection getConn() {
        return conn;
    }
    
}
