package br.com.webpublico.controle;

import br.com.webpublico.entidades.ParametroAlvaraImediato;
import br.com.webpublico.entidades.ParametroAlvaraImediatoCoibicao;
import br.com.webpublico.entidades.comum.Formulario;
import br.com.webpublico.entidades.comum.FormularioCampo;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.ServicoConstrucao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.ParametroAlvaraImediatoFacade;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(
                id = "novoParametroAlvaraImediato",
                pattern = "/cadastro-imobiliario/alvara-imediato/parametro/novo/",
                viewId = "/faces/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/parametro/edita.xhtml"),
        @URLMapping(
                id = "editarParametroAlvaraImediato",
                pattern = "/cadastro-imobiliario/alvara-imediato/parametro/editar/#{parametroAlvaraImediatoControlador.id}/",
                viewId = "/faces/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/parametro/edita.xhtml"),
        @URLMapping(
                id = "verParametroAlvaraImediato",
                pattern = "/cadastro-imobiliario/alvara-imediato/parametro/ver/#{parametroAlvaraImediatoControlador.id}/",
                viewId = "/faces/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/parametro/visualiza.xhtml"),
        @URLMapping(
                id = "listarParametroAlvaraImediato",
                pattern = "/cadastro-imobiliario/alvara-imediato/parametro/listar/",
                viewId = "/faces/tributario/cadastromunicipal/cadastroimobiliario/alvaraimediato/parametro/lista.xhtml")
})
public class ParametroAlvaraImediatoControlador extends PrettyControlador<ParametroAlvaraImediato> implements CRUD {

    @EJB
    private ParametroAlvaraImediatoFacade facade;
    private List<FormularioCampo> campos;
    private ParametroAlvaraImediatoCoibicao parametroAlvaraImediatoCoibicao;

    public ParametroAlvaraImediatoControlador() {
        super(ParametroAlvaraImediato.class);
    }

    @Override
    public ParametroAlvaraImediatoFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/cadastro-imobiliario/alvara-imediato/parametro/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<FormularioCampo> getCampos() {
        return campos;
    }

    public void setCampos(List<FormularioCampo> campos) {
        this.campos = campos;
    }

    public ParametroAlvaraImediatoCoibicao getParametroAlvaraImediatoCoibicao() {
        return parametroAlvaraImediatoCoibicao;
    }

    public void setParametroAlvaraImediatoCoibicao(ParametroAlvaraImediatoCoibicao parametroAlvaraImediatoCoibicao) {
        this.parametroAlvaraImediatoCoibicao = parametroAlvaraImediatoCoibicao;
    }

    @URLAction(mappingId = "novoParametroAlvaraImediato", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editarParametroAlvaraImediato", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        carregarCampos();
    }

    @URLAction(mappingId = "verParametroAlvaraImediato", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    public void mudouExercicio() {
        selecionado.setServicoConstrucao(null);
    }

    public List<ServicoConstrucao> completarServicoConstrucao(String parte) {
        if (selecionado.getExercicio() == null) {
            return Lists.newArrayList();
        }
        return facade.getServicoConstrucaoFacade().listarFiltrando(parte);
    }

    public void mudouFormulario() {
        selecionado.setCoibicoes(Lists.newArrayList());
        carregarCampos();
    }

    private void carregarCampos() {
        if (selecionado.getFormulario() != null) {
            Formulario formulario = facade.getFormularioFacade().recuperar(selecionado.getFormulario().getId());
            campos = formulario.getFormularioCampoList();
        }
    }

    public void mudouFormularioCampo() {
        if (parametroAlvaraImediatoCoibicao != null) {
            parametroAlvaraImediatoCoibicao.setFormularioCampo(facade.getFormularioCampoFacade()
                    .recuperar(parametroAlvaraImediatoCoibicao.getFormularioCampo().getId()));
        }
    }

    public void salvarCoibicao() {
        try {
            parametroAlvaraImediatoCoibicao.validarCamposObrigatorios();
            parametroAlvaraImediatoCoibicao.setParametro(selecionado);
            selecionado.getCoibicoes().add(parametroAlvaraImediatoCoibicao);
            parametroAlvaraImediatoCoibicao = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    public void cancelarCoibicao() {
        parametroAlvaraImediatoCoibicao = null;
    }

    public void novaCoibicao() {
        parametroAlvaraImediatoCoibicao = new ParametroAlvaraImediatoCoibicao();
    }

    public void alterarCoibicao(ParametroAlvaraImediatoCoibicao parametroAlvaraImediatoCoibicao) {
        selecionado.getCoibicoes().remove(parametroAlvaraImediatoCoibicao);
        this.parametroAlvaraImediatoCoibicao = parametroAlvaraImediatoCoibicao;
    }

    public void removerCoibicao(ParametroAlvaraImediatoCoibicao parametroAlvaraImediatoCoibicao) {
        selecionado.getCoibicoes().remove(parametroAlvaraImediatoCoibicao);
    }
}
