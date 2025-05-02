package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.comum.ConfiguracaoDeRelatorioFacade;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: usuario
 * Date: 17/01/14
 * Time: 09:51
 * To change this template use File | Settings | File Templates.
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-demonstrativo-despesa", pattern = "/relatorio/demonstrativo-despesa-por-natureza/", viewId = "/faces/financeiro/relatorio/relatoriodemonstrativodespesa.xhtml"),
    @URLMapping(id = "relatorio-demonstrativo-despesa-por-periodo", pattern = "/relatorio/demonstrativo-despesa-por-natureza-e-periodo/", viewId = "/faces/financeiro/relatorio/relatorio-demonstrativo-despesa-periodo.xhtml")
})
@ManagedBean
public class RelatorioDemonstrativoDespesaControlador implements Serializable {
    protected static final Logger logger = LoggerFactory.getLogger(RelatorioDemonstrativoDespesaControlador.class);
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private FuncaoFacade funcaoFacade;
    @EJB
    private SubFuncaoFacade subFuncaoFacade;
    @EJB
    private ProgramaPPAFacade programaPPAFacade;
    @EJB
    private TipoAcaoPPAFacade tipoAcaoPPAFacade;
    @EJB
    private ProjetoAtividadeFacade projetoAtividadeFacade;
    @EJB
    private SubProjetoAtividadeFacade subProjetoAtividadeFacade;
    @EJB
    private ConfiguracaoDeRelatorioFacade configuracaoDeRelatorioFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private String mesFinal;
    private List<HierarquiaOrganizacional> unidades;
    private Boolean exibeFonteRecurso;
    private List<FonteDeRecursos> fontes;
    private List<Conta> contasDespesas;
    private String dataInicial;
    private String dataFinal;
    private Date dataInicio;
    private Date dataFim;
    private Exercicio exercicio;
    private String dataInicialMes;
    private String filtro;
    private ApresentacaoRelatorio apresentacao;
    private TipoRelatorio tipoRelatorio;
    private UnidadeGestora unidadeGestora;
    private List<Funcao> funcoes;
    private List<SubFuncao> subFuncoes;
    private List<ProgramaPPA> programas;
    private List<TipoAcaoPPA> tiposAcoes;
    private List<AcaoPPA> acoes;
    private List<SubAcaoPPA> subAcoes;
    private ConverterAutoComplete converterSubProjetoAtividade;
    private List<ContaDeDestinacao> contasDestinacoes;
    private boolean isPorPeriodo;
    private Boolean exibirCodigoCo;
    private Boolean exibirProgramaEProjeto;
    private Boolean exibirFuncaoSubFuncao;

    private enum TipoRelatorio {

        DETALHADO("Detalhado"),
        RESUMIDO("Resumido");
        private String descricao;

