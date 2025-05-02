package br.com.webpublico.controlerelatorio;

import br.com.webpublico.DateUtils;
import br.com.webpublico.entidades.Divida;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.DividaFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.tributario.consultadebitos.enums.TipoDeDebitoDTO;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.WebReportUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.lang.StringUtils;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioParcelamentoDebitos",
        pattern = "/tributario/conta-corrente/relatorio-parcelamento-debitos/",
        viewId = "/faces/tributario/relatorio/relatorioparcelamentodebitos.xhtml"),
})

public class RelatorioParcelamentoDebitosControlador extends AbstractReport implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private DividaFacade dividaFacade;

    private TipoCadastroTributario tipoCadastroTributario;
    private String cadastroInicial;
    private String cadastroFinal;
    private Pessoa contribuinte;
    private Exercicio exercicioInicial;
    private Exercicio exercicioFinal;
    private Long numeroInicial;
    private Long numeroFinal;
    private Date dataParcelamentoInicial;
    private Date dataParcelamentoFinal;
    private String descricaoParametro;
    private Integer parcelasEmAtraso;
    private Divida divida;
    private List<Divida> dividas;
    private SituacaoParcelamento situacaoParcelamento;
    private List<SituacaoParcelamento> situacoesParcelamento;
    private TipoRelatorioApresentacao tipoRelatorio;
    private List<TipoDeDebitoDTO> tiposDeDebito;
    private TipoDeDebitoDTO tipoDeDebito;

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    public String getCadastroInicial() {
        return cadastroInicial;
    }

    public void setCadastroInicial(String cadastroInicial) {
        this.cadastroInicial = cadastroInicial;
    }

    public String getCadastroFinal() {
        return cadastroFinal;
    }

    public void setCadastroFinal(String cadastroFinal) {
        this.cadastroFinal = cadastroFinal;
    }

    public Pessoa getContribuinte() {
        return contribuinte;
    }

    public void setContribuinte(Pessoa contribuinte) {
        this.contribuinte = contribuinte;
    }

    public Exercicio getExercicioInicial() {
        return exercicioInicial;
    }

    public void setExercicioInicial(Exercicio exercicioInicial) {
        this.exercicioInicial = exercicioInicial;
    }

    public Exercicio getExercicioFinal() {
        return exercicioFinal;
    }

    public void setExercicioFinal(Exercicio exercicioFinal) {
        this.exercicioFinal = exercicioFinal;
    }

    public Long getNumeroInicial() {
        return numeroInicial;
    }

    public void setNumeroInicial(Long numeroInicial) {
        this.numeroInicial = numeroInicial;
    }

    public Long getNumeroFinal() {
        return numeroFinal;
    }

    public void setNumeroFinal(Long numeroFinal) {
        this.numeroFinal = numeroFinal;
    }

    public Date getDataParcelamentoInicial() {
        return dataParcelamentoInicial;
    }

    public void setDataParcelamentoInicial(Date dataParcelamentoInicial) {
        this.dataParcelamentoInicial = dataParcelamentoInicial;
    }

    public Date getDataParcelamentoFinal() {
        return dataParcelamentoFinal;
    }

    public void setDataParcelamentoFinal(Date dataParcelamentoFinal) {
        this.dataParcelamentoFinal = dataParcelamentoFinal;
    }

    public String getDescricaoParametro() {
        return descricaoParametro;
    }

    public void setDescricaoParametro(String descricaoParametro) {
        this.descricaoParametro = descricaoParametro;
    }

    public Integer getParcelasEmAtraso() {
        return parcelasEmAtraso;
    }

    public void setParcelasEmAtraso(Integer parcelasEmAtraso) {
        this.parcelasEmAtraso = parcelasEmAtraso;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public void setDividas(List<Divida> dividas) {
        this.dividas = dividas;
    }

    public List<Divida> getDividas() {
        return dividas;
    }

    public SituacaoParcelamento getSituacaoParcelamento() {
        return situacaoParcelamento;
    }

    public void setSituacaoParcelamento(SituacaoParcelamento situacaoParcelamento) {
        this.situacaoParcelamento = situacaoParcelamento;
    }

    public List<SituacaoParcelamento> getSituacoesParcelamento() {
        return situacoesParcelamento;
    }

    public void setSituacoesParcelamento(List<SituacaoParcelamento> situacoesParcelamento) {
        this.situacoesParcelamento = situacoesParcelamento;
    }

    public TipoRelatorioApresentacao getTipoRelatorio() {
        return tipoRelatorio;
    }

    public void setTipoRelatorio(TipoRelatorioApresentacao tipoRelatorio) {
        this.tipoRelatorio = tipoRelatorio;
    }

    public List<TipoDeDebitoDTO> getTiposDeDebito() {
        return tiposDeDebito;
    }

    public void setTiposDeDebito(List<TipoDeDebitoDTO> tiposDeDebito) {
        this.tiposDeDebito = tiposDeDebito;
    }

    public TipoDeDebitoDTO getTipoDeDebito() {
        return tipoDeDebito;
    }

    public void setTipoDeDebito(TipoDeDebitoDTO tipoDeDebito) {
        this.tipoDeDebito = tipoDeDebito;
    }

    public List<SelectItem> selectItemsTiposDebitos() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, " "));
        for (TipoDeDebitoDTO tipo : TipoDeDebitoDTO.values()) {
            if (!TipoDeDebitoDTO.EXP.equals(tipo)) {
                retorno.add(new SelectItem(tipo, tipo.getDescricao()));
            }
        }
        return retorno;
    }

    public void adicionarFiltros(RelatorioDTO dto) {
        StringBuilder filtro = new StringBuilder();
        Map<String, Map<String, String>> filtrosExcel = Maps.newHashMap();
        List<ParametrosRelatorios> parametros = Lists.newArrayList();

        if (tipoCadastroTributario != null) {
            filtro.append("Tipo de Cadastro: ").append(tipoCadastroTributario.getDescricao()).append("; ");
            WebReportUtil.adicionarMapFiltroExcel(filtrosExcel, "Tipo de Cadastro", tipoCadastroTributario.getDescricao());
            parametros.add(new ParametrosRelatorios("case when ci.id is not null then 'IMOBILIARIO' " +
                " when ce.id is not null then 'ECONOMICO' when cr.id is not null then 'RURAL' else 'PESSOA' end ",
                ":tipo_cadastro", null, OperacaoRelatorio.IGUAL, tipoCadastroTributario.name(),
                null, 1, false));

            if (!tipoCadastroTributario.equals(TipoCadastroTributario.PESSOA)) {
                if (!Strings.isNullOrEmpty(cadastroInicial)) {
                    parametros.add(new ParametrosRelatorios("coalesce(ci.inscricaocadastral, ce.inscricaocadastral, cr.numeroincra)", ":cadastro_inicial", null,
                        OperacaoRelatorio.MAIOR_IGUAL, cadastroInicial.trim(), null, 1, false));
                    filtro.append("Cadastro Inicial: ").append(cadastroInicial).append("; ");
                    WebReportUtil.adicionarMapFiltroExcel(filtrosExcel, "Cadastro Inicial", cadastroInicial);
                }

                if (!Strings.isNullOrEmpty(cadastroFinal)) {
                    parametros.add(new ParametrosRelatorios("coalesce(ci.inscricaocadastral, ce.inscricaocadastral, cr.numeroincra)", ":cadastro_final", null,
                        OperacaoRelatorio.MENOR_IGUAL, cadastroFinal.trim(), null, 1, false));
                    filtro.append("Cadastro Final: ").append(cadastroFinal).append("; ");
                    WebReportUtil.adicionarMapFiltroExcel(filtrosExcel, "Cadastro Final", cadastroFinal);
                }
            } else {
                if (contribuinte != null) {
                    parametros.add(new ParametrosRelatorios("calcp.pessoa_id", ":pessoa", null,
                        OperacaoRelatorio.IGUAL, contribuinte.getId(), null, 1, false));
                    filtro.append("Contribuinte: ").append(contribuinte.getNomeCpfCnpj()).append("; ");
                    WebReportUtil.adicionarMapFiltroExcel(filtrosExcel, "Contribuinte", contribuinte.getNomeCpfCnpj());
                }
            }
        }

        if (exercicioInicial != null) {
            parametros.add(new ParametrosRelatorios("e.ano", ":exercicio_inicial", null,
                OperacaoRelatorio.MAIOR_IGUAL, exercicioInicial.getAno(), null, 1, false));
            filtro.append("Exercício Inicial: ").append(exercicioInicial.getAno()).append("; ");
            WebReportUtil.adicionarMapFiltroExcel(filtrosExcel, "Exercício Inicial", exercicioInicial.getAno().toString());
        }

        if (exercicioFinal != null) {
            parametros.add(new ParametrosRelatorios("e.ano", ":exercicio_final", null,
                OperacaoRelatorio.MENOR_IGUAL, exercicioFinal.getAno(), null, 1, false));
            filtro.append("Exercício Final: ").append(exercicioFinal.getAno()).append("; ");
            WebReportUtil.adicionarMapFiltroExcel(filtrosExcel, "Exercício Final", exercicioFinal.getAno().toString());
        }

        if (numeroInicial != null && numeroInicial > 0) {
            parametros.add(new ParametrosRelatorios("pp.numero", ":numero_inicial", null,
                OperacaoRelatorio.MAIOR_IGUAL, numeroInicial, null, 1, false));
            filtro.append("Número Inicial: ").append(numeroInicial).append("; ");
            WebReportUtil.adicionarMapFiltroExcel(filtrosExcel, "Número Inicial", numeroInicial.toString());
        }

        if (numeroFinal != null && numeroFinal > 0) {
            parametros.add(new ParametrosRelatorios("pp.numero", ":numero_final", null,
                OperacaoRelatorio.MENOR_IGUAL, numeroFinal, null, 1, false));
            filtro.append("Número Final: ").append(numeroFinal).append("; ");
            WebReportUtil.adicionarMapFiltroExcel(filtrosExcel, "Número Final", numeroFinal.toString());
        }

        if (dataParcelamentoInicial != null) {
            parametros.add(new ParametrosRelatorios("trunc(pp.dataprocessoparcelamento)", ":data_inicial", null,
                OperacaoRelatorio.MAIOR_IGUAL, DateUtils.getDataFormatada(dataParcelamentoInicial),
                null, 1, true));
            filtro.append("Data Parcelamento Inicial: ").append(DateUtils.getDataFormatada(dataParcelamentoInicial)).append("; ");
            WebReportUtil.adicionarMapFiltroExcel(filtrosExcel, "Data Parcelamento Inicial", DateUtils.getDataFormatada(dataParcelamentoInicial));
        }

        if (dataParcelamentoFinal != null) {
            parametros.add(new ParametrosRelatorios("trunc(pp.dataprocessoparcelamento)", ":data_final", null,
                OperacaoRelatorio.MENOR_IGUAL, DateUtils.getDataFormatada(dataParcelamentoFinal),
                null, 1, false));
            filtro.append("Data Parcelamento Final: ").append(DateUtils.getDataFormatada(dataParcelamentoFinal)).append("; ");
            WebReportUtil.adicionarMapFiltroExcel(filtrosExcel, "Data Parcelamento Final", DateUtils.getDataFormatada(dataParcelamentoFinal));
        }

        if (tiposDeDebito != null && !tiposDeDebito.isEmpty()) {
            StringBuilder subQuery = new StringBuilder("(");
            for (int i = 0; i < tiposDeDebito.size(); i++) {
                if (i != 0) subQuery.append(" or ");
                subQuery.append(" exists(select pvd.id from parcelavalordivida pvd where pvd.valordivida_id = vd.id ");
                if (TipoDeDebitoDTO.EX.equals(tiposDeDebito.get(i))) {
                    subQuery.append(" and pvd.dividaativa = 0 and pvd.dividaativaajuizada = 0 ");
                } else if (TipoDeDebitoDTO.DA.equals(tiposDeDebito.get(i))) {
                    subQuery.append(" and pvd.dividaativa = 1 and pvd.dividaativaajuizada = 0 and coalesce(debitoprotestado, 0) = 0 ");
                } else if (TipoDeDebitoDTO.DAP.equals(tiposDeDebito.get(i))) {
                    subQuery.append(" and pvd.dividaativa = 1 and pvd.dividaativaajuizada = 0 and coalesce(debitoprotestado, 0) = 1 ");
                } else if (TipoDeDebitoDTO.AJZ.equals(tiposDeDebito.get(i))) {
                    subQuery.append(" and pvd.dividaativa = 1 and pvd.dividaativaajuizada = 1 and coalesce(debitoprotestado, 0) = 0 ");
                } else if (TipoDeDebitoDTO.AJZP.equals(tiposDeDebito.get(i))) {
                    subQuery.append(" and pvd.dividaativa = 1 and pvd.dividaativaajuizada = 1 and coalesce(debitoprotestado, 0) = 1 ");
                }
                subQuery.append(") ");
            }
            subQuery.append(")");
            parametros.add(new ParametrosRelatorios(subQuery.toString(), 1));
            filtro.append("Tipo de débito: ").append(StringUtils.join(tiposDeDebito.stream().map(TipoDeDebitoDTO::getDescricao)
                .collect(Collectors.toList()), ", ")).append("; ");
            WebReportUtil.adicionarMapFiltroExcel(filtrosExcel, "Tipo de débito", StringUtils.join(tiposDeDebito.stream().map(TipoDeDebitoDTO::getDescricao)
                .collect(Collectors.toList()), ", "));
        }

        if (!Strings.isNullOrEmpty(descricaoParametro)) {
            parametros.add(new ParametrosRelatorios("lower(param.descricao)", ":parametro", null,
                OperacaoRelatorio.LIKE, "%" + descricaoParametro.trim().toLowerCase() + "%", null, 1, false));
            filtro.append("Descrição do Parâmetro: ").append(descricaoParametro).append("; ");
            WebReportUtil.adicionarMapFiltroExcel(filtrosExcel, "Descrição do Parâmetro", descricaoParametro);
        }

        if (parcelasEmAtraso != null && parcelasEmAtraso > 0) {
            parametros.add(new ParametrosRelatorios("(select count(1) from parcelavalordivida pvd " +
                " inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id " +
                " where pvd.valordivida_id = vd.id and pvd.vencimento < current_date " +
                "   and spvd.situacaoparcela = '" + SituacaoParcela.EM_ABERTO.name() + "') ", ":parcelas_atraso", null,
                OperacaoRelatorio.IGUAL, parcelasEmAtraso, null, 1, false));
            filtro.append("Parcelas em Atraso: ").append(parcelasEmAtraso).append("; ");
            WebReportUtil.adicionarMapFiltroExcel(filtrosExcel, "Parcelas em Atraso", parcelasEmAtraso.toString());
        }

        if (!dividas.isEmpty()) {
            parametros.add(new ParametrosRelatorios("vd.divida_id", ":dividas", null,
                OperacaoRelatorio.IN, dividas.stream().map(Divida::getId)
                .collect(Collectors.toList()), null, 1, false));
            filtro.append("Dívidas: ").append(StringUtils.join(dividas.stream().map(Divida::getDescricao)
                .collect(Collectors.toList()), ", ")).append("; ");
            WebReportUtil.adicionarMapFiltroExcel(filtrosExcel, "Dívidas", StringUtils.join(dividas.stream().map(Divida::getDescricao)
                .collect(Collectors.toList()), ", "));
        }

        if (!situacoesParcelamento.isEmpty()) {
            parametros.add(new ParametrosRelatorios("pp.situacaoparcelamento", ":situacoes", null,
                OperacaoRelatorio.IN, situacoesParcelamento.stream().map(SituacaoParcelamento::name)
                .collect(Collectors.toList()), null, 1, false));
            filtro.append("Situações: ").append(StringUtils.join(situacoesParcelamento.stream().map(SituacaoParcelamento::getDescricao)
                .collect(Collectors.toList()), ", ")).append("; ");
            WebReportUtil.adicionarMapFiltroExcel(filtrosExcel, "Situações", StringUtils.join(situacoesParcelamento.stream().map(SituacaoParcelamento::getDescricao)
                .collect(Collectors.toList()), ", "));
        }

        filtro.append("Tipo de Relatório: ").append(tipoRelatorio.getDescricao()).append("; ");
        WebReportUtil.adicionarMapFiltroExcel(filtrosExcel, "Tipo de Relatório", tipoRelatorio.getDescricao());

        if (parametros.isEmpty()) {
            throw new ValidacaoException("Nenhum filtro informado para emissão do relatório.");
        }

        dto.adicionarParametro("PARAMETROS", ParametrosRelatorios.parametrosToDto(parametros));
        dto.adicionarParametro("FILTROS", filtro.toString());
        dto.adicionarParametro("TIPO_RELATORIO", tipoRelatorio.name());
        dto.adicionarParametro("FILTROS_EXCEL", filtrosExcel);
    }


    @URLAction(mappingId = "novoRelatorioParcelamentoDebitos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        this.tipoCadastroTributario = null;
        this.cadastroInicial = "1";
        this.cadastroFinal = "999999999999999999";
        this.exercicioInicial = null;
        this.exercicioFinal = null;
        this.numeroInicial = null;
        this.numeroFinal = null;
        this.dataParcelamentoInicial = null;
        this.dataParcelamentoFinal = null;
        this.descricaoParametro = null;
        this.parcelasEmAtraso = null;
        this.divida = null;
        this.dividas = Lists.newArrayList();
        this.situacaoParcelamento = null;
        this.situacoesParcelamento = Lists.newArrayList(SituacaoParcelamento.FINALIZADO,
            SituacaoParcelamento.REATIVADO, SituacaoParcelamento.PAGO);
        this.tipoRelatorio = TipoRelatorioApresentacao.RESUMIDO;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            adicionarFiltros(dto);
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setApi(TipoRelatorioDTO.XLS.equals(dto.getTipoRelatorio()) ?
                "tributario/parcelamento-debitos/excel/" : "tributario/parcelamento-debitos/");
            dto.setNomeRelatorio("Relatório de Parcelamento de Débitos");
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome());
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException op) {
            FacesUtil.printAllFacesMessages(op);
        } catch (Exception ex) {
            logger.error("Erro ao gerar o relatorio de parcelamento de débitos. Erro: {}", ex.getMessage());
            logger.debug("Detalhes do erro ao gerar o relatorio de parcelamento de débitos.", ex);
            FacesUtil.addErrorPadrao(ex);
        }
    }

    public List<SelectItem> getSituacoes() {
        return Util.getListSelectItem(SituacaoParcelamento.values());
    }

    public void adicionarSituacao() {
        if (!situacoesParcelamento.contains(situacaoParcelamento)) {
            situacoesParcelamento.add(situacaoParcelamento);
        }
        situacaoParcelamento = null;
    }

    public void removerSituacao(SituacaoParcelamento situacaoParcelamento) {
        situacoesParcelamento.remove(situacaoParcelamento);
    }

    public List<SelectItem> getDividasSelect() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, "     "));
        for (Divida divida : dividaFacade.buscarDividasDeParcelamentoDeDividaAtivaOrdenadoPorDescricao()) {
            toReturn.add(new SelectItem(divida, divida.getDescricao()));
        }
        return toReturn;
    }

    private void validarDividas() {
        ValidacaoException ve = new ValidacaoException();
        if (dividas != null && divida != null && dividas.contains(divida)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Dívida já adicionada!");
        }
        ve.lancarException();
    }

    private void validarTipoDebito() {
        ValidacaoException ve = new ValidacaoException();
        if (tiposDeDebito != null && tipoDeDebito != null && tiposDeDebito.contains(tipoDeDebito)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Tipo de débito já adicionado!");
        }
        ve.lancarException();
    }

    public void addDivida() {
        try {
            validarDividas();
            if (dividas == null) {
                dividas = Lists.newArrayList();
            }
            dividas.add(divida);
            divida = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void addTipoDebito() {
        try {
            validarTipoDebito();
            if (tiposDeDebito == null) {
                tiposDeDebito = Lists.newArrayList();
            }
            tiposDeDebito.add(tipoDeDebito);
            tipoDeDebito = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void delDivida(Divida divida) {
        dividas.remove(divida);
    }

    public void removerTipoDebito(TipoDeDebitoDTO tipo) {
        tiposDeDebito.remove(tipo);
    }

    public List<SelectItem> getTiposRelatorio() {
        return Util.getListSelectItemSemCampoVazio(TipoRelatorioApresentacao.values());
    }
}
