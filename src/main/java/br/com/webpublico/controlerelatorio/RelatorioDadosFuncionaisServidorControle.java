/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.AverbacaoTempoServicoFacade;
import br.com.webpublico.negocios.ContratoFPFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.relatoriofacade.rh.RelatorioTempoDeServicoFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.ObjetoData;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * @author boy
 */
@ManagedBean(name = "relatorioDadosFuncionaisServidorControle")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorioDadosFuncionaisServidor", pattern = "/rh/relatorio-de-dados-funcionais-do-servidor/", viewId = "/faces/rh/relatorios/relatoriodadosfuncionais.xhtml"),
})
public class RelatorioDadosFuncionaisServidorControle implements Serializable {

    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private AverbacaoTempoServicoFacade averbacaoTempoServicoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private RelatorioTempoDeServicoFacade relatorioTempoDeServicoFacade;
    private ContratoFP contratoSelecionado;

    @URLAction(mappingId = "relatorioDeclaracaoBens", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        contratoSelecionado = null;
    }


    public List<ContratoFP> completarContratos(String parte) {
        return contratoFPFacade.recuperaContratoMatricula(parte.trim());
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (contratoSelecionado == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo contrato deve ser informado.");
        }
        ve.lancarException();
    }

    private String retornarTempoServicoDescontandoFaltasAndPenalidades(ContratoFP cont) {
        try {
            return relatorioTempoDeServicoFacade.buscarDadosRelatorioTempoServico(cont.getId(), false).get(0).getSubreport5().get(0).getAnoMesDiaExtenso();
        } catch (Exception e) {
            Integer totalDias = contratoFPFacade.buscarTempoServicoEmDias(cont).intValue();
            ObjetoData data = contratoFPFacade.getDataFromOracle(totalDias);
            return getAnos(data) + getMeses(data) + getDias(data);
        }
    }


    private String retornarTempoAverbadoVigente(ContratoFP cont) {
        try {
            return DataUtil.totalDeDiasEmAnosMesesDias(relatorioTempoDeServicoFacade.buscarDadosRelatorioTempoServico(cont.getId(), false).get(0).getSubreport5().get(0).getTotalAverbado().intValue());
        } catch (Exception e) {
            Integer totalDias = averbacaoTempoServicoFacade.buscarTotalAverbadoPorContratoFPEmDias(cont);
            ObjetoData objetoData = contratoFPFacade.getDataFromOracle(totalDias);
            return getAnos(objetoData) + getMeses(objetoData) + getDias(objetoData);
        }
    }

    private String retornarTempoContribuicaoVigente(ContratoFP cont) {
        try {
            return DataUtil.totalDeDiasEmAnosMesesDias(relatorioTempoDeServicoFacade.buscarDadosRelatorioTempoServico(cont.getId(), false).get(0).getSubreport5().get(0).getTotalExercicio().intValue());
        } catch (Exception e) {
            Integer totalDias = contratoFPFacade.buscarTempoServicoEmDias(cont).intValue();
            totalDias += averbacaoTempoServicoFacade.buscarTotalAverbadoPorContratoFPEmDias(cont);
            ObjetoData data = contratoFPFacade.getDataFromOracle(totalDias);
            return getAnos(data) + getMeses(data) + getDias(data);
        }
    }

    private String getAnos(ObjetoData data) {
        return (data.getAnos() != null ? data.getAnos() + " ano(s) " : "");
    }

    private String getMeses(ObjetoData data) {
        return (data.getAnos() != null && data.getMeses() != null ? " e " + data.getMeses() + " mes(es) " : data.getMeses() != null ? data.getMeses() + " meses " : "");
    }

    private String getDias(ObjetoData data) {
        return (data.getAnos() == null && data.getMeses() == null && data.getDias() != null ? " " + data.getDias() + " dia(s)" : (data.getDias() != null ? "e " + data.getDias() + " dia(s)" : ""));
    }


    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USER", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("idContrato", contratoSelecionado.getId());
            dto.adicionarParametro("dataOperacao", UtilRH.getDataOperacao());
            dto.adicionarParametro("TMP_CONTRIBUICAO", retornarTempoContribuicaoVigente(contratoSelecionado));
            dto.adicionarParametro("TMP_SERVICO", retornarTempoServicoDescontandoFaltasAndPenalidades(contratoSelecionado));
            dto.adicionarParametro("TMP_AVERBADO", retornarTempoAverbadoVigente(contratoSelecionado));
            dto.setNomeRelatorio("RELATÓRIO-DADOS-FUNCIONAIS-DO-SERVIDOR");
            dto.setApi("rh/dados-funcionais-servidor/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public ContratoFP getContratoSelecionado() {
        return contratoSelecionado;
    }

    public void setContratoSelecionado(ContratoFP contratoSelecionado) {
        this.contratoSelecionado = contratoSelecionado;
    }
}
