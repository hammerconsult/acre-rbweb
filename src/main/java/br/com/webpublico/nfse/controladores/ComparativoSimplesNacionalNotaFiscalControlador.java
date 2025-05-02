package br.com.webpublico.nfse.controladores;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.nfse.domain.ComparativoSimplesNacionalNotaFiscal;
import br.com.webpublico.nfse.facades.ComparativoSimplesNacionalNotaFiscalFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.DefaultStreamedContent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Future;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {@URLMapping(id = "novoComparativoSimplesNacionalNotaFiscal", pattern = "/tributario/nfse/comparativo-simples-nacional-nota-fiscal/novo/", viewId = "/faces/tributario/nfse/comparativo-simples-nacional-nota-fiscal/edita.xhtml"),
                         @URLMapping(id = "listarComparativoSimplesNacionalNotaFiscal", pattern = "/tributario/nfse/comparativo-simples-nacional-nota-fiscal/listar/", viewId = "/faces/tributario/nfse/comparativo-simples-nacional-nota-fiscal/lista.xhtml"),
                         @URLMapping(id = "verComparativoSimplesNacionalNotaFiscal", pattern = "/tributario/nfse/comparativo-simples-nacional-nota-fiscal/ver/#{comparativoSimplesNacionalNotaFiscalControlador.id}/", viewId = "/faces/tributario/nfse/comparativo-simples-nacional-nota-fiscal/visualizar.xhtml")})
public class ComparativoSimplesNacionalNotaFiscalControlador extends PrettyControlador<ComparativoSimplesNacionalNotaFiscal> implements Serializable, CRUD {

    @EJB
    private ComparativoSimplesNacionalNotaFiscalFacade facade;
    private AssistenteBarraProgresso assistente;
    private Future<ComparativoSimplesNacionalNotaFiscal> future;

    public ComparativoSimplesNacionalNotaFiscalControlador() {
        super(ComparativoSimplesNacionalNotaFiscal.class);
    }

    @Override
    public ComparativoSimplesNacionalNotaFiscalFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/nfse/comparativo-simples-nacional-nota-fiscal/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public AssistenteBarraProgresso getAssistente() {
        return assistente;
    }

    public DefaultStreamedContent getFileDownload() {
        selecionado.getArquivo().montarImputStream();
        return new DefaultStreamedContent(selecionado.getArquivo().getInputStream(), "text/plain", selecionado.getArquivo().getNome());
    }

    @URLAction(mappingId = "novoComparativoSimplesNacionalNotaFiscal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verComparativoSimplesNacionalNotaFiscal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarArquivo();
    }

    private void recuperarArquivo() {
        selecionado.setArquivo(facade.getArquivoFacade().recuperaDependencias(selecionado.getArquivo().getId()));
    }

    public List<SelectItem> getMeses() {
        return Util.getListSelectItem(Mes.values(), false);
    }

    public void iniciarGeracao() {
        try {
            selecionado.validarCamposObrigatorios();
            assistente = new AssistenteBarraProgresso();
            future = facade.gerarComparativo(assistente, selecionado);
            FacesUtil.executaJavaScript("iniciarGeracao()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void acompanharGeracao() {
        if (future.isDone() || future.isCancelled()) {
            FacesUtil.executaJavaScript("pararTimer()");
            finalizarGeracao();
        }
    }

    private void finalizarGeracao() {
        ComparativoSimplesNacionalNotaFiscal comparativo = null;
        try {
            comparativo = future.get();
            FacesUtil.addOperacaoRealizada("Comparativo gerado com sucesso, você já pode baixar o arquivo.");
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + comparativo.getId() + "/");
        } catch (Exception e) {
            FacesUtil.executaJavaScript("closeDialog(dialogAcompanhamento)");
            FacesUtil.addErrorPadrao(e);
        }
    }
}
