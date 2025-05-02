/*
 * Codigo gerado automaticamente em Wed Mar 30 15:33:07 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Termo;
import br.com.webpublico.entidades.Texto;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TermoFacade;
import br.com.webpublico.util.EntidadeMetaData;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;

@ManagedBean
@SessionScoped
public class TermoControlador extends SuperControladorCRUD implements Serializable {

    @EJB
    private TermoFacade termoFacade;
    private Texto texto = new Texto();

    public TermoControlador() {
        metadata = new EntidadeMetaData(Termo.class);
    }

    public TermoFacade getFacade() {
        return termoFacade;
    }

    public Texto getTexto() {
        return texto;
    }

    public void setTexto(Texto texto) {
        this.texto = texto;
    }

    @Override
    public void novo() {
        super.novo();
        texto = new Texto();
    }

    @Override
    public String salvar() {
        ((Termo) selecionado).setTexto(texto);
        return super.salvar();
    }

    @Override
    public AbstractFacade getFacede() {
        return termoFacade;
    }
}
