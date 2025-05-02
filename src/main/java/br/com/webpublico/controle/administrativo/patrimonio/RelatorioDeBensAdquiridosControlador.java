package br.com.webpublico.controle.administrativo.patrimonio;

/**
 * Created by Desenvolvimento on 13/03/2017.
 */

import br.com.webpublico.controle.RelatorioPatrimonioControlador;
import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatoriobensAdquiridos",
        pattern = "/relatorio-bens-adquiridos/",
        viewId = "/faces/administrativo/patrimonio/relatorios/relatoriobensadquiridos.xhtml")})
public class RelatorioDeBensAdquiridosControlador extends RelatorioPatrimonioControlador {

    @URLAction(mappingId = "novoRelatoriobensAdquiridos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoRelatoriobensAdquiridos() {
        limparCampos();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarFiltrosRelatorioBensAdquiridos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            StringBuffer filtros = new StringBuffer();
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "PATRIMÔNIO");
            dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DE BENS ADQUIRIDOS");
            dto.adicionarParametro("MUNICIPIO", montarNomeDoMunicipio());
            dto.adicionarParametro("DATAATUAL", DataUtil.getDataFormatada(getSistemaFacade().getDataOperacao()));
            dto.adicionarParametro("CONDICAO", montarCondicaoRelatorioBensAdquiridos(filtros));
            dto.adicionarParametro("FILTROS", filtros.toString());
            dto.setNomeRelatorio("RELATÓRIO-DE-BENS-ADQUIRIDOS");
            dto.setApi("administrativo/bens-adquiridos/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e){
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void validarFiltrosRelatorioBensAdquiridos() {
        ValidacaoException ve = new ValidacaoException();
        if (this.getHierarquiaOrganizacional() == null
            && this.getHierarquiaOrganizacionalOrcamentaria() == null
            && this.getContrato() == null
            && this.getRequisicaoDeCompra() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe pelo menos um dos campos (Unidade Organizacional, Unidade Orçamentária, Contrato ou Requisição de Compra)");
        }

        if (getDtinicial() != null && getDtFinal() != null) {
            if (getDtinicial().after(getDtFinal())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data inicial deve ser anterior a data final.");
            }
        }
        ve.lancarException();
    }

    public String montarCondicaoRelatorioBensAdquiridos(StringBuffer filtros) {
        StringBuffer sql = new StringBuffer();

        sql.append(" and GRUPO.TIPOBEM = '").append(TipoBem.MOVEIS.name()).append("' ");

        if (getHierarquiaOrganizacional() != null && getHierarquiaOrganizacional().getCodigo() != null) {
            sql.append(" and VWADM.CODIGO LIKE '").append(getHierarquiaOrganizacional().getCodigoSemZerosFinais()).append("%'");
            filtros.append("Unidade Organizacional: ").append(getHierarquiaOrganizacional().toString()).append(". ");
        }
        if (getHierarquiaOrganizacionalOrcamentaria() != null && getHierarquiaOrganizacionalOrcamentaria().getCodigo() != null) {
            sql.append(" and VWORC.CODIGO = '").append(getHierarquiaOrganizacionalOrcamentaria().getCodigo()).append("'");
            filtros.append("Unidade Orçametária: ").append(getHierarquiaOrganizacionalOrcamentaria().toString()).append(". ");
        }

        if (getGrupoBem() != null) {
            sql.append(" and ESTADO.GRUPOBEM_ID = ").append(getGrupoBem().getId()).append(" ");
            filtros.append("Grupo Patrimonial: ").append(getGrupoBem().toString()).append(". ");
        }

        if (getDtinicial() != null) {
            sql.append(" and trunc(BEM.DATAAQUISICAO) >= to_date('").append(DataUtil.getDataFormatada(getDtinicial())).append("','dd/MM/yyyy') ");
            filtros.append("Data de Aquisição Inicial: ").append(DataUtil.getDataFormatada(getDtinicial())).append(". ");
        }

        if (getDtFinal() != null) {
            sql.append(" and trunc(BEM.DATAAQUISICAO) <= to_date('").append(DataUtil.getDataFormatada(getDtFinal())).append("','dd/MM/yyyy') ");
            filtros.append("Data de Aquisição Final: ").append(DataUtil.getDataFormatada(getDtFinal())).append(". ");
        }

        if (getNumeroNotaFiscal() != null && !getNumeroNotaFiscal().trim().isEmpty()) {
            sql.append(" and exists(select 1 from bemnotafiscal bnf ");
            sql.append("            where bnf.bem_id = bem.id ");
            sql.append("              and bnf.notaFiscal = '").append(getNumeroNotaFiscal().trim()).append("' ) ");
            filtros.append("Nº da Nota Fiscal: ").append(getNumeroNotaFiscal().trim()).append(". ");
        }

        if (getDtNota() != null) {
            sql.append(" and exists(select 1 from bemnotafiscal bnf ");
            sql.append("            where bnf.bem_id = bem.id ");
            sql.append("              and to_char(bnf.dataNotaFiscal, 'dd/MM/yyyy') = '").append(DataUtil.getDataFormatada(getDtNota()).trim()).append("' ) ");
            filtros.append("Data da Nota Fiscal: ").append(DataUtil.getDataFormatada(getDtNota()).trim()).append(". ");
        }

        if (getNumeroEmpenho() != null && !getNumeroEmpenho().trim().isEmpty()) {
            sql.append(" and exists(select 1 from bemnotafiscal bnf ");
            sql.append("               inner join bemnotafiscalempenho bnfe on bnfe.bemnotafiscal_id = bnf.id ");
            sql.append("            where bnf.bem_id = bem.id ");
            sql.append("              and bnfe.numeroEmpenho = '").append(getNumeroEmpenho().trim()).append("' ) ");
            filtros.append("Nº de Empenho: ").append(getNumeroEmpenho().trim()).append(". ");
        }

        if (getDtEmpenho() != null) {
            sql.append(" and exists(select 1 from bemnotafiscal bnf ");
            sql.append("               inner join bemnotafiscalempenho bnfe on bnfe.bemnotafiscal_id = bnf.id ");
            sql.append("            where bnf.bem_id = bem.id ");
            sql.append("              and to_char(bnfe.dataNotaEmpenho, 'dd/MM/yyyy') = '").append(DataUtil.getDataFormatada(getDtEmpenho())).append("' ) ");
            filtros.append("Data do Empenho: ").append(DataUtil.getDataFormatada(getDtEmpenho()).trim()).append(". ");
        }

        if (getNumeroLiquidacao() != null && !getNumeroLiquidacao().trim().isEmpty()) {
            sql.append(" and exists(Select 1 " +
                "                     From Itemaquisicao " +
                "                   inner join itemsolicitacaoaquisicao isaq on Itemaquisicao.itemsolicitacaoaquisicao_id = isaq.id  "+
                "                   Inner Join Itemdoctoitemaquisicao Associacao On Associacao.Id = isaq.Itemdoctoitemaquisicao_Id "+
                "                   Inner Join Itemdoctofiscalliquidacao Itemfiscal On Itemfiscal.Id = Associacao.Itemdoctofiscalliquidacao_Id "+
                "                   Inner Join Doctofiscalliquidacao Documento On Documento.Id = Itemfiscal.Doctofiscalliquidacao_Id "+
                "                   Inner Join Liquidacaodoctofiscal On Liquidacaodoctofiscal.Doctofiscalliquidacao_Id = Documento.Id "+
                "                   Inner Join Liquidacao            On Liquidacao.Id = Liquidacaodoctofiscal.Liquidacao_Id "+
                "                     Where Itemaquisicao.Id = Item.Id "+
                "                       and Liquidacao.Numero = '").append(getNumeroLiquidacao().trim()).append("' ) ");
            filtros.append("Nº: Liquidação: ").append(getNumeroLiquidacao().trim()).append(". ");
        }

        if (getDtLiquidacao() != null) {
            sql.append(" and exists(Select 1 " +
                "                     From Itemaquisicao "+
                "                   inner join itemsolicitacaoaquisicao isaq on Itemaquisicao.itemsolicitacaoaquisicao_id = isaq.id  "+
                "                   Inner Join Itemdoctoitemaquisicao Associacao On Associacao.Id = isaq.Itemdoctoitemaquisicao_Id "+
                "                   Inner Join Itemdoctofiscalliquidacao Itemfiscal On Itemfiscal.Id = Associacao.Itemdoctofiscalliquidacao_Id "+
                "                   Inner Join Doctofiscalliquidacao Documento On Documento.Id = Itemfiscal.Doctofiscalliquidacao_Id "+
                "                   Inner Join Liquidacaodoctofiscal On Liquidacaodoctofiscal.Doctofiscalliquidacao_Id = Documento.Id "+
                "                   Inner Join Liquidacao            On Liquidacao.Id = Liquidacaodoctofiscal.Liquidacao_Id "+
                "                     Where Itemaquisicao.Id = Item.Id "+
                "                       and to_char(Liquidacao.dataliquidacao, 'dd/MM/yyyy') = '").append(DataUtil.getDataFormatada(getDtLiquidacao()).trim()).append("' ) ");
            filtros.append("Data de Liquidação: ").append(DataUtil.getDataFormatada(getDtLiquidacao())).append(". ");
        }

        if (getLocalizacao() != null && !getLocalizacao().trim().isEmpty()) {
            sql.append(" and lower(ESTADO.LOCALIZACAO) LIKE '%").append(getLocalizacao().trim().toLowerCase()).append("%' ");
            filtros.append("Localização: ").append(getLocalizacao().trim()).append(". ");
        }

        if (getRequisicaoDeCompra() != null) {
            sql.append(" and exists( select 1 " +
                "                       from requisicaodecompra" +
                "                     where " +
                "                       id = sol.requisicaodecompra_id" +
                "                       and numero = ").append(getRequisicaoDeCompra().getNumero()).append(" ) ");
            filtros.append("N°: Requisição de Compra: ").append(getRequisicaoDeCompra().toStringAutoComplete()).append(". ");
        }

        if (getContrato() != null) {
            sql.append(" and exists( select 1 " +
                "                       from requisicaodecompra" +
                "                     where " +
                "                       id = sol.requisicaodecompra_id" +
                "                       and contrato_id = ").append(getContrato().getId()).append(" ) ");
            filtros.append("Contrato: ").append(getContrato().toStringAutoComplete()).append(". ");
        }
        sql.append(" and aq.situacao = '").append(SituacaoEventoBem.FINALIZADO.name()).append("' ");

        return sql.toString();
    }
}
