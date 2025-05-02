package br.com.webpublico.controle.administrativo;

import br.com.webpublico.entidades.Contrato;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ContratoFacade;
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

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioSaldoContrato", pattern = "/relatorio-saldo-contrato/", viewId = "/faces/administrativo/relatorios/relatorio-saldo-contrato.xhtml")
})
public class RelatorioSaldoContratoControlador implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ContratoFacade contratoFacade;
    private Contrato contrato;
    private Boolean analitico;


    @URLAction(mappingId = "novoRelatorioSaldoContrato", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoRelatorio() {
        contrato = null;
        analitico = Boolean.FALSE;
    }

    public void gerarRelatorio() {
        try {
            validarFiltros();
            RelatorioDTO dto = new RelatorioDTO();
            String nomeRelatorio = "RELATÓRIO DE SALDO DE CONTRATO " + getDescricaoAnaliticoSintetico().toUpperCase();
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE " + sistemaFacade.getMunicipio().toUpperCase());
            dto.adicionarParametro("NOMERELATORIO", nomeRelatorio);
            dto.adicionarParametro("MODULO", "ADMINISTRATIVO");
            dto.adicionarParametro("SECRETARIA", contratoFacade.buscarHierarquiaVigenteContrato(contrato, sistemaFacade.getDataOperacao()));
            dto.adicionarParametro("ANALITICO", analitico);
            dto.adicionarParametro("condicao", " where c.id = " + contrato.getId());
            dto.adicionarParametro("FILTROS", "Contrato: " + contrato + "; Tipo Relatório: " + getDescricaoAnaliticoSintetico() + "; ");
            dto.setNomeRelatorio(nomeRelatorio);
            dto.setApi("administrativo/saldo-contrato/");
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

    private String getDescricaoAnaliticoSintetico() {
        return analitico ? "Analítico" : "Sintético";
    }

    private void validarFiltros() {
        ValidacaoException ve = new ValidacaoException();
        if (contrato == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Contrato deve ser informado.");
        }
        ve.lancarException();
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public Boolean getAnalitico() {
        return analitico;
    }

    public void setAnalitico(Boolean analitico) {
        this.analitico = analitico;
    }
}
