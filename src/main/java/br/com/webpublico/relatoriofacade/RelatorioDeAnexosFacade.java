/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.relatoriofacade;

import br.com.webpublico.negocios.ContaFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 * @author reidocrime
 */
@Stateless
public class RelatorioDeAnexosFacade {

    @EJB
    private ContaFacade contaFacade;

    public ContaFacade getContaFacade() {
        return contaFacade;
    }
}
