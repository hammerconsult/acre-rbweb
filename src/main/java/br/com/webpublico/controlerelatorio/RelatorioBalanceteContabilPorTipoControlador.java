/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ContaFacade;
import br.com.webpublico.relatoriofacade.RelatorioBalanceteContabilPorTipoFacade;
import br.com.webpublico.report.ReportService;
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
 * @author wiplash
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-balancete-contabil-por-tipo", pattern = "/relatorio/balancete-contabil-por-tipo/", viewId = "/faces/financeiro/relatorio/relatoriobalancetecontabilportipo.xhtml")
})
@ManagedBean
public class RelatorioBalanceteContabilPorTipoControlador extends RelatorioContabilSuperControlador implements Serializable {

    @EJB
    private ContaFacade contaFacade;
    @EJB
    private RelatorioBalanceteContabilPorTipoFacade relatorioBalanceteContabilPorTipoFacade;
    private TipoBalancete tipoInicial;
    private TipoBalancete tipoFinal;
    @Enumerated(EnumType.STRING)
    private TipoAdministracao tipoAdministracao;
    private Boolean exibirContaAuxiliar;
    private Boolean exibirContaAuxiliarDetalhada;

    @Enumerated(EnumType.STRING)
    private ClassificacaoContaAuxiliar classificacaoContaAuxiliar;

    public RelatorioBalanceteContabilPorTipoControlador() {
    }

    public List<SelectItem> getApresentacoes() {
        return Util.getListSelectItemSemCampoVazio(ApresentacaoRelatorio.values());
    }

