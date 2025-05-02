package br.com.webpublico.controle.administrativo.patrimonio;

import br.com.webpublico.controle.RelatorioPatrimonioControlador;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidadesauxiliares.FiltroConsultaMovimentacaoEstoqueContabil;
import br.com.webpublico.entidadesauxiliares.administrativo.relatorio.FiltroPatrimonio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorioEstoquePorGrupoMaterialComparativo",
        pattern = "/relatorio-estoque-por-grupo-material-comparativo/",
        viewId = "/faces/administrativo/patrimonio/relatorios/relatorioestoqueporgrupomaterialcomparativo.xhtml")})
public class RelatorioDeEstoquePorGrupoMaterialComparativoControlador extends RelatorioPatrimonioControlador {

    private FiltroPatrimonio filtroPatrimonio;

    @URLAction(mappingId = "relatorioEstoquePorGrupoMaterialComparativo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void relatorioEstoquePorGrupoMaterialComparativo() {
        limparCampos();
        novoFiltro();
    }

    private void novoFiltro() {
        filtroPatrimonio = new FiltroPatrimonio();
        filtroPatrimonio.setDataReferencia(getSistemaFacade().getDataOperacao());
    }

    public void gerarRelatorio(FiltroConsultaMovimentacaoEstoqueContabil filtroMaterial) {
        novoFiltro();
        filtroPatrimonio.setDataReferencia(filtroMaterial.getDataFinal());
        filtroPatrimonio.setHierarquiaOrc(filtroMaterial.getHierarquiaOrcamentaria());
        filtroPatrimonio.setGrupoMaterial(filtroMaterial.getGrupoMaterial());
        gerarRelatorio();
    }

    public void gerarRelatorio() {
        try {
            validarCampos();
            RelatorioDTO dto = montarParametros();
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

    public RelatorioDTO montarParametros() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.adicionarParametro("USUARIO",  getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.setNomeParametroBrasao("BRASAO");
        dto.adicionarParametro("MUNICIPIO", montarNomeDoMunicipio());
        dto.adicionarParametro("MODULO", "Materiais");
        dto.adicionarParametro("SECRETARIA", montaNomeSecretaria());
        dto.setNomeRelatorio("RELATÓRIO DE ESTOQUE POR GRUPO MATERIAL COMPARATIVO");
        dto.adicionarParametro("NOMERELATORIO", dto.getNomeRelatorio());
        montarClausulaWhere(filtroPatrimonio, dto);
        dto.setApi("administrativo/estoque-por-grupo-material-comparativo/");
        return dto;
    }

    private void montarClausulaWhere(FiltroPatrimonio filtroPatrimonio, RelatorioDTO dto) {
        StringBuilder where = new StringBuilder();
        StringBuilder filtros = new StringBuilder();
        String clausula = " where ";

        if (filtroPatrimonio.getHierarquiaOrc() != null && filtroPatrimonio.getHierarquiaOrc().getCodigo() != null) {
            where.append(clausula).append(" RELATORIO.UNIDADE = ").append(filtroPatrimonio.getHierarquiaOrc().getSubordinada().getId());
            filtros.append("Unidade Orçamentária: ").append(filtroPatrimonio.getHierarquiaOrc().toString()).append(". ").append("\n");
            clausula = " and ";
        }

        if (filtroPatrimonio.getGrupoMaterial() != null) {
            where.append(clausula).append(" RELATORIO.GRUPOMATERIAL = ").append(filtroPatrimonio.getGrupoMaterial().getId());
            filtros.append("Grupo Patrimonial ").append(filtroPatrimonio.getGrupoMaterial().toString()).append(". ").append("\n");
        }
        filtros.append("Data de Referência: ").append(DataUtil.getDataFormatada(filtroPatrimonio.getDataReferencia())).append("\n");
        dto.adicionarParametro("dataReferencia", DataUtil.getDataFormatada(filtroPatrimonio.getDataReferencia()));
        dto.adicionarParametro("condicao", where.toString());
        dto.adicionarParametro("FILTROS", filtros.toString());
    }

    @Override
    protected void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (filtroPatrimonio.getHierarquiaOrc() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo unidade orçamentária é obrigatório!");
        }
        if (filtroPatrimonio.getDataReferencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data de referência é obrigatório!");
        }
        ve.lancarException();
    }

    public FiltroPatrimonio getFiltroPatrimonio() {
        return filtroPatrimonio;
    }

    public void setFiltroPatrimonio(FiltroPatrimonio filtroPatrimonio) {
        this.filtroPatrimonio = filtroPatrimonio;
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrganizacionalOrcamentariaOndeUsuarioLogadoEhGestorMateriais(String parte) {
        return super.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalOrcamentariaOndeUsuarioEhGestorMaterial(parte,
            getSistemaFacade().getDataOperacao(), getSistemaFacade().getUsuarioCorrente());
    }

}
