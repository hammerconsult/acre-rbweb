package br.com.webpublico.controle;


import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.PlanoAnualContratacaoGrupoObjetoCompraVO;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoObjetoCompra;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.PlanoAnualContratacaoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
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
import java.util.Collections;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {@URLMapping(id = "novo-pac", pattern = "/plano-anual-contratacoes/novo/", viewId = "/faces/administrativo/licitacao/pac/edita.xhtml"),
    @URLMapping(id = "editar-pac", pattern = "/plano-anual-contratacoes/editar/#{planoAnualContratacaoControlador.id}/", viewId = "/faces/administrativo/licitacao/pac/edita.xhtml"),
    @URLMapping(id = "ver-pac", pattern = "/plano-anual-contratacoes/ver/#{planoAnualContratacaoControlador.id}/", viewId = "/faces/administrativo/licitacao/pac/visualizar.xhtml"),
    @URLMapping(id = "listar-pac", pattern = "/plano-anual-contratacoes/listar/", viewId = "/faces/administrativo/licitacao/pac/lista.xhtml"),})

public class PlanoAnualContratacaoControlador extends PrettyControlador<PlanoAnualContratacao> implements Serializable, CRUD {

    @EJB
    private PlanoAnualContratacaoFacade facade;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private PlanoAnualContratacaoGrupoObjetoCompra pacGrupoObjetoCompra;
    private PlanoAnualContratacaoObjetoCompra pacObjetoCompra;
    private List<PlanoAnualContratacaoGrupoObjetoCompraVO> gruposVo;
    private Integer tabIndex;
    private TipoObjetoCompra tipoObjetoCompra;
    private ConverterAutoComplete converterPlanoAnualContratacaoObjetoCompra;

