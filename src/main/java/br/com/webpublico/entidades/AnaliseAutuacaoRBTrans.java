package br.com.webpublico.entidades;

import br.com.webpublico.enums.ParecerAnalise;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author AndreGustavo
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RBTrans")
@Etiqueta("Análise da Autuação")
public class AnaliseAutuacaoRBTrans implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataAnalise;
    private String descricao;
    @Enumerated(EnumType.STRING)
    private ParecerAnalise parecerAnalise;
    @OneToMany(mappedBy = "analiseAutuacaoRBTrans", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<CampoInconsistenciaAutoInfracaoRBTrans> camposInconsistentes;
    @Transient
    private Long criadoEm;

    public AnaliseAutuacaoRBTrans() {
        criadoEm = System.nanoTime();
        camposInconsistentes = new ArrayList<>();
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public List<CampoInconsistenciaAutoInfracaoRBTrans> getCamposInconsistentes() {
        return camposInconsistentes;
    }

    public void setCamposInconsistentes(List<CampoInconsistenciaAutoInfracaoRBTrans> camposInconsistentes) {
        this.camposInconsistentes = camposInconsistentes;
    }

    public ParecerAnalise getParecerAnalise() {
        return parecerAnalise;
    }

    public void setParecerAnalise(ParecerAnalise parecerAnalise) {
        this.parecerAnalise = parecerAnalise;
    }

    public Date getDataAnalise() {
        return dataAnalise;
    }

    public void setDataAnalise(Date dataAnalise) {
        this.dataAnalise = dataAnalise;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        if (!(object instanceof AnaliseAutuacaoRBTrans)) {
            return false;
        }
        AnaliseAutuacaoRBTrans other = (AnaliseAutuacaoRBTrans) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.AnaliseAutuacaoRBTrans[ id=" + id + " ]";
    }
}
