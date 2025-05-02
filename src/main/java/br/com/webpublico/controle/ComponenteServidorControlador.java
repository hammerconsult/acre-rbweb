package br.com.webpublico.controle;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.negocios.ContratoFPFacade;
import br.com.webpublico.negocios.VinculoFPFacade;
import br.com.webpublico.negocios.VinculosFPPermitidosPorUsuarioFacade;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 08/05/15
 * Time: 12:53
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@SessionScoped
public class ComponenteServidorControlador implements Serializable {

    @EJB
    private ContratoFPFacade contratoFPFacade;
    private ContratoFP selecionado;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private VinculosFPPermitidosPorUsuarioFacade vinculosFPPermitidosPorUsuarioFacade;

    public ComponenteServidorControlador() {
    }

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    public List<Object[]> completaContrato(String s) {
        return vinculosFPPermitidosPorUsuarioFacade.buscarVinculosPermitidosParaUsuarioLogado(s.trim(), ContratoFP.class);
    }

    public ContratoFP getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(ContratoFP selecionado) {
        this.selecionado = selecionado;
    }

    public void naoFazNada() {

    }

    public void actionListener(SelectEvent evt) {
        this.selecionado = ((ContratoFP) evt.getObject());
    }
}
