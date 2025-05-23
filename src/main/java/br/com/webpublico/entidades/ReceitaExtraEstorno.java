/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.enums.StatusPagamento;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.interfaces.IGeraContaAuxiliar;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilBeanContabil;
import br.com.webpublico.util.UtilGeradorContaAuxiliar;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.TreeMap;
import java.util.UUID;

/**
 * @author venon
 */
@Entity

@Audited
@Etiqueta("Estorno de Receita Extraorçamentária")
@GrupoDiagrama(nome = "Contabil")
public class ReceitaExtraEstorno extends SuperEntidade implements EntidadeContabil, IGeraContaAuxiliar {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Número")
    @Tabelavel
    @Pesquisavel
    private String numero;
    @Etiqueta("Data")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @ReprocessamentoContabil
    private Date dataEstorno;
    @Tabelavel
    @Etiqueta("Data da Concilição")
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataConciliacao;
    @Obrigatorio
    @Etiqueta("Histórico")
    private String complementoHistorico;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Receita Extraorçamentária")
    @Tabelavel
    @Pesquisavel
    private ReceitaExtra receitaExtra;
    @Etiqueta("Usuário do Sistema")
    @ManyToOne
    @Pesquisavel
    private UsuarioSistema usuarioSistema;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Unidade Organizacional")
    @ReprocessamentoContabil
    private UnidadeOrganizacional unidadeOrganizacional;
    @Obrigatorio
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Monetario
    @Etiqueta("Valor(R$)")
    @Pesquisavel
    private BigDecimal valor;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Etiqueta("Situação")
    private StatusPagamento statusPagamento;
    @ManyToOne
    @ReprocessamentoContabil
    @Obrigatorio
    @Etiqueta(value = "Evento Contábil")
    private EventoContabil eventoContabil;
    @ManyToOne
    private Exercicio exercicio;
    @Etiqueta("Unidade Organizacional Administrativa")
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacionalAdm;
    private String historicoNota;
    private String historicoRazao;
    private String uuid;
    @ManyToOne
    private Identificador identificador;

    public ReceitaExtraEstorno() {
        valor = new BigDecimal(BigInteger.ZERO);
        dataEstorno = new Date();
        uuid = UUID.randomUUID().toString();
    }

