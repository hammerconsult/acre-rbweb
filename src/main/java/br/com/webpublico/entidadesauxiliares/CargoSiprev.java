package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.EsferaDoPoder;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 25/09/14
 * Time: 10:24
 * To change this template use File | Settings | File Templates.
 */
public class CargoSiprev {
    private String descricao;
    private BigDecimal valorMinimo;
    private BigDecimal valorMaximo;
    private Date inicioVigencia;
    private Date finalVigencia;
    private String codigoCargo;
    private boolean permiteAcumulo;
    private String nomeEntidade;
    private EsferaDoPoder esferaDoPoder;
    private String codigoCarreira;
    private BigDecimal atoLegalId;
    private Date atoDataPublicacao;
    private Date atoDataEmissao;
    private String atoNumero;
    private BigDecimal atoAno;
    private String atoEmenta;
    private BigDecimal aposentadoriaEspecial;
    private String tipoCargoAcumulavelSIPREV;
    private String tipoContagemSIPREV;
    private BigDecimal dedicacaoExclusiva;
    private BigDecimal tecnicoCientifico;
    private BigDecimal id;
    private BigDecimal planoId;

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public Date getAtoDataPublicacao() {
        return atoDataPublicacao;
    }

    public void setAtoDataPublicacao(Date atoDataPublicacao) {
        this.atoDataPublicacao = atoDataPublicacao;
    }

    public BigDecimal getAposentadoriaEspecial() {
        return aposentadoriaEspecial;
    }

    public void setAposentadoriaEspecial(BigDecimal aposentadoriaEspecial) {
        this.aposentadoriaEspecial = aposentadoriaEspecial;
    }

    public String getTipoCargoAcumulavelSIPREV() {
        return tipoCargoAcumulavelSIPREV;
    }

    public void setTipoCargoAcumulavelSIPREV(String tipoCargoAcumulavelSIPREV) {
        this.tipoCargoAcumulavelSIPREV = tipoCargoAcumulavelSIPREV;
    }

    public BigDecimal getDedicacaoExclusiva() {
        return dedicacaoExclusiva;
    }

    public void setDedicacaoExclusiva(BigDecimal dedicacaoExclusiva) {
        this.dedicacaoExclusiva = dedicacaoExclusiva;
    }

    public Date getAtoDataEmissao() {
        return atoDataEmissao;
    }

    public BigDecimal getTecnicoCientifico() {
        return tecnicoCientifico;
    }

    public void setTecnicoCientifico(BigDecimal tecnicoCientifico) {
        this.tecnicoCientifico = tecnicoCientifico;
    }

    public void setAtoDataEmissao(Date atoDataEmissao) {
        this.atoDataEmissao = atoDataEmissao;
    }

    public String getAtoNumero() {
        return atoNumero;
    }

    public void setAtoNumero(String atoNumero) {
        this.atoNumero = atoNumero;
    }

    public String getTipoContagemSIPREV() {
        return tipoContagemSIPREV;
    }

    public void setTipoContagemSIPREV(String tipoContagemSIPREV) {
        this.tipoContagemSIPREV = tipoContagemSIPREV;
    }

    public BigDecimal getAtoAno() {
        return atoAno;
    }

    public void setAtoAno(BigDecimal atoAno) {
        this.atoAno = atoAno;
    }

    public String getAtoEmenta() {
        return atoEmenta;
    }

    public void setAtoEmenta(String atoEmenta) {
        this.atoEmenta = atoEmenta;
    }

    public BigDecimal getAtoLegalId() {
        return atoLegalId;
    }


    public void setAtoLegalId(BigDecimal atoLegalId) {
        this.atoLegalId = atoLegalId;
    }

    public String getNomeEntidade() {
        return nomeEntidade;
    }

    public void setNomeEntidade(String nomeEntidade) {
        this.nomeEntidade = nomeEntidade;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValorMinimo() {
        return valorMinimo;
    }

    public void setValorMinimo(BigDecimal valorMinimo) {
        this.valorMinimo = valorMinimo;
    }

    public BigDecimal getValorMaximo() {
        return valorMaximo;
    }

    public void setValorMaximo(BigDecimal valorMaximo) {
        this.valorMaximo = valorMaximo;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public String getCodigoCargo() {
        return codigoCargo;
    }

    public void setCodigoCargo(String codigoCargo) {
        this.codigoCargo = codigoCargo;
    }

    public boolean isPermiteAcumulo() {
        return permiteAcumulo;
    }

    public void setPermiteAcumulo(boolean permiteAcumulo) {
        this.permiteAcumulo = permiteAcumulo;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public EsferaDoPoder getEsferaDoPoder() {
        return esferaDoPoder;
    }

    public void setEsferaDoPoder(EsferaDoPoder esferaDoPoder) {
        this.esferaDoPoder = esferaDoPoder;
    }

    public String getCodigoCarreira() {
        return codigoCarreira;
    }

    public void setCodigoCarreira(String codigoCarreira) {
        this.codigoCarreira = codigoCarreira;
    }

    public BigDecimal getPlanoId() {
        return planoId;
    }

    public void setPlanoId(BigDecimal planoId) {
        this.planoId = planoId;
    }
}
