package br.com.webpublico.nfse.controladores;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.ArquivoNFSETRE;
import br.com.webpublico.nfse.facades.ArquivoNFSETREFacade;
import br.com.webpublico.nfse.facades.SingletonArquivosNfse;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.Future;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listar-arquivo-nfse-tre",
        pattern = "/tributario/nfse/arquivo-nfse-tre/listar/",
        viewId = "/faces/tributario/nfse/arquivo-nfse-tre/lista.xhtml"),

    @URLMapping(id = "novo-arquivo-nfse-tre",
        pattern = "/tributario/nfse/arquivo-nfse-tre/novo/",
        viewId = "/faces/tributario/nfse/arquivo-nfse-tre/edita.xhtml"),

    @URLMapping(id = "log-arquivo-nfse-tre",
        pattern = "/tributario/nfse/arquivo-nfse-tre/log/#{arquivoNFSETREControlador.id}/",
        viewId = "/faces/tributario/nfse/arquivo-nfse-tre/log.xhtml"),

    @URLMapping(id = "ver-arquivo-nfse-tre",
        pattern = "/tributario/nfse/arquivo-nfse-tre/ver/#{arquivoNFSETREControlador.id}/",
        viewId = "/faces/tributario/nfse/arquivo-nfse-tre/visualizar.xhtml")
})
public class ArquivoNFSETREControlador extends PrettyControlador<ArquivoNFSETRE> implements Serializable, CRUD {

    final Logger logger = LoggerFactory.getLogger(ArquivoNFSETREControlador.class);
    @EJB
    private ArquivoNFSETREFacade facade;
    @EJB
    private SingletonArquivosNfse singletonArquivosNfse;
    private AssistenteBarraProgresso assistente;
    private Future futureGeracaoDetalhes;
    private Future<ArquivoNFSETRE> futureSalvar;
    private StreamedContent streamedContent;


    public ArquivoNFSETREControlador() {
        super(ArquivoNFSETRE.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/nfse/arquivo-nfse-tre/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public AssistenteBarraProgresso getAssistente() {
        return assistente;
    }

    public void setAssistente(AssistenteBarraProgresso assistente) {
        this.assistente = assistente;
    }

    public StreamedContent getStreamedContent() {
        return streamedContent;
    }

    public void setStreamedContent(StreamedContent streamedContent) {
        this.streamedContent = streamedContent;
    }

    @Override
    @URLAction(mappingId = "novo-arquivo-nfse-tre", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setDataGeracao(facade.getSistemaFacade().getDataOperacao());
        selecionado.setUsuarioGeracao(facade.getSistemaFacade().getUsuarioCorrente());
        selecionado.setAbrangenciaInicial(DataUtil.adicionarMeses(selecionado.getDataGeracao(), -2));
        selecionado.setAbrangenciaFinal(selecionado.getDataGeracao());
        gerarAnoMesRemessa();
    }

    @URLAction(mappingId = "log-arquivo-nfse-tre", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void log() throws IOException {
        selecionado = facade.recuperar(getId());
        SingletonArquivosNfse.AcompanhamentoArquivoTRE arquivosTRE = singletonArquivosNfse.getArquivosTRE(selecionado);
        if (arquivosTRE != null) {
            assistente = arquivosTRE.getAssistente();
            futureGeracaoDetalhes = arquivosTRE.getFuture();
        } else {
            assistente = new AssistenteBarraProgresso();
            assistente.setTotal(selecionado.getTotalLinhas());
            futureGeracaoDetalhes = facade.gerarArquivoNfseTre(selecionado, assistente);
            singletonArquivosNfse.iniciarArquivoTRE(selecionado, assistente, futureGeracaoDetalhes);
        }
    }

    @URLAction(mappingId = "ver-arquivo-nfse-tre", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        prepararArquivoGeradoToDownload();
    }

    @Override
    public void salvar() {
        try {
            selecionado.realizarValidacoes();
            futureSalvar = facade.salvarArquivo(selecionado);
            FacesUtil.executaJavaScript("acompanharSalvar()");
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
            FacesUtil.executaJavaScript("aguarde.hide()");
        } catch (Exception e) {
            descobrirETratarException(e);
            FacesUtil.executaJavaScript("aguarde.hide()");
        }
    }

    public void acompanharSalvar() {
        if (futureSalvar != null && futureSalvar.isDone()) {
            try {
                selecionado = futureSalvar.get();
                futureSalvar = null;
                FacesUtil.executaJavaScript("finalizarSalvar()");
            } catch (Exception e) {
                logger.error("Erro ao pegar o selecionado da future do arquivo TRE");
            }
            futureSalvar = null;
        }
    }

    public void finalizarSalvar() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "log/" + selecionado.getId());
    }

    public void acompanharGeracaoDetalhes() {
        if (futureGeracaoDetalhes == null || futureGeracaoDetalhes.isDone() || futureGeracaoDetalhes.isCancelled()) {
            FacesUtil.executaJavaScript("pararTimer()");
            assistente.setDescricaoProcesso("Finalizando a geração do arquivo.");
            FacesUtil.executaJavaScript("finalizarGeracao()");
            singletonArquivosNfse.finalizarArquivoTRE(selecionado);
        }
    }

    public void finalizarGeracaoDetalhes() {
        selecionado = facade.recuperar(selecionado.getId());
        prepararArquivoGeradoToDownload();
    }


    private void prepararArquivoGeradoToDownload() {
        if (selecionado.getArquivoGerado() != null) {
            selecionado.getArquivoGerado().montarImputStream();
            streamedContent = new DefaultStreamedContent(selecionado.getArquivoGerado().getInputStream(),
                selecionado.getArquivoGerado().getMimeType(), selecionado.getArquivoGerado().getNome());
        }
    }

    public void irParaVisualizar() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
    }

    public void gerarAnoMesRemessa() {
        selecionado.setAnoRemessa(null);
        selecionado.setMesRemessa(null);
        if (selecionado.getDataGeracao() != null) {
            selecionado.setAnoRemessa(DataUtil.getAno(selecionado.getDataGeracao()));
            selecionado.setMesRemessa(DataUtil.getMes(selecionado.getDataGeracao()));
        }
    }
}
