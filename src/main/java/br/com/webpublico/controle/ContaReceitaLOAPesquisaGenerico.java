/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.util.Util;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
public class ContaReceitaLOAPesquisaGenerico extends ComponentePesquisaGenerico implements Serializable {
    @Override
    public String getComplementoQuery() {
        ReceitaLOAWizardControlador receitaLOAWizardControlador = (ReceitaLOAWizardControlador) Util.getControladorPeloNome("receitaLOAWizardControlador");
        Long id = receitaLOAWizardControlador.getId();

        return "where obj.loa.id = '" + id + "' and " + montaCondicao() + montaOrdenacao();
    }

    public String processaIntervalo(String nomeCampo, String intervalo, boolean isString, boolean isValor, boolean isDate) {
        if (!nomeCampo.contains("obj.codigo")) {
            return super.processaIntervalo(nomeCampo, intervalo, isString, isValor, isDate, false);
        } else {
            String nomeDoCampo = "replace(obj.codigo,'.','') ";
            String valor = intervalo.replace(".", "");
            if (!valor.contains(";") && !valor.contains("-")) {
                if (isString && !isValor) {
                    return nomeDoCampo + " LIKE '" + valor.toLowerCase() + "%'";
                }
            }

            String aspas = isString ? "'" : "";
            StringBuilder retorno = new StringBuilder("(");
            int lengthInicialRetorno = retorno.length();
            String[] pedacos = valor.split(";");
            List<String> pedacosTraco = new ArrayList<>();
            for (String pedaco : pedacos) {

                if (!pedaco.contains("-") && !"".equals(pedaco.trim())) {
                    if (retorno.length() == lengthInicialRetorno) {
                        retorno.append(nomeDoCampo).append(" in (");
                    }
                    retorno.append(aspas).append(pedaco).append(aspas).append(",");
                } else {
                    pedacosTraco.add(pedaco);
                }
            }
            if (retorno.length() != lengthInicialRetorno) {
                retorno.deleteCharAt(retorno.lastIndexOf(","));
                retorno.append(")");
            }
            for (String pedaco : pedacosTraco) {
                String[] pedacosDoTraco = pedaco.split("-");
                if (pedacosDoTraco.length >= 2) {
                    if (retorno.length() != lengthInicialRetorno) {
                        retorno.append(" OR ");
                    }
                    retorno.append(nomeDoCampo).append(" BETWEEN ").append(aspas).append(pedacosDoTraco[0].toLowerCase()).append(aspas).append(" AND ").append(aspas).append(pedacosDoTraco[pedacosDoTraco.length - 1].toLowerCase()).append(aspas).append("");
                }
            }
            if (retorno.length() != lengthInicialRetorno) {
                retorno.append(")");
            }
            //System.out.println("retorno..: " + retorno.toString());
            return retorno.toString();
        }
    }

}
