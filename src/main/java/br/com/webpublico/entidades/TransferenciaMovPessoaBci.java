package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 19/02/15
 * Time: 10:26
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
public class TransferenciaMovPessoaBci extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private TransferenciaMovPessoa transferenciaMovPessoa;
    @ManyToOne
    private CadastroImobiliario cadastroImobiliario;

    public TransferenciaMovPessoaBci() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TransferenciaMovPessoa getTransferenciaMovPessoa() {
        return transferenciaMovPessoa;
    }

    public void setTransferenciaMovPessoa(TransferenciaMovPessoa transferenciaMovPessoa) {
        this.transferenciaMovPessoa = transferenciaMovPessoa;
    }

    public CadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
    }
}
