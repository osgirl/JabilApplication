/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Classes;




import Forms.MainPanel;
import java.io.*;
import java.util.StringTokenizer;


public class FileManager {
    
    //ver.1.02 changes in csvUserReader() new field (carrierID) needed to be inserted while importing a user-- 29.11.2014
    SQLConnection sqlConnector;
    public static FileManager fileManager;
    
    public FileManager(){
        
        fileManager=this;
        sqlConnector = SQLConnection.sqlConnector;
    }
    public void fileWrite(File file, String fileName, String fileDescription, String updatedBy, boolean fileActive, int transportsID) {
        try {
        FileInputStream io = new FileInputStream(file);
        int dotIndex = fileName.lastIndexOf(".");
        String fileType = fileName.substring(dotIndex);
        int fileLength = (int)file.length();
        sqlConnector.sendFileToBase(fileName,fileDescription,fileType,fileLength,(InputStream)io, updatedBy, fileActive,transportsID);
        }
        catch(Exception ex) {
        System.out.println("Blad w fileWrite");
        System.out.println(ex.getMessage());
        }
    }    
    public void fileRead(File file,String fileName) {
        try {
            String path = file.getPath();          
            path = path+"\\"+fileName;
            sqlConnector.getFileFromBase(fileName,path);
        }
        catch(Exception ex) {
        System.out.println("Blad w fileRead");
        System.out.println(ex.getMessage());
        }
      }
    public void csvWriter(String path){
        try{
        path = path+"\\selectedEvents.csv";
        BufferedWriter bw = new BufferedWriter(new FileWriter(path));
        bw.write("Load successful!");
        bw.newLine();
        } catch (Exception e) {
            System.out.println("Exception while writing csv file: " + e);
        }
    }
    public void csvReader(File file){
         try {
            //csv file containing data
            //String strFile = "C:/FileIO/example.csv";
            //create BufferedReader to read csv file
            BufferedReader br = new BufferedReader(new FileReader(file.getPath()));
            String path = file.getPath();
            int index = path.lastIndexOf(".");
            path = path.substring(0, index)+"_report.csv";
            BufferedWriter bw = new BufferedWriter(new FileWriter(path));
            String strLine = "";
            StringTokenizer st = null;
            int lineNumber = 0, tokenNumber = 0;
            //read comma separated file line by line
            while ((strLine = br.readLine()) != null) {
                try{
                lineNumber++;
                //break comma separated line using ";"
                st = new StringTokenizer(strLine, ";");
                String BLNumber=null,PONumber=null;
                String vendor=null,carrier=null,buyer = null;
                
                for (int i=0;i<5;i++) {
                    //display csv values
                    tokenNumber++;
                    String token = st.nextToken();
                    //System.out.println("Line # " + lineNumber + ", Token # " + tokenNumber + ", Token : " + token);
                    switch(i){
                        case 0: BLNumber = token; break;
                        case 1: PONumber = token;break;
                        case 2: vendor = token;break;
                        case 3: carrier = token;break;
                        case 4: buyer = token;break;    
                            
                    }
                }
                int userID,vendorID, carrierID,statusID;    
                String query = "SELECT ID FROM "+PropertiesClass.props.DB_NAME+".Vendor WHERE DataName='"+vendor+"';";
                if(sqlConnector.checkQueryAppearence(query)) vendorID = Integer.parseInt(sqlConnector.getQueryFirstElement(query));
                else throw new Exception("Wrong Vendor!");
                query = "SELECT ID FROM "+PropertiesClass.props.DB_NAME+".Transports WHERE BLNumber='"+BLNumber+"';";
                if(sqlConnector.checkQueryAppearence(query))  throw new Exception("Event Number Exists!");
                query = "SELECT ID FROM "+PropertiesClass.props.DB_NAME+".Carrier WHERE DataName='"+carrier+"';";
                if(sqlConnector.checkQueryAppearence(query))  carrierID = Integer.parseInt(sqlConnector.getQueryFirstElement(query));
                else throw new Exception("Wrong Carrier!");               
                query = "SELECT ID FROM "+PropertiesClass.props.DB_NAME+".User WHERE Login='"+buyer+"';";
                if(sqlConnector.checkQueryAppearence(query)) userID = Integer.parseInt(sqlConnector.getQueryFirstElement(query));
                else throw new Exception("Wrong Buyer!");
                query = "SELECT ID FROM "+PropertiesClass.props.DB_NAME+".Status WHERE DataName='Opened';";
                statusID = Integer.parseInt(sqlConnector.getQueryFirstElement(query));
                query = "SELECT a.`E-mail` FROM "+PropertiesClass.props.DB_NAME+".User a WHERE Login='"+MainPanel.userLogin+"';";
                String buyerMail = sqlConnector.getQueryFirstElement(query);
                query = "SELECT * FROM "+PropertiesClass.props.DB_NAME+".Transports WHERE BLNumber='"+BLNumber+"';";
                //check if event already exist in db
                if(!sqlConnector.checkQueryAppearence(query)){
                    sqlConnector.addEventToTable(BLNumber, PONumber, buyerMail, userID, vendorID, carrierID, statusID);
                    sendMail(userID,BLNumber,PONumber, carrier, vendor, buyer);
                    bw.write(strLine+";Load successful!");
                    bw.newLine();
                }else{
                    bw.write(strLine+";Event already exists!");
                    bw.newLine();
                }
            
            }catch(Exception e){ bw.write(strLine+";Wrong Data! "+e.getMessage() );
                bw.newLine();}
                //reset token number
                tokenNumber = 0;
            }
            try {
                if (bw != null) {
                    bw.flush();
                    bw.close();
                }
                if (br != null) {                    
                    br.close();
                }
                
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("Exception while reading csv file: " + e);
        }
    }
    //ver.1.02 new field in user import (carrierName) -- 29.11.2014
    public void csvUserReader(File file){
         try {
            //csv file containing data
            //String strFile = "C:/FileIO/example.csv";
            //create BufferedReader to read csv file
            BufferedReader br = new BufferedReader(new FileReader(file.getPath()));
            String path = file.getPath();
            int index = path.lastIndexOf(".");
            path = path.substring(0, index)+"_report.csv";
            BufferedWriter bw = new BufferedWriter(new FileWriter(path));
            String strLine = "";
            StringTokenizer st = null;
            int lineNumber = 0, tokenNumber = 0;
            //read comma separated file line by line
            while ((strLine = br.readLine()) != null) {
                try{
                lineNumber++;
                //break comma separated line using ";"
                st = new StringTokenizer(strLine, ";");
                String login=null,password=null;
                String firstName=null,lastName=null,companyCode = null;
                String eMail=null,userDescription=null,groupName = null, carrierName = null;
                
                for (int i=0;i<9;i++) {
                    //display csv values
                    tokenNumber++;
                    String token = st.nextToken();
                    //System.out.println("Line # " + lineNumber + ", Token # " + tokenNumber + ", Token : " + token);
                    switch(i){
                        case 0: login = token; break;
                        case 1: password = token;break;
                        case 2: firstName = token;break;
                        case 3: lastName = token;break;
                        case 4: companyCode = token;break;    
                        case 5: eMail = token;break;
                        case 6: userDescription = token;break;
                        case 7: groupName = token;break;
                        case 8: carrierName = token;break; 
                    }
                }
                int groupID;    
                String query = "SELECT ID FROM "+PropertiesClass.props.DB_NAME+".Group WHERE DataName='"+groupName+"';";
                if(sqlConnector.checkQueryAppearence(query)) groupID = Integer.parseInt(sqlConnector.getQueryFirstElement(query));
                else throw new Exception("Wrong Group!");
                
                int carrierID;    
                query = "SELECT ID FROM "+PropertiesClass.props.DB_NAME+".Carrier WHERE DataName='"+carrierName+"';";
                if(sqlConnector.checkQueryAppearence(query)) carrierID = Integer.parseInt(sqlConnector.getQueryFirstElement(query));
                else 
                    if(groupName.matches("Courier"))throw new Exception("Wrong Carrier!"); 
                    else carrierID = -1;
                
                
                query = "SELECT * FROM "+PropertiesClass.props.DB_NAME+".User WHERE Login='"+login+"';";
                //check if user already exist in db
                if(!sqlConnector.checkQueryAppearence(query)){
                    sqlConnector.addUserToTable(login, password, firstName, lastName, companyCode, eMail, userDescription,MainPanel.mainPanel.userLogin,groupID,carrierID);
                    //sendMail(userID,BLNumber,PONumber, carrier, vendor, buyer);
                    bw.write(strLine+";Load successful!");
                    bw.newLine();
                }else{
                    bw.write(strLine+";Login already exists!");
                    bw.newLine();
                }
            
            }catch(Exception e){ bw.write(strLine+";Wrong Data! "+e.getMessage() );
                bw.newLine();}
                //reset token number
                tokenNumber = 0;
            }
            try {
                if (bw != null) {
                    bw.flush();
                    bw.close();
                }
                if (br != null) {                    
                    br.close();
                }
                
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("Exception while reading csv user file: " + e);
        }
    }
    public void csvVendorReader(File file){
         try {
            //csv file containing data
            //String strFile = "C:/FileIO/example.csv";
            //create BufferedReader to read csv file
            BufferedReader br = new BufferedReader(new FileReader(file.getPath()));
            String path = file.getPath();
            int index = path.lastIndexOf(".");
            path = path.substring(0, index)+"_report.csv";
            BufferedWriter bw = new BufferedWriter(new FileWriter(path));
            String strLine = "";
            StringTokenizer st = null;
            int lineNumber = 0, tokenNumber = 0;
            //read comma separated file line by line
            while ((strLine = br.readLine()) != null) {
                try{
                lineNumber++;
                //break comma separated line using ";"
                st = new StringTokenizer(strLine, ";");
                String dataName=null,dataValue=null;
                String dataDesc=null,dataActive=null,eMail = null;
                
                for (int i=0;i<5;i++) {
                    //display csv values
                    tokenNumber++;
                    String token = st.nextToken();
                    //System.out.println("Line # " + lineNumber + ", Token # " + tokenNumber + ", Token : " + token);
                    switch(i){
                        case 0: dataName = token; break;
                        case 1: dataValue = token;break;
                        case 2: dataDesc = token;break;
                        case 3: dataActive = token;break;
                        case 4: eMail = token;break;    
                        
                    }
                }
                String query = "SELECT * FROM "+PropertiesClass.props.DB_NAME+".Vendor WHERE DataName='"+dataName+"';";
                //check if vendor already exist in db
                boolean dataActiveBool;
                if(dataActive.contentEquals("1")||dataActive.contentEquals("true")) dataActiveBool=true; else dataActiveBool=false;
                if(!sqlConnector.checkQueryAppearence(query)){
                    sqlConnector.addItemToTable("Vendor",dataName,dataValue,dataDesc,dataActiveBool,eMail);
                    //sendMail(userID,BLNumber,PONumber, carrier, vendor, buyer);
                    bw.write(strLine+";Load successful!");
                    bw.newLine();
                }else{
                    bw.write(strLine+";Vendor already exists!");
                    bw.newLine();
                }
            
            }catch(Exception e){ bw.write(strLine+";Wrong Data! "+e.getMessage() );
                bw.newLine();}
                //reset token number
                tokenNumber = 0;
            }
            try {
                if (bw != null) {
                    bw.flush();
                    bw.close();
                }
                if (br != null) {                    
                    br.close();
                }
                
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("Exception while reading csv Vendor file: " + e);
        }
    }
    public void csvCarrierReader(File file){
                 try {
            //csv file containing data
            //String strFile = "C:/FileIO/example.csv";
            //create BufferedReader to read csv file
            BufferedReader br = new BufferedReader(new FileReader(file.getPath()));
            String path = file.getPath();
            int index = path.lastIndexOf(".");
            path = path.substring(0, index)+"_report.csv";
            BufferedWriter bw = new BufferedWriter(new FileWriter(path));
            String strLine = "";
            StringTokenizer st = null;
            int lineNumber = 0, tokenNumber = 0;
            //read comma separated file line by line
            while ((strLine = br.readLine()) != null) {
                try{
                lineNumber++;
                //break comma separated line using ";"
                st = new StringTokenizer(strLine, ";");
                String dataName=null,dataValue=null;
                String dataDesc=null,dataActive=null;
                
                for (int i=0;i<4;i++) {
                    //display csv values
                    tokenNumber++;
                    String token = st.nextToken();
                    //System.out.println("Line # " + lineNumber + ", Token # " + tokenNumber + ", Token : " + token);
                    switch(i){
                        case 0: dataName = token; break;
                        case 1: dataValue = token;break;
                        case 2: dataDesc = token;break;
                        case 3: dataActive = token;break;
                        
                    }
                }
                String query = "SELECT * FROM "+PropertiesClass.props.DB_NAME+".Carrier WHERE DataName='"+dataName+"';";
                //check if vendor already exist in db
                boolean dataActiveBool;
                if(dataActive.contentEquals("1")||dataActive.contentEquals("true")) dataActiveBool=true; else dataActiveBool=false;
                if(!sqlConnector.checkQueryAppearence(query)){
                    sqlConnector.addItemToTable("Carrier",dataName,dataValue,dataDesc,dataActiveBool);
                    //sendMail(userID,BLNumber,PONumber, carrier, vendor, buyer);
                    bw.write(strLine+";Load successful!");
                    bw.newLine();
                }else{
                    bw.write(strLine+";Carrier already exists!");
                    bw.newLine();
                }
            
            }catch(Exception e){ bw.write(strLine+";Wrong Data! "+e.getMessage() );
                bw.newLine();}
                //reset token number
                tokenNumber = 0;
            }
            try {
                if (bw != null) {
                    bw.flush();
                    bw.close();
                }
                if (br != null) {                    
                    br.close();
                }
                
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("Exception while reading csv Carrier file: " + e);
        }
    }
    private void sendMail(int buyerID, String BLNumber, String PONumber, String carrierName, String vendorName, String buyerName){
        
        String query = "SELECT `E-mail` FROM "+PropertiesClass.props.DB_NAME+".User WHERE ID="+buyerID+";";
        String buyerMail = sqlConnector.getQueryFirstElement(query);
        query = "SELECT `E-mail` FROM "+PropertiesClass.props.DB_NAME+".User WHERE Login='"+MainPanel.userLogin+"';";
        String creatorMail = sqlConnector.getQueryFirstElement(query);
        query = "SELECT `E-mail` FROM "+PropertiesClass.props.DB_NAME+".Vendor WHERE DataName='"+vendorName+"';";
        String vendorMail = sqlConnector.getQueryFirstElement(query);
        String [] mails;
        //ver.1.01 Creator deleted from a mail list even when he chooses himself as buyer or vendor -- 28.08.2014
        if (!buyerMail.contentEquals(creatorMail))
        {
            if (!vendorMail.contentEquals(creatorMail))
            {
               mails = new String [] {buyerMail, vendorMail};
            }else mails = new String [] {buyerMail};
             
        }else
        { 
            if (!vendorMail.contentEquals(creatorMail))
            {
               mails = new String [] {vendorMail};
            }else mails = new String [] {};
        }
        
        MailSender mail= new MailSender();
         try{
             //ver.1.01 Event Number and status added to the mail title -- 28.08.2014 
            mail.send(mails,"New Event Created: "+BLNumber+", Opened","New event has been created:\nBLNumber: "+BLNumber+"\nPONumber: "+PONumber+"\nCarrier: "+carrierName+"\nVendor: "+vendorName+"\nBuyer: "+buyerName);          
         }catch(Exception e){System.out.println("Błąd w mailu przy czytaniu Eventów z pliku: "+e);}
    
    }
}
