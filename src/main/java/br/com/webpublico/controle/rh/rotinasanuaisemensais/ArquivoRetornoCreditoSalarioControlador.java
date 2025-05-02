package br.com.webpublico.controle.rh.rotinasanuaisemensais;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidades.rh.creditodesalario.caixa.ItemRetornoCreditoSalario;
import br.com.webpublico.entidades.rh.creditodesalario.caixa.RetornoCaixaOcorrencias;
import br.com.webpublico.entidades.rh.creditodesalario.caixa.RetornoCreditoSalario;
import br.com.webpublico.entidadesauxiliares.DependenciasDirf;
import br.com.webpublico.enums.CodigoOcorrenciaRetornoArquivoCaixa;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoRegistroCnab240;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.rh.rotinasanuaismensais.ArquivoRetornoCreditoSalarioFacade;
import br.com.webpublico.util.*;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.lowagie.text.DocumentException;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.io.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultUploadedFile;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.*;
import java.util.*;
import java.util.concurrent.Future;

@ManagedBean(name = "arquivoRetornoCreditoSalarioControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "upload-arquivo-credito-salario", pattern = "/retorno-arquivo-credito-de-salario/novo/", viewId = "/faces/rh/rotinasanuaisemensais/arquivoretorno/edita.xhtml"),
    @URLMapping(id = "editar-retorno-credito-salario", pattern = "/retorno-arquivo-credito-de-salario/editar/#{arquivoRetornoCreditoSalarioControlador.id}/", viewId = "/faces/rh/rotinasanuaisemensais/arquivoretorno/edita.xhtml"),
    @URLMapping(id = "ver-retorno-credito-salario", pattern = "/retorno-arquivo-credito-de-salario/ver/#{arquivoRetornoCreditoSalarioControlador.id}/", viewId = "/faces/rh/rotinasanuaisemensais/arquivoretorno/visualizar.xhtml"),
    @URLMapping(id = "lista-retorno-credito-salario", pattern = "/retorno-arquivo-credito-de-salario/listar/", viewId = "/faces/rh/rotinasanuaisemensais/arquivoretorno/lista.xhtml")
})
public class ArquivoRetornoCreditoSalarioControlador extends PrettyControlador<RetornoCreditoSalario> implements CRUD {

    File pdfFile;
    @EJB
    private ArquivoRetornoCreditoSalarioFacade facade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    private FileUploadEvent fileUploadEvent;
    private UploadedFile uploadFile;
    private ItemRetornoCreditoSalario itemRetorno;
    private ConverterGenerico converterExercicio;
    private boolean mostratRegistrosComErros;
    private boolean terminouOProcessamentoArquivo;
    private VinculoFP vinculoFPSelecionado;
    private ItemRetornoCreditoSalario itemRetornoSelecionado;
    private DependenciasDirf dependenciasDirf;
    private Future<AssistenteBarraProgresso> future;
    private Future<List<ItemRetornoCreditoSalario>> futureItemRetorno;
    private List<Future<Set<Long>>> futureFichaFinanceira;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private Map<String, VinculoFP> vinculoPorCpf;
    private List<Future<AssistenteBarraProgresso>> itemFutureSalvarRegistro;
    private List<ItemRetornoCreditoSalario> itensVisualizar;
    private List<CodigoOcorrenciaRetornoArquivoCaixa> ocorrencias;

