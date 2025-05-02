package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoLocalizacao;
import br.com.webpublico.enums.TipoMateriaPrima;
import br.com.webpublico.enums.TipoSimNao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Audited
@Etiqueta("Enquadramento Ambiental")
public class EnquadramentoAmbiental extends SuperEntidade implements Serializable {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;
    @ManyToOne
    @Etiqueta("Cadastro Econômico")
    private CadastroEconomico cadastroEconomico;
    @Enumerated(EnumType.STRING)
    private TipoMateriaPrima tipoMateriaPrima;
    @Enumerated(EnumType.STRING)
    private TipoLocalizacao tipoLocalizacao;
    private BigDecimal areaConstruida;
    private BigDecimal capacidadeInstalada;
    private BigDecimal areaTerreno;
    private TipoSimNao geracaoRuidosEVibracao;
    private TipoSimNao cobertaImpermeavel;
    private TipoSimNao especiesProtegidaPorLei;
    private TipoSimNao terrenoParticular;
    private Integer quantidadeEspeciesSuprimidas;
    private Date inicioVigencia;
    private Date finalVigencia;

    public EnquadramentoAmbiental() {
        areaConstruida = BigDecimal.ZERO;
        capacidadeInstalada = BigDecimal.ZERO;
        areaTerreno = BigDecimal.ZERO;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public TipoMateriaPrima getTipoMateriaPrima() {
        return tipoMateriaPrima;
    }

    public void setTipoMateriaPrima(TipoMateriaPrima tipoMateriaPrima) {
        this.tipoMateriaPrima = tipoMateriaPrima;
    }

    public TipoLocalizacao getTipoLocalizacao() {
        return tipoLocalizacao;
    }

    public void setTipoLocalizacao(TipoLocalizacao tipoLocalizacao) {
        this.tipoLocalizacao = tipoLocalizacao;
    }

    public BigDecimal getAreaConstruida() {
        return areaConstruida;
    }

    public void setAreaConstruida(BigDecimal areaConstruida) {
        this.areaConstruida = areaConstruida;
    }

    public BigDecimal getCapacidadeInstalada() {
        return capacidadeInstalada;
    }

    public void setCapacidadeInstalada(BigDecimal capacidadeInstalada) {
        this.capacidadeInstalada = capacidadeInstalada;
    }

    public BigDecimal getAreaTerreno() {
        return areaTerreno;
    }

    public void setAreaTerreno(BigDecimal areaTerreno) {
        this.areaTerreno = areaTerreno;
    }

    public TipoSimNao getGeracaoRuidosEVibracao() {
        return geracaoRuidosEVibracao;
    }

    public void setGeracaoRuidosEVibracao(TipoSimNao geracaoRuidosEVibracao) {
        this.geracaoRuidosEVibracao = geracaoRuidosEVibracao;
    }

    public TipoSimNao getCobertaImpermeavel() {
        return cobertaImpermeavel;
    }

    public void setCobertaImpermeavel(TipoSimNao cobertaImpermeavel) {
        this.cobertaImpermeavel = cobertaImpermeavel;
    }

    public TipoSimNao getEspeciesProtegidaPorLei() {
        return especiesProtegidaPorLei;
    }

    public void setEspeciesProtegidaPorLei(TipoSimNao especiesProtegidaPorLei) {
        this.especiesProtegidaPorLei = especiesProtegidaPorLei;
    }

    public TipoSimNao getTerrenoParticular() {
        return terrenoParticular;
    }

    public void setTerrenoParticular(TipoSimNao terrenoParticular) {
        this.terrenoParticular = terrenoParticular;
    }

    public Integer getQuantidadeEspeciesSuprimidas() {
        return quantidadeEspeciesSuprimidas;
    }

    public void setQuantidadeEspeciesSuprimidas(Integer quantidadeEspeciesSuprimidas) {
        this.quantidadeEspeciesSuprimidas = quantidadeEspeciesSuprimidas;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public boolean isVigente() {
        return finalVigencia == null;
    }
}
