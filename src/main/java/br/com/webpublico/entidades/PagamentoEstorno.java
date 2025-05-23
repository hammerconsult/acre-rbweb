/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.contabil.execucao.DesdobramentoPagamentoEstorno;
import br.com.webpublico.entidadesauxiliares.manad.ManadRegistro;
import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.interfaces.EntidadeContabilComValor;
import br.com.webpublico.interfaces.IGeraContaAuxiliar;
import br.com.webpublico.interfaces.IManadRegistro;
import br.com.webpublico.negocios.manad.ManadContabilFacade;
import br.com.webpublico.util.*;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

/**
 * @author venon
 */
@Entity
@Audited
@Etiqueta("Estornos de Pagamentos")
@GrupoDiagrama(nome = "Contabil")
public class PagamentoEstorno extends SuperEntidade implements EntidadeContabilComValor, IManadRegistro, IGeraContaAuxiliar {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ErroReprocessamentoContabil
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número")
    private String numero;
    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data")
    @Obrigatorio
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataEstorno;
    @Etiqueta("Data Concilição")
    @Pesquisavel
    @Tabelavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @ErroReprocessamentoContabil
    private Date dataConciliacao;
    @ErroReprocessamentoContabil
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Pagamento")
    @ManyToOne
    private Pagamento pagamento;
    @Invisivel
    @ManyToOne
    private MovimentoDespesaORC movimentoDespesaORC;
    @Enumerated(EnumType.STRING)
    @ReprocessamentoContabil
    private CategoriaOrcamentaria categoriaOrcamentaria;
    @Etiqueta("Histórico Contábil")
    @ManyToOne
    private HistoricoContabil historicoContabil;
    @Obrigatorio
    @Etiqueta("Histórico")
    private String complementoHistorico;
    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    @Etiqueta("Unidade Organizacional")
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    @Etiqueta("Unidade Organizacional Administrativa")
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacionalAdm;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Evento Contábil")
    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    private EventoContabil eventoContabil;
    private String historicoRazao;
    private String historicoNota;
    //    @Obrigatorio
    @Etiqueta("Usuário do Sistema")
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    private UsuarioSistema usuarioSistema;
    @ManyToOne
    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    @Obrigatorio
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Pesquisavel
    @Monetario
    @Etiqueta("Valor (R$)")
    private BigDecimal valor;
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Pesquisavel
    @Monetario
    @Etiqueta("Valor Final (R$)")
    private BigDecimal valorFinal;
    private String uuid;
    @ManyToOne
    private Identificador identificador;


