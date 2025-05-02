package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroPesquisaBem;
import br.com.webpublico.entidadesauxiliares.RelatorioSolicitacaoEstorno;
import br.com.webpublico.enums.EstadoConservacaoBem;
import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 24/09/14
 * Time: 08:46
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class SolicitacaoEstornoTransferenciaFacade extends AbstractFacade<SolicitacaoEstornoTransferencia> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private LoteEfetivacaoTransferenciaBemFacade lotefacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private InventarioBensMoveisFacade inventarioBensMoveisFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ConfigMovimentacaoBemFacade configMovimentacaoBemFacade;

    public SolicitacaoEstornoTransferenciaFacade() {
        super(SolicitacaoEstornoTransferencia.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public SolicitacaoEstornoTransferencia recuperar(Object id) {
        SolicitacaoEstornoTransferencia sol = super.recuperar(id);
        if (sol.getDetentorArquivoComposicao() != null) {
            sol.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        return sol;
    }

    public List<SolicitacaoEstornoTransferencia> completaSolicitacaoEstornoSemEfetivacao(String filtro, SistemaFacade sistemaFacade, TipoBem tipoBem) {
        String sql = "SELECT " +
            "     SOL.*  " +
            "     FROM SOLICITESTORNOTRANSFER SOL " +
            " INNER JOIN LOTEEFETTRANSFBEM LOTEEF  ON SOL.LOTEEFETIVACAOTRANSFERENCIA_ID = LOTEEF.ID " +
            " INNER JOIN UNIDADEORGANIZACIONAL UND ON UND.ID = LOTEEF.UNIDADEORGANIZACIONAL_ID " +
            " WHERE (TO_CHAR(SOL.CODIGO) LIKE :filtro OR LOWER(UND.DESCRICAO) LIKE :filtro) " +
            "      AND SOL.SITUACAO = :SITUACAO " +
            "      AND LOTEEF.USUARIOSISTEMA_ID = :USU" +
            "      AND  EXTRACT(YEAR FROM SOL.DATADECRIACAO) = :ANO" +
            "      AND LOTEEF.TIPOBEM = :tipobem " +
            " ORDER BY SOL.CODIGO DESC ";

        Query q = em.createNativeQuery(sql, SolicitacaoEstornoTransferencia.class);
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        q.setParameter("SITUACAO", SituacaoEventoBem.AGUARDANDO_EFETIVACAO.name());
        q.setParameter("USU", sistemaFacade.getUsuarioCorrente().getId());
        q.setParameter("tipobem", tipoBem.name());
        q.setParameter("ANO", sistemaFacade.getExercicioCorrente().getAno().toString());
        return q.getResultList();
    }

    public List<ItemEstornoTransferencia> recuperarItensSolicitacaoEstorno(SolicitacaoEstornoTransferencia solicitacao) {
        String sql = "select ev.*, item.* " +
            "       from ItemEstornoTransferencia item" +
            "     inner join eventobem ev on ev.id = item.id" +
            "       where item.solicitacaoEstorno_id = :sol ";
        Query q = em.createNativeQuery(sql, ItemEstornoTransferencia.class);
        q.setParameter("sol", solicitacao.getId());
        return q.getResultList();
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<SolicitacaoEstornoTransferencia> salvarSolicitacao(SolicitacaoEstornoTransferencia entity, List<Bem> bens, AssistenteMovimentacaoBens assistente) {
        try {
            entity = em.merge(entity);
            processarBensRemovidos(entity, assistente.getConfigMovimentacaoBem());
            criarItemEstornoTransferencia(entity, bens, assistente, SituacaoEventoBem.EM_ELABORACAO);
            bloquearBens(bens, assistente.getConfigMovimentacaoBem());
            assistente.zerarContadoresProcesso();
            assistente.setDescricaoProcesso("Persistindo Registros...");
            assistente.setDescricaoProcesso(" ");
        } catch (Exception ex) {
            logger.error("Erro ao salvar solicitação de estorno de transferência de bens {}" + ex);
        }
        return new AsyncResult<>(entity);
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<SolicitacaoEstornoTransferencia> concluirSolicitacao(SolicitacaoEstornoTransferencia entity, List<Bem> bens, AssistenteMovimentacaoBens assistente) {
        try {
            entity = em.merge(entity);
            processarBensRemovidos(entity, assistente.getConfigMovimentacaoBem());
            criarItemEstornoTransferencia(entity, bens, assistente, SituacaoEventoBem.AGUARDANDO_EFETIVACAO);
            bloquearBens(bens, assistente.getConfigMovimentacaoBem());

            entity.setSituacao(SituacaoEventoBem.AGUARDANDO_EFETIVACAO);
            if (entity.getCodigo() == null) {
                entity.setCodigo(singletonGeradorCodigo.getProximoCodigo(SolicitacaoEstornoTransferencia.class, "codigo"));
            }
            assistente.zerarContadoresProcesso();
            assistente.setDescricaoProcesso("Persistindo Registros...");
            entity = em.merge(entity);
            assistente.setDescricaoProcesso(" ");
        } catch (Exception ex) {
            logger.error("Erro ao concluir solicitação de estorno de transferência de bens  {}" + ex);
        }
        return new AsyncResult<>(entity);
    }

    private void criarItemEstornoTransferencia(SolicitacaoEstornoTransferencia entity, List<Bem> bens, AssistenteMovimentacaoBens assistente, SituacaoEventoBem situacaoEventoBem) {
        assistente.setDescricaoProcesso("Criando Evento Solicitação de Estorno de Trasnferância...");
        assistente.zerarContadoresProcesso();
        assistente.setTotal(bens.size());

        for (Bem bem : bens) {
            ItemEstornoTransferencia novoItem = new ItemEstornoTransferencia();
            Date dataLancamento = DataUtil.getDataComHoraAtual(assistente.getDataLancamento());
            novoItem.setDataLancamento(dataLancamento);
            novoItem.setSolicitacaoEstorno(entity);
            novoItem.setSituacaoEventoBem(situacaoEventoBem);
            EstadoBem estadoInicial = bemFacade.recuperarUltimoEstadoDoBem(bem);
            EstadoBem estadoResultante = em.merge(bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(estadoInicial));

            novoItem.setEstadoInicial(estadoInicial);
            novoItem.setEstadoResultante(estadoResultante);
            Number idItemEfetivacao = recuperarIdItemEfetivacaoTransferencia(entity.getLoteEfetivacaoTransferencia(), bem);
            if (idItemEfetivacao != null) {
                novoItem.setEfetivacaoTransferencia(new EfetivacaoTransferenciaBem(idItemEfetivacao.longValue()));
            }
            novoItem.setBem(bem);
            em.merge(novoItem);
            assistente.conta();
        }
    }

    private void processarBensRemovidos(SolicitacaoEstornoTransferencia entity, ConfigMovimentacaoBem configuracao) {
        if (entity.getId() != null) {
            List<Number> idsBem = buscarIdsBemPorSolicitacaoEstornoTransferencia(entity);
            desbloquearBens(idsBem, configuracao);

            List<Number> idsItemSolicitacao = buscarIdsItemSolicitacao(entity);
            List<Number> idsEstadoResultante = buscarIdsEstadoResultanteEstornoTransferencia(entity);

            for (Number id : idsItemSolicitacao) {
                Query deleteItem = em.createNativeQuery(" delete from itemestornotransferencia where id = " + id.longValue());
                deleteItem.executeUpdate();

                Query deleteEvento = em.createNativeQuery(" delete from eventobem where id = " + id.longValue());
                deleteEvento.executeUpdate();
            }

            for (Number idEstado : idsEstadoResultante) {
                Query deleteEstadoBem = em.createNativeQuery(" delete from estadobem where id = " + idEstado.longValue());
                deleteEstadoBem.executeUpdate();
            }
        }
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<List<Bem>> pesquisarBens(AssistenteMovimentacaoBens assistente) {
        SolicitacaoEstornoTransferencia selecionado = (SolicitacaoEstornoTransferencia) assistente.getSelecionado();
        FiltroPesquisaBem filtro = novoFiltroPesquisaBem(selecionado);
        assistente.setDescricaoProcesso("Pesquisando bens da unidade: " + filtro.getHierarquiaAdministrativa() + "...");

        StringBuilder sql = new StringBuilder();
        sql.append(" select   ")
            .append("  bem.id as idBem,   ")
            .append("  bem.identificacao,  ")
            .append("  bem.descricao,  ")
            .append("  est.id as idEstadoBem,  ")
            .append("  vwadm.codigo || ' - ' || vwadm.descricao as unidade, ")
            .append("  est.valorOriginal as valorBem ")
            .append(" from bem   ")
            .append("  inner join eventobem ev on ev.bem_id = bem.id ")
            .append("  inner join efetivacaotransferenciabem efet on ev.id = efet.id ")
            .append("  inner join estadobem est on est.id = ev.estadoresultante_id  ")
            .append("  inner join vwhierarquiaadministrativa vwadm on est.detentoraadministrativa_id = vwadm.subordinada_id ")
            .append("      and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(vwadm.iniciovigencia) and coalesce(trunc(vwadm.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy'))")
            .append(" where efet.lote_id = :loteID  ")
            .append("  and ev.situacaoeventobem = :situacao  ")
            .append("  and bem.movimentoTipoUm = :bloqueado  ")
            .append("  and bem.movimentoTipoDois = :bloqueado  ")
            .append("  and not exists(  ")
            .append("                select   ")
            .append("                evEst.id  ")
            .append("                from solicitestornotransfer sol  ")
            .append("                  inner join loteefettransfbem lote on lote.id = sol.loteefetivacaotransferencia_id  ")
            .append("                  inner join efetivacaoestornotransf efet on efet.solicitacaoestorno_id = sol.id  ")
            .append("                  inner join itemefetiestortransf item on item.efetivacaoestorno_id = efet.id  ")
            .append("                  inner join eventobem evEst on evEst.id = item.id  ")
            .append("                where evEst.situacaoeventobem = :situacao  ")
            .append("                  and evEst.bem_id = ev.bem_id  ")
            .append("                  and lote.id = :loteID) ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("loteID", selecionado.getLoteEfetivacaoTransferencia().getId());
        q.setParameter("situacao", SituacaoEventoBem.FINALIZADO.name());
        q.setParameter("bloqueado", Boolean.FALSE);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(assistente.getDataLancamento()));

        List<Bem> bensPesquisados = preencherListaDeBensApartirArrayObject(q.getResultList());
        List<Bem> bensValidados = configMovimentacaoBemFacade.validarAndRetornarBensDisponiveisParaMovimentacaoComDataRetroativa(bensPesquisados, assistente);
        List<Bem> bensDisponiveis = validarAndRetornarBemQueNaoPossuiTransferenciaPosterirorAUltima(assistente, selecionado, bensValidados);

        configMovimentacaoBemFacade.verificarBensMoveisEmBloqueioParaMovimentacao(filtro, assistente);
        return new AsyncResult<List<Bem>>(bensDisponiveis);
    }

    private FiltroPesquisaBem novoFiltroPesquisaBem(SolicitacaoEstornoTransferencia selecionado) {
        HierarquiaOrganizacional hierarquiaAdministrativa = getHierarquiaDaUnidade(selecionado);
        FiltroPesquisaBem filtro = new FiltroPesquisaBem();
        filtro.setHierarquiaAdministrativa(hierarquiaAdministrativa);
        filtro.setDataOperacao(new Date());
        filtro.setDataReferencia(new Date());
        return filtro;
    }

    private List<Long> buscarIdsBensEfetivacaoTransferencia(LoteEfetivacaoTransferenciaBem efetivacao) {
        String sql = "select ev.bem_id from efetivacaotransferenciabem item " +
            "         inner join eventobem ev on ev.id = item.id " +
            "         inner join loteefettransfbem efet on efet.id = item.lote_id " +
            "         where efet.id = :idEfetivacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idEfetivacao", efetivacao.getId());
        return q.getResultList();
    }

    private HierarquiaOrganizacional getHierarquiaDaUnidade(SolicitacaoEstornoTransferencia selecionado) {
        return hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(
            TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
            selecionado.getLoteEfetivacaoTransferencia().getUnidadeOrganizacional(),
            selecionado.getDataDeCriacao());
    }

    private List<Bem> validarAndRetornarBemQueNaoPossuiTransferenciaPosterirorAUltima(AssistenteMovimentacaoBens assistente, SolicitacaoEstornoTransferencia selecionado, List<Bem> bensPesquisados) {
        List<Bem> bensDisponiveis = Lists.newArrayList();
        for (Bem bem : bensPesquisados) {
            Long ultimaEfetivacao = lotefacade.recuperarIdUltimoLoteEfetivacaoTransferenciaDoBemQueNaoFoiEstornado(bem, assistente);
            if (ultimaEfetivacao != null && !ultimaEfetivacao.equals(selecionado.getLoteEfetivacaoTransferencia().getId())) {
                LoteEfetivacaoTransferenciaBem recuperado = lotefacade.recuperarSimples(ultimaEfetivacao);
                assistente.getMensagens().add("O bem já sofreu uma nova efetivação de transferência e não poderá ser estornado. A ultima movimentação foi na efetivação de transferência n°: " + recuperado.getCodigo());
                continue;
            }
            bensDisponiveis.add(bem);
        }
        return bensDisponiveis;
    }


    public List<Bem> pesquisarBensVinculadoAoItemEstornoTrasnferencia(SolicitacaoEstornoTransferencia entity, AssistenteMovimentacaoBens assistente) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select   ")
            .append("  bem.id as idBem,   ")
            .append("  bem.identificacao,  ")
            .append("  bem.descricao,  ")
            .append("  est.id as idEstadoBem,  ")
            .append("  vwadm.codigo || ' - ' || vwadm.descricao as unidade, ")
            .append("  est.valorOriginal as valorBem ")
            .append(" from bem   ")
            .append("  inner join eventobem ev on ev.bem_id = bem.id ")
            .append("  inner join itemestornotransferencia item on ev.id = item.id ")
            .append("  inner join estadobem est on est.id = ev.estadoresultante_id  ")
            .append("  inner join vwhierarquiaadministrativa vwadm on est.detentoraadministrativa_id = vwadm.subordinada_id ")
            .append("      and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(vwadm.iniciovigencia) and coalesce(trunc(vwadm.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy'))")
            .append(" where item.solicitacaoestorno_id = :idSolicitacao ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("idSolicitacao", entity.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(entity.getDataDeCriacao()));
        List<Bem> bensPesquisados = preencherListaDeBensApartirArrayObject(q.getResultList());
        List<Bem> bensDisponiveis = configMovimentacaoBemFacade.validarAndRetornarBensDisponiveisParaMovimentacaoComDataRetroativa(bensPesquisados, assistente);
        return bensDisponiveis;
    }

    private List<Bem> preencherListaDeBensApartirArrayObject(List<Object[]> objetosDaConsulta) {
        List<Bem> retornaBens = new ArrayList<>();
        for (Object[] bem : objetosDaConsulta) {
            BigDecimal id = (BigDecimal) bem[0];
            Bem b = new Bem();
            b.setId(id.longValue());
            b.setIdentificacao((String) bem[1]);
            b.setDescricao((String) bem[2]);
            b.setIdUltimoEstado(((BigDecimal) bem[3]).longValue());
            b.setAdministrativa((String) bem[4]);
            b.setValorOriginal((BigDecimal) bem[5]);
            retornaBens.add(b);
        }
        return retornaBens;
    }


    public void remover(SolicitacaoEstornoTransferencia entity, ConfigMovimentacaoBem configuracao) {
        processarBensRemovidos(entity, configuracao);
        super.remover(entity);
    }

    private void bloquearBens(List<Bem> bens, ConfigMovimentacaoBem configMovimentacaoBem) {
        if (!bens.isEmpty()) {
            for (Bem bem : bens) {
                configMovimentacaoBemFacade.bloquearUnicoBem(configMovimentacaoBem, bem.getId());
            }
        }
    }

    private void desbloquearBens(List<Number> bensRecuperados, ConfigMovimentacaoBem configuracao) {
        if (bensRecuperados != null && !bensRecuperados.isEmpty()) {
            configMovimentacaoBemFacade.desbloquearBens(configuracao, bensRecuperados);
        }
    }

    private List<Number> buscarIdsItemSolicitacao(SolicitacaoEstornoTransferencia entity) {
        String sql = "" +
            " select " +
            "  item.id " +
            " from itemestornotransferencia item " +
            " where item.solicitacaoestorno_id = :idSolicitacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacao", entity.getId());
        return ((List<Number>) q.getResultList());
    }

    private Number recuperarIdItemEfetivacaoTransferencia(LoteEfetivacaoTransferenciaBem efetivacao, Bem bem) {
        StringBuilder sql = new StringBuilder();
        sql.append("select item.id from efetivacaotransferenciabem item ")
            .append("inner join eventobem ev on ev.id = item.id ")
            .append("inner join bem on bem.id = ev.bem_id ")
            .append("inner join loteefettransfbem efet on efet.id = item.lote_id ")
            .append("where efet.id = :idEfetivacao ")
            .append("and bem.id = :idBem ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("idEfetivacao", efetivacao.getId());
        q.setParameter("idBem", bem.getId());
        return (Number) q.getResultList().get(0);
    }

    private List<Number> buscarIdsBemPorSolicitacaoEstornoTransferencia(SolicitacaoEstornoTransferencia entity) {
        String sql = "" +
            " select " +
            "  ev.bem_id " +
            "from ItemEstornoTransferencia item " +
            "inner join eventobem ev on ev.id = item.id " +
            "where item.solicitacaoestorno_id = :idSolicitacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacao", entity.getId());
        return ((List<Number>) q.getResultList());
    }

    private List<Number> buscarIdsEstadoResultanteEstornoTransferencia(SolicitacaoEstornoTransferencia entity) {
        String sql = "" +
            " select " +
            "  ev.estadoresultante_id " +
            "from ItemEstornoTransferencia item " +
            "inner join eventobem ev on ev.id = item.id " +
            "where item.solicitacaoestorno_id = :idSolicitacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacao", entity.getId());
        return ((List<Number>) q.getResultList());
    }

    public List<RelatorioSolicitacaoEstorno> buscarRegistrosRelatorioSolicitacaoEstornoBens(SolicitacaoEstornoTransferencia solicitacaoEstorno) {
        String sql = "SELECT " +
            "   solicitacaoEstorno.codigo, " +
            "   solicitacaoEstorno.DATADECRIACAO, " +
            "   solicitacao.descricao, " +
            "   solicitacaoEstorno.situacao as situcao, " +
            "  origem.descricao as origem, " +
            "  pf_origem.nome as res_origem, " +
            "  destino.descricao as destino, " +
            "  pf_destino.nome as res_destino, " +
            "  estado.estadobem AS ESTADOCONSERVACAOBEM, " +
            "  BEM.IDENTIFICACAO AS CODIGOPATRIMONIO, " +
            "  to_number(bem.identificacao) as atual, " +
            "  trim(BEM.DESCRICAO) as DESCRICAOBEM, " +
            "  ev.dataoperacao, " +
            "  ESTADO.VALORORIGINAL AS VALORDOLANCAMENTO, " +
            "  VWADM.CODIGO || ' - ' || VWADM.DESCRICAO AS ADMINISTRATIVA, " +
            "  vworc.codigo || ' - ' || vworc.descricao as orcamentaria, " +
            "  ESTADO.VALORACUMULADODAAMORTIZACAO + ESTADO.VALORACUMULADODADEPRECIACAO + ESTADO.VALORACUMULADODAEXAUSTAO + ESTADO.VALORACUMULADODEAJUSTE AS AJUSTES, " +
            "  grupobem.codigo as grupo, " +
            "  grupobem.descricao as grupo_desc " +
            " " +
            "FROM SOLICITESTORNOTRANSFER  solicitacaoEstorno " +
            "  INNER JOIN ItemEstornoTransferencia item ON item.SOLICITACAOESTORNO_ID =  solicitacaoEstorno.ID " +
            "  INNER JOIN eventobem ev ON ev.id = item.id " +
            "  INNER JOIN bem ON bem.id = ev.bem_id " +
            "  INNER JOIN estadobem estado ON estado.id = ev.estadoresultante_id " +
            "  INNER JOIN vwhierarquiaadministrativa vwadm ON vwadm.subordinada_id = estado.detentoraadministrativa_id " +
            "  INNER JOIN vwhierarquiaorcamentaria   vworc ON vworc.subordinada_id = estado.detentoraorcamentaria_id " +
            "  INNER JOIN EfetivacaoTransferenciaBem efetivacao ON item.EFETIVACAOTRANSFERENCIA_ID = efetivacao.ID " +
            "  INNER JOIN TransferenciaBem transferencia ON efetivacao.TRANSFERENCIABEM_ID = transferencia.ID " +
            "  INNER JOIN LOTETRANSFERENCIABEM solicitacao ON transferencia.LOTETRANSFERENCIABEM_ID = solicitacao.ID " +
            "  INNER JOIN vwhierarquiaadministrativa origem ON origem.subordinada_id =  solicitacao.unidadeorigem_id " +
            "    and trunc(solicitacaoEstorno.DATADECRIACAO) between trunc(origem.iniciovigencia) and coalesce(trunc(origem.fimvigencia), trunc(solicitacaoEstorno.DATADECRIACAO)) " +
            "  INNER JOIN vwhierarquiaadministrativa destino ON destino.subordinada_id =  solicitacao.unidadedestino_id " +
            "    and trunc(solicitacaoEstorno.DATADECRIACAO) between trunc(destino.iniciovigencia) and coalesce(trunc(destino.fimvigencia), trunc(solicitacaoEstorno.DATADECRIACAO)) " +
            "  INNER JOIN pessoafisica pf_origem ON pf_origem.id = solicitacao.responsavelorigem_id " +
            "  INNER JOIN pessoafisica pf_destino ON pf_destino.id = solicitacao.responsaveldestino_id " +
            "  INNER JOIN grupobem ON grupobem.id = estado.grupobem_id " +
            "    where trunc(solicitacaoEstorno.DATADECRIACAO) between trunc(vwadm.iniciovigencia) and coalesce(trunc(vwadm.fimvigencia), trunc(solicitacaoEstorno.DATADECRIACAO)) " +
            "    and trunc(solicitacaoEstorno.DATADECRIACAO) between trunc(vworc.iniciovigencia) and coalesce(trunc(vworc.fimvigencia), trunc(solicitacaoEstorno.DATADECRIACAO)) " +
            "    AND solicitacaoEstorno.ID = :solicitacaoEstorno_id " +
            " ORDER BY orcamentaria,grupobem.codigo,atual ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("solicitacaoEstorno_id",solicitacaoEstorno.getId());
        List<RelatorioSolicitacaoEstorno> retorno = Lists.newArrayList();
        if (!q.getResultList().isEmpty()) {
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                RelatorioSolicitacaoEstorno solicitacao = new RelatorioSolicitacaoEstorno();
                solicitacao.setCodigo((BigDecimal) obj[0]);
                solicitacao.setDataHoraCriacao((Date) obj[1]);
                solicitacao.setDescricao((String) obj[2]);
                solicitacao.setSituacaoTransferenciaBem(Util.traduzirEnum(SituacaoEventoBem.class,(String) obj[3]).getDescricao());
                solicitacao.setOrigem((String) obj[4]);
                solicitacao.setResponsavelOrigem((String) obj[5]);
                solicitacao.setDestino((String) obj[6]);
                solicitacao.setResponsavelDestino((String) obj[7]);
                solicitacao.setEstadoConservacaoBem(Util.traduzirEnum(EstadoConservacaoBem.class,(String) obj[8]).getDescricao());
                solicitacao.setCodigoPatrimonio((String) obj[9]);
                solicitacao.setAtual((BigDecimal) obj[10]);
                solicitacao.setDescricaoBem((String) obj[11]);
                solicitacao.setDataHoraCriacao((Date) obj[12]);
                solicitacao.setValordoLancamento((BigDecimal) obj[13]);
                solicitacao.setAdministrativa((String) obj[14]);
                solicitacao.setOrcamentaria((String) obj[15]);
                solicitacao.setAjustes((BigDecimal) obj[16]);
                solicitacao.setGrupoCodigo((String) obj[17]);
                solicitacao.setGrupoDescricao((String) obj[18]);
                retorno.add(solicitacao);
            }
            return retorno;
        }
        return Lists.newArrayList();
    }

    public EntidadeFacade getEntidadeFacade() {
        return entidadeFacade;
    }

    public BemFacade getBemFacade() {
        return bemFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public LoteEfetivacaoTransferenciaBemFacade getLotefacade() {
        return lotefacade;
    }

    public SingletonGeradorCodigo getSingletonGeradorCodigo() {
        return singletonGeradorCodigo;
    }

    public InventarioBensMoveisFacade getInventarioBensMoveisFacade() {
        return inventarioBensMoveisFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public ConfigMovimentacaoBemFacade getConfigMovimentacaoBemFacade() {
        return configMovimentacaoBemFacade;
    }
}
