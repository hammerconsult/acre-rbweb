/*
 * Codigo gerado automaticamente em Thu Oct 27 13:45:25 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ManutencaoObjetoFrota;
import br.com.webpublico.entidades.ObjetoFrota;
import br.com.webpublico.entidades.PecaInstalada;
import br.com.webpublico.entidades.RevisaoObjetoFrota;
import br.com.webpublico.negocios.administrativo.frotas.ParametroFrotasFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class ManutencaoObjetoFrotaFacade extends AbstractFacade<ManutencaoObjetoFrota> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private PecaObjetoFrotaFacade pecaObjetoFrotaFacade;
    @EJB
    private ParametroFrotasFacade parametroFrotasFacade;
    @EJB
    private MaterialFacade materialFacade;
    @EJB
    private ContratoFacade contratoFacade;


    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ManutencaoObjetoFrotaFacade() {
        super(ManutencaoObjetoFrota.class);
    }

    @Override
    public ManutencaoObjetoFrota recuperar(Object id) {
        ManutencaoObjetoFrota manutencaoVeiculo = (ManutencaoObjetoFrota) super.recuperar(id);
        manutencaoVeiculo.getPecaInstalada().size();
        if(manutencaoVeiculo.getDetentorArquivoComposicao() != null) {
            manutencaoVeiculo.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        return manutencaoVeiculo;
    }

    public RevisaoObjetoFrota manutencaoRelacionadaEmRevisao(ManutencaoObjetoFrota manutencaoVeiculo) {
        String hql = "from " + RevisaoObjetoFrota.class.getSimpleName() + " where manutencaoObjetoFrota = :manutencao";
        Query query = em.createQuery(hql);
        query.setParameter("manutencao", manutencaoVeiculo);
        query.setMaxResults(1);
        try {
            return (RevisaoObjetoFrota) query.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public List<PecaInstalada> recuperaPecas(ManutencaoObjetoFrota manutencaoVeiculo) {
        String hql = "select man.pecaInstalada from " + ManutencaoObjetoFrota.class.getSimpleName() + " man where man = :manutencao";
        Query q = em.createQuery(hql);
        q.setParameter("manutencao", manutencaoVeiculo);
        try {
            return q.getResultList();
        } catch (NoResultException nre) {
            return new ArrayList<PecaInstalada>();
        }
    }

    public List<ManutencaoObjetoFrota> listarManutencao(ObjetoFrota objetoFrota, String parte) {
        if(objetoFrota == null || objetoFrota.getId() == null){
            return new ArrayList();
        }
        String sql = " select * from manutencaoobjetofrota manutencao " +
                " where manutencao.objetofrota_id = " + objetoFrota.getId() +
                "   and to_char(manutencao.id) like '%"+parte.trim()+"%' ";
        Query q = em.createNativeQuery(sql, ManutencaoObjetoFrota.class);
        return q.getResultList();
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public PecaObjetoFrotaFacade getPecaObjetoFrotaFacade() {
        return pecaObjetoFrotaFacade;
    }

    public ParametroFrotasFacade getParametroFrotasFacade() {
        return parametroFrotasFacade;
    }

    public MaterialFacade getMaterialFacade() {
        return materialFacade;
    }

    public ContratoFacade getContratoFacade() {
        return contratoFacade;
    }
}
