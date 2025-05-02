/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.CategoriaPCS;
import br.com.webpublico.entidades.OpcaoSalarialCC;
import br.com.webpublico.entidades.ProgressaoPCS;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class CargoComissaoPesquisaGenericaControlador extends PesquisaGenericaRHControlador {

    public CargoComissaoPesquisaGenericaControlador() {
        setNomeVinculo("obj.contratoFP");
    }

    @Override
    public void getCampos() {
        super.getCampos();

        ItemPesquisaGenerica opcaoSalarialCC = new ItemPesquisaGenerica();
        opcaoSalarialCC.setLabel("Opção Salarial CC");
        opcaoSalarialCC.setCondicao("opcaoSalarialCC.descricao");
        opcaoSalarialCC.setTipo(OpcaoSalarialCC.class);

        ItemPesquisaGenerica categoriaPCS = new ItemPesquisaGenerica();
        categoriaPCS.setLabel("Categoria PCS");
        categoriaPCS.setCondicao("enquadramento.categoriaPCS.descricao");
        categoriaPCS.setTipo(CategoriaPCS.class);
        categoriaPCS.setPertenceOutraClasse(true);

        ItemPesquisaGenerica progressaoPCS = new ItemPesquisaGenerica();
        progressaoPCS.setLabel("Progressão PCS");
        progressaoPCS.setCondicao("enquadramento.progressaoPCS.descricao");
        progressaoPCS.setTipo(ProgressaoPCS.class);
        progressaoPCS.setPertenceOutraClasse(true);

        super.getItens().add(opcaoSalarialCC);
        super.getItens().add(categoriaPCS);
        super.getItens().add(progressaoPCS);

        super.getItensOrdenacao().add(opcaoSalarialCC);
        super.getItensOrdenacao().add(categoriaPCS);
        super.getItensOrdenacao().add(progressaoPCS);
    }

    @Override
    public String getComplementoQuery() {
        return " left join obj.enquadramentoCCs enquadramento where " + montaCondicao() + montaOrdenacao();
    }

}
