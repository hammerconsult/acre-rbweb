/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * @author peixe
 */
@ManagedBean
@ViewScoped
public class ConcesaoFeriasLicensaPesquisaGenericaControlador extends PesquisaGenericaRHControlador {

    public ConcesaoFeriasLicensaPesquisaGenericaControlador() {
        setNomeVinculo("obj.periodoAquisitivoFL.contratoFP");
    }

    @Override
    public String getHqlConsulta() {
        return "select new br.com.webpublico.entidades.ConcessaoFeriasLicenca(obj.id, obj.periodoAquisitivoFL.contratoFP, obj.dataInicial, obj.dataFinal, obj.dataComunicacao) from " + classe.getSimpleName() + " obj ";
    }

    @Override
    public String getComplementoQuery() {
        return " inner join obj.periodoAquisitivoFL.baseCargo.basePeriodoAquisitivo base " +
                " where base.tipoPeriodoAquisitivo = 'FERIAS' and " + montaCondicao() + montaOrdenacao();
    }
    @Override
    public void getCampos() {
        super.getCampos();
        adicionaItemPesquisaGenerica("Nome Tratamento/Social do Servidor", "obj.periodoAquisitivoFL.contratoFP.matriculaFP.pessoa.nomeTratamento", String.class, Boolean.FALSE);
    }
}
