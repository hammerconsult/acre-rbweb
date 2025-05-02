package br.com.webpublico.controle;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: RenatoRomanini
 * Date: 23/11/13
 * Time: 17:25
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class GrupoOrcamentarioPesquisaGenericoControlador extends ComponentePesquisaGenerico implements Serializable {
    @Override
    public String montaCondicao() {
        return super.montaCondicao() + " and obj.exercicio.id = " + super.getSistemaControlador().getExercicioCorrente().getId();
    }
}
