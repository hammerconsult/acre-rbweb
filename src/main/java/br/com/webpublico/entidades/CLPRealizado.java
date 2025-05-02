/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoLancamentoCLPRealizado;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author jaimaum
 */
@Entity

@Audited
@Etiqueta("CLP Realizado")
public class CLPRealizado implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataCLPRealizado;
    @ManyToOne
    private CLP clp;
    @OneToMany(mappedBy = "cLPRealizado", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<CLPConfiguracaoParametro> cLPConfiguracaoParametro;
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    @Enumerated(EnumType.STRING)
    private TipoLancamentoCLPRealizado tipoLancamentoCLP;
    @Transient
    private Long criadoEm;

    public CLPRealizado() {
        criadoEm = System.nanoTime();
        cLPConfiguracaoParametro = new ArrayList<CLPConfiguracaoParametro>();
    }

    public CLPRealizado(Date dataCLPRealizado, CLP clp, List<CLPConfiguracaoParametro> cLPConfiguracaoParametro, UnidadeOrganizacional unidadeOrganizacional, TipoLancamentoCLPRealizado tipoLancamentoCLP) {
        this.dataCLPRealizado = dataCLPRealizado;
        this.clp = clp;
        this.cLPConfiguracaoParametro = cLPConfiguracaoParametro;
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.tipoLancamentoCLP = tipoLancamentoCLP;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CLP getClp() {
        return clp;
    }

    public void setClp(CLP clp) {
        this.clp = clp;
    }


    public List<CLPConfiguracaoParametro> getcLPConfiguracaoParametro() {
        return cLPConfiguracaoParametro;
    }

    public void setcLPConfiguracaoParametro(List<CLPConfiguracaoParametro> cLPConfiguracaoParametro) {
        this.cLPConfiguracaoParametro = cLPConfiguracaoParametro;
    }

    public Date getDataCLPRealizado() {
        return dataCLPRealizado;
    }

    public void setDataCLPRealizado(Date dataCLPRealizado) {
        this.dataCLPRealizado = dataCLPRealizado;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public TipoLancamentoCLPRealizado getTipoLancamentoCLP() {
        return tipoLancamentoCLP;
    }

    public void setTipoLancamentoCLP(TipoLancamentoCLPRealizado tipoLancamentoCLP) {
        this.tipoLancamentoCLP = tipoLancamentoCLP;
    }


    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.CLPRealizado[ id=" + id + " ]";
    }
}
