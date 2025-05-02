package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author AndreGustavo
 */
@Entity
@Audited
@GrupoDiagrama(nome = "RBTrans")
@Etiqueta("Ocorrência Autuação RBTrans")
public class OcorrenciaAutuacaoRBTrans implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private AutuacaoFiscalizacaoRBTrans autuacaoFiscalizacao;
    @ManyToOne
    private OcorrenciaFiscalizacaoRBTrans ocorrenciaFiscalizacao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AutuacaoFiscalizacaoRBTrans getAutuacaoFiscalizacao() {
        return autuacaoFiscalizacao;
    }

    public void setAutuacaoFiscalizacao(AutuacaoFiscalizacaoRBTrans autuacaoFiscalizacao) {
        this.autuacaoFiscalizacao = autuacaoFiscalizacao;
    }

    public OcorrenciaFiscalizacaoRBTrans getOcorrenciaFiscalizacao() {
        return ocorrenciaFiscalizacao;
    }

    public void setOcorrenciaFiscalizacao(OcorrenciaFiscalizacaoRBTrans ocorrenciaFiscalizacao) {
        this.ocorrenciaFiscalizacao = ocorrenciaFiscalizacao;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OcorrenciaAutuacaoRBTrans)) {
            return false;
        }
        OcorrenciaAutuacaoRBTrans other = (OcorrenciaAutuacaoRBTrans) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.OcorrenciaAutuacaoRBTrans[ id=" + id + " ]";
    }
}
