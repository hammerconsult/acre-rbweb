package br.com.webpublico.controlerelatorio.rh;

import br.com.webpublico.entidades.Entidade;
import br.com.webpublico.entidades.GrupoRecursoFP;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.comum.relatorio.ConfiguracaoDeRelatorio;
import br.com.webpublico.enums.CategoriaDeclaracaoPrestacaoContas;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.EntidadeFacade;
import br.com.webpublico.negocios.FolhaDePagamentoFacade;
import br.com.webpublico.negocios.GrupoRecursoFPFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.comum.ConfiguracaoDeRelatorioFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.base.Strings;
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
import javax.faces.model.SelectItem;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Created by zaca on 05/04/17.
 */
@ManagedBean(name = "relatorioAgrupamentoBasesEncargosControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorioAgrupamentoBasesEncargos", pattern = "/relatorio/agrupamento-bases-recolhimento-encargos-sociais/", viewId = "/faces/rh/relatorios/relatorioagrupamentobasesrecolhimentoencargosociais.xhtml")
})
public class RelatorioAgrupamentoBasesRecolhimentoEncargosSociaisControlador implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioAgrupamentoBasesRecolhimentoEncargosSociaisControlador.class);

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ConfiguracaoDeRelatorioFacade configuracaoDeRelatorioFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    private GrupoRecursoFPFacade grupoRecursoFPFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    private Integer ano;
    private Mes mes;
    private Integer versao;
    private Boolean exibirFgts;
    private Boolean exibirInss;
    private Boolean exibirRpps;
    private Boolean exibirRpc;
    private Boolean exibirIrrf;
    private Boolean exibirSalarioMaternidade;
    private Boolean exibirSalarioFamilia;
    private Boolean subFolha;
    private Boolean detalhado;
    private GrupoRecursoFP[] recursosSelecionados;
    private List<GrupoRecursoFP> grupoRecursoFPs;
    private Entidade entidade;
    private TipoFolhaDePagamento tipoFolhaDePagamento;

    @URLAction(mappingId = "relatorioAgrupamentoBasesEncargos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        mes = null;
        ano = null;
        tipoFolhaDePagamento = null;
        exibirFgts = Boolean.FALSE;
        exibirInss = Boolean.FALSE;
        exibirIrrf = Boolean.FALSE;
        exibirRpps = Boolean.FALSE;
        exibirRpc = Boolean.FALSE;
        exibirSalarioFamilia = Boolean.FALSE;
        exibirSalarioMaternidade = Boolean.FALSE;
        subFolha = Boolean.FALSE;
        detalhado = Boolean.FALSE;
        grupoRecursoFPs = Lists.newArrayList();
        recursosSelecionados = new GrupoRecursoFP[]{};
        entidade = null;
    }

    private ResponseEntity<byte[]> retornarByte(RelatorioDTO dto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RelatorioDTO> request = new HttpEntity<>(dto, headers);
        ConfiguracaoDeRelatorio configuracao = configuracaoDeRelatorioFacade.getConfiguracaoPorChave();
        return new RestTemplate().exchange(configuracao.getUrl() + dto.getApi() + "gerar", HttpMethod.POST, request, byte[].class);
    }

    public StreamedContent gerarRelatorioEmExcel() {
        try {
            validarCampos();
            RelatorioDTO dto = montarParametros();
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("rh/agrupamento-bases-recolhimento-encargos-sociais/excel/");
            dto.adicionarParametro("NOME_ARQUIVO", getNomeRelatorio());
            ResponseEntity<byte[]> responseEntity = retornarByte(dto);
            byte[] bytes = responseEntity.getBody();
            InputStream stream = new ByteArrayInputStream(bytes);
            return new DefaultStreamedContent(stream, "application/xlsx", "RelatorioRecolhimentoFundoPrevidenciario" + ".xlsx");
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
        return null;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = montarParametros();
            dto.setNomeRelatorio("RELATÓRIO-AGRUPAMENTO-BASE-RECOLHIMENTO-ENCARGOS-SOCIAIS");
            dto.setApi("rh/agrupamento-bases-recolhimento-encargos-sociais/");
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao gerar relatório de Agrupamento de Bases para Recolhimento dos Encargos Sociais {}", e);
            FacesUtil.addErrorGenerico(e);
        }
    }

    private RelatorioDTO montarParametros() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.setNomeParametroBrasao("BRASAO");
        dto.adicionarParametro("MODULO", "RECURSOS HUMANOS");
        dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO");
        dto.adicionarParametro("NOMERELATORIO", getNomeRelatorio());
        dto.adicionarParametro("MES", mes.getNumeroMes());
        dto.adicionarParametro("ANO", ano);
        dto.adicionarParametro("TIPOFOLHA", tipoFolhaDePagamento.getDescricao());
        dto.adicionarParametro("DETALHADO", detalhado);
        dto.adicionarParametro("VERSAODESCRICAO", " Versão: " + (versao != null ? versao : "Todas"));
        dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome());
        dto.adicionarParametro("exibirFgts", exibirFgts);
        dto.adicionarParametro("exibirInss", exibirInss);
        dto.adicionarParametro("exibirRpps", exibirRpps);
        dto.adicionarParametro("exibirRpc", exibirRpc);
        dto.adicionarParametro("exibirIrrf", exibirIrrf);
        dto.adicionarParametro("exibirSalarioMaternidade", exibirSalarioMaternidade);
        dto.adicionarParametro("exibirSalarioFamilia", exibirSalarioFamilia);
        dto.adicionarParametro("complementoQuery", getComplementoQuery());
        dto.adicionarParametro("CRITERIOSUTILIZADOS", buscarCriteriosUtilizados());
        return dto;
    }

    private String buscarCriteriosUtilizados() {
        StringBuilder criterios = new StringBuilder("");
        if (exibirInss) {
            criterios.append("INSS, ");
        }
        if (exibirRpps) {
            criterios.append("RPPS, ");
        }
        if (exibirRpc) {
            criterios.append("RPC, ");
        }
        if (exibirIrrf) {
            criterios.append("IRRF, ");
        }
        if (exibirSalarioMaternidade) {
            criterios.append("Salário Maternidade, ");
        }
        if (exibirSalarioFamilia) {
            criterios.append("Salário Família, ");
        }
        if (exibirFgts) {
            criterios.append("FGTS, ");
        }
        if (!Strings.isNullOrEmpty(criterios.toString())) {
            return criterios.delete(criterios.length() - 2, criterios.length()).append(".").toString();
        }
        return "";
    }

    private String getNomeRelatorio() {
        return "RELATÓRIO DE AGRUPAMENTOS DE BASES PARA RECOLHIMENTO DOS ENCARGOS SOCIAIS";
    }

    public String getComplementoQuery() {
        String clausula = " where ";
        StringBuilder filtros = new StringBuilder();
        if (versao != null) {
            filtros.append(clausula).append(" folha.versao = ").append(versao);
            clausula = " and ";
        }
        if (tipoFolhaDePagamento != null) {
            filtros.append(clausula).append(" ficha.tipofolhadepagamento = '").append(tipoFolhaDePagamento.name()).append("'");
            clausula = " and ";
        }
        if (mes != null) {
            filtros.append(clausula).append(" ficha.mes = ").append(mes.getNumeroMesIniciandoEmZero());
            clausula = " and ";
        }
        if (ano != null) {
            filtros.append(clausula).append(" ficha.ano = ").append(ano);
            clausula = " and ";
        }
        if (recursosSelecionados.length != 0) {
            String idsGrupoRecurso = "";
            for (GrupoRecursoFP grupoRecursoFP : recursosSelecionados) {
                idsGrupoRecurso += grupoRecursoFP.getId() + ",";
            }
            filtros.append(clausula).append(" grupo.id in (").append(idsGrupoRecurso.substring(0, idsGrupoRecurso.length() - 1)).append(")");
            clausula = " and ";
        }
        if (exibirFgts || exibirInss || exibirIrrf || exibirSalarioFamilia || exibirSalarioMaternidade) {
            filtros.append(clausula);
            filtros.append(" ( ");
            if (exibirFgts) {
                filtros.append("(ficha.fgts <> 0 or ficha.basefgts <> 0) or ");
            }
            if (exibirInss) {
                filtros.append(" (ficha.inss <> 0  or ficha.baseinss <> 0) or ");
            }
            if (exibirRpps) {
                filtros.append(" (ficha.rpps <> 0  or ficha.baserpps <> 0) or ");
            }
            if (exibirIrrf) {
                filtros.append(" (ficha.irrf <> 0  or ficha.baseirrf <> 0) or ");
            }
            if (exibirSalarioFamilia) {
                filtros.append(" (ficha.salariofamilia <> 0  or ficha.basesalariofamilia <> 0) or ");
            }
            if (exibirSalarioMaternidade) {
                filtros.append(" (ficha.salariomaternidade <> 0  or ficha.basesalariomaternidade <> 0) or ");
            }
            if (exibirRpc) {
                filtros.append("    ((select coalesce(sum(item.valor), 0) ");
                filtros.append(" from itemfichafinanceirafp item ");
                filtros.append(" inner join eventofp ev on item.eventofp_id = ev.id ");
                filtros.append(" where item.fichafinanceirafp_id = fichafolha.id ");
                filtros.append(" and ev.tipocontribuicaobbprev = 'SERVIDOR') <> 0 or ");
                filtros.append("    (select coalesce(sum(item.valorbasedecalculo), 0) ");
                filtros.append(" from itemfichafinanceirafp item ");
                filtros.append(" inner join eventofp ev on item.eventofp_id = ev.id ");
                filtros.append(" where item.fichafinanceirafp_id = fichafolha.id ");
                filtros.append(" and ev.tipocontribuicaobbprev = 'SERVIDOR') <> 0) or ");
            }
            filtros.delete(filtros.length() - 4, filtros.length());
            filtros.append(" ) ");
        }
        return filtros.toString();
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (mes == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês é obrigatório.");
        }
        if (ano == null || ano == 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano é obrigatório.");
        }
        if (tipoFolhaDePagamento == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Folha é obrigatório.");
        }
        if (entidade == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Estabelecimento é obrigatório.");
        }
        if (recursosSelecionados.length == 0) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione O(s) Grupo(s) de Recurso Folha de Pagamento.");
        }
        ve.lancarException();
    }

    public List<SelectItem> getMeses() {
        return Util.getListSelectItem(Mes.values(), false);
    }

    public List<SelectItem> getTiposFolhasPagamento() {
        return Util.getListSelectItem(TipoFolhaDePagamento.values(), false);
    }

    public List<SelectItem> getVersoes() {
        if (mes != null && getAno() != null && getTipoFolhaDePagamento() != null) {
            return Util.getListSelectItem(folhaDePagamentoFacade.recuperarVersao(mes, getAno(), getTipoFolhaDePagamento()), false);
        }
        return Lists.newArrayList();
    }

    public List<SelectItem> getEntidades() {
        return Util.getListSelectItem(entidadeFacade.buscarEntidadesDeclarantesVigentePorCategoria(CategoriaDeclaracaoPrestacaoContas.SEFIP), false);
    }

    public void carregarGrupoRecursosFPPorEntidade() {
        if (getEntidade() != null && ano != null && mes != null) {
            List<GrupoRecursoFP> grupoRecursoFPs = Lists.newLinkedList();
            for (HierarquiaOrganizacional hierarquiaOrganizacional : buscarHierarquiasDaEntidade()) {
                for (GrupoRecursoFP grupoRecursoFP : grupoRecursoFPFacade.buscarGruposRecursoFPPorOrgaoAndDataOperacao(hierarquiaOrganizacional)) {
                    if (!grupoRecursoFPs.contains(grupoRecursoFP)) {
                        grupoRecursoFPs.add(grupoRecursoFP);
                    }
                }
            }
            setGrupoRecursoFPs(grupoRecursoFPs);
        }
    }

    public List<HierarquiaOrganizacional> buscarHierarquiasDaEntidade() {
        List<HierarquiaOrganizacional> hierarquias = entidadeFacade.buscarHierarquiasDaEntidade(entidade, CategoriaDeclaracaoPrestacaoContas.SEFIP, DataUtil.primeiroDiaMes(DataUtil.criarDataComMesEAno(mes.getNumeroMes(), ano).toDate()).getTime(), DataUtil.ultimoDiaMes(DataUtil.criarDataComMesEAno(mes.getNumeroMes(), ano).toDate()).getTime());
        Collections.sort(hierarquias);
        return hierarquias;
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

    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }

    public TipoFolhaDePagamento getTipoFolhaDePagamento() {
        return tipoFolhaDePagamento;
    }

    public void setTipoFolhaDePagamento(TipoFolhaDePagamento tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }

    public Boolean getExibirFgts() {
        return exibirFgts;
    }

    public void setExibirFgts(Boolean exibirFgts) {
        this.exibirFgts = exibirFgts;
    }

    public Boolean getExibirInss() {
        return exibirInss;
    }

    public void setExibirInss(Boolean exibirInss) {
        this.exibirInss = exibirInss;
    }

    public Boolean getExibirIrrf() {
        return exibirIrrf;
    }

    public void setExibirIrrf(Boolean exibirIrrf) {
        this.exibirIrrf = exibirIrrf;
    }

    public Boolean getExibirSalarioMaternidade() {
        return exibirSalarioMaternidade;
    }

    public void setExibirSalarioMaternidade(Boolean exibirSalarioMaternidade) {
        this.exibirSalarioMaternidade = exibirSalarioMaternidade;
    }

    public Boolean getExibirSalarioFamilia() {
        return exibirSalarioFamilia;
    }

    public void setExibirSalarioFamilia(Boolean exibirSalarioFamilia) {
        this.exibirSalarioFamilia = exibirSalarioFamilia;
    }

    public Boolean getSubFolha() {
        return subFolha;
    }

    public void setSubFolha(Boolean subFolha) {
        this.subFolha = subFolha;
    }

    public Boolean getDetalhado() {
        return detalhado;
    }

    public void setDetalhado(Boolean detalhado) {
        this.detalhado = detalhado;
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

    public Entidade getEntidade() {
        return entidade;
    }

    public void setEntidade(Entidade entidade) {
        this.entidade = entidade;
    }

    public Boolean getExibirRpps() {
        return exibirRpps;
    }

    public void setExibirRpps(Boolean exibirRpps) {
        this.exibirRpps = exibirRpps;
    }

    public Boolean getExibirRpc() {
        return exibirRpc;
    }

    public void setExibirRpc(Boolean exibirRpc) {
        this.exibirRpc = exibirRpc;
    }
}
