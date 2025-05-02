package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.entidades.Veiculo;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.HashMap;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorioDetalhamentoVeiculo", pattern = "/detalhamento-veiculo/",
        viewId = "/faces/administrativo/frota/relatorios/relatorioDetalhamentoVeiculo.xhtml")
})
public class RelatorioDetalhamentoVeiculoControlador extends AbstractReport {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private StringBuffer filtro;
    private StringBuffer where;
    private FiltrosRelatorio filtrosRelatorio;

    private void montarCondicaoEFiltros() {
        where = new StringBuffer();
        filtro = new StringBuffer();

        if (filtrosRelatorio.getVeiculo() != null && filtrosRelatorio.getVeiculo().getId() != null) {
            where.append(" and v.id = ").append(filtrosRelatorio.getVeiculo().getId());
            filtro.append("Veículo: " + filtrosRelatorio.getVeiculo());
        }
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            montarCondicaoEFiltros();
            String nomeRelatorio = "DETALHAMENTO DE VEÍCULO";
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("FILTROS", filtro.toString());
            dto.adicionarParametro("MODULO", "FROTAS");
            dto.adicionarParametro("SECRETARIA", "SECRETÁRIA DE FROTAS");
            dto.adicionarParametro("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
            dto.adicionarParametro("NOMERELATORIO", nomeRelatorio);
            dto.adicionarParametro("condicao", where.toString());
            dto.adicionarParametro("usuarioSistemaId", sistemaFacade.getUsuarioCorrente().getId());
            dto.setNomeRelatorio(nomeRelatorio);
            dto.setApi("administrativo/detalhamento-veiculo/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    @URLAction(mappingId = "relatorioDetalhamentoVeiculo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparFiltros() {
        filtro = new StringBuffer();
        where = new StringBuffer();
        filtrosRelatorio = new FiltrosRelatorio();
    }


    public FiltrosRelatorio getFiltrosRelatorio() {
        return filtrosRelatorio;
    }

    public void setFiltrosRelatorio(FiltrosRelatorio filtrosRelatorio) {
        this.filtrosRelatorio = filtrosRelatorio;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public void setSistemaFacade(SistemaFacade sistemaFacade) {
        this.sistemaFacade = sistemaFacade;
    }

    public StringBuffer getFiltro() {
        return filtro;
    }

    public void setFiltro(StringBuffer filtro) {
        this.filtro = filtro;
    }

    public StringBuffer getWhere() {
        return where;
    }

    public void setWhere(StringBuffer where) {
        this.where = where;
    }

    public class FiltrosRelatorio {

        private Veiculo veiculo;

        public FiltrosRelatorio() {
        }

        public Veiculo getVeiculo() {
            return veiculo;
        }

        public void setVeiculo(Veiculo veiculo) {
            this.veiculo = veiculo;
        }
    }
}
