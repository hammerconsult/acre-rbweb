/*
 * Codigo gerado automaticamente em Fri Oct 14 20:25:32 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.OcorrenciaObjetoFrota;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.OcorrenciaObjetoFrotaFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "ocorrenciaObjetoFrotaNovo", pattern = "/frota/ocorrencia/novo/", viewId = "/faces/administrativo/frota/ocorrencia/edita.xhtml"),
    @URLMapping(id = "ocorrenciaObjetoFrotaListar", pattern = "/frota/ocorrencia/listar/", viewId = "/faces/administrativo/frota/ocorrencia/lista.xhtml"),
    @URLMapping(id = "ocorrenciaObjetoFrotaEditar", pattern = "/frota/ocorrencia/editar/#{ocorrenciaObjetoFrotaControlador.id}/", viewId = "/faces/administrativo/frota/ocorrencia/edita.xhtml"),
    @URLMapping(id = "ocorrenciaObjetoFrotaVer", pattern = "/frota/ocorrencia/ver/#{ocorrenciaObjetoFrotaControlador.id}/", viewId = "/faces/administrativo/frota/ocorrencia/visualizar.xhtml"),
})
public class OcorrenciaObjetoFrotaControlador extends PrettyControlador<OcorrenciaObjetoFrota> implements Serializable, CRUD {

    @EJB
    private OcorrenciaObjetoFrotaFacade ocorrenciaObjetoFrotaFacade;

    public OcorrenciaObjetoFrotaControlador() {
        super(OcorrenciaObjetoFrota.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return ocorrenciaObjetoFrotaFacade;
    }

    @URLAction(mappingId = "ocorrenciaObjetoFrotaNovo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "ocorrenciaObjetoFrotaVer", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "ocorrenciaObjetoFrotaEditar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/frota/ocorrencia/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void novoVeiculo() {
        Web.navegacao(getCaminhoOrigem(), "/frota/veiculo/novo/", selecionado);
    }

    public void novoTipoOcorrenciaObjetoFrota() {
        Web.navegacao(getCaminhoOrigem(), "/frota/tipo-ocorrencia/novo/", selecionado);
    }


    public void processaMudancaTipoObjetoFrota() {
        selecionado.setObjetoFrota(null);
    }

    @Override
    public boolean validaRegrasEspecificas() {
        boolean validou = true;
        if (selecionado.getDataOcorrencia().compareTo(selecionado.getObjetoFrota().getDataAquisicao()) < 0) {
            validou = false;
            FacesUtil.addOperacaoNaoPermitida("A Data da Ocorrência não pode ser inferior a Data de Aquisição do Veículo ou Equipamento/Máquina.");
        }
        return validou;
    }
}
