/*
 * Codigo gerado automaticamente em Thu May 05 14:24:15 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class ProvisaoPPAFacade extends AbstractFacade<ProvisaoPPA> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ProvisaoPPADespesaFacade provisaoPPADespesaFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private LOAFacade loaFacade;
    @EJB
    private ProvisaoPPAFonteFacade provisaoPPAFonteFacade;
    @EJB
    private LDOFacade ldoFacade;
    @EJB
    private SubProjetoAtividadeFacade subProjetoAtividadeFacade;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    @EJB
    private ProjetoAtividadeFacade projetoAtividadeFacade;
    @EJB
    private ExtensaoFonteRecursoFacade extensaoFonteRecursoFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;


    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProvisaoPPAFacade() {
        super(ProvisaoPPA.class);
    }

    @Override
    public ProvisaoPPA recuperar(Object id) {
        ProvisaoPPA pp = em.find(ProvisaoPPA.class, id);
        pp.getMedicaoProvisaoPPAS().size();
        return pp;
    }


    public List<ProvisaoPPA> listaProvisaoPPAs(String s, Exercicio e, String... atributos) {

        String hql = "select a from ProvisaoPPA a, Exercicio e, AcaoPPA ac where a.exercicio =:e and ";
        hql += "a.exercicio=e.id and a.acao=ac and ";
        for (String atributo : atributos) {
            hql += "lower(ac." + atributo + ") like :filtro OR ";
        }

        hql = hql.substring(0, hql.length() - 3);
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("e", e);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<ProvisaoPPA> recuperaProvisoesPPA(ProdutoPPA prod) {
        List<ProvisaoPPA> recuperaPro = new ArrayList<ProvisaoPPA>();
        String hql = "from ProvisaoPPA p where p.produtoPPA = :prod ";
        if (prod != null) {
            Query q = em.createQuery(hql);
            q.setParameter("prod", prod);
            recuperaPro = q.getResultList();
        } else {
            recuperaPro = new ArrayList<ProvisaoPPA>();
        }
        return recuperaPro;
    }

    public List<LOA> validaEfetivacaoLoa(Exercicio ex) {
        String hql = "select lo from LOA lo inner join lo.ldo as ld where ld.exercicio= :param and lo.efetivada = true";
        Query q = em.createQuery(hql);
        q.setParameter("param", ex);
        return q.getResultList();
    }

    public List<ProvisaoPPA> buscarProvisoesPorAno(String parte, ProdutoPPA  produtoPPA){
        String sql = " select prov.* from PROVISAOPPA prov  " +
                     " inner join EXERCICIO ex on ex.ID = prov.EXERCICIO_ID " +
                     " where prov.PRODUTOPPA_ID = :idProdutoPPA " +
                     " and ex.ANO like :parte ";
        Query q = em.createNativeQuery(sql, ProvisaoPPA.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("idProdutoPPA", produtoPPA.getId());
        try{
            return q.getResultList();
        } catch (Exception ex){
            return new ArrayList<>();
        }
    }

    public ProvisaoPPADespesaFacade getProvisaoPPADespesaFacade() {
        return provisaoPPADespesaFacade;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public LOAFacade getLoaFacade() {
        return loaFacade;
    }

    public ProvisaoPPAFonteFacade getProvisaoPPAFonteFacade() {
        return provisaoPPAFonteFacade;
    }

    public LDOFacade getLdoFacade() {
        return ldoFacade;
    }

    public SubProjetoAtividadeFacade getSubProjetoAtividadeFacade() {
        return subProjetoAtividadeFacade;
    }

    public FonteDeRecursosFacade getFonteDeRecursosFacade() {
        return fonteDeRecursosFacade;
    }

    public ProjetoAtividadeFacade getProjetoAtividadeFacade() {
        return projetoAtividadeFacade;
    }

    public void removerFonte(ProvisaoPPAFonte provisaoPPAFonte) {
        em.remove(em.find(ProvisaoPPAFonte.class, provisaoPPAFonte.getId()));
    }

    public ProvisaoPPA getProvisaoPPAPorProdutoAndExercicio(ProdutoPPA produtoPPA, Exercicio exercicio) {
        if (produtoPPA != null) {
            if (produtoPPA.getId() != null) {
                ProdutoPPA recuperado = projetoAtividadeFacade.getProdutoPPAFacade().recuperar(produtoPPA.getId());
                List<ProvisaoPPA> provisoes = recuperado.getProvisoes();
                for (ProvisaoPPA provisoe : provisoes) {
                    if (provisoe.getExercicio().getId().equals(exercicio.getId())) {
                        return provisoe;
                    }
                }
            }
        }
        return null;
    }

    public ExtensaoFonteRecursoFacade getExtensaoFonteRecursoFacade() {
        return extensaoFonteRecursoFacade;
    }

    public boolean isGestorOrcamento(UsuarioSistema usuario,UnidadeOrganizacional unidadeOrcamentaria, Date dataOperacao){
        return usuarioSistemaFacade.isGestorOrcamento(usuario, unidadeOrcamentaria, dataOperacao);
    }
}
