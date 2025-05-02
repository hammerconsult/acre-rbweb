package br.com.webpublico.controle;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created by carlos on 30/04/15.
 */

@ManagedBean
@ViewScoped
public class PesquisaGenericaCategoriaPCSControlador extends ComponentePesquisaGenerico implements Serializable {

    @Override
    public String getComplementoQuery() {
        return " where superior.id is null and " + montaCondicao() + montaOrdenacao();
    }
}
