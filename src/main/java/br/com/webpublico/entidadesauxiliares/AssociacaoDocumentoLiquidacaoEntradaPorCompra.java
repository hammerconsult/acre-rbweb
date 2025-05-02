package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.DoctoFiscalEntradaCompra;
import br.com.webpublico.entidades.EntradaCompraMaterial;
import br.com.webpublico.entidades.ItemDoctoItemEntrada;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

public class AssociacaoDocumentoLiquidacaoEntradaPorCompra {

    private EntradaCompraMaterial entradaCompraMaterial;
    private DoctoFiscalEntradaCompra documentoFiscalEntrada;
    private TipoAssociacao tipoAssociacao;
    private List<DoctoFiscalEntradaCompra> documentosFiscaisEntrada;
    private List<VOLiquidacaoDocumentoFiscal> documentosFiscaisLiquidacao;
    private List<ItemDoctoItemEntrada> itensDocumentoEntrada;
    private List<FiltroAssociacaoDocumentoFiscalLiquidacao> filtros;
    private BigDecimal valorTotalLiquidadoDocumentoFiscalLiquidacao;

    public AssociacaoDocumentoLiquidacaoEntradaPorCompra() {
        documentosFiscaisEntrada = Lists.newArrayList();
        documentosFiscaisLiquidacao = Lists.newArrayList();
        filtros = Lists.newArrayList();
        itensDocumentoEntrada = Lists.newArrayList();
        valorTotalLiquidadoDocumentoFiscalLiquidacao = BigDecimal.ZERO;
    }

    public List<ItemDoctoItemEntrada> getItensDocumentoEntrada() {
        return itensDocumentoEntrada;
    }

    public void setItensDocumentoEntrada(List<ItemDoctoItemEntrada> itensDocumentoEntrada) {
        this.itensDocumentoEntrada = itensDocumentoEntrada;
    }

    public EntradaCompraMaterial getEntradaCompraMaterial() {
        return entradaCompraMaterial;
    }

    public void setEntradaCompraMaterial(EntradaCompraMaterial entradaCompraMaterial) {
        this.entradaCompraMaterial = entradaCompraMaterial;
    }

    public DoctoFiscalEntradaCompra getDocumentoFiscalEntrada() {
        return documentoFiscalEntrada;
    }

    public void setDocumentoFiscalEntrada(DoctoFiscalEntradaCompra documentoFiscalEntrada) {
        this.documentoFiscalEntrada = documentoFiscalEntrada;
    }

    public List<DoctoFiscalEntradaCompra> getDocumentosFiscaisEntrada() {
        return documentosFiscaisEntrada;
    }

    public void setDocumentosFiscaisEntrada(List<DoctoFiscalEntradaCompra> documentosFiscaisEntrada) {
        this.documentosFiscaisEntrada = documentosFiscaisEntrada;
    }

    public List<VOLiquidacaoDocumentoFiscal> getDocumentosFiscaisLiquidacao() {
        return documentosFiscaisLiquidacao;
    }

    public void setDocumentosFiscaisLiquidacao(List<VOLiquidacaoDocumentoFiscal> documentosFiscaisLiquidacao) {
        this.documentosFiscaisLiquidacao = documentosFiscaisLiquidacao;
    }

    public List<FiltroAssociacaoDocumentoFiscalLiquidacao> getFiltros() {
        return filtros;
    }

    public void setFiltros(List<FiltroAssociacaoDocumentoFiscalLiquidacao> filtros) {
        this.filtros = filtros;
    }

    public TipoAssociacao getTipoAssociacao() {
        return tipoAssociacao;
    }

    public void setTipoAssociacao(TipoAssociacao tipoAssociacao) {
        this.tipoAssociacao = tipoAssociacao;
    }

    public BigDecimal getValorTotalLiquidadoDocumentoFiscalLiquidacao() {
        return valorTotalLiquidadoDocumentoFiscalLiquidacao;
    }

    public void setValorTotalLiquidadoDocumentoFiscalLiquidacao(BigDecimal valorTotalLiquidadoDocumentoFiscalLiquidacao) {
        this.valorTotalLiquidadoDocumentoFiscalLiquidacao = valorTotalLiquidadoDocumentoFiscalLiquidacao;
    }

    public BigDecimal getValorTotalLiquidado() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasDocumentosFiscaisLiquidacao()) {
            for (VOLiquidacaoDocumentoFiscal docLiq : getDocumentosFiscaisLiquidacao()) {
                if (docLiq.getSelecionado()) {
                    total = total.add(docLiq.getLiquidacaoDoctoFiscal().getValorLiquidado());
                }
            }
        }
        return total;
    }

    public BigDecimal getValorTotalItensDocumento() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemDoctoItemEntrada item : itensDocumentoEntrada) {
            total = total.add(item.getValorTotal());
        }
        return total;
    }

    public BigDecimal getValorTotalLiquidadoItensDocumento() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemDoctoItemEntrada item : itensDocumentoEntrada) {
            if (item.getValorLiquidado() != null) {
                total = total.add(item.getValorLiquidado());
            }
        }
        return total;
    }

    public boolean hasDocumentosFiscaisLiquidacao() {
        return documentosFiscaisLiquidacao != null && !documentosFiscaisLiquidacao.isEmpty();
    }

    public boolean hasDocumentoEntradaSelecionado() {
        return documentoFiscalEntrada != null;
    }

    public boolean hasEntradaPorCompraSelecionada() {
        return entradaCompraMaterial != null;
    }

    public boolean isAssociarDocumento() {
        return TipoAssociacao.ASSOCIAR_DOCUMENTO.equals(tipoAssociacao);
    }

    public boolean isDesbloquearGrupo() {
        return TipoAssociacao.DESBLOQUEAR_VALOR_LIQUIDADO_GRUPO_DOCUMENTO.equals(tipoAssociacao);
    }
    public boolean isGerarNumeroSituacao() {
        return TipoAssociacao.GERAR_NUMERO_SITUACAO.equals(tipoAssociacao);
    }

    public enum TipoAssociacao {
        ASSOCIAR_DOCUMENTO("Associar Documento"),
        DESBLOQUEAR_VALOR_LIQUIDADO_GRUPO_DOCUMENTO("Desbloquear Valor Liquidado no Grupo do Documento"),
        GERAR_NUMERO_SITUACAO("Gerar Número/Situação Entrada");
        private String descricao;

        TipoAssociacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }
}
