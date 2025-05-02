package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoCessao;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 08/05/14
 * Time: 15:53
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Table(name = "AVALISOLIPRORROGACAOCESSAO")
@Etiqueta("Avaliação de Prorrogação da Cessão")
public class AvaliacaoSolicitacaoProrrogacaoCessao extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número")
    private Long numero;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data da Avaliação")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataAvaliacao;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @OneToOne
    @Etiqueta("Solicitação de Prorrogação")
    private SolicitacaoProrrogacaoCessao solicitaProrrogacaoCessao;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Situação da Avaliação")
    @Enumerated(EnumType.STRING)
    private SituacaoAvaliacaoProrrogacaoCessao situacaoAvaliacaoProrrogacao;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    @Etiqueta("Avaliador")
    private UsuarioSistema avaliador;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Observação")
    private String observacao;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @OneToMany(mappedBy = "avaliacao",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemAvaliacaoProrrogacao> itensAvaliacao;

    @Etiqueta("Tipo de Cessão")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private TipoCessao tipoCessao;

    public AvaliacaoSolicitacaoProrrogacaoCessao() {
        super();
        itensAvaliacao = new ArrayList<>();
        tipoCessao = TipoCessao.INTERNO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Date getDataAvaliacao() {
        return dataAvaliacao;
    }

    public void setDataAvaliacao(Date dataAvaliacao) {
        this.dataAvaliacao = dataAvaliacao;
    }

    public SituacaoAvaliacaoProrrogacaoCessao getSituacaoAvaliacaoProrrogacao() {
        return situacaoAvaliacaoProrrogacao;
    }

    public void setSituacaoAvaliacaoProrrogacao(SituacaoAvaliacaoProrrogacaoCessao situacaoAvaliacaoProrrogacao) {
        this.situacaoAvaliacaoProrrogacao = situacaoAvaliacaoProrrogacao;
    }

    public SolicitacaoProrrogacaoCessao getSolicitaProrrogacaoCessao() {
        return solicitaProrrogacaoCessao;
    }

    public void setSolicitaProrrogacaoCessao(SolicitacaoProrrogacaoCessao solicitaProrrogacaoCessao) {
        this.solicitaProrrogacaoCessao = solicitaProrrogacaoCessao;
    }
    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public UsuarioSistema getAvaliador() {
        return avaliador;
    }

    public void setAvaliador(UsuarioSistema avaliador) {
        this.avaliador = avaliador;
    }

    public List<ItemAvaliacaoProrrogacao> getItensAvaliacao() {
        return itensAvaliacao;
    }

    public void setItensAvaliacao(List<ItemAvaliacaoProrrogacao> itensAvaliacao) {
        this.itensAvaliacao = itensAvaliacao;
    }

    public TipoCessao getTipoCessao() {
        return tipoCessao;
    }

    public void setTipoCessao(TipoCessao tipoCessao) {
        this.tipoCessao = tipoCessao;
    }

    public enum  SituacaoAvaliacaoProrrogacaoCessao {

        APROVADA("Aprovada"),
        REJEITADA("Rejeitada");

        private String descricao;

        private SituacaoAvaliacaoProrrogacaoCessao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    public Boolean isExterno() {
        return TipoCessao.EXTERNO.equals(this.tipoCessao);
    }

    public Boolean isInterno() {
        return TipoCessao.INTERNO.equals(this.tipoCessao);
    }

}
