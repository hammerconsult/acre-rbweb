package br.com.webpublico.entidades;

import br.com.webpublico.enums.OperacaoSaldoItemContrato;
import br.com.webpublico.enums.OrigemSaldoItemContrato;
import br.com.webpublico.enums.SubTipoSaldoItemContrato;
import br.com.webpublico.enums.TipoSaldoItemContrato;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import com.google.common.collect.ComparisonChain;
import org.hibernate.envers.Audited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@GrupoDiagrama(nome = "Contrato")
@Audited
@Etiqueta("Movimento Item Contrato")
public class MovimentoItemContrato extends SuperEntidade implements Comparable<MovimentoItemContrato> {

    private static final Logger logger = LoggerFactory.getLogger(MovimentoItemContrato.class);
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Item Contrato")
    private ItemContrato itemContrato;

    private Long indice;

    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Data do Movimento")
    private Date dataMovimento;

    @Etiqueta("Id Origem")
    private Long idOrigem;

    @Etiqueta("Id Movimento")
    private Long idMovimento;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Origem")
    private OrigemSaldoItemContrato origem;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo")
    private TipoSaldoItemContrato tipo;

    @Enumerated(EnumType.STRING)
    @Etiqueta("SubTipo")
    private SubTipoSaldoItemContrato subTipo;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Origem")
    private OperacaoSaldoItemContrato operacao;

    @Monetario
    @Etiqueta("Quantidade")
    private BigDecimal quantidade;

    @Monetario
    @Etiqueta("Valor Unitário")
    private BigDecimal valorUnitario;

    @Monetario
    @Etiqueta("Valor Total")
    private BigDecimal valorTotal;

    public MovimentoItemContrato() {
        quantidade = BigDecimal.ZERO;
        valorUnitario = BigDecimal.ZERO;
        valorTotal = BigDecimal.ZERO;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemContrato getItemContrato() {
        return itemContrato;
    }

    public void setItemContrato(ItemContrato itemContrato) {
        this.itemContrato = itemContrato;
    }

    public Long getIndice() {
        return indice;
    }

    public void setIndice(Long indice) {
        this.indice = indice;
    }

    public Date getDataMovimento() {
        return dataMovimento;
    }

    public void setDataMovimento(Date dataMovimento) {
        this.dataMovimento = dataMovimento;
    }

    public Long getIdOrigem() {
        return idOrigem;
    }

    public void setIdOrigem(Long idOrigem) {
        this.idOrigem = idOrigem;
    }

    public Long getIdMovimento() {
        return idMovimento;
    }

    public void setIdMovimento(Long idMovimento) {
        this.idMovimento = idMovimento;
    }

    public OrigemSaldoItemContrato getOrigem() {
        return origem;
    }

    public void setOrigem(OrigemSaldoItemContrato origem) {
        this.origem = origem;
    }

    public TipoSaldoItemContrato getTipo() {
        return tipo;
    }

    public void setTipo(TipoSaldoItemContrato tipo) {
        this.tipo = tipo;
    }

    public SubTipoSaldoItemContrato getSubTipo() {
        return subTipo;
    }

    public void setSubTipo(SubTipoSaldoItemContrato subTipo) {
        this.subTipo = subTipo;
    }

    public OperacaoSaldoItemContrato getOperacao() {
        return operacao;
    }

    public void setOperacao(OperacaoSaldoItemContrato operacao) {
        this.operacao = operacao;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String getUrlMovimentoItemContratoOrigem() {
        String caminho = "";
        switch (origem) {
            case CONTRATO:
                caminho = "/contrato-adm/ver/";
                caminho = getComplementoUrlMovimentoExecucao(caminho);
                break;
            case ADITIVO:
                caminho = "/aditivo-contrato/ver/";
                caminho = getComplementoUrlMovimentoExecucao(caminho);
                break;
            case APOSTILAMENTO:
                caminho = "/apostilamento-contrato/ver/";
                caminho = getComplementoUrlMovimentoExecucao(caminho);
                break;
        }
        return caminho + idMovimento + "/";
    }

    private String getComplementoUrlMovimentoExecucao(String caminho) {
        if (tipo.isExecucaoNormal()) {
            caminho = "/execucao-contrato-adm/ver/";
        } else if (tipo.isExecucaoEstorno()) {
            caminho = "/execucao-contrato-estorno/ver/";
        }
        return caminho;
    }

    public String getOrigemTipoSubTipo(){
        return origem.getDescricao() + " - " + tipo.getDescricao() + " - " + subTipo.getDescricao();
    }

    @Override
    public int compareTo(MovimentoItemContrato o) {
        return ComparisonChain.start()
            .compare(this.origem.getOrdem(), o.getOrigem().getOrdem())
            .compare(this.idOrigem, o.getIdOrigem())
            .compare(this.indice, o.getIndice())
            .result();
    }
}
