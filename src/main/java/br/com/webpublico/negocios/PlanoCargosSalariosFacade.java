/*
 * Codigo gerado automaticamente em Fri Jul 01 16:20:16 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CategoriaPCS;
import br.com.webpublico.entidades.MesesPromocao;
import br.com.webpublico.entidades.PlanoCargosSalarios;
import br.com.webpublico.entidades.ProgressaoPCS;
import br.com.webpublico.entidades.rh.pccr.MesesProgressao;
import br.com.webpublico.entidades.rh.pccr.MesesProgressaoCategoria;
import br.com.webpublico.entidades.rh.pccr.MesesProgressaoProgressaoPCS;
import br.com.webpublico.enums.TipoPCS;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Stateless
public class PlanoCargosSalariosFacade extends AbstractFacade<PlanoCargosSalarios> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private CategoriaPCSFacade categoriaPCSFacade;
    @EJB
    private ProgressaoPCSFacade progressaoPCSFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PlanoCargosSalariosFacade() {
        super(PlanoCargosSalarios.class);
    }

    @Override
    public PlanoCargosSalarios recuperar(Object id) {
        PlanoCargosSalarios p = em.find(PlanoCargosSalarios.class, id);
        p.getEntidadesPCS().size();
        p.getMesesProgressao().size();
        p.getMesesPromocao().size();
        return p;
    }

    public List<PlanoCargosSalarios> listaFiltrandoPorVigenciaAutoComplete(Date vigencia, String filtro) {
        List<PlanoCargosSalarios> listaPCS = new ArrayList<PlanoCargosSalarios>();

        String hql = " from PlanoCargosSalarios a "
            + " where :dataVigencia between a.inicioVigencia and coalesce(a.finalVigencia,:dataVigencia) and lower(a.descricao) like :filtro order by a.descricao";

        Query q = em.createQuery(hql);

        q.setParameter("dataVigencia", vigencia);
        q.setParameter("filtro", "%" + filtro.toLowerCase() + "%");

        listaPCS = q.getResultList();


        return listaPCS;
    }

    public List<PlanoCargosSalarios> listaFiltrandoVigencia(Date dataVigencia) {
        List<PlanoCargosSalarios> listaPCS = new ArrayList<PlanoCargosSalarios>();

        String hql = " from PlanoCargosSalarios a where :vigencia >= a.inicioVigencia and "
            + " :vigencia <= coalesce(a.finalVigencia,:vigencia) order by a.descricao ";

        Query q = em.createQuery(hql);
        q.setParameter("vigencia", Util.getDataHoraMinutoSegundoZerado(dataVigencia));
        listaPCS = q.getResultList();

        return listaPCS;
    }

    /**
     * Metodo que retorna somente os planos de cargos e salarios que sao do tipo
     * Cargo em Comissão ou Quadro efetivo.
     *
     * @param dataVigencia
     * @return lista de PlanoCargos e Salarios
     */
    public List<PlanoCargosSalarios> getPlanosPorQuadro(Date dataVigencia) {
        String hql = " from PlanoCargosSalarios a where :vigencia >= a.inicioVigencia and "
            + " :vigencia <= coalesce(a.finalVigencia,:vigencia) and a.tipoPCS <> :funcaoGratificada order by a.descricao";
        Calendar c = Calendar.getInstance();
        c.setTime(dataVigencia);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        Query q = em.createQuery(hql);
        q.setParameter("vigencia", c.getTime());
        q.setParameter("funcaoGratificada", TipoPCS.FUNCAO_GRATIFICADA);
        return q.getResultList();
    }


    public List<PlanoCargosSalarios> getPlanoEmFuncaoGratificadaVigente(TipoPCS tipoPCS) {
        Query q = em.createQuery("from PlanoCargosSalarios pcs " +
            " where pcs.tipoPCS = :tipo " +
            " and :dataOperacao between pcs.inicioVigencia and coalesce(pcs.finalVigencia, :dataOperacao) ");
        q.setParameter("tipo", tipoPCS);
        q.setParameter("dataOperacao", UtilRH.getDataOperacao());
        return q.getResultList();

    }

    public MesesProgressao buscarMesesParaProgressaoVigenteDoPlano(PlanoCargosSalarios pcs, Date dataReferencia) {
        String sql = " select m.* from mesesprogressao m " +
            " inner join planocargossalarios pcs on pcs.id = m.planocargossalarios_id" +
            " where pcs.id = :pcs_id" +
            " and :data_referencia between m.inicioVigencia and coalesce(m.finalVigencia, :data_referencia)";
        Query q = em.createNativeQuery(sql, MesesProgressao.class);
        q.setParameter("pcs_id", pcs.getId());
        q.setParameter("data_referencia", dataReferencia);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (MesesProgressao) q.getResultList().get(0);
    }

    public MesesPromocao buscarMesesParaPromocaoVigenteDoPlano(PlanoCargosSalarios pcs, Date dataReferencia) {
        String sql = " select m.* from mesespromocao m " +
            " inner join planocargossalarios pcs on pcs.id = m.planocargossalarios_id" +
            " where pcs.id = :pcs_id" +
            " and :data_referencia between m.inicioVigencia and coalesce(m.finalVigencia, :data_referencia)";
        Query q = em.createNativeQuery(sql, MesesPromocao.class);
        q.setParameter("pcs_id", pcs.getId());
        q.setParameter("data_referencia", dataReferencia);
        q.setMaxResults(1);
        try {
            return (MesesPromocao) q.getSingleResult();
        } catch (NoResultException nre) {
            throw new ExcecaoNegocioGenerica("Nenhuma configuração de meses para promoção encontrada para plano de cargos e salários. " + pcs);
        }
    }

    public Integer getMesesProgressaoPorPrioridade(PlanoCargosSalarios planoCargosSalarios, CategoriaPCS categoriaPCS, ProgressaoPCS progressaoPCS, Date dataOperacao) {

        MesesProgressao mesesProgressaoPlano = buscarMesesParaProgressaoVigenteDoPlano(planoCargosSalarios, dataOperacao);
        MesesProgressaoCategoria mesesProgressaoCategoria = categoriaPCSFacade.buscarMesesParaProgressaoVigente(categoriaPCS.getSuperior(), dataOperacao);
        MesesProgressaoProgressaoPCS mesesProgressaoProgressaoPCS = progressaoPCSFacade.buscarMesesParaProgressaoVigente(progressaoPCS.getSuperior(), dataOperacao);
        if (mesesProgressaoProgressaoPCS != null && mesesProgressaoProgressaoPCS.getMeses() > 0) {
            return mesesProgressaoProgressaoPCS.getMeses();
        }
        if (mesesProgressaoCategoria != null && mesesProgressaoCategoria.getMeses() > 0) {
            return mesesProgressaoCategoria.getMeses();
        }
        if (mesesProgressaoPlano != null && mesesProgressaoPlano.getMeses() > 0) {
            return mesesProgressaoPlano.getMeses();
        }

        return 0;
    }
}
