package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Fabio
 */
@Entity
@Audited
@Table(name = "PESSOATRANSFPROPANT")
public class PessoaTransferenciaProprietarioAnterior implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private TransferenciaProprietario transferenciaProprietario;
    @ManyToOne
    private Pessoa pessoa;
    private Double proporcao;
    @Transient
    private Long criadoEm;

    public PessoaTransferenciaProprietarioAnterior() {
        criadoEm = System.nanoTime();
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public TransferenciaProprietario getTransferenciaProprietario() {
        return transferenciaProprietario;
    }

    public void setTransferenciaProprietario(TransferenciaProprietario transferenciaProprietario) {
        this.transferenciaProprietario = transferenciaProprietario;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getProporcao() {
        return proporcao;
    }

    public void setProporcao(Double proporcao) {
        this.proporcao = proporcao;
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
        return "br.com.webpublico.entidades.PessoaTransferenciaProprietarioAnterior[ id=" + id + " ]";
    }
}
