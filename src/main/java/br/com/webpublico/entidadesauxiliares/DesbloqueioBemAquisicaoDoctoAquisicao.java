package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.DoctoFiscalLiquidacao;
import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.enums.TipoLancamento;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class DesbloqueioBemAquisicaoDoctoAquisicao {

    private DoctoFiscalLiquidacao documentoAquisicao;
    private Boolean documentoDesbloqueado;
    private List<DesbloqueioBemAquisicaoDoctoLiquidacaoMov> movimentosDoctoLiquidacao;
    private List<ItemDoctoFiscalAquisicao> itensDocumento;

    public DesbloqueioBemAquisicaoDoctoAquisicao() {
        itensDocumento = Lists.newArrayList();
        movimentosDoctoLiquidacao = Lists.newArrayList();
        documentoDesbloqueado = false;
    }

    public DoctoFiscalLiquidacao getDocumentoAquisicao() {
        return documentoAquisicao;
    }

    public void setDocumentoAquisicao(DoctoFiscalLiquidacao documentoAquisicao) {
        this.documentoAquisicao = documentoAquisicao;
    }

    public List<ItemDoctoFiscalAquisicao> getItensDocumento() {
        return itensDocumento;
    }

    public void setItensDocumento(List<ItemDoctoFiscalAquisicao> itensDocumento) {
        this.itensDocumento = itensDocumento;
    }


    public List<DesbloqueioBemAquisicaoDoctoLiquidacaoMov> getMovimentosDoctoLiquidacao() {
        return movimentosDoctoLiquidacao;
    }

    public void setMovimentosDoctoLiquidacao(List<DesbloqueioBemAquisicaoDoctoLiquidacaoMov> movimentosDoctoLiquidacao) {
        this.movimentosDoctoLiquidacao = movimentosDoctoLiquidacao;
    }

    public BigDecimal getValorTotalOriginalBens() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemDoctoFiscalAquisicao item : itensDocumento) {
            total = total.add(item.getEstadoBem().getValorOriginal());
        }
        return total;
    }

    public BigDecimal getValorTotalLiquidadoBens() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemDoctoFiscalAquisicao item : itensDocumento) {
            total = total.add(item.getEstadoBem().getValorLiquidado());
        }
        return total;
    }

    public BigDecimal getValorTotalMovimento(CategoriaOrcamentaria categoria, TipoLancamento tipoLancamento) {
        BigDecimal total = BigDecimal.ZERO;
        for (DesbloqueioBemAquisicaoDoctoLiquidacaoMov mov : movimentosDoctoLiquidacao) {
            if (mov.getCategoriaOrcamentaria().equals(categoria) && mov.getTipoLancamento().equals(tipoLancamento)) {
                total = total.add(mov.getValor());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalDocumentoNormal() {
        return getValorTotalMovimento(CategoriaOrcamentaria.NORMAL, TipoLancamento.NORMAL).subtract(getValorTotalMovimento(CategoriaOrcamentaria.NORMAL, TipoLancamento.ESTORNO));
    }

    public BigDecimal getValorTotalDocumentoResto() {
        return getValorTotalMovimento(CategoriaOrcamentaria.RESTO, TipoLancamento.NORMAL).subtract(getValorTotalMovimento(CategoriaOrcamentaria.RESTO, TipoLancamento.ESTORNO));
    }

    public BigDecimal getSaldoDocumento() {
        return documentoAquisicao.getValor().subtract(getValorTotalDocumentoNormal()).subtract(getValorTotalDocumentoResto());
    }

    public Boolean getDocumentoDesbloqueado() {
        return documentoDesbloqueado;
    }

    public void setDocumentoDesbloqueado(Boolean documentoDesbloqueado) {
        this.documentoDesbloqueado = documentoDesbloqueado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DesbloqueioBemAquisicaoDoctoAquisicao that = (DesbloqueioBemAquisicaoDoctoAquisicao) o;
        return Objects.equals(documentoAquisicao, that.documentoAquisicao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documentoAquisicao);
    }
}
