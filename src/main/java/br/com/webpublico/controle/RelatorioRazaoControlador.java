/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.relatoriofacade.RelatorioRazaoFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author reidocrime
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-razao-contabil", pattern = "/relatorio/razao-contabil", viewId = "/faces/financeiro/relatorio/relatoriorazao.xhtml")
})
@ManagedBean
public class RelatorioRazaoControlador extends AbstractReport implements Serializable {

    @EJB
    private RelatorioRazaoFacade relatorioRazaoFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private List<HierarquiaOrganizacional> unidades;
    private ConverterAutoComplete converterConta;
    private Date dtInicial;
    private Date dtFinal;
    private Conta contaInicio;
    private Conta contaFim;
    private Boolean mostraSaldoDiario;
    private Boolean quebraPaginaMes;
    private UnidadeGestora unidadeGestora;
    private TipoBalancete tipoInicial;
    private TipoBalancete tipoFinal;
    private String filtro;
    private List<String> listaErros;
    @Enumerated(EnumType.STRING)
    private EsferaDoPoder esferaDoPoder;
    @Enumerated(EnumType.STRING)
    private TipoAdministracao tipoAdministracao;
    @Enumerated(EnumType.STRING)
    private TipoEntidade natureza;
    @Enumerated(EnumType.STRING)
    private ApresentacaoRelatorio apresentacao;
    @Enumerated(EnumType.STRING)
    private TipoEventoContabil tipoEventoContabil;

    private List<String> buscarTipoBalanceteInicial() {
        List<String> toReturn = new ArrayList<>();
        switch (tipoInicial.name()) {
            case "TRANSPORTE":
                toReturn.add(TipoBalancete.TRANSPORTE.name());
                break;
            case "ABERTURA":
                toReturn.add(TipoBalancete.TRANSPORTE.name());
                break;
            case "MENSAL":
                toReturn.add(TipoBalancete.TRANSPORTE.name());
                toReturn.add(TipoBalancete.ABERTURA.name());
                toReturn.add(TipoBalancete.MENSAL.name());
                break;
            case "APURACAO":
                toReturn.add(TipoBalancete.TRANSPORTE.name());
                toReturn.add(TipoBalancete.ABERTURA.name());
                toReturn.add(TipoBalancete.MENSAL.name());
                break;
            case "ENCERRAMENTO":
                toReturn.add(TipoBalancete.TRANSPORTE.name());
                toReturn.add(TipoBalancete.ABERTURA.name());
                toReturn.add(TipoBalancete.MENSAL.name());
                toReturn.add(TipoBalancete.APURACAO.name());
                break;
        }
        return toReturn;
    }

    private List<String> buscarTipoBalanceteFinal() {
        List<String> toReturn = new ArrayList<>();
        switch (tipoFinal.name()) {
            case "TRANSPORTE":
                toReturn.add(TipoBalancete.TRANSPORTE.name());
                break;
            case "ABERTURA":
                toReturn.add(TipoBalancete.ABERTURA.name());
                break;
            case "MENSAL":
                toReturn.add(TipoBalancete.MENSAL.name());
                break;
            case "APURACAO":
                if (tipoInicial.equals(TipoBalancete.APURACAO)) {
                    toReturn.add(TipoBalancete.APURACAO.name());
                } else {
                    toReturn.add(TipoBalancete.MENSAL.name());
                    toReturn.add(TipoBalancete.APURACAO.name());
                }
                break;
            case "ENCERRAMENTO":
                if (tipoInicial.equals(TipoBalancete.ENCERRAMENTO)) {
                    toReturn.add(TipoBalancete.ENCERRAMENTO.name());
                } else if (tipoInicial.equals(TipoBalancete.APURACAO)) {
                    toReturn.add(TipoBalancete.APURACAO.name());
                    toReturn.add(TipoBalancete.ENCERRAMENTO.name());
                } else {
                    toReturn.add(TipoBalancete.MENSAL.name());
                    toReturn.add(TipoBalancete.APURACAO.name());
                    toReturn.add(TipoBalancete.ENCERRAMENTO.name());
                }
                break;
        }
        return toReturn;
    }

