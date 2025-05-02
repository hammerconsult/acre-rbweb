package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.Cidade;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.nfse.domain.dtos.ConstrucaoCivilLocalizacaoNfseDTO;
import br.com.webpublico.nfse.domain.dtos.NfseEntity;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Entity
@Audited
public class ConstrucaoCivilLocalizacao extends SuperEntidade implements Serializable, NfseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Obrigatorio
    @Etiqueta("Cidade")
    @ManyToOne
    private Cidade cidade;

    @Obrigatorio
    @Etiqueta("Logradouro")
    private String logradouro;

    @Obrigatorio
    @Etiqueta("Número")
    private String numero;

    @Obrigatorio
    @Etiqueta("Bairro")
    private String bairro;

    @Obrigatorio
    @Etiqueta("CEP")
    private String cep;

    private String nomeEmpreendimento;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
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

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getNomeEmpreendimento() {
        return nomeEmpreendimento;
    }

    public void setNomeEmpreendimento(String nomeEmpreendimento) {
        this.nomeEmpreendimento = nomeEmpreendimento;
    }

    @Override
    public ConstrucaoCivilLocalizacaoNfseDTO toNfseDto() {
        return new ConstrucaoCivilLocalizacaoNfseDTO(id,
            cidade.toNfseDto(),
            logradouro,
            numero,
            bairro,
            cep,
            nomeEmpreendimento);
    }
}
