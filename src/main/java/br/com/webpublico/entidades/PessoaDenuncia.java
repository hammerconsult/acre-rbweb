package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Audited
@Etiqueta("Pessoa da Denuncia")
public class PessoaDenuncia implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Identificador")
    private Long id;
    @Etiqueta("Nome/Razão Social")
    private String nome;
    @Etiqueta("CPF/CNPJ")
    private String cpfCnpj;
    @Etiqueta("Endereço")
    private String endereco;
    @Etiqueta("Número")
    private String numero;
    @Etiqueta("Complemento")
    private String complemento;
    @ManyToOne
    @Etiqueta("Bairro")
    private Bairro bairro;
    @Etiqueta("Telefone Fixo")
    private String telefoneFixo;
    @Etiqueta("Telefone Celular")
    private String telefoneCelular;
    @Transient
    private Long criadoEm;

    public PessoaDenuncia() {
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
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

    public Bairro getBairro() {
        return bairro;
    }

    public void setBairro(Bairro bairro) {
        this.bairro = bairro;
    }

    public String getTelefoneFixo() {
        return telefoneFixo;
    }

    public void setTelefoneFixo(String telefoneFixo) {
        this.telefoneFixo = telefoneFixo;
    }

    public String getTelefoneCelular() {
        return telefoneCelular;
    }

    public void setTelefoneCelular(String telefoneCelular) {
        this.telefoneCelular = telefoneCelular;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(obj, this);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        return (nome != null ? nome  : " ") +(cpfCnpj != null ? " (" + cpfCnpj + ")" : " ") ;
    }
}
