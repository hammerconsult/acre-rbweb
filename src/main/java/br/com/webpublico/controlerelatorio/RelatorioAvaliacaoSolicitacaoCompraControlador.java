/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidadesauxiliares.FiltroRelatorioAdministrativo;
import br.com.webpublico.enums.TipoStatusSolicitacao;
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
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Felipe Marinzeck
 */
@ViewScoped
@ManagedBean

@URLMappings(mappings = {
    @URLMapping(id = "novo-relatorio-avaliacao-solicitacao", pattern = "/licitacao/relatorio-avaliacao-solicitacao/", viewId = "/faces/administrativo/relatorios/relatorioavaliacaosolicitacaocompra.xhtml")
})
public class RelatorioAvaliacaoSolicitacaoCompraControlador implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;
    private FiltroRelatorioAdministrativo filtroRelatorio;

    @URLAction(mappingId = "novo-relatorio-avaliacao-solicitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        novoFiltro();
    }

    private void novoFiltro() {
        filtroRelatorio = new FiltroRelatorioAdministrativo();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.adicionarParametro("NOME_RELATORIO", "Relatório de Avaliação da Solicitação de Compra");
            dto.adicionarParametro("MODULO", "Administrativo");
            dto.adicionarParametro("DATA_OPERACAO", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), true);
            dto.adicionarParametro("WHERE", montarCondicao(filtroRelatorio));
            dto.adicionarParametro("FILTROS", filtroRelatorio.getFiltros());
            dto.setNomeRelatorio("Relatório de Avaliação da Solicitação de Compra");
            dto.setApi("administrativo/avaliacao-solicitacao/");
            dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE DESENVOLVIMENTO ECONÔMICO E FINANÇAS");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
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
        String filtros = "";

        if (filtroRelatorio.getIdObjeto() != null) {
            sb.append(complemento).append(" ava.id = ").append(filtroRelatorio.getIdObjeto());
            return sb.toString();
        }
        if (filtroRelatorio.getDataInicial() != null && filtroRelatorio.getDataFinal() != null) {
            sb.append(complemento).append(" trunc(ava.dataavaliacao) between to_date('").append(DataUtil.getDataFormatada(filtroRelatorio.getDataInicial())).append("', 'dd/mm/yyyy') and to_date('").append(DataUtil.getDataFormatada(filtroRelatorio.getDataFinal())).append("', 'dd/mm/yyyy')");
            filtros += " Período de Avaliação: " + DataUtil.getDataFormatada(filtroRelatorio.getDataInicial()) + " à " + DataUtil.getDataFormatada(filtroRelatorio.getDataFinal()) + " ";
            complemento = " and ";
        }
        if (filtroRelatorio.getModalidadeLicitacao() != null) {
            sb.append(complemento).append(" sm.modalidadelicitacao = '").append(filtroRelatorio.getModalidadeLicitacao().name()).append("'");
            filtros += "Modalidade: " + filtroRelatorio.getModalidadeLicitacao().getDescricao() + " ";
            complemento = " and ";
        }
        if (filtroRelatorio.getStatusSolicitacao() != null) {
            sb.append(complemento).append(" ava.tipostatussolicitacao = '").append(filtroRelatorio.getStatusSolicitacao().name()).append("'");
            filtros += "Status: " + filtroRelatorio.getStatusSolicitacao().getDescricao() + " ";
            complemento = " and ";
        }
        if (filtroRelatorio.getPessoa() != null) {
            sb.append(complemento).append(" pf.nome = '").append(filtroRelatorio.getPessoa().getNome()).append("'");
            filtros += "Solicitante: " + filtroRelatorio.getPessoa().getNome() + " ";
            complemento = " and ";
        }
        if (!Strings.isNullOrEmpty(filtroRelatorio.getDescricao())) {
            sb.append(complemento).append(" lower(sm.descricao) like lower('%").append(filtroRelatorio.getDescricao()).append("%') ");
            filtros += "Descrição: " + filtroRelatorio.getDescricao() + " ";
        }
        filtroRelatorio.setFiltros(filtros);
        return sb.toString();
    }

    public List<SelectItem> getTipoStatusSolicitacao() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (TipoStatusSolicitacao status : TipoStatusSolicitacao.values()) {
            retorno.add(new SelectItem(status, status.getDescricao()));
        }
        return retorno;
    }

    public FiltroRelatorioAdministrativo getFiltroRelatorio() {
        return filtroRelatorio;
    }

    public void setFiltroRelatorio(FiltroRelatorioAdministrativo filtroRelatorio) {
        this.filtroRelatorio = filtroRelatorio;
    }
}
