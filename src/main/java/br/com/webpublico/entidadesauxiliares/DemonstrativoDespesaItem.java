package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: usuario
 * Date: 24/03/14
 * Time: 14:19
 * To change this template use File | Settings | File Templates.
 */
public class DemonstrativoDespesaItem {

    private Long id;
    private String codigo;
    private String descricao;
    private BigDecimal orcadaInicial;
    private BigDecimal valorSuplementar;
    private BigDecimal creditoEspecial;
    private BigDecimal creditoExtra;
    private BigDecimal anulacao;
    private BigDecimal empenhadoAno;
    private BigDecimal liquidadoAno;
    private BigDecimal pagoAno;
    private BigDecimal empenhadoMes;
    private BigDecimal liquidadoMes;
    private BigDecimal pagoMes;
    private BigDecimal total;
    private BigDecimal saldoEmpenhar;
    private BigDecimal saldoLiquidar;
    private BigDecimal saldoPagar;
    private BigDecimal nivel;
    private String orgao;
    private String codigoOrgao;
    private String unidade;
    private String codigoUnidade;
    private String unidadeGestora;
    private String codigoFonteDeRecursos;
    private String fonteDeRecursos;
    private Long idOrgao;
    private Long idUnidade;
    private Long idUnidadeGestora;

//    Atributos para relatório por período
    private BigDecimal empenhado;
    private BigDecimal liquidado;
    private BigDecimal empenhadoLiquidar;
    private BigDecimal pago;
    private BigDecimal liquidadoPagar;

    public DemonstrativoDespesaItem() {
        empenhadoAno = BigDecimal.ZERO;
        empenhadoMes = BigDecimal.ZERO;
        liquidadoAno = BigDecimal.ZERO;
        liquidadoMes = BigDecimal.ZERO;
        pagoAno = BigDecimal.ZERO;
        pagoMes = BigDecimal.ZERO;
    }

