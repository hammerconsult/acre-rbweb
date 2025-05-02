package br.com.webpublico.controle;

import br.com.webpublico.util.Util;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: RenatoRomanini
 * Date: 16/08/13
 * Time: 17:51
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "LDOPesquisaGenericoControlador")
@ViewScoped
public class LDOPesquisaGenericoControlador extends ComponentePesquisaGenerico implements Serializable {
    @Override
    public String getComplementoQuery() {
        SistemaControlador sistemaControlador = (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
        return " where obj.exercicio.id = " + sistemaControlador.getExercicioCorrente().getId() + " and " + montaCondicao() + montaOrdenacao();
    }
}
