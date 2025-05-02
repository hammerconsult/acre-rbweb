/*
 * Codigo gerado automaticamente em Mon Sep 05 15:28:59 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.util.*;

@ManagedBean(name = "disponibilidadeControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoDisponibilidadePorExtincaoCargo", pattern = "/disponibilidade/novo/#{disponibilidadeControlador.id}/", viewId = "/faces/rh/administracaodepagamento/disponibilidade/edita.xhtml"),
        @URLMapping(id = "listarDisponibilidade", pattern = "/disponibilidade/listar/", viewId = "/faces/rh/administracaodepagamento/disponibilidade/lista.xhtml"),
        @URLMapping(id = "verDisponibilidade", pattern = "/disponibilidade/ver/#{disponibilidadeControlador.id}/", viewId = "/faces/rh/administracaodepagamento/disponibilidade/visualizar.xhtml")
})
public class DisponibilidadeControlador extends PrettyControlador<Disponibilidade> implements Serializable, CRUD {

    @EJB
    private DisponibilidadeFacade disponibilidadeFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private ExtincaoCargoFacade extincaoCargoFacade;
    @EJB
    private SituacaoFuncionalFacade situacaoFuncionalFacade;
    private Boolean porExtincao;
    private Boolean todosContratosSelecionados;

    public DisponibilidadeControlador() {
        super(Disponibilidade.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return disponibilidadeFacade;
    }

    public Boolean getTodosContratosSelecionados() {
        return todosContratosSelecionados;
    }

    public void setTodosContratosSelecionados(Boolean todosContratosSelecionados) {
        this.todosContratosSelecionados = todosContratosSelecionados;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/disponibilidade/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoDisponibilidadePorExtincaoCargo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoPorExtincao() {
        super.novo();
        porExtincao = Boolean.TRUE;
        selecionado.setExtincaoCargo(extincaoCargoFacade.recuperar(getId()));
        if (selecionado.getExtincaoCargo().getDisponibilidade() != null) {
            selecionado = disponibilidadeFacade.recuperar(selecionado.getExtincaoCargo().getDisponibilidade().getId());
        }
        recuperarContratosVigentesNosCargosDaExtincaoSelecionada();
    }

    @Override
    @URLAction(mappingId = "verDisponibilidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    public List<ExtincaoCargo> completaExtincaoCargo(String parte) {
        return extincaoCargoFacade.completaExtincaoCargo(parte.trim());
    }

    public void recuperarContratosVigentesNosCargosDaExtincaoSelecionada() {
        if (selecionado != null && selecionado.getExtincaoCargo() != null) {
            List<ContratoFP> contratos = disponibilidadeFacade.recuperarContratosVigentesNosCargosDaExtincao(selecionado.getExtincaoCargo());
            for (ContratoFP c : contratos){
                if (!selecionado.selecionarContrato(c)){
                    ItemDisponibilidade id = new ItemDisponibilidade();
                    id.setContratoFP(c);
                    id.setSelecionadoEmLista(false);
                    selecionado.getItensDiponibilidade().add(id);
                }
            }
        }
    }

    public List<Cargo> recuperarCargosDaExtincao() {
        return extincaoCargoFacade.recuperarCargosDaExtincao(selecionado.getExtincaoCargo());
    }

    public Boolean getPorExtincao() {
        return porExtincao;
    }

    public void setPorExtincao(Boolean porExtincao) {
        this.porExtincao = porExtincao;
    }

    public void adicionarNaListaDeContratosSelecionados(ActionEvent ev) {
        ItemDisponibilidade id = (ItemDisponibilidade) ev.getComponent().getAttributes().get("item");
        id.setSelecionadoEmLista(true);
        id.setDisponibilidade(selecionado);
        if (id.getDataDisponibilidade() == null) {
            id.setDataDisponibilidade(selecionado.getExtincaoCargo().getDataExtincao());
        }
    }

    public void removerDaListaDeContratosSelecionados(ActionEvent ev) {
        ItemDisponibilidade id = (ItemDisponibilidade) ev.getComponent().getAttributes().get("item");
        id.setSelecionadoEmLista(false);
        id.setDataDisponibilidade(null);
    }

    public void selecionarTodosOsContratos() {
        todosContratosSelecionados = Boolean.TRUE;
        for(ItemDisponibilidade id : selecionado.getItensDiponibilidade()){
            id.setSelecionadoEmLista(true);
            id.setDisponibilidade(selecionado);
            if (id.getDataDisponibilidade() == null) {
                id.setDataDisponibilidade(selecionado.getExtincaoCargo().getDataExtincao());
            }
        }
    }

    public void removerTodosOsContratos() {
        todosContratosSelecionados = Boolean.FALSE;
        for(ItemDisponibilidade id : selecionado.getItensDiponibilidade()){
            id.setSelecionadoEmLista(false);
            id.setDataDisponibilidade(null);
        }
    }

    @Override
    public void redireciona() {
        if (porExtincao){
            FacesUtil.redirecionamentoInterno("/extincao-cargo/listar/");
            return;
        }
        super.redireciona();
    }
}
