package br.com.webpublico.controle;

import br.com.webpublico.entidades.AtoLegal;
import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.PeriodoAquisitivoFL;
import br.com.webpublico.entidades.ProgramacaoLicencaPremio;
import br.com.webpublico.enums.StatusPeriodoAquisitivo;
import br.com.webpublico.enums.TipoFalta;
import br.com.webpublico.enums.TipoPeriodoAquisitivo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.DateTime;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.*;

@ManagedBean(name = "programacaoLicencaPremioControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoProgramacaoLicencaPremioControlador", pattern = "/programacao-licenca-premio/novo/", viewId = "/faces/rh/administracaodepagamento/programacaolicencapremio/edita.xhtml"),
    @URLMapping(id = "editarProgramacaoLicencaPremioControlador", pattern = "/programacao-licenca-premio/editar/#{programacaoLicencaPremioControlador.id}/", viewId = "/faces/rh/administracaodepagamento/programacaolicencapremio/edita.xhtml"),
    @URLMapping(id = "listarProgramacaoLicencaPremioControlador", pattern = "/programacao-licenca-premio/listar/", viewId = "/faces/rh/administracaodepagamento/programacaolicencapremio/lista.xhtml"),
    @URLMapping(id = "verProgramacaoLicencaPremioControlador", pattern = "/programacao-licenca-premio/ver/#{programacaoLicencaPremioControlador.id}/", viewId = "/faces/rh/administracaodepagamento/programacaolicencapremio/visualizar.xhtml")
})
public class ProgramacaoLicencaPremioControlador extends PrettyControlador<ProgramacaoLicencaPremio> implements Serializable, CRUD {

    @EJB
    private ProgramacaoLicencaPremioFacade programacaoLicencaPremioFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private PeriodoAquisitivoFLFacade periodoAquisitivoFLFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    private ConverterGenerico converterPeriodoAquisitivoFL;
    private ConverterAutoComplete converterContratoFP;
    private ConverterAutoComplete converterAtoLegal;
    @EJB
    private BasePeriodoAquisitivoFacade basePeriodoAquisitivoFacade;
    @EJB
    private FaltasFacade faltasFacade;
    @EJB
    private ParametroLicencaPremioFacade parametroLicencaPremioFacade;
    private Integer diasRestanteGozo;
    private List<ProgramacaoLicencaPremio> programacoesDoPeriodoAquisitivo;
    private Long diasFalta;
    private Integer totalDiasAGozar;
    private Integer diasFinalVigencia;
    private boolean bloquearDatasVigencia = false;

    public ProgramacaoLicencaPremioControlador() {
        super(ProgramacaoLicencaPremio.class);
    }

    public List<ProgramacaoLicencaPremio> getProgramacoesDoPeriodoAquisitivo() {
        return programacoesDoPeriodoAquisitivo;
    }

    public void setProgramacoesDoPeriodoAquisitivo(List<ProgramacaoLicencaPremio> programacoesDoPeriodoAquisitivo) {
        this.programacoesDoPeriodoAquisitivo = programacoesDoPeriodoAquisitivo;
    }

    public Integer getDiasRestanteGozo() {
        return diasRestanteGozo;
    }

    public void setDiasRestanteGozo(Integer diasRestanteGozo) {
        this.diasRestanteGozo = diasRestanteGozo;
    }

    public Long getDiasFalta() {
        return diasFalta;
    }

    public void setDiasFalta(Long diasFalta) {
        this.diasFalta = diasFalta;
    }

    public Integer getDiasFinalVigencia() {
        return diasFinalVigencia;
    }

    public void setDiasFinalVigencia(Integer diasFinalVigencia) {
        this.diasFinalVigencia = diasFinalVigencia;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/programacao-licenca-premio/";
    }

    @Override
    public AbstractFacade getFacede() {
        return programacaoLicencaPremioFacade;
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoProgramacaoLicencaPremioControlador", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        diasFalta = 0L;
        totalDiasAGozar = 0;
        diasRestanteGozo = 0;
        diasFinalVigencia = 0;
        selecionado.setDiasRetardo(0);
        selecionado.setDataCadastro(programacaoLicencaPremioFacade.getSistemaFacade().getDataOperacao());
        selecionado.setParametroLicencaPremio(parametroLicencaPremioFacade.recuperaVigente());
        recuperarProgramacoes();
    }

