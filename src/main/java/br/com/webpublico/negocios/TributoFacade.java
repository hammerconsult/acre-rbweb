/*
 * Codigo gerado automaticamente em Mon Dec 05 14:26:26 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ContaTributoReceita;
import br.com.webpublico.entidades.Tributo;
import br.com.webpublico.negocios.tributario.services.ServiceIntegracaoTributarioContabil;
import br.com.webpublico.util.StringUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class TributoFacade extends AbstractFacade<Tributo> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EntidadeFacade entidadeFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private EnquadramentoTributoExercicioFacade enquadramentoFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TributoFacade() {
        super(Tributo.class);
    }

    public EntidadeFacade getEntidadeFacade() {
        return entidadeFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public EnquadramentoTributoExercicioFacade getEnquadramentoFacade() {
        return enquadramentoFacade;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    public FonteDeRecursosFacade getFonteDeRecursosFacade() {
        return fonteDeRecursosFacade;
    }

    @Override
    public Tributo recuperar(Object id) {
        Tributo toReturn = em.find(Tributo.class, id);
        if (toReturn != null) {
            toReturn.getContaTributoReceitas().size();
        }
        return toReturn;
    }

    public boolean validaDescricao(Tributo tributo) {
        if (tributo == null || tributo.getDescricao() == null || "".equals(tributo.getDescricao())) {
            return true;
        }
        String hql = " from Tributo t where lower(t.descricao) = :des";

        if (tributo.getId() != null) {
            hql += " and t != :tributo";
        }
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("des", tributo.getDescricao().trim().toLowerCase());
        if (tributo.getId() != null) {
            q.setParameter("tributo", tributo);
        }
        //System.out.println("q.getResultList().isEmpty() " + q.getResultList().isEmpty());
        return (q.getResultList().isEmpty());
    }

    public Tributo recuperarPorDescricaoExata(String descricao) {
        Query q = em.createQuery("from Tributo t where lower(t.descricao) = :descricao");
        q.setParameter("descricao", descricao.trim().toLowerCase());
        List tributos = q.getResultList();
        if (tributos.isEmpty()) {
            return null;
        } else {
            return (Tributo) tributos.get(0);
        }
    }

    public Long sugereCodigoTributo() {
        Query q = em.createNativeQuery("SELECT coalesce(max(codigo), 0) + 1 AS codigo FROM Tributo");
        BigDecimal resultado = (BigDecimal) q.getSingleResult();
        return resultado.longValue();
    }

    public boolean codigoJaExiste(Object selecionado) {
        if (selecionado == null || ((Tributo) selecionado).getCodigo() == null || ((Tributo) selecionado).getCodigo().intValue() <= 0) {
            return false;
        }
        String hql = "from Tributo tr where tr.codigo = :codigo";
        if (((Tributo) selecionado).getId() != null) {
            hql += " and tr != :tributo";
        }
        Query q = em.createQuery(hql);
        q.setParameter("codigo", ((Tributo) selecionado).getCodigo());
        if (((Tributo) selecionado).getId() != null) {
            q.setParameter("tributo", selecionado);
        }
        return !q.getResultList().isEmpty();
    }

    public List<Tributo> tributosOrdenadosPorDescricao() {
        return em.createQuery(" select t from Tributo t order by t.descricao").getResultList();
    }

    public List<Tributo> listaImpostoTaxa(String parte) {
        String hql = " select t from Tributo t "
            + " where (t.tipoTributo = '" + Tributo.TipoTributo.TAXA.name() + "' or "
            + " t.tipoTributo = '" + Tributo.TipoTributo.IMPOSTO.name() + "') "
            + " and lower(t.descricao) like :parte";
        Query q = em.createQuery(hql);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        return q.getResultList();
    }

    public List<Tributo> buscarTributoPorTipo(String parte, Tributo.TipoTributo tipoTributo) {
        String codigo = StringUtil.retornaApenasNumeros(parte);
        String hql = " select t from Tributo t "
            + " where t.tipoTributo = :tipoTributo"
            + " and lower(t.descricao) like :parte ";
        if (!"".equals(codigo)) {
            hql +=" or t.codigo = :codigo";
        }

        Query q = em.createQuery(hql);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        if (!"".equals(codigo)) {
            q.setParameter("codigo", Long.parseLong(codigo));
        }
        q.setParameter("tipoTributo", tipoTributo);
        return q.getResultList();
    }

    @Override
    public void salvarNovo(Tributo entity) {
        removerContas(entity.getContasParaRemover());
        super.salvarNovo(entity);
    }

    @Override
    public void salvar(Tributo entity) {
        removerContas(entity.getContasParaRemover());
        super.salvar(entity);
    }

    private void removerContas(List<ContaTributoReceita> contasParaRemover) {
        limparMapaDeContasDaIntegracao();
        if (contasParaRemover == null || contasParaRemover.isEmpty())
            return;

        for (ContaTributoReceita ctr : contasParaRemover) {
            if (ctr.getId() != null) {
                ctr = em.find(ContaTributoReceita.class, ctr.getId());
                em.remove(ctr);
            }
        }

    }

    private void limparMapaDeContasDaIntegracao() {
        try {
            ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
            ServiceIntegracaoTributarioContabil service = (ServiceIntegracaoTributarioContabil) ap.getBean("serviceIntegracaoTributarioContabil");
            service.limparMapaContas();
        } catch (Exception e) {
            logger.error("Erro ao limpar o mapa de contas: ", e);
        }
    }

    public List<Tributo> buscarTributosPorDescricao(String parte) {
        String sql = " select tributo.* from tributo " +
            " where trim(lower(replace(tributo.descricao,'.',''))) like :parte " +
            " order by tributo.descricao ";
        Query q = em.createNativeQuery(sql, Tributo.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase().replace(".","") + "%");
        return q.getResultList();
    }

    public List<Tributo> listaTributosOrdenadoPorDescricao() {
        String sql = "select d.* from Tributo d order by d.descricao";
        Query q = em.createNativeQuery(sql, Tributo.class);
        return q.getResultList();
    }
}
