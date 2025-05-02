package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ItemDoctoItemEntrada;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class DocumentoFiscalIntegracaoGrupoItem implements Serializable {

    private static final BigDecimal CEM = new BigDecimal("100");
    private Long idItem;
    private Long idBem;
    private String numeroItem;
    private String descricaoItem;
    private ItemDoctoItemEntrada itemDoctoItemEntrada;
    private Long idGrupo;
    private String grupo;
    private String situacao;
    private BigDecimal valorLancamento;
    private BigDecimal valorALiquidar;
    private BigDecimal valorLiquidado;
    private BigDecimal quantidade;
    private BigDecimal valorUnitario;
    private BigDecimal valorTotal;
    private DocumentoFiscalIntegracaoGrupo documentoFiscalIntegracaoGrupo;

    public DocumentoFiscalIntegracaoGrupoItem() {
        valorLiquidado = BigDecimal.ZERO;
        valorALiquidar = BigDecimal.ZERO;
    }

    public BigDecimal getValorALiquidar() {
        return valorALiquidar;
    }

    public void setValorALiquidar(BigDecimal valorALiquidar) {
        this.valorALiquidar = valorALiquidar;
    }

    public Long getIdItem() {
        return idItem;
    }

    public void setIdItem(Long idItem) {
        this.idItem = idItem;
    }

    public void setValorLancamento(BigDecimal valorLancamento) {
        this.valorLancamento = valorLancamento;
    }

    public void setValorLiquidado(BigDecimal valorLiquidado) {
        this.valorLiquidado = valorLiquidado;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public BigDecimal getValorLancamento() {
        return valorLancamento;
    }

    public BigDecimal getValorLiquidado() {
        return valorLiquidado;
    }

    public Long getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(Long idGrupo) {
        this.idGrupo = idGrupo;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getNumeroItem() {
        return numeroItem;
    }

    public void setNumeroItem(String numeroItem) {
        this.numeroItem = numeroItem;
    }

    public String getDescricaoItem() {
        return descricaoItem;
    }

    public void setDescricaoItem(String descricaoItem) {
        this.descricaoItem = descricaoItem;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public Long getIdBem() {
        return idBem;
    }

    public void setIdBem(Long idBem) {
        this.idBem = idBem;
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

    public ItemDoctoItemEntrada getItemDoctoItemEntrada() {
        return itemDoctoItemEntrada;
    }

    public void setItemDoctoItemEntrada(ItemDoctoItemEntrada itemDoctoItemEntrada) {
        this.itemDoctoItemEntrada = itemDoctoItemEntrada;
    }

    public DocumentoFiscalIntegracaoGrupo getDocumentoFiscalIntegracaoGrupo() {
        return documentoFiscalIntegracaoGrupo;
    }

    public void setDocumentoFiscalIntegracaoGrupo(DocumentoFiscalIntegracaoGrupo documentoFiscalIntegracaoGrupo) {
        this.documentoFiscalIntegracaoGrupo = documentoFiscalIntegracaoGrupo;
    }

    public boolean isGrupoDiferenteDoOriginal() {
        if (documentoFiscalIntegracaoGrupo != null) {
            switch (documentoFiscalIntegracaoGrupo.getTipoContaDespesa()) {
                case BEM_MOVEL:
                    return documentoFiscalIntegracaoGrupo.getGrupoBemDesdobramento().getId().compareTo(idGrupo) != 0;

                case BEM_ESTOQUE:
                    return documentoFiscalIntegracaoGrupo.getGrupoMaterialDesdobramento().getId().compareTo(idGrupo) != 0;

                default:
                    return false;
            }
        }
        return false;
    }

    public static BigDecimal getValorProporcionalAoItem(BigDecimal valorTotalDocumento, BigDecimal valorLiquidado, BigDecimal valorTotalItem) {
        BigDecimal porcentagemPorGrupo = valorLiquidado.multiply(CEM).divide(valorTotalDocumento, 8, RoundingMode.HALF_EVEN);
        return valorTotalItem.multiply(porcentagemPorGrupo).divide(CEM, 2, RoundingMode.HALF_EVEN);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocumentoFiscalIntegracaoGrupoItem that = (DocumentoFiscalIntegracaoGrupoItem) o;
        return Objects.equals(idItem, that.idItem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idItem);
    }
}
