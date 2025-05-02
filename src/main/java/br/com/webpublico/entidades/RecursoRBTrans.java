package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoProcessoRBTrans;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author AndreGustavo
 */
@Entity
@Audited
@Etiqueta("Recurso RBTrans")
@GrupoDiagrama(nome = "RBTrans")
public class RecursoRBTrans implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private AnaliseRecursoRBTrans analiseRecursoRBTrans;
    @Enumerated(EnumType.STRING)
    private TipoProcessoRBTrans tipoProcessoRBTrans;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRecurso;
    @ManyToOne
    private AutuacaoFiscalizacaoRBTrans autuacaoFiscalizacao;
    private Long numeroProtocolo;
    private Integer anoProtocolo;
    private String teorProtocolo;
    @Transient
    private boolean apresentado;

    public RecursoRBTrans() {
        apresentado = false;
    }

    public boolean isApresentado() {
        return apresentado;
    }

    public void setApresentado(boolean apresentado) {
        this.apresentado = apresentado;
    }

    public AnaliseRecursoRBTrans getAnaliseRecursoRBTrans() {
        return analiseRecursoRBTrans;
    }

    public void setAnaliseRecursoRBTrans(AnaliseRecursoRBTrans analiseRecursoRBTrans) {
        this.analiseRecursoRBTrans = analiseRecursoRBTrans;
    }

    public Long getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(Long numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public Integer getAnoProtocolo() {
        return anoProtocolo;
    }

    public void setAnoProtocolo(Integer anoProtocolo) {
        this.anoProtocolo = anoProtocolo;
    }

    public String getTeorProtocolo() {
        return teorProtocolo;
    }

    public void setTeorProtocolo(String teorProtocolo) {
        this.teorProtocolo = teorProtocolo;
    }

    public AutuacaoFiscalizacaoRBTrans getAutuacaoFiscalizacao() {
        return autuacaoFiscalizacao;
    }

    public void setAutuacaoFiscalizacao(AutuacaoFiscalizacaoRBTrans autuacaoFiscalizacao) {
        this.autuacaoFiscalizacao = autuacaoFiscalizacao;
    }

    public Date getDataRecurso() {
        return dataRecurso;
    }

    public void setDataRecurso(Date dataRecurso) {
        this.dataRecurso = dataRecurso;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoProcessoRBTrans getTipoProcessoRBTrans() {
        return tipoProcessoRBTrans;
    }

    public void setTipoProcessoRBTrans(TipoProcessoRBTrans tipoProcessoRBTrans) {
        this.tipoProcessoRBTrans = tipoProcessoRBTrans;
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
        if (!(object instanceof RecursoRBTrans)) {
            return false;
        }
        RecursoRBTrans other = (RecursoRBTrans) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.RecursoRBTrans[ id=" + id + " ]";
    }
}
