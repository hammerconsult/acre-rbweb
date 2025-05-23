/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.contabil.execucao.DesdobramentoPagamento;
import br.com.webpublico.entidadesauxiliares.manad.ManadRegistro;
import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.enums.StatusPagamento;
import br.com.webpublico.enums.TipoDocPagto;
import br.com.webpublico.enums.TipoOperacaoPagto;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadeContabilComValor;
import br.com.webpublico.interfaces.IGeraContaAuxiliar;
import br.com.webpublico.interfaces.IManadRegistro;
import br.com.webpublico.negocios.manad.ManadContabilFacade;
import br.com.webpublico.util.*;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

/**
 * @author venon
 */
@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity

public class Pagamento extends SuperEntidade implements Serializable, EntidadeContabilComValor, IManadRegistro, IGeraContaAuxiliar {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Número")
    @Tabelavel
    @Pesquisavel
    @ErroReprocessamentoContabil
    private String numeroPagamento;
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @ErroReprocessamentoContabil
    @Etiqueta("Data de Previsão")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date previstoPara;
    @Etiqueta("Data do Pagamento")
    @Tabelavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @Pesquisavel
    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    private Date dataPagamento;
    @Etiqueta("Data de Conciliação")
    @Tabelavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @Pesquisavel
    private Date dataConciliacao;
    @Pesquisavel
    @Tabelavel
    @ErroReprocessamentoContabil
    @Etiqueta("Liquidação")
    @ManyToOne
    @Obrigatorio
    private Liquidacao liquidacao;
    @Pesquisavel
    @Etiqueta("Valor(R$)")
    @Monetario
    @ErroReprocessamentoContabil
    @Obrigatorio
    private BigDecimal valor;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Situação")
    private StatusPagamento status;
    @Obrigatorio
    @ErroReprocessamentoContabil
    private BigDecimal saldo;
    @Etiqueta("Usuário")
    @Tabelavel(campoSelecionado = false)
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @Etiqueta("Histórico Padrão")
    @ManyToOne
    private HistoricoContabil historicoContabil;
    @Etiqueta("Histórico")
    @Obrigatorio
    @Tabelavel(campoSelecionado = false)
    private String complementoHistorico;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    private String numeroPagamentoOriginal;
    @Etiqueta("Número da Previsão")
    private String numeroPrevisao;
    private Boolean pago;
    @Invisivel
    @Tabelavel(campoSelecionado = false)
    @OneToMany(mappedBy = "pagamento")
//    @IndexColumn(name="id")
    private List<PagamentoEstorno> pagamentoEstornos;
    @Invisivel
    @ManyToOne
    private MovimentoDespesaORC movimentoDespesaORC;
    @OneToMany(mappedBy = "pagamento", cascade = CascadeType.ALL, orphanRemoval = true)
//    @IndexColumn(name="id")
    @Tabelavel(campoSelecionado = false)
    private List<RetencaoPgto> retencaoPgtos;
    @OneToMany(mappedBy = "pagamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GuiaPagamento> guiaPagamento;
    @Obrigatorio
    @Etiqueta("Conta Financeira")
    @ManyToOne
    @Tabelavel(campoSelecionado = false)
    private SubConta subConta;
    @Invisivel
    @Transient
    private Long criadoEm;
    @Enumerated(EnumType.STRING)
    @ReprocessamentoContabil
    private CategoriaOrcamentaria categoriaOrcamentaria;
    @OneToOne
    private Pagamento pagamento;
    @ManyToOne
    private Exercicio exercicio;
    @ManyToOne
    private Exercicio exercicioOriginal;
    @Obrigatorio
    @Etiqueta("Tipo de Documento")
    @Enumerated(EnumType.STRING)
    @Tabelavel(campoSelecionado = false)
    private TipoDocPagto tipoDocPagto;
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Operação")
    @Tabelavel(campoSelecionado = false)
    private TipoOperacaoPagto tipoOperacaoPagto;
    @Etiqueta("Ordem Bancária")
    @Tabelavel(campoSelecionado = false)
    private String numDocumento;
    @Etiqueta("Valor Final")
    @Monetario
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    private BigDecimal valorFinal;
    @Etiqueta("Saldo Final")
    @Monetario
    @Tabelavel(campoSelecionado = false)
    private BigDecimal saldoFinal;
    @Etiqueta("Unidade Organizacional")
    @ManyToOne
    @ReprocessamentoContabil
    private UnidadeOrganizacional unidadeOrganizacional;
    @Etiqueta("Unidade Organizacional Administrativa")
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacionalAdm;
    @ManyToOne
    @ErroReprocessamentoContabil
    @ReprocessamentoContabil
    @Etiqueta("Evento Contábil")
    @Tabelavel(campoSelecionado = false)
    private EventoContabil eventoContabil;
    private String historicoNota;
    private String historicoRazao;
    @Etiqueta("Conta Bancária Fornecedor")
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    private ContaCorrenteBancaria contaPgto;
    @ManyToOne
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Finalidade do Pagamento")
    private FinalidadePagamento finalidadePagamento;
    @Etiqueta("Número RE")
    private String numeroRE;
    private Long idObn600;
    private Long idObn350;
    private String uuid;
    @ManyToOne
    private Identificador identificador;
    @Version
    private Long versao;

    @OneToMany(mappedBy = "pagamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DesdobramentoPagamento> desdobramentos;

    public Pagamento(Long id) {
        this.id = id;
    }

    public Pagamento() {
        pago = false;
        valor = new BigDecimal(BigInteger.ZERO);
        saldo = new BigDecimal(BigInteger.ZERO);
        saldoFinal = new BigDecimal(BigInteger.ZERO);
        status = StatusPagamento.ABERTO;
        criadoEm = System.nanoTime();
        pagamentoEstornos = new ArrayList<PagamentoEstorno>();
        guiaPagamento = new ArrayList<GuiaPagamento>();
        retencaoPgtos = new ArrayList<RetencaoPgto>();
        valorFinal = new BigDecimal(BigInteger.ZERO);
        this.desdobramentos = Lists.newArrayList();
    }

    public BigDecimal getSaldoFinal() {
        return saldoFinal;
    }

    public void setSaldoFinal(BigDecimal saldoFinal) {
        this.saldoFinal = saldoFinal;
    }

    public TipoOperacaoPagto getTipoOperacaoPagto() {
        return tipoOperacaoPagto;
    }

    public void setTipoOperacaoPagto(TipoOperacaoPagto tipoOperacaoPagto) {
        this.tipoOperacaoPagto = tipoOperacaoPagto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public HistoricoContabil getHistoricoContabil() {
        return historicoContabil;
    }

    public void setHistoricoContabil(HistoricoContabil historicoContabil) {
        this.historicoContabil = historicoContabil;
    }

    public Liquidacao getLiquidacao() {
        return liquidacao;
    }

    public void setLiquidacao(Liquidacao liquidacao) {
        this.liquidacao = liquidacao;
    }

    public Date getPrevistoPara() {
        return previstoPara;
    }

    public void setPrevistoPara(Date previstoPara) {
        this.previstoPara = previstoPara;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
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

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public String getNumeroPagamento() {
        return numeroPagamento;
    }

    public void setNumeroPagamento(String numeroPagamento) {
        this.numeroPagamento = numeroPagamento;
    }

    public String getNumeroPrevisao() {
        return numeroPrevisao;
    }

    public void setNumeroPrevisao(String numeroPrevisao) {
        this.numeroPrevisao = numeroPrevisao;
    }

    public Boolean getPago() {
        return pago;
    }

    public void setPago(Boolean pago) {
        this.pago = pago;
    }

    public List<PagamentoEstorno> getPagamentoEstornos() {
        return pagamentoEstornos;
    }

    public void setPagamentoEstornos(List<PagamentoEstorno> pagamentoEstornos) {
        this.pagamentoEstornos = pagamentoEstornos;
    }

    public MovimentoDespesaORC getMovimentoDespesaORC() {
        return movimentoDespesaORC;
    }

    public void setMovimentoDespesaORC(MovimentoDespesaORC movimentoDespesaORC) {
        this.movimentoDespesaORC = movimentoDespesaORC;
    }

    public List<RetencaoPgto> getRetencaoPgtos() {
        return retencaoPgtos;
    }

    public void setRetencaoPgtos(List<RetencaoPgto> retencaoPgtos) {
        this.retencaoPgtos = retencaoPgtos;
    }

    public StatusPagamento getStatus() {
        return status;
    }

    public void setStatus(StatusPagamento status) {
        this.status = status;
    }

    public SubConta getSubConta() {
        return subConta;
    }

    public void setSubConta(SubConta subConta) {
        this.subConta = subConta;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public CategoriaOrcamentaria getCategoriaOrcamentaria() {
        return categoriaOrcamentaria;
    }

    public void setCategoriaOrcamentaria(CategoriaOrcamentaria categoriaOrcamentaria) {
        this.categoriaOrcamentaria = categoriaOrcamentaria;
    }

    public Pagamento getPagamento() {
        return pagamento;
    }

    public void setPagamento(Pagamento pagamento) {
        this.pagamento = pagamento;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Exercicio getExercicioOriginal() {
        return exercicioOriginal;
    }

    public void setExercicioOriginal(Exercicio exercicioOriginal) {
        this.exercicioOriginal = exercicioOriginal;
    }

    public String getNumeroPagamentoOriginal() {
        return numeroPagamentoOriginal;
    }

    public void setNumeroPagamentoOriginal(String numeroPagamentoOriginal) {
        this.numeroPagamentoOriginal = numeroPagamentoOriginal;
    }

    public String getComplementoHistorico() {
        return complementoHistorico;
    }

    public void setComplementoHistorico(String complementoHistorico) {
        this.complementoHistorico = complementoHistorico;
    }

    public String getNumDocumento() {
        return numDocumento;
    }

    public void setNumDocumento(String numDocumento) {
        this.numDocumento = numDocumento;
    }

    public TipoDocPagto getTipoDocPagto() {
        return tipoDocPagto;
    }

    public void setTipoDocPagto(TipoDocPagto tipoDocPagto) {
        this.tipoDocPagto = tipoDocPagto;
    }

    public BigDecimal getValorFinal() {
        return valorFinal;
    }

    public void setValorFinal(BigDecimal valorFinal) {
        this.valorFinal = valorFinal;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public Identificador getIdentificador() {
        return identificador;
    }

    public void setIdentificador(Identificador identificador) {
        this.identificador = identificador;
    }

    public UnidadeOrganizacional getUnidadeOrganizacionalAdm() {
        return unidadeOrganizacionalAdm;
    }

    public void setUnidadeOrganizacionalAdm(UnidadeOrganizacional unidadeOrganizacionalAdm) {
        this.unidadeOrganizacionalAdm = unidadeOrganizacionalAdm;
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public String getHistoricoNota() {
        return historicoNota;
    }

    public void setHistoricoNota(String historicoNota) {
        this.historicoNota = historicoNota;
    }

    public String getHistoricoRazao() {
        return historicoRazao;
    }

    public void setHistoricoRazao(String historicoRazao) {
        this.historicoRazao = historicoRazao;
    }

    public Date getDataConciliacao() {
        return dataConciliacao;
    }

    public void setDataConciliacao(Date dataConciliacao) {
        this.dataConciliacao = dataConciliacao;
    }

    public FinalidadePagamento getFinalidadePagamento() {
        return finalidadePagamento;
    }

    public void setFinalidadePagamento(FinalidadePagamento finalidadePagamento) {
        this.finalidadePagamento = finalidadePagamento;
    }

    public String getNumeroRE() {
        return numeroRE;
    }

    public void setNumeroRE(String numeroRE) {
        this.numeroRE = numeroRE;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Long getVersao() {
        return versao;
    }

    public void setVersao(Long versao) {
        this.versao = versao;
    }

    public List<DesdobramentoPagamento> getDesdobramentos() {
        return desdobramentos;
    }

    public void setDesdobramentos(List<DesdobramentoPagamento> desdobramentos) {
        this.desdobramentos = desdobramentos;
    }

    public void gerarHistoricoNota() {
        historicoNota = "";
        if (this.getLiquidacao().getEmpenho() != null) {
            historicoNota += " Pagamento: " + this.getNumeroPagamento() + "/" + this.getExercicio().getAno() + UtilBeanContabil.SEPARADOR_HISTORICO;
            historicoNota += " Liquidação: " + this.getLiquidacao().getNumero() + "/" + this.getLiquidacao().getExercicio().getAno() + UtilBeanContabil.SEPARADOR_HISTORICO;
            historicoNota += this.getLiquidacao().getEmpenho().getNumeroExercicioParaHistorico() + UtilBeanContabil.SEPARADOR_HISTORICO;
            historicoNota += " Dotação: " + this.getLiquidacao().getEmpenho().getDespesaORC().getHistoricoPadrao() + UtilBeanContabil.SEPARADOR_HISTORICO;
            historicoNota += " Conta de Despesa: " + this.getLiquidacao().getEmpenho().getDespesaORC().getDescricaoContaDespesaPPA() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getSubConta() != null) {
            historicoNota += "Conta Financeira: " + this.getSubConta().getCodigo() + "/" + this.getSubConta().getDescricao() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getLiquidacao().getEmpenho().getFonteDespesaORC() != null) {
            historicoNota += "Fonte de Recursos: " + ((ContaDeDestinacao) this.getLiquidacao().getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos()).getFonteDeRecursos().toString().trim() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
    }

    public void gerarHistoricoRazao() {
        String historicoEvento = "";
        if (this.getEventoContabil().getClpHistoricoContabil() != null) {
            historicoEvento = this.getEventoContabil().getClpHistoricoContabil().toString();
        }
        this.historicoRazao = historicoEvento + " " + this.historicoNota;
        this.historicoRazao = Util.cortarString(this.historicoNota, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public void gerarHistoricos() {
        gerarHistoricoNota();
        gerarHistoricoRazao();
    }

    public ContaCorrenteBancaria getContaPgto() {
        return contaPgto;
    }

    public void setContaPgto(ContaCorrenteBancaria contaPgto) {
        this.contaPgto = contaPgto;
    }

    public List<GuiaPagamento> getGuiaPagamento() {
        return guiaPagamento;
    }

    public void setGuiaPagamento(List<GuiaPagamento> guiaPagamento) {
        this.guiaPagamento = guiaPagamento;
    }

    public BigDecimal getValorTotalGuiasPagamento() {
        BigDecimal valor = BigDecimal.ZERO;
        for (GuiaPagamento guiaPag : getGuiaPagamento()) {
            valor = valor.add(guiaPag.getValorTotalPorGuia());
        }
        return valor;
    }

    @Override
    public String toString() {
        try {
            return "Nº " + numeroPagamento + " - " + liquidacao.getEmpenho().getFornecedor().getNome() + " - " + new SimpleDateFormat("dd/MM/yyyy").format(previstoPara) + " - " + Util.formataValor(valor);
        } catch (Exception e) {
            return "N° " + numeroPagamento + " - " + Util.formataValor(valor);
        }

    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return numeroPagamento + "/" + exercicio.getAno() + " - " + liquidacao.getNumero();
    }

    @Override
    public String getComplementoHistoricoPrestacaoDeContas() {
        String retorno = toString();
        if (liquidacao.getEmpenho().isEmpenhoRestoPagar()) {
            retorno = "Pagamento de Restos a Pagar: nº " + numeroPagamento + " - " + DataUtil.getDataFormatada(dataPagamento) + " - " +
                "Liquidação de Restos a Pagar: nº ";
            if (liquidacao.getLiquidacao() != null) {
                retorno += liquidacao.getLiquidacao().getNumero() + " - " + DataUtil.getDataFormatada(liquidacao.getLiquidacao().getDataLiquidacao()) + " - ";
            } else {
                retorno += liquidacao.getNumero() + " - " + DataUtil.getDataFormatada(liquidacao.getDataLiquidacao()) + " - ";
            }
            retorno += "Restos a Pagar: nº " + liquidacao.getEmpenho().getNumero() + " - " + DataUtil.getDataFormatada(liquidacao.getEmpenho().getDataEmpenho()) + " - " +
                "Empenho: nº " + liquidacao.getEmpenho().getEmpenho().getNumero() + " - " + DataUtil.getDataFormatada(liquidacao.getEmpenho().getEmpenho().getDataEmpenho()) + " - " +
                "Função " + liquidacao.getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getAcaoPrincipal().getFuncao().getCodigo() + " - " + liquidacao.getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getAcaoPrincipal().getFuncao().getDescricao() + " - " +
                "Subfunção " + liquidacao.getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getAcaoPrincipal().getSubFuncao().getCodigo() + " - " + liquidacao.getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getAcaoPrincipal().getSubFuncao().getDescricao() + " - " +
                "Conta de Despesa " + liquidacao.getEmpenho().getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getCodigo() + " - " + liquidacao.getEmpenho().getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getDescricao() + " - " +
                "Fonte de Recurso " + liquidacao.getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao().getFonteDeRecursos().getCodigo() + " - " + liquidacao.getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao().getFonteDeRecursos().getDescricao() + " - " +
                "Pessoa " + liquidacao.getEmpenho().getFornecedor().getNome() + " - " +
                "Valor " + Util.formataValor(valor);
        }
        return retorno;
    }

    @Override
    public ManadRegistro.ManadModulo getModulo() {
        return ManadRegistro.ManadModulo.CONTABIL;
    }

    @Override
    public ManadRegistro.ManadRegistroTipo getTipoRegistro() {
        return ManadRegistro.ManadRegistroTipo.L150;
    }

    @Override
    public void getConteudoManad(ManadContabilFacade facade, StringBuilder conteudo) {
        HierarquiaOrganizacional unidade = ((ManadContabilFacade) facade).getHierarquiaDaUnidade(this.getUnidadeOrganizacional(), this.getDataPagamento());
        HierarquiaOrganizacional orgao = ((ManadContabilFacade) facade).getHierarquiaDaUnidade(unidade.getSuperior(), this.getDataPagamento());

        SubConta contaDebito = this.getSubConta();
        ContaCorrenteBancaria contaCredito = this.getContaPgto();
        String orgaoUnidade = orgao.getCodigoNo() + unidade.getCodigoNo();

        ManadUtil.criaLinha(1, ManadRegistro.ManadRegistroTipo.L150.name(), conteudo);
        ManadUtil.criaLinha(2, this.getLiquidacao().getEmpenho().getNumero(), conteudo);
        ManadUtil.criaLinha(3, this.getNumeroPagamento(), conteudo);
        ManadUtil.criaLinha(4, ManadUtil.getDataSemBarra(this.getDataPagamento()), conteudo);
        ManadUtil.criaLinha(5, ManadUtil.getValor(this.getValorFinal()), conteudo);
        ManadUtil.criaLinha(6, "D", conteudo);
        ManadUtil.criaLinha(7, this.getComplementoHistorico() == null ? " " : this.getComplementoHistorico(), conteudo);
        ManadUtil.criaLinha(8, contaDebito != null ? contaDebito.getContaBancariaEntidade().getNumeroContaComDigitoVerificador() : "", conteudo);
        ManadUtil.criaLinha(9, orgaoUnidade, conteudo);
        ManadUtil.criaLinha(10, contaCredito != null ? contaCredito.getNumeroContaComDigito() : "", conteudo);
        ManadUtil.criaLinha(11, orgaoUnidade, conteudo);
        ManadUtil.quebraLinha(conteudo);
    }

    public String getHistoricoArquivoOBN600() {
        String historico = this.complementoHistorico.trim();
        historico = historico.replace("\n", "");
        historico = historico.replace("\r", "");
        historico = historico.replace("\t", "");
        return historico;
    }

    public Boolean isPagamentoComGuia() {
        return (TipoDocPagto.FATURA.equals(this.tipoDocPagto)
            || TipoDocPagto.CONVENIO.equals(this.tipoDocPagto)
            || TipoDocPagto.GPS.equals(this.tipoDocPagto)
            || TipoDocPagto.DARF.equals(this.tipoDocPagto)
            || TipoDocPagto.DARF_SIMPLES.equals(this.tipoDocPagto));
    }

    public Boolean isPagamentoComOperacaoDeGuia() {
        if (TipoOperacaoPagto.PAGAMENTO_DE_GUIA_COM_CODIGO_DE_BARRA.equals(this.tipoOperacaoPagto)
            || TipoOperacaoPagto.PAGAMENTO_DE_GUIA_COM_CODIGO_DE_BARRA_NAO_CONTA_UNICA.equals(this.tipoOperacaoPagto)
            || TipoOperacaoPagto.PAGAMENTO_DE_GUIA_SEM_CODIGO_DE_BARRA.equals(this.tipoOperacaoPagto)
            || TipoOperacaoPagto.PAGAMENTO_DE_GUIA_SEM_CODIGO_DE_BARRA_NAO_CONTA_UNICA.equals(this.tipoOperacaoPagto)) {
            return true;
        }
        return false;
    }

    public boolean isPagamentoDeferido() {
        return this.status != null && StatusPagamento.DEFERIDO.equals(this.status);
    }

    public boolean isPagamentoPago() {
        return this.status != null && StatusPagamento.PAGO.equals(this.status);
    }

    public boolean isPagamentoAberto() {
        return this.status != null && StatusPagamento.ABERTO.equals(this.status);
    }

    public boolean isPagamentoEfetuado() {
        return this.status != null && StatusPagamento.EFETUADO.equals(this.status);
    }

    public boolean isPagamentoIndeferido() {
        return this.status != null && StatusPagamento.INDEFERIDO.equals(this.status);
    }

    public BigDecimal getTotalDesdobramentos() {
        BigDecimal total = BigDecimal.ZERO;
        for (DesdobramentoPagamento d : this.getDesdobramentos()) {
            total = total.add(d.getValor());
        }
        return total;
    }

    public BigDecimal getSaldoTotalDesdobramentos() {
        BigDecimal total = BigDecimal.ZERO;
        for (DesdobramentoPagamento desd : this.getDesdobramentos()) {
            total = total.add(desd.getSaldo());
        }
        return total;
    }

    public Long getIdObn600() {
        return idObn600;
    }

    public void setIdObn600(Long idObn600) {
        this.idObn600 = idObn600;
    }

    public Long getIdObn350() {
        return idObn350;
    }

    public void setIdObn350(Long idObn350) {
        this.idObn350 = idObn350;
    }

    @Override
    public TreeMap getMapContaAuxiliarSistema(TipoContaAuxiliar tipoContaAuxiliar) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarDetalhadaSiconfi(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        AcaoPPA acaoPPA = liquidacao.getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA();
        if (liquidacao != null && liquidacao.getEmpenho() != null &&
            liquidacao.getEmpenho().getCodigoCO() != null && liquidacao.getEmpenho().getContaDeDestinacao() != null) {
            liquidacao.getEmpenho().getContaDeDestinacao().setCodigoCOEmenda(liquidacao.getEmpenho().getCodigoCO().getCodigo());
        }
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada1(getUnidadeOrganizacional());
            case "94":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada4(getUnidadeOrganizacional(),
                    contaContabil.getSubSistema(),
                    liquidacao.getEmpenho().getContaDeDestinacao(),
                    liquidacao.getEmpenho().getExercicio());
            case "95":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada5(getUnidadeOrganizacional(),
                    liquidacao.getEmpenho().getContaDeDestinacao(),
                    liquidacao.getEmpenho().getExercicio());
            case "97":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada7(getUnidadeOrganizacional(),
                    acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                    liquidacao.getEmpenho().getContaDeDestinacao(),
                    liquidacao.getEmpenho().getContaDespesa(),
                    (liquidacao.getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                        liquidacao.getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                            liquidacao.getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                            liquidacao.getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0));
            case "98":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada8(getUnidadeOrganizacional(),
                    contaContabil.getSubSistema(),
                    0,
                    liquidacao.getEmpenho().getContaDeDestinacao());
            case "99":
                if (CategoriaOrcamentaria.RESTO.equals(categoriaOrcamentaria)) {
                    return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada9(getUnidadeOrganizacional(),
                        acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                        liquidacao.getEmpenho().getContaDeDestinacao(),
                        liquidacao.getEmpenho().getContaDespesa(),
                        (liquidacao.getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                            liquidacao.getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                                liquidacao.getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                                liquidacao.getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0),
                        liquidacao.getEmpenho().getEmpenho().getExercicio().getAno(),
                        liquidacao.getEmpenho().getExercicio(),
                        liquidacao.getEmpenho().getEmpenho().getExercicio());
                }
        }
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarDetalhadaSiconfiRecebido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarDetalhadaSiconfiConcedido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarSiconfi(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        AcaoPPA acaoPPA = liquidacao.getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA();
        if (liquidacao != null && liquidacao.getEmpenho() != null &&
            liquidacao.getEmpenho().getCodigoCO() != null && liquidacao.getEmpenho().getContaDeDestinacao() != null) {
            liquidacao.getEmpenho().getContaDeDestinacao().setCodigoCOEmenda(liquidacao.getEmpenho().getCodigoCO().getCodigo());
        }
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar1(getUnidadeOrganizacional());
            case "94":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar4(getUnidadeOrganizacional(),
                    contaContabil.getSubSistema(),
                    liquidacao.getEmpenho().getContaDeDestinacao());
            case "95":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar5(getUnidadeOrganizacional(),
                    liquidacao.getEmpenho().getContaDeDestinacao());
            case "97":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar7(getUnidadeOrganizacional(),
                    acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                    liquidacao.getEmpenho().getContaDeDestinacao(),
                    (liquidacao.getEmpenho().getContaDespesa().getCodigoSICONFI() != null ?
                        liquidacao.getEmpenho().getContaDespesa().getCodigoSICONFI() :
                        liquidacao.getEmpenho().getContaDespesa().getCodigo().replace(".", "")),
                    (liquidacao.getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                        liquidacao.getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                            liquidacao.getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                            liquidacao.getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0));
            case "98":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar8(getUnidadeOrganizacional(),
                    contaContabil.getSubSistema(),
                    0,
                    liquidacao.getEmpenho().getContaDeDestinacao());
            case "99":
                if (CategoriaOrcamentaria.RESTO.equals(categoriaOrcamentaria)) {
                    return UtilGeradorContaAuxiliar.gerarContaAuxiliar9(unidadeOrganizacional,
                        acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                        liquidacao.getEmpenho().getContaDeDestinacao(),
                        liquidacao.getEmpenho().getContaDespesa(),
                        (liquidacao.getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                            liquidacao.getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                                liquidacao.getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                                liquidacao.getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0),
                        liquidacao.getEmpenho().getEmpenho().getExercicio().getAno(),
                        liquidacao.getEmpenho().getExercicio());
                }
        }
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarSiconfiRecebido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarSiconfiConcedido(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        return null;
    }

    public void definirValorFinalPagto() {
        BigDecimal valorFinal = getValor();
        if (getRetencaoPgtos() != null && !getRetencaoPgtos().isEmpty()) {
            for (RetencaoPgto rp : getRetencaoPgtos()) {
                valorFinal = valorFinal.subtract(rp.getValor());
            }
        }
        setValorFinal(valorFinal);
        setSaldoFinal(valorFinal);
    }
}
