package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 19/02/15
 * Time: 10:31
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
public class TransferenciaMovPessoaBce extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private TransferenciaMovPessoa transferenciaMovPessoa;
    @ManyToOne
    private CadastroEconomico cadastroEconomico;

    public TransferenciaMovPessoaBce() {
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

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }
}
