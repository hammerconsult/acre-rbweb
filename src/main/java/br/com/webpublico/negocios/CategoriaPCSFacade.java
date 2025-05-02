/*
 * Codigo gerado automaticamente em Sat Jul 02 09:02:20 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Cargo;
import br.com.webpublico.entidades.CategoriaPCS;
import br.com.webpublico.entidades.PlanoCargosSalarios;
import br.com.webpublico.entidades.rh.pccr.MesesProgressao;
import br.com.webpublico.entidades.rh.pccr.MesesProgressaoCategoria;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class CategoriaPCSFacade extends AbstractFacade<CategoriaPCS> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private List<CategoriaPCS> categoriaPCSs;
    @EJB
    private PlanoCargosSalariosFacade planoCargosSalariosFacade;

    public CategoriaPCSFacade() {
        super(CategoriaPCS.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<CategoriaPCS> getRaiz() {
        Query q = em.createQuery("from CategoriaPCS g where g.superior is null");
        try {
            return q.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public List<CategoriaPCS> getRaizPorPlano(PlanoCargosSalarios p) {
        Query q = em.createQuery("from CategoriaPCS g where g.superior is null and g.planoCargosSalarios = :p order by g.descricao");
        try {
            q.setParameter("p", p);
            return q.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public List<CategoriaPCS> getFilhosDe(CategoriaPCS c) {
        Query q = em.createQuery("from CategoriaPCS c where c.superior = :superior");
        q.setParameter("superior", c);
        return q.getResultList();
    }

    public List<CategoriaPCS> getFolhas() {
        Query q = em.createQuery("from CategoriaPCS c where c.filhos is empty");

        return q.getResultList();
    }

    public List<CategoriaPCS> getFolhas(PlanoCargosSalarios plano) {
        Query q = em.createQuery("from CategoriaPCS c where c.filhos is empty " +
            " and c.planoCargosSalarios = :plano order by c.descricao");
        q.setParameter("plano", plano);

        return q.getResultList();
    }

    public List<CategoriaPCS> getFolhasPorPlano(PlanoCargosSalarios plano) {
        Query q = em.createQuery("from CategoriaPCS c where c.filhos is empty and c.planoCargosSalarios = :plano and c.superior is not null");
        q.setParameter("plano", plano);

        return q.getResultList();
    }

    public List<CategoriaPCS> getFolhasPorPlanoCargo(PlanoCargosSalarios plano, Cargo cargo, Date dataVigencia) {
        Query q = em.createQuery("select c from CategoriaPCS c inner join c.cargosCategoriaPCS cargos where cargos.cargo = :cargo and "
            + " c.filhos is empty and c.planoCargosSalarios = :plano and c.superior is not null "
            + " and :dataVigencia >= cargos.cargo.inicioVigencia "
            + " and :dataVigencia <= coalesce(cargos.cargo.finalVigencia,:dataVigencia) ");
        q.setParameter("plano", plano);
        q.setParameter("cargo", cargo);
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(dataVigencia));

        return q.getResultList();
    }

    public List<CategoriaPCS> listaPorPlano(PlanoCargosSalarios p) {
        Query q = em.createQuery("from CategoriaPCS c where c.planoCargosSalarios = :p");
        q.setParameter("p", p);
        return q.getResultList();
    }

    public List<CategoriaPCS> listaCategoriaNoEnquadramentoPorPlano(PlanoCargosSalarios p) {
        Query q = em.createQuery(" select categoria from EnquadramentoPCS enquadramento "
            + " inner join enquadramento.categoriaPCS categoria "
            + " where categoria.planoCargosSalarios = :p "
            + " and :dataVigencia >= enquadramento.inicioVigencia "
            + " and :dataVigencia <= coalesce(enquadramento.finalVigencia,:dataVigencia) ");
        q.setParameter("p", p);
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(new Date()));
        return q.getResultList();
    }

    @Override
    public CategoriaPCS recuperar(Object id) {
        CategoriaPCS c = em.find(CategoriaPCS.class, id);
        Hibernate.initialize(c.getCargosCategoriaPCS());
        Hibernate.initialize(c.getMesesPromocao());
        Hibernate.initialize(c.getFilhos());
        for (CategoriaPCS categoriaPCS : c.getFilhos()) {
            Hibernate.initialize(categoriaPCS.getCargosCategoriaPCS());
        }
        if (c.temPlanoCargosSalarios()) {
            c.setPlanoCargosSalarios(planoCargosSalariosFacade.recuperar(c.getPlanoCargosSalarios().getId()));
        }
        return c;
    }

    public List<CategoriaPCS> listaCategoriaPCSs(PlanoCargosSalarios p) {
        String hql = "select a from CategoriaPCS a, PlanoCargosSalarios b ";
        hql += " where a.planoCargosSalarios and b.id = :superior";
        Query q = em.createQuery(hql);
        q.setParameter("superior", p);

        return q.getResultList();
    }

    public List<CategoriaPCS> listaFiltrandoPlanoCargoSalario(PlanoCargosSalarios planoCargosSalarios) {
        String hql = " from CategoriaPCS a ";
        String where = "";

        if (planoCargosSalarios.getId() != null) {
            where += " where a.planoCargosSalarios = :plano";
        } else {
            return new ArrayList<>();
        }

        hql += where + " order by a.descricao ";

        Query q = getEntityManager().createQuery(hql);

        if (planoCargosSalarios != null) {
            q.setParameter("plano", planoCargosSalarios);
        }

        return q.getResultList();
    }

    public List<CategoriaPCS> listaFiltrandoPCSVigente(PlanoCargosSalarios planoCargosSalarios, Date dataVigencia) {
        Query q = em.createQuery(" select categoria from CategoriaPCS categoria "
            + " inner join categoria.planoCargosSalarios plano "
            + " where plano = :pcs"
            + " and :dataVigencia >= plano.inicioVigencia "
            + " and :dataVigencia <= coalesce(plano.finalVigencia,:dataVigencia)"
            + " order by categoria.descricao ");
        q.setParameter("pcs", planoCargosSalarios);
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(dataVigencia));
        return q.getResultList();
    }

    public List<CategoriaPCS> getCategoriaPorPlanosX(PlanoCargosSalarios pcs) {
        List<CategoriaPCS> retorno = null;
        if (pcs != null) {
            Query q = em.createQuery("from CategoriaPCS obj where obj.planoCargosSalarios = :pcss order by obj.id");
            q.setParameter("pcss", pcs);
            retorno = q.getResultList();
        } else {
            Query q = em.createQuery("from CategoriaPCS obj order by obj.id");
            retorno = q.getResultList();
        }
        return retorno;
    }

    public List<CategoriaPCS> buscaCategoriasNoEnquadramentoPCS(PlanoCargosSalarios pcs) {
        String hql = " select distinct categoria from EnquadramentoPCS enquadramento "
            + " inner join enquadramento.categoriaPCS categoria "
            + " where categoria.planoCargosSalarios = :pcs order by categoria.descricao";//
        Query q = em.createQuery(hql);                //
        q.setParameter("pcs", pcs);

        return q.getResultList();
    }

    public List<CategoriaPCS> getCategoriaPCSs() {
        return categoriaPCSs;
    }

    public void setCategoriaPCSs(List<CategoriaPCS> categoriaPCSs) {
        this.categoriaPCSs = categoriaPCSs;
    }

    public CategoriaPCS recuperaCategoriaNoEnquadramentoFuncionalVigente(CategoriaPCS categoriaPCS) {
        String hql = " select distinct categoria from EnquadramentoFuncional enquadramento "
            + " inner join enquadramento.categoriaPCS categoria "
            + " where categoria = :parametroCategoria "
            + " and :dataVigencia >= enquadramento.inicioVigencia and :dataVigencia <= coalesce(enquadramento.finalVigencia,:dataVigencia)";
        Query q = em.createQuery(hql);
        q.setParameter("parametroCategoria", categoriaPCS);
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(new Date()));
        q.setMaxResults(1);

        if (q.getResultList().isEmpty()) {
            return null;
        }

        return (CategoriaPCS) q.getSingleResult();
    }

    public List<CategoriaPCS> listaFiltrandoDescricaoComPCSVigente(PlanoCargosSalarios planoCargosSalarios, String s, Date dataVigencia) {
        Query q = em.createQuery(" select c from CategoriaPCS c"
            + " inner join c.planoCargosSalarios plano "
            + " left join c.superior superior "
            + " where plano = :pcs "
            + " and :dataVigencia >= plano.inicioVigencia "
            + " and :dataVigencia <= coalesce(plano.finalVigencia,:dataVigencia)"
            + " and (lower(c.descricao) like :filtro"
            + " or lower(superior.descricao) like :filtro) ");
        q.setParameter("filtro", "%" + s.trim().toLowerCase() + "%");
        q.setParameter("pcs", planoCargosSalarios);
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(dataVigencia));
        return q.getResultList();
    }

    public List<CategoriaPCS> listaFiltrandoVigente(String s) {
        Query q = em.createQuery(" select c from CategoriaPCS c join c.planoCargosSalarios p"
            + " where :dataVigencia between p.inicioVigencia and coalesce(p.finalVigencia,:dataVigencia) and (lower(c.descricao) like :filtro) and c.superior is not null");
        q.setParameter("filtro", "%" + s.trim().toLowerCase() + "%");
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(UtilRH.getDataOperacao()));
        q.setMaxResults(50);
        return q.getResultList();
    }

    public List<CategoriaPCS> recuperaCategoriasNoEnquadramentoFuncionalVigente(PlanoCargosSalarios pcs, Date dataVigencia) {
        String hql = " select distinct categoria from EnquadramentoPCS enquadramento "
            + " inner join enquadramento.categoriaPCS categoria "
            + " where categoria.planoCargosSalarios = :pcs "
            + " and :dataVigencia between enquadramento.inicioVigencia and coalesce(enquadramento.finalVigencia,:dataVigencia) "
            + " order by categoria.descricao";//
        Query q = em.createQuery(hql);                //
        q.setParameter("pcs", pcs);
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(dataVigencia));

        return q.getResultList();
    }

    public List<CategoriaPCS> recuperaCategoriasPeloPCS(PlanoCargosSalarios pcs) {
        String hql = " select distinct categoria from CategoriaPCS categoria "
            + " where categoria.planoCargosSalarios = :pcs "
            + " order by categoria.descricao";
        Query q = em.createQuery(hql);
        q.setParameter("pcs", pcs);
        return q.getResultList();
    }

    public List<CategoriaPCS> buscarCategoriasNoEnquadramentoFuncionalPorCargoAndDataVigencia(Cargo cargo, Date dataVigencia) {
        String hql = " select distinct categoria from CategoriaPCS cat inner join cat.superior categoria inner"
            + " join cat.cargosCategoriaPCS cargos" +
            "  where cargos.cargo = :cargo  "
            + " and :dataVigencia between cargos.inicioVigencia and coalesce(cargos.finalVigencia,:dataVigencia) "
            + " order by categoria.descricao";
        Query q = em.createQuery(hql);
        q.setParameter("cargo", cargo);
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(dataVigencia));

        return q.getResultList();
    }

    public List<PlanoCargosSalarios> buscarPlanoCargosSalariosNoEnquadramentoFuncionalPorCargoAndDataVigencia(Cargo cargo, Date dataVigencia) {
        String hql = " select distinct planos from CategoriaPCS cat inner join cat.superior categoria inner"
            + " join cat.cargosCategoriaPCS cargos "
            + " join cat.planoCargosSalarios planos "
            + "  where cargos.cargo = :cargo  "
            + " and :dataVigencia between cargos.inicioVigencia and coalesce(cargos.finalVigencia,:dataVigencia) "
            + " and :dataVigencia between planos.inicioVigencia and coalesce(planos.finalVigencia,:dataVigencia) "
            + " order by planos.descricao";
        Query q = em.createQuery(hql);
        q.setParameter("cargo", cargo);
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(dataVigencia));

        return q.getResultList();
    }

    public List<CategoriaPCS> listaFiltrandoVigenteSuperior(String s) {
        Query q = em.createQuery(" select c from CategoriaPCS c join c.planoCargosSalarios p"
            + " where :dataVigencia between p.inicioVigencia and coalesce(p.finalVigencia,:dataVigencia) and (lower(c.descricao) like :filtro) and c.superior is null");
        q.setParameter("filtro", "%" + s.trim().toLowerCase() + "%");
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(UtilRH.getDataOperacao()));
        q.setMaxResults(50);
        return q.getResultList();
    }

    public MesesProgressaoCategoria buscarMesesParaProgressaoVigente(CategoriaPCS pcs, Date dataReferencia) {
        String sql = " select m.* from MesesProgressaoCategoria m " +
            " inner join CategoriaPcs pcs on pcs.id = m.categoriapcs_id " +
            " where pcs.id = :pcs_id" +
            " and :data_referencia between m.inicioVigencia and coalesce(m.finalVigencia, :data_referencia)";
        Query q = em.createNativeQuery(sql, MesesProgressaoCategoria.class);
        q.setParameter("pcs_id", pcs.getId());
        q.setParameter("data_referencia", dataReferencia);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (MesesProgressaoCategoria) q.getResultList().get(0);
    }
}
