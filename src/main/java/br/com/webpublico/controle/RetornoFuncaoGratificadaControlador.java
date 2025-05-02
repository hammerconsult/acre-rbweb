/*
 * Codigo gerado automaticamente em Fri Apr 20 16:12:42 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.PropositoAtoLegal;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "retornoFuncaoGratificadaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRetornoFuncaoGratificada", pattern = "/encerramento-funcao-gratificada/novo/", viewId = "/faces/rh/administracaodepagamento/retornofuncaogratificada/edita.xhtml"),
    @URLMapping(id = "editarRetornoFuncaoGratificada", pattern = "/encerramento-funcao-gratificada/editar/#{retornoFuncaoGratificadaControlador.id}/", viewId = "/faces/rh/administracaodepagamento/retornofuncaogratificada/edita.xhtml"),
    @URLMapping(id = "listarRetornoFuncaoGratificada", pattern = "/encerramento-funcao-gratificada/listar/", viewId = "/faces/rh/administracaodepagamento/retornofuncaogratificada/lista.xhtml"),
    @URLMapping(id = "verRetornoFuncaoGratificada", pattern = "/encerramento-funcao-gratificada/ver/#{retornoFuncaoGratificadaControlador.id}/", viewId = "/faces/rh/administracaodepagamento/retornofuncaogratificada/visualizar.xhtml")
})
public class RetornoFuncaoGratificadaControlador extends PrettyControlador<RetornoFuncaoGratificada> implements Serializable, CRUD {

    @EJB
    private RetornoFuncaoGratificadaFacade retornoFuncaoGratificadaFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    private ConverterAutoComplete converterContratoFP;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private FuncaoGratificadaFacade funcaoGratificadaFacade;
    private ConverterAutoComplete converterAtoLegal;
    private ConverterAutoComplete converterAtoLegalRetorno;
    @EJB
    private TipoProvimentoFacade tipoProvimentoFacade;
    private int inicio = 0;

    public RetornoFuncaoGratificadaControlador() {
        super(RetornoFuncaoGratificada.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return retornoFuncaoGratificadaFacade;
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (tipoProvimentoFacade.recuperaTipoProvimentoPorCodigo(TipoProvimento.EXONERACAO_DE_FUNCAO_GRATIFICADA) == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Tipo de Provimentos para Encerramento de Função Gratificada não foi cadastrado.");
        }
        ve.lancarException();
    }

    @URLAction(mappingId = "novoRetornoFuncaoGratificada", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        try {
            validarCampos();
            super.novo();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    @URLAction(mappingId = "verRetornoFuncaoGratificada", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarRetornoFuncaoGratificada", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public boolean validaRegrasParaExcluir() {
        if (retornoFuncaoGratificadaFacade.getFichaFinanceiraFPFacade().existeFolhaProcessadaPorVinculoFPAndData(selecionado.getContratoFP(), selecionado.getDataRetorno())) {
            FacesUtil.addOperacaoNaoPermitida("Foi encontrada folha processada para este encerramento de função gratificada na data de retorno. É impossível excluir este registro!");
            return false;
        }
        return true;
    }

    public Converter getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, atoLegalFacade);
        }
        return converterAtoLegal;
    }

    public Converter getConverterAtoLegalRetorno() {
        if (converterAtoLegalRetorno == null) {
            converterAtoLegalRetorno = new ConverterAutoComplete(AtoLegal.class, atoLegalFacade);
        }
        return converterAtoLegalRetorno;
    }

    public Converter getConverterContratoFP() {
        if (converterContratoFP == null) {
            converterContratoFP = new ConverterAutoComplete(ContratoFP.class, contratoFPFacade);
        }
        return converterContratoFP;
    }

    public List<ContratoFP> completaContratoFP(String s) {
        return contratoFPFacade.recuperaContratoFuncaoGratificada(s.trim());
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return atoLegalFacade.listaFiltrandoAtoLegalPorPropositoAtoLegalSQL(parte.trim(), PropositoAtoLegal.ATO_DE_PESSOAL);
    }

    public List<AtoLegal> completaAtoLegalRetorno(String parte) {
        return atoLegalFacade.listaFiltrandoAtoLegalPorPropositoAtoLegalSQL(parte.trim(), PropositoAtoLegal.ATO_DE_PESSOAL);
    }

    public RetornoFuncaoGratificada getRetornoFuncaoGratificada() {
        return (RetornoFuncaoGratificada) selecionado;
    }

    public List<FuncaoGratificada> getFuncaoGratificadas() {
        if (getRetornoFuncaoGratificada().getContratoFP() != null) {
            Date dataParametro = new Date();
            if (isOperacaoNovo()) {
                dataParametro = retornoFuncaoGratificadaFacade.getSistemaFacade().getDataOperacao();
            }
            if (isOperacaoEditar()) {
                dataParametro = selecionado.getDataRetorno();
            }
            return funcaoGratificadaFacade.recuperaFuncaoGratificadaContrato(getRetornoFuncaoGratificada().getContratoFP(), dataParametro);
        }
        return Lists.newArrayList();
    }

    public List<EnquadramentoFG> recuperarEnquadramentosFGs(FuncaoGratificada fg) {
        List<EnquadramentoFG> enquadramentoFGS = Lists.newArrayList();
        for (FuncaoGratificada funcaoGratificada : getFuncaoGratificadas()) {
            funcaoGratificada = funcaoGratificadaFacade.recuperarEnquadramentos(fg.getId());
            enquadramentoFGS.add(funcaoGratificada.getEnquadramentoCCVigente());
        }
        return enquadramentoFGS;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/encerramento-funcao-gratificada/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
