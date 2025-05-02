package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroPesquisaBem;
import br.com.webpublico.entidadesauxiliares.LoteTransferenciaBemVO;
import br.com.webpublico.entidadesauxiliares.VOItemSolicitacaoTransferencia;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.negocios.tributario.singletons.SingletonConcorrenciaPatrimonio;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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
 * User: BRAINIAC
 * Date: 16/12/13
 * Time: 14:38
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class LoteTransferenciaFacade extends AbstractFacade<LoteTransferenciaBem> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private InventarioBensMoveisFacade inventarioBensMoveisFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private ParametroPatrimonioFacade parametroPatrimonioFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    @EJB
    private ConfigMovimentacaoBemFacade configMovimentacaoBemFacade;
    @EJB
    private SingletonConcorrenciaPatrimonio singletonBloqueioPatrimonio;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private FaseFacade faseFacade;
    @EJB
    private AprovacaoTransferenciaBemFacade aprovacaoTransferenciaBemFacade;
    @EJB
    private LoteEfetivacaoTransferenciaBemFacade loteEfetivacaoTransferenciaBemFacade;


    public LoteTransferenciaFacade() {
        super(LoteTransferenciaBem.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    @Override
    public LoteTransferenciaBem recuperar(Object id) {
        LoteTransferenciaBem entity = super.recuperar(id);
        if (entity.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(entity.getDetentorArquivoComposicao().getArquivosComposicao());
        }
        return entity;
    }

    public LoteTransferenciaBem recuperarComDependencias(Long id) {
        LoteTransferenciaBem recuperar = recuperar(id);
        Hibernate.initialize(recuperar.getTransferencias());
        return recuperar;
    }

    public LoteTransferenciaBem recuperarSemDependencias(Object id) {
        LoteTransferenciaBem entity = super.recuperar(id);
        if (entity.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(entity.getDetentorArquivoComposicao().getArquivosComposicao());
        }
        return entity;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public void remover(LoteTransferenciaBem lote, ConfigMovimentacaoBem configuracao) {
        desbloquearBem(lote, configuracao);
        super.remover(lote);
    }

    public LoteTransferenciaBem salvarSolicitacao(LoteTransferenciaBem lote, ConfigMovimentacaoBem configuracao) {
        processarBensRemovidos(lote, configuracao);
        lote.setDataVersao(new Date());
        return em.merge(lote);
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    @Asynchronous
    public Future<LoteTransferenciaBem> concluirTransferencia(LoteTransferenciaBem entity, AssistenteMovimentacaoBens assistente) {
        try {
            assistente.setDescricaoProcesso("Concluindo Solicitação de Transferência...");
            List<Long> ids = em.createQuery("select t.id from TransferenciaBem t where t.loteTransferenciaBem = :lote").setParameter("lote", entity).getResultList();
            assistente.setTotal(ids.size());
            List<Number> bens = Lists.newArrayList();
            for (Long id : ids) {
                TransferenciaBem tr = em.find(TransferenciaBem.class, id.longValue());
                tr.setSituacaoEventoBem(SituacaoEventoBem.AGUARDANDO_APROVACAO);
                em.merge(tr);
                bens.add(tr.getBem().getId());
                assistente.conta();
            }
            entity.setSituacaoTransferenciaBem(SituacaoDaSolicitacao.AGUARDANDO_APROVACAO);
            if (entity.getCodigo() == null) {
                entity.setCodigo(singletonGeradorCodigo.getProximoCodigo(LoteTransferenciaBem.class, "codigo"));
            }
            LoteTransferenciaBem salvo = em.merge(entity);

            configMovimentacaoBemFacade.alterarSituacaoEvento(bens, SituacaoEventoBem.AGUARDANDO_APROVACAO, OperacaoMovimentacaoBem.SOLICITACAO_TRANSFERENCIA_BEM);
            singletonBloqueioPatrimonio.desbloquearMovimentoPorUnidade(LoteTransferenciaBem.class, entity.getUnidadeOrigem());
            return new AsyncResult<>(salvo);
        } catch (Exception e) {
            throw e;
        }
    }

    public List<LoteTransferenciaBem> buscarSolicitacaoTransferenciaPorUnidade(String parte) {
        String sql = " select sol.* from lotetransferenciabem sol " +
            "          inner join usuariounidadeorganizacio uuo on uuo.unidadeorganizacional_id = sol.unidadedestino_id " +
            "          inner join hierarquiaorganizacional ho on ho.subordinada_id = sol.unidadedestino_id" +
            "        where sol.situacaotransferenciabem = :situacao " +
            "        and sol.tipobem = :tipobem " +
            "        and uuo.usuariosistema_id = :idUsuario " +
            "        and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.iniciovigencia) and coalesce(trunc(ho.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "        and ho.tipohierarquiaorganizacional = :tipoHierarquia " +
            "        and uuo.gestorpatrimonio = :gestorPatrimonio " +
            "        and (sol.codigo like :parte or lower(sol.descricao) like :parte) " +
            "   order by sol.codigo desc ";
        Query q = em.createNativeQuery(sql, LoteTransferenciaBem.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("tipobem", TipoBem.MOVEIS.name());
        q.setParameter("gestorPatrimonio", Boolean.TRUE);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("idUsuario", sistemaFacade.getUsuarioCorrente().getId());
        q.setParameter("situacao", SituacaoDaSolicitacao.AGUARDANDO_EFETIVACAO.name());
        q.setParameter("tipoHierarquia", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setMaxResults(50);
        return q.getResultList();
    }

    public List<LoteTransferenciaBem> buscarSolicitacaoTransferenciaAprovacao(String parte) {
        String sql = " select sol.* from lotetransferenciabem sol " +
            "          inner join usuariounidadeorganizacio uuo on uuo.unidadeorganizacional_id = sol.unidadedestino_id " +
            "        where sol.situacaotransferenciabem = :situacao " +
            "        and sol.tipobem = :tipobem " +
            "        and uuo.usuariosistema_id = :idUsuario " +
            "        and uuo.gestorpatrimonio = :gestorPatrimonio " +
            "        and (sol.codigo like :parte or lower(sol.descricao) like :parte) " +
            "   order by sol.codigo desc ";
        Query q = em.createNativeQuery(sql, LoteTransferenciaBem.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("tipobem", TipoBem.MOVEIS.name());
        q.setParameter("gestorPatrimonio", Boolean.TRUE);
        q.setParameter("idUsuario", sistemaFacade.getUsuarioCorrente().getId());
        q.setParameter("situacao", SituacaoDaSolicitacao.AGUARDANDO_APROVACAO.name());
        q.setMaxResults(50);
        return q.getResultList();
    }

    public boolean hasSolicitacao(Long idsolicitacao) {
        String sql = " select * from APROVACAOTRANSFERENCIABEM a " +
            " where a.SOLICITACAOTRANSFERENCIA_ID = :idsolicitacao ";
        Query q = em.createNativeQuery(sql, AprovacaoTransferenciaBem.class);
        q.setParameter("idsolicitacao", idsolicitacao);
        return !q.getResultList().isEmpty();
    }

    public List<LoteTransferenciaBem> buscarSolicitacaoTransferenciaPorEfetivacao(LoteEfetivacaoTransferenciaBem efetivacao) {
        String sql = " select distinct lotetransf.* from lotetransferenciabem lotetransf " +
            "         inner join transferenciabem transf on transf.lotetransferenciabem_id = lotetransf.id " +
            "         left join efetivacaotransferenciabem efettransf on efettransf.transferenciabem_id = transf.id " +
            "where efettransf.lote_id = :idEfetivacao or transf.loteefetivacao_id = :idEfetivacao ";
        Query q = em.createNativeQuery(sql, LoteTransferenciaBem.class);
        q.setParameter("idEfetivacao", efetivacao.getId());
        return q.getResultList();
    }

    public List<TransferenciaBem> getListaDeTransferenciasPorLote(LoteTransferenciaBem lote) {
        return em.createQuery("select tb " +
            "                from TransferenciaBem tb" +
            "               where loteTransferenciaBem = :lote" +
            "            order by tb.bem.descricao").setParameter("lote", lote).getResultList();
    }

    private List<Number> buscarIdsItemSolicitacao(LoteTransferenciaBem entity) {
        StringBuilder sql = new StringBuilder();
        sql.append("  select item.id from TransferenciaBem item ")
            .append(" where item.loteTransferenciaBem_id = :idSolicitacao ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("idSolicitacao", entity.getId());
        return ((List<Number>) q.getResultList());
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public AssistenteMovimentacaoBens salvarTransferenciaHierarquiaEncerrada(List<LoteTransferenciaBemVO> trasnferenciasVo, AssistenteMovimentacaoBens assistente) {
        assistente.setDescricaoProcesso("Criando Solicitação de Transferência...");
        assistente.setTotal(trasnferenciasVo.size());

        for (LoteTransferenciaBemVO transfVO : trasnferenciasVo) {
            LoteTransferenciaBem novaSolicitacao = new LoteTransferenciaBem();
            novaSolicitacao.setDataHoraCriacao(transfVO.getDataHoraCriacao());
            novaSolicitacao.setTransfHierarquiaEncerrada(true);
            novaSolicitacao.setTipoTransferencia(TipoTransferenciaUnidadeBem.INTERNA);
            novaSolicitacao.setTipoBem(TipoBem.MOVEIS);
            novaSolicitacao.setDescricao(transfVO.getDescricao());
            novaSolicitacao.setUnidadeOrigem(transfVO.getHierarquiaOrigem().getSubordinada());
            novaSolicitacao.setResponsavelOrigem(transfVO.getResponsavelOrigem());
            novaSolicitacao.setUnidadeDestino(transfVO.getHierarquiaDestino().getSubordinada());
            novaSolicitacao.setResponsavelDestino(transfVO.getResponsavelDestino());
            novaSolicitacao.setSituacaoTransferenciaBem(SituacaoDaSolicitacao.AGUARDANDO_APROVACAO);
            if (novaSolicitacao.getCodigo() == null) {
                novaSolicitacao.setCodigo(singletonGeradorCodigo.getProximoCodigo(LoteTransferenciaBem.class, "codigo"));
            }
            novaSolicitacao = em.merge(novaSolicitacao);
            LoteTransferenciaBem entity = em.find(LoteTransferenciaBem.class, novaSolicitacao.getId());

            assistente.setDataLancamento(entity.getDataHoraCriacao());
            List<TransferenciaBem> itensSolicitacao = criarItemSolicitacaoTransferencia(transfVO.getBens(), entity, assistente, SituacaoEventoBem.AGUARDANDO_APROVACAO);

            assistente.setDescricaoProcesso("Criando Aprovação da Solicitação de Transferência...");
            AprovacaoTransferenciaBem novaAprovacao = new AprovacaoTransferenciaBem();
            novaAprovacao.setDataAprovacao(entity.getDataHoraCriacao());
            novaAprovacao.setSolicitacaoTransferencia(entity);
            novaAprovacao.setTipoBem(TipoBem.MOVEIS);
            novaAprovacao.setUsuarioSistema(assistente.getUsuarioSistema());
            novaAprovacao.setSituacao(SituacaoDaSolicitacao.ACEITA);

            List<VOItemSolicitacaoTransferencia> bensAprovacao = Lists.newArrayList();
            for (TransferenciaBem itemSol : itensSolicitacao) {
                VOItemSolicitacaoTransferencia itemAprov = new VOItemSolicitacaoTransferencia();
                itemAprov.setIdItem(itemSol.getId());
                bensAprovacao.add(itemAprov);
            }
            aprovacaoTransferenciaBemFacade.salvarAprovacao(bensAprovacao, novaAprovacao, assistente);

            LoteEfetivacaoTransferenciaBem novaEfetivacao = new LoteEfetivacaoTransferenciaBem();
            novaEfetivacao.setDataEfetivacao(entity.getDataHoraCriacao());
            novaEfetivacao.setSolicitacaoTransferencia(entity);
            novaEfetivacao.setTipoBem(TipoBem.MOVEIS);
            novaEfetivacao.setUnidadeOrganizacional(entity.getUnidadeDestino());
            novaEfetivacao.setUsuarioSistema(assistente.getUsuarioSistema());

            List<VOItemSolicitacaoTransferencia> bensEfet = Lists.newArrayList();
            for (TransferenciaBem itemSol : itensSolicitacao) {
                VOItemSolicitacaoTransferencia itemEfet = new VOItemSolicitacaoTransferencia();
                itemEfet.setIdItem(itemSol.getId());
                itemEfet.setIdBem(itemSol.getBem().getId());

                HierarquiaOrganizacional hoOrcamentaria = hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), itemSol.getUnidadeOrcamentariaResultante(), assistente.getDataAtual());
                itemEfet.setUnidadeOrcamentariaOrigem(hoOrcamentaria);
                itemEfet.setUnidadeOrcamentariaDestino(transfVO.getHierarquiaOrcamentariaDestino() !=null ? transfVO.getHierarquiaOrcamentariaDestino() : hoOrcamentaria);

                HierarquiaOrganizacional hoAdmOrigem = hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), entity.getUnidadeOrigem(), itemSol.getDataLancamento());
                itemEfet.setUnidadeAdministrativaOrigem(hoAdmOrigem);

                HierarquiaOrganizacional hoAdmDestino = hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), novaSolicitacao.getUnidadeDestino(), assistente.getDataAtual());
                itemEfet.setUnidadeAdministrativaDestino(hoAdmDestino);
                bensEfet.add(itemEfet);
            }
            assistente.setDescricaoProcesso("Criando Efetivação da Solicitação de Transferência...");
            loteEfetivacaoTransferenciaBemFacade.salvarEfetivacao(novaEfetivacao, bensEfet, assistente);
            assistente.getTransferenciasBens().add(entity);
            assistente.conta();
        }
        return assistente;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<LoteTransferenciaBem> criarTransferenciaBem(List<Bem> bens, LoteTransferenciaBem lote, AssistenteMovimentacaoBens assistente) {
        assistente.setDescricaoProcesso("Criando Eventos de Solicitação de Transferência...");
        assistente.setTotal(bens.size());
        criarItemSolicitacaoTransferencia(bens, lote, assistente, SituacaoEventoBem.EM_ELABORACAO);

        return new AsyncResult<>(lote);
    }

    private void processarBensRemovidos(LoteTransferenciaBem entity, ConfigMovimentacaoBem configuracao) {
        if (entity.getId() != null) {
            List<Number> idsItemSolicitacao = buscarIdsItemSolicitacao(entity);
            desbloquearBem(entity, configuracao);
            for (Number id : idsItemSolicitacao) {
                Query deleteItem = em.createNativeQuery(" delete from transferenciabem where id = " + id.longValue());
                deleteItem.executeUpdate();

                Query deleteEvento = em.createNativeQuery("delete from eventobem where id = " + id.longValue());
                deleteEvento.executeUpdate();
            }
        }
    }

    private void desbloquearBem(LoteTransferenciaBem entity, ConfigMovimentacaoBem configuracao) {
        List<Number> idsBem = buscarIdsBemPorSolicitacaoTransferencia(entity);
        configMovimentacaoBemFacade.desbloquearBens(configuracao, idsBem);
    }

    private List<TransferenciaBem> criarItemSolicitacaoTransferencia(List<Bem> bens, LoteTransferenciaBem lote, AssistenteMovimentacaoBens assistente, SituacaoEventoBem situacao) {
        List<Number> bensBloqueio = Lists.newArrayList();
        List<TransferenciaBem> itensTransf = Lists.newArrayList();
        for (Bem bem : bens) {
            EstadoBem estadoInicial = bemFacade.recuperarUltimoEstadoDoBem(bem);
            EstadoBem estadoResultante = em.merge(bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(estadoInicial));

            Date dataLancamento = DataUtil.getDataComHoraAtual(assistente.getDataLancamento());
            TransferenciaBem novaTransf = new TransferenciaBem(bem, lote, estadoInicial, estadoResultante);
            novaTransf.setSituacaoEventoBem(situacao);
            novaTransf.setDataLancamento(dataLancamento);
            if (lote.getTransfHierarquiaEncerrada()) {
                novaTransf.setDataOperacao(dataLancamento);
            }
            bensBloqueio.add(bem.getId());
            novaTransf = em.merge(novaTransf);
            itensTransf.add(novaTransf);
            assistente.conta();
        }
        configMovimentacaoBemFacade.bloquearBensPorSituacao(assistente.getConfigMovimentacaoBem(), bensBloqueio, situacao);
        return itensTransf;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public List<Bem> pesquisarBemHierarquiaEncerrada(FiltroPesquisaBem filtro, AssistenteMovimentacaoBens assistente) {

        assistente.setDescricaoProcesso("Pesquisando bens da unidade: " + filtro.getHierarquiaOrcamentaria());

        StringBuilder sql = new StringBuilder();
        sql.append(" select bem.id,")
            .append("  bem.descricao as descricaobem,")
            .append("  bem.identificacao as identificacaobem,")
            .append("  vworc.codigo || ' - ' || vworc.descricao  as unidade,")
            .append("  etb.id as idUltimoEstadoBem ")
            .append(" from eventobem eb")
            .append("   inner join bem on bem.id = eb.bem_id")
            .append("   inner join estadobem etb on etb.id = eb.estadoresultante_id")
            .append("   inner join grupobem grupo on grupo.id = etb.grupobem_id")
            .append("   inner join movimentobloqueiobem mov on mov.bem_id = bem.id  ")
            .append("   inner join vwhierarquiaorcamentaria vworc on vworc.subordinada_id = etb.detentoraorcamentaria_id ")
            .append("      and vworc.subordinada_id = :idUnidadeOrc")
            .append("      and eb.situacaoeventobem <> :sitaucaoBaixado")
            .append("      and eb.dataoperacao = (select max(evMax.dataoperacao) from eventobem evmax where evmax.bem_id = bem.id) ")
            .append("      and to_date(:dataOperacao, 'dd/MM/yyyy') between vworc.iniciovigencia and coalesce(vworc.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy'))")
            .append("      and mov.datamovimento = (select max(mov2.datamovimento) from movimentobloqueiobem mov2 where bem.id = mov2.bem_id)  ")
            .append("      and grupo.tipoBem = :tipoBem ");
        if (filtro.getHirarquiaEncerrada()) {
            sql.append("  and not exists(select 1 ")
                .append("               from hierarquiaorganizacional hoAdm ")
                .append("               where to_date(:dataOperacao, 'dd/MM/yyyy') between hoAdm.iniciovigencia and coalesce(hoAdm.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) ")
                .append("               and hoAdm.tipohierarquiaorganizacional = :tipoHoAdm ")
                .append("               and hoAdm.subordinada_id = etb.detentoraadministrativa_id) ");
        }
        sql.append(FiltroPesquisaBem.getCondicaoBloqueioPesquisaBem(assistente.getConfigMovimentacaoBem()));
        sql.append(filtro.getBem() != null ? " and bem.id = :idBem" : "");
        sql.append(!Util.isListNullOrEmpty(filtro.getHierarquiasOrcamentarias()) ? " and etb.detentoraOrcamentaria_id in (:idUnidadeOrc)" : "");
        sql.append(filtro.getGrupoBem() != null ? " and grupo.id = :idGrupoBem" : "");
        sql.append(" order by to_number(bem.identificacao) ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("idUnidadeOrc", filtro.getHierarquiaOrcamentaria().getSubordinada().getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(filtro.getDataOperacao()));
        q.setParameter("tipoBem", filtro.getTipoBem().name());
        q.setParameter("sitaucaoBaixado", SituacaoEventoBem.BAIXADO.name());
        if (filtro.getHirarquiaEncerrada()) {
            q.setParameter("tipoHoAdm", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        }
        FiltroPesquisaBem.adicionarParametrosConfigMovimentacaoBem(q, assistente.getConfigMovimentacaoBem());
        if (filtro.getBem() != null) {
            q.setParameter("idBem", filtro.getBem().getId());
        }
        List<Bem> bensPesquisados = popularListaBens(q, assistente);
        recuperarUltimaHieraquiaVigenteBem(bensPesquisados);

        assistente.zerarContadoresProcesso();
        return bensPesquisados;
    }

    private void recuperarUltimaHieraquiaVigenteBem(List<Bem> bensPesquisados) {
        for (Bem bem : bensPesquisados) {
            EventoBem eventoBem = bemFacade.recuperarUltimoEventoBemComHierarquiaVigente(bem);
            bem.setEventoBem(eventoBem);
        }
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<List<Bem>> pesquisarBem(FiltroPesquisaBem filtro, AssistenteMovimentacaoBens assistente) {

        assistente.setDescricaoProcesso("Pesquisando bens da unidade: " + filtro.getHierarquiaAdministrativa());

        StringBuilder sql = new StringBuilder();
        sql.append(" select bem.id,")
            .append("  bem.descricao as descricaobem,")
            .append("  bem.identificacao as identificacaobem,")
            .append("  vworc.codigo || ' - ' || vworc.descricao  as unidade,")
            .append("  etb.id as idUltimoEstadoBem ")
            .append(" from eventobem eb")
            .append("   inner join bem on bem.id = eb.bem_id")
            .append("   inner join estadobem etb on etb.id = eb.estadoresultante_id")
            .append("   inner join grupobem grupo on grupo.id = etb.grupobem_id")
            .append("   inner join movimentobloqueiobem mov on mov.bem_id = bem.id  ")
            .append("   inner join vwhierarquiaorcamentaria vworc on vworc.subordinada_id = etb.detentoraorcamentaria_id ")
            .append("   inner join vwhierarquiaadministrativa vwadm on vwadm.subordinada_id = etb.detentoraadministrativa_id   ")
            .append("      and vwadm.subordinada_id = :idUnidadeAdm")
            .append("      and eb.dataoperacao = (select max(evMax.dataoperacao) from eventobem evmax where evmax.bem_id = bem.id) ")
            .append("      and to_date(:dataOperacao, 'dd/MM/yyyy') between vworc.iniciovigencia and coalesce(vworc.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy'))")
            .append("      and to_date(:dataOperacao, 'dd/MM/yyyy') between vwadm.iniciovigencia and coalesce(vwadm.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy'))")
            .append("      and mov.datamovimento = (select max(mov2.datamovimento) from movimentobloqueiobem mov2 where bem.id = mov2.bem_id)  ")
            .append("      and etb.estadobem <> :baixado ")
            .append("      and grupo.tipoBem = :tipoBem ");
        sql.append(FiltroPesquisaBem.getCondicaoBloqueioPesquisaBem(assistente.getConfigMovimentacaoBem()));
        sql.append(filtro.getBem() != null ? " and bem.id = :idBem" : "");
        sql.append(!Util.isListNullOrEmpty(filtro.getHierarquiasOrcamentarias()) ? " and etb.detentoraOrcamentaria_id in (:idUnidadeOrc)" : "");
        sql.append(filtro.getGrupoBem() != null ? " and grupo.id = :idGrupoBem" : "");
        sql.append(" order by to_number(bem.identificacao) ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("idUnidadeAdm", filtro.getHierarquiaAdministrativa().getSubordinada().getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(filtro.getDataOperacao()));
        q.setParameter("tipoBem", filtro.getTipoBem().name());
        q.setParameter("baixado", EstadoConservacaoBem.BAIXADO.name());
        FiltroPesquisaBem.adicionarParametrosConfigMovimentacaoBem(q, assistente.getConfigMovimentacaoBem());
        if (filtro.getGrupoBem() != null) {
            q.setParameter("idGrupoBem", filtro.getGrupoBem().getId());
        }
        if (filtro.getBem() != null) {
            q.setParameter("idBem", filtro.getBem().getId());
        }
        if (!Util.isListNullOrEmpty(filtro.getHierarquiasOrcamentarias())) {
            q.setParameter("idUnidadeOrc", filtro.getIdsHierarquiaOrcamentaria());
        }
        List<Bem> bensPesquisados = popularListaBens(q, assistente);
        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("Verificando Disponibilidade para Movimentação...");
        List<Bem> bensDisponiveis = validarRetornandoBensDisponiveis(assistente, bensPesquisados);

        if (assistente.isOperacaoEditar()) {
            assistente.setBensSalvos(buscarBensVinculadosTransferencia((LoteTransferenciaBem) assistente.getSelecionado(), assistente));
        } else {
            assistente.setBensSalvos(bensDisponiveis);
        }
        configMovimentacaoBemFacade.verificarBensMoveisEmBloqueioParaMovimentacao(filtro, assistente);
        return new AsyncResult<>(bensDisponiveis);
    }

    public List<Bem> buscarBensVinculadosTransferencia(LoteTransferenciaBem lote, AssistenteMovimentacaoBens assistente) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select bem.id,")
            .append("  bem.descricao as descricaobem,")
            .append("  bem.identificacao as identificacaobem,")
            .append("  vworc.codigo || ' - ' || vworc.descricao    as unidade,")
            .append("  etb.id as idUltimoEstadoBem ")
            .append(" from  ")
            .append("  transferenciabem tr  ")
            .append("   inner join eventobem eb on eb.id = tr.id ")
            .append("   inner join bem on bem.id = eb.bem_id ")
            .append("   inner join estadobem etb on etb.id = eb.estadoresultante_id ")
            .append("   inner join grupoBem grupo on grupo.id = etb.grupoBem_id  ")
            .append("   inner join vwhierarquiaorcamentaria vworc on vworc.subordinada_id = etb.detentoraorcamentaria_id ")
            .append("  where tr.loteTransferenciaBem_id = :idLote ")
            .append("   and to_date(:dataOperacao, 'dd/MM/yyyy') between vworc.iniciovigencia and coalesce(vworc.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy'))")
            .append("  order by to_number(bem.identificacao)");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("idLote", lote.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(lote.getDataHoraCriacao()));
        List<Bem> bensPesquisados = popularListaBens(q, assistente);
        return validarRetornandoBensDisponiveis(assistente, bensPesquisados);
    }

    private List<Bem> popularListaBens(Query q, AssistenteMovimentacaoBens assistente) {
        List<Object[]> objetosDaConsulta = q.getResultList();
        if (assistente != null) {
            assistente.zerarContadoresProcesso();
            assistente.setTotal(objetosDaConsulta.size());
        }

        List<Bem> retornaBens = new ArrayList<>();
        for (Object[] obj : objetosDaConsulta) {
            long id = ((BigDecimal) obj[0]).longValue();
            Bem bem = new Bem();
            bem.setId(id);
            bem.setDescricao((String) obj[1]);
            bem.setIdentificacao((String) obj[2]);
            bem.setOrcamentaria((String) obj[3]);
            bem.setIdUltimoEstado(((BigDecimal) obj[4]).longValue());
            EstadoBem estadoBem = bemFacade.recuperarEstadoBemPorId(bem.getIdUltimoEstado());
            Bem.preencherDadosTrasientsDoBem(bem, estadoBem);
            retornaBens.add(bem);
            if (assistente != null) {
                assistente.conta();
            }
        }
        return retornaBens;
    }

    private List<Bem> validarRetornandoBensDisponiveis(AssistenteMovimentacaoBens assistente, List<Bem> bens) {
        return configMovimentacaoBemFacade.validarAndRetornarBensDisponiveisParaMovimentacaoComDataRetroativa(bens, assistente);
    }

    public Long count(String sql) {
        try {
            Query query = em.createNativeQuery(sql);
            return ((BigDecimal) query.getSingleResult()).longValue();
        } catch (NoResultException nre) {
            return 0L;
        }
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Object[] filtarComContadorDeRegistros(String sql, String sqlCount, Object objeto, int inicio, int max) throws Exception {
        Query consulta = em.createNativeQuery(sql);
        Query consultaCount = em.createNativeQuery(sqlCount);
        Long count = 0L;
        List<LoteTransferenciaBem> trasferencias = new ArrayList<>();
        try {
            count = ((BigDecimal) consultaCount.getSingleResult()).longValue();
            if (max != 0) {
                consulta.setMaxResults(max);
                consulta.setFirstResult(inicio);
            }
            List<Object[]> lista = consulta.getResultList();
            for (Object[] object : lista) {

                LoteTransferenciaBem transferenciaBem = new LoteTransferenciaBem();
                transferenciaBem.setId(((BigDecimal) object[0]).longValue());
                transferenciaBem.setCodigo(object[1] != null ? ((BigDecimal) object[1]).longValue() : null);
                transferenciaBem.setDataHoraCriacao((Date) object[2]);
                transferenciaBem.setDescricao((String) object[3]);
                PessoaFisica pfOrigem = new PessoaFisica();
                pfOrigem.setNome((String) object[4]);
                pfOrigem.setCpf((String) object[5]);
                transferenciaBem.setResponsavelOrigem(pfOrigem);
                PessoaFisica pfDestino = new PessoaFisica();
                pfDestino.setNome((String) object[6]);
                pfDestino.setCpf((String) object[7]);
                transferenciaBem.setResponsavelDestino(pfDestino);
                transferenciaBem.setDescricaoUnidadeOrigem((String) object[8]);
                transferenciaBem.setDescricaoUnidadeDestino((String) object[9]);
                transferenciaBem.setSituacaoTransferenciaBem(SituacaoDaSolicitacao.valueOf((String) object[10]));
                trasferencias.add(transferenciaBem);

            }
        } catch (NoResultException nre) {
            logger.error("Erro ao montar lista de protocolos {} ", nre);
        }
        Object[] retorno = new Object[2];
        retorno[0] = trasferencias;
        retorno[1] = count;
        return retorno;
    }

    public List<Number> buscarIdsBemPorSolicitacaoTransferencia(LoteTransferenciaBem lote) {
        String sql = "" +
            " select " +
            "   ev.bem_id " +
            " from transferenciabem item " +
            " inner join eventobem ev on ev.id = item.id " +
            " where item.lotetransferenciabem_id = :idLote ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idLote", lote.getId());
        return ((List<Number>) q.getResultList());
    }

    public Boolean hasTransferenciaEfetitavada(Number idItemSolicitacao) {
        String sql = "  select * from efetivacaotransferenciabem where transferenciabem_id = :idItemSolicitacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItemSolicitacao", idItemSolicitacao);
        return !q.getResultList().isEmpty();
    }


    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public InventarioBensMoveisFacade getInventarioBensMoveisFacade() {
        return inventarioBensMoveisFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public SingletonGeradorCodigo getSingletonGeradorCodigo() {
        return singletonGeradorCodigo;
    }

    public ParametroPatrimonioFacade getParametroPatrimonioFacade() {
        return parametroPatrimonioFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public EntidadeFacade getEntidadeFacade() {
        return entidadeFacade;
    }

    public ConfigMovimentacaoBemFacade getConfigMovimentacaoBemFacade() {
        return configMovimentacaoBemFacade;
    }

    public BemFacade getBemFacade() {
        return bemFacade;
    }

    public SingletonConcorrenciaPatrimonio getSingletonBloqueioPatrimonio() {
        return singletonBloqueioPatrimonio;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public FaseFacade getFaseFacade() {
        return faseFacade;
    }

    public List<HierarquiaOrganizacional> buscarUnidadesPorSubordinada(String parte, UnidadeOrganizacional subordinada, TipoTransferenciaUnidadeBem tipo) {

        String sql = "select distinct h.*  " +
            "from hierarquiaorganizacional h  " +
            "         inner join HIERARQUIAORGRESP resp on resp.HIERARQUIAORGADM_ID = h.id  " +
            "         inner join hierarquiaorganizacional orc on resp.hierarquiaorgorc_id = orc.id  " +
            "where sysdate between h.iniciovigencia and coalesce(h.fimvigencia, sysdate)  " +
            "  and sysdate between resp.datainicio and coalesce(resp.datafim, sysdate)  " +
            "  and ((upper(translate(h.descricao, 'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç', 'aaaaaaaaeeeeiioooooouuuucc'))) like  " +
            "       translate(upper(:parte), 'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç', 'aaaaaaaaeeeeiioooooouuuucc') or h.codigo like :parte or  " +
            "       replace(h.codigo, '.', '') like :parte)";


        sql += TipoTransferenciaUnidadeBem.INTERNA.equals(tipo) ? "and orc.subordinada_id in (select orc.subordinada_id " +
            "                             from hierarquiaorganizacional h " +
            "                                      inner join HIERARQUIAORGRESP resp on resp.HIERARQUIAORGADM_ID = h.id " +
            "                                      inner join hierarquiaorganizacional orc on resp.hierarquiaorgorc_id = orc.id " +
            "                             where sysdate between h.iniciovigencia and coalesce(h.fimvigencia, sysdate) " +
            "                               and sysdate between resp.datainicio and coalesce(resp.datafim, sysdate) " +
            "                                 and h.subordinada_id = :idSubordinada)" :
            "and orc.subordinada_id in (select orc.subordinada_id " +
                "                             from hierarquiaorganizacional h " +
                "                                      inner join HIERARQUIAORGRESP resp on resp.HIERARQUIAORGADM_ID = h.id " +
                "                                      inner join hierarquiaorganizacional orc on resp.hierarquiaorgorc_id = orc.id " +
                "                             where sysdate between h.iniciovigencia and coalesce(h.fimvigencia, sysdate) " +
                "                               and sysdate between resp.datainicio and coalesce(resp.datafim, sysdate) " +
                "                                 and h.subordinada_id != :idSubordinada)";

        sql += " order by h.codigo";

        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("idSubordinada", subordinada.getId());
        q.setParameter("parte", ("%" + parte + "%"));

        List<HierarquiaOrganizacional> result = q.getResultList();

        if (result.isEmpty()) {
            return new ArrayList<>();
        }
        return result;
    }

    public List<HierarquiaOrganizacional> buscarUnidadesOrcamentaria(Long subordinadaId) {
        String sql = "select distinct orc.* from hierarquiaorganizacional h " +
            "                                   inner join HIERARQUIAORGRESP resp on resp.HIERARQUIAORGADM_ID = h.id " +
            "                                   inner join hierarquiaorganizacional orc on resp.hierarquiaorgorc_id = orc.id " +
            "where  sysdate between h.iniciovigencia and coalesce(h.fimvigencia,  sysdate) " +
            "  and  sysdate between resp.datainicio and coalesce(resp.datafim,  sysdate) " +
            "  and h.subordinada_id = :subordinadaId";

        Query q = em.createNativeQuery(sql, HierarquiaOrganizacional.class);
        q.setParameter("subordinadaId", subordinadaId);

        List<HierarquiaOrganizacional> result = q.getResultList();

        if (result.isEmpty()) {
            return new ArrayList<>();
        }
        return result;
    }
}
