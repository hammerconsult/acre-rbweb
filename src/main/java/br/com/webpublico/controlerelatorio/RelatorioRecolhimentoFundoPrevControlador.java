package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controlerelatorio.rh.RelatorioPagamentoRH;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.comum.relatorio.ConfiguracaoDeRelatorio;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.rh.relatorios.TipoLotacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.GrupoRecursoFPFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.RecursoFPFacade;
import br.com.webpublico.negocios.TipoPrevidenciaFPFacade;
import br.com.webpublico.negocios.comum.ConfiguracaoDeRelatorioFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.collections.CollectionUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Paschualleto
 * Date: 21/05/14
 * Time: 10:38
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-recolhimento-fundo-previdenciario", pattern = "/relatorio/recolhimento-fundo-previdenciario/", viewId = "/faces/rh/relatorios/relatoriorecolhimentofundoprevidenciario.xhtml")
})
public class RelatorioRecolhimentoFundoPrevControlador extends RelatorioPagamentoRH {

    @EJB
    private ConfiguracaoDeRelatorioFacade configuracaoDeRelatorioFacade;
    @EJB
    private TipoPrevidenciaFPFacade previdenciaFPFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private GrupoRecursoFPFacade grupoRecursoFPFacade;
    @EJB
    private RecursoFPFacade recursoFPFacade;
    private PessoaFisica pessoaFisica;
    private TipoPrevidenciaFP tipoPrevidenciaFP;
    private Boolean subFolha;
    private Boolean detalhado;
    private TipoLotacao tipoLotacao;
    private List<HierarquiaOrganizacional> hierarquias;
    private List<RecursoFP> recursos;
    private List<GrupoRecursoFP> gruposRecursoFPs;
    private Mes mesRelatorio;

