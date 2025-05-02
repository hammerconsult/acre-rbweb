/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.ComponenteTreeDespesaORC;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author reidocrime
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-empenho", pattern = "/relatorio/empenho/", viewId = "/faces/financeiro/relatorio/relatoriofiltroempenho.xhtml")})
@ManagedBean
public class RelatorioEmpenhoControlador {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private EmpenhoFacade empenhoFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private HistoricoContabilFacade historicoContabilFacade;
    @EJB
    private FuncaoFacade funcaoFacade;
    @EJB
    private SubFuncaoFacade subFuncaoFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ClasseCredorFacade classeCredorFacade;
    @EJB
    private ProgramaPPAFacade programaPPAFacade;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    @EJB
    private AcaoPrincipalFacade acaoPrincipalFacade;
    @EJB
    private SubProjetoAtividadeFacade subProjetoAtividadeFacade;
    @EJB
    private ContratoFacade contratoFacade;
    private ConverterAutoComplete converterSubProjetoAtividade;
    private Conta contaDespesa;
    private Conta contaDeDestinacao;
    private Date dataInicial;
    private Date dataFinal;
    private Date dataReferencia;
    private ClasseCredor classeCredor;
    private Pessoa fornecedor;
    private String filtros;
    private ProgramaPPA programaPPA;
    private SubAcaoPPA subAcaoPPA;
    private AcaoPrincipal acaoPrincipal;
    private TipoRelatorio tipoRelatorio;
    private Funcao funcao;
    private SubFuncao subFuncao;
    private List<HierarquiaOrganizacional> unidades;
    private Boolean pendente;
    private FonteDeRecursos fonteDeRecursos;
    private UnidadeGestora unidadeGestora;
    private ApresentacaoRelatorio apresentacao;
    @ManagedProperty(name = "componenteTreeDespesaORC", value = "#{componente}")
    private ComponenteTreeDespesaORC componenteTreeDespesaORC;
    private TipoEmpenho tipoEmpenho;
    private ModalidadeLicitacaoEmpenho modalidadeLicitacaoEmpenho;
    private Contrato contrato;

