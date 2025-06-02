/*
 * Codigo gerado automaticamente em Mon Oct 24 16:57:46 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ReferenciaFPFuncao;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.TipoPeriodoAquisitivo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author peixe
 */
@ManagedBean(name = "concessaoLicencaPremioControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoConcessaoLicencaPremio", pattern = "/concessao-licenca-premio/novo/", viewId = "/faces/rh/administracaodepagamento/concessaolicencapremio/edita.xhtml"),
    @URLMapping(id = "editarConcessaoLicencaPremio", pattern = "/concessao-licenca-premio/editar/#{concessaoLicencaPremioControlador.id}/", viewId = "/faces/rh/administracaodepagamento/concessaolicencapremio/edita.xhtml"),
    @URLMapping(id = "listarConcessaoLicencaPremio", pattern = "/concessao-licenca-premio/listar/", viewId = "/faces/rh/administracaodepagamento/concessaolicencapremio/lista.xhtml"),
    @URLMapping(id = "verConcessaoLicencaPremio", pattern = "/concessao-licenca-premio/ver/#{concessaoLicencaPremioControlador.id}/", viewId = "/faces/rh/administracaodepagamento/concessaolicencapremio/visualizar.xhtml")
})
public class ConcessaoLicencaPremioControlador extends PrettyControlador<ConcessaoFeriasLicenca> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(ConcessaoLicencaPremioControlador.class);

    @EJB
    private ConcessaoFeriasLicencaFacade concessaoFeriasLicencaFacade;
    @EJB
    private PeriodoAquisitivoFLFacade periodoAquisitivoFLFacade;
    private ConverterGenerico converterProgramacaoLicencaPremio;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    private ContratoFP contratoFP;
    private ConverterAutoComplete converterContratoFP;
    private Date inicioAbonoPecuniario;
    private Date fimAbonoPecuniario;
    @EJB
    private ConfiguracaoFaltasInjustificadasFacade configuracaoFaltasInjustificadasFacade;
    private Integer totalFaltasInjustificadas;
    @EJB
    private SugestaoFeriasFacade sugestaoFeriasFacade;
    @EJB
    private FaltasFacade faltasFacade;
    @EJB
    private BasePeriodoAquisitivoFacade basePeriodoAquisitivoFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    private ConverterAutoComplete converterAtoLegal;
    private int inicio = 0;
    @EJB
    private ProgramacaoLicencaPremioFacade programacaoLicencaPremioFacade;
    @EJB
    private AfastamentoFacade afastamentoFacade;
    @EJB
    private ParametroLicencaPremioFacade parametroLicencaPremioFacade;
    @EJB
    private FuncoesFolhaFacade funcoesFolhaFacade;

    private boolean bloquearDatasVigencia = false;

   public static final int FALTAS_INJUSTIFICADAS = 15;

    public ConcessaoLicencaPremioControlador() {
        super(ConcessaoFeriasLicenca.class);
    }

    public List getLista() {
        return concessaoFeriasLicencaFacade.listaTipos(TipoPeriodoAquisitivo.LICENCA);
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

    public Date getInicioAbonoPecuniario() {
        return inicioAbonoPecuniario;
    }

    public void setInicioAbonoPecuniario(Date inicioAbonoPecuniario) {
        this.inicioAbonoPecuniario = inicioAbonoPecuniario;
    }

    public ConcessaoFeriasLicenca getConcessao() {
        return ((ConcessaoFeriasLicenca) selecionado);
    }

    public void setDataInicial(SelectEvent event) {
        getConcessao().setDataInicialAbonoPecuniario((Date) event.getObject());
    }

    public void setDataFinal(SelectEvent event) {
        getConcessao().setDataFinalAbonoPecuniario((Date) event.getObject());
    }

    public void setMes(Integer i) {
        getConcessao().setMes(i);
    }

    public void setAno(Integer i) {
        getConcessao().setAno(i);
    }

    public Integer getDiasDeAbono() {
        ConcessaoFeriasLicenca cfl = getConcessao();
        if (getConcessao().getDataInicialAbonoPecuniario() != null) {
            if (getConcessao().getDataFinalAbonoPecuniario() != null) {
                if (getConcessao().getDataFinalAbonoPecuniario().after(getConcessao().getDataInicialAbonoPecuniario())) {
                    if (getConcessao().getDataInicialAbonoPecuniario().after(getConcessao().getDataFinal())) {
                        logger.debug("Validou");
                        Long dataInicial = getConcessao().getDataInicialAbonoPecuniario().getTime() / (24 * 60 * 60 * 1000);
                        Long dataFim = getConcessao().getDataFinalAbonoPecuniario().getTime() / (24 * 60 * 60 * 1000);
                        Long diferenca = (dataFim - dataInicial) + 1;
                        getConcessao().setDiasAbonoPecuniario(diferenca.intValue());
                        return diferenca.intValue();
                    } else {
                        logger.debug("Nao validou....");
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "a Vigência do Abono Pecúniario está entre a Vigência da Concessão."));
                    }
                } else if (cfl.getDataInicialAbonoPecuniario().equals(cfl.getDataFinalAbonoPecuniario())) {
                    getConcessao().setDiasAbonoPecuniario(1);
                    return 1;
                } else {
                    logger.debug("Nao validou....");
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "a data final é menor que a data inicial"));
                }
            } else {
                logger.debug("Nao validou...");
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "A vigência final do abono está nula"));
            }
        } else {
            logger.debug("Nao validou..");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "A vigência inicial do abono pecuniário está nula."));
        }
        getConcessao().setDiasAbonoPecuniario(0);
        return 0;
    }

    public void setDiasDeAbono(Integer i) {
        getConcessao().setDiasAbonoPecuniario(i);
    }

    public Integer getDias() {
        if (validaVigencia(false)) {
            logger.debug("Validou");
            Long dataInicial = getConcessao().getDataInicial().getTime() / (24 * 60 * 60 * 1000);
            Long dataFim = getConcessao().getDataFinal().getTime() / (24 * 60 * 60 * 1000);
            Long diferenca = (dataFim - dataInicial) + 1;
            getConcessao().setDias(diferenca.intValue());
            return diferenca.intValue();
        } else {
            return null;
        }
    }

    public void setDias(Integer i) {
        getConcessao().setDias(i);
    }

    public boolean verificaDataComunicacao() {
        ConcessaoFeriasLicenca c = getConcessao();
        if (getConcessao().getLicencaPremioIndenizada()) {
            return true;
        }

        if (getConcessao().getDataComunicacao() != null && getConcessao().getDataInicial() != null) {

            Calendar dtComunicacao = Calendar.getInstance();
            dtComunicacao.setTime(getConcessao().getDataComunicacao());
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

            if (diferenca >= 30) {
                return true;
            } else {
                logger.debug("Nao validou..");
                FacesUtil.addError(SummaryMessages.ATENCAO.getDescricao(), "A data de Comunicação deve anteceder no mínimo 30 dias da data inicial");

            }
        }
        return false;
    }

    public boolean validaVigencia(boolean emiteMsg) {
        ConcessaoFeriasLicenca cfl = getConcessao();
        boolean valida = false;

        if (getConcessao().getLicencaPremioIndenizada()) {
            return true;
        }

        if (getConcessao().getDataInicial() != null && getConcessao().getDataFinal() != null) {
            if (getConcessao().getDataInicial().equals(getConcessao().getDataFinal())) {
                valida = true;
            } else if (getConcessao().getDataInicial().before(getConcessao().getDataFinal())) {
                valida = true;
            } else {
                if (emiteMsg) {
                    FacesContext.getCurrentInstance().addMessage("msgs", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "a data final é menor que a data inicial"));
                }
                valida = false;
            }
        } else {
            if (emiteMsg) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "A vigência está nula"));
            }
            valida = false;
        }
        return valida;
    }

    public String getDiasDeDireito() {
        if (getConcessao().getPeriodoAquisitivoFL() != null) {
            return (getConcessao().getPeriodoAquisitivoFL().getSaldoDeDireito()) + "";
        } else {
            return "";
        }
    }

    public String getSaldo() {
        if (getConcessao().getPeriodoAquisitivoFL() != null) {
            if (isOperacaoNovo()) {
                return (getConcessao().getPeriodoAquisitivoFL().getSaldo()) + "";
            } else {
                return (getConcessao().getPeriodoAquisitivoFL().getSaldo() - getTotalFaltasInjustificadas() + quantidadeDias()) + "";
            }
        } else {
            return "";
        }
    }

    public List<SelectItem> programacoesLicencaPremio() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        lista.add(new SelectItem(null, ""));

        if (contratoFP != null) {
            for (ProgramacaoLicencaPremio p : programacaoLicencaPremioFacade.recuperaProgramacoes(contratoFP)) {
                String dataInicial = DataUtil.getDataFormatada(p.getInicioVigencia());
                String dataFinal = DataUtil.getDataFormatada(p.getFinalVigencia());
                Integer dias = DataUtil.diasEntre(new DateTime(p.getInicioVigencia()), new DateTime(p.getFinalVigencia()));
                if (dias.compareTo(0) <= 0) {
                    dias = 0;
                }
                lista.add(new SelectItem(p, "Programação: " + dataInicial + " - " + dataFinal + " : " + dias + " dias - Período Aquisitivo: " + p.getPeriodoAquisitivoFL().toStringAlternativo()));
            }
        }
        return lista;
    }

    public ConverterGenerico getConverterProgramacaoLicencaPremio() {
        if (converterProgramacaoLicencaPremio == null) {
            converterProgramacaoLicencaPremio = new ConverterGenerico(ProgramacaoLicencaPremio.class, programacaoLicencaPremioFacade);
        }
        return converterProgramacaoLicencaPremio;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public Converter getConverterContratoFP() {
        if (converterContratoFP == null) {
            converterContratoFP = new ConverterAutoComplete(ContratoFP.class, contratoFPFacade);
        }
        return converterContratoFP;
    }

    public List<ContratoFP> buscarContratoFP(String parte) {
        return contratoFPFacade.buscarContratoPorTipoPeriodoAquisitivo(parte.trim(), TipoPeriodoAquisitivo.LICENCA);
    }

    @URLAction(mappingId = "novoConcessaoLicencaPremio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        contratoFP = null;
        ((ConcessaoFeriasLicenca) selecionado).setDataCadastro(new Date());
        totalFaltasInjustificadas = 0;
    }

    @URLAction(mappingId = "verConcessaoLicencaPremio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        selecionar();
    }

    @URLAction(mappingId = "editarConcessaoLicencaPremio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        selecionar();
    }

    public Boolean validaCampos() {
        validaVigencia(true);

        if (Util.validaCampos(selecionado)) {
            ConcessaoFeriasLicenca concessao = getConcessao();

            if (concessao.getProgramacaoLicencaPremio() == null) {
                FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "O campo Programação da Licença Prêmio é obrigatório.");
            }

            if (!validaVigencia(false)) {
                return false;
            }

            if (!validaDiasAbono()) {
                FacesUtil.addError(SummaryMessages.ATENCAO.getDescricao(), "O total dos dias de Abono Pecúniario não deve exceder 1/3 do total dos dias de direito");
                return false;
            }
            Integer saldo = Integer.valueOf(getSaldo());
            if (getConcessao().getDias().compareTo(saldo) > 0) {
                FacesUtil.addError(SummaryMessages.ATENCAO.getDescricao(), "O número de dias de concessão não pode exceder a quantidade de dias de saldo.");
                return false;
            }


            Integer diasAbonoPecuniario = getConcessao().getDiasAbonoPecuniario();
            if (diasAbonoPecuniario == null) {
                diasAbonoPecuniario = 0;
            }

            Integer diasComAbono = (getConcessao().getDias() + diasAbonoPecuniario);
            if (diasComAbono.compareTo(saldo) > 0) {
                FacesUtil.addError(SummaryMessages.ATENCAO.getDescricao(), "O número de dias de Concessão mais os dias de abono pecuniário não pode exceder a quantidade de dias de saldo.");
                return false;
            }

            if (getConcessao().getAtoLegal() == null) {
                FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "O Ato legal é obrigatório");
                return false;
            }
            if (selecionado.getDataInicial() != null) {
                List<ConcessaoFeriasLicenca> listaDeFerias = concessaoFeriasLicencaFacade.listaFiltrandoServidorPorCompetencia(getContratoFP(), selecionado.getDataInicial(), selecionado.getDataFinal());
                if (listaDeFerias != null && !listaDeFerias.isEmpty()) {
                    for (ConcessaoFeriasLicenca ferias : listaDeFerias) {
                        if (ferias.getId().equals(selecionado.getId())) {
                            continue;
                        }
                        if ((ferias.getDataInicial().compareTo(selecionado.getDataInicial()) >= 0
                            && ferias.getDataInicial().compareTo(selecionado.getDataFinal()) <= 0)
                            || ferias.getDataFinal().compareTo(selecionado.getDataInicial()) >= 0
                            && ferias.getDataFinal().compareTo(selecionado.getDataFinal()) <= 0) {
                            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "O servidor(a) " + getContratoFP() + " está gozando de "
                                + ferias.getPeriodoAquisitivoFL().getBaseCargo().getBasePeriodoAquisitivo().getTipoPeriodoAquisitivo().getDescricao()
                                + " no período de " + DataUtil.getDataFormatada(ferias.getDataInicial()) + " a "
                                + DataUtil.getDataFormatada(ferias.getDataFinal())
                                + ", o sistema não permite conceder Licença Prêmio para servidor no mesmo período de "
                                + ferias.getPeriodoAquisitivoFL().getBaseCargo().getBasePeriodoAquisitivo().getTipoPeriodoAquisitivo().getDescricao() + ".");
                            return false;
                        }
                    }
                }

                List<Afastamento> listaDeafastamentos = afastamentoFacade.listaFiltrandoServidorPorCompetencia(getContratoFP(), selecionado.getDataInicial(), selecionado.getDataFinal());
                if (listaDeafastamentos != null && !listaDeafastamentos.isEmpty()) {
                    for (Afastamento afastamentos : listaDeafastamentos) {
                        if (selecionado.getDataInicial().compareTo(afastamentos.getInicio()) >= 0
                            && selecionado.getDataInicial().compareTo(afastamentos.getTermino()) <= 0
                            || selecionado.getDataFinal().compareTo(afastamentos.getInicio()) >= 0
                            && selecionado.getDataFinal().compareTo(afastamentos.getTermino()) <= 0
                            || afastamentos.getInicio().compareTo(selecionado.getDataInicial()) >= 0
                            && afastamentos.getInicio().compareTo(selecionado.getDataFinal()) <= 0
                            || afastamentos.getTermino().compareTo(selecionado.getDataInicial()) >= 0
                            && afastamentos.getTermino().compareTo(selecionado.getDataFinal()) <= 0) {
                            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "O servidor(a) " + getContratoFP() + " se encontra Afastado no período de "
                                + DataUtil.getDataFormatada(afastamentos.getInicio()) + " a "
                                + DataUtil.getDataFormatada(afastamentos.getTermino())
                                + ", o sistema não permite conceder Licença Prêmio para servidor no mesmo período de Afastamento.");
                            return false;
                        }
                    }
                }
            }
            return verificaDataComunicacao();
        }
        return false;
    }

    public boolean validaDiasAbono() {
        BigDecimal totalPermitido = new BigDecimal(getConcessao().getPeriodoAquisitivoFL().getSaldoDeDireito());
        totalPermitido = (totalPermitido.divide(new BigDecimal(3), 1, RoundingMode.HALF_UP));
        Integer diasAbonoPecuniario = getConcessao().getDiasAbonoPecuniario();
        if (diasAbonoPecuniario == null) {
            diasAbonoPecuniario = 0;
        }
        if (diasAbonoPecuniario > totalPermitido.intValue()) {
            return false;
        }
        return true;
    }

    public void selecionar() {
        contratoFP = ((ConcessaoFeriasLicenca) selecionado).getPeriodoAquisitivoFL().getContratoFP();
        selecionado.setContratoFP(contratoFP);
    }

    private void validarCamposEspecificos() {
        ValidacaoException ve = new ValidacaoException();
        if (!selecionado.getLicencaPremioIndenizada()) {
            if (selecionado.getDataInicial() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("A Data Inicial deve ser informada.");
            }
            if (selecionado.getDataFinal() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("A Data Final deve ser informada.");
            }
            if (selecionado.getDataComunicacao() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("A Data Comunicação deve ser informada.");
            }
        }
        ve.lancarException();
    }

    private void validarFaltasNoPeriodoAquisitivo() {
        if (selecionado.getPeriodoAquisitivoFL().getFinalVigencia().before(selecionado.getInicioVigencia())) {
            ValidacaoException ve = new ValidacaoException();
            ParametroLicencaPremio parametroLicencaPremio = parametroLicencaPremioFacade.recuperaVigente();
            Integer totalFaltas = faltasFacade.recuperaDiasDeFaltasPorPeriodo(contratoFP.getIdCalculo(), selecionado.getPeriodoAquisitivoFL().getInicioVigencia(), selecionado.getPeriodoAquisitivoFL().getFinalVigencia());
            if (parametroLicencaPremio != null && parametroLicencaPremio.getQuantidadeDiasPerdaPeriodo() != null && parametroLicencaPremio.getQuantidadeDiasPerdaPeriodo() > 0 && totalFaltas >= parametroLicencaPremio.getQuantidadeDiasPerdaPeriodo()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Devido ao número de faltas injustificadas (16 ou mais) o vínculo perdeu o direito à concessão do período aquisitivo");
            } else if (totalFaltas > 0 && parametroLicencaPremio != null && parametroLicencaPremio.getAdiarConcecaoUmMesPorFalta()) {
                java.time.LocalDate dataInicialPermitida = (DataUtil.dateToLocalDate(selecionado.getPeriodoAquisitivoFL().getFinalVigencia()).plusDays(1)).plusMonths(totalFaltas);
                if (dataInicialPermitida.isAfter(DataUtil.dateToLocalDate(selecionado.getDataInicial()))) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Data de início de gozo inválida devido a faltas injustificadas. Regra: cada falta injustificada acrescenta 1 mês ao final do período aquisitivo para concessão. Acima de 15 faltas injustificadas não será mais permitida a concessão do período aquisitivo.<br/><br/>" +
                        " Número de faltas injustificadas apuradas: <b>" + totalFaltas + "<b/><br/><br/>" +
                        " Concessão permitida a partir de: <b>" + DataUtil.getDataFormatada(dataInicialPermitida) + "<b>");
                }
            } else if (totalFaltas > 0 && parametroLicencaPremio != null && parametroLicencaPremio.getReferenciaFP() != null) {
                FolhaDePagamento fp = new FolhaDePagamento();
                fp.setTipoFolhaDePagamento(TipoFolhaDePagamento.NORMAL);
                contratoFP.setFolha(fp);
                contratoFP.setMes(DataUtil.getMes(selecionado.getPeriodoAquisitivoFL().getInicioVigencia()));
                contratoFP.setAno(DataUtil.getAno(selecionado.getPeriodoAquisitivoFL().getFinalVigencia()));

                ReferenciaFPFuncao faixa = funcoesFolhaFacade.obterReferenciaFaixaFP(contratoFP, parametroLicencaPremio.getReferenciaFP().getCodigo(), (double) totalFaltas, 0, 0);
                if (faixa != null) {
                    int diasParaDescontar = faixa.getValor().intValue();
                    java.time.LocalDate dataInicialPermitida = DataUtil.dateToLocalDate(selecionado.getPeriodoAquisitivoFL().getFinalVigencia()).plusDays(diasParaDescontar + 1);
                    if (dataInicialPermitida.isAfter(DataUtil.dateToLocalDate(selecionado.getDataInicial()))) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("Data de início de gozo inválida devido a faltas injustificadas. Regra: cada falta injustificada acrescenta 1 mês ao final do período aquisitivo para concessão. Acima de 15 faltas injustificadas não será mais permitida a concessão do período aquisitivo.<br/><br/>" +
                            " Número de faltas injustificadas apuradas: <b>" + totalFaltas + "<b/><br/><br/>" +
                            " Concessão permitida a partir de: <b>" + DataUtil.getDataFormatada(dataInicialPermitida) + "<b>");
                    }
                }
            }
            ve.lancarException();
        }
    }

    @Override
    public void salvar() {
        try {
            validarCamposEspecificos();
            validarFaltasNoPeriodoAquisitivo();
            ConcessaoFeriasLicenca concessao = ((ConcessaoFeriasLicenca) selecionado);
            concessao.setTotalFaltasInjustificadas(totalFaltasInjustificadas);
            if (validaCampos()) {
                if (operacao == Operacoes.NOVO) {
                    try {
                        ConcessaoFeriasLicenca concessaoFeriasVigente = concessaoFeriasLicencaFacade.buscarConcessaoVigentePorTipo(concessao.getPeriodoAquisitivoFL().getContratoFP(), TipoPeriodoAquisitivo.LICENCA);
                        concessao.setContratoFP(contratoFP);
                        if (concessao.getDataInicial() != null) {
                            concessao.setMes(DataUtil.getMes(concessao.getDataInicial()));
                            concessao.setAno(DataUtil.getAno(concessao.getDataInicial()));
                        }
                        concessaoFeriasLicencaFacade.salvarNovo(concessao, concessaoFeriasVigente, null, null);
                        FacesContext.getCurrentInstance().addMessage("msgs", new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Salvo com Sucesso"));

                        redireciona();
                    } catch (ValidacaoException ve) {
                        FacesUtil.printAllFacesMessages(ve.getMensagens());
                    } catch (Exception e) {
                        FacesUtil.addOperacaoNaoRealizada("Houve um erro ao salvar a Concessão. Erro:" + e);
                    }
                } else {

                    try {
                        concessaoFeriasLicencaFacade.salvar(((ConcessaoFeriasLicenca) selecionado));
                        FacesContext.getCurrentInstance().addMessage("msgs", new FacesMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Salvo com Sucesso"));
                        redireciona();
                    } catch (Exception e) {
                        FacesContext.getCurrentInstance().addMessage("msgs", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Atenção", "Houve um erro ao editar a Concessão. Erro:" + e));
                    }
                }
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public Integer getTotalFaltasInjustificadas() {
        if (selecionado != null) {
            PeriodoAquisitivoFL periodo = selecionado.getPeriodoAquisitivoFL();
            if (periodo != null && contratoFP != null) {
                totalFaltasInjustificadas = faltasFacade.recuperaDiasDeFaltas(contratoFP, periodo);
            } else {
                totalFaltasInjustificadas = 0;
            }
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

    public boolean validaVigenciaAbonoPecuniario() {
        ConcessaoFeriasLicenca cfl = getConcessao();
        boolean valida = true;

        if (cfl.getDataInicialAbonoPecuniario() != null && cfl.getDataFinalAbonoPecuniario() != null) {
            if (cfl.getDataInicialAbonoPecuniario().equals(cfl.getDataFinalAbonoPecuniario())) {
                valida = true;
            } else if (cfl.getDataInicialAbonoPecuniario().before(cfl.getDataFinalAbonoPecuniario())) {
                valida = true;
            } else {
                FacesContext.getCurrentInstance().addMessage("Formulario:dataFinal", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "a data final do abono pecuniário é menor que a data inicial do abono pecuniário"));
                valida = false;
            }
        } else {
            FacesContext.getCurrentInstance().addMessage("Formulario:dataInicial", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "A vigência do abono pecuniário não foi informada"));
            valida = false;
        }
        return valida;
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

    public void sugerirProgramacaoFerias() {
        if (isProgramacaoValida()) {
            ConcessaoFeriasLicenca concessaoFeriasLicenca = getConcessao();
            if (concessaoFeriasLicenca.getProgramacaoLicencaPremio() != null) {
                concessaoFeriasLicenca.setPeriodoAquisitivoFL(concessaoFeriasLicenca.getProgramacaoLicencaPremio().getPeriodoAquisitivoFL());
            } else {
                concessaoFeriasLicenca.setPeriodoAquisitivoFL(null);
            }
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
            }
            if (isProgramacaoInformada() && selecionado.getProgramacaoLicencaPremio().getId() != null) {
                selecionado.setDataInicial(selecionado.getProgramacaoLicencaPremio().getInicioVigencia());
                selecionado.setDataFinal(selecionado.getProgramacaoLicencaPremio().getFinalVigencia());
                selecionado.setLicencaPremioIndenizada(selecionado.getProgramacaoLicencaPremio().getLicencaPremioIndenizada());
                selecionado.setDiasPecunia(selecionado.getProgramacaoLicencaPremio().getDiasPecunia());
            }
        }
    }

    private boolean isProgramacaoValida() {
        if (existeConcessao()) {
            FacesUtil.addOperacaoNaoPermitida("Já existe concessão de licença prêmio para a programação selecionada.");
            selecionado.setProgramacaoLicencaPremio(null);
            return false;
        }
        return true;
    }

    public Converter getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, atoLegalFacade);
        }
        return converterAtoLegal;
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return atoLegalFacade.listaFiltrandoNomeNumero(parte.trim());
    }

    public void calculaFinalVigencia() {
        if (isProgramacaoInformada()) {
            DateTime dtInicioConcessao = null;
            DateTime dtInicioProgramacao = null;
            if (selecionado.getDataInicial() != null) {
                dtInicioConcessao = new DateTime(selecionado.getDataInicial());
            }
            if (selecionado.getProgramacaoLicencaPremio().getInicioVigencia() != null) {
                dtInicioProgramacao = new DateTime(selecionado.getProgramacaoLicencaPremio().getInicioVigencia());
            }

            if (dtInicioConcessao != null && dtInicioProgramacao != null) {

                if (dtInicioConcessao.isBefore(dtInicioProgramacao)) {
                    FacesUtil.addOperacaoNaoPermitida("A data inicial da concessão deve ser posterior a data " + DataUtil.getDataFormatada(dtInicioProgramacao.toDate()));
                    selecionado.setDataInicial(dtInicioProgramacao.toDate());
                }
            }
            selecionado.setDataFinal(DataUtil.adicionaDias(selecionado.getDataInicial(), getDiasProgramado()));
        }
    }

    public boolean isProgramacaoInformada() {
        return selecionado.getProgramacaoLicencaPremio() != null;
    }

    public Integer diasJaGozados() {
        if (isProgramacaoInformada()) {
            Integer dias = Days.daysBetween(new DateTime(selecionado.getProgramacaoLicencaPremio().getInicioVigencia()), new DateTime(UtilRH.getDataOperacao())).getDays();
            Integer diasPossiveis = getDiasProgramado();

            if (dias > diasPossiveis) {
                return diasPossiveis;
            }

            if (dias < 0) {
                return 0;
            }
            return dias;
        } else {
            return 0;
        }
    }

    public Integer diasRestantesAGozar() {
        if (isProgramacaoInformada()) {
            Integer dias = Days.daysBetween(new DateTime(selecionado.getProgramacaoLicencaPremio().getInicioVigencia()), new DateTime(UtilRH.getDataOperacao())).getDays();
            Integer diasPossiveis = getDiasProgramado();

            if (dias < 0) {
                return diasPossiveis;
            }
            if (diasPossiveis > dias) {
                return diasPossiveis - dias;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    private int getDiasProgramado() {
        return Days.daysBetween(new DateTime(selecionado.getProgramacaoLicencaPremio().getInicioVigencia()), new DateTime(selecionado.getProgramacaoLicencaPremio().getFinalVigencia())).getDays();
    }

    public Integer quantidadeDias() {
        selecionado.setDias(0);
        if (selecionado.getDataFinal() != null && selecionado.getDataInicial() != null) {
            selecionado.setDias(DataUtil.diasEntre(new DateTime(selecionado.getDataInicial()), new DateTime(selecionado.getDataFinal())));
        }
        return selecionado.getDias();
    }

    public boolean existeConcessao() {
        if (isOperacaoNovo()) {
            if (isProgramacaoInformada()) {
                if (concessaoFeriasLicencaFacade.recuperaConcessaoFeriasLicencaDaProgramacao(selecionado.getProgramacaoLicencaPremio()) != null) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/concessao-licenca-premio/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }


    public boolean isLicencaPremioIndenizada() {
        return selecionado.getLicencaPremioIndenizada();
    }

    public boolean isBloquearDatasVigencia() {
        return bloquearDatasVigencia;
    }

    public void setBloquearDatasVigencia(boolean bloquearDatasVigencia) {
        this.bloquearDatasVigencia = bloquearDatasVigencia;
    }


    public void limparDiasPecuniaAndHabilitarDatas() {
        if (!selecionado.getLicencaPremioIndenizada()) {
            bloquearDatasVigencia = false;
            selecionado.setDiasPecunia(null);
        }
    }


}
