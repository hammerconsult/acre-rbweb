/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CadastroImobiliario;
import br.com.webpublico.entidades.Construcao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Munif
 */
@Stateless
public class ConstrucaoFacade extends AbstractFacade<Construcao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConstrucaoFacade() {
        super(Construcao.class);
    }

    public List<Construcao> listaFiltrandoPorCadastroImobiliario(CadastroImobiliario cadastro) {
        Query q = em.createQuery("FROM Construcao construcao WHERE construcao.imovel = :cadastroImobiliario");
        q.setParameter("cadastroImobiliario", cadastro);
        List<Construcao> result = q.getResultList();
        if (result == null) {
            return new ArrayList<Construcao>();
        } else {
            return result;
        }
    }

    public List<Construcao> listarPorCadastroImobiliario(CadastroImobiliario cadastroImobiliario, String parte) {
        Query q = em.createQuery("FROM Construcao construcao WHERE construcao.imovel = :cadastroImobiliario AND construcao.descricao LIKE :parte");
        q.setParameter("cadastroImobiliario", cadastroImobiliario);
        q.setParameter("parte", "%" + parte + "%");
        List<Construcao> result = q.getResultList();
        if (result == null) {
            return new ArrayList<Construcao>();
        } else {
            return result;
        }
    }
}
