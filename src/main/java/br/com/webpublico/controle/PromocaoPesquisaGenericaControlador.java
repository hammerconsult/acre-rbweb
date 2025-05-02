/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.CategoriaPCS;
import br.com.webpublico.entidades.ProgressaoPCS;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * @author Leonardo
 */
@ManagedBean
@ViewScoped
public class PromocaoPesquisaGenericaControlador extends PesquisaGenericaRHControlador {

    public PromocaoPesquisaGenericaControlador() {
        setNomeVinculo("obj.enquadramentoNovo.contratoServidor");
    }

    @Override
    public void getCampos() {
        super.getCampos();
        adicionaItemPesquisaGenerica("Categoria PCS Novo ", "obj.enquadramentoNovo.categoriaPCS.descricao", CategoriaPCS.class, Boolean.TRUE);
        adicionaItemPesquisaGenerica("Progressão PCS Novo", "obj.enquadramentoNovo.progressaoPCS.descricao", ProgressaoPCS.class, Boolean.TRUE);
        adicionaItemPesquisaGenerica("Categoria PCS Antigo", "obj.enquadramentoAnterior.categoriaPCS.descricao", CategoriaPCS.class, Boolean.TRUE);
        adicionaItemPesquisaGenerica("Progressão PCS Antigo", "obj.enquadramentoAnterior.progressaoPCS.descricao", ProgressaoPCS.class, Boolean.TRUE);
    }


    @Override
    public String getHqlConsulta() {
        return "select new Promocao(obj.id, obj.enquadramentoNovo, obj.enquadramentoAnterior, obj.dataPromocao, "
                + " obj.enquadramentoNovo.contratoServidor) "
                + " from " + classe.getSimpleName() + " obj ";
    }
}
