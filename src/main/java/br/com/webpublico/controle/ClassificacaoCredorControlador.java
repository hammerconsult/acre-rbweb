/*
 * Codigo gerado automaticamente em Thu Apr 05 08:46:51 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ClassificacaoCredor;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ClassificacaoCredorFacade;
import br.com.webpublico.util.EntidadeMetaData;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;

@ManagedBean
@SessionScoped
public class ClassificacaoCredorControlador extends SuperControladorCRUD implements Serializable {

    @EJB
    private ClassificacaoCredorFacade classificacaoCredorFacade;

    public ClassificacaoCredorControlador() {
        metadata = new EntidadeMetaData(ClassificacaoCredor.class);
    }

    public ClassificacaoCredorFacade getFacade() {
        return classificacaoCredorFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return classificacaoCredorFacade;
    }
}
