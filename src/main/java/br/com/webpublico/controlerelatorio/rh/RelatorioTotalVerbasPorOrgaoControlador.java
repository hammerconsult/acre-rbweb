package br.com.webpublico.controlerelatorio.rh;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.GrupoRecursoFP;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidadesauxiliares.rh.AssistenteRelatorioTotalVerbasPorOrgao;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.rh.OpcaoGeracaoRelatorio;
import br.com.webpublico.enums.rh.OpcaoVerba;
import br.com.webpublico.enums.rh.Verbas;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.*;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.*;

/**
 * Created by Buzatto on 29/10/2015.
 */
@ViewScoped
@ManagedBean
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-total-verbas-por-orgao", pattern = "/relatorio/relatorio-total-verbas-por-orgao/", viewId = "/faces/rh/relatorios/relatoriototalverbaspororgao.xhtml"),
    @URLMapping(id = "relatorio-total-verbas-por-orgao-entraram-sairam-folha", pattern = "/relatorio/relatorio-total-verbas-por-orgao-entraram-sairam-folha/", viewId = "/faces/rh/relatorios/relatoriototalverbaspororgaoentraramsairamfp.xhtml"),
})
public class RelatorioTotalVerbasPorOrgaoControlador extends AbstractReport {

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private EventoFPFacade eventoFPFacade;
    @EJB
    private GrupoRecursoFPFacade grupoRecursoFPFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;

    private StringBuilder filtros;
    private Integer mes;
    private Integer ano;
    private TipoFolhaDePagamento tipoFolhaDePagamento;
    private OpcaoGeracaoRelatorio opcao;
    private String agrupadoPorOrgao;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private List<GrupoRecursoFP> grupos;
    private Integer versao;
    private OpcaoVerba opcaoVerba;
    private Boolean relatorioVerbasEntraramESairamFP;
    private List<EventoFP> eventosFP;
    private Verbas verbasSelecionada;
    private EventoFP eventoFPSelecionado;
    private ConverterGenerico converterGenericoEventoFP;
    private boolean detalhado;

    public RelatorioTotalVerbasPorOrgaoControlador() {
        geraNoDialog = Boolean.TRUE;
    }

    public OpcaoVerba getOpcaoVerba() {
        return opcaoVerba;
    }

    public void setOpcaoVerba(OpcaoVerba opcaoVerba) {
        this.opcaoVerba = opcaoVerba;
    }

    @URLAction(mappingId = "relatorio-total-verbas-por-orgao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtros = new StringBuilder();
        mes = null;
        ano = null;
        tipoFolhaDePagamento = TipoFolhaDePagamento.NORMAL;
        opcao = null;
        versao = null;
        anularHierarquiaOrganizacional();
        instanciarListaDeGrupos();
        relatorioVerbasEntraramESairamFP = false;
    }

