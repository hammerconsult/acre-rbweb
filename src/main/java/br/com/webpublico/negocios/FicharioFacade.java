/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Fichario;
import br.com.webpublico.entidades.GavetaFichario;
import br.com.webpublico.entidades.PastaGaveta;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * @author andre
 */
@Stateless
public class FicharioFacade extends AbstractFacade<Fichario> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;

    public FicharioFacade() {
        super(Fichario.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public Fichario recuperar(Object id) {
        Fichario fichario = em.find(Fichario.class, id);
        fichario.getGavetasFicharios().size();
        if (fichario.getGavetasFicharios() != null && !fichario.getGavetasFicharios().isEmpty()) {
            for (GavetaFichario gavetaFichario : fichario.getGavetasFicharios()) {
                gavetaFichario.getPastasGavetas().size();
            }
        }
        return fichario;
    }

    public List<Fichario> listaFiltrandoCodigoDescricao(String s) {
        Query q = em.createQuery(" from Fichario f "
            + " where lower(cast(f.codigo as string)) like :parametro"
            + " or lower(f.descricao) like :parametro ");
        q.setParameter("parametro", "%" + s.toLowerCase().trim() + "%");

        return q.getResultList();
    }

    public boolean hasPastaGavetaEmUso(GavetaFichario gavetaFichario) {
        String sql = "select pastagavetafp.* from PastaGavetaContratoFP pastagavetafp " +
            "        inner join pastagaveta pasta on pastagavetafp.PASTAGAVETA_ID = pasta.id " +
            "        inner join GAVETAFICHARIO gaveta on pasta.GAVETAFICHARIO_ID = gaveta.id " +
            "        where gaveta.id = :idGavetaFichario " +
            "        and :dataOperacao between pastagavetafp.INICIOVIGENCIA and coalesce(pastagavetafp.FINALVIGENCIA, :dataOperacao)";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataOperacao", sistemaFacade.getDataOperacao());
        q.setParameter("idGavetaFichario", gavetaFichario.getId());
        return q.getResultList().isEmpty();
    }

    public PastaGaveta buscarUltimaGavetaEmUso(GavetaFichario gavetaFichario) {
        String sql = "select pasta.* from PastaGavetaContratoFP pastagavetafp " +
            "        inner join pastagaveta pasta on pastagavetafp.PASTAGAVETA_ID = pasta.id " +
            "        inner join GAVETAFICHARIO gaveta on pasta.GAVETAFICHARIO_ID = gaveta.id " +
            "        where gaveta.id = :idGavetaFichario " +
            "        and :dataOperacao between pastagavetafp.INICIOVIGENCIA and coalesce(pastagavetafp.FINALVIGENCIA, :dataOperacao)" +
            "        order by pasta.numero desc ";
        Query q = em.createNativeQuery(sql, PastaGaveta.class);
        q.setParameter("dataOperacao", sistemaFacade.getDataOperacao());
        q.setParameter("idGavetaFichario", gavetaFichario.getId());
        try {
            if (!q.getResultList().isEmpty()) {
                return (PastaGaveta) q.getResultList().get(0);
            }
            return null;
        } catch (Exception ex) {
            return null;
        }
    }


}
