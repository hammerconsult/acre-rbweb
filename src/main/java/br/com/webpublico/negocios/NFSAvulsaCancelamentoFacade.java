/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.NFSAvulsa;
import br.com.webpublico.entidades.NFSAvulsaCancelamento;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.util.Util;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Leonardo
 */
@Stateless
public class NFSAvulsaCancelamentoFacade extends AbstractFacade<NFSAvulsaCancelamento> {


    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private NFSAvulsaFacade notaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    public NFSAvulsaCancelamentoFacade() {
        super(NFSAvulsaCancelamento.class);
    }

    public NFSAvulsaFacade getNotaFacade() {
        return notaFacade;
    }

    public boolean verificaExistenciaNota(NFSAvulsa nfsAvulsa) {
        String hql = "from NFSAvulsaCancelamento nfs where nfs.nfsAvulsa = :nota";
        Query q = em.createQuery(hql);
        q.setParameter("nota", nfsAvulsa);
        return !q.getResultList().isEmpty();
    }

    public List<NFSAvulsaCancelamento> listaDecrescente() {
        String hql = "from  NFSAvulsaCancelamento obj order by obj.data desc ";
        Query q = getEntityManager().createQuery(hql);
        return q.getResultList();
    }

    public List filtrar(int inicio, int max, NFSAvulsa nfsAvulsa, Pessoa filtroPrestador, String numeroInicial, String numeroFinal, Date dataCancelInicial, Date dataCancelFinal) {
        String hql = " select nota from NFSAvulsaCancelamento nota " +
                "  inner join nota.nfsAvulsa nfsAvulsa " +
                "  inner join nfsAvulsa.prestador prestador";
        String juncao = " where";

        if (nfsAvulsa != null) {
            hql += juncao += " nfsAvulsa = :nfsAvulsa";
            juncao = " and ";
        }

        if (filtroPrestador != null) {
            hql += juncao += " prestador = :prestador";
            juncao = " and ";
        }

        if (numeroInicial != null && !numeroInicial.isEmpty()) {
            hql += juncao += " to_char(nfsAvulsa.numero) >= :numeroInicial";
            juncao = " and ";
        }
        if (numeroFinal != null && !numeroFinal.isEmpty()) {
            hql += juncao += " to_char(nfsAvulsa.numero) <= :numeroFinal";
            juncao = " and ";
        }

        if (dataCancelInicial != null) {
            hql += juncao += " to_char(nota.data, 'dd/MM/yyyy') >= :emissaoInicial";
            juncao = " and ";
        }
        if (dataCancelFinal != null) {
            hql += juncao += " to_char(nota.data, 'dd/MM/yyyy') <= :emissaoFinal";
        }
        hql += " order by nota.data desc";
        Query q = em.createQuery(hql);
        q.setMaxResults(max + 10);
        q.setFirstResult(inicio);

        if (nfsAvulsa != null) {
            q.setParameter("nfsAvulsa", nfsAvulsa);
        }
        if (filtroPrestador != null) {
            q.setParameter("prestador", filtroPrestador);
        }

        if (numeroInicial != null && !numeroInicial.isEmpty()) {
            q.setParameter("numeroInicial", numeroInicial);

        }
        if (numeroFinal != null && !numeroFinal.isEmpty()) {
            q.setParameter("numeroFinal", numeroFinal);
        }

        if (dataCancelInicial != null) {
            SimpleDateFormat sp = new SimpleDateFormat("dd/MM/yyyy");
            q.setParameter("dataCancelInicial", sp.format(dataCancelInicial));
        }
        if (dataCancelInicial != null) {
            SimpleDateFormat sp = new SimpleDateFormat("dd/MM/yyyy");
            q.setParameter("dataCancelInicial", sp.format(dataCancelInicial));
        }
        //new Util().imprimeSQL(hql, q);
        return q.getResultList();
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }
}
