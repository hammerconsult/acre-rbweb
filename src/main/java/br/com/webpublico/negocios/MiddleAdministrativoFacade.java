/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.EfetivacaoBaixaPatrimonial;
import br.com.webpublico.entidadesauxiliares.AssistenteContabilizacaoBaixa;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import java.io.Serializable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author Edi
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class MiddleAdministrativoFacade implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(MiddleAdministrativoFacade.class);

    @Resource
    SessionContext context;
    @Resource
    private UserTransaction userTransaction;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EfetivacaoBaixaPatrimonialFacade efetivacaoBaixaPatrimonialFacade;

    protected EntityManager getEntityManager() {
        return em;
    }


    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<EfetivacaoBaixaPatrimonial> simularContabilizacao(EfetivacaoBaixaPatrimonial selecionado,
                                                                    AssistenteContabilizacaoBaixa assistenteContabilizar,
                                                                    AssistenteMovimentacaoBens assistente) {
        try {
            efetivacaoBaixaPatrimonialFacade.deletarLogErro(selecionado);
            assistente.setSimularContabilizacao(Boolean.TRUE);
            assistente.setSelecionado(selecionado);

            efetivacaoBaixaPatrimonialFacade.popularEventosParaContabilzacao(selecionado, assistenteContabilizar, assistente);
            efetivacaoBaixaPatrimonialFacade.contabilizarTodosProcessoEventoBem(selecionado, assistente, assistenteContabilizar);
            efetivacaoBaixaPatrimonialFacade.salvarLogErro(assistente.getLogErrosEfetivacaoBaixa());

            assistente.setSimularContabilizacao(Boolean.FALSE);
            assistente.zerarContadoresProcesso();
            assistente.setDescricaoProcesso("Finalizando Processo...");
            assistente.setSelecionado(selecionado);
            assistente.setDescricaoProcesso(" ");
        } catch (Exception ex) {
            logger.error("Erro ao simular efetivação de baixa de bens. " + ex.getMessage());
            throw ex;
        }
        return new AsyncResult<>(selecionado);
    }
}
