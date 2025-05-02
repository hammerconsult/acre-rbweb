package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.ProcessoCalculoGeralIssFixo;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.CalculaIssFixoGeralFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
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


@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorioProcessoCalc", pattern = "/relatorio-processo-calculo-lancamento-geral-iss-fixo/",
        viewId = "/faces/tributario/issqn/calculogeralissfixo/relatorio/relatorio.xhtml")
})
@ManagedBean
public class RelatorioLancamentoIssFixoGeralControlador implements Serializable {

    @EJB
    private CalculaIssFixoGeralFacade calculaIssFixoGeralFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private boolean relatorioAgrupado;
    private ProcessoCalculoGeralIssFixo processoCalculoGeralIssFixoRelatorio;


    public RelatorioLancamentoIssFixoGeralControlador() {

    }

    @URLAction(mappingId = "relatorioProcessoCalc", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        processoCalculoGeralIssFixoRelatorio = new ProcessoCalculoGeralIssFixo();
        relatorioAgrupado = false;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            if(!hasAlgumProcessoParaImprimir()) {
                mostrarMensagemSemRegistro();
            }
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setApi("tributario/calculo-iss-fixo-geral/");
            dto.setNomeRelatorio("RELATÓRIO DE PROCESSO DE CÁLCULO DE LANÇAMENTO GERAL DE ISS FIXO");
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("CONDICAO", montarCondicao());
            dto.adicionarParametro("RELATORIOAGRUPADO", relatorioAgrupado);
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome());
            dto.adicionarParametro("NOMERELATORIO", getNomeRelatorio());
            dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE FINANÇAS");
            dto.adicionarParametro("MODULO", "Tributário");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (
            WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    public List<ProcessoCalculoGeralIssFixo> completarProcessoCalculoGeralFixo(String parte) {
        return calculaIssFixoGeralFacade.listaFiltrando(parte.trim(), "descricao", "exercicio.ano", "cmcInicial", "cmcFinal", "dataDoLancamento", "situacaoProcesso", "usuarioSistema");
    }

    public String getNomeRelatorio() {
        return "RELATÓRIO DE PROCESSO DE CÁLCULO DE LANÇAMENTO GERAL DE ISS FIXO";
    }

    public String montarCondicao() {
        String where = "";
        if (processoCalculoGeralIssFixoRelatorio != null && processoCalculoGeralIssFixoRelatorio.getId() != null) {
            where = " where processo_geral.id = " + processoCalculoGeralIssFixoRelatorio.getId();
        }
        return where;
    }

    public ProcessoCalculoGeralIssFixo getProcessoCalculoGeralIssFixoRelatorio() {
        return processoCalculoGeralIssFixoRelatorio;
    }

    public void setProcessoCalculoGeralIssFixoRelatorio(ProcessoCalculoGeralIssFixo processoCalculoGeralIssFixoRelatorio) {
        this.processoCalculoGeralIssFixoRelatorio = processoCalculoGeralIssFixoRelatorio;
    }

    public boolean hasAlgumProcessoParaImprimir() {
        return calculaIssFixoGeralFacade.temAlgumProcesso();
    }

    public void mostrarMensagemSemRegistro() {
        FacesUtil.addError(SummaryMessages.ATENCAO.getDescricao(), "Nenhum processo de cálculo encontrado!");
    }

    public boolean getRelatorioAgrupado() {
        return relatorioAgrupado;
    }

    public void setRelatorioAgrupado(boolean relatorioAgrupado) {
        this.relatorioAgrupado = relatorioAgrupado;
    }

}
