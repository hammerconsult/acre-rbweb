/*
 * Codigo gerado automaticamente em Tue Mar 01 17:36:11 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Lote;
import br.com.webpublico.entidades.Quadra;
import br.com.webpublico.entidades.Setor;
import br.com.webpublico.entidades.Testada;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class LoteFacade extends AbstractFacade<Lote> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private QuadraFacade quadraFacade;
    @EJB
    private AtributoFacade atributoFacade;
    @EJB
    private LogradouroFacade logradouroFacade;
    @EJB
    private LoteamentoFacade loteamentoFacade;
    @EJB
    private SetorFacade setorFacade;
    @EJB
    private FaceFacade faceFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private BairroFacade bairroFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LoteFacade() {
        super(Lote.class);
    }

    public QuadraFacade getQuadraFacade() {
        return quadraFacade;
    }

    public AtributoFacade getAtributoFacade() {
        return atributoFacade;
    }

    public LogradouroFacade getLogradouroFacade() {
        return logradouroFacade;
    }

    public LoteamentoFacade getLoteamentoFacade() {
        return loteamentoFacade;
    }

    public SetorFacade getSetorFacade() {
        return setorFacade;
    }

    public FaceFacade getFaceFacade() {
        return faceFacade;
    }

    public BairroFacade getBairroFacade() {
        return bairroFacade;
    }

    public ConfiguracaoTributarioFacade getConfiguracaoTributarioFacade() {
        return configuracaoTributarioFacade;
    }

    @Override
    public Lote recuperar(Object id) {
        try {
            Lote l = em.find(Lote.class, id);
            Hibernate.initialize(l.getCaracteristicasLote());
            Hibernate.initialize(l.getTestadas());
            for (Testada obj : l.getTestadas()) {
                if (obj.getFace() != null && obj.getFace().getLogradouroBairro() != null) {
                    obj.getFace().getLogradouroBairro().getLogradouro();
                    obj.getFace().getLogradouroBairro().getBairro();
                }
            }
            Hibernate.initialize(l.getLotesCondominio());
            return l;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Lote recuperarPorCodigoQuadraSetor(String codigo, Quadra quadra, Setor setor) {
        ////System.out.println("Código do Lote: " + codigo);
        Query q = em.createQuery(" select obj FROM Lote obj WHERE lower(obj.codigoLote) = :codigo AND "
            + "obj.quadra = :quadra AND obj.setor = :setor");
        q.setParameter("codigo", codigo.toLowerCase().trim());
        q.setParameter("quadra", quadra);
        q.setParameter("setor", setor);
        q.setMaxResults(1);
        List<Lote> result = q.getResultList();
        if (result != null && !result.isEmpty()) {
            final Lote retorno = result.get(0);
            Hibernate.initialize(retorno.getTestadas());
            return retorno;
        } else {
            return null;
        }
    }

    public List<Lote> recuperarLotePorQuadraSetor(Quadra quadra, Setor setor) {
        Query q = em.createQuery("select obj FROM Lote obj WHERE "
            + "obj.quadra = :quadra AND obj.setor = :setor order by obj.codigoLote");
        q.setParameter("quadra", quadra);
        q.setParameter("setor", setor);
        return q.getResultList();
    }

    public Testada recuperarTestadaPrincipal(Lote lote) {
        Query q = em.createQuery("select t from Testada t where t.lote = :lote and t.principal = true"
            + "");
        q.setParameter("lote", lote);
        q.setMaxResults(1);
        List<Testada> result = q.getResultList();
        if (result.isEmpty()) {
            return null;
        } else {
            return result.get(0);
        }
    }

    public boolean codigoJaExisteNaQuadra(Lote lote) {
        String hql = "from Lote lote where lote.quadra = :quadra and lote.codigoLote = :codigoLote";
        if (lote.getId() != null) {
            hql += " and lote != :lote";
        }
        Query q = em.createQuery(hql);
        q.setParameter("quadra", lote.getQuadra());
        q.setParameter("codigoLote", lote.getCodigoLote());
        if (lote.getId() != null) {
            q.setParameter("lote", lote);
        }
        return !q.getResultList().isEmpty();
    }

    public List<Lote> listaPorQuadra(String trim, Quadra quadra) {
        String hql = "select lote from Lote lote where lote.quadra = :quadra and lote.codigoLote like :parte";
        Query q = em.createQuery(hql);
        q.setParameter("quadra", quadra);
        q.setParameter("parte", "%" + trim + "%");
        return q.getResultList();
    }

    public List<Lote> listaFiltrando(String s) {
        String hql = "from Lote l  " +
            "where lower(l.codigoLote) like :filtro " +
            "or lower(l.descricaoLoteamento) like :filtro " +
            "or lower(l.quadra.descricaoLoteamento) like :filtro " +
            "or lower(l.quadra.descricao) like :filtro " +
            "or lower(l.setor.codigo) like :filtro " +
            "or lower(l.setor.nome) like :filtro";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(MAX_RESULTADOS_NO_AUTOCOMPLATE);
        return q.getResultList();
    }

    public Object recuperaAtributoPorLote(String atributo, Lote lote) {
        String hql = " Select " + atributo + " from Lote lote " +
            "       join lote.quadra quadra " +
            "       join lote.testadas testadas " +
            "       join testadas.face face " +
            "       join face.logradouroBairro logradouroBairro" +
            "       join logradouroBairro.logradouro logradouro" +
            "       join logradouroBairro.bairro bairro " +
            "  where lote.id = :id";
        Query q = em.createQuery(hql);
        q.setParameter("id", lote.getId());
        List<Object> result = q.getResultList();
        if (result != null && !result.isEmpty()) {
            return result.get(0);
        }
        return null;
    }

    public List recuperaAtributoPrincipaldoLote(String atributo, Lote lote) {
        String hql = " Select " + atributo + " from Lote lote " +
            "       join lote.testadas testada " +
            "       join testada.face face " +
            "       join face.logradouroBairro logradouroBairro" +
            "       join logradouroBairro.logradouro logradouro" +
            "       join logradouroBairro.bairro bairro" +
            "  where lote.id = :id and testada.principal = :principal";
        Query q = em.createQuery(hql);
        q.setParameter("id", lote.getId());
        q.setParameter("principal", Boolean.TRUE);
        List<Object> result = q.getResultList();
        if (result != null && !result.isEmpty()) {
            return result;
        }
        return Lists.newArrayList();
    }

    public List<Lote> buscarLotePorQuadraInicialAndQuadraFinal(Quadra quadraInicial, Quadra quadraFinal, String parte) {
        String sql = " from Lote l " +
            " where (l.quadra.codigo between :quadraInicial and :quadraFinal) " +
            "   and (l.codigoLote like :parte) " +
            " order by l.quadra.codigo, l.codigoLote ";
        Query q = em.createQuery(sql);
        q.setParameter("quadraInicial", quadraInicial.getCodigo());
        q.setParameter("quadraFinal", quadraFinal.getCodigo());
        q.setParameter("parte", "%" + parte.trim() + "%");
        return q.getResultList();
    }

}
