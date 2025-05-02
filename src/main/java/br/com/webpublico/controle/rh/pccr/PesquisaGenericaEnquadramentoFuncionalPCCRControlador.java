/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle.rh.pccr;

import br.com.webpublico.controle.PesquisaGenericaRHControlador;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;

/**
 * @author Peixe
 * @since 18/08/2014
 */
@ManagedBean
@SessionScoped
public class PesquisaGenericaEnquadramentoFuncionalPCCRControlador extends PesquisaGenericaRHControlador implements Serializable {
    public PesquisaGenericaEnquadramentoFuncionalPCCRControlador() {
        setNomeVinculo("contratoServidor");
    }
}
