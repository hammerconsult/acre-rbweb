package br.com.webpublico.singletons;

import br.com.webpublico.entidades.Menu;
import br.com.webpublico.entidades.RecursoSistema;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.seguranca.SingletonRecursosSistema;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class SingletonMenu implements Serializable {

    private static String APP_PERFIL_DESATIVAR_CONT = "DESATIVAR_CONTABIL";
    private static String APP_PERFIL_DESATIVAR_TRI = "DESATIVAR_TRIBUTARIO";
    private static String APP_PERFIL_DESATIVAR_RH = "DESATIVAR_RH";

    @PersistenceContext
    protected transient EntityManager entityManager;

    private List<Menu> menus;
    private Map<UsuarioSistema, List<Menu>> menusDoUsuario;
    private SingletonRecursosSistema singletonRecursosSistema;

    @PostConstruct
    public void init() {
        singletonRecursosSistema = (SingletonRecursosSistema) Util.getSpringBeanPeloNome("singletonRecursosSistema");
    }

    public List<Menu> getMenus() {
        if (menus == null || menus.isEmpty()) {
            String hql = "select distinct m from Menu m order by m.label asc";
            Query q = entityManager.createQuery(hql);
            menus = q.getResultList();
        }
        return menus;
    }

    public void limparMenusUsuario(UsuarioSistema us) {
        if (menusDoUsuario != null && menusDoUsuario.get(us) != null) {
            menusDoUsuario.get(us).clear();
        }
    }

    public void limparTodosMenus() {
        menusDoUsuario = Maps.newHashMap();
        menus = Lists.newArrayList();
    }

    public List<Menu> getMenusRecursosGrupoRecursos(UsuarioSistema usuarioSistema) {
        if (menusDoUsuario == null) {
            menusDoUsuario = Maps.newHashMap();
        }
        if (menusDoUsuario.get(usuarioSistema) == null || menusDoUsuario.get(usuarioSistema).isEmpty()) {
            menusDoUsuario.put(usuarioSistema, Lists.<Menu>newArrayList());
            usuarioSistema = singletonRecursosSistema.getUsuarioPorCpf(usuarioSistema.getCpf());
            montarMenusParaUsuario(usuarioSistema);
        }
        return menusDoUsuario.get(usuarioSistema);
    }

    private void montarMenusParaUsuario(UsuarioSistema usuarioSistema) {
        Set<RecursoSistema> caminhosUsuario = singletonRecursosSistema.getTodosRecursosDoUsuario(usuarioSistema);
        for (Menu menu : getMenus()) {
            for (RecursoSistema recurso : caminhosUsuario) {
                if (menu.getCaminho() != null && menu.getCaminho().equals(recurso.getCaminho())) {
                    if (!recurso.getCaminho().contains("tributario/cadastromunicipal")) {
                        String perfilContabil = System.getenv(APP_PERFIL_DESATIVAR_CONT);
                        if (perfilContabil != null && perfilContabil.equals("true")) {
                            if (recurso.getCaminho().contains("/financeiro/") || recurso.getCaminho().contains("/administrativo/")) {
                                continue;
                            }
                        }

                        String perfilTributario = System.getenv(APP_PERFIL_DESATIVAR_TRI);
                        if (perfilTributario != null && perfilTributario.equals("true")) {
                            if (recurso.getCaminho().contains("/tributario/")) {
                                continue;
                            }
                        }

                        String perfilRH = System.getenv(APP_PERFIL_DESATIVAR_RH);
                        if (perfilRH != null && perfilRH.equals("true")) {
                            if (recurso.getCaminho().contains("/rh/")) {
                                continue;
                            }
                        }
                    }
                    menusDoUsuario.get(usuarioSistema).add(menu);
                }
            }
        }
    }

    public Set<RecursoSistema> getTodosRecursosDoUsuario(UsuarioSistema usuarioSistema) {
        return singletonRecursosSistema.getTodosRecursosDoUsuario(usuarioSistema);
    }

}
