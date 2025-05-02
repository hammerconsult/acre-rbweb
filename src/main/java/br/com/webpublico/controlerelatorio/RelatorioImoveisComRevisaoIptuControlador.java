package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.Logradouro;
import br.com.webpublico.entidades.ProcessoCalculoIPTU;
import br.com.webpublico.entidadesauxiliares.FiltroRelatorioImoveisComRevisaoIptu;
import br.com.webpublico.entidadesauxiliares.RelatorioRevisaoIPTU;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoImovel;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.LogradouroFacade;
import br.com.webpublico.negocios.ProcessoCalculoIPTUFacade;
import br.com.webpublico.negocios.RelatorioRevisaoIPTUFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-revisao-iptu", pattern = "/relatorio/revisao-iptu/", viewId = "/faces/tributario/iptu/relatorio/relatorio-imoveis-com-revisao-iptu.xhtml")
})
public class RelatorioImoveisComRevisaoIptuControlador extends AbstractReport {

    private final Logger logger = LoggerFactory.getLogger(RelatorioImoveisComRevisaoIptuControlador.class);

    private FiltroRelatorioImoveisComRevisaoIptu filtroRelatorio;
    @EJB
    private ProcessoCalculoIPTUFacade processoCalculoIPTUFacade;
    @EJB
    private LogradouroFacade logradouroFacade;
    @EJB
    private RelatorioRevisaoIPTUFacade relatorioRevisaoIPTUFacade;

    private AssistenteBarraProgresso assistenteBarraProgresso;
    private Future<ByteArrayOutputStream> futureBytesRelatorio;
    private List<Future<List<RelatorioRevisaoIPTU>>> listaFuturesAssistente;
    private boolean finalizando;


