package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoReducaoValorBem;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoReducaoValorBem;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 07/10/14
 * Time: 14:15
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Lote Redução Valor Bem")
public class LoteReducaoValorBem extends SuperEntidade implements PossuidorArquivo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Etiqueta("Código")
    private Long codigo;

    @Etiqueta("Data de Lançamento")
    @Obrigatorio
    @Tabelavel(TIPOCAMPO = Tabelavel.TIPOCAMPO.DATA)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataLancamento;

    @Etiqueta("Data da Operação")
    @Obrigatorio
    @Tabelavel(TIPOCAMPO = Tabelavel.TIPOCAMPO.DATA)
    @Temporal(TemporalType.TIMESTAMP)
    private Date data;

    @ManyToOne
    @Etiqueta("Unidade Orçamentária")
    private UnidadeOrganizacional unidadeOrcamentaria;

    @Tabelavel
    @Obrigatorio
    @Etiqueta("Unidade Orçamentária")
    @Transient
    private HierarquiaOrganizacional hierarquiaOrcamentaria;

    @Obrigatorio
    @Tabelavel
    @ManyToOne
    @Etiqueta("Usuário da Operação")
    private UsuarioSistema usuarioSistema;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo Bem")
    private TipoBem tipoBem;

    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Invisivel
    private TipoReducaoValorBem tipoReducao;

    @Etiqueta("Reduções")
    @Invisivel
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "loteReducaoValorBem", fetch = FetchType.LAZY)
    private List<ReducaoValorBem> reducoes;

    @Etiqueta("Bens não Aplicáveis")
    @Invisivel
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "loteReducaoValorBem", fetch = FetchType.LAZY)
    private List<ReducaoValorBemNaoAplicavel> bensNaoAplicaveis;

    @Etiqueta("Bens - Residual")
    @Invisivel
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "loteReducaoValorBem", fetch = FetchType.LAZY)
    private List<ReducaoValorBemResidual> bensResidual;

    @Etiqueta("Logs de Redução")
    @OneToOne(cascade = CascadeType.ALL)
    private LogReducaoOuEstorno logReducaoOuEstorno;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Situação")
    @Enumerated(EnumType.STRING)
    private SituacaoReducaoValorBem situacao;

    @Transient
    private String usuarioOperacao;
    @Transient
    private List<ReducaoValorBemContabil> reducoesValorBemContabil;

    private Integer quantidadeDepreciacao;
    private BigDecimal valorDepreciacao;

    private Integer quantidadeExaustao;
    private BigDecimal valorExaustao;

    private Integer quantidadeAmortizacao;
    private BigDecimal valorAmortizacao;


    //Construtor utilizado para a pesquisa genérica
    public LoteReducaoValorBem(LoteReducaoValorBem lote, HierarquiaOrganizacional hierarquiaOrcamentaria) {
        this.setId(lote.getId());
        this.setCodigo(lote.getCodigo());
        this.setUsuarioSistema(lote.getUsuarioSistema());
        this.setData(lote.getData());
        this.setDataLancamento(lote.getDataLancamento());
        this.setHierarquiaOrcamentaria(hierarquiaOrcamentaria);
        this.setSituacao(lote.getSituacao());
    }

    public LoteReducaoValorBem() {
        this.reducoes = Lists.newArrayList();
        this.logReducaoOuEstorno = new LogReducaoOuEstorno();
        this.reducoesValorBemContabil = Lists.newArrayList();
        this.logReducaoOuEstorno.setMensagens(Lists.<MsgLogReducaoOuEstorno>newArrayList());
        this.bensNaoAplicaveis = Lists.newArrayList();
        this.bensResidual = Lists.newArrayList();
        this.quantidadeDepreciacao = 0;
        this.valorDepreciacao = BigDecimal.ZERO;
        this.quantidadeExaustao = 0;
        this.valorExaustao = BigDecimal.ZERO;
        this.quantidadeAmortizacao = 0;
        this.valorAmortizacao = BigDecimal.ZERO;
    }

    public List<ReducaoValorBemContabil> getReducoesValorBemContabil() {
        return reducoesValorBemContabil;
    }

    public void setReducoesValorBemContabil(List<ReducaoValorBemContabil> reducoesValorBemContabil) {
        this.reducoesValorBemContabil = reducoesValorBemContabil;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public String getUsuarioOperacao() {
        return usuarioOperacao;
    }

    public void setUsuarioOperacao(String usuarioOperacao) {
        this.usuarioOperacao = usuarioOperacao;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public TipoReducaoValorBem getTipoReducao() {
        return tipoReducao;
    }

    public void setTipoReducao(TipoReducaoValorBem tipoReducao) {
        this.tipoReducao = tipoReducao;
    }

    public LogReducaoOuEstorno getLogReducaoOuEstorno() {
        return logReducaoOuEstorno;
    }

    public void setLogReducaoOuEstorno(LogReducaoOuEstorno logReducaoOuEstorno) {
        this.logReducaoOuEstorno = logReducaoOuEstorno;
    }

    public List<ReducaoValorBem> getReducoes() {
        return reducoes;
    }

    public void setReducoes(List<ReducaoValorBem> reducoes) {
        this.reducoes = reducoes;
    }

    public List<ReducaoValorBem> getReducoesOrdenadas() {
        if (getReducoes() ==null || getReducoes().isEmpty()){
            return Lists.newArrayList();
        }
        List<ReducaoValorBem> toReturn = getReducoes();
        Collections.sort(toReturn, new Comparator<ReducaoValorBem>() {
            @Override
            public int compare(ReducaoValorBem o1, ReducaoValorBem o2) {
                return o1.getBem().getIdentificacao().compareTo(o2.getBem().getIdentificacao());
            }
        });

        return toReturn;
    }

    public List<ReducaoValorBemResidual> getBensResidual() {
        return bensResidual;
    }

    public void setBensResidual(List<ReducaoValorBemResidual> bensResidual) {
        this.bensResidual = bensResidual;
    }

    public List<ReducaoValorBemResidual> getBensResidualOrdenados() {
        List<ReducaoValorBemResidual> toReturn = getBensResidual();
        if (toReturn != null) {
            Collections.sort(toReturn, new Comparator<ReducaoValorBemResidual>() {
                @Override
                public int compare(ReducaoValorBemResidual o1, ReducaoValorBemResidual o2) {
                    return o1.getBem().getIdentificacao().compareTo(o2.getBem().getIdentificacao());
                }
            });
        }
        return toReturn;
    }

    public TipoBem getTipoBem() {
        return tipoBem;
    }

    public void setTipoBem(TipoBem tipoBem) {
        this.tipoBem = tipoBem;
    }

    public UnidadeOrganizacional getUnidadeOrcamentaria() {
        return unidadeOrcamentaria;
    }

    public void setUnidadeOrcamentaria(UnidadeOrganizacional unidadeOrcamentaria) {
        this.unidadeOrcamentaria = unidadeOrcamentaria;
    }

    public HierarquiaOrganizacional getHierarquiaOrcamentaria() {
        return hierarquiaOrcamentaria;
    }

    public void setHierarquiaOrcamentaria(HierarquiaOrganizacional hierarquiaOrcamentaria) {
        this.unidadeOrcamentaria = hierarquiaOrcamentaria != null ? hierarquiaOrcamentaria.getSubordinada() : null;
        this.hierarquiaOrcamentaria = hierarquiaOrcamentaria;
    }

    public List<ReducaoValorBemNaoAplicavel> getBensNaoAplicaveis() {
        return bensNaoAplicaveis;
    }

    public void setBensNaoAplicaveis(List<ReducaoValorBemNaoAplicavel> bensNaoAplicaveis) {
        this.bensNaoAplicaveis = bensNaoAplicaveis;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public SituacaoReducaoValorBem getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoReducaoValorBem situacao) {
        this.situacao = situacao;
    }

    public List<GrupoBem> getGruposBemNaoAplicavel() {
        if (this.bensNaoAplicaveis != null && !this.bensNaoAplicaveis.isEmpty()) {
            List<GrupoBem> gruposBem = Lists.newArrayList();
            for (ReducaoValorBemNaoAplicavel bensNaoAplicaveis : this.bensNaoAplicaveis) {
                if (!gruposBem.contains(bensNaoAplicaveis.getGrupoBem())) {
                    gruposBem.add(bensNaoAplicaveis.getGrupoBem());
                }
            }
            Collections.sort(gruposBem, new Comparator<GrupoBem>() {
                @Override
                public int compare(GrupoBem o1, GrupoBem o2) {
                    return o1.getCodigo().compareTo(o2.getCodigo());
                }
            });
            return gruposBem;
        }
        return Lists.newArrayList();
    }

    public List<Bem> getBenNaoAplicaveisPorGrupoBem(GrupoBem grupoBem) {
        if (this.bensNaoAplicaveis != null && !this.bensNaoAplicaveis.isEmpty()) {
            List<Bem> bens = Lists.newArrayList();
            for (ReducaoValorBemNaoAplicavel bensNaoAplicaveis : this.bensNaoAplicaveis) {
                if (bensNaoAplicaveis.getGrupoBem().equals(grupoBem)) {
                    bens.add(bensNaoAplicaveis.getBem());
                }
            }
            Collections.sort(bens, new Comparator<Bem>() {
                @Override
                public int compare(Bem o1, Bem o2) {
                    return o1.getIdentificacao().compareTo(o2.getIdentificacao());
                }
            });
            return bens;
        }
        return Lists.newArrayList();
    }


    public BigDecimal getValorTotalInconsistencia() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        if (this.logReducaoOuEstorno.getMensagens() != null) {
            for (MsgLogReducaoOuEstorno msgLogReducaoOuEstorno : this.logReducaoOuEstorno.getMensagens()) {
                valorTotal = valorTotal.add(msgLogReducaoOuEstorno.getValor() != null ? msgLogReducaoOuEstorno.getValor() : BigDecimal.ZERO);
            }
        }
        return valorTotal;
    }

    public BigDecimal getValorTotalNaoAplicavel() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        if (this.getBensNaoAplicaveis() != null) {
            for (ReducaoValorBemNaoAplicavel reducaoValorBemNaoAplicavel : this.getBensNaoAplicaveis()) {
                valorTotal = valorTotal.add(reducaoValorBemNaoAplicavel.getValorOriginal());
            }
        }
        return valorTotal;
    }

    public BigDecimal getValorTotalResidual() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        if (this.getBensResidual() != null) {
            for (ReducaoValorBemResidual reducaoValorBemResidual : this.getBensResidual()) {
                valorTotal = valorTotal.add(reducaoValorBemResidual.getValorOriginal());
            }
        }
        return valorTotal;
    }

    public boolean isEstornada() {
        return SituacaoReducaoValorBem.ESTORNADA.equals(this.situacao);
    }

    public boolean isEmElaboracao() {
        return SituacaoReducaoValorBem.EM_ELABORACAO.equals(this.situacao);
    }

    public boolean isEmAndamento() {
        return SituacaoReducaoValorBem.EM_ANDAMENTO.equals(this.situacao);
    }

    public boolean isEstornoEmAndamento() {
        return SituacaoReducaoValorBem.ESTORNO_EM_ANDAMENTO.equals(this.situacao);
    }

    public boolean isConcluida() {
        return SituacaoReducaoValorBem.CONCLUIDA.equals(this.situacao);
    }

    public BigDecimal getResidualValorTotalOriginal() {
        BigDecimal toReturn = BigDecimal.ZERO;
        if (this.getBensResidual() != null) {
            for (ReducaoValorBemResidual reducaoValorBemResidual : this.getBensResidual()) {
                toReturn = toReturn.add(reducaoValorBemResidual.getValorOriginal());
            }
        }
        return toReturn;
    }

    public BigDecimal getResidualValorTotalLiquido() {
        BigDecimal toReturn = BigDecimal.ZERO;
        if (this.getBensResidual() != null) {
            for (ReducaoValorBemResidual reducaoValorBemResidual : this.getBensResidual()) {
                toReturn = toReturn.add(reducaoValorBemResidual.getValorLiquido());
            }
        }
        return toReturn;
    }

    public Integer getQuantidadeDepreciacao() {
        return quantidadeDepreciacao != null ? quantidadeDepreciacao : 0;
    }

    public void setQuantidadeDepreciacao(Integer quantidadeDepreciacao) {
        this.quantidadeDepreciacao = quantidadeDepreciacao;
    }

    public BigDecimal getValorDepreciacao() {
        return valorDepreciacao != null ? valorDepreciacao : BigDecimal.ZERO;
    }

    public void setValorDepreciacao(BigDecimal valorDepreciacao) {
        this.valorDepreciacao = valorDepreciacao;
    }

    public Integer getQuantidadeExaustao() {
        return quantidadeExaustao != null ? quantidadeExaustao : 0;
    }

    public void setQuantidadeExaustao(Integer quantidadeExaustao) {
        this.quantidadeExaustao = quantidadeExaustao;
    }

    public BigDecimal getValorExaustao() {
        return valorExaustao != null ? valorExaustao : BigDecimal.ZERO;
    }

    public void setValorExaustao(BigDecimal valorExaustao) {
        this.valorExaustao = valorExaustao;
    }

    public Integer getQuantidadeAmortizacao() {
        return quantidadeAmortizacao != null ? quantidadeAmortizacao : 0;
    }

    public void setQuantidadeAmortizacao(Integer quantidadeAmortizacao) {
        this.quantidadeAmortizacao = quantidadeAmortizacao;
    }

    public BigDecimal getValorAmortizacao() {
        return valorAmortizacao != null ? valorAmortizacao : BigDecimal.ZERO;
    }

    public void setValorAmortizacao(BigDecimal valorAmortizacao) {
        this.valorAmortizacao = valorAmortizacao;
    }

    @Override
    public String toString() {
        try {
            return codigo + " - " + DataUtil.getDataFormatada(dataLancamento) + " - " + unidadeOrcamentaria;
        }catch (NullPointerException e){
            return "";
        }
    }
}
