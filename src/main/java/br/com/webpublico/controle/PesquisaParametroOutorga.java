package br.com.webpublico.controle;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Created by William on 21/03/2017.
 */
@ManagedBean
@ViewScoped
public class PesquisaParametroOutorga extends ComponentePesquisaGenerico {

    @Override
    protected String montaOrdenacao() {
        if (camposOrdenacao == null || camposOrdenacao.size() <= 1) {
            return " order by obj.exercicio desc";
        }
        return super.montaOrdenacao();
    }
}
