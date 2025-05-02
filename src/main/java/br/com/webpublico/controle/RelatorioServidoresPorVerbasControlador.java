package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidadesauxiliares.FiltroRelatorioServidoresPorVerbas;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.EventoFPFacade;
import br.com.webpublico.negocios.RelatorioServidoresPorVerbasFacade;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.*;

@ManagedBean(name = "relatorioServidoresPorVerbasControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-servidores-por-verbas", pattern = "/relatorio/servidores-por-verbas/", viewId = "/faces/rh/relatorios/relatorioservidoresporverbas.xhtml")
})
public class RelatorioServidoresPorVerbasControlador extends AbstractReport {

    protected static final Logger logger = LoggerFactory.getLogger(RelatorioServidoresPorVerbasControlador.class);
    @EJB
    private RelatorioServidoresPorVerbasFacade facade;
    @EJB
    private EventoFPFacade eventoFPFacade;
    private FiltroRelatorioServidoresPorVerbas filtroRelatorio;
    private boolean todasVerbas;

    public RelatorioServidoresPorVerbasControlador() {
        geraNoDialog = Boolean.TRUE;
    }

    @URLAction(mappingId = "relatorio-servidores-por-verbas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        filtroRelatorio = new FiltroRelatorioServidoresPorVerbas();
        filtroRelatorio.limparCampos();
        todasVerbas = false;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            filtroRelatorio.validarCampos();
            verificarSeHaVerbasAdicionadas();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setApi("rh/servidores-por-verbas/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public RelatorioDTO montarRelatorioDTO() {
        RelatorioDTO dto = new RelatorioDTO();

        String filtros = "Órgão: " + filtroRelatorio.getHierarquiaAdministrativa().getCodigoNo() + " " + (filtroRelatorio.getHierarquiaAdministrativa().getSuperior() != null ? filtroRelatorio.getHierarquiaAdministrativa().getSuperior().getDescricao() : filtroRelatorio.getHierarquiaAdministrativa().getDescricao())
            + ", Verba(s): " + getDescricaoVerbas() + ", Folha: " + Mes.getMesToInt(filtroRelatorio.getMes()).getDescricao() + " de " + filtroRelatorio.getAno() + ", Tipo: " + filtroRelatorio.getTipoFolhaDePagamento().getDescricao() + ", Versão: " + (filtroRelatorio.getVersao() != null ? String.valueOf(filtroRelatorio.getVersao()) : "Todas") + ", Desvincular Verbas: " + (filtroRelatorio.getDesvincularVerbas() ? "Sim" : "Não");

        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("MUNICIPIO", montarNomeDoMunicipio());
        dto.setNomeParametroSubreportDir("SUBREPORT_DIR");
        dto.setNomeRelatorio("SERVIDORES POR VERBA - MENSAL");
        dto.adicionarParametro("NOMERELATORIO", dto.getNomeRelatorio());
        adicionarParametroDescricaoOrgao(dto);
        dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.adicionarParametro("idsVerbaSelecionados", facade.getIdsVerba(filtroRelatorio.getVerbas()));
        dto.adicionarParametro("complementoQuery", facade.getFiltrosSqlComuns(filtroRelatorio));
        dto.adicionarParametro("VERSAO", filtroRelatorio.getVersao() != null ? String.valueOf(filtroRelatorio.getVersao()) : "Todas");
        dto.adicionarParametro("MODULO", "Recursos Humanos");
        dto.adicionarParametro("DESCRICAOMESANO", Mes.getMesToInt(filtroRelatorio.getMes()).getDescricao().toUpperCase() + " DE " + filtroRelatorio.getAno());
        dto.adicionarParametro("VERBAS", getDescricaoVerbas());
        dto.adicionarParametro("TIPOFOLHA", "FOLHA " + filtroRelatorio.getTipoFolhaDePagamento().getDescricao().toUpperCase());
        dto.adicionarParametro("ORGAO", filtroRelatorio.getHierarquiaAdministrativa().getDescricao());
        dto.adicionarParametro("DATA_POR_EXTENSO", (DataUtil.recuperarDataPorExtenso(getSistemaFacade().getDataOperacao())));
        dto.adicionarParametro("FILTRO_TXT_EXCEL", filtros);
        dto.adicionarParametro("DESVINCULAR_VERBAS", filtroRelatorio.getDesvincularVerbas());

        return dto;
    }

    private String getDescricaoVerbas() {
        String descricao = "";
        if (todasVerbas) {
            return "TODAS AS VANTAGENS E DESCONTOS";
        }
        for (EventoFP eventoFP : filtroRelatorio.getVerbas()) {
            descricao += eventoFP.getCodigo() + " - " + eventoFP.getDescricao() + ", ";
        }
        descricao = descricao.substring(0, descricao.length() - 2);
        return descricao;
    }

    private void adicionarParametroDescricaoOrgao(RelatorioDTO dto) {
        try {
            dto.adicionarParametro("DESCRICAOORGAO", filtroRelatorio.getHierarquiaAdministrativa().getCodigoNo() + " " + filtroRelatorio.getHierarquiaAdministrativa().getSuperior().getDescricao());
        } catch (NullPointerException npe) {
            dto.adicionarParametro("DESCRICAOORGAO", filtroRelatorio.getHierarquiaAdministrativa().getCodigoNo() + " " + filtroRelatorio.getHierarquiaAdministrativa().getSubordinada());
        }
    }

    public void removerVerba(EventoFP eventoFP) {
        filtroRelatorio.getVerbas().remove(eventoFP);
    }

    public void verificarSeHaVerbasAdicionadas() {
        if (filtroRelatorio.getVerbas().isEmpty()) {
            filtroRelatorio.setVerbas(eventoFPFacade.listaEventosAtivosFolha());
            ordernarVerbas();
            todasVerbas = true;
        }
    }

    public void adicionarVerba() {
        try {
            if (todasVerbas) {
                filtroRelatorio.setVerbas(Lists.newArrayList());
                todasVerbas = false;
            }
            validarAdicionarVerba();
            filtroRelatorio.getVerbas().add(filtroRelatorio.getVerba());
            filtroRelatorio.setVerba(null);
            ordernarVerbas();
            FacesUtil.executaJavaScript("setaFoco('Formulario:tab-view-geral:verba_input')");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void ordernarVerbas() {
        Collections.sort(filtroRelatorio.getVerbas(), new Comparator<EventoFP>() {
            @Override
            public int compare(EventoFP o1, EventoFP o2) {
                if (o1.getCodigo() == null || o2.getCodigo() == null) {
                    return 0;
                }
                Long codigo1 = Long.parseLong(o1.getCodigo());
                Long codigo2 = Long.parseLong(o2.getCodigo());
                return codigo1.compareTo(codigo2);
            }
        });
    }

    private void validarAdicionarVerba() {
        ValidacaoException ve = new ValidacaoException();
        if (filtroRelatorio.getVerba() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo verba deve ser infomado.");
        } else {
            for (EventoFP verba : filtroRelatorio.getVerbas()) {
                if (verba.equals(this.filtroRelatorio.getVerba())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A verba " + filtroRelatorio.getVerba() + " já foi adicionada na lista.");
                }
            }
        }
        ve.lancarException();
    }

    public List<SelectItem> getVersoes() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, "Todas"));
        if (filtroRelatorio.getMes() != null && filtroRelatorio.getAno() != null && filtroRelatorio.getTipoFolhaDePagamento() != null) {
            for (Integer versao : facade.getFolhaDePagamentoFacade().recuperarVersao(Mes.getMesToInt(filtroRelatorio.getMes()), filtroRelatorio.getAno(), filtroRelatorio.getTipoFolhaDePagamento())) {
                retorno.add(new SelectItem(versao, String.valueOf(versao)));
            }
        }
        return retorno;
    }

