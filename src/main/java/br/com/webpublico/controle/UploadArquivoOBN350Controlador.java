/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.ArquivoRetornoOBN350;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ArquivoRetornoOBN350Facade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.obn350.*;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultUploadedFile;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author reidocrime
 */
@ManagedBean(name = "uploadArquivoOBN350Controlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "upload-obn350", pattern = "/arquivo-obn350/novo/", viewId = "/faces/financeiro/orcamentario/arquivoremessa/arquivoretorno/edita.xhtml"),
    @URLMapping(id = "editandoobn350", pattern = "/arquivo-obn350/editar/#{uploadArquivoOBN350Controlador.id}/", viewId = "/faces/financeiro/orcamentario/arquivoremessa/arquivoretorno/edita.xhtml"),
    @URLMapping(id = "ver-obn350", pattern = "/arquivo-obn350/ver/#{uploadArquivoOBN350Controlador.id}/", viewId = "/faces/financeiro/orcamentario/arquivoremessa/arquivoretorno/visualizar.xhtml"),
    @URLMapping(id = "lista-obn350", pattern = "/arquivo-obn350/listar/", viewId = "/faces/financeiro/orcamentario/arquivoremessa/arquivoretorno/lista.xhtml")
})
public class UploadArquivoOBN350Controlador extends PrettyControlador<ArquivoRetornoOBN350> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(UploadArquivoOBN350Controlador.class);

    @EJB
    private ArquivoRetornoOBN350Facade arquivoRetornoOBN350Facade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private UploadedFile uploadFile;
    private LayoutObn350 layoutObn350;
    private boolean mostraTodosRegistros;
    private Arquivo arquivo;
    private FileUploadEvent fileUploadEvent;

    public UploadArquivoOBN350Controlador() {
        super(ArquivoRetornoOBN350.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return arquivoRetornoOBN350Facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/arquivo-obn350/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "upload-obn350", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataImportacao(sistemaControlador.getDataOperacao());
        selecionado.setExercicio(sistemaControlador.getExercicioCorrente());
        uploadFile = new DefaultUploadedFile();
        mostraTodosRegistros = false;
    }

    @URLAction(mappingId = "editandoobn350", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "ver-obn350", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        selecionado.getArquivo().montarImputStream();
        convertInputStreamArquivoParaObn300(selecionado.getArquivo().getInputStream());
    }

    public void handleFilesUploads(FileUploadEvent event) {
        try {
            fileUploadEvent = event;
            uploadFile = event.getFile();
            arquivo = new Arquivo();
            arquivo.setNome(uploadFile.getFileName());
            arquivo.setDescricao(uploadFile.getFileName());
            arquivo.setTamanho(event.getFile().getSize());
            arquivo.setMimeType(event.getFile().getContentType());

            convertInputStreamArquivoParaObn300(uploadFile.getInputstream());
        } catch (IOException | ExcecaoNegocioGenerica ex) {
            logger.error("Erro: ", ex);
        }
    }

    private void convertInputStreamArquivoParaObn300(InputStream inputStream) throws ExcecaoNegocioGenerica {
        try {
            InputStreamReader in = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(in);
            layoutObn350 = new LayoutObn350(bufferedReader, sistemaControlador.getExercicioCorrente().getAno());
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Erro ao converter em linhas o arquivo selecionado.");
        }
    }

    public void reprocessarGuias() {
        try {
            arquivoRetornoOBN350Facade.reprocessarGuias(selecionado, layoutObn350);
            FacesUtil.addOperacaoRealizada("Reprocessamento realizado com sucesso!");
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um erro ao reprocessar as guias: " + ex.getMessage());
        }
    }

    @Override
    public void salvar() {
        salvarArquivo();
    }

    public void salvarArquivo() {
        try {
            validarArquivo();
            arquivoRetornoOBN350Facade.salvarNovo(selecionado, layoutObn350, arquivo, fileUploadEvent);
            FacesUtil.addOperacaoRealizada("Arquivo baixado no sistema com sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            logger.error("Erro ao salvar o arquivo OBN350: {}", ve);
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao salvar o arquivo OBN350: {}", ex);
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    private void validarArquivo() {
        ValidacaoException ve = new ValidacaoException();
        Exercicio exercicioArquivo = arquivoRetornoOBN350Facade.getExercicioFacade().recuperarExercicioPeloAno(Integer.valueOf(layoutObn350.getHeaderObn350().getAnoDoArquivo()));
        if (!sistemaControlador.getExercicioCorrente().equals(exercicioArquivo)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O exercício do arquivo (" + exercicioArquivo.getAno() + ") é diferente do exercício corrente (" + sistemaControlador.getExercicioCorrente().getAno() + ")!");
        }
        ve.lancarException();
        for (RegistroObn350TipoDois registroObn350TipoDois : layoutObn350.getRegistroObn350TipoDois()) {
            if (DataUtil.getDateParse(registroObn350TipoDois.getDataReferenciaDaRelacao()).after(sistemaControlador.getDataOperacao())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Existe(m) registro(s) no arquivo com data superior à data logada (" + DataUtil.getDataFormatada(sistemaControlador.getDataOperacao()) + ").");
                break;
            }
        }
        ve.lancarException();

    }

    public Boolean arquivoProcessadoComSucesso() {
        if (layoutObn350 == null) {
            return false;
        }
        if (layoutObn350.getRegistroObn350TipoDois() == null) {
            return false;
        }
        for (RegistroObn350TipoDois registroObn350TipoDois : layoutObn350.getRegistroObn350TipoDois()) {
            if (!registroObn350TipoDois.getObteveSucesso()) {
                return false;
            }
        }
        for (RegistroObn350TipoTres registroObn350TipoTres : layoutObn350.getRegistrosObn350TipoTres()) {
            if (!registroObn350TipoTres.getObteveSucesso()) {
                return false;
            }
        }
        for (RegistroObn350TipoQuatro registroObn350TipoQuatro : layoutObn350.getRegistroObn350TipoQuatro()) {
            if (!registroObn350TipoQuatro.getObteveSucesso()) {
                return false;
            }
        }
        for (RegistroObn350TipoCinco registroObn350TipoCinco : layoutObn350.getRegistroObn350TipoCinco()) {
            if (!registroObn350TipoCinco.getObteveSucesso()) {
                return false;
            }
        }
        return true;
    }

    public Boolean renderizarObnTipoTres() {
        return layoutObn350 != null && !layoutObn350.getRegistrosObn350TipoTres().isEmpty();
    }

    public Boolean renderizarObnTipoQuatro() {
        return layoutObn350 != null && !layoutObn350.getRegistroObn350TipoQuatro().isEmpty();
    }

    public Boolean renderizarObnTipoCinco() {
        return layoutObn350 != null && !layoutObn350.getRegistroObn350TipoCinco().isEmpty();
    }

    public void emitirArquivo() {
        try {
            if (layoutObn350 != null) {
                AbstractReport abstractReport = AbstractReport.getAbstractReport();
                HashMap parameters = new HashMap();
                String nomeArquivo = "RelatorioArquivoRetornoOBN350.jasper";
                parameters.put("IMAGEM", abstractReport.getCaminhoImagem());
                parameters.put("SUBREPORT_DIR", abstractReport.getCaminho());
                parameters.put("EMITIR_TODOS", isMostraTodosRegistros());
                parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
                parameters.put("MODULO", "Financeiro");
                parameters.put("MOV_ERRO", retornaQtdeMovComErros());
                parameters.put("MOV_PAGO", retornaQtdeMovPagos());
                parameters.put("USER", sistemaControlador.getUsuarioCorrente().getPessoaFisica().getNome());
                List<LayoutObn350> lista = new ArrayList<>();
                Collections.sort(layoutObn350.getRegistroObn350TipoDois(), new Comparator<RegistroObn350TipoDois>() {
                    @Override
                    public int compare(RegistroObn350TipoDois o1, RegistroObn350TipoDois o2) {
                        return o1.getNumeroMovimento().compareTo(o2.getNumeroMovimento());
                    }
                });
                lista.add(layoutObn350);
                JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(lista);
                abstractReport.gerarRelatorioComDadosEmCollectionAlterandoNomeArquivo(getNomeRelatorio(), nomeArquivo, parameters, ds);
            } else {
                FacesUtil.addErroAoGerarRelatorio("Nenhum arquivo selecionado para a geração do relatório.");
            }
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public BigDecimal retornaQtdeMovComErros() {
        BigDecimal qtdeComErros = BigDecimal.ZERO;
        for (RegistroObn350TipoDois registroObn350TipoDois : layoutObn350.getRegistroObn350TipoDois()) {
            if (!registroObn350TipoDois.getObteveSucesso()) {
                qtdeComErros = qtdeComErros.add(BigDecimal.ONE);
            }
        }
        return qtdeComErros;
    }

    public BigDecimal retornaQtdeMovPagos() {
        BigDecimal qtdePagos = BigDecimal.ZERO;
        for (RegistroObn350TipoDois registroObn350TipoDois : layoutObn350.getRegistroObn350TipoDois()) {
            if (registroObn350TipoDois.getObteveSucesso()) {
                qtdePagos = qtdePagos.add(BigDecimal.ONE);
            }
        }
        return qtdePagos;
    }

    public String getNomeRelatorio() {
        return "RELATORIO-OBN350" + DataUtil.getDataFormatada(selecionado.getDataImportacao());
    }

    public LayoutObn350 getLayoutObn350() {
        return layoutObn350;
    }

    public boolean isMostraTodosRegistros() {
        return mostraTodosRegistros;
    }

    public void setMostraTodosRegistros(boolean mostraTodosRegistros) {
        this.mostraTodosRegistros = mostraTodosRegistros;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}
