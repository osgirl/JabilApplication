/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

/**
 *
 * @author Sowa
 */

import java.io.*;
import java.util.Properties;


public class PropertiesClass {
        
        public static PropertiesClass props ;
        public   String DB_URL, DB_USER, DB_PASSWORD, DB_NAME;// = "db properties";
        public  String M_HOST,M_USER,M_PASSWORD,M_PORT; // = "mail properties";
        public String A_ADDRESS; // = Applet properties
          //Plik z konfiguracją
        //URL defaultImage;
        File file; 
        InputStream input;
        //private File f = this.Class.getResourceAsStream ("some/pkg/resource.properties");
        //przyszły obiekt Properties
        public Properties properties = new Properties();
        
        public PropertiesClass(){
            props=this;           
            input = CDSApplet.cdsApplet.getClass().getResourceAsStream("/Properties/" + "conf.properties");
            loadProperties();
        }
         
        private void loadProperties(){
                //Strumień wejściowy
                //InputStream is;
                try {
                        //input = new FileInputStream(file);
                        //ładujemy nasze ustawienia
                        properties.load(input);
                        DB_URL = properties.getProperty("DB_Url");
                        DB_USER = properties.getProperty("DB_User");
                        DB_PASSWORD = properties.getProperty("DB_Password");
                        DB_NAME = properties.getProperty("DB_Name");
                        M_HOST = properties.getProperty("M_Host");
                        M_USER = properties.getProperty("M_User");
                        M_PASSWORD = properties.getProperty("M_Password");
                        M_PORT = properties.getProperty("M_Port");
                        A_ADDRESS = properties.getProperty("A_Address");
                        //System.out.println(DB_URL);
                } catch (FileNotFoundException e) {
                        e.printStackTrace();
                } catch (IOException e) {
                        e.printStackTrace();
                }
        }
                
      /*  public void saveProperties(String key, String value){
        OutputStream os;
        try {
                os = new FileOutputStream(f);
                properties.setProperty(key, value);
                properties.store(os, null);
        } catch (FileNotFoundException e) {
                e.printStackTrace();
        } catch (IOException e) {
                e.printStackTrace();
}}*/
        
}
 

