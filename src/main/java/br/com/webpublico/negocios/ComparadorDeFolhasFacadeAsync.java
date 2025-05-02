/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ObjetoPesquisa;
import br.com.webpublico.entidadesauxiliares.rh.RelatorioRH;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoEventoFP;
import br.com.webpublico.util.Util;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author Peixe
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ComparadorDeFolhasFacadeAsync implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ComparadorDeFolhasFacadeAsync.class);

    @Resource
    SessionContext context;
    @Resource
    private UserTransaction userTransaction;
    @EJB
    private ComparadorDeFollhasFacade comparadorDeFollhasFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    private RecursoDoVinculoFPFacade recursoDoVinculoFPFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private LotacaoFuncionalFacade lotacaoFuncionalFacade;

    protected EntityManager getEntityManager() {
        return em;
    }

    @Asynchronous
    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public Future<RelatorioRH> contabilizarVinculos(ObjetoPesquisa objetoPesquisa) {
        logger.debug("Iniciando processo.");
        /*Map<EventoFP, Map<TipoEventoFP, BigDecimal>> valores = Maps.newHashMap();
        AsyncResult<Map<EventoFP, Map<TipoEventoFP, BigDecimal>>> future = new AsyncResult<>(valores);*/
        RelatorioRH relatorio = new RelatorioRH();
        AsyncResult<RelatorioRH> future = new AsyncResult<>(relatorio);
        try {
            DateTime dataReferencia = new DateTime(Util.criaDataUltimoDiaDoMesFP(Mes.getMesToInt(objetoPesquisa.getMes()).getNumeroMesIniciandoEmZero(), objetoPesquisa.getAno()));
            List<VinculoFP> vinculos = folhaDePagamentoFacade.recuperaVinculosPorTipoFolhaMesEAno(Mes.getMesToInt(objetoPesquisa.getMes()), objetoPesquisa.getAno(), objetoPesquisa.getTipoFolhaDePagamentoWeb());
            relatorio.setVinculos(comparadorDeFollhasFacade.classificarVinculos(vinculos, dataReferencia, relatorio.getVinculosValor(), objetoPesquisa));
            logger.debug("iniciando recuperação de valores...");
            comparadorDeFollhasFacade.classificarERecuperarValores(objetoPesquisa, relatorio.getValores(), relatorio.getVinculos());

            /*for (Map.Entry<EventoFP, Map<TipoEventoFP, BigDecimal>> eventoFPMapEntry : valores.entrySet()) {
                logger.debug("Evento: {} ", eventoFPMapEntry.getKey());
                for (Map.Entry<TipoEventoFP, BigDecimal> tipoValor : eventoFPMapEntry.getValue().entrySet()) {
                    logger.debug("Tipo: {} Valor: {}", tipoValor.getKey(), tipoValor.getValue());
                }
            }*/
            imprimirOrganizado(relatorio.getValores());
            return future;
        } catch (Exception e) {
            logger.debug("Erro durante o calculo da folha.." + e);
        }
        return future;
    }

    private void imprimirOrganizado(Map<EventoFP, Map<TipoEventoFP, BigDecimal>> valores) {
        BigDecimal totalVantagem = BigDecimal.ZERO;
        BigDecimal totalDesconto = BigDecimal.ZERO;
        for (Map.Entry<EventoFP, Map<TipoEventoFP, BigDecimal>> eventoFPMapEntry : valores.entrySet()) {
            for (Map.Entry<TipoEventoFP, BigDecimal> tipoValor : eventoFPMapEntry.getValue().entrySet()) {
                logger.debug(eventoFPMapEntry.getKey() + " Tipo: {} Valor: {}", tipoValor.getKey(), tipoValor.getValue());
                if (TipoEventoFP.VANTAGEM.equals(tipoValor.getKey())) {
                    totalVantagem = totalVantagem.add(tipoValor.getValue());
                }
                if (TipoEventoFP.DESCONTO.equals(tipoValor.getKey())) {
                    totalDesconto = totalDesconto.add(tipoValor.getValue());
                }
            }
        }
        logger.debug(" Total de Vantagem: {}", totalVantagem);
        logger.debug(" Total de Desconto: {}", totalDesconto);

    }

    @Asynchronous
    @TransactionTimeout(value = 2, unit = TimeUnit.HOURS)
    public void definirRecursoFPEmFichaFinaceiraVazias() {
        logger.debug("Iniciando a busca por fichas sem Recurso FP Preenchido ");
        List<BigDecimal> fichasId = fichaFinanceiraFPFacade.buscarFichasSemRecursoFP();
        if (fichasId != null && !fichasId.isEmpty()) {
            int total = fichasId.size();
            int contador = 0;
            for (BigDecimal idFicha : fichasId) {
                FichaFinanceiraFP ficha = fichaFinanceiraFPFacade.recuperarFichaSemItemFicha(idFicha.longValue());
                if (contador % 100 == 0) {
                    logger.debug("Total de Registros Processados : {} percental {}%", contador, (total / (contador * 100)));
                }
                RecursoFP recursoFP = recursoDoVinculoFPFacade.buscarRecursoDoVinculoPorVinculoAndReferencia(ficha.getVinculoFP(), ficha.getFolhaDePagamento().getCalculadaEm());
                if (recursoFP != null) {
                    ficha.setRecursoFP(recursoFP);
                    em.merge(ficha);
                }
                contador++;
            }
        }
        logger.debug("Finalizou o processo de definição de RecursoFP nas Fichas Financeiras");
    }

    @Asynchronous
    @TransactionTimeout(value = 2, unit = TimeUnit.HOURS)
    public void definirLotacaoFuncionalEmFichaFinaceiraVazias() {
        logger.debug("Iniciando a busca por fichas sem Lotacao  Preenchido ");
        try {
            List<BigDecimal> fichasId = fichaFinanceiraFPFacade.buscarFichasSemLotacao();
            if (fichasId != null && !fichasId.isEmpty()) {
                int total = fichasId.size();
                int contador = 1;
                for (BigDecimal idFicha : fichasId) {
                    FichaFinanceiraFP ficha = fichaFinanceiraFPFacade.recuperarFichaSemItemFicha(idFicha.longValue());
                    if (contador % 100 == 0) {
                        logger.debug("Total de Registros Processados : {} percental {}%", contador, (total / (contador * 100)));
                    }
                    if (ficha.getUnidadeOrganizacional() == null) {
                        LotacaoFuncional lotacaoFuncional = lotacaoFuncionalFacade.buscarLotacaoFuncionalVigente(ficha.getVinculoFP(), ficha.getFolhaDePagamento().getCalculadaEm());

                        if (lotacaoFuncional != null) {
                            ficha.setUnidadeOrganizacional(lotacaoFuncional.getUnidadeOrganizacional());
                            fichaFinanceiraFPFacade.salvar(ficha);
                        }
                    }
                    contador++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.debug("Finalizou o processo de definição de Lotação Funcional nas Fichas Financeiras");
    }

}
