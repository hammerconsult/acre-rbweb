package br.com.webpublico.controle;

import br.com.webpublico.enums.CategoriaOrcamentaria;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA. User: wiplash Date: 13/11/13 Time: 08:38 To
 * change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class EmpenhoRestoPagarEstornoPesquisaGenerica extends ComponentePesquisaGenerico implements Serializable {

    @Override
    public String getComplementoQuery() {

        return  " where obj.categoriaOrcamentaria = '" + CategoriaOrcamentaria.RESTO + "' "
                + " and obj.exercicio.ano = " + getSistemaControlador().getExercicioCorrente().getAno()
                + " and obj.saldo > 0 "
                + " and " + montaCondicao() + montaOrdenacao();
    }
}
