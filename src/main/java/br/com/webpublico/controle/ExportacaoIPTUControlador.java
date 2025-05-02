package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ExportacaoIPTU;
import br.com.webpublico.entidadesauxiliares.VOCadastroImobiliario;
import br.com.webpublico.enums.tributario.TipoEnderecoExportacaoIPTU;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ExportacaoIPTUFacade;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigoExportacaoDebitosIPTU;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.io.IOUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

@ManagedBean(name = "exportacaoIPTUControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "exportacaoIPTU", pattern = "/tributario/exportacao-iptu/", viewId = "/faces/tributario/iptu/exportacao/exportacao.xhtml"),
    @URLMapping(id = "exportacaoIPTUAnteriores", pattern = "/tributario/exportacao-iptu-anteriores/", viewId = "/faces/tributario/iptu/exportacao/exportacao.xhtml")
})
public class ExportacaoIPTUControlador implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ExportacaoIPTUControlador.class);
    @EJB
    private ExportacaoIPTUFacade exportacaoIPTUFacade;
    final double CEM = 100D;
    @EJB
    private SingletonGeradorCodigoExportacaoDebitosIPTU singleton;
    private ExportacaoIPTU selecionado;
    private StreamedContent file;
    private Future<ProcessoExportacaoIPTU> future;
    private String nomeArquivo;
    private AssistenteBarraProgressoExportacaoIPTU assistenteBarraProgresso;
    private boolean todosItensMarcados;

    private int activeTabIndex = 0;


    public int getActiveTabIndex() {
        return activeTabIndex;
    }

    public void setActiveTabIndex(int activeTabIndex) {
        this.activeTabIndex = activeTabIndex;
    }

    private List<ProcessoExportacaoIPTU> processos;


    private ProcessoExportacaoIPTU processoExportacaoIPTU;

    public ExportacaoIPTUControlador() {
    }

    public void posExportacao() {
        if (future != null && future.isDone()) {
            try {
                processoExportacaoIPTU = future.get();
                if (processoExportacaoIPTU != null) {
                    FacesUtil.executaJavaScript("terminaExportacao();");
                } else {
                    FacesUtil.addOperacaoNaoRealizada("Nenhum I.P.T.U encontrado para exportação.");
                    FacesUtil.executaJavaScript("terminaExportacaoSemIPTU();");
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    public ExportacaoIPTU getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(ExportacaoIPTU selecionado) {
        this.selecionado = selecionado;
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public AssistenteBarraProgressoExportacaoIPTU getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgressoExportacaoIPTU assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    public List<ProcessoExportacaoIPTU> getProcessos() {
        return processos;
    }

    public ProcessoExportacaoIPTU getProcessoExportacaoIPTU() {
        return processoExportacaoIPTU;
    }

    @URLAction(mappingId = "exportacaoIPTUAnteriores", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void abrirAnteriores() {
        novo();
        activeTabIndex = 1;
    }

    @URLAction(mappingId = "exportacaoIPTU", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        selecionado = new ExportacaoIPTU();
        selecionado.setExercicio(getSistemaControlador().getExercicioCorrente());
        selecionado.setTipoImovel(ExportacaoIPTU.TipoImovelExportacao.AMBOS);
        selecionado.setValorInicial(BigDecimal.ZERO);
        selecionado.setValorFinal(new BigDecimal("999999.99"));
        selecionado.setGeraParcelasJaImpressas(true);
        selecionado.setInscricaoInicial("100000000000000");
        selecionado.setInscricaoFinal("999999999999999");
        if (singleton.getProcessoExpotacaoEmExecucao() != null && singleton.getProcessoExpotacaoEmExecucao().keySet().stream().findFirst().isPresent()) {
            assistenteBarraProgresso = singleton.getProcessoExpotacaoEmExecucao().keySet().stream().findFirst().get();
            future = singleton.getProcessoExpotacaoEmExecucao().get(assistenteBarraProgresso);
        }
        processos = exportacaoIPTUFacade.buscarTodosOrdenandoPorParametro("id desc");
    }

    public StreamedContent gerarArquivoUnico() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        List<ArquivoComposicao> arquivosComposicao = processoExportacaoIPTU.getDetentorArquivoComposicao().getArquivosComposicao();
        if (arquivosComposicao == null || arquivosComposicao.isEmpty()) return null;
        for (ArquivoComposicao arquivoComposicao : arquivosComposicao) {
            try {
                Arquivo arq = exportacaoIPTUFacade.getArquivoFacade().recuperaDependencias(arquivoComposicao.getArquivo().getId());
                arq.montarImputStream();
                copy(arq.getInputStream(), baos);
            } catch (Exception e) {
                FacesUtil.addOperacaoNaoRealizada("Não foi possivel juntar as partes do arquivo");
                logger.error("Não foi possivel juntar as partes do arquivo", e);
                break;
            }
        }
        Arquivo arq = arquivosComposicao.get(0).getArquivo();
        String nomeArquivo = arq.getNome();
        if (nomeArquivo.contains("parte")) nomeArquivo = nomeArquivo.split("_parte")[0];
        InputStream inputStream = new ByteArrayInputStream(baos.toByteArray());
        return new DefaultStreamedContent(inputStream, arq.getMimeType(), nomeArquivo + ".txt");
    }

    private static void copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024];
        int length;
        while ((length = input.read(buffer)) != -1) {
            output.write(buffer, 0, length);
        }
    }

    public boolean hasArquivoParticionado() {
        if (processoExportacaoIPTU == null) return false;
        return processoExportacaoIPTU.getDetentorArquivoComposicao().getArquivosComposicao().size() > 1;
    }

    public void verArquivo(ProcessoExportacaoIPTU processo) {
        processoExportacaoIPTU = exportacaoIPTUFacade.recuperar(processo.getId());
    }

    public void recomecar() {
        singleton.limparProcessoEmExecucao();
        FacesUtil.redirecionamentoInterno("/tributario/exportacao-iptu/");
    }

    public void cancelar() {
        future.cancel(false);
        recomecar();
    }

    public List<SelectItem> getTiposImoveis() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (ExportacaoIPTU.TipoImovelExportacao object : ExportacaoIPTU.TipoImovelExportacao.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposEnderecos() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (TipoEnderecoExportacaoIPTU tipoEndereco : TipoEnderecoExportacaoIPTU.values()) {
            toReturn.add(new SelectItem(tipoEndereco, tipoEndereco.getDescricao()));
        }
        return toReturn;
    }

    public void gerarArquivoIPTU() {
        try {
            assistenteBarraProgresso = new AssistenteBarraProgressoExportacaoIPTU();
            assistenteBarraProgresso.setExercicio(exportacaoIPTUFacade.getSistemaFacade().getExercicioCorrente());
            assistenteBarraProgresso.setUsuarioSistema(exportacaoIPTUFacade.getSistemaFacade().getUsuarioCorrente());
            assistenteBarraProgresso.setDescricaoProcesso("Buscando cadastros para exportação do arquivo de IPTU");

            nomeArquivo = selecionado.getInscricaoInicial() + "-" + selecionado.getInscricaoFinal() + "-" + selecionado.getExercicio().getAno() + ".txt";
            future = exportacaoIPTUFacade.gerarArquivo(selecionado, assistenteBarraProgresso);

            singleton.setProcessoExpotacaoEmExecucao(assistenteBarraProgresso, future);

        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public void downloadExportacao() {
        try {
            if (file != null) {
                escreveNoResponse(IOUtils.toByteArray(file.getStream()), nomeArquivo);
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }


    public void escreveNoResponse(byte[] bytes, String nomeArquivo) throws IOException {
        if (bytes != null && bytes.length > 0) {
            HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            response.setContentType("application/text");
            response.setHeader("Content-Disposition", "inline; filename=" + nomeArquivo);
            response.setContentLength(bytes.length);
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(bytes, 0, bytes.length);
            outputStream.flush();
            outputStream.close();
            FacesContext.getCurrentInstance().responseComplete();
        }
    }

    public StreamedContent getFile() {
        return file;
    }

    public void setFile(StreamedContent file) {
        this.file = file;
    }

    private void validarLote() {
        if (selecionado.getLotes() != null) {
            for (Lote lote : selecionado.getLotes()) {
                if (lote.equals(selecionado.getLote())) {
                    throw new ValidacaoException("Esse Lote já foi adicionado.");
                }
            }
        }
    }

    private void validarSetor() {
        if (selecionado.getSetores() != null && selecionado.getSetor() != null) {
            for (Setor setor : selecionado.getSetores()) {
                if (setor.equals(selecionado.getSetor())) {
                    throw new ValidacaoException("Esse Setor já foi adicionado.");
                }
            }
        }
    }

    private void validarQuadra() {
        if (selecionado.getQuadras() != null && selecionado.getQuadra() != null) {
            for (Quadra quadra : selecionado.getQuadras()) {
                if (quadra.equals(selecionado.getQuadra())) {
                    throw new ValidacaoException("Essa Quadra já foi adicionado.");
                }
            }
        }
    }

    private void validarBairro() {
        if (selecionado.getBairros() != null && selecionado.getBairro() != null) {
            for (Bairro bairro : selecionado.getBairros()) {
                if (bairro.equals(selecionado.getBairro())) {
                    throw new ValidacaoException("Esse Bairro já foi adicionado.");
                }
            }
        }
    }

    private void validarLogradouro() {
        if (selecionado.getLogradouros() != null && selecionado.getBairro() != null) {
            for (Logradouro logradouro : selecionado.getLogradouros()) {
                if (logradouro.equals(selecionado.getLogradouro())) {
                    throw new ValidacaoException("Esse Logradouro já foi adicionado.");
                }
            }
        }
    }

    private void validarCadastrosIgnorados() {
        ValidacaoException ve = new ValidacaoException();
        if (Boolean.TRUE.equals(selecionado.getCadastrosCepInvalido()) && selecionado.getTipoEndereco() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe um tipo de endereço para validar os CEPs.");
        }
        if (Boolean.TRUE.equals(selecionado.getCadastrosNumeroInvalido()) && selecionado.getTipoEndereco() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe um tipo de endereço para validar os Números.");
        }
        if (Strings.isNullOrEmpty(selecionado.getInscricaoInicial())
            && Strings.isNullOrEmpty(selecionado.getInscricaoFinal())
            && selecionado.getQuadras().isEmpty()
            && selecionado.getSetores().isEmpty()
            && selecionado.getLogradouros().isEmpty()
            && selecionado.getBairros().isEmpty()
            && selecionado.getLotes().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione um dos filtros para fazer a busca.");
        }
        ve.lancarException();
    }

    public void removerSetor(Setor setor) {
        selecionado.getSetores().remove(setor);
    }

    public void removerLote(Lote lote) {
        selecionado.getLotes().remove(lote);
    }

    public void removerQuadra(Quadra quadra) {
        selecionado.getQuadras().remove(quadra);
    }

    public void removerBairro(Bairro bairro) {
        selecionado.getBairros().remove(bairro);
    }

    public void removerLogradouro(Logradouro logradouro) {
        selecionado.getLogradouros().remove(logradouro);
    }

    public void removerCadastro(VOCadastroImobiliario cadastro) {
        selecionado.getCadastrosIgnorados().remove(cadastro);
    }

    public void marcarCadastroIgnorado(VOCadastroImobiliario cadastroPesquisado) {
        cadastroPesquisado.setSelecionado(true);
    }

    public void desmarcarCadastroIgnorado(VOCadastroImobiliario cadastroPesquisado) {
        cadastroPesquisado.setSelecionado(false);
    }

    public boolean hasCadastroIgnorado(VOCadastroImobiliario cadastroPesquisado) {
        return !cadastroPesquisado.isSelecionado();
    }

    private void validarCadastrosPesquisados() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getCadastros().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("Selecione ao menos um Cadastro Imobiliário para adicionar na relação de imóveis Ignorados.");
        }
        ve.lancarException();
    }

    public void adicionarCadastrosPesquisados() {
        try {
            validarCadastrosPesquisados();
            for (VOCadastroImobiliario cadastro : selecionado.getCadastros()) {
                if (cadastro.isSelecionado()) {
                    Util.adicionarObjetoEmLista(selecionado.getCadastrosIgnorados(), cadastro);
                }
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void adicionarLogradouro() {
        try {
            validarLogradouro();
            Util.adicionarObjetoEmLista(selecionado.getLogradouros(), selecionado.getLogradouro());
            selecionado.setLogradouro(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void adicionarBairro() {
        try {
            validarBairro();
            Util.adicionarObjetoEmLista(selecionado.getBairros(), selecionado.getBairro());
            selecionado.setBairro(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void adicionarQuadra() {
        try {
            validarQuadra();
            Util.adicionarObjetoEmLista(selecionado.getQuadras(), selecionado.getQuadra());
            selecionado.setQuadra(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void adicionarSetor() {
        try {
            validarSetor();
            Util.adicionarObjetoEmLista(selecionado.getSetores(), selecionado.getSetor());
            selecionado.setSetor(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void adicionarLote() {
        try {
            validarLote();
            Util.adicionarObjetoEmLista(selecionado.getLotes(), selecionado.getLote());
            selecionado.setLote(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void buscarCadastrosIgnorados() {
        try {
            validarCadastrosIgnorados();
            selecionado.setCadastros(exportacaoIPTUFacade.buscarCadastrosImobiliariosIgnorados(selecionado));
            hasCadastrosImobiliarios(selecionado);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void hasCadastrosImobiliarios(ExportacaoIPTU selecionado) {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getCadastros() == null || selecionado.getCadastros().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("Nenhum Cadastro Imobiliário encontrado com os filtros informados.");
        }
        ve.lancarException();
    }

    public void gerarRelatorio() {
        try {
            String arquivoJasper = "RelatorioImoveisIgnorados.jasper";
            AbstractReport report = AbstractReport.getAbstractReport();
            report.setGeraNoDialog(true);
            HashMap parameters = new HashMap();
            parameters.put("NOMERELATORIO", "RELATÓRIO DE IMÓVEIS IGNORADOS");
            parameters.put("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            parameters.put("MODULO", "TRIBUTÁRIO");
            parameters.put("BRASAO", report.getCaminhoImagem());
            parameters.put("EXERCICIO", selecionado.getExercicio());
            parameters.put("USUARIO", exportacaoIPTUFacade.getSistemaFacade().getUsuarioCorrente().getNome());
            parameters.put("DATAOPERACAO", exportacaoIPTUFacade.getSistemaFacade().getDataOperacao());
            parameters.put("FILTROS", selecionado.getFiltros());
            JRBeanCollectionDataSource jb = new JRBeanCollectionDataSource(selecionado.getCadastrosIgnorados());
            report.gerarRelatorioComDadosEmCollection(arquivoJasper, parameters, jb);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }


    public List<Setor> completarSetor(String parte) {
        return exportacaoIPTUFacade.getSetorFacade().listaFiltrando(parte.trim(), "nome");
    }

    public List<Lote> completarLote(String parte) {
        return exportacaoIPTUFacade.getLoteFacade().listaFiltrando(parte.trim(), "codigoLote");
    }

    public List<Quadra> completarQuadra(String parte) {
        return exportacaoIPTUFacade.getQuadraFacade().listaFiltrando(parte.trim(), "codigo");
    }

    public List<Bairro> completarBairro(String parte) {
        return exportacaoIPTUFacade.getBairroFacade().completaBairro(parte.trim());
    }

    public List<Logradouro> completarLogradouro(String parte) {
        return exportacaoIPTUFacade.getLogradouroFacade().listaLogradourosAtivos(parte.trim());
    }

    public boolean isCadastrosImobiliariosIgnorados() {
        try {
            return selecionado.getCadastrosIgnorados() != null && !selecionado.getCadastrosIgnorados().isEmpty();
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public boolean isCadastrosImobiliariosPesquisados() {
        try {
            return selecionado.getCadastros() != null && !selecionado.getCadastros().isEmpty();
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public boolean isTodosItensMarcados() {
        return todosItensMarcados;
    }

    public void setTodosItensMarcados(boolean todosItensMarcados) {
        this.todosItensMarcados = todosItensMarcados;
        for (VOCadastroImobiliario cadastro : selecionado.getCadastros()) {
            cadastro.setSelecionado(todosItensMarcados);
        }
    }

    public class AssistenteBarraProgressoExportacaoIPTU extends AssistenteBarraProgresso {
        private Integer numeroDaLinha;
        private Integer numeroDaLinhaPorInscricao;
        private BigDecimal ufmDoAno;
        private List<Tributo> tributosUsadoNoIPTUDoExercicio;

        private ProcessoExportacaoIPTU processoExportacaoIPTU;

        public ProcessoExportacaoIPTU getProcessoExportacaoIPTU() {
            return processoExportacaoIPTU;
        }

        public void setProcessoExportacaoIPTU(ProcessoExportacaoIPTU processoExportacaoIPTU) {
            this.processoExportacaoIPTU = processoExportacaoIPTU;
        }

        public Integer getNumeroDaLinha() {
            return numeroDaLinha;
        }

        public void setNumeroDaLinha(Integer numeroDaLinha) {
            this.numeroDaLinha = numeroDaLinha;
        }

        public Integer getNumeroDaLinhaPorInscricao() {
            return numeroDaLinhaPorInscricao;
        }

        public void setNumeroDaLinhaPorInscricao(Integer numeroDaLinhaPorInscricao) {
            this.numeroDaLinhaPorInscricao = numeroDaLinhaPorInscricao;
        }

        public BigDecimal getUfmDoAno() {
            return ufmDoAno;
        }

        public void setUfmDoAno(BigDecimal ufmDoAno) {
            this.ufmDoAno = ufmDoAno;
        }

        public List<Tributo> getTributosUsadoNoIPTUDoExercicio() {
            return tributosUsadoNoIPTUDoExercicio;
        }

        public void setTributosUsadoNoIPTUDoExercicio(List<Tributo> tributosUsadoNoIPTUDoExercicio) {
            this.tributosUsadoNoIPTUDoExercicio = tributosUsadoNoIPTUDoExercicio;
        }

        public String contaNumeroDaLinha() {
            return (++numeroDaLinha) + "";
        }

        public String contaNumeroDaLinhaPorInscricao() {
            return numeroDaLinhaPorInscricao + "";
        }
    }
}
