package br.com.webpublico.controle;

import br.com.webpublico.entidades.Adesao;
import br.com.webpublico.entidades.AtaRegistroPreco;
import br.com.webpublico.entidades.Documento;
import br.com.webpublico.entidades.UnidadeExterna;
import br.com.webpublico.enums.TipoAdesao;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AdesaoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 10/04/14
 * Time: 18:20
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-adesao", pattern = "/adesao/novo/", viewId = "/faces/administrativo/licitacao/adesao/edita.xhtml"),
        @URLMapping(id = "editar-adesao", pattern = "/adesao/editar/#{adesaoControlador.id}/", viewId = "/faces/administrativo/licitacao/adesao/edita.xhtml"),
        @URLMapping(id = "ver-adesao", pattern = "/adesao/ver/#{adesaoControlador.id}/", viewId = "/faces/administrativo/licitacao/adesao/visualizar.xhtml"),
        @URLMapping(id = "listar-adesao", pattern = "/adesao/listar/", viewId = "/faces/administrativo/licitacao/adesao/lista.xhtml")
})
public class AdesaoControlador extends PrettyControlador<Adesao> implements Serializable, CRUD {

    @EJB
    private AdesaoFacade adesaoFacade;
    private ConverterAutoComplete converterAtaDeRegistroDePreco;

    public AdesaoControlador() {
        super(Adesao.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/adesao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return adesaoFacade;
    }

    @Override
    @URLAction(mappingId = "novo-adesao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        inicializarDados();
    }

    @Override
    @URLAction(mappingId = "editar-adesao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    @URLAction(mappingId = "ver-adesao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    public void cancelar() {
        super.cancelar();
    }

    @Override
    public void salvar() {
        if (podeSalvar()) {
            super.salvar();
        } else {
            FacesUtil.addError("Atenção", "Essa Ata de Registro de Preço já possui o limite de Adesão.");
        }
    }

    public boolean podeSalvar() {
        if (adesaoFacade.recuperarAdesaoDaAtaDeRegistroDePreco(selecionado.getAtaDeRegistroDePreco()).size() >= 5 && selecionado.getId() == null) {
            return false;
        }
        return true;
    }

    public void inicializarDados() {
        selecionado.setData(adesaoFacade.getSistemaFacade().getDataOperacao());
        selecionado.setTipoAdesao(TipoAdesao.EXTERNA);
        selecionado.setAdesaoAceita(Boolean.TRUE);
    }

    public ConverterAutoComplete getConverterAtaDeRegistroDePreco() {
        if (converterAtaDeRegistroDePreco == null) {
            converterAtaDeRegistroDePreco = new ConverterAutoComplete(AtaRegistroPreco.class, adesaoFacade.getAtaRegistroPrecoFacade());
        }
        return converterAtaDeRegistroDePreco;
    }

    public List<AtaRegistroPreco> listarAtasDeRegistroDePreco() {
        return adesaoFacade.getAtaRegistroPrecoFacade().recuperarAtasDeRegistroDePreco();
    }

    public List<Documento> completaDocumento(String parte) {
        return adesaoFacade.getDocumentoFacade().listaDecrescente();
    }

    public List<UnidadeExterna> completaUnidadeExterna(String parte) {
        return adesaoFacade.getUnidadeExternaFacade().listaDecrescente();
    }

    public List<Adesao> recuperarAdesaoDaAtaDeRegistroDePreco() {
        if (selecionado.getAtaDeRegistroDePreco() != null) {
            return adesaoFacade.recuperarAdesaoDaAtaDeRegistroDePreco(selecionado.getAtaDeRegistroDePreco());
        }
        return new ArrayList<>();
    }

    public void carregarAdesoes() {
        if (podeCarregar()) {
            FacesUtil.executaJavaScript("dialogAdesao.show()");
        }
    }

    private boolean podeCarregar() {
        if (selecionado.getAtaDeRegistroDePreco() == null) {
            FacesUtil.addCampoObrigatorio("É obrigatório informar a ata de registro de preço antes de visualizar as adesões.");
            return false;
        }
        return true;
    }
}