    @URLAction(mappingId = "novo-revisao-iptu", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparFiltros() {
        filtroRelatorio = new FiltroRelatorioImoveisComRevisaoIptu();
        assistenteBarraProgresso = new AssistenteBarraProgresso();
        filtroRelatorio.setExercicioInicial(getExercicioCorrente());
        filtroRelatorio.setExercicioFinal(getExercicioCorrente());
    }

    public void processar() throws IOException {
        try {
            finalizando = Boolean.FALSE;
            filtroRelatorio.setCancelarProcesso(Boolean.FALSE);
            montarFiltros();

            assistenteBarraProgresso = new AssistenteBarraProgresso();
            assistenteBarraProgresso.setDescricaoProcesso("Processando dados...");
            validarCampos();
            List<BigDecimal> ids = relatorioRevisaoIPTUFacade.buscarBciPorFiltros(filtroRelatorio);
            if (ids != null && !ids.isEmpty()) {
                assistenteBarraProgresso.setTotal(ids.size());
                listaFuturesAssistente = new ArrayList<>();
                int partes = ids.size() > 100 ? (ids.size() / 4) : ids.size();
                List<List<BigDecimal>> partition = Lists.partition(ids, partes);
                for (List<BigDecimal> idsParte : partition) {
                    listaFuturesAssistente.add(relatorioRevisaoIPTUFacade.buscarRevisoesCalculoIPTU(idsParte, filtroRelatorio, assistenteBarraProgresso));
                }
            } else {
                FacesUtil.addOperacaoNaoRealizada("Nenhum registro encontrado a partir dos filtros informados!");
            }
        } catch (ValidacaoException vl) {
            FacesUtil.printAllFacesMessages(vl.getMensagens());
            listaFuturesAssistente = null;
        }
    }

    private void montarFiltros() {
        filtroRelatorio.setFiltrosUtilizados(new StringBuilder());
        String separarFiltro = "";
        if (filtroRelatorio.getExercicioInicial() != null) {
            filtroRelatorio.getFiltrosUtilizados().append(separarFiltro).append("Exercício Inicial: ").append(filtroRelatorio.getExercicioInicial().getAno());
            separarFiltro = ", ";
        }
        if (filtroRelatorio.getExercicioFinal() != null) {
            filtroRelatorio.getFiltrosUtilizados().append(separarFiltro).append("Exercício Final: ").append(filtroRelatorio.getExercicioFinal().getAno());
            separarFiltro = ", ";
        }
        if (filtroRelatorio.getProtocolo() != null && !filtroRelatorio.getProtocolo().isEmpty()) {
            filtroRelatorio.getFiltrosUtilizados().append(separarFiltro).append("Protocolo: ").append(filtroRelatorio.getProtocolo());
            separarFiltro = ", ";
        }
        if (filtroRelatorio.getDataLancamentoInicial() != null) {
            filtroRelatorio.getFiltrosUtilizados().append(separarFiltro).append("Data de Lançamento Inicial: ").append(DataUtil.getDataFormatada(filtroRelatorio.getDataLancamentoInicial()));
            separarFiltro = ", ";
        }
        if (filtroRelatorio.getDataLancamentoFinal() != null) {
            filtroRelatorio.getFiltrosUtilizados().append(separarFiltro).append("Data de Lançamento Final: ").append(DataUtil.getDataFormatada(filtroRelatorio.getDataLancamentoFinal()));
            separarFiltro = ", ";
        }
        if (filtroRelatorio.getDataVencimentoInicial() != null) {
            filtroRelatorio.getFiltrosUtilizados().append(separarFiltro).append("Data de Vencimento Inicial: ").append(DataUtil.getDataFormatada(filtroRelatorio.getDataVencimentoInicial()));
            separarFiltro = ", ";
        }
        if (filtroRelatorio.getDataVencimentoFinal() != null) {
            filtroRelatorio.getFiltrosUtilizados().append(separarFiltro).append("Data de Vencimento Final: ").append(DataUtil.getDataFormatada(filtroRelatorio.getDataVencimentoFinal()));
            separarFiltro = ", ";
        }
        if (filtroRelatorio.getProcessoCalculoIPTU() != null) {
            filtroRelatorio.getFiltrosUtilizados().append(separarFiltro).append("Processo: ").append(filtroRelatorio.getProcessoCalculoIPTU());
            separarFiltro = ", ";
        }
        if (filtroRelatorio.getBairro() != null) {
            filtroRelatorio.getFiltrosUtilizados().append(separarFiltro).append("Bairro: ").append(filtroRelatorio.getBairro().getDescricao());
            separarFiltro = ", ";
        }
        if (filtroRelatorio.getLogradouro() != null) {
            filtroRelatorio.getFiltrosUtilizados().append(separarFiltro).append("Logradoudo: ").append(filtroRelatorio.getLogradouro().getNome());
            separarFiltro = ", ";
        }
        if (filtroRelatorio.getTipoImovel() != null) {
            filtroRelatorio.getFiltrosUtilizados().append(separarFiltro).append("Tipo Imóvel: ").append(filtroRelatorio.getTipoImovel().getDescricao());
            separarFiltro = ", ";
        }
        if (filtroRelatorio.getSetorInicial() != null && !filtroRelatorio.getSetorInicial().trim().isEmpty()) {
            filtroRelatorio.getFiltrosUtilizados().append(separarFiltro).append("Setor Inicial: ").append(filtroRelatorio.getSetorInicial());
            separarFiltro = ", ";
        }
        if (filtroRelatorio.getSetorFinal() != null && !filtroRelatorio.getSetorFinal().trim().isEmpty()) {
            filtroRelatorio.getFiltrosUtilizados().append(separarFiltro).append("Setor Final: ").append(filtroRelatorio.getSetorFinal());
            separarFiltro = ", ";
        }
        if (filtroRelatorio.getQuadraInicial() != null && !filtroRelatorio.getQuadraInicial().trim().isEmpty()) {
            filtroRelatorio.getFiltrosUtilizados().append(separarFiltro).append("Quadra Inicial: ").append(filtroRelatorio.getQuadraInicial());
            separarFiltro = ", ";
        }
        if (filtroRelatorio.getQuadraFinal() != null && !filtroRelatorio.getQuadraFinal().trim().isEmpty()) {
            filtroRelatorio.getFiltrosUtilizados().append(separarFiltro).append("Quadra Final: ").append(filtroRelatorio.getQuadraFinal());
            separarFiltro = ", ";
        }
        if (filtroRelatorio.getCadastroInicial() != null && !filtroRelatorio.getCadastroInicial().trim().isEmpty()) {
            filtroRelatorio.getFiltrosUtilizados().append(separarFiltro).append(" Cadastro Inicial: ").append(filtroRelatorio.getCadastroInicial());
            separarFiltro = ", ";
        }
        if (filtroRelatorio.getCadastroFinal() != null && !filtroRelatorio.getCadastroFinal().trim().isEmpty()) {
            filtroRelatorio.getFiltrosUtilizados().append(separarFiltro).append(" Cadastro Final: ").append(filtroRelatorio.getCadastroFinal());
            separarFiltro = ", ";
        }
        if (filtroRelatorio.getProprietario() != null) {
            filtroRelatorio.getFiltrosUtilizados().append(separarFiltro).append("Proprietário: ").append(filtroRelatorio.getProprietario().getNomeAutoComplete());
            separarFiltro = ", ";
        }
        if (filtroRelatorio.getCompromissario() != null) {
            filtroRelatorio.getFiltrosUtilizados().append(separarFiltro).append("Compromissário: ").append(filtroRelatorio.getCompromissario().getNomeAutoComplete());
            separarFiltro = ", ";
        }
        if (filtroRelatorio.getSituacoes() != null && filtroRelatorio.getSituacoes().length > 0) {
            filtroRelatorio.getFiltrosUtilizados().append(" Situações");
            separarFiltro = ": ";
            for (SituacaoParcela sit : filtroRelatorio.getSituacoes()) {
                filtroRelatorio.getFiltrosUtilizados().append(separarFiltro).append(sit.getDescricao());
                separarFiltro = ", ";
            }
        }
        if (filtroRelatorio.getValorInicial() != null) {
            filtroRelatorio.getFiltrosUtilizados().append(separarFiltro).append("Valor Inicial: ").append(filtroRelatorio.getValorInicial());
            separarFiltro = ", ";
        }
        if (filtroRelatorio.getValorFinal() != null) {
            filtroRelatorio.getFiltrosUtilizados().append(separarFiltro).append("Valor Final: ").append(filtroRelatorio.getValorFinal());
            separarFiltro = ", ";
        }
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public void finalizarProcesso() throws ExecutionException, InterruptedException {
        if (!filtroRelatorio.getCancelarProcesso() && listaFuturesAssistente != null) {
            assistenteBarraProgresso.setDescricaoProcesso("Finalizando processo...");
            List<RelatorioRevisaoIPTU> dados = new ArrayList<>();
            for (Future<List<RelatorioRevisaoIPTU>> listFuture : listaFuturesAssistente) {
                dados.addAll(listFuture.get());
            }
            Collections.sort(dados);
            assistenteBarraProgresso.setDescricaoProcesso("Gerando arquivo...");
            futureBytesRelatorio = relatorioRevisaoIPTUFacade.gerarRelatorio(dados, this.getCaminhoImagem(), this.montarNomeDoMunicipio(), this.getCaminhoSubReport(), this.getSistemaFacade().getUsuarioCorrente().getPessoaFisica().getNome(), filtroRelatorio.getFiltrosUtilizados().toString(), filtroRelatorio.getDetalhar(), getSistemaControlador().getUnidadeOrganizacionalAdministrativaCorrente());
            FacesUtil.executaJavaScript("aguarde.show()");
        } else {
            FacesUtil.executaJavaScript("pararAtualizacao()");
            FacesUtil.executaJavaScript("aguarde.hide()");
            FacesUtil.executaJavaScript("dialogProcessando.hide()");
            FacesUtil.atualizarComponente("Formulario");
        }
    }

    public Boolean acompanharProcesso() throws ExecutionException, InterruptedException {
        boolean terminou = true;
        if (listaFuturesAssistente != null && !listaFuturesAssistente.isEmpty()) {
            for (Future<List<RelatorioRevisaoIPTU>> future : listaFuturesAssistente) {
                if (!future.isDone()) {
                    terminou = false;
                    break;
                }
            }
        }
        if (terminou) {
            if (!finalizando) {
                finalizarProcesso();
                finalizando = Boolean.TRUE;
            }
        }
        return terminou;
    }

    public Boolean isRelatorioFinalizado() {
        boolean terminou = true;
        if (futureBytesRelatorio == null || !futureBytesRelatorio.isDone()) {
            terminou = false;
        }
        if (terminou) {
            FacesUtil.executaJavaScript("pararAtualizacao()");
            FacesUtil.executaJavaScript("aguarde.hide()");
        }
        return terminou;
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (filtroRelatorio.getDataLancamentoInicial() != null && filtroRelatorio.getDataLancamentoFinal() != null) {
            if (filtroRelatorio.getDataLancamentoInicial().compareTo(filtroRelatorio.getDataLancamentoFinal()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Data inicial de lançamento deve ser anterior a data final.");
            }
        }
        if (filtroRelatorio.getDataVencimentoInicial() != null && filtroRelatorio.getDataVencimentoFinal() != null) {
            if (filtroRelatorio.getDataVencimentoInicial().compareTo(filtroRelatorio.getDataVencimentoFinal()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Data inicial de vencimento deve ser anterior a data final.");
            }
        }
        if (filtroRelatorio.getDataPagamentoInicial() != null && filtroRelatorio.getDataPagamentoFinal() != null) {
            if (filtroRelatorio.getDataPagamentoInicial().compareTo(filtroRelatorio.getDataPagamentoFinal()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Data inicial de pagamento deve ser anterior a data final.");
            }
        }
        if (filtroRelatorio.getCadastroInicial() == null || filtroRelatorio.getCadastroInicial().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o cadastro inicial.");
        }
        if (filtroRelatorio.getCadastroFinal() == null || filtroRelatorio.getCadastroFinal().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o cadastro final.");
        }
        if (filtroRelatorio.getExercicioInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o exercício inicial.");
        }
        if (filtroRelatorio.getExercicioFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o exercício final.");
        }
        if (filtroRelatorio.getValorInicial() != null && filtroRelatorio.getValorInicial().compareTo(BigDecimal.ZERO) < 0) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o valor inicial, deve ser maior que zero.");
        }
        if (filtroRelatorio.getValorFinal() != null && filtroRelatorio.getValorFinal().compareTo(BigDecimal.ZERO) < 0) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o valor final, deve ser maior que zero e maior que o valor inicial.");
        }
        if (
            filtroRelatorio.getValorInicial() != null && filtroRelatorio.getValorInicial().compareTo(BigDecimal.ZERO) >= 0
                &&
                filtroRelatorio.getValorFinal() != null && filtroRelatorio.getValorFinal().compareTo(BigDecimal.ZERO) >= 0
                && filtroRelatorio.getValorInicial().compareTo(filtroRelatorio.getValorFinal()) > 0
            ) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Se informado o valor inicial deve ser menor que o valor final.");
        }
        filtroRelatorio.getFiltrosUtilizados().append(" Detalhado: ").append(Util.converterBooleanSimOuNao(filtroRelatorio.getDetalhar())).append(" ");
        ve.lancarException();
    }

    public List<ProcessoCalculoIPTU> completaProcessoCalculo(String parte) {
        return processoCalculoIPTUFacade.listaProcessosPorDescricao(parte.trim());
    }

    public FiltroRelatorioImoveisComRevisaoIptu getFiltroRelatorio() {
        return filtroRelatorio;
    }

    public void setFiltroRelatorio(FiltroRelatorioImoveisComRevisaoIptu filtroRelatorio) {
        this.filtroRelatorio = filtroRelatorio;
    }

    public void processaSelecaoBairro() {
        filtroRelatorio.setLogradouro(null);
    }

    public List<Logradouro> completaLogradouro(String parte) {
        if (filtroRelatorio.getBairro() != null) {
            return logradouroFacade.completaLogradourosPorBairro(parte, filtroRelatorio.getBairro());
        } else {
            return logradouroFacade.listaLogradourosAtivos(parte);
        }
    }

    public List<SelectItem> tiposDeImoveis() {
        return Util.getListSelectItem(TipoImovel.values());
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    public void cancelarProcesso() {
        assistenteBarraProgresso.setDescricaoProcesso("Cancelando o processo...");
        filtroRelatorio.setCancelarProcesso(Boolean.TRUE);
    }

    public void baixarRolIPTU() {
        try {
            FacesContext faces = FacesContext.getCurrentInstance();

            HttpServletResponse response = (HttpServletResponse) faces.
                getExternalContext().getResponse();

            response.addHeader("Content-Disposition:", "attachment; filename=Rel-Revisao_iptu.pdf");
            response.setContentType("application/pdf");

            byte[] bytes = futureBytesRelatorio.get().toByteArray();

            response.setContentLength(bytes.length);
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(bytes, 0, bytes.length);
            outputStream.flush();
            outputStream.close();

            faces.responseComplete();
        } catch (Exception e) {
            logger.error("Erro ao baixar o pdf do relatório: " + e.getMessage());
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    public List<SituacaoParcela> getTodasAsSituacoes() {
        return SituacaoParcela.getValues();
    }
}
