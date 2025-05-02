package br.com.webpublico.controle;

import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;
import com.google.common.collect.Lists;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean
@ViewScoped
public class ClasseCredorPesquisaGenerica extends ComponentePesquisaGenerico implements Serializable {

    @Override
    public void getCampos() {
        itens = Lists.newArrayList();
        itensOrdenacao = Lists.newArrayList();
        super.getCampos();
        ItemPesquisaGenerica codigo = new ItemPesquisaGenerica("to_number(obj.codigo)", "Código", String.class, false, false);
        itens.add(codigo);
        itensOrdenacao.add(codigo);
    }


    @Override
    protected String montaOrdenacao() {
        if (getCamposOrdenacao().size() <= 1) {
            for (ItemPesquisaGenerica itemPesquisaGenerica : getCamposOrdenacao()) {
                if (itemPesquisaGenerica.getCondicao() == null) {
                    return " order by to_number(codigo) ";
                }
                return super.montaOrdenacao();
            }
        }
        return super.montaOrdenacao();
    }
}

