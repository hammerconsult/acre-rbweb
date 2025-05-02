package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controlerelatorio.administrativo.RelatorioMaterialSuperControlador;
import br.com.webpublico.entidades.EfetivacaoLevantamentoEstoque;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Created by Desenvolvimento on 13/02/2017.
 */

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioLevantamentoEstoque", pattern = "/relatorio-levantamento-estoque/", viewId = "/faces/administrativo/materiais/relatorios/relatorio-levantamento-estoque.xhtml")
})
public class RelatorioLevantamentoEstoque extends RelatorioMaterialSuperControlador {

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private EfetivacaoLevantamentoEstoque efetivacaoLevantamentoEstoque;

    public RelatorioLevantamentoEstoque() {
        super();
        efetivacaoLevantamentoEstoque = null;
    }

    public void gerarRelatorio(EfetivacaoLevantamentoEstoque selecionado) {
        efetivacaoLevantamentoEstoque = selecionado;
        gerarRelatorio();
    }

    public void gerarRelatorio() {
        try {
            validarCampos();
            String nomeRelatorio = "Relatório de Levantamento de Estoque";
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "Materiais");
            dto.adicionarParametro("NOMERELATORIO", nomeRelatorio);
            dto.adicionarParametro("APRESENTACAO", getApresentacaoRelatorio().name());
            dto.adicionarParametro("TIPOHIERARQUIA", getTipoHierarquiaOrganizacional().name());
            dto.adicionarParametro("dataReferencia", DataUtil.getDataFormatada(getDataReferencia()));
            dto.adicionarParametro("condicao", montarCondicao());
            dto.adicionarParametro("FILTROS", getFiltros().toString());
            if (efetivacaoLevantamentoEstoque != null) {
                dto.adicionarParametro("idEfetivacaoLevantamento", efetivacaoLevantamentoEstoque.getId());
            }
            dto.setNomeRelatorio(nomeRelatorio);
            dto.setApi("administrativo/levantamento-estoque/");
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

    public String montarCondicao() {
        limparCamposFiltros();
        if (efetivacaoLevantamentoEstoque ==null) {
            getFiltros().append("Critérios Utilizados: ").append("\n");
            getFiltros().append("Apresentação:  ").append(getApresentacaoRelatorio().getDescricao()).append(" - ").append(getTipoHierarquiaOrganizacional().getDescricao()).append("\n");
            getFiltros().append("Data de Referência: ").append(DataUtil.getDataFormatada(getDataReferencia())).append("\n");
        }

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
        if (efetivacaoLevantamentoEstoque != null) {
            getFiltros().append("Efetivação do Levantamento").append("\n");
            getFiltros().append("Código: ").append(efetivacaoLevantamentoEstoque.getNumero()).append("\n");
            getFiltros().append("Data: ").append(DataUtil.getDataFormatada(efetivacaoLevantamentoEstoque.getDataEntrada())).append("\n");
            getFiltros().append("Responsável: ").append(efetivacaoLevantamentoEstoque.getResponsavel()).append("\n");
            HierarquiaOrganizacional hierarquiaDaUnidade = hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), efetivacaoLevantamentoEstoque.getUnidadeOrganizacional(), getSistemaFacade().getDataOperacao());
            getFiltros().append("Unidade: ").append(hierarquiaDaUnidade !=null ? hierarquiaDaUnidade : efetivacaoLevantamentoEstoque.getUnidadeOrganizacional()).append("\n");

        }
        return getCondicaoWhere().toString();
    }


    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (getDataReferencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a data de referência.");
        }
        ve.lancarException();
    }
}
