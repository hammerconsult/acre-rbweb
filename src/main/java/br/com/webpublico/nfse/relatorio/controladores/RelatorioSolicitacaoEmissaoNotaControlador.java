package br.com.webpublico.nfse.relatorio.controladores;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.nfse.domain.SituacaoAidfe;
import br.com.webpublico.nfse.domain.dtos.FiltroSolicitacaoEmissaoNota;
import br.com.webpublico.nfse.domain.dtos.RelatorioSolicitacaoEmissaoNotaDTO;
import br.com.webpublico.nfse.facades.NotaFiscalFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.faces.bean.ManagedBean;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.HashMap;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-relatorio-solicitacao-emissao-nota",
        pattern = "/nfse/relatorio/solicitacao-emissao-nota/",
        viewId = "/faces/tributario/nfse/relatorio/solicitacao-emissao-nota.xhtml")
})
public class RelatorioSolicitacaoEmissaoNotaControlador extends AbstractReport {

    @EJB
    private NotaFiscalFacade notaFiscalFacade;
    private FiltroSolicitacaoEmissaoNota filtro;

    @URLAction(mappingId = "novo-relatorio-solicitacao-emissao-nota", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtro = new FiltroSolicitacaoEmissaoNota();
    }

    public void gerarRelatorio() {
        try {
            List<RelatorioSolicitacaoEmissaoNotaDTO> solicitacoes = notaFiscalFacade.buscarSolicitacoesDeEmissaoDeNota(filtro);
            setGeraNoDialog(true);
            HashMap parameters = new HashMap();
            parameters.put("IMAGEM", getCaminhoImagem());
            parameters.put("MUNICIPIO", "Municipio de Rio Branco");
            parameters.put("TITULO", "Secretaria Municipal de Finanças");
            parameters.put("USUARIO", SistemaFacade.obtemLogin());
            parameters.put("FILTROS", filtro.montarDescricaoFiltros());
            String arquivoJasper = "RelatorioSolicitacaoEmissaoNota.jasper";
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(solicitacoes);
            gerarRelatorioComDadosEmCollectionView(arquivoJasper, parameters, ds);
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public List<SelectItem> getSituacoes() {
        return Util.getListSelectItem(SituacaoAidfe.values());
    }

    public FiltroSolicitacaoEmissaoNota getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroSolicitacaoEmissaoNota filtro) {
        this.filtro = filtro;
    }
}

