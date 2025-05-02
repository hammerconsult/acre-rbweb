/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.*;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Felipe Marinzeck
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Licitacao")
@Etiqueta("Requisição de Compra")
public class RequisicaoDeCompra extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número")
    private Long numero;

    @Tabelavel
    @Obrigatorio
    @Temporal(javax.persistence.TemporalType.DATE)
    @Pesquisavel
    @Etiqueta("Data")
    private Date dataRequisicao;

    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Length(maximo = 3000)
    @Etiqueta("Descrição")
    private String descricao;

    @Etiqueta("Descrição Prazo Entrega")
    private String descricaoPrazoEntrega;

    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Length(maximo = 3000)
    @Etiqueta("Local de Entrega")
    private String localEntrega;

    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Objeto de Compra")
    private TipoObjetoCompra tipoObjetoCompra;

    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Requisiçao")
    private TipoRequisicaoCompra tipoRequisicao;

    @ManyToOne
    @Pesquisavel
    @Etiqueta("Contrato")
    private Contrato contrato;

    @ManyToOne
    @Pesquisavel
    @Etiqueta("Reconhecimento de Dívida")
    private ReconhecimentoDivida reconhecimentoDivida;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Criado por")
    private PessoaFisica criadoPor;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação")
    private SituacaoRequisicaoCompra situacaoRequisicaoCompra;

    @Etiqueta("Prazo de Entrega")
    @Positivo
    @Obrigatorio
    private Integer prazoEntrega;

    @Etiqueta("Tipo Prazo de Entrega")
    @Enumerated(EnumType.STRING)
    private TipoPrazo tipoPrazoEntrega;

    @Etiqueta("Tipo Conta Despesa")
    @Enumerated(EnumType.STRING)
    private TipoContaDespesa tipoContaDespesa;

    @OneToMany(mappedBy = "requisicaoDeCompra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemRequisicaoDeCompra> itens;

    @OneToMany(mappedBy = "requisicaoCompra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RequisicaoCompraExecucao> execucoes;

    public RequisicaoDeCompra() {
        itens = Lists.newArrayList();
        execucoes = Lists.newArrayList();
        situacaoRequisicaoCompra = SituacaoRequisicaoCompra.EM_ELABORACAO;

    }

    public String getDescricaoPrazoEntrega() {
        return descricaoPrazoEntrega;
    }

    public void setDescricaoPrazoEntrega(String descricaoPrazoEntrega) {
        this.descricaoPrazoEntrega = descricaoPrazoEntrega;
    }

    public TipoRequisicaoCompra getTipoRequisicao() {
        return tipoRequisicao;
    }

    public void setTipoRequisicao(TipoRequisicaoCompra tipoRequisicao) {
        this.tipoRequisicao = tipoRequisicao;
    }

    public PessoaFisica getCriadoPor() {
        return criadoPor;
    }

    public void setCriadoPor(PessoaFisica criadoPor) {
        this.criadoPor = criadoPor;
    }

    public ReconhecimentoDivida getReconhecimentoDivida() {
        return reconhecimentoDivida;
    }

    public void setReconhecimentoDivida(ReconhecimentoDivida reconhecimentoDivida) {
        this.reconhecimentoDivida = reconhecimentoDivida;
    }

    public SituacaoRequisicaoCompra getSituacaoRequisicaoCompra() {
        return situacaoRequisicaoCompra;
    }

    public void setSituacaoRequisicaoCompra(SituacaoRequisicaoCompra situacaoRequisicaoCompra) {
        this.situacaoRequisicaoCompra = situacaoRequisicaoCompra;
    }

    public Integer getPrazoEntrega() {
        return prazoEntrega;
    }

    public void setPrazoEntrega(Integer prazoEntrega) {
        this.prazoEntrega = prazoEntrega;
    }

    public TipoPrazo getTipoPrazoEntrega() {
        return tipoPrazoEntrega;
    }

    public void setTipoPrazoEntrega(TipoPrazo tipoPrazoEntrega) {
        this.tipoPrazoEntrega = tipoPrazoEntrega;
    }

    public TipoContaDespesa getTipoContaDespesa() {
        return tipoContaDespesa;
    }

    public void setTipoContaDespesa(TipoContaDespesa tipoContaDespesa) {
        this.tipoContaDespesa = tipoContaDespesa;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataRequisicao() {
        return dataRequisicao;
    }

    public void setDataRequisicao(Date dataRequisicao) {
        this.dataRequisicao = dataRequisicao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<ItemRequisicaoDeCompra> getItens() {
        return itens;
    }

    public void setItens(List<ItemRequisicaoDeCompra> listaItemRequisicao) {
        this.itens = listaItemRequisicao;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public String getLocalEntrega() {
        return localEntrega;
    }

    public void setLocalEntrega(String localEntrega) {
        this.localEntrega = localEntrega;
    }

    public TipoObjetoCompra getTipoObjetoCompra() {
        return tipoObjetoCompra;
    }

    public void setTipoObjetoCompra(TipoObjetoCompra tipoObjetoCompra) {
        this.tipoObjetoCompra = tipoObjetoCompra;
    }

    public ExecucaoProcesso getExecucaoProcesso() {
        try {
            Optional<RequisicaoCompraExecucao> first = execucoes.stream()
                .filter(exec -> exec.getExecucaoProcesso() != null && tipoRequisicao.isExecucaoProcesso())
                .findFirst();
            return first.map(RequisicaoCompraExecucao::getExecucaoProcesso).orElse(null);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public Pessoa getIdProcesso() {
        switch (tipoRequisicao) {
            case CONTRATO:
                return contrato.getContratado();
            case RECONHECIMENTO_DIVIDA:
                return reconhecimentoDivida.getFornecedor();
            case ATA_REGISTRO_PRECO:
            case DISPENSA_LICITACAO_INEXIGIBILIDADE:
                return getExecucaoProcesso().getFornecedor();
            default:
                return null;
        }
    }

    public String getDescricaoProcesso() {
        switch (tipoRequisicao) {
            case CONTRATO:
                return contrato.toString();
            case RECONHECIMENTO_DIVIDA:
                return reconhecimentoDivida.toString();
            case ATA_REGISTRO_PRECO:
            case DISPENSA_LICITACAO_INEXIGIBILIDADE:
                return getExecucaoProcesso().getDescricaoProcesso();
            default:
                return "";
        }
    }

    public Pessoa getFornecedor() {
        if (isTipoContrato() && contrato != null) {
            return contrato.getContratado();
        } else if (isTipoReconhecimentoDivida() && reconhecimentoDivida != null) {
            return reconhecimentoDivida.getFornecedor();
        } else if (getExecucaoProcesso() != null) {
            return getExecucaoProcesso().getFornecedor();
        }
        return null;
    }

    public Date getDataProcesso() {
        switch (tipoRequisicao) {
            case CONTRATO:
                return contrato.getDataAprovacao();
            case RECONHECIMENTO_DIVIDA:
                return reconhecimentoDivida.getDataReconhecimento();
            case ATA_REGISTRO_PRECO:
            case DISPENSA_LICITACAO_INEXIGIBILIDADE:
                return getExecucaoProcesso().getDataLancamento();
            default:
                return new Date();
        }
    }

    public boolean hasFornecedor() {
        return getFornecedor() != null;
    }

    public boolean isTipoContrato() {
        return tipoRequisicao != null && tipoRequisicao.isContrato();
    }

    public boolean isTipoAtaRegistroPreco() {
        return tipoRequisicao != null && tipoRequisicao.isAtaRegistroPreco();
    }

    public boolean isTipoDispensaInexigibilidade() {
        return tipoRequisicao != null && tipoRequisicao.isDispensaInexigibilidade();
    }

    public boolean isTipoReconhecimentoDivida() {
        return tipoRequisicao != null && tipoRequisicao.isReconhecimentoDivida();
    }

    public boolean isTipoExecucaoProcesso() {
        return tipoRequisicao != null && tipoRequisicao.isExecucaoProcesso();
    }

    public boolean isRequisicaoVPD() {
        return TipoContaDespesa.VARIACAO_PATRIMONIAL_DIMINUTIVA.equals(tipoContaDespesa);
    }

    @Override
    public String toString() {
        return toStringAutoComplete();
    }

    public String toStringAutoComplete() {
        try {
            String descricao = this.descricao.length() > 80 ? this.descricao.substring(0, 80) + "..." : this.descricao;
            return this.numero + " - " + descricao + " - " + DataUtil.getDataFormatada(this.dataRequisicao);
        } catch (Exception e) {
            return this.numero + " - " + DataUtil.getDataFormatada(this.dataRequisicao);
        }
    }

    public Boolean isEmElaboracao() {
        return SituacaoRequisicaoCompra.EM_ELABORACAO.equals(situacaoRequisicaoCompra);
    }

    public boolean isLicitacaoMaiorDesconto() {
        if (isTipoContrato() && contrato != null && contrato.isDeRegistroDePrecoExterno()) {
            return contrato.getRegistroSolicitacaoMaterialExterno() != null && contrato.getRegistroSolicitacaoMaterialExterno().getTipoAvaliacao().isMaiorDesconto();
        }
        Licitacao licitacao = null;
        if (isTipoContrato() && contrato != null && contrato.getLicitacao() != null) {
            licitacao = contrato.getLicitacao();

        } else if (isTipoAtaRegistroPreco() && getExecucaoProcesso() != null && getExecucaoProcesso().getAtaRegistroPreco() != null) {
            licitacao = getExecucaoProcesso().getAtaRegistroPreco().getLicitacao();
        }
        return licitacao != null && licitacao.getTipoAvaliacao().isMaiorDesconto();
    }

    public BigDecimal getValorTotal() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasItens()) {
            for (ItemRequisicaoDeCompra item : itens) {
                total = total.add(item.getValorTotal());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalDesdobradoItens() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasItens()) {
            for (ItemRequisicaoDeCompra item : itens) {
                total = total.add(item.getValorTotalDesdobrado());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalDescontoItens() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasItens()) {
            for (ItemRequisicaoDeCompra item : itens) {
                total = total.add(item.getValorDescontoTotal());
            }
        }
        return total;
    }

    public boolean hasItens() {
        return itens != null && !itens.isEmpty();
    }

    public List<RequisicaoCompraExecucao> getExecucoes() {
        return execucoes;
    }

    public void setExecucoes(List<RequisicaoCompraExecucao> execucoes) {
        this.execucoes = execucoes;
    }

    public AtaRegistroPreco getAtaRegistroPreco() {
        return getExecucaoProcesso().getAtaRegistroPreco();
    }

    public DispensaDeLicitacao getDispensaLicitacao() {
        return getExecucaoProcesso().getDispensaLicitacao();
    }
}
