
package carrito;

import GUI.CarritoGUI;
import panamahitek.Arduino.PanamaHitek_Arduino;
import panamahitek.Arduino.PanamaHitek_multiMessage;


/**
 *
 * @author jpmazate
 */
public class Carrito {
    public static void main(String[] args) {
       try{
          
       
           CarritoGUI carrito = new CarritoGUI();
           carrito.setVisible(true);
       
       
       }catch(Exception e ){
           e.printStackTrace();
       }
       
    }
    
}
