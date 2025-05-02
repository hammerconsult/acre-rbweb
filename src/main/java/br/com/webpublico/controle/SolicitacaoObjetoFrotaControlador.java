/*
 * Codigo gerado automaticamente em Fri Nov 25 20:23:22 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.SolicitacaoObjetoFrota;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoObjetoFrota;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.SolicitacaoObjetoFrotaFacade;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "solicitacaoObjetoFrotaNovo", pattern = "/frota/solicitacao/novo/", viewId = "/faces/administrativo/frota/solicitacao/edita.xhtml"),
    @URLMapping(id = "solicitacaoObjetoFrotaListar", pattern = "/frota/solicitacao/listar/", viewId = "/faces/administrativo/frota/solicitacao/lista.xhtml"),
    @URLMapping(id = "solicitacaoObjetoFrotaEditar", pattern = "/frota/solicitacao/editar/#{solicitacaoObjetoFrotaControlador.id}/", viewId = "/faces/administrativo/frota/solicitacao/edita.xhtml"),
    @URLMapping(id = "solicitacaoObjetoFrotaVer", pattern = "/frota/solicitacao/ver/#{solicitacaoObjetoFrotaControlador.id}/", viewId = "/faces/administrativo/frota/solicitacao/visualizar.xhtml"),
})
public class SolicitacaoObjetoFrotaControlador extends PrettyControlador<SolicitacaoObjetoFrota> implements Serializable, CRUD {

    @EJB
    private SolicitacaoObjetoFrotaFacade facade;

    public SolicitacaoObjetoFrotaControlador() {
        super(SolicitacaoObjetoFrota.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/frota/solicitacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "solicitacaoObjetoFrotaNovo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        inicializar();
    }

    @URLAction(mappingId = "solicitacaoObjetoFrotaVer", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "solicitacaoObjetoFrotaEditar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    private void inicializar() {
        selecionado.setRealizadaEm(new Date());
        selecionado.setUsuarioCadastro(facade.getSistemaFacade().getUsuarioCorrente());
    }

    public List<SelectItem> tiposDeObjetoFrota() {
        return Util.getListSelectItem(Arrays.asList(TipoObjetoFrota.values()));
    }

    public List<SolicitacaoObjetoFrota> completaSolicitacaoVeiculo(String parte) {
        return facade.buscarSolicitacoesPorPessoaSemReserva(parte, facade.getSistemaFacade().getUnidadeOrganizacionalAdministrativaCorrente());
    }

    public List<HierarquiaOrganizacional> completarUnidadeSolicitante(String parte) {
        return facade.getHierarquiaOrganizacionalFacade().buscarHierarquiaUsuarioPorTipo(
            parte.trim(),
            facade.getSistemaFacade().getUsuarioCorrente(),
            facade.getSistemaFacade().getDataOperacao(),
            TipoHierarquiaOrganizacional.ADMINISTRATIVA);
    }

    public List<SelectItem> getTiposDeObjetoFrota() {
        return Util.getListSelectItem(TipoObjetoFrota.values());
    }

    public void processaMudancaTipoObjetoFrota() {

    }

    @Override
    public boolean validaRegrasEspecificas() {
        boolean validou = true;
        if (selecionado.getDataDevolucao() != null && selecionado.getDataRetirada() != null) {
            if (selecionado.getDataDevolucao().compareTo(selecionado.getDataRetirada()) < 0) {
                validou = false;
                FacesUtil.addOperacaoNaoPermitida("A Data de Devolução não pode ser inferior a Data de Retirada.");
            }
        }
        return validou;
    }

    public void redirecionarParaSolicitacao(SolicitacaoObjetoFrota selecionado) {
        this.selecionado = selecionado;
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
    }

}
