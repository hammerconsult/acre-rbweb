package br.com.webpublico.entidades;

import br.com.webpublico.enums.*;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadeDetendorDocumentoLicitacao;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Entity

@Audited
@GrupoDiagrama(nome = "Licitações")
public class ExecucaoProcesso extends SuperEntidade implements EntidadeDetendorDocumentoLicitacao, Comparable<ExecucaoProcesso> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Data de Lançamento")
    private Date dataLancamento;

    @Etiqueta("Número")
    private Integer numero;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Execução")
    private TipoExecucaoProcesso tipoExecucao;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Forma de Entrega")
    private FormaEntregaExecucao formaEntrega;

    @ManyToOne
    @Etiqueta("Fornecedor")
    private Pessoa fornecedor;

    @ManyToOne
    @Etiqueta("Unidade Orçamentária")
    private UnidadeOrganizacional unidadeOrcamentaria;

    @Etiqueta("Valor")
    private BigDecimal valor;

    @OneToOne(cascade = CascadeType.ALL)
    private DetentorDocumentoLicitacao detentorDocumentoLicitacao;

    @Invisivel
    @Etiqueta("Itens")
    @OneToMany(mappedBy = "execucaoProcesso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExecucaoProcessoItem> itens;

    @Invisivel
    @Etiqueta("Empenhos")
    @OneToMany(mappedBy = "execucaoProcesso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExecucaoProcessoEmpenho> empenhos;

    @Invisivel
    @Etiqueta("Reservas")
    @OneToMany(mappedBy = "execucaoProcesso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExecucaoProcessoReserva> reservas;

    @Etiqueta("Execução Ata")
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "execucaoProcesso", orphanRemoval = true)
    private ExecucaoProcessoAta execucaoProcessoAta;

    @Etiqueta("Execução Dispensa")
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "execucaoProcesso", orphanRemoval = true)
    private ExecucaoProcessoDispensa execucaoProcessoDispensa;

    @Etiqueta("Execução Contrato")
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "execucaoProcesso", orphanRemoval = true)
    private ExecucaoProcessoContrato execucaoProcessoContrato;

    @Etiqueta("Id Origem")
    private Long idOrigem;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Origem")
    private OrigemSaldoExecucaoProcesso origem;

    public ExecucaoProcesso() {
        valor = BigDecimal.ZERO;
        dataLancamento = new Date();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public TipoExecucaoProcesso getTipoExecucao() {
        return tipoExecucao;
    }

    public void setTipoExecucao(TipoExecucaoProcesso tipoExecucao) {
        this.tipoExecucao = tipoExecucao;
    }

    public FormaEntregaExecucao getFormaEntrega() {
        return formaEntrega;
    }

    public void setFormaEntrega(FormaEntregaExecucao formaEntrega) {
        this.formaEntrega = formaEntrega;
    }

    public Pessoa getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Pessoa fornecedor) {
        this.fornecedor = fornecedor;
    }

    public UnidadeOrganizacional getUnidadeOrcamentaria() {
        return unidadeOrcamentaria;
    }

    public void setUnidadeOrcamentaria(UnidadeOrganizacional unidadeOrcamentaria) {
        this.unidadeOrcamentaria = unidadeOrcamentaria;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    @Override
    public DetentorDocumentoLicitacao getDetentorDocumentoLicitacao() {
        return detentorDocumentoLicitacao;
    }

    @Override
    public TipoMovimentoProcessoLicitatorio getTipoAnexo() {
        return null;
    }

    @Override
    public void setDetentorDocumentoLicitacao(DetentorDocumentoLicitacao detentorDocumentoLicitacao) {
        this.detentorDocumentoLicitacao = detentorDocumentoLicitacao;
    }

    public List<ExecucaoProcessoItem> getItens() {
        return itens;
    }

    public void setItens(List<ExecucaoProcessoItem> itens) {
        this.itens = itens;
    }

    public List<ExecucaoProcessoEmpenho> getEmpenhos() {
        return empenhos;
    }

    public void setEmpenhos(List<ExecucaoProcessoEmpenho> empenhos) {
        this.empenhos = empenhos;
    }

    public List<ExecucaoProcessoReserva> getReservas() {
        return reservas;
    }

    public void setReservas(List<ExecucaoProcessoReserva> reservas) {
        this.reservas = reservas;
    }

    public ExecucaoProcessoAta getExecucaoProcessoAta() {
        return execucaoProcessoAta;
    }

    public void setExecucaoProcessoAta(ExecucaoProcessoAta execucaoProcessoAta) {
        this.execucaoProcessoAta = execucaoProcessoAta;
    }

    public ExecucaoProcessoDispensa getExecucaoProcessoDispensa() {
        return execucaoProcessoDispensa;
    }

    public void setExecucaoProcessoDispensa(ExecucaoProcessoDispensa execucaoProcessoDispensa) {
        this.execucaoProcessoDispensa = execucaoProcessoDispensa;
    }

    public ExecucaoProcessoContrato getExecucaoProcessoContrato() {
        return execucaoProcessoContrato;
    }

    public void setExecucaoProcessoContrato(ExecucaoProcessoContrato execucaoProcessoContrato) {
        this.execucaoProcessoContrato = execucaoProcessoContrato;
    }

    public Long getIdOrigem() {
        return idOrigem;
    }

    public void setIdOrigem(Long idOrigemSaldo) {
        this.idOrigem = idOrigemSaldo;
    }

    public OrigemSaldoExecucaoProcesso getOrigem() {
        return origem;
    }

    public void setOrigem(OrigemSaldoExecucaoProcesso origemSaldo) {
        this.origem = origemSaldo;
    }

    public AtaRegistroPreco getAtaRegistroPreco() {
        return execucaoProcessoAta.getAtaRegistroPreco();
    }

    public DispensaDeLicitacao getDispensaLicitacao() {
        return execucaoProcessoDispensa.getDispensaLicitacao();
    }

    public ModalidadeLicitacao getModalidadeProcesso() {
        if (tipoExecucao.isAta()) {
            return getAtaRegistroPreco().getLicitacao().getModalidadeLicitacao();
        }
        return getDispensaLicitacao().getModalidade();
    }

    public TipoNaturezaDoProcedimentoLicitacao getNaturezaProcesso() {
        if (tipoExecucao.isAta()) {
            return getAtaRegistroPreco().getLicitacao().getNaturezaDoProcedimento();
        }
        return getDispensaLicitacao().getNaturezaProcedimento();
    }

    public SubTipoObjetoCompra getSubTipoObjetoCompra() {
        if (tipoExecucao.isAta()) {
            return getProcessoCompra().getSolicitacaoMaterial().getSubTipoObjetoCompra();
        }
        return getProcessoCompra().getSolicitacaoMaterial().getSubTipoObjetoCompra();
    }

    public UnidadeOrganizacional getUnidadeAdministrativa() {
        if (tipoExecucao.isAta()) {
            return getAtaRegistroPreco().getUnidadeOrganizacional();
        }
        return getProcessoCompra().getUnidadeOrganizacional();
    }


    public BigDecimal getValorTotalItensExecucao() {
        BigDecimal valor = BigDecimal.ZERO;
        for (ExecucaoProcessoItem item : itens) {
            valor = valor.add(item.getValorTotal());
        }
        return valor;
    }

    public BigDecimal getValorTotalReservas() {
        BigDecimal valor = BigDecimal.ZERO;
        for (ExecucaoProcessoReserva exRes : reservas) {
            valor = valor.add(exRes.getValor());
        }
        return valor;
    }

    public boolean isExecucaoAta() {
        return tipoExecucao != null && tipoExecucao.isAta();
    }

    public boolean isExecucaoDispensa() {
        return tipoExecucao != null && tipoExecucao.isDispensa();
    }

    public Boolean hasEmpenhos() {
        return empenhos != null && !empenhos.isEmpty();
    }

    public Boolean hasReserva() {
        return reservas != null && !reservas.isEmpty();
    }

    public boolean hasSolicitacaoEmpenhoEmpenhada() {
        return hasEmpenhos() && empenhos.stream().anyMatch(exem -> exem.getEmpenho() != null);
    }

    public HashMap<TipoObjetoCompra, List<ExecucaoProcessoItem>> agruparItensPorTipoObjetoCompra() {
        HashMap<TipoObjetoCompra, List<ExecucaoProcessoItem>> mapa = Maps.newHashMap();
        for (ExecucaoProcessoItem itemExec : getItens()) {
            if (itemExec.hasQuantidade() || itemExec.hasValor()) {
                TipoObjetoCompra tipoObjetoCompra = itemExec.getItemProcessoCompra().getObjetoCompra().getTipoObjetoCompra();
                if (mapa.containsKey(tipoObjetoCompra)) {
                    mapa.get(tipoObjetoCompra).add(itemExec);
                } else {
                    mapa.put(tipoObjetoCompra, Lists.<ExecucaoProcessoItem>newArrayList(itemExec));
                }
            }
        }
        return mapa;
    }

    @Override
    public int compareTo(ExecucaoProcesso o) {
        if (this.numero != null && o.getNumero() != null) {
            return this.numero.compareTo(o.getNumero());
        }
        return 0;
    }

    @Override
    public String toString() {
        try {
            if (tipoExecucao.isAta()) {
                return "Nº " + numero + " - " + DataUtil.getDataFormatada(dataLancamento) + " - " + Util.formataValor(valor)
                    + ", Ata nº " + getAtaRegistroPreco().getNumero() + " - " + getFornecedor();
            }
            return "Nº " + numero + " - " + DataUtil.getDataFormatada(dataLancamento) + " - " + Util.formataValor(valor)
                + ", " + getModalidadeProcesso().getDescricao() + " nº " + getDispensaLicitacao().getNumeroDaDispensa() + " - " + getFornecedor();
        } catch (NullPointerException bp) {
            return "";
        }
    }

    public ProcessoDeCompra getProcessoCompra() {
        if (tipoExecucao.isAta()) {
            return getAtaRegistroPreco().getLicitacao().getProcessoDeCompra();
        }
        return getDispensaLicitacao().getProcessoDeCompra();
    }

    public String getNumeroProcesso() {
        if (tipoExecucao.isAta()) {
            return String.valueOf(getAtaRegistroPreco().getNumero());
        }
        return String.valueOf(getDispensaLicitacao().getNumeroDaDispensa());
    }

    public String getNumeroAnoProcesso() {
        if (tipoExecucao.isAta()) {
            return getAtaRegistroPreco().getNumero() + "/" + getAtaRegistroPreco().getLicitacao().getExercicio().getAno();
        }
        return getDispensaLicitacao().getNumeroDaDispensa() + "/" + getDispensaLicitacao().getExercicioDaDispensa().getAno();
    }

    public Integer getAnoProcesso() {
        if (tipoExecucao.isAta()) {
            return getAtaRegistroPreco().getLicitacao().getExercicio().getAno();
        }
        return getDispensaLicitacao().getExercicioDaDispensa().getAno();
    }

    public String getDescricaoProcesso() {
        if (tipoExecucao.isAta()) {
            return tipoExecucao.getDescricao()
                + " N° " + getAtaRegistroPreco().getNumero()
                + " - " + getAtaRegistroPreco().getLicitacao().getModalidadeLicitacao()
                + " " + getAtaRegistroPreco().getLicitacao().getNaturezaDoProcedimento()
                + " " + getAtaRegistroPreco().getLicitacao().toStringNumeroExercicio();
        }
        return tipoExecucao.getDescricao() + " " + getDispensaLicitacao().toString();
    }

    public Long getIdProcesso() {
        if (tipoExecucao.isAta()) {
            return getAtaRegistroPreco().getId();
        } else if (tipoExecucao.isDispensa()) {
            return getDispensaLicitacao().getId();
        }
        return null;
    }
}
