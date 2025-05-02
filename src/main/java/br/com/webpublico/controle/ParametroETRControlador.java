package br.com.webpublico.controle;

import br.com.webpublico.entidades.ParametroETR;
import br.com.webpublico.entidades.ParametroETRAceite;
import br.com.webpublico.entidades.ParametroETRFormulario;
import br.com.webpublico.entidades.ParametroETRFormularioCampoDispensa;
import br.com.webpublico.entidades.comum.Formulario;
import br.com.webpublico.entidades.comum.FormularioCampo;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.ParametroETRFacade;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {@URLMapping(
        id = "parametroETR",
        pattern = "/tributario/parametro-etr/",
        viewId = "/faces/tributario/cadastromunicipal/licenciamentoetr/parametroetr/edita.xhtml")})
public class ParametroETRControlador extends PrettyControlador<ParametroETR> implements CRUD {

    @EJB
    private ParametroETRFacade facade;
    private ParametroETRFormulario parametroETRFormulario;
    private ParametroETRFormularioCampoDispensa parametroETRFormularioCampoDispensa;
    private ParametroETRAceite parametroETRAceite;

    @Override
    public ParametroETRFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/parametro-etr/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public ParametroETRFormulario getParametroETRFormulario() {
        return parametroETRFormulario;
    }

    public void setParametroETRFormulario(ParametroETRFormulario parametroETRFormulario) {
        this.parametroETRFormulario = parametroETRFormulario;
    }

    public ParametroETRFormularioCampoDispensa getParametroETRFormularioCampoDispensa() {
        return parametroETRFormularioCampoDispensa;
    }

    public void setParametroETRFormularioCampoDispensa(ParametroETRFormularioCampoDispensa parametroETRFormularioCampoDispensa) {
        this.parametroETRFormularioCampoDispensa = parametroETRFormularioCampoDispensa;
    }

    public ParametroETRAceite getParametroETRAceite() {
        return parametroETRAceite;
    }

    public void setParametroETRAceite(ParametroETRAceite parametroETRAceite) {
        this.parametroETRAceite = parametroETRAceite;
    }

    @URLAction(mappingId = "parametroETR", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void parametroETR() {
        selecionado = facade.recuperarParametroETR();
        if (selecionado == null) {
            operacao = Operacoes.NOVO;
            selecionado = new ParametroETR();
        } else {
            operacao = Operacoes.EDITAR;
        }
        parametroETRAceite = new ParametroETRAceite();
    }

    @Override
    public void redireciona() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao());
    }

    public void novoParametroEtrFormulario() {
        parametroETRFormulario = new ParametroETRFormulario();
    }

    public void cancelarParametroEtrFormulario() {
        if (parametroETRFormulario.getParametroETR() != null) {
            selecionado.getParametroETRFormularioList().add(parametroETRFormulario);
        }
        parametroETRFormulario = null;
    }

    public void salvarParametroEtrFormulario() {
        try {
            parametroETRFormulario.validarCamposObrigatorios();
            parametroETRFormulario.setParametroETR(selecionado);
            selecionado.getParametroETRFormularioList().add(parametroETRFormulario);
            parametroETRFormulario = new ParametroETRFormulario();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    public void alterarParametroEtrFormulario(ParametroETRFormulario parametroETRFormulario) {
        this.parametroETRFormulario = parametroETRFormulario;
        removerParametroEtrFormulario(parametroETRFormulario);
        recuperarFormulario();
    }

    public void removerParametroEtrFormulario(ParametroETRFormulario parametroETRFormulario) {
        selecionado.getParametroETRFormularioList().remove(parametroETRFormulario);
    }

    public List<SelectItem> selectItemFormularioCampo() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (FormularioCampo formularioCampo : parametroETRFormulario.getFormulario().getFormularioCampoList()) {
            if (formularioCampo.getTipoComponente().getDefineOpcoes())
                toReturn.add(new SelectItem(formularioCampo, formularioCampo.getTitulo()));
        }
        return toReturn;
    }

    public void salvarParametroEtrFormularioCampoDispensa() {
        try {
            parametroETRFormularioCampoDispensa.validarCamposObrigatorios();
            parametroETRFormularioCampoDispensa.setParametroETRFormulario(parametroETRFormulario);
            parametroETRFormulario.getFormularioCampoDispensaList().add(parametroETRFormularioCampoDispensa);
            parametroETRFormularioCampoDispensa = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    public void cancelarParametroEtrFormularioCampoDispensa() {
        parametroETRFormularioCampoDispensa = null;
    }

    public void novoParametroEtrFormularioCampoDispensa() {
        parametroETRFormularioCampoDispensa = new ParametroETRFormularioCampoDispensa();
    }

    public void alterarParametroEtrFormularioCampoDispensa(ParametroETRFormularioCampoDispensa parametroETRFormularioCampoDispensa) {
        this.parametroETRFormularioCampoDispensa = parametroETRFormularioCampoDispensa;
        removerParametroEtrFormularioCampoDispensa(parametroETRFormularioCampoDispensa);
        recuperarFormularioCampo();
    }

    public void removerParametroEtrFormularioCampoDispensa(ParametroETRFormularioCampoDispensa parametroETRFormularioCampoDispensa) {
        this.parametroETRFormulario.getFormularioCampoDispensaList().remove(parametroETRFormularioCampoDispensa);
    }

    public void mudouFormulario() {
        recuperarFormulario();
    }

    private void recuperarFormulario() {
        if (this.parametroETRFormulario.getFormulario() != null) {
            Formulario formulario = facade.getFormularioFacade().recuperar(this.parametroETRFormulario.getFormulario().getId());
            this.parametroETRFormulario.setFormulario(formulario);
        }
    }

    public void mudouFormularioCampo() {
        recuperarFormularioCampo();
    }

    private void recuperarFormularioCampo() {
        if (this.parametroETRFormularioCampoDispensa.getFormularioCampo() != null) {
            FormularioCampo formularioCampo = facade.getFormularioCampoFacade().recuperar(this.parametroETRFormularioCampoDispensa.getFormularioCampo().getId());
            this.parametroETRFormularioCampoDispensa.setFormularioCampo(formularioCampo);
        }
    }

    public Date getDataAtual() {
        return new Date();
    }

    public void adicionarParametroETRAceite() {
        try {
            validarParametroETRAceite(parametroETRAceite);
            parametroETRAceite.setParametroETR(selecionado);
            selecionado.getAceiteList().add(parametroETRAceite);
            parametroETRAceite = new ParametroETRAceite();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    private void validarParametroETRAceite(ParametroETRAceite parametroETRAceite) {
        parametroETRAceite.realizarValidacoes();
        if (selecionado.hasUnidadeAdministrativa(parametroETRAceite.getUnidadeOrganizacional())) {
            throw new ValidacaoException("Unidade Administrativa já está adicionada.");
        }
    }

    public void removerParametroEtrAceite(ParametroETRAceite parametroETRAceite) {
        selecionado.getAceiteList().remove(parametroETRAceite);
    }

}
