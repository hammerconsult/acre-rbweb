package br.com.webpublico.seguranca.menu;

import br.com.webpublico.entidades.Menu;
import br.com.webpublico.entidades.UsuarioSistema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author Felipe Marinzeck
 */
@Controller
@RequestMapping(value = "/ler-menu")
@Scope("session")
public class LeitorMenu implements Serializable {

    @Autowired
    private LeitorMenuFacade leitorMenuFacade;


    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public List<Menu> getItensParaContruirMenu() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UsuarioSistema usuarioSistema = (UsuarioSistema) authentication.getPrincipal();
        return leitorMenuFacade.getItensParaContruirMenu(usuarioSistema);
    }

    @RequestMapping(value = "/autocomplete", method = RequestMethod.GET)
    @ResponseBody
    public List<Menu> getItensParaAutoComplete() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UsuarioSistema usuarioSistema = (UsuarioSistema) authentication.getPrincipal();
        List<Menu> todos = new ArrayList<>();
        for (Menu m : leitorMenuFacade.getItensParaContruirMenu(usuarioSistema)) {
            todos.add(m.getPai());
            todos.add(m);
        }
        todos = new ArrayList(new HashSet(todos));
        return todos;
    }
}
