package br.com.webpublico.controle;

import br.com.webpublico.entidades.BaixaObjetoFrota;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.BaixaObjetoFrotaFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 07/11/14
 * Time: 15:32
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "baixaObjetoFrotaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "baixaVeicEquiNovo", pattern = "/baixa-veiculo-equipamento-maquina/novo/", viewId = "/faces/administrativo/frota/baixa/edita.xhtml"),
    @URLMapping(id = "baixaVeicEquiListar", pattern = "/baixa-veiculo-equipamento-maquina/listar/", viewId = "/faces/administrativo/frota/baixa/lista.xhtml"),
    @URLMapping(id = "baixaVeicEquiEditar", pattern = "/baixa-veiculo-equipamento-maquina/editar/#{baixaObjetoFrotaControlador.id}/", viewId = "/faces/administrativo/frota/baixa/edita.xhtml"),
    @URLMapping(id = "baixaVeicEquiVer", pattern = "/baixa-veiculo-equipamento-maquina/ver/#{baixaObjetoFrotaControlador.id}/", viewId = "/faces/administrativo/frota/baixa/visualizar.xhtml"),
})
public class BaixaObjetoFrotaControlador extends PrettyControlador<BaixaObjetoFrota> implements Serializable, CRUD {

    @EJB
    private BaixaObjetoFrotaFacade baixaObjetoFrotaFacade;

    public BaixaObjetoFrotaControlador() {
        super(BaixaObjetoFrota.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/baixa-veiculo-equipamento-maquina/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return baixaObjetoFrotaFacade;
    }

    @URLAction(mappingId = "baixaVeicEquiNovo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        inicializarBaixaObjetoFrota();
    }

    @URLAction(mappingId = "baixaVeicEquiVer", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "baixaVeicEquiEditar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    private void inicializarBaixaObjetoFrota() {
        selecionado.setDataBaixa(baixaObjetoFrotaFacade.getSistemaFacade().getDataOperacao());
        selecionado.setResponsavel(baixaObjetoFrotaFacade.getSistemaFacade().getUsuarioCorrente());
    }
}
