package br.com.webpublico.controle;

import br.com.webpublico.entidades.Contrato;
import br.com.webpublico.entidades.PublicacaoContrato;
import br.com.webpublico.entidades.VeiculoDePublicacao;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.PublicacaoContratoFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-publicacao-contrato", pattern = "/publicacao-contrato/novo/", viewId = "/faces/administrativo/contrato/publicacao-contrato/edita.xhtml"),
    @URLMapping(id = "ver-publicacao-contrato", pattern = "/publicacao-contrato/ver/#{publicacaoContratoControlador.id}/", viewId = "/faces/administrativo/contrato/publicacao-contrato/visualizar.xhtml"),
    @URLMapping(id = "editar-publicacao-contrato", pattern = "/publicacao-contrato/editar/#{publicacaoContratoControlador.id}/", viewId = "/faces/administrativo/contrato/publicacao-contrato/edita.xhtml"),
    @URLMapping(id = "listar-publicacao-contrato", pattern = "/publicacao-contrato/listar/", viewId = "/faces/administrativo/contrato/publicacao-contrato/lista.xhtml")
})
public class PublicacaoContratoControlador extends PrettyControlador<PublicacaoContrato> implements Serializable, CRUD {

    @EJB
    private PublicacaoContratoFacade facade;

    public PublicacaoContratoControlador() {
        super(PublicacaoContrato.class);
    }

    @Override
    @URLAction(mappingId = "novo-publicacao-contrato", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "ver-publicacao-contrato", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "editar-publicacao-contrato", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    public List<VeiculoDePublicacao> completarVeiculosDePublicacao(String filtro) {
        return facade.getVeiculoDePublicacaoFacade().listaFiltrando(filtro.trim(), "nome");
    }

    public List<Contrato> completarContratos(String filtro) {
        return facade.getContratoFacade().buscarContratos(filtro.trim());
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/publicacao-contrato/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
