package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class AlterEnderecoCMCAnterior extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Número")
    private String numero;
    @Etiqueta("Complemento")
    private String complemento;
    @Etiqueta("CEP")
    private String cep;
    @ManyToOne
    @Etiqueta("Alteração de Cadastro Econômico")
    private AlteracaoCMC alteracaoCMC;
    @ManyToOne
    @Etiqueta("Logradouro")
    private Logradouro logradouro;
    @ManyToOne
    @Etiqueta("Bairro")
    private Bairro bairro;

    public AlterEnderecoCMCAnterior() {
    }

    @Override
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

    public AlteracaoCMC getAlteracaoCMC() {
        return alteracaoCMC;
    }

    public void setAlteracaoCMC(AlteracaoCMC alteracaoCMC) {
        this.alteracaoCMC = alteracaoCMC;
    }

    public Logradouro getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(Logradouro logradouro) {
        this.logradouro = logradouro;
    }

    public Bairro getBairro() {
        return bairro;
    }

    public void setBairro(Bairro bairro) {
        this.bairro = bairro;
    }
}
