package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoPeriodoFase;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: wiplash
 * Date: 16/10/13
 * Time: 15:25
 * To change this template use File | Settings | File Templates.
 */
@GrupoDiagrama(nome = "Segurança")
@Entity
@Audited
public class PeriodoFaseUnidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Etiqueta("Início da Vigência")
    @Obrigatorio
    private Date inicioVigencia;
    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Etiqueta("Fim da Vigência")
    @Obrigatorio
    private Date fimVigencia;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    private SituacaoPeriodoFase situacaoPeriodoFase;
    @Transient
    private Long criadoEm;
    @ManyToOne
    @Obrigatorio
    private PeriodoFase periodoFase;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Unidade Organizacional")
    @Obrigatorio
    private UnidadeOrganizacional unidadeOrganizacional;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "periodoFaseUnidade")
    private List<PeriodoFaseUsuario> usuarios;

    public PeriodoFaseUnidade() {
        criadoEm = System.nanoTime();
        usuarios = new ArrayList<PeriodoFaseUsuario>();
    }

    public PeriodoFaseUnidade(Date inicioVigencia, Date fimVigencia, SituacaoPeriodoFase situacaoPeriodoFase, PeriodoFase periodoFase, UnidadeOrganizacional unidadeOrganizacional) {
        this.inicioVigencia = inicioVigencia;
        this.fimVigencia = fimVigencia;
        this.situacaoPeriodoFase = situacaoPeriodoFase;
        this.periodoFase = periodoFase;
        this.unidadeOrganizacional = unidadeOrganizacional;
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public SituacaoPeriodoFase getSituacaoPeriodoFase() {
        return situacaoPeriodoFase;
    }

    public void setSituacaoPeriodoFase(SituacaoPeriodoFase situacaoPeriodoFase) {
        this.situacaoPeriodoFase = situacaoPeriodoFase;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public PeriodoFase getPeriodoFase() {
        return periodoFase;
    }

    public void setPeriodoFase(PeriodoFase periodoFase) {
        this.periodoFase = periodoFase;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public List<PeriodoFaseUsuario> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<PeriodoFaseUsuario> usuarios) {
        this.usuarios = usuarios;
    }

    @Override
    public boolean equals(Object o) {
        return IdentidadeDaEntidade.calcularEquals(this, o);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    public boolean isVigente(Date dataOperacao) {
        return inicioVigencia.compareTo(Util.getDataHoraMinutoSegundoZerado(dataOperacao)) < 0 && (fimVigencia == null || (fimVigencia.compareTo(Util.getDataHoraMinutoSegundoZerado(dataOperacao)) >= 0));  //To change body of created methods use File | Settings | File Templates.
    }

    @Override
    public String toString() {
        return unidadeOrganizacional.getDescricao() + " ( " + DataUtil.getDataFormatada(inicioVigencia) + " - " + DataUtil.getDataFormatada(fimVigencia) + " ) - " + situacaoPeriodoFase.getDescricao();
    }
}
