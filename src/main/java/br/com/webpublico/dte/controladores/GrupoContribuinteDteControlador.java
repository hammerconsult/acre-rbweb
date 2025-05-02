package br.com.webpublico.dte.controladores;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.dte.entidades.GrupoContribuinteCmcDte;
import br.com.webpublico.dte.entidades.GrupoContribuinteDte;
import br.com.webpublico.dte.entidades.ModeloDocumentoDte;
import br.com.webpublico.dte.facades.GrupoContribuinteDteFacade;
import br.com.webpublico.dte.facades.ModeloDocumentoDteFacade;
import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listarGrupoContribuinteDte", pattern = "/tributario/dte/grupo-contribuinte/listar/",
        viewId = "/faces/tributario/dte/grupocontribuinte/lista.xhtml"),
    @URLMapping(id = "verGrupoContribuinteDte", pattern = "/tributario/dte/grupo-contribuinte/ver/#{grupoContribuinteDteControlador.id}/",
        viewId = "/faces/tributario/dte/grupocontribuinte/visualizar.xhtml"),
    @URLMapping(id = "novoGrupoContribuinteDte", pattern = "/tributario/dte/grupo-contribuinte/novo/",
        viewId = "/faces/tributario/dte/grupocontribuinte/edita.xhtml"),
    @URLMapping(id = "editarGrupoContribuinteDte", pattern = "/tributario/dte/grupo-contribuinte/editar/#{grupoContribuinteDteControlador.id}/",
        viewId = "/faces/tributario/dte/grupocontribuinte/edita.xhtml")
})
public class GrupoContribuinteDteControlador extends PrettyControlador<GrupoContribuinteDte> implements CRUD {

    @EJB
    private GrupoContribuinteDteFacade facade;
    private CadastroEconomico cadastroEconomico;

    public GrupoContribuinteDteControlador() {
        super(GrupoContribuinteDte.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/dte/grupo-contribuinte/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoGrupoContribuinteDte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editarGrupoContribuinteDte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verGrupoContribuinteDte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public List<GrupoContribuinteDte> completarGrupoContribuinte(String parte) {
        if (parte == null)
            parte = "";
        return facade.buscarGrupoPorDescricao(parte);
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public void adicionarCadastroEconomico() {
        try {
            validarCadastroEconomico();
            GrupoContribuinteCmcDte grupoContribuinteCmcDte = new GrupoContribuinteCmcDte();
            grupoContribuinteCmcDte.setGrupo(selecionado);
            grupoContribuinteCmcDte.setCadastroEconomico(cadastroEconomico);
            selecionado.getCadastros().add(grupoContribuinteCmcDte);
            cadastroEconomico = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            logger.error("ERRO {}", e);
            FacesUtil.addErrorGenerico(e);
        }
    }

    private void validarCadastroEconomico() {
        ValidacaoException ve = new ValidacaoException();
        if (cadastroEconomico == null)
            ve.adicionarMensagemDeCampoObrigatorio("O Cadastro Econômico deve ser informado.");
        if (selecionado.hasCadastro(cadastroEconomico))
            ve.adicionarMensagemDeCampoObrigatorio("O Cadastro Econômico já foi adicionado.");
    }

    public void removerCadastroEconomico(GrupoContribuinteCmcDte grupoContribuinteCmcDte) {
        selecionado.getCadastros().remove(grupoContribuinteCmcDte);
    }
}
