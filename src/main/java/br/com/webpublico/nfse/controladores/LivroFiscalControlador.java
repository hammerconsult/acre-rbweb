package br.com.webpublico.nfse.controladores;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.nfse.ParecerFiscalNfse;
import br.com.webpublico.nfse.domain.LivroFiscal;
import br.com.webpublico.nfse.domain.dtos.LivroFiscalCompetenciaNfseDTO;
import br.com.webpublico.nfse.enums.TipoMovimentoMensal;
import br.com.webpublico.nfse.facades.DeclaracaoMensalServicoFacade;
import br.com.webpublico.nfse.facades.LivroFiscalFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import org.joda.time.LocalDate;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@ManagedBean(name = "livroFiscalControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "LivroFiscalListar", pattern = "/nfse/livro-fiscal/listar/",
        viewId = "/faces/tributario/nfse/livrofiscal/lista.xhtml")
})
public class LivroFiscalControlador extends PrettyControlador<LivroFiscal> implements CRUD {

    @EJB
    private LivroFiscalFacade livroFiscalFacade;
    @EJB
    private DeclaracaoMensalServicoFacade declaracaoMensalServicoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private Integer exercicioInicial, exercicioFinal;
    private Mes mesInicial, mesFinal;
    private CadastroEconomico cadastroEconomico;
    private Future<List<LivroFiscalCompetenciaNfseDTO>> futurePrestados;
    private List<LivroFiscalCompetenciaNfseDTO> prestados;
    private Future<List<LivroFiscalCompetenciaNfseDTO>> futureTomados;
    private List<LivroFiscalCompetenciaNfseDTO> tomados;
    private List<ParecerFiscalNfse> pareceres;
    private ParecerFiscalNfse parecer;
    private Boolean consultaRealizada = Boolean.FALSE;