    public PlanoAnualContratacaoControlador() {
        super(PlanoAnualContratacao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novo-pac", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setInicioElaboracao(facade.getSistemaFacade().getDataOperacao());
        selecionado.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
        selecionado.setUnidadeOrganizacional(facade.getSistemaFacade().getUnidadeOrganizacionalAdministrativaCorrente());
        atribuirHierarquia();
        tabIndex = 0;
    }

    @URLAction(mappingId = "ver-pac", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-pac", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        atribuirHierarquia();
        Collections.sort(selecionado.getGruposObjetoCompra());
        for (PlanoAnualContratacaoGrupoObjetoCompra grupo : selecionado.getGruposObjetoCompra()) {
            Collections.sort(grupo.getObjetosCompra());
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/plano-anual-contratacoes/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void salvar() {
        try {
            validarRegrasSalvar();
            selecionado = facade.salvarRetornando(selecionado);
            redirecionarParaVerOrEditar(selecionado.getId(), "ver");
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void validarRegrasSalvar() {
        Util.validarCampos(selecionado);
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getGruposObjetoCompra().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O plano não possui grupo objeto de compra.");
        }
        ve.lancarException();
    }

    private void atribuirHierarquia() {
        setHierarquiaOrganizacional(facade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(facade.getSistemaFacade().getDataOperacao(), selecionado.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ADMINISTRATIVA));
    }

    public boolean hasGrupoOc() {
        return pacGrupoObjetoCompra != null;
    }

    public boolean hasGruposVo() {
        return gruposVo != null && !gruposVo.isEmpty();
    }

    public void novoGrupoOc() {
        pacGrupoObjetoCompra = new PlanoAnualContratacaoGrupoObjetoCompra();
        pacGrupoObjetoCompra.setPlanoAnualContratacao(selecionado);
        FacesUtil.executaJavaScript("setaFoco('Formulario:tab-view:grupo-oc_input')");
    }

    public void cancelarGrupoOc() {
        pacGrupoObjetoCompra = null;
    }

    public void adicionarGrupoOcSelecionados() {
        try {
            if (hasGruposVo()) {
                for (PlanoAnualContratacaoGrupoObjetoCompraVO grupo : gruposVo) {
                    if (grupo.getSelecionado()) {
                        novoGrupoOc();
                        pacGrupoObjetoCompra.setGrupoObjetoCompra(grupo.getGrupoObjetoCompra());
                        validarGrupoOc();
                        Util.adicionarObjetoEmLista(selecionado.getGruposObjetoCompra(), pacGrupoObjetoCompra);
                    }
                }
            }
            FacesUtil.atualizarComponente("Formulario:tab-view:pn-grupo");
            FacesUtil.executaJavaScript("dlgPesquisaGrupoOc.hide()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }


    public void adicionarGrupoOc() {
        try {
            validarGrupoOc();
            Util.adicionarObjetoEmLista(selecionado.getGruposObjetoCompra(), pacGrupoObjetoCompra);
            novoGrupoOc();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerGrupoOc(PlanoAnualContratacaoGrupoObjetoCompra obj) {
        selecionado.getGruposObjetoCompra().remove(obj);
    }

    public void selecionarGrupoOc(PlanoAnualContratacaoGrupoObjetoCompra obj) {
        pacGrupoObjetoCompra = obj;
        tabIndex = 1;
        novoObjetoCompra();
        FacesUtil.atualizarComponente("Formulario:tab-view");
    }

    public void validarGrupoOc() {
        Util.validarCampos(pacGrupoObjetoCompra);
        ValidacaoException ve = new ValidacaoException();
        for (PlanoAnualContratacaoGrupoObjetoCompra grupo : selecionado.getGruposObjetoCompra()) {
            if (grupo.getGrupoObjetoCompra().equals(pacGrupoObjetoCompra.getGrupoObjetoCompra())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A grupo objeto de compra " + grupo.getGrupoObjetoCompra().getCodigo() + "já foi adicionado lista.");
            }
        }
        ve.lancarException();
    }

    public boolean hasObjetoCompra() {
        return pacObjetoCompra != null;
    }

    public void novoObjetoCompra() {
        pacObjetoCompra = new PlanoAnualContratacaoObjetoCompra();
        pacObjetoCompra.setPlanoAnualContratacaoGrupo(pacGrupoObjetoCompra);
        FacesUtil.executaJavaScript("setaFoco('Formulario:tab-view:objeto-compra_input')");
    }

    public void cancelarObjetoCompra() {
        pacObjetoCompra = null;
    }

    public void adicionarObjetoCompra() {
        try {
            validarObjetoCompra();
            if (pacObjetoCompra.getNumero() == null) {
                pacObjetoCompra.setNumero(getMaiorNumeroDisponivelPacObjetoCompra() + 1);
            }
            Util.adicionarObjetoEmLista(pacGrupoObjetoCompra.getObjetosCompra(), pacObjetoCompra);
            novoObjetoCompra();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private Integer getMaiorNumeroDisponivelPacObjetoCompra() {
        Integer maiorNumeroDisponivel = 0;
        if (selecionado.getGruposObjetoCompra() != null && !selecionado.getGruposObjetoCompra().isEmpty()) {
            for (PlanoAnualContratacaoGrupoObjetoCompra pacGrupo : selecionado.getGruposObjetoCompra()) {
                if (pacGrupo.getObjetosCompra() != null && !pacGrupo.getObjetosCompra().isEmpty()) {
                    for (PlanoAnualContratacaoObjetoCompra pacObjetoCompra : pacGrupo.getObjetosCompra()) {
                        if (pacObjetoCompra.getNumero().compareTo(maiorNumeroDisponivel) > 0) {
                            maiorNumeroDisponivel = pacObjetoCompra.getNumero();
                        }
                    }
                }
            }
        }
        return maiorNumeroDisponivel;
    }

    public void removerObjetoCompra(PlanoAnualContratacaoObjetoCompra obj) {
        pacGrupoObjetoCompra.getObjetosCompra().remove(obj);
    }

    public void validarObjetoCompra() {
        Util.validarCampos(pacObjetoCompra);
        ValidacaoException ve = new ValidacaoException();
        for (PlanoAnualContratacaoObjetoCompra obj : pacGrupoObjetoCompra.getObjetosCompra()) {
            if (obj.getObjetoCompra().equals(pacObjetoCompra.getObjetoCompra())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A objeto de compra já foi adicionado lista.");
            }
        }
        ve.lancarException();
    }

    public List<SelectItem> getTiposObjetoCompra() {
        return Util.getListSelectItemSemCampoVazio(TipoObjetoCompra.values());
    }

    public List<GrupoObjetoCompra> completarGrupoObjetoCompra(String parte) {
        return facade.getGrupoObjetoCompraFacade().buscarGruposAssociadoObjetoCompra(parte.trim(), null);
    }

    public void buscarGruposOc() {
        List<GrupoObjetoCompra> grupos = facade.getGrupoObjetoCompraFacade().buscarGruposAssociadoObjetoCompra("", tipoObjetoCompra);
        gruposVo = Lists.newArrayList();
        for (GrupoObjetoCompra grupo : grupos) {
            PlanoAnualContratacaoGrupoObjetoCompraVO grupoVo = new PlanoAnualContratacaoGrupoObjetoCompraVO(grupo);
            gruposVo.add(grupoVo);
        }
    }

    public void selecionarTodosGrupos(boolean selecinado) {
        if (hasGruposVo()) {
            for (PlanoAnualContratacaoGrupoObjetoCompraVO item : gruposVo) {
                item.setSelecionado(selecinado);
            }
        }
    }

    public boolean todosGruposSelcionados() {
        if (!hasGruposVo()) {
            return false;
        }
        boolean todosSelecionados = true;
        for (PlanoAnualContratacaoGrupoObjetoCompraVO item : gruposVo) {
            if (!item.getSelecionado()) {
                todosSelecionados = false;
                break;
            }
        }
        return todosSelecionados;
    }

    public List<ObjetoCompra> completarObjetoCompra(String parte) {
        if (hasGrupoOc() && pacGrupoObjetoCompra.getGrupoObjetoCompra() != null) {
            return facade.getObjetoCompraFacade().buscarPorGrupoObjetoCompra(parte.trim(), pacGrupoObjetoCompra.getGrupoObjetoCompra());
        }
        return Lists.newArrayList();
    }

    public void voltarParaGrupoOc() {
        tabIndex = 0;
        cancelarGrupoOc();
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        if (hierarquiaOrganizacional != null) {
            selecionado.setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
        }
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public PlanoAnualContratacaoGrupoObjetoCompra getPacGrupoObjetoCompra() {
        return pacGrupoObjetoCompra;
    }

    public void setPacGrupoObjetoCompra(PlanoAnualContratacaoGrupoObjetoCompra pacGrupoObjetoCompra) {
        this.pacGrupoObjetoCompra = pacGrupoObjetoCompra;
    }

    public PlanoAnualContratacaoObjetoCompra getPacObjetoCompra() {
        return pacObjetoCompra;
    }

    public void setPacObjetoCompra(PlanoAnualContratacaoObjetoCompra pacObjetoCompra) {
        this.pacObjetoCompra = pacObjetoCompra;
    }

    public List<PlanoAnualContratacaoGrupoObjetoCompraVO> getGruposVo() {
        return gruposVo;
    }

    public void setGruposVo(List<PlanoAnualContratacaoGrupoObjetoCompraVO> gruposVo) {
        this.gruposVo = gruposVo;
    }

    public Integer getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(Integer tabIndex) {
        this.tabIndex = tabIndex;
    }

    public TipoObjetoCompra getTipoObjetoCompra() {
        return tipoObjetoCompra;
    }

    public void setTipoObjetoCompra(TipoObjetoCompra tipoObjetoCompra) {
        this.tipoObjetoCompra = tipoObjetoCompra;
    }

    public ConverterAutoComplete getConverterPlanoAnualContratacaoObjetoCompra() {
        if (converterPlanoAnualContratacaoObjetoCompra == null) {
            converterPlanoAnualContratacaoObjetoCompra = new ConverterAutoComplete(PlanoAnualContratacaoObjetoCompra.class, facade.getPlanoAnualContratacaoObjetoCompraFacade());
        }
        return converterPlanoAnualContratacaoObjetoCompra;
    }
}
