package br.com.webpublico.controlerelatorio.administrativo.patrimonio.levantamento;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidadesauxiliares.administrativo.relatorio.FiltroPatrimonio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
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
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wellington on 08/08/17.
 */
@ManagedBean(name = "relatorioLevantamentoBemMovelConferenciaGrupoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-levantamento-conferencia-grupo", pattern = "/patrimonio/levantamento-bem-movel-conferencia-grupo/",
        viewId = "/faces/administrativo/patrimonio/relatorios/relatoriolevantamentobemmovelconferenciagrupo.xhtml"),
})
public class RelatorioLevantamentoBemMovelConferenciaGrupoControlador extends AbstractReport {

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private FiltroPatrimonio filtroPatrimonio;

    @URLAction(mappingId = "relatorio-levantamento-conferencia-grupo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        inicializarFiltroPatrimonio();
    }

    private void inicializarFiltroPatrimonio() {
        filtroPatrimonio = new FiltroPatrimonio();
        filtroPatrimonio.setDataReferencia(getSistemaFacade().getDataOperacao());
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarFiltrosObrigatorios();
            RelatorioDTO dto = new RelatorioDTO();
            String nomeRelatorio = "Relatório de Levantamento de Bens Móveis para Conferência de Grupos";
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "Patrimônio");
            dto.adicionarParametro("SECRETARIA", getSistemaFacade().getUnidadeOrganizacionalAdministrativaCorrente().toString());
            dto.adicionarParametro("NOMERELATORIO", nomeRelatorio);
            dto.adicionarParametro("condicao", montarCondicaoEFiltros(filtroPatrimonio, dto));
            dto.adicionarParametro("dataReferencia", DataUtil.getDataFormatada(filtroPatrimonio.getDataReferencia()));
            dto.setNomeRelatorio(nomeRelatorio);
            dto.setApi("administrativo/levantamento-conferencia-grupo/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private String montarCondicaoEFiltros(FiltroPatrimonio filtroPatrimonio, RelatorioDTO dto) {
        StringBuilder where = new StringBuilder();
        StringBuilder filtros = new StringBuilder();

        where.append(" and l.datalevantamento <= to_date('").append(DataUtil.getDataFormatada(filtroPatrimonio.getDataReferencia())).append("', 'dd/MM/yyyy')");
        filtros.append("Data de referência: ").append(DataUtil.getDataFormatada(filtroPatrimonio.getDataReferencia())).append("; ");

        if (filtroPatrimonio.getHierarquiaAdm() != null) {
            where.append(" and l.unidadeadministrativa_id = ").append(filtroPatrimonio.getHierarquiaAdm().getSubordinada().getId());
            filtros.append("Unidade Administrativa: ").append(filtroPatrimonio.getHierarquiaAdm()).append("; ");
        }

        if (filtroPatrimonio.getHierarquiaOrc() != null) {
            where.append(" and l.unidadeorcamentaria_id = ").append(filtroPatrimonio.getHierarquiaOrc().getSubordinada().getId());
            filtros.append("Unidade Orçamentária: ").append(filtroPatrimonio.getHierarquiaOrc()).append("; ");
        }

        if (filtroPatrimonio.getDescricaoBem() != null && !filtroPatrimonio.getDescricaoBem().trim().isEmpty()) {
            where.append(" and l.descricaobem like '%").append(filtroPatrimonio.getDescricaoBem()).append("%'");
            filtros.append("Descrição do Bem: ").append(filtroPatrimonio.getDescricaoBem()).append("; ");
        }

        if (filtroPatrimonio.getObjetoCompra() != null && filtroPatrimonio.getObjetoCompra().getId() != null) {
            where.append(" and oc.id = ").append(filtroPatrimonio.getObjetoCompra().getId());
            filtros.append("Objeto de Compra: ").append(filtroPatrimonio.getObjetoCompra()).append("; ");
        }

        if (filtroPatrimonio.getGrupoObjetoCompra() != null && filtroPatrimonio.getGrupoObjetoCompra().getId() != null) {
            where.append(" and goc.id = ").append(filtroPatrimonio.getGrupoObjetoCompra().getId());
            filtros.append("Grupo do Objeto de Compra: ").append(filtroPatrimonio.getGrupoObjetoCompra()).append("; ");
        }

        if (filtroPatrimonio.getGrupoBem() != null && filtroPatrimonio.getGrupoBem().getId() != null) {
            where.append(" and gb.id = ").append(filtroPatrimonio.getGrupoBem().getId());
            filtros.append("Grupo do Bem: ").append(filtroPatrimonio.getGrupoBem()).append("; ");
        }

        if (filtroPatrimonio.getDataAquisicaoInicial() != null) {
            where.append(" and trunc(l.dataaquisicao) >= to_date('").append(DataUtil.getDataFormatada(filtroPatrimonio.getDataAquisicaoInicial())).append("', 'dd/MM/yyyy')");
            filtros.append("Data de Aquisição Inicial: ").append(DataUtil.getDataFormatada(filtroPatrimonio.getDataAquisicaoInicial())).append("; ");
        }

        if (filtroPatrimonio.getDataAquisicaoFinal() != null) {
            where.append(" and trunc(l.dataaquisicao) <= to_date('").append(DataUtil.getDataFormatada(filtroPatrimonio.getDataAquisicaoFinal())).append("', 'dd/MM/yyyy')");
            filtros.append("Data de Aquisição Final: ").append(DataUtil.getDataFormatada(filtroPatrimonio.getDataAquisicaoFinal())).append("; ");
        }

        dto.adicionarParametro("FILTROS", filtros.toString());
        return where.toString();
    }

    private void validarFiltrosObrigatorios() {
        ValidacaoException ve = new ValidacaoException();
        if (filtroPatrimonio.getHierarquiaOrc() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Orçamentária deve ser informado.");
        }
        if (filtroPatrimonio.getDataReferencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Referência deve ser informado.");
        }
        ve.lancarException();
    }

    public void limparCampos() {
        inicializarFiltroPatrimonio();
    }

    public FiltroPatrimonio getFiltroPatrimonio() {
        return filtroPatrimonio;
    }

    public void setFiltroPatrimonio(FiltroPatrimonio filtroPatrimonio) {
        this.filtroPatrimonio = filtroPatrimonio;
    }

    public List<SelectItem> buscarHierarquiasOrcamentarias() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (HierarquiaOrganizacional obj : hierarquiaOrganizacionalFacade.retornaHierarquiaOrganizacionalOrcamentariaOndeUsuarioEhGestorPatrimonio(getSistemaFacade().getUsuarioCorrente(), getSistemaFacade().getDataOperacao())) {
            toReturn.add(new SelectItem(obj, obj.toString()));
        }
        return toReturn;
    }
}
