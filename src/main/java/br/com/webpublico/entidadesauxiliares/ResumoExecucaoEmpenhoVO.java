package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.CategoriaOrcamentaria;

import java.math.BigDecimal;
import java.util.Date;

public class ResumoExecucaoEmpenhoVO {

    private Long idSolitiacaoEmpenho;
    private Date dataSolicitacaoEmpenho;
    private Long id;
    private String numero;
    private Date data;
    private CategoriaOrcamentaria categoria;
    private String historico;
    private BigDecimal valor;
    private ResumoExecucaoEmpenhoVO empenhoResto;

    public Long getIdSolitiacaoEmpenho() {
        return idSolitiacaoEmpenho;
    }

    public void setIdSolitiacaoEmpenho(Long idSolitiacaoEmpenho) {
        this.idSolitiacaoEmpenho = idSolitiacaoEmpenho;
    }

    public Date getDataSolicitacaoEmpenho() {
        return dataSolicitacaoEmpenho;
    }

    public void setDataSolicitacaoEmpenho(Date dataSolicitacaoEmpenho) {
        this.dataSolicitacaoEmpenho = dataSolicitacaoEmpenho;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public CategoriaOrcamentaria getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaOrcamentaria categoria) {
        this.categoria = categoria;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public ResumoExecucaoEmpenhoVO getEmpenhoResto() {
        return empenhoResto;
    }

    public void setEmpenhoResto(ResumoExecucaoEmpenhoVO empenhoResto) {
        this.empenhoResto = empenhoResto;
    }

    public boolean isEmpenhoExecutado() {
        return id != null;
    }

    public boolean hasEmpenhoResto() {
        return empenhoResto != null;
    }

    public String getUrlSolicitacaoEmpenho() {
        return "/solicitacao-empenho/ver/" + idSolitiacaoEmpenho + "/";
    }

    public String getUrlEmpenho() {
        if (isEmpenhoExecutado()) {
            String caminho = categoria.isNormal() ? "/empenho/ver/" : "/empenho/resto-a-pagar/ver/";
            return caminho + id + "/";
        }
        return "";
    }

    public String getUrlEmpenhoResto() {
        if (hasEmpenhoResto()) {
            return "/empenho/resto-a-pagar/ver/" + empenhoResto.getId() + "/";
        }
        return "";
    }
}