    public ArquivoRetornoCreditoSalarioControlador() {
        super(RetornoCreditoSalario.class);
        itemFutureSalvarRegistro = Lists.newArrayList();
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    public List<ItemRetornoCreditoSalario> getItensVisualizar() {
        return itensVisualizar;
    }

    public void setItensVisualizar(List<ItemRetornoCreditoSalario> itensVisualizar) {
        this.itensVisualizar = itensVisualizar;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/retorno-arquivo-credito-de-salario/";
    }

    @Override
    public Object getUrlKeyValue() {
        return getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "upload-arquivo-credito-salario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        init();
        uploadFile = new DefaultUploadedFile();
        ocorrencias = Lists.newArrayList();
    }

    private void init() {
        selecionado.setDataRegistro(sistemaFacade.getDataOperacao());
        itemRetorno = new ItemRetornoCreditoSalario();
        mostratRegistrosComErros = false;
        terminouOProcessamentoArquivo = false;
        dependenciasDirf = new DependenciasDirf();
        vinculoPorCpf = new HashMap<>();
    }

    @URLAction(mappingId = "ver-retorno-credito-salario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        ocorrencias = Lists.newArrayList();
    }

    public void recuperarItemRetorno() {
        futureItemRetorno = facade.buscarItensRetorno(selecionado);
    }

    @Override
    public void salvar() {
        try {
            validaArquivoSelecionado();
            criarArquivoPDFLog();
            selecionado = facade.salvarNovo(selecionado, uploadFile, pdfFile);
            adicionarLogTipoSucesso(dependenciasDirf, getMensagemSucessoAoSalvar());
            assistenteBarraProgresso = new AssistenteBarraProgresso();
            assistenteBarraProgresso.setDescricaoProcesso("Buscando Ficha Financeira para atualização...");
            assistenteBarraProgresso.setTotal(selecionado.getItemRetornoCreditoSalario().size());

            selecionado.setDataRegistro(new Date());

            int partes = selecionado.getItemRetornoCreditoSalario().size() > 500 ? (selecionado.getItemRetornoCreditoSalario().size() / 4) : selecionado.getItemRetornoCreditoSalario().size();
            List<List<ItemRetornoCreditoSalario>> itemRetorno = Lists.partition(Lists.newArrayList(selecionado.getItemRetornoCreditoSalario()), partes);
            futureFichaFinanceira = Lists.newArrayList();
            for (List<ItemRetornoCreditoSalario> parte : itemRetorno) {
                futureFichaFinanceira.add(facade.buscarFichaFinanceira(parte, assistenteBarraProgresso));
            }
            FacesUtil.executaJavaScript("acompanhaBuscaFichaFinanceira()");
        } catch (ValidacaoException ex) {
            for (FacesMessage facesMessage : ex.getMensagens()) {
                adicionarLogTipoErro(dependenciasDirf, "Ocorreu um erro ao salvar os Arquivo de Retorno: " + facesMessage.getDetail());
            }
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    public void salvarFichaFinanceira() {
        try {
            List<Long> fichas = Lists.newArrayList();
            for (Future<Set<Long>> listFuture : futureFichaFinanceira) {
                fichas.addAll(listFuture.get());
            }
            assistenteBarraProgresso.zerarContadoresProcesso();
            assistenteBarraProgresso.setDescricaoProcesso("Finalizando o processo e atualizando os registros...");
            assistenteBarraProgresso.setTotal(fichas.size());
            itemFutureSalvarRegistro.add(facade.atualizarFichaFinanceira(assistenteBarraProgresso, fichas));
            FacesUtil.executaJavaScript("verificarSeTerminouSalvar()");

        } catch (Exception e) {
            logger.error("Problema ao atualzar ficha financeira ", e.getMessage());
        }
    }

    private void validaArquivoSelecionado() {
        ValidacaoException exception = new ValidacaoException();
        Util.validarCampos(selecionado);
        if (fileUploadEvent == null) {
            exception.adicionarMensagemDeCampoObrigatorio("O Arquivo deve ser selecionado!");
            throw exception;
        }
    }


    public void handleFilesUploads(FileUploadEvent event) {
        try {
            Util.validarCampos(selecionado);
            selecionado.setItemRetornoCreditoSalario(new ArrayList<ItemRetornoCreditoSalario>());
            dependenciasDirf.setLogGeral(new HashMap<DependenciasDirf.TipoLog, List<String>>());
            facade.adicionarLogTipoCabecalho(dependenciasDirf, "Iniciando o upload e a leitura do arquivo, aguarde...");
            fileUploadEvent = event;
            uploadFile = event.getFile();
            StringWriter stringWriter = new StringWriter();
            IOUtils.copy(uploadFile.getInputstream(), stringWriter);
            InputStreamReader streamReader = new InputStreamReader(uploadFile.getInputstream());
            BufferedReader reader = new BufferedReader(streamReader);
            int quantidadeLinhas = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                montarVinculoPorCPF(line, itemRetorno);
                quantidadeLinhas++;
            }
            assistenteBarraProgresso = new AssistenteBarraProgresso();
            assistenteBarraProgresso.setDescricaoProcesso("Iniciando o upload e a leitura do arquivo");
            assistenteBarraProgresso.setTotal(1);
            assistenteBarraProgresso.setTotal(quantidadeLinhas);
            future = facade.processarConteudoArquivo(selecionado, vinculoPorCpf, dependenciasDirf, itemRetorno, uploadFile, isOperacaoNovo(), assistenteBarraProgresso);
            FacesUtil.executaJavaScript("acompanhaLeituraArquivo()");
        } catch (Exception e) {
            logger.error("Não foi possível fazer o upload do arquivo, Erro: " + e.getMessage());
            adicionarLogTipoErro(dependenciasDirf, "Não foi possível fazer o upload do arquivo!");
            logger.error("Erro:", e);
        }
    }

    private void montarVinculoPorCPF(String line, ItemRetornoCreditoSalario itemRetorno) {
        TipoRegistroCnab240 tipoRegistroCnab240 = itemRetorno.getTipoRegistroCnab240(line);
        String cpf = itemRetorno.getCPFCnb240(line, tipoRegistroCnab240);
        Long vinculo = facade.recuperarVinculosFP(cpf, selecionado, dependenciasDirf);
        if (vinculo != null) {
            vinculoPorCpf.put(cpf, facade.getVinculoFPFacade().recuperarSimples(vinculo));
        }
    }

    public List<SelectItem> getMeses() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, " "));
        for (Mes mes : Mes.values()) {
            retorno.add(new SelectItem(mes, mes.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getExercicios() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem(null, ""));
        for (Exercicio ex : this.exercicioFacade.listaExerciciosAtual()) {
            lista.add(new SelectItem(ex, ex.toString()));
        }
        return lista;
    }

    public ConverterGenerico getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterGenerico(Exercicio.class, exercicioFacade);
        }
        return converterExercicio;
    }

    public void selecionarItemRetono(ItemRetornoCreditoSalario itemRetorno) {
        itemRetornoSelecionado = itemRetorno;
    }


    public ItemRetornoCreditoSalario getItemRetorno() {
        return itemRetorno;
    }

    public void setItemRetorno(ItemRetornoCreditoSalario itemRetorno) {
        this.itemRetorno = itemRetorno;
    }

    public VinculoFP getVinculoFPSelecionado() {
        return vinculoFPSelecionado;
    }

    public void setVinculoFPSelecionado(VinculoFP vinculoFPSelecionado) {
        this.vinculoFPSelecionado = vinculoFPSelecionado;
    }

    public boolean isMostratRegistrosComErros() {
        return mostratRegistrosComErros;
    }

    public void setMostratRegistrosComErros(boolean mostratRegistrosComErros) {
        this.mostratRegistrosComErros = mostratRegistrosComErros;
    }

    public DependenciasDirf getDependenciasDirf() {
        return dependenciasDirf;
    }

    public void setDependenciasDirf(DependenciasDirf dependenciasDirf) {
        this.dependenciasDirf = dependenciasDirf;
    }

    public ItemRetornoCreditoSalario getItemRetornoSelecionado() {
        return itemRetornoSelecionado;
    }

    public StreamedContent recuperarArquivoParaDownload(Arquivo arquivoSelecionado) {
        return facade.recuperarArquivoSelecionadoParaDownload(arquivoSelecionado);
    }

    private void adicionarLogTipoSucesso(DependenciasDirf dependenciasDirf, String mensagem) {
        dependenciasDirf.adicionarLog(DependenciasDirf.TipoLog.SUCESSO, mensagem);
    }

    private void adicionarLogTipoErro(DependenciasDirf dependenciasDirf, String mensagem) {
        dependenciasDirf.adicionarLog(DependenciasDirf.TipoLog.ERRO, mensagem);
    }

    public boolean isTerminouOProcessamentoArquivo() {
        return terminouOProcessamentoArquivo;
    }

    public void setTerminouOProcessamentoArquivo(boolean terminouOProcessamentoArquivo) {
        this.terminouOProcessamentoArquivo = terminouOProcessamentoArquivo;
    }

    public List<CodigoOcorrenciaRetornoArquivoCaixa> getOcorrencias() {
        return ocorrencias;
    }

    public void setOcorrencias(List<CodigoOcorrenciaRetornoArquivoCaixa> ocorrencias) {
        this.ocorrencias = ocorrencias;
    }

    public String getConteudoArquivoPDF(boolean mostrarAntesDeSalvar) {
        String content = "";
        content += "<?xml version='1.0' encoding='iso-8859-1'?>\n";
        content += "<!DOCTYPE HTML PUBLIC 'HTML 4.01 Transitional//PT' 'http://www.w3.org/TR/html4/loose.dtd'>\n";
        content += "<html>\n";
        content += " <head>";
        content += " <title></title>";
        content += " </head>\n";
        content += " <body style='font-family: Arial, 'Helvetica Neue', Helvetica, sans-serif; font-size: 10px;'>";

        content += "<div style='border: 1px solid black;text-align: left; padding : 3px;'>\n";
        content += " <table>" + "<tr>";
        content += " <td width='100'><img src='" + getCaminhoBrasao() + "' alt='Smiley face' height='80' width='75' /></td>   ";
        content += " <td><b> PREFEITURA MUNICIPAL DE RIO BRANCO <br/>\n";
        content += "         MUNICÍPIO DE RIO BRANCO<br/>\n";
        content += "         INCONSISTÊNCIAS ARQUIVO DE CRÉDITO DE SALÁRIO DA CAIXA ECONÔMICA FEDERAL </b></td>\n";
        content += "</tr>" + "</table>";
        content += "</div>\n";

        content += "<div style='border: 1px solid black;text-align: left; margin-top: -1px; padding : 3px;'>\n";
        content += " <table cellpadding='4' cellspacing='0' style='margin-top: 10px; border-collapse: collapse; align=left'>";
        content += "<tr>";
        content += "  <td><b>Mensagens:</b></td>";
        content += "</tr>";
        content += "<td>" + dependenciasDirf.recuperarSomenteStringDoLogEstruturado() + "</td>";
        content += "</table>";
        content += "</div>\n";

        content += "</body>";
        content += "</html>";
        if (mostrarAntesDeSalvar) {
            String nome = "Log da geração do arquivo de Retorno de Crédito de Salário da Caixa Econômica Federal" + selecionado;
            nome = nome.replace(" ", "_");
            Util.downloadPDF(nome, content, FacesContext.getCurrentInstance());
            return null;
        }
        return content;
    }

    public void gerarLog() {
        try {
            validaArquivoSelecionado();
            getConteudoArquivoPDF(true);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }

    }

    public String getCaminhoBrasao() {
        String imagem = FacesUtil.geraUrlImagemDir();
        imagem += "/img/escudo.png";
        return imagem;
    }

    public void criarArquivoPDFLog() {
        try {
            pdfFile = File.createTempFile("RETORNO_CREDITO_SLARIO_CAIXA_LOG", "pdf");
            FileOutputStream fos = new FileOutputStream(pdfFile);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Html2Pdf.convert(getConteudoArquivoPDF(false), baos);
            byte[] bytes = baos.toByteArray();
            fos.write(bytes);
            fos.close();
        } catch (IOException e) {
            logger.error("Ocorreu um eror ao criar o arquivo de Log, Erro: " + e.getMessage());
            adicionarLogTipoErro(dependenciasDirf, "Ocorreu um eror ao criar o arquivo de Log, Erro: " + e.getLocalizedMessage());
        } catch (DocumentException e) {
            logger.error("Ocorreu um eror ao criar o arquivo de Log, Erro: " + e.getMessage());
            adicionarLogTipoErro(dependenciasDirf, "Ocorreu um eror ao criar o arquivo de Log, Erro: " + e.getLocalizedMessage());
        } catch (Exception e) {
            logger.error("Ocorreu um eror ao criar o arquivo de Log, Erro: " + e.getMessage());
            adicionarLogTipoErro(dependenciasDirf, "Ocorreu um erro ao criar o arquivo de Log, Erro: " + e.getLocalizedMessage());
        }
    }


    public List<ItemRetornoCreditoSalario> getListaSemVinculosDuplicados() {
        List<ItemRetornoCreditoSalario> itensRetorno = new ArrayList<>();
        if (isMostratRegistrosComErros()) {
            for (ItemRetornoCreditoSalario itemRetornoCreditoSalario : selecionado.getItemRetornoCreditoSalario()) {
                if (!itemRetornoCreditoSalario.getRetornoCaixaOcorrencias().isEmpty()) {
                    itensRetorno.add(itemRetornoCreditoSalario);
                }
            }
        } else {
            itensRetorno.addAll(selecionado.getItemRetornoCreditoSalario());
        }
        return itensRetorno;
    }

    public void verificarSeTerminouLeitura() {
        FacesUtil.executaJavaScript("aguarde.hide()");
        if (future != null && future.isDone()) {
            FacesUtil.executaJavaScript("termina()");
            terminouOProcessamentoArquivo = true;
            future = null;
        }
    }

    public void verificarSeTerminouBuscarFichas() {
        FacesUtil.executaJavaScript("aguarde.hide()");
        boolean terminou = false;
        if (futureFichaFinanceira != null && !futureFichaFinanceira.isEmpty()) {
            terminou = true;
            for (Future<Set<Long>> listFuture : futureFichaFinanceira) {
                if (!listFuture.isDone()) {
                    terminou = false;
                    break;
                }
            }
        }
        if (terminou) {
            FacesUtil.executaJavaScript("salvarFichaFinanceira()");
            terminouOProcessamentoArquivo = true;
        }
    }

    public Boolean verificarSeTerminoPersistir() {
        boolean terminou = false;
        if (itemFutureSalvarRegistro != null && !itemFutureSalvarRegistro.isEmpty()) {
            terminou = true;
            for (Future<AssistenteBarraProgresso> future : itemFutureSalvarRegistro) {
                if (!future.isDone()) {
                    terminou = false;
                    break;
                }
            }
        }
        if (terminou) {
            FacesUtil.executaJavaScript("terminaAtualizacaoFichaFinanceira()");
            terminouOProcessamentoArquivo = true;
            future = null;
        }
        return terminou;
    }

    public void verificarSeTerminouBuscarItens() {
        if (futureItemRetorno != null && futureItemRetorno.isDone()) {
            FacesUtil.executaJavaScript("termina()");
            try {
                itensVisualizar = futureItemRetorno.get();
                FacesUtil.atualizarComponente("Formulario");
            } catch (Exception e) {
                logger.error("Ocorreu um erro ao buscar os itens de retorno da caixa ", e.getMessage());
            }
            futureItemRetorno = null;
        }
    }

    public String buscarDescricaoOcorrencias(String ocorrencias) {
        List<CodigoOcorrenciaRetornoArquivoCaixa> codigosOcorrencia = facade.buscarOcorrencias(ocorrencias);
        StringBuilder retorno = new StringBuilder("");
        for (CodigoOcorrenciaRetornoArquivoCaixa ocorrencia : codigosOcorrencia) {
            retorno.append(ocorrencia.getDescricao()).append(", ");
        }
        if (!retorno.toString().isEmpty()) {
            return retorno.substring(0, retorno.length() - 2) + ".";
        } else {
            return "";
        }
    }

    public String obterIconeOcorrencias(String codigosOcorrencias) {
        List<CodigoOcorrenciaRetornoArquivoCaixa> ocorrencias = facade.buscarOcorrencias(codigosOcorrencias);
        String retorno = "fa fa-check";
        for (CodigoOcorrenciaRetornoArquivoCaixa ocorrencia : ocorrencias) {
            if (!CodigoOcorrenciaRetornoArquivoCaixa.CREDITO_DEBITO_EFETIVADO.equals(ocorrencia) && !CodigoOcorrenciaRetornoArquivoCaixa.DEBITO_AUTORIZADO_PELA_AGENCIA.equals(ocorrencia)) {
                retorno = "fa fa-exclamation-triangle";
            }
        }
        return retorno;
    }

    public String obterCampoOcorrencias(String codigosOcorrencias) {
        List<CodigoOcorrenciaRetornoArquivoCaixa> ocorrencias = facade.buscarOcorrencias(codigosOcorrencias);
        String retorno = "alert alert-success";
        for (CodigoOcorrenciaRetornoArquivoCaixa ocorrencia : ocorrencias) {
            if (!CodigoOcorrenciaRetornoArquivoCaixa.CREDITO_DEBITO_EFETIVADO.equals(ocorrencia) && !CodigoOcorrenciaRetornoArquivoCaixa.DEBITO_AUTORIZADO_PELA_AGENCIA.equals(ocorrencia)) {
                retorno = "alert alert-warning";
            }
        }
        return retorno;
    }

    public String obterEndereco(String line) {
        StringBuilder retorno = new StringBuilder("");
        if (!Strings.isNullOrEmpty(line)) {
            retorno.append(line.substring(32, 62).trim()).append(", ");
            retorno.append(StringUtil.removeZerosEsquerda(line.substring(62, 67).trim())).append(", ");
            retorno.append(line.substring(67, 82).trim()).append(", ");
            retorno.append(line.substring(82, 97).trim()).append(", ");
            retorno.append(line.substring(97, 117).trim()).append(", ");
            retorno.append(line.substring(117, 125).trim()).append(", ");
            retorno.append(line.substring(125, 127).trim()).append(".");
        }
        return retorno.toString();
    }

    public String obterLoteServico(ItemRetornoCreditoSalario item) {
        return StringUtil.removeZerosEsquerda(item.getLinhaArquivo().substring(3, 7));
    }

    public String obterDescricaoOcorrenciasDetalheB(ItemRetornoCreditoSalario item) {
        String ocorrencias = item.getLinhaArquivo().substring(225, 240);
        if (!Strings.isNullOrEmpty(ocorrencias) && ocorrencias.trim().length() == 14 && item.getVinculoFP().getMatriculaFP().getPessoa().getCpf().equals(ocorrencias)) {
            return "";
        } else {
            return buscarDescricaoOcorrencias(ocorrencias);
        }
    }

    public String obterIconeOcorrenciasDetalheB(ItemRetornoCreditoSalario item) {
        String ocorrencias = item.getLinhaArquivo().substring(225, 240);
        if (!Strings.isNullOrEmpty(ocorrencias) && ocorrencias.trim().length() == 14 && item.getVinculoFP().getMatriculaFP().getPessoa().getCpf().equals(ocorrencias)) {
            return "fa fa-check";
        } else {
            return obterIconeOcorrencias(ocorrencias);
        }
    }

    public String obterCampoOcorrenciasDetalheB(ItemRetornoCreditoSalario item) {
        String ocorrencias = item.getLinhaArquivo().substring(225, 240);
        if (!Strings.isNullOrEmpty(ocorrencias) && ocorrencias.trim().length() == 14 && item.getVinculoFP().getMatriculaFP().getPessoa().getCpf().equals(ocorrencias)) {
            return "alert alert-success";
        } else {
            return obterCampoOcorrencias(ocorrencias);
        }
    }


    public String obterIconeOcorrencia(CodigoOcorrenciaRetornoArquivoCaixa ocorrencia) {
        if (CodigoOcorrenciaRetornoArquivoCaixa.CREDITO_DEBITO_EFETIVADO.equals(ocorrencia) || CodigoOcorrenciaRetornoArquivoCaixa.DEBITO_AUTORIZADO_PELA_AGENCIA.equals(ocorrencia)) {
            return "fa fa-check";
        } else {
            return "fa fa-exclamation-triangle";
        }
    }

    public String obterCampoOcorrencia(CodigoOcorrenciaRetornoArquivoCaixa ocorrencia) {
        if (CodigoOcorrenciaRetornoArquivoCaixa.CREDITO_DEBITO_EFETIVADO.equals(ocorrencia) || CodigoOcorrenciaRetornoArquivoCaixa.DEBITO_AUTORIZADO_PELA_AGENCIA.equals(ocorrencia)) {
            return "alert alert-success";
        } else {
            return "alert alert-warning";
        }
    }

    public void carregarOcorrenciasRetorno(String ocorrencias) {
        this.ocorrencias = Lists.newArrayList();
        if (ocorrencias != null) {
            this.ocorrencias = facade.buscarOcorrencias(ocorrencias);
        }
    }

    public void carregarOcorrencias(ItemRetornoCreditoSalario item) {
        this.ocorrencias = Lists.newArrayList();
        List<RetornoCaixaOcorrencias> ocorrencias = facade.buscarOcorrencias(item);
        for (RetornoCaixaOcorrencias ocorrencia : ocorrencias) {
            this.ocorrencias.add(ocorrencia.getOcorrencia());
        }
    }

    public Boolean isAtencaoRetorno(String ocorrencias) {
        List<CodigoOcorrenciaRetornoArquivoCaixa> listOcorrencias = facade.buscarOcorrencias(ocorrencias);
        for (CodigoOcorrenciaRetornoArquivoCaixa ocorrencia : listOcorrencias) {
            return !CodigoOcorrenciaRetornoArquivoCaixa.CREDITO_DEBITO_EFETIVADO.equals(ocorrencia) && !CodigoOcorrenciaRetornoArquivoCaixa.DEBITO_AUTORIZADO_PELA_AGENCIA.equals(ocorrencia);
        }

        return false;
    }

    public Boolean isAtencao(ItemRetornoCreditoSalario item) {
        List<RetornoCaixaOcorrencias> ocorrencias = facade.buscarOcorrencias(item);
        for (RetornoCaixaOcorrencias ocorrencia : ocorrencias) {
            return !CodigoOcorrenciaRetornoArquivoCaixa.CREDITO_DEBITO_EFETIVADO.equals(ocorrencia.getOcorrencia()) && !CodigoOcorrenciaRetornoArquivoCaixa.DEBITO_AUTORIZADO_PELA_AGENCIA.equals(ocorrencia.getOcorrencia());
        }
        return false;
    }

    public void redirecionaVer() {
        if (selecionado != null && selecionado.getId() != null) {
            redireciona(getCaminhoPadrao() + "ver/" + selecionado.getId());
        } else {
            redireciona();
        }
    }
}
