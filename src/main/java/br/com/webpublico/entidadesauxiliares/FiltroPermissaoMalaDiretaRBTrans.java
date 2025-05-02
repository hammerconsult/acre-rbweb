package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Claudio
 * Date: 20/03/14
 * Time: 14:30
 * To change this template use File | Settings | File Templates.
 */
public class FiltroPermissaoMalaDiretaRBTrans implements Serializable {

    private Integer permissaoInicial;
    private Integer permissaoFinal;

    public FiltroPermissaoMalaDiretaRBTrans() {
    }

    public Integer getPermissaoInicial() {
        return permissaoInicial;
    }

    public void setPermissaoInicial(Integer permissaoInicial) {
        this.permissaoInicial = permissaoInicial;
    }

    public Integer getPermissaoFinal() {
        return permissaoFinal;
    }

    public void setPermissaoFinal(Integer permissaoFinal) {
        this.permissaoFinal = permissaoFinal;
    }
}