    public List<SelectItem> getClassificacoesContaAuxiliar() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, "0 - Nenhum"));
        for (ClassificacaoContaAuxiliar cca : ClassificacaoContaAuxiliar.values()) {
            toReturn.add(new SelectItem(cca, cca.getCodigoDescricao()));
        }
        return toReturn;
    }

    public void gerarRelatorio() {
        try {
            validarDatasSemVerificarExercicioLogado();
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
            validarDatasSemVerificarExercicioLogado();
            RelatorioDTO dto = montarRelatorioDto();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
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
        RelatorioDTO dto = new RelatorioDTO();
        dto.adicionarParametro("USER", getNomeUsuarioLogado(), false);
        dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
        dto.setNomeParametroSubreportDir("SUBREPORT_DIR");
        dto.setNomeParametroBrasao("BRASAO");
        montarParametros(dto);
        dto.adicionarParametro("FILTRO", filtros);
        dto.adicionarParametro("DATAINICIAL", DataUtil.getDataFormatada(dataInicial));
        dto.adicionarParametro("DATAFINAL", DataUtil.getDataFormatada(dataFinal));
        dto.adicionarParametro("APRESENTACAO", apresentacao.name());
        dto.setNomeRelatorio(relatorioBalanceteContabilPorTipoFacade.getNomeArquivo(getExercicio(), unidadeGestora, dataInicial, dataFinal, apresentacao, tipoInicial, tipoFinal));
        dto.setApi("contabil/balancete-contabil-tipo/");
        return dto;
    }

    private List<String> buscarTipoBalanceteInicial() {
        List<String> toReturn = Lists.newArrayList();
        switch (tipoInicial) {
            case TRANSPORTE:
                toReturn.add(TipoBalancete.TRANSPORTE.name());
                break;
            case ABERTURA:
                toReturn.add(TipoBalancete.TRANSPORTE.name());
                break;
            case MENSAL:
                toReturn.add(TipoBalancete.TRANSPORTE.name());
                toReturn.add(TipoBalancete.ABERTURA.name());
                toReturn.add(TipoBalancete.MENSAL.name());
                break;
            case APURACAO:
                toReturn.add(TipoBalancete.TRANSPORTE.name());
                toReturn.add(TipoBalancete.ABERTURA.name());
                toReturn.add(TipoBalancete.MENSAL.name());
                break;
            case ENCERRAMENTO:
                toReturn.add(TipoBalancete.TRANSPORTE.name());
                toReturn.add(TipoBalancete.ABERTURA.name());
                toReturn.add(TipoBalancete.MENSAL.name());
                toReturn.add(TipoBalancete.APURACAO.name());
                break;
        }
        return toReturn;
    }

    private List<String> buscarTipoBalanceteFinal() {
        List<String> toReturn = Lists.newArrayList();
        switch (tipoFinal) {
            case TRANSPORTE:
                toReturn.add(TipoBalancete.TRANSPORTE.name());
                break;
            case ABERTURA:
                toReturn.add(TipoBalancete.ABERTURA.name());
                break;
            case MENSAL:
                toReturn.add(TipoBalancete.MENSAL.name());
                break;
            case APURACAO:
                toReturn.add(TipoBalancete.APURACAO.name());
                if (TipoBalancete.MENSAL.equals(tipoInicial)) {
                    toReturn.add(TipoBalancete.MENSAL.name());
                } else if (TipoBalancete.ABERTURA.equals(tipoInicial) || TipoBalancete.TRANSPORTE.equals(tipoInicial)) {
                    toReturn.add(TipoBalancete.MENSAL.name());
                    toReturn.add(TipoBalancete.ABERTURA.name());
                }
                break;
            case ENCERRAMENTO:
                toReturn.add(TipoBalancete.ENCERRAMENTO.name());
                if (TipoBalancete.APURACAO.equals(tipoInicial)) {
                    toReturn.add(TipoBalancete.APURACAO.name());
                } else if (TipoBalancete.MENSAL.equals(tipoInicial)) {
                    toReturn.add(TipoBalancete.MENSAL.name());
                    toReturn.add(TipoBalancete.APURACAO.name());
                } else if (TipoBalancete.ABERTURA.equals(tipoInicial) || TipoBalancete.TRANSPORTE.equals(tipoInicial)) {
                    toReturn.add(TipoBalancete.MENSAL.name());
                    toReturn.add(TipoBalancete.ABERTURA.name());
                    toReturn.add(TipoBalancete.APURACAO.name());
                }
                break;
        }
        return toReturn;
    }

    //Utilizado para calcular corretamente os movimentos de crédito e débito filtrando os tipos na última data do saldo.
    private List<String> buscarTipoBalanceteFinalParaSaldoAtual() {
        List<String> toReturn = Lists.newArrayList();
        switch (tipoFinal) {
            case TRANSPORTE:
                toReturn.add(TipoBalancete.TRANSPORTE.name());
                break;
            case ABERTURA:
                toReturn.add(TipoBalancete.ABERTURA.name());
                break;
            case MENSAL:
                if (TipoBalancete.ABERTURA.equals(tipoInicial) || TipoBalancete.TRANSPORTE.equals(tipoInicial)) {
                    toReturn.add(TipoBalancete.ABERTURA.name());
                }
                toReturn.add(TipoBalancete.MENSAL.name());
                break;
            case APURACAO:
                toReturn.add(TipoBalancete.APURACAO.name());
                if (TipoBalancete.MENSAL.equals(tipoInicial)) {
                    toReturn.add(TipoBalancete.MENSAL.name());
                } else if (TipoBalancete.ABERTURA.equals(tipoInicial) || TipoBalancete.TRANSPORTE.equals(tipoInicial)) {
                    toReturn.add(TipoBalancete.MENSAL.name());
                    toReturn.add(TipoBalancete.ABERTURA.name());
                }
                break;
            case ENCERRAMENTO:
                toReturn.add(TipoBalancete.ENCERRAMENTO.name());
                if (TipoBalancete.APURACAO.equals(tipoInicial)) {
                    toReturn.add(TipoBalancete.APURACAO.name());
                } else if (TipoBalancete.MENSAL.equals(tipoInicial)) {
                    toReturn.add(TipoBalancete.MENSAL.name());
                    toReturn.add(TipoBalancete.APURACAO.name());
                } else if (TipoBalancete.ABERTURA.equals(tipoInicial) || TipoBalancete.TRANSPORTE.equals(tipoInicial)) {
                    toReturn.add(TipoBalancete.MENSAL.name());
                    toReturn.add(TipoBalancete.ABERTURA.name());
                    toReturn.add(TipoBalancete.APURACAO.name());
                }
                break;
        }
        return toReturn;
    }

    private void montarParametros(RelatorioDTO relatorioDTO) {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        Exercicio exercicio = getExercicio();
        parametros.add(new ParametrosRelatorios(null, ":DATAEXERCICIO", null, OperacaoRelatorio.MAIOR_IGUAL, "01/01/" + exercicio.getAno(), null, 3, true));
        parametros.add(new ParametrosRelatorios(null, ":DATAINICIAL", null, OperacaoRelatorio.MENOR_IGUAL, DataUtil.getDataFormatada(dataInicial), null, 0, true));
        parametros.add(new ParametrosRelatorios(null, ":tipoBalanceteInicial", null, OperacaoRelatorio.IN, buscarTipoBalanceteInicial(), null, 0, true));
        parametros.add(new ParametrosRelatorios(null, ":DATAFINAL", null, OperacaoRelatorio.MENOR_IGUAL, DataUtil.getDataFormatada(dataFinal), null, 0, true));
        parametros.add(new ParametrosRelatorios(null, ":tipoBalanceteFinal", null, OperacaoRelatorio.IN, buscarTipoBalanceteFinal(), null, 0, true));
        parametros.add(new ParametrosRelatorios(null, ":tipoBalanceteSaldoAtual", null, OperacaoRelatorio.IN, buscarTipoBalanceteFinalParaSaldoAtual(), null, 0, true));
        filtros = " Período: " + DataUtil.getDataFormatada(dataInicial) + " (" + tipoInicial.getDescricao() + ") a " + DataUtil.getDataFormatada(dataFinal) + " (" + tipoFinal.getDescricao() + ") -";
        parametros.add(new ParametrosRelatorios(" c.exercicio_id ", ":exercicio", null, OperacaoRelatorio.IGUAL, exercicio.getId(), null, 1, false));

        if (this.listaUnidades.size() > 0) {
            List<Long> listaIdsUnidades = Lists.newArrayList();
            String unidades = "";
            for (HierarquiaOrganizacional lista : listaUnidades) {
                listaIdsUnidades.add(lista.getSubordinada().getId());
                unidades += " " + lista.getCodigo().substring(3, 10) + " -";
            }
            filtros += " Unidade(s): " + unidades;
            parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, listaIdsUnidades, null, 1, false));

        } else if (this.unidadeGestora == null) {
            List<Long> listaIdsUnidades = Lists.newArrayList();
            List<HierarquiaOrganizacional> listaUndsUsuarios = relatorioBalanceteContabilPorTipoFacade.getHierarquiaOrganizacionalFacade().listaHierarquiaUsuarioCorrentePorNivel("", getSistemaFacade().getUsuarioCorrente(), getSistemaFacade().getExercicioCorrente(), getSistemaFacade().getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            for (HierarquiaOrganizacional lista : listaUndsUsuarios) {
                listaIdsUnidades.add(lista.getSubordinada().getId());
            }
            if (listaIdsUnidades.size() != 0) {
                parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, listaIdsUnidades, null, 1, false));
            }
        }

        if (tipoAdministracao != null) {
            parametros.add(new ParametrosRelatorios(" vw.TIPOADMINISTRACAO  ", ":tipoAdm", null, OperacaoRelatorio.IGUAL, tipoAdministracao.name(), null, 1, false));
            filtros += " Tipo Adminstração: " + tipoAdministracao.getDescricao() + " -";
        }
        if (conta != null) {
            parametros.add(new ParametrosRelatorios(" C.CODIGO  ", ":contaCod", null, OperacaoRelatorio.LIKE, conta.getCodigoSemZerosAoFinal() + "%", null, 2, false));
            filtros += " Conta Contábil: " + conta.toString() + " -";
        }
        if (unidadeGestora != null) {
            parametros.add(new ParametrosRelatorios(" ug.codigo ", ":ugCodigo", null, OperacaoRelatorio.IGUAL, this.unidadeGestora.getCodigo(), null, 1, false));
            filtros += " Unidade Gestora: " + unidadeGestora.getCodigo() + " -";
        }
        if (classificacaoContaAuxiliar != null) {
            filtros += " Tipo de Conta Auxiliar: " + classificacaoContaAuxiliar.getCodigoDescricao() + " -";

        }

        atualizaFiltrosGerais();
        relatorioDTO.adicionarParametro("apresentacao", apresentacao.getToDto());
        relatorioDTO.adicionarParametro("pesquisouUg", unidadeGestora != null);
        relatorioDTO.adicionarParametro("parametros", ParametrosRelatorios.parametrosToDto(parametros));
        relatorioDTO.adicionarParametro("exibirContaAuxiliar", exibirContaAuxiliar);
        relatorioDTO.adicionarParametro("exibirContaAuxiliarDetalhada", exibirContaAuxiliarDetalhada);
        relatorioDTO.adicionarParametro("dataInicial", DataUtil.getDataFormatada(dataInicial));
        relatorioDTO.adicionarParametro("tipoBalancete", tipoInicial.getToDto());
        relatorioDTO.adicionarParametro("tipoBalanceteFinal", tipoFinal.getToDto());
        relatorioDTO.adicionarParametro("classificacaoContaAuxiliar", classificacaoContaAuxiliar != null ? classificacaoContaAuxiliar.getToDto() : null);
    }

    @Override
    public Exercicio getExercicio() {
        return getExercicioFacade().getExercicioPorAno(new Integer(new SimpleDateFormat("yyyy").format(dataInicial)));
    }

    @URLAction(mappingId = "relatorio-balancete-contabil-por-tipo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        super.limparCamposGeral();
        tipoAdministracao = null;
        exibirContaAuxiliar = false;
        atualizarContaAuxiliarDetalhada();
    }

    public void atualizarContaAuxiliarDetalhada() {
        exibirContaAuxiliarDetalhada = false;
    }

    public List<SelectItem> getListaTipoAdminstracao() {
        List<SelectItem> toReturn = new ArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (TipoAdministracao tp : TipoAdministracao.values()) {
            toReturn.add(new SelectItem(tp, tp.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getListaTipoBalanceteInicial() {
        List<SelectItem> toReturn = new ArrayList();
        toReturn.add(new SelectItem(TipoBalancete.MENSAL, TipoBalancete.MENSAL.getDescricao()));
        if (getMesDaData(dataInicial).equals("01")) {
            toReturn.add(new SelectItem(TipoBalancete.ABERTURA, TipoBalancete.ABERTURA.getDescricao()));
            toReturn.add(new SelectItem(TipoBalancete.TRANSPORTE, TipoBalancete.TRANSPORTE.getDescricao()));
        } else if (getMesDaData(dataInicial).equals("12")) {
            toReturn.add(new SelectItem(TipoBalancete.ENCERRAMENTO, TipoBalancete.ENCERRAMENTO.getDescricao()));
            toReturn.add(new SelectItem(TipoBalancete.APURACAO, TipoBalancete.APURACAO.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getListaTipoBalanceteFinal() {
        List<SelectItem> toReturn = new ArrayList();
        toReturn.add(new SelectItem(TipoBalancete.MENSAL, TipoBalancete.MENSAL.getDescricao()));
        if (getMesDaData(dataFinal).equals("01")) {
            toReturn.add(new SelectItem(TipoBalancete.ABERTURA, TipoBalancete.ABERTURA.getDescricao()));
            toReturn.add(new SelectItem(TipoBalancete.TRANSPORTE, TipoBalancete.TRANSPORTE.getDescricao()));
        } else if (getMesDaData(dataFinal).equals("12")) {
            toReturn.add(new SelectItem(TipoBalancete.ENCERRAMENTO, TipoBalancete.ENCERRAMENTO.getDescricao()));
            toReturn.add(new SelectItem(TipoBalancete.APURACAO, TipoBalancete.APURACAO.getDescricao()));
        }
        return toReturn;
    }

    private String getMesDaData(Date data) {
        return DataUtil.getDataFormatada(data).substring(3, 5);
    }

    public List<Conta> completarContasContabeis(String parte) {
        return contaFacade.listaContasContabeis(parte.trim(), getSistemaFacade().getExercicioCorrente());
    }

    @Override
    public String getNomeRelatorio() {
        return "";
    }


    public TipoAdministracao getTipoAdministracao() {
        return tipoAdministracao;
    }

    public void setTipoAdministracao(TipoAdministracao tipoAdministracao) {
        this.tipoAdministracao = tipoAdministracao;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    public void setContaFacade(ContaFacade contaFacade) {
        this.contaFacade = contaFacade;
    }

    public Boolean getExibirContaAuxiliar() {
        return exibirContaAuxiliar;
    }

    public void setExibirContaAuxiliar(Boolean exibirContaAuxiliar) {
        this.exibirContaAuxiliar = exibirContaAuxiliar;
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

    public ClassificacaoContaAuxiliar getClassificacaoContaAuxiliar() {
        return classificacaoContaAuxiliar;
    }

    public void setClassificacaoContaAuxiliar(ClassificacaoContaAuxiliar classificacaoContaAuxiliar) {
        this.classificacaoContaAuxiliar = classificacaoContaAuxiliar;
    }

    public Boolean getExibirContaAuxiliarDetalhada() {
        return exibirContaAuxiliarDetalhada;
    }

    public void setExibirContaAuxiliarDetalhada(Boolean exibirContaAuxiliarDetalhada) {
        this.exibirContaAuxiliarDetalhada = exibirContaAuxiliarDetalhada;
    }
}
