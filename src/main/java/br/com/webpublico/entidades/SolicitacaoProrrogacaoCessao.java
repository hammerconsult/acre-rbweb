package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoCessao;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 08/05/14
 * Time: 15:56
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Table(name = "SOLICITAPRORROGACAOCESSAO")
@Etiqueta("Solicitação de Prorrogação da Cessão")
public class SolicitacaoProrrogacaoCessao extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Data da Solicitação")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataDaSolicitacao;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número")
    private Long numero;

    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Data Final da Prorrogação")
    @Temporal(TemporalType.DATE)
    private Date novaDataFinal;

    @Transient
    @Tabelavel
    @Etiqueta("Situação da Avaliação")
    @Enumerated(EnumType.STRING)
    private AvaliacaoSolicitacaoProrrogacaoCessao.SituacaoAvaliacaoProrrogacaoCessao situacaoAvaliacaoProrrogacao;

    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Solicitante")
    @ManyToOne
    private UsuarioSistema solicitante;

    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Cessão")
    @ManyToOne
    private LoteCessao loteCessao;

    @OneToMany(mappedBy = "solicitacaoProrrogacao", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ItemSolicitacaoProrrogacao> prorrogacoesCessao;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @Etiqueta("Tipo de Cessão")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private TipoCessao tipoCessao;

    @Etiqueta("Responsável de Origem")
    @ManyToOne
    private PessoaFisica responsavelOrigem;

    @Etiqueta("Responsável de Destino")
    @ManyToOne
    private PessoaFisica responsavelDestino;

    public SolicitacaoProrrogacaoCessao(Long id, Date dataDaSolicitacao, Long numero, Date novaDataFinal, UsuarioSistema solicitante, LoteCessao loteCessao, TipoCessao tipoCessao, AvaliacaoSolicitacaoProrrogacaoCessao.SituacaoAvaliacaoProrrogacaoCessao situacaoAvaliacaoProrrogacao) {
        this.id = id;
        this.dataDaSolicitacao = dataDaSolicitacao;
        this.numero = numero;
        this.novaDataFinal = novaDataFinal;
        this.solicitante = solicitante;
        this.loteCessao = loteCessao;
        this.tipoCessao = tipoCessao;
        this.situacaoAvaliacaoProrrogacao = situacaoAvaliacaoProrrogacao;
    }

    public SolicitacaoProrrogacaoCessao() {
        super();
        prorrogacoesCessao = Lists.newArrayList();
        tipoCessao = TipoCessao.INTERNO;
    }

    public List<ItemSolicitacaoProrrogacao> getProrrogacoesCessao() {
        return prorrogacoesCessao;
    }

    public void setProrrogacoesCessao(List<ItemSolicitacaoProrrogacao> prorrogacoesCessao) {
        this.prorrogacoesCessao = prorrogacoesCessao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataDaSolicitacao() {
        return dataDaSolicitacao;
    }

    public void setDataDaSolicitacao(Date dataDaSolicitacao) {
        this.dataDaSolicitacao = dataDaSolicitacao;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Date getNovaDataFinal() {
        return novaDataFinal;
    }

    public void setNovaDataFinal(Date novaDataFinal) {
        this.novaDataFinal = novaDataFinal != null ? DataUtil.dataSemHorario(novaDataFinal) : null;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public UsuarioSistema getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(UsuarioSistema solicitante) {
        this.solicitante = solicitante;
    }

    public LoteCessao getLoteCessao() {
        return loteCessao;
    }

    public void setLoteCessao(LoteCessao loteCessao) {
        this.loteCessao = loteCessao;
    }

    @Override
    public String toString() {
        return this.numero + " - " + DataUtil.getDataFormatada(this.dataDaSolicitacao) + " - Cessão: " + this.loteCessao.getCodigo();
    }

    public TipoCessao getTipoCessao() {
        return tipoCessao;
    }

    public void setTipoCessao(TipoCessao tipoCessao) {
        this.tipoCessao = tipoCessao;
    }

    public Boolean isExterno() {
        return TipoCessao.EXTERNO.equals(this.tipoCessao);
    }

    public Boolean isInterno() {
        return TipoCessao.INTERNO.equals(this.tipoCessao);
    }

    public PessoaFisica getResponsavelOrigem() {
        return responsavelOrigem;
    }

    public void setResponsavelOrigem(PessoaFisica responsavelOrigem) {
        this.responsavelOrigem = responsavelOrigem;
    }

    public PessoaFisica getResponsavelDestino() {
        return responsavelDestino;
    }

    public void setResponsavelDestino(PessoaFisica responsavelDestino) {
        this.responsavelDestino = responsavelDestino;
    }
}
