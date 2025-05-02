package br.com.webpublico.controle;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean
@ViewScoped
public class ConfigContaDespesaTipoDocumentoPesquisaGenerica extends ComponentePesquisaGenerico implements Serializable {

    @Override
    public String getComplementoQuery() {
        return "  where to_date('" + super.getSistemaControlador().getDataOperacaoFormatada() + "','dd/mm/yyyy') between obj.inicioVigencia and coalesce(obj.finalVigencia,to_date('" + super.getSistemaControlador().getDataOperacaoFormatada() + "','dd/mm/yyyy'))"
            + " and " + montaCondicao() + montaOrdenacao();
    }
}
