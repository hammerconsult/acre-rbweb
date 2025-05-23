/*
 * Codigo gerado automaticamente em Mon Oct 24 16:57:46 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.ItemEnvioDadosRBPonto;
import br.com.webpublico.entidades.rh.ParametroFerias;
import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoRH;
import br.com.webpublico.enums.StatusPeriodoAquisitivo;
import br.com.webpublico.enums.TipoFerias;
import br.com.webpublico.enums.TipoPeriodoAquisitivo;
import br.com.webpublico.enums.rh.TipoAutorizacaoRH;
import br.com.webpublico.enums.rh.TipoInformacaoEnvioRBPonto;
import br.com.webpublico.enums.rh.administracaopagamento.TipoTercoFeriasAutomatico;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.administracaodepagamento.LancamentoTercoFeriasAutFacade;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoRHFacade;
import br.com.webpublico.negocios.rh.integracaoponto.EnvioDadosRBPontoFacade;
import br.com.webpublico.util.*;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaConcessaoFerias", pattern = "/concessaoferias/novo/", viewId = "/faces/rh/administracaodepagamento/concessaoferiaslicenca/edita.xhtml"),
    @URLMapping(id = "editarConcessaoFerias", pattern = "/concessaoferias/editar/#{concessaoFeriasLicencaControlador.id}/", viewId = "/faces/rh/administracaodepagamento/concessaoferiaslicenca/edita.xhtml"),
    @URLMapping(id = "listarConcessaoFerias", pattern = "/concessaoferias/listar/", viewId = "/faces/rh/administracaodepagamento/concessaoferiaslicenca/lista.xhtml"),
    @URLMapping(id = "verConcessaoFerias", pattern = "/concessaoferias/ver/#{concessaoFeriasLicencaControlador.id}/", viewId = "/faces/rh/administracaodepagamento/concessaoferiaslicenca/visualizar.xhtml")
})
public class ConcessaoFeriasLicencaControlador extends PrettyControlador<ConcessaoFeriasLicenca> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(ConcessaoFeriasLicencaControlador.class);

    @EJB
    private ConcessaoFeriasLicencaFacade concessaoFeriasLicencaFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private PeriodoAquisitivoFLFacade periodoAquisitivoFLFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private ConfiguracaoFaltasInjustificadasFacade configuracaoFaltasInjustificadasFacade;
    @EJB
    private SugestaoFeriasFacade sugestaoFeriasFacade;
    @EJB
    private FaltasFacade faltasFacade;
    @EJB
    private BasePeriodoAquisitivoFacade basePeriodoAquisitivoFacade;
    @EJB
    private AfastamentoFacade afastamentoFacade;
    @EJB
    private EnvioDadosRBPontoFacade envioDadosRBPontoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private LancamentoTercoFeriasAutFacade lancamentoTercoFeriasAutFacade;
    @EJB
    private ConfiguracaoRHFacade configuracaoRHFacade;
    @EJB
    private ParametroFeriasFacade parametroFeriasFacade;
    private ConverterGenerico converterPeriodoAquisitivoFL;
    private ConverterAutoComplete converterContratoFP;
    private Date inicioAbonoPecuniario;
    private Date fimAbonoPecuniario;
    private Integer totalFaltasInjustificadas;
    private Integer diasDeDireito;
    private Arquivo arquivo;
    private FileUploadEvent fileUploadEvent;
    private UploadedFile file;

    @ManagedProperty(name = "sugestaoFeriasControlador", value = "#{sugestaoFeriasControlador}")
    private SugestaoFeriasControlador sugestaoFeriasControlador;

    private List<ItemEnvioDadosRBPonto> itensEnvioDadosRBPonto;
    private boolean habilitarCampoMesAno;
    private boolean permiteMesAnoAnteriorFinalPA;

    public ConcessaoFeriasLicencaControlador() {
        super(ConcessaoFeriasLicenca.class);
    }

    public ConcessaoFeriasLicencaFacade getFacade() {
        return concessaoFeriasLicencaFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return concessaoFeriasLicencaFacade;
    }

    public List<ContratoFP> getListaContratos() {
        return contratoFPFacade.lista();
    }

    public Date getFimAbonoPecuniario() {
        return fimAbonoPecuniario;
    }

    public void setFimAbonoPecuniario(Date fimAbonoPecuniario) {
        this.fimAbonoPecuniario = fimAbonoPecuniario;
    }

    public List getLista() {

        return concessaoFeriasLicencaFacade.listaTipos(TipoPeriodoAquisitivo.FERIAS);
    }

    public Date getInicioAbonoPecuniario() {
        return inicioAbonoPecuniario;
    }

    public void setInicioAbonoPecuniario(Date inicioAbonoPecuniario) {
        this.inicioAbonoPecuniario = inicioAbonoPecuniario;
    }

    public void setDataInicial(SelectEvent event) {
        selecionado.setDataInicialAbonoPecuniario((Date) event.getObject());
    }

    public void setDataFinal(SelectEvent event) {
        selecionado.setDataFinalAbonoPecuniario((Date) event.getObject());
    }

    public void setDias(Integer i) {
        selecionado.setDias(i);
    }

    public Integer getDiasDeAbono() {
        ConcessaoFeriasLicenca cfl = selecionado;
        if (selecionado.getDataInicialAbonoPecuniario() != null) {
            if (selecionado.getDataFinalAbonoPecuniario() != null) {
                if (selecionado.getDataFinalAbonoPecuniario().after(selecionado.getDataInicialAbonoPecuniario())) {
                    if (selecionado.getDataInicialAbonoPecuniario().after(selecionado.getDataFinal())) {
                        logger.debug("Validou");
                        Long dataInicial = selecionado.getDataInicialAbonoPecuniario().getTime() / (24 * 60 * 60 * 1000);
                        Long dataFim = selecionado.getDataFinalAbonoPecuniario().getTime() / (24 * 60 * 60 * 1000);
                        Long diferenca = (dataFim - dataInicial) + 1;
                        selecionado.setDiasAbonoPecuniario(diferenca.intValue());
                        return diferenca.intValue();
                    } else {
                        logger.debug("Nao validou....");
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção!", "a Vigência do Abono Pecúniario está entre a Vigência da Concessão."));
                    }
                } else if (cfl.getDataInicialAbonoPecuniario().equals(cfl.getDataFinalAbonoPecuniario())) {
                    selecionado.setDiasAbonoPecuniario(1);
                    return 1;
                } else {
                    logger.debug("Nao validou....");
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção!", "a data final é anterior a data inicial"));
                }
            } else {
                logger.debug("Nao validou...");
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção!", "A vigência final do abono está nula"));
            }
        } else {
            logger.debug("Nao validou..");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção!", "A vigência inicial do abono pecuniário está nula."));
        }
        selecionado.setDiasAbonoPecuniario(0);
        return 0;
    }

    public void setDiasDeAbono(Integer i) {
        selecionado.setDiasAbonoPecuniario(i);
    }

    public Integer getMes() {
        Calendar calendar = Calendar.getInstance();
        if (validaVigencia()) {
            calendar.setTime(selecionado.getDataFinal());
            Integer i = calendar.get(Calendar.MONTH);
            logger.debug("Mes: {0}", i);
            logger.debug("Mês: {0}", calendar.get(Calendar.MONTH));
            selecionado.setMes(i == 0 ? 12 : i);
            return i;
        }
        return null;
    }

    public void setMes(Integer i) {
        selecionado.setMes(i);
    }

    public Integer getAno() {
        Calendar calendar = Calendar.getInstance();
        if (validaVigencia()) {
            calendar.setTime(selecionado.getDataFinal());
            Integer i = calendar.get(Calendar.YEAR);
            logger.debug("Ano: {0}", i);

            selecionado.setAno(i);
            return i;
        }
        return null;
    }

    public void setAno(Integer i) {
        selecionado.setAno(i);
    }

    private void validarDataComunicacao() {
        ConcessaoFeriasLicenca c = selecionado;

        if (selecionado.getDataComunicacao() != null && selecionado.getDataInicial() != null) {

            Calendar dtComunicacao = Calendar.getInstance();
            dtComunicacao.setTime(selecionado.getDataComunicacao());
            Calendar dtMenorData = Calendar.getInstance();
            if (c.getDataInicialAbonoPecuniario() != null) {
                if (c.getDataInicial().equals(c.getDataInicialAbonoPecuniario())) {
                    dtMenorData.setTime(c.getDataInicial());
                }
                if (c.getDataInicial().after(c.getDataInicialAbonoPecuniario())) {
                    dtMenorData.setTime(c.getDataInicialAbonoPecuniario());
                } else {
                    dtMenorData.setTime(c.getDataInicial());
                }
            } else {
                dtMenorData.setTime(c.getDataInicial());
            }
            Long dataInicial = dtComunicacao.getTime().getTime() / (24 * 60 * 60 * 1000);
            Long dataFim = dtMenorData.getTime().getTime() / (24 * 60 * 60 * 1000);
            Long diferenca = (dataFim - dataInicial) + 1;

            if (diferenca < 30) {
                logger.debug("Nao validou..");
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção!", "A data de Comunicação deve ser menor que 30 dias da primeira data inicial"));
            }
        }
    }

    public boolean validaVigencia() {
        boolean valida = false;

        if (selecionado.getDataInicial() != null && selecionado.getDataFinal() != null) {
            if (selecionado.getDataInicial().equals(selecionado.getDataFinal())) {
                valida = true;
            } else if (selecionado.getDataInicial().before(selecionado.getDataFinal())) {
                valida = true;
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção!", "A data final é anterior a data inicial"));
                valida = false;
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção!", "A vigência está nula"));
            valida = false;
        }
        return valida;
    }

    //    public void diasAbonoPecuniario() {
//        ConcessaoFeriasLicenca cfl = ((ConcessaoFeriasLicenca) selecionado);
//        if (((ConcessaoFeriasLicenca) selecionado).getDataInicialAbonoPecuniario() != null) {
//            if (cfl.getDataFinalAbonoPecuniario() != null) {
//                if (cfl.getDataFinalAbonoPecuniario().after(cfl.getDataInicialAbonoPecuniario())) {
//                    //System.out.println("VALIDOU");
//                    Long dataInicial = cfl.getDataInicialAbonoPecuniario().getTime();
//                    Long dataFim = cfl.getDataFinalAbonoPecuniario().getTime();
//                    Long diferenca = ((dataFim - dataInicial) / (24 * 60 * 60 * 1000)) + 1;
//                    cfl.setDiasAbonoPecuniario(diferenca.intValue());
//                } else {
//                    //System.out.println("Nao validou....");
//                    FacesContext.getCurrentInstance().addMessage("msgs", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "a data final é menor que a data inicial"));
//
//                }
//            } else {
//                //System.out.println("Nao validou...");
//                FacesContext.getCurrentInstance().addMessage("msgs", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "A vigência final do abono está nula"));
//            }
//        } else {
//            //System.out.println("Nao validou..");
//            FacesContext.getCurrentInstance().addMessage("msgs", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "A vigência inicial do abono pecuniário está nula."));
//        }
//    }
    public Integer getSaldoDeDireito() {
        Integer retorno = 0;
        if (selecionado.getPeriodoAquisitivoFL() != null) {
            retorno = selecionado.getPeriodoAquisitivoFL().getSaldoDeDireito() == null ? 0 : selecionado.getPeriodoAquisitivoFL().getSaldoDeDireito();
        }
        diasDeDireito = retorno;
        return diasDeDireito;
    }

    public List<SelectItem> getPeriodoAquisitivoFL() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));

        SimpleDateFormat fd = new SimpleDateFormat("dd/MM/yyyy");
        if (selecionado.getContratoFP() == null) {
            return toReturn;
        }
        List<PeriodoAquisitivoFL> periodos = periodoAquisitivoFLFacade.recuperarPeriodosAquisitivosComFeriasAprovadas(selecionado.getContratoFP());
        if (isOperacaoEditar()) {
            toReturn = new ArrayList<>();
            periodos.add(selecionado.getPeriodoAquisitivoFL());
        }
        for (PeriodoAquisitivoFL p : periodos) {
            String descricao = "P.A. " + fd.format(p.getInicioVigencia()) + " - " + fd.format(p.getFinalVigencia()) + " - Saldo de Direito (" + p.getSaldoDeDireitoToString() + ")";
            descricao += p.getStatus().equals(StatusPeriodoAquisitivo.CONCEDIDO) ? " - (" + StatusPeriodoAquisitivo.CONCEDIDO.getDescricao() + ")" : "";
            toReturn.add(new SelectItem(p, descricao));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterPeriodoAquisitivoFL() {
        if (converterPeriodoAquisitivoFL == null) {
            converterPeriodoAquisitivoFL = new ConverterGenerico(PeriodoAquisitivoFL.class, periodoAquisitivoFLFacade);
        }
        return converterPeriodoAquisitivoFL;
    }

    public Converter getConverterContratoFP() {
        if (converterContratoFP == null) {
            converterContratoFP = new ConverterAutoComplete(ContratoFP.class, contratoFPFacade);
        }
        return converterContratoFP;
    }


    public List<ContratoFP> contratoFPs(String parte) {
        return contratoFPFacade.recuperaContrato(parte.trim());
    }

    @URLAction(mappingId = "novaConcessaoFerias", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataCadastro(new Date());
        totalFaltasInjustificadas = 0;
        fileUploadEvent = null;
        arquivo = new Arquivo();
        habilitarCampoMesAno = true;
        permiteMesAnoAnteriorFinalPA = vinculoFPFacade.hasAutorizacaoEspecialRH(sistemaFacade.getUsuarioCorrente(), TipoAutorizacaoRH.PERMITIR_REALIZAR_CONCESSAO_FERIAS_MES_ANO_PAGAMENTO_INFERIOR_FINAL_PA);
    }

    private void validarCampos() {
        Util.validarCampos(selecionado);
        validarDiasAbono();
        validarDiasSolicitados();
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data de início da concessão deve ser informado.");
        }
        if (selecionado.getFimVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data de término da concessão deve ser informado.");
        }

        if (isPermiteInformarMesAnoPagamento()) {
            if (selecionado.getMes() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Mês de Pagamento deve ser informado.");
            }
            if (selecionado.getAno() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Ano de Pagamento deve ser informado.");
            }
            if (selecionado.getAno() != null && selecionado.getMes() != null) {
                if (selecionado.getMes() > 12 || selecionado.getMes() <= 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O mês informado é inválido!");
                }
                if (selecionado.getAno() == 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O ano informado é inválido!");
                }
                if (!permiteMesAnoAnteriorFinalPA) {
                    java.time.LocalDate dataFinalPeriodo = DataUtil.dateToLocalDate(selecionado.getPeriodoAquisitivoFL().getFinalVigencia());
                    if ((selecionado.getMes() < dataFinalPeriodo.getMonthValue() && selecionado.getAno() == dataFinalPeriodo.getYear()) || selecionado.getAno() < dataFinalPeriodo.getYear()) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("O Mês/Ano de Pagamento é inferior ao Mês/Ano do final de vigência do Período Aquisitivo.");
                    }
                }
            }
        }

        if (selecionado.getPeriodoAquisitivoFL() != null && selecionado.getPeriodoAquisitivoFL().getContratoFP().getFinalVigencia() != null) {
            Date finalContrato = selecionado.getPeriodoAquisitivoFL().getContratoFP().getFinalVigencia();
            if ((selecionado.getInicioVigencia() != null && selecionado.getInicioVigencia().after(finalContrato)) || (selecionado.getFimVigencia() != null && selecionado.getFimVigencia().after(finalContrato))) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data de gozo inicial ou final não pode ser maior que o final de vigência do contrato.");
            }
        }
        if (selecionado.getDataInicial() != null && selecionado.getDataFinal() != null) {
            List<ConcessaoFeriasLicenca> listaDeFerias = concessaoFeriasLicencaFacade.listaFiltrandoServidorPorCompetencia(selecionado.getContratoFP(), selecionado.getDataInicial(), selecionado.getDataFinal());
            if (listaDeFerias != null && !listaDeFerias.isEmpty()) {
                for (ConcessaoFeriasLicenca ferias : listaDeFerias) {
                    if (ferias.getId().equals(selecionado.getId())) {
                        continue;
                    }
                    if (selecionado.getDataInicial().compareTo(ferias.getDataInicial()) >= 0
                        && selecionado.getDataInicial().compareTo(ferias.getDataFinal()) <= 0
                        || selecionado.getDataFinal().compareTo(ferias.getDataInicial()) >= 0
                        && selecionado.getDataFinal().compareTo(ferias.getDataFinal()) <= 0) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("O servidor(a) " + selecionado.getContratoFP() + " está gozando de "
                            + ferias.getPeriodoAquisitivoFL().getBaseCargo().getBasePeriodoAquisitivo().getTipoPeriodoAquisitivo().getDescricao()
                            + " no período de " + DataUtil.getDataFormatada(ferias.getDataInicial()) + " a "
                            + DataUtil.getDataFormatada(ferias.getDataFinal())
                            + ", o sistema não permite conceder Férias para servidor no mesmo período de "
                            + ferias.getPeriodoAquisitivoFL().getBaseCargo().getBasePeriodoAquisitivo().getTipoPeriodoAquisitivo().getDescricao() + ".");
                    }
                }
            }
            List<Afastamento> listaDeafastamentos = afastamentoFacade.listaFiltrandoServidorPorCompetencia(selecionado.getContratoFP(), selecionado.getDataInicial(), selecionado.getDataFinal());
            if (listaDeafastamentos != null && !listaDeafastamentos.isEmpty()) {
                for (Afastamento afastamentos : listaDeafastamentos) {
                    if (!afastamentos.getTipoAfastamento().getProcessaNormal() &&
                        (selecionado.getDataInicial().compareTo(afastamentos.getInicio()) >= 0
                            && selecionado.getDataInicial().compareTo(afastamentos.getTermino()) <= 0
                            || selecionado.getDataFinal().compareTo(afastamentos.getInicio()) >= 0
                            && selecionado.getDataFinal().compareTo(afastamentos.getTermino()) <= 0
                            || afastamentos.getInicio().compareTo(selecionado.getDataInicial()) >= 0
                            && afastamentos.getInicio().compareTo(selecionado.getDataFinal()) <= 0
                            || afastamentos.getTermino().compareTo(selecionado.getDataInicial()) >= 0
                            && afastamentos.getTermino().compareTo(selecionado.getDataFinal()) <= 0)) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("O servidor(a) " + selecionado.getContratoFP() + " se encontra Afastado no período de "
                            + DataUtil.getDataFormatada(afastamentos.getInicio()) + " a "
                            + DataUtil.getDataFormatada(afastamentos.getTermino())
                            + ", o sistema não permite conceder Férias para servidor no mesmo período de Afastamento.");
                    }
                }
            }
        }

        if (selecionado.getPeriodoAquisitivoFL() != null && selecionado.getDataInicial() != null) {
            ParametroFerias parametroFerias = parametroFeriasFacade.recuperarVigentePorTipoRegime(selecionado.getDataInicial(), selecionado.getContratoFP().getTipoRegime());
            if (parametroFerias != null) {
                if (selecionado.getDataInicial() != null && selecionado.getDataFinal() != null) {
                    int totalDiasConcessao = DataUtil.diasEntre(DataUtil.dateToLocalDate(selecionado.getDataInicial()), DataUtil.dateToLocalDate(selecionado.getDataFinal()));
                    if (parametroFerias.getQuantidMinimaDiasPorParcela() != null && parametroFerias.getQuantidMinimaDiasPorParcela() > 0 && totalDiasConcessao < parametroFerias.getQuantidMinimaDiasPorParcela()) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("A quantidade de dias da concessão por parcela deve ser maior que " + parametroFerias.getQuantidMinimaDiasPorParcela() + ".");
                    }
                }

                if (parametroFerias.getQuantidadeParcelaMaxima() != null && parametroFerias.getQuantidadeParcelaMaxima() > 0) {
                    List<ConcessaoFeriasLicenca> concessoesPA = concessaoFeriasLicencaFacade.listaConcessaoFeriasLicencaPorPeriodoAquisitivo(selecionado.getPeriodoAquisitivoFL());
                    if (concessoesPA != null && !concessoesPA.isEmpty() && concessoesPA.size() >= parametroFerias.getQuantidadeParcelaMaxima()) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("O número de concessões para o período aquisitivo selecionado ultrapassou a quantidade de parcelas permitidas.");
                    }
                }
            }
        }
        verificarExisteMensagemValidacaoException(ve);
        validarDataComunicacao();
    }

    private void validarDiasAbono() {
        ValidacaoException ve = new ValidacaoException();
        BigDecimal totalPermitido = new BigDecimal(selecionado.getPeriodoAquisitivoFL().getSaldoDeDireito());
        totalPermitido = (totalPermitido.divide(new BigDecimal(3), 1, RoundingMode.HALF_UP));
        if ((selecionado.getDiasAbonoPecuniario() != null ? selecionado.getDiasAbonoPecuniario() : Integer.valueOf("0")) > totalPermitido.intValue()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O total dos dias de Abono Pecúniario não deve exceder 1/3 do total dos dias de direito");
        }
        ve.lancarException();
    }

    private void validarDiasSolicitados() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDataInicial() != null && selecionado.getDataFinal() != null && selecionado.getDataInicial().after(selecionado.getDataFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data Final deve ser maior que a Data Inicial.");
        }
        ve.lancarException();
        int diasConcedidos = 0;
        for (ConcessaoFeriasLicenca concessaoSalva : concessaoFeriasLicencaFacade.buscarConcessoesFeriasLicencaPorPeriodoAquisitivoFL(selecionado.getPeriodoAquisitivoFL())) {
            if (selecionado.getId() == null || !selecionado.getId().equals(concessaoSalva.getId())) {
                diasConcedidos += concessaoSalva.getDias();
            }
        }

        if (selecionado.getDataInicial() != null && selecionado.getDataFinal() != null) {
            Integer diasSolicitados = DataUtil.diferencaDiasInteira(selecionado.getDataInicial(), selecionado.getDataFinal()) + 1;
            Integer saldo = selecionado.getPeriodoAquisitivoFL().getSaldoDeDireito();

            if ((diasSolicitados + diasConcedidos) > saldo) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O número de dias de concessão não pode exceder a quantidade de dias de saldo.");
            }
            diasSolicitados += selecionado.getDiasAbonoPecuniario() == null ? 0 : selecionado.getDiasAbonoPecuniario();
            if ((diasSolicitados + diasConcedidos) > saldo) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O número de dias de Concessão mais os dias de abono pecuniário não podem exceder a quantidade de dias de saldo.");
            }
            List<ConcessaoFeriasLicenca> concessoes = concessaoFeriasLicencaFacade.findConcessaoFeriasLincencaByServidor(selecionado.getContratoFP().getId(), TipoPeriodoAquisitivo.FERIAS);

            if (!DataUtil.isVigenciaValida(selecionado, concessoes)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Foi encontrado um registro vigente.");
            }
        }
        ve.lancarException();
    }

    private void verificarExisteMensagemValidacaoException(ValidacaoException ve) {
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    @URLAction(mappingId = "editarConcessaoFerias", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        inicilizarCampos();
        carregarEnvioDadosPonto();
        permiteMesAnoAnteriorFinalPA = vinculoFPFacade.hasAutorizacaoEspecialRH(sistemaFacade.getUsuarioCorrente(), TipoAutorizacaoRH.PERMITIR_REALIZAR_CONCESSAO_FERIAS_MES_ANO_PAGAMENTO_INFERIOR_FINAL_PA);
    }

    @URLAction(mappingId = "verConcessaoFerias", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        inicilizarCampos();
        carregarEnvioDadosPonto();
    }

    private void inicilizarCampos() {
        selecionado.setContratoFP(selecionado.getPeriodoAquisitivoFL().getContratoFP());
        arquivo = new Arquivo();
        fileUploadEvent = null;
        if (selecionado.getArquivo() != null) {
            arquivo = selecionado.getArquivo();
        }
    }

    @Override
    public void salvar() {
        selecionado.setTotalFaltasInjustificadas(totalFaltasInjustificadas);
        try {
            validarCampos();
            if (isOperacaoNovo()) {
                ConcessaoFeriasLicenca concessaoFeriasVigente = concessaoFeriasLicencaFacade.buscarConcessaoVigentePorTipo(selecionado.getPeriodoAquisitivoFL().getContratoFP(), TipoPeriodoAquisitivo.FERIAS);
                concessaoFeriasLicencaFacade.salvarNovo(selecionado, concessaoFeriasVigente, arquivo, fileUploadEvent);
            } else {
                concessaoFeriasLicencaFacade.salvar(selecionado, arquivo, fileUploadEvent);
            }
            FacesUtil.addOperacaoRealizada("Salvo com Sucesso.");
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Houve um erro ao salvar a Concessão. Erro:" + e);
        }
    }

    public Integer getTotalFaltasInjustificadas() {
        PeriodoAquisitivoFL periodo = selecionado.getPeriodoAquisitivoFL();

        if (periodo != null && selecionado.getContratoFP() != null) {
            totalFaltasInjustificadas = faltasFacade.recuperaDiasDeFaltas(selecionado.getContratoFP(), periodo);
        } else {
            totalFaltasInjustificadas = 0;
        }

        return totalFaltasInjustificadas;
    }

    public void setTotalFaltasInjustificadas(Integer totalFaltasInjustificadas) {
        this.totalFaltasInjustificadas = totalFaltasInjustificadas;
    }

    @Override
    public void cancelar() {
        super.cancelar();
        totalFaltasInjustificadas = 0;
    }

    @Override
    public void excluir() {
        try {
            concessaoFeriasLicencaFacade.realizarTratativasParaExclusaoDeConcessao(selecionado, null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
            return;
        }
        redireciona();
    }

    public void excluirSelecionado() {
        try {
            selecionado.getPeriodoAquisitivoFL().setStatus(StatusPeriodoAquisitivo.NAO_CONCEDIDO);
            selecionado.getPeriodoAquisitivoFL().setSaldo(selecionado.getPeriodoAquisitivoFL().getSaldoDeDireito());
            periodoAquisitivoFLFacade.salvar(selecionado.getPeriodoAquisitivoFL());

            concessaoFeriasLicencaFacade.remover((ConcessaoFeriasLicenca) selecionado);


            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Excluído com sucesso!", "O registro selecionado foi excluído com sucesso!"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Exceção do sistema!", e.getMessage()));
        }
        redireciona();
    }

    public void sugerirProgramacaoFerias() {
        ConcessaoFeriasLicenca concessaoFeriasLicenca = selecionado;
        SugestaoFerias sugestaoFerias = sugestaoFeriasFacade.buscaFiltrandoPeriodoAquisitivoFL(concessaoFeriasLicenca.getPeriodoAquisitivoFL());
        if (sugestaoFerias != null) {
            concessaoFeriasLicenca.setDataInicial(sugestaoFerias.getDataInicio());
            concessaoFeriasLicenca.setDataFinal(sugestaoFerias.getDataFim());

            Calendar dataComunic = Calendar.getInstance();
            dataComunic.setTime(sugestaoFerias.getDataInicio());
            dataComunic.add(Calendar.DAY_OF_MONTH, -30);
            concessaoFeriasLicenca.setDataComunicacao(dataComunic.getTime());

            if (sugestaoFerias.getAbonoPecunia()) {
                Calendar dataPecunia = Calendar.getInstance();
                dataPecunia.setTime(sugestaoFerias.getDataFim());
                dataPecunia.add(Calendar.DAY_OF_MONTH, 1);
                concessaoFeriasLicenca.setDataInicialAbonoPecuniario(dataPecunia.getTime());
                dataPecunia.add(Calendar.DAY_OF_MONTH, sugestaoFerias.getDiasAbono() - 1);
                concessaoFeriasLicenca.setDataFinalAbonoPecuniario(dataPecunia.getTime());
            }
            atribuirMesAnoPagamento();
            selecionado.setDiasAbonoPecuniario(sugestaoFerias.getDiasAbono() == null ? 0 : sugestaoFerias.getDiasAbono());
        }
    }

    private void atribuirMesAnoPagamento() {
        habilitarCampoMesAno = true;
        if (lancamentoTercoFeriasAutFacade.recuperaLancamentoTercoFeriasAutPorPeriodoAquisitivo(selecionado.getPeriodoAquisitivoFL()) != null) {
            java.time.LocalDate dataAtual = DataUtil.dateToLocalDate(sistemaFacade.getDataOperacao());
            java.time.LocalDate dataFinalPeriodo = DataUtil.dateToLocalDate(selecionado.getPeriodoAquisitivoFL().getFinalVigencia()).plusDays(1);
            if (!isPermiteInformarMesAnoPagamento()) {
                if (dataAtual.isBefore(dataFinalPeriodo) || (dataAtual.getYear() == dataFinalPeriodo.getYear() && dataAtual.getMonthValue() == dataFinalPeriodo.getMonthValue())) {
                    selecionado.setMes(dataFinalPeriodo.getMonthValue());
                    selecionado.setAno(dataFinalPeriodo.getYear());
                    habilitarCampoMesAno = false;
                } else {
                    selecionado.setMes(null);
                    selecionado.setAno(null);
                }
            }

        }
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public FileUploadEvent getFileUploadEvent() {
        return fileUploadEvent;
    }

    public void setFileUploadEvent(FileUploadEvent fileUploadEvent) {
        this.fileUploadEvent = fileUploadEvent;
    }

    public List<ItemEnvioDadosRBPonto> getItensEnvioDadosRBPonto() {
        return itensEnvioDadosRBPonto;
    }

    public void setItensEnvioDadosRBPonto(List<ItemEnvioDadosRBPonto> itensEnvioDadosRBPonto) {
        this.itensEnvioDadosRBPonto = itensEnvioDadosRBPonto;
    }

    public boolean isHabilitarCampoMesAno() {
        return habilitarCampoMesAno;
    }

    public void setHabilitarCampoMesAno(boolean habilitarCampoMesAno) {
        this.habilitarCampoMesAno = habilitarCampoMesAno;
    }

    public void apagaArquivo() {
        ((ConcessaoFeriasLicenca) selecionado).setArquivo(null);
    }

//    public void handleFileUpload(FileUploadEvent event) {
//        ConcessaoFeriasLicenca c = (ConcessaoFeriasLicenca) selecionado;
//        fileUploadEvent = event;
//        file = fileUploadEvent.getFile();
//    }

    public void handleFileUpload(FileUploadEvent event) {
        try {
            Arquivo arquivo = new Arquivo();
            arquivo.setMimeType(event.getFile().getContentType());
            arquivo.setNome(event.getFile().getFileName());
            arquivo.setTamanho(event.getFile().getSize());
            arquivo.setInputStream(event.getFile().getInputstream());
            arquivo = arquivoFacade.novoArquivoMemoria(arquivo);
            this.selecionado.setArquivo(arquivo);
        } catch (Exception ex) {
            logger.debug(ex.getMessage());
        }
    }

    public List<SelectItem> getTipoFerias() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));

        for (TipoFerias tipo : TipoFerias.values()) {
            toReturn.add(new SelectItem(tipo, tipo.toString()));
        }

        return toReturn;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/concessaoferias/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public StreamedContent recuperarArquivoParaDownload() {
        StreamedContent s = null;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (ArquivoParte a : selecionado.getArquivo().getPartes()) {
            try {
                buffer.write(a.getDados());
            } catch (IOException ex) {
                logger.error("Erro: ", ex);
            }
        }
        InputStream is = new ByteArrayInputStream(buffer.toByteArray());
        s = new DefaultStreamedContent(is, selecionado.getArquivo().getMimeType(), selecionado.getArquivo().getNome());
        return s;
    }

    public void removerArquivo() {
        this.selecionado.setArquivo(null);
    }

    public SugestaoFeriasControlador getSugestaoFeriasControlador() {
        return sugestaoFeriasControlador;
    }

    public void setSugestaoFeriasControlador(SugestaoFeriasControlador sugestaoFeriasControlador) {
        this.sugestaoFeriasControlador = sugestaoFeriasControlador;
    }

    public void confirmarProgramacao() {
        sugestaoFeriasControlador.setSugestaoFeriasSelecionada(selecionado.getPeriodoAquisitivoFL().getSugestaoFerias());
        if (!sugestaoFeriasControlador.validarCamposObrigatorios()) {
            return;
        }

        if (!sugestaoFeriasControlador.validarProgramacao()) {
            return;
        }

        RequestContext.getCurrentInstance().execute("programacaoFerias.hide()");
        RequestContext.getCurrentInstance().update("panelGeral");
    }

    private void carregarEnvioDadosPonto() {
        itensEnvioDadosRBPonto = envioDadosRBPontoFacade.buscarItensEnvioDadosPontoPorIdentificador(selecionado.getId(), TipoInformacaoEnvioRBPonto.FERIAS);
    }

    public boolean isPermiteInformarMesAnoPagamento() {
        ConfiguracaoRH configuracaoRH = configuracaoRHFacade.recuperarConfiguracaoRHVigente();
        return configuracaoRH.getTipoTercoFeriasAutomatico() == null || !TipoTercoFeriasAutomatico.ANIVERSARIO_CONTRATO
            .equals(configuracaoRH.getTipoTercoFeriasAutomatico());
    }
}
