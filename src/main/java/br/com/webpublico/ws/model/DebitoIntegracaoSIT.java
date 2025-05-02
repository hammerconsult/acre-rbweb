package br.com.webpublico.ws.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

@XmlRootElement
public class DebitoIntegracaoSIT {
    @XmlElement
    private String setor;
    @XmlElement
    private String quadra;
    @XmlElement
    private String lote;
    @XmlElement
    private String autonoma;
    @XmlElement
    private Boolean temRestricao;
    @XmlElement
    private BigDecimal valorDevido;

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }

    public String getQuadra() {
        return quadra;
    }

    public void setQuadra(String quadra) {
        this.quadra = quadra;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public String getAutonoma() {
        return autonoma;
    }

    public void setAutonoma(String autonoma) {
        this.autonoma = autonoma;
    }

    public Boolean getTemRestricao() {
        return temRestricao;
    }

    public void setTemRestricao(Boolean temRestricao) {
        this.temRestricao = temRestricao;
    }

    public BigDecimal getValorDevido() {
        return valorDevido;
    }

    public void setValorDevido(BigDecimal valorDevido) {
        this.valorDevido = valorDevido;
    }
}