    public LivroFiscalControlador() {
        super(LivroFiscal.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return livroFiscalFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/nfse/livro-fiscal/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public Boolean getConsultaRealizada() {
        return consultaRealizada;
    }

    @URLAction(mappingId = "LivroFiscalListar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        LocalDate agora = LocalDate.now();
        exercicioInicial = agora.getYear();
        exercicioFinal = agora.getYear();
        mesInicial = Mes.getMesToInt(agora.getMonthOfYear() - 1);
        mesFinal = Mes.getMesToInt(agora.getMonthOfYear());
    }

    public void novoParecer(Integer mes, Integer exercicio) {
        parecer = new ParecerFiscalNfse();
        parecer.setAno(exercicio);
        parecer.setMes(Mes.getMesToInt(mes));
        parecer.setTipo(ParecerFiscalNfse.TipoParecer.LIVRO_FISCAL);
        parecer.setFiscal(sistemaFacade.getUsuarioCorrente());
        parecer.setDataParecer(new Date());
        parecer.setCadastroEconomico(cadastroEconomico);
        buscarPareceresPorCompetencia(parecer.getMes(), parecer.getAno());
    }

    public void salvarParecer() {
        livroFiscalFacade.salvar(parecer);
        buscarPareceresPorCompetencia(parecer.getMes(), parecer.getAno());
        novoParecer(parecer.getMes().getNumeroMes(), parecer.getAno());
    }

    public void buscarPareceresPorCompetencia(Mes mes, Integer ano) {
        pareceres = livroFiscalFacade.buscarPareceresPorCompetencia(mes, ano, cadastroEconomico.getId());
    }

    public List<SelectItem> getMeses() {
        return Util.getListSelectItem(Mes.values(), false);
    }

    public Integer getExercicioInicial() {
        return exercicioInicial;
    }

    public void setExercicioInicial(Integer exercicioInicial) {
        this.exercicioInicial = exercicioInicial;
    }

    public Integer getExercicioFinal() {
        return exercicioFinal;
    }

    public void setExercicioFinal(Integer exercicioFinal) {
        this.exercicioFinal = exercicioFinal;
    }

    public Mes getMesInicial() {
        return mesInicial;
    }

    public void setMesInicial(Mes mesInicial) {
        this.mesInicial = mesInicial;
    }

    public Mes getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(Mes mesFinal) {
        this.mesFinal = mesFinal;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public List<ParecerFiscalNfse> getPareceres() {
        return pareceres;
    }

    public ParecerFiscalNfse getParecer() {
        return parecer;
    }

    public void setParecer(ParecerFiscalNfse parecer) {
        this.parecer = parecer;
    }

    public List<LivroFiscalCompetenciaNfseDTO> getPrestados() {
        return prestados;
    }

    public List<LivroFiscalCompetenciaNfseDTO> getTomados() {
        return tomados;
    }

    public void validar() {
        ValidacaoException ve = new ValidacaoException();
        if (mesInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a competência inicial");
        }
        if (mesFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a competência final");
        }
        if (exercicioInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o exercício inicial");
        }
        if (exercicioFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o exercício final");
        }
        if (cadastroEconomico == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o prestador de serviços");
        }

        ve.lancarException();
    }

    public void iniciarConsulta() {
        limparDados();
    }

    private void limparDados() {
        consultaRealizada = Boolean.FALSE;
        prestados = null;
        tomados = null;
    }

    public void consultar() {
        try {
            validar();
            if (cadastroEconomico.getEnquadramentoVigente() == null ||
                !cadastroEconomico.getEnquadramentoVigente().isInstituicaoFinanceira()) {
                futurePrestados = declaracaoMensalServicoFacade.buscarCompetenciasLivroFiscalAsync(cadastroEconomico.getId(),
                    exercicioInicial, exercicioFinal,
                    mesInicial.getNumeroMes(), mesFinal.getNumeroMes(), TipoMovimentoMensal.NORMAL);
            } else {
                futurePrestados = declaracaoMensalServicoFacade.buscarCompetenciasLivroFiscalAsync(cadastroEconomico.getId(),
                    exercicioInicial, exercicioFinal,
                    mesInicial.getNumeroMes(), mesFinal.getNumeroMes(), TipoMovimentoMensal.INSTITUICAO_FINANCEIRA);
            }
            futureTomados = declaracaoMensalServicoFacade.buscarCompetenciasLivroFiscalAsync(cadastroEconomico.getId(),
                exercicioInicial, exercicioFinal,
                mesInicial.getNumeroMes(), mesFinal.getNumeroMes(), TipoMovimentoMensal.RETENCAO);
            FacesUtil.executaJavaScript("acompanharConsulta()");
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e);
            FacesUtil.executaJavaScript("aguarde.hide()");
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
            FacesUtil.executaJavaScript("aguarde.hide()");
        }
    }

    public void acompanharConsulta() {
        if (futurePrestados.isDone() && futureTomados.isDone()) {
            FacesUtil.executaJavaScript("finalizarConsulta()");
        }
    }

    public void finalizarConsulta() {
        try {
            prestados = futurePrestados.get();
            tomados = futureTomados.get();
            consultaRealizada = Boolean.TRUE;
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Erro ao finalizar consulta do livro fiscal", e);
        }
    }

    public void gerarLivroFiscal(LivroFiscalCompetenciaNfseDTO livroFiscalDTO) {
        try {
            RelatorioDTO relatorioDTO = criarRelatorioDTO(livroFiscalDTO);
            ReportService.getInstance().gerarRelatorio(declaracaoMensalServicoFacade.getSistemaFacade().getUsuarioCorrente(), relatorioDTO);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (
            WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException op) {
            FacesUtil.printAllFacesMessages(op);
        } catch (Exception ex) {
            logger.error("Erro ao gerar o relatorio de livro fiscal. Erro {}", ex);
            FacesUtil.addErrorPadrao(ex);
        }
    }

    private RelatorioDTO criarRelatorioDTO(LivroFiscalCompetenciaNfseDTO livroFiscalDTO) {
        RelatorioDTO relatorioDTO = new RelatorioDTO();
        relatorioDTO.adicionarParametro("MUNICIPIO", "PREFEITURA DE RIO BRANCO");
        relatorioDTO.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE FINANÇAS");
        relatorioDTO.adicionarParametro("CONTRIBUINTE", cadastroEconomico != null ? cadastroEconomico.toString() : "");
        relatorioDTO.adicionarParametro("PRESTADOR_ID", livroFiscalDTO.getPrestadorId());
        relatorioDTO.adicionarParametro("EXERCICIO", livroFiscalDTO.getExercicio());
        relatorioDTO.adicionarParametro("MES", livroFiscalDTO.getMes());
        relatorioDTO.adicionarParametro("TIPO_MOVIMENTO", livroFiscalDTO.getTipoMovimento().name());
        relatorioDTO.adicionarParametro("QUANTIDADE", livroFiscalDTO.getQuantidade());
        relatorioDTO.adicionarParametro("ISSQN_PROPRIO", livroFiscalDTO.getIssqnProprio());
        relatorioDTO.adicionarParametro("ISSQN_RETIDO", livroFiscalDTO.getIssqnRetido());
        relatorioDTO.adicionarParametro("ISSQN_PAGO", livroFiscalDTO.getIssqnPago());
        relatorioDTO.adicionarParametro("SALDO_ISSQN", livroFiscalDTO.getSaldoIssqn());
        relatorioDTO.adicionarParametro("VALOR_SERVICO", livroFiscalDTO.getValorServico());
        relatorioDTO.adicionarParametro("DETALHADO", livroFiscalDTO.getDetalhado());
        relatorioDTO.setNomeRelatorio("livro-fiscal.pdf");
        relatorioDTO.setApi("tributario/nfse/livro-fiscal/");
        return relatorioDTO;
    }

    public void handleCadastroEconomico() {
        limparDados();
    }
}
