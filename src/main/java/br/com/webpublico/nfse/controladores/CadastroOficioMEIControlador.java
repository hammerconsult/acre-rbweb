package br.com.webpublico.nfse.controladores;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.nfse.domain.CadastroOficioMEI;
import br.com.webpublico.nfse.domain.LinhaCadastroOficioMEI;
import br.com.webpublico.nfse.domain.LinhaCadastroOficioMEI;
import br.com.webpublico.nfse.facades.CadastroOficioMEIFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(
        id = "importarArquivoCadastroMEI",
        pattern = "/tributario/simples-nacional/cadastro-oficio-MEI/importar/",
        viewId = "/faces/tributario/simples-nacional/cadastro-oficios-MEI/importar.xhtml")

})
public class CadastroOficioMEIControlador implements Serializable {

    private static Logger logger = LoggerFactory.getLogger(CadastroOficioMEIControlador.class);
    @EJB
    private CadastroOficioMEIFacade facade;
    private CadastroOficioMEI selecionado;
    private List<LinhaCadastroOficioMEI> linhas;
    private Future<List<LinhaCadastroOficioMEI>> futureLeituraArquivo;
    private Future futureProcessarArquivo;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private int etapa;

    public CadastroOficioMEI getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(CadastroOficioMEI selecionado) {
        this.selecionado = selecionado;
    }

    public int getEtapa() {
        return etapa;
    }

    public void setEtapa(int etapa) {
        this.etapa = etapa;
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    @URLAction(mappingId = "importarArquivoCadastroMEI", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void importar() {
        selecionado = new CadastroOficioMEI();
        selecionado.setDataImportacao(new Date());
        selecionado.setUsuarioImportacao(facade.getSistemaFacade().getUsuarioCorrente());
        etapa = 0;
    }

    public void lerArquivo() {
        futureLeituraArquivo = null;
        etapa = 1;
        assistenteBarraProgresso = new AssistenteBarraProgresso();
        try {
            facade.realizarValidacoes(selecionado);
            futureLeituraArquivo = facade.lerArquivo(selecionado, assistenteBarraProgresso);
            FacesUtil.executaJavaScript("acompanharLeituraArquivo()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.debug("Erro ao ler arquivo ", ex);
        }
    }

    public void acompanharLeituraArquivo() throws ExecutionException, InterruptedException {
        if (futureLeituraArquivo != null && (futureLeituraArquivo.isCancelled() || futureLeituraArquivo.isDone())) {
            FacesUtil.executaJavaScript("pararTimer()");
            linhas = futureLeituraArquivo.get();
            futureLeituraArquivo = null;
            etapa = 2;
        }
    }

    public void acompanharProcessamentoArquivo() {
        if (futureProcessarArquivo != null && (futureProcessarArquivo.isCancelled() || futureProcessarArquivo.isDone())) {
            FacesUtil.executaJavaScript("pararTimer()");
            futureProcessarArquivo = null;
            FacesUtil.addOperacaoRealizada("Arquivo processado com sucesso!");
        }
    }

    public void processarArquivo() {
        futureProcessarArquivo = null;
        etapa = 3;
        assistenteBarraProgresso = new AssistenteBarraProgresso();
        futureProcessarArquivo = facade.processarArquivo(selecionado, linhas, assistenteBarraProgresso);
        FacesUtil.executaJavaScript("acompanharProcessamentoArquivo()");
    }
}
