package br.com.webpublico.nfse.relatorio.controladores;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.nfse.domain.SorteioNfse;
import br.com.webpublico.nfse.domain.dtos.FiltroRelatorioCupomParticipante;
import br.com.webpublico.nfse.domain.dtos.RelatorioCupomParticipanteDTO;
import br.com.webpublico.nfse.facades.NotaFiscalFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.HashMap;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-relatorio-cupom-participante",
        pattern = "/nfse/relatorio/cupom-participante/",
        viewId = "/faces/tributario/nfse/relatorio/cupom-participante.xhtml")
})
public class RelatorioCupomParticipanteControlador extends AbstractReport {

    @EJB
    private NotaFiscalFacade notaFiscalFacade;
    private FiltroRelatorioCupomParticipante filtro;

    @URLAction(mappingId = "novo-relatorio-cupom-participante", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtro = new FiltroRelatorioCupomParticipante();
    }

    public void gerarRelatorio() {
        try {
            List<RelatorioCupomParticipanteDTO> cuponsParticipantes = notaFiscalFacade.buscarCuponsParticipantes(filtro);
            setGeraNoDialog(true);
            HashMap parameters = new HashMap();
            parameters.put("IMAGEM", getCaminhoImagem());
            parameters.put("MUNICIPIO", "Municipio de Rio Branco");
            parameters.put("TITULO", "Secretaria Municipal de Finanças");
            parameters.put("USUARIO", SistemaFacade.obtemLogin());

            parameters.put("FILTROS", filtro.montarDescricaoFiltros());
            String arquivoJasper = "RelatorioCupomParticipante.jasper";
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(cuponsParticipantes);
            gerarRelatorioComDadosEmCollectionView(arquivoJasper, parameters, ds);
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public List<SorteioNfse> completarCampanhasNfse(String filtro) {
        return notaFiscalFacade.buscarCampanhasNfse(filtro);
    }

    public FiltroRelatorioCupomParticipante getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroRelatorioCupomParticipante filtro) {
        this.filtro = filtro;
    }
}
