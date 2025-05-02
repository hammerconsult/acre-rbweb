package br.com.webpublico.controle;


import br.com.webpublico.entidades.ConfiguracaoExcecaoDespesaContrato;
import br.com.webpublico.entidades.Conta;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfiguracaoExcecaoDespesaContratoFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-configuracao-excecao-despesa-contrato", pattern = "/configuracao-excecao-despesa-contrato/novo/", viewId = "/faces/financeiro/configuracao-excecao-despesa-contrato/edita.xhtml"),
    @URLMapping(id = "editar-configuracao-excecao-despesa-contrato", pattern = "/configuracao-excecao-despesa-contrato/editar/#{configuracaoExcecaoDespesaContratoControlador.id}/", viewId = "/faces/financeiro/configuracao-excecao-despesa-contrato/edita.xhtml"),
    @URLMapping(id = "ver-configuracao-excecao-despesa-contrato", pattern = "/configuracao-excecao-despesa-contrato/ver/#{configuracaoExcecaoDespesaContratoControlador.id}/", viewId = "/faces/financeiro/configuracao-excecao-despesa-contrato/visualizar.xhtml"),
    @URLMapping(id = "listar-configuracao-excecao-despesa-contrato", pattern = "/configuracao-excecao-despesa-contrato/listar/", viewId = "/faces/financeiro/configuracao-excecao-despesa-contrato/lista.xhtml")
})
public class ConfiguracaoExcecaoDespesaContratoControlador extends PrettyControlador<ConfiguracaoExcecaoDespesaContrato> implements Serializable, CRUD {

    @EJB
    private ConfiguracaoExcecaoDespesaContratoFacade facade;

    public ConfiguracaoExcecaoDespesaContratoControlador() {
        super(ConfiguracaoExcecaoDespesaContrato.class);
    }

    @URLAction(mappingId = "novo-configuracao-excecao-despesa-contrato", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setInicioVigencia(facade.getSistemaFacade().getDataOperacao());
    }

    @URLAction(mappingId = "editar-configuracao-excecao-despesa-contrato", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "ver-configuracao-excecao-despesa-contrato", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
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
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    private void validarCampos() {
        Util.validarCampos(selecionado);
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getFimVigencia() != null && selecionado.getFimVigencia().before(selecionado.getInicioVigencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Fim de Vigência deve ser superior ao Início de Vigência.");
        }
        if (facade.hasConfiguracaoVigente(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe uma configuração vigente para a Conta de Despesa " + selecionado.getContaDespesa());
        }
        ve.lancarException();
    }

    public List<Conta> completarContasDeDespesa(String filtro) {
        return facade.getContaFacade().listaFiltrandoContaDespesa(filtro, facade.getSistemaFacade().getExercicioCorrente());
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-excecao-despesa-contrato/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
