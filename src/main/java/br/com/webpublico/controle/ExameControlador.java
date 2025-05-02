package br.com.webpublico.controle;

import br.com.webpublico.entidades.Cargo;
import br.com.webpublico.entidades.Exame;
import br.com.webpublico.entidades.Risco;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CargoFacade;
import br.com.webpublico.negocios.ExameFacade;
import br.com.webpublico.negocios.RiscoFacade;
import br.com.webpublico.singletons.SingletonGeradorCodigoRH;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by carlos on 18/06/15.
 */
@ManagedBean(name = "exameControlador")
@ViewScoped
@URLMappings(
        mappings = {
                @URLMapping(id = "listarExame", pattern = "/exame/listar/", viewId = "/faces/rh/administracaodepagamento/exame/lista.xhtml"),
                @URLMapping(id = "criarExame", pattern = "/exame/novo/", viewId = "/faces/rh/administracaodepagamento/exame/edita.xhtml"),
                @URLMapping(id = "editarExame", pattern = "/exame/editar/#{exameControlador.id}/", viewId = "/faces/rh/administracaodepagamento/exame/edita.xhtml"),
                @URLMapping(id = "verExame", pattern = "/exame/ver/#{exameControlador.id}/", viewId = "/faces/rh/administracaodepagamento/exame/visualizar.xhtml")
        }
)
public class ExameControlador extends PrettyControlador<Exame> implements CRUD {

    @EJB
    private ExameFacade exameFacade;


    public ExameControlador() {
        super(Exame.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/exame/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return exameFacade;
    }

    @Override
    @URLAction(mappingId = "criarExame", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "verExame", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "editarExame", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        selecionado.setCodigo(exameFacade.getSingletonGeradorCodigoRH().gerarCodigoExame());
        if (validaCampos()) {
            super.salvar();
        }
    }

    public Boolean validaCampos() {
        boolean valida = true;

        if (selecionado.getRisco() == null) {
            FacesUtil.addCampoObrigatorio("Selecione um Risco.");
            valida = false;
        }

        if (selecionado.getCargo() == null) {
            FacesUtil.addCampoObrigatorio("O Cargo é um campo obrigatório.");
            valida = false;
        }

        if (selecionado.getCodigo() == null) {
            FacesUtil.addCampoObrigatorio("O Código é um campo obrigatório.");
            valida = false;
        }

        if (selecionado.getDescricao() == null || selecionado.getDescricao().trim().equals("")) {
            FacesUtil.addCampoObrigatorio("A Descrição é um campo obrigatório.");
            valida = false;
        }

        return valida;
    }

    public List<SelectItem> tipoDeRiscoSelectMenu() {
        List<SelectItem> tipo = new ArrayList<>();
        tipo.add(new SelectItem(null, ""));

        for (Risco riscos : exameFacade.getRiscoFacade().listaRiscos()) {
            tipo.add(new SelectItem(riscos, riscos.getDescricao()));
        }

        return tipo;
    }

    public List<Cargo> completaCargo(String parte) {
        return exameFacade.getCargoFacade().buscarCargosVigentesPorUnidadeOrganizacionalAndUsuario(parte);
    }

}