    public ReceitaExtraEstorno(String numero, BigDecimal valor, Date dataEstorno, String complementoHistorico, ReceitaExtra receitaExtra, UsuarioSistema usuarioSistema, UnidadeOrganizacional unidadeOrganizacional) {
        this.numero = numero;
        this.valor = valor;
        this.dataEstorno = dataEstorno;
        this.complementoHistorico = complementoHistorico;
        this.receitaExtra = receitaExtra;
        this.usuarioSistema = usuarioSistema;
        this.unidadeOrganizacional = unidadeOrganizacional;
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

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
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

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public ReceitaExtra getReceitaExtra() {
        return receitaExtra;
    }

    public void setReceitaExtra(ReceitaExtra receitaExtra) {
        this.receitaExtra = receitaExtra;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public StatusPagamento getStatusPagamento() {
        return statusPagamento;
    }

    public void setStatusPagamento(StatusPagamento statusPagamento) {
        this.statusPagamento = statusPagamento;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
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

    public void gerarHistoricoNota() {
        historicoNota = " ";
        if (this.getNumero() != null) {
            historicoNota += "N: " + this.getNumero() + "/" + this.getExercicio().getAno() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getReceitaExtra().getNumero() != null) {
            historicoNota += "Receita Extraorçamentária: " + this.getReceitaExtra().getNumero() + "/" + this.getReceitaExtra().getExercicio().getAno() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getReceitaExtra().getRetencaoPgto() != null) {
            if (this.getReceitaExtra().getRetencaoPgto().getPagamento() != null) {
                Pagamento pagamento = this.getReceitaExtra().getRetencaoPgto().getPagamento();
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
        if (this.getReceitaExtra().getSubConta() != null) {
            historicoNota += "Conta Financeira: " + this.getReceitaExtra().getSubConta() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getReceitaExtra().getFonteDeRecursos() != null) {
            historicoNota += "Fonte de Recursos: " + this.getReceitaExtra().getFonteDeRecursos().toString().trim() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getReceitaExtra().getContaExtraorcamentaria() != null) {
            historicoNota += "Conta Extraorçamentária: " + this.getReceitaExtra().getContaExtraorcamentaria().toString().trim() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getReceitaExtra().getPessoa() != null) {
            historicoNota += "Pessoa: " + this.getReceitaExtra().getPessoa().getNomeCpfCnpj() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        if (this.getReceitaExtra().getClasseCredor() != null) {
            historicoNota += "Classe: " + this.getReceitaExtra().getClasseCredor().toString() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        historicoNota = historicoNota + complementoHistorico;
        this.historicoNota = Util.cortarString(this.historicoNota, UtilBeanContabil.QUANTIDADE_CARACTERES_HISTORICO);
    }

    public void gerarHistoricoRazao() {
        String historicoEvento = "";
        if (this.getEventoContabil() != null && this.getEventoContabil().getClpHistoricoContabil() != null) {
            historicoEvento = this.getEventoContabil().getClpHistoricoContabil().toString();
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
        return "Número: " + numero;
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        try {
            return numero + "/" + exercicio.getAno() + " - " + receitaExtra.getRetencaoPgto().getPagamento().getNumeroPagamento();
        } catch (Exception e) {
            return numero + "/" + exercicio.getAno();
        }

    }

    @Override
    public String getComplementoHistoricoPrestacaoDeContas() {
        return toString();
    }

    @Override
    public TreeMap getMapContaAuxiliarSistema(TipoContaAuxiliar tipoContaAuxiliar) {
        return null;
    }

    @Override
    public TreeMap getMapContaAuxiliarDetalhadaSiconfi(TipoContaAuxiliar tipoContaAuxiliar, ContaContabil contaContabil) {
        AcaoPPA acaoPPA = null;
        if (receitaExtra.getRetencaoPgto() != null) {
            acaoPPA = getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA();
        }
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada1(getUnidadeOrganizacional());
            case "94":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada4(getUnidadeOrganizacional(),
                    contaContabil.getSubSistema(),
                    receitaExtra.getContaDeDestinacao(),
                    receitaExtra.getExercicio());
            case "95":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada5(getUnidadeOrganizacional(),
                    receitaExtra.getContaDeDestinacao(),
                    receitaExtra.getExercicio());
            case "97":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada7(getUnidadeOrganizacional(),
                    acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                    receitaExtra.getContaDeDestinacao(),
                    getEmpenho().getContaDespesa(),
                    (getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                        getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                            getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                            getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0));
            case "99":
                if (CategoriaOrcamentaria.RESTO.equals(receitaExtra.getRetencaoPgto().getPagamento().getCategoriaOrcamentaria())) {
                    return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada9(getUnidadeOrganizacional(),
                        acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                        receitaExtra.getContaDeDestinacao(),
                        getEmpenho().getContaDespesa(),
                        (getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                            getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                                getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                                getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0),
                        getEmpenho().getEmpenho().getExercicio().getAno(),
                        getEmpenho().getExercicio(),
                        getEmpenho().getEmpenho().getExercicio());
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
        AcaoPPA acaoPPA = null;
        if (receitaExtra.getRetencaoPgto() != null) {
            acaoPPA = getEmpenho().getDespesaORC().getProvisaoPPADespesa().getSubAcaoPPA().getAcaoPPA();
        }
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar1(getUnidadeOrganizacional());
            case "94":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar4(getUnidadeOrganizacional(),
                    contaContabil.getSubSistema(),
                    receitaExtra.getContaDeDestinacao());
            case "95":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar5(getUnidadeOrganizacional(),
                    receitaExtra.getContaDeDestinacao());
            case "97":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar7(getUnidadeOrganizacional(),
                    acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                    receitaExtra.getContaDeDestinacao(),
                    (getEmpenho().getContaDespesa().getCodigoSICONFI() != null ?
                        getEmpenho().getContaDespesa().getCodigoSICONFI() :
                        getEmpenho().getContaDespesa().getCodigo().replace(".", "")),
                    (getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                        getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                            getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                            getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0));
            case "99":
                if (CategoriaOrcamentaria.RESTO.equals(receitaExtra.getRetencaoPgto().getPagamento().getCategoriaOrcamentaria())) {
                    return UtilGeradorContaAuxiliar.gerarContaAuxiliar9(unidadeOrganizacional,
                        acaoPPA.getFuncao().getCodigo() + acaoPPA.getSubFuncao().getCodigo(),
                        receitaExtra.getContaDeDestinacao(),
                        getEmpenho().getContaDespesa(),
                        (getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("4") ? 2 :
                            getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("1") ||
                                getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("2") ||
                                getEmpenho().getCodigoExtensaoFonteRecursoAsString().startsWith("3") ? 1 : 0),
                        getEmpenho().getEmpenho().getExercicio().getAno(),
                        getEmpenho().getExercicio());
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

    private Empenho getEmpenho() {
        return receitaExtra.getRetencaoPgto().getPagamento().getLiquidacao().getEmpenho();
    }
}
