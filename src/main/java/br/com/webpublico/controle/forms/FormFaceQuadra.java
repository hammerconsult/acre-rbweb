package br.com.webpublico.controle.forms;

import br.com.webpublico.entidades.Bairro;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: java
 * Date: 19/08/13
 * Time: 16:30
 * To change this template use File | Settings | File Templates.
 */
public class FormFaceQuadra implements Serializable {

    private Bairro bairro;

    public Bairro getBairro() {
        return bairro;
    }

    public void setBairro(Bairro bairro) {
        this.bairro = bairro;
    }
}
