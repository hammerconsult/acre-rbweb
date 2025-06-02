package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteContabilizacaoBaixa;
import br.com.webpublico.entidadesauxiliares.VOItemBaixaPatrimonial;
import br.com.webpublico.enums.*;
import br.com.webpublico.negocios.tributario.singletons.SingletonConcorrenciaPatrimonio;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 11/06/14
 * Time: 08:56
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class EfetivacaoBaixaPatrimonialFacade extends AbstractFacade<EfetivacaoBaixaPatrimonial> {

    private static BigDecimal UMNEGATIVO = new BigDecimal(-1);
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ParecerBaixaPatrimonialFacade parecerBaixaPatrimonialFacade;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private IntegradorPatrimonialContabilFacade integradorPatrimonialContabilFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private LeilaoAlienacaoFacade leilaoAlienacaoFacade;
    @EJB
    private SolicitacaoAlienacaoFacade solicitacaoAlienacaoFacade;
    @EJB
    private SolicitacaoBaixaPatrimonialFacade solicitacaoBaixaPatrimonialFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private MiddleAdministrativoFacade middleAdministrativoFacade;
    @EJB
    private ConfigMovimentacaoBemFacade configMovimentacaoBemFacade;
    @EJB
    private SingletonConcorrenciaPatrimonio singletonBloqueioPatrimonio;
    @EJB
    private LeilaoAlienacaoFacade getLeilaoAlienacaoFacade;

    public EfetivacaoBaixaPatrimonialFacade() {
        super(EfetivacaoBaixaPatrimonial.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public EfetivacaoBaixaPatrimonial recuperar(Object id) {
        EfetivacaoBaixaPatrimonial efetivacaoBaixaPatrimonial = super.recuperar(id);
        efetivacaoBaixaPatrimonial.setListaItemEfetivacao(bemFacade.recuperarBem(efetivacaoBaixaPatrimonial.getListaItemEfetivacao()));
        if (efetivacaoBaixaPatrimonial.getDetentorArquivoComposicao() != null) {
            efetivacaoBaixaPatrimonial.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        try {
            efetivacaoBaixaPatrimonial.getListaLoteAlienacao().size();
            for (EfetivacaoBaixaLote ef : efetivacaoBaixaPatrimonial.getListaLoteAlienacao()) {
                ef.getLeilaoAlienacaoLote().getBensDoLoteDaAlienacao().size();
                ef.getLeilaoAlienacaoLote().getDetentDoctoFiscalLiquidacao().getDoctoLista().size();
            }
        } catch (Exception e) {
            logger.debug("Detalhes {}", e);
        }
        return efetivacaoBaixaPatrimonial;
    }

    public EfetivacaoBaixaPatrimonial recuperarComDependenciasArquivo(Object id) {
        EfetivacaoBaixaPatrimonial entity = super.recuperar(id);
        if (entity.getDetentorArquivoComposicao() != null) {
            entity.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        entity.getListaLoteAlienacao().size();
        return entity;
    }

    public Future<EfetivacaoBaixaPatrimonial> simularContabilizacao(EfetivacaoBaixaPatrimonial selecionado,
                                                                    AssistenteContabilizacaoBaixa assistenteSimulacao,
                                                                    AssistenteMovimentacaoBens assistente) {

        return middleAdministrativoFacade.simularContabilizacao(selecionado, assistenteSimulacao, assistente);
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<EfetivacaoBaixaPatrimonial> salvarEfetivacaoBaixa(EfetivacaoBaixaPatrimonial selecionado, List<VOItemBaixaPatrimonial> bensParaEfetivar,
                                                                    AssistenteMovimentacaoBens assistente, AssistenteContabilizacaoBaixa assistenteContabilizacao,
                                                                    List<EfetivacaoBaixaLote> lotesEfetivacao) {
        try {
            selecionado.setSituacao(SituacaoBaixaPatrimonial.AGUARDANDO_EFETIVACAO);
            selecionado = salvarEfetivacao(selecionado);
            assistente.setSimularContabilizacao(Boolean.TRUE);
            assistente.setSelecionado(selecionado);

            salvarLoteEfetivacaoBaixa(selecionado, lotesEfetivacao);
            apurarValorLiquidoContabilDoBem(selecionado, assistente, bensParaEfetivar, assistenteContabilizacao);
            if (selecionado.isSolicitacaoBaixaPorAlienacao()) {
                registrarGanhoPerdaBem(selecionado, assistente, bensParaEfetivar, assistenteContabilizacao);
            }
            criarItemEfetivacaoBaixa(selecionado, assistente, bensParaEfetivar, assistenteContabilizacao);

            if (selecionado.isSolicitacaoBaixaPorAlienacao()) {
                List<Long> idsLotes = getIdsLoteAlienacao(bensParaEfetivar);
                assistenteContabilizacao.getItensAlienacao().addAll(leilaoAlienacaoFacade.buscarItemLeilaoAlienacao(idsLotes, assistente));
            }

            bloquearBens(selecionado, bensParaEfetivar, assistente);

            assistente.zerarContadoresProcesso();
            assistente.setDescricaoProcesso("Finalizando Processo...");
            assistente.setSelecionado(selecionado);
            assistente.setDescricaoProcesso(" ");
        } catch (Exception ex) {
            logger.error("Erro ao salvar efetivação de baixa de bens. {}", ex);
            throw ex;
        }
        return new AsyncResult<>(selecionado);
    }

    private void bloquearBens(EfetivacaoBaixaPatrimonial selecionado, List<VOItemBaixaPatrimonial> bensParaEfetivar, AssistenteMovimentacaoBens assistente) {
        List<Number> bens = Lists.newArrayList();
        if (selecionado.isSolicitacaoBaixaPorAlienacao()) {
            for (VOItemBaixaPatrimonial item : bensParaEfetivar) {
                for (VOItemBaixaPatrimonial bem : item.getBensAgrupados()) {
                    bens.add(bem.getIdBem());
                }
            }
        } else {
            for (VOItemBaixaPatrimonial item : bensParaEfetivar) {
                bens.add(item.getBemEfetivacao().getId());
            }
        }
        configMovimentacaoBemFacade.bloquearBensPorSituacao(assistente.getConfigMovimentacaoBem(), bens, SituacaoEventoBem.AGUARDANDO_EFETIVACAO);
    }

    public void salvarSolicitacaoBaixaControleVersao(EfetivacaoBaixaPatrimonial selecionado) {
        try {
            SolicitacaoBaixaPatrimonial solicitacaoBaixa = selecionado.getParecerBaixaPatrimonial().getSolicitacaoBaixa();
            solicitacaoBaixa.setDataVersao(new Date());
            em.merge(solicitacaoBaixa);
        } catch (Exception ex) {
            logger.error("Erro ao salvar solicitação de baixa de bens. {}", ex);
            throw ex;
        }
    }

    private void salvarLoteEfetivacaoBaixa(EfetivacaoBaixaPatrimonial selecionado, List<EfetivacaoBaixaLote> lotesEfetivacao) {
        for (EfetivacaoBaixaLote loteEfetivacao : lotesEfetivacao) {
            loteEfetivacao.setEfetivacaoBaixaPatrimonial(selecionado);
            em.merge(loteEfetivacao);
        }
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<EfetivacaoBaixaPatrimonial> concluirEfetivacaoBaixa(EfetivacaoBaixaPatrimonial selecionado,
                                                                      AssistenteMovimentacaoBens assistente,
                                                                      AssistenteContabilizacaoBaixa assistenteContabilizar) {
        try {
            assistente.setDescricaoProcesso("Concluíndo Efetivação da Baixa de Bens " + selecionado.getTipoBem().getDescricao() + "...");
            selecionado = salvarEfetivacao(selecionado);
            assistente.setSimularContabilizacao(Boolean.FALSE);

            popularEventosParaContabilzacao(selecionado, assistenteContabilizar, assistente);

            contabilizarTodosProcessoEventoBem(selecionado, assistente, assistenteContabilizar);
            concluirTodosProcessoEventoBem(assistenteContabilizar, assistente);
            if (TipoBem.MOVEIS.equals(selecionado.getTipoBem())) {
                desbloquearBens(assistenteContabilizar, assistente.getConfigMovimentacaoBem());
            }
            finalizarSolicitacaoBaixa(selecionado);

            if (selecionado.getCodigo() == null) {
                selecionado.setCodigo(singletonGeradorCodigo.getProximoCodigo(EfetivacaoBaixaPatrimonial.class, "codigo"));
            }
            selecionado.setSituacao(SituacaoBaixaPatrimonial.FINALIZADA);
            selecionado = salvarEfetivacao(selecionado);

            assistente.zerarContadoresProcesso();
            assistente.setDescricaoProcesso("Finalizando Processo...");
            assistente.setSelecionado(selecionado);
            assistente.setDescricaoProcesso(" ");
        } catch (Exception ex) {
            String erro = "Ocorreu um erro durante a Conclusão da efetivação de baixa: " + ex.getMessage();
            assistente.getMensagens().add(erro);
            logger.error("Erro ao concluir efetivação de baixa de bens. {}", ex);
            throw ex;
        }
        return new AsyncResult<>(selecionado);
    }

    private void desbloquearBens(AssistenteContabilizacaoBaixa assistenteContabilizar, ConfigMovimentacaoBem configuracao) {
        List<Number> bensDesbloqueio = Lists.newArrayList();
        for (ItemEfetivacaoBaixaPatrimonial itemBaixa : assistenteContabilizar.getItensEfetivacaoBaixa()) {
            bensDesbloqueio.add(itemBaixa.getBem().getId());
        }
        if (!bensDesbloqueio.isEmpty()) {
            configMovimentacaoBemFacade.desbloquearBensEfetivacaoBaixa(configuracao, bensDesbloqueio);
        }
    }

    private void finalizarSolicitacaoBaixa(EfetivacaoBaixaPatrimonial selecionado) {
        SolicitacaoBaixaPatrimonial solicitacaoBaixa = selecionado.getParecerBaixaPatrimonial().getSolicitacaoBaixa();
        solicitacaoBaixa.setSituacao(SituacaoBaixaPatrimonial.FINALIZADA);
        em.merge(solicitacaoBaixa);
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public void contabilizarTodosProcessoEventoBem(EfetivacaoBaixaPatrimonial selecionado, AssistenteMovimentacaoBens assistente, AssistenteContabilizacaoBaixa assistenteContabilizar) {
        try {
            contabilizarApuracaoValorLiquidoContabil(selecionado, assistente, assistenteContabilizar);
            if (selecionado.isSolicitacaoBaixaPorAlienacao()) {
                contabilzarRegistroGanhosPerdas(selecionado, assistente, assistenteContabilizar.getRegistroGanhosPatrimonial(), assistenteContabilizar.getRegistroPerdasPatrimonial());
                contabilizarAlienacao(assistente, assistenteContabilizar.getItensAlienacao());
            } else {
                contabilizarDesincorporacao(selecionado, assistente, assistenteContabilizar.getItensEfetivacaoBaixa());
            }
        } catch (Exception ex) {
            logger.error("contabilizarTodosProcessoEventoBem {}", ex);
            throw ex;
        }
    }

    private void ordenarBensMoveis(List<BensMoveis> bensMoveis) {
        Collections.sort(bensMoveis, new Comparator<BensMoveis>() {
            @Override
            public int compare(BensMoveis o1, BensMoveis o2) {
                return o1.getDataBensMoveis().compareTo(o2.getDataBensMoveis());
            }
        });
    }

    private void ordenarBensImoveis(List<BensImoveis> bensMoveis) {
        Collections.sort(bensMoveis, new Comparator<BensImoveis>() {
            @Override
            public int compare(BensImoveis o1, BensImoveis o2) {
                return o1.getDataBem().compareTo(o2.getDataBem());
            }
        });
    }

    private void ordenarEventosAlienacao(List<LeilaoAlienacaoLoteBem> eventosBem) {
        Collections.sort(eventosBem, new Comparator<LeilaoAlienacaoLoteBem>() {
            @Override
            public int compare(LeilaoAlienacaoLoteBem o1, LeilaoAlienacaoLoteBem o2) {
                return o1.getDataOperacao().compareTo(o2.getDataOperacao());
            }
        });
    }

    private void ordenarEventosGanhoPerda(List<GanhoPerdaPatrimonial> eventosBem) {
        Collections.sort(eventosBem, new Comparator<GanhoPerdaPatrimonial>() {
            @Override
            public int compare(GanhoPerdaPatrimonial o1, GanhoPerdaPatrimonial o2) {
                return o1.getDataOperacao().compareTo(o2.getDataOperacao());
            }
        });
    }

    private void ordenarEventosBaixa(List<ItemEfetivacaoBaixaPatrimonial> eventosBem) {
        Collections.sort(eventosBem, new Comparator<ItemEfetivacaoBaixaPatrimonial>() {
            @Override
            public int compare(ItemEfetivacaoBaixaPatrimonial o1, ItemEfetivacaoBaixaPatrimonial o2) {
                return o1.getDataOperacao().compareTo(o2.getDataOperacao());
            }
        });
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public void popularEventosParaContabilzacao(EfetivacaoBaixaPatrimonial selecionado, AssistenteContabilizacaoBaixa assistenteContabilizar, AssistenteMovimentacaoBens assistente) {
        if (selecionado.isSolicitacaoBaixaPorAlienacao()) {
            popularListaEventosParaContabilizarPorAlienacao(assistenteContabilizar, assistente, selecionado);
        } else {
            popularListaEventosParaContabilizar(assistenteContabilizar, assistente, selecionado);
        }
    }

    public void popularListaEventosParaContabilizar(AssistenteContabilizacaoBaixa assistenteContabilizar,
                                                    AssistenteMovimentacaoBens assistente, EfetivacaoBaixaPatrimonial selecionado) {
        assistente.setDescricaoProcesso("Recuperando Eventos para  Contabilização...");
        assistente.setTotal(adicionarQtdeBensParaAssistente(selecionado, assistenteContabilizar.getBensParaContabilizar()));
        popularListasEventos(assistenteContabilizar, selecionado, assistente);
    }

    public void popularListaEventosParaContabilizarPorAlienacao(AssistenteContabilizacaoBaixa assistenteContabilizar,
                                                                AssistenteMovimentacaoBens assistente, EfetivacaoBaixaPatrimonial selecionado) {

        popularListasEventos(assistenteContabilizar, selecionado, assistente);

        if (assistenteContabilizar.getRegistroGanhosPatrimonial().isEmpty()) {
            assistenteContabilizar.getRegistroGanhosPatrimonial().addAll(buscarGanhoPerdaPatrimonial(selecionado, GanhoPerdaPatrimonial.Tipo.GANHO, assistente));
        }
        if (assistenteContabilizar.getRegistroPerdasPatrimonial().isEmpty()) {
            assistenteContabilizar.getRegistroPerdasPatrimonial().addAll(buscarGanhoPerdaPatrimonial(selecionado, GanhoPerdaPatrimonial.Tipo.PERDA, assistente));
        }

        if (assistenteContabilizar.getItensAlienacao().isEmpty()) {
            List<Long> idsLotes = getIdsLoteAlienacao(assistenteContabilizar.getBensParaContabilizar());
            assistenteContabilizar.getItensAlienacao().addAll(leilaoAlienacaoFacade.buscarItemLeilaoAlienacao(idsLotes, assistente));
        }
    }

    private List<Long> getIdsLoteAlienacao(List<VOItemBaixaPatrimonial> itens) {
        List<Long> idsLotes = Lists.newArrayList();
        for (VOItemBaixaPatrimonial lote : itens) {
            idsLotes.add(lote.getLeilaoAlienacaoLote().getId());
        }
        return idsLotes;
    }

    private void popularListasEventos(AssistenteContabilizacaoBaixa assistenteContabilizar, EfetivacaoBaixaPatrimonial selecionado, AssistenteMovimentacaoBens assistente) {
        if (assistenteContabilizar.getEventosApuracaoValorLiquido().isEmpty()) {
            assistenteContabilizar.getEventosApuracaoValorLiquido().addAll(buscarApuracaoValorLiquidoDepreciacao(selecionado, assistente));
            assistenteContabilizar.getEventosApuracaoValorLiquido().addAll(buscarApuracaoValorLiquidoAmortizacao(selecionado, assistente));
            assistenteContabilizar.getEventosApuracaoValorLiquido().addAll(buscarApuracaoValorLiquidoExaustao(selecionado, assistente));
            assistenteContabilizar.getEventosApuracaoValorLiquido().addAll(buscarApuracaoValorLiquidoAjuste(selecionado, assistente));
        }
        if (assistenteContabilizar.getItensEfetivacaoBaixa().isEmpty()) {
            assistenteContabilizar.getItensEfetivacaoBaixa().addAll(buscarItemEfetivacaoBaixa(selecionado, assistente));
        }
        if (assistenteContabilizar.getItensParecerBaixa().isEmpty()) {
            assistenteContabilizar.getItensParecerBaixa().addAll(getParecerBaixaPatrimonialFacade().buscarIdsItemParecerBaixa(selecionado.getParecerBaixaPatrimonial()));
        }
        if (assistenteContabilizar.getItensSolicitacaoBaixa().isEmpty()) {
            assistenteContabilizar.getItensSolicitacaoBaixa().addAll(getSolicitacaoBaixaPatrimonialFacade().buscarIdsItemSolicitacaoBaixa(selecionado.getParecerBaixaPatrimonial().getSolicitacaoBaixa()));
        }
    }

    public EfetivacaoBaixaPatrimonial salvarEfetivacao(EfetivacaoBaixaPatrimonial entity) {
        return em.merge(entity);
    }

    public void salvarLogErro(List<LogErroEfetivacaoBaixaBem> logsErro) {
        for (LogErroEfetivacaoBaixaBem log : logsErro) {
            em.merge(log);
        }
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public void concluirTodosProcessoEventoBem(AssistenteContabilizacaoBaixa assitenteContabilzacao, AssistenteMovimentacaoBens assistente) {

        int total = assitenteContabilzacao.getItensSolicitacaoBaixa().size()
            + assitenteContabilzacao.getItensParecerBaixa().size()
            + assitenteContabilzacao.getRegistroGanhosPatrimonial().size()
            + assitenteContabilzacao.getRegistroPerdasPatrimonial().size()
            + assitenteContabilzacao.getEventosApuracaoValorLiquido().size()
            + assitenteContabilzacao.getItensEfetivacaoBaixa().size();
        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("Concluíndo Processo Baixa - Atualizando Eventos...");
        assistente.setTotal(total);
        try {
            concluirItemSolicitacaoBaixa(assitenteContabilzacao, assistente);
            concluirItemParecerBaixa(assitenteContabilzacao, assistente);
            concluirItemGanhoPatrimonial(assitenteContabilzacao, assistente);
            concluirItemPerdaPatrimonial(assitenteContabilzacao, assistente);
            concluirItemApuracaoValorLiquido(assitenteContabilzacao, assistente);
            concluirItemBaixaPatrimonial(assitenteContabilzacao, assistente);
        } catch (Exception ex) {
            logger.error("concluirTodosProcessoEventoBem {}", ex);
            throw ex;
        }
    }

    private void concluirItemSolicitacaoBaixa(AssistenteContabilizacaoBaixa assitenteContabilzacao, AssistenteMovimentacaoBens assistente) {
        for (Long idItem : assitenteContabilzacao.getItensSolicitacaoBaixa()) {
            concluirEventoBem(idItem, SituacaoEventoBem.FINALIZADO);
            assistente.conta();
        }
    }

    private void concluirItemParecerBaixa(AssistenteContabilizacaoBaixa assitenteContabilzacao, AssistenteMovimentacaoBens assistente) {
        for (Long idItem : assitenteContabilzacao.getItensParecerBaixa()) {
            concluirEventoBem(idItem, SituacaoEventoBem.FINALIZADO);
            assistente.conta();
        }
    }

    private void concluirItemGanhoPatrimonial(AssistenteContabilizacaoBaixa assitenteContabilzacao, AssistenteMovimentacaoBens assistente) {
        for (GanhoPerdaPatrimonial ganho : assitenteContabilzacao.getRegistroGanhosPatrimonial()) {
            concluirEventoBem(ganho.getId(), SituacaoEventoBem.FINALIZADO);
            assistente.conta();
        }
    }

    private void concluirItemPerdaPatrimonial(AssistenteContabilizacaoBaixa assitenteContabilzacao, AssistenteMovimentacaoBens assistente) {
        for (GanhoPerdaPatrimonial ganho : assitenteContabilzacao.getRegistroPerdasPatrimonial()) {
            concluirEventoBem(ganho.getId(), SituacaoEventoBem.FINALIZADO);
            assistente.conta();
        }
    }

    private void concluirItemApuracaoValorLiquido(AssistenteContabilizacaoBaixa assitenteContabilzacao, AssistenteMovimentacaoBens assistente) {
        for (EventoBem eventoBem : assitenteContabilzacao.getEventosApuracaoValorLiquido()) {
            concluirEventoBem(eventoBem.getId(), SituacaoEventoBem.FINALIZADO);
            assistente.conta();
        }
    }

    private void concluirItemBaixaPatrimonial(AssistenteContabilizacaoBaixa assitenteContabilzacao, AssistenteMovimentacaoBens assistente) {
        for (ItemEfetivacaoBaixaPatrimonial itemBaixa : assitenteContabilzacao.getItensEfetivacaoBaixa()) {
            EstadoBem estadoResultante = itemBaixa.getEstadoResultante();
            estadoResultante.setEstadoBem(EstadoConservacaoBem.BAIXADO);
            estadoResultante.setValorOriginal(BigDecimal.ZERO);
            em.merge(estadoResultante);
            concluirEventoBem(itemBaixa.getId(), SituacaoEventoBem.BAIXADO);
            assistente.conta();
        }
    }

    public void concluirEventoBem(Long idEventoBem, SituacaoEventoBem situacaoEventoBem) {
        String sql = " " +
            "   update eventobem ev set ev.situacaoeventobem = :situacao " +
            "   where ev.id = :idEvento ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idEvento", idEventoBem);
        q.setParameter("situacao", situacaoEventoBem.name());
        q.executeUpdate();
    }

    public void deletarLogErro(EfetivacaoBaixaPatrimonial entity) {
        StringBuilder sql = new StringBuilder();
        sql.append(" delete from logerroefetivacaobaixabem where efetivacaobaixapatrimonial_id = :idEfetivacao ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("idEfetivacao", entity.getId());
        q.executeUpdate();
    }


    private void criarItemEfetivacaoBaixa(EfetivacaoBaixaPatrimonial selecionado, AssistenteMovimentacaoBens assistente,
                                          List<VOItemBaixaPatrimonial> bensEfetivacao, AssistenteContabilizacaoBaixa assistenteContabilizacao) {

        List<VOItemBaixaPatrimonial> itensParaCriarEventoBaixa = Lists.newArrayList();
        itensParaCriarEventoBaixa.addAll(getBensParaProcessar(selecionado, bensEfetivacao));

        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("Criando Evento da Efetivação Baixa...");
        assistente.setTotal(itensParaCriarEventoBaixa.size());
        for (VOItemBaixaPatrimonial item : itensParaCriarEventoBaixa) {
            ItemEfetivacaoBaixaPatrimonial novoItem = criarItemEfetivacaoBaixa(selecionado, assistente, item);
            novoItem = em.merge(novoItem);
            assistenteContabilizacao.getItensEfetivacaoBaixa().add(novoItem);
            assistente.conta();
        }
    }

    private List<VOItemBaixaPatrimonial> getBensParaProcessar(EfetivacaoBaixaPatrimonial selecionado, List<VOItemBaixaPatrimonial> bensEfetivacao) {
        List<VOItemBaixaPatrimonial> toReturn = Lists.newArrayList();
        if (selecionado.isSolicitacaoBaixaPorAlienacao()) {
            for (VOItemBaixaPatrimonial loteLeilao : bensEfetivacao) {
                toReturn.addAll(loteLeilao.getBensAgrupados());
            }
        } else {
            toReturn.addAll(bensEfetivacao);
        }
        return toReturn;
    }

    public void contabilizarDesincorporacao(EfetivacaoBaixaPatrimonial selecionado, AssistenteMovimentacaoBens assistente, List<ItemEfetivacaoBaixaPatrimonial> eventosDesincoporacaoParaContabilizar) {
        ordenarEventosBaixa(eventosDesincoporacaoParaContabilizar);
        integradorPatrimonialContabilFacade.desincorporarBens(
            eventosDesincoporacaoParaContabilizar,
            selecionado.getHistoricoRazaoItemBaixa(),
            assistente);
    }

    private Integer adicionarQtdeBensParaAssistente(EfetivacaoBaixaPatrimonial selecionado, List<VOItemBaixaPatrimonial> bensEfetivacao) {
        Integer total = 0;
        if (selecionado.isSolicitacaoBaixaPorAlienacao()) {
            for (VOItemBaixaPatrimonial lote : bensEfetivacao) {
                total = total + lote.getBensAgrupados().size();
            }
        } else {
            total = bensEfetivacao.size();
        }
        return total;
    }

    private ItemEfetivacaoBaixaPatrimonial criarItemEfetivacaoBaixa(EfetivacaoBaixaPatrimonial selecionado, AssistenteMovimentacaoBens assistente, VOItemBaixaPatrimonial itemBem) {
        EstadoBem ultimoEstado;
        if (itemBem.getEstadoResultante() != null) {
            ultimoEstado = itemBem.getEstadoResultante();
        } else {
            ultimoEstado = bemFacade.recuperarUltimoEstadoDoBem(itemBem.getBemEfetivacao());
        }
        ItemEfetivacaoBaixaPatrimonial itemEfetivacaoBaixa = new ItemEfetivacaoBaixaPatrimonial();
        Date dataLancamento = DataUtil.getDataComHoraAtual(assistente.getDataLancamento());
        itemEfetivacaoBaixa.setDataLancamento(dataLancamento);
        itemEfetivacaoBaixa.setSituacaoEventoBem(SituacaoEventoBem.AGUARDANDO_EFETIVACAO);
        EstadoBem estadoResultante = bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(ultimoEstado);
        estadoResultante = em.merge(estadoResultante);

        itemEfetivacaoBaixa.setBem(itemBem.getBemEfetivacao());
        itemEfetivacaoBaixa.setEstadoInicial(ultimoEstado);
        itemEfetivacaoBaixa.setEstadoResultante(estadoResultante);
        itemEfetivacaoBaixa.setEfetivacaoBaixa(selecionado);
        itemEfetivacaoBaixa.setValorDoLancamento(ultimoEstado.getValorLiquido());
        return itemEfetivacaoBaixa;
    }

    public void registrarGanhoPerdaBem(EfetivacaoBaixaPatrimonial selecionado, AssistenteMovimentacaoBens assistente,
                                       List<VOItemBaixaPatrimonial> bensLoteAlienacao, AssistenteContabilizacaoBaixa assistenteContabilizacao) throws ExcecaoNegocioGenerica {

        assistente.setDescricaoProcesso("Criando Evento Registro de Ganhos / Perdas...");
        assistente.zerarContadoresProcesso();
        List<VOItemBaixaPatrimonial> bensParaProcessar = getBensParaProcessar(selecionado, bensLoteAlienacao);
        assistente.setTotal(bensParaProcessar.size());

        BigDecimal diferenca;
        EstadoBem ultimoEstado;
        for (VOItemBaixaPatrimonial itemBem : bensParaProcessar) {
            if (itemBem.getEstadoResultante() != null) {
                ultimoEstado = itemBem.getEstadoResultante();
            } else {
                ultimoEstado = bemFacade.recuperarUltimoEstadoDoBem(itemBem.getBemEfetivacao());
            }
            EstadoBem estadoResultante = bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(ultimoEstado);
            diferenca = itemBem.getValorProporcionalArrematado().subtract(ultimoEstado.getValorLiquido());
            Date dataLancamento = DataUtil.getDataComHoraAtual(assistente.getDataLancamento());
            if (diferenca.compareTo(BigDecimal.ZERO) > 0) {
                estadoResultante.setValorOriginal(estadoResultante.getValorOriginal().add(diferenca));
                estadoResultante = em.merge(estadoResultante);

                GanhoPerdaPatrimonial ganhoPatrimonial = new GanhoPerdaPatrimonial(itemBem.getBemEfetivacao(), ultimoEstado, estadoResultante, SituacaoEventoBem.AGUARDANDO_EFETIVACAO, GanhoPerdaPatrimonial.Tipo.GANHO, diferenca);
                ganhoPatrimonial.setEfetivacaoBaixaPatrimonial(selecionado);
                ganhoPatrimonial.setTipoOperacao(TipoOperacao.DEBITO);
                ganhoPatrimonial.setTipoEventoBem(TipoEventoBem.GANHOPATRIMONIAL);
                ganhoPatrimonial.setDataLancamento(dataLancamento);
                ganhoPatrimonial = em.merge(ganhoPatrimonial);
                assistenteContabilizacao.getRegistroGanhosPatrimonial().add(ganhoPatrimonial);

            } else if (diferenca.compareTo(BigDecimal.ZERO) < 0) {
                estadoResultante.setValorOriginal(estadoResultante.getValorOriginal().subtract(diferenca.abs()));
                estadoResultante = em.merge(estadoResultante);

                GanhoPerdaPatrimonial perdaPatrimonial = new GanhoPerdaPatrimonial(itemBem.getBemEfetivacao(), ultimoEstado, estadoResultante, SituacaoEventoBem.AGUARDANDO_EFETIVACAO, GanhoPerdaPatrimonial.Tipo.PERDA, diferenca.multiply(UMNEGATIVO));
                perdaPatrimonial.setEfetivacaoBaixaPatrimonial(selecionado);
                perdaPatrimonial.setTipoEventoBem(TipoEventoBem.PERDAPATRIMONIAL);
                perdaPatrimonial.setTipoOperacao(TipoOperacao.CREDITO);
                perdaPatrimonial.setDataLancamento(dataLancamento);
                perdaPatrimonial = em.merge(perdaPatrimonial);
                assistenteContabilizacao.getRegistroPerdasPatrimonial().add(perdaPatrimonial);
            }
            itemBem.setEstadoResultante(estadoResultante);
            assistente.conta();
        }
    }

    private void contabilizarApuracaoValorLiquidoContabil(EfetivacaoBaixaPatrimonial selecionado, AssistenteMovimentacaoBens assistente, AssistenteContabilizacaoBaixa assistenteContabilizacao) {
        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("Criando Bens no Contábil para Contabilização...");
        assistente.setTotal(assistenteContabilizacao.getEventosApuracaoValorLiquido().size());
        assistenteContabilizacao.setBensMoveis(new ArrayList<BensMoveis>());
        assistenteContabilizacao.setBensImoveis(new ArrayList<BensImoveis>());

        for (EventoBem eventoBem : assistenteContabilizacao.getEventosApuracaoValorLiquido()) {
            assistenteContabilizacao.setEventoBem(eventoBem);
            criarInstanciaBemContabil(selecionado, assistenteContabilizacao);
            assistente.conta();
        }
        contabilizarApuracaoValorLiquidoContabilBensMoveis(selecionado, assistente, assistenteContabilizacao);
        contabilizarApuracaoValorLiquidoContabilBensImoveis(selecionado, assistente, assistenteContabilizacao);
    }

    private void contabilizarApuracaoValorLiquidoContabilBensImoveis(EfetivacaoBaixaPatrimonial selecionado, AssistenteMovimentacaoBens assistente, AssistenteContabilizacaoBaixa assistenteContabilizacao) {
        if (!assistenteContabilizacao.getBensImoveis().isEmpty()) {
            ordenarBensImoveis(assistenteContabilizacao.getBensImoveis());
            assistente.zerarContadoresProcesso();
            assistente.setDescricaoProcesso(assistente.getSimularContabilizacao() ? "Simulando Contabilização Apuração do Valor Líquido Contábil..." : "Contabilizando Apuração do Valor Líquido Contábil...");
            assistente.setTotal(assistenteContabilizacao.getBensImoveis().size());
            for (BensImoveis bensImoveis : assistenteContabilizacao.getBensImoveis()) {
                integradorPatrimonialContabilFacade.contabilizarApuracaoValorLiquidoContabilBensImoveis(selecionado, bensImoveis, assistente);
                assistente.conta();
            }
        }
    }

    private void contabilizarApuracaoValorLiquidoContabilBensMoveis(EfetivacaoBaixaPatrimonial selecionado, AssistenteMovimentacaoBens assistente, AssistenteContabilizacaoBaixa assistenteContabilizacao) {
        if (!assistenteContabilizacao.getBensMoveis().isEmpty()) {
            ordenarBensMoveis(assistenteContabilizacao.getBensMoveis());
            assistente.zerarContadoresProcesso();
            assistente.setDescricaoProcesso(assistente.getSimularContabilizacao() ? "Simulando Contabilização Apuração do Valor Líquido Contábil..." : "Contabilizando Apuração do Valor Líquido Contábil...");

            if (assistenteContabilizacao.getBensMoveis().size() >= 200) {
                List<List<BensMoveis>> partes = Lists.partition(assistenteContabilizacao.getBensMoveis(), assistenteContabilizacao.getBensMoveis().size() / 50);
                assistente.setTotal(partes.size());
                for (List<BensMoveis> bensMoveisParticionados : partes) {
                    for (BensMoveis bemMovel : bensMoveisParticionados) {
                        integradorPatrimonialContabilFacade.apurarValorLiquidoContabil(selecionado, bemMovel, assistente);
                    }
                    em.flush();
                    em.clear();
                    assistente.conta();
                }
            } else {
                assistente.setTotal(assistenteContabilizacao.getBensMoveis().size());
                for (BensMoveis bemMovel : assistenteContabilizacao.getBensMoveis()) {
                    integradorPatrimonialContabilFacade.apurarValorLiquidoContabil(selecionado, bemMovel, assistente);
                    assistente.conta();
                }
            }
        }
    }

    private void criarInstanciaBemMovelContabil(AssistenteContabilizacaoBaixa assistenteContabilizar) {
        EventoBem eventoBem = assistenteContabilizar.getEventoBem();
        BensMoveis bensMoveis = integradorPatrimonialContabilFacade.criarNovaInstanciaDeBensMoveis(
            eventoBem,
            assistenteContabilizar.getTipoOperacaoBensMoveis(),
            assistenteContabilizar.getHistorico(),
            assistenteContabilizar.getExercicio());
        bensMoveis.setBem(eventoBem.getBem());
        assistenteContabilizar.getBensMoveis().add(bensMoveis);
    }

    private void criarInstanciaBemImovelContabil(AssistenteContabilizacaoBaixa assistenteContabilizar) {
        EventoBem eventoBem = assistenteContabilizar.getEventoBem();
        BensImoveis bensImoveis = integradorPatrimonialContabilFacade.criarNovaInstanciaDeBensImoveis(
            eventoBem,
            assistenteContabilizar.getTipoOperacaoBensImoveis(),
            assistenteContabilizar.getHistorico(),
            assistenteContabilizar.getExercicio());
        bensImoveis.setBem(eventoBem.getBem());
        assistenteContabilizar.getBensImoveis().add(bensImoveis);
    }

    private void criarInstanciaBemContabil(EfetivacaoBaixaPatrimonial selecionado, AssistenteContabilizacaoBaixa assistenteContabilizar) {
        EventoBem eventoBem = assistenteContabilizar.getEventoBem();
        if (eventoBem.getTipoEventoBem() != null) {
            switch (eventoBem.getTipoEventoBem()) {
                case APURACAOVALORLIQUIDODEPRECIACAO:
                    assistenteContabilizar.setHistorico(selecionado.getCodigo() != null ? ((ApuracaoValorLiquidoDepreciacao) eventoBem).getHistoricoRazao(selecionado) : "");
                    if (eventoBem.getTipoBemResultante().isMovel()) {
                        assistenteContabilizar.setTipoOperacaoBensMoveis(TipoOperacaoBensMoveis.APURACAO_VALOR_LIQUIDO_BENS_MOVEIS_DEPRECIACAO);
                        criarInstanciaBemMovelContabil(assistenteContabilizar);

                    } else if (eventoBem.getTipoBemResultante().isImovel()) {
                        assistenteContabilizar.setTipoOperacaoBensImoveis(TipoOperacaoBensImoveis.APURACAO_VALOR_LIQUIDO_BENS_IMOVEIS_DEPRECIACAO);
                        criarInstanciaBemImovelContabil(assistenteContabilizar);
                    }
                    break;
                case APURACAOVALORLIQUIDOAMORTIZACAO:
                    assistenteContabilizar.setHistorico(selecionado.getCodigo() != null ? ((ApuracaoValorLiquidoAmortizacao) eventoBem).getHistoricoRazao(selecionado) : "");
                    if (eventoBem.getTipoBemResultante().isMovel()) {
                        assistenteContabilizar.setTipoOperacaoBensMoveis(TipoOperacaoBensMoveis.APURACAO_VALOR_LIQUIDO_BENS_MOVEIS_AMORTIZACAO);
                        criarInstanciaBemMovelContabil(assistenteContabilizar);

                    } else if (eventoBem.getTipoBemResultante().isImovel()) {
                        assistenteContabilizar.setTipoOperacaoBensImoveis(TipoOperacaoBensImoveis.APURACAO_VALOR_LIQUIDO_BENS_IMOVEIS_AMORTIZACAO);
                        criarInstanciaBemImovelContabil(assistenteContabilizar);
                    }
                    break;
                case APURACAOVALORLIQUIDOEXAUSTAO:
                    assistenteContabilizar.setHistorico(selecionado.getCodigo() != null ? ((ApuracaoValorLiquidoExaustao) eventoBem).getHistoricoRazao(selecionado) : "");
                    if (eventoBem.getTipoBemResultante().isMovel()) {
                        assistenteContabilizar.setTipoOperacaoBensMoveis(TipoOperacaoBensMoveis.APURACAO_VALOR_LIQUIDO_BENS_MOVEIS_EXAUSTAO);
                        criarInstanciaBemMovelContabil(assistenteContabilizar);

                    } else if (eventoBem.getTipoBemResultante().isImovel()) {
                        assistenteContabilizar.setTipoOperacaoBensImoveis(TipoOperacaoBensImoveis.APURACAO_VALOR_LIQUIDO_BENS_IMOVEIS_EXAUSTAO);
                        criarInstanciaBemImovelContabil(assistenteContabilizar);
                    }
                    break;
                case APURACAOVALORLIQUIDOAJUSTE:
                    assistenteContabilizar.setHistorico(selecionado.getCodigo() != null ? ((ApuracaoValorLiquidoAjuste) eventoBem).getHistoricoRazao(selecionado) : "");
                    if (eventoBem.getTipoBemResultante().isMovel()) {
                        assistenteContabilizar.setTipoOperacaoBensMoveis(TipoOperacaoBensMoveis.APURACAO_VALOR_LIQUIDO_BENS_MOVEIS_REDUCAO_VALOR_RECUPERAVEL);
                        criarInstanciaBemMovelContabil(assistenteContabilizar);

                    } else if (eventoBem.getTipoBemResultante().isImovel()) {
                        assistenteContabilizar.setTipoOperacaoBensImoveis(TipoOperacaoBensImoveis.APURACAO_VALOR_LIQUIDO_BENS_IMOVEIS_REDUCAO_VALOR_RECUPERAVEL);
                        criarInstanciaBemImovelContabil(assistenteContabilizar);
                    }
                    break;
            }
        }
    }

    private void contabilzarRegistroGanhosPerdas(EfetivacaoBaixaPatrimonial selecionado, AssistenteMovimentacaoBens assistente,
                                                 List<GanhoPerdaPatrimonial> ganhosPatrimoniais, List<GanhoPerdaPatrimonial> perdasPatrimoniais) {

        ordenarEventosGanhoPerda(ganhosPatrimoniais);
        ordenarEventosGanhoPerda(perdasPatrimoniais);
        String historico;
        if (!ganhosPatrimoniais.isEmpty()) {
            historico = selecionado.getCodigo() != null ? selecionado.getHistoricoRazaoGanho(selecionado.getCodigo().toString()) : "";
            integradorPatrimonialContabilFacade.registrarGanhosDosBens(
                ganhosPatrimoniais,
                historico,
                assistente);
        }
        if (!perdasPatrimoniais.isEmpty()) {
            historico = selecionado.getCodigo() != null ? selecionado.getHistoricoRazaoPerda(selecionado.getCodigo().toString()) : "";
            integradorPatrimonialContabilFacade.registrarPerdasDosBens(
                perdasPatrimoniais,
                historico,
                assistente);
        }
    }

    private void salvarRegistroGanho(AssistenteMovimentacaoBens assistente, List<GanhoPerdaPatrimonial> ganhosPatrimoniais, AssistenteContabilizacaoBaixa assistenteContabilizacao) {

        assistente.setDescricaoProcesso("Salvando Registro de Ganhos do Bem...");
        assistente.zerarContadoresProcesso();
        assistente.setTotal(ganhosPatrimoniais.size());
        for (GanhoPerdaPatrimonial ganho : ganhosPatrimoniais) {
            ganho = em.merge(ganho);
            assistenteContabilizacao.getRegistroGanhosPatrimonial().add(ganho);
            assistente.conta();
        }
    }

    private void salvarRegistroPerda(AssistenteMovimentacaoBens assistente, List<GanhoPerdaPatrimonial> perdasPatrimoniais, AssistenteContabilizacaoBaixa assistenteContabilizacao) {
        assistente.setDescricaoProcesso("Salvando Registro de Perdas do Bem...");
        assistente.zerarContadoresProcesso();
        assistente.setTotal(perdasPatrimoniais.size());
        for (GanhoPerdaPatrimonial perda : perdasPatrimoniais) {
            perda = em.merge(perda);
            assistenteContabilizacao.getRegistroPerdasPatrimonial().add(perda);
            assistente.conta();
        }
    }

    private void contabilizarAlienacao(AssistenteMovimentacaoBens assitente, List<LeilaoAlienacaoLoteBem> bensLeilaoAlienacao) {
        ordenarEventosAlienacao(bensLeilaoAlienacao);
        integradorPatrimonialContabilFacade.alienarBens(bensLeilaoAlienacao, assitente);
    }

    private void apurarValorLiquidoContabilDoBem(EfetivacaoBaixaPatrimonial selecionado, AssistenteMovimentacaoBens assistente,
                                                 List<VOItemBaixaPatrimonial> bensParaEfetivar, AssistenteContabilizacaoBaixa assistenteContabilizacao) {

        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("Criando Evento de Apuração Valor Líquido Contábil...");
        List<VOItemBaixaPatrimonial> itensParaCriarEvento = getBensParaProcessar(selecionado, bensParaEfetivar);
        assistente.setTotal(itensParaCriarEvento.size());

        for (VOItemBaixaPatrimonial item : itensParaCriarEvento) {
            criarProcessoApuracaoValorLiquidoContabil(assistente, item, assistenteContabilizacao);
            assistente.conta();
        }
    }


    private void criarProcessoApuracaoValorLiquidoContabil(AssistenteMovimentacaoBens assistente, VOItemBaixaPatrimonial item, AssistenteContabilizacaoBaixa assistenteContabilizacao) {

        Bem bem = bemFacade.recuperarBemPorIdBem(item.getIdBem());
        if (bem != null) {
            EstadoBem ultimoEstado = bemFacade.recuperarUltimoEstadoDoBem(bem);
            EstadoBem novoEstadoBem = null;
            if (ultimoEstado.getValorAcumuladoDaDepreciacao().compareTo(BigDecimal.ZERO) > 0) {
                novoEstadoBem = criarApuracaoValorLiquidoDepreciacao(ultimoEstado, assistente, assistenteContabilizacao, bem);
            }
            if (ultimoEstado.getValorAcumuladoDaAmortizacao().compareTo(BigDecimal.ZERO) > 0) {
                novoEstadoBem = criarApuracaoValorLiquidoAmortizacao(ultimoEstado, assistente, assistenteContabilizacao, bem);
            }
            if (ultimoEstado.getValorAcumuladoDaExaustao().compareTo(BigDecimal.ZERO) > 0) {
                novoEstadoBem = criarApuracaoValorLiquidoExaustao(ultimoEstado, assistente, assistenteContabilizacao, bem);
            }
            if (ultimoEstado.getValorAcumuladoDeAjuste().compareTo(BigDecimal.ZERO) > 0) {
                novoEstadoBem = criarApuracaoValorLiquidoAjuste(ultimoEstado, assistente, assistenteContabilizacao, bem);
            }
            item.setEstadoResultante(novoEstadoBem);
            item.setBemEfetivacao(bem);
        }
    }

    private EstadoBem criarApuracaoValorLiquidoDepreciacao(EstadoBem ultimoEstado, AssistenteMovimentacaoBens assistente,
                                                           AssistenteContabilizacaoBaixa assistenteContabilizacao, Bem bem) {
        EstadoBem novoEstadoResultante = bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(ultimoEstado);
        novoEstadoResultante.setValorOriginal(novoEstadoResultante.getValorOriginal().subtract(ultimoEstado.getValorAcumuladoDaDepreciacao()));
        novoEstadoResultante.setValorAcumuladoDaDepreciacao(BigDecimal.ZERO);
        novoEstadoResultante = em.merge(novoEstadoResultante);

        ApuracaoValorLiquidoDepreciacao depreciacao = new ApuracaoValorLiquidoDepreciacao(bem, ultimoEstado, novoEstadoResultante);
        depreciacao.setEfetivacaoBaixaPatrimonial((EfetivacaoBaixaPatrimonial) assistente.getSelecionado());
        Date dataLancamento = DataUtil.getDataComHoraAtual(assistente.getDataLancamento());
        depreciacao.setDataLancamento(dataLancamento);
        depreciacao = em.merge(depreciacao);
        assistenteContabilizacao.getEventosApuracaoValorLiquido().add(depreciacao);
        return novoEstadoResultante;
    }

    private EstadoBem criarApuracaoValorLiquidoAmortizacao(EstadoBem ultimoEstado, AssistenteMovimentacaoBens assistente,
                                                           AssistenteContabilizacaoBaixa assistenteContabilizacao, Bem bem) {
        EstadoBem novoEstadoResultante = bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(ultimoEstado);
        novoEstadoResultante.setValorOriginal(novoEstadoResultante.getValorOriginal().subtract(ultimoEstado.getValorAcumuladoDaAmortizacao()));
        novoEstadoResultante.setValorAcumuladoDaAmortizacao(BigDecimal.ZERO);
        novoEstadoResultante = em.merge(novoEstadoResultante);

        ApuracaoValorLiquidoAmortizacao amortizacao = new ApuracaoValorLiquidoAmortizacao(bem, ultimoEstado, novoEstadoResultante);
        amortizacao.setEfetivacaoBaixaPatrimonial((EfetivacaoBaixaPatrimonial) assistente.getSelecionado());
        Date dataLancamento = DataUtil.getDataComHoraAtual(assistente.getDataLancamento());
        amortizacao.setDataLancamento(dataLancamento);
        amortizacao = em.merge(amortizacao);
        assistenteContabilizacao.getEventosApuracaoValorLiquido().add(amortizacao);
        return novoEstadoResultante;
    }

    private EstadoBem criarApuracaoValorLiquidoExaustao(EstadoBem ultimoEstado, AssistenteMovimentacaoBens assistente,
                                                        AssistenteContabilizacaoBaixa assistenteContabilizacao, Bem bem) {
        EstadoBem novoEstadoResultante = bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(ultimoEstado);
        novoEstadoResultante.setValorOriginal(novoEstadoResultante.getValorOriginal().subtract(ultimoEstado.getValorAcumuladoDaExaustao()));
        novoEstadoResultante.setValorAcumuladoDaExaustao(BigDecimal.ZERO);
        novoEstadoResultante = em.merge(novoEstadoResultante);

        ApuracaoValorLiquidoExaustao exaustao = new ApuracaoValorLiquidoExaustao(bem, ultimoEstado, novoEstadoResultante);
        exaustao.setEfetivacaoBaixaPatrimonial((EfetivacaoBaixaPatrimonial) assistente.getSelecionado());
        Date dataLancamento = DataUtil.getDataComHoraAtual(assistente.getDataLancamento());
        exaustao.setDataLancamento(dataLancamento);
        exaustao = em.merge(exaustao);
        assistenteContabilizacao.getEventosApuracaoValorLiquido().add(exaustao);
        return novoEstadoResultante;
    }

    private EstadoBem criarApuracaoValorLiquidoAjuste(EstadoBem ultimoEstado, AssistenteMovimentacaoBens assistente,
                                                      AssistenteContabilizacaoBaixa assistenteContabilizacao, Bem bem) {
        EstadoBem novoEstadoResultante = bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(ultimoEstado);
        novoEstadoResultante.setValorOriginal(novoEstadoResultante.getValorOriginal().subtract(ultimoEstado.getValorAcumuladoDeAjuste()));
        novoEstadoResultante.setValorAcumuladoDeAjuste(BigDecimal.ZERO);
        novoEstadoResultante = em.merge(novoEstadoResultante);

        ApuracaoValorLiquidoAjuste ajuste = new ApuracaoValorLiquidoAjuste(bem, ultimoEstado, novoEstadoResultante);
        ajuste.setEfetivacaoBaixaPatrimonial((EfetivacaoBaixaPatrimonial) assistente.getSelecionado());
        Date dataLancamento = DataUtil.getDataComHoraAtual(assistente.getDataLancamento());
        ajuste.setDataLancamento(dataLancamento);
        ajuste = em.merge(ajuste);
        assistenteContabilizacao.getEventosApuracaoValorLiquido().add(ajuste);
        return novoEstadoResultante;

    }


    private void salvarApuracaoValorLiquidoAmortizacao(AssistenteMovimentacaoBens assistente, List<ApuracaoValorLiquidoAmortizacao> amortizacoesParaProcessar,
                                                       AssistenteContabilizacaoBaixa assistenteContabilizacao) {
        if (!amortizacoesParaProcessar.isEmpty()) {
            assistente.zerarContadoresProcesso();
            assistente.setDescricaoProcesso("Salvando Apuração Valor Líquido Contábil - Amortização...");
            assistente.setTotal(amortizacoesParaProcessar.size());

            for (ApuracaoValorLiquidoAmortizacao item : amortizacoesParaProcessar) {
                item = em.merge(item);
                assistenteContabilizacao.getEventosApuracaoValorLiquido().add(item);
                assistente.conta();
            }
        }
    }

    private void salvarApuracaoValorLiquidoExaustao(AssistenteMovimentacaoBens assistente, List<ApuracaoValorLiquidoExaustao> exaustoesParaProcessar,
                                                    AssistenteContabilizacaoBaixa assistenteContabilizacao) {
        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("Salvando Apuração Valor Líquido Contábil - Exaustão...");
        assistente.setTotal(exaustoesParaProcessar.size());
        for (ApuracaoValorLiquidoExaustao item : exaustoesParaProcessar) {
            item = em.merge(item);
            assistenteContabilizacao.getEventosApuracaoValorLiquido().add(item);
            assistente.conta();
        }
    }

    private void salvarApuracaoValorLiquidoAjuste(AssistenteMovimentacaoBens assistente, List<ApuracaoValorLiquidoAjuste> ajustesParaProcessar,
                                                  AssistenteContabilizacaoBaixa assistenteContabilizacao) {
        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("Salvando Apuração Valor Líquido Contábil - Ajuste...");
        assistente.setTotal(ajustesParaProcessar.size());
        for (ApuracaoValorLiquidoAjuste item : ajustesParaProcessar) {
            item = em.merge(item);
            assistenteContabilizacao.getEventosApuracaoValorLiquido().add(item);
            assistente.conta();
        }
    }

    public List<LogErroEfetivacaoBaixaBem> buscarInconsistenciasEfetivacaoBaixa(EfetivacaoBaixaPatrimonial selecionado) {
        String sql = "select * from logerroefetivacaobaixabem " +
            "         where efetivacaobaixapatrimonial_id = :idEfetivacao ";
        Query q = em.createNativeQuery(sql, LogErroEfetivacaoBaixaBem.class);
        q.setParameter("idEfetivacao", selecionado.getId());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public List<GanhoPerdaPatrimonial> buscarGanhoPerdaPatrimonial(EfetivacaoBaixaPatrimonial selecionado, GanhoPerdaPatrimonial.Tipo tipoGanhoPerda, AssistenteMovimentacaoBens assistente) {
        String sql = "  " +
            " select " +
            "  item.id " +
            " from ganhoperdapatrimonial item " +
            "  inner join eventobem ev on ev.id = item.id " +
            " where item.tipo = :tipoGanhoPerda" +
            "  and item.efetivacaobaixapatrimonial_id = :idEfetivacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("tipoGanhoPerda", tipoGanhoPerda.name());
        q.setParameter("idEfetivacao", selecionado.getId());
        List<GanhoPerdaPatrimonial> toReturn = Lists.newArrayList();
        try {
            List<BigDecimal> ids = q.getResultList();
            if (ids != null && !ids.isEmpty()) {
                assistente.setDescricaoProcesso("Recuperando " + tipoGanhoPerda.name() + " Patrimonial...");
                assistente.zerarContadoresProcesso();
                assistente.setTotal(ids.size());
                for (BigDecimal id : ids) {
                    GanhoPerdaPatrimonial ganhoPerdaPatrimonial = em.find(GanhoPerdaPatrimonial.class, id.longValue());
                    if (ganhoPerdaPatrimonial != null) {
                        toReturn.add(ganhoPerdaPatrimonial);
                    }
                    assistente.conta();
                }
            }
        } catch (Exception ex) {
            logger.error("{}", ex);
        }
        return toReturn;
    }

    public List<ItemEfetivacaoBaixaPatrimonial> buscarItemEfetivacaoBaixa(EfetivacaoBaixaPatrimonial selecionado, AssistenteMovimentacaoBens assistente) {
        String sql = "  " +
            " select " +
            "  item.id " +
            " from itemefetivacaobaixa item " +
            "  inner join eventobem ev on ev.id = item.id " +
            " where item.efetivacaobaixa_id = :idEfetivacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idEfetivacao", selecionado.getId());
        List<ItemEfetivacaoBaixaPatrimonial> toReturn = Lists.newArrayList();
        try {
            List<BigDecimal> ids = q.getResultList();
            if (ids != null && !ids.isEmpty()) {
                assistente.setDescricaoProcesso("Recuperando Evento Efetivação da Baixa...");
                assistente.zerarContadoresProcesso();
                assistente.setTotal(ids.size());
                for (BigDecimal id : ids) {
                    ItemEfetivacaoBaixaPatrimonial itemBaixa = em.find(ItemEfetivacaoBaixaPatrimonial.class, id.longValue());
                    if (itemBaixa != null) {
                        toReturn.add(itemBaixa);
                    }
                    assistente.conta();
                }
            }
        } catch (Exception ex) {
            logger.error("{}", ex);
        }
        return toReturn;
    }


    public List<EventoBem> buscarApuracaoValorLiquidoDepreciacao(EfetivacaoBaixaPatrimonial selecionado, AssistenteMovimentacaoBens assistente) {
        String sql = "  " +
            " select " +
            "  item.id " +
            " from apuracaovlliqdepreciacao item " +
            "  inner join eventobem ev on ev.id = item.id " +
            " where item.efetivacaobaixapatrimonial_id = :idEfetivacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idEfetivacao", selecionado.getId());
        List<EventoBem> toReturn = Lists.newArrayList();
        try {
            List<BigDecimal> ids = q.getResultList();
            if (ids != null && !ids.isEmpty()) {
                assistente.setDescricaoProcesso("Recuperando Apuração do Valor Líquido Depreciação...");
                assistente.zerarContadoresProcesso();
                assistente.setTotal(ids.size());

                for (BigDecimal id : ids) {
                    EventoBem apuracaoDepreciacao = em.find(EventoBem.class, id.longValue());
                    if (apuracaoDepreciacao != null) {
                        toReturn.add(apuracaoDepreciacao);
                    }
                    assistente.conta();
                }
            }
        } catch (Exception ex) {
            logger.error("{}", ex);
        }
        return toReturn;
    }


    public List<ApuracaoValorLiquidoAmortizacao> buscarApuracaoValorLiquidoAmortizacao(EfetivacaoBaixaPatrimonial selecionado, AssistenteMovimentacaoBens assistente) {
        String sql = "  " +
            " select " +
            "  item.id " +
            " from apuracaovlliqamortizacao item " +
            "  inner join eventobem ev on ev.id = item.id " +
            " where item.efetivacaobaixapatrimonial_id = :idEfetivacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idEfetivacao", selecionado.getId());
        List<ApuracaoValorLiquidoAmortizacao> toReturn = Lists.newArrayList();
        try {
            List<BigDecimal> ids = q.getResultList();
            if (ids != null && !ids.isEmpty()) {
                assistente.setDescricaoProcesso("Recuperando Apuração do Valor Líquido Amortização...");
                assistente.zerarContadoresProcesso();
                assistente.setTotal(ids.size());
                for (BigDecimal id : ids) {
                    ApuracaoValorLiquidoAmortizacao apuracaoAmortizacao = em.find(ApuracaoValorLiquidoAmortizacao.class, id.longValue());
                    if (apuracaoAmortizacao != null) {
                        toReturn.add(apuracaoAmortizacao);
                    }
                    assistente.conta();
                }
            }
        } catch (Exception ex) {
            logger.error("{}", ex);
        }
        return toReturn;
    }

    public List<ApuracaoValorLiquidoExaustao> buscarApuracaoValorLiquidoExaustao(EfetivacaoBaixaPatrimonial selecionado, AssistenteMovimentacaoBens assistente) {
        String sql = "  " +
            " select " +
            "  item.id " +
            " from apuracaovlliqexaustao item " +
            "  inner join eventobem ev on ev.id = item.id " +
            " where item.efetivacaobaixapatrimonial_id = :idEfetivacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idEfetivacao", selecionado.getId());
        List<ApuracaoValorLiquidoExaustao> toReturn = Lists.newArrayList();
        try {
            List<BigDecimal> ids = q.getResultList();
            if (ids != null && !ids.isEmpty()) {
                assistente.setDescricaoProcesso("Recuperando Apuração do Valor Líquido Exaustão...");
                assistente.zerarContadoresProcesso();
                assistente.setTotal(ids.size());

                for (BigDecimal id : ids) {
                    ApuracaoValorLiquidoExaustao apuracaoExaustao = em.find(ApuracaoValorLiquidoExaustao.class, id.longValue());
                    if (apuracaoExaustao != null) {
                        toReturn.add(apuracaoExaustao);
                    }
                    assistente.conta();
                }
            }
        } catch (Exception ex) {
            logger.error("{}", ex);
        }
        return toReturn;
    }

    public List<ApuracaoValorLiquidoAjuste> buscarApuracaoValorLiquidoAjuste(EfetivacaoBaixaPatrimonial selecionado, AssistenteMovimentacaoBens assistente) {
        String sql = "  " +
            " select " +
            "  item.id " +
            " from apuracaovlliqexaustao item " +
            "   inner join eventobem ev on ev.id = item.id " +
            " where item.efetivacaobaixapatrimonial_id = :idEfetivacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idEfetivacao", selecionado.getId());
        List<ApuracaoValorLiquidoAjuste> toReturn = Lists.newArrayList();
        try {
            List<BigDecimal> ids = q.getResultList();
            if (ids != null && !ids.isEmpty()) {
                assistente.setDescricaoProcesso("Recuperando Apuração do Valor Líquido Ajuste...");
                assistente.zerarContadoresProcesso();
                assistente.setTotal(ids.size());
                for (BigDecimal id : ids) {
                    ApuracaoValorLiquidoAjuste apuracaoAjuste = em.find(ApuracaoValorLiquidoAjuste.class, id.longValue());
                    if (apuracaoAjuste != null) {
                        toReturn.add(apuracaoAjuste);
                    }
                    assistente.conta();
                }
            }
        } catch (Exception ex) {
            logger.error("{}", ex);
        }
        return toReturn;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<List<VOItemBaixaPatrimonial>> buscarItensEfetivadosPorLoteLeilaoAlienacao(EfetivacaoBaixaPatrimonial selecionado) {
        List<VOItemBaixaPatrimonial> toReturn = Lists.newArrayList();
        for (EfetivacaoBaixaLote loteEfetivado : selecionado.getListaLoteAlienacao()) {
            VOItemBaixaPatrimonial item = new VOItemBaixaPatrimonial();
            item.setLeilaoAlienacaoLote(loteEfetivado.getLeilaoAlienacaoLote());
            item.setBensAgrupados(buscarItensLeilaoAlienacaoPorLote(loteEfetivado.getLeilaoAlienacaoLote()));
            toReturn.add(item);
        }
        return new AsyncResult<List<VOItemBaixaPatrimonial>>(toReturn);
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<List<VOItemBaixaPatrimonial>> buscarItensSolicitacaoBaixaPorLoteLeilaoAlienacao(LeilaoAlienacaoLote loteLeilao, AssistenteMovimentacaoBens assistente) {
        List<VOItemBaixaPatrimonial> toReturn = Lists.newArrayList();
        VOItemBaixaPatrimonial item = new VOItemBaixaPatrimonial();
        item.setLeilaoAlienacaoLote(loteLeilao);
        List<VOItemBaixaPatrimonial> bensPesquisados = buscarItensLeilaoAlienacaoPorLote(loteLeilao);
        List<VOItemBaixaPatrimonial> bensDisponiveis = preencherAndValidarBensPesquisadosEfetivacaoBaixa(assistente, bensPesquisados);
        item.setBensAgrupados(bensDisponiveis);
        toReturn.add(item);
        return new AsyncResult<List<VOItemBaixaPatrimonial>>(toReturn);
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<List<VOItemBaixaPatrimonial>> buscarBensParecerBaixaParaEfetivacao(ParecerBaixaPatrimonial parecer, AssistenteMovimentacaoBens assistente) {
        String sql = "" +
            " select " +
            "  item.id as idItem,  " +
            "  bem.id as idBem,   " +
            "  null as idLote,  " +
            "  est.detentoraorcamentaria_id as idUnidadeOrc,   " +
            "  est.detentoraadministrativa_id as idUnidadeAdm,   " +
            "  bem.identificacao as registro,   " +
            "  bem.descricao as descricao,   " +
            "  bem.dataaquisicao as dataAquisicao,   " +
            "  est.estadobem as estadoConservacao,  " +
            "  vw.codigo || ' - ' || vw.descricao as unidadeOrc,   " +
            "  vwadm.codigo || ' - ' || vwadm.descricao as unidadeAdm,   " +
            "  coalesce(est.valororiginal,0) as valorOriginal,   " +
            "  coalesce(est.valoracumuladodadepreciacao, 0) as valorDepreciacao,    " +
            "  coalesce(est.valoracumuladodaamortizacao, 0) as valorAmortizacao,    " +
            "  coalesce(est.valoracumuladodaexaustao, 0) as valorExaustao,    " +
            "  coalesce(est.valoracumuladodeajuste, 0) as valorAjustePerda,   " +
            "  coalesce(est.valoracumuladodaamortizacao, 0) +    " +
            "  coalesce(est.valoracumuladodadepreciacao, 0) +    " +
            "  coalesce(est.valoracumuladodaexaustao, 0) +    " +
            "  coalesce(est.valoracumuladodeajuste, 0) as valorAjuste,   " +
            "  0 as valorAvaliadao,   " +
            "  0 as valorProporcionalArrematado,   " +
            "  grupo.codigo || ' - ' || grupo.descricao as grupoPatrionial  " +
            " from parecerbaixapatrimonial obj   " +
            "  inner join itemparecerebaixa item on item.parecerbaixa_id = obj.id " +
            "  inner join eventobem ev on ev.id = item.id   " +
            "  inner join estadobem est on est.id = ev.estadoresultante_id " +
            "  inner join grupobem grupo on grupo.id = est.grupobem_id " +
            "  inner join vwhierarquiaorcamentaria vw on vw.subordinada_id = est.detentoraorcamentaria_id   " +
            "    and ev.dataoperacao between vw.iniciovigencia and coalesce(vw.fimvigencia, ev.dataoperacao)   " +
            "  inner join vwhierarquiaadministrativa vwadm on vwadm.subordinada_id = est.detentoraadministrativa_id   " +
            "    and ev.dataoperacao between vwadm.iniciovigencia and coalesce(vwadm.fimvigencia, ev.dataoperacao)   " +
            "  inner join bem on bem.id = ev.bem_id     " +
            " where obj.id = :idParecer ";
        sql += " order by to_number(bem.identificacao) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idParecer", parecer.getId());
        List<VOItemBaixaPatrimonial> bensPesquisados = Lists.newArrayList();
        for (Object[] obj : (List<Object[]>) q.getResultList()) {
            bensPesquisados.add(VOItemBaixaPatrimonial.criarObjetoVoItemSolicitacaoBaixa(obj));
        }
        List<VOItemBaixaPatrimonial> bensDisponiveis = preencherAndValidarBensPesquisadosEfetivacaoBaixa(assistente, bensPesquisados);
        return new AsyncResult<List<VOItemBaixaPatrimonial>>(bensDisponiveis);
    }

    public List<VOItemBaixaPatrimonial> buscarItensLeilaoAlienacaoPorLote(LeilaoAlienacaoLote leilaoAlienacaoLote) {
        String sql = solicitacaoBaixaPatrimonialFacade.getSelectItensLeilaoAlienacao();
        sql += " where lote.id = :idLeilao ";
        sql += "   and lote.arrematado = :arrematado ";
        sql += " and ev.dataoperacao = (select max(evmax.dataoperacao) from eventobem evmax where evmax.bem_id = ev.bem_id) ";
        sql += " order by to_number(bem.identificacao) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idLeilao", leilaoAlienacaoLote.getId());
        q.setParameter("arrematado", Boolean.TRUE);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(leilaoAlienacaoLote.getLeilaoAlienacao().getDataEfetivacao()));
        q.setParameter("tipoHierarquiaAdm", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setParameter("tipoHierarquiaOrc", TipoHierarquiaOrganizacional.ORCAMENTARIA.name());
        List<VOItemBaixaPatrimonial> toReturn = Lists.newArrayList();
        for (Object[] obj : (List<Object[]>) q.getResultList()) {
            toReturn.add(VOItemBaixaPatrimonial.criarObjetoVoItemSolicitacaoBaixa(obj));
        }
        return toReturn;
    }

    private List<VOItemBaixaPatrimonial> preencherAndValidarBensPesquisadosEfetivacaoBaixa(AssistenteMovimentacaoBens assistente, List<VOItemBaixaPatrimonial> bensPesquisados) {
        if (TipoBem.MOVEIS.equals(((EfetivacaoBaixaPatrimonial) assistente.getSelecionado()).getTipoBem())) {
            return bensPesquisados;
        }
        List<VOItemBaixaPatrimonial> bensDisponiveis = Lists.newArrayList();
        if (assistente != null && assistente.getConfigMovimentacaoBem() != null && assistente.getConfigMovimentacaoBem().getValidarMovimentoRetroativo()) {
            EfetivacaoBaixaPatrimonial selecionado = (EfetivacaoBaixaPatrimonial) assistente.getSelecionado();
            List<LogErroEfetivacaoBaixaBem> inconsistencias = Lists.newArrayList();

            for (VOItemBaixaPatrimonial itemBaixa : bensPesquisados) {
                Bem bem = bemFacade.recuperarBemPorIdBem(itemBaixa.getIdBem());
                String mensagem = configMovimentacaoBemFacade.validarMovimentoComDataRetroativaBem(bem.getId(), assistente.getConfigMovimentacaoBem().getOperacaoMovimentacaoBem().getDescricao(), assistente.getDataLancamento());
                if (!mensagem.isEmpty()) {
                    LogErroEfetivacaoBaixaBem logErro = new LogErroEfetivacaoBaixaBem(selecionado, bem, mensagem);
                    inconsistencias.add(logErro);
                    continue;
                }
                bensDisponiveis.add(itemBaixa);
            }
            assistente.setLogErrosEfetivacaoBaixa(inconsistencias);
            return bensDisponiveis;
        }
        return bensPesquisados;
    }

    public BemFacade getBemFacade() {
        return bemFacade;
    }

    public IntegradorPatrimonialContabilFacade getIntegradorPatrimonialContabilFacade() {
        return integradorPatrimonialContabilFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public SingletonGeradorCodigo getSingletonGeradorCodigo() {
        return singletonGeradorCodigo;
    }

    public ParecerBaixaPatrimonialFacade getParecerBaixaPatrimonialFacade() {
        return parecerBaixaPatrimonialFacade;
    }

    public LeilaoAlienacaoFacade getLeilaoAlienacaoFacade() {
        return leilaoAlienacaoFacade;
    }

    public SolicitacaoAlienacaoFacade getSolicitacaoAlienacaoFacade() {
        return solicitacaoAlienacaoFacade;
    }

    public EntidadeFacade getEntidadeFacade() {
        return entidadeFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public SolicitacaoBaixaPatrimonialFacade getSolicitacaoBaixaPatrimonialFacade() {
        return solicitacaoBaixaPatrimonialFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public ConfigMovimentacaoBemFacade getConfigMovimentacaoBemFacade() {
        return configMovimentacaoBemFacade;
    }

    public SingletonConcorrenciaPatrimonio getSingletonBloqueioPatrimonio() {
        return singletonBloqueioPatrimonio;
    }
}
