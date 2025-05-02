package br.com.webpublico.controle;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Criado por Mateus
 * Data: 08/03/2017.
 */
@ManagedBean
@ViewScoped
public class ValeTransportePesquisaGenerica extends PesquisaGenericaRHControlador implements Serializable {

    public ValeTransportePesquisaGenerica() {
        setNomeVinculo("obj.contratoFP");
    }

    @Override
    public void getCampos() {
        super.getCampos();
    }
}
