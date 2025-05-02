/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * @author Paschualleto
 */
@ManagedBean
@ViewScoped
public class EventoContabilPesquisaGenerico extends ComponentePesquisaGenerico implements Serializable {

//    @Override
//    public String getHqlConsulta() {
//        return " select new br.com.webpublico.entidades.EventoContabil "
//                + " ( obj.id, obj.codigo , obj.descricao, obj.tipoEventoContabil , obj.tipoLancamento , obj.tipoBalancete ,obj.inicioVigencia ,obj.fimVigencia) "
//                + " from " + classe.getSimpleName() + " obj ";
//    }

}
