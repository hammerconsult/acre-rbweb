package br.com.webpublico.controle.comum;


import br.com.webpublico.controle.ComponentePesquisaGenerico;
import br.com.webpublico.enums.tributario.OrigemFaleConosco;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class PesquisaFaleConoscoControlador extends ComponentePesquisaGenerico {

    @Override
    public String getComplementoQuery() {
        return " where obj.origemFaleConosco != '" + OrigemFaleConosco.NFSE.name() + "' and " + montaCondicao() + montaOrdenacao();
    }

}
