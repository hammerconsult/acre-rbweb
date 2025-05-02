package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.VOItemBaixaPatrimonial;
import br.com.webpublico.enums.SituacaoBaixaPatrimonial;
import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.SituacaoParecer;
import br.com.webpublico.enums.TipoBem;
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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 11/06/14
 * Time: 08:47
 * To change this template use File | Settings | File Templates.
 */

@Stateless
public class ParecerBaixaPatrimonialFacade extends AbstractFacade<ParecerBaixaPatrimonial> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SolicitacaoBaixaPatrimonialFacade solicitacaoFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private ConfigMovimentacaoBemFacade configMovimentacaoBemFacade;
    @EJB
    private SingletonConcorrenciaPatrimonio singletonBloqueioPatrimonio;
    @EJB
    private EntidadeFacade entidadeFacade;

    public ParecerBaixaPatrimonialFacade() {
        super(ParecerBaixaPatrimonial.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ParecerBaixaPatrimonial recuperar(Object id) {
        ParecerBaixaPatrimonial parecer = super.recuperar(id);
        parecer.getListaItemParecer().size();
        if (parecer.getDetentorArquivoComposicao() != null) {
            parecer.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        if (parecer.getSolicitacaoBaixa() != null) {
            parecer.setSolicitacaoBaixa(solicitacaoFacade.recuperar(parecer.getSolicitacaoBaixa().getId()));
        }
        return parecer;
    }

    public ParecerBaixaPatrimonial recuperarComDependeciaArquivo(Object id) {
        ParecerBaixaPatrimonial parecer = super.recuperar(id);
        if (parecer.getDetentorArquivoComposicao() != null) {
            parecer.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        return parecer;
    }

    @Override
    public void salvar(ParecerBaixaPatrimonial entity) {
        super.salvarNovo(entity);
    }

    public List<VOItemBaixaPatrimonial> buscarBensParecerBaixa(ParecerBaixaPatrimonial selecionado) {
        List<VOItemBaixaPatrimonial> toReturn = Lists.newArrayList();
        buscarBens(selecionado, toReturn);
        return toReturn;
    }

    public void buscarBens(ParecerBaixaPatrimonial selecionado, List<VOItemBaixaPatrimonial> toReturn) {
        List<HierarquiaOrganizacional> hierarquias = solicitacaoFacade.buscarHierarquiaAdministrativaItemSolicitacaoBaixa(selecionado.getSolicitacaoBaixa());
        if (selecionado.getSolicitacaoBaixa().isTipoBaixaAlienacao()) {
            for (HierarquiaOrganizacional hierarquia : hierarquias) {
                VOItemBaixaPatrimonial item = new VOItemBaixaPatrimonial();
                item.setHierarquiaAdministrativa(hierarquia);
                item.setBensAgrupados(buscarItensParecerBaixa(selecionado, hierarquia.getSubordinada()));
                toReturn.add(item);
            }
        } else {
            List<VOItemBaixaPatrimonial> bens = buscarItensParecerBaixa(selecionado, null);
            toReturn.addAll(bens);
        }
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public List<VOItemBaixaPatrimonial> buscarBensSolicitacaoBaixa(ParecerBaixaPatrimonial selecionado, AssistenteMovimentacaoBens assistente) {
        List<VOItemBaixaPatrimonial> toReturn = Lists.newArrayList();
        buscarBensSolicitacaoBaixa(selecionado, toReturn, assistente);
        return toReturn;
    }

    private void buscarBensSolicitacaoBaixa(ParecerBaixaPatrimonial selecionado, List<VOItemBaixaPatrimonial> toReturn, AssistenteMovimentacaoBens assistente) {
        List<HierarquiaOrganizacional> hierarquias = solicitacaoFacade.buscarHierarquiaAdministrativaItemSolicitacaoBaixa(selecionado.getSolicitacaoBaixa());
        if (selecionado.getSolicitacaoBaixa().isTipoBaixaAlienacao()) {
            for (HierarquiaOrganizacional hierarquia : hierarquias) {
                VOItemBaixaPatrimonial item = new VOItemBaixaPatrimonial();
                item.setHierarquiaAdministrativa(hierarquia);
                item.setBensAgrupados(solicitacaoFacade.buscarItensSolicitacaoBaixa(selecionado.getSolicitacaoBaixa(), hierarquia.getSubordinada(), assistente));
                toReturn.add(item);
            }
        } else {
            List<VOItemBaixaPatrimonial> bens = solicitacaoFacade.buscarItensSolicitacaoBaixa(selecionado.getSolicitacaoBaixa(), null, assistente);
            toReturn.addAll(bens);
        }
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public ParecerBaixaPatrimonial salvarRetornando(ParecerBaixaPatrimonial selecionado, List<VOItemBaixaPatrimonial> bensSolicitacao,
                                                            AssistenteMovimentacaoBens assistente) {
        try {
            atribuirSituacaoParaSolicitacaoBaixa(selecionado);
            if (selecionado.getCodigo() == null) {
                selecionado.setCodigo(singletonGeradorCodigo.getProximoCodigo(ParecerBaixaPatrimonial.class, "codigo"));
            }

            selecionado = em.merge(selecionado);
            salvarProcessandoBens(selecionado, bensSolicitacao, assistente);

            for (VOItemBaixaPatrimonial voItem : bensSolicitacao) {
                ItemSolicitacaoBaixaPatrimonial itemSolicitacao = em.find(ItemSolicitacaoBaixaPatrimonial.class, voItem.getIdItem());
                itemSolicitacao.setSituacaoEventoBem(selecionado.deferido() ? SituacaoEventoBem.AGUARDANDO_EFETIVACAO : SituacaoEventoBem.FINALIZADO);
                em.merge(itemSolicitacao);
            }
            desbloquearBens(bensSolicitacao, assistente.getConfigMovimentacaoBem(), selecionado);

            assistente.zerarContadoresProcesso();
            assistente.setDescricaoProcesso("Finalizando Processo...");
            assistente.setSelecionado(selecionado);
            assistente.setDescricaoProcesso(" ");
        } catch (Exception ex) {
            logger.error("Erro ao salvar parecer de baixa de bens móveis. " + ex.getMessage());
            throw ex;
        }
        return selecionado;
    }

    private void atribuirSituacaoParaSolicitacaoBaixa(ParecerBaixaPatrimonial selecionado) {
        SolicitacaoBaixaPatrimonial solicitacaoBaixa = selecionado.getSolicitacaoBaixa();
        solicitacaoBaixa.setSituacao(selecionado.deferido() ? SituacaoBaixaPatrimonial.AGUARDANDO_EFETIVACAO : SituacaoBaixaPatrimonial.FINALIZADA);
        em.merge(solicitacaoBaixa);
    }

    private void desbloquearBens(List<VOItemBaixaPatrimonial> bensSolicitacao, ConfigMovimentacaoBem configuracao, ParecerBaixaPatrimonial parecer) {
        if (TipoBem.MOVEIS.equals(parecer.getTipoBem())) {
            List<Number> idsBemParaDesbloquear = Lists.newArrayList();
            for (VOItemBaixaPatrimonial bem : bensSolicitacao) {
                idsBemParaDesbloquear.add(bem.getIdBem());
            }
            if (parecer.deferido()) {
                configMovimentacaoBemFacade.bloquearBens(configuracao, idsBemParaDesbloquear);
            } else {
                configMovimentacaoBemFacade.desbloquearBens(configuracao, idsBemParaDesbloquear);
            }
        }
    }

    private void salvarProcessandoBens(ParecerBaixaPatrimonial selecionado, List<VOItemBaixaPatrimonial> bensSolicitacao, AssistenteMovimentacaoBens assistente) {
        assistente.zerarContadoresProcesso();
        assistente.setTotal(bensSolicitacao.size());
        assistente.setDescricaoProcesso("Criando Eventos Parecer Baixa Patrimonial de Bens " + selecionado.getTipoBem().getDescricao() + "...");

        Date dataLancamento = DataUtil.getDataComHoraAtual(assistente.getDataLancamento());
        for (VOItemBaixaPatrimonial item : bensSolicitacao) {
            ItemParecerBaixaPatrimonial novoItemParecer = criarItemParecerBaixa(selecionado, item, dataLancamento);
            em.merge(novoItemParecer);
            assistente.conta();
        }
        if (!selecionado.deferido()) {

        }
    }

    private ItemParecerBaixaPatrimonial criarItemParecerBaixa(ParecerBaixaPatrimonial selecionado, VOItemBaixaPatrimonial itemSolicitacao, Date dataLancamento) {

        EstadoBem ultimoEstado = bemFacade.recuperarUltimoEstadoDoBem(itemSolicitacao.getBemEfetivacao());
        EstadoBem estadoResultante = em.merge(bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(ultimoEstado));
        SituacaoEventoBem situacaoEventoBem = selecionado.deferido() ? SituacaoEventoBem.AGUARDANDO_EFETIVACAO : SituacaoEventoBem.RECUSADO;

        ItemParecerBaixaPatrimonial itemParecer = new ItemParecerBaixaPatrimonial();
        itemParecer.setDataLancamento(dataLancamento);
        itemParecer.setSituacaoEventoBem(situacaoEventoBem);
        itemParecer.setBem(itemSolicitacao.getBemEfetivacao());
        itemParecer.setParecerBaixa(selecionado);
        itemParecer.setEstadoInicial(ultimoEstado);
        itemParecer.setEstadoResultante(estadoResultante);
        return itemParecer;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public List<VOItemBaixaPatrimonial> buscarItensParecerBaixa(ParecerBaixaPatrimonial selecionado, UnidadeOrganizacional unidadeOrganizacional) {
        String sql = "" +
            " select  " +
            "  item.id as idItem, " +
            "  bem.id as idBem,  " +
            "  null as idLote, " +
            "  est.detentoraorcamentaria_id as idUnidadeOrc,  " +
            "  est.detentoraadministrativa_id as idUnidadeAdm,  " +
            "  bem.identificacao as registro,  " +
            "  bem.descricao as descricao,  " +
            "  bem.dataaquisicao as dataAquisicao,  " +
            "  est.estadobem as estadoConservacao, " +
            "  vw.codigo || ' - ' || vw.descricao as unidade,  " +
            "  vwadm.codigo || ' - ' || vwadm.descricao as unidadeAdm,   " +
            "  coalesce(est.valororiginal,0) as valorOriginal,  " +
            "  coalesce(est.valoracumuladodadepreciacao, 0) as valorDepreciacao,   " +
            "  coalesce(est.valoracumuladodaamortizacao, 0) as valorAmortizacao,   " +
            "  coalesce(est.valoracumuladodaexaustao, 0) as valorExaustao,   " +
            "  coalesce(est.valoracumuladodeajuste, 0) as valorAjustePerda,  " +
            "  coalesce(est.valoracumuladodaamortizacao, 0) +   " +
            "  coalesce(est.valoracumuladodadepreciacao, 0) +   " +
            "  coalesce(est.valoracumuladodaexaustao, 0) +   " +
            "  coalesce(est.valoracumuladodeajuste, 0) as valorAjuste,  " +
            "  0 as valorAvaliadao,   " +
            "  0 as valorProporcionalArrematado,   " +
            "  grupo.codigo || ' - ' || grupo.descricao as grupoPatrionial  " +
            " from parecerbaixapatrimonial parec " +
            "  inner join itemparecerebaixa item on item.parecerbaixa_id = parec.id " +
            "  inner join eventobem ev on ev.id = item.id  " +
            "  inner join estadobem est on est.id = ev.estadoresultante_id  " +
            "  inner join grupobem grupo on grupo.id = est.grupobem_id " +
            "  inner join vwhierarquiaorcamentaria vw on vw.subordinada_id = est.detentoraorcamentaria_id  " +
            "    and parec.dateparecer between vw.iniciovigencia and coalesce(vw.fimvigencia, parec.dateparecer)  " +
            "  inner join vwhierarquiaadministrativa vwadm on vwadm.subordinada_id = est.detentoraadministrativa_id   " +
            "    and parec.dateparecer between vwadm.iniciovigencia and coalesce(vwadm.fimvigencia, parec.dateparecer)   " +
            "  inner join bem on bem.id = ev.bem_id    " +
            "  where parec.id = :idSolicitacao ";
        if (unidadeOrganizacional != null) {
            sql += " and vwadm.subordinada_id = :idUniade ";
        }
        sql += " order by to_number(bem.identificacao) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacao", selecionado.getId());
        if (unidadeOrganizacional != null) {
            q.setParameter("idUniade", unidadeOrganizacional.getId());
        }
        List<VOItemBaixaPatrimonial> toReturn = Lists.newArrayList();
        for (Object[] obj : (List<Object[]>) q.getResultList()) {
            VOItemBaixaPatrimonial item = VOItemBaixaPatrimonial.criarObjetoVoItemSolicitacaoBaixa(obj);
            item.setBemEfetivacao(bemFacade.recuperarBemPorIdBem(item.getIdBem().longValue()));
            toReturn.add(item);
        }
        return toReturn;
    }

    public List<ParecerBaixaPatrimonial> buscarParecerDeferido(String filtro, TipoBem tipoBem) {
        String sql = " SELECT P.* " +
            "     FROM PARECERBAIXAPATRIMONIAL P  " +
            "       INNER JOIN SOLICITABAIXAPATRIMONIAL S ON P.SOLICITACAOBAIXA_ID = S.ID " +
            "     WHERE P.TIPOBEM = :tipoBem AND P.SITUACAOPARECER = :situacaoParecer " +
            "       AND ((TO_CHAR(S.CODIGO) = :filtroIgual ) " +
            "       or lower(translate(s.MOTIVO,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:filtro,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc') " +
            "        )" +
            "       AND S.SITUACAO = :situacaoBaixa" +
            "       and not exists (select 1 from " +
            "                       efetivacaobaixapatrimonial ef " +
            "                       where ef.parecerbaixapatrimonial_id = p.id " +
            "                       )" +
            " ORDER BY S.CODIGO DESC ";
        Query q = em.createNativeQuery(sql, ParecerBaixaPatrimonial.class);
        q.setParameter("situacaoParecer", SituacaoParecer.DEFERIDO.name());
        q.setParameter("filtro", "%" + filtro.toLowerCase() + "%");
        q.setParameter("filtroIgual", filtro);
        q.setParameter("situacaoBaixa", SituacaoBaixaPatrimonial.AGUARDANDO_EFETIVACAO.name());
        q.setParameter("tipoBem", tipoBem.name());
        q.setMaxResults(50);
        return q.getResultList();
    }

    public List<ParecerBaixaPatrimonial> buscarFiltrandoParecerBaixa(String filtro, TipoBem tipoBem) {
        String sql = " SELECT P.* " +
            "     FROM PARECERBAIXAPATRIMONIAL P  " +
            "       INNER JOIN SOLICITABAIXAPATRIMONIAL S ON P.SOLICITACAOBAIXA_ID = S.ID " +
            "     WHERE P.TIPOBEM = :tipoBem " +
            "       AND ((TO_CHAR(S.CODIGO)                        LIKE :FILTRO ) " +
            "           OR (TO_CHAR(S.DATASOLICITACAO, 'dd/MM/yyyy') LIKE :FILTRO  ) " +
            "           OR (TO_CHAR(LOWER(S.MOTIVO))                 LIKE :FILTRO ))" +
            " ORDER BY S.CODIGO DESC ";
        Query q = em.createNativeQuery(sql, ParecerBaixaPatrimonial.class);
        q.setParameter("FILTRO", "%" + filtro.toLowerCase() + "%");
        q.setParameter("tipoBem", tipoBem.name());
        q.setMaxResults(50);
        return q.getResultList();
    }

    public List<Long> buscarIdsItemParecerBaixa(ParecerBaixaPatrimonial parecer) {
        String sql = "  " +
            " select " +
            "  item.id " +
            " from itemparecerebaixa item " +
            "  inner join eventobem ev on ev.id = item.id " +
            " where item.parecerBaixa_id = :idParecer ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idParecer", parecer.getId());
        List<Long> toReturn = Lists.newArrayList();
        try {
            List<BigDecimal> ids = q.getResultList();
            if (!ids.isEmpty()) {
                for (BigDecimal id : ids) {
                    toReturn.add(id.longValue());
                }
            }
        } catch (Exception ex) {
            logger.error("buscarIdsItemParecerBaixa {}", ex);
        }
        return toReturn;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<List<VOItemBaixaPatrimonial>> buscarBensParecerBaixaParaEfetivacao(ParecerBaixaPatrimonial parecer) {
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
        List<VOItemBaixaPatrimonial> toReturn = Lists.newArrayList();
        for (Object[] obj : (List<Object[]>) q.getResultList()) {
            toReturn.add(VOItemBaixaPatrimonial.criarObjetoVoItemSolicitacaoBaixa(obj));
        }
        return new AsyncResult<List<VOItemBaixaPatrimonial>>(toReturn);
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public void gerarItensParecerBaixaCorrecaoDados(ParecerBaixaPatrimonial parecerBaixaPatrimonial, List<VOItemBaixaPatrimonial> bens) {
        for (VOItemBaixaPatrimonial item : bens) {
            for (VOItemBaixaPatrimonial itemSolicitacao : item.getBensAgrupados()) {
                ItemSolicitacaoBaixaPatrimonial itemBaixa = em.find(ItemSolicitacaoBaixaPatrimonial.class, itemSolicitacao.getIdItem());
                EstadoBem ultimoEstado = itemBaixa.getEstadoResultante();
                EstadoBem estadoResultante = em.merge(bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(ultimoEstado));

                ItemParecerBaixaPatrimonial itemParecer = new ItemParecerBaixaPatrimonial();
                itemParecer.setBem(itemSolicitacao.getBemEfetivacao());
                itemParecer.setParecerBaixa(parecerBaixaPatrimonial);
                itemParecer.setSituacaoEventoBem(SituacaoEventoBem.FINALIZADO);
                itemParecer.setDataLancamento(itemBaixa.getDataLancamento());
                itemParecer.setEstadoInicial(ultimoEstado);
                itemParecer.setEstadoResultante(estadoResultante);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(itemBaixa.getDataOperacao());
                calendar.set(Calendar.MINUTE, calendar.getTime().getMinutes() + 1);
                itemParecer.setDataOperacao( calendar.getTime());
                em.merge(itemParecer);

            }
        }
    }

    public List<VOItemBaixaPatrimonial> buscarBensSolicitacaoBaixaCorrecaoDados(ParecerBaixaPatrimonial selecionado) {
        List<VOItemBaixaPatrimonial> toReturn = Lists.newArrayList();
        buscarBensSolicitacaoBaixa(selecionado, toReturn, null);
        return toReturn;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public BemFacade getBemFacade() {
        return bemFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public SolicitacaoBaixaPatrimonialFacade getSolicitacaoFacade() {
        return solicitacaoFacade;
    }

    public SingletonGeradorCodigo getSingletonGeradorCodigo() {
        return singletonGeradorCodigo;
    }

    public ConfigMovimentacaoBemFacade getConfigMovimentacaoBemFacade() {
        return configMovimentacaoBemFacade;
    }

    public SingletonConcorrenciaPatrimonio getSingletonBloqueioPatrimonio() {
        return singletonBloqueioPatrimonio;
    }

    public EntidadeFacade getEntidadeFacade() {
        return entidadeFacade;
    }
}
