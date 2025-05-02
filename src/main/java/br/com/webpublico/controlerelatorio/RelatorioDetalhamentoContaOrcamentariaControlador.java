package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ContaFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
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
    @URLMapping(id = "relatorio-detalhamento-conta-orcamentaria", pattern = "/relatorio/detalhamento-conta-orcamentaria/", viewId = "/faces/financeiro/relatorio/relatorio-detalhamento-conta-orcamentaria.xhtml")
})
@ManagedBean
public class RelatorioDetalhamentoContaOrcamentariaControlador implements Serializable {

    @EJB
    private ContaFacade contaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private String filtro;
    private Conta conta;

    @URLAction(mappingId = "relatorio-detalhamento-conta-orcamentaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        conta = null;
    }

    public void gerarRelatorio() {
        try {
            filtro = "";
            RelatorioDTO dto = new RelatorioDTO();
            dto.setApi("contabil/detalhamento-conta-orcamentaria/");
            dto.setNomeRelatorio("Relatório de Detalhamento das Contas Orçamentárias");
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC ");
            dto.adicionarParametro("USER", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.adicionarParametro("CLAUSULA", montarParametros());
            dto.adicionarParametro("FILTRO", filtro);
            dto.adicionarParametro("EXERCICIO", sistemaFacade.getExercicioCorrente().getAno().toString());
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    private String montarParametros() {
        String clausula = "";
        clausula += " where c.exercicio_id = " + sistemaFacade.getExercicioCorrente().getId();
        if (conta != null) {
            clausula += " and c.codigo like '" + conta.getCodigoSemZerosAoFinal() + "%'";
            filtro += " Conta: " + conta.getCodigo();
        }
        return clausula;
    }

    public List<Conta> completarContas(String filtro) {
        return contaFacade.buscarContasDespesaPorNivelNivel(filtro, 4, sistemaFacade.getExercicioCorrente());
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }
}
