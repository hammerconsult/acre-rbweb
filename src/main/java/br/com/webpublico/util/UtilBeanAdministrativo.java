package br.com.webpublico.util;

import br.com.webpublico.controle.*;
import br.com.webpublico.controlerelatorio.ProgramaAlimentacaoControlador;
import br.com.webpublico.entidades.*;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created by mga on 28/11/2017.
 */
@ViewScoped
@ManagedBean
public class UtilBeanAdministrativo implements Serializable {

    public void redirecionarParaFormularioCotacao(FormularioCotacao selecionado) {
        FormularioCotacaoControlador controlador = (FormularioCotacaoControlador) Util.getControladorPeloNome("formularioCotacaoControlador");
        controlador.setSelecionado(selecionado);
        FacesUtil.redirecionamentoInterno(controlador.getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public void redirecionarParaCotacao(Cotacao selecionado) {
        CotacaoControlador controlador = (CotacaoControlador) Util.getControladorPeloNome("cotacaoControlador");
        controlador.setSelecionado(selecionado);
        FacesUtil.redirecionamentoInterno(controlador.getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public void redirecionarParaSolicitacaoCompra(SolicitacaoMaterial selecionado) {
        SolicitacaoMaterialControlador controlador = (SolicitacaoMaterialControlador) Util.getControladorPeloNome("solicitacaoMaterialControlador");
        controlador.setSelecionado(selecionado);
        FacesUtil.redirecionamentoInterno(controlador.getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public void redirecionarParaProcessoCompra(ProcessoDeCompra selecionado) {
        ProcessoDeCompraControlador controlador = (ProcessoDeCompraControlador) Util.getControladorPeloNome("processoDeCompraControlador");
        controlador.setSelecionado(selecionado);
        FacesUtil.redirecionamentoInterno(controlador.getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public void redirecionarParaAlteracaoContratual(AlteracaoContratual selecionado) {
        String caminho;
        if (selecionado.getTipoAlteracaoContratual().isAditivo()) {
            caminho = selecionado.getTipoCadastro().isContrato() ? "/aditivo-contrato/" : "/aditivo-ata-registro-preco/";
        } else {
            caminho = selecionado.getTipoCadastro().isContrato() ? "/apostilamento-contrato/" : "/apostilamento-ata-registro-preco/";
        }
        FacesUtil.redirecionamentoInterno(caminho + "ver/" + selecionado.getId() + "/");
    }

    public void redirecionarParaAjusteContrato(AjusteContrato selecionado) {
        AjusteContratoControlador controlador = (AjusteContratoControlador) Util.getControladorPeloNome("ajusteContratoControlador");
        controlador.setSelecionado(selecionado);
        FacesUtil.redirecionamentoInterno(controlador.getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public void redirecionarParaProgramaAlimentacao(ProgramaAlimentacao selecionado) {
        ProgramaAlimentacaoControlador controlador = (ProgramaAlimentacaoControlador) Util.getControladorPeloNome("programaAlimentacaoControlador");
        controlador.setSelecionado(selecionado);
        FacesUtil.redirecionamentoInterno(controlador.getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

    public void redirecionarParaFeira(Feira selecionado) {
        FeiraControlador controlador = (FeiraControlador) Util.getControladorPeloNome("feiraControlador");
        controlador.setSelecionado(selecionado);
        FacesUtil.redirecionamentoInterno(controlador.getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }
}