    public List<SelectItem> getListaApresentacao() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (ApresentacaoRelatorio ap : ApresentacaoRelatorio.values()) {
            toReturn.add(new SelectItem(ap, ap.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getListaTipoEmpenho() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, "Todos"));
        for (TipoEmpenho te : TipoEmpenho.values()) {
            toReturn.add(new SelectItem(te, te.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getListModalidadeLicitacaoEmpenho() {
        List<SelectItem> items = Lists.newArrayList();
        items.add(new SelectItem(null, ""));
        for (ModalidadeLicitacaoEmpenho modalide : ModalidadeLicitacaoEmpenho.values()) {
            items.add(new SelectItem(modalide, modalide.getDescricao()));
        }
        return items;
    }

    public List<Pessoa> buscarFornecedor(String parte) {
        return pessoaFacade.listaTodasPessoasPorId(parte.trim());
    }

    public List<ClasseCredor> buscarClasseCredor(String parte) {
        return classeCredorFacade.listaFiltrandoDescricao(parte.trim());
    }

    @URLAction(mappingId = "relatorio-empenho", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        dataInicial = DataUtil.montaData(1, 0, sistemaFacade.getExercicioCorrente().getAno()).getTime();
        dataFinal = sistemaFacade.getDataOperacao();
        dataReferencia = sistemaFacade.getDataOperacao();
        apresentacao = ApresentacaoRelatorio.CONSOLIDADO;
        fornecedor = null;
        limparFiltros();
        funcao = null;
        subFuncao = null;
        filtros = "";
        tipoRelatorio = TipoRelatorio.RESUMIDO;
        componenteTreeDespesaORC.setCodigo("");
        componenteTreeDespesaORC.setDespesaORCSelecionada(new DespesaORC());
        componenteTreeDespesaORC.setDespesaSTR("");
        unidades = Lists.newArrayList();
        pendente = Boolean.FALSE;
    }

    public void limparFiltros() {
        programaPPA = null;
        subAcaoPPA = null;
        acaoPrincipal = null;
        fonteDeRecursos = null;
        contaDespesa = null;
        contaDeDestinacao = null;
        contrato = null;
    }

    public List<SelectItem> getTiposDeRelatorio() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoRelatorio t : TipoRelatorio.values()) {
            toReturn.add(new SelectItem(t, t.descricao));
        }

        return toReturn;
    }

    public Exercicio buscarExercicioPelaDataFinal() {
        if (dataFinal != null) {
            return exercicioFacade.recuperarExercicioPeloAno(DataUtil.getAno(dataFinal));
        }
        return sistemaFacade.getExercicioCorrente();
    }

    public List<Conta> buscarContas(String parte) {
        Exercicio exDataFinal = buscarExercicioPelaDataFinal();
        if (componenteTreeDespesaORC != null &&
            componenteTreeDespesaORC.getDespesaORCSelecionada() != null &&
            componenteTreeDespesaORC.getDespesaORCSelecionada().getId() != null) {
            return contaFacade.buscarContasFilhasDespesaORCPorTipo(
                parte.trim(),
                componenteTreeDespesaORC.getDespesaORCSelecionada().getProvisaoPPADespesa().getContaDeDespesa(),
                exDataFinal,
                null,
                false);
        } else {
            return contaFacade.listaFiltrandoContaDespesa(parte.trim(), exDataFinal);
        }
    }

    public List<Conta> buscarContasDeDestinacao(String parte) {
        return contaFacade.buscarContasDeDestinacaoDeRecursos(parte.trim(), buscarExercicioPelaDataFinal());
    }

    public List<FonteDeRecursos> completarFontesDeRecursos(String parte) {
        return fonteDeRecursosFacade.listaFiltrandoPorExercicio(parte.trim(), buscarExercicioPelaDataFinal());
    }

    public List<ProgramaPPA> buscarProgramasPorExercicio(String filtro) {
        return programaPPAFacade.buscarProgramasPorExercicio(filtro, buscarExercicioPelaDataFinal());
    }

    public List<SubAcaoPPA> completarSubAcoesPPA(String filtro) {
        return subProjetoAtividadeFacade.buscarSubProjetoAtividadePorExercicio(filtro.trim(), sistemaFacade.getExercicioCorrente());
    }

    public List<AcaoPrincipal> completarAcoesPrincipais(String filtro) {
        return acaoPrincipalFacade.buscarAcaoPPAPorExercicio(filtro, buscarExercicioPelaDataFinal());
    }

    public List<Contrato> completarContratos(String parte) {
        return contratoFacade.buscarContratos(parte.trim());
    }

    public ConverterAutoComplete getConverterSubAcaoPPA() {
        if (converterSubProjetoAtividade == null) {
            converterSubProjetoAtividade = new ConverterAutoComplete(SubAcaoPPA.class, subProjetoAtividadeFacade);
        }
        return converterSubProjetoAtividade;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarDatas();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.adicionarParametro("EXERCICIO", buscarExercicioPelaDataFinal().getAno() + "");
            dto.adicionarParametro("TIPO_EMPENHO", "Relatório de Empenho " + (tipoEmpenho != null ? tipoEmpenho.getDescricao() : ""));
            dto.adicionarParametro("apresentacao", apresentacao.getToDto());
            dto.adicionarParametro("pesquisouUg", unidadeGestora != null);
            dto.adicionarParametro("DATAREFERENCIA", DataUtil.getDataFormatada(dataReferencia));
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
            dto.adicionarParametro("FILTROS", filtros);
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi(TipoRelatorio.RESUMIDO.equals(tipoRelatorio) ? "contabil/empenho/" : "contabil/empenho/detalhado/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public String getNomeRelatorio() {
        return TipoRelatorio.RESUMIDO.equals(tipoRelatorio) ? ("RELATORIO-EMPENHO-RESUMIDO-" + DataUtil.getAno(dataInicial) + DataUtil.getMes(dataInicial)
            + "A" + DataUtil.getAno(dataFinal) + DataUtil.getMes(dataFinal)) :
            ("RELATORIO-EMPENHO-DETALHADO-" + DataUtil.getAno(dataInicial) + DataUtil.getMes(dataInicial)
                + "A" + DataUtil.getAno(dataFinal) + DataUtil.getMes(dataFinal));
    }

    private void validarDatas() {
        ValidacaoException ve = new ValidacaoException();

        if (this.dataInicial == null || this.dataFinal == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Favor informar um intervalo de datas.");
        }

        if (this.dataReferencia == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Referência deve ser informado.");
        }

        if ((this.dataInicial != null && this.dataFinal != null) && this.dataInicial.after(this.dataFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Data Inicial não pode ser maior que a Data Final.");

        }
        SimpleDateFormat formato = new SimpleDateFormat("yyyy");
        if ((this.dataInicial != null && this.dataFinal != null)
            && formato.format(dataInicial).compareTo(formato.format(dataFinal)) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("As datas estão com exercícios diferentes.");

        }
        ve.lancarException();
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public List<Funcao> buscarFuncao(String parte) {
        return funcaoFacade.listaFiltrandoFuncao(parte);
    }

    public List<SubFuncao> buscarSubFuncao(String parte) {
        return subFuncaoFacade.listaFiltrandoSubFuncao(parte);
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        parametros.add(new ParametrosRelatorios(" trunc(EMP.DATAEMPENHO) ", ":DataInicial", ":DataFinal", OperacaoRelatorio.BETWEEN, DataUtil.getDataFormatada(dataInicial), DataUtil.getDataFormatada(dataFinal), 1, true));
        filtros = " Período: " + DataUtil.getDataFormatada(dataInicial) + " a " + DataUtil.getDataFormatada(dataFinal) + " -";
        filtros += " Data de Referência: " + DataUtil.getDataFormatada(dataReferencia) + " -";
        if (!unidades.isEmpty()) {
            List<Long> idsUnidades = Lists.newArrayList();
            StringBuilder unidadesString = new StringBuilder();
            for (HierarquiaOrganizacional hierarquia : unidades) {
                idsUnidades.add(hierarquia.getSubordinada().getId());
                unidadesString.append(" ").append((hierarquia.getCodigo().substring(3, 10))).append(" -");
            }
            filtros += " Unidade(s): " + unidadesString.toString();

            parametros.add(new ParametrosRelatorios(" vw.subordinada_id", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
        } else if (unidadeGestora == null) {
            List<HierarquiaOrganizacional> unidadesDoUsuario = hierarquiaOrganizacionalFacade.listaHierarquiaUsuarioCorrentePorNivel("", sistemaFacade.getUsuarioCorrente(), buscarExercicioPelaDataFinal(), sistemaFacade.getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            List<Long> idsUnidades = new ArrayList<>();
            for (HierarquiaOrganizacional hierarquia : unidadesDoUsuario) {
                idsUnidades.add(hierarquia.getSubordinada().getId());
            }
            if (!idsUnidades.isEmpty()) {
                parametros.add(new ParametrosRelatorios(" vw.subordinada_id ", ":undId", null, OperacaoRelatorio.IN, idsUnidades, null, 1, false));
            }
        }
        if (fornecedor != null) {
            parametros.add(new ParametrosRelatorios(" P.ID ", ":fornecedorId", null, OperacaoRelatorio.IGUAL, fornecedor.getId(), null, 1, false));
            filtros += " Pessoa: " + fornecedor.getNome() + " -";
        }

        if (classeCredor != null) {
            parametros.add(new ParametrosRelatorios(" cc.ID ", ":classeId", null, OperacaoRelatorio.IGUAL, classeCredor.getId(), null, 1, false));
            filtros += " Classe:  " + classeCredor.getCodigo() + " -";
        }

        DespesaORC desp = componenteTreeDespesaORC.getDespesaORCSelecionada();
        if (desp != null) {
            if (desp.getId() != null) {
                parametros.add(new ParametrosRelatorios(" D.ID ", ":despesaId", null, OperacaoRelatorio.IGUAL, desp.getId(), null, 1, false));
                filtros += " Despesa Orçamentaria: " + desp.getCodigo() + " -";
            }
        }

        if (this.funcao != null) {
            parametros.add(new ParametrosRelatorios(" F.ID ", ":funcaoId", null, OperacaoRelatorio.IGUAL, funcao.getId(), null, 1, false));
            filtros += " Função: " + funcao + " -";
        }

        if (this.subFuncao != null) {
            parametros.add(new ParametrosRelatorios(" SF.ID ", ":subFuncaoId", null, OperacaoRelatorio.IGUAL, subFuncao.getId(), null, 1, false));
            filtros += " Subfunção: " + subFuncao + " -";
        }

        if (programaPPA != null) {
            parametros.add(new ParametrosRelatorios(" PROG.ID", ":idPrograma", null, OperacaoRelatorio.IGUAL, programaPPA.getId(), null, 1, false));
            filtros += " Programa: " + programaPPA + " -";
        }

        if (acaoPrincipal != null) {
            parametros.add(new ParametrosRelatorios(" Ac.ID", ":idAcao", null, OperacaoRelatorio.IGUAL, acaoPrincipal.getId(), null, 1, false));
            filtros += " Projeto/Atividade: " + acaoPrincipal + " -";
        }

        if (subAcaoPPA != null) {
            parametros.add(new ParametrosRelatorios(" SUB.ID", ":idProjetoAtividade", null, OperacaoRelatorio.IGUAL, subAcaoPPA.getId(), null, 1, false));
            filtros += " Sub-Projeto/Atividade: " + subAcaoPPA.toStringAutoCompleteRelatorio() + " -";
        }

        if (pendente) {
            StringBuilder sqlEmpenhoPendente = new StringBuilder();
            sqlEmpenhoPendente.append(" ( emp.valor - coalesce((select sum(est.valor) from empenhoestorno est where est.empenho_id = emp.id and trunc(est.dataestorno) <= to_date(:dataReferencia, 'dd/mm/yyyy')), 0) ");
            sqlEmpenhoPendente.append(" - coalesce((select sum(pag.valor) from pagamento pag ");
            sqlEmpenhoPendente.append("             inner join liquidacao liq on liq.id = pag.liquidacao_id where liq.empenho_id = emp.id and trunc(pag.dataPagamento) <= to_date(:dataReferencia, 'dd/mm/yyyy')), 0) ");
            sqlEmpenhoPendente.append(" + coalesce((select sum(est.valor) from pagamentoestorno est ");
            sqlEmpenhoPendente.append("             inner join pagamento pag on pag.id = est.pagamento_id ");
            sqlEmpenhoPendente.append("             inner join liquidacao liq on liq.id = pag.liquidacao_id where liq.empenho_id = emp.id and trunc(est.dataestorno) <= to_date(:dataReferencia, 'dd/mm/yyyy')), 0) ");
            sqlEmpenhoPendente.append(" ) > 0  ");
            parametros.add(new ParametrosRelatorios(sqlEmpenhoPendente.toString(), "", null, null, "", null, 5, false));
            filtros += " Empenhos Pendentes -";
        }

        if (contaDespesa != null) {
            parametros.add(new ParametrosRelatorios(" contaDesdob.codigo ", ":cCodigo", null, OperacaoRelatorio.LIKE, contaDespesa.getCodigoSemZerosAoFinal() + "%", null, 1, false));
            filtros += " Conta de Despesa: " + contaDespesa.getCodigo() + " -";
        }

        if (unidadeGestora != null) {
            parametros.add(new ParametrosRelatorios(" ug.codigo ", ":ugCodigo", null, OperacaoRelatorio.IGUAL, unidadeGestora.getCodigo(), null, 1, false));
            filtros += " Unidade Gestora: " + unidadeGestora.getCodigo() + " -";
        }
        if (unidadeGestora != null || apresentacao.isApresentacaoUnidadeGestora()) {
            SimpleDateFormat formato = new SimpleDateFormat("yyyy");
            Exercicio exercicio = exercicioFacade.recuperarExercicioPeloAno(new Integer(formato.format(dataInicial)));
            parametros.add(new ParametrosRelatorios(null, ":exercicio", null, null, exercicio.getId(), null, 0, false));
        }

        if (fonteDeRecursos != null) {
            parametros.add(new ParametrosRelatorios(" fonte.id ", ":fonteCodigo", null, OperacaoRelatorio.IGUAL, fonteDeRecursos.getId(), null, 1, false));
            filtros += " Fonte de Recursos: " + fonteDeRecursos.getCodigo() + " -";
        }
        if (contaDeDestinacao != null) {
            parametros.add(new ParametrosRelatorios(" contad.id ", ":contaDestinacao", null, OperacaoRelatorio.IGUAL, contaDeDestinacao.getId(), null, 1, false));
            filtros += " Conta de Destinação de Recurso: " + contaDeDestinacao.getCodigo() + " -";
        }

        if (this.modalidadeLicitacaoEmpenho != null) {
            parametros.add(new ParametrosRelatorios(" EMP.MODALIDADELICITACAO ", ":modalidade", null, OperacaoRelatorio.IGUAL, modalidadeLicitacaoEmpenho.name(), null, 1, false));
            filtros += " Modalidade: " + modalidadeLicitacaoEmpenho.getDescricao() + " -";
        }
        if (tipoEmpenho != null) {
            parametros.add(new ParametrosRelatorios(" emp.tipoempenho ", ":tipoEmp", null, OperacaoRelatorio.IGUAL, tipoEmpenho.name(), null, 1, false));
        }

        if (contrato != null) {
            parametros.add(new ParametrosRelatorios(" emp.contrato_id ", ":idContrato", null, OperacaoRelatorio.IGUAL, contrato.getId(), null, 1, false));
            filtros += " Contrato: " + contrato.toString() + " -";
        }

        filtros = filtros.substring(0, filtros.length() - 1);
        parametros.add(new ParametrosRelatorios(" trunc(EST.DATAESTORNO) ", ":dataReferencia", null, OperacaoRelatorio.MENOR_IGUAL, DataUtil.getDataFormatada(dataReferencia), null, 2, true));
        parametros.add(new ParametrosRelatorios(" trunc(LIQ.DATALIQUIDACAO) ", ":dataReferencia", null, OperacaoRelatorio.MENOR_IGUAL, DataUtil.getDataFormatada(dataReferencia), null, 3, true));
        parametros.add(new ParametrosRelatorios(" trunc(PAG.DATAPAGAMENTO) ", ":dataReferencia", null, OperacaoRelatorio.MENOR_IGUAL, DataUtil.getDataFormatada(dataReferencia), null, 4, true));
        return parametros;
    }

    public Pessoa getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Pessoa fornecedor) {
        this.fornecedor = fornecedor;
    }

    public ComponenteTreeDespesaORC getComponenteTreeDespesaORC() {
        return componenteTreeDespesaORC;
    }

    public void setComponenteTreeDespesaORC(ComponenteTreeDespesaORC componenteTreeDespesaORC) {
        this.componenteTreeDespesaORC = componenteTreeDespesaORC;
    }

    public Funcao getFuncao() {
        return funcao;
    }

    public void setFuncao(Funcao funcao) {
        this.funcao = funcao;
    }

    public SubFuncao getSubFuncao() {
        return subFuncao;
    }

    public void setSubFuncao(SubFuncao subFuncao) {
        this.subFuncao = subFuncao;
    }

    public Date getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(Date dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    public TipoRelatorio getTipoRelatorio() {
        return tipoRelatorio;
    }

    public void setTipoRelatorio(TipoRelatorio tipoRelatorio) {
        this.tipoRelatorio = tipoRelatorio;
    }

    public List<HierarquiaOrganizacional> getUnidades() {
        return unidades;
    }

    public void setUnidades(List<HierarquiaOrganizacional> unidades) {
        this.unidades = unidades;
    }

    public Boolean getPendente() {
        return pendente;
    }

    public void setPendente(Boolean pendente) {
        this.pendente = pendente;
    }

    public UnidadeGestora getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(UnidadeGestora unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public ApresentacaoRelatorio getApresentacao() {
        return apresentacao;
    }

    public void setApresentacao(ApresentacaoRelatorio apresentacao) {
        this.apresentacao = apresentacao;
    }

    public String getFiltros() {
        return filtros;
    }

    public void setFiltros(String filtros) {
        this.filtros = filtros;
    }

    public Conta getContaDespesa() {
        return contaDespesa;
    }

    public void setContaDespesa(Conta contaDespesa) {
        this.contaDespesa = contaDespesa;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public TipoEmpenho getTipoEmpenho() {
        return tipoEmpenho;
    }

    public void setTipoEmpenho(TipoEmpenho tipoEmpenho) {
        this.tipoEmpenho = tipoEmpenho;
    }

    public ClasseCredor getClasseCredor() {
        return classeCredor;
    }

    public void setClasseCredor(ClasseCredor classeCredor) {
        this.classeCredor = classeCredor;
    }

    public AcaoPrincipal getAcaoPrincipal() {
        return acaoPrincipal;
    }

    public void setAcaoPrincipal(AcaoPrincipal acaoPrincipal) {
        this.acaoPrincipal = acaoPrincipal;
    }

    public ProgramaPPA getProgramaPPA() {
        return programaPPA;
    }

    public void setProgramaPPA(ProgramaPPA programaPPA) {
        this.programaPPA = programaPPA;
    }

    public ModalidadeLicitacaoEmpenho getModalidadeLicitacaoEmpenho() {
        return modalidadeLicitacaoEmpenho;
    }

    public void setModalidadeLicitacaoEmpenho(ModalidadeLicitacaoEmpenho modalidadeLicitacaoEmpenho) {
        this.modalidadeLicitacaoEmpenho = modalidadeLicitacaoEmpenho;
    }

    public SubAcaoPPA getSubAcaoPPA() {
        return subAcaoPPA;
    }

    public void setSubAcaoPPA(SubAcaoPPA subAcaoPPA) {
        this.subAcaoPPA = subAcaoPPA;
    }

    public Conta getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(Conta contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    private enum TipoRelatorio {

        DETALHE("Detalhado"),
        RESUMIDO("Resumido");
        private String descricao;

        TipoRelatorio(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}
