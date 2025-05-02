/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.ModuloTipoDoctoOficial;
import br.com.webpublico.enums.TipoCadastroDoctoOficial;
import br.com.webpublico.enums.TipoCadastroTributario;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author Wellington
 */
@Stateless
public class ParametrosDividaAtivaFacade extends AbstractFacade<ParametrosDividaAtiva> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;


    public ParametrosDividaAtivaFacade() {
        super(ParametrosDividaAtiva.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public AtoLegalFacade getAtoLegalFacade() {
        return atoLegalFacade;
    }


    public ParametrosDividaAtiva parametrosDividaAtivaExercicioCorrente() {
        Calendar calendar = Calendar.getInstance();
        Exercicio exercicio = exercicioFacade.getExercicioPorAno(calendar.get(Calendar.YEAR));
        return parametrosDividaAtivaPorExercicio(exercicio);
    }

    public ParametrosDividaAtiva parametrosDividaAtivaPorExercicio(Exercicio exercicio) {

        String hql = "from ParametrosDividaAtiva where exercicio = :exercicio";
        Query q = em.createQuery(hql);
        q.setParameter("exercicio", exercicio);
        q.setMaxResults(1);
        List<ParametrosDividaAtiva> listaReturn = q.getResultList();
        if (!listaReturn.isEmpty()) {
            ParametrosDividaAtiva parametrosDividaAtiva = listaReturn.get(0);
            parametrosDividaAtiva.getDividasEnvioCDA().size();
            return parametrosDividaAtiva;
        }
        return null;
    }

    public TipoDoctoOficial tipoDoctoOficialPorTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario, Exercicio exercicio) {
        Query consulta = em.createQuery(" select tipo from TipoDoctoOficial tipo where tipo.moduloTipoDoctoOficial = 'TERMODIVIDAATIVA'"
            + " and tipo.tipoCadastroDoctoOficial = :tipo");
        consulta.setParameter("tipo", recuperaTipoCadastroDoctoOficial(tipoCadastroTributario));
        try {
            return (TipoDoctoOficial) consulta.getResultList().get(0);
        } catch (Exception e) {
            return new TipoDoctoOficial();
        }

    }

    public List<TipoDoctoOficial> completaTipoDoctoOficialPorTipo(String parte, TipoCadastroDoctoOficial tipo, ModuloTipoDoctoOficial moduloTipoDoctoOficial) {
        String hql = " select tipo from TipoDoctoOficial tipo where tipo.moduloTipoDoctoOficial = :moduloTipoDoctoOficial"
            + " and (lower(tipo.descricao) like :parte or to_char(tipo.codigo) like :parte)"
            + " and tipo.tipoCadastroDoctoOficial = :tipo";
        Query consulta = em.createQuery(hql);
        consulta.setParameter("parte", "%" + parte.toLowerCase() + "%");
        consulta.setParameter("tipo", tipo);
        consulta.setParameter("moduloTipoDoctoOficial", moduloTipoDoctoOficial);
        try {
            return (List<TipoDoctoOficial>) consulta.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<TipoDoctoOficial>();
        }
    }

    private TipoCadastroDoctoOficial recuperaTipoCadastroDoctoOficial(TipoCadastroTributario tipoCadastroTributario) {
        if (tipoCadastroTributario == null) {
            return null;
        }
        if (tipoCadastroTributario.equals(TipoCadastroTributario.ECONOMICO)) {
            return TipoCadastroDoctoOficial.CADASTROECONOMICO;
        }
        if (tipoCadastroTributario.equals(TipoCadastroTributario.IMOBILIARIO)) {
            return TipoCadastroDoctoOficial.CADASTROIMOBILIARIO;
        }
        if (tipoCadastroTributario.equals(TipoCadastroTributario.RURAL)) {
            return TipoCadastroDoctoOficial.CADASTRORURAL;
        }
        return null;
    }

    public List<ParametrosDividaAtiva> listaFiltrandoX() {
        Query q = this.em.createQuery("select p from ParametrosDividaAtiva p order by p.exercicio.ano desc");
        return q.getResultList();
    }

    @Override
    public ParametrosDividaAtiva recuperar(Object id) {
        ParametrosDividaAtiva param = super.recuperar(id);
        param.getDividasEnvioCDA().size();
        param.getParametroDividaAtivaLeisDivida().size();
        for (AgrupadorCDA agrupadorCDA : param.getAgrupadoresCDA()) {
            agrupadorCDA.getDividas().size();
        }
        return param;
    }

    public AgrupadorCDA buscarAgrupadoresDaDivida(Divida divida) {
        String sql = "select agrupador from AgrupadorCDA agrupador join agrupador.dividas divida " +
            " where divida.divida = :divida";
        Query q = em.createQuery(sql);
        q.setParameter("divida", divida);
        q.setMaxResults(1);
        if(!q.getResultList().isEmpty()) {
            AgrupadorCDA agrupador = (AgrupadorCDA) q.getResultList().get(0);
            agrupador.getDividas().size();
            return agrupador;
        }
        return null;
    }
}
