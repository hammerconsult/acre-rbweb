/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.enums.LayoutArquivoBordero;
import br.com.webpublico.entidades.LayoutArquivoRemessa;
import br.com.webpublico.entidades.RegistroArquivoRemessa;
import br.com.webpublico.enums.TipoFaturaArquivoRemessa;
import br.com.webpublico.enums.TipoPagamentoArquivoRemessa;
import br.com.webpublico.enums.TipoRegistroArquivoRemessa;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.LayoutArquivoRemessaFacade;
import br.com.webpublico.util.ArquivoOBN600;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.util.Util;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author wiplash
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-layout-arquivo-remessa", pattern = "/layout-arquivo-remessa/novo/", viewId = "/faces/financeiro/orcamentario/arquivoremessa/layoutarquivoremessa/edita.xhtml"),
    @URLMapping(id = "editar-layout-arquivo-remessa", pattern = "/layout-arquivo-remessa/editar/#{layoutArquivoRemessaControlador.id}/", viewId = "/faces/financeiro/orcamentario/arquivoremessa/layoutarquivoremessa/edita.xhtml"),
    @URLMapping(id = "ver-layout-arquivo-remessa", pattern = "/layout-arquivo-remessa/ver/#{layoutArquivoRemessaControlador.id}/", viewId = "/faces/financeiro/orcamentario/arquivoremessa/layoutarquivoremessa/visualizar.xhtml"),
    @URLMapping(id = "listar-layout-arquivo-remessa", pattern = "/layout-arquivo-remessa/listar/", viewId = "/faces/financeiro/orcamentario/arquivoremessa/layoutarquivoremessa/lista.xhtml")
})
public class LayoutArquivoRemessaControlador extends PrettyControlador<LayoutArquivoRemessa> implements Serializable, CRUD {

    @EJB
    private LayoutArquivoRemessaFacade layoutArquivoRemessaFacade;
    @EJB
    private ArquivoOBN600 arquivoOBN600;
    private RegistroArquivoRemessa registroArquivoRemessa;

    public void setaLayoutArquivoBordero(AjaxBehaviorEvent event) {
        LayoutArquivoBordero lab = (LayoutArquivoBordero) ((UIOutput) event.getSource()).getValue();
        selecionado.setLayoutArquivoBordero(lab);
        if (selecionado.getLayoutArquivoBordero() != null) {
            if (selecionado.getLayoutArquivoBordero().equals(LayoutArquivoBordero.OBN600)) {
                gerarHeaderObn600();
                gerarRegistroUmObn600();
                gerarRegistroDoisObn600();
                gerarRegistroTresObn600();
                if (selecionado.getTipoFaturaArquivoRemessa() != null) {
                    if (selecionado.getTipoFaturaArquivoRemessa().equals(TipoFaturaArquivoRemessa.FATURA)) {
                        gerarRegistroQuatroObn600Fatura();
                    } else {
                        gerarRegistroQuatroObn600Convenio();
                    }
                }
                if (selecionado.getTipoPagamentoArquivoRemessa() != null) {
                    if (selecionado.getTipoPagamentoArquivoRemessa().equals(TipoPagamentoArquivoRemessa.GPS)) {
                        gerarRegistroCincoObn600GPS();
                    } else if (selecionado.getTipoPagamentoArquivoRemessa().equals(TipoPagamentoArquivoRemessa.DARF)) {
                        gerarRegistroCincoObn600DARF();
                    } else {
                        gerarRegistroCincoObn600DARFSimples();
                    }
                }
                gerarTrailerObn600();
            } else {
                apagaRegistrosLayout();
            }
        } else {
            apagaRegistrosLayout();
        }
    }

    public void alteraRegistroArquivo(ActionEvent evt) {
        RegistroArquivoRemessa rar = (RegistroArquivoRemessa) evt.getComponent().getAttributes().get("registro");
        registroArquivoRemessa = (RegistroArquivoRemessa) Util.clonarObjeto(rar);
    }