    public RelatorioRecolhimentoFundoPrevControlador() {
        geraNoDialog = Boolean.TRUE;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
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
        dto.adicionarParametro("FILTROS", "");
        dto.adicionarParametro("MODULO", "RECURSOS HUMANOS");
        dto.adicionarParametro("NOMERELATORIO", "RECOLHIMENTO DE FUNDO PREVIDENCIÁRIO - SEGURADO");
        dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO E GESTÃO");
        dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getLogin(), false);
        dto.adicionarParametro("VERSAODESCRICAO", montarDescricaoVersao());
        dto.adicionarParametro("TIPO_PREVIDENCIA", tipoPrevidenciaFP.getDescricao());
        dto.adicionarParametro("TIPO_REGIME_PREVIDENCIARIO", tipoPrevidenciaFP.getTipoRegimePrevidenciario().getToDto());
        dto.adicionarParametro("DETALHADO", detalhado);
        dto.adicionarParametro("MES", (mesRelatorio.getNumeroMesIniciandoEmZero()));
        dto.adicionarParametro("ANO", getAno());
        dto.adicionarParametro("TIPOFOLHA", getTipoFolhaDePagamento().getDescricao());
        dto.adicionarParametro("DATAOPERACAO", DataUtil.getDataFormatada(getSistemaFacade().getDataOperacao()));
        dto.adicionarParametro("SQL", montarConsultas());
        dto.adicionarParametro("CODIGOHO", hierarquiaOrganizacional.getCodigoSemZerosFinais());
        dto.adicionarParametro("TIPO_FOLHAPAGAMENTO", getTipoFolhaDePagamento());
        dto.adicionarParametro("NOME_ARQUIVO", getNomeRelatorio());
        dto.setApi("rh/relatorio-recolhimento-fundo-previdenciario/");
        return dto;
    }

    private String getNomeRelatorio() {
        return "RECOLHIMENTO DE FUNDO PREVIDENCIÁRIO - SEGURADO";
    }

    private String montarConsultas() {
        String filtros = "";
        filtros += recuperarCodigoVerba(tipoPrevidenciaFP);
        filtros += montarCampoVersao();
        if (!recuperarSomenteRecursosSelecionados().isEmpty()) {
            filtros += " and (";
            for (RecursoFP recursoFP : recuperarSomenteRecursosSelecionados()) {
                filtros += " rec.codigo like '" + recursoFP.getCodigo() + "' or ";
            }
            filtros = filtros.substring(0, filtros.length() - 3);
            filtros += ")";
        }
        if (!recuperarSomenteLotacoesSelecionados().isEmpty()) {
            filtros += " and (";
            for (HierarquiaOrganizacional hierarquia : recuperarSomenteLotacoesSelecionados()) {
                filtros += " ho.codigo like '" + hierarquia.getCodigoSemZerosFinais() + "%' or ";
            }
            filtros = filtros.substring(0, filtros.length() - 3);
            filtros += ")";
        }
        if (!recuperarSomenteGrupoRecursosSelecionados().isEmpty()) {
            filtros += " and gpr.id in (";
            for (GrupoRecursoFP grupo : recuperarSomenteGrupoRecursosSelecionados()) {
                filtros += grupo.getId() + ", ";
            }
            filtros = filtros.substring(0, filtros.length() - 2);
            filtros += ")";
        }
        return filtros;
    }

    private String recuperarCodigoVerba(TipoPrevidenciaFP tipoPrevidenciaFP) {
        List<EventoFP> eventoFPs = previdenciaFPFacade.getEventoFPFacade().buscarEventoPorTipoPrevidencia(tipoPrevidenciaFP);
        if (CollectionUtils.isEmpty(eventoFPs)) {
            FacesUtil.addAtencao("Verba não configurada para o " + tipoPrevidenciaFP.getDescricao());
            return "";
        }
        StringBuilder codigos = new StringBuilder("");
        codigos.append(" and e.codigo IN ( ");
        for (EventoFP eventoFP : eventoFPs) {
            codigos.append(eventoFP.getCodigo()).append(", ");
        }
        int indexOf = codigos.lastIndexOf(",");
        codigos.delete(indexOf, codigos.length() - 1);
        codigos.append(" ) ");
        return codigos.toString();
    }

    @Override
    @URLAction(mappingId = "relatorio-recolhimento-fundo-previdenciario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        setMes(null);
        setAno(null);
        subFolha = Boolean.TRUE;
        detalhado = Boolean.FALSE;
        hierarquiaOrganizacional = null;
        setTipoFolhaDePagamento(null);
        tipoPrevidenciaFP = null;
        gruposRecursoFPs = Lists.newArrayList();
        hierarquias = Lists.newArrayList();
        recursos = Lists.newArrayList();
        tipoLotacao = null;
    }

    @Override
    public Integer getMes() {
        return mesRelatorio.getNumeroMesIniciandoEmZero();
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (getMesRelatorio() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês é obrigatório");
        }
        if (getAno() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano é obrigatório.");
        }
        if (getTipoFolhaDePagamento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Folha é obrigatório.");
        }
        if (hierarquiaOrganizacional == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Órgão é obrigatório.");
        }
        if (tipoPrevidenciaFP == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Previdência é obrigatório.");
        }
        if (tipoLotacao == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Lotação é obrigatório.");
        }
        if (isRelatorioPorOrgaoLotacaoFuncional()) {
            if (recuperarSomenteLotacoesSelecionados().size() <= 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório selecionar uma lotação funcional para os filtros informados.");
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
        ve.lancarException();
    }

    public void limparItensSelecionados() {
        gruposRecursoFPs = Lists.newArrayList();
        hierarquias = Lists.newArrayList();
        recursos = Lists.newArrayList();
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

    public List<SelectItem> getTipoLotacoes() {
        return Util.getListSelectItem(TipoLotacao.values());
    }

    private void recuperarHierarquias() {
        if (hierarquiaOrganizacional != null) {
            hierarquias = hierarquiaOrganizacionalFacade
                .buscarHierarquiasFiltrandoVigentesPorPeriodoAndTipo(hierarquiaOrganizacional.getCodigoSemZerosFinais(),
                    getDataVigencia(), getDataVigencia(), TipoHierarquiaOrganizacional.ADMINISTRATIVA);
            hierarquias.remove(0);
        }
        if (!hierarquias.isEmpty()) {
            Collections.sort(hierarquias, new Comparator<HierarquiaOrganizacional>() {
                @Override
                public int compare(HierarquiaOrganizacional o1, HierarquiaOrganizacional o2) {
                    String i1 = o1.getCodigo();
                    String i2 = o2.getCodigo();
                    return i1.compareTo(i2);
                }
            });
        }
    }

    private void recuperarRecursosFP() {
        if (getMes() != null && getAno() != null) {
            recursos = recursoFPFacade.buscarRecursosPorMesAndAnoFolha(mesRelatorio, getAno(), false);
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

    private void recuperarGrupoRecursosFP() {
        gruposRecursoFPs = grupoRecursoFPFacade.buscarGruposRecursoFPPorDataOperacao(getDataVigencia());
        if (!gruposRecursoFPs.isEmpty()) {
            Collections.sort(gruposRecursoFPs, new Comparator<GrupoRecursoFP>() {
                @Override
                public int compare(GrupoRecursoFP o1, GrupoRecursoFP o2) {
                    String i1 = o1.getNome();
                    String i2 = o2.getNome();
                    return i1.compareTo(i2);
                }
            });
        }
    }

    private Date getDataVigencia() {
        if (mesRelatorio != null && getAno() != null) {
            return DataUtil.criarDataComMesEAno(mesRelatorio.getNumeroMes(), getAno()).toDate();
        } else {
            return UtilRH.getDataOperacao();
        }
    }

    private boolean isMesValido() {
        return getMes() != null && getMes().compareTo(1) > 0 && getMes().compareTo(12) < 0;
    }

    public boolean isRelatorioPorOrgaoLotacaoFuncional() {
        return TipoLotacao.LOTACAO_FUNCIONAL.equals(tipoLotacao);
    }

    public boolean isRelatorioPorOrgaoRecursoFP() {
        return TipoLotacao.RECURSO_FP.equals(tipoLotacao);
    }

    public boolean isRelatorioPorOrgaoGrupoRecursoFP() {
        return TipoLotacao.GRUPO_RECURSO_FP.equals(tipoLotacao);
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
        for (GrupoRecursoFP grupo : gruposRecursoFPs) {
            grupo.setSelecionado(true);
        }
    }

    public void adicionarTodasLotacoes() {
        for (HierarquiaOrganizacional hierarquia : hierarquias) {
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
        for (HierarquiaOrganizacional ho : hierarquias) {
            ho.setSelecionado(false);
        }
    }

    public void removerTodosGrupos() {
        for (GrupoRecursoFP grupo : gruposRecursoFPs) {
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
        for (GrupoRecursoFP grupo : gruposRecursoFPs) {
            if (!grupo.getSelecionado())
                return false;
        }
        return true;
    }

    public Boolean todasLotacoesMarcadas() {
        for (HierarquiaOrganizacional hierarquia : hierarquias) {
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
        for (GrupoRecursoFP grupoRecursoFP : gruposRecursoFPs) {
            if (grupoRecursoFP.getSelecionado()) {
                retorno.add(grupoRecursoFP);
            }
        }
        return retorno;
    }

    public List<HierarquiaOrganizacional> recuperarSomenteLotacoesSelecionados() {
        List<HierarquiaOrganizacional> retorno = new ArrayList<>();
        for (HierarquiaOrganizacional ho : hierarquias) {
            if (ho.isSelecionado()) {
                retorno.add(ho);
            }
        }
        return retorno;
    }

    public List<SelectItem> getMesesRelatorios() {
        return Util.getListSelectItem(Mes.values(), false);
    }

    @Override
    public List<SelectItem> getTiposFolha() {
        return Util.getListSelectItem(TipoFolhaDePagamento.values(), false);
    }

    public List<SelectItem> getTipoPrevidencia() {
        return Util.getListSelectItem(previdenciaFPFacade.lista());
    }

    public List<HierarquiaOrganizacional> completarHierarquias(String parte) {
        return hierarquiaOrganizacionalFacade.filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public TipoPrevidenciaFP getTipoPrevidenciaFP() {
        return tipoPrevidenciaFP;
    }

    public void setTipoPrevidenciaFP(TipoPrevidenciaFP tipoPrevidenciaFP) {
        this.tipoPrevidenciaFP = tipoPrevidenciaFP;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
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

    public TipoLotacao getTipoLotacao() {
        return tipoLotacao;
    }

    public void setTipoLotacao(TipoLotacao tipoLotacao) {
        this.tipoLotacao = tipoLotacao;
    }

    public List<HierarquiaOrganizacional> getHierarquias() {
        return hierarquias;
    }

    public void setHierarquias(List<HierarquiaOrganizacional> hierarquias) {
        this.hierarquias = hierarquias;
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

    public Mes getMesRelatorio() {
        return mesRelatorio;
    }

    public void setMesRelatorio(Mes mesRelatorio) {
        this.mesRelatorio = mesRelatorio;
    }

    public void setarNumeroMes() {
        if (mesRelatorio != null) {
            setMes(mesRelatorio.getNumeroMes());
        }
    }
}
