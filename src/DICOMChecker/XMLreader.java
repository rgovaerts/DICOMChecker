/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DICOMChecker;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
 
public class XMLreader {
    
    private ArrayList listOfTagsAndType;
    
    public ArrayList getListOfTagsAndType(){
        return this.listOfTagsAndType;
    }
    
    public XMLreader(){
        try {
            //D:/Users/INFO-H-400\Desktop\DICOMChecker-master\src\DICOMChecker\DocBookDICOM2013_docbook_20140430202104\source\docbook\part03
            File fXmlFile = new File("/home/remy/NetBeansProjects/DICOMChecker/src/DICOMChecker/DocBookDICOM2013_docbook_20140430202104/source/docbook/part03/part03.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = (Document) dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();

            // parcours l'annexe A du xml pour récupérer la liste des 
            // références des modules pour la modalité A.3.3 
            // (CT Image IOD Module Table)
            String modalityTable = "A.3.3";
            //listes des références des tables de l'annexe C de chaque module de la modalité
            ArrayList listOfRefs = searchReferenceXML(modalityTable, doc);
            this.listOfTagsAndType = new ArrayList();
            for(int i = 0; i < listOfRefs.size(); i++){
                this.listOfTagsAndType.add(tagsAndTypeGrab((String) listOfRefs.get(i), doc));
            }
            /*
             * DEBUG
            for(int i = 0; i<this.listOfTagsAndType.size(); i++){
                System.out.println(this.listOfTagsAndType.get(i));

            }
            * 
            */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static Node searchXML(Node n, String address){
        NodeList nList = n.getChildNodes();
        int temp = 0;
        for (temp = 0; temp < nList.getLength(); temp++) {
            Node chapterNode = nList.item(temp);
            if (chapterNode.getNodeType() == Node.ELEMENT_NODE) {
                Element chapter = (Element) chapterNode;
                if (chapter.getAttribute("xml:id").equals(address)){
                    /*
                     * DEBUG
                    System.out.println("Chapter id : " + chapter.getAttribute("xml:id"));
                    System.out.println("----------------------------");
                     * 
                     */
                    break;
                }
            }
        }
        return nList.item(temp);
    }
    public static Node searchXMLTag(Node n, String tag){
        NodeList nList = n.getChildNodes();
        int temp = 0;
        for (temp = 0; temp < nList.getLength(); temp++) {
            Node chapterNode = nList.item(temp);
            if (chapterNode.getNodeType() == Node.ELEMENT_NODE) {
                Element chapter = (Element) chapterNode;
                if (chapter.getTagName().equals(tag)){
                    break;
                }
            }
        }
        return nList.item(temp);
    }

    public static ArrayList tagsAndTypeGrab(String ref, Document doc){
        //fonction qui récupère les tags des champs dans un tableau d'un module DICOM
        ArrayList listOfTags = new ArrayList();
        String[] parts = ref.split("\\.");
        Node myNodeC = searchXML(doc.getFirstChild(), "chapter_C");
        String totParts = new String("");
        for(int i = 1; i<parts.length; i++){
            totParts = totParts + "." + parts[i];
            myNodeC = searchXML(myNodeC, "sect_C" + totParts);
        }
        myNodeC = searchXMLTag(myNodeC, "table");
        Element tbody = (Element) searchXMLTag(myNodeC, "tbody");
        /*
         * DEBUG
        System.out.println(tbody.getTagName());
        System.out.println("----------------------------");
         * 
         */
        NodeList trList = tbody.getElementsByTagName("tr");
        for (int temp = 0; temp < trList.getLength(); temp++){//boucle sur les tr du tbody
            Element elemTR = (Element) trList.item(temp);
            NodeList tdList = elemTR.getElementsByTagName("td");
            if (tdList.getLength()==4){//conidition qui séléctionne les lignes ou il y a 4 colonnes
                Element elemAttributeName = (Element) tdList.item(0);
                NodeList paraListAttributeName = elemAttributeName.getElementsByTagName("para");
                String attributeName = paraListAttributeName.item(0).getTextContent();                
                Element elemTAG = (Element) tdList.item(1);
                NodeList paraListTAG = elemTAG.getElementsByTagName("para");
                String tag = paraListTAG.item(0).getTextContent();
                Element elemType = (Element) tdList.item(2);
                NodeList paraListType = elemType.getElementsByTagName("para");
                String type = paraListType.item(0).getTextContent();
                ArrayList field = new ArrayList();
                field.add(0, tag);
                field.add(1, attributeName);
                field.add(2, type);
                listOfTags.add(field);
            }
        }            
        return listOfTags;
    }
   
    public static ArrayList searchReferenceXML(String modalityTable, Document doc){
        //fonction qui cherche et renvoie un String[] de tout les xref d'un tableau
        ArrayList listOfReferences = new ArrayList();
        
        String[] parts = modalityTable.split("\\.");
        System.out.println(parts[0]);
        Node myNodeA = searchXML(doc.getFirstChild(), "chapter_A");
        String totParts = new String("");
        for(int i = 1; i<parts.length; i++){
            totParts = totParts + "." + parts[i];
            myNodeA = searchXML(myNodeA, "sect_A" + totParts);
        }
        myNodeA = searchXMLTag(myNodeA, "table");
        Element tbody = (Element) searchXMLTag(myNodeA, "tbody");
        /*
         * DEBUG
        System.out.println(tbody.getTagName());
        System.out.println("----------------------------");
         * 
         */
        
        NodeList trList = tbody.getElementsByTagName("tr");
        for (int temp = 0; temp < trList.getLength(); temp++){//boucle sur les tr du tbody
            Element elemTR = (Element) trList.item(temp);
            NodeList tdList = elemTR.getElementsByTagName("td");
            for(int temp2 = 0; temp2 < tdList.getLength(); temp2++){//boucle sur les td du tr
                Element elemTD = (Element) tdList.item(temp2);
                NodeList paraList = elemTD.getElementsByTagName("para");
                for(int temp3 = 0; temp3 < paraList.getLength(); temp3++){//boucle sur les para du td
                    Element elemPara = (Element) paraList.item(temp3);
                    NodeList xrefList = elemPara.getElementsByTagName("xref");
                    if(xrefList.getLength() == 1){
                        Element xref = (Element) xrefList.item(0);                       
                        listOfReferences.add(xref.getAttribute("linkend"));
                    }
                }
            }
        }
        return listOfReferences;
    }        
 }