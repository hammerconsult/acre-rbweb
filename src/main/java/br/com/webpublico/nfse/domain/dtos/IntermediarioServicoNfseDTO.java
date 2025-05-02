package br.com.webpublico.nfse.domain.dtos;

import com.google.common.base.Strings;

public class IntermediarioServicoNfseDTO implements br.com.webpublico.nfse.domain.dtos.NfseDTO, Cloneable {

    private Long idPessoa;
    private Long id;
    private String cpfCnpj;
    private String nomeRazaoSocial;

    public IntermediarioServicoNfseDTO() {
    }

    public IntermediarioServicoNfseDTO(String cpfCnpj, String nomeRazaoSocial) {
        this.setCpfCnpj(cpfCnpj);
        this.setNomeRazaoSocial(nomeRazaoSocial);
    }

    public Long getIdPessoa() {
        return idPessoa;
    }

    public void setIdPessoa(Long idPessoa) {
        this.idPessoa = idPessoa;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        if (!Strings.isNullOrEmpty(cpfCnpj)) {
            this.cpfCnpj = cpfCnpj.replaceAll("[^a-zA-Z0-9]+", "");
        }
    }

    public String getNomeRazaoSocial() {
        return nomeRazaoSocial;
    }

    public void setNomeRazaoSocial(String nomeRazaoSocial) {
        this.nomeRazaoSocial = nomeRazaoSocial;
    }

    @Override
    public IntermediarioServicoNfseDTO clone() throws CloneNotSupportedException {
        IntermediarioServicoNfseDTO cloneable = new IntermediarioServicoNfseDTO();
        cloneable.cpfCnpj = this.cpfCnpj;
        cloneable.idPessoa = this.idPessoa;
        cloneable.id = this.id;
        cloneable.nomeRazaoSocial = this.nomeRazaoSocial;
        return cloneable;
    }

    @Override
    public String toString() {
        return "DadosPessoaisNfseDTO{" +
            "idPessoa=" + idPessoa +
            ", id=" + id +
            ", cpfCnpj='" + cpfCnpj + '\'' +
            ", nomeRazaoSocial='" + nomeRazaoSocial + '\'' +
            '}';
    }
}
