package br.com.webpublico.controlerelatorio.administrativo;

import br.com.webpublico.controle.portaltransparencia.PortalTransparenciaNovoFacade;
import br.com.webpublico.entidades.BensMoveis;
import br.com.webpublico.entidadesauxiliares.RelatorioEtiquetaBemFiltro;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
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

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-etiqueta-bem", pattern = "/relatorios/etiqueta-bem/", viewId = "/faces/administrativo/relatorios/relatorio-etiqueta-bem.xhtml")
})
public class RelatorioEtiquetaBemControlador implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;

    @EJB
    private PortalTransparenciaNovoFacade portalTransparenciaNovoFacade;

    private RelatorioEtiquetaBemFiltro filtroPesquisa;

    @URLAction(mappingId = "relatorio-etiqueta-bem", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void relatorioEtiquetaBem() {
        novoFiltroPesquisa();
    }

    private void novoFiltroPesquisa() {
        filtroPesquisa = new RelatorioEtiquetaBemFiltro();
    }

    public void limparFiltrosPesquisa() {
        novoFiltroPesquisa();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            filtroPesquisa.validarFiltros();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setNomeRelatorio("RELATÓRIO DE ETIQUETAS DOS BENS PATRIMONIAIS");
            dto.adicionarParametro("CONDICAO", filtroPesquisa.montarCondicao());
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.adicionarParametro("HAS_FILTROS_NOTA_OR_EMPENHO_UTILIZADOS", filtroPesquisa.hasFiltrosNotaOrEmpenhoUtilizados());
            dto.adicionarParametro("URL_PORTAL", portalTransparenciaNovoFacade.getUrlExternoPortalTransparenciaPrefeitura());
            dto.setApi("administrativo/etiqueta-bem/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e){
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public RelatorioEtiquetaBemFiltro getFiltroPesquisa() {
        return filtroPesquisa;
    }

    public void setFiltroPesquisa(RelatorioEtiquetaBemFiltro filtroPesquisa) {
        this.filtroPesquisa = filtroPesquisa;
    }

    public List<SelectItem> getMeses() {
        return Util.getListSelectItem(Mes.values(), false);
    }

    public List<SelectItem> getTiposAquisicao() {
        return Util.getListSelectItem(TipoAquisicaoBem.values());
    }

    public List<SelectItem> getEstadosConservacao() {
        return Util.getListSelectItem(EstadoConservacaoBem.values());
    }

    public List<SelectItem> getSituacoesEstadoConservacao() {
        return Util.getListSelectItem(SituacaoConservacaoBem.values());
    }

    public List<SelectItem> getTiposGrupo() {
        return Util.getListSelectItem(TipoGrupo.tipoGrupoPorClasseDeUtilizacao(BensMoveis.class));
    }

}
