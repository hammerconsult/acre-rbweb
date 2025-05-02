/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.entidadesauxiliares.FiltroRelatorioAdministrativo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.base.Strings;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Felipe Marinzeck
 */
@ViewScoped
@ManagedBean
@URLMappings(mappings = {
    @URLMapping(id = "novo-relatorio-solicitacao", pattern = "" +
        "/licitacao/relatorio-solicitacao/", viewId = "/faces/administrativo/relatorios/relatoriosolicitacao.xhtml")
})
public class SolicitacaoCompraRelatorioControlador implements Serializable {

    private FiltroRelatorioAdministrativo filtroRelatorio;
    @EJB
    private SistemaFacade sistemaFacade;

    @URLAction(mappingId = "novo-relatorio-solicitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        novoFiltroRelatorio();
    }

    public void novoFiltroRelatorio() {
        filtroRelatorio = new FiltroRelatorioAdministrativo();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            UsuarioSistema usuarioCorrente = sistemaFacade.getUsuarioCorrente();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setNomeRelatorio("Solicitação de Compra");
            dto.adicionarParametro("MODULO", "Administrativo");
            dto.adicionarParametro("DATA_OPERACAO", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
            dto.adicionarParametro("CONDICAO", montarCondicao(filtroRelatorio));
            dto.adicionarParametro("FILTROS", filtroRelatorio.getFiltros());
            dto.adicionarParametro("NOMERELATORIO", dto.getNomeRelatorio());
            dto.adicionarParametro("USUARIO", usuarioCorrente.getNome(), false);
            dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE DESENVOLVIMENTO ECONÔMICO E FINANÇAS");
            dto.setApi("administrativo/solicitacao-compra/");
            ReportService.getInstance().gerarRelatorio(usuarioCorrente, dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public static String montarCondicao(FiltroRelatorioAdministrativo filtroRelatorio) {
        StringBuilder sb = new StringBuilder();
        String complemento = " and ";
        String filtros = " ";
        if (filtroRelatorio.getIdObjeto() != null) {
            sb.append(complemento).append(" sm.id = ").append(filtroRelatorio.getIdObjeto());
        } else {
            if (filtroRelatorio.getNumero() != null) {
                sb.append(complemento).append(" sm.numero = ").append(filtroRelatorio.getNumero());
                filtros += " Número Solicitação: " + filtroRelatorio.getNumero();
            }
            Date dataInicial = filtroRelatorio.getDataInicial();
            Date dataFinal = filtroRelatorio.getDataFinal();
            if (dataInicial != null && dataFinal != null) {
                sb.append(complemento).append(" trunc(sm.datasolicitacao) between to_date('" + DataUtil.getDataFormatada(dataInicial) + "', 'dd/mm/yyyy') and to_date('" + DataUtil.getDataFormatada(dataFinal) + "', 'dd/mm/yyyy')");
                filtros += " Período: " + DataUtil.getDataFormatada(dataInicial) + " à " + DataUtil.getDataFormatada(dataFinal);
            }
            if (filtroRelatorio.getTipoSolicitacao() != null) {
                sb.append(complemento).append(" sm.tiposolicitacao = '" + filtroRelatorio.getTipoSolicitacao().name() + "'");
                filtros += " Tipo Solicitação: " + filtroRelatorio.getTipoSolicitacao().getDescricao();
            }
            if (filtroRelatorio.getHierarquiaAdministrativa() != null) {
                sb.append(complemento).append(" vw.subordinada_id = '" + filtroRelatorio.getHierarquiaAdministrativa().getSubordinada().getId() + "'");
                filtros += " Unidade Administrativa: " + filtroRelatorio.getHierarquiaAdministrativa().getDescricao();
            }
            if (filtroRelatorio.getUnidadeGestora() != null) {
                sb.append(complemento).append(" sm.unidadegestora_id = '" + filtroRelatorio.getUnidadeGestora().getId() + "'");
                filtros += " Unidade Gestora: " + filtroRelatorio.getUnidadeGestora().getDescricao();
            }
            if (!Strings.isNullOrEmpty(filtroRelatorio.getDescricao())) {
                sb.append(complemento).append(" lower(sm.descricao) like lower('%").append(filtroRelatorio.getDescricao()).append("%') ");
                filtros += " Descrição: " + filtroRelatorio.getDescricao();
            }
        }
        filtroRelatorio.setFiltros(filtros);
        return sb.toString();
    }

    public FiltroRelatorioAdministrativo getFiltroRelatorio() {
        return filtroRelatorio;
    }

    public void setFiltroRelatorio(FiltroRelatorioAdministrativo filtroRelatorio) {
        this.filtroRelatorio = filtroRelatorio;
    }
}
