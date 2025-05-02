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
import java.io.Serializable;

@ManagedBean(name = "relatorioMaterialPorGrupoControle")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioMateriaisPorGrupo", pattern = "/relatorio-materiais-por-grupo/novo/", viewId = "/faces/administrativo/materiais/relatorios/relatoriomateriaisporgrupo.xhtml"),
})
public class RelatorioMaterialPorGrupoControle extends RelatorioMaterialSuperControlador implements Serializable {

    public RelatorioMaterialPorGrupoControle() {
        super();
        getFiltroMateriais().setDataReferencia(getSistemaFacade().getDataOperacao());
    }

    public void gerarRelatorio() {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("NOMERELATORIO", getNomeRelatorio());
            dto.adicionarParametro("MODULO", "MATERIAIS");
            dto.adicionarParametro("dataReferencia", DataUtil.getDataFormatada(getFiltroMateriais().getDataReferencia()));
            dto.adicionarParametro("condicao", montarCondicao(dto));
            dto.setNomeRelatorio(getNomeRelatorio());
            dto.setApi("administrativo/material-por-grupo/");
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
        return "RELATÓRIO DE MATERIAIS POR GRUPO DE MATERIAL";
    }

    public void validarCampos(){
        ValidacaoException ve = new ValidacaoException();
        if(getFiltroMateriais() == null || getFiltroMateriais().getDataReferencia() == null){
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Data de Referência");
        }
        ve.lancarException();
    }

    private String montarCondicao(RelatorioDTO dto) {
        StringBuilder condicao = new StringBuilder();
        StringBuilder filtros = new StringBuilder();

        if (getFiltroMateriais().getHierarquiaOrcamentaria() != null) {
            condicao.append(" AND LOCALORCAMENTARIO.unidadeOrcamentaria_ID = ").append(getFiltroMateriais().getHierarquiaOrcamentaria().getSubordinada().getId());
            filtros.append(" Unidade Orçamentária: ").append(getFiltroMateriais().getHierarquiaOrcamentaria().toString()).append(". ");
        }

        if (getFiltroMateriais().getGrupoMaterial() != null) {
            condicao.append(" AND Grupo.CODIGO = '").append(getFiltroMateriais().getGrupoMaterial().getCodigo()).append("' ");
            filtros.append(" Grupo Material: ").append(getFiltroMateriais().getGrupoMaterial().toString()).append(". ");
        }

        if (getFiltroMateriais().getMaterial() != null) {
            condicao.append(" and mat.id = ").append(getFiltroMateriais().getMaterial().getId());
            filtros.append(" Material: ").append(getFiltroMateriais().getMaterial().toString());
        }

        filtros.append(" Data de Referência: ").append(DataUtil.getDataFormatada(getFiltroMateriais().getDataReferencia()));

        if (filtros.length() == 0) {
            filtros.append(" Nenhum critério utilizado.");
        }
        dto.adicionarParametro("FILTROS", filtros.toString());

        return condicao.toString();
    }
}
