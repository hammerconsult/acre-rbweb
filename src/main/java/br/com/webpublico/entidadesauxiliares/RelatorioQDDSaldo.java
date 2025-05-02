package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 06/10/15
 * Time: 14:19
 * To change this template use File | Settings | File Templates.
 */
public class RelatorioQDDSaldo {

    private String codigoExtensao;
    private String descricaoExtensao;
    private String tipoContaCorrente;
    private Long idAcao;
    private Long idSubAcao;
    private Long idUnidade;
    private Long idAcaoPrincipal;
    private String descricaoAcao;
    private String codigoUnidade;
    private String descricaoUnidade;
    private String codigoOrgao;
    private String descricaoOrgao;
    private String funcional;
    private String conta;
    private String fonte;
    private String esferaOrcamentaria;
    private BigDecimal dotacaoInicial;
    private BigDecimal suplementado;
    private BigDecimal reduzido;
    private BigDecimal bloqueado;
    private BigDecimal bloqueadoPorLicitacao;
    private BigDecimal dotacaoAtual;
    private BigDecimal empenhado;
    private BigDecimal saldo;
    private BigDecimal liquidado;
    private BigDecimal pago;
    private String programaCodigo;
    private String programaDenominacao;
    private String fonteDescricao;
    private String acaoPrincipalDescricao;
    private String acaoPrincipalCodigo;
    private String codigoAplicacaoTCE;
    private List<RelatorioQDDSaldo> contas;

    public RelatorioQDDSaldo() {
        contas = new ArrayList<>();
        dotacaoInicial = BigDecimal.ZERO;
        suplementado = BigDecimal.ZERO;
        reduzido = BigDecimal.ZERO;
        bloqueado = BigDecimal.ZERO;
        bloqueadoPorLicitacao = BigDecimal.ZERO;
        dotacaoAtual = BigDecimal.ZERO;
        empenhado = BigDecimal.ZERO;
        saldo = BigDecimal.ZERO;
        liquidado = BigDecimal.ZERO;
        pago = BigDecimal.ZERO;

    }

    public String getCodigoExtensao() {
        return codigoExtensao;
    }

    public void setCodigoExtensao(String codigoExtensao) {
        this.codigoExtensao = codigoExtensao;
    }

    public String getDescricaoExtensao() {
        return descricaoExtensao;
    }

    public void setDescricaoExtensao(String descricaoExtensao) {
        this.descricaoExtensao = descricaoExtensao;
    }

    public String getTipoContaCorrente() {
        return tipoContaCorrente;
    }

    public void setTipoContaCorrente(String tipoContaCorrente) {
        this.tipoContaCorrente = tipoContaCorrente;
    }

    public Long getIdAcao() {
        return idAcao;
    }

    public void setIdAcao(Long idAcao) {
        this.idAcao = idAcao;
    }

    public Long getIdSubAcao() {
        return idSubAcao;
    }

    public void setIdSubAcao(Long idSubAcao) {
        this.idSubAcao = idSubAcao;
    }

    public Long getIdUnidade() {
        return idUnidade;
    }

    public void setIdUnidade(Long idUnidade) {
        this.idUnidade = idUnidade;
    }

    public String getDescricaoAcao() {
        return descricaoAcao;
    }

    public void setDescricaoAcao(String descricaoAcao) {
        this.descricaoAcao = descricaoAcao;
    }

    public String getCodigoUnidade() {
        return codigoUnidade;
    }

    public void setCodigoUnidade(String codigoUnidade) {
        this.codigoUnidade = codigoUnidade;
    }

    public String getDescricaoUnidade() {
        return descricaoUnidade;
    }

    public void setDescricaoUnidade(String descricaoUnidade) {
        this.descricaoUnidade = descricaoUnidade;
    }

    public String getCodigoOrgao() {
        return codigoOrgao;
    }

    public void setCodigoOrgao(String codigoOrgao) {
        this.codigoOrgao = codigoOrgao;
    }

    public String getDescricaoOrgao() {
        return descricaoOrgao;
    }

