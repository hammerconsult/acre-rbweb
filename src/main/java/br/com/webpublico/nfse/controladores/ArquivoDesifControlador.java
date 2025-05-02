package br.com.webpublico.nfse.controladores;


import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.*;
import br.com.webpublico.nfse.facades.ArquivoDesifFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.ExcelUtil;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "arquivo-desif-listar", pattern = "/nfse/arquivo-desif/listar/",
        viewId = "/faces/tributario/nfse/arquivo-desif/lista.xhtml"),
    @URLMapping(id = "arquivo-desif-ver", pattern = "/nfse/arquivo-desif/ver/#{arquivoDesifControlador.id}/",
        viewId = "/faces/tributario/nfse/arquivo-desif/visualizar.xhtml")
})
public class ArquivoDesifControlador extends PrettyControlador<ArquivoDesif> implements CRUD {

    @EJB
    private ArquivoDesifFacade facade;
    private LazyDataModel<ArquivoDesifRegistro0100> registros0100;
    private LazyDataModel<ArquivoDesifRegistro0200> registros0200;
    private LazyDataModel<ArquivoDesifRegistro0300> registros0300;
    private LazyDataModel<ArquivoDesifRegistro0400> registros0400;
    private LazyDataModel<ArquivoDesifRegistro0410> registros0410;
    private LazyDataModel<ArquivoDesifRegistro0430> registros0430;
    private LazyDataModel<ArquivoDesifRegistro0440> registros0440;
    private LazyDataModel<ArquivoDesifRegistro1000> registros1000;
    private AssistenteBarraProgresso assistente;
    private DefaultStreamedContent fileDownload;
    private Future<File> futureExportacao;

