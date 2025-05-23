/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.rh.relatorios.TipoAgrupadorRelatorio;
import br.com.webpublico.enums.rh.relatorios.TipoContribuicao;
import br.com.webpublico.enums.rh.relatorios.TipoLotacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.comum.ConfiguracaoDeRelatorioFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.*;
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
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.*;

@ViewScoped
@ManagedBean
@URLMappings(mappings = {
    @URLMapping(id = "folha-por-secretaria-recurso-vinculoFP", pattern = "/relatorio/folha-por-secretaria-recurso-vinculoFP/", viewId = "/faces/rh/relatorios/relatoriofolhaporsecretariarecvincfp.xhtml")
})
public class RelatorioFolhaPorSecretariaRecVincFPControlador implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(RelatorioFolhaPorSecretariaRecVincFPControlador.class);
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ConfiguracaoDeRelatorioFacade configuracaoDeRelatorioFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    private CompetenciaFPFacade competenciaFPFacade;
    @EJB
    private RecursoFPFacade recursoFPFacade;
    @EJB
    private GrupoRecursoFPFacade grupoRecursoFPFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    public Integer versao;
    private Integer mes;
    private Integer ano;
    private TipoFolhaDePagamento tipoFolhaDePagamento;
    private TipoAgrupadorRelatorio tipoAgrupadorRelatorio;
    private Boolean relatorioAgrupado;
    private TipoLotacao tipoLotacao;
    private HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada;
    private ConverterAutoComplete converterHierarquia;
    private List<HierarquiaOrganizacional> listaTodasHierarquias;
    private String filtros;
    private List<RecursoFP> recursos;
    private List<GrupoRecursoFP> listaGrupoRecursoFPs;
    private TipoContribuicao tipoContribuicao;
    private Entidade entidade;
    private TipoSelecao tipoSelecao;

    public RelatorioFolhaPorSecretariaRecVincFPControlador() {
    }

    public void limparCampos() {
        inicializarInformacoesTela();
        inicializarObjetos();
    }

    private void inicializarObjetos() {
        relatorioAgrupado = Boolean.TRUE;
        recursos = new ArrayList<>();
        listaGrupoRecursoFPs = new ArrayList<>();
        recursos = Lists.newLinkedList();
        listaTodasHierarquias = Lists.newLinkedList();
        listaGrupoRecursoFPs = Lists.newLinkedList();
        recuperarHierarquias();
        recuperarRecursosFP();
        recuperarGrupoRecursosFP();
        listaTodasHierarquias = new ArrayList<>();
    }

    public void limparCamposPadrao() {
        filtros = "";
        mes = null;
        ano = null;
        tipoFolhaDePagamento = null;
        tipoAgrupadorRelatorio = null;
        tipoLotacao = null;
        hierarquiaOrganizacionalSelecionada = null;
        definirMesAnoPorCompetenciaAberta();
        listaTodasHierarquias = new ArrayList<>();
    }


    @URLAction(mappingId = "folha-por-secretaria-recurso-vinculoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        if (!FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().isEmpty()) {
            gerarRelatorioParametrosUrl();
        } else {
            inicializarInformacoesTela();
        }
        inicializarObjetos();
    }

    private void inicializarInformacoesTela() {
        filtros = "";
        mes = null;
        ano = null;
        tipoFolhaDePagamento = null;
        tipoAgrupadorRelatorio = null;
        tipoLotacao = null;
        hierarquiaOrganizacionalSelecionada = null;
        definirMesAnoPorCompetenciaAberta();
        tipoSelecao = TipoSelecao.TIPO_LOTACAO;
    }

    private void gerarRelatorioParametrosUrl() {
        if (buscarParametrosURL("ano") != null && buscarParametrosURL("mes") != null &&
            buscarParametrosURL("tipo") != null && buscarParametrosURL("versao") != null &&
            buscarParametrosURL("orgao") != null) {
            ano = Integer.valueOf(buscarParametrosURL("ano"));
            mes = Integer.valueOf(buscarParametrosURL("mes"));
            tipoFolhaDePagamento = TipoFolhaDePagamento.valueOf(buscarParametrosURL("tipo"));
            versao = Integer.valueOf(buscarParametrosURL("versao"));
            HierarquiaOrganizacional hierarquia = (hierarquiaOrganizacionalFacade.
                recuperaHierarquiaOrganizacionalPelaUnidade(Long.valueOf(buscarParametrosURL("orgao"))));
            if (hierarquia != null) {
                tipoAgrupadorRelatorio = TipoAgrupadorRelatorio.POR_ORGAO;
                tipoLotacao = TipoLotacao.LOTACAO_FUNCIONAL;
                this.hierarquiaOrganizacionalSelecionada = hierarquia;
            }
        } else {
            limparCamposPadrao();
        }
    }

    private String buscarParametrosURL(String ano) {
        return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(ano);
    }

    public boolean isRelatorioGeralAgrupadoLotacaoFuncional() {
        if (TipoAgrupadorRelatorio.GERAL.equals(tipoAgrupadorRelatorio)
            && relatorioAgrupado
            && TipoLotacao.LOTACAO_FUNCIONAL.equals(tipoLotacao)) {
            return true;
        }
        return false;
    }

    public boolean isRelatorioGeralAgrupadoPorRecursoFP() {
        if (TipoAgrupadorRelatorio.GERAL.equals(tipoAgrupadorRelatorio)
            && relatorioAgrupado
            && TipoLotacao.RECURSO_FP.equals(tipoLotacao)) {
            return true;
        }
        return false;
    }

    public boolean isRelatorioGeralAgrupadoPorGrupoRecursoFP() {
        if (TipoAgrupadorRelatorio.GERAL.equals(tipoAgrupadorRelatorio)
            && relatorioAgrupado
            && TipoLotacao.GRUPO_RECURSO_FP.equals(tipoLotacao)) {
            return true;
        }
        return false;
    }

    private List<Long> adicionarIdsParaRecursoFp() {
        List<Long> ids = new ArrayList<>();
        String codigo = " ";
        if (recuperarSomenteRecursosSelecionados() != null) {
            for (RecursoFP rec : recuperarSomenteRecursosSelecionados()) {
                ids.add(rec.getId());
                codigo += rec.getCodigo() + ", ";
            }
            filtros += "Recurso(s) FP: " + codigo;
        }
        removerSeparadorFiltro();
        return ids;
    }


    private List<Long> adicionarIdsParaRecursoFpDoGrupoRecurso() {
        List<Long> ids = new ArrayList<>();
        String descricao = " ";
        if (recuperarSomenteGrupoRecursosSelecionados() != null) {
            for (GrupoRecursoFP grupoRecursoFP : recuperarSomenteGrupoRecursosSelecionados()) {
                ids.add(grupoRecursoFP.getId());
                descricao += grupoRecursoFP.getNome() + ", ";
            }
            filtros += "Grupo de Recurso(s) FP: " + descricao;
        }
        removerSeparadorFiltro();
        return ids;
    }

    private void removerSeparadorFiltro() {
        filtros = filtros.length() == 0 ? " " : filtros.substring(0, filtros.length() - 2);
    }

    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }

    public List<SelectItem> getVersoes() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        if (mes != null && ano != null && tipoFolhaDePagamento != null && isMesValido()) {
            for (Integer versao : folhaDePagamentoFacade.recuperarVersao(Mes.getMesToInt(mes), ano, tipoFolhaDePagamento)) {
                retorno.add(new SelectItem(versao, String.valueOf(versao)));
            }
        }
        return retorno;
    }

    private List<ParametrosRelatorios> buscarFiltrosSummary() {
        filtros = "";
        List<ParametrosRelatorios> parametros = atribuirParametrosRelatorio();
        if (!recuperarSomenteRecursosSelecionados().isEmpty()) {
            parametros.add(new ParametrosRelatorios(" rec.id ", ":recursosId", null, OperacaoRelatorio.IN, adicionarIdsParaRecursoFp(), null, 1, false));
        }
        if (!recuperarSomenteGrupoRecursosSelecionados().isEmpty()) {
            parametros.add(new ParametrosRelatorios(" grupo.id ", ":grupoRecursoId", null, OperacaoRelatorio.IN, adicionarIdsParaRecursoFpDoGrupoRecurso(), null, 1, false));
        }
        return parametros;
    }

    private void adicionarParametrosHiearquiaOrRecursosOrGrupos(RelatorioDTO dto) {
        if (isRelatorioPorOrgaoLotacaoFuncional()) {
            dto.adicionarParametro("hierarquias", montarCondicaoDasHierarquias(recuperarSomenteLotacoesSelecionados()));
            dto.adicionarParametro("recursos", Lists.newArrayList());
        } else if (isRelatorioGeralAgrupadoLotacaoFuncional()) {
            dto.adicionarParametro("hierarquias", montarCondicaoDasHierarquias(listaTodasHierarquias));
            dto.adicionarParametro("recursos", Lists.newArrayList());
            String codigo = "";
            for (HierarquiaOrganizacional ho : listaTodasHierarquias) {
                codigo += ho.getCodigo().substring(0, 5) + ", ";
            }
            filtros += "Órgão(s): " + codigo;
            removerSeparadorFiltro();
        } else if (isRelatorioPorOrgaoRecursoFP()) {
            dto.adicionarParametro("hierarquias", "");
            dto.adicionarParametro("recursos", RecursoFP.toDtos(recuperarSomenteRecursosSelecionados()));
        } else if (isRelatorioGeralAgrupadoPorRecursoFP()) {
            dto.adicionarParametro("hierarquias", "");
            dto.adicionarParametro("recursos", RecursoFP.toDtos(recursos));
        } else if (isRelatorioPorOrgaoGrupoRecursoFP()) {
            dto.adicionarParametro("gruposRecursoFp", GrupoRecursoFP.toDtos(recuperarSomenteGrupoRecursosSelecionados()));
        } else if (isRelatorioGeralAgrupadoPorGrupoRecursoFP()) {
            dto.adicionarParametro("gruposRecursoFp", GrupoRecursoFP.toDtos(listaGrupoRecursoFPs));
        } else {
            dto.adicionarParametro("hierarquias", "");
        }
    }

    private boolean temTipoFolhaInformado() {
        return tipoFolhaDePagamento != null;
    }

    private List<ParametrosRelatorios> atribuirParametrosRelatorio() {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        parametros.add(new ParametrosRelatorios(" folha.ano ", ":ano", null, OperacaoRelatorio.IGUAL, ano, null, 1, false));
        parametros.add(new ParametrosRelatorios(" folha.mes ", ":mes", null, OperacaoRelatorio.IGUAL, mes - 1, null, 1, false));
        if (versao != null) {
            parametros.add(new ParametrosRelatorios(" folha.versao ", ":versao", null, OperacaoRelatorio.IGUAL, versao, null, 1, false));
            filtros += "Versão de Folha: " + versao + "; ";
        }
        if (temTipoFolhaInformado()) {
            parametros.add(new ParametrosRelatorios(" folha.tipoFolhaDePagamento ", ":tipoFolha", null, OperacaoRelatorio.IGUAL, tipoFolhaDePagamento.name(), null, 1, false));
            filtros += "Tipo de Folha: " + tipoFolhaDePagamento.getDescricao() + "; ";
        }
        if (tipoContribuicao != null) {
            parametros.add(new ParametrosRelatorios(" tipoprev.CODIGO ", ":codPrev", null, OperacaoRelatorio.IGUAL, tipoContribuicao.getCodigo(), null, 2, false));
            filtros += "Contribuição:" + tipoContribuicao.toString() + "; ";
        }
        return parametros;
    }

    private String getTituloGroupHeader() {
        String titulo;
        if (TipoLotacao.RECURSO_FP.equals(tipoLotacao)) {
            titulo = "Recurso FP";
        } else if (TipoLotacao.GRUPO_RECURSO_FP.equals(tipoLotacao)) {
            titulo = "Grupo de Recurso FP";
        } else if (TipoLotacao.GRUPO_RECURSO_FP.equals(tipoLotacao)) {
            titulo = "Lotação Funcional ";
        } else {
            titulo = "Geral";
        }
        return titulo;
    }

    private String getTituloDoGroupFooter() {
        String titulo;
        if (TipoLotacao.RECURSO_FP.equals(tipoLotacao)) {
            titulo = "Total por Recurso FP ";
        } else if (TipoLotacao.GRUPO_RECURSO_FP.equals(tipoLotacao)) {
            titulo = "Total por Grupo de Recurso FP ";
        } else if (TipoLotacao.LOTACAO_FUNCIONAL.equals(tipoLotacao)) {
            titulo = "Total por Lotação Funcional ";
        } else {
            titulo = "Total Geral ";
        }
        return titulo;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setApi("rh/folha-por-secretaria-recurso-vinculofp/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao gerar relatório de Folha de Pagamento Por Secretaria e Recurso VinculoFP {}", e);
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public RelatorioDTO montarRelatorioDTO() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.adicionarParametro("MUNICIPIO", "PREFEITURA MUNICIPAL DE RIO BRANCO");
        dto.adicionarParametro("MODULO", "Recursos Humanos");
        dto.setNomeParametroBrasao("IMAGEM");
        String nomeDoRelatorio = "Resumo da Folha Por Secretaria Agrupado por Lotação de Pagamento";
        dto.adicionarParametro("NOMERELATORIO", nomeDoRelatorio);
        dto.adicionarParametro("SECRETARIA", "Secretaria Municipal de Administração e Gestão de Pessoas");
        dto.adicionarParametro("TITULO_HEADER", getTituloGroupHeader());
        dto.adicionarParametro("TITULO_FOOTER", getTituloDoGroupFooter());
        dto.adicionarParametro("EXERCICIO", sistemaFacade.getExercicioCorrente().getAno() + "");
        dto.adicionarParametro("TIPO_RELATORIO", tipoAgrupadorRelatorio.getDescricao());
        dto.adicionarParametro("MES", Mes.getMesToInt(mes).getDescricao());
        dto.adicionarParametro("ANO", ano.toString());
        dto.adicionarParametro("USER", StringUtil.primeiroCaracterMaiusculo(sistemaFacade.getUsuarioCorrente().getLogin()), Boolean.FALSE);
        dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(atribuirParametrosRelatorio()));
        dto.adicionarParametro("parametrosSummary", ParametrosRelatorios.parametrosToDto(buscarFiltrosSummary()));
        dto.adicionarParametro("isRelatorioPorOrgaoLotacaoFuncional", isRelatorioPorOrgaoLotacaoFuncional());
        dto.adicionarParametro("isRelatorioGeralAgrupadoLotacaoFuncional", isRelatorioGeralAgrupadoLotacaoFuncional());
        dto.adicionarParametro("isRelatorioPorOrgaoRecursoFP", isRelatorioPorOrgaoRecursoFP());
        dto.adicionarParametro("isRelatorioGeralAgrupadoPorRecursoFP", isRelatorioGeralAgrupadoPorRecursoFP());
        dto.adicionarParametro("isRelatorioPorOrgaoGrupoRecursoFP", isRelatorioPorOrgaoGrupoRecursoFP());
        dto.adicionarParametro("isRelatorioGeralAgrupadoPorGrupoRecursoFP", isRelatorioGeralAgrupadoPorGrupoRecursoFP());
        dto.adicionarParametro("dataOperacao", sistemaFacade.getDataOperacao());
        adicionarParametrosHiearquiaOrRecursosOrGrupos(dto);
        dto.adicionarParametro("FILTROS", filtros != null ? filtros.length() > 2000 ? filtros.substring(0, 2000) + "..." : filtros : "");
        dto.setNomeRelatorio(nomeDoRelatorio);
        return dto;
    }


    private String montarCondicaoDasHierarquias(List<HierarquiaOrganizacional> listaHierarquia) {
        String condicao = "";
        if (!listaHierarquia.isEmpty()) {
            condicao = " and (";
            String juncao = "";
            for (HierarquiaOrganizacional hierarquia : listaHierarquia) {
                condicao += juncao + "vw.codigo LIKE '" + hierarquia.getCodigoSemZerosFinais() + "%'";
                juncao = " or ";
            }
            condicao += ")";
        }
        return condicao;
    }

    private boolean isMesValido() {
        if (mes != null) {
            if (mes.compareTo(1) < 0 || mes.compareTo(12) > 0) {
                return false;
            }
        }
        return true;
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();

        if (mes == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês é deve ser informado.");
        }
        if (!isMesValido()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Mês deve ser entre 1 e 12.");
        }
        if (ano == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano deve ser informado.");
        }
        if (!temTipoFolhaInformado()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Folha deve ser informado.");
        }
        if (tipoAgrupadorRelatorio == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Relatório deve ser informado.");
        }
        if (relatorioAgrupado) {
            if (TipoSelecao.TIPO_LOTACAO.equals(tipoSelecao) && tipoLotacao == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Lotação deve ser informado.");
            }
        }
        if (isRelatorioPorOrgaoLotacaoFuncional()) {
            if (recuperarSomenteLotacoesSelecionados().size() <= 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório selecionar um órgão para os filtros informados.");
            }
        }
        if (isRelatorioPorOrgaoRecursoFP()) {
            if (recuperarSomenteRecursosSelecionados().size() <= 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório selecionar um recurso fp para os filtros informados.");
            }
        }
        if (isRelatorioPorOrgaoGrupoRecursoFP()) {
            if (recuperarSomenteGrupoRecursosSelecionados().size() <= 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório selecionar um grupo de recurso fp para os filtros informados.");
            }
        }
        if (TipoSelecao.ESTABELECIMENTO.equals(tipoSelecao) && entidade == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Estabelecimento deve ser informado.");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public TipoFolhaDePagamento getTipoFolhaDePagamento() {
        return tipoFolhaDePagamento;
    }

    public void setTipoFolhaDePagamento(TipoFolhaDePagamento tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalSelecionada() {
        return hierarquiaOrganizacionalSelecionada;
    }

    public void setHierarquiaOrganizacionalSelecionada(HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada) {
        this.hierarquiaOrganizacionalSelecionada = hierarquiaOrganizacionalSelecionada;
    }

    public Converter getConverterHierarquia() {
        if (converterHierarquia == null) {
            converterHierarquia = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterHierarquia;
    }

    public List<HierarquiaOrganizacional> completarHierarquia(String parte) {
        List<HierarquiaOrganizacional> hos = new ArrayList<>();
        hos.addAll(hierarquiaOrganizacionalFacade.filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), getDataVigencia()));
        return hos;
    }

    public void limparHierarquiaSelecionada() {
        if (isMesValido()) {
            hierarquiaOrganizacionalSelecionada = null;
            listaTodasHierarquias = Lists.newArrayList();
            limparItensSelecionados();
        }
    }

    private Date getDataVigencia() {
        if (mes != null && ano != null && isMesValido()) {
            return DataUtil.criarDataComMesEAno(mes, ano).toDate();
        } else {
            return UtilRH.getDataOperacao();
        }
    }

    public void limpaCampos() {
        hierarquiaOrganizacionalSelecionada = null;
        mes = null;
        ano = null;
    }

    public List<SelectItem> getTiposFolha() {
        List<SelectItem> retorno = new ArrayList<>();
        for (TipoFolhaDePagamento tipo : TipoFolhaDePagamento.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }

    public TipoAgrupadorRelatorio getTipoAgrupadorRelatorio() {
        return tipoAgrupadorRelatorio;
    }

    public void setTipoAgrupadorRelatorio(TipoAgrupadorRelatorio tipoAgrupadorRelatorio) {
        this.tipoAgrupadorRelatorio = tipoAgrupadorRelatorio;
    }

    public boolean getRenderizarAgrupar() {
        if (TipoAgrupadorRelatorio.GERAL.equals(tipoAgrupadorRelatorio)) {
            return true;
        }
        return false;
    }

    public Boolean getRelatorioAgrupado() {
        return relatorioAgrupado;
    }

    public void setRelatorioAgrupado(Boolean relatorioAgrupado) {
        this.relatorioAgrupado = relatorioAgrupado;
    }

    public void definirNullParaTipoLotacao() {
        tipoLotacao = null;
    }

    public TipoLotacao getTipoLotacao() {
        return tipoLotacao;
    }

    public void setTipoLotacao(TipoLotacao tipoLotacao) {
        this.tipoLotacao = tipoLotacao;
    }

    public boolean desabilitarCampoTipoLotacao() {
        if ((TipoAgrupadorRelatorio.GERAL.equals(tipoAgrupadorRelatorio)
            && !relatorioAgrupado) || TipoSelecao.ESTABELECIMENTO.equals(tipoSelecao)) {
            return true;
        }
        return false;
    }

    public List<SelectItem> getListaTipoLotacoes() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        for (TipoLotacao tipo : TipoLotacao.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }

    public void limparEntidade() {
        entidade = null;
        limparItensSelecionados();
        tipoSelecao = TipoSelecao.TIPO_LOTACAO;
    }

    public void limparItensSelecionados() {
        hierarquiaOrganizacionalSelecionada = null;
        listaTodasHierarquias = new ArrayList<>();
        recuperarHierarquias();
        recuperarRecursosFP();
        recuperarGrupoRecursosFP();
        limparFiltroCampo();
    }

    private void limparFiltroCampo() {
        if (isRelatorioPorOrgaoLotacaoFuncional()) {
            FacesUtil.executaJavaScript("wTabelaLotacao.clearFilters()");
        }
        if (isRelatorioPorOrgaoRecursoFP()) {
            FacesUtil.executaJavaScript("wTabelaRecursos.clearFilters()");
        }
        if (isRelatorioPorOrgaoGrupoRecursoFP()) {
            FacesUtil.executaJavaScript("wTabelaGrupoRecursos.clearFilters()");
        }
    }

    public boolean mostrarPanelTabelas() {
        return (isRelatorioPorOrgaoGrupoRecursoFP()
            || isRelatorioPorOrgaoLotacaoFuncional()
            || isRelatorioPorOrgaoRecursoFP());

    }

    public boolean isRelatorioPorOrgaoLotacaoFuncional() {
        if (TipoAgrupadorRelatorio.POR_ORGAO.equals(tipoAgrupadorRelatorio)
            && TipoLotacao.LOTACAO_FUNCIONAL.equals(tipoLotacao)) {
            return true;
        }
        return false;
    }

    public boolean isRelatorioPorOrgaoRecursoFP() {
        if (TipoAgrupadorRelatorio.POR_ORGAO.equals(tipoAgrupadorRelatorio)
            && TipoLotacao.RECURSO_FP.equals(tipoLotacao)) {
            return true;
        }
        return false;
    }

    public boolean isRelatorioPorOrgaoGrupoRecursoFP() {
        return (TipoAgrupadorRelatorio.POR_ORGAO.equals(tipoAgrupadorRelatorio) && TipoLotacao.GRUPO_RECURSO_FP.equals(tipoLotacao))
            || (TipoSelecao.ESTABELECIMENTO.equals(tipoSelecao) && entidade != null);
    }

    public List<HierarquiaOrganizacional> getListaTodasHierarquias() {
        return listaTodasHierarquias;
    }

    public void setListaTodasHierarquias(List<HierarquiaOrganizacional> listaTodasHierarquias) {
        this.listaTodasHierarquias = listaTodasHierarquias;
    }

    public void recuperarHierarquias() {
        if (tipoAgrupadorRelatorio != null && tipoAgrupadorRelatorio.equals(TipoAgrupadorRelatorio.POR_ORGAO)) {
            if (hierarquiaOrganizacionalSelecionada != null) {
                listaTodasHierarquias = hierarquiaOrganizacionalFacade
                    .buscarHierarquiasFiltrandoVigentesPorPeriodoAndTipo(hierarquiaOrganizacionalSelecionada.getCodigoSemZerosFinais(),
                        getDataVigencia(), getDataVigencia(), TipoHierarquiaOrganizacional.ADMINISTRATIVA);
                listaTodasHierarquias.remove(0);
            }
        } else {
            listaTodasHierarquias = hierarquiaOrganizacionalFacade.listaTodasPorNivel("", "2",
                TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), getDataVigencia());
        }
        if (!listaTodasHierarquias.isEmpty()) {
            Collections.sort(listaTodasHierarquias, new Comparator<HierarquiaOrganizacional>() {
                @Override
                public int compare(HierarquiaOrganizacional o1, HierarquiaOrganizacional o2) {
                    String i1 = o1.getCodigo();
                    String i2 = o2.getCodigo();
                    return i1.compareTo(i2);
                }
            });
        }
    }

    public void recuperarRecursosFP() {
        if (mes != null && ano != null && isMesValido()) {
            recursos = recursoFPFacade.buscarRecursosPorMesAndAnoFolha(Mes.getMesToInt(mes), ano, true);
            if (!recursos.isEmpty()) {
                Collections.sort(recursos, new Comparator<RecursoFP>() {
                    @Override
                    public int compare(RecursoFP o1, RecursoFP o2) {
                        String i1 = o1.getCodigo();
                        String i2 = o2.getCodigo();
                        return i1.compareTo(i2);
                    }
                });
            }
        }
    }

    public void recuperarGrupoRecursosFP() {
        if (TipoSelecao.ESTABELECIMENTO.equals(tipoSelecao)) {
            if (entidade != null) {
                listaGrupoRecursoFPs = buscarGrupoRecursosFPPorEntidade();
            }
        } else {
            listaGrupoRecursoFPs = grupoRecursoFPFacade.buscarGruposRecursoFPPorDataOperacao(getDataVigencia());
        }
        if (!listaGrupoRecursoFPs.isEmpty()) {
            Collections.sort(listaGrupoRecursoFPs, new Comparator<GrupoRecursoFP>() {
                @Override
                public int compare(GrupoRecursoFP o1, GrupoRecursoFP o2) {
                    String i1 = o1.getNome();
                    String i2 = o2.getNome();
                    return i1.compareTo(i2);
                }
            });
        }
    }

    public List<GrupoRecursoFP> buscarGrupoRecursosFPPorEntidade() {
        List<GrupoRecursoFP> grupoRecursoFPs = Lists.newLinkedList();
        for (HierarquiaOrganizacional hierarquiaOrganizacional : buscarHierarquiasDaEntidade()) {
            for (GrupoRecursoFP grupoRecursoFP : grupoRecursoFPFacade.buscarGruposRecursoFPPorOrgaoAndDataOperacao(hierarquiaOrganizacional)) {
                if (!grupoRecursoFPs.contains(grupoRecursoFP)) {
                    grupoRecursoFPs.add(grupoRecursoFP);
                }
            }
        }
        return grupoRecursoFPs;
    }


    public List<HierarquiaOrganizacional> buscarHierarquiasDaEntidade() {
        List<HierarquiaOrganizacional> hierarquias = entidadeFacade.buscarHierarquiasDaEntidade(entidade, CategoriaDeclaracaoPrestacaoContas.SEFIP, DataUtil.primeiroDiaMes(DataUtil.criarDataComMesEAno(mes, ano).toDate()).getTime(), DataUtil.ultimoDiaMes(DataUtil.criarDataComMesEAno(mes, ano).toDate()).getTime());
        Collections.sort(hierarquias);
        return hierarquias;
    }

    public void definirMesAnoPorCompetenciaAberta() {
        try {
            CompetenciaFP competencia = competenciaFPFacade.recuperarUltimaCompetencia(StatusCompetencia.ABERTA, TipoFolhaDePagamento.NORMAL);
            if (competencia != null) {
                mes = competencia.getMes().getNumeroMes();
                ano = competencia.getExercicio().getAno();
            } else {
                mes = null;
                ano = null;
            }
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }


    public List<SelectItem> getTipoAgrupamentoRelatorio() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        for (TipoAgrupadorRelatorio tipo : TipoAgrupadorRelatorio.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposFolhaDePagamento() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        for (TipoFolhaDePagamento tipo : TipoFolhaDePagamento.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }

    //    ADICIONAR ITEM
    public void adicionarRecurso(ActionEvent ev) {
        RecursoFP r = (RecursoFP) ev.getComponent().getAttributes().get("recurso");
        r.setSelecionado(true);
    }

    public void adicionarGrupoRecurso(ActionEvent ev) {
        GrupoRecursoFP g = (GrupoRecursoFP) ev.getComponent().getAttributes().get("grupoRecurso");
        g.setSelecionado(true);
    }

    public void adicionarLotacao(ActionEvent ev) {
        HierarquiaOrganizacional ho = (HierarquiaOrganizacional) ev.getComponent().getAttributes().get("lotacao");
        ho.setSelecionado(true);
    }


    //    ADICIONAR TODOS ITENS
    public void adicionarTodosRecursos() {
        for (RecursoFP recurso : recursos) {
            recurso.setSelecionado(true);
        }
    }

    public void adicionarTodosGrupoRecursos() {
        for (GrupoRecursoFP grupo : listaGrupoRecursoFPs) {
            grupo.setSelecionado(true);
        }
    }

    public void adicionarTodasLotacoes() {
        for (HierarquiaOrganizacional hierarquia : listaTodasHierarquias) {
            hierarquia.setSelecionado(true);
        }
    }


    //    DESMARCAR TODOS ITENS
    public void removerTodosRecursos() {
        for (RecursoFP recurso : recursos) {
            recurso.setSelecionado(false);
        }
    }

    public void removerTodasLotacoes() {
        for (HierarquiaOrganizacional ho : listaTodasHierarquias) {
            ho.setSelecionado(false);
        }
    }

    public void removerTodosGrupos() {
        for (GrupoRecursoFP grupo : listaGrupoRecursoFPs) {
            grupo.setSelecionado(false);
        }
    }

    //DESMARCAR ITEM
    public void removerRecurso(ActionEvent ev) {
        RecursoFP r = (RecursoFP) ev.getComponent().getAttributes().get("recurso");
        r.setSelecionado(false);
    }

    public void removerGrupoRecurso(ActionEvent ev) {
        GrupoRecursoFP g = (GrupoRecursoFP) ev.getComponent().getAttributes().get("grupoRecurso");
        g.setSelecionado(false);
    }

    public void removerLotacao(ActionEvent ev) {
        HierarquiaOrganizacional ho = (HierarquiaOrganizacional) ev.getComponent().getAttributes().get("lotacao");
        ho.setSelecionado(false);
    }

    public Boolean todosRecursosMarcados() {
        for (RecursoFP recurso : recursos) {
            if (!recurso.isSelecionado())
                return false;
        }
        return true;
    }

    public Boolean todosGrupoRecursoMarcados() {
        for (GrupoRecursoFP grupo : listaGrupoRecursoFPs) {
            if (!grupo.getSelecionado())
                return false;
        }
        return true;
    }

    public Boolean todasLotacoesMarcadas() {
        for (HierarquiaOrganizacional hierarquia : listaTodasHierarquias) {
            if (!hierarquia.isSelecionado())
                return false;
        }
        return true;
    }


    public List<RecursoFP> recuperarSomenteRecursosSelecionados() {
        List<RecursoFP> retorno = new ArrayList<>();
        for (RecursoFP recurso : recursos) {
            if (recurso.isSelecionado()) {
                retorno.add(recurso);
            }
        }
        return retorno;
    }

    public List<GrupoRecursoFP> recuperarSomenteGrupoRecursosSelecionados() {
        List<GrupoRecursoFP> retorno = new ArrayList<>();
        for (GrupoRecursoFP grupoRecursoFP : listaGrupoRecursoFPs) {
            if (grupoRecursoFP.getSelecionado()) {
                retorno.add(grupoRecursoFP);
            }
        }
        return retorno;
    }

    public List<HierarquiaOrganizacional> recuperarSomenteLotacoesSelecionados() {
        List<HierarquiaOrganizacional> retorno = new ArrayList<>();
        for (HierarquiaOrganizacional ho : listaTodasHierarquias) {
            if (ho.isSelecionado()) {
                retorno.add(ho);
            }
        }
        return retorno;
    }

    public void atribuirValoresTipoSelecao() {
        entidade = null;
        hierarquiaOrganizacionalSelecionada = null;
        removerTodosGrupos();
        listaTodasHierarquias = Lists.newArrayList();
        listaGrupoRecursoFPs = Lists.newArrayList();
        FacesUtil.executaJavaScript("wTabelaGrupoRecursos.clearFilters()");
        if (TipoSelecao.ESTABELECIMENTO.equals(tipoSelecao)) {
            tipoLotacao = TipoLotacao.GRUPO_RECURSO_FP;
        } else {
            limparHierarquiaSelecionada();
        }
    }

    public List<SelectItem> getEntidades() {
        return Util.getListSelectItem(entidadeFacade.buscarEntidadesDeclarantesVigentePorCategoria(CategoriaDeclaracaoPrestacaoContas.SEFIP), false);
    }

    public boolean hasTipoSelecaoEstabelecimento() {
        return TipoSelecao.ESTABELECIMENTO.equals(tipoSelecao);
    }

    public List<SelectItem> getTiposSelecao() {
        return Util.getListSelectItemSemCampoVazio(TipoSelecao.values(), false);
    }

    public List<SelectItem> getTiposContribuicoes() {
        return Util.getListSelectItem(TipoContribuicao.values(), false);
    }

    public List<GrupoRecursoFP> getListaGrupoRecursoFPs() {
        return listaGrupoRecursoFPs;
    }

    public void setListaGrupoRecursoFPs(List<GrupoRecursoFP> listaGrupoRecursoFPs) {
        this.listaGrupoRecursoFPs = listaGrupoRecursoFPs;
    }

    public List<RecursoFP> getRecursos() {
        return recursos;
    }

    public void setRecursos(List<RecursoFP> recursos) {
        this.recursos = recursos;
    }

    public TipoContribuicao getTipoContribuicao() {
        return tipoContribuicao;
    }

    public void setTipoContribuicao(TipoContribuicao tipoContribuicao) {
        this.tipoContribuicao = tipoContribuicao;
    }

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }

    public TipoSelecao getTipoSelecao() {
        return tipoSelecao;
    }

    public void setTipoSelecao(TipoSelecao tipoSelecao) {
        this.tipoSelecao = tipoSelecao;
    }

    public enum TipoSelecao {
        TIPO_LOTACAO("Tipo de Lotação", 1),
        ESTABELECIMENTO("Estabelecimento", 2);
        private String descricao;
        private Integer codigo;

        public String getDescricao() {
            return descricao;
        }

        public Integer getCodigo() {
            return codigo;
        }

        TipoSelecao(String descricao, Integer codigo) {
            this.descricao = descricao;
            this.codigo = codigo;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }
}
