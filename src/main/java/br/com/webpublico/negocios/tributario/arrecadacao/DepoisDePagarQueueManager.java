package br.com.webpublico.negocios.tributario.arrecadacao;

import br.com.webpublico.entidades.*;
import br.com.webpublico.negocios.*;
import br.com.webpublico.nfse.facades.DeclaracaoMensalServicoFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.*;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


@Singleton
public class DepoisDePagarQueueManager {

    private final Logger logger = LoggerFactory.getLogger(DepoisDePagarQueueManager.class);
    @EJB
    private DepoisDePagarQueue depoisDePagarQueue;
    @EJB
    private LoteBaixaFacade loteBaixaFacade;

    @EJB
    private DAMFacade damFacade;
    @Resource
    private SessionContext sessionContext;
    private boolean isProcessing = false;


    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public Future<Void> processNextTask() {
        if (isProcessing) {
            return null;
        }
        DepoisDePagarResquest loteBaixa = depoisDePagarQueue.getNext();
        if (loteBaixa == null) {
            isProcessing = false;
            return null;
        }
        try {
            isProcessing = true;
            run(loteBaixa);
        } finally {
            isProcessing = false;
        }
        sessionContext.getBusinessObject(DepoisDePagarQueueManager.class).processNextTask();
        return null;
    }

    public void run(DepoisDePagarResquest resquest) {
        List<ParcelaValorDivida> parcelas = resquest.getParcelas();
        AssistenteBarraProgresso assistenteArrecadacao = new AssistenteBarraProgresso("Pós pagamento do Lote: " + resquest.getNome(), parcelas.size());
        for (ParcelaValorDivida parcela : parcelas) {
            try {
                Calculo calculo = loteBaixaFacade.recuperarCalculo(parcela.getValorDivida());
                Calculo.TipoCalculo tipo = calculo.getTipoCalculo();
                assistenteArrecadacao.setDescricaoProcesso("Pós pagamento do Lote: " + resquest.getNome() + " [" + tipo.getDescricao() + "::" + calculo.getId() + "]");
                CalculoExecutorDepoisDePagar calculoExecutorDepoisDePagar = (CalculoExecutorDepoisDePagar) Util.getEJBViaLookupJavaModule(tipo.getExecutorDepoisDePagar().getSimpleName());
                calculoExecutorDepoisDePagar.depoisDePagar(calculo);
            } catch (Exception e) {
                Util.loggingError(this.getClass(), "Não efetuou o pós pagamento ", e);
            } finally {
                assistenteArrecadacao.conta();
            }
        }
    }
}