        TipoRelatorio(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public List<SelectItem> getApresentacoes() {
        List<SelectItem> toReturn = Lists.newArrayList();
        for (ApresentacaoRelatorio ap : ApresentacaoRelatorio.values()) {
            toReturn.add(new SelectItem(ap, ap.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposRelatorio() {
        List<SelectItem> toReturn = Lists.newArrayList();
        for (TipoRelatorio ta : TipoRelatorio.values()) {
            toReturn.add(new SelectItem(ta, ta.getDescricao()));
        }
        return toReturn;
    }

    @URLAction(mappingId = "relatorio-demonstrativo-despesa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        isPorPeriodo = false;
        iniciarVariaveis();
    }

    public void alterarApresentacaoECamposExibir() {
        apresentacao = ApresentacaoRelatorio.CONSOLIDADO;
        exibeFonteRecurso = false;
        exibirProgramaEProjeto = false;
        exibirFuncaoSubFuncao = false;
        exibirCodigoCo = false;
    }

    @URLAction(mappingId = "relatorio-demonstrativo-despesa-por-periodo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCamposPorPeriodo() {
        isPorPeriodo = true;
        iniciarVariaveis();
    }

    public List<Funcao> completarFuncoes(String filtro) {
        return funcaoFacade.listaFiltrandoFuncao(filtro.trim());
    }

    public List<SubFuncao> completarSubFuncoes(String filtro) {
        return subFuncaoFacade.listaFiltrandoSubFuncao(filtro);
    }

    public List<ProgramaPPA> completarProgramas(String filtro) {
        return programaPPAFacade.buscarProgramasPorExercicio(filtro, exercicio);
    }

    public List<TipoAcaoPPA> completarTiposAcoesPPA(String filtro) {
        return tipoAcaoPPAFacade.listaFiltrando(filtro.trim(), "descricao");
    }

    public List<AcaoPPA> completarAcoesPPA(String filtro) {
        return projetoAtividadeFacade.buscarAcoesPPAPorExercicio(filtro.trim(), exercicio);
    }

    public List<SubAcaoPPA> completarSubAcoesPPA(String filtro) {
        if (acoes != null) {
            return subProjetoAtividadeFacade.buscarSubPorProjetoAtividades(filtro.trim(), acoes);
        } else {
            return subProjetoAtividadeFacade.buscarSubProjetoAtividadePorExercicio(filtro.trim(), exercicio);
        }
    }

    public List<Conta> completarContasDespesa(String filtro) {
        return contaFacade.listaFiltrandoContaDespesa(filtro.trim(), exercicio);
    }

    public List<FonteDeRecursos> completarFontesDeRecursos(String filtro) {
        return fonteDeRecursosFacade.listaFiltrandoPorExercicio(filtro.trim(), exercicio);
    }

    public List<Conta> completarContasDeDestinacao(String filtro) {
        return contaFacade.buscarContasDeDestinacaoDeRecursos(filtro, exercicio);
    }

    public ConverterAutoComplete getConverterSubProjetoAtividade() {
        if (converterSubProjetoAtividade == null) {
            converterSubProjetoAtividade = new ConverterAutoComplete(SubAcaoPPA.class, subProjetoAtividadeFacade);
        }
        return converterSubProjetoAtividade;
    }

    public void limparSubAcaoPPA() {
        subAcoes = Lists.newArrayList();
    }

    private void iniciarVariaveis() {
        alterarApresentacaoECamposExibir();
        fontes = Lists.newArrayList();
        contasDestinacoes = Lists.newArrayList();
        contasDespesas = Lists.newArrayList();
        tipoRelatorio = TipoRelatorio.DETALHADO;
        dataInicio = sistemaFacade.getDataOperacao();
        dataFim = sistemaFacade.getDataOperacao();
        mesFinal = "01";
        unidades = Lists.newArrayList();
        exercicio = sistemaFacade.getExercicioCorrente();
    }

    public List<Conta> completarContas(String parte) {
        try {
            List<Conta> contas = contaFacade.listaFiltrandoDespesaCriteria(parte.trim(), exercicio);
            if (contas.isEmpty()) {
                return new ArrayList<Conta>();
            } else {
                return contas;
            }
        } catch (Exception e) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), e.getMessage());
            return new ArrayList<Conta>();
        }
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto;
            if (isPorPeriodo) {
                dto = montarRelatorioDTOPorPeriodo(tipoRelatorioExtensao);
            } else {
                dto = montarRelatorioDTO(tipoRelatorioExtensao);
            }
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private RelatorioDTO montarRelatorioDTO(String tipoRelatorioExtensao) {
        RelatorioDTO dto = new RelatorioDTO();
        HashMap parameters = new HashMap();
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        dto.adicionarParametro("ANO_EXERCICIO", exercicio.getAno().toString());
        dataInicial = "01/01/" + exercicio.getAno();
        Integer diaMes;
        if ("EX".equals(mesFinal)) {
            dataFinal = "31/12/" + exercicio.getAno();
            dataInicialMes = "01/01/" + exercicio.getAno();
            dto.adicionarParametro("FILTRO_DATA", "EXERCÍCIO");
        } else {
            diaMes = Util.getDiasMes(new Integer(mesFinal), exercicio.getAno());
            dataFinal = diaMes + "/" + mesFinal + "/" + exercicio.getAno();
            dataInicialMes = "01/" + mesFinal + "/" + exercicio.getAno();
            dto.adicionarParametro("FILTRO_DATA", Mes.getMesToInt(Integer.valueOf(mesFinal)).getDescricao());
        }
        montarFiltrosEParametros(dto, parameters, parametros);
        adicionarParametros(dto);
        dto.adicionarParametro("NOME_ARQUIVO", getNomeArquivo());
        dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
        dto.setNomeRelatorio(getNomeArquivo());
        dto.setApi("contabil/demonstrativo-despesa-por-natureza/");
        return dto;
    }

    public void limparCamposPorFiltroExercicio() {
        funcoes = Lists.newArrayList();
        subFuncoes = Lists.newArrayList();
        programas = Lists.newArrayList();
        tiposAcoes = Lists.newArrayList();
        acoes = Lists.newArrayList();
        subAcoes = Lists.newArrayList();
        contasDespesas = Lists.newArrayList();
        fontes = Lists.newArrayList();
        contasDestinacoes = Lists.newArrayList();
        unidadeGestora = null;
        unidades = Lists.newArrayList();
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (exercicio == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo exercício deve ser informado!");
        }
        ve.lancarException();
    }

    public Date getDataFinalComMesEAno() {
        return DataUtil.criarDataUltimoDiaMes("EX".equals(mesFinal) ? 12 : Integer.valueOf(mesFinal), exercicio.getAno()).toDate();
    }

    private String getNomeArquivo() {
        if (TipoRelatorio.DETALHADO.equals(tipoRelatorio)) {
            return "DDN" + "-" + ("EX".equals(mesFinal) ? "EXERCÍCIO" : Mes.getMesToInt(Integer.valueOf(mesFinal)).getDescricao().toUpperCase()) + exercicio.getAno().toString();
        } else {
            return "DDN-RESUMIDO" + "-" + ("EX".equals(mesFinal) ? "EXERCÍCIO" : Mes.getMesToInt(Integer.valueOf(mesFinal)).getDescricao().toUpperCase()) + exercicio.getAno().toString();
        }
    }

    private void montarFiltrosEParametros(RelatorioDTO dto, HashMap parameters, List<ParametrosRelatorios> parametros) {
        parametros.add(new ParametrosRelatorios(null, ":DATAINICIAL", null, null, dataInicial, null, 0, true));
        parameters.put("DATAINICIAL", dataInicial);
        parametros.add(new ParametrosRelatorios(null, ":DATAFINAL", null, null, dataFinal, null, 0, true));
        parameters.put("DATAFINAL", dataFinal);
        parametros.add(new ParametrosRelatorios(null, ":DATAINICIOMES", null, null, dataInicialMes, null, 0, true));
        parameters.put("DATAINICIOMES", dataInicialMes);
        parametros.add(new ParametrosRelatorios(null, ":EXERCICIO_ID", null, null, exercicio.getId(), null, 0, false, Long.class));
        parameters.put("EXERCICIO_ID", exercicio.getId());
        filtro = "";
        montarFiltroUnidade(parameters, parametros);
        montarFiltroGerais(parameters, parametros);
        dto.adicionarParametro("DATAINICIAL", dataInicial);
        dto.adicionarParametro("DATAFINAL", dataFinal);
        dto.adicionarParametro("DATAINICIOMES", dataInicialMes);
        dto.adicionarParametro("EXERCICIO_ID", exercicio.getId());
        dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(parametros));
        dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
        dto.adicionarParametro("apresentacaoRelatorio", apresentacao);
        dto.adicionarParametro("detalhado", TipoRelatorio.DETALHADO.equals(tipoRelatorio));
        dto.adicionarParametro("parametrosFonte", parameters);
        dto.adicionarParametro("pesquisouUnd", !unidades.isEmpty());
        atualizarFiltros();
    }

    private void atualizarFiltros() {
        if (!filtro.isEmpty()) {
            filtro = filtro.substring(0, filtro.length() - 1);
        }
    }

    private void montarFiltroGerais(HashMap parameters, List<ParametrosRelatorios> parametros) {
        adicionarFuncoes(parameters, parametros);
        adicionarSubFuncoes(parameters, parametros);
        adicionarProgramas(parameters, parametros);
        adicionarTiposAcoes(parameters, parametros);
        adicionarAcoes(parameters, parametros);
        adicionarSubAcoes(parameters, parametros);
        adicionarContasDespesas(parameters, parametros);
        adicionarFontes(parameters, parametros);
        adicionarContasDestinacoes(parameters, parametros);
        if (unidadeGestora != null) {
            parametros.add(new ParametrosRelatorios(" ug.id ", ":ugId", null, OperacaoRelatorio.IGUAL, unidadeGestora.getId(), null, 1, false));
            parameters.put("ugId", unidadeGestora.getId());
            filtro += " Unidade Gestora: " + unidadeGestora.getCodigo() + " -";
        }
    }

    private void adicionarFuncoes(HashMap parameters, List<ParametrosRelatorios> parametros) {
        if (funcoes != null && !funcoes.isEmpty()) {
            List<Long> ids = Lists.newArrayList();
            String juncao = "";
            filtro += " Função: ";
            for (Funcao funcao : funcoes) {
                ids.add(funcao.getId());
                filtro += juncao + funcao.getCodigo();
                juncao = ", ";
            }
            filtro += " -";
            parametros.add(new ParametrosRelatorios(" f.id ", ":funcaoId", null, OperacaoRelatorio.IN, ids, null, 1, false));
            parameters.put("funcaoId", ids);
        }
    }

    private void adicionarSubFuncoes(HashMap parameters, List<ParametrosRelatorios> parametros) {
        if (subFuncoes != null && !subFuncoes.isEmpty()) {
            List<Long> ids = Lists.newArrayList();
            String juncao = "";
            filtro += " Subfunção: ";
            for (SubFuncao sub : subFuncoes) {
                ids.add(sub.getId());
                filtro += juncao + sub.getCodigo();
                juncao = ", ";
            }
            filtro += " -";
            parametros.add(new ParametrosRelatorios(" sf.id ", ":subfuncaoId", null, OperacaoRelatorio.IN, ids, null, 1, false));
            parameters.put("subfuncaoId", ids);
        }
    }

    private void adicionarProgramas(HashMap parameters, List<ParametrosRelatorios> parametros) {
        if (programas != null && !programas.isEmpty()) {
            List<Long> ids = Lists.newArrayList();
            String juncao = "";
            filtro += " Programa: ";
            for (ProgramaPPA programa : programas) {
                ids.add(programa.getId());
                filtro += juncao + programa.getCodigo();
                juncao = ", ";
            }
            filtro += " -";
            parametros.add(new ParametrosRelatorios(" prog.id ", ":progId", null, OperacaoRelatorio.IN, ids, null, 1, false));
            parameters.put("progId", ids);
        }
    }

    private void adicionarTiposAcoes(HashMap parameters, List<ParametrosRelatorios> parametros) {
        if (tiposAcoes != null && !tiposAcoes.isEmpty()) {
            List<Long> ids = Lists.newArrayList();
            String juncao = "";
            filtro += " Tipo de Ação: ";
            for (TipoAcaoPPA tipo : tiposAcoes) {
                ids.add(tipo.getId());
                filtro += juncao + tipo.getCodigo();
                juncao = ", ";
            }
            filtro += " -";
            parametros.add(new ParametrosRelatorios(" tpa.id ", ":tipoAcaoId", null, OperacaoRelatorio.IN, ids, null, 1, false));
            parameters.put("tipoAcaoId", ids);
        }
    }

    private void adicionarAcoes(HashMap parameters, List<ParametrosRelatorios> parametros) {
        if (acoes != null && !acoes.isEmpty()) {
            List<Long> ids = Lists.newArrayList();
            String juncao = "";
            filtro += " Projeto/Atividade: ";
            for (AcaoPPA acao : acoes) {
                ids.add(acao.getId());
                filtro += juncao + acao.getCodigo();
                juncao = ", ";
            }
            filtro += " -";
            parametros.add(new ParametrosRelatorios(" a.id ", ":acaoId", null, OperacaoRelatorio.IN, ids, null, 1, false));
            parameters.put("acaoId", ids);
        }
    }

    private void adicionarSubAcoes(HashMap parameters, List<ParametrosRelatorios> parametros) {
        if (subAcoes != null && !subAcoes.isEmpty()) {
            List<Long> ids = Lists.newArrayList();
            String juncao = "";
            filtro += " Sub-Projeto/Atividade: ";
            for (SubAcaoPPA subAcao : subAcoes) {
                ids.add(subAcao.getId());
                filtro += juncao + subAcao.getCodigo();
                juncao = ", ";
            }
            filtro += " -";
            parametros.add(new ParametrosRelatorios(" sub.id ", ":subAcaoId", null, OperacaoRelatorio.IN, ids, null, 1, false));
            parameters.put("subAcaoId", ids);
        }
    }

    private void adicionarContasDespesas(HashMap parameters, List<ParametrosRelatorios> parametros) {
        if (contasDespesas != null && !contasDespesas.isEmpty()) {
            List<String> codigos = Lists.newArrayList();
            String juncao = "";
            filtro += " Conta de Despesa: ";
            for (Conta cd : contasDespesas) {
                codigos.add(cd.getCodigo());
                filtro += juncao + cd.getCodigo();
                juncao = ", ";
            }
            filtro += " -";
            parametros.add(new ParametrosRelatorios(" c.codigo ", ":cCodigo", null, OperacaoRelatorio.IN, codigos, null, 1, false));
            if (parameters != null) parameters.put("cCodigo", codigos);
        }
    }

    private void adicionarFontes(HashMap parameters, List<ParametrosRelatorios> parametros) {
        if (fontes != null && !fontes.isEmpty()) {
            List<Long> ids = Lists.newArrayList();
            String juncao = "";
            filtro += " Fonte de Recursos: ";
            for (FonteDeRecursos fonte : fontes) {
                ids.add(fonte.getId());
                filtro += juncao + fonte.getCodigo();
                juncao = ", ";
            }
            filtro += " -";
            parametros.add(new ParametrosRelatorios(" fr.id ", ":frId", null, OperacaoRelatorio.IN, ids, null, 1, false));
            if (parameters != null) parameters.put("frId", ids);
        }
    }

    private void adicionarContasDestinacoes(HashMap parameters, List<ParametrosRelatorios> parametros) {
        if (contasDestinacoes != null && !contasDestinacoes.isEmpty()) {
            List<Long> ids = Lists.newArrayList();
            String juncao = "";
            filtro += " Conta de Destinação de Recurso: ";
            for (ContaDeDestinacao cdd : contasDestinacoes) {
                ids.add(cdd.getId());
                filtro += juncao + cdd.getCodigo();
                juncao = ", ";
            }
            filtro += " -";
            parametros.add(new ParametrosRelatorios(" cd.id ", ":cdId", null, OperacaoRelatorio.IN, ids, null, 1, false));
            parameters.put("cdId", ids);
        }
    }

    private void montarFiltroUnidade(HashMap parametros, List<ParametrosRelatorios> listaParametros) {
        List<Long> idsUnidades = Lists.newArrayList();
        if (!unidades.isEmpty()) {
            String codigosUnidades = "";
            for (HierarquiaOrganizacional hierarquiaOrganizacional : unidades) {
                idsUnidades.add(hierarquiaOrganizacional.getSubordinada().getId());
                codigosUnidades += " " + hierarquiaOrganizacional.getCodigo().substring(3, 10) + " -";
            }
            filtro += " Unidade(s): " + codigosUnidades;
            listaParametros.add(new ParametrosRelatorios(" vw.subordinada_id", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
            parametros.put("undId", idsUnidades);
        } else if (unidadeGestora == null) {
            List<HierarquiaOrganizacional> unidadesDoUsuario = hierarquiaOrganizacionalFacade.listaHierarquiaUsuarioCorrentePorNivel("", sistemaFacade.getUsuarioCorrente(), exercicio, getDataFinalComMesEAno(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            for (HierarquiaOrganizacional hierarquiaOrganizacional : unidadesDoUsuario) {
                idsUnidades.add(hierarquiaOrganizacional.getSubordinada().getId());
            }
            if (!idsUnidades.isEmpty()) {
                listaParametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
                parametros.put("undId", idsUnidades);
            }
        }
    }

    private void adicionarParametros(RelatorioDTO dto) {
        dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
        dto.adicionarParametro("FONTES", exibeFonteRecurso);
        dto.adicionarParametro("exibirProgramaEProjeto", exibirProgramaEProjeto);
        dto.adicionarParametro("exibirFuncaoSubFuncao", exibirFuncaoSubFuncao);
        dto.adicionarParametro("exibirCodigoCo", exibirCodigoCo);
        dto.adicionarParametro("exibirProgramaEProjeto", exibirProgramaEProjeto);
        dto.adicionarParametro("exibirFuncaoSubFuncao", exibirFuncaoSubFuncao);
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("USER", sistemaFacade.getUsuarioCorrente().getNome(), false);
        dto.adicionarParametro("FILTRO", filtro);
        dto.adicionarParametro("APRESENTACAO", apresentacao.name());
    }

    private RelatorioDTO montarRelatorioDTOPorPeriodo(String tipoRelatorioExtensao) {
        RelatorioDTO dto = new RelatorioDTO();
        dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
        dto.adicionarParametro("ANO_EXERCICIO", exercicio.getAno().toString());
        dto.adicionarParametro("FILTRO_DATA", DataUtil.getDataFormatada(dataInicio) + " a " + DataUtil.getDataFormatada(dataFim));
        dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
        dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
        dto.adicionarParametro("apresentacaoRelatorio", apresentacao);
        dto.adicionarParametro("NOME_ARQUIVO", nomeArquivoPorPeriodo());
        adicionarParametros(dto);
        dto.setNomeRelatorio(nomeArquivoPorPeriodo());
        dto.setApi("contabil/demonstrativo-despesa-por-natureza-e-periodo/");
        return dto;
    }

    private String nomeArquivoPorPeriodo() {
        return "DDN-PORPERIODO" + "-" + DataUtil.getDataFormatada(dataInicio, "ddMM") + "A" + DataUtil.getDataFormatada(dataFim, "ddMM") + exercicio.toString();
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        parametros.add(new ParametrosRelatorios(null, ":DATAINICIAL", null, null, DataUtil.getDataFormatada(dataInicio), null, 0, true));
        parametros.add(new ParametrosRelatorios(null, ":DATAFINAL", null, null, DataUtil.getDataFormatada(dataFim), null, 0, true));
        parametros.add(new ParametrosRelatorios(null, ":EXERCICIO_ID", null, null, exercicio.getId(), null, 0, false));
        filtro = "";
        montarFiltroUnidadePorPeriodo(parametros);
        montarFiltroGeraisPorPeriodo(parametros);
        atualizarFiltros();
        return parametros;
    }


    private void montarFiltroGeraisPorPeriodo(List<ParametrosRelatorios> listaParametros) {
        adicionarFontes(null, listaParametros);
        adicionarContasDespesas(null, listaParametros);
        if (unidadeGestora != null) {
            listaParametros.add(new ParametrosRelatorios(" ug.id ", ":ugId", null, OperacaoRelatorio.IGUAL, unidadeGestora.getId(), null, 1, false));
            filtro += " Unidade Gestora: " + unidadeGestora.getCodigo() + " -";
        }
    }

    private void montarFiltroUnidadePorPeriodo(List<ParametrosRelatorios> listaParametros) {
        List<Long> idsUnidades = Lists.newArrayList();
        if (!unidades.isEmpty()) {
            String codigosUnidades = "";
            for (HierarquiaOrganizacional hierarquiaOrganizacional : unidades) {
                idsUnidades.add(hierarquiaOrganizacional.getSubordinada().getId());
                codigosUnidades += " " + hierarquiaOrganizacional.getCodigo().substring(3, 10) + " -";
            }
            filtro += " Unidade(s): " + codigosUnidades;
            listaParametros.add(new ParametrosRelatorios(" vw.subordinada_id", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
        } else if (unidadeGestora == null) {
            List<HierarquiaOrganizacional> unidadesDoUsuario = hierarquiaOrganizacionalFacade.listaHierarquiaUsuarioCorrentePorNivel("", sistemaFacade.getUsuarioCorrente(), exercicio, sistemaFacade.getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            for (HierarquiaOrganizacional hierarquiaOrganizacional : unidadesDoUsuario) {
                idsUnidades.add(hierarquiaOrganizacional.getSubordinada().getId());
            }
            if (!idsUnidades.isEmpty()) {
                listaParametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
            }
        }
    }

    public String getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(String mesFinal) {
        this.mesFinal = mesFinal;
    }

    public List<HierarquiaOrganizacional> getUnidades() {
        return unidades;
    }

    public void setUnidades(List<HierarquiaOrganizacional> unidades) {
        this.unidades = unidades;
    }

    public Boolean getExibeFonteRecurso() {
        return exibeFonteRecurso;
    }

    public void setExibeFonteRecurso(Boolean exibeFonteRecurso) {
        this.exibeFonteRecurso = exibeFonteRecurso;
    }

    public List<FonteDeRecursos> getFontes() {
        return fontes;
    }

    public void setFontes(List<FonteDeRecursos> fontes) {
        this.fontes = fontes;
    }

    public String getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(String dataInicial) {
        this.dataInicial = dataInicial;
    }

    public String getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(String dataFinal) {
        this.dataFinal = dataFinal;
    }

    public String getDataInicialMes() {
        return dataInicialMes;
    }

    public void setDataInicialMes(String dataInicialMes) {
        this.dataInicialMes = dataInicialMes;
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

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public List<Conta> getContasDespesas() {
        return contasDespesas;
    }

    public void setContasDespesas(List<Conta> contasDespesas) {
        this.contasDespesas = contasDespesas;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public TipoRelatorio getTipoRelatorio() {
        return tipoRelatorio;
    }

    public void setTipoRelatorio(TipoRelatorio tipoRelatorio) {
        this.tipoRelatorio = tipoRelatorio;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public List<Funcao> getFuncoes() {
        return funcoes;
    }

    public void setFuncoes(List<Funcao> funcoes) {
        this.funcoes = funcoes;
    }

    public List<SubFuncao> getSubFuncoes() {
        return subFuncoes;
    }

    public void setSubFuncoes(List<SubFuncao> subFuncoes) {
        this.subFuncoes = subFuncoes;
    }

    public List<ProgramaPPA> getProgramas() {
        return programas;
    }

    public void setProgramas(List<ProgramaPPA> programas) {
        this.programas = programas;
    }

    public List<TipoAcaoPPA> getTiposAcoes() {
        return tiposAcoes;
    }

    public void setTiposAcoes(List<TipoAcaoPPA> tiposAcoes) {
        this.tiposAcoes = tiposAcoes;
    }

    public List<AcaoPPA> getAcoes() {
        return acoes;
    }

    public void setAcoes(List<AcaoPPA> acoes) {
        this.acoes = acoes;
    }

    public List<SubAcaoPPA> getSubAcoes() {
        return subAcoes;
    }

    public void setSubAcoes(List<SubAcaoPPA> subAcoes) {
        this.subAcoes = subAcoes;
    }

    public void setConverterSubProjetoAtividade(ConverterAutoComplete converterSubProjetoAtividade) {
        this.converterSubProjetoAtividade = converterSubProjetoAtividade;
    }

    public List<ContaDeDestinacao> getContasDestinacoes() {
        return contasDestinacoes;
    }

    public void setContasDestinacoes(List<ContaDeDestinacao> contasDestinacoes) {
        this.contasDestinacoes = contasDestinacoes;
    }

    public Boolean getExibirCodigoCo() {
        return exibirCodigoCo == null ? Boolean.FALSE : exibirCodigoCo;
    }

    public void setExibirCodigoCo(Boolean exibirCodigoCo) {
        this.exibirCodigoCo = exibirCodigoCo;
    }

    public Boolean getExibirProgramaEProjeto() {
        return exibirProgramaEProjeto;
    }

    public void setExibirProgramaEProjeto(Boolean exibirProgramaEProjeto) {
        this.exibirProgramaEProjeto = exibirProgramaEProjeto;
    }

    public Boolean getExibirFuncaoSubFuncao() {
        return exibirFuncaoSubFuncao;
    }

    public void setExibirFuncaoSubFuncao(Boolean exibirFuncaoSubFuncao) {
        this.exibirFuncaoSubFuncao = exibirFuncaoSubFuncao;
    }
}
