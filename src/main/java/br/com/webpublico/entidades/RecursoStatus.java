package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 06/03/15
 * Time: 13:07
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
public class RecursoStatus extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private RecursoLicitacao recursoLicitacao;

    @OneToOne(cascade = CascadeType.ALL)
    private StatusLicitacao statusLicitacao;

    public RecursoStatus() {
        super();
    }

    public RecursoStatus(RecursoLicitacao recursoLicitacao) {
        this.recursoLicitacao = recursoLicitacao;
    }

    public RecursoStatus(RecursoLicitacao recursoLicitacao, StatusLicitacao statusLicitacao) {
        this.recursoLicitacao = recursoLicitacao;
        this.statusLicitacao = statusLicitacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RecursoLicitacao getRecursoLicitacao() {
        return recursoLicitacao;
    }

    public void setRecursoLicitacao(RecursoLicitacao recursoLicitacao) {
        this.recursoLicitacao = recursoLicitacao;
    }

    public StatusLicitacao getStatusLicitacao() {
        return statusLicitacao;
    }

    public void setStatusLicitacao(StatusLicitacao statusLicitacao) {
        this.statusLicitacao = statusLicitacao;
    }
}
