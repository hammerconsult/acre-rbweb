package br.com.webpublico.controlerelatorio.administrativo;

import br.com.webpublico.entidades.ProcessoDeCompra;
import br.com.webpublico.entidadesauxiliares.administrativo.relatorio.demonstrativocompras.FiltroDemonstrativoCompras;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ProcessoDeCompraFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Created by Wellington on 07/04/2016.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-demonstrativo-compras", pattern = "/administrativo/demonstrativo-compras/",
        viewId = "/faces/administrativo/relatorios/demonstrativocompras.xhtml"),
})
public class DemonstrativoComprasControlador {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ProcessoDeCompraFacade processoDeCompraFacade;
    private FiltroDemonstrativoCompras filtroDemonstrativoCompras;

    @URLAction(mappingId = "novo-demonstrativo-compras", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtroDemonstrativoCompras = new FiltroDemonstrativoCompras();
    }

    public FiltroDemonstrativoCompras getFiltroDemonstrativoCompras() {
        return filtroDemonstrativoCompras;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarFiltrosObrigatorios();
            RelatorioDTO dto = montarParametros();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setNomeRelatorio("DEMONSTRATIVO-DE-LICITAÇÕES-CONTRATOS-E-OBRAS-CONTRATADAS");
            dto.setApi("administrativo/demonstrativo-licitacoes-contrato-obra/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    private void validarFiltrosObrigatorios() {
        ValidacaoException ve = new ValidacaoException();
        if (filtroDemonstrativoCompras.getHierarquiaOrcamentaria() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Orçamentária deve ser informado.");
        }
        if (filtroDemonstrativoCompras.getProcessoDeComprasSelecionados() == null ||
            filtroDemonstrativoCompras.getProcessoDeComprasSelecionados().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Deve ser selecionado ao menos um processo de compra.");
        }
        ve.lancarException();
    }

    private RelatorioDTO montarParametros() {
        RelatorioDTO relatorioDTO = new RelatorioDTO();
        relatorioDTO.setNomeParametroBrasao("BRASAO");
        relatorioDTO.adicionarParametro("FILTROS", montarDescritivoDeCriteriosUtilizados());
        relatorioDTO.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
        relatorioDTO.adicionarParametro("MODULO", "Compras e Licitações");
        relatorioDTO.adicionarParametro("SECRETARIA", sistemaFacade.getSistemaService().getHierarquiAdministrativaCorrente().toString());
        relatorioDTO.adicionarParametro("dataOperacao", sistemaFacade.getDataOperacao().getTime());
        relatorioDTO.adicionarParametro("subordinadaId", filtroDemonstrativoCompras.getHierarquiaOrcamentaria().getSubordinada().getId());
        relatorioDTO.adicionarParametro("idsProcessosDeCompraSelecionados", filtroDemonstrativoCompras.getIdsProcessosDeCompraSelecionados());
        relatorioDTO.adicionarParametro("NOMERELATORIO", "Demonstrativo de Licitações, Contratos e Obras Contratadas");
        return relatorioDTO;
    }

    private String montarDescritivoDeCriteriosUtilizados() {
        return "Unidade Orçamentária: " + filtroDemonstrativoCompras.getHierarquiaOrcamentaria() + "; ";
    }

    public void buscarProcessosDeCompraDaUnidadeOrcamentaria() {
        filtroDemonstrativoCompras.setProcessosDeCompra(processoDeCompraFacade.buscarProcessosDeCompraPorUnidadeOrcamentaria(filtroDemonstrativoCompras.getHierarquiaOrcamentaria().getSubordinada()));
        desmarcarTodosOsProcessoDeComprasSelecionados();
    }

    public Boolean isSelecionadoTodosOsProcessosDeCompra() {
        return filtroDemonstrativoCompras.getProcessoDeComprasSelecionados().size() > 0 &&
            filtroDemonstrativoCompras.getProcessosDeCompra().size() == filtroDemonstrativoCompras.getProcessoDeComprasSelecionados().size();
    }


    public void desmarcarTodosOsProcessoDeComprasSelecionados() {
        filtroDemonstrativoCompras.setProcessoDeComprasSelecionados(Lists.<ProcessoDeCompra>newArrayList());
    }

    public void marcarTodosOsProcessoDeComprasSelecionados() {
        filtroDemonstrativoCompras.setProcessoDeComprasSelecionados(filtroDemonstrativoCompras.getProcessosDeCompra());
    }

    public Boolean isProcessoDeCompraSelecionado(ProcessoDeCompra processoDeCompra) {
        return filtroDemonstrativoCompras.getProcessoDeComprasSelecionados().contains(processoDeCompra);
    }

    public void removerSelecaoDoProcessoDeCompra(ProcessoDeCompra processoDeCompra) {
        filtroDemonstrativoCompras.getProcessoDeComprasSelecionados().remove(processoDeCompra);
    }

    public void adicionarSelecaoDoProcessoDeCompra(ProcessoDeCompra processoDeCompra) {
        filtroDemonstrativoCompras.getProcessoDeComprasSelecionados().add(processoDeCompra);
    }
}
