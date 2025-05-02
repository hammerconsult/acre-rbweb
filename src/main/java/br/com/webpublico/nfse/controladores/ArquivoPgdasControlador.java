/*
 * Codigo gerado automaticamente em Mon Oct 31 21:28:23 BRST 2011
 * Gerador de Controlador
 *
 */
package br.com.webpublico.nfse.controladores;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteArquivoPGDAS;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.pgdas.ArquivoPgdas;
import br.com.webpublico.nfse.domain.pgdas.TipoLinhaArquivoPGDAS;
import br.com.webpublico.nfse.domain.pgdas.registros.*;
import br.com.webpublico.nfse.domain.pgdas.util.TabelaView;
import br.com.webpublico.nfse.domain.pgdas.util.UtilPgdas;
import br.com.webpublico.nfse.facades.ArquivoPgdasFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.*;
import java.util.Arrays;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-arquivo-pgdas", pattern = "/tributario/arquivo-pgdas/novo/", viewId = "/faces/tributario/simples-nacional/arquivo-pgdas/edita.xhtml"),
    @URLMapping(id = "listar-arquivo-pgdas", pattern = "/tributario/arquivo-pgdas/listar/", viewId = "/faces/tributario/simples-nacional/arquivo-pgdas/lista.xhtml"),
    @URLMapping(id = "editar-arquivo-pgdas", pattern = "/tributario/arquivo-pgdas/editar/#{arquivoPgdasControlador.id}/", viewId = "/faces/tributario/simples-nacional/arquivo-pgdas/edita.xhtml"),
    @URLMapping(id = "ver-arquivo-pgdas", pattern = "/tributario/arquivo-pgdas/ver/#{arquivoPgdasControlador.id}/", viewId = "/faces/tributario/simples-nacional/arquivo-pgdas/visualizar.xhtml"),
})
public class ArquivoPgdasControlador extends PrettyControlador<ArquivoPgdas> implements Serializable, CRUD {

    @EJB
    private ArquivoPgdasFacade arquivoPgdasFacade;


