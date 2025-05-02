/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Menu;
import br.com.webpublico.seguranca.menu.LeitorMenuFacade;
import br.com.webpublico.util.Util;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * @author Felipe Marinzeck
 */
@Stateless
public class MenuFacade extends AbstractFacade<Menu> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public MenuFacade() {
        super(Menu.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    private void limparSingletonMenu(){
        LeitorMenuFacade leitorMenuFacade = (LeitorMenuFacade) Util.getSpringBeanPeloNome("leitorMenuFacade");
        leitorMenuFacade.limparTodosMenus();
    }

    @Override
    public void salvar(Menu entity) {
        super.salvar(entity);
        limparSingletonMenu();
    }

    @Override
    public void remover(Menu entity) {
        super.remover(entity);
        limparSingletonMenu();
    }

    public List<Menu> getItensParaContruirMenu() {
        String hql = "select m from Menu m where m.pai is null order by m.ordem asc";
        Query q = getEntityManager().createQuery(hql);
        return q.getResultList();
    }

    public boolean isLabelRepetido(Menu menu) {
        String hql = "from Menu m where m.label = :label";
        if (menu.getId() != null) {
            hql += " and m.id != :id";
        }
        Query q = em.createQuery(hql);
        q.setParameter("label", menu.getLabel());
        if (menu.getId() != null) {
            q.setParameter("id", menu.getId());
        }

        q.setMaxResults(1);

        try {
            return q.getSingleResult() != null;
        } catch (NoResultException nre) {
            return false;
        }
    }

    public Menu recuperarPeloCaminho(String caminho) {
        String hql = "select m from Menu m where m.caminho = :caminho";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("caminho", caminho);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (Menu) q.getResultList().get(0);
    }

    public Menu salvarRetornando(Menu menuSelecionado) {
        Menu menu = em.merge(menuSelecionado);
        limparSingletonMenu();
        return menu;
    }
}
