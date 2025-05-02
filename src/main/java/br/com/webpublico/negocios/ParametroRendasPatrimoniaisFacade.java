package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.LotacaoVistoriadora;
import br.com.webpublico.entidades.ParametroRendas;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.util.Util;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

/**
 * @author daniel
 */
@Stateless
public class ParametroRendasPatrimoniaisFacade extends AbstractFacade<ParametroRendas> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private IndiceEconomicoFacade indiceEconomicoFacade;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private LotacaoVistoriadoraFacade lotacaoVistoriadoraFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;

    public ParametroRendasPatrimoniaisFacade() {
        super(ParametroRendas.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public IndiceEconomicoFacade getIndiceEconomicoFacade() {
        return indiceEconomicoFacade;
    }

    public DividaFacade getDividaFacade() {
        return dividaFacade;
    }

    public LotacaoVistoriadoraFacade getLotacaoVistoriadoraFacade() {
        return lotacaoVistoriadoraFacade;
    }

    public ConfiguracaoTributarioFacade getConfiguracaoTributarioFacade() {
        return configuracaoTributarioFacade;
    }

    public boolean jaExiste(ParametroRendas selecionado) {
        if (selecionado == null || selecionado.getExercicio() == null || selecionado.getExercicio().getId() == null) {
            return false;
        }

        if (selecionado == null || selecionado.getLotacaoVistoriadora() == null || selecionado.getLotacaoVistoriadora().getId() == null) {
            return false;
        }

        String hql = "from ParametroRendas pr where pr.exercicio = :exercicio and pr.lotacaoVistoriadora = :lotacao";
        if (selecionado.getId() != null) {
            hql += " and pr <> :parametro";
        }
        Query q = em.createQuery(hql);
        q.setParameter("exercicio", selecionado.getExercicio());
        q.setParameter("lotacao", selecionado.getLotacaoVistoriadora());
        if (selecionado.getId() != null) {
            q.setParameter("parametro", selecionado);
        }

        return !q.getResultList().isEmpty();
    }

    @Override
    public ParametroRendas recuperar(Object id) {
        ParametroRendas retorno = em.find(ParametroRendas.class, id);
        retorno.getListaServicoRateioCeasa().size();
        return retorno;
    }

    public ParametroRendas recuperaParametroRendasPorExercicioLotacaoVistoriadora(Exercicio exercicio, LotacaoVistoriadora lotacaoVistoriadora) {
        Query q = em.createQuery(" from ParametroRendas p "
            + " where p.exercicio = :exercicio and p.lotacaoVistoriadora = :lotacao ");
        q.setParameter("exercicio", exercicio);
        q.setParameter("lotacao", lotacaoVistoriadora);
        if (!q.getResultList().isEmpty()) {
            for (ParametroRendas p : (List<ParametroRendas>) q.getResultList()) {
                p.getListaServicoRateioCeasa().size();
            }
            return (ParametroRendas) q.getResultList().get(0);
        }
        return null;
    }

    public List<ParametroRendas> buscarParametroRendasPorExercicio(Exercicio exercicio) {
        Query q = em.createNativeQuery("select * from ParametroRendas where exercicio_id = :idExercicio", ParametroRendas.class);
        q.setParameter("idExercicio", exercicio.getId());
        return q.getResultList();
    }

    public List<ParametroRendas> listaParametroRendasPorLotacaoDoUsuario(UsuarioSistema usuarioSistema) {
        Query q = em.createQuery(" select p from ParametroRendas p where p.lotacaoVistoriadora in "
            + " (select lotacao.lotacao from VigenciaTribUsuario vigenciaTribUsuario "
            + " inner join vigenciaTribUsuario.usuarioSistema usuarioSistema "
            + " inner join vigenciaTribUsuario.lotacaoTribUsuarios lotacao "
            + " where usuarioSistema = :usuario "
            + " and :dataVigencia >= vigenciaTribUsuario.vigenciaInicial and "
            + " :dataVigencia <= coalesce(vigenciaTribUsuario.vigenciaFinal,:dataVigencia))");
        q.setParameter("usuario", usuarioSistema);
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(new Date()));
        return q.getResultList();
    }
}
