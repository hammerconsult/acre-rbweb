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
 * @since 10/02/2017 14:13
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoLevantamentoMaterial", pattern = "/relatorio-levantamento-por-grupo-material/", viewId = "/faces/administrativo/materiais/relatorios/levantamento-por-grupo-material.xhtml")})
public class RelatorioLevantamentoGrupoMaterialControlador extends RelatorioMaterialSuperControlador {

    public void gerarEstoqueGrupoMaterial() {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome());
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "Materiais");
            dto.adicionarParametro("MUNICIPIO", montarNomeDoMunicipio());
            dto.adicionarParametro("NOMERELATORIO", getNomeRelatorio());
            dto.adicionarParametro("APRESENTACAO", getApresentacaoRelatorio().name());
            dto.adicionarParametro("condicao", montarCondicoes());
            dto.adicionarParametro("dataReferencia", DataUtil.getDataFormatada(getDataReferencia()));
            dto.adicionarParametro("apresentacao", getApresentacaoRelatorio().getToDto());
            dto.adicionarParametro("tipoHierarquia", getTipoHierarquiaOrganizacional().getToDto());
            dto.adicionarParametro("FILTROS", getFiltros().toString());
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("administrativo/levantamento-grupo-material/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio("Ocorreu um erro ao gerar o relatório: " + ex.getMessage());
        }
    }

    private String getNomeRelatorio() {
        return "RELATÓRIO DE LEVANTAMENTO POR GRUPO MATERIAL";
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (getDataReferencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Referência é obrigatório!");
        }
        ve.lancarException();
    }

    public String montarCondicoes() {

        StringBuilder condicoes = new StringBuilder();
        getFiltros().setLength(0);
        getFiltros().append("Filtros: ");
        if (getHierarquiaAdministrativa() != null) {
            condicoes.append(" AND ADM.SUBORDINADA_ID = ").append(getHierarquiaAdministrativa().getSubordinada().getId());
            getFiltros().append(" Unidade Administrativa: ").append(getHierarquiaAdministrativa().getSubordinada().getDescricao());
            getFiltros().append(",");
        }
        if (getHierarquiaOrcamentaria() != null) {
            condicoes.append(" AND ORC.SUBORDINADA_ID = ").append(getHierarquiaOrcamentaria().getSubordinada().getId());
            getFiltros().append(" Unidade Orçamentária: ").append(getHierarquiaOrcamentaria().getSubordinada().getDescricao());
            getFiltros().append(",");
        }
        if (getTipoEstoque() != null) {
            condicoes.append(" AND L.TIPOESTOQUE =  ").append("'"+getTipoEstoque().name()+"'");
            getFiltros().append(" Tipo de Estoque: ").append(getTipoEstoque().getDescricao());
            getFiltros().append(",");
        }
        if (getLocalEstoque() != null) {
            condicoes.append(" AND L.ID = ").append(getLocalEstoque().getId());
            getFiltros().append(" Local de Estoque: ").append(getLocalEstoque().getDescricao());
            getFiltros().append(",");
        }
        if (getGrupoMaterial() != null) {
            condicoes.append(" AND GP.ID = ").append(getGrupoMaterial().getId());
            getFiltros().append(" Grupo de Material: ").append(getGrupoMaterial());
            getFiltros().append(",");
        }
        if (getDataReferencia() != null) {
            getFiltros().append(" Data de Referência: ").append(DataUtil.getDataFormatada(getDataReferencia()));
        }
        return condicoes.toString();
    }

}
