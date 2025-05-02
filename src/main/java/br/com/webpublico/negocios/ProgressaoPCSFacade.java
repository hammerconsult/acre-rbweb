/*
 * Codigo gerado automaticamente em Sat Jul 02 09:46:00 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CategoriaPCS;
import br.com.webpublico.entidades.PlanoCargosSalarios;
import br.com.webpublico.entidades.ProgressaoPCS;
import br.com.webpublico.entidades.rh.pccr.MesesProgressao;
import br.com.webpublico.entidades.rh.pccr.MesesProgressaoCategoria;
import br.com.webpublico.entidades.rh.pccr.MesesProgressaoProgressaoPCS;
import br.com.webpublico.util.Util;
import org.hibernate.Hibernate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class ProgressaoPCSFacade extends AbstractFacade<ProgressaoPCS> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ProgressaoPCSFacade() {
        super(ProgressaoPCS.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<ProgressaoPCS> getRaiz() {
        Query q = em.createQuery("from ProgressaoPCS p where p.superior is null");

        try {
            return q.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public List<ProgressaoPCS> getFilhosDe(ProgressaoPCS pp, PlanoCargosSalarios p) {
        Query q = em.createQuery("from ProgressaoPCS p where p.superior = :superior and p.planoCargosSalarios = :parametro order by p.descricao asc");
        q.setParameter("superior", pp);
        q.setParameter("parametro", p);
        return q.getResultList();
    }

    @Override
    public ProgressaoPCS recuperar(Object id) {
        ProgressaoPCS p = em.find(ProgressaoPCS.class, id);
        Hibernate.initialize(p.getFilhos());
        Hibernate.initialize(p.getMesesProgressao());
        return p;
    }

    public List<ProgressaoPCS> listaProgressoesPCSs(PlanoCargosSalarios p) {
        String hql = "from ProgressaoPCS p ";
        hql += " where p.id not in (select po.superior as ids from ProgressaoPCS po where po.superior is not null) and p.planoCargosSalarios = :superior";
        Query q = em.createQuery(hql);
        q.setParameter("superior", p);
        return q.getResultList();
    }

    public List<ProgressaoPCS> getFolhas(PlanoCargosSalarios plano) {
        Query q = em.createQuery("from ProgressaoPCS c where c.filhos is empty and c.planoCargosSalarios = :plano");
        q.setParameter("plano", plano);

        return q.getResultList();
    }

    public List<ProgressaoPCS> getRaizPorPlano(PlanoCargosSalarios p) {
        Query q = em.createQuery("from ProgressaoPCS g where g.superior is null and g.planoCargosSalarios = :p");
        try {
            q.setParameter("p", p);
            return q.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public List<ProgressaoPCS> getRaizPorPlanoECategoria(PlanoCargosSalarios p, CategoriaPCS categoria) {
        Query q = em.createQuery("select distinct g.superior from ProgressaoPCS g, EnquadramentoPCS enquadramento, CategoriaPCS categoria "
            + " where g.planoCargosSalarios = :p and enquadramento.categoriaPCS = :cat and enquadramento.progressaoPCS = g ");

        try {
            q.setParameter("p", p);
            q.setParameter("cat", categoria);
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<ProgressaoPCS> buscarProgressaoPorPlanoECategoria(Long id, PlanoCargosSalarios plano, CategoriaPCS categoria) {
        String hql = "select distinct p from ProgressaoPCS p " +
            "            where p.planoCargosSalarios = :plano and p.categoriaPCS = :categoria";
        if (id != null) {
            hql += " and p.id <> :id";
        }
        Query q = em.createQuery(hql);
        try {
            q.setParameter("plano", plano);
            q.setParameter("categoria", categoria);
            if (id != null) {
                q.setParameter("id", id);
            }
            return q.getResultList();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public List<ProgressaoPCS> getRaizPorPlanoECategoriaVigenteNoEnquadramento(PlanoCargosSalarios p, CategoriaPCS categoria, Date dataVigencia) {
        Query q = em.createQuery("select distinct g.superior from ProgressaoPCS g, EnquadramentoPCS enquadramento, CategoriaPCS categoria "
            + " where g.planoCargosSalarios = :p "
            + " and enquadramento.categoriaPCS = :cat "
            + " and enquadramento.progressaoPCS = g "
            + " and :dataVigencia between enquadramento.inicioVigencia "
            + " and coalesce(enquadramento.finalVigencia, :dataVigencia) ");

        try {
            q.setParameter("p", p);
            q.setParameter("cat", categoria);
            q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(dataVigencia));
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<ProgressaoPCS> getFilhosPorPlanoECategoria(PlanoCargosSalarios p, CategoriaPCS categoria) {
        Query q = em.createQuery("select distinct g from ProgressaoPCS g, EnquadramentoPCS enquadramento, CategoriaPCS categoria "
            + " where g.planoCargosSalarios = :p and enquadramento.categoriaPCS = :cat and enquadramento.progressaoPCS = g ");

        try {
            q.setParameter("p", p);
            q.setParameter("cat", categoria);
            return q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean verificaProgressoesVigente(ProgressaoPCS progressao) {
        progressao = recuperar(progressao.getId());
        int contador = 0;
        for (ProgressaoPCS filho : progressao.getFilhos()) {
            String hql = " select count(enquadramento) from EnquadramentoPCS enquadramento inner join enquadramento.progressaoPCS p "
                + " where p = :filho "
                + " and :dataVigencia >= enquadramento.inicioVigencia "
                + " and :dataVigencia <= coalesce(enquadramento.finalVigencia,:dataVigencia) ";
            Query q = em.createQuery(hql);                //

            q.setParameter("filho", filho);
            q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(new Date()));
            contador += (Integer.parseInt(q.getSingleResult() + ""));
        }

        return contador > 0;

    }

    public List<ProgressaoPCS> listaPorPlano(PlanoCargosSalarios p) {
        String hql = "from ProgressaoPCS p where p.planoCargosSalarios = :p";
        Query q = em.createQuery(hql);
        q.setParameter("p", p);
        return q.getResultList();
    }

    public List<ProgressaoPCS> listaFiltrandoPlanoCargoSalario(PlanoCargosSalarios planoCargosSalarios, Date dataVigencia) {

        String hql = " from ProgressaoPCS a";


        if (planoCargosSalarios.getId() != null) {
            hql += " where a.planoCargosSalarios = :plano";
        } else {
            return new ArrayList<>();
        }

        hql += " order by a.descricao ";

        Query q = getEntityManager().createQuery(hql);

        if (planoCargosSalarios != null) {
            q.setParameter("plano", planoCargosSalarios);
        }

        return q.getResultList();
    }

    public ProgressaoPCS getRaiz(PlanoCargosSalarios p) {
        ProgressaoPCS pro = null;
        Query q = em.createQuery("from ProgressaoPCS p where p.superior is null and p.planoCargosSalarios = :parametro");
        q.setParameter("parametro", p);
        if (q.getResultList().isEmpty()) {
        } else {
            pro = (ProgressaoPCS) q.getSingleResult();
        }
        return pro;
    }

    public List<ProgressaoPCS> getProgressaoPorPlanosX(PlanoCargosSalarios pcs) {
        List<ProgressaoPCS> retorno = null;
        if (pcs != null) {
            Query q = em.createQuery("from ProgressaoPCS obj where obj.planoCargosSalarios = :pcss order by obj.id");
            q.setParameter("pcss", pcs);
            retorno = q.getResultList();
        } else {
            Query q = em.createQuery("from ProgressaoPCS obj order by obj.id");
            retorno = q.getResultList();
        }
        return retorno;
    }

    public List<ProgressaoPCS> getRaizFiltrandoPCS(PlanoCargosSalarios pcs) {
        Query q = em.createQuery("from ProgressaoPCS p where p.superior is null and p.planoCargosSalarios = :parametro ");
        q.setParameter("parametro", pcs);
        try {
            return q.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public List<ProgressaoPCS> listaFiltrandoDescricaoPCS(String s, PlanoCargosSalarios pcs) {
        Query q = em.createQuery("from ProgressaoPCS p where p.planoCargosSalarios = :pcs "
            + " and lower(p.descricao) like :parametro ");
        q.setParameter("pcs", pcs);
        q.setParameter("parametro", "%" + s.toLowerCase() + "%");
        return q.getResultList();
    }

    public List<ProgressaoPCS> buscaProgressoesNoEnquadramentoPCS(PlanoCargosSalarios pcs) {
        String hql = " select distinct progressao from EnquadramentoPCS enquadramento "
            + " inner join enquadramento.progressaoPCS progressao "
            + " where progressao.planoCargosSalarios = :pcs "
            + " and :dataVigencia >= enquadramento.inicioVigencia "
            + " and :dataVigencia <= coalesce(enquadramento.finalVigencia,:dataVigencia) "
            + " order by progressao.descricao ";//
        Query q = em.createQuery(hql);                //
        q.setParameter("pcs", pcs);
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(new Date()));
        return q.getResultList();
    }

    public ProgressaoPCS recuperaProgressaoNoEnquadramentoFuncionalVigente(ProgressaoPCS progressaoPCS) {
        String hql = " select distinct progressao from EnquadramentoFuncional enquadramento "
            + " inner join enquadramento.progressaoPCS progressao "
            + " where progressao = :parametroProgressao "
            + " and :dataVigencia >= enquadramento.inicioVigencia and :dataVigencia <= coalesce(enquadramento.finalVigencia,:dataVigencia)";
        Query q = em.createQuery(hql);
        q.setParameter("parametroProgressao", progressaoPCS);
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(new Date()));
        q.setMaxResults(1);

        if (q.getResultList().isEmpty()) {
            return null;
        }

        return (ProgressaoPCS) q.getSingleResult();
    }

    public List<ProgressaoPCS> listaUltimoNivelProgressaoPCS(PlanoCargosSalarios planoCargosSalarios) {
        List<ProgressaoPCS> toReturn = new ArrayList<>();
        for (ProgressaoPCS p : buscaProgressoesNoEnquadramentoPCS(planoCargosSalarios)) {
            if (p.getFilhos().isEmpty()) {
                toReturn.add(p);
            }
        }
        return toReturn;
    }

    public List<ProgressaoPCS> listaFiltrandoPCSVigente(PlanoCargosSalarios planoCargosSalarios, Date dataVigencia) {
        Query q = em.createQuery(" select progressao from ProgressaoPCS progressao "
            + " inner join progressao.planoCargosSalarios plano "
            + " where plano = :pcs"
            + " and :dataVigencia >= plano.inicioVigencia "
            + " and :dataVigencia <= coalesce(plano.finalVigencia,:dataVigencia)"
            + " order by progressao.descricao ");
        q.setParameter("pcs", planoCargosSalarios);
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(dataVigencia));
        return q.getResultList();
    }

    public List<ProgressaoPCS> buscaProgressoesNoEnquadramentoPCS(CategoriaPCS categoriaPCS, Date dataVigencia) {
        String hql = " select distinct progressao from EnquadramentoPCS enquadramento "
            + " inner join enquadramento.categoriaPCS categoria "
            + " inner join enquadramento.progressaoPCS progressao "
            + " where categoria = :categoria "
            + " and :dataVigencia >= enquadramento.inicioVigencia "
            + " and :dataVigencia <= coalesce(enquadramento.finalVigencia,:dataVigencia) "
            + " order by progressao.descricao ";
        Query q = em.createQuery(hql);
        q.setParameter("categoria", categoriaPCS);
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(dataVigencia));
        return q.getResultList();
    }

    public List<ProgressaoPCS> buscaProgressoesNoEnquadramentoPCSPorCategoriaSuperior(CategoriaPCS categoriaPCS, Date dataVigencia) {
        String hql = " select distinct progressao from EnquadramentoPCS enquadramento "
            + " inner join enquadramento.categoriaPCS.superior categoria "
            + " inner join enquadramento.progressaoPCS progressao "
            + " where categoria = :categoria "
            + " and :dataVigencia >= enquadramento.inicioVigencia "
            + " and :dataVigencia <= coalesce(enquadramento.finalVigencia,:dataVigencia) "
            + " order by progressao.descricao ";
        Query q = em.createQuery(hql);
        q.setParameter("categoria", categoriaPCS);
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(dataVigencia));
        return q.getResultList();
    }

    public List<ProgressaoPCS> buscaProgressoesSuperioresEnquadramentoPCSPorCategoriaSuperior(CategoriaPCS categoriaPCS, Date dataVigencia) {
        String hql = " select distinct progressao from EnquadramentoPCS enquadramento "
            + " inner join enquadramento.categoriaPCS.superior categoria "
            + " inner join enquadramento.progressaoPCS.superior progressao "
            + " where categoria = :categoria "
            + " and :dataVigencia >= enquadramento.inicioVigencia "
            + " and :dataVigencia <= coalesce(enquadramento.finalVigencia,:dataVigencia) "
            + " order by progressao.descricao ";
        Query q = em.createQuery(hql);
        q.setParameter("categoria", categoriaPCS);
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(dataVigencia));
        return q.getResultList();
    }

    public List<ProgressaoPCS> buscaProgressoesNoEnquadramentoPCSSemVigencia(CategoriaPCS categoriaPCS) {
        String hql = " select distinct progressao from EnquadramentoPCS enquadramento "
            + " inner join enquadramento.categoriaPCS categoria "
            + " inner join enquadramento.progressaoPCS progressao "
            + " where categoria = :categoria "
            + " order by progressao.descricao ";
        Query q = em.createQuery(hql);
        q.setParameter("categoria", categoriaPCS);
        return q.getResultList();
    }

    public List<ProgressaoPCS> buscaProgressoesNoEnquadramentoPCS(String descricao, String migracaoChave, CategoriaPCS categoriaPCS, Date dataVigencia) {
        String hql = " select distinct progressao from EnquadramentoPCS enquadramento "
            + " inner join enquadramento.categoriaPCS categoria "
            + " inner join enquadramento.progressaoPCS progressao "
            + " where categoria = :categoria and progressao.descricao like :descricao"
            + " and :dataVigencia >= enquadramento.inicioVigencia "
            + " and :dataVigencia <= coalesce(enquadramento.finalVigencia,:dataVigencia) and progressao.migracaoChave like :chave "
            + " order by progressao.descricao ";
        Query q = em.createQuery(hql);
        q.setParameter("categoria", categoriaPCS);
        q.setParameter("descricao", "%" + descricao + "%");
        q.setParameter("chave", "%" + migracaoChave + "%");
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(dataVigencia));
        return q.getResultList();
    }

    public List<ProgressaoPCS> buscaProgressoesNoEnquadramentoPCS(ProgressaoPCS superior) {
        String hql = " select distinct progressao from EnquadramentoPCS enquadramento "
            + " inner join enquadramento.categoriaPCS categoria "
            + " inner join enquadramento.progressaoPCS progressao "
            + " where progressao.superior = :superior and progressao.filhos is empty"
            + " and :dataVigencia >= enquadramento.inicioVigencia "
            + " and :dataVigencia <= coalesce(enquadramento.finalVigencia,:dataVigencia) "
            + " order by progressao.ordem ";
        Query q = em.createQuery(hql);
        q.setParameter("superior", superior);
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(new Date()));
        return q.getResultList();
    }

    public List<ProgressaoPCS> buscarProgressoesNoEnquadramento(ProgressaoPCS progressao) {
        String hql = " select distinct progressao from EnquadramentoPCS enquadramento "
            + " inner join enquadramento.categoriaPCS categoria "
            + " inner join enquadramento.progressaoPCS progressao "
            + " where progressao = :progressao "
            + " order by progressao.ordem ";
        Query q = em.createQuery(hql);
        q.setParameter("progressao", progressao);
        return q.getResultList();
    }

    public ProgressaoPCS recuperaProgressaoEquivalente(ProgressaoPCS progressaoSuperior, ProgressaoPCS progressaoEquivalente) {
        try {
            String sql = "select * from progressaopcs where superior_id = :progressaoSuperior " +
                " and descricao = (select p.descricao from ProgressaoPCS p where p.id = :progressaoEquivalente) ";

            Query q = em.createNativeQuery(sql, ProgressaoPCS.class);
            q.setParameter("progressaoSuperior", progressaoSuperior.getId());
            q.setParameter("progressaoEquivalente", progressaoEquivalente.getId());
            return (ProgressaoPCS) q.getSingleResult();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public MesesProgressaoProgressaoPCS buscarMesesParaProgressaoVigente(ProgressaoPCS pcs, Date dataReferencia) {
        String sql = " select m.* from MesesProgressaoProgressaoPCS m " +
            " inner join ProgressaoPCS pcs on pcs.id = m.progressaopcs_id " +
            " where pcs.id = :pcs_id" +
            " and :data_referencia between m.inicioVigencia and coalesce(m.finalVigencia, :data_referencia)";
        Query q = em.createNativeQuery(sql, MesesProgressaoProgressaoPCS.class);
        q.setParameter("pcs_id", pcs.getId());
        q.setParameter("data_referencia", dataReferencia);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (MesesProgressaoProgressaoPCS) q.getResultList().get(0);
    }
}
