package br.com.webpublico.controle;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.ResponsavelPatrimonio;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.enums.EstadoConservacaoBem;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.negocios.BemFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ParametroPatrimonioFacade;
import br.com.webpublico.negocios.SingletonHierarquiaOrganizacional;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import org.primefaces.component.autocomplete.AutoComplete;
import org.primefaces.model.TreeNode;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 03/01/14
 * Time: 17:46
 * To change this template use File | Settings | File Templates.
 */
@ViewScoped
@ManagedBean
public class HierarquiaAdmPaiFilhoGestorPatrimonioControlador implements Serializable {
    @EJB
    private SingletonHierarquiaOrganizacional singletonHO;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private ParametroPatrimonioFacade parametroPatrimonioFacade;

    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private TreeNode rootAdm;
    private TreeNode selectedNode;
    private AutoComplete autoCompleteUnidadeBinding;
    private AutoComplete autoCompleteResponsavelBinding;
    private Boolean podeExibirMensagemQuandoNaoEncontrarResponsavelPatrimonio;

    public void carregarArvore() {
        rootAdm = singletonHO.getArvoreAdministrativa(bemFacade.getSistemaFacade().getDataOperacao());
    }

    public void selecionarHierarquiaPaiEFilhoGestorPatrimonioDaArvore() {
        if (selectedNode == null) {
            return;
        }
        HierarquiaOrganizacional ho = (HierarquiaOrganizacional) selectedNode.getData();
        UsuarioSistema usuarioCorrente = bemFacade.getSistemaFacade().getUsuarioCorrente();
        List<HierarquiaOrganizacional> hierarquias = bemFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPaiEFilhoOndeUsuarioEhGestorPatrimonio(ho.getDescricao(), null, usuarioCorrente, bemFacade.getSistemaFacade().getDataOperacao());
        selecionarHierarquiaDaArvore(ho, usuarioCorrente, hierarquias);
    }

    private void selecionarHierarquiaDaArvore(HierarquiaOrganizacional ho, UsuarioSistema usuario, List<HierarquiaOrganizacional> hierarquias) {
        for (HierarquiaOrganizacional hierarquiaOrg : hierarquias) {
            if (hierarquiaOrg.equals(ho)) {
                autoCompleteUnidadeBinding.setValue(ho);
                autoCompleteUnidadeBinding.processUpdates(FacesContext.getCurrentInstance());
                recuperarResponsavelPatrimonio();
                return;
            }
        }
        FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "O usuário " + usuario.getNome() + " não possui permissão de gestor patrimonial nesta secretaria.");
    }

    public void recuperarResponsavelPatrimonio() {
        try {
            autoCompleteResponsavelBinding.setValue(parametroPatrimonioFacade.recuperarResponsavelVigente(((HierarquiaOrganizacional) autoCompleteUnidadeBinding.getValue()).getSubordinada(), bemFacade.getSistemaFacade().getDataOperacao()));
        } catch (ExcecaoNegocioGenerica eng) {
            autoCompleteResponsavelBinding.setValue(null);

            if (autoCompleteResponsavelBinding.isRendered() && podeExibirMensagemQuandoNaoEncontrarResponsavelPatrimonio) {
                FacesUtil.addError(SummaryMessages.ATENCAO.getDescricao(), eng.getMessage());
            }
        }
    }

    public List<ResponsavelPatrimonio> completarResponsavelPatrimonioVigente(String parte) {
        return parametroPatrimonioFacade.completarResponsavelPatrimonioVigente(parte, bemFacade.getSistemaFacade().getDataOperacao());
    }

    public void atribuirPodeExibirMensagemQuandoNaoEncontrarResponsavelPatrimonio(String bool) {
        podeExibirMensagemQuandoNaoEncontrarResponsavelPatrimonio = Boolean.valueOf(bool);
    }

    public TreeNode getRootAdm() {
        return rootAdm;
    }

    public void setRootAdm(TreeNode rootAdm) {
        this.rootAdm = rootAdm;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public AutoComplete getAutoCompleteUnidadeBinding() {
        return autoCompleteUnidadeBinding;
    }

    public void setAutoCompleteUnidadeBinding(AutoComplete autoCompleteUnidadeBinding) {
        this.autoCompleteUnidadeBinding = autoCompleteUnidadeBinding;
    }

    public AutoComplete getAutoCompleteResponsavelBinding() {
        return autoCompleteResponsavelBinding;
    }

    public void setAutoCompleteResponsavelBinding(AutoComplete autoCompleteResponsavelBinding) {
        this.autoCompleteResponsavelBinding = autoCompleteResponsavelBinding;
    }

    public List<SelectItem> getListaSelectItemEstadosDeConservacao() {
        return Util.getListSelectItem(Arrays.asList(EstadoConservacaoBem.getValoresPossiveisParaLevantamentoDeBemPatrimonial()));
    }
}
