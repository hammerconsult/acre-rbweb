package br.com.webpublico.controle.rh.cadastrofuncional;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.ContaCorrenteBancaria;
import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ContaCorrenteBancPessoaFacade;
import br.com.webpublico.negocios.ContratoFPDataModelPesquisar;
import br.com.webpublico.negocios.ContratoFPFacade;
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
 * @Author peixe on 20/04/2017  10:52.
 */
@ManagedBean(name = "contratoFPContaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "editarContratoConta", pattern = "/contratofp-conta/editar/#{contratoFPContaControlador.id}/", viewId = "/faces/rh/cadastrofuncional/contratofp-conta/edita.xhtml"),
    @URLMapping(id = "pesquisarContratoConta", pattern = "/contratofp-conta/pesquisar/", viewId = "/faces/rh/cadastrofuncional/contratofp-conta/pesquisar.xhtml")
})
public class ContratoFPContaControlador extends PrettyControlador<ContratoFP> implements Serializable, CRUD {

    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private ContratoFPDataModelPesquisar contratoFPDataModelPesquisar;
    @EJB
    private ContaCorrenteBancPessoaFacade contaCorrenteBancPessoaFacade;

    public ContratoFPContaControlador() {
        super(ContratoFP.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/contratofp-conta/";
    }

    @Override
    public Object getUrlKeyValue() {
        return getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return contratoFPFacade;
    }

    public ContratoFPDataModelPesquisar getContratoFPDataModelPesquisar() {
        return contratoFPDataModelPesquisar;
    }

    public void setContratoFPDataModelPesquisar(ContratoFPDataModelPesquisar contratoFPDataModelPesquisar) {
        this.contratoFPDataModelPesquisar = contratoFPDataModelPesquisar;
    }

    @URLAction(mappingId = "pesquisarContratoConta", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {

    }

    @URLAction(mappingId = "editarContratoConta", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void pesquisar() {
        operacao = Operacoes.EDITAR;
        selecionado = contratoFPFacade.recuperarSomenteContrato(getId());
    }

    private void redirecionarParaPesquisa() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "pesquisar/");
    }

    @Override
    public void cancelar() {
        redirecionarParaPesquisa();
    }

    public List<SelectItem> getContasCorrentesBancarias() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        if (selecionado != null && selecionado.getMatriculaFP() != null) {
            for (ContaCorrenteBancaria object : contaCorrenteBancPessoaFacade.listaContasPorPessoaFisica(selecionado.getMatriculaFP().getPessoa())) {
                toReturn.add(new SelectItem(object, object.toString()));
            }
        }
        return toReturn;
    }

    @Override
    public void redireciona() {
        redirecionarParaPesquisa();
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            contratoFPFacade.salvaRetornando(selecionado);
            FacesUtil.addOperacaoRealizada(SummaryMessages.OPERACAO_REALIZADA.getDescricao());
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
            return;
        } catch (Exception e) {
            descobrirETratarException(e);
        }
        redireciona();
    }


    public void validarCampos() {
        ValidacaoException val = new ValidacaoException();
        if (selecionado.getContaCorrente() == null) {
            val.adicionarMensagemDeCampoObrigatorio("Selecione a Conta Bancária corretamente.");
        }
        val.lancarException();
    }
}

