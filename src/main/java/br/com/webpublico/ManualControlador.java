package br.com.webpublico;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.ArquivoParte;
import br.com.webpublico.entidades.Manual;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ManualFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "manualControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoManual", pattern = "/manuais/novo/", viewId = "/faces/tributario/manual/edita.xhtml"),
        @URLMapping(id = "editarManual", pattern = "/manuais/editar/#{manualControlador.id}", viewId = "/faces/tributario/manual/edita.xhtml"),
        @URLMapping(id = "listarManual", pattern = "/manuais/listar/", viewId = "/faces/tributario/manual/lista.xhtml"),
})
public class ManualControlador extends PrettyControlador<Manual> implements Serializable, CRUD {

    @EJB
    private ManualFacade manualFacade;
    private String filtro;
    List<Manual> lista;

    public ManualControlador() {
        super(Manual.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/manuais/";
    }

    @Override
    public AbstractFacade getFacede() {
        return manualFacade;
    }


    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }


    @URLAction(mappingId = "novoManual", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();

    }

    @URLAction(mappingId = "editarManual", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();

    }

    public List<Manual> getLista() {
        if (lista == null || lista.isEmpty()) {
            filtra();
        }
        return lista;
    }

    public void filtra() {
        lista = manualFacade.lista(filtro);
    }

    public void handleFileUpload(FileUploadEvent event) {
        try {
            selecionado.setArquivo(criarArquivo(event.getFile()));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }


    private Arquivo criarArquivo(UploadedFile file) throws IOException {
        Arquivo arquivo = new Arquivo();

        arquivo.setDescricao(file.getFileName());
        arquivo.setMimeType(file.getContentType());
        arquivo.setNome(file.getFileName());
        arquivo.setTamanho(file.getSize());
        arquivo.setInputStream(file.getInputstream());
        arquivo = criarPartesDoArquivo(arquivo);

        return arquivo;
    }

    private Arquivo criarPartesDoArquivo(Arquivo arquivo) throws IOException {
        int bytesLidos = 0;

        while (true) {
            int restante = arquivo.getInputStream().available();
            byte[] buffer = new byte[restante > ArquivoParte.TAMANHO_MAXIMO ? ArquivoParte.TAMANHO_MAXIMO : restante];
            bytesLidos = arquivo.getInputStream().read(buffer);
            if (bytesLidos <= 0) {
                break;
            }
            ArquivoParte arquivoParte = new ArquivoParte();
            arquivoParte.setDados(buffer);
            arquivoParte.setArquivo(arquivo);
            arquivo.getPartes().add(arquivoParte);
        }

        return arquivo;
    }

    public void abreManual(Manual manual) throws IOException {
        if (manual.getArquivo() != null) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            manual = manualFacade.recuperar(manual.getId());
            for (ArquivoParte arquivoParte : manual.getArquivo().getPartes()) {
                bytes.write(arquivoParte.getDados());
            }
            AbstractReport.poeRelatorioNaSessao(bytes.toByteArray());
        }
    }

    public void excluir(Manual manual) {
        lista.remove(manual);
        manualFacade.remover(manual);
        FacesUtil.addOperacaoRealizada("Manual excluído com sucesso.");
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }
}
