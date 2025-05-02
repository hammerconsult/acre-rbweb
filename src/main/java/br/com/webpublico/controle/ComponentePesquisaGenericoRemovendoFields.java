package br.com.webpublico.controle;

import br.com.webpublico.entidadesauxiliares.AtributoRelatorioGenerico;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 04/10/13
 * Time: 09:51
 * To change this template use File | Settings | File Templates.
 */
public abstract class ComponentePesquisaGenericoRemovendoFields extends ComponentePesquisaGenerico implements Serializable {

    public abstract List<String> getNomesDosFieldsParaRemover();

    @Override
    public void prepararConfiguracaoRelatorio() {
        super.prepararConfiguracaoRelatorio();
        removerAtributos();
    }

    private void removerAtributos() {
        List<AtributoRelatorioGenerico> remover = new ArrayList<>();
        for (String nome : getNomesDosFieldsParaRemover()) {
            for (AtributoRelatorioGenerico atr : getRelatorioTabela().getTarget()) {
                if (atr.getField().getName().equalsIgnoreCase(nome)) {
                    remover.add(atr);
                }
            }
        }
        for (AtributoRelatorioGenerico atr : remover) {
            getRelatorioTabela().getTarget().remove(atr);
        }
    }
}