    public void salvarRegistroArquivo() {
        if (validaCamposRegistro()) {
            if (registroArquivoRemessa.getTipoRegistroArquivoRemessa().equals(TipoRegistroArquivoRemessa.HEADER)) {
                selecionado.setHeaderArquivoRemessas(Util.adicionarObjetoEmLista(selecionado.getHeaderArquivoRemessas(), registroArquivoRemessa));
            } else if (registroArquivoRemessa.getTipoRegistroArquivoRemessa().equals(TipoRegistroArquivoRemessa.TIPO_1)) {
                selecionado.setRegistroUmArquivoRemessas(Util.adicionarObjetoEmLista(selecionado.getRegistroUmArquivoRemessas(), registroArquivoRemessa));
            } else if (registroArquivoRemessa.getTipoRegistroArquivoRemessa().equals(TipoRegistroArquivoRemessa.TIPO_2)) {
                selecionado.setRegistroDoisArquivoRemessas(Util.adicionarObjetoEmLista(selecionado.getRegistroDoisArquivoRemessas(), registroArquivoRemessa));
            } else if (registroArquivoRemessa.getTipoRegistroArquivoRemessa().equals(TipoRegistroArquivoRemessa.TIPO_3)) {
                selecionado.setRegistroTresArquivoRemessas(Util.adicionarObjetoEmLista(selecionado.getRegistroTresArquivoRemessas(), registroArquivoRemessa));
            } else if (registroArquivoRemessa.getTipoRegistroArquivoRemessa().equals(TipoRegistroArquivoRemessa.TIPO_4)) {
                selecionado.setRegistroQuatroArquivoRemessas(Util.adicionarObjetoEmLista(selecionado.getRegistroQuatroArquivoRemessas(), registroArquivoRemessa));
            } else if (registroArquivoRemessa.getTipoRegistroArquivoRemessa().equals(TipoRegistroArquivoRemessa.TIPO_5)) {
                selecionado.setRegistroCincoArquivoRemessas(Util.adicionarObjetoEmLista(selecionado.getRegistroCincoArquivoRemessas(), registroArquivoRemessa));
            } else if (registroArquivoRemessa.getTipoRegistroArquivoRemessa().equals(TipoRegistroArquivoRemessa.TRAILER)) {
                selecionado.setTrailerArquivoRemessas(Util.adicionarObjetoEmLista(selecionado.getTrailerArquivoRemessas(), registroArquivoRemessa));
            }
            registroArquivoRemessa = null;
        }
    }

    public Boolean validaCamposRegistro() {
        if (registroArquivoRemessa.getPosicaoInicial() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório", "O campo Posição Inicial é obrigatório"));
            return false;
        }
        if (registroArquivoRemessa.getPosicaoInicial() <= 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório", "O campo Posição Inicial tem que ser maior que zero"));
            return false;
        }
        if (registroArquivoRemessa.getTamanho() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório", "O campo Tamanho é obrigatório"));
            return false;
        }
        if (registroArquivoRemessa.getTamanho() <= 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório", "O campo Tamanho tem que ser maior que zero"));
            return false;
        }
        if (registroArquivoRemessa.getPosicaoFinal() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório", "O campo Posição Final é obrigatório"));
            return false;
        }
        if (registroArquivoRemessa.getPosicaoFinal() <= 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório", "O campo Posição Final tem que ser maior que zero"));
            return false;
        }
        return true;
    }

    public void cancelarRegistroArquivo() {
        registroArquivoRemessa = null;
    }

    public void setaTipoFaturaArquivoRemessa(AjaxBehaviorEvent event) {
        TipoFaturaArquivoRemessa tfar = (TipoFaturaArquivoRemessa) ((UIOutput) event.getSource()).getValue();
        selecionado.setTipoFaturaArquivoRemessa(tfar);
        if (selecionado.getLayoutArquivoBordero() != null) {
            if (selecionado.getLayoutArquivoBordero().equals(LayoutArquivoBordero.OBN600)) {
                if (selecionado.getRegistroQuatroArquivoRemessas().size() > 0) {
                    List<RegistroArquivoRemessa> regQuatroAux = new ArrayList<>();
                    for (RegistroArquivoRemessa hqar : selecionado.getRegistroQuatroArquivoRemessas()) {
                        regQuatroAux.add(hqar);
                    }
                    for (RegistroArquivoRemessa hqar : regQuatroAux) {
                        selecionado.getRegistroQuatroArquivoRemessas().remove(hqar);
                    }
                }
                if (selecionado.getTipoFaturaArquivoRemessa().equals(TipoFaturaArquivoRemessa.FATURA)) {
                    gerarRegistroQuatroObn600Fatura();
                } else {
                    gerarRegistroQuatroObn600Convenio();
                }
            }
        }
    }