    @URLAction(mappingId = "verProgramacaoLicencaPremioControlador", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        selecionado.setContratoFP(selecionado.getPeriodoAquisitivoFL().getContratoFP());
        selecionado.setPeriodoAquisitivo(selecionado.getPeriodoAquisitivoFL().toStringAlternativo());
        selecionado.setDiasGozo(DataUtil.diasEntre(new DateTime(selecionado.getInicioVigencia()), new DateTime(selecionado.getFinalVigencia())));
    }

    @URLAction(mappingId = "editarProgramacaoLicencaPremioControlador", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarProgramacoes();
        selecionado.setContratoFP(selecionado.getPeriodoAquisitivoFL().getContratoFP());
        diasFalta = faltasFacade.recuperaQuantidadeFaltasPorPeriodo(selecionado.getContratoFP(), selecionado.getPeriodoAquisitivoFL().getInicioVigencia(), selecionado.getPeriodoAquisitivoFL().getFinalVigencia(), TipoFalta.FALTA_INJUSTIFICADA);
        validarPeriodoAquisitivo();
        setDiasFinalVigencia(DataUtil.diasEntreDate(selecionado.getInicioVigencia(), selecionado.getFinalVigencia()));
        setDiasRestanteGozo(selecionado.getParametroLicencaPremio().getDiasGozo() - getDiasFinalVigencia());
    }

    private void validarRegrasLicencaPremioIndenizada() {
        ValidacaoException ve = new ValidacaoException();
        if (!selecionado.getLicencaPremioIndenizada()) {
            if (selecionado.getInicioVigencia() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Início de Gozo é obrigatório.");
            }
            if (selecionado.getFinalVigencia() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Final de Gozo é obrigatório.");
            }
            if (selecionado.getInicioVigencia() != null && selecionado.getFinalVigencia() != null) {
                if (selecionado.getInicioVigencia().after(selecionado.getFinalVigencia())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O Início de Gozo não pode ser maior que o Final de Gozo.");
                }
            }
        } else {
            if (selecionado.getDiasPecunia() == null || selecionado.getDiasPecunia() <= 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Dias de Pecúnia deve ser informado e ser maior que zero;");
            }
        }
        ve.lancarException();
    }

    private void verificarDiasPecunia() {
        if (selecionado.getDiasPecunia() != null && selecionado.getDiasPecunia() <= totalDiasAGozar) {
            selecionado.setInicioVigencia(null);
            selecionado.setFinalVigencia(null);
        }
    }

