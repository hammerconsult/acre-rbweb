package br.com.webpublico.util;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

/**
 * @author Fabio
 */
public class XmlUtil {

    /**
     * @see Método para transformar um objeto em xml
     */
    public static String marshal(Object object) {
        final StringWriter out = new StringWriter();
        JAXBContext context = null;
        try {
            context = JAXBContext.newInstance(object.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "ISO-8859-1");
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(object, new StreamResult(out));
        } catch (PropertyException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return out.toString();
    }

}