    public List<SelectItem> getTipoFolhasPagamento() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (Object obj : TipoFolhaDePagamento.values()) {
            toReturn.add(new SelectItem(obj, obj.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposEventofp() {
        return Util.getListSelectItem(TipoEventoFP.values());
    }


    public List<HierarquiaOrganizacional> completarHierarquia(String parte) {
        return facade.getHierarquiaOrganizacionalFacade().filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), getDataVigencia());
    }

    public List<EventoFP> completaEventoFP(String parte) {
        return facade.getEventoFPFacade().listaFiltrandoEventosAtivos(parte.trim());
    }

    public void limparHierarquiaSelecionada() {
        filtroRelatorio.setHierarquiaAdministrativa(null);
    }

    private Date getDataVigencia() {
        if (filtroRelatorio.getMes() != null && filtroRelatorio.getAno() != null) {
            return DataUtil.criarDataComMesEAno(filtroRelatorio.getMes(), filtroRelatorio.getAno()).toDate();
        } else {
            return UtilRH.getDataOperacao();
        }
    }

    public FiltroRelatorioServidoresPorVerbas getFiltroRelatorio() {
        return filtroRelatorio;
    }

    public void setFiltroRelatorio(FiltroRelatorioServidoresPorVerbas filtroRelatorio) {
        this.filtroRelatorio = filtroRelatorio;
    }
}
