package br.com.webpublico.controle;

import br.com.webpublico.entidadesauxiliares.DataTablePesquisaGenerico;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;

@ManagedBean
@ViewScoped
public class FeriadoPesquisaGenerico extends ComponentePesquisaGenerico implements Serializable {

    @Override
    public void getCampos() {
        itens = Lists.newArrayList();
        itensOrdenacao = Lists.newArrayList();
        super.getCampos();
        ItemPesquisaGenerica dataFeriado = new ItemPesquisaGenerica("obj.dataFeriado", "Data do Feriado", Date.class, false, false);
        DataTablePesquisaGenerico dataTablePesquisaGenerico = new DataTablePesquisaGenerico();
        dataTablePesquisaGenerico.setItemPesquisaGenerica(dataFeriado);
        dataTablePesquisaGenerico.setValuePesquisa(DataUtil.getDataFormatada(DataUtil.criarDataComMesEAno(1, getSistemaControlador().getExercicioCorrente().getAno()).toDate()));
        dataTablePesquisaGenerico.setValeuPesquisaDataFim(DataUtil.getDataFormatada(DataUtil.criarDataUltimoDiaMes(12, getSistemaControlador().getExercicioCorrente().getAno()).toDate()));
        itens.add(dataFeriado);
        camposPesquisa.add(dataTablePesquisaGenerico);
        itensOrdenacao.add(dataFeriado);
    }

}
