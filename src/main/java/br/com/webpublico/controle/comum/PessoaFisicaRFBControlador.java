package br.com.webpublico.controle.comum;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.comum.PessoaFisicaRFB;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.comum.AtualizacaoPessoaFisicaRFBFacade;
import br.com.webpublico.negocios.comum.ImportacaoPessoaFisicaRFBFacade;
import br.com.webpublico.negocios.comum.PessoaFisicaRFBFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;
import java.util.concurrent.Future;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(
        id = "pessoaFisicaRFBListar",
        pattern = "/comum/pessoa-fisica-rfb/listar/",
        viewId = "/faces/comum/pessoafisicarfb/lista.xhtml"),
})
public class PessoaFisicaRFBControlador extends PrettyControlador<PessoaFisicaRFB> implements CRUD {

    @EJB
    private PessoaFisicaRFBFacade facade;
    @EJB
    private ImportacaoPessoaFisicaRFBFacade importacaoPessoaFisicaRFBFacade;
    @EJB
    private AtualizacaoPessoaFisicaRFBFacade atualizacaoPessoaFisicaRFBFacade;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private Future futureImportacao;
    private List<Future> futuresAtualizacao;

    @Override
    public PessoaFisicaRFBFacade getFacede() {
        return facade;
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/comum/pessoa-fisica-rfb/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void handleFileUpload(FileUploadEvent event) {
        try {
            assistenteBarraProgresso = new AssistenteBarraProgresso();
            futureImportacao = importacaoPessoaFisicaRFBFacade.importarArquivo(assistenteBarraProgresso, event.getFile().getInputstream());
            FacesUtil.executaJavaScript("closeDialog(dialogUpload)");
            FacesUtil.executaJavaScript("openDialog(dialogAcompanhamento)");
            FacesUtil.executaJavaScript("pollImportacao.start()");
            FacesUtil.atualizarComponente("formularioAcompanhamento");
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void acompanharImportacao() {
        if (futureImportacao.isDone() || futureImportacao.isCancelled()) {
            FacesUtil.executaJavaScript("pollImportacao.stop()");
            FacesUtil.executaJavaScript("closeDialog(dialogAcompanhamento)");
            FacesUtil.addInfo("Informação!", "Arquivo importado com sucesso.");
        }
    }

    public void atualizarDados() {
        try {
            assistenteBarraProgresso = new AssistenteBarraProgresso();
            assistenteBarraProgresso.setDescricaoProcesso("Atualizando pessoas físicas com os dados da RFB.");
            assistenteBarraProgresso.setTotal(facade.countNaoAtualizadas());
            futuresAtualizacao = Lists.newArrayList();
            for (int i = 0; i < 5; i++) {
                futuresAtualizacao.add(atualizacaoPessoaFisicaRFBFacade.atualizarDados(assistenteBarraProgresso));
            }
            FacesUtil.executaJavaScript("openDialog(dialogAcompanhamento)");
            FacesUtil.executaJavaScript("pollAtualizacao.start()");
            FacesUtil.atualizarComponente("formularioAcompanhamento");
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void acompanharAtualizacao() {
        for (Future future : futuresAtualizacao) {
            if (!future.isDone() && !future.isCancelled()) {
                return;
            }
        }
        FacesUtil.executaJavaScript("pollAtualizacao.stop()");
        FacesUtil.executaJavaScript("closeDialog(dialogAcompanhamento)");
        FacesUtil.addInfo("Informação!", "Dados atualizados com sucesso.");
    }

}
