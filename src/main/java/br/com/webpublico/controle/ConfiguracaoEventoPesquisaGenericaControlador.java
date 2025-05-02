package br.com.webpublico.controle;

import br.com.webpublico.entidadesauxiliares.DataTablePesquisaGenerico;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Romanini
 * Date: 21/06/13
 * Time: 09:19
 * To change this template use File | Settings | File Templates.
 */
@ViewScoped
@ManagedBean
public class ConfiguracaoEventoPesquisaGenericaControlador extends ComponentePesquisaGenerico implements Serializable {

    protected ItemPesquisaGenerica criarItemPesquisa(String condicao, String label, Boolean percenteOutraClasse, Object tipo) {
        ItemPesquisaGenerica itemPesquisaGenerica = new ItemPesquisaGenerica();
        itemPesquisaGenerica.setCondicao(condicao);
        itemPesquisaGenerica.setLabel(label);
        itemPesquisaGenerica.setTipo(tipo);
        itemPesquisaGenerica.setPertenceOutraClasse(percenteOutraClasse);
        itemPesquisaGenerica.setTipoOrdenacao("asc");
        return itemPesquisaGenerica;
    }

    @Override
    public String montaCondicao() {
        String condicao = super.montaCondicao();
        List<DataTablePesquisaGenerico> camposPesquisa1 = super.getCamposPesquisa();
        for (DataTablePesquisaGenerico dataTablePesquisaGenerico : camposPesquisa1) {
            if (dataTablePesquisaGenerico.getItemPesquisaGenerica() != null) {
                if (dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao().contains("fimVigencia")) {
                    if (!super.validaItem(dataTablePesquisaGenerico)) {
                        condicao = condicao + " and obj.fimVigencia is null ";
                    }
                }
                if (dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao().contains("inicioVigencia")) {
                    if (!super.validaItem(dataTablePesquisaGenerico)) {
                        condicao = condicao + " and obj.inicoVigencia is null ";
                    }
                }
            }
        }

        return condicao + " and to_date('" + super.getSistemaControlador().getDataOperacaoFormatada() + "','dd/mm/yyyy') between obj.inicioVigencia and coalesce(obj.fimVigencia,to_date('" + super.getSistemaControlador().getDataOperacaoFormatada() + "','dd/mm/yyyy'))";
    }

    @Override
    public void alteraValorCampo(DataTablePesquisaGenerico item) {
        if (renderIsDate(item)) {
            if (item.getItemPesquisaGenerica().getCondicao().contains("inicioVigencia")) {
                try {
                    String exercicio = super.getSistemaControlador().getExercicioCorrenteAsInteger().toString();
                    Date parse = new SimpleDateFormat("dd/MM/yyyy").parse("01/01/" + exercicio);
                    item.setValuePesquisa(new SimpleDateFormat("dd/MM/yyyy").format(parse));
                    item.setValeuPesquisaDataFim(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
                } catch (Exception e) {

                }
            } else {
                item.setValuePesquisa(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
                item.setValeuPesquisaDataFim(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
            }
        } else if (renderIsBigDecimal(item)) {
            item.setValuePesquisa(String.valueOf(BigDecimal.ZERO));
        } else if (renderIsBoolean(item)) {
            item.setValuePesquisa("true");
        } else {
            item.setValuePesquisa("");
        }
    }
}
