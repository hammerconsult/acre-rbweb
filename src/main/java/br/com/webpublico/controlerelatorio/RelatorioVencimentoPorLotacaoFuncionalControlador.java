package br.com.webpublico.controlerelatorio;

/**
 * Created by William on 05/04/2017.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import br.com.webpublico.entidades.CompetenciaFP;
import br.com.webpublico.entidades.GrupoRecursoFP;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.RecursoFP;
import br.com.webpublico.entidades.comum.relatorio.ConfiguracaoDeRelatorio;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.entidadesauxiliares.RelatorioVencimentoPorLotacaoFuncional;
import br.com.webpublico.enums.*;
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
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ViewScoped
@ManagedBean
@URLMappings(mappings = {
    @URLMapping(id = "vencimento-por-lotacao-funcional", pattern = "/relatorio/vencimento-por-lotacao-funcional/", viewId = "/faces/rh/relatorios/relatoriovencimentoporlotacaofuncional.xhtml")
})
public class RelatorioVencimentoPorLotacaoFuncionalControlador implements Serializable {

    public static final String NOME_ARQUIVO = "Relatório de Vencimento por Lotação Funcional";
    protected static final Logger logger = LoggerFactory.getLogger(RelatorioVencimentoPorLotacaoFuncionalControlador.class);
    public Integer versao;
    private Integer mes;
    private Integer ano;
    private TipoFolhaDePagamento tipoFolhaDePagamento;
    private ConverterAutoComplete converterHierarquia;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private GrupoRecursoFPFacade grupoRecursoFPFacade;
    private String filtros;
    private List<RecursoFP> recursos;
    private List<GrupoRecursoFP> gruposRecursoFPs;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private List<HierarquiaOrganizacional> hierarquiasOrganizacionais = Lists.newArrayList();
    private TipoLotacao tipoLotacao;
    private HierarquiaOrganizacional hierarquiaOrganizacionalFiltro;
    @EJB
    private CompetenciaFPFacade competenciaFPFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    private ConfiguracaoDeRelatorioFacade configuracaoDeRelatorioFacade;

    public RelatorioVencimentoPorLotacaoFuncionalControlador() {
    }

    @URLAction(mappingId = "vencimento-por-lotacao-funcional", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        filtros = "";
        mes = null;
        ano = null;
        tipoFolhaDePagamento = null;
        recursos = Lists.newArrayList();
        gruposRecursoFPs = Lists.newArrayList();
        hierarquiaOrganizacionalFiltro = new HierarquiaOrganizacional();
        hierarquiasOrganizacionais = Lists.newArrayList();
        tipoLotacao = null;
        definirMesAnoPorCompetenciaAberta();
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

    public List<HierarquiaOrganizacional> getHierarquiasOrganizacionais() {
        return hierarquiasOrganizacionais;
    }

    public void setHierarquiasOrganizacionais(List<HierarquiaOrganizacional> hierarquiasOrganizacionais) {
        this.hierarquiasOrganizacionais = hierarquiasOrganizacionais;
    }

    public List<SelectItem> getVersoes() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        if (mes != null && ano != null && tipoFolhaDePagamento != null) {
            for (Integer versao : folhaDePagamentoFacade.recuperarVersao(Mes.getMesToInt(mes), ano, tipoFolhaDePagamento)) {
                retorno.add(new SelectItem(versao, String.valueOf(versao)));
            }
        }
        return retorno;
    }

    public List<HierarquiaOrganizacional> buscarHierarquiasOrganizacionais(String parte) {
        return hierarquiaOrganizacionalFacade.filtraPorNivel(parte.trim(), "2", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }


    private void filtrarGruposRecursoFP() {
        if (hierarquiaOrganizacionalFiltro != null) {
            gruposRecursoFPs = grupoRecursoFPFacade.buscarGruposRecursoFPPorOrgaoAndDataOperacao(hierarquiaOrganizacionalFiltro);
        }
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalFiltro() {
        return hierarquiaOrganizacionalFiltro;
    }

    public void setHierarquiaOrganizacionalFiltro(HierarquiaOrganizacional hierarquiaOrganizacionalFiltro) {
        this.hierarquiaOrganizacionalFiltro = hierarquiaOrganizacionalFiltro;
    }

    private boolean temTipoFolhaInformado() {
        return tipoFolhaDePagamento != null;
    }

    private void atribuirParametrosRelatorio(List<ParametrosRelatorios> parametros) {
        parametros.add(new ParametrosRelatorios(" folha.ano ", ":ano", null, OperacaoRelatorio.IGUAL, ano, null, 1, false));
        parametros.add(new ParametrosRelatorios(" folha.mes ", ":mes", null, OperacaoRelatorio.IGUAL, mes - 1, null, 1, false));
        if (versao != null) {
            parametros.add(new ParametrosRelatorios(" folha.versao ", ":versao", null, OperacaoRelatorio.IGUAL, versao, null, 1, false));
            filtros += "Versão de Folha: " + versao + " - ";
        }
        if (temTipoFolhaInformado()) {
            parametros.add(new ParametrosRelatorios(" folha.tipoFolhaDePagamento ", ":tipoFolha", null, OperacaoRelatorio.IGUAL, tipoFolhaDePagamento.name(), null, 1, false));
            filtros += "Tipo de Folha: " + tipoFolhaDePagamento.getDescricao() + " - ";
        }
        if (hierarquiaOrganizacionalFiltro != null && !isRelatorioPorOrgaoGrupoRecursoFP() && !isRelatorioPorOrgaoLotacaoFuncional()){
            filtros += "Hierarquia Organizacional:" + hierarquiaOrganizacionalFiltro;
        }
        if (isRelatorioPorOrgaoGrupoRecursoFP()) {
            atribuirParametrosGrupoRecurso(parametros);
        } else if (isRelatorioPorOrgaoLotacaoFuncional()) {
            atribuirParametrosHierarquias(parametros);
        }
    }

    private void atribuirParametrosGrupoRecurso(List<ParametrosRelatorios> parametros) {
        List<Long> grupos = Lists.newArrayList();
        for (GrupoRecursoFP grupoRecursoFP : gruposRecursoFPs) {
            if (grupoRecursoFP.estaSelecionado()) {
                grupos.add(grupoRecursoFP.getId());
            }
        }
        if (!grupos.isEmpty()) {
            filtros += "Hierarquia Organizacional:" + hierarquiaOrganizacionalFiltro;
            parametros.add(new ParametrosRelatorios(" grupo.id ", ":codigo", null, OperacaoRelatorio.IN, grupos, null, 1, false));
        }
    }

    private void atribuirParametrosHierarquias(List<ParametrosRelatorios> parametros) {
        if (!hierarquiasOrganizacionais.isEmpty()) {
            List<Long> subordinadas = Lists.newArrayList();
            for (HierarquiaOrganizacional hierarquiaOrganizacional : hierarquiasOrganizacionais) {
                if (hierarquiaOrganizacional.isSelecionado()) {
                    subordinadas.add(hierarquiaOrganizacional.getSubordinada().getId());
                }
            }
            subordinadas.add(hierarquiaOrganizacionalFiltro.getSubordinada().getId());

            filtros += "Órgão: " + hierarquiaOrganizacionalFiltro.getCodigoSemZerosFinais();
            parametros.add(new ParametrosRelatorios(" ho.subordinada_id ", ":subordinada_id", null, OperacaoRelatorio.IN, subordinadas, null, 1, false));
        }
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("CSV", false);
            dto.setApi("rh/relatorio-vencimento-por-lotacao-funcional/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
            logger.error("Erro ao gerar relatório. ", e);
        }
    }

    private RelatorioDTO montarRelatorioDTO() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.setNomeParametroBrasao("BRASAO");
        dto.setNomeRelatorio(getNomeRelatorio());
        dto.adicionarParametro("MES", Mes.getMesToInt(mes).getDescricao());
        dto.adicionarParametro("ANO", ano.toString());
        dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
        dto.adicionarParametro("FILTROS", filtros != null ? filtros.length() > 2000 ? filtros.substring(0, 2000) + "..." : filtros : "");
        dto.adicionarParametro("MODULO", "Recursos Humanos");
        dto.adicionarParametro("NOMERELATORIO", getNomeRelatorio());
        dto.adicionarParametro("SECRETARIA", "Secretaria Municipal de Administração e Gestão de Pessoas");
        dto.adicionarParametro("MUNICIPIO", "PREFEITURA MUNICIPAL DE RIO BRANCO");
        dto.adicionarParametro("USER", StringUtil.primeiroCaracterMaiusculo(sistemaFacade.getUsuarioCorrente().getNome()));
        dto.adicionarParametro("EXERCICIO", sistemaFacade.getExercicioCorrente().getAno() + "");
        dto.adicionarParametro("EXIBIRPORLOTACAOFUNCIONAL", isRelatorioPorOrgaoLotacaoFuncional());
        return dto;
    }

    private List<ParametrosRelatorios> montarParametros() {
        filtros = "";
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        atribuirParametrosRelatorio(parametros);
        return parametros;
    }

    private String getNomeRelatorio() {
        return "Relatório De Vencimentos Por Sub Folha";
    }

    public StreamedContent fileDownload() {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.adicionarParametro("CSV", false);
            dto.setApi("rh/relatorio-vencimento-por-lotacao-funcional/excel/");
            ResponseEntity<byte[]> responseEntity = retornarByte(dto);
            byte[] bytes = responseEntity.getBody();
            InputStream stream = new ByteArrayInputStream(bytes);
            return new DefaultStreamedContent(stream, "application/xlsx", NOME_ARQUIVO + ".xlsx");
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
        return null;
    }

    private ResponseEntity<byte[]> retornarByte(RelatorioDTO dto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RelatorioDTO> request = new HttpEntity<>(dto, headers);
        ConfiguracaoDeRelatorio configuracao = configuracaoDeRelatorioFacade.getConfiguracaoPorChave();
        return new RestTemplate().exchange(configuracao.getUrl() + dto.getApi() + "gerar", HttpMethod.POST, request, byte[].class);
    }

    public StreamedContent fileDownloadCSV() {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.adicionarParametro("CSV", true);
            dto.setApi("rh/relatorio-vencimento-por-lotacao-funcional/csv/");
            ResponseEntity<byte[]> responseEntity = retornarByte(dto);
            byte[] bytes = responseEntity.getBody();
            InputStream stream = new ByteArrayInputStream(bytes);
            return new DefaultStreamedContent(stream, "application/csv", NOME_ARQUIVO + ".csv");
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
        return null;
    }

    private boolean isMesValido() {
        if (mes != null) {
            if (mes.compareTo(1) < 0 || mes.compareTo(12) > 0) {
                return false;
            }
        }
        return true;
    }

    public boolean todosGruposMarcados() {
        for (GrupoRecursoFP grupo : gruposRecursoFPs) {
            if (!grupo.estaSelecionado()) {
                return false;
            }
        }
        return true;
    }


    public void marcarTodosGrupos() {
        for (GrupoRecursoFP grupo : gruposRecursoFPs) {
            grupo.setSelecionado(true);
        }
    }

    public void desmarcarTodosGrupos() {
        for (GrupoRecursoFP grupo : gruposRecursoFPs) {
            grupo.setSelecionado(false);
        }
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

        if (isRelatorioPorOrgaoLotacaoFuncional() && hierarquiasOrganizacionais.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório selecionar um órgão para os filtros informados.");
        }
        if (isRelatorioPorOrgaoGrupoRecursoFP() && gruposRecursoFPs.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório selecionar um grupo de recurso fp para os filtros informados.");
        }
        ve.lancarException();
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

    public Converter getConverterHierarquia() {
        if (converterHierarquia == null) {
            converterHierarquia = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterHierarquia;
    }

    public List<HierarquiaOrganizacional> completaHierarquia(String parte) {
        List<HierarquiaOrganizacional> hos = new ArrayList<>();
        hos.addAll(hierarquiaOrganizacionalFacade.filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao()));
        return hos;
    }

    public List<SelectItem> getTiposFolha() {
        List<SelectItem> retorno = new ArrayList<>();
        for (TipoFolhaDePagamento tipo : TipoFolhaDePagamento.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }


    private void definirMesAnoPorCompetenciaAberta() {
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

    public void limparItensSelecionados() {
        hierarquiasOrganizacionais = Lists.newArrayList();
        gruposRecursoFPs = Lists.newArrayList();
        if (isRelatorioPorOrgaoLotacaoFuncional()) {
            filtrarHierarquias();
        } else if (isRelatorioPorOrgaoGrupoRecursoFP()) {
            filtrarGruposRecursoFP();
        }
        limparFiltroCampo();
    }

    private void limparFiltroCampo() {
        if (isRelatorioPorOrgaoLotacaoFuncional()) {
            FacesUtil.executaJavaScript("wTabelaLotacao.clearFilters()");
        }
        if (isRelatorioPorOrgaoGrupoRecursoFP()) {
            FacesUtil.executaJavaScript("wTabelaGrupoRecursos.clearFilters()");
        }
    }

    private void filtrarHierarquias() {
        if (hierarquiaOrganizacionalFiltro != null) {
            hierarquiasOrganizacionais = hierarquiaOrganizacionalFacade
                .buscarHierarquiasFilhasDeUmaHierarquiaOrganizacionalAdministrativa(hierarquiaOrganizacionalFiltro,
                    getDataVigencia());
        }
    }

    public void removerTodasLotacoes() {
        for (HierarquiaOrganizacional hierarquia : hierarquiasOrganizacionais) {
            hierarquia.setSelecionado(false);
        }
    }

    public Boolean todasLotacoesMarcadas() {
        for (HierarquiaOrganizacional hierarquia : hierarquiasOrganizacionais) {
            if (!hierarquia.isSelecionado())
                return false;
        }
        return true;
    }

    public void adicionarTodasLotacoes() {
        for (HierarquiaOrganizacional hierarquia : hierarquiasOrganizacionais) {
            hierarquia.setSelecionado(true);
        }
    }

    public void removerLotacao(ActionEvent ev) {
        HierarquiaOrganizacional hierarquiaOrganizacional = (HierarquiaOrganizacional) ev.getComponent().getAttributes().get("lotacao");
        hierarquiaOrganizacional.setSelecionado(false);
    }

    public void adicionarLotacao(ActionEvent ev) {
        HierarquiaOrganizacional hierarquiaOrganizacional = (HierarquiaOrganizacional) ev.getComponent().getAttributes().get("lotacao");
        hierarquiaOrganizacional.setSelecionado(true);
    }

    public void removerTodosGrupos() {
        for (GrupoRecursoFP grupo : gruposRecursoFPs) {
            grupo.setSelecionado(false);
        }
    }

    public boolean isRelatorioPorOrgaoLotacaoFuncional() {
        return TipoLotacao.LOTACAO_FUNCIONAL.equals(tipoLotacao);
    }

    public boolean isRelatorioPorOrgaoGrupoRecursoFP() {
        return TipoLotacao.GRUPO_RECURSO_FP.equals(tipoLotacao);
    }

    private Date getDataVigencia() {
        if (mes != null && ano != null && isMesValido()) {
            return DataUtil.criarDataComMesEAno(mes, ano).toDate();
        } else {
            return UtilRH.getDataOperacao();
        }
    }

    public List<SelectItem> getTiposFolhaDePagamento() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        for (TipoFolhaDePagamento tipo : TipoFolhaDePagamento.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposLotacao() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        toReturn.add(new SelectItem(TipoLotacao.GRUPO_RECURSO_FP, TipoLotacao.GRUPO_RECURSO_FP.getDescricao()));
        toReturn.add(new SelectItem(TipoLotacao.LOTACAO_FUNCIONAL, TipoLotacao.LOTACAO_FUNCIONAL.getDescricao()));
        return toReturn;
    }

    public List<RecursoFP> getRecursos() {
        return recursos;
    }

    public void setRecursos(List<RecursoFP> recursos) {
        this.recursos = recursos;
    }

    public List<GrupoRecursoFP> getGruposRecursoFPs() {
        return gruposRecursoFPs;
    }

    public void setGruposRecursoFPs(List<GrupoRecursoFP> gruposRecursoFPs) {
        this.gruposRecursoFPs = gruposRecursoFPs;
    }

    public TipoLotacao getTipoLotacao() {
        return tipoLotacao;
    }

    public void setTipoLotacao(TipoLotacao tipoLotacao) {
        this.tipoLotacao = tipoLotacao;
    }
}
