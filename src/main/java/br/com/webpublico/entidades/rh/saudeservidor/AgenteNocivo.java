package br.com.webpublico.entidades.rh.saudeservidor;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.rh.esocial.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Audited
public class AgenteNocivo extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private RiscoOcupacional riscoOcupacional;


    @Enumerated(EnumType.STRING)
    private CodigoAgenteNocivo codigoAgenteNocivo;

    @Enumerated(EnumType.STRING)
    private TipoAvaliacaoAgenteNocivo tipoAvaliacaoAgenteNocivo;

    private String descricaoAgenteNocivo;

    private BigDecimal intensidadeConcentracao;


    private BigDecimal limiteTolerancia;

    @Enumerated(EnumType.STRING)
    private TipoUnidadeMedida tipoUnidadeMedida;

    private String tecnicaMedicao;

    @Enumerated(EnumType.STRING)
    private UtilizaEPC utilizaEPC;

    private Boolean epcEficaz;

    @Enumerated(EnumType.STRING)
    private UtilizaEPI utilizaEPI;

    private Boolean epiEficaz;


    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CodigoAgenteNocivo getCodigoAgenteNocivo() {
        return codigoAgenteNocivo;
    }

    public void setCodigoAgenteNocivo(CodigoAgenteNocivo codigoAgenteNocivo) {
        this.codigoAgenteNocivo = codigoAgenteNocivo;
    }

    public TipoAvaliacaoAgenteNocivo getTipoAvaliacaoAgenteNocivo() {
        return tipoAvaliacaoAgenteNocivo;
    }

    public void setTipoAvaliacaoAgenteNocivo(TipoAvaliacaoAgenteNocivo tipoAvaliacaoAgenteNocivo) {
        this.tipoAvaliacaoAgenteNocivo = tipoAvaliacaoAgenteNocivo;
    }

    public String getDescricaoAgenteNocivo() {
        return descricaoAgenteNocivo;
    }

    public void setDescricaoAgenteNocivo(String descricaoAgenteNocivo) {
        this.descricaoAgenteNocivo = descricaoAgenteNocivo;
    }

    public BigDecimal getIntensidadeConcentracao() {
        return intensidadeConcentracao;
    }

    public void setIntensidadeConcentracao(BigDecimal intensidadeConcentracao) {
        this.intensidadeConcentracao = intensidadeConcentracao;
    }

    public BigDecimal getLimiteTolerancia() {
        return limiteTolerancia;
    }

    public void setLimiteTolerancia(BigDecimal limiteTolerancia) {
        this.limiteTolerancia = limiteTolerancia;
    }

    public TipoUnidadeMedida getTipoUnidadeMedida() {
        return tipoUnidadeMedida;
    }

    public void setTipoUnidadeMedida(TipoUnidadeMedida tipoUnidadeMedida) {
        this.tipoUnidadeMedida = tipoUnidadeMedida;
    }

    public String getTecnicaMedicao() {
        return tecnicaMedicao;
    }

    public void setTecnicaMedicao(String tecnicaMedicao) {
        this.tecnicaMedicao = tecnicaMedicao;
    }

    public RiscoOcupacional getRiscoOcupacional() {
        return riscoOcupacional;
    }

    public void setRiscoOcupacional(RiscoOcupacional riscoOcupacional) {
        this.riscoOcupacional = riscoOcupacional;
    }

    public UtilizaEPC getUtilizaEPC() {
        return utilizaEPC;
    }

    public void setUtilizaEPC(UtilizaEPC utilizaEPC) {
        this.utilizaEPC = utilizaEPC;
    }

    public Boolean getEpcEficaz() {
        return epcEficaz != null ? epcEficaz : false;
    }

    public void setEpcEficaz(Boolean epcEficaz) {
        this.epcEficaz = epcEficaz;
    }

    public UtilizaEPI getUtilizaEPI() {
        return utilizaEPI;
    }

    public void setUtilizaEPI(UtilizaEPI utilizaEPI) {
        this.utilizaEPI = utilizaEPI;
    }

    public Boolean getEpiEficaz() {
        return epiEficaz != null ? epiEficaz : false;
    }

    public void setEpiEficaz(Boolean epiEficaz) {
        this.epiEficaz = epiEficaz;
    }
}
