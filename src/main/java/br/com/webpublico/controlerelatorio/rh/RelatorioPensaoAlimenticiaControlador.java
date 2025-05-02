package br.com.webpublico.controlerelatorio.rh;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.GrupoRecursoFP;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.FolhaDePagamentoFacade;
import br.com.webpublico.negocios.GrupoRecursoFPFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.UtilRH;
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
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by carlos on 30/03/17.
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-pensao-alimenticia", pattern = "/relatorio/relatorio-pensao-alimenticia/", viewId = "/faces/rh/relatorios/relatoriopensaoalimenticia.xhtml")
})
@ManagedBean
public class RelatorioPensaoAlimenticiaControlador implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private GrupoRecursoFPFacade grupoRecursoFPFacade;
    private static final Logger logger = LoggerFactory.getLogger(RelatorioPensaoAlimenticiaControlador.class);
    private Mes mes;
    private Integer ano;
    private TipoFolhaDePagamento tipoFolhaDePagamento;
    private ConverterAutoComplete converterHierarquia;
    private Integer versao;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private String filtro;
    private GrupoRecursoFP[] recursosSelecionados;
    private List<GrupoRecursoFP> grupoRecursoFPs;

    public RelatorioPensaoAlimenticiaControlador() {

    }

    @URLAction(mappingId = "relatorio-pensao-alimenticia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        mes = Mes.JANEIRO;
        ano = getSistemaControlador().getExercicioCorrenteAsInteger();
        tipoFolhaDePagamento = null;
        versao = null;
        filtro = null;
        hierarquiaOrganizacional = null;
        recursosSelecionados = new GrupoRecursoFP[]{};
        grupoRecursoFPs = Lists.newArrayList();
        carregarGrupoRecursosFPPorHierarquia();
    }

    public void gerarRelatorio(String tipoRelatorio) {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorio));
            dto.setApi("rh/relatorio-pensao-alimenticia/");
            ReportService.getInstance().gerarRelatorio(sistemaControlador.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
            logger.error("Erro ao tentar gerar reltório. ", e);
        }
    }

    private RelatorioDTO montarRelatorioDTO() {
        RelatorioDTO dto = new RelatorioDTO();
        filtro = hierarquiaOrganizacional != null ? "ÓRGÃO: " + hierarquiaOrganizacional.getSubordinada().getDescricao().toUpperCase() : "";
        dto.setNomeParametroBrasao("BRASAO");
        dto.setNomeRelatorio("Relatório Pensão Alimentícia");
        dto.adicionarParametro("MUNICIPIO", "PREFEITURA MUNICIPAL DE " + getSistemaControlador().getMunicipio().toUpperCase());
        dto.adicionarParametro("USUARIO", sistemaControlador.getUsuarioCorrente().getLogin(), false);
        dto.adicionarParametro("MODULO", "Recursos Humanos");
        dto.adicionarParametro("FILTROS", filtro);
        dto.adicionarParametro("ANO", getAno());
        dto.adicionarParametro("MES", getMes().getDescricao().toUpperCase());
        dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO");
        dto.adicionarParametro("DEPARTAMENTO", "DEPARTAMENTO DE RECURSOS HUMANOS");
        dto.adicionarParametro("VERSAO", versao);
        dto.adicionarParametro("TIPOFOLHA", tipoFolhaDePagamento.toString().toUpperCase());
        dto.adicionarParametro("CODIGO_HIERARQUIA", hierarquiaOrganizacional != null ? hierarquiaOrganizacional.getCodigoSemZerosFinais() + "%" : null);
        dto.adicionarParametro("TIPO_FOLHA_DE_PAGAMENTO", getTipoFolhaDePagamento().getToDto());
        dto.adicionarParametro("MES_NUMERICO", getMes().getNumeroMes() - 1);
        dto.adicionarParametro("COMPLEMENTO_WHERE", getComplementoWhere());
        return dto;
    }

    private String getComplementoWhere() {
        StringBuilder complemento = new StringBuilder("");
        if (recursosSelecionados != null && recursosSelecionados.length > 0) {
            String idsGrupoRecurso = "";
            for (GrupoRecursoFP grupoRecursoFP : recursosSelecionados) {
                idsGrupoRecurso += grupoRecursoFP.getId() + ",";
            }
            complemento.append(" AND GRUPO.ID in (").append(idsGrupoRecurso, 0, idsGrupoRecurso.length() - 1).append(")");
        }
        return complemento.toString();
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (getAno() == null || getAno() == 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano é obrigatório.");
        }

        if (getTipoFolhaDePagamento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Folha é obrigatório.");
        }

        if (getVersao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Versão é obrigatório.");
        }

        ve.lancarException();
    }

    public List<SelectItem> getVersoes() {
        List<SelectItem> retorno = new ArrayList<>();
        if (mes != null && ano != null && tipoFolhaDePagamento != null) {
            for (Integer versao : folhaDePagamentoFacade.recuperarVersao(mes, ano, tipoFolhaDePagamento)) {
                retorno.add(new SelectItem(versao, String.valueOf(versao)));
            }
        }
        return retorno;
    }

    public List<SelectItem> getTiposFolha() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (TipoFolhaDePagamento tipo : TipoFolhaDePagamento.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getMeses() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (Mes object : Mes.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<HierarquiaOrganizacional> completaHierarquia(String parte) {
        return hierarquiaOrganizacionalFacade.filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public void carregarGrupoRecursosFPPorHierarquia() {
        recursosSelecionados = new GrupoRecursoFP[]{};
        grupoRecursoFPs = Lists.newArrayList();
        if (ano != null && mes != null) {
            Date dataOperacao = DataUtil.ultimoDiaMes(DataUtil.criarDataPrimeiroDiaMes(mes.getNumeroMes(), ano));
            if (hierarquiaOrganizacional != null) {
                if (hierarquiaOrganizacional.getIndiceDoNo() == 1) {
                    List<HierarquiaOrganizacional> hierarquias = hierarquiaOrganizacionalFacade.buscarHierarquiasAdministrativasFilhasPorNivel(hierarquiaOrganizacional, dataOperacao, 2);
                    for (HierarquiaOrganizacional ho : hierarquias) {
                        grupoRecursoFPs.addAll(grupoRecursoFPFacade.buscarGruposRecursoFPPorOrgaoAndDataOperacao(ho, dataOperacao));
                    }
                } else {
                    grupoRecursoFPs = grupoRecursoFPFacade.buscarGruposRecursoFPPorOrgaoAndDataOperacao(hierarquiaOrganizacional, dataOperacao);
                }
            } else {
                grupoRecursoFPs = grupoRecursoFPFacade.buscarGruposRecursoFPPorDataOperacao(dataOperacao);
            }
        }
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
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

    public ConverterAutoComplete getConverterHierarquia() {
        if (converterHierarquia == null) {
            converterHierarquia = new ConverterAutoComplete(HierarquiaOrganizacional.class, hierarquiaOrganizacionalFacade);
        }
        return converterHierarquia;
    }

    public void setConverterHierarquia(ConverterAutoComplete converterHierarquia) {
        this.converterHierarquia = converterHierarquia;
    }

    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public GrupoRecursoFP[] getRecursosSelecionados() {
        return recursosSelecionados;
    }

    public void setRecursosSelecionados(GrupoRecursoFP[] recursosSelecionados) {
        this.recursosSelecionados = recursosSelecionados;
    }

    public List<GrupoRecursoFP> getGrupoRecursoFPs() {
        return grupoRecursoFPs;
    }

    public void setGrupoRecursoFPs(List<GrupoRecursoFP> grupoRecursoFPs) {
        this.grupoRecursoFPs = grupoRecursoFPs;
    }
}
