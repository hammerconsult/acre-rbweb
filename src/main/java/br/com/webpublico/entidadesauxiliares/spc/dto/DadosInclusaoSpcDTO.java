package br.com.webpublico.entidadesauxiliares.spc.dto;

import br.com.webpublico.entidadesauxiliares.spc.enums.TipoPessoa;

import java.math.BigDecimal;
import java.util.Date;

public class DadosInclusaoSpcDTO {

    private String numeroContrato;
    private TipoPessoa tipoPessoa;
    private String cpfCnpj;
    private String nomeRazaoSocial;
    private Date dataLancamento;
    private Date dataVencimento;
    private BigDecimal valorDebito;
    private String codigoTipoDevedor;
    private Long idNaturezaInclusao;
    private String bairro;
    private String logradouro;
    private String numero;
    private String complemento;
    private String cep;
    private String cidade;
    private String uf;

    public DadosInclusaoSpcDTO() {
    }

    public String getNumeroContrato() {
        return numeroContrato;
    }

    public void setNumeroContrato(String numeroContrato) {
        this.numeroContrato = numeroContrato;
    }

    public TipoPessoa getTipoPessoa() {
        return tipoPessoa;
    }

    public void setTipoPessoa(TipoPessoa tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getNomeRazaoSocial() {
        return nomeRazaoSocial;
    }

    public void setNomeRazaoSocial(String nomeRazaoSocial) {
        this.nomeRazaoSocial = nomeRazaoSocial;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public Date getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(Date dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public BigDecimal getValorDebito() {
        return valorDebito;
    }

    public void setValorDebito(BigDecimal valorDebito) {
        this.valorDebito = valorDebito;
    }

    public String getCodigoTipoDevedor() {
        return codigoTipoDevedor;
    }

    public void setCodigoTipoDevedor(String codigoTipoDevedor) {
        this.codigoTipoDevedor = codigoTipoDevedor;
    }

    public Long getIdNaturezaInclusao() {
        return idNaturezaInclusao;
    }

    public void setIdNaturezaInclusao(Long idNaturezaInclusao) {
        this.idNaturezaInclusao = idNaturezaInclusao;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }
}
