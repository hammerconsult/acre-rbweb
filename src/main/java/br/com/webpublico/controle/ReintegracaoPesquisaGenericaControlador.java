/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.AtoLegal;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Date;

/**
 *
 * @author Leonardo
 */
@ManagedBean
@ViewScoped
public class ReintegracaoPesquisaGenericaControlador extends PesquisaGenericaRHControlador{

    public ReintegracaoPesquisaGenericaControlador() {
        setNomeVinculo("contratoFP");
    }

    @Override
    public void getCampos() {
        super.getCampos();
        adicionaItemPesquisaGenerica("Ato Legal.Número", "obj.atoLegal.numero", AtoLegal.class, Boolean.TRUE);
        adicionaItemPesquisaGenerica("Ato Legal.Data de Publicação", "obj.atoLegal.dataPublicacao", Date.class, Boolean.TRUE);
    }
}
