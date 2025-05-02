package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteConsultaAvaliacaoAlienacao;
import br.com.webpublico.entidadesauxiliares.FiltroPesquisaBem;
import br.com.webpublico.entidadesauxiliares.VOItemSolicitacaoAlienacao;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SituacaoAlienacao;
import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.negocios.tributario.singletons.SingletonConcorrenciaPatrimonio;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.tratamentoerros.BuscaCausa;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


/**
 * Created by Desenvolvimento on 14/09/2017.
 */
@Stateless
public class AvaliacaoAlienacaoFacade extends AbstractFacade<AvaliacaoAlienacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private ParametroPatrimonioFacade parametroPatrimonioFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    @EJB
    private SolicitacaoAlienacaoFacade solicitacaoAlienacaoFacade;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ConfigMovimentacaoBemFacade configMovimentacaoBemFacade;
    @EJB
    private SingletonConcorrenciaPatrimonio singletonBloqueioPatrimonio;
    @EJB
    private AprovacaoAlienacaoFacade aprovacaoAlienacaoFacade;

    public AvaliacaoAlienacaoFacade() {
        super(AvaliacaoAlienacao.class);
    }

    @Override
    public AvaliacaoAlienacao recuperar(Object id) {
        AvaliacaoAlienacao av = super.recuperar(id);
        Hibernate.initialize(av.getLotes());
        for (LoteAvaliacaoAlienacao lote : av.getLotes()) {
            Hibernate.initialize(lote.getItensSolicitacaoAlienacao());
        }
        Hibernate.initialize(av.getDetentorArquivoComposicao().getArquivosComposicao());
        return av;
    }

    public AvaliacaoAlienacao recuperarDependenciasArquivo(Object id) {
        AvaliacaoAlienacao av = super.recuperar(id);
        Hibernate.initialize(av.getLotes());
        Hibernate.initialize(av.getDetentorArquivoComposicao().getArquivosComposicao());
        return av;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }


    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public AvaliacaoAlienacao concluirAvaliacao(AvaliacaoAlienacao entity, AssistenteMovimentacaoBens assistente) {
        try {
            assistente.setDescricaoProcesso("Concluindo Avaliação de Alienação...");
            if (entity.getCodigo() == null) {
                entity.setCodigo(singletonGeradorCodigo.getProximoCodigo(AvaliacaoAlienacao.class, "codigo"));
            }
            entity.setSituacao(SituacaoEventoBem.CONCLUIDO);
            entity = em.merge(entity);

            alterarSituacaoItemAvaliacao(entity.getLotes(), assistente, SituacaoEventoBem.AGUARDANDO_EFETIVACAO);

            List<Number> bens = Lists.newArrayList();
            for (LoteAvaliacaoAlienacao lote : entity.getLotes()) {
                bens.addAll(buscarIdsBensAvaliacao(lote));
            }
            configMovimentacaoBemFacade.alterarSituacaoEvento(bens, SituacaoEventoBem.AGUARDANDO_EFETIVACAO, OperacaoMovimentacaoBem.AVALIACAO_ALIENACAO_BEM);

            movimentarSituacaoSolicitacaoAlienacao(entity);

            assistente.zerarContadoresProcesso();
            assistente.setDescricaoProcesso("Finalizando Processo...");
            assistente.setSelecionado(entity);
            assistente.setDescricaoProcesso(" ");
        } catch (Exception ex) {
            logger.error("Erro ao concluir avaliação de alienação de bens móveis {} ", ex);
            throw ex;
        }
        return entity;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public AssistenteMovimentacaoBens verificarItensNaoSelecionadosParaMesmaSequenciaCodigoPatrimonial(AssistenteMovimentacaoBens assistente, AvaliacaoAlienacao selecionado, List<VOItemSolicitacaoAlienacao> bensAprovados) {
        List<String> mensagem = Lists.newArrayList();
        EntidadeSequenciaPropria primeiraEntidadeAdicionada = recuperarEntidadeGeradoraCodigoPatrimonio(selecionado.getLotes().get(0).getItensSolicitacao().get(0), assistente.getDataLancamento());
        if (!bensAprovados.isEmpty()) {
            assistente.setDescricaoProcesso("Verificando sequência do registro patrimonial para bens aprovados não adicionados ao lote...");
            assistente.zerarContadoresProcesso();
            assistente.setTotal(bensAprovados.size());

            for (VOItemSolicitacaoAlienacao itemNaoAvalidado : bensAprovados) {
                EntidadeSequenciaPropria entidadeAprovada = recuperarEntidadeGeradoraCodigoPatrimonio(itemNaoAvalidado, assistente.getDataLancamento());

                if (primeiraEntidadeAdicionada.equals(entidadeAprovada)) {
                    mensagem.add(" O bem: <b>" + itemNaoAvalidado.getRegistroPatrimonial() + "</b>, pertence a mesma sequência de registro patrimonial para o lote já adicionado. Para continuar, avalie esta bem também.");
                }
                assistente.conta();
            }
        }

        assistente.setDescricaoProcesso("Verificando sequência do registro patrimonial para bens adicionados ao lote...");
        for (LoteAvaliacaoAlienacao todosLotes : selecionado.getLotes()) {
            assistente.zerarContadoresProcesso();
            assistente.setTotal(todosLotes.getItensSolicitacao().size());

            for (VOItemSolicitacaoAlienacao itemDeCadaLote : todosLotes.getItensSolicitacao()) {
                EntidadeSequenciaPropria entidadeDaLista = recuperarEntidadeGeradoraCodigoPatrimonio(itemDeCadaLote, assistente.getDataLancamento());

                if (!primeiraEntidadeAdicionada.equals(entidadeDaLista)) {
                    mensagem.add("O bem: <b>" + itemDeCadaLote.getRegistroPatrimonial() + ".</b>  Não poderá ser avaliado, pois não segue a mesma sequência própria dos demais bens adicionados na lista.");
                }
                assistente.conta();
            }
        }
        assistente.getMensagens().addAll(mensagem);
        return assistente;
    }

    private void movimentarSituacaoSolicitacaoAlienacao(AvaliacaoAlienacao selecionado) {
        List<SolicitacaoAlienacao> solicitacoesAlienacao = solicitacaoAlienacaoFacade.buscarSolicitacaoPorAvaliacaoAlienacao(selecionado);
        for (SolicitacaoAlienacao solicitacaoAlienacao : solicitacoesAlienacao) {
            solicitacaoAlienacao.setSituacao(SituacaoAlienacao.AVALIADA);
            em.merge(solicitacaoAlienacao);
        }
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public AvaliacaoAlienacao salvarAvaliacao(AvaliacaoAlienacao entity, AssistenteMovimentacaoBens assistente, List<LoteAvaliacaoAlienacao> lotesRemovidos) {
        try {
            assistente.setDescricaoProcesso("Salvando Avaliação de Alienação...");
            for (SolicitacaoAlienacao solicitacao : assistente.getSolicitacoesAlienacao()) {
                solicitacaoAlienacaoFacade.salvarSolicitacaoControleVersao(solicitacao);
            }
            List<LoteAvaliacaoAlienacao> lotes = Lists.newArrayList(entity.getLotes());
            entity.getLotes().clear();
            AvaliacaoAlienacao avaliacaoSalva = em.merge(entity);
            removerItemAndLote(lotesRemovidos, avaliacaoSalva, assistente);
            avaliacaoSalva.setLotes(lotes);
            return avaliacaoSalva;
        } catch (Exception ex) {
            logger.error("Erro ao salvarAvaliacao {}", ex);
            throw ex;
        }
    }

    private void removerItemAndLote(List<LoteAvaliacaoAlienacao> lotesRemovidos, AvaliacaoAlienacao avaliacaoAlienacao, AssistenteMovimentacaoBens assistente) {
        if (avaliacaoAlienacao.getId() != null) {
            List<Number> itensAvaliacaoParaRemover = Lists.newArrayList();
            List<Number> itensSolicitacaoParaAtualizar = Lists.newArrayList();
            List<Number> idsBem = Lists.newArrayList();

            for (LoteAvaliacaoAlienacao lote : lotesRemovidos) {
                if (lote.getId() != null) {
                    itensAvaliacaoParaRemover.addAll(buscarIdsItemAvaliacao(lote));
                    itensSolicitacaoParaAtualizar.addAll(solicitacaoAlienacaoFacade.buscarIdsItemSolicitacaoPorLote(lote));
                    idsBem.addAll(buscarIdsBensAvaliacao(lote));
                }
            }
            removerItemDoLote(itensSolicitacaoParaAtualizar, itensAvaliacaoParaRemover);
            List<LoteAvaliacaoAlienacao> listRemover = Lists.newArrayList();
            listRemover.addAll(lotesRemovidos);
            removerLote(listRemover);

            configMovimentacaoBemFacade.deletarMovimentoBloqueioBem(assistente.getConfigMovimentacaoBem(), idsBem);
        }
    }


    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public List<LoteAvaliacaoAlienacao> salvarLotes(AvaliacaoAlienacao avaliacao, List<LoteAvaliacaoAlienacao> lotesParaProcessar, AssistenteMovimentacaoBens assistente) {
        try {
            List<LoteAvaliacaoAlienacao> retorno = Lists.newArrayList();
            assistente.setDescricaoProcesso("Criando Lote Avaliação...");
            assistente.zerarContadoresProcesso();
            assistente.setTotal(lotesParaProcessar.size());

            for (LoteAvaliacaoAlienacao loteAvaliado : lotesParaProcessar) {
                List<VOItemSolicitacaoAlienacao> itensParaProcessar = loteAvaliado.getItensSolicitacao();
                loteAvaliado.setAvaliacaoAlienacao(avaliacao);
                LoteAvaliacaoAlienacao salvo = em.merge(loteAvaliado);
                salvo.setItensSolicitacao(itensParaProcessar);
                retorno.add(salvo);
                assistente.conta();
            }
            return retorno;
        } catch (Exception ex) {
            logger.error("Erro ao criarLoteAvaliacao {}", ex);
            salvarExceptionLog(ex);
            return null;
        }
    }


    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public List<ItemSolicitacaoAlienacao> salvarItens(AssistenteMovimentacaoBens assistente, List<LoteAvaliacaoAlienacao> lotesAvaliados) {
        List<ItemSolicitacaoAlienacao> itens = Lists.newArrayList();
        List<Number> bens = Lists.newArrayList();

        assistente.setDescricaoProcesso("Criando Evento da Avaliação de Avaliação...");
        assistente.zerarContadoresProcesso();
        for (LoteAvaliacaoAlienacao loteAvaliado : lotesAvaliados) {
            assistente.setTotal(assistente.getTotal() + loteAvaliado.getItensSolicitacao().size());
        }
        for (LoteAvaliacaoAlienacao loteAvaliado : lotesAvaliados) {
            for (VOItemSolicitacaoAlienacao itemDoLote : loteAvaliado.getItensSolicitacao()) {
                ItemSolicitacaoAlienacao itemSolicitacao = em.find(ItemSolicitacaoAlienacao.class, itemDoLote.getIdItem());
                itemSolicitacao.setLoteAvaliacaoAlienacao(loteAvaliado);
                itemSolicitacao.setValorAvaliado(itemDoLote.getValorAvaliado());
                ItemSolicitacaoAlienacao itemSalvo = em.merge(itemSolicitacao);
                itens.add(itemSalvo);

                ItemAvaliacaoAlienacao itemAvaliacaoAlienacao = criarItemAvaliacaoAlienacao(loteAvaliado, itemSalvo, assistente);
                bens.add(itemAvaliacaoAlienacao.getBem().getId());
                em.merge(itemAvaliacaoAlienacao);
                assistente.conta();
            }
        }
        configMovimentacaoBemFacade.bloquearBens(assistente.getConfigMovimentacaoBem(), bens);
        return itens;
    }

    private ItemAvaliacaoAlienacao criarItemAvaliacaoAlienacao(LoteAvaliacaoAlienacao loteAvaliado, ItemSolicitacaoAlienacao itemSolicitacao, AssistenteMovimentacaoBens assistente) {
        Date dataLancamento = DataUtil.getDataComHoraAtual(assistente.getDataLancamento());
        EstadoBem ultimoEstado = bemFacade.recuperarUltimoEstadoDoBem(itemSolicitacao.getBem());
        EstadoBem estadoResultante = em.merge(bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(ultimoEstado));

        ItemAvaliacaoAlienacao itemAvaliacao = new ItemAvaliacaoAlienacao();
        itemAvaliacao.setBem(itemSolicitacao.getBem());
        itemAvaliacao.setDataLancamento(dataLancamento);
        itemAvaliacao.setValorDoLancamento(BigDecimal.ZERO);
        itemAvaliacao.setValorAvaliado(itemSolicitacao.getValorAvaliado());
        itemAvaliacao.setEstadoInicial(ultimoEstado);
        itemAvaliacao.setEstadoResultante(estadoResultante);
        itemAvaliacao.setLoteAvaliacaoAlienacao(loteAvaliado);
        itemAvaliacao.setSituacaoEventoBem(SituacaoEventoBem.EM_ELABORACAO);
        itemAvaliacao.setDetentorArquivoComposicao(itemSolicitacao.getDetentorArquivoComposicao());
        return itemAvaliacao;
    }

    private void salvarExceptionLog(Exception ex) {
        Throwable t = BuscaCausa.desenrolarException(ex);
        ExceptionLog exceptionLog = new ExceptionLog();
        exceptionLog.setDataRegistro(new Date());
        exceptionLog.setStackTrace(Util.getStackTraceDaException(t));
        exceptionLog.setTipoException(BuscaCausa.desenrolarException(t).getClass().getSimpleName());
        exceptionLog.setCausedBy(BuscaCausa.getCausedBy(t));
        em.merge(exceptionLog);
    }

    private void removerLote(List<LoteAvaliacaoAlienacao> lotesRemovidos) {
        if (lotesRemovidos != null && !lotesRemovidos.isEmpty()) {
            for (LoteAvaliacaoAlienacao loteRemovido : lotesRemovidos) {
                Query update = em.createNativeQuery("delete from LoteAvaliacaoAlienacao where id = " + loteRemovido.getId());
                update.executeUpdate();
            }
        }
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public AssistenteConsultaAvaliacaoAlienacao buscarLotesAvaliados(AvaliacaoAlienacao selecionado, FiltroPesquisaBem filtroPesquisaBem, AssistenteMovimentacaoBens assistenteBarraProg) {
        AssistenteConsultaAvaliacaoAlienacao assistente = new AssistenteConsultaAvaliacaoAlienacao();
        assistente.getBensAprovados().addAll(buscarItensAprovadosSolicitacao(filtroPesquisaBem, assistenteBarraProg));
        if (selecionado.getId() != null) {
            selecionado = recuperarDependenciasArquivo(selecionado.getId());
            List<LoteAvaliacaoAlienacao> lotes = selecionado.getLotes();
            for (LoteAvaliacaoAlienacao loteAvaliado : lotes) {
                loteAvaliado.getItensSolicitacao().addAll(buscarItensAprovadosAdicionadosLote(loteAvaliado, filtroPesquisaBem));
            }
            assistente.getLotesAvaliacao().addAll(lotes);
        }
        return assistente;
    }

    private void removerItemDoLote(List<Number> itensSolicitacao, List<Number> itensAvaliacao) {
        if (!itensSolicitacao.isEmpty()) {
            for (Number id : itensSolicitacao) {
                Query update = em.createNativeQuery("update ItemSolicitacaoAlienacao set valorAvaliado = 0.0, loteAvaliacaoAlienacao_id = null where id = " + id.longValue());
                update.executeUpdate();
            }
        }
        if (!itensAvaliacao.isEmpty()) {
            for (Number id : itensAvaliacao) {
                Query deleteItem = em.createNativeQuery(" delete from itemavaliacaoalienacao where id = " + id.longValue());
                deleteItem.executeUpdate();

                Query deleteEvento = em.createNativeQuery("delete from eventobem where id = " + id.longValue());
                deleteEvento.executeUpdate();
            }
        }
    }

    public void alterarSituacaoItemAvaliacao(List<LoteAvaliacaoAlienacao> lotesAvaliacao, AssistenteMovimentacaoBens assistente, SituacaoEventoBem situacaoEventoBem) {
        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("Atualizando Evento da Avaliação de Alienação...");
        List<Number> itensAvaliacao = Lists.newArrayList();

        for (LoteAvaliacaoAlienacao lote : lotesAvaliacao) {
            itensAvaliacao.addAll(buscarIdsItemAvaliacao(lote));
        }
        assistente.setTotal(itensAvaliacao.size());
        for (Number id : itensAvaliacao) {
            Query update = em.createNativeQuery("update eventobem set situacaoeventobem = '" + situacaoEventoBem.name() + "' where id = " + id);
            update.executeUpdate();
            assistente.conta();
        }
    }

    public List<VOItemSolicitacaoAlienacao> buscarItensAprovadosSolicitacao(FiltroPesquisaBem filtroPesquisa, AssistenteMovimentacaoBens assistente) {
        String sql = "" +
            " select  " +
            "  itemsol.id as idItem, " +
            "  bem.id as idBem, " +
            "  null as idLote, " +
            "  null as idItemLeilao, " +
            "  est.detentoraorcamentaria_id as idUnidade, " +
            "  est.detentoraadministrativa_id as idUnidadeAdm, " +
            "  est.grupobem_id as idGrupoBem, " +
            "  bem.identificacao as registro, " +
            "  bem.registroAnterior as registroAnterior,  " +
            "  bem.descricao as descricao, " +
            "  ev.dataoperacao as dataOperacao, " +
            "  vw.codigo || ' - ' || vw.descricao as unidade, " +
            "  coalesce((select estOriginal.valororiginal  " +
            "            from estadobem estOriginal  " +
            "            inner join eventobem evOriginal on evOriginal.estadoresultante_id = estOriginal.id " +
            "            where evOriginal.dataoperacao = (select max(ev1.dataoperacao) from eventobem ev1 where ev1.bem_id = evOriginal.bem_id) " +
            "            and evOriginal.bem_id = bem.id),0) as valorOriginal,  " +
            "  coalesce((select estajuste.valoracumuladodaamortizacao + estajuste.valoracumuladodadepreciacao + estajuste.valoracumuladodeajuste " +
            "            from estadobem estajuste  " +
            "            inner join eventobem evAjuste on evAjuste.estadoresultante_id = estajuste.id " +
            "            where evAjuste.dataoperacao = (select max(ev1.dataoperacao) from eventobem ev1 where ev1.bem_id = evAjuste.bem_id) " +
            "            and evAjuste.bem_id = bem.id), 0) as valorAjuste,  " +
            "  0 as valorAvaliado,   " +
            "  0 as valorArrematado,  " +
            "  bem.dataaquisicao  " +
            " from itemaprovacaoalienacao itemAprov " +
            "  inner join aprovacaoalienacao ap on ap.id = itemaprov.aprovacaoalienacao_id " +
            "  inner join eventobem ev on ev.id = itemaprov.id " +
            "  inner join estadobem est on est.id = ev.estadoresultante_id " +
            "  inner join vwhierarquiaorcamentaria vw on vw.subordinada_id = est.detentoraorcamentaria_id " +
            "    and ap.dataefetivacao between vw.iniciovigencia and coalesce(vw.fimvigencia, ap.dataefetivacao) " +
            "  inner join bem on bem.id = ev.bem_id " +
            "  inner join itemsolicitacaoalienacao itemSol on itemSol.id = itemaprov.itemsolicitacaoalienacao_id " +
            " where itemsol.loteavaliacaoalienacao_id is null " +
            "  and ap.situacaoefetivacao = :situacao ";
        sql += filtroPesquisa.getHierarquiaAdministrativa() != null ? " and est.detentoraadministrativa_id = :idUnidadeAdm " : " ";
        sql += filtroPesquisa.getHierarquiaOrcamentaria() != null ? " and est.detentoraorcamentaria_id = :idUnidadeOrc " : " ";
        sql += filtroPesquisa.getGrupoBem() != null ? " and est.grupobem_id = :idGrupoBem " : " ";
        sql += filtroPesquisa.getBem() != null ? " and bem.id = :idBem" : " ";
        sql += " order by to_number(bem.identificacao) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("situacao", SituacaoAlienacao.APROVADA.name());
        if (filtroPesquisa.getHierarquiaAdministrativa() != null) {
            q.setParameter("idUnidadeAdm", filtroPesquisa.getHierarquiaAdministrativa().getSubordinada().getId());
        }
        if (filtroPesquisa.getHierarquiaOrcamentaria() != null) {
            q.setParameter("idUnidadeOrc", filtroPesquisa.getHierarquiaOrcamentaria().getSubordinada().getId());
        }
        if (filtroPesquisa.getGrupoBem() != null) {
            q.setParameter("idGrupoBem", filtroPesquisa.getGrupoBem().getId());
        }
        if (filtroPesquisa.getBem() != null) {
            q.setParameter("idBem", filtroPesquisa.getBem().getId());
        }
        List<Object[]> objetos = q.getResultList();
        List<VOItemSolicitacaoAlienacao> toReturn = new ArrayList<>();
        for (Object[] obj : objetos) {
            VOItemSolicitacaoAlienacao item = VOItemSolicitacaoAlienacao.criarObjetoVoItemSolicitacaoAlienacao(obj);
            toReturn.add(item);
        }
        solicitacaoAlienacaoFacade.verificarBensDisponiveisParaMovimentacao(assistente, toReturn);
        return toReturn;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public List<VOItemSolicitacaoAlienacao> buscarItensAprovadosAdicionadosLote(LoteAvaliacaoAlienacao loteAvaliacao, FiltroPesquisaBem filtroPesquisa) {
        String sql = "" +
            " select   " +
            "  item.id as idItem,  " +
            "  bem.id as idBem,  " +
            "  lote.id as idLote,  " +
            "  null as idItemLeilao, " +
            "  est.detentoraorcamentaria_id as idUnidade,  " +
            "  est.detentoraadministrativa_id as idUnidadeAdm, " +
            "  est.grupobem_id as idGrupoBem, " +
            "  bem.identificacao as registro,  " +
            "  bem.registroAnterior as registroAnterior,  " +
            "  bem.descricao as descricao,  " +
            "  ev.dataoperacao as dataOperacao,  " +
            "  vw.codigo || ' - ' || vw.descricao as unidade,  " +
            "  coalesce((select estOriginal.valororiginal  " +
            "            from estadobem estOriginal  " +
            "            inner join eventobem evOriginal on evOriginal.estadoresultante_id = estOriginal.id " +
            "            where evOriginal.dataoperacao = (select max(ev1.dataoperacao) from eventobem ev1 where ev1.bem_id = evOriginal.bem_id) " +
            "            and evOriginal.bem_id = bem.id),0) as valorOriginal,  " +
            "  coalesce((select estajuste.valoracumuladodaamortizacao + estajuste.valoracumuladodadepreciacao + estajuste.valoracumuladodeajuste " +
            "            from estadobem estajuste  " +
            "            inner join eventobem evAjuste on evAjuste.estadoresultante_id = estajuste.id " +
            "            where evAjuste.dataoperacao = (select max(ev1.dataoperacao) from eventobem ev1 where ev1.bem_id = evAjuste.bem_id) " +
            "            and evAjuste.bem_id = bem.id), 0) as valorAjuste,  " +
            "  coalesce(item.valoravaliado, 0) as valorAvaliadao, " +
            "  0 as valorArrematado,  " +
            "  bem.dataaquisicao  " +
            " from avaliacaoalienacao ap " +
            "  inner join loteavaliacaoalienacao lote on lote.avaliacaoalienacao_id = ap.id " +
            "  inner join itemsolicitacaoalienacao item on item.loteavaliacaoalienacao_id = lote.id  " +
            "  inner join eventobem ev on ev.id = item.id  " +
            "  inner join estadobem est on est.id = ev.estadoresultante_id  " +
            "  inner join vwhierarquiaorcamentaria vw on vw.subordinada_id = est.detentoraorcamentaria_id  " +
            "    and ev.dataoperacao between vw.iniciovigencia and coalesce(vw.fimvigencia, ev.dataoperacao)  " +
            "  inner join bem on bem.id = ev.bem_id  " +
            "  where lote.id = :idLote ";
        sql += filtroPesquisa.getHierarquiaAdministrativa() != null ? " and est.detentoraadministrativa_id = :idUnidadeAdm " : " ";
        sql += filtroPesquisa.getHierarquiaOrcamentaria() != null ? " and est.detentoraorcamentaria_id = :idUnidadeOrc " : " ";
        sql += filtroPesquisa.getGrupoBem() != null ? " and est.grupobem_id = :idGrupoBem " : " ";
        sql += filtroPesquisa.getBem() != null ? " and bem.id = :idBem" : " ";
        sql += " order by to_number(bem.identificacao) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idLote", loteAvaliacao.getId());
        if (filtroPesquisa.getHierarquiaAdministrativa() != null) {
            q.setParameter("idUnidadeAdm", filtroPesquisa.getHierarquiaAdministrativa().getSubordinada().getId());
        }
        if (filtroPesquisa.getHierarquiaOrcamentaria() != null) {
            q.setParameter("idUnidadeOrc", filtroPesquisa.getHierarquiaOrcamentaria().getSubordinada().getId());
        }
        if (filtroPesquisa.getGrupoBem() != null) {
            q.setParameter("idGrupoBem", filtroPesquisa.getGrupoBem().getId());
        }
        if (filtroPesquisa.getBem() != null) {
            q.setParameter("idBem", filtroPesquisa.getBem().getId());
        }
        List<VOItemSolicitacaoAlienacao> itens = Lists.newArrayList();
        for (Object[] obj : (List<Object[]>) q.getResultList()) {
            VOItemSolicitacaoAlienacao itemVo = VOItemSolicitacaoAlienacao.criarObjetoVoItemSolicitacaoAlienacao(obj);
            itens.add(itemVo);
        }
        return itens;
    }

    public List<AvaliacaoAlienacao> buscarAvaliacaoAlienacaoParaLeilao(String parte) {
        String sql = " " +
            " select " +
            "   av.* " +
            " from avaliacaoalienacao av " +
            "   where av.situacao = '" + SituacaoEventoBem.CONCLUIDO.name() + "' " +
            "   and not exists (select 1 " +
            "                   from LeilaoAlienacao leilao " +
            "                   where leilao.avaliacaoalienacao_id = av.id) " +
            "   and (av.codigo like :parte or LOWER(av.descricao) like :parte) ";
        Query q = em.createNativeQuery(sql, AvaliacaoAlienacao.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        return q.getResultList();
    }

    public List<Number> buscarIdsItemAvaliacao(LoteAvaliacaoAlienacao loteAvaliacaoAlienacao) {
        String sql = " " +
            " select " +
            "   item.id " +
            " from itemavaliacaoalienacao item " +
            "   where item.loteavaliacaoalienacao_id = :idLote ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idLote", loteAvaliacaoAlienacao.getId());
        return q.getResultList();
    }


    public List<Number> buscarIdsBensAvaliacao(LoteAvaliacaoAlienacao loteAvaliacaoAlienacao) {
        String sql = " " +
            " select " +
            "   bem.id " +
            " from itemavaliacaoalienacao item " +
            "   inner join eventobem ev on ev.id = item.id " +
            "   inner join bem on bem.id = ev.bem_id" +
            "   where item.loteavaliacaoalienacao_id = :idLote ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idLote", loteAvaliacaoAlienacao.getId());
        return q.getResultList();
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public void removerSelecionado(AvaliacaoAlienacao entity) {
        AssistenteMovimentacaoBens assistente = new AssistenteMovimentacaoBens(entity.getDataAvaliacao(), Operacoes.VER);
        ConfigMovimentacaoBem configBem = configMovimentacaoBemFacade.buscarConfiguracaoMovimentacaoBem(entity.getDataAvaliacao(), OperacaoMovimentacaoBem.AVALIACAO_ALIENACAO_BEM);
        assistente.setConfigMovimentacaoBem(configBem);

        removerItemAndLote(entity.getLotes(), entity, assistente);
        entity.setDetentorArquivoComposicao(null);
        em.remove(em.find(AvaliacaoAlienacao.class, entity.getId()));
    }

    public EntidadeSequenciaPropria recuperarEntidadeGeradoraCodigoPatrimonio(VOItemSolicitacaoAlienacao itemSolicitacao, Date dataOperacao) {
        Entidade entidade = entidadeFacade.recuperarEntidadeOrcamentaria(itemSolicitacao.getIdUnidadeOrc(), dataOperacao);
        if (entidade != null) {
            EntidadeSequenciaPropria entidadeSequenciaPropria = parametroPatrimonioFacade.recuperarSequenciaPropriaPorTipoGeracao(entidade, TipoBem.MOVEIS);
            if (entidadeSequenciaPropria != null) {
                return entidadeSequenciaPropria;
            }
            throw new ExcecaoNegocioGenerica("Entidade Sequência Própria não encontrada para entidade: " + entidade + ".");
        }
        throw new ExcecaoNegocioGenerica("Entidade não encontrada para a unidade orçamentária resultande: " + itemSolicitacao.getUnidadeOrcamentaria() + ".");
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ParametroPatrimonioFacade getParametroPatrimonioFacade() {
        return parametroPatrimonioFacade;
    }

    public SolicitacaoAlienacaoFacade getSolicitacaoAlienacaoFacade() {
        return solicitacaoAlienacaoFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public void setHierarquiaOrganizacionalFacade(HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade) {
        this.hierarquiaOrganizacionalFacade = hierarquiaOrganizacionalFacade;
    }

    public BemFacade getBemFacade() {
        return bemFacade;
    }

    public ConfigMovimentacaoBemFacade getConfigMovimentacaoBemFacade() {
        return configMovimentacaoBemFacade;
    }

    public SingletonConcorrenciaPatrimonio getSingletonBloqueioPatrimonio() {
        return singletonBloqueioPatrimonio;
    }

    public AprovacaoAlienacaoFacade getAprovacaoAlienacaoFacade() {
        return aprovacaoAlienacaoFacade;
    }
}
