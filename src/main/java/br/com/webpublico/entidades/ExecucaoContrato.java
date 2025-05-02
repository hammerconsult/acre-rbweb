/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.entidades;

import br.com.webpublico.enums.FormaEntregaExecucao;
import br.com.webpublico.enums.OperacaoSaldoItemContrato;
import br.com.webpublico.enums.OrigemSaldoItemContrato;
import br.com.webpublico.enums.TipoObjetoCompra;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.hibernate.envers.Audited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author Felipe Marinzeck
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Contratos")
@Etiqueta("Execução de Contrato")
public class ExecucaoContrato extends SuperEntidade implements PossuidorArquivo, Comparable<ExecucaoContrato> {

    private static final Logger logger = LoggerFactory.getLogger(ExecucaoContrato.class);
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(TemporalType.DATE)
    @Pesquisavel
    @Etiqueta("Data de Lançamento")
    private Date dataLancamento;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número")
    private Integer numero;

    @ManyToOne
    @Tabelavel
    @Etiqueta("Contrato")
    private Contrato contrato;

    @Monetario
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Pesquisavel
    @Etiqueta("Valor")
    private BigDecimal valor;

    @Deprecated
    @Invisivel
    @OneToMany(mappedBy = "execucaoContrato", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta("Nota Fiscais da Execução")
    private List<NotaFiscalExecucaoContrato> notasFiscais;

    @Invisivel
    @OneToMany(mappedBy = "execucaoContrato", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta("Itens da Execução")
    private List<ExecucaoContratoItem> itens;

    @Invisivel
    @OneToMany(mappedBy = "execucaoContrato", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta("Reservas")
    private List<ExecucaoContratoTipo> reservas;

    @Deprecated
    @Invisivel
    @OneToMany(mappedBy = "execucaoContrato", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta("Obras")
    private List<ObraExecucaoContrato> obras;

    @Invisivel
    @OneToMany(mappedBy = "execucaoContrato", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta("Empenhos")
    private List<ExecucaoContratoEmpenho> empenhos;

    @Invisivel
    @OneToMany(mappedBy = "execucaoContrato", cascade = CascadeType.MERGE)
    private List<ExecucaoContratoEstorno> estornosExecucao;

    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Forma de Entrega")
    private FormaEntregaExecucao formaEntrega;

    @Pesquisavel
    @Etiqueta("Execução em Andamento")
    private Boolean execucaoAndamento;

    private Boolean reprocessada;

    @ManyToOne
    @Etiqueta("Unidade Orçamentária")
    private UnidadeOrganizacional unidadeOrcamentaria;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Origem")
    private OrigemSaldoItemContrato origem;

    @Etiqueta("Id de Origem")
    private Long idOrigem;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Operação de Origem")
    private OperacaoSaldoItemContrato operacaoOrigem;

    @Transient
    @Etiqueta("Unidade Orçamentária")
    private HierarquiaOrganizacional hierarquiaOrcamentaria;

    @Transient
    private List<ExecucaoContratoItem> itensQuantidade;
    @Transient
    private List<ExecucaoContratoItem> itensValor;

    public ExecucaoContrato() {
        super();
        valor = BigDecimal.ZERO;
        execucaoAndamento = Boolean.FALSE;
        dataLancamento = new Date();
        empenhos = Lists.newArrayList();
        reservas = Lists.newArrayList();
        itensQuantidade = Lists.newArrayList();
        itensValor = Lists.newArrayList();
    }

    public List<ExecucaoContratoEstorno> getEstornosExecucao() {
        return estornosExecucao;
    }

    public void setEstornosExecucao(List<ExecucaoContratoEstorno> estornos) {
        this.estornosExecucao = estornos;
    }

    public List<ExecucaoContratoItem> getItensQuantidade() {
        return itensQuantidade;
    }

    public void setItensQuantidade(List<ExecucaoContratoItem> itensQuantidade) {
        this.itensQuantidade = itensQuantidade;
    }

    public List<ExecucaoContratoItem> getItensValor() {
        return itensValor;
    }

    public void setItensValor(List<ExecucaoContratoItem> itensValor) {
        this.itensValor = itensValor;
    }

    public List<ExecucaoContratoItem> getItens() {
        return itens;
    }

    public void setItens(List<ExecucaoContratoItem> itens) {
        this.itens = itens;
    }

    public List<ExecucaoContratoTipo> getReservas() {
        return reservas;
    }

    public void setReservas(List<ExecucaoContratoTipo> reservas) {
        this.reservas = reservas;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<NotaFiscalExecucaoContrato> getNotasFiscais() {
        return notasFiscais;
    }

    public void setNotasFiscais(List<NotaFiscalExecucaoContrato> notasFiscais) {
        this.notasFiscais = notasFiscais;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public List<ObraExecucaoContrato> getObras() {
        return obras;
    }

    public void setObras(List<ObraExecucaoContrato> obras) {
        this.obras = obras;
    }

    public List<ExecucaoContratoEmpenho> getEmpenhos() {
        return empenhos;
    }

    public void setEmpenhos(List<ExecucaoContratoEmpenho> empenhos) {
        this.empenhos = empenhos;
    }

    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public FormaEntregaExecucao getFormaEntrega() {
        return formaEntrega;
    }

    public void setFormaEntrega(FormaEntregaExecucao formaEntrega) {
        this.formaEntrega = formaEntrega;
    }

    public Boolean getExecucaoAndamento() {
        return execucaoAndamento;
    }

    public void setExecucaoAndamento(Boolean execucaoAndamento) {
        this.execucaoAndamento = execucaoAndamento;
    }

    public Boolean getReprocessada() {
        return reprocessada;
    }

    public void setReprocessada(Boolean reprocessada) {
        this.reprocessada = reprocessada;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
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
        if (hierarquiaOrcamentaria != null) {
            unidadeOrcamentaria = hierarquiaOrcamentaria.getSubordinada();
        }
        this.hierarquiaOrcamentaria = hierarquiaOrcamentaria;
    }

    public OrigemSaldoItemContrato getOrigem() {
        return origem;
    }

    public void setOrigem(OrigemSaldoItemContrato origem) {
        this.origem = origem;
    }

    public Long getIdOrigem() {
        return idOrigem;
    }

    public void setIdOrigem(Long idOrigem) {
        this.idOrigem = idOrigem;
    }

    public OperacaoSaldoItemContrato getOperacaoOrigem() {
        return operacaoOrigem;
    }

    public void setOperacaoOrigem(OperacaoSaldoItemContrato operacaoOrigem) {
        this.operacaoOrigem = operacaoOrigem;
    }

    public void adicionarItensListaOriginal() {
        setItens(Lists.<ExecucaoContratoItem>newArrayList());
        getItens().addAll(getItensQuantidade());
        getItens().addAll(getItensValor());
    }

    public void calcularValorTotal() {
        try {
            setValor(getValorTotalItemQuantidade().add(getValorTotalItemValor()));
        } catch (NullPointerException e) {
            setValor(BigDecimal.ZERO);
        }
    }

    public boolean hasSolicitacaoEmpenhoEmpenhada() {
        try {
            if (empenhos != null) {
                for (ExecucaoContratoEmpenho empenho : empenhos) {
                    if (empenho.getSolicitacaoEmpenho().getEmpenho() != null) {
                        return true;
                    }
                }
            }
        } catch (NullPointerException npe) {
            return false;
        }
        return false;
    }

    public BigDecimal getValorReservado() {
        BigDecimal valorReservado = BigDecimal.ZERO;
        if (getReservas() != null) {
            for (ExecucaoContratoTipo execucaoContratoTipo : getReservas()) {
                valorReservado = valorReservado.add(execucaoContratoTipo.getValorTotalReservado());
            }
        }
        return valorReservado;
    }

    public BigDecimal getValorDisponivel() {
        if (getValor() == null) {
            return BigDecimal.ZERO;
        }
        return getValor().subtract(getValorReservado());
    }

    public BigDecimal getValorTotalItens() {
        return getValorTotalItemValor().add(getValorTotalItemQuantidade());
    }

    public BigDecimal getValorTotalItensExecucao() {
        BigDecimal valor = BigDecimal.ZERO;
        for (ExecucaoContratoItem item : itens) {
            valor = valor.add(item.getValorTotal());
        }
        return valor;
    }

    public BigDecimal getValorTotalReservas() {
        BigDecimal valor = BigDecimal.ZERO;
        for (ExecucaoContratoTipo item : reservas) {
            valor = valor.add(item.getValor());
        }
        return valor;
    }

    public BigDecimal getValorEmpenhado() {
        BigDecimal total = BigDecimal.ZERO;
        if (empenhos != null && !empenhos.isEmpty()) {
            for (ExecucaoContratoEmpenho exEmp : empenhos) {
                if (exEmp.getEmpenho() != null) {
                    total = total.add(exEmp.getEmpenho().getValor());
                }
            }
        }
        return total;
    }

    public BigDecimal getValorExecucaoLiquido() {
        return getValor().subtract(getValorEstornado()).subtract(getValorCancelamentoRestoPagar());
    }

    public BigDecimal getValorAEmpenhar() {
        return getValor().subtract(getValorEmpenhado()).subtract(getValorEstornadoSolicitacaoEmpenho()).add(getValorEstornado()).add(getValorCancelamentoRestoPagar());
    }

    public BigDecimal getValorCancelamentoRestoPagar() {
        BigDecimal total = BigDecimal.ZERO;
        if (empenhos != null && !empenhos.isEmpty()) {
            for (ExecucaoContratoEmpenho exEmp : empenhos) {
                if (exEmp.getEmpenho() != null && exEmp.getEstornosEmpenho() != null) {
                    for (EmpenhoEstorno estorno : exEmp.getEstornosEmpenho()) {
                        if (estorno.isEmpenhoEstornoResto()) {
                            total = total.add(estorno.getValor());
                        }
                    }
                }
            }
        }
        return total;
    }

    public BigDecimal getValorEstornadoSolicitacaoEmpenho() {
        BigDecimal total = BigDecimal.ZERO;
        if (empenhos != null && !empenhos.isEmpty()) {
            for (ExecucaoContratoEmpenho exEmp : empenhos) {
                if (exEmp.getEmpenho() == null && exEmp.getSolicitacaoEmpenho().getEstornada()) {
                    total = total.add(exEmp.getSolicitacaoEmpenho().getValor());
                }
            }
        }
        return total;
    }

    public BigDecimal getValorEstornado() {
        BigDecimal total = BigDecimal.ZERO;
        if (empenhos != null && !empenhos.isEmpty()) {
            for (ExecucaoContratoEmpenho exEmp : empenhos) {
                if (exEmp.getEmpenho() != null && exEmp.getEmpenho().isEmpenhoNormal() && exEmp.getEstornosEmpenho() != null) {
                    for (EmpenhoEstorno estorno : exEmp.getEstornosEmpenho()) {
                        if (estorno.isEmpenhoEstornoNormal()) {
                            total = total.add(estorno.getValor());
                        }
                    }
                }
            }
        }
        return total;
    }

    public BigDecimal getValorTotalItemQuantidade() {
        BigDecimal total = BigDecimal.ZERO;
        try {
            for (ExecucaoContratoItem item : getItensQuantidade()) {
                total = total.add(item.getValorTotal());
            }
            return total;
        } catch (NullPointerException e) {
            return total;
        }
    }

    public BigDecimal getValorTotalItemValor() {
        BigDecimal total = BigDecimal.ZERO;
        try {
            for (ExecucaoContratoItem item : getItensValor()) {
                total = total.add(item.getValorTotal());
            }
            return total;
        } catch (NullPointerException e) {
            return total;
        }
    }

    public Integer getProximoNumero(List<? extends Object> lista) {
        try {
            return lista.size() + 1;
        } catch (Exception e) {
            return 1;
        }
    }

    public Boolean hasEmpenhos() {
        return empenhos != null && !empenhos.isEmpty();
    }

    public Boolean hasEstornoExecucao() {
        return estornosExecucao != null && !estornosExecucao.isEmpty();
    }

    public Boolean hasEstornosEmpenho() {
        if (hasEmpenhos()) {
            for (ExecucaoContratoEmpenho exEmp : empenhos) {
                if (exEmp.getEstornosEmpenho() != null && !exEmp.getEstornosEmpenho().isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    public Boolean hasItens() {
        return (itensQuantidade != null && !itensQuantidade.isEmpty()) || (itensValor != null && !itensValor.isEmpty());
    }

    public Boolean hasReservas() {
        return getReservas() != null && !getReservas().isEmpty();
    }

    public Boolean hasItensExecucaoQuantidade() {
        return itensQuantidade != null && !itensQuantidade.isEmpty();
    }

    public Boolean hasItensExecucaovalor() {
        return itensValor != null && !itensValor.isEmpty();
    }

    public HashMap<TipoObjetoCompra, List<ExecucaoContratoItem>> agruparItensPorTipoObjetoCompra() {
        HashMap<TipoObjetoCompra, List<ExecucaoContratoItem>> mapa = Maps.newHashMap();
        for (ExecucaoContratoItem itemExec : getItens()) {
            if (itemExec.hasQuantidade() || itemExec.hasValorTotal()) {
                TipoObjetoCompra tipoObjetoCompra = itemExec.getItemContrato().getItemAdequado().getObjetoCompra().getTipoObjetoCompra();
                if (mapa.containsKey(tipoObjetoCompra)) {
                    mapa.get(tipoObjetoCompra).add(itemExec);
                } else {
                    mapa.put(tipoObjetoCompra, Lists.<ExecucaoContratoItem>newArrayList(itemExec));
                }
            }
        }
        return mapa;
    }

    @Override
    public int compareTo(ExecucaoContrato o) {
        if (this.numero != null && o.getNumero() != null) {
            return this.numero.compareTo(o.getNumero());
        }
        return 0;
    }

    public String toStringExecucao() {
        try {
            return "Nº " + numero + " - " + DataUtil.getDataFormatada(dataLancamento) + " - " + Util.formataValor(valor);
        } catch (NullPointerException e) {
            return "";
        }
    }

    @Override
    public String toString() {
        try {
            String s = "";
            if (numero != null) {
                s = "nº " + numero;
            }
            if (!contrato.getNumeroAnoTermo().isEmpty()) {
                s += " -  Contrato: " + contrato.getNumeroContrato() + " - " + contrato.getNumeroAnoTermo();
            }
            if (valor != null) {
                s += " - " + Util.formataValor(valor);
            }
            return s;
        } catch (NullPointerException bp) {
            return "";
        }
    }
}
