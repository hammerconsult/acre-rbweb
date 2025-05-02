package br.com.webpublico.negocios.contabil.assync;

import br.com.webpublico.entidades.Pagamento;
import br.com.webpublico.entidadesauxiliares.contabil.BarraProgressoAssistente;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.PagamentoFacade;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.*;
import javax.faces.application.FacesMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by renat on 08/11/2016.
 */
@Stateless
public class DeferirPagamentoFacade implements Serializable {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private PagamentoFacade pagamentoFacade;

    public PagamentoFacade getPagamentoFacade() {
        return pagamentoFacade;
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public void deferirPagamentos(Date data, List<Pagamento> pagamentos, BarraProgressoAssistente barraProgressoItens) {
        barraProgressoItens.inicializa();
        barraProgressoItens.setTotal(pagamentos.size());
        barraProgressoItens.setMensagem("Total de pagamentos..: "+pagamentos.size());
        for (Pagamento pag : pagamentos) {
            try {
                pag = deferirPagamento(data, pag);
                barraProgressoItens.setProcessados(barraProgressoItens.getProcessados() + 1);
                barraProgressoItens.addMensagem("<div style='font-size: 12px'>Deferido o pagamento: " + pag + ".</font></div></br>");
                pagamentoFacade.desbloquearPagamento(pag);
            } catch (ExcecaoNegocioGenerica ex) {
                barraProgressoItens.addMensagemErro("<div style='font-size: 12px'>Pagamento: " + pag + ". Erro(s): " + "<font style='color: red;'> " + ex.getMessage() + "</font></div></br>");
                pagamentoFacade.desbloquearPagamento(pag);
                continue;
            } catch (ValidacaoException ex) {
                for (FacesMessage facesMessage : ex.getAllMensagens()) {
                    barraProgressoItens.addMensagemErro("<div style='font-size: 12px'>Pagamento: " + pag + ". Erro(s): " + "<font style='color: red;'> " +facesMessage.getDetail() + "</font></div></br>");
                }
                continue;
            } catch (Exception e) {
                barraProgressoItens.addMensagemErro(e.getMessage());
                pagamentoFacade.desbloquearPagamento(pag);
                continue;
            }
        }
        barraProgressoItens.finaliza();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private Pagamento deferirPagamento(Date data, Pagamento pag) {
        pag = pagamentoFacade.recuperar(pag.getId());
        pagamentoFacade.deferirPagamento(pag, data);
        return pag;
    }
}
