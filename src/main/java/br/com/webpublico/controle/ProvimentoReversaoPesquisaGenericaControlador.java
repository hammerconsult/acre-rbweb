package br.com.webpublico.controle;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Claudio
 * Date: 30/07/13
 * Time: 08:48
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class ProvimentoReversaoPesquisaGenericaControlador  extends PesquisaGenericaRHControlador implements Serializable {


    public ProvimentoReversaoPesquisaGenericaControlador() {
        setNomeVinculo("aposentadoria");
    }

}
