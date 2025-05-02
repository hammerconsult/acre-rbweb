/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.LancamentoFP;
import br.com.webpublico.entidadesauxiliares.AtributoRelatorioGenerico;
import br.com.webpublico.util.MoneyConverter;
import br.com.webpublico.util.PercentualConverter;
import br.com.webpublico.util.anotacoes.Tabelavel;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * @author Peixe
 * @since 18/08/2014
 */
@ManagedBean
@ViewScoped
public class PesquisaGenericaLancamentoFPControlador extends PesquisaGenericaRHControlador implements Serializable {
    public PesquisaGenericaLancamentoFPControlador() {
        setNomeVinculo("obj.vinculoFP");
    }

    @Override
    public String preencherCampo(Object objeto, AtributoRelatorioGenerico atributo) {
        if (atributo.getField().getName().equals("quantificacao")) {
            atributo.setAlinhamento(Tabelavel.ALINHAMENTO.DIREITA);
            LancamentoFP lanc = (LancamentoFP) objeto;
            if (lanc.getTipoLancamentoFP().isValorEmReais()) {
                return new MoneyConverter().getAsString(null, null, lanc.getQuantificacao());
            }

            if (lanc.getTipoLancamentoFP().isValorEmPercentual()) {
                return new PercentualConverter().getAsString(null, null, lanc.getQuantificacao());
            }
        }
        return super.preencherCampo(objeto, atributo);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void filtrar() {
        String hql = getHqlConsulta();
        String hqlCount = getHqlContador() ;
        executarConsulta(hql, hqlCount);
    }

    @Override
    public String ordenacaoPadrao() {
        return " order by cast(obj.vinculoFP.matriculaFP.matricula as integer) asc, cast(obj.eventoFP.codigo as integer) asc ";
    }

    @Override
    public String getHqlParaTotalDeRegistros() {
        return getHqlContador();
    }

    @Override
    public String getHqlContador() {
        if (getSistemaControlador().getUsuarioCorrente().possuiAcessoTodosVinculosRH()) {
            return super.getHqlContador()+getComplementoQuery();
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
            return super.getHqlConsulta()+getComplementoQuery();
        }

        String complemento = " and " + montaCondicao() + montaOrdenacao();
        return "select obj from LancamentoFP obj" +
                " inner join obj.vinculoFP vin " +
                " inner join vin.lotacaoFuncionals lotacao " +
                getComplementoQueryFiltrandoPelasUnidadesDoUsuario() +
                complemento;
    }
}