    public void setaTipoPagamentoArquivoRemessa(AjaxBehaviorEvent event) {
        TipoPagamentoArquivoRemessa tpar = (TipoPagamentoArquivoRemessa) ((UIOutput) event.getSource()).getValue();
        selecionado.setTipoPagamentoArquivoRemessa(tpar);
        if (selecionado.getLayoutArquivoBordero() != null) {
            if (selecionado.getLayoutArquivoBordero().equals(LayoutArquivoBordero.OBN600)) {
                if (selecionado.getRegistroCincoArquivoRemessas().size() > 0) {
                    List<RegistroArquivoRemessa> regCincoAux = new ArrayList<>();
                    for (RegistroArquivoRemessa rcar : selecionado.getRegistroCincoArquivoRemessas()) {
                        regCincoAux.add(rcar);
                    }
                    for (RegistroArquivoRemessa rcar : regCincoAux) {
                        selecionado.getRegistroCincoArquivoRemessas().remove(rcar);
                    }
                }
                if (selecionado.getTipoPagamentoArquivoRemessa().equals(TipoPagamentoArquivoRemessa.GPS)) {
                    gerarRegistroCincoObn600GPS();
                } else if (selecionado.getTipoPagamentoArquivoRemessa().equals(TipoPagamentoArquivoRemessa.DARF)) {
                    gerarRegistroCincoObn600DARF();
                } else {
                    gerarRegistroCincoObn600DARFSimples();
                }
            }
        }
    }

    public void apagaRegistrosLayout() {
        if (selecionado.getHeaderArquivoRemessas().size() > 0) {
            List<RegistroArquivoRemessa> regAux = new ArrayList<>();
            for (RegistroArquivoRemessa har : selecionado.getHeaderArquivoRemessas()) {
                regAux.add(har);
            }
            for (RegistroArquivoRemessa har : regAux) {
                selecionado.getHeaderArquivoRemessas().remove(har);
            }
        }
        if (selecionado.getRegistroUmArquivoRemessas().size() > 0) {
            List<RegistroArquivoRemessa> regUmAux = new ArrayList<>();
            for (RegistroArquivoRemessa ruar : selecionado.getRegistroUmArquivoRemessas()) {
                regUmAux.add(ruar);
            }
            for (RegistroArquivoRemessa ruar : regUmAux) {
                selecionado.getRegistroUmArquivoRemessas().remove(ruar);
            }
        }
        if (selecionado.getRegistroDoisArquivoRemessas().size() > 0) {
            List<RegistroArquivoRemessa> regDoisAux = new ArrayList<>();
            for (RegistroArquivoRemessa rdar : selecionado.getRegistroDoisArquivoRemessas()) {
                regDoisAux.add(rdar);
            }
            for (RegistroArquivoRemessa rdar : regDoisAux) {
                selecionado.getRegistroDoisArquivoRemessas().remove(rdar);
            }
        }
        if (selecionado.getRegistroTresArquivoRemessas().size() > 0) {
            List<RegistroArquivoRemessa> regTresAux = new ArrayList<>();
            for (RegistroArquivoRemessa rtar : selecionado.getRegistroTresArquivoRemessas()) {
                regTresAux.add(rtar);
            }
            for (RegistroArquivoRemessa rtar : regTresAux) {
                selecionado.getRegistroTresArquivoRemessas().remove(rtar);
            }
        }
        if (selecionado.getRegistroQuatroArquivoRemessas().size() > 0) {
            List<RegistroArquivoRemessa> regQuatroAux = new ArrayList<>();
            for (RegistroArquivoRemessa hqar : selecionado.getRegistroQuatroArquivoRemessas()) {
                regQuatroAux.add(hqar);
            }
            for (RegistroArquivoRemessa hqar : regQuatroAux) {
                selecionado.getRegistroQuatroArquivoRemessas().remove(hqar);
            }
        }
        if (selecionado.getRegistroCincoArquivoRemessas().size() > 0) {
            List<RegistroArquivoRemessa> regCincoAux = new ArrayList<>();
            for (RegistroArquivoRemessa rcar : selecionado.getRegistroCincoArquivoRemessas()) {
                regCincoAux.add(rcar);
            }
            for (RegistroArquivoRemessa rcar : regCincoAux) {
                selecionado.getRegistroCincoArquivoRemessas().remove(rcar);
            }
        }
        if (selecionado.getTrailerArquivoRemessas().size() > 0) {
            List<RegistroArquivoRemessa> trailerAux = new ArrayList<>();
            for (RegistroArquivoRemessa tar : selecionado.getTrailerArquivoRemessas()) {
                trailerAux.add(tar);
            }
            for (RegistroArquivoRemessa tar : trailerAux) {
                selecionado.getTrailerArquivoRemessas().remove(tar);
            }
        }
    }

    public Boolean verificaLayoutOBN600() {
        if (selecionado.getLayoutArquivoBordero() != null) {
            return selecionado.getLayoutArquivoBordero().equals(LayoutArquivoBordero.OBN600);
        } else {
            return false;
        }
    }

    private void gerarHeaderObn600() {

        selecionado.getHeaderArquivoRemessas().addAll(arquivoOBN600.gerarPosicaoHeaderObn600(selecionado));
    }

