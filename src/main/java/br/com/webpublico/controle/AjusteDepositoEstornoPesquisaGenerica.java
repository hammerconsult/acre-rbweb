package br.com.webpublico.controle;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created by mga on 24/08/2017.
 */
@ManagedBean
@ViewScoped
public class AjusteDepositoEstornoPesquisaGenerica extends ComponentePesquisaGenerico implements Serializable {

    @Override
    public String getComplementoQuery() {
        return " where obj.unidadeOrganizacional.id in (" + getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente().getId() + ")" +
            "    and " + montaCondicao() + montaOrdenacao();
    }
}
