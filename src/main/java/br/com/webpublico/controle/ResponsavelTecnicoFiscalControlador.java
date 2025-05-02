package br.com.webpublico.controle;

import br.com.webpublico.entidades.Contrato;
import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.ProfissionalConfea;
import br.com.webpublico.entidades.ResponsavelTecnicoFiscal;
import br.com.webpublico.enums.ObraAtribuicao;
import br.com.webpublico.enums.TipoFiscalContrato;
import br.com.webpublico.enums.TipoResponsavelFiscal;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ResponsavelTecnicoFiscalFacade;
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
import java.util.List;

/**
 * Created by Desenvolvimento on 29/03/2016.
 */

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-tecnico-fiscal-obra", pattern = "/fiscal/novo/", viewId = "/faces/administrativo/obras/responsaveltecnicoefiscal/edita.xhtml"),
    @URLMapping(id = "editar-tecnico-fiscal-obra", pattern = "/fiscal/editar/#{responsavelTecnicoFiscalControlador.id}/", viewId = "/faces/administrativo/obras/responsaveltecnicoefiscal/edita.xhtml"),
    @URLMapping(id = "ver-tecnico-fiscal-obra", pattern = "/fiscal/ver/#{responsavelTecnicoFiscalControlador.id}/", viewId = "/faces/administrativo/obras/responsaveltecnicoefiscal/visualizar.xhtml"),
    @URLMapping(id = "listar-tecnico-fiscal-obra", pattern = "/fiscal/listar/", viewId = "/faces/administrativo/obras/responsaveltecnicoefiscal/lista.xhtml")
})
public class ResponsavelTecnicoFiscalControlador extends PrettyControlador<ResponsavelTecnicoFiscal> implements Serializable, CRUD {

    @EJB
    private ResponsavelTecnicoFiscalFacade responsavelTecnicoFiscalFacade;

    public ResponsavelTecnicoFiscalControlador() {
        super(ResponsavelTecnicoFiscal.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return responsavelTecnicoFiscalFacade;
    }

    @URLAction(mappingId = "novo-tecnico-fiscal-obra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "ver-tecnico-fiscal-obra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-tecnico-fiscal-obra", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/fiscal/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<SelectItem> getTiposDeFiscal() {
        return Util.getListSelectItem(TipoFiscalContrato.values());
    }

    public List<SelectItem> getAtribuicaoFiscal() {
        return Util.getListSelectItem(ObraAtribuicao.values());
    }

//    public List<SelectItem> getTiposART() {
//        return Util.getListSelectItem(TipoArt.values());
//    }

    public List<SelectItem> getTiposResponsavel() {
        return Util.getListSelectItem(TipoResponsavelFiscal.values());
    }

    public List<ContratoFP> completaContratoFP(String parte) {
        return responsavelTecnicoFiscalFacade.getContratoFPFacade().recuperarFiltrandoContratosVigentesEm(parte.trim(), UtilRH.getDataOperacao());
    }

//    public List<AtoLegal> completaAtoLegal(String parte) {
//        return responsavelTecnicoFiscalFacade.getAtoLegalFacade().listaFiltrandoAtoLegal(parte.trim());
//    }

    public List<ProfissionalConfea> completaProfissionalConfea(String parte) {
        return responsavelTecnicoFiscalFacade.getProfissionalConfeaFacade().listaFiltrando(parte.trim(), "codigo", "tituloMasculino", "tituloFeminino", "tituloAbreviado");
    }

    public List<Contrato> completarContratoFirmado(String parte) {
        return responsavelTecnicoFiscalFacade.getContratoFacade().buscarContratoPorNumeroOrExercicio(parte.trim());
    }

    @Override
    public boolean validaRegrasEspecificas() {
        try {
            selecionado.validarConfirmacao();
            return true;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            return ve.validou;
        }
    }

}
