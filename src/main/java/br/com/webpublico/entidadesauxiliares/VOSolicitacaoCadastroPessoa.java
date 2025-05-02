package br.com.webpublico.entidadesauxiliares;

import java.util.Date;

public class VOSolicitacaoCadastroPessoa {
    private Long idPessoa;
    private String key;
    private Date dataExpiracao;
    private String nomeRazaoSocial;
    private String cpfCnpj;
    private String enderecoCorreio;
    private boolean damPago;

    public VOSolicitacaoCadastroPessoa() {
        damPago = false;
    }

    public VOSolicitacaoCadastroPessoa(Long idPessoa, String key, Date dataExpiracao, String nomeRazaoSocial, String cpfCnpj, String enderecoCorreio) {
        this.idPessoa = idPessoa;
        this.key = key;
        this.dataExpiracao = dataExpiracao;
        this.nomeRazaoSocial = nomeRazaoSocial;
        this.cpfCnpj = cpfCnpj;
        this.enderecoCorreio = enderecoCorreio;
        this.damPago = false;
    }

    public Long getIdPessoa() {
        return idPessoa;
    }

    public void setIdPessoa(Long idPessoa) {
        this.idPessoa = idPessoa;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Date getDataExpiracao() {
        return dataExpiracao;
    }

    public void setDataExpiracao(Date dataExpiracao) {
        this.dataExpiracao = dataExpiracao;
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

    public String getEnderecoCorreio() {
        return enderecoCorreio;
    }

    public void setEnderecoCorreio(String enderecoCorreio) {
        this.enderecoCorreio = enderecoCorreio;
    }

    public boolean isDamPago() {
        return damPago;
    }

    public void setDamPago(boolean damPago) {
        this.damPago = damPago;
    }
}
