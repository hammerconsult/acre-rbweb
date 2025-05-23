/*
 * Codigo gerado automaticamente em Fri May 13 14:50:18 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.PessoaJuridica;
import br.com.webpublico.entidades.UnidadeGestora;
import br.com.webpublico.entidades.UnidadeGestoraUnidadeOrganizacional;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoUnidadeGestora;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.UnidadeGestoraFacade;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-unidade-gestora", pattern = "/unidade-gestora/novo/", viewId = "/faces/tributario/cadastromunicipal/unidadegestora/edita.xhtml"),
    @URLMapping(id = "editar-unidade-gestora", pattern = "/unidade-gestora/editar/#{unidadeGestoraControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/unidadegestora/edita.xhtml"),
    @URLMapping(id = "ver-unidade-gestora", pattern = "/unidade-gestora/ver/#{unidadeGestoraControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/unidadegestora/visualizar.xhtml"),
    @URLMapping(id = "listar-unidade-gestora", pattern = "/unidade-gestora/listar/", viewId = "/faces/tributario/cadastromunicipal/unidadegestora/lista.xhtml")
})
public class UnidadeGestoraControlador extends PrettyControlador<UnidadeGestora> implements Serializable, CRUD {

    @EJB
    private UnidadeGestoraFacade facade;
    private UnidadeGestoraUnidadeOrganizacional unidadeOrganizacional;
    private List<UnidadeGestoraUnidadeOrganizacional> unidades;
    private boolean isSomenteUnidadesVigentes = Boolean.TRUE;

    public UnidadeGestoraControlador() {
        super(UnidadeGestora.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/unidade-gestora/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-unidade-gestora", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
    }

    @URLAction(mappingId = "editar-unidade-gestora", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        if (selecionado.getExercicio() == null) {
            selecionado.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
        }
        montarListaUnidades();
    }

    @URLAction(mappingId = "ver-unidade-gestora", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
        montarListaUnidades();
    }

    public List<PessoaJuridica> completaPessoaJuridica(String parte) {
        return facade.getPessoaJuridicaFacade().listaFiltrandoRazaoSocialCNPJ(parte.trim());
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacionalAdministrativa(String parte) {
        return facade.getHierarquiaOrganizacionalFacade().filtrandoHierarquiaHorganizacionalTipo(parte.trim(), "" + TipoHierarquiaOrganizacional.ADMINISTRATIVA, UtilRH.getDataOperacao());
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacionalOrcamentaria(String parte) {
        return facade.getHierarquiaOrganizacionalFacade().filtrandoHierarquiaHorganizacionalTipo(parte.trim(), "" + TipoHierarquiaOrganizacional.ORCAMENTARIA, UtilRH.getDataOperacao());
    }

    public void cancelarUnidade() {
        this.unidadeOrganizacional = null;
        FacesUtil.executaJavaScript("setaFoco('Formulario:btnNovaUnidade')");
    }

    public void validarUnidadeOrganizacional() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCampos(unidadeOrganizacional);
        for (UnidadeGestoraUnidadeOrganizacional uguo : selecionado.getUnidadeGestoraUnidadesOrganizacionais()) {
            if (uguo.getUnidadeOrganizacional().equals(unidadeOrganizacional.getHierarquiaOrganizacional().getSubordinada())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Unidade '" + unidadeOrganizacional.getHierarquiaOrganizacional() + "'já foi adicionada a esta unidade gestora.");
            }
        }
        ve.lancarException();
    }

    public void adicionarUnidadeOrganizacional() {
        try {
            validarUnidadeOrganizacional();
            unidadeOrganizacional.setUnidadeGestora(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getUnidadeGestoraUnidadesOrganizacionais(), unidadeOrganizacional);
            cancelarUnidade();
            montarListaUnidades();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void novaUnidadeOrganizacional() {
        unidadeOrganizacional = new UnidadeGestoraUnidadeOrganizacional();
        FacesUtil.executaJavaScript("setaFoco('Formulario:iUnidadeOrganizacionalOrc_input')");
    }

    public void removerUnidadeOrganizacional(UnidadeGestoraUnidadeOrganizacional obj) {
        selecionado.getUnidadeGestoraUnidadesOrganizacionais().remove(obj);
        montarListaUnidades();
    }

    @Override
    public void salvar() {
        try {
            validarSalvar();
            super.salvar();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    private void validarSalvar() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getUnidadeGestoraUnidadesOrganizacionais() == null || selecionado.getUnidadeGestoraUnidadesOrganizacionais().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório informar ao menos uma Unidade Organizacional (Orçamentária).");
        }
        ve.lancarException();
        if (TipoUnidadeGestora.ADMINISTRATIVO.equals(selecionado.getTipoUnidadeGestora())) {
            Set<UnidadeGestoraUnidadeOrganizacional> unidades = new HashSet<>();
            for (UnidadeGestoraUnidadeOrganizacional unidade : selecionado.getUnidadeGestoraUnidadesOrganizacionais()) {
                unidades.addAll(facade.buscarUnidadeGestoraPorUnidadeOrcamentariaAndTipo(selecionado, unidade.getUnidadeOrganizacional()));
            }
            if (!unidades.isEmpty()) {
                for (UnidadeGestoraUnidadeOrganizacional ug : unidades) {
                    HierarquiaOrganizacional hierarquiaUnidade = recuperarHierarquiaOrcamentariaDaUnidadeVigente(ug);
                    if (hierarquiaUnidade != null) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("A unidade orçamentária " + hierarquiaUnidade
                            + " está sendo utilizada na unidade gestora " + ug.getUnidadeGestora().getCodigo() + " - tipo " + selecionado.getTipoUnidadeGestora().getDescricao() + ".");
                    }
                }
            }
        }
        ve.lancarException();
    }

    public List<SelectItem> getTiposUnidadeGestora() {
        return Util.getListSelectItem(TipoUnidadeGestora.values());
    }

    public HierarquiaOrganizacional recuperarHierarquiaOrcamentariaDaUnidadeVigente(UnidadeGestoraUnidadeOrganizacional uo) {
        return facade.getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), uo.getUnidadeOrganizacional(), UtilRH.getDataOperacao());
    }

    public void montarListaUnidades() {
        HierarquiaOrganizacional ho;
        unidades = new ArrayList<>();

        for (UnidadeGestoraUnidadeOrganizacional uo : selecionado.getUnidadeGestoraUnidadesOrganizacionais()) {
            ho = facade.getHierarquiaOrganizacionalFacade()
                .getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ORCAMENTARIA.name(),
                    uo.getUnidadeOrganizacional(),
                    facade.getSistemaFacade().getDataOperacao());

            if (ho == null && !isSomenteUnidadesVigentes) {
                ho = facade.getHierarquiaOrganizacionalFacade()
                    .getHierarquiaDaUnidadeSemConsiderarVigencia(TipoHierarquiaOrganizacional.ORCAMENTARIA.name(),
                        uo.getUnidadeOrganizacional());
            }

            if (ho != null) {
                uo.setHierarquiaOrganizacional(ho);
                unidades.add(uo);
            }
        }
        FacesUtil.atualizarComponente("Formulario:tabelaUnidades");
    }

    public UnidadeGestoraUnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeGestoraUnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public List<UnidadeGestoraUnidadeOrganizacional> getUnidades() {
        return unidades;
    }

    public void setUnidades(List<UnidadeGestoraUnidadeOrganizacional> unidades) {
        this.unidades = unidades;
    }

    public boolean isSomenteUnidadesVigentes() {
        return isSomenteUnidadesVigentes;
    }

    public void setSomenteUnidadesVigentes(boolean somenteUnidadesVigentes) {
        isSomenteUnidadesVigentes = somenteUnidadesVigentes;
    }
}
