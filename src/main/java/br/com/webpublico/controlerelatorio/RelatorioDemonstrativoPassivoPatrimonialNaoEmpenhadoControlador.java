package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDemonstrativoPassivoPatrimonialNaoEmpenhadoParametros;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(
        id = "relatorio-demonstrativo-passivo-patrimonial-nao-empenhado",
        pattern = "/relatorio/demonstrativo-passivo-patrimonial-nao-empenhado/",
        viewId = "/faces/financeiro/relatorio/relatorio-demonstrativo-passivo-patrimonial-nao-empenhado.xhtml"
    )
})
public class RelatorioDemonstrativoPassivoPatrimonialNaoEmpenhadoControlador extends RelatorioContabilSuperControlador
    implements Serializable {

    private static final String MODULO_CONTABIL = "Contábil";
    private static final String API_RELATORIO = "contabil/demonstrativo-passivo-patrimonial-nao-empenhado/";
    private static final String SEPARADOR_FILTRO = " - ";
    private static final String SEPARADOR_DATA = " até ";
    private static final String MENSAGEM_VIGENCIA_INVALIDA = "O Término da Vigência deve ser maior que o Início da Vigência.";
    private static final String MENSAGEM_EXECUCAO_INVALIDA = "O Término da Execução deve ser maior que o Início da Execução.";
    private static final String FILTRO_PERIODO_VIGENCIA = "Período Vigência: ";
    private static final String FILTRO_INICIO_VIGENCIA = "Início Vigência: ";
    private static final String FILTRO_TERMINO_VIGENCIA = "Término Vigência: ";
    private static final String FILTRO_PERIODO_EXECUCAO = "Período Execução: ";
    private static final String FILTRO_INICIO_EXECUCAO = "Início Execução: ";
    private static final String FILTRO_TERMINO_EXECUCAO = "Término Execução: ";
    private static final String CAMPO_HIERARQUIA_ADMINISTRATIVA_SUBORDINADA_ID = "vwadm.subordinada_id";
    private static final String PARAMETRO_HIERARQUIA_ADMINISTRATIVA_SUBORDINADA_ID = ":unidadeAdministrativaId";
    private static final String FILTRO_UNIDADE_ADMINISTRATIVA = "Unidade Administrativa: ";
    private static final String CAMPO_HIERARQUIA_ORCAMENTARIA_SUBORDINADA_ID = "hor.vworc_subordinada_id";
    private static final String PARAMETRO_HIERARQUIA_ORCAMENTARIA_SUBORDINADA_ID = ":unidadeOrcamentariaId";
    private static final String FILTRO_UNIDADE_ORCAMENTARIA = "Unidade Orçamentária: ";
    private static final String NOME_RELATORIO =
        "RELATORIO-DEMONSTRATIVO-PASSIVO-PATRIMONIAL-NAO-EMPENHADO";

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;

    private HierarquiaOrganizacional hierarquiaAdministrativa;
    private HierarquiaOrganizacional hierarquiaOrcamentaria;
    private Date inicioVigencia;
    private Date terminoVigencia;
    private Date inicioExecucao;
    private Date terminoExecucao;

    @URLAction(mappingId = "relatorio-demonstrativo-passivo-patrimonial-nao-empenhado",
        phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        this.hierarquiaAdministrativa = null;
        this.hierarquiaOrcamentaria = null;
        this.inicioVigencia = null;
        this.terminoVigencia = null;
        this.inicioExecucao = null;
        this.terminoExecucao = null;
    }

    public void gerarRelatorio() {
        try {
            RelatorioDTO dto = montarDTO();
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private RelatorioDTO montarDTO() {
        RelatorioDTO dto = new RelatorioDTO();
        adicionarParametrosComuns(dto);
        adicionarParametrosDatas(dto);
        adicionarParametrosRelatorio(dto);
        return dto;
    }

    private void adicionarParametrosComuns(RelatorioDTO dto) {
        dto.adicionarParametro(RelatorioDemonstrativoPassivoPatrimonialNaoEmpenhadoParametros.USUARIO,
            getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.setNomeParametroBrasao(RelatorioDemonstrativoPassivoPatrimonialNaoEmpenhadoParametros.BRASAO);
        dto.adicionarParametro(RelatorioDemonstrativoPassivoPatrimonialNaoEmpenhadoParametros.MUNICIPIO,
            getDescricaoSegundoEsferaDoPoder());
        dto.adicionarParametro(RelatorioDemonstrativoPassivoPatrimonialNaoEmpenhadoParametros.MODULO,
            MODULO_CONTABIL);
    }

    private void adicionarParametrosDatas(RelatorioDTO dto) {
        dto.adicionarParametro(RelatorioDemonstrativoPassivoPatrimonialNaoEmpenhadoParametros.DATA_OPERACAO,
            DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        dto.adicionarParametro(RelatorioDemonstrativoPassivoPatrimonialNaoEmpenhadoParametros.ANO_EXERCICIO,
            getSistemaControlador().getExercicioCorrente().getAno().toString());
        dto.adicionarParametro(RelatorioDemonstrativoPassivoPatrimonialNaoEmpenhadoParametros.EXERCICIO,
            getSistemaControlador().getExercicioCorrente().getId());

        dto.adicionarParametro(RelatorioDemonstrativoPassivoPatrimonialNaoEmpenhadoParametros.INICIO_VIGENCIA,
            DataUtil.getDataFormatada(inicioVigencia));
        dto.adicionarParametro(RelatorioDemonstrativoPassivoPatrimonialNaoEmpenhadoParametros.TERMINO_VIGENCIA,
            DataUtil.getDataFormatada(terminoVigencia));
        dto.adicionarParametro(RelatorioDemonstrativoPassivoPatrimonialNaoEmpenhadoParametros.INICIO_EXECUCAO,
            DataUtil.getDataFormatada(inicioExecucao));
        dto.adicionarParametro(RelatorioDemonstrativoPassivoPatrimonialNaoEmpenhadoParametros.TERMINO_EXECUCAO,
            DataUtil.getDataFormatada(terminoExecucao));
    }

    private void adicionarParametrosRelatorio(RelatorioDTO dto) {
        dto.adicionarParametro(RelatorioDemonstrativoPassivoPatrimonialNaoEmpenhadoParametros.PARAMETROS_RELATORIO,
            ParametrosRelatorios.parametrosToDto(montarParametrosEFiltros()));
        dto.adicionarParametro(RelatorioDemonstrativoPassivoPatrimonialNaoEmpenhadoParametros.FILTROS, filtros);
        dto.setNomeRelatorio(getNomeRelatorio());
        dto.setApi(API_RELATORIO);
    }

    private List<ParametrosRelatorios> montarParametrosEFiltros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        filtros = "";
        validarFiltrosData();
        adicionarFiltrosData();
        adicionarParametrosEFiltrosHierarquiaAdministrativa(parametros);
        adicionarParametrosEFiltrosHierarquiaOrcamentaria(parametros);
        atualizarFiltros();
        return parametros;
    }

    private void validarFiltrosData() {
        ValidacaoException ve = new ValidacaoException();
        if (terminoVigencia != null && inicioVigencia != null) {
            if (terminoVigencia.before(inicioVigencia)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(MENSAGEM_VIGENCIA_INVALIDA);
            }
        }
        if (terminoExecucao != null && inicioExecucao != null) {
            if (terminoExecucao.before(inicioExecucao)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida(MENSAGEM_EXECUCAO_INVALIDA);
            }
        }
        ve.lancarException();
    }

    private void adicionarFiltrosData() {
        adicionarFiltrosPeriodoVigencia();
        adicionarFiltrosPeriodoExecucao();
    }

    private void adicionarFiltrosPeriodoVigencia() {
        adicionarFiltrosPeriodo(inicioVigencia, terminoVigencia, FILTRO_PERIODO_VIGENCIA, FILTRO_INICIO_VIGENCIA,
            FILTRO_TERMINO_VIGENCIA);
    }

    private void adicionarFiltrosPeriodoExecucao() {
        adicionarFiltrosPeriodo(inicioExecucao, terminoExecucao, FILTRO_PERIODO_EXECUCAO, FILTRO_INICIO_EXECUCAO,
            FILTRO_TERMINO_EXECUCAO);
    }

    private void adicionarFiltrosPeriodo(Date dataInicio, Date dataTermino, String filtroPeriodo, String filtroInicio,
        String filtroTermino) {
        if (dataInicio != null && dataTermino != null) {
            filtros += filtroPeriodo + DataUtil.getDataFormatada(dataInicio) + SEPARADOR_DATA
                + DataUtil.getDataFormatada(dataTermino) + SEPARADOR_FILTRO;
        } else if (dataInicio != null) {
            filtros += filtroInicio + DataUtil.getDataFormatada(dataInicio) + SEPARADOR_FILTRO;
        } else if (dataTermino != null) {
            filtros += filtroTermino + DataUtil.getDataFormatada(dataTermino) + SEPARADOR_FILTRO;
        }
    }

    private void adicionarParametrosEFiltrosHierarquiaAdministrativa(List<ParametrosRelatorios> parametros) {
        if (hierarquiaAdministrativa != null) {
            parametros.add(new ParametrosRelatorios(CAMPO_HIERARQUIA_ADMINISTRATIVA_SUBORDINADA_ID,
                PARAMETRO_HIERARQUIA_ADMINISTRATIVA_SUBORDINADA_ID, null, OperacaoRelatorio.IGUAL,
                hierarquiaAdministrativa.getSubordinada().getId(), null, 1, false));

            filtros += FILTRO_UNIDADE_ADMINISTRATIVA + hierarquiaAdministrativa + SEPARADOR_FILTRO;
        } else {
            List<HierarquiaOrganizacional> hierarquiaAdministrativasDoUsuario =
                hierarquiaOrganizacionalFacade.buscarHierarquiaUsuarioPorTipo("",
                    sistemaFacade.getUsuarioCorrente(), sistemaFacade.getDataOperacao(),
                    TipoHierarquiaOrganizacional.ADMINISTRATIVA, 2);

            List<Long> idsUnidadesAdministrativas = new ArrayList<>();

            for (HierarquiaOrganizacional hierarquiaAdministrativa : hierarquiaAdministrativasDoUsuario) {
                idsUnidadesAdministrativas.add(hierarquiaAdministrativa.getSubordinada().getId());
            }

            if (!idsUnidadesAdministrativas.isEmpty()) {
                parametros.add(new ParametrosRelatorios(CAMPO_HIERARQUIA_ADMINISTRATIVA_SUBORDINADA_ID,
                    PARAMETRO_HIERARQUIA_ADMINISTRATIVA_SUBORDINADA_ID, null, OperacaoRelatorio.IN,
                    idsUnidadesAdministrativas, null, 1, false));
            }
        }
    }

    private void adicionarParametrosEFiltrosHierarquiaOrcamentaria(List<ParametrosRelatorios> parametros) {
        if (hierarquiaOrcamentaria != null) {
            parametros.add(new ParametrosRelatorios(CAMPO_HIERARQUIA_ORCAMENTARIA_SUBORDINADA_ID,
                PARAMETRO_HIERARQUIA_ORCAMENTARIA_SUBORDINADA_ID, null, OperacaoRelatorio.IGUAL,
                hierarquiaOrcamentaria.getSubordinada().getId(), null, 1, false));

            filtros += FILTRO_UNIDADE_ORCAMENTARIA + hierarquiaOrcamentaria + SEPARADOR_FILTRO;
        } else {
            List<HierarquiaOrganizacional> hierarquiasOrcamentariasDoUsuario =
                hierarquiaOrganizacionalFacade.buscarHierarquiaUsuarioPorTipo("",
                    sistemaFacade.getUsuarioCorrente(), sistemaFacade.getDataOperacao(),
                    TipoHierarquiaOrganizacional.ORCAMENTARIA, 3);

            List<Long> idsUnidadesOrcamentarias = new ArrayList<>();

            for (HierarquiaOrganizacional hierarquiaOrcamentaria : hierarquiasOrcamentariasDoUsuario) {
                idsUnidadesOrcamentarias.add(hierarquiaOrcamentaria.getSubordinada().getId());
            }

            if (!idsUnidadesOrcamentarias.isEmpty()) {
                parametros.add(new ParametrosRelatorios(CAMPO_HIERARQUIA_ORCAMENTARIA_SUBORDINADA_ID,
                    PARAMETRO_HIERARQUIA_ORCAMENTARIA_SUBORDINADA_ID, null, OperacaoRelatorio.IN,
                    idsUnidadesOrcamentarias, null, 1, false));
            }
        }
    }

    private void atualizarFiltros() {
        filtros = filtros.isEmpty() ? "" : filtros.substring(0, filtros.length() - 2);
    }

    @Override
    public String getNomeRelatorio() {
        return NOME_RELATORIO;
    }

    public HierarquiaOrganizacional getHierarquiaAdministrativa() {
        return hierarquiaAdministrativa;
    }

    public void setHierarquiaAdministrativa(HierarquiaOrganizacional hierarquiaAdministrativa) {
        this.hierarquiaAdministrativa = hierarquiaAdministrativa;
    }

    public HierarquiaOrganizacional getHierarquiaOrcamentaria() {
        return hierarquiaOrcamentaria;
    }

    public void setHierarquiaOrcamentaria(HierarquiaOrganizacional hierarquiaOrcamentaria) {
        this.hierarquiaOrcamentaria = hierarquiaOrcamentaria;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getTerminoVigencia() {
        return terminoVigencia;
    }

    public void setTerminoVigencia(Date terminoVigencia) {
        this.terminoVigencia = terminoVigencia;
    }

    public Date getInicioExecucao() {
        return inicioExecucao;
    }

    public void setInicioExecucao(Date inicioExecucao) {
        this.inicioExecucao = inicioExecucao;
    }

    public Date getTerminoExecucao() {
        return terminoExecucao;
    }

    public void setTerminoExecucao(Date terminoExecucao) {
        this.terminoExecucao = terminoExecucao;
    }

}
