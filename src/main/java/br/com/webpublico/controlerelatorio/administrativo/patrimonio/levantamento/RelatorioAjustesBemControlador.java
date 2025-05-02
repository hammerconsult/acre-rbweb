package br.com.webpublico.controlerelatorio.administrativo.patrimonio.levantamento;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidadesauxiliares.administrativo.relatorio.FiltroPatrimonio;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.exception.ValidacaoException;
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
import java.util.List;

/**
 * Created by wellington on 08/08/17.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-ajustes-bem-movel", pattern = "/patrimonio/ajustes-bem-movel/",
        viewId = "/faces/administrativo/patrimonio/relatorios/relatorioajustesbemmovel.xhtml")
})
public class RelatorioAjustesBemControlador {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private FiltroPatrimonio filtroPatrimonio;
    private String filtros;

    @URLAction(mappingId = "relatorio-ajustes-bem-movel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoBemMovel() {
        inicializarFiltroPatrimonio(TipoBem.MOVEIS);
    }

    private void inicializarFiltroPatrimonio(TipoBem tipoBem) {
        filtroPatrimonio = new FiltroPatrimonio();
        filtroPatrimonio.setTipoBem(tipoBem);
        filtroPatrimonio.setDataReferencia(sistemaFacade.getDataOperacao());
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarFiltrosObrigatorios();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "Patrimônio");
            dto.adicionarParametro("SECRETARIA", sistemaFacade.getUnidadeOrganizacionalAdministrativaCorrente().toString());
            dto.adicionarParametro("NOMERELATORIO", "Relatório de Ajustes de Bens " + filtroPatrimonio.getTipoBem().getDescricao());
            dto.adicionarParametro("condicaoUnidadeOrc", montarCondicaoUnidadeOrcEFiltros());
            dto.adicionarParametro("FILTROS", filtros);
            dto.adicionarParametro("tipoBem", filtroPatrimonio.getTipoBem().getToDto());
            dto.adicionarParametro("dataReferencia", getDataReferencia());
            dto.adicionarParametro("mes", filtroPatrimonio.getMes());
            dto.adicionarParametro("ano", filtroPatrimonio.getAno());
            dto.setNomeRelatorio("RELATÓRIO-DE-AJUSTES-DE-BENS-MÓVEIS");
            dto.setApi("administrativo/ajustes-bem-movel/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e){
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void validarFiltrosObrigatorios() {
        ValidacaoException ve = new ValidacaoException();
        if (filtroPatrimonio.getMes() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês deve ser informado.");
        } else if (filtroPatrimonio.getMes() <= 0 || filtroPatrimonio.getMes() > 12) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês deve conter um valor entre 1 e 12.");
        }
        if (filtroPatrimonio.getAno() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano deve ser informado.");
        }
        ve.lancarException();
    }

    public void limparCampos() {
        inicializarFiltroPatrimonio(filtroPatrimonio.getTipoBem());
    }

    public FiltroPatrimonio getFiltroPatrimonio() {
        return filtroPatrimonio;
    }

    public void setFiltroPatrimonio(FiltroPatrimonio filtroPatrimonio) {
        this.filtroPatrimonio = filtroPatrimonio;
    }

    public List<HierarquiaOrganizacional> buscarHierarquiasOrcamentarias(String parte) {
        return hierarquiaOrganizacionalFacade.buscarFiltrandoHierarquiaOrganizacionalOrcamentariaOndeUsuarioEhGestorPatrimonio(
            sistemaFacade.getUsuarioCorrente(),
            sistemaFacade.getDataOperacao(),
            parte.trim());
    }

    private String getDataReferencia (){
        return DataUtil.getDataFormatada(DataUtil.montaData(DataUtil.ultimoDiaDoMes(filtroPatrimonio.getMes()), filtroPatrimonio.getMes() - 1, filtroPatrimonio.getAno()).getTime());
    }

    private String montarCondicaoUnidadeOrcEFiltros() {
        String condicaoUnidadeOrc = " ";
        filtros = "Mês: " + filtroPatrimonio.getMes() + "; ";
        filtros += "Ano: " + filtroPatrimonio.getAno() + "; ";
        if (filtroPatrimonio.getHierarquiaOrc() != null){
            condicaoUnidadeOrc += " where unidade_orcamentaria.id_orcamentaria = " + filtroPatrimonio.getHierarquiaOrc().getSubordinada().getId();
            filtros += "Unidade Orçamentária: " + filtroPatrimonio.getHierarquiaOrc() + "; ";
        }
        return condicaoUnidadeOrc;
    }
}
