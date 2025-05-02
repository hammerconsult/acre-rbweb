package br.com.webpublico.controle;

import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;
import br.com.webpublico.enums.Situacao;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created by israeleriston on 01/06/16.
 */
@ManagedBean
@ViewScoped
public class AgenciaPesquisaGenericaControlador extends ComponentePesquisaGenerico implements Serializable{

    @Override
    public void getCampos() {
       ItemPesquisaGenerica uf = new ItemPesquisaGenerica("obj.enderecoCorreio.uf", "Uf", String.class, false, false);
        ItemPesquisaGenerica localidade = new ItemPesquisaGenerica("obj.enderecoCorreio.localidade", "Cidade", String.class, false, false);
        ItemPesquisaGenerica situacao = new ItemPesquisaGenerica("obj.situacao", "Situacao", Situacao.class , true , false);
        super.getCampos();
        super.getItens().add(localidade);
        super.getItens().add(uf);
        super.getItens().add(situacao);
        super.getItensOrdenacao().add(localidade);
        super.getItensOrdenacao().add(uf);
        super.getItensOrdenacao().add(situacao);


    }

}
