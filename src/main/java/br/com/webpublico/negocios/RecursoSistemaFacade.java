/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.GrupoRecurso;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.Menu;
import br.com.webpublico.entidades.RecursoSistema;
import br.com.webpublico.enums.ModuloSistema;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.util.List;

/**
 * @author Munif
 */
@Stateless
public class RecursoSistemaFacade extends AbstractFacade<RecursoSistema> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private MenuFacade menuFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RecursoSistemaFacade() {
        super(RecursoSistema.class);
    }

    public List<RecursoSistema> listaRecursos(String s, ModuloSistema moduloRecurso) {
        String hql = "from RecursoSistema ";

        if (moduloRecurso != null || (s != null && !"".equals(s))) {
            hql = hql + " where ";
        }

        if (s != null && !"".equals(s)) {
            hql = hql + " (lower(nome) like :filtro or lower(caminho) like :filtro) ";
            if (moduloRecurso != null) {
                hql = hql + " and ";
            }
        }

        if (moduloRecurso != null) {
            hql = hql + " (modulo = :modulo and modulo is not null) ";
        }

        hql = hql + " order by nome ";

        Query q = getEntityManager().createQuery(hql);
        if (s != null && !"".equals(s)) {
            q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        }
        if (moduloRecurso != null) {
            q.setParameter("modulo", moduloRecurso);
        }
        return q.getResultList();
    }

    public List<RecursoSistema> listaOrdenadoPorCaminho() {
        String hql = "from RecursoSistema order by caminho";
        return em.createQuery(hql).getResultList();
    }

    public void renomeiaTodosRecursos() {
        List<RecursoSistema> todos = listaOrdenadoPorCaminho();
        String caminho = "";
        List<RecursoSistema> recursosDoMesmoCaminho = Lists.newArrayList();
        for (RecursoSistema rs : todos) {
            if (!caminhoSemArquivo(rs.getCaminho()).equals(caminho)) {
                if (!recursosDoMesmoCaminho.isEmpty()) {
                    renomeiaRecursos(recursosDoMesmoCaminho);
                }
                recursosDoMesmoCaminho.clear();
                caminho = caminhoSemArquivo(rs.getCaminho());
            }
            recursosDoMesmoCaminho.add(rs);
        }
    }

    private String caminhoSemArquivo(String caminho) {
        if (caminho.contains("/")) {
            return caminho.substring(0, caminho.lastIndexOf("/") + 1);
        }
        return "";
    }

    private void renomeiaRecursos(List<RecursoSistema> recursosDoMesmoCaminho) {
        String nomeRecurso = "";
        List<RecursoSistema> recursosDoMenu = Lists.newArrayList();
        for (RecursoSistema rs : recursosDoMesmoCaminho) {
            Menu menu = menuFacade.recuperarPeloCaminho(rs.getCaminho());
            if (menu != null) {
                nomeRecurso = menu.getCaminhoDoMenu().replace("INÍCIO > ", "");

                recursosDoMenu.add(rs);
                rs.setNome(nomeRecurso);
                rs.setModulo(moduloPeloNome(rs.getNome()));
                salvar(rs);
            }
        }

        for (RecursoSistema rs : recursosDoMenu) {
            if (recursosDoMesmoCaminho.contains(rs)) {
                recursosDoMesmoCaminho.remove(rs);
            }
        }

        if (!"".equals(nomeRecurso)) {
            for (RecursoSistema rs : recursosDoMesmoCaminho) {
                rs.setNome(nomeRecurso + " > " + rs.getCaminho().substring(rs.getCaminho().lastIndexOf("/") + 1, rs.getCaminho().length()).replace(".xhtml", "").toUpperCase());
                rs.setModulo(moduloPeloNome(rs.getNome()));
                salvar(rs);
            }
        }
    }

    private ModuloSistema moduloPeloNome(String nome) {
        if (nome.startsWith("CONTÁBIL >")) {
            return ModuloSistema.CONTABIL;
        } else if (nome.startsWith("PLANEJAMENTO PÚBLICO >")) {
            return ModuloSistema.PLANEJAMENTO;
        } else if (nome.startsWith("MATERIAIS >")) {
            return ModuloSistema.MATERIAIS;
        } else if (nome.startsWith("COMPRAS E LICITAÇÕES >")) {
            return ModuloSistema.LICITACAO;
        } else if (nome.startsWith("PROTOCOLO >")) {
            return ModuloSistema.PROTOCOLO;
        } else if (nome.startsWith("RBTRANS >")) {
            return ModuloSistema.RBTRANS;
        } else if (nome.startsWith("TRIBUTÁRIO >")) {
            return ModuloSistema.TRIBUTARIO;
        } else if (nome.startsWith("FROTA >") || nome.startsWith("FROTAS >")) {
            return ModuloSistema.FROTA;
        } else if (nome.startsWith("OBRA >") || nome.startsWith("OBRAS >")) {
            return ModuloSistema.OBRAS;
        } else if (nome.startsWith("RECURSOS HUMANOS >")) {
            return ModuloSistema.RH;
        } else if (nome.startsWith("GERENCIAMENTO DE RECURSOS >")) {
            return ModuloSistema.GERENCIAMENTO;
        } else if (nome.startsWith("CONFIGURAÇÕES >")) {
            return ModuloSistema.CONFIGURACAO;
        } else if (nome.startsWith("PATRIMONIO >") || nome.startsWith("PATRIMÔNIO >")) {
            return ModuloSistema.PATRIMONIO;
        } else {
            return ModuloSistema.CADASTROS;
        }

    }


    public List<RecursoSistema> buscarRecusosPorGrupoRecurso(GrupoRecurso grupoRecurso) {
        Query q = em.createQuery("select grupo.recursos from GrupoRecurso grupo where grupo = :grupo ");
        q.setParameter("grupo", grupoRecurso);
        return q.getResultList();
    }

    public RecursoSistema recuperarRecursoSistemaPorCaminho(String caminho) {
        String sql = " select * from recursosistema where lower(caminho) = :caminho";
        Query q = em.createNativeQuery(sql, RecursoSistema.class);
        q.setParameter("caminho", caminho.toLowerCase().trim());
        q.setMaxResults(1);
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return (RecursoSistema) q.getResultList().get(0);
        }
        return null;
    }
}
