package br.com.webpublico.controle;

import br.com.webpublico.entidades.LocalEstoque;
import br.com.webpublico.entidadesauxiliares.FiltroMateriais;
import br.com.webpublico.enums.ConsolidadoDetalhado;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.LocalEstoqueFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "relatorioMovimentacaoEstoqueControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelMovEstoque", pattern = "/relatorio-movimentacao-estoque/", viewId = "/faces/administrativo/materiais/relatorios/movimentacao-estoque.xhtml")
})
public class RelatorioMovimentacaoEstoqueControlador implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private LocalEstoqueFacade localEstoqueFacade;
    private FiltroMateriais filtroRelatorio;

    public RelatorioMovimentacaoEstoqueControlador() {
        super();
    }

    @URLAction(mappingId = "novoRelMovEstoque", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoRelatorio() {
        filtroRelatorio = new FiltroMateriais();
        filtroRelatorio.setDataInicial(null);
        filtroRelatorio.setDataFinal(null);
    }

    public void gerarRelatorio() {
        try {
            validarFiltrosEstoque();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "Materiais");
            dto.adicionarParametro("NOMERELATORIO", getNomeRelatorio());
            dto.adicionarParametro("CONDICAO", montaCondicao());
            dto.adicionarParametro("TIPORELATORIO", filtroRelatorio.getTipoRelatorio().name());
            dto.adicionarParametro("FILTROS", filtroRelatorio.getFiltros());
            dto.adicionarParametro("DATAOPERACAO", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("administrativo/movimentacao-estoque/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String getNomeRelatorio() {
        return "RELATÓRIO DE MOVIMENTAÇÃO DE ESTOQUE";
    }

    private String montaCondicao() {
        String filtros = "";
        StringBuilder condicao = new StringBuilder();
        filtros = "Tipo de Relatório: " + filtroRelatorio.getTipoRelatorio().getDescricao() + ", ";
        if (filtroRelatorio.getDataInicial() != null) {
            condicao.append(" AND TO_DATE(TO_CHAR(MO.DATAMOVIMENTO, 'dd/MM/yyyy'), 'dd/MM/yyyy') >= to_date('").append(Util.dateToString(filtroRelatorio.getDataInicial())).append("', 'dd/MM/yyyy') ");
            filtros = "Data Inicial: " + DataUtil.getDataFormatada(filtroRelatorio.getDataInicial()) + ", ";
        }
        if (filtroRelatorio.getDataFinal() != null) {
            condicao.append(" AND TO_DATE(TO_CHAR(MO.DATAMOVIMENTO, 'dd/MM/yyyy'), 'dd/MM/yyyy') <= to_date('").append(Util.dateToString(filtroRelatorio.getDataFinal())).append("', 'dd/MM/yyyy') ");
            filtros = "Data Final: " + DataUtil.getDataFormatada(filtroRelatorio.getDataFinal()) + ", ";
        }
        if (filtroRelatorio.getMaterial() != null) {
            condicao.append("  AND M.ID = ").append(filtroRelatorio.getMaterial().getId());
            filtros = "Material: " + filtroRelatorio.getMaterial().toString() + ", ";
        }
        if (filtroRelatorio.getGrupoMaterial() != null) {
            condicao.append(" AND M.GRUPO_ID = ").append(filtroRelatorio.getGrupoMaterial().getId());
            filtros = "Grupo Material: " + filtroRelatorio.getGrupoMaterial().toString() + ", ";
        }
        if (filtroRelatorio.getLocalEstoque() != null) {
            condicao.append(" AND l.codigo like '").append(filtroRelatorio.getLocalEstoque().getCodigoSemZerosFinais()).append("%'");
            filtros = "Local de Estoque: " + filtroRelatorio.getLocalEstoque().toString() + ", ";
        }
        if (filtroRelatorio.getHierarquiaAdministrativa() != null) {
            condicao.append(" AND VW.SUBORDINADA_ID = ").append(filtroRelatorio.getHierarquiaAdministrativa().getSubordinada().getId());
            filtros = "Unidade Organizacional: " + filtroRelatorio.getHierarquiaAdministrativa().toString() + ", ";
        }
        filtroRelatorio.setFiltros(filtros.substring(0, filtros.length() - 1));
        return condicao.toString();
    }

    private void validarFiltrosEstoque() {
        ValidacaoException ve = new ValidacaoException();
        if (filtroRelatorio.getHierarquiaAdministrativa() == null && filtroRelatorio.getLocalEstoque() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe uma unidade organizacional ou um local de estoque.");
        }
        if (filtroRelatorio.getDataInicial() != null && filtroRelatorio.getDataFinal() != null && filtroRelatorio.getDataFinal().before(filtroRelatorio.getDataInicial())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data Inicial deve ser anterior ou igual à Data Final.");
        }
        ve.lancarException();
    }

    public List<SelectItem> getTiposRelatorio() {
        return Util.getListSelectItemSemCampoVazio(ConsolidadoDetalhado.values());
    }

    public List<LocalEstoque> completarLocalEstoque(String filtro) {
        if (filtroRelatorio.getHierarquiaAdministrativa() != null) {
            return localEstoqueFacade.completarLocaisEstoquePrimeiroNivel(filtro, filtroRelatorio.getHierarquiaAdministrativa().getSubordinada());
        }
        return localEstoqueFacade.completarLocaisEstoquePrimeiroNivel(filtro);
    }

    public FiltroMateriais getFiltroRelatorio() {
        return filtroRelatorio;
    }

    public void setFiltroRelatorio(FiltroMateriais filtroRelatorio) {
        this.filtroRelatorio = filtroRelatorio;
    }
}
