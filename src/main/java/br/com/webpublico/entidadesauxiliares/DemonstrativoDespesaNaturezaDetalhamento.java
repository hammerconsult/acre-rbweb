package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Criado por Mateus
 * Data: 15/02/2017.
 */
public class DemonstrativoDespesaNaturezaDetalhamento {

    private String orgao;
    private String unidade;
    private String unidadeGestora;
    private String codigo;
    private String titulo;
    private BigDecimal dotacaoAtualizada;
    private BigDecimal empenhada;
    private BigDecimal liquidada;
    private BigDecimal paga;
    private BigDecimal empenhadoALiquidar;
    private BigDecimal liquidadaAPagar;
    private Long idOrgao;
    private Long idUnidade;
    private Long idUnidadeGestora;
    private Long idConta;
    private BigDecimal nivel;

    public DemonstrativoDespesaNaturezaDetalhamento() {
        dotacaoAtualizada = BigDecimal.ZERO;
        empenhada = BigDecimal.ZERO;
        liquidada = BigDecimal.ZERO;
        paga = BigDecimal.ZERO;
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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public BigDecimal getDotacaoAtualizada() {
        return dotacaoAtualizada;
    }

    public void setDotacaoAtualizada(BigDecimal dotacaoAtualizada) {
        this.dotacaoAtualizada = dotacaoAtualizada;
    }

    public BigDecimal getEmpenhada() {
        return empenhada;
    }

    public void setEmpenhada(BigDecimal empenhada) {
        this.empenhada = empenhada;
    }

    public BigDecimal getLiquidada() {
        return liquidada;
    }

    public void setLiquidada(BigDecimal liquidada) {
        this.liquidada = liquidada;
    }

    public BigDecimal getPaga() {
        return paga;
    }

    public void setPaga(BigDecimal paga) {
        this.paga = paga;
    }

    public BigDecimal getEmpenhadoALiquidar() {
        return empenhada != null ? (liquidada != null ? empenhada.subtract(liquidada) : empenhada) : BigDecimal.ZERO;
    }

    public BigDecimal getLiquidadaAPagar() {
        return liquidada != null ? (paga != null ? liquidada.subtract(paga) : liquidada) : BigDecimal.ZERO;
    }

    public void setEmpenhadoALiquidar(BigDecimal empenhadoALiquidar) {
        this.empenhadoALiquidar = empenhadoALiquidar;
    }

    public void setLiquidadaAPagar(BigDecimal liquidadaAPagar) {
        this.liquidadaAPagar = liquidadaAPagar;
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

    public BigDecimal getNivel() {
        return nivel;
    }

    public void setNivel(BigDecimal nivel) {
        this.nivel = nivel;
    }

    public Long getIdConta() {
        return idConta;
    }

    public void setIdConta(Long idConta) {
        this.idConta = idConta;
    }

    public String getCodigoSemZerosAoFinal() {
        int zeroAPartirDe = codigo.length();
        for (int tamanho = codigo.length() - 1; tamanho >= 0; tamanho--) {
            if (".".equals(codigo.substring(tamanho, tamanho + 1))) {
                zeroAPartirDe = tamanho;
            } else if (!"0".equalsIgnoreCase(codigo.substring(tamanho, tamanho + 1))) {
                return codigo.substring(0, zeroAPartirDe);
            }
        }
        return codigo;
    }
}
