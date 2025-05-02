package br.com.webpublico.controle;

import br.com.webpublico.entidadesauxiliares.GrupoEvento;
import br.com.webpublico.entidadesauxiliares.GrupoPontuacao;
import br.com.webpublico.negocios.EventoCalculoFacade;
import br.com.webpublico.negocios.PontuacaoFacade;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXB;
import java.io.*;
import java.util.Locale;

@ManagedBean(name = "importacaoExportacaoConfiguracaoIPTU")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "imporExportEventoCalculo", pattern = "/evento-de-calculo/importar-exportar/", viewId = "/faces/tributario/configuracao/importarexportar/edita.xhtml")
})
public class ImportacaoExportacaoConfiguracaoIPTU implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ImportacaoExportacaoConfiguracaoIPTU.class);
    @EJB
    private EventoCalculoFacade eventoCalculoFacade;
    @EJB
    private PontuacaoFacade pontuacaoFacade;

    public void exportarEventos() {
        try {
            GrupoEvento grupo = new GrupoEvento();
            grupo.setEventos(eventoCalculoFacade.lista());
            exportar(grupo, "importacao-eventos");
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void exportarPontos() {
        try {
            GrupoPontuacao grupo = new GrupoPontuacao();
            grupo.setPontos(pontuacaoFacade.lista());
            exportar(grupo, "importacao-pontos");
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void exportar(Object grupo, String filename) {
        try {
            StringWriter sw = new StringWriter();
            JAXB.marshal(grupo, sw);
            String conteudo = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
            conteudo += sw.toString();
            HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            response.setContentType("application/force-download");
            response.setHeader("Content-disposition", "attachment;filename=" + filename + ".xml");
            response.setCharacterEncoding("iso-8859-1");
            response.setLocale(new Locale("pt_BR_", "pt", "BR"));
            byte[] bytes = conteudo.getBytes();
            response.setContentLength(bytes.length);
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(bytes, 0, bytes.length);
            outputStream.flush();
            outputStream.close();
            FacesContext.getCurrentInstance().responseComplete();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void uploadEventos(FileUploadEvent event) throws FileNotFoundException, IOException {
        byte[] buffer = event.getFile().getContents();
        String xml;
        try (InputStream input = event.getFile().getInputstream()) {
            input.read(buffer);
            xml = new String(buffer).toString();
        }
        leXmlEventos(xml);
    }

    private void leXmlEventos(String xml) {
        GrupoEvento grupo =  JAXB.unmarshal(xml, GrupoEvento.class);
        eventoCalculoFacade.salvar(grupo);
    }

    public void uploadPontos(FileUploadEvent event) throws FileNotFoundException, IOException {
        byte[] buffer = event.getFile().getContents();
        String xml;
        try (InputStream input = event.getFile().getInputstream()) {
            input.read(buffer);
            xml = new String(buffer).toString();
        }
        leXmlPontos(xml);
    }

    private void leXmlPontos(String xml) {
        GrupoPontuacao grupo =  JAXB.unmarshal(xml, GrupoPontuacao.class);
        pontuacaoFacade.salvarImportacao(grupo);
    }
}
