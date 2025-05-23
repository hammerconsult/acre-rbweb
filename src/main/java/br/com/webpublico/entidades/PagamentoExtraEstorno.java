/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

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
import java.util.*;

/**
 * @author venon
 */
@Entity
@GrupoDiagrama(nome = "Contabil")
@Audited
@Etiqueta("Estorno da Despesa Extraorçamentária")
public class PagamentoExtraEstorno extends SuperEntidade implements EntidadeContabil, IGeraContaAuxiliar {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número")
    private String numero;
    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    @Tabelavel
    @Etiqueta("Data")
    @Pesquisavel
    @Obrigatorio
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataEstorno;
    @Etiqueta("Data Conciliação")
    @Pesquisavel
    @Tabelavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @ErroReprocessamentoContabil
    private Date dataConciliacao;
    @Obrigatorio
    @Etiqueta("Histórico")
    private String historico;
    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    @Etiqueta("Unidade Organizacional")
    @Tabelavel
    @Obrigatorio
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    @ErroReprocessamentoContabil
    @Etiqueta("Despesa Extraorçamentária")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @ManyToOne
    private PagamentoExtra pagamentoExtra;
    @Etiqueta("Valor (R$)")
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Monetario
    @Obrigatorio
    @Positivo(permiteZero = false)
    private BigDecimal valor;
    @Etiqueta("Usuário do Sistema")
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "pagamentoExtraEstorno", orphanRemoval = true)
    private List<PagamentoEstornoRecExtra> pagamentoEstornoRecExtras;
    @ReprocessamentoContabil
    @ErroReprocessamentoContabil
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Evento Contábil")
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

    public PagamentoExtraEstorno() {
        zerarValor();
        pagamentoEstornoRecExtras = new ArrayList<PagamentoEstornoRecExtra>();
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

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
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

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public PagamentoExtra getPagamentoExtra() {
        return pagamentoExtra;
    }

    public void setPagamentoExtra(PagamentoExtra pagamentoExtra) {
        this.pagamentoExtra = pagamentoExtra;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
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

    public List<PagamentoEstornoRecExtra> getPagamentoEstornoRecExtras() {
        return pagamentoEstornoRecExtras;
    }

    public void setPagamentoEstornoRecExtras(List<PagamentoEstornoRecExtra> pagamentoEstornoRecExtras) {
        this.pagamentoEstornoRecExtras = pagamentoEstornoRecExtras;
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
        if (this.getPagamentoExtra() != null) {
            historicoNota += "Pagamento Extra: " + this.getPagamentoExtra().getNumero() + "/" + this.getPagamentoExtra().getExercicio().getAno() + UtilBeanContabil.SEPARADOR_HISTORICO;
        }
        for (PagamentoEstornoRecExtra pagamentoExtraEstorno : pagamentoEstornoRecExtras) {
            if (pagamentoExtraEstorno.getReceitaExtra().getNumero() != null) {
                historicoNota += "Receita Extra-Orçamentária: " + pagamentoExtraEstorno.getReceitaExtra().getNumero() + "/" + pagamentoExtraEstorno.getReceitaExtra().getExercicio().getAno() + UtilBeanContabil.SEPARADOR_HISTORICO;
            }
            if (pagamentoExtraEstorno.getReceitaExtra().getRetencaoPgto() != null) {
                if (pagamentoExtraEstorno.getReceitaExtra().getRetencaoPgto().getPagamento() != null) {
                    Pagamento pagamento = pagamentoExtraEstorno.getReceitaExtra().getRetencaoPgto().getPagamento();
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

            if (pagamentoExtraEstorno.getReceitaExtra().getSubConta() != null) {
                historicoNota += "Conta Financeira: " + pagamentoExtraEstorno.getReceitaExtra().getSubConta() + UtilBeanContabil.SEPARADOR_HISTORICO;
            }
            if (pagamentoExtraEstorno.getReceitaExtra().getFonteDeRecursos() != null) {
                historicoNota += "Fonte de Recursos: " + pagamentoExtraEstorno.getReceitaExtra().getFonteDeRecursos().toString().trim() + UtilBeanContabil.SEPARADOR_HISTORICO;
            }
            if (pagamentoExtraEstorno.getReceitaExtra().getContaExtraorcamentaria() != null) {
                historicoNota += "Conta Extraorçamentária: " + pagamentoExtraEstorno.getReceitaExtra().getContaExtraorcamentaria().toString().trim() + UtilBeanContabil.SEPARADOR_HISTORICO;
            }
            if (pagamentoExtraEstorno.getReceitaExtra().getPessoa() != null) {
                historicoNota += "Pessoa: " + pagamentoExtraEstorno.getReceitaExtra().getPessoa().getNomeCpfCnpj() + UtilBeanContabil.SEPARADOR_HISTORICO;
            }
            if (pagamentoExtraEstorno.getReceitaExtra().getClasseCredor() != null) {
                historicoNota += "Classe: " + pagamentoExtraEstorno.getReceitaExtra().getClasseCredor().toString() + UtilBeanContabil.SEPARADOR_HISTORICO;
            }
        }

        historicoNota = historicoNota + historico;
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
        return "Número: " + numero;
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return numero + "/" + exercicio.getAno();
    }

    public void zerarValor() {
        valor = new BigDecimal(BigInteger.ZERO);
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
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada1(getUnidadeOrganizacional());
            case "92":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada2(getUnidadeOrganizacional(),
                    contaContabil.getSubSistema());
            case "94":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada4(getUnidadeOrganizacional(),
                    contaContabil.getSubSistema(),
                    pagamentoExtra.getContaDeDestinacao(),
                    getPagamentoExtra().getExercicio());
            case "95":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliarDetalhada5(getUnidadeOrganizacional(),
                    pagamentoExtra.getContaDeDestinacao(),
                    getPagamentoExtra().getExercicio());
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
        switch (tipoContaAuxiliar.getCodigo()) {
            case "91":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar1(getUnidadeOrganizacional());
            case "92":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar2(getUnidadeOrganizacional(),
                    contaContabil.getSubSistema());
            case "94":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar4(getUnidadeOrganizacional(),
                    contaContabil.getSubSistema(),
                    pagamentoExtra.getContaDeDestinacao());
            case "95":
                return UtilGeradorContaAuxiliar.gerarContaAuxiliar5(getUnidadeOrganizacional(),
                    pagamentoExtra.getContaDeDestinacao());
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
