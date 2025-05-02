package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ArquivoLicitacaoPropostaFornecedor;
import br.com.webpublico.entidadesauxiliares.ArquivoPropostaFornecedor;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ConfiguracaoArquivoPropostaFornecedorFacade;
import br.com.webpublico.negocios.PropostaFornecedorFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: RenatoRomanini
 * Date: 07/03/15
 * Time: 16:21
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "arquivoPropostaFornecedorControlador")
@ViewScoped
@URLMapping(id = "novo-arquivo-proposta", pattern = "/licitacao/arquivo-proposta-fornecedor/", viewId = "/faces/administrativo/licitacao/arquivopropostafornecedor/edita.xhtml")
public class ArquivoPropostaFornecedorControlador implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ArquivoPropostaFornecedorControlador.class);
    @EJB
    private PropostaFornecedorFacade propostaFornecedorFacade;
    @EJB
    private ConfiguracaoArquivoPropostaFornecedorFacade configuracaoArquivoPropostaFornecedorFacade;
    private ArquivoPropostaFornecedor selecionado;
    private StreamedContent file;
    private File zipFile;
    private StreamedContent streamedContent;
    private HashMap<String, File> files;
    private Boolean novoArquivo;
    private UploadedFile propostaFile;
    private ArquivoLicitacaoPropostaFornecedor arquivoLicitacaoPropostaFornecedor;
    private boolean arquivoInvalido;


    public ArquivoPropostaFornecedorControlador() {
        arquivoInvalido = false;
    }

    @URLAction(mappingId = "novo-arquivo-proposta", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        selecionado = new ArquivoPropostaFornecedor();
        novoArquivo = Boolean.TRUE;
        files = new HashMap<>();
    }

    public List<Pessoa> completarPessoa(String parte) {
        if (selecionado.getLicitacao() != null) {
            return propostaFornecedorFacade.buscarFornecedorSemPropostaParaEstaLicitacao(parte.trim(), selecionado.getLicitacao());
        }
        FacesUtil.addOperacaoNaoPermitida("Informe uma licitação para selecionar um fornecedor.");
        return new ArrayList<>();
    }

    public void carregarListasDaLicitacao() {
        selecionado.setLicitacao(propostaFornecedorFacade.getLicitacaoFacade().recuperar(selecionado.getLicitacao().getId()));
        selecionado.setData(selecionado.getLicitacao().getAbertaEm());
        selecionado.setFornecedor(null);
        selecionado.setRepresentante(null);
        ProcessoDeCompra pdc = selecionado.getLicitacao().getProcessoDeCompra();
        if (pdc != null) {
            pdc.setLotesProcessoDeCompra(propostaFornecedorFacade.getLicitacaoFacade().getProcessoDeCompraFacade().recuperarLotesProcesso(pdc));
            ordenaLotesEItensDoProcessoEPreencheLista(pdc);
        }
    }

    public void ordenaLotesEItensDoProcessoEPreencheLista(ProcessoDeCompra pdc) {
        selecionado.setItens(new ArrayList<ItemProcessoDeCompra>());

        Collections.sort(pdc.getLotesProcessoDeCompra(), new Comparator<LoteProcessoDeCompra>() {
            @Override
            public int compare(LoteProcessoDeCompra o1, LoteProcessoDeCompra o2) {
                return o1.getNumero().compareTo(o2.getNumero());
            }
        });

        for (LoteProcessoDeCompra lpdc : pdc.getLotesProcessoDeCompra()) {
            Collections.sort(lpdc.getItensProcessoDeCompra(), new Comparator<ItemProcessoDeCompra>() {
                @Override
                public int compare(ItemProcessoDeCompra o1, ItemProcessoDeCompra o2) {
                    return o1.getNumero().compareTo(o2.getNumero());
                }
            });

            selecionado.getItens().addAll(lpdc.getItensProcessoDeCompra());
        }
    }

    public ArquivoPropostaFornecedor getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(ArquivoPropostaFornecedor selecionado) {
        this.selecionado = selecionado;
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getLicitacao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo licitação deve ser informado.");
        }
        if (selecionado.getData() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data deve ser informado.");
        }
        ve.lancarException();
    }

    public void validarImportacaoArquivoProposta() {
        ValidacaoException ve = new ValidacaoException();
        if (propostaFile == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Adicione o arquivo de importação da proposta do fornecedor.");
        }
        if (arquivoInvalido) {
            ve.adicionarMensagemDeCampoObrigatorio("Arquivo com conteúdo invalido.");
        }
        ve.lancarException();
    }

    public void gerarArquivo() {
        try {
            validarCampos();
            file = gerarFile();

            ConfiguracaoArquivoPropostaFornecedor configuracaoArquivoPropostaFornecedor = configuracaoArquivoPropostaFornecedorFacade.buscarUltimaConfiguracao();
            Arquivo manual = configuracaoArquivoPropostaFornecedor.getManual();
            Arquivo sistema = configuracaoArquivoPropostaFornecedor.getArquivo();

            files.put(manual.getNome(), criarFile(manual));
            files.put(sistema.getNome(), criarFile(sistema));
            addToZip();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (IOException e) {
            logger.error(" Erro ao gerar o arquivo de proposta do fornecedor cause.: {} ", e.getCause());
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    private File criarFile(Arquivo manual) {
        try {
            File arquivo = File.createTempFile(manual.getNome(), manual.getMimeType());
            try {
                FileOutputStream fileOuputStream = new FileOutputStream(arquivo);
                logger.error("PARTES DO ARQUIVO " + manual.getPartes().size());
                for (ArquivoParte arquivoParte : manual.getPartes()) {
                    fileOuputStream.write(arquivoParte.getDados());
                }
                fileOuputStream.close();
                return arquivo;
            } catch (Exception e) {
                logger.error("Erro ao criarFile no arquivo proposta fornecedor {}", e);
                logger.debug(e.getMessage());
            }
        } catch (IOException e) {
            logger.debug(e.getMessage());
        }
        return null;
    }

    private StreamedContent gerarFile() throws IOException {
        StringWriter sw = new StringWriter();
        ObjectMapper mapper = new ObjectMapper();

        mapper.writeValue(sw, new ArquivoLicitacaoPropostaFornecedor(selecionado));

        String nomeArquivo = "Proposta-Fornecedor-Licitacao-" + selecionado.getLicitacao().getNumeroLicitacao() + ".txt";
        File arquivo = new File(nomeArquivo);

        OutputStreamWriter bufferOut = new OutputStreamWriter(new FileOutputStream(arquivo), "UTF-8");
        try {
            bufferOut.write(sw.toString());
            InputStream stream = new FileInputStream(arquivo);
            files.put("arquivo", arquivo);
            return new DefaultStreamedContent(stream, "text/plain", nomeArquivo);
        } finally {
            bufferOut.close();
        }
    }

    public void addToZip() {
        try {
            String nomeArquivo = "Proposta-Fornecedor-Licitacao-" + selecionado.getLicitacao().getNumeroLicitacao();

            zipFile = File.createTempFile(nomeArquivo, "zip");
            byte[] buffer = new byte[1024];
            FileOutputStream fout = new FileOutputStream(zipFile);
            ZipOutputStream zout = new ZipOutputStream(fout);

            for (Map.Entry<String, File> m : files.entrySet()) {
                System.out.println("Key zip file arquivo proposta fornecedor: " + m.getKey());
                System.out.println("Value zip file arquivo proposta fornecedor: " + m.getValue());
                FileInputStream fin = new FileInputStream(m.getValue());
                zout.putNextEntry(new ZipEntry(m.getKey()));
                int length;
                while ((length = fin.read(buffer)) > 0) {
                    zout.write(buffer, 0, length);
                }
                fin.close();
            }
            zout.close();

            FileInputStream fis = new FileInputStream(zipFile);
            streamedContent = new DefaultStreamedContent(fis, "application/zip", nomeArquivo + ".zip");
        } catch (IOException ioe) {
            FacesUtil.addError("Erro grave!", "Ocorreu um erro para gerar o arquivo, comunique o administrador.");
        }
    }

    public void handleFilesUploads(FileUploadEvent event) {
        try {
            arquivoInvalido = false;
            propostaFile = event.getFile();
            InputStream inputstream = event.getFile().getInputstream();
            InputStreamReader in = new InputStreamReader(inputstream);
            BufferedReader bufferedReader = new BufferedReader(in);

            String line = bufferedReader.readLine();
            ObjectMapper mapper = new ObjectMapper();

            arquivoLicitacaoPropostaFornecedor = mapper.readValue(line, ArquivoLicitacaoPropostaFornecedor.class);

        } catch (Exception ex) {
            arquivoInvalido = true;
            logger.debug(ex.getMessage());
            FacesUtil.addOperacaoNaoPermitida("Arquivo com conteúdo inválido !");
        }
    }

    public void redirecionarProposta() {
        try {
            validarImportacaoArquivoProposta();
            Web.poeNaSessao(arquivoLicitacaoPropostaFornecedor);
            PropostaFornecedorControlador propostaFornecedorControlador = (PropostaFornecedorControlador) Util.getControladorPeloNome("propostaFornecedorControlador");
            FacesUtil.redirecionamentoInterno(propostaFornecedorControlador.getCaminhoDestinoNovoCadastro());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public StreamedContent getFile() {
        return file;
    }

    public void setFile(StreamedContent file) {
        this.file = file;
    }

    public Boolean getNovoArquivo() {
        return novoArquivo;
    }

    public void setNovoArquivo(Boolean novoArquivo) {
        this.novoArquivo = novoArquivo;
    }

    public ArquivoLicitacaoPropostaFornecedor getArquivoLicitacaoPropostaFornecedor() {
        return arquivoLicitacaoPropostaFornecedor;
    }

    public void setArquivoLicitacaoPropostaFornecedor(ArquivoLicitacaoPropostaFornecedor arquivoLicitacaoPropostaFornecedor) {
        this.arquivoLicitacaoPropostaFornecedor = arquivoLicitacaoPropostaFornecedor;
    }

    public File getZipFile() {
        return zipFile;
    }

    public void setZipFile(File zipFile) {
        this.zipFile = zipFile;
    }

    public StreamedContent getStreamedContent() {
        return streamedContent;
    }

    public void setStreamedContent(StreamedContent streamedContent) {
        this.streamedContent = streamedContent;
    }

    public HashMap<String, File> getFiles() {
        return files;
    }

    public void setFiles(HashMap<String, File> files) {
        this.files = files;
    }

    public UploadedFile getPropostaFile() {
        return propostaFile;
    }

    public void setPropostaFile(UploadedFile propostaFile) {
        this.propostaFile = propostaFile;
    }

    public List<Licitacao> buscarLicitacoes(String parte) {
        return propostaFornecedorFacade.getLicitacaoFacade().buscarLicitacoesNumeroOrDescricaoOrExercicoParaArquivoProposta(parte.trim());
    }
}
