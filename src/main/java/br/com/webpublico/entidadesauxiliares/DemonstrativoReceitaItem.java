package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: usuario
 * Date: 19/03/14
 * Time: 10:31
 * To change this template use File | Settings | File Templates.
 */
public class DemonstrativoReceitaItem {

    private Long id;
    private String codigo;
    private String descricao;
    private String tipoMovimento;
    private String tipoOperacao;
    private BigDecimal orcadaInicial;
    private BigDecimal orcadaAtual;
    private BigDecimal arrecadadoNoMes;
    private BigDecimal arrecadadoAteMes;
    private BigDecimal nivel;
    private String orgao;
    private String unidade;
    private String unidadeGestora;
    private Long idOrgao;
    private Long idUnidade;
    private Long idUnidadeGestora;
    private String categoria;
    private List<DemonstrativoReceitaItem> totaisPorFonte;
    private Integer ordem;

    public DemonstrativoReceitaItem() {
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

    public BigDecimal getOrcadaAtual() {
        return orcadaAtual;
    }

    public void setOrcadaAtual(BigDecimal orcadaAtual) {
        this.orcadaAtual = orcadaAtual;
    }

    public BigDecimal getArrecadadoNoMes() {
        return arrecadadoNoMes;
    }

    public void setArrecadadoNoMes(BigDecimal arrecadadoNoMes) {
        this.arrecadadoNoMes = arrecadadoNoMes;
    }

    public BigDecimal getArrecadadoAteMes() {
        return arrecadadoAteMes;
    }

    public void setArrecadadoAteMes(BigDecimal arrecadadoAteMes) {
        this.arrecadadoAteMes = arrecadadoAteMes;
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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getTipoOperacao() {
        return tipoOperacao;
    }

    public void setTipoOperacao(String tipoOperacao) {
        this.tipoOperacao = tipoOperacao;
    }

    public String getTipoMovimento() {
        return tipoMovimento;
    }

    public void setTipoMovimento(String tipoMovimento) {
        this.tipoMovimento = tipoMovimento;
    }

    public List<DemonstrativoReceitaItem> getTotaisPorFonte() {
        return totaisPorFonte;
    }

    public void setTotaisPorFonte(List<DemonstrativoReceitaItem> totaisPorFonte) {
        this.totaisPorFonte = totaisPorFonte;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }
}
