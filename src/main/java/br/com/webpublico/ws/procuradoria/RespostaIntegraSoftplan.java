/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.ws.procuradoria;

/**
 *
 * @author Arthur
 */
public class RespostaIntegraSoftplan {
    private final Long cdRetorno;
    private final String deRetorno;

    public RespostaIntegraSoftplan(Long cdRetorno, String deRetorno) {
        this.cdRetorno = cdRetorno;
        this.deRetorno = deRetorno;
    }

    public Long getCdRetorno() {
        return cdRetorno;
    }

    public String getDeRetorno() {
        return deRetorno;
    }
}
