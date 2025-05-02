package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.negocios.*;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
public class VerExecucaoOrcPorEmpenhoControlador implements Serializable {
    @EJB
    private EmpenhoEstornoFacade empenhoEstornoFacade;
    @EJB
    private LiquidacaoFacade liquidacaoFacade;
    @EJB
    private LiquidacaoEstornoFacade liquidacaoEstornoFacade;
    @EJB
    private PagamentoFacade pagamentoFacade;
    @EJB
    private PagamentoEstornoFacade pagamentoEstornoFacade;

    public List<Empenho> getEmpenho(Empenho empenho) {
        return Lists.newArrayList(empenho);
    }

    public List<EmpenhoEstorno> getEstornosDeEmpenhos(Empenho empenho) {
        return empenhoEstornoFacade.buscarEstornoEmpenho(empenho);
    }

    public List<Liquidacao> getLiquidacoes(Empenho empenho) {
        return liquidacaoFacade.buscarPorEmpenho(empenho);
    }

    public List<LiquidacaoEstorno> getEstornosDeLiquidacoes(Empenho empenho) {
        return liquidacaoEstornoFacade.listaPorEmpenho(empenho);
    }

    public List<Pagamento> getPagamentos(Empenho empenho) {
        return pagamentoFacade.listaPorEmpenho(empenho);
    }

    public List<PagamentoEstorno> getEstornosDePagamentos(Empenho empenho) {
        return pagamentoEstornoFacade.listaPorEmpenho(empenho);
    }
}
