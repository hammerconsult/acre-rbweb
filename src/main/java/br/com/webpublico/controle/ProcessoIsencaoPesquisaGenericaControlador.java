/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.util.EntidadeMetaData;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

/**
 * @author cheles
 */
@ManagedBean
@ViewScoped
public class ProcessoIsencaoPesquisaGenericaControlador extends ComponentePesquisaGenerico {

    @Override
    public String nomeDaClasse() {
        return "Solicitação de Processo de Isenção de IPTU";
    }

    @Override
    public String getHqlConsulta() {
        return "select obj  from IsencaoCadastroImobiliario obj ";
    }

    @Override
    public String getHqlContador() {
        return "select count(obj.id)  from IsencaoCadastroImobiliario obj ";
    }

    @Override
    public String getComplementoQuery() {
        return " inner join obj.processoIsencaoIPTU where " + montaCondicao() + montaOrdenacao();
    }

}
