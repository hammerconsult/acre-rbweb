package br.com.webpublico.controle;

import br.com.webpublico.controle.portaltransparencia.entidades.PaginaMenuHorizontalPaginaPrefeitura;
import br.com.webpublico.controle.portaltransparencia.entidades.PaginaMenuHorizontalPortal;
import br.com.webpublico.controle.portaltransparencia.entidades.SubPaginaMenuHorizontal;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.TipoAtoLegal;
import br.com.webpublico.enums.TipoQuadroPaginaInicialPortal;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.PaginaMenuHorizontalPortalFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@ManagedBean(name = "paginaMenuHorizontalPortalControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaPaginaMenuHorizontal", pattern = "/portal/transparencia/pagina-menu-horizontal/novo/", viewId = "/faces/portaltransparencia/pagina-menu-horizontal/edita.xhtml"),
    @URLMapping(id = "editarPaginaMenuHorizontal", pattern = "/portal/transparencia/pagina-menu-horizontal/editar/#{paginaMenuHorizontalPortalControlador.id}/", viewId = "/faces/portaltransparencia/pagina-menu-horizontal/edita.xhtml"),
    @URLMapping(id = "verPaginaMenuHorizontal", pattern = "/portal/transparencia/pagina-menu-horizontal/ver/#{paginaMenuHorizontalPortalControlador.id}/", viewId = "/faces/portaltransparencia/pagina-menu-horizontal/visualizar.xhtml"),
    @URLMapping(id = "listarPaginaMenuHorizontal", pattern = "/portal/transparencia/pagina-menu-horizontal/listar/", viewId = "/faces/portaltransparencia/pagina-menu-horizontal/lista.xhtml")
})
public class PaginaMenuHorizontalPortalControlador extends PrettyControlador<PaginaMenuHorizontalPortal> implements Serializable, CRUD {
    @EJB
    private PaginaMenuHorizontalPortalFacade facade;
    private PaginaMenuHorizontalPaginaPrefeitura paginaMenuHorizontalPaginaPrefeitura;
    private boolean mostrarCampoTipoAtoLegal;
    private boolean mostrarColunaTipoAtoLegal;
    private SubPaginaMenuHorizontal subPagina;

