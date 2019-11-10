/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
 *
 * @author jpmazate
 */
public class ArchivoController {

    private File archivo;
    private String lineaCodigo;

    public ArchivoController(File archivo) {
        this.archivo = archivo;
    }

    public void leerArchivo(JTextArea area) {
        area.setText("");// reinicia el text area
        try {// abre la via para leer el archivo
            FileReader archivo1 = new FileReader(archivo);
            BufferedReader archivo = new BufferedReader(archivo1);
            String auxiliar = archivo.readLine();// lee la linea
            this.lineaCodigo = auxiliar;// agarra la linea de codigo
            while (auxiliar != null) {// añade linea por linea
                area.append(auxiliar);
                area.append("\n");// agrega salto de linea
                auxiliar = archivo.readLine();
            }
        } catch (Exception e) {// hubo problema
            JOptionPane.showMessageDialog(null, "Hubo un problema al analizar el archivo");
        }
    }
    
    
    public File guardarArchivo(File archivo, JTextArea area) {
        try {// si el archivo existe 
            if (archivo.exists()) {
                // guarda datos de la lista
                try (FileWriter out = new FileWriter(archivo, false); BufferedWriter archivoSalida = new BufferedWriter(out);) {
                    archivoSalida.write(area.getText());
                    archivoSalida.newLine();// agarra la linea y la mete al archivo que se selecciono
                    archivoSalida.flush();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                JOptionPane.showMessageDialog(null, "Se guardo correctamente");
                return archivo;
            } else {// si no existe el archivo dice que no se puede
                JOptionPane.showMessageDialog(null, "No funciona");
                return null;
            }
        } catch (Exception e) {
            return guardarComoArchivo(area);
        }
    }

    // archivo guardar como 
    public File guardarComoArchivo(JTextArea area) {
        JFileChooser guardarComo = new JFileChooser();
        guardarComo.setApproveButtonText("Guardar");
        int opcion = guardarComo.showSaveDialog(null);// agarra los valores del text area
        if (opcion == JFileChooser.APPROVE_OPTION) {
        File archivo = new File(guardarComo.getSelectedFile() + ".txt");// lo guarda como punto txt
        try (FileWriter out = new FileWriter(archivo, false); BufferedWriter archivoSalida = new BufferedWriter(out);) {
            archivoSalida.write(area.getText());// agarra el texto y lo escribe dentro del archivo nuevo
            archivoSalida.newLine();
            archivoSalida.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        JOptionPane.showMessageDialog(null, "Se guardo correctamente");
        return archivo;

        }else if (opcion == JFileChooser.CANCEL_OPTION) {
            JOptionPane.showMessageDialog(null, "No se ha cargado ningun archivo");
            return null;
        }
        return null;
    }

}
