package br.com.webpublico.controle;

import br.com.webpublico.entidades.FatorDeRisco;
import br.com.webpublico.entidades.Risco;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.FatorDeRiscoFacade;
import br.com.webpublico.negocios.RiscoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by carlos on 17/06/15.
 */
@ManagedBean(name = "fatorDeRiscoControlador")
@ViewScoped
@URLMappings(
        mappings = {
                @URLMapping(id = "listarFatorDeRisco", pattern = "/fator-risco/listar/", viewId = "/faces/rh/administracaodepagamento/fatorrisco/lista.xhtml"),
                @URLMapping(id = "criarFatorDeRisco", pattern = "/fator-risco/novo/", viewId = "/faces/rh/administracaodepagamento/fatorrisco/edita.xhtml"),
                @URLMapping(id = "editarFatorDeRisco", pattern = "/fator-risco/editar/#{fatorDeRiscoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/fatorrisco/edita.xhtml"),
                @URLMapping(id = "verFatorDeRisco", pattern = "/fator-risco/ver/#{fatorDeRiscoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/fatorrisco/visualizar.xhtml")
        }
)
public class FatorDeRiscoControlador extends PrettyControlador<FatorDeRisco> implements CRUD {

    @EJB
    private FatorDeRiscoFacade fatorDeRiscoFacade;
    @EJB
    private RiscoFacade riscoFacade;

    private Risco risco;

    @Override
    public String getCaminhoPadrao() {
        return "/fator-risco/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return fatorDeRiscoFacade;
    }

    @Override
    @URLAction(mappingId = "criarFatorDeRisco", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "verFatorDeRisco", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "editarFatorDeRisco", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    public FatorDeRiscoControlador() {
        super(FatorDeRisco.class);
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            super.salvar();
        }
    }

    public Boolean validaCampos() {
        boolean valida = true;

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

        for (Risco riscos : riscoFacade.listaRiscos()) {
            tipo.add(new SelectItem(riscos, riscos.getDescricao()));
        }
        return tipo;
    }
//============================================ Getters e Setters =======================================================

    public Risco getRisco() {
        return risco;
    }

    public void setRisco(Risco risco) {
        this.risco = risco;
    }
}
