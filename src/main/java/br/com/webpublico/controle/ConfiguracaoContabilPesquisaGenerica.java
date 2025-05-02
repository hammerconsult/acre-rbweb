/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Usuario
 */
@ManagedBean
@ViewScoped
public class ConfiguracaoContabilPesquisaGenerica extends ComponentePesquisaGenerico implements Serializable {

    @Override
    public void getCampos() {
        super.getCampos();

        ItemPesquisaGenerica item = new ItemPesquisaGenerica("desde", "Data", Date.class);
        super.getItens().add(item);
    }

}
