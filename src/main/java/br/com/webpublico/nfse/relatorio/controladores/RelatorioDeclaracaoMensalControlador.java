package br.com.webpublico.nfse.relatorio.controladores;

import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoRelatorioApresentacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.nfse.domain.dtos.FiltroRelatorioDeclaracaoMensalDTO;
import br.com.webpublico.nfse.enums.TipoMovimentoMensal;
import br.com.webpublico.nfse.facades.DeclaracaoMensalServicoFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Renato on 18/01/2019.
 */


@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-declaracao-mensal",
        pattern = "/nfse/relatorio/declaracao-mensal/",
        viewId = "/faces/tributario/nfse/relatorio/declaracao-mensal.xhtml"),
})
public class RelatorioDeclaracaoMensalControlador implements Serializable {

    public static final Logger logger = LoggerFactory.getLogger(RelatorioDeclaracaoMensalControlador.class);
    @EJB
    private DeclaracaoMensalServicoFacade declaracaoMensalServicoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private FiltroRelatorioDeclaracaoMensalDTO filtro;

    public RelatorioDeclaracaoMensalControlador() {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    @URLAction(mappingId = "novo-declaracao-mensal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtro = new FiltroRelatorioDeclaracaoMensalDTO();
        filtro.setExercicio(sistemaFacade.getExercicioCorrente());
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            filtro.validarCamposObrigatorios();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setApi("tributario/nfse/declaracao-mensal-servico/" +
                (TipoRelatorioDTO.XLS.equals(dto.getTipoRelatorio()) ? "excel/" : ""));
            dto.setNomeParametroBrasao("BRASAO");
            dto.setNomeRelatorio("Encerramento Mensal de Serviço");
            filtro.montarParametros(dto);
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException op) {
            FacesUtil.printAllFacesMessages(op);
        } catch (Exception ex) {
            logger.error("Erro ao gerar o relatorio de encerramento mensal de servico. Erro {}", ex);
            FacesUtil.addErrorPadrao(ex);
        }
    }

    public List<SelectItem> getMeses() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (Mes mes : Mes.values()) {
            toReturn.add(new SelectItem(mes, mes.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposMovimentoMensal() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        toReturn.add(new SelectItem(TipoMovimentoMensal.NORMAL, TipoMovimentoMensal.NORMAL.getDescricao()));
        toReturn.add(new SelectItem(TipoMovimentoMensal.RETENCAO, TipoMovimentoMensal.RETENCAO.getDescricao()));
        return toReturn;
    }

    public List<SelectItem> getTiposRelatorio() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(TipoRelatorioApresentacao.RESUMIDO, "Consolidado"));
        toReturn.add(new SelectItem(TipoRelatorioApresentacao.DETALHADO, "Analítico"));
        return toReturn;
    }

    public FiltroRelatorioDeclaracaoMensalDTO getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroRelatorioDeclaracaoMensalDTO filtro) {
        this.filtro = filtro;
    }
}
