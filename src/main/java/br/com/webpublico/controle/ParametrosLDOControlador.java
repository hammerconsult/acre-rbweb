/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;


import br.com.webpublico.entidades.ParametrosLDO;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ParametrosLDOFacade;
import br.com.webpublico.util.ConverterExercicio;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.PercentualConverter;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

/**
 * @author Usuario
 */
@ManagedBean(name = "parametrosLDOControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-parametros-ldo", pattern = "/parametros-ldo/novo/", viewId = "/faces/admin/parametrosldo/edita.xhtml"),
    @URLMapping(id = "editar-parametros-ldo", pattern = "/parametros-ldo/editar/#{parametrosLDOControlador.id}/", viewId = "/faces/admin/parametrosldo/edita.xhtml"),
    @URLMapping(id = "ver-parametros-ldo", pattern = "/parametros-ldo/ver/#{parametrosLDOControlador.id}/", viewId = "/faces/admin/parametrosldo/visualizar.xhtml"),
    @URLMapping(id = "listar-parametros-ldo", pattern = "/parametros-ldo/listar/", viewId = "/faces/admin/parametrosldo/lista.xhtml")
})
public class ParametrosLDOControlador extends PrettyControlador<ParametrosLDO> implements Serializable, CRUD {

    @EJB
    private ParametrosLDOFacade parametrosLDOFacade;
    private PercentualConverter percentualConverter;
    private ConverterExercicio converterExercicio;

    public ParametrosLDOControlador() {
        super(ParametrosLDO.class);
    }

    public String getCaminhoPadrao() {
        return "/parametros-ldo/";
    }

    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return parametrosLDOFacade;
    }

    @URLAction(mappingId = "novo-parametros-ldo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "ver-parametros-ldo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-parametros-ldo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        try {
            Util.validarCampos(selecionado);
            ValidacaoException ve = new ValidacaoException();
            validarDuplicidade(ve);
            validarInflacoesEEstimativa();
            selecionado.setDesde(parametrosLDOFacade.getSistemaFacade().getDataOperacao());
            if (isOperacaoNovo()) {
                parametrosLDOFacade.salvarNovo(selecionado);
            } else {
                parametrosLDOFacade.salvar(selecionado);
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoSalvar()));
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    private void validarInflacoesEEstimativa() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getInflacaoReal().compareTo(0d) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Inflação Real deve ser maior que 0 (ZERO).");
        }
        if (selecionado.getInflacaoProjetada().compareTo(0d) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Inflação Projetada deve ser maior que 0 (ZERO).");
        }
        if (selecionado.getEstimativaCrescimento().compareTo(0d) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Estimativa de Crescimento deve ser maior que 0 (ZERO).");
        }
        ve.lancarException();
    }

    private void validarDuplicidade(ValidacaoException ve) {
        if (parametrosLDOFacade.verificarExisteParametrosLdoPorExercicioAndLDO(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um Parâmetro LDO cadastrado para o Exercício " + selecionado.getExercicio());
        }
        ve.lancarException();
    }

    public List<SelectItem> getListaLdo() {
        return Util.getListSelectItem(parametrosLDOFacade.getLdoFacade().lista());
    }

    public PercentualConverter getPercentualConverter() {
        if (percentualConverter == null) {
            percentualConverter = new PercentualConverter();
        }
        return percentualConverter;
    }

    public ConverterExercicio getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterExercicio(parametrosLDOFacade.getExercicioFacade());
        }
        return converterExercicio;
    }
}
