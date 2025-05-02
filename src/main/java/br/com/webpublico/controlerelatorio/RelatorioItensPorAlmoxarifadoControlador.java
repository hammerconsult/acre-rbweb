package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controlerelatorio.administrativo.RelatorioMaterialSuperControlador;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;

/**
 * @author Julio Cesar
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-item-por-localestoque", pattern = "/relatorios/itens-por-almoxarifado/", viewId = "/faces/administrativo/relatorios/relatorioitensporalmoxarifado.xhtml"),
})
@ManagedBean
public class RelatorioItensPorAlmoxarifadoControlador extends RelatorioMaterialSuperControlador {

    @URLAction(mappingId = "relatorio-item-por-localestoque", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        limparCampos();
    }

    public void gerarRelatorio() {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "MATERIAIS");
            dto.adicionarParametro("NOMERELATORIO", getNomeRelatorio());
            dto.adicionarParametro("dataInicial", DataUtil.getDataFormatada(getFiltroMateriais().getDataInicial()));
            dto.adicionarParametro("dataFinal", DataUtil.getDataFormatada(getFiltroMateriais().getDataFinal()));
            dto.adicionarParametro("condicao", montarCondicao(dto));
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("administrativo/itens-por-almoxarifado/");
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

    @Override
    public List<HierarquiaOrganizacional> completarHierarquiaOrcamentaria(String parte) {
        if (getFiltroMateriais().getDataFinal() != null) {
            if (getHierarquiaAdministrativa() != null) {
                return getHierarquiaOrganizacionalFacade().retornaHierarquiaOrcamentariaPorUnidadeAdministrativa(getHierarquiaAdministrativa().getSubordinada(), getFiltroMateriais().getDataFinal());
            }
            return getHierarquiaOrganizacionalFacade().filtraPorNivel(parte.trim(), "3", TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), getFiltroMateriais().getDataFinal());
        }
        return Lists.newArrayList();
    }

    private String getNomeRelatorio() {
        return "RELATÓRIO DE ITENS POR ALMOXARIFADO";
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (getFiltroMateriais().getDataInicial() == null && getFiltroMateriais().getDataFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Período deve ser informado.");
        }
        ve.lancarException();
        if (getDataInicial().after(getDataFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data inicial deve ser anterior ou igual a data final.");
        }
        ve.lancarException();
    }

    public String montarCondicao(RelatorioDTO dto) {
        StringBuilder condicao = new StringBuilder();
        StringBuilder filtros = new StringBuilder();
        if (getFiltroMateriais().getDataInicial() != null) {
            filtros.append("Período de ").append(DataUtil.getDataFormatada(getFiltroMateriais().getDataInicial()));
        }

        if (getFiltroMateriais().getDataFinal() != null) {
            filtros.append(" até ").append(DataUtil.getDataFormatada(getFiltroMateriais().getDataFinal())).append(", ");
        }

        if (getFiltroMateriais().getHierarquiaOrcamentaria() != null) {
            condicao.append(" AND VW_ORC.SUBORDINADA_ID = ").append(getFiltroMateriais().getHierarquiaOrcamentaria().getSubordinada().getId());
            filtros.append(" Unidade Orçamentária: ").append(getFiltroMateriais().getHierarquiaOrcamentaria().toString()).append(", ");
            dto.adicionarParametro("SECRETARIA", getFiltroMateriais().getHierarquiaOrcamentaria().getDescricao().toUpperCase());
        }

        if (getFiltroMateriais().getLocalEstoque() != null) {
            condicao.append(" AND  local.id = ").append(getFiltroMateriais().getLocalEstoque().getId());
            filtros.append("Local de Estoque ").append(getFiltroMateriais().getLocalEstoque().toString()).append(", ");
        }

        if (getFiltroMateriais().getLicitacao() != null) {
            condicao.append(" AND LIC.ID = ").append(getFiltroMateriais().getLicitacao().getId());
            filtros.append("Licitação: ").append(getFiltroMateriais().getLicitacao().toString()).append(", ");
        }

        if (getFiltroMateriais().getGrupoMaterial() != null) {
            condicao.append(" AND GRUPO.ID = ").append(getFiltroMateriais().getGrupoMaterial().getId());
            filtros.append("Grupo Material: ").append(getFiltroMateriais().getGrupoMaterial().toString()).append(", ");
        }

        if (getFiltroMateriais().getMaterial() != null) {
            condicao.append(" AND MAT.ID = ").append(getFiltroMateriais().getMaterial().getId());
            filtros.append("Material: ").append(getFiltroMateriais().getMaterial().getCodigo()).append(", ");
        }
        dto.adicionarParametro("FILTROS", filtros.toString());
        return condicao.toString();
    }
}
