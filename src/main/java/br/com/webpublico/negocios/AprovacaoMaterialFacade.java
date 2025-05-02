/*
 * Codigo gerado automaticamente em Mon Feb 27 15:06:12 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoRequisicaoMaterial;
import br.com.webpublico.enums.TipoAprovacaoMaterial;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class AprovacaoMaterialFacade extends AbstractFacade<AprovacaoMaterial> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private RequisicaoMaterialFacade requisicaoMaterialFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private EstoqueFacade estoqueFacade;
    @EJB
    private UsuarioSistemaFacade aprovadorFacade;
    @EJB
    private PoliticaDeEstoqueFacade politicaDeEstoqueFacade;
    @EJB
    private LocalEstoqueFacade localEstoqueFacade;
    @EJB
    private MaterialFacade materialFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private SaidaMaterialFacade saidaMaterialFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AprovacaoMaterialFacade() {
        super(AprovacaoMaterial.class);
    }

    @Override
    public AprovacaoMaterial recuperar(Object id) {
        AprovacaoMaterial entity = super.recuperar(id);
        Hibernate.initialize(entity.getItensAprovados());
        return entity;
    }

    public RequisicaoMaterialFacade getRequisicaoMaterialFacade() {
        return requisicaoMaterialFacade;
    }

    public Long recuperarIdRevisaoAuditoria(Long id) {
        String sql = " select rev.id from aprovacaomaterial_aud aud " +
            " inner join revisaoauditoria rev on rev.id = aud.rev " +
            " where rev.id = (select rev from aprovacaomaterial_aud aaud where id = :id and aaud.revtype = 0) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("id", id);
        try {
            return ((BigDecimal) q.getSingleResult()).longValue();
        } catch (Exception ex) {
            return null;
        }
    }

    public List<ItemAprovacaoMaterial> buscarItensAvaliacaoRequisicao(AprovacaoMaterial entity) {
        String sql = "select item.* from ItemAprovacaoMaterial item where item.aprovacaoMaterial_id = :param ";
        Query q = em.createNativeQuery(sql, ItemAprovacaoMaterial.class);
        q.setParameter("param", entity.getId());
        try {
            return q.getResultList();
        } catch (Exception e) {
            return Lists.newArrayList();
        }
    }

    public ReservaEstoque recuperarReservaEstoque(ItemAprovacaoMaterial iam) {
        String sql = "select re.* from reservaestoque re where re.itemaprovacaomaterial_id = :item";
        Query q = em.createNativeQuery(sql, ReservaEstoque.class);
        q.setParameter("item", iam.getId());
        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        return (ReservaEstoque) resultList.get(0);
    }

    public List<AprovacaoMaterial> buscarAprovacaoMaterial(String parte) {
        String sql = "" +
            "  select am.* from aprovacaomaterial am " +
            "     inner join requisicaomaterial req on req.id = am.requisicaomaterial_id" +
            "   where (lower(am.numero) like :parte" +
            "                or req.numero like :parte " +
            "                or req.descricao like :parte) " +
            "  order by am.numero desc ";
        Query q = em.createNativeQuery(sql, AprovacaoMaterial.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public AprovacaoMaterial recuperarAprovacaoRequisicaoAndItens(RequisicaoMaterial rm) {
        try {
            AprovacaoMaterial aprovacao = (AprovacaoMaterial) em.createNativeQuery("SELECT * FROM APROVACAOMATERIAL WHERE REQUISICAOMATERIAL_ID = :REQUISICAO_ID", AprovacaoMaterial.class).setParameter("REQUISICAO_ID", rm.getId()).getSingleResult();
            aprovacao.getItensAprovados().size();
            return aprovacao;
        } catch (NoResultException ex) {
            return null;
        }
    }

    public List<ItemAprovacaoMaterial> buscarItensAprovados(RequisicaoMaterial rm) {
        String sql = " " +
            "  select item.* from itemaprovacaomaterial item " +
            "     inner join aprovacaomaterial am on am.id = item.aprovacaomaterial_id  " +
            "     where am.requisicaomaterial_id = :idRequisicao  " +
            "     and item.tipoaprovacao in (:tiposAprovados)  ";
        Query q = em.createNativeQuery(sql, ItemAprovacaoMaterial.class);
        q.setParameter("idRequisicao", rm.getId());
        q.setParameter("tiposAprovados", Util.getListOfEnumName(TipoAprovacaoMaterial.getTiposAprovados()));
        try {
            return q.getResultList();
        } catch (Exception e) {
            return Lists.newArrayList();
        }
    }

    public boolean isItemAprovado(ItemRequisicaoMaterial itemReq) {
        String sql = " " +
            "  select item.* from itemaprovacaomaterial item " +
            "     where item.itemRequisicaoMaterial_id = :idItemReq  " +
            "     and item.tipoaprovacao in (:tiposAprovados)  ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItemReq", itemReq.getId());
        q.setParameter("tiposAprovados", Util.getListOfEnumName(TipoAprovacaoMaterial.getTiposAprovados()));
        try {
            return !q.getResultList().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }


    @Override
    public void remover(AprovacaoMaterial aprovacao) {
        aprovacao.setRequisicaoMaterial(requisicaoMaterialFacade.recarregar(aprovacao.getRequisicaoMaterial()));
        aprovacao.zerarQuantidadeAprovadaDosItensRequisicaoMaterial();
        aprovacao.getRequisicaoMaterial().setTipoSituacaoRequisicao(SituacaoRequisicaoMaterial.NAO_AVALIADA);

        em.merge(aprovacao.getRequisicaoMaterial());

        for (ItemAprovacaoMaterial iam : aprovacao.getItensAprovados()) {
            em.remove(recuperarReservaEstoque(iam));
        }
        super.remover(aprovacao);
    }

    @Override
    public void salvar(AprovacaoMaterial aprovacao) {
        aprovacao.atribuirTipoSituacaoDaRequisicao();
        atribuirQuantidadeAprovadaEMaterialAprovadoAosItensRequisitados(aprovacao);

        em.merge(aprovacao.getRequisicaoMaterial());

        if (aprovacao.getId() == null) {
            aprovacao.setNumero(singletonGeradorCodigo.getProximoCodigo(AprovacaoMaterial.class, "numero"));
            em.persist(aprovacao);
            criarNotificacaoAvaliacaoRequisicaoMaterial(aprovacao);
        } else {
            em.merge(aprovacao);
        }
        reservarMateriais(aprovacao);
    }

    private void atribuirQuantidadeAprovadaEMaterialAprovadoAosItensRequisitados(AprovacaoMaterial aprovacao) {
        for (ItemAprovacaoMaterial iam : aprovacao.getItensAprovados()) {
            iam.getItemRequisicaoMaterial().setQuantidadeAutorizada(iam.getQuantidadeAprovada());
            iam.getItemRequisicaoMaterial().setMaterialAprovado(iam.getMaterial());
            em.merge(iam.getItemRequisicaoMaterial());
        }
    }

    private void reservarMateriais(AprovacaoMaterial aprovacao) {
        LocalEstoque origem = aprovacao.getRequisicaoMaterial().getLocalEstoqueOrigem();

        for (ItemAprovacaoMaterial iam : aprovacao.getItensAprovados()) {
            ReservaEstoque re = criarNovaOuRecuperarReserva(origem, iam);
            if (re.getId() == null && !iam.getTipoAprovacao().equals(TipoAprovacaoMaterial.REJEITADO)) {
                em.persist(re);
            } else if (iam.getTipoAprovacao().equals(TipoAprovacaoMaterial.REJEITADO)) {
                em.remove(re);
            }
        }
    }

    private ReservaEstoque criarNovaOuRecuperarReserva(LocalEstoque local, ItemAprovacaoMaterial item) {
        ReservaEstoque re = recuperarReservaEstoque(item);
        if (re == null) {
            re = new ReservaEstoque(local, item);
        }
        return re;
    }

    public void criarNotificacaoAvaliacaoRequisicaoMaterial(AprovacaoMaterial aprovacaoMaterial) {
        RequisicaoMaterial requisicaoMaterial = aprovacaoMaterial.getRequisicaoMaterial();

        String msg = "A requisição de material N° " + requisicaoMaterial.getNumero() + " - " + requisicaoMaterial.getDescricao()
            + " foi avaliada por " + aprovacaoMaterial.getAprovador().getNome() + " e está "
            + requisicaoMaterial.getTipoSituacaoRequisicao().getDescricao() + ".";

        String link = "/requisicao-de-material/ver/" + requisicaoMaterial.getId() + "/";

        Notificacao notificacao = new Notificacao();
        notificacao.setDescricao(msg);
        notificacao.setLink(link);
        notificacao.setGravidade(Notificacao.Gravidade.ATENCAO);
        notificacao.setTitulo(TipoNotificacao.REQUISICAO_MATERIAL.getDescricao());
        notificacao.setTipoNotificacao(TipoNotificacao.REQUISICAO_MATERIAL);
        notificacao.setUsuarioSistema(aprovacaoMaterial.getAprovador());
        NotificacaoService.getService().notificar(notificacao);
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public EstoqueFacade getEstoqueFacade() {
        return estoqueFacade;
    }

    public UsuarioSistemaFacade getAprovadorFacade() {
        return aprovadorFacade;
    }

    public PoliticaDeEstoqueFacade getPoliticaDeEstoqueFacade() {
        return politicaDeEstoqueFacade;
    }

    public LocalEstoqueFacade getLocalEstoqueFacade() {
        return localEstoqueFacade;
    }

    public MaterialFacade getMaterialFacade() {
        return materialFacade;
    }

    public SingletonGeradorCodigo getSingletonGeradorCodigo() {
        return singletonGeradorCodigo;
    }

    public SaidaMaterialFacade getSaidaMaterialFacade() {
        return saidaMaterialFacade;
    }
}
