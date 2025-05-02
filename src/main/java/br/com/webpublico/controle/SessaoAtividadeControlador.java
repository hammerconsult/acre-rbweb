package br.com.webpublico.controle;

import br.com.webpublico.entidades.CNAE;
import br.com.webpublico.entidades.SessaoAtividade;
import br.com.webpublico.entidades.SessaoAtividadeCnae;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.SessaoAtividadeFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoSessaoAtividade", pattern = "/sessao-atividade/novo/", viewId = "/faces/tributario/sessaoatividade/edita.xhtml"),
    @URLMapping(id = "editarSessaoAtividade", pattern = "/sessao-atividade/editar/#{sessaoAtividadeControlador.id}/", viewId = "/faces/tributario/sessaoatividade/edita.xhtml"),
    @URLMapping(id = "listarSessaoAtividade", pattern = "/sessao-atividade/listar/", viewId = "/faces/tributario/sessaoatividade/lista.xhtml"),
    @URLMapping(id = "verSessaoAtividade", pattern = "/sessao-atividade/ver/#{sessaoAtividadeControlador.id}/", viewId = "/faces/tributario/sessaoatividade/visualizar.xhtml")
})
public class SessaoAtividadeControlador extends PrettyControlador<SessaoAtividade> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(SessaoAtividadeControlador.class);

    @EJB
    private SessaoAtividadeFacade sessaoAtividadeFacade;
    private CNAE cnae;

    public SessaoAtividadeControlador() {
        super(SessaoAtividade.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return sessaoAtividadeFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/sessao-atividade/";
    }

    @Override
    public Object getUrlKeyValue() {
        return getId();
    }

    @Override
    @URLAction(mappingId = "novoSessaoAtividade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editarSessaoAtividade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verSessaoAtividade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public void adicionarItem() {
        try {
            validarCnae();
            SessaoAtividadeCnae sessaoAtividadeCnae = new SessaoAtividadeCnae();
            sessaoAtividadeCnae.setCnae(cnae);
            sessaoAtividadeCnae.setSessaoAtividade(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getSessaoAtividadeCnaes(), sessaoAtividadeCnae);
            cnae = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }

    }

    public void validarCnae() {
        ValidacaoException ve = new ValidacaoException();
        if (cnae == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Selecione um CNAE.");
        }
        ve.lancarException();
    }

    public void removerItem(SessaoAtividadeCnae item) {
        selecionado.getSessaoAtividadeCnaes().remove(item);
    }

    public CNAE getCnae() {
        return cnae;
    }

    public void setCnae(CNAE cnae) {
        this.cnae = cnae;
    }
}
