package br.com.webpublico.controle.contabil;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.ConfiguracaoAnexosRREO;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.contabil.ConfiguracaoAnexosRREOFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-configuracao-anexos-rreo", pattern = "/configuracao-anexos-rreo/novo/", viewId = "/faces/financeiro/configuracao-anexos/edita.xhtml"),
    @URLMapping(id = "edita-configuracao-anexos-rreo", pattern = "/configuracao-anexos-rreo/editar/#{configuracaoAnexosRREOControlador.id}/", viewId = "/faces/financeiro/configuracao-anexos/edita.xhtml"),
    @URLMapping(id = "listar-configuracao-anexos-rreo", pattern = "/configuracao-anexos-rreo/listar/", viewId = "/faces/financeiro/configuracao-anexos/lista.xhtml"),
    @URLMapping(id = "ver-configuracao-anexos-rreo", pattern = "/configuracao-anexos-rreo/ver/#{configuracaoAnexosRREOControlador.id}/", viewId = "/faces/financeiro/configuracao-anexos/visualizar.xhtml")
})
public class ConfiguracaoAnexosRREOControlador extends PrettyControlador<ConfiguracaoAnexosRREO> implements Serializable, CRUD {

    @EJB
    private ConfiguracaoAnexosRREOFacade facade;

    public ConfiguracaoAnexosRREOControlador() {
        super(ConfiguracaoAnexosRREO.class);
    }

    @URLAction(mappingId = "novo-configuracao-anexos-rreo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
    }

    @URLAction(mappingId = "edita-configuracao-anexos-rreo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "ver-configuracao-anexos-rreo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            if (isOperacaoNovo()) {
                facade.salvarNovo(selecionado);
            } else {
                facade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    private void validarCampos() {
        Util.validarCampos(selecionado);
        ValidacaoException ve = new ValidacaoException();
        if (facade.hasConfiguracaoExistente(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe uma configuração para o exercício " + selecionado.getExercicio().getAno() + ".");
        }
        ve.lancarException();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-anexos-rreo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
