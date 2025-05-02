package br.com.webpublico.controle;

import br.com.webpublico.entidades.Entidade;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.OrgaoEntidadePncp;
import br.com.webpublico.entidades.OrgaoEntidadeUnidadePncp;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-entidade-pncp", pattern = "/entidade-pncp/novo/", viewId = "/faces/administrativo/licitacao/entidade-pncp/edita.xhtml"),
    @URLMapping(id = "editar-entidade-pncp", pattern = "/entidade-pncp/editar/#{orgaoEntidadePncpControlador.id}/", viewId = "/faces/administrativo/licitacao/entidade-pncp/edita.xhtml"),
    @URLMapping(id = "listar-entidade-pncp", pattern = "/entidade-pncp/listar/", viewId = "/faces/administrativo/licitacao/entidade-pncp/lista.xhtml"),
    @URLMapping(id = "ver-entidade-pncp", pattern = "/entidade-pncp/ver/#{orgaoEntidadePncpControlador.id}/", viewId = "/faces/administrativo/licitacao/entidade-pncp/visualizar.xhtml"),
})
public class OrgaoEntidadePncpControlador extends PrettyControlador<OrgaoEntidadePncp> implements Serializable, CRUD {

    @EJB
    private OrgaoEntidadePncpFacade facade;
    @EJB
    private EntidadeFacade entidadeFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    public List<Entidade> entidades;
    private OrgaoEntidadeUnidadePncp unidadePncp;

    public OrgaoEntidadePncpControlador() {
        super(OrgaoEntidadePncp.class);
    }

    @URLAction(mappingId = "nova-entidade-pncp", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        entidades = entidadeFacade.buscarTodasEntidadesAtivas();
        novaUnidade();
    }

    @URLAction(mappingId = "editar-entidade-pncp", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        entidades = entidadeFacade.buscarTodasEntidadesAtivas();
        novaUnidade();
        atribuirHierarquiaDaUnidade();
    }

    @URLAction(mappingId = "ver-entidade-pncp", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        recuperarObjeto();
        operacao = Operacoes.VER;
        atribuirHierarquiaDaUnidade();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/entidade-pncp/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void salvar() {
        try {
            validaSalvar();
            selecionado = facade.salvarRetornando(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "novo/");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    private void validaSalvar() {
        Util.validarCampos(selecionado);
        ValidacaoException ve = new ValidacaoException();
        if (Util.isListNullOrEmpty(selecionado.getUnidades())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Para continuar, a entidae deve possuir uma lista de órgãos.");
        }
        if (facade.hasEntidadePncpCadastradaParaEntidade(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A entidade " + selecionado.getEntidade() + " já está vinculada a uma entidade pncp.");
        }
        ve.lancarException();
    }

    public void novaUnidade() {
        unidadePncp = new OrgaoEntidadeUnidadePncp();
    }

    public void removerUnidade(OrgaoEntidadeUnidadePncp orgao) {
        selecionado.getUnidades().remove(orgao);
    }

    public void adicionarUnidade() {
        try {
            validarUnidade();
            unidadePncp.setOrgaoEntidadePncp(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getUnidades(), unidadePncp);
            novaUnidade();
            FacesUtil.executaJavaScript("setaFoco('Formulario:ac-orgao_input')");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void validarUnidade() {
        Util.validarCampos(selecionado);
        Util.validarCampos(unidadePncp);
        ValidacaoException ve = new ValidacaoException();
        OrgaoEntidadeUnidadePncp unidadeCadastrada = facade.buscarUnidadePncp(selecionado, unidadePncp.getUnidadeOrganizacional());
        if (unidadeCadastrada != null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A unidade " + unidadePncp.getHierarquiaOrganizacional().getCodigo() + " já está cadastrada para o órgão/entidade " + unidadeCadastrada.getOrgaoEntidadePncp().getEntidade());
        }
        ve.lancarException();

        for (OrgaoEntidadeUnidadePncp orgaoEnt : selecionado.getUnidades()) {
            if (!unidadePncp.equals(orgaoEnt) && orgaoEnt.getUnidadeOrganizacional().equals(unidadePncp.getUnidadeOrganizacional())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Unidade já foi adicionada na lista.");
            }
        }
        ve.lancarException();
    }

    public List<SelectItem> getEntidades() {
        List<SelectItem> list = Lists.newArrayList();
        list.add(new SelectItem(null, ""));
        if (!Util.isListNullOrEmpty(entidades)) {
            for (Entidade ent : entidades) {
                list.add(new SelectItem(ent, ent.getNomeEntidadePessoaJuridica()));
            }
        }
        return list;
    }

    private void atribuirHierarquiaDaUnidade() {
        selecionado.getUnidades().forEach(orgao -> orgao.setHierarquiaOrganizacional(hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(
            TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
            orgao.getUnidadeOrganizacional(),
            sistemaFacade.getDataOperacao())));
    }

    public List<HierarquiaOrganizacional> completarUnidade(String parte) {
        return hierarquiaOrganizacionalFacade.listaFiltrandoPorOrgaoAndTipoAdministrativa(parte.trim());
    }

    public void listenerEntidade() {
        FacesUtil.executaJavaScript("setaFoco('Formulario:ac-orgao_input')");
    }

    public OrgaoEntidadeUnidadePncp getUnidadePncp() {
        return unidadePncp;
    }

    public void setUnidadePncp(OrgaoEntidadeUnidadePncp unidadePncp) {
        this.unidadePncp = unidadePncp;
    }
}
