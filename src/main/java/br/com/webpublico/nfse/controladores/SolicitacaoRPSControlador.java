package br.com.webpublico.nfse.controladores;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.nfse.domain.SolicitacaoRPS;
import br.com.webpublico.nfse.enums.SituacaoSolicitacaoRPS;
import br.com.webpublico.nfse.facades.SolicitacaoRPSFacade;
import br.com.webpublico.util.FacesUtil;
import com.google.common.base.Strings;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Date;

/**
 * Created by Renato on 19/01/2019.
 */


@ManagedBean(name = "solicitacaoRPSControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listar-solicitacao-rps", pattern = "/nfse/solicitacao-rps/listar/", viewId = "/faces/tributario/nfse/solicitacao-rps/lista.xhtml"),
    @URLMapping(id = "ver-solicitacao-rps", pattern = "/nfse/solicitacao-rps/ver/#{solicitacaoRPSControlador.id}/", viewId = "/faces/tributario/nfse/solicitacao-rps/visualizar.xhtml"),
})
public class SolicitacaoRPSControlador extends PrettyControlador<SolicitacaoRPS> implements CRUD {

    @EJB
    private SolicitacaoRPSFacade solicitacaoRPSFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public SolicitacaoRPSControlador() {
        super(SolicitacaoRPS.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return solicitacaoRPSFacade;
    }


    @Override
    public String getCaminhoPadrao() {
        return "/nfse/solicitacao-rps/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "ver-solicitacao-rps", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        if (selecionado.getUserPrefeitura() == null) {
            selecionado.setUserPrefeitura(sistemaFacade.getUsuarioCorrente());
        }
    }

    public void deferir() {
        try {
            selecionado.setSituacao(SituacaoSolicitacaoRPS.DEFERIDA);
            selecionado.setAnalisadaEm(new Date());
            solicitacaoRPSFacade.salvar(selecionado);
            FacesUtil.addOperacaoRealizada("Solicitação de RPS  deferida com sucesso!");
            redireciona();
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e.getMensagens());
        }
    }

    public void indeferir() {
        try {
            validarIndeferimento();
            selecionado.setSituacao(SituacaoSolicitacaoRPS.INDEFERIDA);
            selecionado.setAnalisadaEm(new Date());
            solicitacaoRPSFacade.salvar(selecionado);
            FacesUtil.addOperacaoRealizada("Solicitação de RPS indeferida com sucesso!");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
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
