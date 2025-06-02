/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.contabil.SuperEntidadeContabilGerarContaAuxiliar;
import br.com.webpublico.entidadesauxiliares.contabil.GeradorContaAuxiliarDTO;
import br.com.webpublico.enums.StatusPagamento;
import br.com.webpublico.enums.TipoDocPagto;
import br.com.webpublico.enums.TipoOperacaoPagto;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilBeanContabil;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author venon
 */
@Entity

@GrupoDiagrama(nome = "Contabil")
@Audited
@Etiqueta("Despesa Extraorçamentária")
public class PagamentoExtra extends SuperEntidadeContabilGerarContaAuxiliar implements Serializable, EntidadeContabil {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Etiqueta("Número")
    @Pesquisavel
    @ErroReprocessamentoContabil
    private String numero;
    @Obrigatorio
    @Etiqueta("Data")
    @Temporal(javax.persistence.TemporalType.DATE)
    @Pesquisavel
    @Tabelavel
    @ErroReprocessamentoContabil
    private Date previstoPara;
    @Etiqueta("Data de Pagamento")
    @Temporal(javax.persistence.TemporalType.DATE)
    @Pesquisavel
    @Tabelavel
    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    private Date dataPagto;
    @Etiqueta("Data de Conciliação")
    @Temporal(javax.persistence.TemporalType.DATE)
    @Pesquisavel
    @Tabelavel
    private Date dataConciliacao;
    @Etiqueta("Unidade Organizacional")
    @Obrigatorio
    @ManyToOne
    @Pesquisavel
    @ErroReprocessamentoContabil
    @ReprocessamentoContabil
    private UnidadeOrganizacional unidadeOrganizacional;
    @Obrigatorio
    @Etiqueta("Conta Financeira")
    @Tabelavel
    @ManyToOne
    @ErroReprocessamentoContabil
    private SubConta subConta;
    @ManyToOne
    @ErroReprocessamentoContabil
    private FonteDeRecursos fonteDeRecursos;
    @Etiqueta("Conta de Destinação de Recurso")
    @Tabelavel
    @Obrigatorio
    @ManyToOne
    private ContaDeDestinacao contaDeDestinacao;
    @Etiqueta("Conta Extraorçamentária")
    @Obrigatorio
    @Tabelavel
    @ManyToOne
    @ErroReprocessamentoContabil
    private Conta contaExtraorcamentaria;
    @ManyToOne
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Pessoa ")
    @ErroReprocessamentoContabil
    private Pessoa fornecedor;
    @ManyToOne
    @Obrigatorio
    @Tabelavel
    @ErroReprocessamentoContabil
    @Etiqueta("CLasse")
    private ClasseCredor classeCredor;
    @Obrigatorio
    @Etiqueta("Histórico")
    @ErroReprocessamentoContabil
    private String complementoHistorico;
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Obrigatorio
    @Pesquisavel
    @Monetario
    @ErroReprocessamentoContabil
    @Etiqueta("Valor (R$)")
    private BigDecimal valor;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pagamentoExtra", orphanRemoval = true)
    private List<PagamentoReceitaExtra> pagamentoReceitaExtras;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pagamentoExtra", orphanRemoval = true)
    private List<GuiaPagamentoExtra> guiaPagamentoExtras;
    @Tabelavel
    @Pesquisavel
    @Monetario
    @ErroReprocessamentoContabil
    @Etiqueta("Saldo (R$)")
    private BigDecimal saldo;
    @Etiqueta("Usuário do Sistema")
    @ManyToOne
    @Pesquisavel
    private UsuarioSistema usuarioSistema;
    @Invisivel
    @Etiqueta("Estornos do Pagamento Extraorçamentário")
    @OneToMany(mappedBy = "pagamentoExtra")
    private List<PagamentoExtraEstorno> pagamentoExtraEstornos;
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Situação")
    private StatusPagamento status;
    @ManyToOne
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Evento Contábil")
    @ErroReprocessamentoContabil
    @ReprocessamentoContabil
    private EventoContabil eventoContabil;
    @ManyToOne
    @ErroReprocessamentoContabil
    private Exercicio exercicio;
    @Etiqueta("Unidade Organizacional Administrativa")
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacionalAdm;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Tipo de Operação")
    private TipoOperacaoPagto tipoOperacaoPagto;
    @Etiqueta("Número do Documento")
    private String numeroPagamento;
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Tipo de Documento")
    private TipoDocPagto tipoDocumento;
    @Pesquisavel
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Finalidade do Pagamento")
    private FinalidadePagamento finalidadePagamento;
    @Etiqueta("Conta Bancária Pessoa")
    @Pesquisavel
    @ManyToOne
    private ContaCorrenteBancaria contaCorrenteBancaria;
    private String historicoNota;
    private String historicoRazao;
    @Etiqueta("Número RE")
    private String numeroRE;
    private Long idObn600;
    private Long idObn350;
    private String uuid;
    @ManyToOne
    private Identificador identificador;
    @Version
    private Long versao;
    private String identificadorDeposito;

