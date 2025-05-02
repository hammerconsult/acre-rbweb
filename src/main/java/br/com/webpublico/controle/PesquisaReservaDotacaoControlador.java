package br.com.webpublico.controle;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Created by fox_m on 16/03/2016.
 */
@ManagedBean
@ViewScoped
public class PesquisaReservaDotacaoControlador extends AbstractPesquisaLicitacaoCompra {

    @Override
    public String getNivelHierarquia() {
        return null;
    }

    @Override
    protected String[] getAtributoUnidadeOrganizacional() {
        return new String[0];
    }
}
