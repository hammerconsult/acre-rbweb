package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoCredorRestituicao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by andregustavo on 18/05/2015.
 */
@Entity
@Audited
public class PessoaRestituicao extends SuperEntidade {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Credor")
    private TipoCredorRestituicao tipoCredorRestituicao;
    @ManyToOne
    private Pessoa pessoa;
    @ManyToOne
    private ProcessoRestituicao processoRestituicao;
    private Boolean restituinte;

    public PessoaRestituicao() {
        restituinte = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoCredorRestituicao getTipoCredorRestituicao() {
        return tipoCredorRestituicao;
    }

    public void setTipoCredorRestituicao(TipoCredorRestituicao tipoCredorRestituicao) {
        this.tipoCredorRestituicao = tipoCredorRestituicao;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public ProcessoRestituicao getProcessoRestituicao() {
        return processoRestituicao;
    }

    public void setProcessoRestituicao(ProcessoRestituicao processoRestituicao) {
        this.processoRestituicao = processoRestituicao;
    }

    public Boolean getRestituinte() {
        return restituinte;
    }

    public void setRestituinte(Boolean restituinte) {
        this.restituinte = restituinte;
    }
}
