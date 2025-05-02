package br.com.webpublico.controlerelatorio;

/**
 * Created by William on 08/02/2018.
 */

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.comum.relatorio.ConfiguracaoDeRelatorio;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.entidadesauxiliares.RelatorioSiope;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.rh.relatorios.TipoLotacao;
import br.com.webpublico.enums.rh.siope.SegmentoAtuacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.comum.ConfiguracaoDeRelatorioFacade;
import br.com.webpublico.relatoriofacade.rh.RelatorioSiopeFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.*;
import br.com.webpublico.webreportdto.dto.comum.MesDTO;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import javax.faces.model.SelectItem;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@ViewScoped
@ManagedBean
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-siope", pattern = "/relatorio/siope/", viewId = "/faces/rh/relatorios/relatoriosiope.xhtml")
})
public class RelatorioSiopeControlador implements Serializable {

    public static final String NOME_ARQUIVO = "Relatório do SIOPE";
    protected static final Logger logger = LoggerFactory.getLogger(RelatorioSiopeControlador.class);
    private Integer mes;
    private Integer ano;
    private ConverterAutoComplete converterHierarquia;
    @EJB
    private GrupoRecursoFPFacade grupoRecursoFPFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ConfiguracaoDeRelatorioFacade configuracaoDeRelatorioFacade;
    @EJB
    private CompetenciaFPFacade competenciaFPFacade;
    @EJB
    private RelatorioSiopeFacade relatorioSiopeFacade;
    private List<RecursoFP> recursos;
    private List<GrupoRecursoFP> gruposRecursoFPs;
    private List<HierarquiaOrganizacional> hierarquiasOrganizacionais = Lists.newArrayList();
    private TipoLotacao tipoLotacao;
    private HierarquiaOrganizacional hierarquiaOrganizacionalFiltro;
    private List<FolhaDePagamento> folhasParaPesquisa;
    private Map<TipoFolhaDePagamento, List<FolhaDePagamento>> mapaFolhas;

    public RelatorioSiopeControlador() {
    }

    public void carregarFolhas() {
        mapaFolhas = Maps.newHashMap();
        folhasParaPesquisa = Lists.newArrayList();

        for (TipoFolhaDePagamento tipoFolhaDePagamento : TipoFolhaDePagamento.getFolhasPorPrioridadeDeUso()) {
            mapaFolhas.put(tipoFolhaDePagamento, Lists.<FolhaDePagamento>newArrayList());
        }
        if (mes != null && ano != null) {
            for (TipoFolhaDePagamento tipo : mapaFolhas.keySet()) {
                mapaFolhas.get(tipo).addAll(folhaDePagamentoFacade.getFolhasDePagamentoEfetivadasNoMesDoTipo(Mes.getMesToInt(mes), ano, tipo));
            }
        }

    }

    public List<TipoFolhaDePagamento> getTipos() {
        return TipoFolhaDePagamento.getFolhasPorPrioridadeDeUso();

    }

    public Map<TipoFolhaDePagamento, List<FolhaDePagamento>> getMapaFolhas() {
        return mapaFolhas;
    }

    @URLAction(mappingId = "relatorio-siope", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        mes = null;
        ano = null;
        mapaFolhas = Maps.newHashMap();
        folhasParaPesquisa = Lists.newArrayList();
        recursos = Lists.newArrayList();
        gruposRecursoFPs = Lists.newArrayList();
        hierarquiaOrganizacionalFiltro = new HierarquiaOrganizacional();
        hierarquiasOrganizacionais = Lists.newArrayList();
        tipoLotacao = null;
        definirMesAnoPorCompetenciaAberta();
    }

