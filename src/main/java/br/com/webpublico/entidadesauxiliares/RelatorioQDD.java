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
public class RelatorioQDD {

    private Long idAcao;
    private Long idSubAcao;
    private Long id;
    private Long idSuperior;
    private Long idUnidade;
    private String descricao;
    private String descricaoAcao;
    private String codigoUnidade;
    private String descricaoUnidade;
    private String codigoOrgao;
    private String descricaoOrgao;
    private String funcional;
    private String codigoConta;
    private String descricaoConta;
    private String codigoFonte;
    private BigDecimal inicial;
    private BigDecimal suplementar;
    private BigDecimal reduzido;
    private BigDecimal valorProvisao;
    private BigDecimal empenhado;
    private BigDecimal anulado;
    private BigDecimal liquidado;
    private BigDecimal aLiquidar;
    private BigDecimal pago;
    private BigDecimal aPagar;
    private BigDecimal disponivel;
    private BigDecimal nivel;
    private List<RelatorioQDD> listaDeContas;

    public RelatorioQDD() {
        valorProvisao = BigDecimal.ZERO;
        empenhado = BigDecimal.ZERO;
        anulado = BigDecimal.ZERO;
        liquidado = BigDecimal.ZERO;
        aLiquidar = BigDecimal.ZERO;
        pago = BigDecimal.ZERO;
        aPagar = BigDecimal.ZERO;
        disponivel = BigDecimal.ZERO;
        listaDeContas = new ArrayList<>();
    }

    public Long getIdAcao() {
        return idAcao;
    }

    public void setIdAcao(Long idAcao) {
        this.idAcao = idAcao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdSuperior() {
        return idSuperior;
    }

    public void setIdSuperior(Long idSuperior) {
        this.idSuperior = idSuperior;
    }

    public Long getIdUnidade() {
        return idUnidade;
    }

    public void setIdUnidade(Long idUnidade) {
        this.idUnidade = idUnidade;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
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

    public String getCodigoConta() {
        return codigoConta;
    }

    public void setCodigoConta(String codigoConta) {
        this.codigoConta = codigoConta;
    }

    public String getDescricaoConta() {
        return descricaoConta;
    }

    public void setDescricaoConta(String descricaoConta) {
        this.descricaoConta = descricaoConta;
    }

    public String getCodigoFonte() {
        return codigoFonte;
    }

    public void setCodigoFonte(String codigoFonte) {
        this.codigoFonte = codigoFonte;
    }

    public BigDecimal getValorProvisao() {
        return valorProvisao;
    }

    public void setValorProvisao(BigDecimal valorProvisao) {
        this.valorProvisao = valorProvisao;
    }

    public BigDecimal getEmpenhado() {
        return empenhado;
    }

    public void setEmpenhado(BigDecimal empenhado) {
        this.empenhado = empenhado;
    }

    public BigDecimal getAnulado() {
        return anulado;
    }

    public void setAnulado(BigDecimal anulado) {
        this.anulado = anulado;
    }

    public BigDecimal getLiquidado() {
        return liquidado;
    }

    public void setLiquidado(BigDecimal liquidado) {
        this.liquidado = liquidado;
    }

    public BigDecimal getaLiquidar() {
        return aLiquidar;
    }

    public void setaLiquidar(BigDecimal aLiquidar) {
        this.aLiquidar = aLiquidar;
    }

    public BigDecimal getPago() {
        return pago;
    }

    public void setPago(BigDecimal pago) {
        this.pago = pago;
    }

    public BigDecimal getaPagar() {
        return aPagar;
    }

    public void setaPagar(BigDecimal aPagar) {
        this.aPagar = aPagar;
    }

    public BigDecimal getDisponivel() {
        return disponivel;
    }

    public void setDisponivel(BigDecimal disponivel) {
        this.disponivel = disponivel;
    }

    public BigDecimal getNivel() {
        return nivel;
    }

    public void setNivel(BigDecimal nivel) {
        this.nivel = nivel;
    }

    public List<RelatorioQDD> getListaDeContas() {
        return listaDeContas;
    }

    public void setListaDeContas(List<RelatorioQDD> listaDeContas) {
        this.listaDeContas = listaDeContas;
    }

    public Long getIdSubAcao() {
        return idSubAcao;
    }

    public void setIdSubAcao(Long idSubAcao) {
        this.idSubAcao = idSubAcao;
    }

    public BigDecimal getInicial() {
        return inicial;
    }

    public void setInicial(BigDecimal inicial) {
        this.inicial = inicial;
    }

    public BigDecimal getSuplementar() {
        return suplementar;
    }

    public void setSuplementar(BigDecimal suplementar) {
        this.suplementar = suplementar;
    }

    public BigDecimal getReduzido() {
        return reduzido;
    }

    public void setReduzido(BigDecimal reduzido) {
        this.reduzido = reduzido;
    }
}
