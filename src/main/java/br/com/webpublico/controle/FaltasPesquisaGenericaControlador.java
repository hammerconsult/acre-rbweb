/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.enums.TipoFalta;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class FaltasPesquisaGenericaControlador extends PesquisaGenericaRHControlador implements Serializable {

    public FaltasPesquisaGenericaControlador() {
        setNomeVinculo("contratoFP");
    }

    @Override
    public void getCampos() {
        super.getCampos();
        adicionaItemPesquisaGenerica("Tipo de Falta", "tipoFalta", TipoFalta.class, Boolean.TRUE, Boolean.TRUE);
        adicionaItemPesquisaGenerica("Nome Tratamento/Social do Servidor", "obj.contratoFP.matriculaFP.pessoa.nomeTratamento", String.class, Boolean.FALSE);
    }
}
