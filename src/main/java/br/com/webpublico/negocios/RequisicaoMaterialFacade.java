/*
 * Codigo gerado automaticamente em Fri Feb 24 09:22:07 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Stateless
public class RequisicaoMaterialFacade extends AbstractFacade<RequisicaoMaterial> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private LocalEstoqueFacade localEstoqueFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private EntradaMaterialFacade entradaMaterialFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private RequisicaoMaterialFacade requisicaoMaterialFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private MaterialFacade materialFacade;
    @EJB
    private AprovacaoMaterialFacade aprovacaoMaterialFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RequisicaoMaterialFacade() {
        super(RequisicaoMaterial.class);
    }

    @Override
    public RequisicaoMaterial recuperar(Object id) {
        RequisicaoMaterial requisicao = super.recuperar(id);
        Hibernate.initialize(requisicao.getItensRequisitados());
        return requisicao;
    }

    @Override
    public RequisicaoMaterial salvarRetornando(RequisicaoMaterial entity) {
        if (entity.getNumero() == null) {
            entity.setNumero(singletonGeradorCodigo.getProximoCodigo(RequisicaoMaterial.class, "numero"));
            em.persist(entity);
            criarNotificacaoNovaRequisicaoMaterial(entity);
        } else {
            entity = em.merge(entity);
        }
        return entity;
    }

    public Long recuperarIdRevisaoAuditoria(Long id) {
        String sql = " select rev.id from requisicaomaterial_aud aud " +
            " inner join revisaoauditoria rev on rev.id = aud.rev " +
            " where rev.id = (select rev from requisicaomaterial_aud raud where id = :id and raud.revtype = 0) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("id", id);
        try {
            return ((BigDecimal) q.getSingleResult()).longValue();
        } catch (Exception ex) {
            return null;
        }
    }

    public List<RequisicaoMaterial> buscarRequisicaoMaterialPorTipo(String parte, TipoRequisicaoMaterial tipoRequisicao) {

        String sql = "" +
            "   select req.* from requisicaomaterial req " +
            "   where (lower(req.descricao) like :parte" +
            "                or req.numero like :parte " +
            "                or req.dataRequisicao like :parte)" +
            " and req.TIPOREQUISICAO = :tipoReq " +
            "   order by req.numero desc ";
        Query q = em.createNativeQuery(sql, RequisicaoMaterial.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("tipoReq", tipoRequisicao.name());
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public List<RequisicaoMaterial> buscarRequisicoesNaoAvaliada(String parte, UsuarioSistema usuario) {
        String sql = "SELECT RM.* " +
            "      FROM REQUISICAOMATERIAL RM " +
            " INNER JOIN GESTORLOCALESTOQUE GLE ON GLE.LOCALESTOQUE_ID = RM.LOCALESTOQUEORIGEM_ID " +
            "     WHERE RM.TIPOSITUACAOREQUISICAO      = :tiposituacaorequisicao " +
            "       AND GLE.USUARIOSISTEMA_ID          = :usuario_id " +
            "       AND (TO_CHAR(RM.DATAREQUISICAO) LIKE :filtro" +
            "           OR RM.NUMERO                LIKE :filtro" +
            "           OR LOWER(RM.DESCRICAO)      LIKE :filtro)" +
            "  ORDER BY RM.NUMERO DESC";
        Query q = em.createNativeQuery(sql, RequisicaoMaterial.class);
        q.setParameter("usuario_id", usuario.getId());
        q.setParameter("tiposituacaorequisicao", SituacaoRequisicaoMaterial.NAO_AVALIADA.name());
        q.setParameter("filtro", "%" + parte.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public List<ItemRequisicaoMaterial> buscarItensRequisicao(RequisicaoMaterial requisicaoMaterial) {
        String sql = "select item.* from itemrequisicaomaterial item where item.requisicaomaterial_id = :idRequisicao";
        Query q = em.createNativeQuery(sql, ItemRequisicaoMaterial.class);
        q.setParameter("idRequisicao", requisicaoMaterial.getId());

        return q.getResultList();
    }

    public Boolean requisicaoJaFoiAvaliada(RequisicaoMaterial requisicaoMaterial) {
        String hql = "from AprovacaoMaterial am where am.requisicaoMaterial = :requisicaoMaterial";
        Query q = em.createQuery(hql);
        q.setParameter("requisicaoMaterial", requisicaoMaterial);
        try {
            return q.getResultList().isEmpty() ? Boolean.FALSE : Boolean.TRUE;
        } catch (Exception e) {
            return Boolean.FALSE;
        }
    }

    public BigDecimal getQuantidadeSaida(ItemRequisicaoMaterial itemReqMat) {
        String sql = " select coalesce(sum(itemSaida.quantidade), 0) as quantidade " +
            "           from itemsaidamaterial itemSaida " +
            "           inner join ITEMREQSAIDAMAT irsm on irsm.ITEMSAIDAMATERIAL_ID = itemSaida.ID " +
            "           where irsm.ITEMREQUISICAOMATERIAL_ID = :idItemRequisicao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItemRequisicao", itemReqMat.getId());
        return (BigDecimal) q.getSingleResult();
    }

    public BigDecimal totalAtendidoDoItemRequisicaoNaUnidade(Date data, Material material, UnidadeOrganizacional unidadeOrganizacional) {
        BigDecimal valor = BigDecimal.ZERO;
        String hql = "select sum(irm.quantidadeAtendida) " +
            "        from ItemRequisicaoMaterial irm"
            + " inner join irm.requisicaoMaterial as rm"
            + " where rm.dataRequisicao >= :primeiroDia" +
            "   and rm.dataRequisicao <= :ultimoDia"
            + " and (irm.materialRequisitado.id = :material or irm.materialAprovado.id = :material)"
            + " and rm.unidadeRequerente = :unidadeOrganizacional";
        Calendar dataAuxilio = Calendar.getInstance();
        dataAuxilio.setTime(data);
        dataAuxilio.set(dataAuxilio.get(Calendar.YEAR), dataAuxilio.get(Calendar.MONTH), 01);
        Date primeiroDia = dataAuxilio.getTime();
        Date ultimoDia = Util.recuperaDataUltimoDiaDoMes(data);
        Query q = em.createQuery(hql);
        q.setParameter("primeiroDia", primeiroDia, TemporalType.DATE);
        q.setParameter("ultimoDia", ultimoDia, TemporalType.DATE);
        q.setParameter("material", material.getId());
        q.setParameter("unidadeOrganizacional", unidadeOrganizacional);
        if (q.getSingleResult() != null) {
            valor = (BigDecimal) q.getSingleResult();
        }
        return valor;
    }

    public SaidaRequisicaoMaterial validaAssociacaoDaRequisicaoComSaidaMaterial(RequisicaoMaterial requisicaoMaterial) {
        String hql = "select saida " +
            "       from SaidaRequisicaoMaterial saida"
            + "    where requisicaoMaterial = :requisicao";

        Query consulta = em.createQuery(hql);
        consulta.setParameter("requisicao", requisicaoMaterial);

        List resultList = consulta.getResultList();
        if (resultList == null || resultList.isEmpty()) {
            return null;
        }

        return (SaidaRequisicaoMaterial) resultList.get(0);
    }

    public List<RequisicaoMaterial> buscarRequisicaoConsumoMaterialComQtdeAutorizadaDisponivel(String parte, UsuarioSistema usuarioSistema) {
        String query = "" +
            "    select distinct " +
            "       rq.* " +
            "   from ItemRequisicaoMaterial item " +
            "     inner join requisicaomaterial rq on rq.id = item.requisicaomaterial_id " +
            "     inner join aprovacaomaterial ap on ap.requisicaomaterial_id = rq.id " +
            "     inner join gestorlocalestoque gestor on gestor.LOCALESTOQUE_ID = rq.LOCALESTOQUEORIGEM_ID " +
            "           and trunc(to_date(:dataOperacao)) BETWEEN gestor.INICIOVIGENCIA AND COALESCE(gestor.FIMVIGENCIA, trunc(to_date(:dataOperacao))) " +
            "   where item.quantidadeautorizada <> item.quantidadeatendida " +
            "     and rq.tiposituacaorequisicao <> '" + SituacaoRequisicaoMaterial.ATENDIDA_TOTALMENTE.name() + "'" +
            "     and gestor.USUARIOSISTEMA_ID = :idUsuario " +
            "and (lower(rq.descricao) like :parte" +
            "                or rq.numero like :parte " +
            "                or rq.dataRequisicao like :parte)" +
            "   and rq.tiporequisicao = :tipoConsumo " +
            "  order by rq.numero desc ";
        Query q = em.createNativeQuery(query, RequisicaoMaterial.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("tipoConsumo", TipoRequisicaoMaterial.CONSUMO.name());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("idUsuario", usuarioSistema.getId());
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public List<RequisicaoMaterial> buscarRequisicaoTransferenciaMaterialComQtdeAutorizadaDisponivel(String parte) {
        String query = "" +
            "    select distinct " +
            "       rq.* " +
            "   from ItemRequisicaoMaterial item " +
            "     inner join requisicaomaterial rq on rq.id = item.requisicaomaterial_id " +
            "     inner join aprovacaomaterial ap on ap.requisicaomaterial_id = rq.id " +
            "   where item.quantidadeAutorizada > 0  " +
            "     and item.quantidadeAtendida < item.quantidadeAutorizada " +
            "     and (lower(rq.descricao) like :parte" +
            "                or rq.numero like :parte " +
            "                or rq.dataRequisicao like :parte)" +
            "   and rq.tiporequisicao = :tipoTransferencia ";
        Query q = em.createNativeQuery(query, RequisicaoMaterial.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("tipoTransferencia", TipoRequisicaoMaterial.TRANSFERENCIA.name());
        q.setMaxResults(50);
        return q.getResultList();
    }

    public List<RequisicaoMaterial> buscarRequisicaoPorPeriodo(String parte, Date inicio, Date fim) {
        String hql = " from RequisicaoMaterial rm "
            + "   where (rm.descricao like :parte or to_char(rm.numero) like :parte) ";

        if (inicio != null) {
            hql += " and rm.dataRequisicao >= :inicio ";
        }
        if (fim != null) {
            hql += " and rm.dataRequisicao <= :fim ";
        }

        hql += " order by rm.id desc";

        Query q = em.createQuery(hql);
        q.setParameter("parte", "%" + parte.trim() + "%");
        if (inicio != null) {
            q.setParameter("inicio", inicio);
        }
        if (fim != null) {
            q.setParameter("fim", fim);
        }
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public RequisicaoMaterial atribuirSituacaoRequisicao(RequisicaoMaterial requisicaoMaterial, Boolean isSaidaParcial) {
        if (requisicaoMaterial.isRequisicaoTransferencia()) {
            List<EntradaMaterial> entradas = entradaMaterialFacade.recuperarEntradasDaRequisicao(requisicaoMaterial);
            boolean temEntrada = !Util.isListNullOrEmpty(entradas);

            if (isSaidaParcial) {
                requisicaoMaterial.setTipoSituacaoRequisicao(temEntrada ? SituacaoRequisicaoMaterial.SAIDA_PARCIAL_ENTRADAS_REALIZADAS : SituacaoRequisicaoMaterial.SAIDA_PARCIAL_ENTRADA_NAO_REALIZADA);
            } else {
                requisicaoMaterial.setTipoSituacaoRequisicao(temEntrada ? SituacaoRequisicaoMaterial.SAIDA_TOTAL_ENTRADAS_REALIZADAS : SituacaoRequisicaoMaterial.SAIDA_TOTAL_ENTRADA_NAO_REALIZADA);
            }
            return requisicaoMaterial;
        }
        requisicaoMaterial.setTipoSituacaoRequisicao(isSaidaParcial ? SituacaoRequisicaoMaterial.ATENDIDA_PARCIALMENTE : SituacaoRequisicaoMaterial.ATENDIDA_TOTALMENTE);
        return requisicaoMaterial;
    }

    public Long count(String sql) {
        try {
            Query query = em.createNativeQuery(sql);
            return ((BigDecimal) query.getSingleResult()).longValue();
        } catch (NoResultException nre) {
            return 0L;
        }
    }

    public void criarNotificacaoNovaRequisicaoMaterial(RequisicaoMaterial requisicaoMaterial) {
        String msg = "Uma nova requisição de material N° " + requisicaoMaterial.getNumero() + " - " + requisicaoMaterial.getDescricao()
            + " foi criada por " + requisicaoMaterial.getRequerenteEAutorizador().getNome() + ", "
            + hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), requisicaoMaterial.getUnidadeRequerente(), sistemaFacade.getDataOperacao());

        String link = "/requisicao-de-material/ver/" + requisicaoMaterial.getId() + "/";

        Notificacao notificacao = new Notificacao();
        notificacao.setDescricao(msg);
        notificacao.setLink(link);
        notificacao.setGravidade(Notificacao.Gravidade.ATENCAO);
        notificacao.setTitulo(TipoNotificacao.REQUISICAO_MATERIAL.getDescricao());
        notificacao.setTipoNotificacao(TipoNotificacao.REQUISICAO_MATERIAL);
        notificacao.setUsuarioSistema(requisicaoMaterial.getRequerenteEAutorizador());
        notificacao.setUnidadeOrganizacional(requisicaoMaterial.getUnidadeRequerente());
        NotificacaoService.getService().notificar(notificacao);
    }

    public void criarNotificacaoRequisicaoNaoAtendida(RequisicaoMaterial requisicaoMaterial) {
        String msg = "A requisição de material N° " + requisicaoMaterial.getNumero() + " - " + requisicaoMaterial.getDescricao()
            + "  está " + requisicaoMaterial.getTipoSituacaoRequisicao().getDescricao() + " com material reservado há mais de 1 semana.";

        String link = "/requisicao-de-material/ver/" + requisicaoMaterial.getId() + "/";

        Notificacao notificacao = new Notificacao();
        notificacao.setDescricao(msg);
        notificacao.setLink(link);
        notificacao.setGravidade(Notificacao.Gravidade.ATENCAO);
        notificacao.setTitulo(TipoNotificacao.REQUISICAO_MATERIAL.getDescricao());
        notificacao.setTipoNotificacao(TipoNotificacao.REQUISICAO_MATERIAL);
        notificacao.setUsuarioSistema(requisicaoMaterial.getRequerenteEAutorizador());
        notificacao.setUnidadeOrganizacional(requisicaoMaterial.getUnidadeRequerente());
        NotificacaoService.getService().notificar(notificacao);
    }

    public List<RequisicaoMaterial> buscarRequisicoesNaoAtendidasHaMaisDeUmaSemana() {
        String sql = " select req.* from requisicaomaterial req " +
            " where (req.tiposituacaorequisicao = :totalAprovadaNaoAtendida " +
            "    or req.tiposituacaorequisicao = :parciAprovadaNaoAtendida " +
            "    or req.tiposituacaorequisicao = :parcialmenteAtendida  " +
            "  AND TRUNC(TO_DATE(:dataOperacao, 'dd/MM/yyyy')) - TRUNC(req.DATAREQUISICAO) > 7 )";
        Query q = em.createNativeQuery(sql, RequisicaoMaterial.class);
        q.setParameter("totalAprovadaNaoAtendida", SituacaoRequisicaoMaterial.APROVADA_TOTALMENTE_E_NAO_ATENDIDA);
        q.setParameter("parciAprovadaNaoAtendida", SituacaoRequisicaoMaterial.APROVADA_PARCIALMENTE_E_NAO_ATENDIDA);
        q.setParameter("parcialmenteAtendida", SituacaoRequisicaoMaterial.ATENDIDA_PARCIALMENTE);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(new Date()));
        try {
            return q.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<>();
        }
    }

    public void lancarNotificacaoRequisicaoNaoAtentida() {
        for (RequisicaoMaterial requisicaoMaterial : buscarRequisicoesNaoAtendidasHaMaisDeUmaSemana()) {
            criarNotificacaoRequisicaoNaoAtendida(requisicaoMaterial);
        }
    }

    public LocalEstoqueFacade getLocalEstoqueFacade() {
        return localEstoqueFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public SingletonGeradorCodigo getSingletonGeradorCodigo() {
        return singletonGeradorCodigo;
    }

    public RequisicaoMaterialFacade getRequisicaoMaterialFacade() {
        return requisicaoMaterialFacade;
    }

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public MaterialFacade getMaterialFacade() {
        return materialFacade;
    }
}
