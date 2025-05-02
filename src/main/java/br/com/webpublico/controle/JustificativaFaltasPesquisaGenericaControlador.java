package br.com.webpublico.controle;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Claudio
 * Date: 10/06/14
 * Time: 11:08
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class JustificativaFaltasPesquisaGenericaControlador extends PesquisaGenericaRHControlador implements Serializable {

    public JustificativaFaltasPesquisaGenericaControlador() {
        setNomeVinculo("faltas.contratoFP");
    }

    @Override
    public void getCampos() {
        super.getCampos();
    }

    @Override
    public String getComplementoQuery() {
        return " inner join obj.faltas faltas where " + montaCondicao() + montaOrdenacao();
    }
}
