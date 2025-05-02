package br.com.webpublico.nfse.controladores;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.controle.Web;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.LoteRPS;
import br.com.webpublico.nfse.domain.XmlWsNfse;
import br.com.webpublico.nfse.domain.dtos.RpsNfseDTO;
import br.com.webpublico.nfse.facades.LoteRPSFacade;
import br.com.webpublico.nfse.facades.RPSFacade;
import br.com.webpublico.nfse.facades.XmlWsNfseFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Renato on 19/01/2019.
 */


@ManagedBean(name = "rpsControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listar-lote-rps", pattern = "/nfse/lote-rps/listar/", viewId = "/faces/tributario/nfse/rps/listaLote.xhtml"),
    @URLMapping(id = "listar-rps", pattern = "/nfse/rps/listar/", viewId = "/faces/tributario/nfse/rps/listaRps.xhtml"),
    @URLMapping(id = "listar-xml-rps", pattern = "/nfse/envio-xml/listar/", viewId = "/faces/tributario/nfse/rps/listaXml.xhtml"),
    @URLMapping(id = "ver-rps", pattern = "/nfse/lote-rps/ver/#{rpsControlador.id}/", viewId = "/faces/tributario/nfse/rps/visualizarLote.xhtml"),
})
public class RpsControlador extends PrettyControlador<LoteRPS> implements CRUD {

    @EJB
    private LoteRPSFacade loteRPSFacade;
    @EJB
    private RPSFacade rpsFacade;
    @EJB
    private XmlWsNfseFacade xmlWsNfseFacade;
    private List<RpsNfseDTO> listaRps;
    private XmlWsNfse xmlSelecionado;

    public RpsControlador() {
        super(LoteRPS.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return loteRPSFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/nfse/rps/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "ver-rps", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        listaRps = rpsFacade.buscarRpsPorLote(selecionado.getId());
    }

    public List<RpsNfseDTO> getListaRps() {
        return listaRps;
    }

    public void visualizarNota(RpsNfseDTO rps) {
        Web.navegacao(getUrlAtual(), "/nfse/nfse/ver/" + rps.getIdNotaFiscal() + "/");
    }

    public void selecionarXML(BigDecimal identificador) {
       xmlSelecionado = xmlWsNfseFacade.recuperar(identificador.longValue());
    }

    public XmlWsNfse getXmlSelecionado() {
        return xmlSelecionado;
    }

}
