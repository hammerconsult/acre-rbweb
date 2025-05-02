package br.com.webpublico.controlerelatorio.rh;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-assentamento-funcional", pattern = "/relatorio/assentamento-funcional/", viewId = "/faces/rh/relatorios/relatorio-assentamento-funcional.xhtml")
})
public class RelatorioAssentamentoFuncionalControlador implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(RelatorioAssentamentoFuncionalControlador.class);

    @EJB
    private SistemaFacade sistemaFacade;
    private ContratoFP selecionado;

    @URLAction(mappingId = "relatorio-assentamento-funcional", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        selecionado = null;
    }

    private void validar() {
        ValidacaoException validacaoException = new ValidacaoException();
        if (selecionado == null) {
            validacaoException.adicionarMensagemDeCampoObrigatorio("Favor informar o servidor.");
        }
        validacaoException.lancarException();
    }

    public void gerarRelatorio() {
        try {
            validar();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setNomeParametroBrasao("BRASAO");
            dto.setNomeRelatorio("RELATÓRIO-DE-ASSENTAMENTOS-FUNCIONAL");
            dto.adicionarParametro("MODULO", "Recursos Humanos");
            dto.adicionarParametro("SECRETARIA", "DEPARTAMENTO DE RECURSOS HUMANOS");
            dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DE ASSENTAMENTOS FUNCIONAL");
            dto.adicionarParametro("DATAOPERACAO", UtilRH.getDataOperacao());
            dto.adicionarParametro("ID_CONTRATO", selecionado.getId());
            dto.adicionarParametro("SERVIDOR", selecionado.getMatriculaFP().getPessoa().getNome() + " - Matricula:" + selecionado.getMatriculaFP().getMatricula() + " - " + selecionado.getUnidadeOrganizacional().getDescricao());
            dto.setApi("rh/relatorio-assentamento-funcional/");
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
            logger.error("Erro ao gerar relatório: " + e);
        }
    }

    public ContratoFP getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(ContratoFP selecionado) {
        this.selecionado = selecionado;
    }
}
