/*
 * Codigo gerado automaticamente em Fri May 13 14:50:18 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.FuncaoResponsavelUnidade;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
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

@ManagedBean(name = "responsavelUnidadeControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-responsavel-unidade", pattern = "/responsavel-unidade/novo/", viewId = "/faces/tributario/cadastromunicipal/responsavelunidade/edita.xhtml"),
    @URLMapping(id = "editar-responsavel-unidade", pattern = "/responsavel-unidade/editar/#{responsavelUnidadeControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/responsavelunidade/edita.xhtml"),
    @URLMapping(id = "ver-responsavel-unidade", pattern = "/responsavel-unidade/ver/#{responsavelUnidadeControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/responsavelunidade/visualizar.xhtml"),
    @URLMapping(id = "listar-responsavel-unidade", pattern = "/responsavel-unidade/listar/", viewId = "/faces/tributario/cadastromunicipal/responsavelunidade/lista.xhtml")
})
public class ResponsavelUnidadeControlador extends PrettyControlador<ResponsavelUnidade> implements Serializable, CRUD {

    @EJB
    private ResponsavelUnidadeFacade responsavelUnidadeFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    private ConverterAutoComplete converterPessoa;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    private ConverterAutoComplete converterAtoLegal;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private ConverterAutoComplete converterHierarquiaOrganizacional;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private AgendaResponsavelUnidade agenda;

    public ResponsavelUnidadeControlador() {
        super(ResponsavelUnidade.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return responsavelUnidadeFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/responsavel-unidade/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-responsavel-unidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editar-responsavel-unidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        hierarquiaOrganizacional = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(UtilRH.getDataOperacao(), selecionado.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ADMINISTRATIVA);
    }

    @URLAction(mappingId = "ver-responsavel-unidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public List<Pessoa> completaPessoa(String parte) {
        return pessoaFacade.listaTodasPessoas(parte.trim());
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, pessoaFacade);
        }
        return converterPessoa;
    }

    public List<SelectItem> getFuncoesGestorOrdenador() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (FuncaoResponsavelUnidade object : FuncaoResponsavelUnidade.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<AtoLegal> completaAtoLeal(String parte) {
        return atoLegalFacade.listaFiltrandoAtoLegal(parte.trim());
    }

    public ConverterAutoComplete getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, atoLegalFacade);
        }
        return converterAtoLegal;
    }

    public ConverterAutoComplete getConverterHierarquiaOrganizacional() {
        if (converterHierarquiaOrganizacional == null) {
            converterHierarquiaOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterHierarquiaOrganizacional;
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacional(String parte) {
        return hierarquiaOrganizacionalFacade.filtrandoHierarquiaHorganizacionalTipo(parte.trim(), "" + TipoHierarquiaOrganizacional.ADMINISTRATIVA, UtilRH.getDataOperacao());
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }


    @Override
    public void salvar() {
        try {
            selecionado.setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
            selecionado.validarAntesDeConfirmar();
            super.salvar();
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addError("Operação não realizada!", e.getMessage());
        }
    }

    public void instanciarAgenda() {
        agenda = new AgendaResponsavelUnidade();
        agenda.setResponsavel(selecionado);
    }

    public void cancelarAgenda() {
        agenda = null;
    }

    public void adicionarAgenda() {
        try {
            Util.validarCampos(agenda);
            selecionado.setAgenda(Util.adicionarObjetoEmLista(selecionado.getAgenda(), agenda));
            cancelarAgenda();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void editarAgenda(AgendaResponsavelUnidade agenda) {
        this.agenda = agenda;
    }

    public void removerAgenda(AgendaResponsavelUnidade agenda) {
        this.selecionado.getAgenda().remove(agenda);
    }

    public AgendaResponsavelUnidade getAgenda() {
        return agenda;
    }

    public void setAgenda(AgendaResponsavelUnidade agenda) {
        this.agenda = agenda;
    }
}
