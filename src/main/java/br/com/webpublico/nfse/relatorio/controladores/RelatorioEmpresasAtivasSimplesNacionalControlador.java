package br.com.webpublico.nfse.relatorio.controladores;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.enums.SituacaoCadastralCadastroEconomico;
import br.com.webpublico.enums.TipoPorte;
import br.com.webpublico.negocios.CadastroEconomicoFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.nfse.domain.dtos.FiltroRelatorioEmpresasAtivasSimplesNacional;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.base.Strings;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-relatorio-empresas-ativas-simples-nacional",
        pattern = "/nfse/relatorio/empresas-ativas-simples-nacional/",
        viewId = "/faces/tributario/nfse/relatorio/empresas-ativas-no-simples-nacional.xhtml")
})
public class RelatorioEmpresasAtivasSimplesNacionalControlador extends AbstractReport {

    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    private FiltroRelatorioEmpresasAtivasSimplesNacional filtro;

    @URLAction(mappingId = "novo-relatorio-empresas-ativas-simples-nacional", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtro = new FiltroRelatorioEmpresasAtivasSimplesNacional();
        filtro.setInscricaoInicial("1");
        filtro.setInscricaoFinal("999999999");
    }

    public void gerarRelatorio() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MUNICIPIO", "Municipio de Rio Branco");
            dto.adicionarParametro("TITULO", "Secretaria Municipal de Finanças");
            dto.adicionarParametro("CONDICAO", montarCondicao());
            dto.adicionarParametro("USUARIO", SistemaFacade.obtemLogin());
            dto.adicionarParametro("FILTROS", filtro.montarDescricaoFiltros());
            dto.setNomeRelatorio("Relatório de Empresas Ativas no Simples Nacional");
            dto.setApi("tributario/empresas-ativas-simples-nacional/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public String montarCondicao() {
        String where = "";

        if (!Strings.isNullOrEmpty(filtro.getInscricaoInicial())) {
            where += " and cad.inscricaoCadastral >= '".concat(filtro.getInscricaoInicial().concat("'"));
        }
        if (!Strings.isNullOrEmpty(filtro.getInscricaoFinal())) {
            where += " and cad.inscricaoCadastral <= '".concat(filtro.getInscricaoFinal().concat("'"));
        }
        if (!Strings.isNullOrEmpty(filtro.getCnpjInicial())) {
            where += " and replace(replace(replace(pj.cnpj, '.', ''), '/', ''), '-', '') >= '".concat(StringUtil.retornaApenasNumeros(filtro.getCnpjInicial()).concat("'"));
        }
        if (!Strings.isNullOrEmpty(filtro.getCnpjFinal())) {
            where += " and replace(replace(replace(pj.cnpj, '.', ''), '/', ''), '-', '') <= '".concat(StringUtil.retornaApenasNumeros(filtro.getCnpjFinal()).concat("'"));
        }
        return where;
    }

    public List<SelectItem> getPortes() {
        return Util.getListSelectItem(TipoPorte.values());
    }

    public List<SelectItem> getSituacoes() {
        return Util.getListSelectItem(SituacaoCadastralCadastroEconomico.values());
    }

    public FiltroRelatorioEmpresasAtivasSimplesNacional getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroRelatorioEmpresasAtivasSimplesNacional filtro) {
        this.filtro = filtro;
    }
}