    public PagamentoExtra() {
        valor = new BigDecimal(BigInteger.ZERO);
        saldo = new BigDecimal(BigInteger.ZERO);
        pagamentoExtraEstornos = new ArrayList<>();
        pagamentoReceitaExtras = new ArrayList<>();
        guiaPagamentoExtras = new ArrayList<>();
        uuid = UUID.randomUUID().toString();
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public String getComplementoHistorico() {
        return complementoHistorico;
    }

    public void setComplementoHistorico(String complementoHistorico) {
        this.complementoHistorico = complementoHistorico;
    }

    public Date getDataPagto() {
        return dataPagto;
    }

    public void setDataPagto(Date dataPagto) {
        this.dataPagto = dataPagto;
    }

    public Date getDataConciliacao() {
        return dataConciliacao;
    }

    public void setDataConciliacao(Date dataConciliacao) {
        this.dataConciliacao = dataConciliacao;
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

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Conta getContaExtraorcamentaria() {
        return contaExtraorcamentaria;
    }

    public void setContaExtraorcamentaria(Conta contaExtraorcamentaria) {
        this.contaExtraorcamentaria = contaExtraorcamentaria;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public Date getPrevistoPara() {
        return previstoPara;
    }

    public void setPrevistoPara(Date previstoPara) {
        this.previstoPara = previstoPara;
    }

    public List<PagamentoExtraEstorno> getPagamentoExtraEstornos() {
        return pagamentoExtraEstornos;
    }

    public void setPagamentoExtraEstornos(List<PagamentoExtraEstorno> pagamentoExtraEstornos) {
        this.pagamentoExtraEstornos = pagamentoExtraEstornos;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public SubConta getSubConta() {
        return subConta;
    }

    public void setSubConta(SubConta subConta) {
        this.subConta = subConta;
    }

    public StatusPagamento getStatus() {
        return status;
    }

    public void setStatus(StatusPagamento status) {
        this.status = status;
    }

    public List<PagamentoReceitaExtra> getPagamentoReceitaExtras() {
        return pagamentoReceitaExtras;
    }

    public void setPagamentoReceitaExtras(List<PagamentoReceitaExtra> pagamentoReceitaExtras) {
        this.pagamentoReceitaExtras = pagamentoReceitaExtras;
    }

    public ClasseCredor getClasseCredor() {
        return classeCredor;
    }

    public void setClasseCredor(ClasseCredor classeCredor) {
        this.classeCredor = classeCredor;
    }

    public UnidadeOrganizacional getUnidadeOrganizacionalAdm() {
        return unidadeOrganizacionalAdm;
    }

    public void setUnidadeOrganizacionalAdm(UnidadeOrganizacional unidadeOrganizacionalAdm) {
        this.unidadeOrganizacionalAdm = unidadeOrganizacionalAdm;
    }

    public TipoOperacaoPagto getTipoOperacaoPagto() {
        return tipoOperacaoPagto;
    }

    public void setTipoOperacaoPagto(TipoOperacaoPagto tipoOperacaoPagto) {
        this.tipoOperacaoPagto = tipoOperacaoPagto;
    }

    public String getNumeroPagamento() {
        return numeroPagamento;
    }

    public void setNumeroPagamento(String numeroPagamento) {
        this.numeroPagamento = numeroPagamento;
    }

    public TipoDocPagto getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocPagto tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public FinalidadePagamento getFinalidadePagamento() {
        return finalidadePagamento;
    }

    public void setFinalidadePagamento(FinalidadePagamento finalidadePagamento) {
        this.finalidadePagamento = finalidadePagamento;
    }

    public ContaCorrenteBancaria getContaCorrenteBancaria() {
        return contaCorrenteBancaria;
    }

    public void setContaCorrenteBancaria(ContaCorrenteBancaria contaCorrenteBancaria) {
        this.contaCorrenteBancaria = contaCorrenteBancaria;
    }

    public List<GuiaPagamentoExtra> getGuiaPagamentoExtras() {
        return guiaPagamentoExtras;
    }

    public void setGuiaPagamentoExtras(List<GuiaPagamentoExtra> guiaPagamentoExtras) {
        this.guiaPagamentoExtras = guiaPagamentoExtras;
    }

    public BigDecimal getValorTotalGuiasPagamento() {
        BigDecimal valor = BigDecimal.ZERO;
        for (GuiaPagamentoExtra guiaPag : getGuiaPagamentoExtras()) {
            valor = valor.add(guiaPag.getValorTotalPorGuia());
        }
        return valor;
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

    public Identificador getIdentificador() {
        return identificador;
    }

    public void setIdentificador(Identificador identificador) {
        this.identificador = identificador;
    }

    public Long getVersao() {
        return versao;
    }

    public void setVersao(Long versao) {
        this.versao = versao;
    }

    public String getIdentificadorDeposito() {
        return identificadorDeposito;
    }

    public void setIdentificadorDeposito(String identificadorDeposito) {
        this.identificadorDeposito = identificadorDeposito;
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public void gerarHistoricoNota() {
        historicoNota = " ";
        if (this.getNumero() != null) {
            historicoNota += "N: " + this.getNumero() + "/" + this.getExercicio().getAno() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        for (PagamentoReceitaExtra pagamentoReceitaExtra : pagamentoReceitaExtras) {
            if (pagamentoReceitaExtra.getReceitaExtra().getNumero() != null) {
                historicoNota += "Receita Extraorçamentária: " + pagamentoReceitaExtra.getReceitaExtra().getNumero() + "/" + pagamentoReceitaExtra.getReceitaExtra().getExercicio().getAno() + UtilBeanContabil.SEPARADOR_HISTORICO;
            }
            if (pagamentoReceitaExtra.getReceitaExtra().getRetencaoPgto() != null) {
                if (pagamentoReceitaExtra.getReceitaExtra().getRetencaoPgto().getPagamento() != null) {
                    Pagamento pagamento = pagamentoReceitaExtra.getReceitaExtra().getRetencaoPgto().getPagamento();
                    historicoNota += " Pagamento: " + pagamento.getNumeroPagamento() + "/" + pagamento.getExercicio().getAno() + UtilBeanContabil.SEPARADOR_HISTORICO;
                    historicoNota += " Liquidação: " + pagamento.getLiquidacao().getNumero() + "/" + pagamento.getLiquidacao().getExercicio().getAno() + UtilBeanContabil.SEPARADOR_HISTORICO;
                    historicoNota += pagamento.getLiquidacao().getEmpenho().getNumeroExercicioParaHistorico() + UtilBeanContabil.SEPARADOR_HISTORICO;
                    historicoNota += " Dotação: " + pagamento.getLiquidacao().getEmpenho().getDespesaORC().getHistoricoPadrao() + UtilBeanContabil.SEPARADOR_HISTORICO;
                    historicoNota += " Conta de Despesa: " + pagamento.getLiquidacao().getEmpenho().getDespesaORC().getDescricaoContaDespesaPPA() + UtilBeanContabil.SEPARADOR_HISTORICO;
                    if (pagamento.getLiquidacao().getDesdobramentos() != null) {
                        for (Desdobramento desdobramento : pagamento.getLiquidacao().getDesdobramentos()) {
                            historicoNota += " Detalhamento: " + desdobramento.getConta().toString() + UtilBeanContabil.SEPARADOR_HISTORICO;
                        }
                    }
                }
            }

            if (pagamentoReceitaExtra.getReceitaExtra().getSubConta() != null) {
                historicoNota += "Conta Financeira: " + pagamentoReceitaExtra.getReceitaExtra().getSubConta() + UtilBeanContabil.SEPARADOR_HISTORICO;
            }
            if (pagamentoReceitaExtra.getReceitaExtra().getContaDeDestinacao() != null) {
                historicoNota += "Conta de Destinação de Recurso: " + pagamentoReceitaExtra.getReceitaExtra().getContaDeDestinacao().toString().trim() + UtilBeanContabil.SEPARADOR_HISTORICO;
            }
            if (pagamentoReceitaExtra.getReceitaExtra().getContaExtraorcamentaria() != null) {
                historicoNota += "Conta Extraorçamentária: " + pagamentoReceitaExtra.getReceitaExtra().getContaExtraorcamentaria().toString().trim() + UtilBeanContabil.SEPARADOR_HISTORICO;
            }
            if (pagamentoReceitaExtra.getReceitaExtra().getPessoa() != null) {
                historicoNota += "Pessoa: " + pagamentoReceitaExtra.getReceitaExtra().getPessoa().getNomeCpfCnpj() + UtilBeanContabil.SEPARADOR_HISTORICO;
            }
            if (pagamentoReceitaExtra.getReceitaExtra().getClasseCredor() != null) {
                historicoNota += "Classe: " + pagamentoReceitaExtra.getReceitaExtra().getClasseCredor().toString() + UtilBeanContabil.SEPARADOR_HISTORICO;
            }
        }

        historicoNota = historicoNota + complementoHistorico;
        this.historicoNota = Util.cortarString(this.historicoNota, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public void gerarHistoricoRazao() {
        String historicoEvento = "";
        if (this.getEventoContabil() != null) {
            if (this.getEventoContabil().getClpHistoricoContabil() != null) {
                historicoEvento = this.getEventoContabil().getClpHistoricoContabil().toString();
            }
        }
        this.historicoRazao = historicoEvento + this.historicoNota;
        this.historicoRazao = Util.cortarString(this.historicoRazao, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public void gerarHistoricos() {
        gerarHistoricoNota();
        gerarHistoricoRazao();
    }


    @Override
    public String toString() {
        return "Nº " + numero + " - " + fornecedor.getNome() + " - " + new SimpleDateFormat("dd/MM/yyyy").format(previstoPara) + " - " + Util.formataValor(valor);
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return numero + "/" + exercicio.getAno();
    }

    public String getHistoricoArquivoOBN600() {
        String historico;
        if (tipoDocumento != null && tipoDocumento.isDepju()) {
            historico = this.identificadorDeposito.trim();
        } else {
            historico = this.complementoHistorico.trim();
        }
        historico = historico.replace("\n", "");
        historico = historico.replace("\r", "");
        historico = historico.replace("\t", "");
        return historico;
    }

    public Boolean isDespesaExtraComGuia() {
        return (TipoDocPagto.FATURA.equals(this.tipoDocumento)
            || TipoDocPagto.CONVENIO.equals(this.tipoDocumento)
            || TipoDocPagto.GPS.equals(this.tipoDocumento)
            || TipoDocPagto.DARF.equals(this.tipoDocumento)
            || TipoDocPagto.DARF_SIMPLES.equals(this.tipoDocumento));
    }

    public Boolean isDespesaExtraComOperacaoDeGuia() {
        if (TipoOperacaoPagto.PAGAMENTO_DE_GUIA_COM_CODIGO_DE_BARRA.equals(this.tipoOperacaoPagto)
            || TipoOperacaoPagto.PAGAMENTO_DE_GUIA_COM_CODIGO_DE_BARRA_NAO_CONTA_UNICA.equals(this.tipoOperacaoPagto)
            || TipoOperacaoPagto.PAGAMENTO_DE_GUIA_SEM_CODIGO_DE_BARRA.equals(this.tipoOperacaoPagto)
            || TipoOperacaoPagto.PAGAMENTO_DE_GUIA_SEM_CODIGO_DE_BARRA_NAO_CONTA_UNICA.equals(this.tipoOperacaoPagto)) {
            return true;
        }
        return false;
    }

    public boolean isDespesaExtraDeferida() {
        return this.status != null && StatusPagamento.DEFERIDO.equals(this.status);
    }

    public boolean isDespesaExtraPaga() {
        return this.status != null && StatusPagamento.PAGO.equals(this.status);
    }

    public boolean isDespesaExtraAberta() {
        return this.status != null && StatusPagamento.ABERTO.equals(this.status);
    }

    public boolean isDespesaExtraEfetuada() {
        return this.status != null && StatusPagamento.EFETUADO.equals(this.status);
    }

    public boolean isDespesaExtraIndeferida() {
        return this.status != null && StatusPagamento.INDEFERIDO.equals(this.status);
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
    public String getComplementoHistoricoPrestacaoDeContas() {
        return toString();
    }

    @Override
    public GeradorContaAuxiliarDTO gerarContaAuxiliarDTO(ParametroEvento.ComplementoId complementoId) {
        return new GeradorContaAuxiliarDTO(getUnidadeOrganizacional(), getContaDeDestinacao(), getExercicio());
    }
}
