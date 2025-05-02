package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by andregustavo on 18/05/2015.
 */
@Entity
@Audited
public class RestituicaoPagamento extends SuperEntidade {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ProcessoRestituicao processoRestituicao;
    @ManyToOne
    private PagamentoJudicial pagamentoJudicial;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProcessoRestituicao getProcessoRestituicao() {
        return processoRestituicao;
    }

    public void setProcessoRestituicao(ProcessoRestituicao processoRestituicao) {
        this.processoRestituicao = processoRestituicao;
    }

    public PagamentoJudicial getPagamentoJudicial() {
        return pagamentoJudicial;
    }

    public void setPagamentoJudicial(PagamentoJudicial pagamentoJudicial) {
        this.pagamentoJudicial = pagamentoJudicial;
    }
}
