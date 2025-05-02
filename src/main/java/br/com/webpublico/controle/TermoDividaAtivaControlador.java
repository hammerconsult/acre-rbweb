/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.TermoInscricaoDividaAtiva;
import br.com.webpublico.entidades.TipoDoctoOficial;
import br.com.webpublico.negocios.TermoInscricaoDividaAtivaFacade;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;

/**
 * @author Claudio
 */
@ManagedBean
@SessionScoped
public class TermoDividaAtivaControlador implements Serializable {

    @EJB
    private TermoInscricaoDividaAtivaFacade termoInscricaoDividaAtivaFacade;
    private TermoInscricaoDividaAtiva selecionado;
    private TipoDoctoOficial tipoDoctoOficial;

    public TermoInscricaoDividaAtiva getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(TermoInscricaoDividaAtiva selecionado) {
        this.selecionado = selecionado;
    }

    public TipoDoctoOficial getTipoDoctoOficial() {
        return tipoDoctoOficial;
    }

    public void setTipoDoctoOficial(TipoDoctoOficial tipoDoctoOficial) {
        this.tipoDoctoOficial = tipoDoctoOficial;
    }

//    public void gerarTermoDeAdvertencia() {
//        if (validaCamposPreenchidosDoTermo()) {
//            try {
//                selecionado.setDocumentoOficial(termoInscricaoDividaAtivaFacade.gera(tipoDoctoOficial, selecionado, sistemaControlador));
//                FacesContext.getCurrentInstance().addMessage("Formulario", new FacesMessage(FacesMessage.SEVERITY_INFO, "Termo de advertência gerado com sucesso!", ""));
//            } catch (UFMException ex) {
//                FacesContext.getCurrentInstance().addMessage("Formulario", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Impossível continuar!", ""));
//
//            }
//        }
//    }

    public boolean validaCamposPreenchidosDoTermo() {
        boolean retorno = true;
        if (selecionado.getNumero() == null || selecionado.getNumero().trim().length() <= 0) {
            retorno = false;
            FacesContext.getCurrentInstance().addMessage("Formulario:mensagensTermoAdvertencia", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao tentar gerar o termo de Advertência.", "O campo número é obrigátorio"));
        }
        return retorno;
    }
}
