/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.util.ConverterExercicio;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;

/**
 * @author Wellington
 */
@ManagedBean
@SessionScoped
public class ExercicioInicialFinalControlador implements Serializable {

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
