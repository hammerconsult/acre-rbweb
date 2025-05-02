package br.com.webpublico.nfse.relatorio.controladores;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.nfse.domain.dtos.FiltroRelatorioReceitaDeclaradaISSQN;
import br.com.webpublico.nfse.domain.dtos.RelatorioReceitaDeclaradaISSQNDTO;
import br.com.webpublico.nfse.facades.NotaFiscalFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-relatorio-receita-declarada-iss",
        pattern = "/nfse/relatorio/receita-declarada-iss/",
        viewId = "/faces/tributario/nfse/relatorio/receita-declarada-iss.xhtml")
})
public class RelatorioReceitaDeclaradaISSControlador extends AbstractReport {

    @EJB
    private NotaFiscalFacade notaFiscalFacade;
    private FiltroRelatorioReceitaDeclaradaISSQN filtro;

    @URLAction(mappingId = "novo-relatorio-receita-declarada-iss", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtro = new FiltroRelatorioReceitaDeclaradaISSQN();
        filtro.setExercicioInicial(getSistemaFacade().getExercicioCorrente());
        filtro.setMesInicial(Mes.getMesToInt(DataUtil.getMes(new Date())));
        filtro.setExercicioFinal(filtro.getExercicioInicial());
        filtro.setMesFinal(filtro.getMesInicial());
    }

    public void gerarRelatorio() {
        try {
            filtro.validarCamposObrigatorios();
            List<RelatorioReceitaDeclaradaISSQNDTO> receitasDeclaradas = notaFiscalFacade.buscarReceitasDeclaradasISS(filtro);
            setGeraNoDialog(true);
            HashMap parameters = new HashMap();
            parameters.put("IMAGEM", getCaminhoImagem());
            parameters.put("MUNICIPIO", "Municipio de Rio Branco");
            parameters.put("TITULO", "Secretaria Municipal de Finanças");
            parameters.put("USUARIO", SistemaFacade.obtemLogin());
            parameters.put("FILTROS", filtro.montarDescricaoFiltros());
            parameters.put("SUBREPORT_DIR", getCaminho());
            parameters.put("TOTALIZADORES", notaFiscalFacade.buscarTotalizadoresReceitasDeclaradasISS(receitasDeclaradas));
            String arquivoJasper = "RelatorioReceitaDeclaradaISS.jasper";
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(receitasDeclaradas);
            gerarRelatorioComDadosEmCollectionView(arquivoJasper, parameters, ds);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public FiltroRelatorioReceitaDeclaradaISSQN getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroRelatorioReceitaDeclaradaISSQN filtro) {
        this.filtro = filtro;
    }
}
