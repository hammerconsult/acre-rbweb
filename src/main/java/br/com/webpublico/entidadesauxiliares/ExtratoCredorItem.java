package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mateus on 01/12/2014.
 */
public class ExtratoCredorItem {

    private Long empenhoId;
    private String dataRegistro;
    private String numero;
    private String operacao;
    private String tipoEmpenho;
    private String funcional;
    private String historico;
    private String fonte;
    private String conta;
    private String classe;
    private String detalhe;
    private String pessoa;
    private String unidade;
    private Long unidadeId;
    private String orgao;
    private Long orgaoId;
    private String unidadeGestora;
    private Long unidadeGestoraId;
    private BigDecimal valor;
    private List<ExtratoCredorDetalhe> detalhes;

    public ExtratoCredorItem() {
        detalhes = new ArrayList<>();
    }

    public Long getEmpenhoId() {
        return empenhoId;
    }

    public void setEmpenhoId(Long empenhoId) {
        this.empenhoId = empenhoId;
    }

    public String getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(String dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getOperacao() {
        return operacao;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }

    public String getFuncional() {
        return funcional;
    }

    public void setFuncional(String funcional) {
        this.funcional = funcional;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public String getFonte() {
        return fonte;
    }

    public void setFonte(String fonte) {
        this.fonte = fonte;
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public String getPessoa() {
        return pessoa;
    }

    public void setPessoa(String pessoa) {
        this.pessoa = pessoa;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public Long getUnidadeId() {
        return unidadeId;
    }

    public void setUnidadeId(Long unidadeId) {
        this.unidadeId = unidadeId;
    }

    public String getOrgao() {
        return orgao;
    }

    public void setOrgao(String orgao) {
        this.orgao = orgao;
    }

    public Long getOrgaoId() {
        return orgaoId;
    }

    public void setOrgaoId(Long orgaoId) {
        this.orgaoId = orgaoId;
    }

    public String getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(String unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public Long getUnidadeGestoraId() {
        return unidadeGestoraId;
    }

    public void setUnidadeGestoraId(Long unidadeGestoraId) {
        this.unidadeGestoraId = unidadeGestoraId;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public List<ExtratoCredorDetalhe> getDetalhes() {
        return detalhes;
    }

    public void setDetalhes(List<ExtratoCredorDetalhe> detalhes) {
        this.detalhes = detalhes;
    }

    public String getTipoEmpenho() {
        return tipoEmpenho;
    }

    public void setTipoEmpenho(String tipoEmpenho) {
        this.tipoEmpenho = tipoEmpenho;
    }

    public String getDetalhe() {
        return detalhe;
    }

    public void setDetalhe(String detalhe) {
        this.detalhe = detalhe;
    }
}
