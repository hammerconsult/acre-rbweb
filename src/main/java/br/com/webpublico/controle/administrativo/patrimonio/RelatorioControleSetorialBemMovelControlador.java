package br.com.webpublico.controle.administrativo.patrimonio;

import br.com.webpublico.controle.RelatorioPatrimonioControlador;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Created by Desenvolvimento on 13/03/2017.
 */

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioControleSetorialBemMovel",
        pattern = "/relatorio-controle-setorial-patrimonio-bem-movel/",
        viewId = "/faces/administrativo/patrimonio/relatorios/relatoriocontrolesetorialBemMovel.xhtml")
})
public class RelatorioControleSetorialBemMovelControlador extends RelatorioPatrimonioControlador {

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarRelatorioControleSetorialBemMovel();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            StringBuffer filtros = new StringBuffer();
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "PATRIMÔNIO");
            dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DE BENS MÓVEIS PARA CONTROLE SETORIAL DE PATRIMÔNIO");
            dto.adicionarParametro("DATAOPERACAO", DataUtil.getDataFormatada(getDtReferencia()));
            dto.adicionarParametro("CONDICAO", montarCondicaoRelatorioControleSetorialPatrimonioLevantamento(filtros));
            dto.adicionarParametro("FILTROS", filtros.toString());
            dto.adicionarParametro("MUNICIPIO", montarNomeDoMunicipio());
            dto.setNomeRelatorio("RELATÓRIO-BENS-MÓVEIS-PARA-CONTROLE-SETORIAL-DE-PATRIMÔNIO");
            dto.setApi("administrativo/controle-setorial-bem-movel/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e ){
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio("Ocorreu um erro ao gerar o relatório: " + ex.getMessage());
        }
    }

    private String montarCondicaoRelatorioControleSetorialPatrimonioLevantamento(StringBuffer filtros) {
        StringBuffer sql = new StringBuffer();
        if (getHierarquiaOrganizacionalOrcamentaria() != null && getHierarquiaOrganizacionalOrcamentaria().getCodigo() != null) {
            sql.append(" and ORC.codigo = '" + getHierarquiaOrganizacionalOrcamentaria().getCodigo() + "' ");
            filtros.append(" Unidade Orçamentária: ").append(getHierarquiaOrganizacionalOrcamentaria().toString()).append(". ");
        }
        filtros.append("Data de referência: ").append(DataUtil.getDataFormatada(getDtReferencia())).append(". ");
        return sql.toString();
    }

    private void validarRelatorioControleSetorialBemMovel() {
        ValidacaoException ve = new ValidacaoException();
        if (getDtReferencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a data de referência.");
        }
        ve.lancarException();
    }
}
