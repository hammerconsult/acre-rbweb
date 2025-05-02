package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controlerelatorio.administrativo.RelatorioMaterialSuperControlador;
import br.com.webpublico.entidades.InventarioEstoque;
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
 * Created by Desenvolvimento on 09/02/2017.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioInventarioEstoque", pattern = "/inventario-estoque-material/", viewId = "/faces/administrativo/materiais/relatorios/relatorio-inventario-estoque.xhtml")
})
public class RelatorioInventarioEstoqueControlador extends RelatorioMaterialSuperControlador {

    public RelatorioInventarioEstoqueControlador() {
        super();
    }

    public void gerarRelatorioConferenciaInventario(InventarioEstoque inventarioEstoque) {
        try {
            if (inventarioEstoque != null) {
                String nomeRelatorio = "RELATÓRIO DE INVENTÁRIO DE ESTOQUE";
                RelatorioDTO dto = new RelatorioDTO();
                dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
                dto.setNomeParametroBrasao("BRASAO");
                dto.adicionarParametro("MODULO", "Materiais");
                dto.adicionarParametro("NOMERELATORIO", nomeRelatorio);
                dto.adicionarParametro("FILTROS", "");
                dto.adicionarParametro("DATAOPERACAO", getSistemaFacade().getDataOperacao());
                dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome());
                dto.adicionarParametro("condicao", " WHERE IV.ID = " + inventarioEstoque.getId());
                dto.setNomeRelatorio(nomeRelatorio);
                dto.setApi("administrativo/inventario-local-estoque/");
                ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
                FacesUtil.addMensagemRelatorioSegundoPlano();
            }
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar o relatório: ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public void imprimir() {
        super.imprimir("RelatorioInventarioLocalEstoque");
    }

    public void gerarRelatorioInventarioEstoque() {
        try {
            validarCampos();
            String nomeRelatorio = "Relatório de Inventário de Estoque";
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "Materiais");
            dto.adicionarParametro("NOMERELATORIO", nomeRelatorio);
            dto.adicionarParametro("APRESENTACAO", getApresentacaoRelatorio().name());
            dto.adicionarParametro("TIPOHIERARQUIA", getTipoHierarquiaOrganizacional().name());
            dto.adicionarParametro("condicao", montarCondicaoEFiltros());
            dto.adicionarParametro("dataReferencia", DataUtil.getDataFormatada(getDataReferencia()));
            dto.adicionarParametro("FILTROS", getFiltros().toString());
            dto.setNomeRelatorio(nomeRelatorio);
            dto.setApi("administrativo/inventario-estoque/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar o relatório: ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }


    private String montarCondicaoEFiltros() {
        limparCamposFiltros();
        getFiltros().append("Critérios Utilizados: ").append("\n");
        getFiltros().append("Apresentação:  ").append(getApresentacaoRelatorio().getDescricao()).append(" - ").append(getTipoHierarquiaOrganizacional().getDescricao()).append("\n");
        if (getHierarquiaAdministrativa() != null) {
            getCondicaoWhere().append(" and adm.subordinada_id = ").append(getHierarquiaAdministrativa().getSubordinada().getId());
            getFiltros().append("Unidade Administrativa: ").append(getHierarquiaAdministrativa()).append("\n");
        }
        if (getHierarquiaOrcamentaria() != null) {
            getCondicaoWhere().append(" and orc.subordinada_id = ").append(getHierarquiaOrcamentaria().getSubordinada().getId());
            getFiltros().append("Unidade Orçamentária: ").append(getHierarquiaOrcamentaria()).append("\n");
        }
        if (getTipoEstoque() != null) {
            getCondicaoWhere().append(" and l.tipoestoque = '").append(getTipoEstoque().name()).append("'");
            getFiltros().append("Tipo de Estoque: ").append(getTipoEstoque().getDescricao()).append("\n");
        }
        if (getLocalEstoque() != null) {
            getCondicaoWhere().append(" and l.id = ").append(getLocalEstoque().getId());
            getFiltros().append("Local de Estoque: ").append(getLocalEstoque()).append("\n");
        }
        if (getGrupoMaterial() != null) {
            getCondicaoWhere().append(" and gp.id = ").append(getGrupoMaterial().getId());
            getFiltros().append("Grupo Material: ").append(getGrupoMaterial()).append("\n");
        }
        if (getMaterial() != null) {
            getCondicaoWhere().append(" and m.id = ").append(getMaterial().getId());
            getFiltros().append("Material: ").append(getMaterial()).append("\n");
        }
        if (getDataReferencia() != null) {
            getFiltros().append("Data de Referência: ").append(DataUtil.getDataFormatada(getDataReferencia())).append("\n");
        }
        return getCondicaoWhere().toString();
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (getDataReferencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a data de referência.");
        }
        ve.lancarException();
    }
}
