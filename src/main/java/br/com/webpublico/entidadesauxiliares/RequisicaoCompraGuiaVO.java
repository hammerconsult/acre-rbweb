package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.RequisicaoDeCompra;
import com.beust.jcommander.internal.Lists;

import java.math.BigDecimal;
import java.util.List;

public class RequisicaoCompraGuiaVO {

    private RequisicaoDeCompra requisicaoCompra;
    private List<RequisicaoCompraGuiaItemVO> itensRequisicao;
    private List<GuiaDistribuicaoVO> guiasDistribuicao;

    public RequisicaoCompraGuiaVO(RequisicaoDeCompra requisicaoCompra) {
        this.requisicaoCompra = requisicaoCompra;
        this.itensRequisicao = Lists.newArrayList();
        this.guiasDistribuicao = Lists.newArrayList();
    }

    public RequisicaoDeCompra getRequisicaoCompra() {
        return requisicaoCompra;
    }

    public void setRequisicaoCompra(RequisicaoDeCompra requisicaoCompra) {
        this.requisicaoCompra = requisicaoCompra;
    }

    public List<GuiaDistribuicaoVO> getGuiasDistribuicao() {
        return guiasDistribuicao;
    }

    public void setGuiasDistribuicao(List<GuiaDistribuicaoVO> guiasDistribuicao) {
        this.guiasDistribuicao = guiasDistribuicao;
    }

    public List<RequisicaoCompraGuiaItemVO> getItensRequisicao() {
        return itensRequisicao;
    }

    public void setItensRequisicao(List<RequisicaoCompraGuiaItemVO> itensRequisicao) {
        this.itensRequisicao = itensRequisicao;
    }

    public BigDecimal getValorTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (RequisicaoCompraGuiaItemVO item : itensRequisicao) {
            total = total.add(item.getValorTotal());
        }
        return total;
    }
}
