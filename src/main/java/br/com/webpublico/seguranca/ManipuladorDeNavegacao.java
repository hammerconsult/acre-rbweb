package br.com.webpublico.seguranca;

import br.com.webpublico.negocios.ExcecaoNegocioGenerica;

import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.NavigationCase;
import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import java.util.Map;
import java.util.Set;

/**
 * @author fabio
 */
public class ManipuladorDeNavegacao extends ConfigurableNavigationHandler {

    private NavigationHandler parent;

    public ManipuladorDeNavegacao(NavigationHandler parent) {
        this.parent = parent;
    }

    @Override
    public void handleNavigation(FacesContext context, String from, String outcome) {
        if (from != null && outcome != null) {
            if (!outcome.endsWith("?faces-redirect=true")) {
                outcome += "?faces-redirect=true";
            }

            try {
                parent.handleNavigation(context, from, outcome);
            } catch (Exception e) {
            }

        }
    }

    @Override
    public NavigationCase getNavigationCase(FacesContext context, String fromAction, String outcome) {
        if (parent instanceof ConfigurableNavigationHandler) {
            return ((ConfigurableNavigationHandler) parent).getNavigationCase(context, fromAction, outcome);
        } else {
            throw new ExcecaoNegocioGenerica("Erro ao invocar o método ManipuladorDeNavegacao.NavigationCase()");
        }
    }

    @Override
    public Map<String, Set<NavigationCase>> getNavigationCases() {
        if (parent instanceof ConfigurableNavigationHandler) {
            return ((ConfigurableNavigationHandler) parent).getNavigationCases();
        } else {
            throw new ExcecaoNegocioGenerica("Erro ao invocar o método ManipuladorDeNavegacao.getNavigationCases()");
        }
    }
}
