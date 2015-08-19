/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;

/**
 *
 * @author Sowa
 */
import com.mysql.jdbc.ResultSetMetaData;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.Desktop;
import sun.misc.Version;

public class SQLConnection {
    //ver.1.02 updateUser() and addUserToTable() updated to insert a new field for Courier user group -- 29.11.2014
    
    public static SQLConnection sqlConnector;    
    Connection con = null;
    Statement st = null;
    ResultSet rs = null;
    PreparedStatement pst = null;
    private   String URL;// = "jdbc:mysql://localhost:3306/mydb";
    private   String USER;// = "root";
    private   String PASSWORD;// = encrypted password;   
    public   String DBNAME;
    
    public SQLConnection(){
        sqlConnector = this;
        System.setProperty("file.encoding", "UTF-8");
        //PropertiesClass pc = new PropertiesClass();
        URL = PropertiesClass.props.DB_URL;
        USER = PropertiesClass.props.DB_USER;
        PASSWORD = PropertiesClass.props.DB_PASSWORD;
        DBNAME = PropertiesClass.props.DB_NAME;
        startConnection();

    }
    private void startConnection(){
        
        try {
                StringBuffer msg = new StringBuffer();
                DriverManager.setLoginTimeout(10);
                con = DriverManager.getConnection(URL, USER, Cipher.mDecrypt(PASSWORD,msg));
                st = con.createStatement();
                //System.out.println("Komunikacja z baza nawiazana");
        }
        catch (SQLException ex) {
                System.out.println("Blad w startConnection");
                Logger lgr = Logger.getLogger(Version.class.getName());
                lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
        
    }        
    public void closeConnection(){
        try {
                    if (rs != null) {
                        rs.close();
                    }
                    if (st != null) {
                        st.close();
                    }
                    if (con != null) {
                        con.close();                      
                    }

                } catch (SQLException ex) {
                    Logger lgr = Logger.getLogger(Version.class.getName());
                    lgr.log(Level.WARNING, ex.getMessage(), ex);
                }}
    public void getSixQueryColumns(String query, String[] dataName, String[] dataValue, String[] dataDesc, String[] dataActive, String[] updatedBy, String[] updatedDate){
        //startConnection();
        try {
            pst = con.prepareStatement(query);
            rs = pst.executeQuery();
            rs.last();
            int size = rs.getRow();
            rs.beforeFirst();
            int i = 0;
            while (rs.next()) {
            dataName[i] = rs.getString(1);
            dataValue[i] = rs.getString(2);
            dataDesc[i] = rs.getString(3);
            dataActive[i] = rs.getString(4);
            updatedBy[i] = rs.getString(5);
            updatedDate[i] = rs.getString(6);
            i++;
            //System.out.println(rs.getString(1));
            }
            //closeConnection();
            } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(Version.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
            //closeConnection();
            }
    } 
    public DefaultTableModel getQueryTableModel(String query){
        
            
        try {
            pst = con.prepareStatement(query);
            rs = pst.executeQuery();
            
            ResultSetMetaData metaData = (ResultSetMetaData) rs.getMetaData();
            int numberOfColumns = metaData.getColumnCount();
            Vector columnNames = new Vector();

            // Get the column names
            for (int column = 0; column < numberOfColumns; column++) {
                columnNames.addElement(metaData.getColumnLabel(column + 1));
            }

            // Get all rows.
            Vector rows = new Vector();

            while (rs.next()) {
                Vector newRow = new Vector();

                for (int i = 1; i <= numberOfColumns; i++) {
                    newRow.addElement(rs.getObject(i));
                }

                rows.addElement(newRow);
            }

            return new DefaultTableModel(rows, columnNames);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
        
    public String[] getQueryFirstColumn(String query){
        String[] column;
        //startConnection();
        try {
            pst = con.prepareStatement(query);
            rs = pst.executeQuery();
            rs.last();
            int size = rs.getRow();
            rs.beforeFirst();
            column = new String[size];
            int i = 0;
            while (rs.next()) {
            column[i] = rs.getString(1);
            i++;
            //System.out.println(rs.getString(1));
            }
            //closeConnection();
            return column;
            } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(Version.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
            //closeConnection();
            return null;
            }
    } 
    public String getQueryFirstElement(String query){
        String element;
        //startConnection();
        try {
            pst = con.prepareStatement(query);
            rs = pst.executeQuery();            
            rs.next();
            element = rs.getString(1);
            //closeConnection();
            return element;
            } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(Version.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
            //closeConnection();
            return null;
            }
    }
    public String[] getQueryFirstRow(String query,int columns){
        String[] element = new String[columns];
        //startConnection();
        try {
            pst = con.prepareStatement(query);
            rs = pst.executeQuery();            
            rs.next();
            for(int i=0;i<columns;i++){
             element[i] = rs.getString(i+1);   
            }
            //closeConnection();
            return element;
            } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(Version.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
            //closeConnection();
            return null;
            }
    }   
    public boolean checkQueryAppearence(String query){
    
        //startConnection();
        try {
            pst = con.prepareStatement(query);
            rs = pst.executeQuery();
            rs.last();
            int size = rs.getRow();
            //System.out.println("size: "+size);
            //System.out.println("query: "+query);
            if(size==0) return false;
            //System.out.println(true);
            //closeConnection();
            return true;
            } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(Version.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
            //closeConnection();
            return false;
            }
    }
    public void sendFileToBase(String fileName, String fileDescription, String fileType, int fileLength, InputStream input , String updatedBy, boolean fileActive, int transportsID){    
        try {

            String query = "INSERT INTO "+DBNAME+".`LinkedDoc` (`FileName`,`FileDescription`, `FileType`, `FileData`, `UpdatedBy`, `UpdatedDate`, `FileActive`, `Transports_ID` ) VALUES (?,?,?,?,?,NOW(),?,?);";
            //startConnection();
            pst = con.prepareStatement(query);
            pst.setString(1,fileName);
            pst.setString(2,fileDescription);
            pst.setString(3,fileType);
            pst.setBinaryStream(4,input, fileLength);
            pst.setString(5,updatedBy);
            pst.setBoolean(6, fileActive);
            pst.setInt(7,transportsID);
            pst.executeUpdate();
            //closeConnection();
        }
        catch(Exception ex) {
            System.out.println("Blad w sendFileToBase");
            System.out.println(ex.getMessage());
            //closeConnection();
        }
    }
    public void getFileFromBase(String fileName, String path){
        //startConnection();
        try {
            String query = "SELECT * FROM "+DBNAME+".LinkedDoc WHERE FileName='"+fileName+"'";
            pst = con.prepareStatement(query);
            rs = pst.executeQuery();
            rs.next();
            InputStream bodyOut = rs.getBinaryStream("FileData");
            FileOutputStream f = new FileOutputStream(path);           
            int c;
            while ((c=bodyOut.read())>-1) {
                f.write(c);
            }
            f.close();
            bodyOut.close();
            //closeConnection();
        }
        catch(Exception ex) {
            System.out.println("Blad w getFileFromBase");
            System.out.println(ex.getMessage());
            //closeConnection();
        }
     }
    public void openFileFromBase(String fileName, File tempFile){
        //startConnection();
        try {
            String query = "SELECT * FROM "+DBNAME+".LinkedDoc WHERE FileName='"+fileName+"'";
            pst = con.prepareStatement(query);
            rs = pst.executeQuery();
            rs.next();
            //File file = new File("tempFile");
            InputStream bodyOut = rs.getBinaryStream("FileData");
            FileOutputStream f = new FileOutputStream(tempFile);
            int c;
            while ((c=bodyOut.read())>-1) {
                f.write(c);
            }            
            f.close();
            bodyOut.close();
            try {
 	
		if (tempFile.exists()) {
 
			if (Desktop.isDesktopSupported()) {
				Desktop.getDesktop().open(tempFile);
			} else {
				System.out.println("Awt Desktop is not supported!");
			}
 
		} else {
			System.out.println("File is not exists!");
		}
 
		//System.out.println("Done");
 
            } catch (Exception ex) {
                    ex.printStackTrace();
            }
            //closeConnection();
        }
        catch(Exception ex) {
            System.out.println("Blad w openFileFromBase");
            System.out.println(ex.getMessage());
            //closeConnection();
        }
     }
    public void addLabelToFile(String label, String updatedBy, int linkedDocID){
        try {
            String query = "INSERT INTO "+DBNAME+".Label (`Label`, `UpdatedBy`, `UpdatedDate`, `LinkedDoc_ID`) VALUES (?, ?, NOW(), ?);";
            //startConnection();
            pst = con.prepareStatement(query);
            pst.setString(1,label);
            pst.setString(2,updatedBy);
            pst.setInt(3,linkedDocID);
            pst.executeUpdate();
            //closeConnection();
        }
        catch(Exception ex) {
            System.out.println("Blad w addLabelToFile");
            System.out.println(ex.getMessage());
            //closeConnection();
        }
    }
    public void addCommentToTable(String comment, String updatedBy, int transportID){
        try {
            String query = "INSERT INTO "+DBNAME+".Comments (`Comment`, `UpdatedBy`, `UpdatedDate`, `Transports_ID`) VALUES (?, ?, NOW(), ?);";
            //startConnection();
            pst = con.prepareStatement(query);
            pst.setString(1,comment);
            pst.setString(2,updatedBy);
            pst.setInt(3,transportID);
            pst.executeUpdate();
            //closeConnection();
        }
        catch(Exception ex) {
            System.out.println("Blad w addCommentToTable");
            System.out.println(ex.getMessage());
            //closeConnection();
        }
    }
    public void addItemToTable(String table,String dataName, String dataValue, String dataDescription, boolean dataActive){
        try {

            String query = "INSERT INTO "+DBNAME+"."+table+" (`DataName`, `DataValue`, `DataDescription`, `DataActive`, `UpdatedBy`, `UpdatedDate`) VALUES (?, ?, ?, ?, ?, NOW());";
            //startConnection();
            pst = con.prepareStatement(query);
            pst.setString(1,dataName);
            pst.setString(2,dataValue);
            pst.setString(3,dataDescription);
            pst.setBoolean(4,dataActive);
            pst.setString(5,Forms.MainPanel.userLogin);
            pst.executeUpdate();
            //closeConnection();
        }
        catch(Exception ex) {
            System.out.println("Blad w addItemToTable");
            System.out.println(ex.getMessage());
            //closeConnection();
        }
    }
    public void addItemToTable(String table,String dataName, String dataValue, String dataDescription, boolean dataActive, String email, boolean masterUnit){
        try {

            String query = "INSERT INTO "+DBNAME+"."+table+" (`DataName`, `DataValue`, `DataDescription`, `DataActive`, `UpdatedBy`, `UpdatedDate`, `E-mail`, `MasterUnit`) VALUES (?, ?, ?, ?, ?, NOW(), ?,?);";
            //startConnection();
            pst = con.prepareStatement(query);
            pst.setString(1,dataName);
            pst.setString(2,dataValue);
            pst.setString(3,dataDescription);
            pst.setBoolean(4,dataActive);
            pst.setString(5,Forms.MainPanel.userLogin);
            pst.setString(6,email);
            pst.setBoolean(7, masterUnit);          
            pst.executeUpdate();
            //closeConnection();
        }
        catch(Exception ex) {
            System.out.println("Blad w addItemToTable2");
            System.out.println(ex.getMessage());
            //closeConnection();
        }
    }
    public void updateEventStatus(int statusID, String BLNumber){
        try {

            String query = "UPDATE "+DBNAME+".Transports SET Status_ID = ? WHERE BLNumber = ?;";
            //startConnection();
            pst = con.prepareStatement(query);
            pst.setInt(1,statusID);
            pst.setString(2,BLNumber);
            pst.executeUpdate();
            //closeConnection();
        }
        catch(Exception ex) {
            System.out.println("Blad w updateEventStatus");
            System.out.println(ex.getMessage());
            //closeConnection();
        }
    }    
    public void updateTable(String table,String selectedItem,String dataName, String dataValue, String dataDescription, boolean dataActive){
        try {

            String query = "UPDATE "+DBNAME+"."+table+" SET DataName = ?, DataValue = ?, DataDescription = ?, DataActive = ?, UpdatedBy = ?, UpdatedDate = NOW() WHERE DataName = ?;";
            //startConnection();
            pst = con.prepareStatement(query);
            pst.setString(1,dataName);
            pst.setString(2,dataValue);
            pst.setString(3,dataDescription);
            pst.setBoolean(4,dataActive);
            pst.setString(5,Forms.MainPanel.userLogin);
            pst.setString(6,selectedItem);
            pst.executeUpdate();
            //closeConnection();
        }
        catch(Exception ex) {
            System.out.println("Blad w updateTable");
            System.out.println(ex.getMessage());
            //closeConnection();
        }
    }
    public void updateTable(String table,String selectedItem,String dataName, String dataValue, String dataDescription, boolean dataActive, String email, boolean masterUnit, int masterUnitID){
        try {

            String query = "UPDATE "+DBNAME+"."+table+" SET `DataName` = ?, `DataValue` = ?, `DataDescription` = ?, `DataActive` = ?, `UpdatedBy` = ?, `UpdatedDate` = NOW(), `E-mail` = ?,`MasterUnit` = ?, `MasterUnitId` = ?  WHERE DataName = ? ;";
            //startConnection();
            pst = con.prepareStatement(query);
            pst.setString(1,dataName);
            pst.setString(2,dataValue);
            pst.setString(3,dataDescription);
            pst.setBoolean(4,dataActive);
            pst.setString(5,Forms.MainPanel.userLogin);
            pst.setString(6,email);
            pst.setBoolean(7, masterUnit);
            pst.setInt(8, masterUnitID);
            pst.setString(9,selectedItem);
            pst.executeUpdate();
            //closeConnection();
        }
        catch(Exception ex) {
            System.out.println("Blad w updateTable2");
            System.out.println(ex.getMessage());
            //closeConnection();
        }
    }
    //ver.1.02 new parameter int carrierID -- 29.11.2014
    public void addUserToTable(String login, String password, String firstName, String lastName, String companyCode, String email , String userDescription, String updatedBy, int groupID, int carrierID){
        try {

            String query = "INSERT INTO "+DBNAME+".User (`Login`, `Password`, `FirstName`, `LastName`, `CompanyCode`, `e-mail`, `UserDescription`, `UpdatedBy`, `UpdatedDate`, `Group_ID`, `Carrier_ID`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW(), ?, ?);";
            //startConnection();
            pst = con.prepareStatement(query);
            pst.setString(1,login);
            pst.setString(2,password);
            pst.setString(3,firstName);
            pst.setString(4,lastName);
            pst.setString(5,companyCode);
            pst.setString(6,email);
            pst.setString(7,userDescription);
            pst.setString(8,updatedBy);
            pst.setInt(9,groupID);
            if (carrierID == -1) pst.setNull(10, java.sql.Types.INTEGER);
            else pst.setInt(10,carrierID);
            pst.executeUpdate();
            //closeConnection();
        }
        catch(Exception ex) {
            System.out.println("Blad w addUserToTable");
            System.out.println(ex.getMessage());
            //closeConnection();
        }
    }
    //ver.1.02 new parameter int carrierID -- 29.11.2014
    public void updateUser(String login, String password, String firstName, String lastName, String companyCode, String email , String userDescription, String updatedBy, int groupID, int carrierID, String selectedItem){
        try {

            String query = "UPDATE "+DBNAME+".User SET `Login` = ?, `Password` = ?, `FirstName` = ?, `LastName` = ?, `CompanyCode` = ?, `e-mail` = ?, `UserDescription` = ?, `UpdatedBy` = ?, `UpdatedDate` = NOW(), `Group_ID` = ?, `Carrier_ID` = ? WHERE `Login` = ?;";
            //startConnection();
            pst = con.prepareStatement(query);
            pst.setString(1,login);
            pst.setString(2,password);
            pst.setString(3,firstName);
            pst.setString(4,lastName);
            pst.setString(5,companyCode);
            pst.setString(6,email);
            pst.setString(7,userDescription);
            pst.setString(8,updatedBy);
            pst.setInt(9,groupID);
            if (carrierID == -1) pst.setNull(10, java.sql.Types.INTEGER);
            else pst.setInt(10,carrierID);
            pst.setString(11,selectedItem);
            pst.executeUpdate();
            //closeConnection();
        }
        catch(Exception ex) {
            System.out.println("Blad w updateUser");
            System.out.println(ex.getMessage());
            //closeConnection();
        }
    }
    
    public void updateUser(String login, String password){
        try {

            String query = "UPDATE "+DBNAME+".User SET `Password` = ?, `UpdatedBy` = ?, `UpdatedDate` = NOW() WHERE `Login` = ?;";
            //startConnection();
            pst = con.prepareStatement(query);
            pst.setString(1,password);
            pst.setString(2,login);
            pst.setString(3,login);
            pst.executeUpdate();
            //closeConnection();
        }
        catch(Exception ex) {
            System.out.println("Blad w updateUser");
            System.out.println(ex.getMessage());
            //closeConnection();
        }
    }
    
    public void addEventToTable(String BLNumber, String PONumber, String updatedBy, int buyerID, int vendorID, int carrierID , int statusID){
       try {

            String query = "INSERT INTO "+DBNAME+".Transports (`BLNumber`, `PONumber`, `UpdatedBy`, `UpdatedDate`, `Buyer_ID`, `Vendor_ID`, `Carrier_ID`, `Status_ID`) VALUES (?, ?, ?, NOW(), ?, ?, ? , ?);";
            //startConnection();
            pst = con.prepareStatement(query);
            pst.setString(1,BLNumber);
            pst.setString(2,PONumber);
            pst.setString(3,updatedBy);
            pst.setInt(4,buyerID);
            pst.setInt(5,vendorID);
            pst.setInt(6,carrierID);
            pst.setInt(7,statusID);
            pst.executeUpdate();
            //closeConnection();
        }
        catch(Exception ex) {
            System.out.println("Blad w addEventToTable");
            System.out.println(ex.getMessage());
            //closeConnection();
        } 
    }
    public void addLogToTable(String dataDescription, String updatedBy, String fileName, int transportID){
       try {

            String query = "INSERT INTO "+DBNAME+".Logs ( DataDescription, UpdatedBy, UpdatedDate, FileName, Transports_ID) VALUES (?, ?, NOW(), ?, ?);";
            //startConnection();
            pst = con.prepareStatement(query);
            pst.setString(1,dataDescription);
            pst.setString(2,updatedBy);
            pst.setString(3,fileName);
            pst.setInt(4,transportID);           
            pst.executeUpdate();
            //closeConnection();
        }
        catch(Exception ex) {
            System.out.println("Blad w addLogToTable");
            System.out.println(ex.getMessage());
            //closeConnection();
        } 
    }
    public void delLabelsFromFile(int docID){
        try {
            String query = "DELETE FROM "+DBNAME+".Label WHERE LinkedDoc_ID ='"+docID+"';";
            //startConnection();
            pst = con.prepareStatement(query);
            pst.executeUpdate();
            //closeConnection();
        }
        catch(Exception ex) {
            System.out.println("Blad w delLabelFromFile");
            System.out.println(ex.getMessage());
            //closeConnection();
        }
    }
     public void deleteFileFromBase(String fileName,int transportID){
        try {
            String query = "DELETE b.* FROM "+DBNAME+".LinkedDoc a, "+DBNAME+".Label b WHERE a.ID = b.LinkedDoc_ID AND a.Transports_ID='"+transportID+"' AND a.FileName = '"+fileName+"';";
            //startConnection();
            pst = con.prepareStatement(query);
            pst.executeUpdate();
            query = "DELETE a.* FROM "+DBNAME+".LinkedDoc a WHERE a.Transports_ID='"+transportID+"' AND a.FileName = '"+fileName+"';";
            pst = con.prepareStatement(query);
            pst.executeUpdate();
            //closeConnection();
        }
        catch(Exception ex) {
            System.out.println("Blad w deleteFileFromBase");
            System.out.println(ex.getMessage());
            //closeConnection();
        }
    }
    public void deleteEventFromBase(int transportID)
    {
       try {
            String query = "DELETE t.* FROM "+DBNAME+".Transports t WHERE t.ID ='"+transportID+"';";
            //startConnection();
            pst = con.prepareStatement(query);
            pst.executeUpdate();
            //closeConnection();
        }
        catch(Exception ex) {
            System.out.println("Blad w deleteEventFromBase");
            System.out.println(ex.getMessage());
            //closeConnection();
        } 
    }
}

