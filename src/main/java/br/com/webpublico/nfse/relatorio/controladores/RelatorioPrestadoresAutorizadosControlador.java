package br.com.webpublico.nfse.relatorio.controladores;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.enums.SituacaoCadastralCadastroEconomico;
import br.com.webpublico.enums.TipoPorte;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.nfse.domain.dtos.FiltroRelatorioPrestadoresAutorizados;
import br.com.webpublico.nfse.domain.dtos.RelatorioPrestadoresAutorizados;
import br.com.webpublico.nfse.facades.NotaFiscalFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.HashMap;
import java.util.List;


@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-relatorio-prestadores-autorizados",
        pattern = "/nfse/relatorio/prestadores-autorizados/",
        viewId = "/faces/tributario/nfse/relatorio/prestadores-autorizados.xhtml")
})
public class RelatorioPrestadoresAutorizadosControlador extends AbstractReport {

    @EJB
    private NotaFiscalFacade notaFiscalFacade;
    private FiltroRelatorioPrestadoresAutorizados filtro;

    @URLAction(mappingId = "novo-relatorio-prestadores-autorizados", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtro = new FiltroRelatorioPrestadoresAutorizados();
    }

    public void gerarRelatorio() {
        try {
            List<RelatorioPrestadoresAutorizados> dados = notaFiscalFacade.buscarDadosRelatorioPrestadoresAutorizados(filtro);
            setGeraNoDialog(true);
            HashMap parameters = new HashMap();
            parameters.put("IMAGEM", getCaminhoImagem());
            parameters.put("MUNICIPIO", "Municipio de Rio Branco");
            parameters.put("TITULO", "Secretaria Municipal de Finanças");
            parameters.put("USUARIO", SistemaFacade.obtemLogin());
            parameters.put("NOME_RELATORIO", "RELATÓRIO DE PRESTADORES AUTORIZADOS");
            parameters.put("FILTROS", filtro.montarDescricaoFiltros());
            String arquivoJasper = "RelatorioPrestadoresAutorizados.jasper";
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(dados);
            gerarRelatorioComDadosEmCollectionView(arquivoJasper, parameters, ds);
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public List<SelectItem> getPortes() {
        return Util.getListSelectItem(TipoPorte.values());
    }

    public List<SelectItem> getSituacoes() {
        return Util.getListSelectItem(SituacaoCadastralCadastroEconomico.values());
    }

    public FiltroRelatorioPrestadoresAutorizados getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroRelatorioPrestadoresAutorizados filtro) {
        this.filtro = filtro;
    }
}
