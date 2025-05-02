package br.com.webpublico.controle;

import br.com.webpublico.entidades.ObjetoFrota;
import br.com.webpublico.enums.TipoObjetoFrota;
import br.com.webpublico.util.Util;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 11/09/14
 * Time: 18:10
 * To change this template use File | Settings | File Templates.
 */

@ManagedBean
@ViewScoped
public class ComponentePesquisaObjetoFrotaControlador implements Serializable {


    public ComponentePesquisaObjetoFrotaControlador() {
    }

    public List<SelectItem> tiposDeObjetoFrota() {
        return Util.getListSelectItem(Arrays.asList(TipoObjetoFrota.values()));
    }

    public void novoVeiculo(PrettyControlador controlador) {
        Web.navegacao(controlador.getCaminhoOrigem(), "/frota/veiculo/novo/", controlador.getSelecionado());
    }

    public void novoEquipamento(PrettyControlador controlador) {
        Web.navegacao(controlador.getCaminhoOrigem(), "/frota/equipamento/novo/", controlador.getSelecionado());
    }

    public void processaSelecaoObjetoFrota(){}
}
