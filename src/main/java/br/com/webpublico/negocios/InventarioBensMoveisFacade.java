/*
 * Codigo gerado automaticamente em Fri Mar 04 09:44:14 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.BemVo;
import br.com.webpublico.entidadesauxiliares.FiltroPesquisaBem;
import br.com.webpublico.enums.EstadoConservacaoBem;
import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
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
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class InventarioBensMoveisFacade extends AbstractFacade<InventarioBens> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ConfigMovimentacaoBemFacade configMovimentacaoBemFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonConcorrenciaPatrimonio singletonConcorrenciaPatrimonio;

    public InventarioBensMoveisFacade() {
        super(InventarioBens.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public void remover(InventarioBens entity, AssistenteMovimentacaoBens assistente) {
        List<Number> idsBem = buscarIdsBemInventario(entity);
        configMovimentacaoBemFacade.desbloquearBensJDBC(assistente.getConfigMovimentacaoBem(), idsBem);
        super.remover(entity);
    }

    @Asynchronous
    @TransactionTimeout(value = 4, unit = TimeUnit.HOURS)
    public Future<AssistenteMovimentacaoBens> aberturaInventario(AssistenteMovimentacaoBens assistente, FiltroPesquisaBem filtro) {
        InventarioBens entity = (InventarioBens) assistente.getSelecionado();
        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("Abertura de Inventário para a Unidade: " + filtro.getHierarquiaAdministrativa() + "...");
        try {
            entity.setDataAbertura(new Date());
            if (entity.getCodigo() == null) {
                entity.setCodigo(singletonGeradorCodigo.getProximoCodigo(InventarioBens.class, "codigo"));
            }
            entity = em.merge(entity);
            pesquisarBens(assistente, filtro, entity);

            assistente.zerarContadoresProcesso();
            assistente.setDescricaoProcesso("Finalizando Processo...");
            assistente.setSelecionado(entity);
            assistente.setDescricaoProcesso("");
        } catch (Exception ex) {
            logger.error("Erro ao salvar a abertura de inventário. ", ex);
            throw ex;
        }
        return new AsyncResult<>(assistente);
    }

    @Asynchronous
    @TransactionTimeout(value = 4, unit = TimeUnit.HOURS)
    public Future<AssistenteMovimentacaoBens> fechamentoInventario(AssistenteMovimentacaoBens assistente) {
        InventarioBens entity = (InventarioBens) assistente.getSelecionado();
        entity = em.merge(entity);

        if (entity.getDataFechamento() != null) {
            HierarquiaOrganizacional hoAdm = hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), entity.getUnidadeAdministrativa(), entity.getDataFechamento());
            assistente.setDescricaoProcesso("Fechamento de Inventário da Unidade " + hoAdm + "...");
            assistente.zerarContadoresProcesso();

            List<Number> idsBem = Lists.newArrayList();
            List<Object[]> itensIventario = recuperarItensDoInventario(entity);

            assistente.setTotal(itensIventario.size());
            for (Object[] itemInv : itensIventario) {
                FechamentoInventarioBem fechamento = new FechamentoInventarioBem();
                fechamento.setSituacaoEventoBem(SituacaoEventoBem.FINALIZADO);
                fechamento.setInventarioBens(entity);

                Bem bem = bemFacade.recuperarBemPorIdBem(((BigDecimal) itemInv[1]).longValue());
                fechamento.setBem(bem);

                EstadoBem estadoInicial = bemFacade.recuperarUltimoEstadoDoBem(bem);
                fechamento.setEstadoInicial(estadoInicial);

                EstadoBem estadoResultante = bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(estadoInicial);
                estadoResultante = em.merge(estadoResultante);
                fechamento.setEstadoResultante(estadoResultante);
                em.merge(fechamento);

                long idEvento = ((BigDecimal) itemInv[0]).longValue();
                bemFacade.atualizarSituacaoEventoBem(idEvento, SituacaoEventoBem.FINALIZADO);

                idsBem.add(bem.getId());
                assistente.conta();
            }
            configMovimentacaoBemFacade.desbloquearBensJDBC(assistente.getConfigMovimentacaoBem(), idsBem);
        }
        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("Finalizando Processo...");
        assistente.setSelecionado(entity);
        assistente.setDescricaoProcesso("");
        return new AsyncResult<>(assistente);
    }

    private void pesquisarBens(AssistenteMovimentacaoBens assistente, FiltroPesquisaBem filtro, InventarioBens entity) {
        assistente.setDescricaoProcesso("Pesquisando bens para inventariar...");
        String sql = " " +
            "  select distinct " +
            "   bem.id as id_bem, " +
            "   estIni.id as estado_inicial, " +
            "   est.id as estado_resultante," +
            "   bem.identificacao " +
            " from bem " +
            "  inner join eventobem ev on ev.bem_id = bem.id " +
            "  inner join estadobem est on est.id = ev.estadoresultante_id " +
            "  inner join estadobem estIni on estini.id = ev.estadoinicial_id " +
            "  inner join grupobem gb on gb.id = est.grupobem_id " +
            "  inner join movimentobloqueiobem mov on mov.bem_id = bem.id " +
            "  inner join vwhierarquiaorcamentaria vworc on est.detentoraOrcamentaria_id = vworc.subordinada_id" +
            "        and to_date(:dataOperacao, 'dd/MM/yyyy') between vworc.iniciovigencia and coalesce(vworc.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "  inner join vwhierarquiaadministrativa vwadm on est.detentoraadministrativa_id = vwadm.subordinada_id" +
            "        and to_date(:dataOperacao, 'dd/MM/yyyy') between vwadm.iniciovigencia and coalesce(vwadm.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "  where est.detentoraAdministrativa_id = :idUnidadeAdm " +
            "     and gb.tipobem = :tipoBem " +
            "     and est.estadobem <> :baixado " +
            "     and ev.dataoperacao = (select max(evMax.dataoperacao) from eventobem evMax where evMax.bem_id = ev.bem_id) " +
            "     and mov.datamovimento = (select max(movMax.datamovimento) from movimentobloqueiobem movMax where movMax.bem_id = mov.bem_id) ";
        sql += filtro.getHierarquiaOrcamentaria() != null ? " and vworc.subordinada_id = :idUnidadeOrc " : "";
        sql += FiltroPesquisaBem.getCondicaoBloqueioPesquisaBem(assistente.getConfigMovimentacaoBem());
        sql += " order by to_number(bem.identificacao) ";
        Query q = em.createNativeQuery(sql);
        FiltroPesquisaBem.adicionarParametrosConfigMovimentacaoBem(q, assistente.getConfigMovimentacaoBem());
        q.setParameter("tipoBem", filtro.getTipoBem().name());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(filtro.getDataOperacao()));
        q.setParameter("idUnidadeAdm", filtro.getHierarquiaAdministrativa().getSubordinada().getId());
        if (filtro.getHierarquiaOrcamentaria() != null) {
            q.setParameter("idUnidadeOrc", filtro.getHierarquiaOrcamentaria().getSubordinada().getId());
        }
        q.setParameter("baixado", EstadoConservacaoBem.BAIXADO.name());

        List<BemVo> bensVo = Lists.newArrayList();
        List<Number> idsBensParaBloquear = Lists.newArrayList();
        List<Object[]> resultList = q.getResultList();

        assistente.setTotal(resultList.size());
        for (Object[] obj : resultList) {
            BemVo bemVo = new BemVo();
            bemVo.setId(((BigDecimal) obj[0]).longValue());
            Bem bem = bemFacade.recuperarSemDependencias(bemVo.getId());
            bemVo.setBem(bem);
            bemVo.setEstadoInicial(bemFacade.recuperarEstadoBemPorId(((BigDecimal) obj[1]).longValue()));
            bemVo.setEstadoResultante(bemFacade.recuperarEstadoBemPorId(((BigDecimal) obj[2]).longValue()));
            bensVo.add(bemVo);
            assistente.conta();
        }
        configMovimentacaoBemFacade.verificarBensMoveisEmBloqueioParaMovimentacao(filtro, assistente);

        assistente.setDescricaoProcesso("Criando itens a inventariar...");
        assistente.zerarContadoresProcesso();
        assistente.setTotal(bensVo.size());

        for (BemVo bemVO : bensVo) {
            ItemInventarioBens item = new ItemInventarioBens();
            item.setBem(bemVO.getBem());
            item.setInventarioBens(entity);
            item.setEstadoInicial(bemVO.getEstadoInicial());
            item.setEstadoResultante(bemVO.getEstadoResultante());
            em.merge(item);
            idsBensParaBloquear.add(bemVO.getBem().getId());
            assistente.conta();
        }
        configMovimentacaoBemFacade.bloquearBensPorSituacaoJDBC(assistente.getConfigMovimentacaoBem(), idsBensParaBloquear, SituacaoEventoBem.AGUARDANDO_EFETIVACAO);
    }

    private List<Object[]> recuperarItensDoInventario(InventarioBens inventarioBens) {
        String sql = "select item.id, ev.bem_id from ItemInventarioBens item " +
            "          inner join eventobem ev on ev.id = item.id  " +
            "         where item.inventarioBens_id = :inventario ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("inventario", inventarioBens);
        return q.getResultList();
    }

    public Integer countItensInventario(InventarioBens inventarioBens) {
        String hql = "select count(item.id) from ItemInventarioBens item where item.inventarioBens.id = :idInventario";
        Query q = em.createQuery(hql);
        q.setParameter("idInventario", inventarioBens.getId());
        if (!q.getResultList().isEmpty()) {
            return ((Long) q.getSingleResult()).intValue();
        }
        return 0;
    }

    public List<ItemInventarioBens> recuperarItensInventario(InventarioBens inventarioBens, int max, int first) {
        String hql = "select item from ItemInventarioBens item where item.inventarioBens.id = :idInventario " +
            " order by to_number(regexp_replace(item.bem.identificacao, '[^[:digit:]]')), item.bem.descricao";
        Query q = em.createQuery(hql);
        q.setMaxResults(max);
        q.setFirstResult(first);
        q.setParameter("idInventario", inventarioBens.getId());
        return q.getResultList();
    }

    public List<Number> buscarIdsBemInventario(InventarioBens inventarioBens) {
        String sql = "select ev.bem_id from ItemInventarioBens item " +
            "           inner join eventobem ev on ev.id = item.id " +
            "       where item.inventarioBens_id = :idInventario ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idInventario", inventarioBens.getId());
        return q.getResultList();
    }

    public void hasInventarioAberto(HierarquiaOrganizacional adm) throws ExcecaoNegocioGenerica {
        String sql = "Select 1" +
            " from inventariobens iv" +
            " inner join vwhierarquiaadministrativa adm on adm.id = iv.unidadeadministrativa_id" +
            " where iv.datafechamento is null" +
            "  and to_date(:dataOperacao, 'dd/mm/yyyy') between adm.iniciovigencia and coalesce(adm.fimvigencia, to_date(:dataOperacao, 'dd/mm/yyyy'))" +
            "   and adm.codigo like :codigo";
        Query q = em.createNativeQuery(sql);
        q.setParameter("codigo", "01." + adm.getCodigoDo2NivelDeHierarquia() + "%");
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        if (!q.getResultList().isEmpty()) {
            throw new ExcecaoNegocioGenerica("Operação não pode ser realizada! Existe um inventário aberto para a unidade administrativa " + adm);
        }
    }

    public void hasInventarioAbertoDiferente(InventarioBens inventarioBens) throws ExcecaoNegocioGenerica {
        String sql = "Select 1" +
            " from inventariobens iv" +
            " inner join vwhierarquiaadministrativa adm on adm.id = iv.unidadeadministrativa_id" +
            " where iv.datafechamento is null" +
            "  and to_date(:dataOperacao, 'dd/mm/yyyy') between adm.iniciovigencia and coalesce(adm.fimvigencia, to_date(:dataOperacao, 'dd/mm/yyyy'))" +
            "   and adm.codigo like :codigo";
        sql += inventarioBens.getId() != null ? "   and iv.id <> :id" : "";
        Query q = em.createNativeQuery(sql);

        HierarquiaOrganizacional hoAdm = hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), inventarioBens.getUnidadeAdministrativa(), sistemaFacade.getDataOperacao());
        q.setParameter("codigo", hoAdm.getCodigoSemZerosFinais() + "%");
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        if (inventarioBens.getId() != null) {
            q.setParameter("id", inventarioBens.getId());
        }
        if (!q.getResultList().isEmpty()) {
            throw new ValidacaoException("Existe um inventário aberto para esta unidade.");
        }
    }

    public ItemInventarioBens salvarItem(ItemInventarioBens item) {
        return em.merge(item);
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public SingletonConcorrenciaPatrimonio getSingletonConcorrenciaPatrimonio() {
        return singletonConcorrenciaPatrimonio;
    }

    public ConfigMovimentacaoBemFacade getConfigMovimentacaoBemFacade() {
        return configMovimentacaoBemFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }
}
