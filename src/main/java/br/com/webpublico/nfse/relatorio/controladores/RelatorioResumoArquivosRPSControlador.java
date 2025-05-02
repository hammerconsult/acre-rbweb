package br.com.webpublico.nfse.relatorio.controladores;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.nfse.domain.dtos.AbstractFiltroNotaFiscal;
import br.com.webpublico.nfse.domain.dtos.FiltroNotaFiscal;
import br.com.webpublico.nfse.domain.dtos.RelatorioNotasFiscaisDTO;
import br.com.webpublico.nfse.enums.SituacaoLoteRps;
import br.com.webpublico.nfse.facades.NotaFiscalFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.faces.bean.ManagedBean;
import javax.ejb.EJB;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-resumo-arquivos-rps",
        pattern = "/nfse/relatorio/resumo-arquivos-rps/",
        viewId = "/faces/tributario/nfse/relatorio/resumo-arquivos-rps.xhtml")
})
public class RelatorioResumoArquivosRPSControlador extends AbstractReport {

    @EJB
    private NotaFiscalFacade notaFiscalService;
    private AbstractFiltroNotaFiscal filtro;

    @URLAction(mappingId = "novo-resumo-arquivos-rps", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtro = new FiltroNotaFiscal();
        filtro.setDataInicial(DataUtil.getPrimeiroDiaAno(new Date()));
        filtro.setDataFinal(new Date());
    }

    public void gerarRelatorio() {
        try {
            filtro.validarCamposPorEmissao();
            List<RelatorioNotasFiscaisDTO> notasFiscaisDTO = notaFiscalService.buscarNotasFiscaisPorRPS(filtro);
            setGeraNoDialog(true);
            HashMap parameters = new HashMap();
            parameters.put("IMAGEM", getCaminhoImagem());
            parameters.put("MUNICIPIO", "Municipio de Rio Branco");
            parameters.put("TITULO", "Secretaria Municipal de Finanças");
            parameters.put("USUARIO", SistemaFacade.obtemLogin());
            parameters.put("FILTROS", filtro.montarDescricaoFiltros());
            String arquivoJasper = "RelatorioResumoArquivosRPS.jasper";
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(notasFiscaisDTO);
            gerarRelatorioComDadosEmCollectionView(arquivoJasper, parameters, ds);
        } catch (ValidacaoException onpe) {
            FacesUtil.printAllFacesMessages(onpe);
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();

            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public List<SelectItem> getSituacoes() {
        List<SelectItem> retorno = Lists.newArrayList();
        for (SituacaoLoteRps situacaoLoteRps : SituacaoLoteRps.values()) {
            retorno.add(new SelectItem(situacaoLoteRps, situacaoLoteRps.getDescricao()));
        }
        return retorno;
    }

    public AbstractFiltroNotaFiscal getFiltro() {
        return filtro;
    }

    public void setFiltro(AbstractFiltroNotaFiscal filtro) {
        this.filtro = filtro;
    }
}