    private List<ParametrosRelatorios> montarParametros() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        atribuirParametrosRelatorio(parametros);
        return parametros;
    }

    public List<HierarquiaOrganizacional> getHierarquiasOrganizacionais() {
        return hierarquiasOrganizacionais;
    }

    public void setHierarquiasOrganizacionais(List<HierarquiaOrganizacional> hierarquiasOrganizacionais) {
        this.hierarquiasOrganizacionais = hierarquiasOrganizacionais;
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

    private List<String> getTiposFolhasSelecionadas() {
        List<String> tipoFolha = Lists.newArrayList();
        for (FolhaDePagamento folhaDePagamento : folhasParaPesquisa) {
            tipoFolha.add(folhaDePagamento.getTipoFolhaDePagamento().name());
        }
        return tipoFolha;
    }

    private List<Integer> getVersoesFolhasSelecionadas() {
        List<Integer> tipoFolha = Lists.newArrayList();
        for (FolhaDePagamento folhaDePagamento : folhasParaPesquisa) {
            tipoFolha.add(folhaDePagamento.getVersao());
        }
        return tipoFolha;
    }

    private void atribuirParametrosRelatorio(List<ParametrosRelatorios> parametros) {
        parametros.add(new ParametrosRelatorios(" folha.ano ", ":ano", null, OperacaoRelatorio.IGUAL, ano, null, 1, false));
        parametros.add(new ParametrosRelatorios(" folha.mes ", ":mes", null, OperacaoRelatorio.IGUAL, mes - 1, null, 1, false));
        parametros.add(new ParametrosRelatorios(" folha.tipoFolhaDePagamento ", ":tipoFolhaDePagamento", null, OperacaoRelatorio.IN, getTiposFolhasSelecionadas(), null, 1, false));
        parametros.add(new ParametrosRelatorios(" folha.versao ", ":versao", null, OperacaoRelatorio.IN, getVersoesFolhasSelecionadas(), null, 1, false));
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
            parametros.add(new ParametrosRelatorios(" ho.subordinada_id ", ":subordinada_id", null, OperacaoRelatorio.IN, subordinadas, null, 1, false));
        }
    }

    private ResponseEntity<byte[]> retornarByte(RelatorioDTO dto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RelatorioDTO> request = new HttpEntity<>(dto, headers);
        ConfiguracaoDeRelatorio configuracao = configuracaoDeRelatorioFacade.getConfiguracaoPorChave();
        return new RestTemplate().exchange(configuracao.getUrl() + dto.getApi() + "gerar", HttpMethod.POST, request, byte[].class);
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
        if (isRelatorioPorOrgaoLotacaoFuncional() && hierarquiasOrganizacionais.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório selecionar um órgão para os filtros informados.");
        }
        if (isRelatorioPorOrgaoGrupoRecursoFP() && gruposRecursoFPs.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório selecionar um grupo de recurso fp para os filtros informados.");
        }
        if(folhasParaPesquisa.isEmpty()){
            ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório selecionar um tipo de Folha de Pagamento.");
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

    public CompetenciaFP buscarUltimaCompetenciaTipoNormalEStatusAberta() {
        return competenciaFPFacade.recuperarUltimaCompetencia(StatusCompetencia.ABERTA, TipoFolhaDePagamento.NORMAL);
    }

    private void definirMesAnoPorCompetenciaAberta() {
        try {
            CompetenciaFP competencia = buscarUltimaCompetenciaTipoNormalEStatusAberta();
            if (competencia != null) {
                mes = competencia.getMes().getNumeroMes();
                ano = competencia.getExercicio().getAno();
                carregarFolhas();
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


    public void atribuirFolhasPesquisa(FolhaDePagamento folha) {
        if (folhasParaPesquisa.contains(folha)) {
            folhasParaPesquisa.remove(folha);
        } else {
            folhasParaPesquisa.add(folha);
        }
    }

    public void atribuirFolhasPesquisa(TipoFolhaDePagamento tipo) {
        boolean temFolhaTipo = false;
        for (FolhaDePagamento folhaDePagamento : folhasParaPesquisa) {
            if (tipo.equals(folhaDePagamento.getTipoFolhaDePagamento())) {
                folhasParaPesquisa.removeAll(mapaFolhas.get(tipo));
                temFolhaTipo = true;
                break;
            }
        }
        if (!temFolhaTipo && !mapaFolhas.get(tipo).isEmpty()) {
            folhasParaPesquisa.removeAll(mapaFolhas.get(tipo));
            folhasParaPesquisa.addAll(mapaFolhas.get(tipo));
        }

        if(mapaFolhas.get(tipo).isEmpty()){
            FacesUtil.addOperacaoNaoPermitida("Não foi encontrada nenhuma folha efetivada  do tipo " + tipo.name() +" com essa competência");
        }
    }

    public boolean hasFolhaAdicionada(FolhaDePagamento folha) {
        return folhasParaPesquisa.contains(folha);
    }

    public boolean hasTipoAdicionado(TipoFolhaDePagamento tipo) {
        for (FolhaDePagamento folhaDePagamento : folhasParaPesquisa) {
            if (tipo.equals(folhaDePagamento.getTipoFolhaDePagamento())) {
                return true;
            }
        }
        return false;
    }

    private List<RelatorioSiope> gerarSql() {
        List<ParametrosRelatorios> parametros = new ArrayList<>();
        atribuirParametrosRelatorio(parametros);
        return relatorioSiopeFacade.montarConsulta(parametros);
    }

    public StreamedContent fileDownloadCSV() {
        try {
            validarCampos();
            List<String> titulos = getTitulos();
            List<RelatorioSiope> relatorio = gerarSql();
            List<Object[]> objetos = new ArrayList<>();
            preencherObjetos(relatorio, objetos, true);
            ExcelUtil excel = new ExcelUtil();
            excel.gerarCSV(NOME_ARQUIVO, NOME_ARQUIVO, titulos, objetos, false);
            return excel.fileDownload();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            ex.printStackTrace();
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
        return null;
    }

    public void gerarRelatorio(String tipoRelatorio) {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorio));
            dto.setApi("rh/relatorio-siope/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            e.printStackTrace();
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    private RelatorioDTO montarRelatorioDTO() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.setNomeParametroBrasao("BRASAO");
        dto.setNomeRelatorio("Relatorio Siope");
        dto.adicionarParametro("MES", mes);
        dto.adicionarParametro("ANO", ano);
        dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome());
        dto.adicionarParametro("MODULO", "Recursos Humanos");
        dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO E GESTÃO");
        dto.adicionarParametro("DEPARTAMENTO", "DEPARTAMENTO DE RECURSOS HUMANOS");
        dto.adicionarParametro("NOMERELATORIO", "Relatório Siope");
        dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(montarParametros()));
        dto.adicionarParametro("removerCaracteresCpf", true);
        dto.adicionarParametro("DATAOPERACAO", DataUtil.getDataFormatada(hierarquiaOrganizacionalFacade.getSistemaFacade().getDataOperacao()));
        return dto;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public void setSistemaFacade(SistemaFacade sistemaFacade) {
        this.sistemaFacade = sistemaFacade;
    }

    public List<String> getTitulos() {
        List<String> titulos = new ArrayList<>();
        titulos.add("Formato");
        titulos.add("Titulo da tabela referente ao mês, seguido pelo número da linha");
        titulos.add("Mês");
        titulos.add("CPF");
        titulos.add("Matrícula/Contrato");
        titulos.add("Nome");
        titulos.add("Código INEP");
        titulos.add("Nome da Escola");
        titulos.add("Carga horária");
        titulos.add("Código da Categoria");
        titulos.add("Categoria do profissional");
        titulos.add("Código do serviço");
        titulos.add("Tipo do serviço");
        titulos.add("Cod Situação");
        titulos.add("Situação");
        titulos.add("Código do Segmento de Atuação");
        titulos.add("Segmento de Atuação");
        titulos.add("Salário base");
        titulos.add("60% do FUNDEB");
        titulos.add("40% do FUNDEB");
        titulos.add("Outras receitas");
        titulos.add("Total (60%+40%+O)");
        return titulos;
    }

    public void preencherObjetos(List<RelatorioSiope> relatorio, List<Object[]> objetos, Boolean removerCaracteresCpf) {
        int total = 1;
        System.out.println("relatorio " + relatorio.size());
        for (RelatorioSiope cabecalho : relatorio) {
            Object[] obj = new Object[22];
            if (objetos.isEmpty()) {
                obj[0] = "T";
                obj[1] = "Dados Gerais \\ Remuneração dos Profissionais de Educação \\ " + cabecalho.getMes() + " - " + MesDTO.getMesToInt(Integer.parseInt(cabecalho.getMes()));
                objetos.add(obj);
                obj = new Object[22];
                obj[0] = "I";
            } else {
                obj[0] = "I";
            }
            obj[1] = total;
            obj[2] = cabecalho.getMes();
            String replace = cabecalho.getCpf() != null ? StringUtil.removeCaracteresEspeciais(cabecalho.getCpf()).replace(" ", "") : "";
            obj[3] = removerCaracteresCpf ? "=\"" + replace + "\"" : cabecalho.getCpf();
            obj[4] = cabecalho.getMatricula();
            obj[5] = cabecalho.getNome();
            obj[6] = cabecalho.getCodigoInep() != null ? cabecalho.getCodigoInep() : "";
            obj[7] = cabecalho.getUnidade();
            obj[8] = cabecalho.getCargaHoraria() != null ? cabecalho.getCargaHoraria() : 0;
            obj[9] = cabecalho.getCodigoCategoria() != null ? cabecalho.getCodigoCategoria() : "";
            obj[10] = cabecalho.getCategoriaProfissionalSiope() != null ? cabecalho.getCategoriaProfissionalSiope() : "";
            obj[11] = cabecalho.getCodigoServicoSiope() != null ? cabecalho.getCodigoServicoSiope() : "";
            obj[12] = cabecalho.getTipoServicoSiope() != null ? cabecalho.getTipoServicoSiope() : "";
            obj[13] = cabecalho.getCodigoModalidade().longValue() == 1 ? 1 : 2;
            obj[14] = cabecalho.getCodigoModalidade().longValue() == 1 ? "Efetivo" : "Temporário";
            obj[15] = cabecalho.getSegmentoAtuacao() != null ? SegmentoAtuacao.valueOf(cabecalho.getSegmentoAtuacao()).getCodigo() : "";
            obj[16] = cabecalho.getSegmentoAtuacao() != null ? SegmentoAtuacao.valueOf(cabecalho.getSegmentoAtuacao()).getDescricao() : "";
            obj[17] = cabecalho.getVencimentoBase();
            if (cabecalho.getTipoDeCargo() != null && TipoDeCargo.PROFESSOR.equals(cabecalho.getTipoDeCargo())) {
                obj[18] = cabecalho.getValorBruto();
            } else {
                obj[18] = BigDecimal.ZERO;
            }
            if (cabecalho.getTipoDeCargo() != null && TipoDeCargo.OUTROS.equals(cabecalho.getTipoDeCargo())) {
                obj[19] = cabecalho.getValorBruto();
            } else {
                obj[19] = BigDecimal.ZERO;
            }
            obj[20] = BigDecimal.ZERO;
            obj[21] = ((BigDecimal) obj[18]).add((BigDecimal) obj[19]).add((BigDecimal) obj[20]);
            total++;
            objetos.add(obj);
        }
    }


}