    @Override
    public void salvar() {
        try {
            validarRegrasLicencaPremioIndenizada();
            if (validaCampos()) {
                if (validaRegrasEspecificas()) {
                    verificarDiasPecunia();

                    if (diasRestanteGozo - totalDiasAGozar > 0) {
                        if (selecionado.getParametroLicencaPremio().getParcela() == programacoesDoPeriodoAquisitivo.size()) {

                            ProgramacaoLicencaPremio ultimaProgramacao = programacaoLicencaPremioFacade.recuperaUltimaProgramacao(selecionado.getPeriodoAquisitivoFL());

                            if (ultimaProgramacao != null && !ultimaProgramacao.getId().equals(selecionado.getId())) {

                                ultimaProgramacao.setFinalVigencia(DataUtil.adicionaDias(ultimaProgramacao.getFinalVigencia(), diasRestanteGozo - totalDiasAGozar));

                                programacaoLicencaPremioFacade.salvar(ultimaProgramacao);
                                FacesUtil.addAtencao("O(s) dia(s) restante(s) (" + (diasRestanteGozo - totalDiasAGozar) + " dia(s)), " +
                                    "foram somados ao final de vigência da última programação deste período aquisitivo.");
                            } else {
                                FacesUtil.addAtencao("Utilize todos os dias restantes para esta programação.");
                                return;
                            }
                        }
                    }

                    if (isOperacaoNovo()) {
                        programacaoLicencaPremioFacade.salvarNovo(selecionado);
                        FacesUtil.addInfo("Operação Realizada! ", " Registro salvo com sucesso.");
                    } else {
                        programacaoLicencaPremioFacade.salvar(selecionado);
                        FacesUtil.addInfo("Operação Realizada! ", " Registro alterado com sucesso.");
                    }
                    redireciona();
                }
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Operação não Realizada! ", ex.getMessage()));
        }
    }

    public boolean validaCampos() {
        return Util.validaCampos(selecionado);
    }

    public void navegarAtoLegal() {
        Web.navegacao(getUrlAtual(), "/atolegal/novo/", selecionado);
    }

    @Override
    public boolean validaRegrasEspecificas() {

        if (!validarDatasInicioFinalGozo()) {
            return false;
        }

        if (selecionado.getInicioVigencia().compareTo(DataUtil.adicionaDias(selecionado.getPeriodoAquisitivoFL().getFinalVigencia(), selecionado.getDiasRetardo())) < 0) {
            FacesUtil.addOperacaoNaoPermitida("Este servidor possui " + diasFalta + " falta(s) no período aquisitivo. " +
                "O Início de Gozo deve ser a partir de " + DataUtil.getDataFormatada(DataUtil.adicionaDias(selecionado.getPeriodoAquisitivoFL().getFinalVigencia(), selecionado.getDiasRetardo())));
            return false;
        }

        if (DataUtil.verificaDataFinalMaiorQueDataInicial(selecionado.getInicioVigencia(), selecionado.getFinalVigencia())) {
            FacesUtil.addAtencao("O Final de Vigência não pode ser anterior ao Início de Vigência.");
            return false;
        }

        if (validaNovaProgramacaoReferenteAoLimiteDeParcelasPermitidas()) {
            FacesUtil.addAtencao("Não é possível criar uma nova programação para este período aquisitivo. Já foram criadas o máximo permitido.");
            return false;
        }

        if (diasRestanteGozo == 0) {
            FacesUtil.addOperacaoNaoPermitida("O servidor não possui dias de gozo no período aquisitivo selecionado.");
            return false;
        }

        if (diasRestanteGozo < totalDiasAGozar) {
            FacesUtil.addOperacaoNaoPermitida("Não é possível gozar mais dias do que o permitido.");
            return false;
        }

        if (isOperacaoNovo() && selecionado.getParametroLicencaPremio().getParcela() - programacoesDoPeriodoAquisitivo.size() == 1 && diasRestanteGozo > totalDiasAGozar) {
            FacesUtil.addOperacaoNaoPermitida("Essa Programação usará a última parcela para período de gozo, utilize todos os dias restantes para programar a licença prêmio.");
            return false;
        }
        if (programacaoLicencaPremioFacade.existeOutraProgramacaoNoMesmoPeriodo(selecionado)) {
            FacesUtil.addOperacaoNaoPermitida("Já existe programação para este Período Aquisitivo na data informada.");
            return false;
        }

        return true;
    }

    public boolean validaNovaProgramacaoReferenteAoLimiteDeParcelasPermitidas() {
        try {
            return isOperacaoNovo() && programacoesDoPeriodoAquisitivo.size() == selecionado.getParametroLicencaPremio().getParcela();
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean isParametroVigente() {
        return selecionado.getParametroLicencaPremio() != null;
    }

    private boolean validarDatasInicioFinalGozo() {
        DateTime dtInicioVigencia = new DateTime(selecionado.getInicioVigencia());
        DateTime dtFinalVigencia = null;
        if (selecionado.getFinalVigencia() != null) {
            dtFinalVigencia = new DateTime(selecionado.getFinalVigencia());
        }

        if (!DataUtil.isDataInicioDataFinalValidas(selecionado, programacaoLicencaPremioFacade.recuperaProgramacao(selecionado.getPeriodoAquisitivoFL()), dtInicioVigencia, dtFinalVigencia)) {
            return false;
        }

        return true;
    }

    public void validarDataInicio() {
        if (isPeriodoInformadao()) {
            DateTime dtInicioProgramacao = null;
            DateTime dtFinalAtual = null;
            if (selecionado.getInicioVigencia() != null) {
                dtInicioProgramacao = new DateTime(selecionado.getInicioVigencia());
            }
            if (selecionado.getPeriodoAquisitivoFL().getFinalVigencia() != null) {
                dtFinalAtual = new DateTime(getDataFinalAtual());
            }

            if (dtInicioProgramacao != null && dtFinalAtual != null) {

                if (dtInicioProgramacao.isBefore(dtFinalAtual) || dtInicioProgramacao.isEqual(dtFinalAtual)) {
                    FacesUtil.addOperacaoNaoPermitida("A data inicial da programação deve ser posterior a data " + DataUtil.getDataFormatada(dtFinalAtual.toDate()));
                    selecionado.setInicioVigencia(DataUtil.adicionaDias(dtFinalAtual.toDate(), 1));
                }
            }
            definirFinalVigencia();
        }
    }

    private boolean isPeriodoInformadao() {
        return selecionado.getPeriodoAquisitivoFL() != null;
    }

    private Date getDataFinalAtual() {
        if (programacoesDoPeriodoAquisitivo.isEmpty()) {
            return selecionado.getPeriodoAquisitivoFL().getFinalVigencia();
        }

        Collections.sort(programacoesDoPeriodoAquisitivo, new Comparator<ProgramacaoLicencaPremio>() {
            @Override
            public int compare(ProgramacaoLicencaPremio o1, ProgramacaoLicencaPremio o2) {
                return o1.getId().compareTo(o2.getId()) - 1;
            }
        });

        return programacoesDoPeriodoAquisitivo.get(0).getFimVigencia();
    }

    @Override
    public void excluir() {
        super.excluir();
    }

    public ConverterGenerico getConverterPeriodoAquisitivoFL() {
        if (converterPeriodoAquisitivoFL == null) {
            converterPeriodoAquisitivoFL = new ConverterGenerico(PeriodoAquisitivoFL.class, periodoAquisitivoFLFacade);
        }
        return converterPeriodoAquisitivoFL;
    }

    public List<ContratoFP> completarContratoFP(String parte) {
        return contratoFPFacade.buscarContratoPorTipoPeriodoAquisitivo(parte.trim(), TipoPeriodoAquisitivo.LICENCA);
    }

    public ConverterAutoComplete getConverterContratoFP() {
        if (converterContratoFP == null) {
            converterContratoFP = new ConverterAutoComplete(ContratoFP.class, contratoFPFacade);
        }
        return converterContratoFP;
    }


    public List<AtoLegal> completaAtoLegal(String parte) {
        return atoLegalFacade.listaFiltrandoAtoLegal(parte.trim());
    }

    public ConverterAutoComplete getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, atoLegalFacade);
        }
        return converterAtoLegal;
    }