    @OneToMany(mappedBy = "pagamentoEstorno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DesdobramentoPagamentoEstorno> desdobramentos;

    public PagamentoEstorno() {
        this.valor = new BigDecimal(BigInteger.ZERO);
        this.valorFinal = new BigDecimal(BigInteger.ZERO);
        uuid = UUID.randomUUID().toString();
        this.desdobramentos = Lists.newArrayList();
    }

    public BigDecimal getValorFinal() {
        return valorFinal;
    }

    public void setValorFinal(BigDecimal valorFinal) {
        this.valorFinal = valorFinal;
    }

    public CategoriaOrcamentaria getCategoriaOrcamentaria() {
        return categoriaOrcamentaria;
    }

    public void setCategoriaOrcamentaria(CategoriaOrcamentaria categoriaOrcamentaria) {
        this.categoriaOrcamentaria = categoriaOrcamentaria;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataEstorno() {
        return dataEstorno;
    }

    public void setDataEstorno(Date dataEstorno) {
        this.dataEstorno = dataEstorno;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Pagamento getPagamento() {
        return pagamento;
    }

    public void setPagamento(Pagamento pagamento) {
        this.pagamento = pagamento;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public MovimentoDespesaORC getMovimentoDespesaORC() {
        return movimentoDespesaORC;
    }

    public void setMovimentoDespesaORC(MovimentoDespesaORC movimentoDespesaORC) {
        this.movimentoDespesaORC = movimentoDespesaORC;
    }

    public String getComplementoHistorico() {
        return complementoHistorico;
    }

    public void setComplementoHistorico(String complementoHistorico) {
        this.complementoHistorico = complementoHistorico;
    }

    public HistoricoContabil getHistoricoContabil() {
        return historicoContabil;
    }

    public void setHistoricoContabil(HistoricoContabil historicoContabil) {
        this.historicoContabil = historicoContabil;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public UnidadeOrganizacional getUnidadeOrganizacionalAdm() {
        return unidadeOrganizacionalAdm;
    }

    public void setUnidadeOrganizacionalAdm(UnidadeOrganizacional unidadeOrganizacionalAdm) {
        this.unidadeOrganizacionalAdm = unidadeOrganizacionalAdm;
    }

    public Date getDataConciliacao() {
        return dataConciliacao;
    }

    public void setDataConciliacao(Date dataConciliacao) {
        this.dataConciliacao = dataConciliacao;
    }

    public String getHistoricoRazao() {
        return historicoRazao;
    }

    public void setHistoricoRazao(String historicoRazao) {
        this.historicoRazao = historicoRazao;
    }

    public String getHistoricoNota() {
        return historicoNota;
    }

    public void setHistoricoNota(String historicoNota) {
        this.historicoNota = historicoNota;
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
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

    public List<DesdobramentoPagamentoEstorno> getDesdobramentos() {
        return desdobramentos;
    }

    public void setDesdobramentos(List<DesdobramentoPagamentoEstorno> desdobramentos) {
        this.desdobramentos = desdobramentos;
    }

    public void geraHistoricoNota() {
        historicoNota = " ";
        if (this.getPagamento().getLiquidacao() != null) {
            historicoNota += "Pagamento: " + this.getPagamento().getNumeroPagamento() + "/" + this.getPagamento().getExercicio().getAno() + UtilBeanContabil.SEPARADOR_HISTORICO;
            historicoNota += "Liquidação: " + this.getPagamento().getLiquidacao().getNumero() + "/" + this.getPagamento().getLiquidacao().getExercicio().getAno() + UtilBeanContabil.SEPARADOR_HISTORICO;
            historicoNota += this.getPagamento().getLiquidacao().getEmpenho().getNumeroExercicioParaHistorico() + UtilBeanContabil.SEPARADOR_HISTORICO;
            historicoNota += "Dotação: " + this.getPagamento().getLiquidacao().getEmpenho().getDespesaORC().getHistoricoPadrao();
            historicoNota += "Conta de Despesa: " + this.getPagamento().getLiquidacao().getEmpenho().getDespesaORC().getDescricaoContaDespesaPPA();
        }
        if (this.getPagamento().getSubConta() != null) {
            historicoNota += "Conta Financeira: " + this.getPagamento().getSubConta().getCodigo() + "/" + this.getPagamento().getSubConta().getDescricao() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getPagamento().getLiquidacao().getEmpenho().getFonteDespesaORC() != null) {
            historicoNota += "Fonte de Recursos: " + this.getPagamento().getLiquidacao().getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos().getCodigo() + "/" + this.getPagamento().getLiquidacao().getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos().getDescricao() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
    }

    public void gerarHistoricoRazao() {
        String historicoEvento = "";
        if (this.getEventoContabil() != null) {
            if (this.getEventoContabil().getClpHistoricoContabil() != null) {
                historicoEvento = this.getEventoContabil().getClpHistoricoContabil().toString();
            }
        }
        this.historicoRazao = historicoEvento + " " + this.historicoNota;
        this.historicoRazao = Util.cortarString(this.historicoNota, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public void gerarHistoricos() {
        geraHistoricoNota();
        gerarHistoricoRazao();
    }

    @Override
    public String toString() {
        return this.numero + " " + this.pagamento.getLiquidacao().getEmpenho();
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return numero + " - " + ((EntidadeContabil) pagamento).getReferenciaArquivoPrestacaoDeContas();
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
        HierarquiaOrganizacional unidade = ((ManadContabilFacade) facade).getHierarquiaDaUnidade(this.getUnidadeOrganizacional(), this.getDataEstorno());
        HierarquiaOrganizacional orgao = ((ManadContabilFacade) facade).getHierarquiaDaUnidade(unidade.getSuperior(), this.getDataEstorno());

        SubConta contaDebito = this.getPagamento().getSubConta();
        ContaCorrenteBancaria contaCredito = this.getPagamento().getContaPgto();
        String orgaoUnidade = orgao.getCodigoNo() + unidade.getCodigoNo();

        ManadUtil.criaLinha(1, ManadRegistro.ManadRegistroTipo.L150.name(), conteudo);
        ManadUtil.criaLinha(2, this.getPagamento().getLiquidacao().getEmpenho().getNumero(), conteudo);
        ManadUtil.criaLinha(3, this.getPagamento().getNumeroPagamento(), conteudo);
        ManadUtil.criaLinha(4, ManadUtil.getDataSemBarra(this.getDataEstorno()), conteudo);
        ManadUtil.criaLinha(5, ManadUtil.getValor(this.getValorFinal()), conteudo);
        ManadUtil.criaLinha(6, "C", conteudo);
        ManadUtil.criaLinha(7, this.getComplementoHistorico() == null ? " " : this.getComplementoHistorico(), conteudo);
        ManadUtil.criaLinha(8, contaDebito != null ? contaDebito.getContaBancariaEntidade().getNumeroContaComDigitoVerificador() : "", conteudo);
        ManadUtil.criaLinha(9, orgaoUnidade, conteudo);
        ManadUtil.criaLinha(10, contaCredito != null ? contaCredito.getNumeroContaComDigito() : "", conteudo);
        ManadUtil.criaLinha(11, orgaoUnidade, conteudo);
        ManadUtil.quebraLinha(conteudo);
    }

    @Override
    public String getComplementoHistoricoPrestacaoDeContas() {
        String retorno = toString();
        if (pagamento.getLiquidacao().getEmpenho().isEmpenhoRestoPagar()) {
            retorno = "Estorno de Pagamento de Restos a Pagar: nº " + numero + " - " + DataUtil.getDataFormatada(dataEstorno) + " - " +
                "Pagamento de Restos a Pagar: nº " + pagamento.getNumeroPagamento() + " - " + DataUtil.getDataFormatada(pagamento.getDataPagamento()) + " - " +
                "Liquidação de Restos a Pagar: nº ";
            if (pagamento.getLiquidacao().getLiquidacao() != null) {
                retorno += pagamento.getLiquidacao().getLiquidacao().getNumero() + " - " + DataUtil.getDataFormatada(pagamento.getLiquidacao().getLiquidacao().getDataLiquidacao()) + " - ";
            } else {
                retorno += pagamento.getLiquidacao().getNumero() + " - " + DataUtil.getDataFormatada(pagamento.getLiquidacao().getDataLiquidacao()) + " - ";
            }
            retorno += "Restos a Pagar: nº " + pagamento.getLiquidacao().getEmpenho().getNumero() + " - " + DataUtil.getDataFormatada(pagamento.getLiquidacao().getEmpenho().getDataEmpenho()) + " - " +
                "Empenho: nº " + pagamento.getLiquidacao().getEmpenho().getEmpenho().getNumero() + " - " + DataUtil.getDataFormatada(pagamento.getLiquidacao().getEmpenho().getEmpenho().getDataEmpenho()) + " - " +
                "Função " + pagamento.getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getAcaoPrincipal().getFuncao().getCodigo() + " - " + pagamento.getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getAcaoPrincipal().getFuncao().getDescricao() + " - " +
                "Subfunção " + pagamento.getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getAcaoPrincipal().getSubFuncao().getCodigo() + " - " + pagamento.getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA().getAcaoPrincipal().getSubFuncao().getDescricao() + " - " +
                "Conta de Despesa " + pagamento.getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getCodigo() + " - " + pagamento.getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getDescricao() + " - " +
                "Fonte de Recurso " + pagamento.getLiquidacao().getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao().getFonteDeRecursos().getCodigo() + " - " + pagamento.getLiquidacao().getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao().getFonteDeRecursos().getDescricao() + " - " +
                "Pessoa " + pagamento.getLiquidacao().getEmpenho().getFornecedor().getNome() + " - " +
                "Valor " + Util.formataValor(valor);
        }
        return retorno;
    }

    public BigDecimal getTotalDesdobramentos() {
        BigDecimal total = BigDecimal.ZERO;
        for (DesdobramentoPagamentoEstorno d : this.getDesdobramentos()) {
            total = total.add(d.getValor());
        }
        return total;
    }

    @Override
    public TreeMap getMapContaAuxiliarSistema(TipoContaAuxiliar tipoContaAuxiliar) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarDetalhadaSiconfi(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        AcaoPPA acaoPPA = pagamento.getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA();
        if (pagamento != null && pagamento.getLiquidacao() != null &&  pagamento.getLiquidacao().getEmpenho() != null &&
            pagamento.getLiquidacao().getEmpenho().getCodigoCO() != null && pagamento.getLiquidacao().getEmpenho().getContaDeDestinacao() != null) {
            pagamento.getLiquidacao().getEmpenho().getContaDeDestinacao().setCodigoCOEmenda(pagamento.getLiquidacao().getEmpenho().getCodigoCO().getCodigo());
        }
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada1(getUnidadeOrganizacional());
            case "94":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada4(getUnidadeOrganizacional(),
                    contaContabil.getSubSistema(),
                    pagamento.getLiquidacao().getEmpenho().getContaDeDestinacao(),
                    pagamento.getLiquidacao().getEmpenho().getExercicio());
            case "95":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada5(getUnidadeOrganizacional(),
                    pagamento.getLiquidacao().getEmpenho().getContaDeDestinacao(),
                    pagamento.getLiquidacao().getEmpenho().getExercicio());
            case "97":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada7(getUnidadeOrganizacional(),
                    acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                    pagamento.getLiquidacao().getEmpenho().getContaDeDestinacao(),
                    pagamento.getLiquidacao().getEmpenho().getContaDespesa(),
                    (pagamento.getLiquidacao().getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                        pagamento.getLiquidacao().getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                            pagamento.getLiquidacao().getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                            pagamento.getLiquidacao().getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0));
            case "98":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada8(getUnidadeOrganizacional(),
                    contaContabil.getSubSistema(),
                    0,
                    pagamento.getLiquidacao().getEmpenho().getContaDeDestinacao());
            case "99":
                if (CategoriaOrcamentaria.RESTO.equals(categoriaOrcamentaria)) {
                    return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada9(getUnidadeOrganizacional(),
                        acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                        pagamento.getLiquidacao().getEmpenho().getContaDeDestinacao(),
                        pagamento.getLiquidacao().getEmpenho().getContaDespesa(),
                        (pagamento.getLiquidacao().getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                            pagamento.getLiquidacao().getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                                pagamento.getLiquidacao().getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                                pagamento.getLiquidacao().getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0),
                        pagamento.getLiquidacao().getEmpenho().getEmpenho().getExercicio().getAno(),
                        pagamento.getLiquidacao().getEmpenho().getExercicio(),
                        pagamento.getLiquidacao().getEmpenho().getEmpenho().getExercicio());
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
        AcaoPPA acaoPPA = pagamento.getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA();
        if (pagamento != null && pagamento.getLiquidacao() != null &&  pagamento.getLiquidacao().getEmpenho() != null &&
            pagamento.getLiquidacao().getEmpenho().getCodigoCO() != null && pagamento.getLiquidacao().getEmpenho().getContaDeDestinacao() != null) {
            pagamento.getLiquidacao().getEmpenho().getContaDeDestinacao().setCodigoCOEmenda(pagamento.getLiquidacao().getEmpenho().getCodigoCO().getCodigo());
        }
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar1(getUnidadeOrganizacional());
            case "94":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar4(getUnidadeOrganizacional(),
                    contaContabil.getSubSistema(),
                    pagamento.getLiquidacao().getEmpenho().getContaDeDestinacao());
            case "95":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar5(getUnidadeOrganizacional(),
                    pagamento.getLiquidacao().getEmpenho().getContaDeDestinacao());
            case "97":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar7(getUnidadeOrganizacional(),
                    acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                    pagamento.getLiquidacao().getEmpenho().getContaDeDestinacao(),
                    (pagamento.getLiquidacao().getEmpenho().getContaDespesa().getCodigoSICONFI() != null ?
                        pagamento.getLiquidacao().getEmpenho().getContaDespesa().getCodigoSICONFI() :
                        pagamento.getLiquidacao().getEmpenho().getContaDespesa().getCodigo().replace(".", "")),
                    (pagamento.getLiquidacao().getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                        pagamento.getLiquidacao().getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                            pagamento.getLiquidacao().getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                            pagamento.getLiquidacao().getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0));
            case "98":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar8(getUnidadeOrganizacional(),
                    contaContabil.getSubSistema(),
                    0,
                    pagamento.getLiquidacao().getEmpenho().getContaDeDestinacao());
            case "99":
                if (CategoriaOrcamentaria.RESTO.equals(categoriaOrcamentaria)) {
                    return UtilGeradorContaAuxiliar.gerarContaAuxiliar9(unidadeOrganizacional,
                        acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                        pagamento.getLiquidacao().getEmpenho().getContaDeDestinacao(),
                        pagamento.getLiquidacao().getEmpenho().getContaDespesa(),
                        (pagamento.getLiquidacao().getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                            pagamento.getLiquidacao().getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                                pagamento.getLiquidacao().getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                                pagamento.getLiquidacao().getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0),
                        pagamento.getLiquidacao().getEmpenho().getEmpenho().getExercicio().getAno(),
                        pagamento.getLiquidacao().getEmpenho().getExercicio());
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
}
