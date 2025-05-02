package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controlerelatorio.administrativo.RelatorioMaterialSuperControlador;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * @author Alex
 * @since 09/02/2017 12:01
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoEstoqueMaterial", pattern = "/relatorio-estoque-por-grupo-material/", viewId = "/faces/administrativo/materiais/relatorios/estoque-por-grupo-material.xhtml")})

public class RelatorioEstoqueGrupoMaterialControlador extends RelatorioMaterialSuperControlador {

    public void gerarRelatorio() {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome());
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "Materiais");
            dto.adicionarParametro("MUNICIPIO", montarNomeDoMunicipio());
            dto.adicionarParametro("NOMERELATORIO", getNomeRelatorio());
            dto.adicionarParametro("APRESENTACAO", getFiltroMateriais().getApresentacaoRelatorio().name());
            dto.adicionarParametro("dataReferencia", DataUtil.getDataFormatada(getFiltroMateriais().getDataReferencia()));
            dto.adicionarParametro("condicao", montarCondicoesEFiltros(dto));
            dto.adicionarParametro("apresentacao", getFiltroMateriais().getApresentacaoRelatorio().getToDto());
            dto.adicionarParametro("tipoHierarquia", getFiltroMateriais().getTipoHierarquiaOrganizacional().getToDto());
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("administrativo/estoque-grupo-material/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
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
        return "RELATÓRIO DE ESTOQUE POR GRUPO DE MATERIAL";
    }

    private String montarCondicoesEFiltros(RelatorioDTO dto) {

        StringBuilder condicoes = new StringBuilder();
        StringBuilder filtros = new StringBuilder();
        filtros.append("Filtros: ");
        if (getFiltroMateriais().getDataReferencia() != null) {
            filtros.append(" Data de Referência: ").append(DataUtil.getDataFormatada(getFiltroMateriais().getDataReferencia())).append(",");
        }
        if (getFiltroMateriais().getHierarquiaAdministrativa() != null) {
            condicoes.append(" AND ADM.SUBORDINADA_ID = ").append(getFiltroMateriais().getHierarquiaAdministrativa().getSubordinada().getId());
            filtros.append(" Unidade Administrativa: ").append(getFiltroMateriais().getHierarquiaAdministrativa().getDescricao()).append(",");
        }
        if (getFiltroMateriais().getHierarquiaOrcamentaria() != null) {
            condicoes.append(" AND ORC.SUBORDINADA_ID = ").append(getFiltroMateriais().getHierarquiaOrcamentaria().getSubordinada().getId());
            filtros.append(" Unidade Orçamentária: ").append(getFiltroMateriais().getHierarquiaOrcamentaria().getDescricao()).append(",");
        }
        if (getFiltroMateriais().getTipoEstoque() != null) {
            condicoes.append(" AND L.TIPOESTOQUE =  ").append("'" + getFiltroMateriais().getTipoEstoque().name() + "'");
            filtros.append(" Tipo de Estoque: ").append(getFiltroMateriais().getTipoEstoque().getDescricao()).append(",");
        }
        if (getFiltroMateriais().getLocalEstoque() != null) {
            condicoes.append(" AND L.ID = ").append(getFiltroMateriais().getLocalEstoque().getId());
            filtros.append(" Local de Estoque: ").append(getFiltroMateriais().getLocalEstoque().getDescricao()).append(",");
        }
        if (getFiltroMateriais().getGrupoMaterial() != null) {
            condicoes.append(" AND GP.ID = ").append(getFiltroMateriais().getGrupoMaterial().getId());
            filtros.append(" Grupo de Material: ").append(getFiltroMateriais().getGrupoMaterial()).append(",");
        }
        dto.adicionarParametro("FILTROS", filtros.toString());
        return condicoes.toString();
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (getDataReferencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Referência deve ser informado.");
        }
        ve.lancarException();
    }
}
