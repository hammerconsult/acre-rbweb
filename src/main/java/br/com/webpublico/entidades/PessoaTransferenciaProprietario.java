package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoProprietario;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @author Fabio
 */
@Entity
@Audited
@Table(name = "PESSOATRANSFPROPRIETARIO")
public class PessoaTransferenciaProprietario extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private TransferenciaProprietario transferenciaProprietario;
    @ManyToOne
    private Pessoa pessoa;
    private Double proporcao;
    @Enumerated(EnumType.STRING)
    private TipoProprietario tipoProprietario;

    public PessoaTransferenciaProprietario() {
        proporcao = BigDecimal.ZERO.doubleValue();
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

    public TipoProprietario getTipoProprietario() {
        return tipoProprietario;
    }

    public void setTipoProprietario(TipoProprietario tipoProprietario) {
        this.tipoProprietario = tipoProprietario;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ArquivoPenhora[ id=" + id + " ]";
    }
}