    public List<SelectItem> periodosAquisitivos() {
        List<SelectItem> lista = new ArrayList<SelectItem>();
        lista.add(new SelectItem(null, ""));
        if (selecionado.getContratoFP() != null) {
            List<PeriodoAquisitivoFL> periodos = periodoAquisitivoFLFacade.buscarPeriodosAquisitivos(selecionado.getContratoFP(), TipoPeriodoAquisitivo.LICENCA, StatusPeriodoAquisitivo.NAO_CONCEDIDO, StatusPeriodoAquisitivo.PARCIAL);
            for (PeriodoAquisitivoFL periodo : periodos) {
                lista.add(new SelectItem(periodo, DataUtil.getDataFormatada(periodo.getInicioVigencia()) + " - " + DataUtil.getDataFormatada(periodo.getFinalVigencia())));
            }
        }
        return lista;
    }

    public boolean validarPeriodoAquisitivo() {
        boolean retorno = false;
        try {
            if (isPeriodoInformadao() && selecionado.getPeriodoAquisitivoFL().getId() != null) {
                diasFalta = faltasFacade.recuperaQuantidadeFaltasPorPeriodo(selecionado.getContratoFP(), selecionado.getPeriodoAquisitivoFL().getInicioVigencia(), selecionado.getPeriodoAquisitivoFL().getFinalVigencia(), TipoFalta.FALTA_INJUSTIFICADA);

                if (isRetardaDiasNaProgramacao(diasFalta, selecionado.getParametroLicencaPremio().getDiasFaltaInjustificada())) {
                    selecionado.setDiasRetardo((int) (diasFalta / selecionado.getParametroLicencaPremio().getDiasFaltaInjustificada()));
                    retorno = false;
                } else {
                    selecionado.setDiasRetardo(0);
                    retorno = true;
                }

                recuperarProgramacoes();
                definirDiasRestanteDeGozo();
                definirDataMinimaGozo();
                definirDataMaximaGozo();
                definirTotalDiasAGozar();
                return retorno;

            } else {
                selecionado.setDiasRetardo(0);
                diasFalta = 0L;
                selecionado.setInicioVigencia(null);
                selecionado.setFinalVigencia(null);
            }
        } catch (Exception e) {
            logger.error("Erro: ", e);
            FacesUtil.addOperacaoNaoRealizada("Não foi possível aplicar a validação do período aquisitivo, detalhes: " + e.getMessage());
        }
        return retorno;
    }

