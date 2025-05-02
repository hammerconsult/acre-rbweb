package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.nfse.domain.dtos.IntermediarioServicoNfseDTO;
import br.com.webpublico.nfse.domain.dtos.NfseEntity;
import com.google.common.base.Strings;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
public class IntermediarioServico extends SuperEntidade implements Serializable, NfseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Pessoa pessoa;
    private String cpfCnpj;
    private String nomeRazaoSocial;

    public IntermediarioServico() {
    }

    public IntermediarioServico(String cpfCnpj, String nomeRazaoSocial) {
        this.setCpfCnpj(cpfCnpj);
        this.setNomeRazaoSocial(nomeRazaoSocial);
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

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    @Override
    public String toString() {
        return "DadosPessoaisNfseDTO{" +
            ", id=" + id +
            ", cpfCnpj='" + cpfCnpj + '\'' +
            ", nomeRazaoSocial='" + nomeRazaoSocial + '\'' +
            '}';
    }

    @Override
    public IntermediarioServicoNfseDTO toNfseDto() {
        return new IntermediarioServicoNfseDTO(this.cpfCnpj, this.nomeRazaoSocial);
    }
}
