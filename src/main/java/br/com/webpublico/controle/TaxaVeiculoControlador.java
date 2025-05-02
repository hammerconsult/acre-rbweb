package br.com.webpublico.controle;

import br.com.webpublico.entidades.TaxaVeiculo;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.TaxaVeiculoFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 23/09/14
 * Time: 08:30
 * To change this template use File | Settings | File Templates.
 */

@ManagedBean(name = "taxaVeiculoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "taxaVeiculoNovo", pattern = "/taxa-veiculo/novo/", viewId = "/faces/administrativo/frota/taxaveiculo/edita.xhtml"),
        @URLMapping(id = "taxaVeiculoListar", pattern = "/taxa-veiculo/listar/", viewId = "/faces/administrativo/frota/taxaveiculo/lista.xhtml"),
        @URLMapping(id = "taxaVeiculoEditar", pattern = "/taxa-veiculo/editar/#{taxaVeiculoControlador.id}/", viewId = "/faces/administrativo/frota/taxaveiculo/edita.xhtml"),
        @URLMapping(id = "taxaVeiculoVer", pattern = "/taxa-veiculo/ver/#{taxaVeiculoControlador.id}/", viewId = "/faces/administrativo/frota/taxaveiculo/visualizar.xhtml"),
})
public class TaxaVeiculoControlador extends PrettyControlador<TaxaVeiculo> implements Serializable, CRUD {

    @EJB
    private TaxaVeiculoFacade taxaVeiculoFacade;

    @Override
    public String getCaminhoPadrao() {
        return "/taxa-veiculo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return taxaVeiculoFacade;
    }

    public TaxaVeiculoControlador() {
        super(TaxaVeiculo.class);
    }

    @URLAction(mappingId = "taxaVeiculoNovo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "taxaVeiculoVer", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "taxaVeiculoEditar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public boolean validaRegrasEspecificas() {
        boolean validou = true;
        if(taxaVeiculoFacade.existeTaxaVeiculoCadastrada(selecionado)){
            validou = false;
            FacesUtil.addOperacaoNaoPermitida("Já existe uma taxa cadastrada com essa descrição.");
        }
        if(selecionado.getInicioVigencia() != null &&
                selecionado.getFimVigencia() != null &&
                selecionado.getInicioVigencia().compareTo(selecionado.getFimVigencia()) > 0){
            validou = false;
            FacesUtil.addOperacaoNaoPermitida("O fim de vigência não pode ser menor que o início de vigência.");
        }
        return validou;
    }

    public List<TaxaVeiculo> completaTaxasVeiculo(String parte) {
        return taxaVeiculoFacade.listaFiltrando(parte.trim(), "descricao");
    }
}
