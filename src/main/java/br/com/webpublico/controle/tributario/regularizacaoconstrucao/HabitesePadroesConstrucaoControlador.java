package br.com.webpublico.controle.tributario.regularizacaoconstrucao;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.HabiteseGruposConstrucao;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.HabitesePadroesConstrucao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.tributario.regularizacaoconstrucao.HabiteseGruposConstrucaoFacade;
import br.com.webpublico.negocios.tributario.regularizacaoconstrucao.HabitesePadroesConstrucaoFacade;
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


@ManagedBean(name = "habitesePadroesConstrucaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoHabitesePadroesConstrucao", pattern = "/regularizacao-construcao/habitese-padroes-constucao/novo/", viewId = "/faces/tributario/regularizacaoconstrucao/habitesepadroesconstrucao/edita.xhtml"),
    @URLMapping(id = "editarHabitesePadroesConstrucao", pattern = "/regularizacao-construcao/habitese-padroes-constucao/editar/#{habitesePadroesConstrucaoControlador.id}/", viewId = "/faces/tributario/regularizacaoconstrucao/habitesepadroesconstrucao/edita.xhtml"),
    @URLMapping(id = "listarHabitesePadroesConstrucao", pattern = "/regularizacao-construcao/habitese-padroes-constucao/listar/", viewId = "/faces/tributario/regularizacaoconstrucao/habitesepadroesconstrucao/lista.xhtml"),
    @URLMapping(id = "verHabitesePadroesConstrucao", pattern = "/regularizacao-construcao/habitese-padroes-constucao/ver/#{habitesePadroesConstrucaoControlador.id}/", viewId = "/faces/tributario/regularizacaoconstrucao/habitesepadroesconstrucao/visualizar.xhtml")
})

public class HabitesePadroesConstrucaoControlador extends PrettyControlador<HabitesePadroesConstrucao> implements Serializable, CRUD {

    @EJB
    private HabitesePadroesConstrucaoFacade habitesePadroesConstrucaoFacade;
    @EJB
    private HabiteseGruposConstrucaoFacade habiteseGruposConstrucaoFacade;
    private ConverterAutoComplete converterHabiteseGrupos;

    public HabitesePadroesConstrucaoControlador() {
        super(HabitesePadroesConstrucao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return habitesePadroesConstrucaoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/regularizacao-construcao/habitese-padroes-constucao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoHabitesePadroesConstrucao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verHabitesePadroesConstrucao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarHabitesePadroesConstrucao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    public List<HabiteseGruposConstrucao> completarHabiteseGrupos(String parte) {
        try {
            return habiteseGruposConstrucaoFacade.listarFiltrando(parte.trim());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            return new ArrayList<>();
        }
    }

    public Converter getConverterHabiteseGrupos() {
        if (converterHabiteseGrupos == null) {
            converterHabiteseGrupos = new ConverterAutoComplete(HabiteseGruposConstrucao.class, habiteseGruposConstrucaoFacade);
        }
        return converterHabiteseGrupos;
    }
}
