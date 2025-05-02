/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidadesauxiliares.ImpressaoDemonstrativoAcaoFiscal;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Limpavel;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;

/**
 * @author Claudio
 */
@ManagedBean(name = "demonstrativoAcaoFiscalControlador")
@SessionScoped
@URLMappings(mappings = {
    @URLMapping(id = "gerarRelatorioFiscal", pattern = "/demonstrativo-lancamento-fiscal/", viewId = "/faces/tributario/fiscalizacao/demonstrativo/demonstrativo.xhtml"),
})
public class DemonstrativoAcaoFiscalControlador implements Serializable {

    @Limpavel(Limpavel.STRING_VAZIA)
    private String cmcInicial;
    @Limpavel(Limpavel.STRING_VAZIA)
    private String cmcFinal;
    @Limpavel(Limpavel.STRING_VAZIA)
    private String ordemServicoInicial;
    @Limpavel(Limpavel.STRING_VAZIA)
    private String ordemServicoFinal;
    @Limpavel(Limpavel.STRING_VAZIA)
    private String programacaoInicial;
    @Limpavel(Limpavel.STRING_VAZIA)
    private String programacaoFinal;
    @Limpavel(Limpavel.STRING_VAZIA)
    private String autoInfracaoInicial;
    @Limpavel(Limpavel.STRING_VAZIA)
    private String autoInfracaoFinal;
    @Limpavel(Limpavel.STRING_VAZIA)
    private String anoOrdemServico;
    @Limpavel(Limpavel.STRING_VAZIA)
    private String anoAutoInfracao;
    private TipoCadastroTributario tipoCadastroTributario;

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    public String getAutoInfracaoFinal() {
        return autoInfracaoFinal;
    }

    public void setAutoInfracaoFinal(String autoInfracaoFinal) {
        this.autoInfracaoFinal = autoInfracaoFinal;
    }

    public String getAutoInfracaoInicial() {
        return autoInfracaoInicial;
    }

    public void setAutoInfracaoInicial(String autoInfracaoInicial) {
        this.autoInfracaoInicial = autoInfracaoInicial;
    }

    public String getCmcFinal() {
        return cmcFinal;
    }

    public void setCmcFinal(String cmcFinal) {
        this.cmcFinal = cmcFinal;
    }

    public String getCmcInicial() {
        return cmcInicial;
    }

    public void setCmcInicial(String cmcInicial) {
        this.cmcInicial = cmcInicial;
    }

    public String getOrdemServicoFinal() {
        return ordemServicoFinal;
    }

    public void setOrdemServicoFinal(String ordemServicoFinal) {
        this.ordemServicoFinal = ordemServicoFinal;
    }

    public String getOrdemServicoInicial() {
        return ordemServicoInicial;
    }

    public void setOrdemServicoInicial(String ordemServicoInicial) {
        this.ordemServicoInicial = ordemServicoInicial;
    }

    public String getProgramacaoFinal() {
        return programacaoFinal;
    }

    public void setProgramacaoFinal(String programacaoFinal) {
        this.programacaoFinal = programacaoFinal;
    }

    public String getProgramacaoInicial() {
        return programacaoInicial;
    }

    public void setProgramacaoInicial(String programacaoInicial) {
        this.programacaoInicial = programacaoInicial;
    }

    public String getAnoAutoInfracao() {
        return anoAutoInfracao;
    }

    public void setAnoAutoInfracao(String anoAutoInfracao) {
        this.anoAutoInfracao = anoAutoInfracao;
    }

    public String getAnoOrdemServico() {
        return anoOrdemServico;
    }

    public void setAnoOrdemServico(String anoOrdemServico) {
        this.anoOrdemServico = anoOrdemServico;
    }

    public String montarCondicao() {
        StringBuilder condicao = new StringBuilder();
        String juncao = " where ";
        //cadastro economico
        if (cmcInicial != null && !cmcInicial.isEmpty()) {
            condicao.append(juncao).append(" to_number(ce.inscricaocadastral) >= ").append(cmcInicial);
            juncao = " and ";
        }

        if (cmcFinal != null && !cmcFinal.isEmpty()) {
            condicao.append(juncao).append(" to_number(ce.inscricaocadastral) <= ").append(cmcFinal);
            juncao = " and ";
        }
        //programacao fiscal
        if (programacaoInicial != null && !programacaoInicial.isEmpty()) {
            condicao.append(juncao).append(" to_number(pro.numero) >= ").append(programacaoInicial);
            juncao = " and ";
        }

        if (programacaoFinal != null && !programacaoFinal.isEmpty()) {
            condicao.append(juncao).append(" to_number(pro.numero) <= ").append(programacaoFinal);
            juncao = " and ";
        }
        //ordem de serviço
        if (ordemServicoInicial != null && !ordemServicoInicial.isEmpty()) {
            condicao.append(juncao).append(" to_number(acao.ordemservico) >= ").append(ordemServicoInicial);
            juncao = " and ";
        }
        if (ordemServicoFinal != null && !ordemServicoFinal.isEmpty()) {
            condicao.append(juncao).append(" to_number(acao.ordemservico) <= ").append(ordemServicoFinal);
            juncao = " and ";
        }
        //auto infração fiscal
        if (autoInfracaoInicial != null && !autoInfracaoInicial.isEmpty()) {
            condicao.append(juncao).append(" to_number(ai.numero) >= ").append(autoInfracaoInicial);
            juncao = " and ";
        }

        if (autoInfracaoFinal != null && !autoInfracaoFinal.isEmpty()) {
            condicao.append(juncao).append(" to_number(ai.numero) <= ").append(autoInfracaoFinal);
            juncao = " and ";
        }
        //ano da ordem de serviço
        if (anoOrdemServico != null && !anoOrdemServico.isEmpty()) {
            condicao.append(juncao).append(" to_number(acao.ano) = ").append(anoOrdemServico);
            juncao = " and ";
        }
        //ano do auto de infração
        if (anoAutoInfracao != null && !anoAutoInfracao.isEmpty()) {
            condicao.append(juncao).append(" to_number(ai.ano) = ").append(anoAutoInfracao);
            juncao = " and ";
        }
        return condicao.toString();
    }

    @URLAction(mappingId = "gerarRelatorioFiscal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        Util.limparCampos(this);
        tipoCadastroTributario = null;
    }

    public void gerarRelatorio() {
        try {
            validarCampos();
            new ImpressaoDemonstrativoAcaoFiscal().gerarRelatorio(montarCondicao(), cmcInicial, cmcFinal, ordemServicoInicial, ordemServicoFinal, programacaoInicial, programacaoFinal, autoInfracaoInicial, autoInfracaoFinal);
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if ((ordemServicoInicial != null || ordemServicoFinal != null) && anoOrdemServico == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano da Ordem de Serviço deve ser informado.");
        }
        if ((autoInfracaoInicial != null || autoInfracaoFinal != null) && anoAutoInfracao == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano do Auto de Infração deve ser informado.");
        }
        ve.lancarException();
    }
}