    public void setDescricaoOrgao(String descricaoOrgao) {
        this.descricaoOrgao = descricaoOrgao;
    }

    public String getFuncional() {
        return funcional;
    }

    public void setFuncional(String funcional) {
        this.funcional = funcional;
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public String getFonte() {
        return fonte;
    }

    public void setFonte(String fonte) {
        this.fonte = fonte;
    }

    public String getEsferaOrcamentaria() {
        return esferaOrcamentaria;
    }

    public void setEsferaOrcamentaria(String esferaOrcamentaria) {
        this.esferaOrcamentaria = esferaOrcamentaria;
    }

    public BigDecimal getDotacaoInicial() {
        return dotacaoInicial;
    }

    public void setDotacaoInicial(BigDecimal dotacaoInicial) {
        this.dotacaoInicial = dotacaoInicial;
    }

    public BigDecimal getSuplementado() {
        return suplementado;
    }

    public void setSuplementado(BigDecimal suplementado) {
        this.suplementado = suplementado;
    }

    public BigDecimal getReduzido() {
        return reduzido;
    }

    public void setReduzido(BigDecimal reduzido) {
        this.reduzido = reduzido;
    }

    public BigDecimal getBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(BigDecimal bloqueado) {
        this.bloqueado = bloqueado;
    }

    public BigDecimal getBloqueadoPorLicitacao() {
        return bloqueadoPorLicitacao;
    }

    public void setBloqueadoPorLicitacao(BigDecimal bloqueadoPorLicitacao) {
        this.bloqueadoPorLicitacao = bloqueadoPorLicitacao;
    }

    public BigDecimal getDotacaoAtual() {
        return dotacaoAtual;
    }

    public void setDotacaoAtual(BigDecimal dotacaoAtual) {
        this.dotacaoAtual = dotacaoAtual;
    }

    public BigDecimal getEmpenhado() {
        return empenhado;
    }

    public void setEmpenhado(BigDecimal empenhado) {
        this.empenhado = empenhado;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public List<RelatorioQDDSaldo> getContas() {
        return contas;
    }

    public void setContas(List<RelatorioQDDSaldo> contas) {
        this.contas = contas;
    }

    public BigDecimal getLiquidado() {
        return liquidado;
    }

    public void setLiquidado(BigDecimal liquidado) {
        this.liquidado = liquidado;
    }

    public BigDecimal getPago() {
        return pago;
    }

    public void setPago(BigDecimal pago) {
        this.pago = pago;
    }

    public String getProgramaCodigo() {
        return programaCodigo;
    }

    public void setProgramaCodigo(String programaCodigo) {
        this.programaCodigo = programaCodigo;
    }

    public String getProgramaDenominacao() {
        return programaDenominacao;
    }

    public void setProgramaDenominacao(String programaDenominacao) {
        this.programaDenominacao = programaDenominacao;
    }

    public Long getIdAcaoPrincipal() {
        return idAcaoPrincipal;
    }

    public void setIdAcaoPrincipal(Long idAcaoPrincipal) {
        this.idAcaoPrincipal = idAcaoPrincipal;
    }

    public String getFonteDescricao() {
        return fonteDescricao;
    }

    public void setFonteDescricao(String fonteDescricao) {
        this.fonteDescricao = fonteDescricao;
    }

    public String getAcaoPrincipalDescricao() {
        return acaoPrincipalDescricao;
    }

    public void setAcaoPrincipalDescricao(String acaoPrincipalDescricao) {
        this.acaoPrincipalDescricao = acaoPrincipalDescricao;
    }

    public String getAcaoPrincipalCodigo() {
        return acaoPrincipalCodigo;
    }

    public void setAcaoPrincipalCodigo(String acaoPrincipalCodigo) {
        this.acaoPrincipalCodigo = acaoPrincipalCodigo;
    }

    public String getCodigoAplicacaoTCE() {
        return codigoAplicacaoTCE;
    }

    public void setCodigoAplicacaoTCE(String codigoAplicacaoTCE) {
        this.codigoAplicacaoTCE = codigoAplicacaoTCE;
    }
}