    public DemonstrativoDespesaItem(Long id, String codigo, String descricao, BigDecimal orcadaInicial, BigDecimal valorSuplementar, BigDecimal creditoEspecial, BigDecimal empenhadoAno, BigDecimal liquidadoAno, BigDecimal pagoAno, BigDecimal empenhadoMes, BigDecimal liquidadoMes, BigDecimal pagoMes, BigDecimal total, BigDecimal saldoEmpenhar, BigDecimal saldoLiquidar, BigDecimal saldoPagar, BigDecimal nivel, String orgao, String unidade, String unidadeGestora, Long idOrgao, Long idUnidade, Long idUnidadeGestora) {
        this.id = id;
        this.codigo = codigo;
        this.descricao = descricao;
        this.orcadaInicial = orcadaInicial;
        this.valorSuplementar = valorSuplementar;
        this.creditoEspecial = creditoEspecial;
        this.empenhadoAno = empenhadoAno;
        this.liquidadoAno = liquidadoAno;
        this.pagoAno = pagoAno;
        this.empenhadoMes = empenhadoMes;
        this.liquidadoMes = liquidadoMes;
        this.pagoMes = pagoMes;
        this.total = total;
        this.saldoEmpenhar = saldoEmpenhar;
        this.saldoLiquidar = saldoLiquidar;
        this.saldoPagar = saldoPagar;
        this.nivel = nivel;
        this.orgao = orgao;
        this.unidade = unidade;
        this.unidadeGestora = unidadeGestora;
        this.idOrgao = idOrgao;
        this.idUnidade = idUnidade;
        this.idUnidadeGestora = idUnidadeGestora;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getOrcadaInicial() {
        return orcadaInicial;
    }

    public void setOrcadaInicial(BigDecimal orcadaInicial) {
        this.orcadaInicial = orcadaInicial;
    }

    public BigDecimal getValorSuplementar() {
        return valorSuplementar;
    }

    public void setValorSuplementar(BigDecimal valorSuplementar) {
        this.valorSuplementar = valorSuplementar;
    }

    public BigDecimal getCreditoEspecial() {
        return creditoEspecial;
    }

    public void setCreditoEspecial(BigDecimal creditoEspecial) {
        this.creditoEspecial = creditoEspecial;
    }

    public BigDecimal getEmpenhadoAno() {
        return empenhadoAno;
    }

    public void setEmpenhadoAno(BigDecimal empenhadoAno) {
        this.empenhadoAno = empenhadoAno;
    }

    public BigDecimal getLiquidadoAno() {
        return liquidadoAno;
    }

    public void setLiquidadoAno(BigDecimal liquidadoAno) {
        this.liquidadoAno = liquidadoAno;
    }

    public BigDecimal getPagoAno() {
        return pagoAno;
    }

    public void setPagoAno(BigDecimal pagoAno) {
        this.pagoAno = pagoAno;
    }

    public BigDecimal getEmpenhadoMes() {
        return empenhadoMes;
    }

    public void setEmpenhadoMes(BigDecimal empenhadoMes) {
        this.empenhadoMes = empenhadoMes;
    }

    public BigDecimal getLiquidadoMes() {
        return liquidadoMes;
    }

    public void setLiquidadoMes(BigDecimal liquidadoMes) {
        this.liquidadoMes = liquidadoMes;
    }

    public BigDecimal getPagoMes() {
        return pagoMes;
    }

    public void setPagoMes(BigDecimal pagoMes) {
        this.pagoMes = pagoMes;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getSaldoEmpenhar() {
        return saldoEmpenhar;
    }

    public void setSaldoEmpenhar(BigDecimal saldoEmpenhar) {
        this.saldoEmpenhar = saldoEmpenhar;
    }

    public BigDecimal getSaldoLiquidar() {
        return saldoLiquidar;
    }

    public void setSaldoLiquidar(BigDecimal saldoLiquidar) {
        this.saldoLiquidar = saldoLiquidar;
    }

    public BigDecimal getSaldoPagar() {
        return saldoPagar;
    }

    public void setSaldoPagar(BigDecimal saldoPagar) {
        this.saldoPagar = saldoPagar;
    }

    public BigDecimal getNivel() {
        return nivel;
    }

    public void setNivel(BigDecimal nivel) {
        this.nivel = nivel;
    }

    public String getOrgao() {
        return orgao;
    }

    public void setOrgao(String orgao) {
        this.orgao = orgao;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public String getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(String unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public Long getIdOrgao() {
        return idOrgao;
    }

    public void setIdOrgao(Long idOrgao) {
        this.idOrgao = idOrgao;
    }

    public Long getIdUnidade() {
        return idUnidade;
    }

    public void setIdUnidade(Long idUnidade) {
        this.idUnidade = idUnidade;
    }

    public Long getIdUnidadeGestora() {
        return idUnidadeGestora;
    }

    public void setIdUnidadeGestora(Long idUnidadeGestora) {
        this.idUnidadeGestora = idUnidadeGestora;
    }

    public BigDecimal getEmpenhado() {
        return empenhado;
    }

    public void setEmpenhado(BigDecimal empenhado) {
        this.empenhado = empenhado;
    }

    public BigDecimal getLiquidado() {
        return liquidado;
    }

    public void setLiquidado(BigDecimal liquidado) {
        this.liquidado = liquidado;
    }

    public BigDecimal getEmpenhadoLiquidar() {
        return empenhadoLiquidar;
    }

    public void setEmpenhadoLiquidar(BigDecimal empenhadoLiquidar) {
        this.empenhadoLiquidar = empenhadoLiquidar;
    }

    public BigDecimal getPago() {
        return pago;
    }

    public void setPago(BigDecimal pago) {
        this.pago = pago;
    }

    public BigDecimal getLiquidadoPagar() {
        return liquidadoPagar;
    }

    public void setLiquidadoPagar(BigDecimal liquidadoPagar) {
        this.liquidadoPagar = liquidadoPagar;
    }

    public String getCodigoUnidade() {
        return codigoUnidade;
    }

    public void setCodigoUnidade(String codigoUnidade) {
        this.codigoUnidade = codigoUnidade;
    }

    public String getCodigoOrgao() {
        return codigoOrgao;
    }

    public void setCodigoOrgao(String codigoOrgao) {
        this.codigoOrgao = codigoOrgao;
    }

    public BigDecimal getCreditoExtra() {
        return creditoExtra;
    }

    public void setCreditoExtra(BigDecimal creditoExtra) {
        this.creditoExtra = creditoExtra;
    }

    public BigDecimal getAnulacao() {
        return anulacao;
    }

    public void setAnulacao(BigDecimal anulacao) {
        this.anulacao = anulacao;
    }

    public String getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(String fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public String getCodigoFonteDeRecursos() {
        return codigoFonteDeRecursos;
    }

    public void setCodigoFonteDeRecursos(String codigoFonteDeRecursos) {
        this.codigoFonteDeRecursos = codigoFonteDeRecursos;
    }
}
