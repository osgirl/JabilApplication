/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

import javax.swing.JApplet;
import java.awt.Graphics;


/**
 *
 * @author Sowa
 */
public class CDSApplet extends JApplet {
    
    public static CDSApplet cdsApplet;

    public void init() {    
     createGUI();
     cdsApplet = this;
     PropertiesClass pc = new PropertiesClass();
    }
    private void createGUI() {
    Forms.LoginPanel lPanel = new Forms.LoginPanel();
    this.getContentPane().add(lPanel);
    }
    public void refresh(){
        this.getContentPane().removeAll();
        Forms.LoginPanel lPanel = new Forms.LoginPanel();
        this.getContentPane().add(lPanel);
        this.getContentPane().repaint();
    }
    // TODO overwrite start(), stop() and destroy() methods
}
