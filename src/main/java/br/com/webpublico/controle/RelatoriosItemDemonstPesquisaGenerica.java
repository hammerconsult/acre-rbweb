package br.com.webpublico.controle;

import br.com.webpublico.util.EntidadeMetaData;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: wiplash
 * Date: 25/09/13
 * Time: 10:43
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class RelatoriosItemDemonstPesquisaGenerica extends ComponentePesquisaGenerico implements Serializable {

    @Override
    public String montaCondicao() {
        return super.montaCondicao() + " and obj.exercicio.id = " + getSistemaControlador().getExercicioCorrente().getId();
    }
}
