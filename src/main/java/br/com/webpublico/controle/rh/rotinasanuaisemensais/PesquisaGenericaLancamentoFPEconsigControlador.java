/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle.rh.rotinasanuaisemensais;

import br.com.webpublico.controle.PesquisaGenericaRHControlador;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * @author Peixe
 * @since 18/08/2014
 */
@ManagedBean
@ViewScoped
public class PesquisaGenericaLancamentoFPEconsigControlador extends PesquisaGenericaRHControlador implements Serializable {
    public PesquisaGenericaLancamentoFPEconsigControlador() {
        setNomeVinculo("obj.vinculoFP");
    }


    @Override
    public String montaCondicao() {
        return super.montaCondicao() + " and obj.tipoImportacao = 'ECONSIG' ";
    }

 /*   @Override
    public String getHqlContador() {
        if (getSistemaControlador().getUsuarioCorrente().possuiAcessoTodosVinculosRH()) {
            return super.getHqlContador();
        }
        String complemento = " and " + montaCondicao() + montaOrdenacao();
        return "select count(obj.id) from LancamentoFP obj" +
                " inner join obj.vinculoFP vin " +
                " inner join vin.lotacaoFuncionals lotacao " +
                getComplementoQueryFiltrandoPelasUnidadesDoUsuario() +
                complemento;
    }

    @Override
    public String getHqlConsulta() {
        if (getSistemaControlador().getUsuarioCorrente().possuiAcessoTodosVinculosRH()) {
            return super.getHqlConsulta();
        }

        String complemento = " and " + montaCondicao() + montaOrdenacao();
        return "select obj from LancamentoFP obj" +
                " inner join obj.vinculoFP vin " +
                " inner join vin.lotacaoFuncionals lotacao " +
                getComplementoQueryFiltrandoPelasUnidadesDoUsuario() +
                complemento;
    }*/
}
