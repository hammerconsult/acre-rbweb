package br.com.webpublico.controle.tributario.regularizacaoconstrucao;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.HabiteseClassesConstrucao;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.HabiteseGruposConstrucao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.tributario.regularizacaoconstrucao.HabiteseClassesConstrucaoFacade;
import br.com.webpublico.negocios.tributario.regularizacaoconstrucao.HabiteseGruposConstrucaoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@ManagedBean(name = "habiteseGruposConstrucaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoHabiteseGruposConstrucao", pattern = "/regularizacao-construcao/habitese-grupos-constucao/novo/", viewId = "/faces/tributario/regularizacaoconstrucao/habitesegruposconstrucao/edita.xhtml"),
    @URLMapping(id = "editarHabiteseGruposConstrucao", pattern = "/regularizacao-construcao/habitese-grupos-constucao/editar/#{habiteseGruposConstrucaoControlador.id}/", viewId = "/faces/tributario/regularizacaoconstrucao/habitesegruposconstrucao/edita.xhtml"),
    @URLMapping(id = "listarHabiteseGruposConstrucao", pattern = "/regularizacao-construcao/habitese-grupos-constucao/listar/", viewId = "/faces/tributario/regularizacaoconstrucao/habitesegruposconstrucao/lista.xhtml"),
    @URLMapping(id = "verHabiteseGruposConstrucao", pattern = "/regularizacao-construcao/habitese-grupos-constucao/ver/#{habiteseGruposConstrucaoControlador.id}/", viewId = "/faces/tributario/regularizacaoconstrucao/habitesegruposconstrucao/visualizar.xhtml")
})

public class HabiteseGruposConstrucaoControlador extends PrettyControlador<HabiteseGruposConstrucao> implements Serializable, CRUD {

    @EJB
    private HabiteseGruposConstrucaoFacade habiteseGruposConstrucaoFacade;
    @EJB
    private HabiteseClassesConstrucaoFacade habiteseClassesConstrucaoFacade;
    private ConverterAutoComplete converterHabiteseClasse;

    public HabiteseGruposConstrucaoControlador() {
        super(HabiteseGruposConstrucao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return habiteseGruposConstrucaoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/regularizacao-construcao/habitese-grupos-constucao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoHabiteseGruposConstrucao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verHabiteseGruposConstrucao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarHabiteseGruposConstrucao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    public List<HabiteseClassesConstrucao> completarHabiteseClasses(String parte) {
        try {
            return habiteseClassesConstrucaoFacade.listarFiltrando(parte.trim());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            return new ArrayList<>();
        }
    }

    public Converter getConverterHabiteseClasses() {
        if (converterHabiteseClasse == null) {
            converterHabiteseClasse = new ConverterAutoComplete(HabiteseClassesConstrucao.class, habiteseClassesConstrucaoFacade);
        }
        return converterHabiteseClasse;
    }
}
