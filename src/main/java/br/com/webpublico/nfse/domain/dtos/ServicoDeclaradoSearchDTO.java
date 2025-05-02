package br.com.webpublico.nfse.domain.dtos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ServicoDeclaradoSearchDTO implements Serializable {

    private Long id;
    private Long idEmpresa;
    private String codigo;
    private Integer numero;
    private Date emissao;
    private Date competencia;
    private String nomePrestador;
    private String cpfCnpjPrestador;
    private String situacao;
    private String modalidade;
    private String origem;
    private String tipo;
    private String tipoDocumento;
    private Boolean issRetido;
    private BigDecimal totalServicos;
    private BigDecimal deducoes;
    private BigDecimal baseCalculo;
    private BigDecimal issCalculado;
    private Long idDeclaracao;
    private Long idDms;
    private String situacaoDebito;

    public ServicoDeclaradoSearchDTO() {
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

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Date getEmissao() {
        return emissao;
    }

    public void setEmissao(Date emissao) {
        this.emissao = emissao;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getNomePrestador() {
        return nomePrestador;
    }

    public void setNomePrestador(String nomePrestador) {
        this.nomePrestador = nomePrestador;
    }

    public BigDecimal getTotalServicos() {
        return totalServicos;
    }

    public void setTotalServicos(BigDecimal totalServicos) {
        this.totalServicos = totalServicos;
    }

    public BigDecimal getBaseCalculo() {
        return baseCalculo;
    }

    public void setBaseCalculo(BigDecimal baseCalculo) {
        this.baseCalculo = baseCalculo;
    }

    public String getModalidade() {
        return modalidade;
    }

    public void setModalidade(String modalidade) {
        this.modalidade = modalidade;
    }

    public Long getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(Long idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public Boolean getIssRetido() {
        return issRetido;
    }

    public void setIssRetido(Boolean issRetido) {
        this.issRetido = issRetido;
    }

    public BigDecimal getDeducoes() {
        return deducoes;
    }

    public void setDeducoes(BigDecimal deducoes) {
        this.deducoes = deducoes;
    }

    public BigDecimal getIssCalculado() {
        return issCalculado;
    }

    public void setIssCalculado(BigDecimal issCalculado) {
        this.issCalculado = issCalculado;
    }

    public String getCpfCnpjPrestador() {
        return cpfCnpjPrestador;
    }

    public void setCpfCnpjPrestador(String cpfCnpjPrestador) {
        this.cpfCnpjPrestador = cpfCnpjPrestador;
    }

    public Date getCompetencia() {
        return competencia;
    }

    public void setCompetencia(Date competencia) {
        this.competencia = competencia;
    }

    public Long getIdDeclaracao() {
        return idDeclaracao;
    }

    public void setIdDeclaracao(Long idDeclaracao) {
        this.idDeclaracao = idDeclaracao;
    }

    public Long getIdDms() {
        return idDms;
    }

    public void setIdDms(Long idDms) {
        this.idDms = idDms;
    }

    public String getSituacaoDebito() {
        return situacaoDebito;
    }

    public void setSituacaoDebito(String situacaoDebito) {
        this.situacaoDebito = situacaoDebito;
    }
}
