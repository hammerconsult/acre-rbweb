/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.negocios.ProcessoFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * @author Fabio
 */
@ManagedBean
@ViewScoped
public class ComponenteProtocoloControlador implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(SolicitacaoProcessoIsencaoIPTUControlador.class);
    @EJB
    private ProcessoFacade processoFacade;

    public ComponenteProtocoloControlador() {
    }

    public boolean isProtocoloRecuperadoPorNumeroAndAno(Integer numero, Integer ano, boolean protocolo) {
        if (numero != null && ano != null) {
            boolean retorno = !processoFacade.buscarProcessoPorNumeroEAno(numero, ano).isEmpty();
            return retorno;
        }
        return false;
    }

    public void redirecionarParaProtocolo(String origem, String numero, String ano, Object objetoNaSessao) {
        Web.navegacao(origem, "/protocolo/ver/" + numero + "/" + ano + "/", objetoNaSessao);
    }

    public void redirecionarParaProtocolo(String origem, String numero, Integer ano, Object objetoNaSessao) {
        redirecionarParaProtocolo(origem, numero, ano+"", objetoNaSessao);
    }
}
