package br.com.webpublico.controle.rh.avaliacao;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.rh.avaliacao.NivelResposta;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.rh.avaliacao.NivelRespostaFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "nivelRespostaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-nivel-resposta", pattern = "/avaliacao/nivel-resposta/novo/", viewId = "/faces/rh/avaliacao/nivel-resposta/edita.xhtml"),
    @URLMapping(id = "editar-nivel-resposta", pattern = "/avaliacao/nivel-resposta/editar/#{nivelRespostaControlador.id}/", viewId = "/faces/rh/avaliacao/nivel-resposta/edita.xhtml"),
    @URLMapping(id = "ver-nivel-resposta", pattern = "/avaliacao/nivel-resposta/ver/#{nivelRespostaControlador.id}/", viewId = "/faces/rh/avaliacao/nivel-resposta/visualizar.xhtml"),
    @URLMapping(id = "listar-nivel-resposta", pattern = "/avaliacao/nivel-resposta/listar/", viewId = "/faces/rh/avaliacao/nivel-resposta/lista.xhtml")
})
public class NivelRespostaControlador extends PrettyControlador<NivelResposta> implements Serializable, CRUD {

    @EJB
    private NivelRespostaFacade facade;

    public NivelRespostaControlador() {
        super(NivelResposta.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/avaliacao/nivel-resposta/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-nivel-resposta", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "ver-nivel-resposta", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-nivel-resposta", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

}
