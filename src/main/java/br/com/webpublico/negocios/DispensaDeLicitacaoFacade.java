package br.com.webpublico.negocios;

import br.com.webpublico.controle.portaltransparencia.PortalTransparenciaNovoFacade;
import br.com.webpublico.controle.portaltransparencia.entidades.DispensaLicitacaoPortal;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.pncp.facade.EventoPncpFacade;
import br.com.webpublico.singletons.SingletonGeradorCodigoAdministrativo;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 11/07/14
 * Time: 09:08
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class DispensaDeLicitacaoFacade extends AbstractFacade<DispensaDeLicitacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private ProcessoDeCompraFacade processoDeCompraFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private VeiculoDePublicacaoFacade veiculoDePublicacaoFacade;
    @EJB
    private DoctoHabilitacaoFacade doctoHabilitacaoFacade;
    @EJB
    private SingletonGeradorCodigoAdministrativo singletonGeradorCodigoAdministrativo;
    @EJB
    private PortalTransparenciaNovoFacade portalTransparenciaNovoFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private ConfiguracaoLicitacaoFacade configuracaoLicitacaoFacade;
    @EJB
    private EventoPncpFacade eventoPncpFacade;

    public DispensaDeLicitacaoFacade() {
        super(DispensaDeLicitacao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProcessoDeCompraFacade getProcessoDeCompraFacade() {
        return processoDeCompraFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public VeiculoDePublicacaoFacade getVeiculoDePublicacaoFacade() {
        return veiculoDePublicacaoFacade;
    }

    public DoctoHabilitacaoFacade getDoctoHabilitacaoFacade() {
        return doctoHabilitacaoFacade;
    }

    @Override
    public DispensaDeLicitacao recuperar(Object id) {
        DispensaDeLicitacao dl = super.recuperar(id);
        dl.getDocumentos().size();
        dl.getPareceres().size();
        dl.getPublicacoes().size();
        dl.getFornecedores().size();
        if (dl.getDetentorDocumentoLicitacao() != null) {
            dl.getDetentorDocumentoLicitacao().getDocumentoLicitacaoList().size();
        }
        carregarListasFornecedor(dl);

        return dl;
    }

    public DispensaDeLicitacao recuperraComDependenciasFornecedores(Object id) {
        DispensaDeLicitacao dl = super.recuperar(id);
        Hibernate.initialize(dl.getFornecedores());
        for (FornecedorDispensaDeLicitacao forn : dl.getFornecedores()) {
            if (forn.getPropostaFornecedorDispensa() != null) {
                Hibernate.initialize(forn.getPropostaFornecedorDispensa().getLotesDaProposta());
                for (LotePropostaFornecedorDispensa lote : forn.getPropostaFornecedorDispensa().getLotesDaProposta()) {
                    Hibernate.initialize(lote.getItensProposta());
                }
            }
        }
        return dl;
    }

    public DispensaDeLicitacao recuperrarSemDependencias(Object id) {
        return super.recuperar(id);
    }


    public void carregarListasFornecedor(DispensaDeLicitacao dl) {
        if (dl.getFornecedores() != null || !dl.getFornecedores().isEmpty()) {
            for (FornecedorDispensaDeLicitacao fornecedorDispensaDeLicitacao : dl.getFornecedores()) {
                fornecedorDispensaDeLicitacao.getDocumentos().size();

                if (fornecedorDispensaDeLicitacao.getPropostaFornecedorDispensa() != null) {
                    fornecedorDispensaDeLicitacao.getPropostaFornecedorDispensa().getLotesDaProposta().size();
                    for (LotePropostaFornecedorDispensa loteProposta : fornecedorDispensaDeLicitacao.getPropostaFornecedorDispensa().getLotesDaProposta()) {
                        loteProposta.getItensProposta().size();
                        for (ItemPropostaFornecedorDispensa itemPropostaFornecedorDispensa : loteProposta.getItensProposta()) {
                            itemPropostaFornecedorDispensa.setSelecionado(Boolean.TRUE);
                        }
                    }
                }
            }
        }
    }

    public DispensaDeLicitacao salvarDispensa(DispensaDeLicitacao entity) {
        return em.merge(entity);
    }

    public Long recuperarIdRevisaoAuditoriaDispensaLicitacao(Long idExclusaoDispensa) {
        String sql = " select rev.id from dispensadelicitacao_aud aud " +
            " inner join revisaoauditoria rev on rev.id = aud.rev " +
            " where rev.id = (select rev from dispensadelicitacao_aud disp where disp.id = :id and disp.revtype = 0) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("id", idExclusaoDispensa);
        try {
            return ((BigDecimal) q.getSingleResult()).longValue();
        } catch (Exception ex) {
            return null;
        }
    }

    private void salvarPortal(DispensaDeLicitacao entity) {
        portalTransparenciaNovoFacade.salvarPortal(new DispensaLicitacaoPortal(entity));
    }


    public DispensaDeLicitacao concluirDispensa(DispensaDeLicitacao entity) {
        entity.setSituacao(SituacaoDispensaDeLicitacao.CONCLUIDO);
        if (entity.getNumeroDaDispensa() == null) {
            entity.setNumeroDaDispensa(singletonGeradorCodigoAdministrativo.gerarNumeroDispensa(entity.getExercicioDaDispensa(), entity.getProcessoDeCompra().getUnidadeOrganizacional(), sistemaFacade.getDataOperacao()));
        }
        List<ItemProcessoDeCompra> itens = processoDeCompraFacade.buscarItensProcessoCompra(entity.getProcessoDeCompra());
        for (ItemProcessoDeCompra item : itens) {
            item.setSituacaoItemProcessoDeCompra(SituacaoItemProcessoDeCompra.HOMOLOGADO);
            em.merge(item);
        }
        entity = em.merge(entity);
        salvarPortal(entity);
        return entity;
    }

    public Boolean isDispensaDeLicitacaoComItensDefinidoPorTipoObjetoCompra(DispensaDeLicitacao dispensa, List<TipoObjetoCompra> tiposObjetoCompra) {
        String sql = " select " +
            "           distinct disp.* " +
            "          from dispensadelicitacao disp " +
            "            inner join processodecompra pc on pc.id = disp.processodecompra_id " +
            "            inner join solicitacaomaterial sol on sol.id = pc.solicitacaomaterial_id " +
            "            inner join cotacao cotacao on cotacao.id = sol.cotacao_id " +
            "            inner join lotecotacao lotecot on lotecot.cotacao_id = cotacao.id " +
            "            inner join itemcotacao item on item.lotecotacao_id = lotecot.id " +
            "            inner join objetocompra oc on oc.id = item.objetocompra_id " +
            "          where oc.tipoobjetocompra in (:tiposObjetoCompra) " +
            "            and disp.id = :idDispensa ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idDispensa", dispensa.getId());
        q.setParameter("tiposObjetoCompra", TipoObjetoCompra.recuperarListaName(tiposObjetoCompra));
        q.setMaxResults(1);
        try {
            return q.getSingleResult() != null;
        } catch (NoResultException nre) {
            return false;
        }
    }

    public List<Pessoa> recuperarFornecedoresDispensaDeLicitacao(DispensaDeLicitacao dl) {
        String hql = "select p from DispensaDeLicitacao dl" +
            " inner join dl.fornecedores f" +
            " inner join f.pessoa p" +
            " where dl = :dl";
        Query q = em.createQuery(hql);
        q.setParameter("dl", dl);
        List<Pessoa> pessoas = q.getResultList();
        pessoas = new ArrayList(new HashSet(pessoas));
        return pessoas;
    }

    public List<FornecedorDispensaDeLicitacao> buscarFornecedoresHabilitados(DispensaDeLicitacao dispensa) {
        String sql = " select distinct df.* from fornecedordisplic df " +
            "           inner join propostafornecedordispensa prop on prop.id = df.propostafornecedordispensa_id " +
            "           inner join lotepropostafornedisp lote on lote.propostafornecedordispensa_id = prop.id " +
            "           inner join itempropostafornedisp item on item.lotepropostafornedisp_id = lote.id " +
            "           inner join pessoa p on p.id = df.pessoa_id " +
            "         where df.dispensadelicitacao_id = :idDispensa " +
            "           and df.tipoclassificacaofornecedor = :habilitado ";
        Query q = em.createNativeQuery(sql, FornecedorDispensaDeLicitacao.class);
        q.setParameter("idDispensa", dispensa.getId());
        q.setParameter("habilitado", TipoClassificacaoFornecedor.HABILITADO.name());
        return q.getResultList();
    }

    public List<DispensaDeLicitacao> buscarDispensaDeLicitacaoPorNumeroOrExercicioOrResumoAndUsuarioAndGestor(String numeroOrExericioOrResumo, UsuarioSistema usuarioSistema, Boolean gestorLicitacao) {
        String sql = "" +
            " select distinct d.* from dispensadelicitacao d " +
            "  inner join exercicio e on d.exerciciodadispensa_id = e.id " +
            "  inner join processodecompra pc on d.processodecompra_id = pc.id " +
            "  inner join usuariounidadeorganizacio uuo on uuo.unidadeorganizacional_id = pc.unidadeorganizacional_id " +
            " where (to_char(d.numerodadispensa) like :parte " +
            "        or to_char(e.ano) like :parte " +
            "        or lower(d.resumodoobjeto) like :parte) " +
            "  and uuo.usuariosistema_id = :usuario_id " +
            "  and uuo.gestorlicitacao = :gestor_licitacao " +
            "  and d.situacao = :situacao " +
            " order by d.exerciciodadispensa_id desc, d.numerodadispensa desc ";
        Query q = em.createNativeQuery(sql, DispensaDeLicitacao.class);
        q.setParameter("parte", "%" + numeroOrExericioOrResumo.toLowerCase() + "%");
        q.setParameter("usuario_id", usuarioSistema.getId());
        q.setParameter("gestor_licitacao", gestorLicitacao);
        q.setParameter("situacao", SituacaoDispensaDeLicitacao.CONCLUIDO.name());
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public List<DispensaDeLicitacao> buscarDispensaLicitacaoUsuarioGestorLiciticaoSemContrato(String part, UsuarioSistema usuarioSistema, Boolean gestorLicitacao) {
        String sql = "" +
            " select distinct d.* from dispensadelicitacao d " +
            "  inner join exercicio e on d.exerciciodadispensa_id = e.id " +
            "  inner join processodecompra pc on d.processodecompra_id = pc.id " +
            "  inner join usuariounidadeorganizacio uuo on uuo.unidadeorganizacional_id = pc.unidadeorganizacional_id " +
            " where (to_char(d.numerodadispensa) like :parte " +
            "        or to_char(e.ano) like :parte " +
            "        or lower(d.resumodoobjeto) like :parte) " +
            "  and uuo.usuariosistema_id = :usuario_id " +
            "  and uuo.gestorlicitacao = :gestor_licitacao " +
            "  and d.situacao = :situacao " +
            "  and not exists (select 1 from dispensadelicitacao dl " +
            "                   inner join condispensalicitacao cdl on cdl.dispensadelicitacao_id = dl.id" +
            "                  where dl.id = d.id)" +
            " order by d.exerciciodadispensa_id desc, d.numerodadispensa ";
        Query q = em.createNativeQuery(sql, DispensaDeLicitacao.class);
        q.setParameter("parte", "%" + part.toLowerCase() + "%");
        q.setParameter("usuario_id", usuarioSistema.getId());
        q.setParameter("gestor_licitacao", gestorLicitacao);
        q.setParameter("situacao", SituacaoDispensaDeLicitacao.CONCLUIDO.name());
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public List<DispensaDeLicitacao> buscarDispensaDeLicitacaoPorNumeroOrExercicioOrResumo(String parte) {
        String sql = "" +
            " select d.* from dispensadelicitacao d" +
            "   inner join exercicio e on d.exerciciodadispensa_id = e.id " +
            "   inner join processodecompra pc on d.processodecompra_id = pc.id " +
            " where (to_char(d.numerodadispensa) like :parte " +
            "        or to_char(e.ano) like :parte " +
            "        or lower(d.resumodoobjeto) like :parte) " +
            " order by e.ano desc, d.numerodadispensa ";
        Query q = em.createNativeQuery(sql, DispensaDeLicitacao.class);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        return q.getResultList();
    }

    public List<DispensaDeLicitacao> buscarDispensaLicitacaoExecucaoProcessoPorNumeroOrExercicioOrResumo(String parte, TipoObjetoCompra tipoObjetoCompra) {
        String sql = "" +
            " select d.* from dispensadelicitacao d" +
            "   inner join exercicio e on d.exerciciodadispensa_id = e.id " +
            "   inner join processodecompra pc on d.processodecompra_id = pc.id " +
            "   inner join execucaoprocessodispensa exdisp on exdisp.dispensalicitacao_id = d.id" +
            "   inner join execucaoprocesso ex on ex.id = exdisp.execucaoprocesso_id" +
            "   inner join execucaoprocessoreserva exres on exres.execucaoprocesso_id = ex.id " +
            " where (to_char(d.numerodadispensa) like :parte " +
            "        or lower(translate(d.numerodadispensa,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:parte,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')" +
            "        or to_char(e.ano) like :parte " +
            "        or to_char(d.numerodadispensa) || '/' || to_char(e.ano) like :parte) " +
            " and exres.tipoobjetocompra = :tipoOc  " +
            " order by e.ano desc, d.numerodadispensa ";
        Query q = em.createNativeQuery(sql, DispensaDeLicitacao.class);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setParameter("tipoOc", tipoObjetoCompra.name());
        return q.getResultList();
    }

    public DispensaDeLicitacao recuperarDispensaLicitacaoPorProcessoCompra(ProcessoDeCompra processoDeCompra) {
        String sql = " select * from dispensadelicitacao " +
            "          where processodecompra_id = :idProcesso ";
        Query query = em.createNativeQuery(sql, DispensaDeLicitacao.class);
        query.setParameter("idProcesso", processoDeCompra.getId());
        query.setMaxResults(1);
        try {
            return (DispensaDeLicitacao) query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }


    public List<DispensaDeLicitacao> buscarDispensaLicitacoesPortalTransparencia(Exercicio exercicio) {
        String sql = "select l.* from dispensadelicitacao l" +
            " inner join processodecompra pc on l.processodecompra_id = pc.id" +
            " inner join exercicio ex on pc.exercicio_id = ex.id"
            + " where ex.id = :exercicio";
        Query q = em.createNativeQuery(sql, DispensaDeLicitacao.class);
        q.setParameter("exercicio", exercicio.getId());
        try {
            return q.getResultList();
        } catch (NoResultException nre) {
            return Lists.newArrayList();
        }
    }

    public List<ItemPropostaFornecedorDispensa> getItensVencidosPeloFornecedorPorStatus(DispensaDeLicitacao dl, Pessoa p, List<TipoClassificacaoFornecedor> status) {
        String hql = "select itens from DispensaDeLicitacao dl" +
            " inner join dl.fornecedores f" +
            " inner join f.propostaFornecedorDispensa pfd" +
            " inner join pfd.lotesDaProposta lp" +
            " inner join lp.itensProposta itens" +
            " where f.pessoa = :pessoa" +
            "   and dl = :dispensa ";
        hql += status.equals(TipoClassificacaoFornecedor.getTodosTipos()) ? " and (f.tipoClassificacaoFornecedor in :status or f.tipoClassificacaoFornecedor is null)" : " and f.tipoClassificacaoFornecedor in :status";

        Query q = em.createQuery(hql);
        q.setParameter("dispensa", dl);
        q.setParameter("status", status);
        q.setParameter("pessoa", p);
        return q.getResultList();
    }

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public ConfiguracaoLicitacaoFacade getConfiguracaoLicitacaoFacade() {
        return configuracaoLicitacaoFacade;
    }

    public List<DispensaDeLicitacao> buscarDispensaLicitacoesPorLeiEData(Date dataInicial, Date dataFinal, LeiLicitacao leiLicitacao) {
        String sql = " select distinct dl.* " +
            " from dispensadelicitacao dl " +
            "          inner join processodecompra pc on dl.processodecompra_id = pc.id " +
            "          inner join solicitacaomaterial sm on pc.solicitacaomaterial_id = sm.id " +
            "          inner join amparolegal al on sm.amparolegal_id = al.id " +
            "          inner join usuariounidadeorganizacio uuo on uuo.UNIDADEORGANIZACIONAL_ID = pc.UNIDADEORGANIZACIONAL_ID " +
            "          inner join vwhierarquiaadministrativa vw on vw.SUBORDINADA_ID = uuo.UNIDADEORGANIZACIONAL_ID " +
            " where al.leilicitacao = :leiLicitacao " +
            "   and dl.datadadispensa between :dataInicial and :dataFinal " +
            "   and to_date(:data, 'dd/mm/yyyy') between vw.inicioVigencia and coalesce(vw.fimvigencia, to_date(:data, 'dd/mm/yyyy')) " +
            "   and uuo.USUARIOSISTEMA_ID = :usuarioId ";
        Query q = em.createNativeQuery(sql, DispensaDeLicitacao.class);
        q.setParameter("leiLicitacao", leiLicitacao.name());
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        q.setParameter("data", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("usuarioId", sistemaFacade.getUsuarioCorrente().getId());

        List<DispensaDeLicitacao> dispensasLicitacao = q.getResultList();
        for (DispensaDeLicitacao dispensaLicitacao : dispensasLicitacao) {
            Hibernate.initialize(dispensaLicitacao.getProcessoDeCompra().getLotesProcessoDeCompra());
            for (LoteProcessoDeCompra loteProcessoDeCompra : dispensaLicitacao.getProcessoDeCompra().getLotesProcessoDeCompra()) {
                Hibernate.initialize(loteProcessoDeCompra.getItensProcessoDeCompra());
            }
        }

        return dispensasLicitacao;
    }

    public EventoPncpFacade getEventoPncpFacade() {
        return eventoPncpFacade;
    }
}
