/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jabilapplication;

import java.awt.BorderLayout;
import javax.swing.*;

public class JabilApplication {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       Classes.CDSApplet myApplet = new Classes.CDSApplet(); // define applet of interest
        JFrame myFrame = new JFrame("Applet Holder"); // create frame with title
        // Call applet's init method (since Java App does not
        // call it as a browser automatically does)
        myFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        myApplet.init();	
        // add applet to the frame
        myFrame.add(myApplet, BorderLayout.CENTER);
        myFrame.pack(); // set window to appropriate size (for its elements)
        myFrame.setVisible(true); // usual step to make frame visible
    }
}
