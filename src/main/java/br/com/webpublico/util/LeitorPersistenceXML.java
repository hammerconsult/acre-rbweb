/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

/**
 * @author munif
 */
public class LeitorPersistenceXML {

    private static LeitorPersistenceXML instancia = new LeitorPersistenceXML();
    private String nomeDataSource = "webPublicoJNDI";

    private LeitorPersistenceXML() {
        arrumaNome();
    }

    public static LeitorPersistenceXML getInstance() {
        return instancia;
    }

    public String getNomeDataSource() {
        return nomeDataSource;
    }

    public void setNomeDataSource(String nomeDataSource) {
        this.nomeDataSource = nomeDataSource;
    }

    private void arrumaNome() {
        try {
            InputStream resourceAsStream = LeitorPersistenceXML.class.getResourceAsStream("/META-INF/persistence.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(resourceAsStream);
            NodeList jtas = doc.getElementsByTagName("jta-data-source");
            if (jtas.getLength() > 0) {
                Node item = jtas.item(0);
                nomeDataSource = item.getTextContent();
            }
        } catch (Exception ex) {
            //System.out.println("Problemas ao obter o nome do JNDI");
            ex.printStackTrace();
            //System.out.println("--------------------------\n\n");
        }
    }

}
