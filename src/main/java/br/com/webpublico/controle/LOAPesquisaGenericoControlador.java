package br.com.webpublico.controle;

import br.com.webpublico.util.Util;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: RenatoRomanini
 * Date: 19/08/13
 * Time: 18:15
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "lOAPesquisaGenericoControlador")
@ViewScoped
public class LOAPesquisaGenericoControlador extends ComponentePesquisaGenerico implements Serializable {

    @Override
    public String getComplementoQuery() {
        SistemaControlador sistemaControlador = (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
        return " where obj.ldo.exercicio.id = " + sistemaControlador.getExercicioCorrente().getId() + " and " + montaCondicao() + montaOrdenacao();
    }
}
