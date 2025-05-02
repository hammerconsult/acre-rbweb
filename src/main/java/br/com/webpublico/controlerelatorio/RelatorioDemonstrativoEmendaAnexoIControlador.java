package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.enums.ModalidadeEmenda;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-demonstrativo-emenda-anexoI", pattern = "/relatorio-demonstrativo-emenda-anexoI/", viewId = "/faces/financeiro/relatorio/relatorio-demonstrativo-emenda-anexoI.xhtml")
})
@ManagedBean
public class RelatorioDemonstrativoEmendaAnexoIControlador implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    private ModalidadeEmenda modalidadeEmenda;
    private String filtros;

    private Exercicio exercicio;

    @URLAction(mappingId = "relatorio-demonstrativo-emenda-anexoI", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        modalidadeEmenda = ModalidadeEmenda.DIRETA;
        exercicio = sistemaFacade.getExercicioCorrente();
        filtros = "";
    }

    public List<SelectItem> getModalidades() {
        return Util.getListSelectItem(ModalidadeEmenda.values());
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USER", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.adicionarParametro("MUNICIPIO", "CÂMARA MUNICIPAL DE RIO BRANCO");
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("complemento", montarParametros());
            dto.adicionarParametro("FILTROS", filtros);
            dto.setNomeRelatorio("RELATORIO-DEMONSTRATIVO-EMENDA-ANEXO-I");
            dto.setApi("contabil/demonstrativo-emenda-individual-anexoI-II/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (exercicio == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício deve ser informado.");
        }
        if (modalidadeEmenda == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Modalidade Emenda deve ser informado.");
        }
        ve.lancarException();
    }

    private String montarParametros() {
        String retorno = "and e.ano = " + exercicio.getAno().toString() + " and modalidadeemenda = '" + modalidadeEmenda.name() + "'";
        return retorno;
    }

    public ModalidadeEmenda getModalidadeEmenda() {
        return modalidadeEmenda;
    }

    public void setModalidadeEmenda(ModalidadeEmenda modalidadeEmenda) {
        this.modalidadeEmenda = modalidadeEmenda;
    }

    public String getFiltros() {
        return filtros;
    }

    public void setFiltros(String filtros) {
        this.filtros = filtros;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

}
