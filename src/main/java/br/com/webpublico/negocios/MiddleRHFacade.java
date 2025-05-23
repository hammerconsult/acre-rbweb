/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.administracaodepagamento.EventoFPTipoFolha;
import br.com.webpublico.entidades.rh.administracaodepagamento.FichaFinanceiraFPSimulacao;
import br.com.webpublico.entidades.rh.administracaodepagamento.FilaProcessamentoFolha;
import br.com.webpublico.entidades.rh.administracaodepagamento.SituacaoCalculoFP;
import br.com.webpublico.enums.TipoExecucaoEP;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.interfaces.FolhaCalculavel;
import br.com.webpublico.negocios.rh.administracaodepagamento.FilaProcessamentoFolhaFacade;
import br.com.webpublico.negocios.rh.administracaodepagamento.FolhaDePagamentoSimulacaoFacade;
import br.com.webpublico.singletons.SingletonFolhaDePagamento;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * @author Peixe
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class MiddleRHFacade implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(MiddleRHFacade.class);

    @Resource
    SessionContext context;
    @Resource
    private UserTransaction userTransaction;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    private FolhaDePagamentoSimulacaoFacade folhaDePagamentoSimulacaoFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EventoFPFacade eventoFPFacade;
    @EJB
    private SingletonFolhaDePagamento singletonFolhaDePagamento;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private FilaProcessamentoFolhaFacade filaProcessamentoFolhaFacade;

    protected EntityManager getEntityManager() {
        return em;

    }

    private void verificarControleCargoOrLotacaoFuncional(DetalheProcessamentoFolha detalheProcessamentoFolha, EventoFP eventoFP, VinculoFP vinculo) {
        if (eventoFP.getControleCargoLotacao()) {
            boolean temEventoConfigurado = false;

            if (vinculo instanceof ContratoFP) {
                if (vinculo.getCargo() != null) {
                    vinculo = vinculoFPFacade.recuperarComConfiguracaoCargoAndLotacaoFuncional(vinculo.getId());
                }

                List<ConfiguracaoCargoEventoFP> cargos = (vinculo.getCargo().getItemConfiguracaoCargoEventoFP());

                for (ConfiguracaoCargoEventoFP configuracaoCargo : cargos) {
                    if (configuracaoCargo.getEventoFP().equals(eventoFP)) {
                        temEventoConfigurado = true;
                        break;
                    }
                }
            }

            if (!temEventoConfigurado) {
                eventoFP = eventoFPFacade.recuperarComUnidade(eventoFP.getId());
                vinculo = vinculoFPFacade.recuperarComConfiguracaoCargoAndLotacaoFuncional(vinculo.getId());
                for (EventoFPUnidade eventoFPUnidade : eventoFP.getEventoFPUnidade()) {
                    if (eventoFPUnidade.getUnidadeOrganizacional().equals(vinculo.getLotacaoFuncionalVigente().getUnidadeOrganizacional())) {
                        temEventoConfigurado = true;
                    }
                }
            }

            if (!temEventoConfigurado) {
                if (detalheProcessamentoFolha.getEventosBloqueados() == null) {
                    detalheProcessamentoFolha.setEventosBloqueados(Lists.<EventoFP>newArrayList());
                }
                detalheProcessamentoFolha.getEventosBloqueados().add(eventoFP);
            }
        }
    }

    @Asynchronous
