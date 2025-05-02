package br.com.webpublico.controle;

import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Created by Fabio on 17/01/2019.
 */
@ManagedBean
@ViewScoped
public class PesquisaParametroOTT extends ComponentePesquisaGenerico {

    @Override
    public void getCampos() {
        super.getCampos();
        itens.add(new ItemPesquisaGenerica("obj.dividaVistoriaVeiculoOtt.descricao", "Dívida de Vistoria de Veículo da OTT", String.class, false, true));
        itens.add(new ItemPesquisaGenerica("obj.tributoVistoriaVeiculoOtt.descricao", "Tributo de Vistoria do Veículo da OTT", String.class, false, true));

        itensOrdenacao.add(new ItemPesquisaGenerica("obj.dividaVistoriaVeiculoOtt.descricao", "Dívida de Vistoria de Veículo da OTT", String.class, false, true));
        itensOrdenacao.add(new ItemPesquisaGenerica("obj.tributoVistoriaVeiculoOtt.descricao", "Tributo de Vistoria do Veículo da OTT", String.class, false, true));
    }
}
