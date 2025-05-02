package br.com.webpublico.controle.administrativo.frotas;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.Veiculo;
import br.com.webpublico.entidades.administrativo.frotas.ParametroFrotas;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.administrativo.frotas.ParametroFrotasFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Created by William on 11/02/2016.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorioDePrazoRevisao",
        pattern = "/frota/relatorio-prazo-revisao/", viewId = "/faces/administrativo/frota/relatorios/relatoriodeprazoderevisao.xhtml")
})
public class RelatorioDePrazoRevisao extends AbstractReport {
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ParametroFrotasFacade parametroFrotasFacade;
    private Filtros filtros;
    private StringBuilder criteriosUtilizados;
    private ParametroFrotas parametroFrotas;
    public static final String ARQUIVO_JASPER = "RelatorioDePrazoDeRevisao.jasper";

    private void buscarFiltros(RelatorioDTO dto) {
        criteriosUtilizados = new StringBuilder();
        if (filtros.getVeiculo() != null) {
            dto.adicionarParametro("complementoWhere", " and v.id = " + filtros.getVeiculo().getId());
            criteriosUtilizados = criteriosUtilizados.append(" Veículo: ");
            criteriosUtilizados = criteriosUtilizados.append(filtros.getVeiculo());
        } else {
            criteriosUtilizados = criteriosUtilizados.append(" Todos os veículos. ");
        }
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarRelatorio();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("SECRETARIA", "MUNICÍPIO DE RIO BRANCO");
            String nomRelatorio = "RELATÓRIO DE REVISÕES A VENCER OU VENCIDAS";
            dto.adicionarParametro("NOMERELATORIO", nomRelatorio);
            dto.adicionarParametro("MODULO", "FROTAS");
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.adicionarParametro("codigoSecretaria", buscarCodigoDaSecretariaLogada());
            dto.adicionarParametro("dataOperacao", sistemaFacade.getDataOperacao().getTime());
            dto.adicionarParametro("numeroDiasVencer", parametroFrotas.getDiasDaRevisaoAVencer());
            dto.adicionarParametro("kmAVencer", parametroFrotas.getQuilometrosDaRevisaoAVencer());
            buscarFiltros(dto);
            dto.adicionarParametro("FILTROS", criteriosUtilizados.toString());
            dto.setNomeRelatorio(nomRelatorio);
            dto.setApi("administrativo/prazo-revisao/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void validarRelatorio() {
        ValidacaoException ve = new ValidacaoException();
        if (parametroFrotas == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O parâmetro do frotas deve ser configurado.");
        }
        ve.lancarException();
    }

    private String buscarCodigoDaSecretariaLogada() {
        HierarquiaOrganizacional hierarquiaOrganizacionalCorrente = hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(
            TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
            sistemaFacade.getUnidadeOrganizacionalAdministrativaCorrente(),
            sistemaFacade.getDataOperacao());
        return hierarquiaOrganizacionalCorrente.getCodigo().substring(0, 5);
    }

    @URLAction(mappingId = "relatorioDePrazoRevisao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparFiltros() {
        filtros = new Filtros();
        parametroFrotas = parametroFrotasFacade.buscarParametroFrotas();
    }

    public Filtros getFiltros() {
        return filtros;
    }

    public void setFiltros(Filtros filtros) {
        this.filtros = filtros;
    }

    public class Filtros {

        private Veiculo veiculo;

        public Filtros() {
        }

        public Veiculo getVeiculo() {
            return veiculo;
        }

        public void setVeiculo(Veiculo veiculo) {
            this.veiculo = veiculo;
        }
    }
}

