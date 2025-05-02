/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.EventoContabil;
import br.com.webpublico.enums.TipoEventoContabil;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.EventoContabilFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author juggernaut
 */
@SessionScoped
@ManagedBean
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-evento-contabil", pattern = "/relatorio/evento-contabil/", viewId = "/faces/financeiro/relatorio/relatorioeventocontabil.xhtml")
})
public class RelatorioEventoContabilControlador implements Serializable {

    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private String filtro;
    private List<EventoContabil> eventos;
    private EventoContabil eventoSelecionado;
    private TipoEventoContabil tipoEventoContabil;
    private Date dataReferencia;

    public void gerarRelatorio() {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
            dto.adicionarParametro("complemento", montarClausula());
            dto.adicionarParametro("FILTRO", filtro);
            dto.setNomeRelatorio("Relatório de Evento Contábil");
            dto.setApi("contabil/evento-contabil/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (dataReferencia == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Referência deve ser informado.");
        }
        ve.lancarException();
    }

    public List<EventoContabil> completarEventos(String parte) {
        return eventoContabilFacade.listaFiltrando(parte, "codigo", "descricao");
    }

    @URLAction(mappingId = "relatorio-evento-contabil", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        eventos = Lists.newArrayList();
        eventoSelecionado = null;
        tipoEventoContabil = null;
        dataReferencia = sistemaFacade.getDataOperacao();
    }

    public List<SelectItem> getTiposDeEventoContabil() {
        return Util.getListSelectItem(TipoEventoContabil.values());
    }

    private String montarClausula() {
        StringBuilder stb = new StringBuilder();
        stb.append(" where TO_DATE('").append(DataUtil.getDataFormatada(dataReferencia)).append("', 'DD/MM/YYYY') BETWEEN trunc(EV.INICIOVIGENCIA) AND COALESCE(trunc(EV.FIMVIGENCIA), TO_DATE('").append(DataUtil.getDataFormatada(dataReferencia)).append("', 'DD/MM/YYYY')) ");
        stb.append(" and TO_DATE('").append(DataUtil.getDataFormatada(dataReferencia)).append("', 'DD/MM/YYYY') BETWEEN trunc(clp.INICIOVIGENCIA) AND COALESCE(trunc(clp.FIMVIGENCIA), TO_DATE('").append(DataUtil.getDataFormatada(dataReferencia)).append("', 'DD/MM/YYYY')) ");
        filtro = " Data de Referência: " + DataUtil.getDataFormatada(dataReferencia) + " -";
        if (!eventos.isEmpty()) {
            StringBuilder idEventos = new StringBuilder();
            String evt = "";
            idEventos.append(" ");
            filtro += " Evento(s): ";
            for (EventoContabil evento : eventos) {
                idEventos.append(evt).append(evento.getId());
                filtro += " " + evento.getCodigo() + " -";
                evt = ",";
            }
            stb.append(" AND EV.ID IN (").append(idEventos).append(")");
        }

        if (tipoEventoContabil != null) {
            stb.append(" AND EV.TIPOEVENTOCONTABIL = '").append(tipoEventoContabil.name()).append("'");
            filtro += " Tipo de Evento Contábil: " + this.tipoEventoContabil.getDescricao() + " -";
        }
        filtro = filtro.substring(0, filtro.length() - 1);
        return stb.toString();
    }

    public void adicionar() {
        try {
            validarEvento();
            Util.adicionarObjetoEmLista(eventos, eventoSelecionado);
            eventoSelecionado = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarEvento() {
        ValidacaoException ve = new ValidacaoException();
        if (eventoSelecionado == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Evento Contábil deve ser informado.");
        }
        ve.lancarException();
        if (eventos.contains(eventoSelecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O evento contábil selecionado já está adicionado.");
        }
        ve.lancarException();
    }

    public void removeEvento(EventoContabil ec) {
        eventos.remove(ec);
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public List<EventoContabil> getEventos() {
        return eventos;
    }

    public void setEventos(List<EventoContabil> eventos) {
        this.eventos = eventos;
    }

    public EventoContabil getEventoSelecionado() {
        return eventoSelecionado;
    }

    public void setEventoSelecionado(EventoContabil eventoSelecionado) {
        this.eventoSelecionado = eventoSelecionado;
    }

    public TipoEventoContabil getTipoEventoContabil() {
        return tipoEventoContabil;
    }

    public void setTipoEventoContabil(TipoEventoContabil tipoEventoContabil) {
        this.tipoEventoContabil = tipoEventoContabil;
    }

    public Date getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(Date dataReferencia) {
        this.dataReferencia = dataReferencia;
    }
}
