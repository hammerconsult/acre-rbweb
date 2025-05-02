/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.TipoProvimento;
import br.com.webpublico.entidadesauxiliares.DataTablePesquisaGenerico;
import br.com.webpublico.entidadesauxiliares.ItemPesquisaGenerica;
import com.google.common.collect.Lists;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;

/**
 *
 * @author Claudio
 */
@ManagedBean
@ViewScoped
public class EnquadramentoFuncionalPesquisaGenericaControlador extends PesquisaGenericaRHControlador{

    public EnquadramentoFuncionalPesquisaGenericaControlador() {
        setNomeVinculo("contratoServidor");
    }

    @Override
    public void getCampos() {
        super.getCampos();
        adicionaItemPesquisaGenerica("Nome Tratamento/Social do Servidor", "obj.contratoServidor.matriculaFP.pessoa.nomeTratamento", String.class, Boolean.FALSE);
        itens.add(new ItemPesquisaGenerica("tipoProvimento", "Apenas Provimentos de Enquadramento Funcional ?", Boolean.class, false, false, "Não", "Sim"));
    }

    @Override
    public String montaCondicao() {
        String condicao = super.montaCondicao();
        if (condicao.contains("tipoProvimento = true")) {
            condicao = condicao.replace("tipoProvimento = true", " obj.provimentoFP.tipoProvimento.codigo =  "+TipoProvimento.ENQUADRAMENTO_FUNCIONAL);
        }else{
            List<DataTablePesquisaGenerico> campos = Lists.newArrayList(camposPesquisa);
            List<DataTablePesquisaGenerico> removidos = Lists.newArrayList();
            for(DataTablePesquisaGenerico dataTablePesquisaGenerico:campos){
                if(dataTablePesquisaGenerico.getItemPesquisaGenerica().getCondicao().equals("tipoProvimento")){
                    camposPesquisa.remove(dataTablePesquisaGenerico);
                    removidos.add(dataTablePesquisaGenerico);
                }
            }
            condicao = super.montaCondicao();
            camposPesquisa.addAll(removidos);
        }
        return condicao;
    }
}