    private void gerarRegistroUmObn600() {
        selecionado.getRegistroUmArquivoRemessas().addAll(arquivoOBN600.gerarPosicaoRegistroUmObn600(selecionado));
    }

    private void gerarRegistroDoisObn600() {
        selecionado.getRegistroDoisArquivoRemessas().addAll(arquivoOBN600.gerarPosicaoRegistroDoisObn600(selecionado));
    }

    private void gerarRegistroTresObn600() {
        selecionado.getRegistroTresArquivoRemessas().addAll(arquivoOBN600.gerarPosicaoRegistroTresObn600(selecionado));
    }

    private void gerarRegistroQuatroObn600Fatura() {
        selecionado.getRegistroQuatroArquivoRemessas().addAll(arquivoOBN600.gerarPosicaoRegistroQuatroObn600Fatura(selecionado));
    }

    private void gerarRegistroQuatroObn600Convenio() {
        selecionado.getRegistroQuatroArquivoRemessas().addAll(arquivoOBN600.gerarPosicaoRegistroQuatroObn600Convenio(selecionado));
    }

    private void gerarRegistroCincoObn600GPS() {
        selecionado.getRegistroCincoArquivoRemessas().addAll(arquivoOBN600.gerarPosicaoRegistroCincoObn600GPS(selecionado));
    }

    private void gerarRegistroCincoObn600DARF() {
        selecionado.getRegistroCincoArquivoRemessas().addAll(arquivoOBN600.gerarPosicaoRegistroCincoObn600DARF(selecionado));
    }

    private void gerarRegistroCincoObn600DARFSimples() {
        selecionado.getRegistroCincoArquivoRemessas().addAll(arquivoOBN600.gerarPosicaoRegistroCincoObn600DARFSimples(selecionado));
    }

    private void gerarTrailerObn600() {
        selecionado.getTrailerArquivoRemessas().addAll(arquivoOBN600.gerarPosicaoTrailerObn600(selecionado));
    }

    public LayoutArquivoRemessaControlador() {
        super(LayoutArquivoRemessa.class);
    }

    public List<SelectItem> getListaLayoutArquivoBordero() {
        List<SelectItem> list = new ArrayList<>();
        list.add(new SelectItem(null, ""));
        for (LayoutArquivoBordero object : LayoutArquivoBordero.values()) {
            list.add(new SelectItem(object, object.getDescricao()));
        }
        return list;
    }

    public List<SelectItem> getListaTipoFatura() {
        List<SelectItem> list = new ArrayList<>();
        list.add(new SelectItem(null, ""));
        for (TipoFaturaArquivoRemessa object : TipoFaturaArquivoRemessa.values()) {
            list.add(new SelectItem(object, object.getDescricao()));
        }
        return list;
    }

    public List<SelectItem> getListaTipoPagamento() {
        List<SelectItem> list = new ArrayList<>();
        list.add(new SelectItem(null, ""));
        for (TipoPagamentoArquivoRemessa object : TipoPagamentoArquivoRemessa.values()) {
            list.add(new SelectItem(object, object.getDescricao()));
        }
        return list;
    }

    @Override
    @URLAction(mappingId = "novo-layout-arquivo-remessa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "ver-layout-arquivo-remessa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        recuperarEditarVer();
    }

    @Override
    @URLAction(mappingId = "editar-layout-arquivo-remessa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        recuperarEditarVer();
    }

    public void recuperarEditarVer() {
    }

    public RegistroArquivoRemessa getRegistroArquivoRemessa() {
        return registroArquivoRemessa;
    }

    public void setRegistroArquivoRemessa(RegistroArquivoRemessa registroArquivoRemessa) {
        this.registroArquivoRemessa = registroArquivoRemessa;
    }

    @Override
    public void salvar() {
        try {
            if (operacao.equals(Operacoes.NOVO)) {
                layoutArquivoRemessaFacade.validaExitenciaConfiguracaoMesmolayout(selecionado.getLayoutArquivoBordero());
            }
            super.salvar();
        } catch (Exception ex) {
            FacesUtil.addError("Erro!", ex.getMessage());
        }
    }

    @Override
    public AbstractFacade getFacede() {
        return layoutArquivoRemessaFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/layout-arquivo-remessa/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void onEdit(RowEditEvent event) {
//        FacesMessage msg = new FacesMessage("Car Edited", ((Car) event.getObject()).getModel());


    }

    public void onCancel(RowEditEvent event) {
//        FacesMessage msg = new FacesMessage("Car Cancelled", ((Car) event.getObject()).getModel());

    }
}
