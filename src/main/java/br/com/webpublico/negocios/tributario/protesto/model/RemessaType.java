package br.com.webpublico.negocios.tributario.protesto.model;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "remessaType", propOrder = {
        "nomeArquivo", "comarca"
})
@XmlRootElement(name = "remessa")
public class RemessaType {

    @XmlElement(name = "nome_arquivo", required = true)
    private String nomeArquivo;
    @XmlElement(name = "comarca", required = true)
    private ComarcaRemessaType comarca;

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public ComarcaRemessaType getComarca() {
        return comarca;
    }

    public void setComarca(ComarcaRemessaType comarca) {
        this.comarca = comarca;
    }
}