    public PaginaMenuHorizontalPortalControlador() {
        super(PaginaMenuHorizontalPortal.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/portal/transparencia/pagina-menu-horizontal/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novaPaginaMenuHorizontal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        novaPagina();
    }

    @URLAction(mappingId = "editarPaginaMenuHorizontal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        novaPagina();
        atribuirMostrarColunaTipoAtoLegal();
        ordenarPaginasPorOrdem();
    }

    @URLAction(mappingId = "verPaginaMenuHorizontal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
        atribuirMostrarColunaTipoAtoLegal();
        ordenarPaginasPorOrdem();
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            selecionado = facade.salvarNovaPaginaMenuHorizontal(selecionado);
            FacesUtil.addOperacaoRealizada("A configuração foi salva com sucesso.");
            redirecionarParaVer();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void novaPagina() {
        paginaMenuHorizontalPaginaPrefeitura = new PaginaMenuHorizontalPaginaPrefeitura();
        mostrarCampoTipoAtoLegal = false;
        subPagina = null;
    }

    public void removerPagina(PaginaMenuHorizontalPaginaPrefeitura obj) {
        selecionado.getPaginasPrefeituraPortal().remove(obj);
    }

    public void adicionarPagina() {
        try {
            validarListaPaginas();
            paginaMenuHorizontalPaginaPrefeitura.setPaginaMenuHorizontalPortal(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getPaginasPrefeituraPortal(), paginaMenuHorizontalPaginaPrefeitura);
            novaPagina();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void editarPagina(PaginaMenuHorizontalPaginaPrefeitura obj) {
        try {
            paginaMenuHorizontalPaginaPrefeitura = obj;
            FacesUtil.atualizarComponente("Formulario:tab-view-geral");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void salvarEdicao() {
        if (paginaMenuHorizontalPaginaPrefeitura.getPaginaPrefeituraPortal() != null) {
            paginaMenuHorizontalPaginaPrefeitura.setPaginaMenuHorizontalPortal(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getPaginasPrefeituraPortal(), paginaMenuHorizontalPaginaPrefeitura);
            novaPagina();
        }
    }

    public void validarListaPaginas() {
        ValidacaoException ve = new ValidacaoException();
        if (paginaMenuHorizontalPaginaPrefeitura.getNome() == null || paginaMenuHorizontalPaginaPrefeitura.getNome().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Nome de Exibição deve ser informado.");
        }
        if (paginaMenuHorizontalPaginaPrefeitura.getOrdem() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ordem deve ser informado.");
        }
        if (paginaMenuHorizontalPaginaPrefeitura.getId() == null) {
            for (PaginaMenuHorizontalPaginaPrefeitura pagina : selecionado.getPaginasPrefeituraPortal()) {
                if (!isPaginaAtoLegal()) {
                    if (pagina.getPaginaPrefeituraPortal() != null &&
                        pagina.getPaginaPrefeituraPortal().equals(paginaMenuHorizontalPaginaPrefeitura.getPaginaPrefeituraPortal())) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("Página já adicionada.");
                        novaPagina();
                    }
                } else {
                    if (pagina.getTipoAtoLegal() != null && pagina.getTipoAtoLegal().equals(paginaMenuHorizontalPaginaPrefeitura.getTipoAtoLegal())) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("Página já adicionada. O Tipo de Ato Legal selecionado já está na lista.");
                        novaPagina();
                    }
                }
            }
        }
        ve.lancarException();
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCampos(selecionado);
        if (selecionado.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Início de Vigência.");
        } else if (selecionado.getFimVigencia() != null && selecionado.getFimVigencia().before(selecionado.getInicioVigencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Fim de Vigência deve ser posterior ao Início de Vigência.");
        }

        if (Util.isListNullOrEmpty(selecionado.getPaginasPrefeituraPortal())) {
            ve.adicionarMensagemDeCampoObrigatorio("Ao menos uma página deve ser adicionada.");
        }
        ve.lancarException();
    }

    public List<SelectItem> getTiposAtoLegal() {
        return Util.getListSelectItem(TipoAtoLegal.values(), false);
    }

    public boolean isPaginaAtoLegal() {
        return paginaMenuHorizontalPaginaPrefeitura.getPaginaPrefeituraPortal() != null && Objects.equals(paginaMenuHorizontalPaginaPrefeitura.getPaginaPrefeituraPortal().getChave(), "ato-legal");
    }

    public void atribuirMostrarCampoTipoAtoLegal() {
        if (isPaginaAtoLegal()) {
            mostrarCampoTipoAtoLegal = true;
            mostrarColunaTipoAtoLegal = true;
        }
    }

    public void atribuirMostrarColunaTipoAtoLegal() {
        for (PaginaMenuHorizontalPaginaPrefeitura pagina : selecionado.getPaginasPrefeituraPortal()) {
            if (pagina.getPaginaPrefeituraPortal() != null &&
                Objects.equals(pagina.getPaginaPrefeituraPortal().getChave(), "ato-legal")) {
                mostrarColunaTipoAtoLegal = true;
                break;
            }
        }
    }

    public List<SelectItem> getTipos() {
        return Util.getListSelectItem(TipoQuadroPaginaInicialPortal.values());
    }

    public PaginaMenuHorizontalPaginaPrefeitura getPaginaMenuHorizontalPaginaPrefeitura() {
        return paginaMenuHorizontalPaginaPrefeitura;
    }

    public void setPaginaMenuHorizontalPaginaPrefeitura(PaginaMenuHorizontalPaginaPrefeitura paginaMenuHorizontalPaginaPrefeitura) {
        this.paginaMenuHorizontalPaginaPrefeitura = paginaMenuHorizontalPaginaPrefeitura;
    }

    public boolean isMostrarCampoTipoAtoLegal() {
        return mostrarCampoTipoAtoLegal;
    }

    public void setMostrarCampoTipoAtoLegal(boolean mostrarCampoTipoAtoLegal) {
        this.mostrarCampoTipoAtoLegal = mostrarCampoTipoAtoLegal;
    }

    public boolean isMostrarColunaTipoAtoLegal() {
        return mostrarColunaTipoAtoLegal;
    }

    public void setMostrarColunaTipoAtoLegal(boolean mostrarColunaTipoAtoLegal) {
        this.mostrarColunaTipoAtoLegal = mostrarColunaTipoAtoLegal;
    }

    private void redirecionarParaVer() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
    }

    public SubPaginaMenuHorizontal getSubPagina() {
        return subPagina;
    }

    public void setSubPagina(SubPaginaMenuHorizontal subPagina) {
        this.subPagina = subPagina;
    }

    public void removerSubPagina(SubPaginaMenuHorizontal subPagina) {
        paginaMenuHorizontalPaginaPrefeitura.getSubPaginas().remove(subPagina);
    }

    public void adicionarSubPagina() {
        try {
            validarAdicionarSubPagina();
            subPagina.setPaginaMenuHorizontal(paginaMenuHorizontalPaginaPrefeitura);
            Util.adicionarObjetoEmLista(paginaMenuHorizontalPaginaPrefeitura.getSubPaginas(), subPagina);
            limparSubPagina();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarAdicionarSubPagina() {
        Util.validarCampos(subPagina);
        ValidacaoException ve = new ValidacaoException();
        if (paginaMenuHorizontalPaginaPrefeitura.getSubPaginas().stream().anyMatch(subPaginaAdicionada -> subPaginaAdicionada.getTitulo().equals(subPagina.getTitulo()))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não deve ter Sub Paginas com o mesmo título.");
        }
        ve.lancarException();
    }

    public void editarSubPagina(SubPaginaMenuHorizontal subPaginaMenuHorizontal) {
        subPagina = (SubPaginaMenuHorizontal) Util.clonarObjeto(subPaginaMenuHorizontal);
    }

    public void atualizarSubPagina() {
        paginaMenuHorizontalPaginaPrefeitura.setPaginaMenuHorizontalPortal(selecionado);
        Util.adicionarObjetoEmLista(selecionado.getPaginasPrefeituraPortal(), paginaMenuHorizontalPaginaPrefeitura);
        novaPagina();
        FacesUtil.executaJavaScript("dialogSubPagina.hide()");
    }

    public void limparSubPagina() {
        subPagina = new SubPaginaMenuHorizontal();
    }

    public void fecharDialogSubPagina() {
        subPagina = null;
    }

    public void abrirDialogSubPagina(PaginaMenuHorizontalPaginaPrefeitura paginaMenuHorizontal, boolean validarPaginaMenuHorizontal) {
        try {
            if (validarPaginaMenuHorizontal) {
                validarListaPaginas();
            }
            if (paginaMenuHorizontal != null) {
                paginaMenuHorizontalPaginaPrefeitura = (PaginaMenuHorizontalPaginaPrefeitura) Util.clonarObjeto(paginaMenuHorizontal);
            }
            limparSubPagina();
            ordenarSubPaginasPorOrdem();
            FacesUtil.executaJavaScript("dialogSubPagina.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            FacesUtil.executaJavaScript("dialogSubPagina.hide()");
        }
    }

    private void ordenarPaginasPorOrdem() {
        Collections.sort(selecionado.getPaginasPrefeituraPortal(), new Comparator() {
            public int compare(Object o1, Object o2) {
                PaginaMenuHorizontalPaginaPrefeitura s1 = (PaginaMenuHorizontalPaginaPrefeitura) o1;
                PaginaMenuHorizontalPaginaPrefeitura s2 = (PaginaMenuHorizontalPaginaPrefeitura) o2;
                if (s1.getOrdem() != null && s2.getOrdem() != null) {
                    return s1.getOrdem().compareTo(s2.getOrdem());
                } else {
                    return 0;
                }
            }
        });
    }

    private void ordenarSubPaginasPorOrdem() {
        if (paginaMenuHorizontalPaginaPrefeitura != null) {
            Collections.sort(paginaMenuHorizontalPaginaPrefeitura.getSubPaginas(), new Comparator() {
                public int compare(Object o1, Object o2) {
                    SubPaginaMenuHorizontal s1 = (SubPaginaMenuHorizontal) o1;
                    SubPaginaMenuHorizontal s2 = (SubPaginaMenuHorizontal) o2;
                    if (s1.getOrdem() != null && s2.getOrdem() != null) {
                        return s1.getOrdem().compareTo(s2.getOrdem());
                    } else {
                        return 0;
                    }
                }
            });
        }
    }
}
