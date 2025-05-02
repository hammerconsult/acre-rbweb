/*
 * Codigo gerado automaticamente em Wed Mar 23 09:14:21 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.SetorFacade;
import br.com.webpublico.util.*;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "setorControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoSetor", pattern = "/tributario/setor/novo/", viewId = "/faces/tributario/cadastromunicipal/setor/edita.xhtml"),
        @URLMapping(id = "editarSetor", pattern = "/tributario/setor/editar/#{setorControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/setor/edita.xhtml"),
        @URLMapping(id = "listarSetor", pattern = "/tributario/setor/listar/", viewId = "/faces/tributario/cadastromunicipal/setor/lista.xhtml"),
        @URLMapping(id = "verSetor", pattern = "/tributario/setor/ver/#{setorControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/setor/visualizar.xhtml")
})
public class SetorControlador extends PrettyControlador<Setor> implements Serializable, CRUD {

    @EJB
    private SetorFacade setorFacade;
    Converter converterDistrito;
    private ConfiguracaoTributario conf;

    public SetorControlador() {
        super(Setor.class);
    }

    public ConfiguracaoTributario getConf() {
        return conf;
    }

    public Converter getConverterDistrito() {
        if (converterDistrito == null) {
            converterDistrito = new ConverterGenerico(Distrito.class, setorFacade.getDistritoFacade());
        }
        return converterDistrito;
    }

    public List<SelectItem> getDistritos() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (Distrito obj : setorFacade.getDistritoFacade().lista()) {
            toReturn.add(new SelectItem(obj, obj.getCodigo() + " - " + obj.getNome()));
        }
        return toReturn;
    }

    @URLAction(mappingId = "novoSetor", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        conf = setorFacade.getConfiguracaoTributarioFacade().retornaUltimo();

        if (conf != null) {
            super.novo();
        } else {
            cancelar();
            FacesUtil.addWarn("Configuração não encontrada!", "É necessário cadastrar uma Configuração do Tributário.");
        }
    }

    @URLAction(mappingId = "verSetor", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarSetor", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        conf = setorFacade.getConfiguracaoTributarioFacade().retornaUltimo();

        if (conf != null) {
            super.editar();
        } else {
            cancelar();
            FacesUtil.addWarn("Configuração não encontrada!", "É necessário cadastrar uma Configuração do Tributário.");
        }
    }

    @Override
    public void salvar() {
        if (valida()) {
            super.salvar();
        }
    }

    private boolean valida() {
        boolean retorno = true;

        if (selecionado.getDistrito() == null) {
            retorno = false;
            FacesUtil.addWarn("Atenção!", "O Distrito é um campo obrigatório.");
        } else if ((selecionado.getNome() != null && selecionado.getCodigo() != null && selecionado.getDistrito() != null) && setorFacade.existeEsteSetorNoDistrito(selecionado)) {
            retorno = false;
            FacesUtil.addWarn("Atenção!", "Já existe este Setor no Distrito selecionado.");
        }

        if (selecionado.getCodigo() == null || selecionado.getCodigo().trim().equals("")) {
            retorno = false;
            FacesUtil.addWarn("Atenção!", "O Código do Setor é um campo obrigatório.");
        } else if (selecionado.getDistrito() != null && setorFacade.existeSetorComMesmoCodigo(selecionado)) {
            retorno = false;
            FacesUtil.addWarn("Atenção!", "O Código " + selecionado.getCodigo() + " já está sendo usado.");
        } else if (selecionado.getCodigo().replaceAll(" ", "").length() < conf.getNumDigitosSetor()) {
            retorno = false;
            FacesUtil.addWarn("Código inválido.", "A configuração vigente exige um código com " + conf.getNumDigitosSetor() + " dígitos.");
        }

        if (selecionado.getNome() == null || selecionado.getNome().trim().equals("")) {
            retorno = false;
            FacesUtil.addWarn("Atenção!", "O Nome do Setor é um campo obrigatório.");
        } else if (setorFacade.existeSetorComMesmoNome(getSelecionado().getId(), selecionado.getNome())) {
            retorno = false;
            FacesUtil.addWarn("Atenção!", "O Setor " + selecionado.getNome() + " já existe.");
        }


        return retorno;
    }

    @Override
    public AbstractFacade getFacede() {
        return setorFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/setor/";
    }

    public SetorFacade getFacade() {
        return setorFacade;
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }


}
