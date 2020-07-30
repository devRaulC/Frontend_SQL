package frontEnd;
import controlador.Controlador;
import conexion.Conexion;


/**
 *
 * @author Agares
 */
public class Launcher {
    
    
    public static void main(String[]args){
        
        Conexion.AbrirCone();
        Controlador ctrl = new Controlador();

        
        
    }
}
