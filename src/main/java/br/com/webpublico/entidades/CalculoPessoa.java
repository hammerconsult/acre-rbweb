package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity

@Audited
@GrupoDiagrama(nome = "Tributario")
public class CalculoPessoa implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Transient
    private Long criadoEm = System.nanoTime();
    @ManyToOne
    private Pessoa pessoa;
    @ManyToOne
    private Calculo calculo;
    @Transient
    private Long idPessoa;

    public CalculoPessoa() {
        this.criadoEm = System.nanoTime();
    }

    public CalculoPessoa(CalculoITBI calculo, Pessoa p) {
        this.criadoEm = System.nanoTime();
        this.calculo = calculo;
        this.pessoa = p;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCriadoEm() {
        return this.criadoEm;
    }

    public Calculo getCalculo() {
        return calculo;
    }

    public void setCalculo(Calculo calculo) {
        this.calculo = calculo;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
        setIdPessoa(this.pessoa.getId());
    }

    public Long getIdPessoa() {
        return idPessoa;
    }

    public void setIdPessoa(Long idPessoa) {
        this.idPessoa = idPessoa;
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
        return "br.com.webpublico.entidades.CalculoPessoa[ id=" + id + " ]";
    }
}
