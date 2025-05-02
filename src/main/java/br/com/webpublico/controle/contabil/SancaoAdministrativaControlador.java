package br.com.webpublico.controle.contabil;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.Contrato;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.UnidadeGestora;
import br.com.webpublico.entidades.VeiculoDePublicacao;
import br.com.webpublico.entidades.contabil.SancaoAdministrativa;
import br.com.webpublico.enums.TipoSituacaoSancao;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.contabil.execucao.SancaoAdministrativaFacade;
import br.com.webpublico.util.Util;
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
    @URLMapping(id = "novoSancaoAdministrativa", pattern = "/sancao-administrativa/novo/", viewId = "/faces/financeiro/sancao-administrativa/edita.xhtml"),
    @URLMapping(id = "editarSancaoAdministrativa", pattern = "/sancao-administrativa/editar/#{sancaoAdministrativaControlador.id}/", viewId = "/faces/financeiro/sancao-administrativa/edita.xhtml"),
    @URLMapping(id = "listarSancaoAdministrativa", pattern = "/sancao-administrativa/listar/", viewId = "/faces/financeiro/sancao-administrativa/lista.xhtml"),
    @URLMapping(id = "verSancaoAdministrativa", pattern = "/sancao-administrativa/ver/#{sancaoAdministrativaControlador.id}/", viewId = "/faces/financeiro/sancao-administrativa/visualizar.xhtml")
})
public class SancaoAdministrativaControlador extends PrettyControlador<SancaoAdministrativa> implements Serializable, CRUD {
    @EJB
    private SancaoAdministrativaFacade facade;

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public SancaoAdministrativaControlador() {
        super(SancaoAdministrativa.class);
    }

    @URLAction(mappingId = "novoSancaoAdministrativa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editarSancaoAdministrativa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verSancaoAdministrativa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/sancao-administrativa/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<Pessoa> completarPessoasAtivas(String parte) {
        return facade.buscarPessoasAtivas(parte);
    }

    public List<UnidadeGestora> completarUnidadesGestoras(String parte) {
        return facade.buscarUnidadesGestoras(parte);
    }

    public List<Contrato> completarContratos(String parte) {
        return facade.buscarContratos(parte);
    }

    public List<VeiculoDePublicacao> completarVeiculosDePublicacao(String parte) {
        return facade.buscarVeiculosDePublicacao(parte);
    }

    public List<SelectItem> getTiposSituacoesSancoes() {
        return Util.getListSelectItem(TipoSituacaoSancao.values(), false);
    }
}
