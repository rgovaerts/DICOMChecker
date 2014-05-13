/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DICOMChecker;

import java.io.IOException;
import java.io.StringWriter;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author INFO-H-400
 */
public final class XmlJTree extends JTree{

    DefaultTreeModel dtModel=null;

    /**
     * XmlJTree constructor
     * @param filePath
     */
    XmlJTree(String filePath){
        if(filePath!=null)
        setPath(filePath);
    }

    public void setPath(String filePath){
        Node root = null;
        String val = null;
        /*
            Parse the xml file
        */
        try {
            DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(filePath);
            System.out.print(doc.getTextContent());
            root = (Node) doc.getDocumentElement();
            //val = doc.getTextContent();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,"Can't parse file",
                            "Error", JOptionPane.ERROR_MESSAGE);
            return;
        } catch( ParserConfigurationException ex){
            JOptionPane.showMessageDialog(null,"Can't parse file",
                            "Error", JOptionPane.ERROR_MESSAGE);
            return;
            
        } catch( SAXException ex){
            JOptionPane.showMessageDialog(null,"Can't parse file",
                            "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
            
        /*
            if any result set the appropriate model to the jTree
        */
        if(root!=null){
            dtModel= new DefaultTreeModel(builtTreeNode(root));
            
            this.setModel(dtModel);
        }
    }

    /**
     * fullTreeNode Method
     * construct the full jTree from any xml file
     * this method is recursive
     * @param root
     * @return DefaultMutableTreeNode
     */
    private DefaultMutableTreeNode builtTreeNode(Node root){
        DefaultMutableTreeNode dmtNode;

        dmtNode = new DefaultMutableTreeNode(root.getNodeName());
            NodeList nodeList = root.getChildNodes();
            for (int count = 0; count < nodeList.getLength(); count++) {
                Node tempNode = nodeList.item(count);
                // make sure it's element node.
                if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
                    if (tempNode.hasChildNodes()) {
                        // loop again if has child nodes
                        dmtNode.add(builtTreeNode(tempNode));
                    }
                }
            }
        return dmtNode;
    }
    
    public String node2String(Node node) throws TransformerFactoryConfigurationError, TransformerException {
        // you may prefer to use single instances of Transformer, and
        // StringWriter rather than create each time. That would be up to your
        // judgement and whether your app is single threaded etc
        StreamResult xmlOutput = new StreamResult(new StringWriter());
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.transform(new DOMSource(node), xmlOutput);
        return xmlOutput.getWriter().toString();
    }

    
}
