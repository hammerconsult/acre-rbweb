package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.ResponsavelServico;
import br.com.webpublico.enums.tributario.regularizacaoconstrucao.TipoConstrucao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.AlvaraConstrucaoFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import br.com.webpublico.webreportdto.dto.tributario.FiltroAlvaraConstrucaoDTO;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioAlvaraConstrucao", pattern = "/relatorio/alvara-contrucao/", viewId = "/faces/tributario/alvaraconstrucao/relatorios/relatorio.xhtml")
})
public class RelatorioAlvaraContrucaoControlador {

    @EJB
    private AlvaraConstrucaoFacade alvaraConstrucaoFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private AbstractReport abstractReport;
    private Logger log = LoggerFactory.getLogger(RelatorioAlvaraContrucaoControlador.class);

    private Integer codigoProcesso;
    private Integer exercicioProcesso;
    private Pessoa requerente;
    private CadastroImobiliario cadastroImobiliario;
    private Bairro bairro;
    private Logradouro logradouro;
    private TipoConstrucao tipoConstrucao;
    private BigDecimal areaInicial;
    private BigDecimal areaFinal;
    private Long codigoAlvaraInicial;
    private Long codigoAlvaraFinal;
    private Date dataLancamentoCalculoInicial;
    private Date dataLancamentoCalculoFinal;
    private Date dataExpedicaoInicial;
    private Date dataExpedicaoFinal;
    private Date dataVencimentoInicial;
    private Date dataVencimentoFinal;
    private Date dataVencimentoDebitoInicial;
    private Date dataVencimentoDebitoFinal;
    private ResponsavelServico responsavelServico;
    private String CEI;

