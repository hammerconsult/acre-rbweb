package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.FuncionalidadeAdministrativo;
import br.com.webpublico.singletons.SingletonGeradorCodigoAdministrativo;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 17/04/15
 * Time: 09:12
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class FormularioCotacaoFacade extends AbstractFacade<FormularioCotacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ObjetoCompraFacade objetoCompraFacade;
    @EJB
    private SolicitacaoReposicaoEstoqueFacade solicitacaoReposicaoEstoqueFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private SingletonGeradorCodigoAdministrativo singletonGeradorCodigoAdministrativo;
    @EJB
    private IntencaoRegistroPrecoFacade intencaoRegistroPrecoFacade;

    public FormularioCotacaoFacade() {
        super(FormularioCotacao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public FormularioCotacao recuperar(Object id) {
        FormularioCotacao entity = super.recuperar(id);
        if (entity.getLotesFormulario() != null) {
            Hibernate.initialize(entity.getLotesFormulario());
            for (LoteFormularioCotacao loteFormularioCotacao : entity.getLotesFormulario()) {
                if (loteFormularioCotacao.getItensLoteFormularioCotacao() != null) {
                    Hibernate.initialize(loteFormularioCotacao.getItensLoteFormularioCotacao());
                }
            }
        }
        if (entity.getDetentorDocumentoLicitacao() != null) {
            Hibernate.initialize(entity.getDetentorDocumentoLicitacao().getDocumentoLicitacaoList());
        }
        return entity;
    }

    @Override
    public Object recuperar(Class entidade, Object id) {
        return recuperar(id);
    }

    public FormularioCotacao salvarFormulario(FormularioCotacao entity, SolicitacaoReposicaoEstoque solicitacaoReposicaoEstoque) {
        if (entity.getNumero() == null) {
            entity.setNumero(singletonGeradorCodigoAdministrativo.getProximoCodigoPorFuncionalidade(entity.getUnidadeOrganizacional().getId(), entity.getExercicio().getId(), FuncionalidadeAdministrativo.FORMULARIO_COTACAO));
        }
        salvarEspecificacaoObjetoCompra(entity);
        salvarSolicitacaoReposicaoEstoque(solicitacaoReposicaoEstoque);
        return em.merge(entity);
    }

    private void salvarEspecificacaoObjetoCompra(FormularioCotacao entity) {
        List<ObjetoDeCompraEspecificacao> list = Lists.newArrayList();
        for (LoteFormularioCotacao lote : entity.getLotesFormulario()) {
            for (ItemLoteFormularioCotacao item : lote.getItensLoteFormularioCotacao()) {
                if (!Strings.isNullOrEmpty(item.getDescricaoComplementar())) {
                    objetoCompraFacade.adicionarNovaEspecificacao(item.getObjetoCompra(), item.getDescricaoComplementar(), list);
                }
            }
        }
        objetoCompraFacade.salvarEspecificacoes(list);
    }

    private void salvarSolicitacaoReposicaoEstoque(SolicitacaoReposicaoEstoque solicitacaoReposicaoEstoque) {
        if (solicitacaoReposicaoEstoque != null) {
            getSolicitacaoReposicaoEstoqueFacade().salvar(solicitacaoReposicaoEstoque);
        }
    }

    public List<FormularioCotacao> buscarFormularioCotacaoPorAnoOrNumeroOndeUsuarioEhGestorLicitacao(String filter, UsuarioSistema usuarioSistema) {
        String sql = "  select fc.* \n" +
            "   from formulariocotacao fc \n" +
            "  inner join exercicio e on fc.exercicio_id = e.id \n" +
            " where (to_char(e.ano) like :filter  or to_char(fc.numero) like :filter  or lower(fc.objeto) like :filter )" +
            "   and exists (select 1 from usuariounidadeorganizacio u_un \n" +
            "               where u_un.usuariosistema_id = :id_usuario \n" +
            "                 and u_un.unidadeorganizacional_id = fc.unidadeorganizacional_id \n" +
            "                 and u_un.gestorlicitacao = :gestor_licitacao)\n" +
            " order by e.ano desc, fc.numero desc";
        Query q = em.createNativeQuery(sql, FormularioCotacao.class);
        q.setParameter("filter", "%" + filter.toLowerCase().trim() + "%");
        q.setParameter("id_usuario", usuarioSistema.getId());
        q.setParameter("gestor_licitacao", Boolean.TRUE);
        q.setMaxResults(30);
        return q.getResultList();
    }

    public List<FormularioCotacao> buscarFormularioCotacao(String filter) {
        String sql = "  select fc.* from formulariocotacao fc " +
            "  inner join exercicio ex on ex.id = fc.exercicio_id " +
            "   where (to_char(fc.numero) like :filter  " +
            "           or to_char(fc.numero) || '/' || to_char(ex.ano) like :filter " +
            "           or lower(fc.objeto) like :filter)" +
            " order by fc.numero desc ";
        Query q = em.createNativeQuery(sql, FormularioCotacao.class);
        q.setParameter("filter", "%" + filter.toLowerCase().trim() + "%");
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public SolicitacaoReposicaoEstoque recuperarSolicitacaoReposicaoVinculada(FormularioCotacao formulario) {
        String sql = " SELECT DISTINCT sre.*"
            + "               FROM solicitacaorepestoque sre"
            + "         INNER JOIN itemsolrepestoque isre ON isre.solicitacaorepestoque_id = sre.id"
            + "         INNER JOIN itemloteformulariocotacao ilfc ON ilfc.id = isre.itemloteformulario_id"
            + "         INNER JOIN loteFormularioCotacao lfc ON ilfc.loteFormularioCotacao_id = lfc.id"
            + "         INNER JOIN formularioCotacao fm ON fm.id = lfc.formulariocotacao_id"
            + "              WHERE fm.id = :param";
        Query q = em.createNativeQuery(sql, SolicitacaoReposicaoEstoque.class);
        q.setParameter("param", formulario.getId());
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (SolicitacaoReposicaoEstoque) q.getSingleResult();
    }

    public FormularioCotacao recuperarFormularioCotacaoPorIrp(IntencaoRegistroPreco irp) {
        String sql = " select fc.* from FormularioCotacao fc where fc.INTENCAOREGISTROPRECO_ID = :idIrp ";
        Query q = em.createNativeQuery(sql, FormularioCotacao.class);
        q.setParameter("idIrp", irp.getId());
        try {
            return (FormularioCotacao) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public boolean hasFormularioUtilizadoEmCotacao(FormularioCotacao formulario) {
        if (formulario.getId() == null) {
            return false;
        }
        String sql = "select c.* from cotacao c where c.FORMULARIOCOTACAO_ID = :idFormulario ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idFormulario", formulario.getId());
        return !q.getResultList().isEmpty();
    }

    public ObjetoCompraFacade getObjetoCompraFacade() {
        return objetoCompraFacade;
    }

    public SolicitacaoReposicaoEstoqueFacade getSolicitacaoReposicaoEstoqueFacade() {
        return solicitacaoReposicaoEstoqueFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }


    public IntencaoRegistroPrecoFacade getIntencaoRegistroPrecoFacade() {
        return intencaoRegistroPrecoFacade;
    }


    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }
}
