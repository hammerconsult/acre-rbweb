package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfigContaDespTipoPessoa;
import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoPessoaPermitido;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfigContaDespTipoPessoaFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
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
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 11/09/14
 * Time: 14:25
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "configContaDespTipoPessoaControlador")
@ViewScoped

@URLMappings(mappings = {
    @URLMapping(id = "nova-config-contadespesa-tipopessoa", pattern = "/configuracao-contadespesa-tipopessoa/novo/", viewId = "/faces/financeiro/orcamentario/configuracaocontabil/config-contadespesa-tipopessoa/edita.xhtml"),
    @URLMapping(id = "editar-config-contadespesa-tipopessoa", pattern = "/configuracao-contadespesa-tipopessoa/editar/#{configContaDespTipoPessoaControlador.id}/", viewId = "/faces/financeiro/orcamentario/configuracaocontabil/config-contadespesa-tipopessoa/edita.xhtml"),
    @URLMapping(id = "ver-config-contadespesa-tipopessoa", pattern = "/configuracao-contadespesa-tipopessoa/ver/#{configContaDespTipoPessoaControlador.id}/", viewId = "/faces/financeiro/orcamentario/configuracaocontabil/config-contadespesa-tipopessoa/visualizar.xhtml"),
    @URLMapping(id = "listar-config-contadespesa-tipopessoa", pattern = "/configuracao-contadespesa-tipopessoa/listar/", viewId = "/faces/financeiro/orcamentario/configuracaocontabil/config-contadespesa-tipopessoa/lista.xhtml")
})

public class ConfigContaDespTipoPessoaControlador extends PrettyControlador<ConfigContaDespTipoPessoa> implements Serializable, CRUD {

    @EJB
    private ConfigContaDespTipoPessoaFacade configContaDespTipoPessoaFacade;
    private ConverterAutoComplete converterContaDespesa;

    public ConfigContaDespTipoPessoaControlador() {
        super(ConfigContaDespTipoPessoa.class);
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return configContaDespTipoPessoaFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-contadespesa-tipopessoa/";
    }

    @Override
    @URLAction(mappingId = "nova-config-contadespesa-tipopessoa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setTipoPessoa(TipoPessoaPermitido.PESSOAFISICA);
        selecionado.setExercicio(getSistemaControlador().getExercicioCorrente());
    }

    private SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    @Override
    @URLAction(mappingId = "ver-config-contadespesa-tipopessoa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "editar-config-contadespesa-tipopessoa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCampos(selecionado);
        if (configContaDespTipoPessoaFacade.verificarConfiguracaoVigente(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" Já existe uma configuração vigente utilizando o tipo de pessoa: " + selecionado.getTipoPessoa() + " e conta: " + selecionado.getContaDespesa() + " vigente para " + selecionado.getExercicio() + ".");
        }
        ve.lancarException();
    }


    public ConverterAutoComplete getConverterContaDespesa() {
        if (converterContaDespesa == null) {
            converterContaDespesa = new ConverterAutoComplete(Conta.class, configContaDespTipoPessoaFacade.getContaFacade());
        }
        return converterContaDespesa;
    }

    public List<SelectItem> getLitaTipoDePessoa() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        for (TipoPessoaPermitido tipoPessoa : TipoPessoaPermitido.values()) {
            if (!tipoPessoa.equals(TipoPessoaPermitido.AMBOS)) {
                retorno.add(new SelectItem(tipoPessoa, tipoPessoa.getDescricao()));
            }
        }
        return Util.ordenaSelectItem(retorno);
    }

    public List<Conta> completaContaDespesa(String parte) {
        return configContaDespTipoPessoaFacade.getContaFacade().listaFiltrandoContaDespesa(parte.trim(), getSistemaControlador().getExercicioCorrente());
    }
}
