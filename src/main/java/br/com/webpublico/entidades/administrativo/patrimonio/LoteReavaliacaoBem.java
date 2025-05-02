package br.com.webpublico.entidades.administrativo.patrimonio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by William on 22/09/2015.
 */
@Entity
@Audited
@Etiqueta("Solicitação de Reavaliação de Bens Móveis")
public class LoteReavaliacaoBem extends SuperEntidade implements PossuidorArquivo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código")
    @CodigoGeradoAoConcluir
    private Long codigo;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data/Hora de Criação")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataHoraCriacao;

    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Tipo de Reavaliação do Bem")
    @Enumerated(EnumType.STRING)
    private TipoOperacaoReavaliacaoBens tipoOperacaoBem;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Descrição")
    private String descricao;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Unidade de Organizacional")
    @ManyToOne
    private UnidadeOrganizacional unidadeOrigem;

    @Obrigatorio
    @Tabelavel
    @Etiqueta("Responsável pela Unidade")
    @ManyToOne
    private PessoaFisica responsavel;

    @Etiqueta("Situação")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private SituacaoDaSolicitacao situacaoReavaliacaoBem;

    @OneToMany(mappedBy = "loteReavaliacaoBem", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ReavaliacaoBem> reavaliacaoBens;

    @Etiqueta("Motivo da Recusa")
    private String motivoRecusa;

    @Etiqueta("Removido")
    private Boolean removido = Boolean.FALSE;

    @Etiqueta("Lote Origem")
    @OneToOne
    private LoteReavaliacaoBem loteReavaliacaoBem;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo de Bem")
    @Enumerated(EnumType.STRING)
    private TipoBem tipoBem;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @Transient
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    @Version
    private Long versao;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataVersao;

    public LoteReavaliacaoBem() {
        dataVersao = new Date();
        reavaliacaoBens = Lists.newArrayList();
    }

    public Date getDataVersao() {
        return dataVersao;
    }

    public void setDataVersao(Date dataVersao) {
        this.dataVersao = dataVersao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Date getDataHoraCriacao() {
        return dataHoraCriacao;
    }

    public void setDataHoraCriacao(Date dataHoraCriacao) {
        this.dataHoraCriacao = dataHoraCriacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public PessoaFisica getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(PessoaFisica responsavel) {
        this.responsavel = responsavel;
    }

    public UnidadeOrganizacional getUnidadeOrigem() {
        return unidadeOrigem;
    }

    public void setUnidadeOrigem(UnidadeOrganizacional unidadeOrigem) {
        this.unidadeOrigem = unidadeOrigem;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
        if (this.hierarquiaOrganizacional != null) {
            setUnidadeOrigem(this.hierarquiaOrganizacional.getSubordinada());
        }
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public SituacaoDaSolicitacao getSituacaoReavaliacaoBem() {
        return situacaoReavaliacaoBem;
    }

    public void setSituacaoReavaliacaoBem(SituacaoDaSolicitacao situacaoReavaliacaoBem) {
        this.situacaoReavaliacaoBem = situacaoReavaliacaoBem;
    }

    public String getMotivoRecusa() {
        return motivoRecusa;
    }

    public void setMotivoRecusa(String motivoRecusa) {
        this.motivoRecusa = motivoRecusa;
    }

    public Boolean getRemovido() {
        return removido;
    }

    public void setRemovido(Boolean removido) {
        this.removido = removido;
    }

    public TipoBem getTipoBem() {
        return tipoBem;
    }

    public void setTipoBem(TipoBem tipoBem) {
        this.tipoBem = tipoBem;
    }

    public List<ReavaliacaoBem> getReavaliacaoBens() {
        return reavaliacaoBens;
    }

    public LoteReavaliacaoBem getLoteReavaliacaoBem() {
        return loteReavaliacaoBem;
    }

    public void setLoteReavaliacaoBem(LoteReavaliacaoBem loteReavaliacaoBem) {
        this.loteReavaliacaoBem = loteReavaliacaoBem;
    }

    public TipoOperacaoReavaliacaoBens getTipoOperacaoBem() {
        return tipoOperacaoBem;
    }

    public void setTipoOperacaoBem(TipoOperacaoReavaliacaoBens tipoOperacaoBem) {
        this.tipoOperacaoBem = tipoOperacaoBem;
    }

    public void setReavaliacaoBens(List<ReavaliacaoBem> reavaliacaoBens) {
        this.reavaliacaoBens = reavaliacaoBens;
    }

    public Boolean foiRecusado() {
        return SituacaoDaSolicitacao.RECUSADA.equals(this.situacaoReavaliacaoBem);
    }

    public Boolean foiAceito() {
        return SituacaoDaSolicitacao.ACEITA.equals(this.situacaoReavaliacaoBem);
    }

    public Boolean estaEmElaboracao() {
        return SituacaoDaSolicitacao.EM_ELABORACAO.equals(this.situacaoReavaliacaoBem);
    }

    public void atribuirUnidadeOrcamentariaTodasReavaliacoes() {
        if (!reavaliacaoBens.isEmpty()) {
            for (ReavaliacaoBem tr : reavaliacaoBens) {
                tr.getLoteReavaliacaoBem().setUnidadeOrigem(this.unidadeOrigem);
            }
        }
    }

    public Boolean isReavaliacaoBemDiminutiva() {
        return TipoOperacaoReavaliacaoBens.REAVALIACAO_BENS_MOVEIS_DIMINUTIVA.equals(this.tipoOperacaoBem)
            || TipoOperacaoReavaliacaoBens.REAVALIACAO_BENS_IMOVEIS_DIMINUTIVA.equals(this.tipoOperacaoBem);
    }
}
