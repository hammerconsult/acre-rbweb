package br.com.webpublico.controle;

import br.com.webpublico.entidadesauxiliares.ImportacaoAtributoCadastroImobiliario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.CadastroImobiliarioFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.AsyncExecutor;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.concurrent.CompletableFuture;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaImportacaoAtributoCadastroImobiliario",
        pattern = "/tributario/cadastro-imobiliario/importacao-atributo/",
        viewId = "/faces/tributario/cadastromunicipal/cadastroimobiliario/importacaoatributo/edita.xhtml")
})
public class ImportacaoAtributoCadastroImobiliarioControlador implements Serializable {

    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    private ImportacaoAtributoCadastroImobiliario importacao;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private CompletableFuture completableFuture;

    public ImportacaoAtributoCadastroImobiliario getImportacao() {
        return importacao;
    }

    public void setImportacao(ImportacaoAtributoCadastroImobiliario importacao) {
        this.importacao = importacao;
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    @URLAction(mappingId = "novaImportacaoAtributoCadastroImobiliario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaImportacaoAtributoCadastroImobiliario() {
        importacao = new ImportacaoAtributoCadastroImobiliario();
        importacao.setIndexInscricaoCadastral(0);
        importacao.setDescartarPrimeiraLinha(Boolean.TRUE);
    }

    public void selecionarPlanilha(FileUploadEvent fileUploadEvent) {
        try {
            importacao.setUploadedFile(fileUploadEvent.getFile());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao selecionar a planilha de dados. Detalhe: " + e.getMessage());
        }
    }

    public void iniciarImportacao() {
        try {
            importacao.validar();
            assistenteBarraProgresso = new AssistenteBarraProgresso();
            completableFuture = AsyncExecutor.getInstance().execute(assistenteBarraProgresso, () -> {
                cadastroImobiliarioFacade.importarAtributosCadastroImobiliario(assistenteBarraProgresso, importacao);
                return null;
            });
            FacesUtil.executaJavaScript("iniciarImportacao()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void acompanharImportacao() {
        if (completableFuture.isDone()) {
            FacesUtil.executaJavaScript("finalizarImportacao()");
        }
    }

    public void finalizarImportacao() {
        FacesUtil.addOperacaoRealizada("Importação de atributos do cadastro imobiliário finalizada com sucesso!");
        FacesUtil.redirecionamentoInterno("/tributario/cadastro-imobiliario/importacao-atributo/");
    }
}