    public RelatorioAlvaraContrucaoControlador() {
        abstractReport = AbstractReport.getAbstractReport();
        abstractReport.setGeraNoDialog(true);
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarRegras();

            RelatorioDTO relatorio = new RelatorioDTO();

            UsuarioSistema usuarioSistema = alvaraConstrucaoFacade.recuperarUsuarioCorrente();
            relatorio.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            relatorio.adicionarParametro("USUARIO", usuarioSistema.getNome());
            relatorio.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE FINANÇAS");
            relatorio.adicionarParametro("MODULO", "TRIBUTÁRIO");
            relatorio.setNomeRelatorio("Relatório de Alvará de Construção");
            relatorio.adicionarParametro("NOME_RELATORIO", relatorio.getNomeRelatorio());

            adicionarFiltros(relatorio);
            relatorio.setApi("tributario/alvara-construcao/");

            ReportService.getInstance().gerarRelatorio(usuarioSistema, relatorio);
            FacesUtil.addMensagemRelatorioSegundoPlano();

        } catch (WebReportRelatorioExistenteException e) {
            log.error("Erro ao gerar relatorio de alvará de construcao. (WebReportRelatorioExistenteException) ", e);
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            log.error("Erro ao gerar relatorio de alvará de construcao. (Exception) ", e);
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    private void adicionarFiltros(RelatorioDTO relatorio) {
        FiltroAlvaraConstrucaoDTO filtro = new FiltroAlvaraConstrucaoDTO();

        filtro.setIdRequerente(requerente != null ? requerente.getId() : null);
        filtro.setIdCadastroImobiliario(cadastroImobiliario != null ? cadastroImobiliario.getId() : null);
        filtro.setIdBairro(bairro != null ? bairro.getId() : null);
        filtro.setIdLogradouro(logradouro != null ? logradouro.getId() : null);
        filtro.setIdResponsavelServico(responsavelServico != null ? responsavelServico.getId() : null);
        filtro.setCodigoAlvaraInicial(codigoAlvaraInicial);
        filtro.setCodigoAlvaraFinal(codigoAlvaraFinal);
        filtro.setAreaInicial(areaInicial);
        filtro.setAreaFinal(areaFinal);
        filtro.setCodigoProcesso(codigoProcesso);
        filtro.setExercicioProcesso(exercicioProcesso);
        filtro.setCei(CEI);
        filtro.setTipoConstrucao(tipoConstrucao != null ? tipoConstrucao.getToDto() : null);
        filtro.setDataLancamentoCalculoInicial(dataLancamentoCalculoInicial);
        filtro.setDataLancamentoCalculoFinal(dataLancamentoCalculoFinal);
        filtro.setDataExpedicaoInicial(dataExpedicaoInicial);
        filtro.setDataExpedicaoFinal(dataExpedicaoFinal);
        filtro.setDataVencimentoInicial(dataVencimentoInicial);
        filtro.setDataVencimentoFinal(dataVencimentoFinal);
        filtro.setDataVencimentoDebitoInicial(dataVencimentoDebitoInicial);
        filtro.setDataVencimentoDebitoFinal(dataVencimentoDebitoFinal);

        relatorio.adicionarParametro("filtroAlvara", filtro);
    }

    private void validarRegras() {
        ValidacaoException ve = new ValidacaoException();
        if (!Util.validaInicialFinal(areaInicial, areaFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Á área inicial e final não é valida, informe os dois campos sendo a área inicial ou igual menor que a final");
        }
        if (!Util.validaInicialFinal(codigoAlvaraInicial, codigoAlvaraFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O código do alvará não é valido, informe os dois campos sendo o código inicial menor ou igual que o final");
        }
        if (!Util.validaInicialFinal(dataLancamentoCalculoInicial, dataLancamentoCalculoFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de lançamento do calculo não é valida, informe os dois campos sendo a data inicial menor ou igual que a final");
        }
        if (!Util.validaInicialFinal(dataExpedicaoInicial, dataExpedicaoFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de expedição do alvara não é valida, informe os dois campos sendo a data inicial menor ou igual que a final");
        }
        if (!Util.validaInicialFinal(dataVencimentoInicial, dataVencimentoFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de vencimento do alvara não é valida, informe os dois campos sendo a data inicial menor ou igual que a final");
        }
        if (!Util.validaInicialFinal(dataVencimentoDebitoInicial, dataVencimentoDebitoFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de vencimento do débito não é valida, informe os dois campos sendo a data inicial menor ou igual que a final");
        }
        ve.lancarException();
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public List<SelectItem> getTiposConstrucoes() {
        return Util.getListSelectItemSemCampoVazio(TipoConstrucao.values(), true);
    }

    public AlvaraConstrucaoFacade getAlvaraConstrucaoFacade() {
        return alvaraConstrucaoFacade;
    }

    public void setAlvaraConstrucaoFacade(AlvaraConstrucaoFacade alvaraConstrucaoFacade) {
        this.alvaraConstrucaoFacade = alvaraConstrucaoFacade;
    }

    public AbstractReport getAbstractReport() {
        return abstractReport;
    }

    public void setAbstractReport(AbstractReport abstractReport) {
        this.abstractReport = abstractReport;
    }

    public Logger getLog() {
        return log;
    }

    public void setLog(Logger log) {
        this.log = log;
    }

    public Integer getCodigoProcesso() {
        return codigoProcesso;
    }

    public void setCodigoProcesso(Integer codigoProcesso) {
        this.codigoProcesso = codigoProcesso;
    }

    public Integer getExercicioProcesso() {
        return exercicioProcesso;
    }

    public void setExercicioProcesso(Integer exercicioProcesso) {
        this.exercicioProcesso = exercicioProcesso;
    }

    public CadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
    }

    public Bairro getBairro() {
        return bairro;
    }

    public void setBairro(Bairro bairro) {
        this.bairro = bairro;
    }

    public Logradouro getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(Logradouro logradouro) {
        this.logradouro = logradouro;
    }

    public TipoConstrucao getTipoConstrucao() {
        return tipoConstrucao;
    }

    public void setTipoConstrucao(TipoConstrucao tipoConstrucao) {
        this.tipoConstrucao = tipoConstrucao;
    }

    public BigDecimal getAreaInicial() {
        return areaInicial;
    }

    public void setAreaInicial(BigDecimal areaInicial) {
        this.areaInicial = areaInicial;
    }

    public BigDecimal getAreaFinal() {
        return areaFinal;
    }

    public void setAreaFinal(BigDecimal areaFinal) {
        this.areaFinal = areaFinal;
    }

    public Long getCodigoAlvaraInicial() {
        return codigoAlvaraInicial;
    }

    public void setCodigoAlvaraInicial(Long codigoAlvaraInicial) {
        this.codigoAlvaraInicial = codigoAlvaraInicial;
    }

    public Long getCodigoAlvaraFinal() {
        return codigoAlvaraFinal;
    }

    public void setCodigoAlvaraFinal(Long codigoAlvaraFinal) {
        this.codigoAlvaraFinal = codigoAlvaraFinal;
    }

    public Date getDataLancamentoCalculoInicial() {
        return dataLancamentoCalculoInicial;
    }

    public void setDataLancamentoCalculoInicial(Date dataLancamentoCalculoInicial) {
        this.dataLancamentoCalculoInicial = dataLancamentoCalculoInicial;
    }

    public Date getDataLancamentoCalculoFinal() {
        return dataLancamentoCalculoFinal;
    }

    public void setDataLancamentoCalculoFinal(Date dataLancamentoCalculoFinal) {
        this.dataLancamentoCalculoFinal = dataLancamentoCalculoFinal;
    }

    public Date getDataExpedicaoInicial() {
        return dataExpedicaoInicial;
    }

    public void setDataExpedicaoInicial(Date dataExpedicaoInicial) {
        this.dataExpedicaoInicial = dataExpedicaoInicial;
    }

    public Date getDataExpedicaoFinal() {
        return dataExpedicaoFinal;
    }

    public void setDataExpedicaoFinal(Date dataExpedicaoFinal) {
        this.dataExpedicaoFinal = dataExpedicaoFinal;
    }

    public Date getDataVencimentoInicial() {
        return dataVencimentoInicial;
    }

    public void setDataVencimentoInicial(Date dataVencimentoInicial) {
        this.dataVencimentoInicial = dataVencimentoInicial;
    }

    public Date getDataVencimentoFinal() {
        return dataVencimentoFinal;
    }

    public void setDataVencimentoFinal(Date dataVencimentoFinal) {
        this.dataVencimentoFinal = dataVencimentoFinal;
    }

    public ResponsavelServico getResponsavelServico() {
        return responsavelServico;
    }

    public void setResponsavelServico(ResponsavelServico responsavelServico) {
        this.responsavelServico = responsavelServico;
    }

    public String getCEI() {
        return CEI;
    }

    public void setCEI(String CEI) {
        this.CEI = CEI;
    }

    public Date getDataVencimentoDebitoInicial() {
        return dataVencimentoDebitoInicial;
    }

    public void setDataVencimentoDebitoInicial(Date dataVencimentoDebitoInicial) {
        this.dataVencimentoDebitoInicial = dataVencimentoDebitoInicial;
    }

    public Date getDataVencimentoDebitoFinal() {
        return dataVencimentoDebitoFinal;
    }

    public void setDataVencimentoDebitoFinal(Date dataVencimentoDebitoFinal) {
        this.dataVencimentoDebitoFinal = dataVencimentoDebitoFinal;
    }

    public Pessoa getRequerente() {
        return requerente;
    }

    public void setRequerente(Pessoa requerente) {
        this.requerente = requerente;
    }

    public void limparCampos(){
        FacesUtil.redirecionamentoInterno("/relatorio/alvara-contrucao/");
    }
}
