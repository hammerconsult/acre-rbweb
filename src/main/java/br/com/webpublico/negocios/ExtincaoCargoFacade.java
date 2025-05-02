/*
 * Codigo gerado automaticamente em Mon Sep 05 15:28:59 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
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
public class ExtincaoCargoFacade extends AbstractFacade<ExtincaoCargo> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;


    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ExtincaoCargoFacade() {
        super(ExtincaoCargo.class);
    }

    @Override
    public void salvar(ExtincaoCargo entity) {
        finalizarCargos(entity);
        super.salvar(entity);
    }

    @Override
    public void salvarNovo(ExtincaoCargo entity) {
        finalizarCargos(entity);
        super.salvarNovo(entity);
    }

    public void finalizarCargos(ExtincaoCargo entity) {
        for (ItemExtincaoCargo iec : entity.getItensExtincaoCargo()) {
            iec.setCargo(em.merge(iec.getCargo()));
        }
    }

    @Override
    public ExtincaoCargo recuperar(Object id) {
        ExtincaoCargo retorno = super.recuperar(id);
        retorno.getItensExtincaoCargo().size();
        return retorno;
    }

    public List<ExtincaoCargo> completaExtincaoCargo(String parte) {
        String hql = "select ec from ExtincaoCargo ec" +
                " inner join ec.atoLegal ato" +
                " where (to_char(ato.numero) like :parte or" +
                "        ato.nome like :parte or" +
                "        lower(ato.tipoAtoLegal) like :parte or" +
                "        to_char(ato.dataPromulgacao, 'dd/MM/yyyy') like :parte or" +
                "        to_char(ato.dataPublicacao, 'dd/MM/yyyy') like :parte or" +
                "        to_char(ato.dataEmissao, 'dd/MM/yyyy') like :parte) or" +
                "        to_char(ec.dataExtincao, 'dd/MM/yyyy') like :parte ";
        Query q = em.createQuery(hql);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        return q.getResultList();
    }

    public List<Cargo> recuperarCargosDaExtincao(ExtincaoCargo ec){
        String hql = "select c from ExtincaoCargo ec" +
                " inner join ec.itensExtincaoCargo iec" +
                " inner join iec.cargo c" +
                " where ec = :extincaoCargo";
        Query q = em.createQuery(hql);
        q.setParameter("extincaoCargo", ec);
        return q.getResultList();
    }
}