    private boolean isRetardaDiasNaProgramacao(Long diasFalta, Integer diasFaltaInjustificada) {
        if (diasFalta <= 0) {
            return false;
        }
        if (diasFaltaInjustificada <= 0) {
            return false;
        }
        return diasFalta >= diasFaltaInjustificada;
    }

    private void definirDiasRestanteDeGozo() {
        diasRestanteGozo = 0;
        boolean programacaoLicencaPremioIndenizada = false;
        for (ProgramacaoLicencaPremio programacaoLicencaPremio : programacoesDoPeriodoAquisitivo) {
            programacaoLicencaPremioIndenizada = false;
            if (selecionado.getId() == null && programacaoLicencaPremio.getLicencaPremioIndenizada()) {
                diasRestanteGozo = programacaoLicencaPremio.getPeriodoAquisitivoFL().getSaldo();
                programacaoLicencaPremioIndenizada = true;
            } else if (selecionado.getId() == null || !programacaoLicencaPremio.getId().equals(selecionado.getId())) {
                diasRestanteGozo += DataUtil.diasEntre(new DateTime(programacaoLicencaPremio.getInicioVigencia()), new DateTime(programacaoLicencaPremio.getFinalVigencia()));
            }
        }
        logger.debug("diasRestanteGozo {}", diasRestanteGozo);
        if (!programacaoLicencaPremioIndenizada) {
            diasRestanteGozo = selecionado.getParametroLicencaPremio().getDiasGozo() - diasRestanteGozo;
        }
        logger.debug("diasRestanteGozo 1 {}", diasRestanteGozo);
        diasFinalVigencia = diasRestanteGozo;
    }

    public Date definirDataMinimaGozo() {
        if (isPeriodoInformadao() && selecionado.getPeriodoAquisitivoFL().getId() != null) {
            ProgramacaoLicencaPremio p = null;
            if (selecionado.getId() != null) {
                p = programacaoLicencaPremioFacade.recuperaProgramacaoProximaAnterior(selecionado, ProgramacaoLicencaPremio.ProgramacaoDesejada.ANTERIOR);
            } else {
                p = programacaoLicencaPremioFacade.recuperaUltimaProgramacao(selecionado.getPeriodoAquisitivoFL());
            }
            Date dataMinima;

            if (p != null && !p.getLicencaPremioIndenizada()) {
                dataMinima = DataUtil.adicionaDias(p.getFinalVigencia(), 1);
            } else {
                dataMinima = DataUtil.adicionaDias(selecionado.getPeriodoAquisitivoFL().getFinalVigencia(), selecionado.getDiasRetardo() + 1);
            }

            if (selecionado.getId() == null) {
                selecionado.setInicioVigencia(dataMinima);
            }

            return dataMinima;
        }
        return null;
    }

    public Date definirDataMaximaGozo() {
        if (isPeriodoInformadao() && selecionado.getPeriodoAquisitivoFL().getId() != null) {

            ProgramacaoLicencaPremio p = null;
            if (selecionado.getId() != null) {
                p = programacaoLicencaPremioFacade.recuperaProgramacaoProximaAnterior(selecionado, ProgramacaoLicencaPremio.ProgramacaoDesejada.PROXIMA);
            }

            Date dataMaxima = null;

            if (p != null) {
                dataMaxima = DataUtil.adicionaDias(p.getInicioVigencia(), -1);
            } else if (selecionado.getInicioVigencia() != null) {
                dataMaxima = DataUtil.adicionaDias(selecionado.getInicioVigencia(), diasRestanteGozo - 1);
            }

            if (selecionado.getId() == null) {
                if (diasRestanteGozo == diasFinalVigencia) {
                    selecionado.setFinalVigencia(dataMaxima);
                }
            }

            return dataMaxima;
        }
        return null;
    }

    public void recuperarProgramacoes() {
        programacoesDoPeriodoAquisitivo = new ArrayList<>();
        if (isPeriodoInformadao()) {
            programacoesDoPeriodoAquisitivo = programacaoLicencaPremioFacade.recuperaProgramacao(selecionado.getPeriodoAquisitivoFL());
        }
    }

