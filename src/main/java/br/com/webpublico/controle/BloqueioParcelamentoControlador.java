/*
 * Codigo gerado automaticamente em Thu May 10 14:10:07 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.BloqueioParcelamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.BloqueioParcelamentoFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.singletons.SingletonGeradorCodigoPorExercicio;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;

@ManagedBean(name = "bloqueioParcelamentoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoBloqueioParcelamento", pattern = "/bloqueio-parcelamento/novo/", viewId = "/faces/tributario/contacorrente/bloqueioparcelamento/edita.xhtml"),
        @URLMapping(id = "editarBloqueioParcelamento", pattern = "/bloqueio-parcelamento/editar/#{bloqueioParcelamentoControlador.id}/", viewId = "/faces/tributario/contacorrente/bloqueioparcelamento/edita.xhtml"),
        @URLMapping(id = "listarBloqueioParcelamento", pattern = "/bloqueio-parcelamento/listar/", viewId = "/faces/tributario/contacorrente/bloqueioparcelamento/lista.xhtml"),
        @URLMapping(id = "verBloqueioParcelamento", pattern = "/bloqueio-parcelamento/ver/#{bloqueioParcelamentoControlador.id}/", viewId = "/faces/tributario/contacorrente/bloqueioparcelamento/visualizar.xhtml")
})
public class BloqueioParcelamentoControlador extends PrettyControlador<BloqueioParcelamento> implements Serializable, CRUD {

    @EJB
    private BloqueioParcelamentoFacade bloqueioParcelamentoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonGeradorCodigoPorExercicio singletonGeradorCodigoPorExercicio;

    public BloqueioParcelamentoControlador() {
        super(BloqueioParcelamento.class);
    }

    @URLAction(mappingId = "novoBloqueioParcelamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setExercicio(sistemaFacade.getExercicioCorrente());
        selecionado.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
        selecionado.setCodigo(null);
        selecionado.setDataLancamento(new Date());
    }

    @URLAction(mappingId = "verBloqueioParcelamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarBloqueioParcelamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/bloqueio-parcelamento/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return bloqueioParcelamentoFacade;
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDataLancamento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor Informar a data de lançamento!");
        }
        if (selecionado.getDataInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor Informar a data inicial!");
        } else {
            if (selecionado.getDataFinal() != null && selecionado.getDataInicial().after(selecionado.getDataFinal())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Data Inicial deve ser menor que a Data Final.");

            }
        }
        if (selecionado.getPessoa() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor Informar a Pessoa!");
        }
        if (selecionado.getMotivo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor Informar o Motivo!");
        }
        ve.lancarException();
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            if (selecionado.getCodigo() == null) {
                selecionado.setCodigo(singletonGeradorCodigoPorExercicio.getProximoCodigoDoExercicio(BloqueioParcelamento.class, "codigo", "exercicio_id", selecionado.getExercicio()));
            }
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }
}
