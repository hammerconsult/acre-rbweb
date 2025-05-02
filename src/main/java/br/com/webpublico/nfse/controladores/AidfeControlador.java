package br.com.webpublico.nfse.controladores;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.nfse.domain.Aidfe;
import br.com.webpublico.nfse.domain.SituacaoAidfe;
import br.com.webpublico.nfse.facades.AidfeFacade;
import br.com.webpublico.util.FacesUtil;
import com.google.common.base.Strings;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Date;

@ManagedBean(name = "aidfeControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "aifeListar", pattern = "/nfse/aidfe/listar/", viewId = "/faces/tributario/nfse/aidfe/lista.xhtml"),
    @URLMapping(id = "aidfeVer", pattern = "/nfse/aidfe/ver/#{aidfeControlador.id}/", viewId = "/faces/tributario/nfse/aidfe/visualizar.xhtml"),
})
public class AidfeControlador extends PrettyControlador<Aidfe> implements CRUD {

    @EJB
    private AidfeFacade aidfeFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public AidfeControlador() {
        super(Aidfe.class);
    }

    @Override
    public AbstractFacade<Aidfe> getFacede() {
        return aidfeFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/nfse/aidfe/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "aidfeVer", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        if (selecionado.getUserPrefeitura() == null) {
            selecionado.setUserPrefeitura(sistemaFacade.getUsuarioCorrente());
        }
    }

    public void deferirAidfe() {
        try {
            selecionado.setSituacao(SituacaoAidfe.DEFERIDA);
            selecionado.setAnalisadaEm(new Date());
            selecionado.setQuantidadeDeferida(selecionado.getQuantidadeSolicitada());
            aidfeFacade.salvar(selecionado);
            FacesUtil.addOperacaoRealizada("AIDF-e deferida com sucesso!");
            redireciona();
        } catch (Exception e) {
            FacesUtil.addErrorGenerico(e);
        }
    }

    public void deferirParcialAidfe() {
        try {
            validarDeferimentoParcial();
            selecionado.setSituacao(SituacaoAidfe.DEFERIDA_PARCIALMENTE);
            selecionado.setAnalisadaEm(new Date());
            aidfeFacade.salvar(selecionado);
            FacesUtil.addOperacaoRealizada("AIDF-e deferida parcialmente com sucesso!");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErrorGenerico(e);
        }
    }

    private void validarDeferimentoParcial() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getQuantidadeDeferida() == 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Quantidade Aceita de AIDF-e deve ser maior que Zero.");
        }
        if (selecionado.getQuantidadeDeferida() > selecionado.getQuantidadeSolicitada()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Quantidade Aceita de AIDF-e não pode ser maior que " + selecionado.getQuantidadeSolicitada() + ".");
        }
        if (selecionado.getQuantidadeDeferida() == selecionado.getQuantidadeSolicitada()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Quantidade Aceita é igual a Quantidade Solicitada, nesse caso não pode ser realizado o Deferimento Parcial.");
        }
        if (Strings.isNullOrEmpty(selecionado.getObservacaoAnalise())) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Observação.");
        }
        ve.lancarException();
    }

    public void indeferirAidfe() {
        try {
            validarIndeferimento();
            selecionado.setSituacao(SituacaoAidfe.INDEFERIDA);
            selecionado.setAnalisadaEm(new Date());
            aidfeFacade.salvar(selecionado);
            FacesUtil.addOperacaoRealizada("AIDF-e indeferida com sucesso!");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErrorGenerico(e);
        }
    }

    private void validarIndeferimento() {
        ValidacaoException ve = new ValidacaoException();
        if (Strings.isNullOrEmpty(selecionado.getObservacaoAnalise())) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Observação.");
        }
        ve.lancarException();
    }
}
