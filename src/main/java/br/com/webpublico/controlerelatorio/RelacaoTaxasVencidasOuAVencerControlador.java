package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.TaxaVeiculo;
import br.com.webpublico.entidades.Veiculo;
import br.com.webpublico.entidades.administrativo.frotas.ParametroFrotas;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.administrativo.frotas.ParametroFrotasFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 21/10/14
 * Time: 11:10
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relacaoVeiculoTaxasVencidasAVencer",
        pattern = "/frota/relatorio-veiculo-taxas-vencidas-vencer/", viewId = "/faces/administrativo/frota/relatorios/relacao-taxas-vencidas-ou-a-vencer.xhtml")
})
public class RelacaoTaxasVencidasOuAVencerControlador extends AbstractReport {

    private static Logger logger = LoggerFactory.getLogger(RelacaoTaxasVencidasOuAVencerControlador.class);
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ParametroFrotasFacade parametroFrotasFacade;
    private Filtros filtros;
    private StringBuilder criteriosUtilizados;
    private ParametroFrotas parametroFrotas;

    private String montarCondicaoECriteriosUtilizados() {
        StringBuilder condicao = new StringBuilder();
        criteriosUtilizados = new StringBuilder();
        if (filtros != null) {
            if (filtros.getVeiculo() != null) {
                condicao.append(" and ");
                condicao.append(" veic.id = ");
                condicao.append(filtros.getVeiculo().getId());
                criteriosUtilizados = criteriosUtilizados.append(" Veículo: ");
                criteriosUtilizados = criteriosUtilizados.append(filtros.getVeiculo());
            } else {
                criteriosUtilizados = criteriosUtilizados.append(" Todos os veículos. ");
            }
            if (filtros.getTaxaVeiculo() != null) {
                condicao.append(" and ");
                condicao.append(" taxa.id = ");
                condicao.append(filtros.getTaxaVeiculo().getId());
                criteriosUtilizados = criteriosUtilizados.append(" Taxa: ");
                criteriosUtilizados = criteriosUtilizados.append(filtros.getTaxaVeiculo().getDescricao());
            }
            if (!filtros.getUltimoDigitoPlaca().trim().equals("")) {
                condicao.append(" and ");
                condicao.append(" veic.placa like '");
                condicao.append("%");
                condicao.append(filtros.getUltimoDigitoPlaca());
                condicao.append("'");
                criteriosUtilizados = criteriosUtilizados.append(" Final da Placa: ");
                criteriosUtilizados = criteriosUtilizados.append(filtros.getUltimoDigitoPlaca());
            }
        }
        return condicao.toString();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarParametroFrotas();
            String nomeRelatorio = "Relatório de Taxas a Vencer ou Vencidas";
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("NOMERELATORIO", nomeRelatorio);
            dto.adicionarParametro("MODULO", "Frotas");
            dto.adicionarParametro("condicao", montarCondicaoECriteriosUtilizados());
            dto.adicionarParametro("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
            dto.adicionarParametro("CODIGO_SECRETARIA", buscarCodigoDaSecretariaLogada());
            dto.adicionarParametro("NUMERO_DIAS_A_VENCER", parametroFrotas.getDiasDaTaxaAVencer());
            dto.adicionarParametro("condicaoTaxa", filtros.getTaxaVeiculo() != null ? " and taxa.id = " + filtros.getTaxaVeiculo().getId() : " ");
            dto.adicionarParametro("FILTROS", criteriosUtilizados.toString());
            dto.setNomeRelatorio(nomeRelatorio);
            dto.setApi("administrativo/relacao-taxas-vencidas-ou-a-vencer/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório: " + ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void validarParametroFrotas() {
        ValidacaoException ve = new ValidacaoException();
        if (parametroFrotas == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O parâmetro do frotas deve ser configurado.");
        }
        ve.lancarException();
    }

    private String buscarCodigoDaSecretariaLogada() {
        HierarquiaOrganizacional hierarquiaOrganizacionalCorrente =
            hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(
                sistemaFacade.getDataOperacao(),
                sistemaFacade.getUnidadeOrganizacionalAdministrativaCorrente(),
                TipoHierarquiaOrganizacional.ADMINISTRATIVA);
        return hierarquiaOrganizacionalCorrente.getCodigo().substring(0, 5);
    }

    @URLAction(mappingId = "relacaoVeiculoTaxasVencidasAVencer", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
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
        private TaxaVeiculo taxaVeiculo;
        private String ultimoDigitoPlaca;

        public Filtros() {
        }

        public Veiculo getVeiculo() {
            return veiculo;
        }

        public void setVeiculo(Veiculo veiculo) {
            this.veiculo = veiculo;
        }

        public TaxaVeiculo getTaxaVeiculo() {
            return taxaVeiculo;
        }

        public void setTaxaVeiculo(TaxaVeiculo taxaVeiculo) {
            this.taxaVeiculo = taxaVeiculo;
        }

        public String getUltimoDigitoPlaca() {
            return ultimoDigitoPlaca;
        }

        public void setUltimoDigitoPlaca(String ultimoDigitoPlaca) {
            this.ultimoDigitoPlaca = ultimoDigitoPlaca;
        }
    }
}
