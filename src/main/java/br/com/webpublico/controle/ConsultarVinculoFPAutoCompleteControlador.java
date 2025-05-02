/*
 * Codigo gerado automaticamente em Fri Jul 13 15:21:46 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.negocios.VinculoFPFacade;
import br.com.webpublico.negocios.VinculosFPPermitidosPorUsuarioFacade;
import br.com.webpublico.util.ConverterAutoComplete;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
public class ConsultarVinculoFPAutoCompleteControlador implements Serializable {

    @EJB
    private VinculosFPPermitidosPorUsuarioFacade vinculosFPPermitidosPorUsuarioFacade;
    private VinculoFP selecionado;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    private String idComponente;
    private String classe;


    private String matricula;
    private String numero;

    private void completarCamposMatriculaNumero(Object obj){
        if (obj == null){
            matricula = "";
            numero = "";
            return;
        }

        String[] partes = obj.toString().split("/");
        matricula = partes[0];
        partes = partes[1].split(" ");
        numero = partes[0];
    }

    public void novo(String id, String classe, Object obj) {
        this.idComponente = id;
        this.classe = classe;
        completarCamposMatriculaNumero(obj);
    }

    public ConverterAutoComplete getConverterAutoComplete(){
        return new ConverterAutoComplete(VinculoFP.class, vinculoFPFacade);
    }

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public VinculoFP getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(VinculoFP selecionado) {
        this.selecionado = selecionado;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public List<Object> completarVinculosFP(String parte) throws ClassNotFoundException {
        Class c = classe != null && !classe.isEmpty() ? Class.forName("br.com.webpublico.entidades."+classe) : null;
        return vinculosFPPermitidosPorUsuarioFacade.buscarVinculosPermitidosParaUsuarioLogado(parte.trim(), c);
    }

    public String pegarIdComponente(){
        return idComponente.replace(":","_").replace("-","_");
    }
}
