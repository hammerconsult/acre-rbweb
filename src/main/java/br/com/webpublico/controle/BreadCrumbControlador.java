package br.com.webpublico.controle;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author daniel
 */
@ManagedBean
@SessionScoped
public class BreadCrumbControlador implements Serializable {

    private Map<String, UIComponent> componentes = new HashMap<String, UIComponent>();
    private String menuAtual;

    public Map<String, UIComponent> getComponentes() {
        return componentes;
    }

    public void setComponentes(Map<String, UIComponent> componentes) {
        this.componentes = componentes;
    }

    public String getMenuAtual() {
        return menuAtual;
    }

    public void setMenuAtual(String menuAtual) {
        this.menuAtual = menuAtual;
    }

    public List<UIComponent> getItems() {
        //System.out.println("menuAtual: " + menuAtual);
        UIComponent temp = componentes.get(menuAtual);
        //System.out.println("componente: " + temp);
        List<UIComponent> retorno = new ArrayList<UIComponent>();
        if (temp != null) {
            while (temp.getParent() != null) {
                retorno.add(temp);
                temp = temp.getParent();
            }
        }
        //System.out.println("retorno: " + retorno);
        return retorno;
    }

}