    public Integer getParcelasUsadas() {
        return programacoesDoPeriodoAquisitivo.size();
    }

    public Integer getDiasRestanteCalculado() {
        try {
            return selecionado.getParametroLicencaPremio().getDiasGozo() - getTotalDiasAGozar();
        } catch (NullPointerException npe) {
            return 0;
        }
    }

    public void definirTotalDiasAGozar() {
        if (selecionado.getFinalVigencia() != null && selecionado.getInicioVigencia() != null) {
            totalDiasAGozar = DataUtil.diasEntre(new DateTime(selecionado.getInicioVigencia()), new DateTime(selecionado.getFinalVigencia()));
        }
    }

    public Integer getTotalDiasAGozar() {
        return totalDiasAGozar;
    }

    public void definirFinalVigencia() {
        validarQuantidadeInformada();
        if (selecionado.getInicioVigencia() != null) {
            if (diasFinalVigencia > diasRestanteGozo) {
                diasFinalVigencia = diasRestanteGozo;
            }
            selecionado.setFinalVigencia(DataUtil.adicionaDias(selecionado.getInicioVigencia(), diasFinalVigencia - 1));

            definirTotalDiasAGozar();
        }
    }

    public void definirDataInicio() {
        validarQuantidadeInformada();
        if (selecionado.getFinalVigencia() != null) {
            if (diasFinalVigencia > diasRestanteGozo) {
                diasFinalVigencia = diasRestanteGozo;
            }
            selecionado.setInicioVigencia(DataUtil.removerDias(selecionado.getFinalVigencia(), diasFinalVigencia - 1));

            definirTotalDiasAGozar();
        }
    }

    public Integer getQuantidadeMinimaDeDiasPermitidaParaCadaParcelaConfiguradaNoParametro() {
        if (selecionado.getParametroLicencaPremio() != null) {
            if (selecionado.getParametroLicencaPremio().getQuantidadeMinimaDiasPorParcela() != null) {
                return selecionado.getParametroLicencaPremio().getQuantidadeMinimaDiasPorParcela();
            }
        }
        return 0;
    }

    public void validarQuantidadeInformada() {
        if (diasFinalVigencia < getQuantidadeMinimaDeDiasPermitidaParaCadaParcelaConfiguradaNoParametro()) {
            diasFinalVigencia = getQuantidadeMinimaDeDiasPermitidaParaCadaParcelaConfiguradaNoParametro();
            FacesUtil.addAtencao("A quantidade desejada é inválida, o valor mínimo é de " + getQuantidadeMinimaDeDiasPermitidaParaCadaParcelaConfiguradaNoParametro());
        }
        if (diasFinalVigencia > diasRestanteGozo) {
            FacesUtil.addAtencao("A quantidade desejada é inválida, o valor máximo é de " + diasRestanteGozo);
        }
    }

    public boolean habilitarDiasPecunia() {
        return !selecionado.getLicencaPremioIndenizada();
    }

    public boolean isBloquearDatasVigencia() {
        return bloquearDatasVigencia;
    }

    public void setBloquearDatasVigencia(boolean bloquearDatasVigencia) {
        this.bloquearDatasVigencia = bloquearDatasVigencia;
    }

    public void validarDiasPecunia() {
        if (selecionado.getDiasPecunia() != null && selecionado.getDiasPecunia() <= 0) {
            FacesUtil.addOperacaoNaoPermitida("Uma quantidade de Dias de Pecúnia maior que zero deve ser digitada.");
            selecionado.setDiasPecunia(0);
        }
        if (selecionado.getDiasPecunia() != null && selecionado.getDiasPecunia() > totalDiasAGozar) {
            FacesUtil.addOperacaoNaoPermitida("Quantidade de Dias de Pecúnia maior que o saldo de Dias Restantes a gozar..");
            selecionado.setDiasPecunia(0);
        }
        if (selecionado.getDiasPecunia() != null && selecionado.getDiasPecunia() <= totalDiasAGozar) {
            selecionado.setInicioVigencia(null);
            selecionado.setFinalVigencia(null);
            bloquearDatasVigencia = true;
        }
    }

    public void limparDiasPecuniaAndHabilitarDatas() {
        if (!selecionado.getLicencaPremioIndenizada()) {
            bloquearDatasVigencia = false;
            selecionado.setDiasPecunia(null);
        }
    }
}