    public List<SelectItem> getApresentacoes() {
        return Util.getListSelectItemSemCampoVazio(ApresentacaoRelatorio.values());
    }

    public List<SelectItem> getListaTipoEntidade() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoEntidade te : TipoEntidade.values()) {
            toReturn.add(new SelectItem(te, te.getTipo()));
        }
        return toReturn;
    }

    public List<SelectItem> getListaTipoAdministracao() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoAdministracao ta : TipoAdministracao.values()) {
            toReturn.add(new SelectItem(ta, ta.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getListaTipoEvento() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoEventoContabil te : TipoEventoContabil.values()) {
            toReturn.add(new SelectItem(te, te.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getListaEsferaDoPoder() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (EsferaDoPoder edp : EsferaDoPoder.values()) {
            toReturn.add(new SelectItem(edp, edp.getDescricao()));
        }
        return toReturn;
    }

    @URLAction(mappingId = "relatorio-razao-contabil", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        this.dtInicial = sistemaControlador.getDataOperacao();
        this.dtFinal = sistemaControlador.getDataOperacao();
        this.listaErros = new ArrayList<>();
        this.contaInicio = null;
        this.contaFim = null;
        this.unidades = new ArrayList<>();
        this.quebraPaginaMes = true;
        this.esferaDoPoder = null;
        this.tipoAdministracao = null;
        this.natureza = null;
        this.mostraSaldoDiario = false;
        this.apresentacao = null;
    }

    public List<Conta> completarContas(String parte) {
        return relatorioRazaoFacade.getContaFacade().listaContasContabeis(parte, getExercicioDaDataFinal());
    }

    public List<SelectItem> getListaTipoBalanceteInicial() {
        List<SelectItem> toReturn = new ArrayList();
        toReturn.add(new SelectItem(TipoBalancete.MENSAL, TipoBalancete.MENSAL.getDescricao()));
        if (getMesDaData(dtInicial).equals("01")) {
            toReturn.add(new SelectItem(TipoBalancete.ABERTURA, TipoBalancete.ABERTURA.getDescricao()));
            toReturn.add(new SelectItem(TipoBalancete.TRANSPORTE, TipoBalancete.TRANSPORTE.getDescricao()));
        } else if (getMesDaData(dtInicial).equals("12")) {
            toReturn.add(new SelectItem(TipoBalancete.ENCERRAMENTO, TipoBalancete.ENCERRAMENTO.getDescricao()));
            toReturn.add(new SelectItem(TipoBalancete.APURACAO, TipoBalancete.APURACAO.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getListaTipoBalanceteFinal() {
        List<SelectItem> toReturn = new ArrayList();
        toReturn.add(new SelectItem(TipoBalancete.MENSAL, TipoBalancete.MENSAL.getDescricao()));
        if (getMesDaData(dtFinal).equals("01")) {
            toReturn.add(new SelectItem(TipoBalancete.ABERTURA, TipoBalancete.ABERTURA.getDescricao()));
            toReturn.add(new SelectItem(TipoBalancete.TRANSPORTE, TipoBalancete.TRANSPORTE.getDescricao()));
        } else if (getMesDaData(dtFinal).equals("12")) {
            toReturn.add(new SelectItem(TipoBalancete.ENCERRAMENTO, TipoBalancete.ENCERRAMENTO.getDescricao()));
            toReturn.add(new SelectItem(TipoBalancete.APURACAO, TipoBalancete.APURACAO.getDescricao()));
        }
        return toReturn;
    }

    private String getMesDaData(Date data) {
        return String.valueOf(Util.getMesDaData(data));
    }

    public void limparContasAndUnidades() {
        contaInicio = null;
        contaFim = null;
        unidades = Lists.newArrayList();
        unidadeGestora = null;
    }

    public Exercicio getExercicioDaDataFinal() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        return getExercicioFacade().getExercicioPorAno(Integer.valueOf(format.format(dtFinal)));
    }

    private List<ParametrosRelatorios> buscarParametros() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();

        parametros.add(new ParametrosRelatorios(null, ":DATAEXERCICIO", null, OperacaoRelatorio.MAIOR_IGUAL, "01/01/" + getExercicioDaDataFinal().getAno(), null, 0, true));
        parametros.add(new ParametrosRelatorios(null, ":DATAEXERCICIO", null, OperacaoRelatorio.MAIOR_IGUAL, "01/01/" + getExercicioDaDataFinal().getAno(), null, 3, true));
        parametros.add(new ParametrosRelatorios(null, ":DataInicial", ":DataFinal", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(dtInicial), DataUtil.getDataFormatada(dtFinal), 2, true));
        parametros.add(new ParametrosRelatorios(null, ":tipoBalanceteFinal", null, OperacaoRelatorio.IN, buscarTipoBalanceteFinal(), null, 2, false));
        parametros.add(new ParametrosRelatorios(null, ":tipoBalanceteInicial", null, OperacaoRelatorio.IN, buscarTipoBalanceteInicial(), null, 3, false));
        parametros.add(new ParametrosRelatorios(null, ":DataFinal", null, OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(dtFinal), null, 4, true));
        parametros.add(new ParametrosRelatorios(null, ":DataInicial", null, OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(dtInicial), null, 6, true));

        parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, getExercicioDaDataFinal().getId(), null, 5, false));

        parametros.add(new ParametrosRelatorios(null, ":tipoBalanceteInicial", null, OperacaoRelatorio.IN, buscarTipoBalanceteInicial(), null, 0, false));
        parametros.add(new ParametrosRelatorios(null, ":tipoBalanceteFinal", null, OperacaoRelatorio.IN, buscarTipoBalanceteFinal(), null, 0, false));

        if (this.unidades.size() > 0) {
            List<Long> listaIdsUnidades = new ArrayList<>();
            String codigoUnidades = "";
            for (HierarquiaOrganizacional lista : unidades) {
                listaIdsUnidades.add(lista.getSubordinada().getId());
                codigoUnidades += " " + lista.getCodigo().substring(3, 10) + " -";
            }
            filtro += " Unidade(s): " + codigoUnidades;
            parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, listaIdsUnidades, null, 1, false));
        } else if (this.unidadeGestora == null) {
            List<HierarquiaOrganizacional> listaUndsUsuarios = new ArrayList<>();
            List<Long> listaIdsUnidades = new ArrayList<>();
            listaUndsUsuarios = relatorioRazaoFacade.getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorNivel("", sistemaControlador.getUsuarioCorrente(), getExercicioDaDataFinal(), dtFinal, TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            for (HierarquiaOrganizacional lista : listaUndsUsuarios) {
                listaIdsUnidades.add(lista.getSubordinada().getId());
            }
            if (listaIdsUnidades.size() != 0) {
                parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, listaIdsUnidades, null, 1, false));
            }
        }

        if (contaInicio != null && contaFim != null) {
            parametros.add(new ParametrosRelatorios(" c.codigo ", ":ContaInicial", ":ContaFinal", OperacaoRelatorio.BETWEEN, contaInicio.getCodigo(), contaFim.getCodigo(), 1, false));
        }

        if (this.unidadeGestora != null) {
            parametros.add(new ParametrosRelatorios(" ug.codigo ", ":ugCodigo", null, OperacaoRelatorio.IGUAL, this.unidadeGestora.getCodigo(), null, 1, false));
            filtro += " Unidade Gestora: " + this.unidadeGestora.getCodigo() + " -";
        }
        if (tipoEventoContabil != null) {
            parametros.add(new ParametrosRelatorios(" evento.TIPOEVENTOCONTABIL ", ":tipoEvento", null, OperacaoRelatorio.LIKE, this.tipoEventoContabil.name(), null, 2, false));
            filtro += " Tipo de Evento: " + this.tipoEventoContabil.getDescricao() + " -";
        }

        if (filtro.length() > 1) {
            filtro = filtro.substring(0, filtro.length() - 1);
        }
        return parametros;
    }

    private void validarDatas() {
        ValidacaoException ve = new ValidacaoException();
        if (dtInicial == null || dtFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor informar um intervalo de datas");
        }
        ve.lancarException();
        if (dtInicial.after(dtFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Data Inicial não pode ser maior que a Data Final");
        }
        SimpleDateFormat formato = new SimpleDateFormat("yyyy");
        if (formato.format(dtInicial).compareTo(formato.format(dtFinal)) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("As datas estão com exercícios diferentes");
        }
        ve.lancarException();
    }

    private void validarContaContabil() {
        ValidacaoException ve = new ValidacaoException();
        if (contaFim != null && contaInicio != null) {
            if (contaFim.getCodigo() != null && contaInicio.getCodigo() != null &&
                contaFim.getCodigo().compareTo(contaInicio.getCodigo()) < 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Conta Inicial deve ser menor ou igual a Conta Final");
            }
        } else if (contaInicio == null && contaFim != null) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor Informar uma Conta Inicial");
        } else if (contaInicio != null && contaFim == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Favor Informar uma Conta Final");
        }
        ve.lancarException();
    }

    public void gerarRelatorio() {
        try {
            filtro = "";
            validarDatas();
            validarContaContabil();
            RelatorioDTO dto = montarRelatorioDto();
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            filtro = "";
            validarDatas();
            validarContaContabil();
            RelatorioDTO dto = montarRelatorioDto();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            ReportService.getInstance().gerarRelatorio(sistemaControlador.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório: ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private RelatorioDTO montarRelatorioDto() {
        ConfiguracaoCabecalho configuracaoCabecalho = relatorioRazaoFacade.getConfiguracaoCabecalhoFacade().buscarConfiguracaoCabecalhoPorUnidade(sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
        RelatorioDTO dto = new RelatorioDTO();
        dto.adicionarParametro("USER", sistemaControlador.getUsuarioCorrente().getNome(), false);
        dto.setNomeParametroSubreportDir("SUBREPORT_DIR");
        dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(buscarParametros()));
        dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
        dto.adicionarParametro("dataInicial", DataUtil.getDataFormatada(dtInicial));
        dto.adicionarParametro("MUNICIPIO", configuracaoCabecalho != null ? configuracaoCabecalho.getTitulo() : "Município de Rio Branco - AC");
        dto.adicionarParametro("DATAINICIAL", DataUtil.getDataFormatada(dtInicial) + " (" + tipoInicial.getDescricao() + ")");
        dto.adicionarParametro("DATAFINAL", DataUtil.getDataFormatada(dtFinal) + " (" + tipoFinal.getDescricao() + ")");
        dto.adicionarParametro("FILTRO", filtro);
        dto.adicionarParametro("APRESENTACAO", apresentacao.getToDto());
        dto.adicionarParametro("ANO", getExercicioDaDataFinal().getAno());
        dto.adicionarParametro("tipoInicial", tipoInicial.getToDto());
        dto.adicionarParametro("tipoFinal", tipoFinal.getToDto());
        dto.adicionarParametro("SALDO_DIARIO", mostraSaldoDiario);
        dto.setNomeRelatorio(getNomeRelatorio());
        dto.setApi("contabil/razao/");
        return dto;
    }

    private String getNomeRelatorio() {
        String nome = "";
        if (apresentacao.isApresentacaoConsolidado()) {
            nome = "RAZAO-CONTABIL" + "-" + DataUtil.getDataFormatada(dtInicial, "ddMMyyyy") + "-" + DataUtil.getDataFormatada(dtFinal, "ddMMyyyy");
        } else if (apresentacao.isApresentacaoUnidade()) {
            nome = "RAZAO-CONTABIL-UNIDADE" + "-" + DataUtil.getDataFormatada(dtInicial, "ddMMyyyy") + "-" + DataUtil.getDataFormatada(dtFinal, "ddMMyyyy");
        } else if (apresentacao.isApresentacaoOrgao()) {
            nome = "RAZAO-CONTABIL-ORGAO" + "-" + DataUtil.getDataFormatada(dtInicial, "ddMMyyyy") + "-" + DataUtil.getDataFormatada(dtFinal, "ddMMyyyy");
        } else {
            nome = "RAZAO-CONTABIL-UNIDADEGESTORA" + "-" + DataUtil.getDataFormatada(dtInicial, "ddMMyyyy") + "-" + DataUtil.getDataFormatada(dtFinal, "ddMMyyyy");
        }
        return nome;
    }

    public ConverterAutoComplete getConverterContaContabil() {
        if (converterConta == null) {
            converterConta = new ConverterAutoComplete(Conta.class, relatorioRazaoFacade.getContaFacade());
        }
        return converterConta;
    }

    public Conta getContaFim() {
        return contaFim;
    }

    public void setContaFim(Conta contaFim) {
        this.contaFim = contaFim;
    }

    public Conta getContaInicio() {
        return contaInicio;
    }

    public void setContaInicio(Conta contaInicio) {
        this.contaInicio = contaInicio;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public Date getDtFinal() {
        return dtFinal;
    }

    public void setDtFinal(Date dtFinal) {
        this.dtFinal = dtFinal;
    }

    public Date getDtInicial() {
        return dtInicial;
    }

    public void setDtInicial(Date dtInicial) {
        this.dtInicial = dtInicial;
    }

    public Boolean getMostraSaldoDiario() {
        return mostraSaldoDiario;
    }

    public void setMostraSaldoDiario(Boolean mostraSaldoDiario) {
        this.mostraSaldoDiario = mostraSaldoDiario;
    }

    public Boolean getQuebraPaginaMes() {
        return quebraPaginaMes;
    }

    public void setQuebraPaginaMes(Boolean quebraPaginaMes) {
        this.quebraPaginaMes = quebraPaginaMes;
    }

    public TipoEntidade getNatureza() {
        return natureza;
    }

    public void setNatureza(TipoEntidade natureza) {
        this.natureza = natureza;
    }

    public TipoAdministracao getTipoAdministracao() {
        return tipoAdministracao;
    }

    public void setTipoAdministracao(TipoAdministracao tipoAdministracao) {
        this.tipoAdministracao = tipoAdministracao;
    }

    public EsferaDoPoder getEsferaDoPoder() {
        return esferaDoPoder;
    }

    public void setEsferaDoPoder(EsferaDoPoder esferaDoPoder) {
        this.esferaDoPoder = esferaDoPoder;
    }

    public List<HierarquiaOrganizacional> getUnidades() {
        return unidades;
    }

    public void setUnidades(List<HierarquiaOrganizacional> unidades) {
        this.unidades = unidades;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public ApresentacaoRelatorio getApresentacao() {
        return apresentacao;
    }

    public void setApresentacao(ApresentacaoRelatorio apresentacao) {
        this.apresentacao = apresentacao;
    }

    public UnidadeGestora getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(UnidadeGestora unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public TipoEventoContabil getTipoEventoContabil() {
        return tipoEventoContabil;
    }

    public void setTipoEventoContabil(TipoEventoContabil tipoEventoContabil) {
        this.tipoEventoContabil = tipoEventoContabil;
    }

    public TipoBalancete getTipoInicial() {
        return tipoInicial;
    }

    public void setTipoInicial(TipoBalancete tipoInicial) {
        this.tipoInicial = tipoInicial;
    }

    public TipoBalancete getTipoFinal() {
        return tipoFinal;
    }

    public void setTipoFinal(TipoBalancete tipoFinal) {
        this.tipoFinal = tipoFinal;
    }

}
