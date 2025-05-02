/*
 * Codigo gerado automaticamente em Fri Oct 28 14:20:16 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.StatusPeriodoAquisitivo;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoPeriodoAquisitivo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;
import org.primefaces.event.DateSelectEvent;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "sugestaoFeriasControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoProgramacaoFerias", pattern = "/programacaoferias/novo/", viewId = "/faces/rh/administracaodepagamento/programacaoferias/edita.xhtml")
})
public class SugestaoFeriasControlador extends PrettyControlador<SugestaoFerias> implements Serializable, CRUD {

    @EJB
    private SugestaoFeriasFacade sugestaoFeriasFacade;
    @EJB
    private PeriodoAquisitivoFLFacade periodoAquisitivoFLFacade;
    private List<SugestaoFerias> listaSugestaoFerias;
    private Converter converterContratoFP;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    private ContratoFP contratoFP;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private Converter converterHierarquiaOrganizacional;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private LotacaoFuncionalFacade lotacaoFuncionalFacade;
    @EJB
    private BasePeriodoAquisitivoFacade basePeriodoAquisitivoFacade;
    private List<ContratoFP> contratoFPs;
    @EJB
    private FaltasFacade faltasFacade;
    private StatusPeriodoAquisitivo situacaoFerias;
    private SugestaoFerias sugestaoFeriasSelecionada;
    @EJB
    private AprovacaoFeriasFacade aprovacaoFeriasFacade;

    public SugestaoFeriasControlador() {
        super(SugestaoFerias.class);
    }

    public StatusPeriodoAquisitivo getSituacaoFerias() {
        return situacaoFerias;
    }

    public void setSituacaoFerias(StatusPeriodoAquisitivo situacaoFerias) {
        this.situacaoFerias = situacaoFerias;
    }

    public List<ContratoFP> getContratoFPs() {
        return contratoFPs;
    }

    public void setContratoFPs(List<ContratoFP> contratoFPs) {
        this.contratoFPs = contratoFPs;
    }

    public SugestaoFeriasFacade getFacade() {
        return sugestaoFeriasFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return sugestaoFeriasFacade;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public SugestaoFeriasFacade getSugestaoFeriasFacade() {
        return sugestaoFeriasFacade;
    }

    public void setSugestaoFeriasFacade(SugestaoFeriasFacade sugestaoFeriasFacade) {
        this.sugestaoFeriasFacade = sugestaoFeriasFacade;
    }

    public PeriodoAquisitivoFLFacade getPeriodoAquisitivoFLFacade() {
        return periodoAquisitivoFLFacade;
    }

    public void setPeriodoAquisitivoFLFacade(PeriodoAquisitivoFLFacade periodoAquisitivoFLFacade) {
        this.periodoAquisitivoFLFacade = periodoAquisitivoFLFacade;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public void popularLista() {
        listaSugestaoFerias = new ArrayList<>();

        try {//gera os períodos
//            if (contratoFP != null) {
//                BasePeriodoAquisitivo basePeriodoAquisitivo = basePeriodoAquisitivoFacade.buscaBasePeriodoAquisitivoPorContratoFP(contratoFP);
//
//                if (basePeriodoAquisitivo != null) {
//                    periodoAquisitivoFLFacade.gerarPeriodosAquisitivos(contratoFP, basePeriodoAquisitivo);
//                    contratoFPFacade.salvar(contratoFP);
//                }
//            } else if (hierarquiaOrganizacional != null) {
//                for (ContratoFP contrato : contratoFPFacade.recuperaContratoMatriculaPorLocalDeTrabalhoRecursiva("", hierarquiaOrganizacional)) {
//                    BasePeriodoAquisitivo basePeriodoAquisitivo = basePeriodoAquisitivoFacade.buscaBasePeriodoAquisitivoPorContratoFP(contrato);
//
//                    if (basePeriodoAquisitivo != null) {
//                        periodoAquisitivoFLFacade.gerarPeriodosAquisitivos(contrato, basePeriodoAquisitivo);
//                        contratoFPFacade.salvar(contrato);
//                    }
//                }
//            }

//            for (PeriodoAquisitivoFL periodo : recuperaPeriodos(hierarquiaOrganizacional, contratoFP)) {
            for (ContratoFP contrato : contratoFPs) {
                for (PeriodoAquisitivoFL periodo : periodoAquisitivoFLFacade.recuperaPeriodoAquisitivoPorContratoFPPorTipoPeriodoAquisitivo(contrato, TipoPeriodoAquisitivo.FERIAS)) {
                    if (situacaoFerias != null && !periodo.getStatus().equals(situacaoFerias)) {
                        continue;
                    }
                    SugestaoFerias sg = new SugestaoFerias();
                    sg = sugestaoFeriasFacade.buscaFiltrandoPeriodoAquisitivoFL(periodo);

                    LotacaoFuncional lotacao = lotacaoFuncionalFacade.recuperarLotacaoFuncionalVigentePorContratoFP(periodo.getContratoFP(), lotacaoFuncionalFacade.getSistemaFacade().getDataOperacao());
                    if (sg == null) {
                        sg = new SugestaoFerias(null, null, Boolean.FALSE, periodo, lotacao.getUnidadeOrganizacional());
                    } else {
                        sg.setUnidadeOrganizacional(lotacao.getUnidadeOrganizacional());
                    }
                    listaSugestaoFerias.add(sg);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Converter getConverterHierarquiaOrganizacional() {
        if (converterHierarquiaOrganizacional == null) {
            converterHierarquiaOrganizacional = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterHierarquiaOrganizacional;
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacional(String parte) {
        return hierarquiaOrganizacionalFacade.filtrandoHierarquiaHorganizacionalTipo(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public void setaHierarquiaOrganizacional(SelectEvent item) {
        hierarquiaOrganizacional = (HierarquiaOrganizacional) item.getObject();
        //populaLista();
    }

    @Override
    public void salvar() {
        if (!validaCampos()) {
            return;
        }

        try {
            for (SugestaoFerias sg : listaSugestaoFerias) {
                if (sg.getPeriodoAquisitivoFL().getStatus().equals(StatusPeriodoAquisitivo.CONCEDIDO)) {
                    continue;
                }
            }
            for (SugestaoFerias sg : listaSugestaoFerias) {
                if (sg.getPeriodoAquisitivoFL().getStatus().equals(StatusPeriodoAquisitivo.CONCEDIDO)) {
                    continue;
                }

                if (sg.getId() != null) {
                    sugestaoFeriasFacade.salvar(sg);
                } else {
                    sugestaoFeriasFacade.salvarNovo(sg);
                }

            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Salvo com Sucesso!", "Salvo com Sucesso"));
            filtrar();

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Exceção do sistema!", e.getMessage()));
        }
    }

    public List<SugestaoFerias> getListaSugestaoFerias() {
        return listaSugestaoFerias;
    }

    public void setListaSugestaoFerias(List<SugestaoFerias> listaSugestaoFerias) {
        this.listaSugestaoFerias = listaSugestaoFerias;
    }

    @URLAction(mappingId = "novoProgramacaoFerias", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoProgramacao() {
        novo();
        situacaoFerias = null;
    }

    @Override
    public void novo() {
        hierarquiaOrganizacional = null;
        contratoFP = null;
        listaSugestaoFerias = new ArrayList<>();
        contratoFPs = new ArrayList<>();
    }

    public Boolean validaCampos() {
        if (listaSugestaoFerias == null || listaSugestaoFerias.isEmpty()) {
            FacesUtil.addWarn(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), "Não há registro para salvar.");
            return false;
        }
        return true;
    }

    public List<ContratoFP> completaContratoFP(String parte) {
        if (hierarquiaOrganizacional == null) {
            return contratoFPFacade.recuperaContratoMatriculaSql(parte.trim());
        } else {
            return contratoFPFacade.recuperaContratoVigenteMatriculaPorLocalDeTrabalhoRecursiva(parte.trim(), hierarquiaOrganizacional, UtilRH.getDataOperacao());
        }
    }

    public void setaServidor(SelectEvent item) {
        contratoFP = (ContratoFP) item.getObject();
    }

    public void filtrar() {
        try {
            if (hierarquiaOrganizacional == null && contratoFP == null) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "É obrigatório informar o 'Local de Trabalho' ou 'Servidor', por favor informe um desses campos.");
                return;
            }

            if (hierarquiaOrganizacional != null && contratoFP == null) {

                contratoFPs = contratoFPFacade.recuperaContratoVigenteMatriculaPorLocalDeTrabalhoRecursivaPelaView(hierarquiaOrganizacional);
            } else {
                contratoFPs = new ArrayList<>();
                contratoFPs.add(contratoFP);
            }
            validarLotacaoFuncional();
            popularLista();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }

//        gerarPeriodosAquisitivos(contratoFPs);

    }

    private void validarLotacaoFuncional() {
        ValidacaoException ve = new ValidacaoException();
        for (ContratoFP contrato : contratoFPs) {
            for (PeriodoAquisitivoFL periodo : periodoAquisitivoFLFacade.recuperaPeriodoAquisitivoPorContratoFPPorTipoPeriodoAquisitivo(contrato, TipoPeriodoAquisitivo.FERIAS)) {
                LotacaoFuncional lotacao = lotacaoFuncionalFacade.recuperarLotacaoFuncionalVigentePorContratoFP(periodo.getContratoFP(), lotacaoFuncionalFacade.getSistemaFacade().getDataOperacao());
                if (lotacao == null) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O servidor " + periodo.getContratoFP() + " não possui Lotação Funcional vigente.");
                    ve.lancarException();
                }
            }
        }
    }

    public void gerarPeriodosAquisitivos(List<ContratoFP> contratoFPs) {
        for (ContratoFP contrato : contratoFPs) {
            try {
                BasePeriodoAquisitivo basePeriodoAquisitivoFerias = basePeriodoAquisitivoFacade.buscaBasePeriodoAquisitivoFeriasPorContratoFP(contrato);
                if (basePeriodoAquisitivoFerias != null) {
                    periodoAquisitivoFLFacade.gerarPeriodosAquisitivos(contrato, lotacaoFuncionalFacade.getSistemaFacade().getDataOperacao(), null);
                }
                BasePeriodoAquisitivo basePeriodoAquisitivoLicenca = basePeriodoAquisitivoFacade.buscaBasePeriodoAquisitivoLicencaPremioPorContratoFP(contrato);
                if (basePeriodoAquisitivoLicenca != null) {
                    periodoAquisitivoFLFacade.gerarPeriodosAquisitivos(contrato, lotacaoFuncionalFacade.getSistemaFacade().getDataOperacao(), null);
                }
            } catch (ExcecaoNegocioGenerica eng) {
                FacesUtil.addOperacaoNaoRealizada(eng.getMessage());
                break;
            }
        }
    }

    public Converter getConverterContratoFP() {
        if (converterContratoFP == null) {
            converterContratoFP = new ConverterAutoComplete(ContratoFP.class, contratoFPFacade);
        }
        return converterContratoFP;
    }

    public List<PeriodoAquisitivoFL> recuperaPeriodos(HierarquiaOrganizacional hierarquia, ContratoFP contratoFP) {
        if (hierarquia != null && contratoFP != null) {
            return periodoAquisitivoFLFacade.recuperaPeriodoAquisitivoPorHierarquiaOrganizacionalEContratoFP(hierarquia, contratoFP);
        } else if (hierarquia != null) {
            return periodoAquisitivoFLFacade.recuperaPeriodoAquisitivoPorHierarquiaOrganizacional(hierarquia);
        } else if (contratoFP != null) {
            return periodoAquisitivoFLFacade.recuperaPeriodoAquisitivoPorContratoFP(contratoFP);
        }
        return new ArrayList<>();
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public void zerarDiasAbono() {
        this.sugestaoFeriasSelecionada.setDiasAbono(null);
    }

    public void validaDataVigencia(SugestaoFerias sugestao) {
        if (sugestao != null) {
            if (sugestao.getDataInicio() != null && sugestao.getDataFim() != null) {
                if (sugestao.getDataInicio().before(sugestao.getDataFim())) {
                    Integer totalSaldoDireito = sugestao.getPeriodoAquisitivoFL().getSaldoDeDireito();
                    if (sugestao.getAbonoPecunia() != null && sugestao.getAbonoPecunia()) {
                        if (sugestao.getDiasAbono() != null && (sugestao.getDiasAbono()) > ((totalSaldoDireito / 3))) {
                            sugestao.setDiasAbono(((totalSaldoDireito / 3)));
                            FacesUtil.addWarn("Atenção!", "A Quantidade de dias de abono não pode ser maior que um terço dos dias de férias!");
                        }
                    }
                } else {
                    FacesUtil.addWarn("Atenção!", "A data Inicial deve ser menor que a data final");
                }
            } else {
                FacesUtil.addWarn("Atenção!", "Existem campos obrigatórios sem preechimento.");
            }
        }

    }

    public boolean validaVigenciaLista(Date inicio, Date fim, SugestaoFerias suge) {

        if (inicio == null) {
            FacesUtil.addWarn("Atenção!", "A data inicial da férias é obrigatória.");
            return false;
        }
        if (fim == null) {
            FacesUtil.addWarn("Atenção!", "A data final da férias é obrigatória.");
            return false;
        }
        if (inicio.after(fim)) {
            FacesUtil.addWarn("Atenção!", "A data Inicial deve ser anterior à data final.");
            return false;
        }
        //Integer total = DataUtil.diferencaDiasInteira(suge.getDataInicio(), suge.getDataFim()) + 1;
        Integer total = DataUtil.diferencaDiasInteira(suge.getDataInicio(), suge.getDataFim());
        Integer totalSaldoDireito = suge.getPeriodoAquisitivoFL().getSaldoDeDireito() != null ? suge.getPeriodoAquisitivoFL().getSaldoDeDireito() : 0;
        if (suge.getAbonoPecunia() != null && suge.getAbonoPecunia() && suge.getDiasAbono() != null) {
            total += suge.getDiasAbono();
        }

        if (total > totalSaldoDireito && totalSaldoDireito > 0) {

            FacesUtil.addWarn("Atenção!", "A Quantidade de dias de férias não pode ser maior que " + totalSaldoDireito + " dias.");
            return false;
        }
        if (suge.getAbonoPecunia() != null && suge.getAbonoPecunia()) {
            if (suge.getDiasAbono() != null && (suge.getDiasAbono()) > (totalSaldoDireito / 3)) {
                FacesUtil.addWarn("Atenção!", "A Quantidade de dias de abono não pode ser maior que um terço dos dias de férias.");
                return false;
            }
        }
        return true;

    }

    public void calculaPeriodoFerias(SugestaoFerias sugestao) {
        if (sugestao != null) {
            if (sugestao.getDataInicio() != null) {
                setaDiasFerias(sugestao);
                if (sugestao.getDataFim() != null) {
                    validaDataVigencia(sugestao);
                }
            }
        }
    }

    private void setaDiasFerias(SugestaoFerias sugestao) {
        Integer diasDeFerias = sugestao.getPeriodoAquisitivoFL().getSaldo();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sugestao.getDataInicio());
        if (diasDeFerias != null && diasDeFerias > 0) {
            calendar.add(Calendar.DAY_OF_MONTH, diasDeFerias - 1);
            sugestao.setDataFim(calendar.getTime());
        } else {
            sugestao.setDataFim(null);
            FacesUtil.addWarn("Atençao!", "Não possui período aquisitivo disponível.");
        }
    }

    public SugestaoFerias getSugestaoFeriasSelecionada() {
        return sugestaoFeriasSelecionada;
    }

    public void setSugestaoFeriasSelecionada(SugestaoFerias sugestaoFeriasSelecionada) {
        this.sugestaoFeriasSelecionada = sugestaoFeriasSelecionada;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/programacaoferias/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public boolean validarCamposObrigatorios() {
        boolean validou = true;

        if (sugestaoFeriasSelecionada.getDataInicio() == null) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Por favor, informe o campo 'Data Inicial'.");
            validou = false;
        }

        if (sugestaoFeriasSelecionada.getDataFim() == null) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Por favor, informe o campo 'Data Final'.");
            validou = false;
        }

        if (sugestaoFeriasSelecionada.getAbonoPecunia() != null && sugestaoFeriasSelecionada.getAbonoPecunia()) {
            if (sugestaoFeriasSelecionada.getDiasAbono() == null) {
                FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Por favor, informe o campo 'Dias de Abono'.");
                validou = false;
            }
            if (sugestaoFeriasSelecionada.getDiasAbono() != null && sugestaoFeriasSelecionada.getDiasAbono() <= 0) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "O campo 'Dias de Abono' deve ser um número válido e positivo.");
                validou = false;
            }
        }

        return validou;
    }

    public boolean validarProgramacao() {
        boolean validou = true;

        if (sugestaoFeriasSelecionada.getDataInicio() != null && sugestaoFeriasSelecionada.getDataFim() != null && sugestaoFeriasSelecionada.getDataInicio().after(sugestaoFeriasSelecionada.getDataFim())) {
            FacesUtil.addWarn(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "A 'Data Final' deve ser posterior a 'Data Inicial.'");
            validou = false;
        }

        if (sugestaoFeriasSelecionada.getAbonoPecunia() != null && sugestaoFeriasSelecionada.getAbonoPecunia()) {
            if (sugestaoFeriasSelecionada.getDiasAbono() != null && sugestaoFeriasSelecionada.getDiasAbono() <= 0) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "O campo 'Dias de Abono' deve ser um número válido e positivo.");
                validou = false;
            }
        }

        Integer total = DataUtil.diferencaDiasInteira(sugestaoFeriasSelecionada.getDataInicio(), sugestaoFeriasSelecionada.getDataFim());
        total++;
        Integer totalSaldoDireito = sugestaoFeriasSelecionada.getPeriodoAquisitivoFL().getSaldoDeDireito();
        if (sugestaoFeriasSelecionada.getAbonoPecunia() != null && sugestaoFeriasSelecionada.getAbonoPecunia() && sugestaoFeriasSelecionada.getDiasAbono() != null) {
            total += sugestaoFeriasSelecionada.getDiasAbono();
        }

        if (total > sugestaoFeriasSelecionada.getPeriodoAquisitivoFL().getSaldoDeDireito()) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "A quantidade de dias de ferias deve ser menor que o saldo de direito (" + sugestaoFeriasSelecionada.getPeriodoAquisitivoFL().getSaldoDeDireito() + " dias).");
            validou = false;
        }

        if (sugestaoFeriasSelecionada.getAbonoPecunia() != null && sugestaoFeriasSelecionada.getAbonoPecunia()) {
            if (sugestaoFeriasSelecionada.getDiasAbono() != null && (sugestaoFeriasSelecionada.getDiasAbono()) > (totalSaldoDireito / 3)) {
                FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "A quantidade de dias de abono deve ser menor que um terço dos dias de férias (" + totalSaldoDireito / 3 + " dias).");
                validou = false;
            }
        }

        return validou;
    }

    public void atribuirDataFinalFromChange(AjaxBehaviorEvent event) {
        Date data = (Date) event.getComponent().getAttributes().get("value");
        atribuirDataFimSugestaoFerias(data);
    }

    public void atribuirDataFinalFromDateSelect(DateSelectEvent event) {
        Date data = event.getDate();
        atribuirDataFimSugestaoFerias(data);
    }

    public void atribuirDataFimSugestaoFerias(Date data) {
        try {
            sugestaoFeriasSelecionada.setDataFim(DataUtil.adicionaDias(data, sugestaoFeriasSelecionada.getPeriodoAquisitivoFL().getSaldoDeDireito() - getUmPorDesconsiderarPrimeiroDia()));
        } catch (NullPointerException npe) {
            sugestaoFeriasSelecionada.setDataFim(null);
        }
    }

    public Integer getUmPorDesconsiderarPrimeiroDia() {
        return Integer.valueOf(1);
    }

    public void confirmarProgramacao() {
        if (!validarCamposObrigatorios()) {
            return;
        }

        if (!validarProgramacao()) {
            return;
        }

        sugestaoFeriasSelecionada.setAbonoPecunia(sugestaoFeriasSelecionada.getDiasAbono() == null ? false : true);


        RequestContext.getCurrentInstance().execute("programacaoFerias.hide()");
        RequestContext.getCurrentInstance().update("Formulario:tabelaFerias");
        atualizarSugestaoAtualNaLista();
        FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoSalvar());
    }

    private void atualizarSugestaoAtualNaLista() {
        Integer index = listaSugestaoFerias.indexOf(sugestaoFeriasSelecionada);
        sugestaoFeriasSelecionada = sugestaoFeriasFacade.merge(sugestaoFeriasSelecionada);
        listaSugestaoFerias.set(index, sugestaoFeriasSelecionada);
    }

    public void selecionarSugestaoFerias(ActionEvent ev) {
        sugestaoFeriasSelecionada = (SugestaoFerias) ev.getComponent().getAttributes().get("sugestao");
        if (sugestaoNaoTemSaldoDeDireitoAndNaoEstaAprovada()) {
            FacesUtil.addAtencao("Não será possível alterar esta programação de férias pois ela não possui saldo de direito.");
            return;
        }
        if (sugestaoFeriasSelecionada.estaAprovada()) {
            FacesUtil.addAtencao("Não será possível alterar esta programação de férias pois ela já foi aprovada.");
            return;
        }
        FacesUtil.executaJavaScript("programacaoFerias.show()");
    }

    public boolean sugestaoNaoTemSaldoDeDireitoAndNaoEstaAprovada() {
        return !sugestaoFeriasSelecionada.getPeriodoAquisitivoFL().temSaldoDeDireito() && !sugestaoFeriasSelecionada.estaAprovada();
    }

    public void removerProgramacaoDeFerias(ActionEvent ev) {
        sugestaoFeriasSelecionada = (SugestaoFerias) ev.getComponent().getAttributes().get("sugestao");
        if (sugestaoFeriasSelecionada.getDataInicio() == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Não há programação de férias para este período aquisitivo.");
            return;
        }

        if (sugestaoFeriasSelecionada.getAprovacaoFerias() == null) {
            FacesUtil.addOperacaoNaoRealizada("Não há aprovação de férias para ser removida.");
            return;
        }

        if (sugestaoFeriasSelecionada.getAprovacaoFerias() != null && sugestaoFeriasSelecionada.getAprovacaoFerias().getAprovado()) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Não é possível limpar (remover) a programação de férias deste servidor pois a mesma já foi aprovada.");
            return;
        }

        if (sugestaoFeriasSelecionada.getPeriodoAquisitivoFL().getStatus().equals(StatusPeriodoAquisitivo.CONCEDIDO)) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Não é possível limpar (remover) a programação de férias deste servidor pois o período aquisitivo já foi concedido.");
            return;
        }
        sugestaoFeriasSelecionada.setDiasAbono(null);
        sugestaoFeriasSelecionada.setDataInicio(null);
        sugestaoFeriasSelecionada.setDataFim(null);
        sugestaoFeriasSelecionada.setAbonoPecunia(Boolean.FALSE);
        atualizarSugestaoAtualNaLista();
        FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), "A programação de férias foi removida.");
    }

    public void aprovarSugestaoFerias(ActionEvent ev) {
        sugestaoFeriasSelecionada = (SugestaoFerias) ev.getComponent().getAttributes().get("sugestao");
        try {
            sugestaoFeriasSelecionada = aprovacaoFeriasFacade.aprovar(sugestaoFeriasSelecionada);
            atualizarSugestaoAtualNaLista();
        } catch (ExcecaoNegocioGenerica eng) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), eng.getMessage());
            return;
        }
    }

    public void removerAprovacaoDeFerias(ActionEvent ev) {
        sugestaoFeriasSelecionada = (SugestaoFerias) ev.getComponent().getAttributes().get("sugestao");
        if (sugestaoFeriasSelecionada.getAprovacaoFerias() == null || sugestaoFeriasSelecionada.getAprovacaoFerias().getDataAprovacao() == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), "Não é possível 'Desaprovar' férias que ainda não foram aprovadas.");
            return;
        }
        if (sugestaoFeriasSelecionada.getPeriodoAquisitivoFL().getStatus().equals(StatusPeriodoAquisitivo.CONCEDIDO) || sugestaoFeriasSelecionada.getPeriodoAquisitivoFL().getStatus().equals(StatusPeriodoAquisitivo.PARCIAL)) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Não é possível remover a aprovação de férias já concedidas/concedida parcialmente.");
            return;
        }
        AprovacaoFerias af = sugestaoFeriasSelecionada.getAprovacaoFerias();
        af.setDataAprovacao(null);
        af.setAprovado(Boolean.FALSE);
        sugestaoFeriasSelecionada.setListAprovacaoFerias(Util.adicionarObjetoEmLista(sugestaoFeriasSelecionada.getListAprovacaoFerias(), af));
        atualizarSugestaoAtualNaLista();
    }
}
