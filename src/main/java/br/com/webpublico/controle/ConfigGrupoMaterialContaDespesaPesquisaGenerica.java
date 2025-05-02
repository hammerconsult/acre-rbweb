/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Cota;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * @author Edi
 */
@ManagedBean
@ViewScoped
public class ConfigGrupoMaterialContaDespesaPesquisaGenerica extends ComponentePesquisaGenerico implements Serializable {

    @Override
    public String getComplementoQuery() {
        return "  where to_date('" + super.getSistemaControlador().getDataOperacaoFormatada() + "','dd/mm/yyyy') between trunc(obj.inicioVigencia) and coalesce(trunc(obj.fimVigencia), to_date('" + super.getSistemaControlador().getDataOperacaoFormatada() + "','dd/mm/yyyy'))"
                + " and " + montaCondicao() + montaOrdenacao();
    }

    @Override
    public void getCampos() {
        super.getCampos();

        ItemPesquisaGenerica itemPesquisaGenericaConta = new ItemPesquisaGenerica();
        itemPesquisaGenericaConta.setCondicao("obj.contaDespesa.codigo");
        itemPesquisaGenericaConta.setLabel("Código Conta Despesa");
        itemPesquisaGenericaConta.setTipo(Cota.class);
        itemPesquisaGenericaConta.setPertenceOutraClasse(true);
        super.getItens().add(itemPesquisaGenericaConta);
    }

    @Override
    public String processaIntervalo(String nomeCampo, String intervalo, boolean isString, boolean isValor, boolean isDate, boolean isStringExata) {
        if (nomeCampo.contains("obj.contaDespesa.codigo")) {
            String nomeDoCampo = "replace(obj.contaDespesa.codigo,'.','') ";
            String valor = intervalo.replace(".", "");
            if (!valor.contains(";") && !valor.contains("-")) {
                if (isString && !isValor) {
                    return nomeDoCampo + " LIKE '" + valor.toLowerCase().replace(".", "") + "%'";
                }
            }
        } else {
            return super.processaIntervalo(nomeCampo, intervalo, isString, isValor, isDate, false);
        }
        return nomeCampo;
    }
}
