package controlador;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;
import modelo.Modelo;
import vista.Vista;
import static conexion.Conexion.getConn;

/**
 *
 * @author Raúl
 */
public class Controlador implements ActionListener, MouseListener {

    private final Vista vista;
    private final ButtonGroup grupo;
    PreparedStatement ps;
    ResultSet rs;
    Icon icon;

    //CONSTRUCTOR
    public Controlador() {
        vista = new Vista();
        grupo = vista.getButtonGroup1();

        Iniciador();
    }

    //INICIA LOS OYENTES DE LOS BOTONES DEL FORMULARIO Y LA TABLA
    public void Iniciador() {

        vista.getBtnAñadir().addActionListener(this);
        vista.getBtnModificar().addActionListener(this);
        vista.getBtnEliminar().addActionListener(this);
        vista.getBtnLanzar().addActionListener(this);

        vista.setVisible(true);

        BuscarJuegos();
    }

    //LIMPIA LAS CAJAS DE TEXTO DEL FORMULARIO
    public void LimpiarCajas() {
        vista.getTxtID().setText(null);
        vista.getTxtTitulo().setText(null);
        vista.getTxtConsola().setText(null);
        vista.getTxtGenero().setText(null);
        vista.getTxtNJugadores().setText(null);
    }

    //REFRESCA EL CONTENIDO DE LA TABLA DE JUEGOS DEPENDIENDO DEL SISTEMA ELEGIDO
    public void BuscarJuegos() {
        DefaultTableModel modeloTabla = new DefaultTableModel();
        vista.getTablaJuegos().setModel(modeloTabla);

        //GESTIÓN DEL GRUPO DE BOTONES DE CONSOLAS
        grupo.add(vista.getTodo());
        grupo.add(vista.getMegaDrive());
        grupo.add(vista.getSuperNintendo());

        vista.getTodo().setActionCommand("");
        vista.getMegaDrive().setActionCommand("md.jpg");
        vista.getSuperNintendo().setActionCommand("sns.jpg");

        vista.getTodo().setSelected(true);

        String sql = "SELECT * FROM juego";
        String where = "";

        //CAMBIAR IMAGEN CONSOLA
        JLabel imagen = vista.getJlblIcono();
        String seleccionado = grupo.getSelection().getActionCommand();
        imagen.setIcon(new ImageIcon("C:/Users/Agares/Documents/NetBeansProjects/ProyectoFrontend/build/classes/imagenes/" + seleccionado));

        try {
            //LANZA LA CONSULTA
            Connection con = getConn();
            ps = con.prepareStatement(sql + where);
            rs = ps.executeQuery();

            ResultSetMetaData rsmd = rs.getMetaData();
            int cantidadColumnas = rsmd.getColumnCount();

            modeloTabla.addColumn("idJuego");
            modeloTabla.addColumn("titulo");
            modeloTabla.addColumn("consola");
            modeloTabla.addColumn("genero");
            modeloTabla.addColumn("nJugadores");

            //RECUPERA LOS RESULTADOS
            while (rs.next()) {

                Object[] filas = new Object[cantidadColumnas];

                for (int i = 0; i < cantidadColumnas; i++) {
                    filas[i] = rs.getObject(i + 1);
                }

                modeloTabla.addRow(filas);
            }

        } catch (SQLException e) {

            System.err.println(e);
        }
    }

    //AÑADE A LA TABLA DE JUEGOS UN NUEVO REGISTRO
    public void AñadirJuego() {

        try {
            //LANZA LA CONSULTA
            Connection con = getConn();
            ps = con.prepareStatement("INSERT INTO juego (idJuego,titulo,consola,genero,nJugadores) VALUES (?,?,?,?,?)");
            ps.setInt(1, Integer.parseInt(vista.getTxtID().getText()));
            ps.setString(2, vista.getTxtTitulo().getText());
            ps.setString(3, vista.getTxtConsola().getText());
            ps.setString(4, vista.getTxtGenero().getText());
            ps.setInt(5, Integer.parseInt(vista.getTxtNJugadores().getText()));

            int res = ps.executeUpdate();
            if (res > 0) {
                JOptionPane.showMessageDialog(null, "Nuevo juego registrado.");
            } else {
                System.out.println("Error al guardar el juego.");
            }
            con.close();
            LimpiarCajas();
            BuscarJuegos();

        } catch (Exception e) {
            System.err.println(e);
        }
    }

    //MODIFICA EL REGISTRO MARCADO EN LA CASILLA ID
    public void ModificarJuego() {

        try {
            Connection con = getConn();
            ps = con.prepareStatement("UPDATE juego SET titulo=?, consola=?, genero=?, nJugadores=? WHERE idJuego=?");
            ps.setString(1, vista.getTxtTitulo().getText());
            ps.setString(2, vista.getTxtConsola().getText());
            ps.setString(3, vista.getTxtGenero().getText());
            ps.setInt(4, Integer.parseInt(vista.getTxtNJugadores().getText()));
            ps.setInt(5, Integer.parseInt(vista.getTxtID().getText()));

            int res = ps.executeUpdate();
            if (res > 0) {
                JOptionPane.showMessageDialog(null, "Cambios efectuados.");
            } else {
                System.out.println("Error al cambiar el juego.");
            }
            con.close();
            LimpiarCajas();
            BuscarJuegos();

        } catch (Exception e) {
            System.err.print(e);
        }
    }

    //ELIMINA EL REGISTRO MARCADO EN LA CASILLA ID
    public void EliminarJuego() {

        try {
            Connection con = getConn();
            ps = con.prepareStatement("DELETE FROM juego WHERE idJuego =?");
            ps.setInt(1, Integer.parseInt(vista.getTxtID().getText()));

            int res = ps.executeUpdate();
            if (res > 0) {
                JOptionPane.showMessageDialog(null, "El juego ha sido borrado.");
            } else {
                System.out.println("Error al eliminar el juego.");
            }
            con.close();
            LimpiarCajas();
            BuscarJuegos();

        } catch (Exception e) {
            System.err.println(e);
        }
    }

    //LANZA EL EMULADOR SELECCIONADO Y MUESTRA UNA MINIATURA DE LA CONSOLA
    public void LanzarEmulador() throws IOException {
        Runtime app = Runtime.getRuntime();

        grupo.add(vista.getMegaDrive());
        grupo.add(vista.getSuperNintendo());

        vista.getMegaDrive().setActionCommand("md.jpg");
        vista.getSuperNintendo().setActionCommand("sns.jpg");

        String seleccionado = grupo.getSelection().getActionCommand();

        //CAMBIAR IMAGEN
        JLabel imagen = vista.getJlblIcono();
        imagen.setIcon(new ImageIcon("C:/Users/Agares/Documents/NetBeansProjects/ProyectoFrontend/build/classes/imagenes/" + seleccionado));

        //LANZAR EL EMULADOR
        String path = "C:/Users/Agares/Documents/NetBeansProjects/ProyectoFrontend/build/classes/emuladores/";
        String emulador = null;

        if (seleccionado == "md.jpg") {
            emulador = "Fusion.exe";
        } else if (seleccionado == "sns.jpg") {
            emulador = "snes9x.exe";
        }

        try {
            app.exec(path + emulador);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Modelo juego = new Modelo();
        switch (e.getActionCommand()) {

            case "Añadir":
                AñadirJuego();
                break;
            case "Modificar":
                ModificarJuego();
                break;
            case "Eliminar":
                EliminarJuego();
                break;

            case "Lanzar": {
                try {
                    LanzarEmulador();
                } catch (IOException ex) {
                    Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            break;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mousePressed(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseExited(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
