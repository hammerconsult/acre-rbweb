package br.com.webpublico.seguranca.menu;

import br.com.webpublico.entidades.Menu;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.singletons.SingletonMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Felipe Marinzeck
 * Date: 18/07/13
 * Time: 11:33
 * To change this template use File | Settings | File Templates.
 */
@Service
public class LeitorMenuFacade implements Serializable {
    private final Logger logger = LoggerFactory.getLogger(LeitorMenuFacade.class);

    private SingletonMenu singletonMenu;

    @PostConstruct
    public void init() {
        try {
            singletonMenu = (SingletonMenu) new InitialContext().lookup("java:module/SingletonMenu");
        } catch (Exception e) {
            logger.error("Não foi possivel fazer lookup do singletonMenu");
        }
    }

    public List<Menu> getItensParaContruirMenu(UsuarioSistema us) {
        return getMenusRecursosGrupoRecursos(us);
    }

    public void limparMenusUsuario(UsuarioSistema us) {
        singletonMenu.limparMenusUsuario(us);
    }

    public void limparTodosMenus() {
        singletonMenu.limparTodosMenus();
    }

    private List<Menu> getMenusRecursosGrupoRecursos(UsuarioSistema us) {
        return singletonMenu.getMenusRecursosGrupoRecursos(us);
    }
}
