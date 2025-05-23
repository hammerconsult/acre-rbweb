/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.OrigemSolicitacaoEmpenho;
import br.com.webpublico.enums.TipoEmpenho;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * @author venon
 */
@Entity
@Audited
@GrupoDiagrama(nome = "ReservaDeDotacao")
@Etiqueta("Solicitação de Empenho")

public class SolicitacaoEmpenho extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Data da Solicitação")
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    private Date dataSolicitacao;

    @Obrigatorio
    @Monetario
    @Etiqueta("Valor")
    @Tabelavel
    @Pesquisavel
    private BigDecimal valor;

    @Obrigatorio
    @Etiqueta("Despesa Orçamentária")
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    private DespesaORC despesaORC;

    @Etiqueta("Fonte de Despesa Orçamentária")
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    private FonteDespesaORC fonteDespesaORC;

    @Obrigatorio
    @Etiqueta("Usuário")
    @ManyToOne
    private UsuarioSistema usuarioSistema;

    @Etiqueta("Conta de Despesa Desdobrada")
    @ManyToOne
    private Conta contaDespesaDesdobrada;

    @Obrigatorio
    @Etiqueta("Unidade Organizacional")
    @Pesquisavel
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;

    @Transient
    private HierarquiaOrganizacional hierarquiaOrcamentaria;

    @Etiqueta("Histórico Contábil")
    @ManyToOne
    private HistoricoContabil historicoContabil;

    @Etiqueta("Complemento Histórico")
    private String complementoHistorico;

    @Etiqueta("Tipo de Empenho")
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    private TipoEmpenho tipoEmpenho;

    @Etiqueta("Tipo de Empenho")
    @Enumerated(EnumType.STRING)
    private OrigemSolicitacaoEmpenho origemSolicitacaoEmpenho;

    @Obrigatorio
    @Etiqueta("Fornecedor")
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    private Pessoa fornecedor;

    @Invisivel
    @OneToOne(cascade = CascadeType.MERGE)
    @Etiqueta("Empenho")
    @Pesquisavel
    private Empenho empenho;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Classe")
    @Tabelavel
    @Pesquisavel
    private ClasseCredor classeCredor;

    @ManyToOne
    private Contrato contrato;

    @Deprecated
    @ManyToOne
    private ReconhecimentoDivida reconhecimentoDivida;

    private Boolean gerarReserva;
    private Boolean estornada;

    public SolicitacaoEmpenho() {
        super();
        valor = new BigDecimal(BigInteger.ZERO);
        this.estornada = false;
    }

    public SolicitacaoEmpenho(Date dataSolicitacao, BigDecimal valor, DespesaORC despesaORC, FonteDespesaORC fonteDespesaORC, UsuarioSistema usuarioSistema, Conta contaDespesaDesdobrada, UnidadeOrganizacional unidadeOrganizacional, HistoricoContabil historicoContabil, String complementoHistorico, TipoEmpenho tipoEmpenho, Pessoa fornecedor, Empenho empenho, Long criadoEm) {
        this.dataSolicitacao = dataSolicitacao;
        this.valor = valor;
        this.despesaORC = despesaORC;
        this.fonteDespesaORC = fonteDespesaORC;
        this.usuarioSistema = usuarioSistema;
        this.contaDespesaDesdobrada = contaDespesaDesdobrada;
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.historicoContabil = historicoContabil;
        this.complementoHistorico = complementoHistorico;
        this.tipoEmpenho = tipoEmpenho;
        this.fornecedor = fornecedor;
        this.empenho = empenho;
        this.criadoEm = criadoEm;
        this.estornada = false;
    }

    public SolicitacaoEmpenho(Long id, Date dataSolicitacao, BigDecimal valor) {
        this.id = id;
        this.dataSolicitacao = dataSolicitacao;
        this.valor = valor;
        this.estornada = false;
    }

    public SolicitacaoEmpenho(Empenho empenho) {
        this.dataSolicitacao = empenho.getDataEmpenho();
        this.valor = empenho.getValor();
        this.despesaORC = empenho.getDespesaORC();
        this.fonteDespesaORC = empenho.getFonteDespesaORC();
        this.usuarioSistema = empenho.getUsuarioSistema();
        this.contaDespesaDesdobrada = empenho.hasDesdobramento() ? empenho.getDesdobramentos().get(0).getConta() : null;
        this.unidadeOrganizacional = empenho.getUnidadeOrganizacional();
        this.historicoContabil = empenho.getHistoricoContabil();
        this.complementoHistorico = empenho.getComplementoHistorico();
        this.tipoEmpenho = empenho.getTipoEmpenho();
        this.fornecedor = empenho.getFornecedor();
        this.empenho = empenho;
        this.classeCredor = empenho.getClasseCredor();
        this.contrato = empenho.getContrato();
        this.gerarReserva = false;
        this.origemSolicitacaoEmpenho = OrigemSolicitacaoEmpenho.CONTRATO;
        this.estornada = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComplementoHistorico() {
        return complementoHistorico;
    }

    public void setComplementoHistorico(String complementoHistorico) {
        this.complementoHistorico = complementoHistorico;
    }

    public Conta getContaDespesaDesdobrada() {
        return contaDespesaDesdobrada;
    }

    public void setContaDespesaDesdobrada(Conta contaDespesaDesdobrada) {
        this.contaDespesaDesdobrada = contaDespesaDesdobrada;
    }

    public Date getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(Date dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public DespesaORC getDespesaORC() {
        return despesaORC;
    }

    public void setDespesaORC(DespesaORC despesaORC) {
        this.despesaORC = despesaORC;
    }

    public FonteDespesaORC getFonteDespesaORC() {
        return fonteDespesaORC;
    }

    public void setFonteDespesaORC(FonteDespesaORC fonteDespesaORC) {
        this.fonteDespesaORC = fonteDespesaORC;
    }

    public HistoricoContabil getHistoricoContabil() {
        return historicoContabil;
    }

    public void setHistoricoContabil(HistoricoContabil historicoContabil) {
        this.historicoContabil = historicoContabil;
    }

    public TipoEmpenho getTipoEmpenho() {
        return tipoEmpenho;
    }

    public void setTipoEmpenho(TipoEmpenho tipoEmpenho) {
        this.tipoEmpenho = tipoEmpenho;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Pessoa getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Pessoa fornecedor) {
        this.fornecedor = fornecedor;
    }

    public Empenho getEmpenho() {
        return empenho;
    }

    public void setEmpenho(Empenho empenho) {
        this.empenho = empenho;
    }

    public ClasseCredor getClasseCredor() {
        return classeCredor;
    }

    public void setClasseCredor(ClasseCredor classeCredor) {
        this.classeCredor = classeCredor;
    }

    public Boolean getGerarReserva() {
        return gerarReserva;
    }

    public void setGerarReserva(Boolean gerarReserva) {
        this.gerarReserva = gerarReserva;
    }

    @Override
    public String toString() {
        try {
            return DataUtil.getDataFormatada(dataSolicitacao) + " - " + complementoHistorico + " - " + Util.formataValor(valor);
        } catch (Exception e) {
            return dataSolicitacao + " - R$" + valor;
        }
    }

    public String getDescricaoCurta() {
        String descricao = complementoHistorico.length() > 70 ? complementoHistorico.substring(0, 70) + "..." : complementoHistorico;
        return DataUtil.getDataFormatada(dataSolicitacao) + " - " + descricao + " - " + Util.formataValor(valor);
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public HierarquiaOrganizacional getHierarquiaOrcamentaria() {
        return hierarquiaOrcamentaria;
    }

    public void setHierarquiaOrcamentaria(HierarquiaOrganizacional hierarquiaOrcamentaria) {
        this.hierarquiaOrcamentaria = hierarquiaOrcamentaria;
    }

    public OrigemSolicitacaoEmpenho getOrigemSolicitacaoEmpenho() {
        return origemSolicitacaoEmpenho;
    }

    public void setOrigemSolicitacaoEmpenho(OrigemSolicitacaoEmpenho origemSolicitacaoEmpenho) {
        this.origemSolicitacaoEmpenho = origemSolicitacaoEmpenho;
    }

    public Boolean getEstornada() {
        return estornada !=null ?estornada : false;
    }

    public void setEstornada(Boolean estornada) {
        this.estornada = estornada;
    }
}
