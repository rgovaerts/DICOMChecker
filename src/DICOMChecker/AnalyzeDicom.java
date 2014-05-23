package DICOMChecker;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author INFO-H-400
 */
public class AnalyzeDicom {
    
    private ArrayList<ArrayList<String>> DBFields;
    private ArrayList missingInfo;
    
    public boolean getDB(String modalite)
    {
        try { 
            String Tag;
            String Keyword;
            String VR;
            String VM;
            String Obligatoire;
    
            DBFields = new ArrayList();
            //modalite = "MR";
            String userName = "root";
            String password = "1234";
            String url = "jdbc:mysql://localhost:3306/dicom";
            Object newInstance = Class.forName("com.mysql.jdbc.Driver").newInstance (); // this create a connection to the dataBase
            Connection conn = (Connection) DriverManager.getConnection (url, userName, password);
            String query = "SELECT Tag , Keyword , VR, VM, Obligatoire FROM champs WHERE Modalite = '" + modalite + "' SORT BY Tag ASC";
            Statement s = conn.createStatement();
            s.executeQuery(query);
            ResultSet rs = s.getResultSet(); // get the result of the sql query
            while(rs.next())
            {           
                    ArrayList tableau = new ArrayList () ;
                    Tag = rs.getString("Tag");
                    tableau.add(0,Tag);
                    Keyword = rs.getString("Keyword");
                    tableau.add(1,Keyword);
                    VR = rs.getString("VR");
                    tableau.add(2,VR);
                    VM = rs.getString("VM");
                    tableau.add(3,VM);
                    Obligatoire = rs.getString("Obligatoire");
                    tableau.add(4,Obligatoire);
                    
                    DBFields.add(tableau);
            }
            
            //String a = (String) tableau1.get(0);
            System.out.println(DBFields);
        } catch (InstantiationException ex) {
            Logger.getLogger(AnalyzeDicom.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(AnalyzeDicom.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AnalyzeDicom.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(AnalyzeDicom.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
        
    }
    
    public boolean compareList(ArrayList<ArrayList<String>> fields)
    {
        
        for(int i=0;i<DBFields.size();i++ ) //on cherche dans la db un champs obligatoire
        {
            
            ArrayList temp1 = DBFields.get(i);
            for(int k=0;k<temp1.size();k++ ){
                ArrayList temp2 = (ArrayList) temp1.get(k); 
                if(temp2.get(2).equals("1")) // si c'est un champ obligatoire
                {
                    int j = 0;
                    ArrayList<String> field = fields.get(j);
                    //ArrayList<String> DBField = DBFields.get(i);
                    
                    
                    while(!(field.get(0).equals(temp2.get(0)))) //tant que les tags ne sont pas les memes on cherche dans le dicom
                    {
                        /*
                         * DEBUG
                        System.out.println(field.get(0));
                        System.out.println(temp2.get(0));
                         * 
                         */
                        if(j < fields.size()-1){
                            j++;
                            field = fields.get(j);
                        }
                        else{
                            this.missingInfo = temp2;
                            return false;
                        }//si on parcours le dicom entier sans succes
                    }
                }
            }
        }
        return true;
    }
        
    private void addToDataBaseXML(String data,String data1)
    {
        try {                
                String userName = "AdminSim";
                String password = "admin400";
                String url = "jdbc:mysql://localhost:3306/dicom";
                PreparedStatement pst = null;
                Object newInstance = Class.forName("com.mysql.jdbc.Driver").newInstance (); // this create a connection to the dataBase
                Connection conn = (Connection) DriverManager.getConnection (url, userName, password);
                pst= conn.prepareStatement("INSERT INTO ReferenceXML(idReferenceXML, Section) VALUES(?,?)");
                pst.setString(1,data);
                pst.setString(2,data1);
                pst.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(AnalyzeDicom.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AnalyzeDicom.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(AnalyzeDicom.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(AnalyzeDicom.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void addToDataBaseChamp(ArrayList<ArrayList<String>> list)
    {
        try {                
                String userName = "AdminSim";
                String password = "admin400";
                String url = "jdbc:mysql://localhost:3306/dicom";
                PreparedStatement pst = null;
                Object newInstance = Class.forName("com.mysql.jdbc.Driver").newInstance (); // this create a connection to the dataBase
                Connection conn = (Connection) DriverManager.getConnection (url, userName, password);
                pst= conn.prepareStatement("INSERT INTO Champ(Modalite,Tag,VR,VM,Obligatoire,Keyword) VALUES(?,?,?,?,?,?)");
                for(int i=0; i<list.size(); i++)
                {
                    ArrayList fk = new ArrayList();
                    fk=list.get(i);
                    pst.setString(1,(String)fk.get(0));
                    pst.setString(2,(String)fk.get(1));
                    pst.setString(3,(String)fk.get(2));
                    /*pst.setString(4,(String)fk.get(3));
                    pst.setString(5,(String)fk.get(4));
                    pst.setString(6,(String)fk.get(5));*/
                    pst.executeUpdate();
                }
                
        } catch (SQLException ex) {
            Logger.getLogger(AnalyzeDicom.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AnalyzeDicom.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(AnalyzeDicom.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(AnalyzeDicom.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void addToDataBaseTest(ArrayList<ArrayList<String>> list)
    {
        try {                
                String userName = "AdminSim";
                String password = "admin400";
                String url = "jdbc:mysql://localhost:3306/dicom";
                PreparedStatement pst = null;
                Object newInstance = Class.forName("com.mysql.jdbc.Driver").newInstance (); // this create a connection to the dataBase
                Connection conn = (Connection) DriverManager.getConnection (url, userName, password);
                for(int i=0; i<list.size(); i++)
                {
                    pst= conn.prepareStatement("INSERT INTO TestXML(idTag,AttributeName,Type) VALUES(?,?,?)");
                    ArrayList fk = new ArrayList();
                    fk=list.get(i);
                    pst.setString(1,(String)fk.get(0));
                    pst.setString(2,(String)fk.get(1));
                    pst.setString(3,(String)fk.get(2));
                    pst.executeUpdate();
                }
                
        } catch (SQLException ex) {
            Logger.getLogger(AnalyzeDicom.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AnalyzeDicom.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(AnalyzeDicom.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(AnalyzeDicom.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList getArrayList()
    {
            return DBFields;

    }
    
    public void setDBFields(ArrayList<ArrayList<String>> DBFields)
    {
        this.DBFields = DBFields;
    }
    
    public ArrayList getMissingInfo(){
        return this.missingInfo;
    }
}
    
