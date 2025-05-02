package br.com.webpublico.nfse.controladores;

import br.com.webpublico.nfse.domain.EventoSimplesNacional;
import br.com.webpublico.nfse.domain.LinhaEventoSimplesNacional;
import br.com.webpublico.nfse.facades.EventoSimplesNacionalFacade;
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
        id = "importarArquivoSimplesNacional",
        pattern = "/tributario/simples-nacional/evento-simples-nacional/importar/",
        viewId = "/faces/tributario/simples-nacional/evento-simples-nacional/importar.xhtml")

})
public class EventoSimplesNacionalControlador implements Serializable {

    private static Logger logger = LoggerFactory.getLogger(EventoSimplesNacionalControlador.class);

    @EJB
    private EventoSimplesNacionalFacade facade;

    private EventoSimplesNacional selecionado;

    private List<LinhaEventoSimplesNacional> linhas;

    private Future<List<LinhaEventoSimplesNacional>> futureLeituraArquivo;

    private Future futureProcessarArquivo;

    private AssistenteBarraProgresso assistenteBarraProgresso;

    private int etapa;

    public EventoSimplesNacionalControlador() {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    public EventoSimplesNacional getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(EventoSimplesNacional selecionado) {
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

    @URLAction(mappingId = "importarArquivoSimplesNacional", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void importar() {
        selecionado = new EventoSimplesNacional();
        selecionado.setDataImportacao(new Date());
        selecionado.setUsuarioImportacao(facade.getSistemaService().getUsuarioCorrente());
        etapa = 0;
    }

    public void lerArquivo() {
        futureLeituraArquivo = null;

        if (selecionado.getDetentorArquivoComposicao() == null) {
            FacesUtil.addOperacaoNaoPermitida("Por favor selecione o arquivo para importação.");
            return;
        }

        etapa = 1;

        assistenteBarraProgresso = new AssistenteBarraProgresso();

        futureLeituraArquivo = facade.lerArquivo(selecionado, assistenteBarraProgresso);

        FacesUtil.executaJavaScript("acompanharLeituraArquivo()");
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
