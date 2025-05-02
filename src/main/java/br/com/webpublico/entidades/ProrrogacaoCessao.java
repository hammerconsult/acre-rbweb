package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 08/05/14
 * Time: 16:02
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Prorrogação da Cessão")
public class ProrrogacaoCessao extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @Etiqueta("Avaliação da Prorrogação")
    @OneToOne
    private AvaliacaoSolicitacaoProrrogacaoCessao avaliacaoProrrogacaoCessao;

    @Obrigatorio
    @Etiqueta("Novo Prazo")
    @OneToOne
    private PrazoCessao novoPrazo;

    public ProrrogacaoCessao() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AvaliacaoSolicitacaoProrrogacaoCessao getAvaliacaoProrrogacaoCessao() {
        return avaliacaoProrrogacaoCessao;
    }

    public void setAvaliacaoProrrogacaoCessao(AvaliacaoSolicitacaoProrrogacaoCessao avaliacaoProrrogacaoCessao) {
        this.avaliacaoProrrogacaoCessao = avaliacaoProrrogacaoCessao;
    }

    public PrazoCessao getNovoPrazo() {
        return novoPrazo;
    }

    public void setNovoPrazo(PrazoCessao novoPrazo) {
        this.novoPrazo = novoPrazo;
    }
}
