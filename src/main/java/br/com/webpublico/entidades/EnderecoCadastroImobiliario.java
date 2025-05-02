package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoEndereco;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: java
 * Date: 30/08/13
 * Time: 11:22
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name="ENDCADASTROIMOBILIARIO")
@Audited
public class EnderecoCadastroImobiliario implements Serializable {

    private String numero;
    private String complemento;
    @Enumerated(EnumType.STRING)
    private TipoEndereco tipoEndereco;
    @ManyToOne
    private Logradouro logradouro;
    @ManyToOne
    private CEP cep;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CadastroImobiliario cadastroImobiliario;
    @Invisivel
    @Transient
    private Long criadoEm;
    @Invisivel
    @Transient
    private Bairro bairro;


    public EnderecoCadastroImobiliario() {
        this.criadoEm = System.nanoTime();
    }

    public EnderecoCadastroImobiliario(TipoEndereco tipoEndereco) {
        this.criadoEm = System.nanoTime();
        this.tipoEndereco = tipoEndereco;
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

    public TipoEndereco getTipoEndereco() {
        return tipoEndereco;
    }

    public void setTipoEndereco(TipoEndereco tipoEndereco) {
        this.tipoEndereco = tipoEndereco;
    }

    public Logradouro getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(Logradouro logradouro) {
        this.logradouro = logradouro;
    }

    public CEP getCep() {
        return cep;
    }

    public void setCep(CEP cep) {
        this.cep = cep;
    }

    public Bairro getBairro() {
        return bairro;
    }

    public void setBairro(Bairro bairro) {
        this.bairro = bairro;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        return "Endereço do Cadastro Econômico ";
    }
}