    @URLAction(mappingId = "relatorio-total-verbas-por-orgao-entraram-sairam-folha", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoRelatorioEntradaSaidaVerba() {
        filtros = new StringBuilder();
        mes = null;
        ano = null;
        tipoFolhaDePagamento = TipoFolhaDePagamento.NORMAL;
        opcao = null;
        versao = null;
        anularHierarquiaOrganizacional();
        instanciarListaDeGrupos();
        relatorioVerbasEntraramESairamFP = true;
        eventosFP = eventoFPFacade.listaEventosAtivosFolha();
        verbasSelecionada = Verbas.TODAS;
        converterGenericoEventoFP = new ConverterGenerico(EventoFP.class, eventoFPFacade);
    }

    public List<SelectItem> getTiposFolhaDePagamento() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoFolhaDePagamento tipo : TipoFolhaDePagamento.values()) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getOpcoes() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, "Selecione a opção..."));
        for (OpcaoGeracaoRelatorio opcao : OpcaoGeracaoRelatorio.values()) {
            toReturn.add(new SelectItem(opcao, opcao.getDescricao()));
        }
        return toReturn;
    }

    public List<HierarquiaOrganizacional> completarHierarquia(String parte) {
        return hierarquiaOrganizacionalFacade.filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), getData());
    }

    public void validarCampos(boolean servidoresEntraramESairamFP) {
        ValidacaoException ve = new ValidacaoException();
        if (mes == null) {
            ve.adicionarMensagemDeCampoObrigatorio("É obrigatório informar um Mês.");
        }
        if (ano == null) {
            ve.adicionarMensagemDeCampoObrigatorio("É obrigatório informar um Ano.");
        }
        if (tipoFolhaDePagamento == null) {
            ve.adicionarMensagemDeCampoObrigatorio("É obrigatório informar um Tipo de Folha de Pagamento.");
        }
        if (servidoresEntraramESairamFP) {
            if (verbasSelecionada == null) {
                ve.adicionarMensagemDeCampoObrigatorio("É obrigatório informar o filtro de Verbas.");
            } else if (Verbas.ESPECIFICAR.equals(verbasSelecionada) && eventosFP == null) {
                ve.adicionarMensagemDeCampoObrigatorio("É obrigatório informar qual a Verba.");
            }
        }
        ve.lancarException();
    }

    public void carregarRecursosFPPorHierarquiaOrganizacional(boolean servidoresEntraramESairamFP) {
        try {
            instanciarListaDeGrupos();
            validarCampos(servidoresEntraramESairamFP);
            List<GrupoRecursoFP> gruposPorOrgao = getGruposPorOrgao(hierarquiaOrganizacional);
            podeRecuperarGrupoRecursos();
            for (GrupoRecursoFP grupoRecursoFP : gruposPorOrgao) {
                if (grupoRecursoFPFacade.existeEventoFPParaGrupoAndMesAndAnoAndTipoFolha(grupoRecursoFP, mes, ano, tipoFolhaDePagamento)) {
                    grupos.add(grupoRecursoFP);
                }
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void podeRecuperarGrupoRecursos() {
        ValidacaoException ve = new ValidacaoException();
        if (mes == null || ano == null) {
            cancelarAgrupadorPorOrgaoAndHierarquiaOrganizacionalAndGrupos();
            ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório informar mês e ano para filtrar os grupos de recurso fp.");
        }
        lancarValidacaoExeption(ve);
    }


    private void lancarValidacaoExeption(ValidacaoException ve) {
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public List<GrupoRecursoFP> getGruposPorOrgao(HierarquiaOrganizacional hierarquiaOrganizacional) {
        if (versao != null) {
            return grupoRecursoFPFacade.buscarGruposRecursoFPPorHierarquiaAndMesAndAnoAndVersaoAndTipoFolha(hierarquiaOrganizacional, mes, ano, tipoFolhaDePagamento, versao);
        } else {
            return grupoRecursoFPFacade.buscarGruposRecursoFPPorHierarquiaAndMesAndAnoAndVersaoAndTipoFolha(hierarquiaOrganizacional, mes, ano, tipoFolhaDePagamento, null);
        }
    }

    private void instanciarListaDeGrupos() {
        grupos = new ArrayList<>();
    }

    public void marcarTodosGrupos() {
        for (GrupoRecursoFP grupo : grupos) {
            grupo.setSelecionado(Boolean.TRUE);
        }
    }

    public void desmarcarTodosGrupos() {
        for (GrupoRecursoFP grupo : grupos) {
            grupo.setSelecionado(Boolean.FALSE);
        }
    }

    public void marcarGrupoRecursoFP(GrupoRecursoFP grupoRecursoFP) {
        grupoRecursoFP.setSelecionado(Boolean.TRUE);
    }

    public void desmarcarGrupoRecursoFP(GrupoRecursoFP grupoRecursoFP) {
        grupoRecursoFP.setSelecionado(Boolean.FALSE);
    }

    public boolean todosGruposEstaoMarcados() {
        for (GrupoRecursoFP grupo : grupos) {
            if (!grupo.getSelecionado()) {
                return false;
            }
        }
        return true;
    }

    public boolean isOpcaoGeral() {
        return OpcaoGeracaoRelatorio.GERAL.equals(opcao);
    }

    public boolean isAgrupodoPorOrgao() {
        return "AGRUPADO_POR_ORGAO".equals(agrupadoPorOrgao);
    }

    public boolean naoEhAgrupadoPorOrgao() {
        return !isAgrupodoPorOrgao();
    }

    public boolean isOpcaoSecretaria() {
        return OpcaoGeracaoRelatorio.SECRETARIA.equals(opcao);
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            podeGerar();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO");
            dto.adicionarParametro("RAIZHIERARQUIA", hierarquiaOrganizacionalFacade.getRaizHierarquia(UtilRH.getDataOperacao()).toString());
            dto.adicionarParametro("EXERCICIO", getSistemaFacade().getExercicioCorrente().getAno() + "");
            dto.adicionarParametro("SECRETARIA", "DEPARTAMENTO DE RECURSOS HUMANOS");
            dto.adicionarParametro("NOMERELATORIO", getNomeRelatorio());
            dto.adicionarParametro("MODULO", "Recursos Humanos");
            dto.adicionarParametro("MES", DataUtil.getDescricaoMes(mes));
            dto.adicionarParametro("ANO", ano);
            dto.adicionarParametro("OPCAO", opcao.getDescricao());
            dto.adicionarParametro("opcaoGeracaoRelatorio", opcao);
            dto.adicionarParametro("opcaoVerba", opcaoVerba);
            dto.adicionarParametro("agrupadoPorOrgao", agrupadoPorOrgao);
            dto.adicionarParametro("temGrupoRecursoFPMarcado", temGrupoRecursoFPMarcado());
            dto.adicionarParametro("idsGruposMarcados", getIdsGruposMarcados());
            dto.adicionarParametro("assistenteRelatorio", AssistenteRelatorioTotalVerbasPorOrgao.assistenteToDto(novoAssistenteRelatorio()));
            dto.adicionarParametro("VERSAO", getVersao() != null ? getVersao().toString() : "Todas as Versões");
            dto.adicionarParametro("DETALHADO", detalhado);
            dto.adicionarParametro("FILTROS", montarFiltros());
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi(isOpcaoGeral() && naoEhAgrupadoPorOrgao() ? "rh/total-verbas/" : "rh/total-verbas/por-orgao/");
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

    private AssistenteRelatorioTotalVerbasPorOrgao novoAssistenteRelatorio() {
        AssistenteRelatorioTotalVerbasPorOrgao assistente = new AssistenteRelatorioTotalVerbasPorOrgao();
        assistente.setMes(mes - 1);
        assistente.setAno(ano);
        assistente.setTipoFolha(tipoFolhaDePagamento);
        assistente.setDataAtual(new Date());
        assistente.setDetalhado(detalhado);

        if (isEspecificarVerba()) {
            assistente.setEventoFPSelecionado(eventoFPSelecionado.getCodigo());
        }
        if (hierarquiaOrganizacional != null) {
            assistente.setCodigoHierarquia(hierarquiaOrganizacional.getCodigoSemZerosFinais());
        }
        if (versao != null) {
            assistente.setVersao(versao);
        }
        return assistente;
    }

    private String montarFiltros() {
        filtros = new StringBuilder();
        String separador = "; ";
        String tagAbreNegrito = "<b>";
        String tagFechaNegrito = "</b>";

        filtros.append(tagAbreNegrito).append("Mês: ").append(tagFechaNegrito).append(mes).append(separador);
        filtros.append(tagAbreNegrito).append("Ano: ").append(tagFechaNegrito).append(ano).append(separador);
        filtros.append(tagAbreNegrito).append("Tipo Folha: ").append(tagFechaNegrito).append(tipoFolhaDePagamento.getDescricao()).append(separador);
        filtros.append(tagAbreNegrito).append("Versão: ").append(tagFechaNegrito).append(versao != null ? versao : "Todas").append(separador);
        filtros.append(tagAbreNegrito).append("Opções: ").append(tagFechaNegrito).append(opcao.getDescricao()).append(separador);
        filtros.append(tagAbreNegrito).append("Agrupador: ").append(tagFechaNegrito).append(isAgrupodoPorOrgao() ? "Agrupado por orgão" : "Não Agrupado por orgão").append(separador);
        if (opcaoVerba != null) {
            filtros.append(tagAbreNegrito).append("Opções das Verbas: ").append(tagFechaNegrito).append(opcaoVerba.getDescricao()).append(separador);
        }
        if (verbasSelecionada != null) {
            filtros.append(tagAbreNegrito).append("Verbas: ").append(tagFechaNegrito).append(verbasSelecionada.getDescricao()).append(separador);
        }
        filtros.append(tagAbreNegrito).append("Detalhado: ").append(tagFechaNegrito).append(detalhado ? "Sim" : "Não");
        return filtros.toString();
    }

    private List<Long> getIdsGruposMarcados() {
        List<Long> ids = new ArrayList<>();
        for (GrupoRecursoFP grupo : grupos) {
            if (grupo.getSelecionado()) {
                ids.add(grupo.getId());
            }
        }
        return ids;
    }

    private void podeGerar() {
        ValidacaoException ve = new ValidacaoException();
        if (!temMesInformado()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês deve ser informado.");
        }
        if (!isMesValido()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Mês deve ser entre 1 e 12.");
        }
        if (!temAnoInformado()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano deve ser informado.");
        }
        if (!temTipoFolhaInformado()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Folha deve ser informado.");
        }
        if (!temOpcaoInformada()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Opções deve ser informado.");
        }
        if (!validarAgrupadoPor()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe se o relatório será agrupado por Órgão ou não.");
        }
        if (!validarHierarquiaOrganizacional()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Órgão deve ser informado.");
        }
        if (relatorioVerbasEntraramESairamFP && opcaoVerba == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Opções das verbas deve ser informado.");
        }
        lancarValidacaoExeption(ve);
    }

    private boolean validarAgrupadoPor() {
        if (isOpcaoGeral()) {
            if (agrupadoPorOrgao == null || agrupadoPorOrgao.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean isMesValido() {
        if (temMesInformado()) {
            if (mes.compareTo(getValorMesJaneiro()) < 0 || mes.compareTo(getValorMesDezembro()) > 0) {
                return false;
            }
        }
        return true;
    }

    public Integer getValorMesJaneiro() {
        return 1;
    }

    public Integer getValorMesDezembro() {
        return 12;
    }

    private boolean validarHierarquiaOrganizacional() {
        if (isOpcaoSecretaria()) {
            if (!temHierarquiaOrganizacionalInformada()) {
                return false;
            }
        }
        return true;
    }

    private boolean temOpcaoInformada() {
        return opcao != null;
    }

    private boolean temTipoFolhaInformado() {
        return tipoFolhaDePagamento != null;
    }

    private boolean temVersaoFolhaInformada() {
        return versao != null;
    }

    private boolean temAnoInformado() {
        return ano != null;
    }

    private boolean temMesInformado() {
        return mes != null;
    }

    private boolean temHierarquiaOrganizacionalInformada() {
        return hierarquiaOrganizacional != null;
    }

    private boolean temGrupoRecursoFPMarcado() {
        for (GrupoRecursoFP grupo : grupos) {
            if (grupo.getSelecionado()) {
                return true;
            }
        }
        return false;
    }

    public String getNomeRelatorio() {
        return isOpcaoGeral() && naoEhAgrupadoPorOrgao() ? "RELATÓRIO TOTAL DE VERBAS GERAL" : "RELATÓRIO TOTAL DE VERBAS POR ÓRGÃO";
    }

    public void anularHierarquiaOrganizacional() {
        hierarquiaOrganizacional = null;
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

    public OpcaoGeracaoRelatorio getOpcao() {
        return opcao;
    }

    public void setOpcao(OpcaoGeracaoRelatorio opcao) {
        this.opcao = opcao;
    }

    public String getAgrupadoPorOrgao() {
        return agrupadoPorOrgao;
    }

    public void setAgrupadoPorOrgao(String agrupadoPorOrgao) {
        this.agrupadoPorOrgao = agrupadoPorOrgao;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public List<GrupoRecursoFP> getGrupos() {
        return grupos;
    }

    public void setGrupos(List<GrupoRecursoFP> grupos) {
        this.grupos = grupos;
    }

    public void cancelarAgrupadorPorOrgaoAndHierarquiaOrganizacionalAndGrupos() {
        agrupadoPorOrgao = null;
        setHierarquiaOrganizacional(null);
        instanciarListaDeGrupos();
    }

    private Date getData() {
        if (getMes() != null && getAno() != null) {
            return DataUtil.montaData(1, getMes(), getAno()).getTime();
        } else {
            return UtilRH.getDataOperacao();
        }
    }

    public void limparHierarquia() {
        setHierarquiaOrganizacional(null);
        grupos = Lists.newArrayList();
    }

    public List<SelectItem> getVersoes() {
        List<SelectItem> retorno = new ArrayList<>();
        if (mes != null && ano != null && tipoFolhaDePagamento != null) {
            retorno.add(new SelectItem(null, "Todas"));
            for (Integer versao : folhaDePagamentoFacade.recuperarVersao(Mes.getMesToInt(mes), ano, tipoFolhaDePagamento)) {
                retorno.add(new SelectItem(versao, String.valueOf(versao)));
            }
        }
        return retorno;
    }

    public List<SelectItem> getOpcoesVerbas() {
        return Util.getListSelectItem(OpcaoVerba.values());
    }

    public List<SelectItem> getVerbas() {
        return Util.getListSelectItemSemCampoVazio(Verbas.values());
    }

    public List<SelectItem> getSelectEventosFP() {
        return Util.getListSelectItemSemCampoVazio(eventosFP.toArray());
    }

    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }

    public Verbas getVerbasSelecionada() {
        return verbasSelecionada;
    }

    public void setVerbasSelecionada(Verbas verbasSelecionada) {
        this.verbasSelecionada = verbasSelecionada;
        if (Verbas.TODAS.equals(verbasSelecionada)) {
            eventoFPSelecionado = null;
        }
    }

    public boolean isEspecificarVerba() {
        return Verbas.ESPECIFICAR.equals(verbasSelecionada);
    }

    public EventoFP getEventoFPSelecionado() {
        return eventoFPSelecionado;
    }

    public void setEventoFPSelecionado(EventoFP eventoFPSelecionado) {
        this.eventoFPSelecionado = eventoFPSelecionado;
    }

    public ConverterGenerico getConverterGenericoEventoFP() {
        return converterGenericoEventoFP;
    }

    public void setConverterGenericoEventoFP(ConverterGenerico converterGenericoEventoFP) {
        this.converterGenericoEventoFP = converterGenericoEventoFP;
    }

    public boolean isDetalhado() {
        return detalhado;
    }

    public void setDetalhado(boolean detalhado) {
        this.detalhado = detalhado;
    }

}
