package br.com.webpublico.nfse.domain.dtos;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class RelatorioServicosDeclaradosDTO {

    private Long id;
    private String numero;
    private Date emissao;
    private Date pagamento;
    private String nomeRazaoSocial;
    private String cpfCnpj;
    private String situacao;
    private String modalidade;
    private String issRetido;
    private BigDecimal totalServicos;
    private BigDecimal totalDeducoes;
    private BigDecimal baseCalculo;
    private BigDecimal issCalculo;

    private List<RelatorioServicosDeclaradosDetailDTO> itens;
    private String naturezaOperacao;
    private Date competencia;
    private String numeroDam;
    public RelatorioServicosDeclaradosDTO() {
        totalServicos = BigDecimal.ZERO;
        totalDeducoes = BigDecimal.ZERO;
        baseCalculo = BigDecimal.ZERO;
        issCalculo = BigDecimal.ZERO;
        itens = Lists.newArrayList();
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

    public Date getEmissao() {
        return emissao;
    }

    public void setEmissao(Date emissao) {
        this.emissao = emissao;
    }

    public String getNomeRazaoSocial() {
        return nomeRazaoSocial;
    }

    public void setNomeRazaoSocial(String nomeRazaoSocial) {
        this.nomeRazaoSocial = nomeRazaoSocial;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getModalidade() {
        return modalidade;
    }

    public void setModalidade(String modalidade) {
        this.modalidade = modalidade;
    }

    public String getIssRetido() {
        return issRetido;
    }

    public void setIssRetido(String issRetido) {
        this.issRetido = issRetido;
    }

    public BigDecimal getTotalServicos() {
        return totalServicos == null ? BigDecimal.ZERO : totalServicos;
    }

    public void setTotalServicos(BigDecimal totalServicos) {
        this.totalServicos = totalServicos;
    }

    public BigDecimal getTotalDeducoes() {
        return totalDeducoes == null ? BigDecimal.ZERO : totalDeducoes;
    }

    public void setTotalDeducoes(BigDecimal totalDeducoes) {
        this.totalDeducoes = totalDeducoes;
    }

    public BigDecimal getBaseCalculo() {
        return baseCalculo == null ? BigDecimal.ZERO : baseCalculo;
    }

    public void setBaseCalculo(BigDecimal baseCalculo) {
        this.baseCalculo = baseCalculo;
    }

    public BigDecimal getIssCalculo() {
        return issCalculo == null ? BigDecimal.ZERO : issCalculo;
    }

    public void setIssCalculo(BigDecimal issCalculo) {
        this.issCalculo = issCalculo;
    }

    public List<RelatorioServicosDeclaradosDetailDTO> getItens() {
        return itens;
    }

    public void setItens(List<RelatorioServicosDeclaradosDetailDTO> itens) {
        this.itens = itens;
    }

    public String getNaturezaOperacao() {
        return naturezaOperacao;
    }

    public void setNaturezaOperacao(String naturezaOperacao) {
        this.naturezaOperacao = naturezaOperacao;
    }

    public Date getCompetencia() {
        return competencia;
    }

    public void setCompetencia(Date competencia) {
        this.competencia = competencia;
    }

    public String getNumeroDam() {
        return numeroDam;
    }

    public void setNumeroDam(String numeroDam) {
        this.numeroDam = numeroDam;
    }

    public Date getPagamento() {
        return pagamento;
    }

    public void setPagamento(Date pagamento) {
        this.pagamento = pagamento;
    }
}
