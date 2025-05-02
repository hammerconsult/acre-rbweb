package br.com.webpublico.controle;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Carnage
 * Date: 08/08/13
 * Time: 10:23
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class ReadaptacaoPesquisaGenericaControlador extends PesquisaGenericaRHControlador implements Serializable {

    public ReadaptacaoPesquisaGenericaControlador() {
        setNomeVinculo("contratoFP");
    }
}
