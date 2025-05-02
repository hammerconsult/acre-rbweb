package br.com.webpublico.controle;

import br.com.webpublico.entidades.ImportacaoDebitosIPTU;
import br.com.webpublico.entidades.ItemImportacaoDebitosIPTU;
import br.com.webpublico.enums.InconsistenciaImportacaoDebitoIPTU;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ArquivoFacade;
import br.com.webpublico.negocios.ImportacaoDebitosIPTUFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultUploadedFile;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;

/**
 * Created by Filipe
 * On 17, Abril, 2019,
 * At 11:40
 */


@ManagedBean(name = "importacaoDebitosIPTUControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoImportacaoDebitosIPTU", pattern = "/tributario/importacao-debitos-iptu/novo/", viewId = "/faces/tributario/iptu/importacaodebitos/edita.xhtml"),
        @URLMapping(id = "verImportacaoDebitosIPTU", pattern = "/tributario/importacao-debitos-iptu/ver/#{importacaoDebitosIPTUControlador.id}/", viewId = "/faces/tributario/iptu/importacaodebitos/visualizar.xhtml"),
        @URLMapping(id = "listaImportacaoDebitosIPTU", pattern = "/tributario/importacao-debitos-iptu/listar/", viewId = "/faces/tributario/iptu/importacaodebitos/lista.xhtml")
})
public class ImportacaoDebitosIPTUControlador extends PrettyControlador<ImportacaoDebitosIPTU> implements Serializable, CRUD {

    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private ImportacaoDebitosIPTUFacade importacaoDebitosIPTUFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    private SistemaControlador sistemaControlador;
    private ItemImportacaoDebitosIPTU itemImportacaoDebitosIPTU;
    private FileUploadEvent fileUploadEvent;
    private UploadedFile uploadFile;

    public ImportacaoDebitosIPTUControlador() {
        super(ImportacaoDebitosIPTU.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return importacaoDebitosIPTUFacade;
    }

    @URLAction(mappingId = "novoImportacaoDebitosIPTU", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        carregarNovo();
    }

    private void carregarNovo() {
        selecionado.setResponsavel(sistemaFacade.getUsuarioCorrente());
        selecionado.setDataRegistro(sistemaFacade.getDataOperacao());
        uploadFile = new DefaultUploadedFile();
    }

    @URLAction(mappingId = "verImportacaoDebitosIPTU", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public void handleFilesUploads(FileUploadEvent event) {
        try {
            uploadFile = event.getFile();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        }
        FacesUtil.addOperacaoRealizada("O arquivo " + event.getFile().getFileName() + " foi carregado com sucesso !");
    }

    public BufferedReader converterArquivo() {
        try {
            InputStream impuInputStream = uploadFile.getInputstream();
            InputStreamReader streamReader = new InputStreamReader(impuInputStream);
            BufferedReader buffread = new BufferedReader(streamReader);
            return buffread;
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao converter o arquivo !");
        }
        return null;
    }

    public void lerArquivo() {
        try {
            BufferedReader buffread = converterArquivo();
            String cpfCnpf;
            String codRetorno;
            String linha = "";
            int quantidadeLinhas = 0;
            while ((linha = buffread.readLine()) != null) {
                String identificador = linha.substring(1, 1);
                if (linha.startsWith("A") || linha.startsWith("Z")) {
                    continue;
                }
                if (!linha.equals(identificador)) {
                    itemImportacaoDebitosIPTU = new ItemImportacaoDebitosIPTU();
                    cpfCnpf = linha.substring(63, 77);
                    Util.valida_CpfCnpj(cpfCnpf);
                    codRetorno = linha.substring(450, 452);

                    itemImportacaoDebitosIPTU.setCpf(cpfCnpf);
                    itemImportacaoDebitosIPTU.setCodigoRetorno(codRetorno);
                    itemImportacaoDebitosIPTU.setInconsistencia(InconsistenciaImportacaoDebitoIPTU.getInconcistenciaPorCodigo(codRetorno));
                    itemImportacaoDebitosIPTU.setImportacaoDebitosIPTU(selecionado);
                    itemImportacaoDebitosIPTU.setLinhaDoArquivo(linha);

                    selecionado.getItemImportacaoDebitosIPTU().add(itemImportacaoDebitosIPTU);
                    quantidadeLinhas++;
                }
            }
            buffread.close();
            FacesUtil.addOperacaoRealizada("Arquivo lido com sucesso !");
        } catch (Exception ex) {
            logger.error("Erro: ", ex);
        }
    }

    @Override
    public void salvar() {
        super.salvar();
    }

    public void salvarSelecionado() {
        try {
            importacaoDebitosIPTUFacade.salvarNovo(selecionado);
            FacesUtil.addOperacaoRealizada("Dados salvo no sistema com sucesso !");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro: ", e);
        }

    }

    public boolean verificarSelecionado() {
        return itemImportacaoDebitosIPTU != null;
    }

    public void redireciona() {
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "listar/");
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/importacao-debitos-iptu/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public void setArquivoFacade(ArquivoFacade arquivoFacade) {
        this.arquivoFacade = arquivoFacade;
    }

    public FileUploadEvent getFileUploadEvent() {
        return fileUploadEvent;
    }

    public void setFileUploadEvent(FileUploadEvent fileUploadEvent) {
        this.fileUploadEvent = fileUploadEvent;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

}
