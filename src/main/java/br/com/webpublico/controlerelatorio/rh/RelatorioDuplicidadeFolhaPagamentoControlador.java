package br.com.webpublico.controlerelatorio.rh;

import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.EventoFPFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import org.apache.commons.lang.StringUtils;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.List;

/**
 * Created by William on 20/08/2018.
 */
@ViewScoped
@ManagedBean
@URLMapping(id = "relatorio-duplicidade-pagamento", pattern = "/rh/relatorio/duplicidade-pagamento/", viewId = "/faces/rh/relatorios/duplicidade-pagamento.xhtml")
public class RelatorioDuplicidadeFolhaPagamentoControlador extends SuperRelatorioRH {

    @EJB
    private EventoFPFacade eventoFPFacade;
    private EventoFP eventoFP;
    private List<EventoFP> eventos;

    public EventoFP getEventoFP() {
        return eventoFP;
    }

    public void setEventoFP(EventoFP eventoFP) {
        this.eventoFP = eventoFP;
    }

    public List<EventoFP> getEventos() {
        return eventos;
    }

    public void setEventos(List<EventoFP> eventos) {
        this.eventos = eventos;
    }

    private List<String> getCodigoEventos(List<EventoFP> eventos) {
        List<String> codigos = Lists.newArrayList();
        for (EventoFP evento : eventos) {
            codigos.add(evento.getCodigo());
        }
        return codigos;
    }

    private String montarCriterioUtilizado() {
        StringBuilder criterio = new StringBuilder();
        criterio.append("Mês: ").append(getMes().getDescricao()).append("; ");
        criterio.append("Ano: ").append(getAno()).append("; ");
        criterio.append("Tipo de Folha: ").append(getTipoFolhaDePagamento().getDescricao()).append("; ");
        if (getVersao() != null) {
            criterio.append("Versão: ").append(getVersao()).append("; ");
        }
        if (!eventos.isEmpty()) {
            criterio.append("Evento FP: ").append(getDescricaoEventosUtilizados());
        }
        return criterio.toString();
    }

    private String getDescricaoEventosUtilizados() {
        return StringUtils.join(eventos, ",");
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("mes", getMes().getNumeroMesIniciandoEmZero());
            dto.adicionarParametro("ano", getAno());
            dto.adicionarParametro("versao", getVersao());
            dto.adicionarParametro("tipoFolha", getTipoFolhaDePagamento().getToDto());
            dto.adicionarParametro("tipoFolhaPagameto", getTipoFolhaDePagamento().toString());
            dto.adicionarParametro("MODULO", "Recursos Humanos");
            dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO E GESTÃO");
            dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DE DUPLICIDADE DE PAGAMENTO");
            dto.adicionarParametro("FILTROS", montarCriterioUtilizado());
            dto.adicionarParametro("complementoWhere", complementoWhere());
            dto.setNomeRelatorio("RELATÓRIO-DE-DUPLICIDADE-DE-PAGAMENTO");
            dto.setApi("rh/duplicidade-folha-pagamento/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException val) {
            FacesUtil.printAllFacesMessages(val.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (getMes() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês deve ser informado.");
        }
        if (getAno() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano deve ser informado.");
        }
        if (getTipoFolhaDePagamento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Folha deve ser informado.");
        }
        ve.lancarException();
    }

    @Override
    @URLAction(mappingId = "relatorio-duplicidade-pagamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        setMes(null);
        setAno(null);
        setTipoFolhaDePagamento(null);
        eventoFP = null;
        eventos = Lists.newArrayList();
    }

    public List<SelectItem> getEventoFpSelect() {
        return Util.getListSelectItem(eventoFPFacade.listaEventosAtivosFolha());
    }

    public void adicionarEventoFP() {
        try {
            validarEventoFP();
            eventos.add(eventoFP);
            eventoFP = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarEventoFP() {
        ValidacaoException ve = new ValidacaoException();
        if (eventos.contains(eventoFP)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Esse evento já foi adicionado.");
        }
        ve.lancarException();
    }

    public void removerEventoFP(EventoFP evento) {
        eventos.remove(evento);
    }

    private String complementoWhere() {
        if (!eventos.isEmpty()) {
            return " and e.codigo in (" + StringUtils.join(getCodigoEventos(eventos), ",") + ") ";
        }
        return "";
    }
}