//    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Future<DetalheProcessamentoFolha> calculoGeralNovo(FolhaCalculavel folhaCalculando, List<VinculoFP> vinculos, DetalheProcessamentoFolha detalheProcessamentoFolha,
                                                              UsuarioSistema usuario, Map<VinculoFP, FilaProcessamentoFolha> filas, AssistenteBarraProgresso assistente) {
        AsyncResult<DetalheProcessamentoFolha> future = new AsyncResult<>(detalheProcessamentoFolha);
        try {
            boolean mostra = false;
            //System.out.println("Total de Vinculos recuperadas." + vinculos.size());
            List<EventoFP> eventosAutomaticos = Lists.newArrayList();
            List<EventoFP> todosEventos = eventoFPFacade.listaTodosEventosAtivosPorTipoExecucao(TipoExecucaoEP.FOLHA, true);
            eventosAutomaticos = eventoFPFacade.listaEventosAtivosPorTipoExecucao(TipoExecucaoEP.FOLHA, true);
            detalheProcessamentoFolha.setEventosAutomaticos(eventosAutomaticos);
            Map<String, List<EventoFPTipoFolha>> eventosTipoFolha = convertEventosTipoFolhaToMap(eventosAutomaticos);
            detalheProcessamentoFolha.setEventoFPTiposFolha(eventosTipoFolha);
            List<EventoFP> eventosImpostoRenda = eventoFPFacade.findEventosPorIdentificacao(IdentificacaoEventoFP.IMPOSTO_RENDA);
            List<EventoFP> eventosPensaoAlimenticia = eventoFPFacade.findEventosPorIdentificacao(IdentificacaoEventoFP.PENSAO_ALIMENTICIA);
            detalheProcessamentoFolha.setEventosImpostoDeRenda(eventosImpostoRenda);
            detalheProcessamentoFolha.setEventosPensaoAlimenticia(eventosPensaoAlimenticia);
            if (TipoFolhaDePagamento.isFolhaComplementar(folhaCalculando.getTipoFolhaDePagamento())) {
                if (detalheProcessamentoFolha.getEventosBloqueados() == null) {
                    detalheProcessamentoFolha.setEventosBloqueados(Lists.<EventoFP>newArrayList());
                }
                detalheProcessamentoFolha.getEventosBloqueados().addAll(eventosAutomaticos);
            }


            detalheProcessamentoFolha.setAno(folhaCalculando.getAno());
            detalheProcessamentoFolha.setMes(folhaCalculando.getMes().getNumeroMes());
            for (VinculoFP vinculo : vinculos) {
                try {
                    for (EventoFP eventos : todosEventos) {
                        verificarControleCargoOrLotacaoFuncional(detalheProcessamentoFolha, eventos, vinculo);
                    }

                    deleteFichaDaMatricula(folhaCalculando, detalheProcessamentoFolha, vinculo);


                    if (vinculo instanceof ContratoFP) {
                        ValidaDeclaracoes vd = temTodasAsInformacoesDeDeclaracoes((ContratoFP) vinculo);
                        if (vd.isDeclaracoesSemPreenchimento) {
                            gerarItemDetalhamentoErro(folhaCalculando, detalheProcessamentoFolha, vinculo, " não possui as seguintes declaração(ões): " + vd.getDeclaracoesNaoEcontradas());
                            continue;
                        }
                    }
                    folhaDePagamentoFacade.calculoFolhaIndividualNovo(vinculo, folhaCalculando, mostra, detalheProcessamentoFolha, eventosAutomaticos, usuario);
                    mostra = detalheProcessamentoFolha.getContadorCalculoFolha() % 50 == 0;
                    if (detalheProcessamentoFolha.podeCalcular()) {
                        cancelTasks(detalheProcessamentoFolha);
                        liberaSingleton();
                        detalheProcessamentoFolha.finalizaCalculo();
                        return future;
                    }
                    if (context.wasCancelCalled()) {
                        liberaSingleton();
                        break;
                    }

                } catch (Exception e) {
                    logger.error("Erro: ", e);

                    gerarItemDetalhamentoErro(folhaCalculando, detalheProcessamentoFolha, vinculo, " ocorreu uma exception do sistema: " + e.getMessage());
                    logger.error("Possivel caso de duplicar o calculo para: " + vinculo);
                } finally {
                    atualizarRegistroFila(filas, vinculo);
                    detalheProcessamentoFolha.contaCalculo();
                    assistente.conta();
                    detalheProcessamentoFolha.contaContadorGerado();
                    detalheProcessamentoFolha.countContadorTest();
                }
            }


        } catch (Exception e) {
            logger.debug("Erro durante o calculo da folha.." + e);
            detalheProcessamentoFolha.getErros().put(null, "Erro durante o calculo da folha.." + e);
            e.printStackTrace();
        } finally {
            logger.error("Finalizando o calculo da folha");
            if (detalheProcessamentoFolha.getTotalCadastros().intValue() == detalheProcessamentoFolha.getContadorTest()) {
                singletonFolhaDePagamento.setCalculandoFolha(false);
                logger.error("finalizou todos os calculos");
                detalheProcessamentoFolha.finalizaCalculo();
                folhaCalculando.getDetalhesCalculoRHList().add(folhaCalculando.getDetalhesCalculoRHAtual());
                if (!vinculos.isEmpty() && TipoFolhaDePagamento.NORMAL.equals(folhaCalculando.getTipoFolhaDePagamento())) {
                    executarRotinasPosCalculo(folhaCalculando, detalheProcessamentoFolha, usuario, filas, assistente);
                }
                salvarFolha(folhaCalculando);
            }
        }
        return future;
    }

    private void atualizarRegistroFila(Map<VinculoFP, FilaProcessamentoFolha> filas, VinculoFP vinculo) {
        FilaProcessamentoFolha filaProcessamentoFolha = filas.get(vinculo);
        if (filaProcessamentoFolha != null) {
            filaProcessamentoFolhaFacade.updateSituacao(filaProcessamentoFolha, SituacaoCalculoFP.PROCESSADO);
        }
    }

    private Map<String, List<EventoFPTipoFolha>> convertEventosTipoFolhaToMap(List<EventoFP> eventosAutomaticos) {
        Map<String, List<EventoFPTipoFolha>> maps = Maps.newHashMap();
        for (EventoFP eventosAutomatico : eventosAutomaticos) {
            maps.put(eventosAutomatico.getCodigo(), eventosAutomatico.getTiposFolha());
        }
        return maps;
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    private void executarRotinasPosCalculo(FolhaCalculavel folhaCalculando, DetalheProcessamentoFolha detalheProcessamentoFolha, UsuarioSistema usuario, Map<VinculoFP, FilaProcessamentoFolha> filas, AssistenteBarraProgresso assistente) {
        if (singletonFolhaDePagamento.getContador() > 0) {
            logger.info("Resetando contador..");
            singletonFolhaDePagamento.setContador(0);
            return;
        }

        List<VinculoFP> vinculoFPS = fichaFinanceiraFPFacade.buscarServidoresDuplicados(folhaCalculando.getMes(), folhaCalculando.getAno());
        if (!vinculoFPS.isEmpty()) {
            logger.info("Detectado problema de duplicidade de pagamento, executando rotinas de contenção");
            folhaDePagamentoFacade.iniciarProcessamento(null, folhaCalculando, vinculoFPS, detalheProcessamentoFolha, new Date(), usuario, filas, assistente);
            folhaDePagamentoFacade.inserirLogServidoresDuplicados(folhaCalculando, vinculoFPS, detalheProcessamentoFolha);
            singletonFolhaDePagamento.contarDuplicidade();
        }
    }

    private void gerarItemDetalhamentoErro(FolhaCalculavel folhaCalculando, DetalheProcessamentoFolha detalheProcessamentoFolha, VinculoFP vinculo, String mensagem) {
        ItensDetalhesErrosCalculo i = new ItensDetalhesErrosCalculo();
        i.setDetalhesCalculoRH(folhaCalculando.getDetalhesCalculoRHAtual());
        i.setVinculoFP(vinculo);
        i.setErros(Util.formatterDataHora.format(new Date()) + " O servidor " + vinculo + mensagem);
        folhaCalculando.getDetalhesCalculoRHAtual().getItensDetalhesErrosCalculos().add(i);
        detalheProcessamentoFolha.getDetalhesCalculoRH().getItensDetalhesErrosCalculos().add(i);
        detalheProcessamentoFolha.setContadorNaoGerados(detalheProcessamentoFolha.getContadorNaoGerados() + 1);
    }

    private void salvarFolha(FolhaCalculavel folhaCalculando) {
        folhaDePagamentoFacade.salvarNovaFolha(folhaCalculando);
    }

    public void liberaSingleton() {
        singletonFolhaDePagamento.setCalculandoFolha(false);
    }


    private void deleteFichaDaMatricula(FolhaCalculavel folhaCalculando, DetalheProcessamentoFolha detalheProcessamentoFolha, VinculoFP vinculo) {
        if (!folhaCalculando.isSimulacao()) {
            List<FichaFinanceiraFP> fichaFinanceiraFP = null;
            fichaFinanceiraFP = folhaDePagamentoFacade.retornaFichaFinanceiraPorVinculo(folhaCalculando.getMes(), folhaCalculando.getAno(), folhaCalculando.getTipoFolhaDePagamento(), vinculo);

            if (fichaFinanceiraFP != null) {
                if (fichaFinanceiraFP.isEmpty()) {
                    vinculo.getMatriculaFP().getPessoa().setFichaJaExcluidas(true);
                }
                for (FichaFinanceiraFP ficha : fichaFinanceiraFP) {
                    if (ficha.getCreditoSalarioPago() || ficha.getFolhaDePagamento().folhaEfetivada()) {
                        continue;
                    }
                    apagarMemoriaCalculo(ficha);
                    fichaFinanceiraFPFacade.remover(ficha);
                    vinculo.getMatriculaFP().getPessoa().setFichaJaExcluidas(true);
                }
            }
        } else {
            verificarAndApagarFichaSimulacao(folhaCalculando, vinculo);
        }
    }

    private void apagarMemoriaCalculo(FichaFinanceiraFP ficha) {
        fichaFinanceiraFPFacade.apagarMemoriaCalculo(ficha);
    }

    private void verificarAndApagarFichaSimulacao(FolhaCalculavel folhaCalculando, VinculoFP vinculo) {
        List<FichaFinanceiraFPSimulacao> fichaFinanceiraFP = folhaDePagamentoSimulacaoFacade.buscarFichaFinanceiraSimulacaPorVinculo(folhaCalculando.getMes(), folhaCalculando.getAno(), folhaCalculando.getTipoFolhaDePagamento(), vinculo);
        if (fichaFinanceiraFP != null) {
            if (fichaFinanceiraFP.isEmpty()) {
                vinculo.getMatriculaFP().getPessoa().setFichaJaExcluidas(true);
            }
            for (FichaFinanceiraFPSimulacao ficha : fichaFinanceiraFP) {
                if (ficha.getFolhaDePagamentoSimulacao().folhaEfetivada()) {
                    continue;
                }
                folhaDePagamentoSimulacaoFacade.removerFichaFinanceira(ficha);
                vinculo.getMatriculaFP().getPessoa().setFichaJaExcluidas(true);
            }
        }
    }

    private void cancelTasks(DetalheProcessamentoFolha detalhe) {
        if (detalhe.getTarefa1() != null) detalhe.getTarefa1().cancel(true);
        if (detalhe.getTarefa2() != null) detalhe.getTarefa2().cancel(true);
        if (detalhe.getTarefa3() != null) detalhe.getTarefa3().cancel(true);
        if (detalhe.getTarefa4() != null) detalhe.getTarefa4().cancel(true);
        if (detalhe.getTarefa5() != null) detalhe.getTarefa5().cancel(true);
    }


    public ValidaDeclaracoes temTodasAsInformacoesDeDeclaracoes(ContratoFP contratoFP) {
        ValidaDeclaracoes vd = new ValidaDeclaracoes();
        String declaracoesNaoEcontradas = "";

        if (!contratoFP.temCategoriaSEFIP()) {
            vd.addMensagem(" Atenção, informar a categoria SEFIP. \n\r");
            vd.temErros();
        }

        if (!contratoFP.temTipoAdmissaoFGTS()) {
            vd.addMensagem("Atenção, informar o tipo de Admissão FGTS. \n\r");
            vd.temErros();
        }

        if (!contratoFP.temTipoAdmissaoSEFIP()) {
            vd.addMensagem("Atenção, informar o tipo de Admissão SEFIP. \n\r");
            vd.temErros();
        }

        if (!contratoFP.temTipoAdmissaoRAIS()) {
            vd.addMensagem("Atenção, informar o tipo de Admissão RAIS. \n\r");
            vd.temErros();
        }

        if (!contratoFP.temMovimentoCAGED()) {
            vd.addMensagem("Atenção, informar o movimento CAGED. \n\r");
            vd.temErros();
        }

        if (!contratoFP.temTipoAdmissaoRAIS()) {
            vd.addMensagem("Atenção, informar o tipo de Admissão RAIS. \n\r");
            vd.temErros();
        }

        if (!contratoFP.temTipoOcorrenciaSEFIP()) {
            vd.addMensagem("Atenção, informar o tipo de Ocorrência SEFIP. \n\r");
            vd.temErros();
        }

        if (!contratoFP.temRetencaoIRRF()) {
            vd.addMensagem("Atenção, informar a retenção SEFIP. \n\r");
            vd.temErros();
        }

        if (!contratoFP.temVinculoEmpregaticio()) {
            vd.addMensagem("Atenção, informar o Vínculo Empregatício. \n\r");
            vd.temErros();
        }
        if (!contratoFP.temNaturezaRendimentoDirf()) {
            vd.addMensagem("Atenção, informe Natureza de Rendimento DIRF. \n\r");
            vd.temErros();
        }
        return vd;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void salvarNovoCalculo(Dirf dirf) {
        try {
            //System.out.println("=====  VAI SALVAR ====");
            userTransaction = context.getUserTransaction();
            userTransaction.begin();
            em.persist(dirf);
            //System.out.println("=====  SALVOU ====");
            em.flush();
            userTransaction.commit();
            //System.out.println("=====  VOLTOU ====");
        } catch (Exception e) {
            e.printStackTrace();
            context.setRollbackOnly();
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void salvarNovo(Object obj) {
        try {
            //System.out.println("=====  VAI SALVAR ====");
            userTransaction = context.getUserTransaction();
            userTransaction.begin();
            em.persist(obj);
            //System.out.println("=====  SALVOU ====");
            em.flush();
            userTransaction.commit();
            //System.out.println("=====  VOLTOU ====");
        } catch (Exception e) {
            e.printStackTrace();
            context.setRollbackOnly();
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void salvar(Object obj) {
        try {
            Util.imprime("=====  VAI SALVAR ====");
            userTransaction = context.getUserTransaction();
            userTransaction.begin();
            em.merge(obj);
            Util.imprime("=====  SALVOU ====");
            em.flush();
            userTransaction.commit();
            Util.imprime("=====  COMMIT ====");
        } catch (Exception e) {
            e.printStackTrace();
            context.setRollbackOnly();
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void salvar(Dirf dirf) {
        try {
            Util.imprime("=====  VAI SALVAR ====");
            userTransaction = context.getUserTransaction();
            userTransaction.begin();
            em.merge(dirf);
            Util.imprime("=====  SALVOU ====");
            em.flush();
            userTransaction.commit();
            Util.imprime("=====  COMMIT ====");
        } catch (Exception e) {
            e.printStackTrace();
            context.setRollbackOnly();
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void salvarNovoTransactional(Object object) {
        try {
            userTransaction = context.getUserTransaction();
            userTransaction.begin();
            em.persist(object);
            em.flush();
            userTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            context.setRollbackOnly();
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void salvarTransactional(Object object) {
        try {
            userTransaction = context.getUserTransaction();
            userTransaction.begin();
            em.merge(object);
            em.flush();
            userTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            context.setRollbackOnly();
        }
    }

    public class ValidaDeclaracoes {
        private String declaracoesNaoEcontradas = "";
        private boolean isDeclaracoesSemPreenchimento = false;

        public String getDeclaracoesNaoEcontradas() {
            return declaracoesNaoEcontradas;
        }

        public void setDeclaracoesNaoEcontradas(String declaracoesNaoEcontradas) {
            this.declaracoesNaoEcontradas = declaracoesNaoEcontradas;
        }

        public boolean isDeclaracoesSemPreenchimento() {
            return isDeclaracoesSemPreenchimento;
        }

        public void setDeclaracoesSemPreenchimento(boolean declaracoesSemPreenchimento) {
            isDeclaracoesSemPreenchimento = declaracoesSemPreenchimento;
        }

        public void temErros() {
            isDeclaracoesSemPreenchimento = true;
        }

        public void addMensagem(String msg) {
            declaracoesNaoEcontradas += msg;
        }
    }
}
