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
 * Created by Desenvolvimento on 15/02/2017.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoExtratoEstoque", pattern = "/extrato-estoque/", viewId = "/faces/administrativo/materiais/relatorios/extrato-estoque.xhtml")})

public class ExtratoEstoqueControlador extends RelatorioMaterialSuperControlador {

    @URLAction(mappingId = "novoExtratoEstoque", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoRelatorio() {
         limparCampos();
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacionalAdministrativa(String parte) {
        if (getDataFinal() != null) {
            return getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalTipo(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), getDataFinal());
        }
        return Lists.newArrayList();
    }

    @Override
    public List<HierarquiaOrganizacional> completarHierarquiaOrcamentaria(String parte) {
        if (getHierarquiaAdministrativa() != null) {
            return getHierarquiaOrganizacionalFacade().retornaHierarquiaOrcamentariaPorUnidadeAdministrativa(getHierarquiaAdministrativa().getSubordinada(), getDataFinal());
        }
        return getHierarquiaOrganizacionalFacade().filtraPorNivel(parte.trim(), "3", TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), getDataFinal());
    }

    public void gerarExtratoEstoque() {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("NOMERELATORIO", getNomeRelatorio());
            dto.adicionarParametro("MODULO", "Materiais");
            dto.adicionarParametro("MUNICIPIO", montarNomeDoMunicipio());
            dto.adicionarParametro("APRESENTACAO", getApresentacaoRelatorio().name());
            dto.adicionarParametro("dataInicial", DataUtil.getDataFormatada(getDataInicial()));
            dto.adicionarParametro("dataFinal", DataUtil.getDataFormatada(getDataFinal()));
            dto.adicionarParametro("apresentacaoRelatorioDTO", getApresentacaoRelatorio().getToDto());
            dto.adicionarParametro("TipoHierarquiaOrganizacionalDTO", getTipoHierarquiaOrganizacional().getToDto());
            dto.adicionarParametro("condicao", montarCondicoes());
            dto.adicionarParametro("FILTROS", getFiltros().toString());
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("administrativo/extrato-estoque/");
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
        return "Relatório de Extrato de Estoque";
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (getMaterial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo material deve ser informado.");
        }
        if (getDataInicial() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Inicial deve ser informado.");
        }
        if (getDataFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Final deve ser informado.");
        }
        if (getDataInicial() != null && getDataFinal() != null) {
            if (getDataInicial().after(getDataFinal())) {
                ve.adicionarMensagemDeCampoObrigatorio("A data final deve ser posterior a data inicial.");
            }
        }
        ve.lancarException();
    }

    private String montarCondicoes() {
        limparCamposFiltros();
        getFiltros().append("Critérios Utilizados: ");
        if (getHierarquiaAdministrativa() != null) {
            getCondicaoWhere().append(" AND ADM.SUBORDINADA_ID = ").append(getHierarquiaAdministrativa().getSubordinada().getId());
            getFiltros().append("Unidade Administrativa: ").append(getHierarquiaAdministrativa()).append(", ");
        }
        if (getHierarquiaOrcamentaria() != null) {
            getCondicaoWhere().append(" AND ORC.SUBORDINADA_ID = ").append(getHierarquiaOrcamentaria().getSubordinada().getId());
            getFiltros().append("Unidade Orçamentária: ").append(getHierarquiaOrcamentaria()).append(", ");
        }
        if (getTipoEstoque() != null) {
            getCondicaoWhere().append(" AND MOVIMENTOS.tipogrupo =  ").append("'").append(getTipoEstoque().name()).append("'");
            getFiltros().append("Tipo de Estoque: ").append(getTipoEstoque().getDescricao()).append(", ");
        }
        if (getLocalEstoque() != null) {
            getCondicaoWhere().append(" AND MOVIMENTOS.idLocalEstoque = ").append(getLocalEstoque().getId());
            getFiltros().append("Local de Estoque: ").append(getLocalEstoque().getDescricao()).append(", ");
        }
        if (getGrupoMaterial() != null) {
            getCondicaoWhere().append(" AND MOVIMENTOS.GRUPO_ID = ").append(getGrupoMaterial().getId());
            getFiltros().append("Grupo de Material: ").append(getGrupoMaterial()).append(", ");
        }

        if (getMaterial() != null) {
            getCondicaoWhere().append(" AND MOVIMENTOS.MATERIAL_ID = ").append(getMaterial().getId());
        }
        getFiltros().append("Apresentação: ").append(getApresentacaoRelatorio().getDescricao()).append(", ");
        getFiltros().append("Nº de Registro: ").append(getMaterial().getCodigo()).append(", ");
        getFiltros().append("Especificação: ").append(getMaterial().getDescricao()).append(", ");
        getFiltros().append("Grupo Objeto de Compra: ").append(getMaterial().getObjetoCompra().getGrupoObjetoCompra()).append(", ");
        getFiltros().append("Grupo Material: ").append(getMaterial().getGrupo()).append(", ");
        getFiltros().append("Período: De: ").append(DataUtil.getDataFormatada(getDataInicial())).append(" Até: ").append(DataUtil.getDataFormatada(getDataFinal())).append("\n");
        return getCondicaoWhere().toString();
    }
}
