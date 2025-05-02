package br.com.webpublico.negocios.contabil.assync;

import br.com.webpublico.entidades.PagamentoExtra;
import br.com.webpublico.entidadesauxiliares.contabil.BarraProgressoAssistente;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.PagamentoExtraFacade;
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
 * Created by renato on 29/06/2017
 */
@Stateless
public class DeferirPagamentoExtraFacade implements Serializable {

    @EJB
    private PagamentoExtraFacade pagamentoExtraFacade;

    public PagamentoExtraFacade getPagamentoExtraFacade() {
        return pagamentoExtraFacade;
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public void deferirPagamentos(Date data, List<PagamentoExtra> pagamentos, BarraProgressoAssistente barraProgressoItens) {
        barraProgressoItens.inicializa();
        barraProgressoItens.setTotal(pagamentos.size());
        barraProgressoItens.setMensagem("Total de despesa extra ..: "+pagamentos.size());
        for (PagamentoExtra pag : pagamentos) {
            try {
                pag = deferirPagamento(data, pag);
                barraProgressoItens.setProcessados(barraProgressoItens.getProcessados() + 1);
                barraProgressoItens.addMensagem("<div style='font-size: 12px'>Deferido a despesa extra : " + pag + ".</font></div></br>");
                pagamentoExtraFacade.desbloquearDeferir(pag);
            } catch (ExcecaoNegocioGenerica ex) {
                barraProgressoItens.addMensagemErro("<div style='font-size: 12px'>Despesa extra : " + pag + ". Erro(s): " + "<font style='color: red;'> " + ex.getMessage() + "</font></div></br>");
                pagamentoExtraFacade.desbloquearDeferir(pag);
                continue;
            } catch (ValidacaoException ex) {
                for (FacesMessage facesMessage : ex.getAllMensagens()) {
                    barraProgressoItens.addMensagemErro("<div style='font-size: 12px'>Despesa extra : " + pag + ". Erro(s): " + "<font style='color: red;'> " +facesMessage.getDetail() + "</font></div></br>");
                }
                continue;
            } catch (Exception e) {
                barraProgressoItens.addMensagemErro(e.getMessage());
                pagamentoExtraFacade.desbloquearDeferir(pag);
                continue;
            }
        }
        barraProgressoItens.finaliza();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private PagamentoExtra deferirPagamento(Date data, PagamentoExtra pag) {
        pag = pagamentoExtraFacade.recuperar(pag.getId());
        pag.setDataPagto(data);
        pagamentoExtraFacade.deferirPagamento(pag);
        return pag;
    }
}