    public ArquivoPgdasControlador() {
        super(ArquivoPgdas.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return arquivoPgdasFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/arquivo-pgdas/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novo-arquivo-pgdas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setDataImportacao(arquivoPgdasFacade.getSistemaFacade().getDataOperacao());
        selecionado.setResponsavel(arquivoPgdasFacade.getSistemaFacade().getUsuarioCorrente());
    }

    @Override
    @URLAction(mappingId = "editar-arquivo-pgdas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    @URLAction(mappingId = "ver-arquivo-pgdas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    public void handleFileUploadUnicoArquivo(FileUploadEvent event) {
        if (selecionado.getDetentorArquivoComposicao() == null) {
            selecionado.setDetentorArquivoComposicao(new DetentorArquivoComposicao());
        }
        try {
            adicionarUnicoArquivo(event.getFile());
            lerArquivo();
        } catch (IOException e) {
            logger.debug("Assistente Arquivo Controlador: " + e.getMessage());
        }
    }

    private void adicionarUnicoArquivo(UploadedFile file) throws IOException {
        selecionado.getDetentorArquivoComposicao().setArquivoComposicao(new ArquivoComposicao());
        ArquivoComposicao arquivoComposicao = criarArquivoComposicao(criarArquivo(file), file);
        selecionado.getDetentorArquivoComposicao().setArquivoComposicao(arquivoComposicao);
    }

    private Arquivo criarArquivo(UploadedFile file) throws IOException {
        Arquivo arquivo = new Arquivo();
        arquivo.setDescricao(file.getFileName());
        arquivo.setNome(file.getFileName());
        arquivo.setTamanho(file.getSize());
        arquivo.setInputStream(file.getInputstream());
        arquivo.setMimeType(getMimeType(file.getInputstream()));
        arquivo = criarPartesDoArquivo(arquivo);

        return arquivo;
    }

    public String getMimeType(InputStream is) {
        TikaConfig tika = null;
        try {
            tika = new TikaConfig();
            MediaType mimetype = tika.getDetector().detect(
                TikaInputStream.get(is), new Metadata());
            return mimetype.getType() + "/" + mimetype.getSubtype();
        } catch (TikaException e) {
            logger.error("Não foi possível gerar o mimeType: " + e);
        } catch (IOException e) {
            logger.error("Não foi possível gerar o mimeType: " + e);
        }
        return "application/octet-stream";
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

    private ArquivoComposicao criarArquivoComposicao(Arquivo arquivo, UploadedFile file) {
        ArquivoComposicao arquivoComposicao = new ArquivoComposicao();
        arquivoComposicao.setArquivo(arquivo);
        arquivoComposicao.setFile(file);
        arquivoComposicao.setDataUpload(selecionado.getDataImportacao());
        arquivoComposicao.setDetentorArquivoComposicao(selecionado.getDetentorArquivoComposicao());

        return arquivoComposicao;
    }

    public TabelaView getTabelaPgdasRegistroAAAAA() {
        return new UtilPgdas().getViewObjetosJaRecuperados(selecionado.getRegistrosAAAAA());
    }

    public TabelaView getTabelaPgdasRegistro00000() {
        return new UtilPgdas().getViewObjetosJaRecuperados(selecionado.getRegistros00000());
    }

    public TabelaView getTabelaPgdasRegistro00001() {
        return new UtilPgdas().getViewObjetosJaRecuperados(selecionado.getRegistros00001());
    }

    public TabelaView getTabelaPgdasRegistro01000() {
        return new UtilPgdas().getViewObjetosJaRecuperados(selecionado.getRegistros01000());
    }

    public TabelaView getTabelaPgdasRegistro01500() {
        return new UtilPgdas().getViewObjetosJaRecuperados(selecionado.getRegistros01500());
    }

    public TabelaView getTabelaPgdasRegistro01501() {
        return new UtilPgdas().getViewObjetosJaRecuperados(selecionado.getRegistros01501());
    }

    public TabelaView getTabelaPgdasRegistro01502() {
        return new UtilPgdas().getViewObjetosJaRecuperados(selecionado.getRegistros01502());
    }

    public TabelaView getTabelaPgdasRegistro02000() {
        return new UtilPgdas().getViewObjetosJaRecuperados(selecionado.getRegistros02000());
    }

    public TabelaView getTabelaPgdasRegistro03000() {
        return new UtilPgdas().getViewObjetosJaRecuperados(selecionado.getRegistros03000());
    }

    public TabelaView getTabelaPgdasRegistro03100() {
        return new UtilPgdas().getViewObjetosJaRecuperados(selecionado.getRegistros03100());
    }

    public TabelaView getTabelaPgdasRegistro03110() {
        return new UtilPgdas().getViewObjetosJaRecuperados(selecionado.getRegistros03110());
    }

    public TabelaView getTabelaPgdasRegistro03120() {
        return new UtilPgdas().getViewObjetosJaRecuperados(selecionado.getRegistros03120());
    }

    public TabelaView getTabelaPgdasRegistro03130() {
        return new UtilPgdas().getViewObjetosJaRecuperados(selecionado.getRegistros03130());
    }

    public TabelaView getTabelaPgdasRegistro03111() {
        return new UtilPgdas().getViewObjetosJaRecuperados(selecionado.getRegistros03111());
    }

    public TabelaView getTabelaPgdasRegistro03121() {
        return new UtilPgdas().getViewObjetosJaRecuperados(selecionado.getRegistros03121());
    }

    public TabelaView getTabelaPgdasRegistro03131() {
        return new UtilPgdas().getViewObjetosJaRecuperados(selecionado.getRegistros03131());
    }

    public TabelaView getTabelaPgdasRegistro03112() {
        return new UtilPgdas().getViewObjetosJaRecuperados(selecionado.getRegistros03112());
    }

    public TabelaView getTabelaPgdasRegistro03122() {
        return new UtilPgdas().getViewObjetosJaRecuperados(selecionado.getRegistros03122());
    }

    public TabelaView getTabelaPgdasRegistro03132() {
        return new UtilPgdas().getViewObjetosJaRecuperados(selecionado.getRegistros03132());
    }

    public TabelaView getTabelaPgdasRegistro03500() {
        return new UtilPgdas().getViewObjetosJaRecuperados(selecionado.getRegistros03500());
    }

    public TabelaView getTabelaPgdasRegistro04000() {
        return new UtilPgdas().getViewObjetosJaRecuperados(selecionado.getRegistros04000());
    }

    public TabelaView getTabelaPgdasRegistro99999() {
        return new UtilPgdas().getViewObjetosJaRecuperados(selecionado.getRegistros99999());
    }

    public TabelaView getTabelaPgdasRegistroZZZZZ() {
        return new UtilPgdas().getViewObjetosJaRecuperados(selecionado.getRegistrosZZZZZ());
    }

    public void lerArquivo() {
        try {
            if (selecionado.getDetentorArquivoComposicao() == null) {
                throw new ValidacaoException("É necessário que o arquivo seja importado");
            }
            Arquivo arquivo = selecionado.getDetentorArquivoComposicao().getArquivoComposicao().getArquivo();
            arquivo.montarImputStream();
            InputStream base64ToInputStream = arquivo.getInputStream();
            percorrerLinhasDoArquivo(base64ToInputStream);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    private void percorrerLinhasDoArquivo(InputStream inputStream) throws IOException {
        ConfiguracaoTributario configuracaoTributario = arquivoPgdasFacade.getConfiguracaoTributarioFacade().retornaUltimo();
        selecionado.inicializarListas();
        AssistenteArquivoPGDAS assistente = new AssistenteArquivoPGDAS();
        assistente.setArquivoPgdas(selecionado);
        InputStreamReader streamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(streamReader);
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) {
                continue;
            }
            String[] pipes = line.split("\\|");
            List<String> separados = Arrays.asList(pipes);
            criarLinhasDoArquivo(configuracaoTributario.getCidade(), assistente, separados);
        }
        reader.close();
        streamReader.close();
        inputStream.close();
    }

    private void criarLinhasDoArquivo(Cidade cidade,
                                      AssistenteArquivoPGDAS assistente,
                                      List<String> separados) {
        TipoLinhaArquivoPGDAS tipoLinha = TipoLinhaArquivoPGDAS.valueOf("PG" + separados.get(0));
        switch (tipoLinha) {
            case PGAAAAA: {
                assistente.setPgdasRegistroAAAAA(new PgdasRegistroAAAAA());
                assistente.getPgdasRegistroAAAAA().criarLinha(assistente, separados);
                break;
            }
            case PG00000: {
                List<String> pipes = UtilPgdas.getListComplementar(separados, 22);
                assistente.setPgdasRegistro00000(null);
                if (pipes.get(8).equals(cidade.getCodigoTom())) {
                    assistente.setPgdasRegistro00000(new PgdasRegistro00000());
                    assistente.getPgdasRegistro00000().criarLinha(assistente, separados);
                }
                break;
            }
            case PG00001: {
                if (assistente.getPgdasRegistro00000() != null) {
                    PgdasRegistro00001 pgdasRegistro00001 = new PgdasRegistro00001();
                    pgdasRegistro00001.criarLinha(assistente, separados);
                }
                break;
            }
            case PG01000: {
                if (assistente.getPgdasRegistro00000() != null) {
                    PgdasRegistro01000 pgdasRegistro01000 = new PgdasRegistro01000();
                    pgdasRegistro01000.criarLinha(assistente, separados);
                }
                break;
            }
            case PG01500: {
                if (assistente.getPgdasRegistro00000() != null) {
                    PgdasRegistro01500 pgdasRegistro01500 = new PgdasRegistro01500();
                    pgdasRegistro01500.criarLinha(assistente, separados);
                }
                break;
            }
            case PG01501: {
                if (assistente.getPgdasRegistro00000() != null) {
                    PgdasRegistro01501 pgdasRegistro01501 = new PgdasRegistro01501();
                    pgdasRegistro01501.criarLinha(assistente, separados);
                }
                break;
            }
            case PG01502: {
                if (assistente.getPgdasRegistro00000() != null) {
                    PgdasRegistro01502 pgdasRegistro01502 = new PgdasRegistro01502();
                    pgdasRegistro01502.criarLinha(assistente, separados);
                }
                break;
            }
            case PG02000: {
                if (assistente.getPgdasRegistro00000() != null) {
                    PgdasRegistro02000 pgdasRegistro02000 = new PgdasRegistro02000();
                    pgdasRegistro02000.criarLinha(assistente, separados);
                }
                break;
            }
            case PG03000: {
                if (assistente.getPgdasRegistro00000() != null) {
                    assistente.setPgdasRegistro03000(new PgdasRegistro03000());
                    assistente.getPgdasRegistro03000().criarLinha(assistente, separados);
                }
                break;
            }
            case PG03100: {
                if (assistente.getPgdasRegistro00000() != null) {
                    assistente.setPgdasRegistro03100(new PgdasRegistro03100());
                    assistente.getPgdasRegistro03100().criarLinha(assistente, separados);
                }
                break;
            }
            case PG03110: {
                if (assistente.getPgdasRegistro00000() != null) {
                    PgdasRegistro03110 pgdasRegistro03110 = new PgdasRegistro03110();
                    pgdasRegistro03110.criarLinha(assistente, separados);
                }
                break;
            }
            case PG03111: {
                if (assistente.getPgdasRegistro00000() != null) {
                    PgdasRegistro03111 pgdasRegistro03111 = new PgdasRegistro03111();
                    pgdasRegistro03111.criarLinha(assistente, separados);
                }
                break;
            }
            case PG03112: {
                if (assistente.getPgdasRegistro00000() != null) {
                    PgdasRegistro03112 pgdasRegistro03112 = new PgdasRegistro03112();
                    pgdasRegistro03112.criarLinha(assistente, separados);
                }
                break;
            }
            case PG03120: {
                if (assistente.getPgdasRegistro00000() != null) {
                    PgdasRegistro03120 pgdasRegistro03120 = new PgdasRegistro03120();
                    pgdasRegistro03120.criarLinha(assistente, separados);
                }
                break;
            }
            case PG03121: {
                if (assistente.getPgdasRegistro00000() != null) {
                    PgdasRegistro03121 pgdasRegistro03121 = new PgdasRegistro03121();
                    pgdasRegistro03121.criarLinha(assistente, separados);
                }
                break;
            }
            case PG03122: {
                if (assistente.getPgdasRegistro00000() != null) {
                    PgdasRegistro03122 pgdasRegistro03122 = new PgdasRegistro03122();
                    pgdasRegistro03122.criarLinha(assistente, separados);
                }
                break;
            }
            case PG03130: {
                if (assistente.getPgdasRegistro00000() != null) {
                    PgdasRegistro03130 pgdasRegistro031301 = new PgdasRegistro03130();
                    pgdasRegistro031301.criarLinha(assistente, separados);
                }
                break;
            }
            case PG03131: {
                if (assistente.getPgdasRegistro00000() != null) {
                    PgdasRegistro03131 pgdasRegistro03131 = new PgdasRegistro03131();
                    pgdasRegistro03131.criarLinha(assistente, separados);
                }
                break;
            }
            case PG03132: {
                if (assistente.getPgdasRegistro00000() != null) {
                    PgdasRegistro03132 pgdasRegistro03132 = new PgdasRegistro03132();
                    pgdasRegistro03132.criarLinha(assistente, separados);
                }
                break;
            }
            case PG03500: {
                if (assistente.getPgdasRegistro00000() != null) {
                    PgdasRegistro03500 pgdasRegistro03500 = new PgdasRegistro03500();
                    pgdasRegistro03500.criarLinha(assistente, separados);
                }
                break;
            }
            case PG04000: {
                if (assistente.getPgdasRegistro00000() != null) {
                    PgdasRegistro04000 pgdasRegistro04000 = new PgdasRegistro04000();
                    pgdasRegistro04000.criarLinha(assistente, separados);
                }
                break;
            }
            case PG99999: {
                if (assistente.getPgdasRegistro00000() != null) {
                    PgdasRegistro99999 pgdasRegistro99999 = new PgdasRegistro99999();
                    pgdasRegistro99999.criarLinha(assistente, separados);
                }
                break;
            }
            case PGZZZZZ: {
                PgdasRegistroZZZZZ pgdasRegistroZZZZZ = new PgdasRegistroZZZZZ();
                pgdasRegistroZZZZZ.criarLinha(assistente, separados);
                break;
            }
        }
    }

    @Override
    public void salvar() {
        try {
            if (validaRegrasParaSalvar()) {
                selecionado = arquivoPgdasFacade.salvarRetornando(selecionado);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoSalvar()));
                redireciona(getCaminhoPadrao() + "ver/" + getUrlKeyValue());
            }
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }
}
