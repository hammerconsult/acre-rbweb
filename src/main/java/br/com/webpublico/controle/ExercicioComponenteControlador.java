package br.com.webpublico.controle;

import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.util.ConverterExercicio;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created by zaca on 15/03/17.
 */
@ManagedBean
@ViewScoped
public class ExercicioComponenteControlador implements Serializable {

    @EJB
    private ExercicioFacade exercicioFacade;
    private ConverterExercicio converterExercicio;

    public ConverterExercicio getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterExercicio(exercicioFacade);
        }
        return converterExercicio;
    }
}
