/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio.rh;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.GrupoRecursoFP;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.ItemConfiguracaoEmpregadorESocial;
import br.com.webpublico.enums.CategoriaDeclaracaoPrestacaoContas;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.rh.esocial.TipoApuracaoFolha;
import br.com.webpublico.enums.rh.esocial.TipoClasseESocial;
import br.com.webpublico.esocial.enums.SituacaoESocial;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.EntidadeFacade;
import br.com.webpublico.negocios.GrupoRecursoFPFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.rh.esocial.ConfiguracaoEmpregadorESocialFacade;
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

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean(name = "relatorioEnviadosESocialControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorioEnviadosESocial", pattern = "/relatorio-enviados-esocial/", viewId = "/faces/rh/relatorios/relatorioenviadosesocial.xhtml")
})
public class RelatorioEnviadosESocialControlador extends AbstractReport implements Serializable {

    @EJB
    private ConfiguracaoEmpregadorESocialFacade configuracaoEmpregadorESocialFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private GrupoRecursoFPFacade grupoRecursoFPFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    private Exercicio exercicio;
    private Mes mes;
    private TipoClasseESocial eventoESocial;
    private ConfiguracaoEmpregadorESocial empregador;
    private SituacaoESocial situacaoESocial;
    private String tipoVinculo;
    private String filtros;
    private TipoApuracaoFolha tipoApuracaoFolha;
    private List<HierarquiaOrganizacional> hierarquiasOrganizacionais;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private List<GrupoRecursoFP> grupoRecursoFPs;
    private GrupoRecursoFP[] recursosSelecionados;
    private Boolean agrupado = true;


    public RelatorioEnviadosESocialControlador() {
    }


    @URLAction(mappingId = "relatorioEnviadosESocial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        exercicio = null;
        mes = null;
        tipoApuracaoFolha = null;
        eventoESocial = null;
        empregador = null;
        situacaoESocial = null;
        tipoVinculo = null;
        filtros = null;
        hierarquiasOrganizacionais = Lists.newArrayList();
        hierarquiaOrganizacional = null;
        agrupado = true;
        grupoRecursoFPs = Lists.newArrayList();
        recursosSelecionados = new GrupoRecursoFP[]{};
    }


    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();

