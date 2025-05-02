/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico;

import br.com.webpublico.controle.ComponentePesquisaGenerico;
import br.com.webpublico.controle.PesquisaGenericaRHControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.SituacaoFuncionalFacade;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Felipe Marinzeck
 */
@ManagedBean
@SessionScoped
public class UnidadeGestoraPesquisaGenericaControlador extends ComponentePesquisaGenerico implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;

    public UnidadeGestoraPesquisaGenericaControlador() {
    }

    @Override
    public void getCampos() {
        super.getCampos();
    }

//    @Override
//    public String getHqlContador() {
//        Exercicio exercicio = sistemaFacade.getExercicioCorrente();
//        return "select count(obj.id) "
//                + "   from ContratoFP obj where obj.exercicio.id = "+exercicio.getId();
//    }

    @Override
    public String getComplementoQuery() {
        Exercicio exercicio = sistemaFacade.getExercicioCorrente();
        return " where obj.exercicio.id = " + exercicio.getId() + " and " + montaCondicao() + montaOrdenacao();
    }
}

