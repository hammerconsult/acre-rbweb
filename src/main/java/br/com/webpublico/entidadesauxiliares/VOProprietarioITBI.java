package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;

public class VOProprietarioITBI implements Serializable {
    private Long idPessoa;
    private Long idCadastro;
    private String nomeRazaoSocial;
    private String cpfCnpj;

    public VOProprietarioITBI() {
    }

    public Long getIdPessoa() {
        return idPessoa;
    }

    public void setIdPessoa(Long idPessoa) {
        this.idPessoa = idPessoa;
    }

    public Long getIdCadastro() {
        return idCadastro;
    }

    public void setIdCadastro(Long idCadastro) {
        this.idCadastro = idCadastro;
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

}
