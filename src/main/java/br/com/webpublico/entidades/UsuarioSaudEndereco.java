package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Audited
public class UsuarioSaudEndereco extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @Obrigatorio
    @Etiqueta("CEP")
    private String cep;
    @Obrigatorio
    @Etiqueta("Bairro")
    private String bairro;
    @Obrigatorio
    @Etiqueta("Logradouro")
    private String logradouro;
    @Obrigatorio
    @Etiqueta("Número")
    private String numero;
    @Obrigatorio
    @Etiqueta("Cidade")
    @ManyToOne
    private Cidade cidade;

    @Transient
    private UF uf;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
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

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }

    public UF getUf() {
        return uf;
    }

    public void setUf(UF uf) {
        this.uf = uf;
    }
}
