package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.RegistroLancamentoContabil;
import br.com.webpublico.enums.SituacaoAutoInfracao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Created by FABIO on 29/01/2016.
 */
public class ImpressaoDemonstrativoAcaoFiscal {
    private String semDados = "Não foram encontrados registros para os parâmetros informados.";

    public void gerarRelatorio(String condicao, String cmcInicial, String cmcFinal, String ordemServicoInicial, String ordemServicoFinal, String programacaoInicial, String programacaoFinal, String autoInfracaoInicial, String autoInfracaoFinal) throws NamingException {
        SistemaFacade sistemaFacade = (SistemaFacade) new InitialContext().lookup("java:module/SistemaFacade");
        RelatorioDTO dto = new RelatorioDTO();
        dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
        dto.setNomeParametroBrasao("BRASAO");
        dto.adicionarParametro("condicao", condicao);
        dto.adicionarParametro("SEMDADOS", semDados);
        dto.adicionarParametro("CMCINICIAL", cmcInicial);
        dto.adicionarParametro("CMCFINAL", cmcFinal);
        dto.adicionarParametro("ORDEMSERVICOINICIAL", ordemServicoInicial);
        dto.adicionarParametro("ORDEMSERVICOFINAL", ordemServicoFinal);
        dto.adicionarParametro("PROGRAMACAOINICIAL", programacaoInicial);
        dto.adicionarParametro("PROGRAMACAOFINAL", programacaoFinal);
        dto.adicionarParametro("AUTOINFRACAOINICIAL", autoInfracaoInicial);
        dto.adicionarParametro("AUTOINFRACAOFINAL", autoInfracaoFinal);
        dto.setNomeRelatorio("Demonstrativo do Lançamento Fiscal");
        dto.setApi("tributario/demonstrativo-acao-fiscal/");
        ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
    }

    public void imprimeViaAcao(Long id) {
        try {
            StringBuilder condicao = new StringBuilder();
            condicao.append(" where acao.id = ").append(id.toString())
                .append(" and (reg.situacao != '").append(RegistroLancamentoContabil.Situacao.CANCELADO.name()).append("' and reg.situacao != '").append(RegistroLancamentoContabil.Situacao.ESTORNADO.name()).append("') ");
            gerarRelatorio(condicao.toString(), "", "", "", "", "", "", "", "");
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addError("Atenção!", "Não foi possível gerar o relatório, verifique se a ação fiscal foi devidamente salva antes de ser impressa");
        }
    }

    public void imprimeViaRegistro(Long id) {
        try {
            StringBuilder condicao = new StringBuilder();
            condicao.append(" where reg.id = ").append(id.toString())
                .append(" and (reg.situacao <> '").append(RegistroLancamentoContabil.Situacao.CANCELADO.name()).append("' and reg.situacao <> '")
                .append(RegistroLancamentoContabil.Situacao.ESTORNADO.name()).append("')")
                .append(" and (ai.id is null or ai.situacaoautoinfracao = '").append(SituacaoAutoInfracao.GERADO.name())
                .append("' or ai.situacaoautoinfracao = '").append(SituacaoAutoInfracao.CIENCIA.name())
                .append("' or ai.situacaoautoinfracao = '").append(SituacaoAutoInfracao.REVELIA.name())
                .append("' or ai.id = (select max(id) from autoinfracaofiscal where registro_id = reg.id)) ");
            gerarRelatorio(condicao.toString(), "", "", "", "", "", "", "", "");
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addError("Atenção!", "Não foi possível gerar o relatório, verifique se a ação fiscal foi devidamente salva antes de ser impressa");
        }
    }

}
