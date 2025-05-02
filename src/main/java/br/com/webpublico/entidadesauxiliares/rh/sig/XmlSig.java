package br.com.webpublico.entidadesauxiliares.rh.sig;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;


@XmlRootElement
public class XmlSig implements Serializable {

    @XStreamAsAttribute
    private String version;
    @XStreamAsAttribute
    private String encoding;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}
