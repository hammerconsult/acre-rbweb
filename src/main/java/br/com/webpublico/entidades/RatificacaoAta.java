package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 04/12/14
 * Time: 15:21
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited

@Etiqueta("Ratificação Ata")
public class RatificacaoAta extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Ata Licitação Origem")
    private AtaLicitacao ataLicitacaoOrigem;

    @ManyToOne
    @Etiqueta("Ata Licitação Ratificação")
    private AtaLicitacao ataLicitacaoRatificacao;

    public RatificacaoAta() {
    }

    public RatificacaoAta(AtaLicitacao ataLicitacaoRatificacao) {
        this.ataLicitacaoRatificacao = ataLicitacaoRatificacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AtaLicitacao getAtaLicitacaoOrigem() {
        return ataLicitacaoOrigem;
    }

    public void setAtaLicitacaoOrigem(AtaLicitacao ataLicitacaoOrigem) {
        this.ataLicitacaoOrigem = ataLicitacaoOrigem;
    }

    public AtaLicitacao getAtaLicitacaoRatificacao() {
        return ataLicitacaoRatificacao;
    }

    public void setAtaLicitacaoRatificacao(AtaLicitacao ataLicitacaoRatificacao) {
        this.ataLicitacaoRatificacao = ataLicitacaoRatificacao;
    }
}
