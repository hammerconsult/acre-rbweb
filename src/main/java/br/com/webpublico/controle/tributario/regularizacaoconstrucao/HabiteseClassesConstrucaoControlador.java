package br.com.webpublico.controle.tributario.regularizacaoconstrucao;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.HabiteseClassesConstrucao;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.tributario.regularizacaoconstrucao.HabiteseClassesConstrucaoFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;


@ManagedBean(name = "habiteseClassesConstrucaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoHabiteseClassesConstrucao", pattern = "/regularizacao-construcao/habitese-classes-constucao/novo/", viewId = "/faces/tributario/regularizacaoconstrucao/habiteseclassesconstrucao/edita.xhtml"),
    @URLMapping(id = "editarHabiteseClassesConstrucao", pattern = "/regularizacao-construcao/habitese-classes-constucao/editar/#{habiteseClassesConstrucaoControlador.id}/", viewId = "/faces/tributario/regularizacaoconstrucao/habiteseclassesconstrucao/edita.xhtml"),
    @URLMapping(id = "listarHabiteseClassesConstrucao", pattern = "/regularizacao-construcao/habitese-classes-constucao/listar/", viewId = "/faces/tributario/regularizacaoconstrucao/habiteseclassesconstrucao/lista.xhtml"),
    @URLMapping(id = "verHabiteseClassesConstrucao", pattern = "/regularizacao-construcao/habitese-classes-constucao/ver/#{habiteseClassesConstrucaoControlador.id}/", viewId = "/faces/tributario/regularizacaoconstrucao/habiteseclassesconstrucao/visualizar.xhtml")
})

public class HabiteseClassesConstrucaoControlador extends PrettyControlador<HabiteseClassesConstrucao> implements Serializable, CRUD {

    @EJB
    private HabiteseClassesConstrucaoFacade habiteseClassesConstrucaoFacade;

    public HabiteseClassesConstrucaoControlador() {
        super(HabiteseClassesConstrucao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return habiteseClassesConstrucaoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/regularizacao-construcao/habitese-classes-constucao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoHabiteseClassesConstrucao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verHabiteseClassesConstrucao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarHabiteseClassesConstrucao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

}
