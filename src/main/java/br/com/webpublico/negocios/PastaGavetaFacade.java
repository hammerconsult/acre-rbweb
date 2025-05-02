/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.util.Util;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author andre
 */
@Stateless
public class PastaGavetaFacade extends AbstractFacade<PastaGaveta> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public PastaGavetaFacade() {
        super(PastaGaveta.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<PastaGaveta> listaPastasDisponiveisPorGaveta(GavetaFichario gavetaFichario) {
        Query q = em.createQuery("select pasta from PastaGaveta pasta "
                + " where pasta not in ("
                + " select pasta from PastaGavetaContratoFP pastaGavetaContratoFP "
                + " inner join pastaGavetaContratoFP.pastaGaveta pasta"
                + " inner join pasta.gavetaFichario gaveta "
                + " where gaveta = :parametroGaveta"
                + " and :data >= pastaGavetaContratoFP.inicioVigencia "
                + " and :data <= coalesce(pastaGavetaContratoFP.finalVigencia, :data))"
                + " and pasta.gavetaFichario = :parametroGaveta "
                + " order by pasta.numero ");
        q.setParameter("parametroGaveta", gavetaFichario);
        q.setParameter("data", Util.getDataHoraMinutoSegundoZerado(new Date()));

        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }

        return new ArrayList<PastaGaveta>();
    }

    public PastaGavetaContratoFP recuperapastaVigentePorContratoFP(ContratoFP contratoFP) {
        Query q = em.createQuery("select pasta from PastaGavetaContratoFP pasta"
                + " where pasta.contratoFP = :contrato"
                + " and pasta.finalVigencia is null "
                + " order by pasta.inicioVigencia desc ");
        q.setMaxResults(1);
        q.setParameter("contrato", contratoFP);

        if (!q.getResultList().isEmpty()) {
            return (PastaGavetaContratoFP) q.getSingleResult();
        }

        return null;
    }

    public List<PastaGaveta> listaFiltrandoPastasDisponiveisPorGaveta(String s, GavetaFichario gavetaFichario) {
        Query q = em.createQuery("select pasta from PastaGaveta pasta "
                + " where pasta not in ("
                + " select pasta from PastaGavetaContratoFP pastaGavetaContratoFP "
                + " inner join pastaGavetaContratoFP.pastaGaveta pasta"
                + " inner join pasta.gavetaFichario gaveta"
                + " where gaveta = :parametroGaveta"
                + " and pastaGavetaContratoFP.finalVigencia is null)"
                + " and pasta.gavetaFichario = :parametroGaveta "
                + " and lower(cast(pasta.numero as string)) like :filtro "
                + " order by pasta.numero ");
        q.setParameter("parametroGaveta", gavetaFichario);
        q.setParameter("filtro", "%" + s.toLowerCase().trim() + "%");

        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }

        return new ArrayList<PastaGaveta>();
    }

    public PastaGaveta recuperaPrimeiraPastaDisponivel() {
        Query q = em.createQuery("select pasta from PastaGaveta pasta "
                + " where pasta not in ("
                + " select pasta from PastaGavetaContratoFP pastaGavetaContratoFP "
                + " inner join pastaGavetaContratoFP.pastaGaveta pasta"
                + " inner join pasta.gavetaFichario gaveta "
                + " inner join gaveta.fichario "
                + " where pastaGavetaContratoFP.finalVigencia is null)"
                + " order by pasta.gavetaFichario, pasta.gavetaFichario.fichario, pasta.numero  ");
        q.setMaxResults(1);

        if (!q.getResultList().isEmpty()) {
            return (PastaGaveta) q.getSingleResult();
        }

        return null;
    }

    public PastaGavetaContratoFP recuperapastaVigentePorPessoaFisica(PessoaFisica pessoaFisica) {
        Query q = em.createQuery("select pasta from PastaGavetaContratoFP pasta "
                + " inner join pasta.contratoFP contrato"
                + " inner join contrato.matriculaFP matricula"
                + " inner join matricula.pessoa pf "
                + " where pf = :pf"
                + " and pasta.finalVigencia is null "
                + " order by pasta.inicioVigencia desc ");
        q.setMaxResults(1);
        q.setParameter("pf", pessoaFisica);

        if (!q.getResultList().isEmpty()) {
            return (PastaGavetaContratoFP) q.getSingleResult();
        }

        return null;
    }

    public void removerPastaGavetaContratoFP(ContratoFP c){
        String sql  = "delete from PASTAGAVETACONTRATOFP where contratofp_id = :id";
        Query q = em.createNativeQuery(sql);
        q.setParameter("id", c);
        q.executeUpdate();
    }
}
