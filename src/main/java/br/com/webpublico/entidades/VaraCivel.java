package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author fabio
 */
@GrupoDiagrama(nome = "Dívida Ativa")
@Entity

@Audited
@Etiqueta(value = "Vara Cível")
public class VaraCivel implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    @Invisivel
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Column(length = 100)
    @Obrigatorio
    @Etiqueta("Nome")
    private String nome;
    @Column(length = 70)
    @Etiqueta("Endereço")
    private String endereco;
    @Etiqueta("Número")
    private String numeroEndereco;
    @Column(length = 70)
    @Etiqueta("Bairro")
    private String bairro;
    @Column(length = 70)
    @Etiqueta("Cidade")
    private String cidade;
    @Etiqueta("UF")
    @ManyToOne
    private UF uf;
    @Column(length = 20)
    @Etiqueta("Telefone")
    private String telefone;
    @Transient
    private Long criadoEm;

    public VaraCivel() {
        this.criadoEm = System.nanoTime();
    }

    public VaraCivel(Long id, String nome) {
        this.id = id;
        this.nome = nome;
        this.criadoEm = System.nanoTime();
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

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getNumeroEndereco() {
        return numeroEndereco;
    }

    public void setNumeroEndereco(String numeroEndereco) {
        this.numeroEndereco = numeroEndereco;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public UF getUf() {
        return uf;
    }

    public void setUf(UF uf) {
        this.uf = uf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return nome;
    }
}
