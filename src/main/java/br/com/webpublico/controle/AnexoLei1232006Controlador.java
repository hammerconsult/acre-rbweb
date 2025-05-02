package br.com.webpublico.controle;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.AnexoLei1232006;
import br.com.webpublico.nfse.domain.AnexoLei1232006Faixa;
import br.com.webpublico.nfse.facades.AnexoLei1232006Facade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-anexo-lei-1232006", pattern = "/simples-nacional/anexo-lei-1232006/novo/",
        viewId = "/faces/tributario/simples-nacional/anexo-lei-1232006/edita.xhtml"),
    @URLMapping(id = "editar-anexo-lei-1232006", pattern = "/simples-nacional/anexo-lei-1232006/editar/#{anexoLei1232006Controlador.id}/",
        viewId = "/faces/tributario/simples-nacional/anexo-lei-1232006/edita.xhtml"),
    @URLMapping(id = "ver-anexo-lei-1232006", pattern = "/simples-nacional/anexo-lei-1232006/ver/#{anexoLei1232006Controlador.id}/",
        viewId = "/faces/tributario/simples-nacional/anexo-lei-1232006/visualizar.xhtml"),
    @URLMapping(id = "listar-anexo-lei-1232006", pattern = "/simples-nacional/anexo-lei-1232006/listar/",
        viewId = "/faces/tributario/simples-nacional/anexo-lei-1232006/lista.xhtml")
})
public class AnexoLei1232006Controlador extends PrettyControlador<AnexoLei1232006> implements CRUD {

    @EJB
    private AnexoLei1232006Facade facade;
    private AnexoLei1232006Faixa faixa;

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public AnexoLei1232006Controlador() {
        super(AnexoLei1232006.class);
    }

    public AnexoLei1232006Faixa getFaixa() {
        return faixa;
    }

    public void setFaixa(AnexoLei1232006Faixa faixa) {
        this.faixa = faixa;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/simples-nacional/anexo-lei-1232006/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-anexo-lei-1232006", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "ver-anexo-lei-1232006", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-anexo-lei-1232006", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    public void inserirFaixa() {
        faixa = new AnexoLei1232006Faixa();
        faixa.setAnexoLei1232006(selecionado);
    }

    public void cancelarFaixa() {
        faixa = null;
    }

    private void validarFaixa() throws ValidacaoException {
        faixa.validarCamposObrigatorios();
        ValidacaoException onpe = new ValidacaoException();
        if (selecionado.hasFaixa(faixa)) {
            onpe.adicionarMensagemDeOperacaoNaoPermitida("Está faixa já foi adicionada.");
        }
        onpe.lancarException();
    }

    public void adicionarFaixa() {
        try {
            validarFaixa();
            selecionado.setFaixas(Util.adicionarObjetoEmLista(selecionado.getFaixas(), faixa));
            cancelarFaixa();
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e);
        } catch (Exception e) {
            e.printStackTrace();
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void removerFaixa(AnexoLei1232006Faixa faixa) {
        selecionado.getFaixas().remove(faixa);
    }

    public void editarFaixa(AnexoLei1232006Faixa faixa) {
        this.faixa = faixa;
    }


}