    public ArquivoDesifControlador() {
        super(ArquivoDesif.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/nfse/arquivo-desif/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public LazyDataModel<ArquivoDesifRegistro0100> getRegistros0100() {
        return registros0100;
    }

    public void setRegistros0100(LazyDataModel<ArquivoDesifRegistro0100> registros0100) {
        this.registros0100 = registros0100;
    }

    public LazyDataModel<ArquivoDesifRegistro0200> getRegistros0200() {
        return registros0200;
    }

    public void setRegistros0200(LazyDataModel<ArquivoDesifRegistro0200> registros0200) {
        this.registros0200 = registros0200;
    }

    public LazyDataModel<ArquivoDesifRegistro0300> getRegistros0300() {
        return registros0300;
    }

    public void setRegistros0300(LazyDataModel<ArquivoDesifRegistro0300> registros0300) {
        this.registros0300 = registros0300;
    }

    public LazyDataModel<ArquivoDesifRegistro0400> getRegistros0400() {
        return registros0400;
    }

    public void setRegistros0400(LazyDataModel<ArquivoDesifRegistro0400> registros0400) {
        this.registros0400 = registros0400;
    }

    public LazyDataModel<ArquivoDesifRegistro0410> getRegistros0410() {
        return registros0410;
    }

    public void setRegistros0410(LazyDataModel<ArquivoDesifRegistro0410> registros0410) {
        this.registros0410 = registros0410;
    }

    public LazyDataModel<ArquivoDesifRegistro0430> getRegistros0430() {
        return registros0430;
    }

    public void setRegistros0430(LazyDataModel<ArquivoDesifRegistro0430> registros0430) {
        this.registros0430 = registros0430;
    }

    public LazyDataModel<ArquivoDesifRegistro0440> getRegistros0440() {
        return registros0440;
    }

    public void setRegistros0440(LazyDataModel<ArquivoDesifRegistro0440> registros0440) {
        this.registros0440 = registros0440;
    }

    public LazyDataModel<ArquivoDesifRegistro1000> getRegistros1000() {
        return registros1000;
    }

    public void setRegistros1000(LazyDataModel<ArquivoDesifRegistro1000> registros1000) {
        this.registros1000 = registros1000;
    }

    public AssistenteBarraProgresso getAssistente() {
        return assistente;
    }

    public void setAssistente(AssistenteBarraProgresso assistente) {
        this.assistente = assistente;
    }

    public DefaultStreamedContent getFileDownload() {
        return fileDownload;
    }

    public void setFileDownload(DefaultStreamedContent fileDownload) {
        this.fileDownload = fileDownload;
    }

    @URLAction(mappingId = "arquivo-desif-ver", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarRegistros();
    }

    private void recuperarRegistros() {
        switch (selecionado.getModulo()) {
            case MODULO_1: {
                recuperarRegistros0400();
                recuperarRegistros0410();
                recuperarRegistros1000();
                break;
            }
            case MODULO_2: {
                recuperarRegistros0400();
                recuperarRegistros0430();
                recuperarRegistros0440();
                break;
            }
            case MODULO_3: {
                recuperarRegistros0100();
                recuperarRegistros0200();
                recuperarRegistros0300();
                break;
            }
            case MODULO_4: {
                recuperarRegistros1000();
                break;
            }
        }
    }

    private void recuperarRegistros0100() {
        registros0100 = new LazyDataModel<ArquivoDesifRegistro0100>() {
            @Override
            public List<ArquivoDesifRegistro0100> load(int first, int max, String s, SortOrder sortOrder, Map<String, String> map) {
                setRowCount(facade.getArquivoDesifRegistro0100Facade().contarRegistros(selecionado));
                return facade.getArquivoDesifRegistro0100Facade().buscarRegistros(selecionado, first, max);
            }
        };
    }

    private void recuperarRegistros0200() {
        registros0200 = new LazyDataModel<ArquivoDesifRegistro0200>() {
            @Override
            public List<ArquivoDesifRegistro0200> load(int first, int max, String s, SortOrder sortOrder, Map<String, String> map) {
                setRowCount(facade.getArquivoDesifRegistro0200Facade().contarRegistros(selecionado));
                return facade.getArquivoDesifRegistro0200Facade().buscarRegistros(selecionado, first, max);
            }
        };
    }

    private void recuperarRegistros0300() {
        registros0300 = new LazyDataModel<ArquivoDesifRegistro0300>() {
            @Override
            public List<ArquivoDesifRegistro0300> load(int first, int max, String s, SortOrder sortOrder, Map<String, String> map) {
                setRowCount(facade.getArquivoDesifRegistro0300Facade().contarRegistros(selecionado));
                return facade.getArquivoDesifRegistro0300Facade().buscarRegistros(selecionado, first, max);
            }
        };
    }

    private void recuperarRegistros0400() {
        registros0400 = new LazyDataModel<ArquivoDesifRegistro0400>() {
            @Override
            public List<ArquivoDesifRegistro0400> load(int first, int max, String s, SortOrder sortOrder, Map<String, String> map) {
                setRowCount(facade.getArquivoDesifRegistro0400Facade().contarRegistros(selecionado));
                return facade.getArquivoDesifRegistro0400Facade().buscarRegistros(selecionado, first, max);
            }
        };
    }

    private void recuperarRegistros0410() {
        registros0410 = new LazyDataModel<ArquivoDesifRegistro0410>() {
            @Override
            public List<ArquivoDesifRegistro0410> load(int first, int max, String s, SortOrder sortOrder, Map<String, String> map) {
                setRowCount(facade.getArquivoDesifRegistro0410Facade().contarRegistros(selecionado));
                return facade.getArquivoDesifRegistro0410Facade().buscarRegistros(selecionado, first, max);
            }
        };
    }

    private void recuperarRegistros0430() {
        registros0430 = new LazyDataModel<ArquivoDesifRegistro0430>() {
            @Override
            public List<ArquivoDesifRegistro0430> load(int first, int max, String s, SortOrder sortOrder, Map<String, String> map) {
                setRowCount(facade.getArquivoDesifRegistro0430Facade().contarRegistros(selecionado));
                return facade.getArquivoDesifRegistro0430Facade().buscarRegistros(selecionado, first, max);
            }
        };
    }

    private void recuperarRegistros0440() {
        registros0440 = new LazyDataModel<ArquivoDesifRegistro0440>() {
            @Override
            public List<ArquivoDesifRegistro0440> load(int first, int max, String s, SortOrder sortOrder, Map<String, String> map) {
                setRowCount(facade.getArquivoDesifRegistro0440Facade().contarRegistros(selecionado));
                return facade.getArquivoDesifRegistro0440Facade().buscarRegistros(selecionado, first, max);
            }
        };
    }

    private void recuperarRegistros1000() {
        registros1000 = new LazyDataModel<ArquivoDesifRegistro1000>() {
            @Override
            public List<ArquivoDesifRegistro1000> load(int first, int max, String s, SortOrder sortOrder, Map<String, String> map) {
                setRowCount(facade.getArquivoDesifRegistro1000Facade().contarRegistros(selecionado));
                return facade.getArquivoDesifRegistro1000Facade().buscarRegistros(selecionado, first, max);
            }
        };
    }

    private void iniciarExportacaoExcel() {
        fileDownload = null;
        assistente = new AssistenteBarraProgresso();
        assistente.setDescricaoProcesso("Recuperando dados para exportação");
        assistente.setExecutando(true);
    }

    public void exportarExcel() {
        iniciarExportacaoExcel();
        futureExportacao = facade.exportarExcel(selecionado);
        FacesUtil.executaJavaScript("iniciarExportacaoExcel()");
    }

    public void acompanharExportacaoExcel() {
        if (futureExportacao != null && (futureExportacao.isCancelled() || futureExportacao.isDone())) {
            FacesUtil.executaJavaScript("finalizarExportacaoExcel()");
        }
    }

    public void finalizarExportacaoExcel() throws ExecutionException, InterruptedException, FileNotFoundException {
        File file = futureExportacao.get();
        InputStream stream = new FileInputStream(file);
        fileDownload = new DefaultStreamedContent(stream, ExcelUtil.XLSX_CONTENTTYPE, "ArquivoDesif.xlsx");
        assistente.setExecutando(false);
    }
}