        if (empregador == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Empregador deve ser informado.");
        }
        if (eventoESocial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Evento Esocial deve ser informado.");
        }
        if (exercicio == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano deve ser informado.");
        }
        if (mes == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mes deve ser informado.");
        }
        if (tipoApuracaoFolha == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Apuração deve ser informado.");
        }
        ve.lancarException();
    }

    public GrupoRecursoFP[] getRecursosSelecionados() {
        return recursosSelecionados;
    }

    public void setRecursosSelecionados(GrupoRecursoFP[] recursosSelecionados) {
        this.recursosSelecionados = recursosSelecionados;
    }

    public Boolean getAgrupado() {
        return agrupado != null ? agrupado : true;
    }

    public void setAgrupado(Boolean agrupado) {
        this.agrupado = agrupado;
    }

    public void montarFiltrosUtilizados() {
        filtros = "";
        filtros += "Ano: " + exercicio.getAno().toString() + "; ";
        filtros += "Mês: " + mes.getDescricao() + "; ";
        filtros += Util.isNotNull(tipoApuracaoFolha) ? "Apuração: " + tipoApuracaoFolha.getDescricao() + "; " : "Apuração: Mensal; ";
        filtros += Util.isNotNull(situacaoESocial) ? "Status: " + situacaoESocial.getDescricao() + "; " : "Status: Todos; ";
        filtros += "Evento: " + eventoESocial.getCodigo() + " - " + eventoESocial.getDescricao() + "; ";
        filtros += Util.isNotNull(tipoVinculo) ? "Tipo de Vinculo: " + tipoVinculo + "; " : "Tipo de Vinculo: Todos; ";
        filtros += Util.isNotNull(hierarquiaOrganizacional) ? "Órgão: " + hierarquiaOrganizacional + "; " : "";
        filtros += Util.isNotNull(recursosSelecionados) ? "Grupos de RecursoFP: " + Arrays.stream(recursosSelecionados).map(GrupoRecursoFP::getNome).collect(Collectors.joining(", ")) + "; " : "";
    }

    public void carregarGrupoRecursosFPPorEntidade() {
        if (empregador.getEntidade() != null && exercicio.getAno() != null && mes != null) {
            List<GrupoRecursoFP> grupoRecursoFPs = Lists.newLinkedList();
            if (hierarquiaOrganizacional != null) {
                for (GrupoRecursoFP grupoRecursoFP : grupoRecursoFPFacade.buscarGruposRecursoFPPorOrgaoAndDataOperacao(hierarquiaOrganizacional)) {
                    if (!grupoRecursoFPs.contains(grupoRecursoFP)) {
                        grupoRecursoFPs.add(grupoRecursoFP);
                    }
                }
            } else {
                for (HierarquiaOrganizacional hierarquiaOrganizacional : buscarHierarquiasDaEntidade()) {
                    for (GrupoRecursoFP grupoRecursoFP : grupoRecursoFPFacade.buscarGruposRecursoFPPorOrgaoAndDataOperacao(hierarquiaOrganizacional)) {
                        if (!grupoRecursoFPs.contains(grupoRecursoFP)) {
                            grupoRecursoFPs.add(grupoRecursoFP);
                        }
                    }
                }
            }
            setGrupoRecursoFPs(grupoRecursoFPs);
        }
    }

    public List<HierarquiaOrganizacional> buscarHierarquiasDaEntidade() {
        List<HierarquiaOrganizacional> hierarquias = entidadeFacade.buscarHierarquiasDaEntidade(empregador.getEntidade(), CategoriaDeclaracaoPrestacaoContas.SEFIP, DataUtil.primeiroDiaMes(DataUtil.criarDataComMesEAno(mes.getNumeroMes(), exercicio.getAno()).toDate()).getTime(), DataUtil.ultimoDiaMes(DataUtil.criarDataComMesEAno(mes.getNumeroMes(), exercicio.getAno()).toDate()).getTime());
        Collections.sort(hierarquias);
        return hierarquias;
    }

    public void gerarRelatorio(String tipoRelatorio) {
        try {
            validarCampos();
            montarFiltrosUtilizados();
            HierarquiaOrganizacional secretaria = getSistemaFacade().getSistemaService().getHierarquiAdministrativaCorrente();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorio));
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "Recursos Humanos");
            dto.adicionarParametro("SECRETARIA", secretaria.getDescricao().toUpperCase());
            dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DE CONFÊRENCIA ENVIADOS E-SOCIAL");
            dto.adicionarParametro("DATAOPERACAO", DataUtil.getDataFormatada(UtilRH.getDataOperacao()));
            dto.adicionarParametro("MES", mes.getNumeroMesString());
            dto.adicionarParametro("ANO", exercicio.getAno());
            dto.adicionarParametro("TIPOAPURACAOFOLHA", tipoApuracaoFolha != null ? tipoApuracaoFolha.getToDTO() : TipoApuracaoFolha.MENSAL.getToDTO());
            dto.adicionarParametro("STATUS", situacaoESocial != null ? situacaoESocial.name() : null);
            dto.adicionarParametro("EVENTO", eventoESocial.name());
            dto.adicionarParametro("EMPREGADOR", empregador.getId());
            dto.adicionarParametro("EMPREGADORNOME", empregador.getEntidade().getPessoaJuridica().getCnpj() + " - " + empregador.getEntidade().getNome());
            dto.adicionarParametro("AGRUPADO", agrupado);
            dto.adicionarParametro("TIPOVINCULO", tipoVinculo);
            dto.adicionarParametro("FILTROS", filtros);
            Object valor = hierarquiaOrganizacional != null ? Lists.newArrayList(hierarquiaOrganizacional.getSubordinada().getId()) : montarIdOrgaoEmpregador(empregador);
            dto.adicionarParametro("IDS_UNIDADES", valor);
            dto.adicionarParametro("IDS_GRUPORECURSO", (recursosSelecionados != null && recursosSelecionados.length > 0) ? Arrays.stream(recursosSelecionados).map(GrupoRecursoFP::getId).collect(Collectors.toList()) : null);

            dto.adicionarParametro("COMPLEMENTO_WHERE", montarCondicao());
            dto.setNomeRelatorio("RELATÓRIO-DE-CONFÊRENCIA-ENVIADOS-ESOCIAL");
            dto.setApi("rh/conferencia-enviados-esocial/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
            logger.error("Erro ao gerar relatório: {} ", ex);
        }
    }

    public List<Long> montarIdOrgaoEmpregador(ConfiguracaoEmpregadorESocial empregador) {
        List<Long> ids = Lists.newArrayList();
        empregador = configuracaoEmpregadorESocialFacade.recuperar(empregador.getId());
        for (ItemConfiguracaoEmpregadorESocial item : empregador.getItemConfiguracaoEmpregadorESocial()) {
            ids.add(item.getUnidadeOrganizacional().getId());
        }
        return ids;
    }

    public String montarCondicao() {
        if (TipoClasseESocial.S1200.equals(eventoESocial)) {
            String retorno = " and to_date(:dataOperacao, 'MM/yyyy') between trunc(to_date(to_char(prev.iniciovigencia, 'MM/yyyy'), 'MM/yyyy')) and coalesce( " +
                "        trunc(to_date(to_char(prev.FINALVIGENCIA, 'MM/yyyy'), 'MM/yyyy')), to_date(:dataOperacao, 'MM/yyyy')) " +
                "  and tipoPrev.tiporegimeprevidenciario = 'RGPS' ";
            return retorno;
        }
        if (TipoClasseESocial.S1202.equals(eventoESocial)) {
            return " and to_date(:dataOperacao, 'MM/yyyy') between trunc(to_date(to_char(prev.iniciovigencia, 'MM/yyyy'), 'MM/yyyy')) and coalesce( " +
                "        trunc(to_date(to_char(prev.FINALVIGENCIA, 'MM/yyyy'), 'MM/yyyy')), to_date(:dataOperacao, 'MM/yyyy')) " +
                " and tipoPrev.tiporegimeprevidenciario = 'RPPS' " +
                " and not exists(select v.id from vinculofp v " +
                "               where v.id = vinc.id          " +
                "               and (v.finalvigencia is not null and (extract(month from v.finalvigencia) - 1) = :mes) " +
                "               and (v.finalvigencia is not null and extract(year from v.finalvigencia) = :ano))       " +
                " and (categoria.tipo = 'AGENTE_PUBLICO' or categoria.codigo = '410') ";
        }
        return "";
    }

    public List<SelectItem> buscarEmpregadores() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (ConfiguracaoEmpregadorESocial config : configuracaoEmpregadorESocialFacade.lista()) {
            toReturn.add(new SelectItem(config, config.getEntidade() + " "));
        }
        return toReturn;
    }

    public List<SelectItem> buscarEventos() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        toReturn.add(new SelectItem(TipoClasseESocial.S1200, TipoClasseESocial.S1200 + " "));
        toReturn.add(new SelectItem(TipoClasseESocial.S1202, TipoClasseESocial.S1202 + " "));
        toReturn.add(new SelectItem(TipoClasseESocial.S1210, TipoClasseESocial.S1210 + " "));
        return toReturn;
    }

    public static List<SelectItem> buscarSituacoesEsocial() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, "Todos"));
        retorno.add(new SelectItem(SituacaoESocial.PROCESSADO_COM_SUCESSO, SituacaoESocial.PROCESSADO_COM_SUCESSO.getDescricao()));
        retorno.add(new SelectItem(SituacaoESocial.PROCESSADO_COM_ERRO, SituacaoESocial.PROCESSADO_COM_ERRO.getDescricao()));
        retorno.add(new SelectItem(SituacaoESocial.PROCESSADO_COM_ADVERTENCIA, SituacaoESocial.PROCESSADO_COM_ADVERTENCIA.getDescricao()));
        retorno.add(new SelectItem(SituacaoESocial.NAO_ENVIADO, SituacaoESocial.NAO_ENVIADO.getDescricao()));
        return retorno;
    }

    public static List<SelectItem> buscarTipoVinculo() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, "Todos"));
        retorno.add(new SelectItem("SERVIDOR", "Servidor"));
        retorno.add(new SelectItem("PRESTADOR", "Prestador"));
        return retorno;
    }

    public List<SelectItem> getTiposApuracoesFolha() {
        return Util.getListSelectItem(TipoApuracaoFolha.values());
    }

    public List<SelectItem> buscarMes() {
        return Util.getListSelectItem(Mes.values(), false);
    }

    public List<GrupoRecursoFP> getGrupoRecursoFPs() {
        return grupoRecursoFPs;
    }

    public void setGrupoRecursoFPs(List<GrupoRecursoFP> grupoRecursoFPs) {
        this.grupoRecursoFPs = grupoRecursoFPs;
    }

    public void atribuirTipoApuracao() {
        if (TipoClasseESocial.S1210.equals(eventoESocial)) {
            tipoApuracaoFolha = TipoApuracaoFolha.MENSAL;
        }
    }

    public void carregarHierarquiasOrganizacionais() {
        hierarquiasOrganizacionais = Lists.newArrayList();
        hierarquiaOrganizacional = null;
        hierarquiasOrganizacionais = hierarquiaOrganizacionalFacade.buscarHierarquiasDoEmpregadorEsocial(empregador.getEntidade(), UtilRH.getDataOperacao());
        carregarGrupoRecursosFPPorEntidade();
    }

    public List<SelectItem> getHierarquias() {
        return Util.getListSelectItem(hierarquiasOrganizacionais);
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public TipoClasseESocial getEventoESocial() {
        return eventoESocial;
    }

    public void setEventoESocial(TipoClasseESocial eventoESocial) {
        this.eventoESocial = eventoESocial;
    }

    public ConfiguracaoEmpregadorESocial getEmpregador() {
        return empregador;
    }

    public void setEmpregador(ConfiguracaoEmpregadorESocial empregador) {
        this.empregador = empregador;
    }

    public SituacaoESocial getSituacaoESocial() {
        return situacaoESocial;
    }

    public void setSituacaoESocial(SituacaoESocial situacaoESocial) {
        this.situacaoESocial = situacaoESocial;
    }

    public String getTipoVinculo() {
        return tipoVinculo;
    }

    public void setTipoVinculo(String tipoVinculo) {
        this.tipoVinculo = tipoVinculo;
    }

    public String getFiltros() {
        return filtros;
    }

    public void setFiltros(String filtros) {
        this.filtros = filtros;
    }

    public TipoApuracaoFolha getTipoApuracaoFolha() {
        return tipoApuracaoFolha;
    }

    public void setTipoApuracaoFolha(TipoApuracaoFolha tipoApuracaoFolha) {
        this.tipoApuracaoFolha = tipoApuracaoFolha;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }
}
