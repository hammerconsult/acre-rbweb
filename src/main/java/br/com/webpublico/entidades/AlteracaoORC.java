/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.StatusAlteracaoORC;
import br.com.webpublico.enums.TipoDespesaORC;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author reidocrime
 */
@Etiqueta("Alteração Orçamentária")
@GrupoDiagrama(nome = "Orcamentario")
@Audited
@Entity

public class AlteracaoORC extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Data da Alteração")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataAlteracao;

    @Etiqueta("Data da Efetivação")
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    private Date dataEfetivacao;

    @Etiqueta("Número")
    @Tabelavel
    @Pesquisavel
    private String codigo;

    @Etiqueta("Descrição")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private String descricao;

    @ManyToOne
    @Etiqueta("Unidade Administrativa")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Obrigatorio
    private UnidadeOrganizacional unidadeOrganizacionalAdm;

    @ManyToOne
    @Etiqueta("Unidade Orçamentária")
    @Obrigatorio
    private UnidadeOrganizacional unidadeOrganizacionalOrc;

    @Transient
    @Etiqueta("Valor Total")
    @Monetario
    @Tabelavel
    private BigDecimal valorTotal;

    @Tabelavel
    @Etiqueta("Ato Legal")
    @OneToOne
    private AtoLegal atoLegal;

    @Etiqueta("Situação")
    @Pesquisavel
    @Tabelavel
    @Enumerated(EnumType.STRING)
    private StatusAlteracaoORC status;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Data da Exclusão")
    @Pesquisavel
    private Date dataExclusao;

    @Pesquisavel
    @Etiqueta("Motivo da Exclusão")
    private String motivoExclusao;

    @ManyToOne
    @Etiqueta("Ato Legal Orçamentário")
    private AtoLegalORC atoLegalORC;

    @ManyToOne
    @Etiqueta("Exercício")
    private Exercicio exercicio;

    @OneToMany(mappedBy = "alteracaoORC", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SolicitacaoDespesaOrc> solicitacoes;

    @OneToMany(mappedBy = "alteracaoORC", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SuplementacaoORC> suplementacoesORCs;

    @OneToMany(mappedBy = "alteracaoORC", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnulacaoORC> anulacoesORCs;

    @OneToMany(mappedBy = "alteracaoORC", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReceitaAlteracaoORC> receitasAlteracoesORCs;

    private boolean efetivado;
    @Etiqueta("Justificativa da Suplementação")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Obrigatorio
    private String justificativaSuplementacao;

    @Etiqueta("Justificativa da Anulação")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Obrigatorio
    private String justificativaAnulacao;

    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Etiqueta("Tipo de Crédito")
    private TipoDespesaORC tipoDespesaORC;

    @ManyToOne
    private Arquivo arquivo;

    @Temporal(TemporalType.DATE)
    @Pesquisavel
    @Etiqueta("Data do Indeferimento")
    private Date dataIndeferimento;

    @Pesquisavel
    @Etiqueta("Motivo")
    private String motivoIndeferimento;

    @Transient
    @Tabelavel
    @Etiqueta("Unidade Orçamentária")
    private HierarquiaOrganizacional hierarquia;

    @Version
    private Long versao;

    public AlteracaoORC() {
        this.efetivado = false;
        this.suplementacoesORCs = new ArrayList<>();
        this.anulacoesORCs = new ArrayList<>();
        this.receitasAlteracoesORCs = new ArrayList<>();
        this.solicitacoes = new ArrayList<>();
    }

    public AlteracaoORC(String codigo, Date dataAlteracao, AtoLegalORC atoLegalORC, AtoLegal atoLegal, StatusAlteracaoORC statusAlteracaoORC, String descricao, Date dataEfetivacao) {
        this.codigo = codigo;
        this.dataAlteracao = dataAlteracao;
        this.dataEfetivacao = dataEfetivacao;
        this.atoLegalORC = atoLegalORC;
        this.atoLegal = atoLegal;
        this.efetivado = false;
        this.status = statusAlteracaoORC;
        this.descricao = descricao;
    }

    public AlteracaoORC(AlteracaoORC alteracaoORC, HierarquiaOrganizacional hierarquia) {
        this.setId(alteracaoORC.getId());
        this.setDataAlteracao(alteracaoORC.getDataAlteracao());
        this.setDataEfetivacao(alteracaoORC.getDataEfetivacao());
        this.setHierarquia(hierarquia);
        this.setCodigo(alteracaoORC.getCodigo());
        this.setDescricao(alteracaoORC.getDescricao());
        this.setAtoLegal(atoLegal);
        this.setStatus(alteracaoORC.getStatus());
        BigDecimal total = BigDecimal.ZERO;
        for (SuplementacaoORC suplementacaoORC : alteracaoORC.getSuplementacoesORCs()) {
            total = total.add(suplementacaoORC.getValor());
        }
        this.setValorTotal(total);
    }

    public HierarquiaOrganizacional getHierarquia() {
        return hierarquia;
    }

    public void setHierarquia(HierarquiaOrganizacional hierarquia) {
        this.hierarquia = hierarquia;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public AtoLegalORC getAtoLegalORC() {
        return atoLegalORC;
    }

    public void setAtoLegalORC(AtoLegalORC atoLegalORC) {
        this.atoLegalORC = atoLegalORC;
    }

    public Date getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(Date dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public List<AnulacaoORC> getAnulacoesORCs() {
        return anulacoesORCs;
    }

    public void setAnulacoesORCs(List<AnulacaoORC> anulacoesORCs) {
        this.anulacoesORCs = anulacoesORCs;
    }

    public List<ReceitaAlteracaoORC> getReceitasAlteracoesORCs() {
        return receitasAlteracoesORCs;
    }

    public void setReceitasAlteracoesORCs(List<ReceitaAlteracaoORC> receitasAlteracoesORCs) {
        this.receitasAlteracoesORCs = receitasAlteracoesORCs;
    }

    public List<SuplementacaoORC> getSuplementacoesORCs() {
        return suplementacoesORCs;
    }

    public void setSuplementacoesORCs(List<SuplementacaoORC> suplementacoesORCs) {
        this.suplementacoesORCs = suplementacoesORCs;
    }

    public boolean isEfetivado() {
        return efetivado;
    }

    public void setEfetivado(boolean efetivado) {
        this.efetivado = efetivado;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public StatusAlteracaoORC getStatus() {
        return status;
    }

    public void setStatus(StatusAlteracaoORC status) {
        this.status = status;
    }

    public Date getDataEfetivacao() {
        return dataEfetivacao;
    }

    public void setDataEfetivacao(Date dataEfetivacao) {
        this.dataEfetivacao = dataEfetivacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getJustificativaSuplementacao() {
        return justificativaSuplementacao;
    }

    public void setJustificativaSuplementacao(String justificativaSuplementacao) {
        this.justificativaSuplementacao = justificativaSuplementacao;
    }

    public String getJustificativaAnulacao() {
        return justificativaAnulacao;
    }

    public void setJustificativaAnulacao(String justificativaAnulacao) {
        this.justificativaAnulacao = justificativaAnulacao;
    }

    public UnidadeOrganizacional getUnidadeOrganizacionalAdm() {
        return unidadeOrganizacionalAdm;
    }

    public void setUnidadeOrganizacionalAdm(UnidadeOrganizacional unidadeOrganizacionalAdm) {
        this.unidadeOrganizacionalAdm = unidadeOrganizacionalAdm;
    }

    public UnidadeOrganizacional getUnidadeOrganizacionalOrc() {
        return unidadeOrganizacionalOrc;
    }

    public void setUnidadeOrganizacionalOrc(UnidadeOrganizacional unidadeOrganizacionalOrc) {
        this.unidadeOrganizacionalOrc = unidadeOrganizacionalOrc;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public List<SolicitacaoDespesaOrc> getSolicitacoes() {
        return solicitacoes;
    }

    public void setSolicitacoes(List<SolicitacaoDespesaOrc> solicitacoes) {
        this.solicitacoes = solicitacoes;
    }

    public String getMotivoIndeferimento() {
        return motivoIndeferimento;
    }

    public void setMotivoIndeferimento(String motivoIndeferimento) {
        this.motivoIndeferimento = motivoIndeferimento;
    }

    public Date getDataIndeferimento() {
        return dataIndeferimento;
    }

    public void setDataIndeferimento(Date dataIndeferimento) {
        this.dataIndeferimento = dataIndeferimento;
    }

    @Override
    public String toString() {
        if (codigo != null && descricao != null) {
            return codigo + " - " + descricao;
        } else {
            return codigo + " - " + unidadeOrganizacionalOrc.getDescricao();
        }
    }

    public TipoDespesaORC getTipoDespesaORC() {
        return tipoDespesaORC;
    }

    public void setTipoDespesaORC(TipoDespesaORC tipoDespesaORC) {
        this.tipoDespesaORC = tipoDespesaORC;
    }

    public String toStringAutocomplete() {
        int tamanho = 70;
        return codigo + " - " + (descricao.length() > tamanho ? descricao.substring(0, tamanho) : descricao);
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Boolean isAlteracaoEmElaboracao() {
        return this.status != null && StatusAlteracaoORC.ELABORACAO.equals(this.getStatus());
    }

    public Boolean isAlteracaoEmAnalise() {
        return this.status != null && StatusAlteracaoORC.EM_ANALISE.equals(this.getStatus());
    }

    public Boolean isAlteracaoDeferida() {
        return this.status != null && StatusAlteracaoORC.DEFERIDO.equals(this.getStatus());
    }

    public Boolean isAlteracaoIndeferida() {
        return this.status != null && StatusAlteracaoORC.INDEFERIDO.equals(this.getStatus());
    }

    public Boolean isAlteracaoEstornada() {
        return this.status != null && StatusAlteracaoORC.ESTORNADA.equals(this.getStatus());
    }

    public String getMotivoExclusao() {
        return motivoExclusao;
    }

    public void setMotivoExclusao(String motivoExclusao) {
        this.motivoExclusao = motivoExclusao;
    }

    public Date getDataExclusao() {
        return dataExclusao;
    }

    public void setDataExclusao(Date dataExclusao) {
        this.dataExclusao = dataExclusao;
    }
}
