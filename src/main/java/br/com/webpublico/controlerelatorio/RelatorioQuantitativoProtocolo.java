/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoRelatorioQuantitativoProtocolo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.UsuarioSistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "relatorioQuantitativoProtocolo")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "limparCamposRelatorioQuantitativoExpedido", pattern = "/protocolo/relatorio/quantitativo-de-protocolos-expedidos/", viewId = "/faces/tributario/cadastromunicipal/relatorio/quantitativoprotocolocriado.xhtml"),
    @URLMapping(id = "limparCamposRelatorioQuantitativoRecebido", pattern = "/protocolo/relatorio/quantitativo-de-protocolos-recebidos/", viewId = "/faces/tributario/cadastromunicipal/relatorio/quantitativoprotocolorecebido.xhtml"),
    @URLMapping(id = "limparCamposRelatorioQuantitativoEncaminhado", pattern = "/protocolo/relatorio/quantitativo-de-protocolos-encaminhados/", viewId = "/faces/tributario/cadastromunicipal/relatorio/quantitativoprotocoloencaminhado.xhtml"),
    @URLMapping(id = "limparCamposRelatorioQuantitativoArquivado", pattern = "/protocolo/relatorio/quantitativo-de-protocolos-arquivados/", viewId = "/faces/tributario/cadastromunicipal/relatorio/quantitativoprotocoloarquivado.xhtml"),
    @URLMapping(id = "limparCamposRelatorioQuantitativoFinalizado", pattern = "/protocolo/relatorio/quantitativo-de-protocolos-finalizados/", viewId = "/faces/tributario/cadastromunicipal/relatorio/quantitativoprotocoloFinalizado.xhtml"),
    @URLMapping(id = "limparCamposRelatorioQuantitativoAceitos", pattern = "/protocolo/relatorio/quantitativo-de-protocolos-aceitos/", viewId = "/faces/tributario/cadastromunicipal/relatorio/quantitativoprotocoloaceitos.xhtml")
})
public class RelatorioQuantitativoProtocolo implements Serializable {

    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private Date dataInicial;
    private Date dataFinal;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private TipoRelatorioQuantitativoProtocolo tipoRelatorio;

    public RelatorioQuantitativoProtocolo() {
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public TipoRelatorioQuantitativoProtocolo getTipoRelatorio() {
        return tipoRelatorio;
    }

    public void setTipoRelatorio(TipoRelatorioQuantitativoProtocolo tipoRelatorio) {
        this.tipoRelatorio = tipoRelatorio;
    }

    @URLAction(mappingId = "limparCamposRelatorioQuantitativoExpedido", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparRelatorioQuantitativoExpedido() {
        limparCampos();
        tipoRelatorio = TipoRelatorioQuantitativoProtocolo.EXPEDIDOS;
    }

    @URLAction(mappingId = "limparCamposRelatorioQuantitativoRecebido", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparRelatorioQuantitativoRecebido() {
        limparCampos();
        tipoRelatorio = TipoRelatorioQuantitativoProtocolo.RECEBIDOS;
    }

    @URLAction(mappingId = "limparCamposRelatorioQuantitativoEncaminhado", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparRelatorioQuantitativoEncaminhado() {
        limparCampos();
        tipoRelatorio = TipoRelatorioQuantitativoProtocolo.ENCAMINHADOS;
    }

    @URLAction(mappingId = "limparCamposRelatorioQuantitativoArquivado", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparRelatorioQuantitativoArquivado() {
        limparCampos();
        tipoRelatorio = TipoRelatorioQuantitativoProtocolo.ARQUIVADOS;
    }

    @URLAction(mappingId = "limparCamposRelatorioQuantitativoFinalizado", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparRelatorioQuantitativoFinalizado() {
        limparCampos();
        tipoRelatorio = TipoRelatorioQuantitativoProtocolo.FINALIZADOS;
    }

    @URLAction(mappingId = "limparCamposRelatorioQuantitativoAceitos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparRelatorioQuantitativoAceitos() {
        limparCampos();
        tipoRelatorio = TipoRelatorioQuantitativoProtocolo.ACEITOS;
    }

    private void limparCampos() {
        dataInicial = null;
        dataFinal = null;
        hierarquiaOrganizacional = null;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public List<HierarquiaOrganizacional> completarHierarquiasAdministrativas(String filtro) {
        Date dataOperacao = sistemaFacade.getDataOperacao();
        if (dataFinal != null) {
            dataOperacao = dataFinal;

        }
        return usuarioSistemaFacade.filtrandoHierarquiaOrganizacionalAdministrativaComUsuarios(filtro, dataOperacao);
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (dataInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Data Inicial!");
        }
        if (dataFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Data Final!");
        }
        if (hierarquiaOrganizacional == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Unidade Organizacional!");
        }
        ve.lancarException();
        if (dataInicial.after(dataFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data Inicial deve ser anterior ou igual a Data Final!");
        }
        ve.lancarException();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            HierarquiaOrganizacional hierarquiaNoPeriodo = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade( dataFinal, hierarquiaOrganizacional.getSubordinada(), TipoHierarquiaOrganizacional.ADMINISTRATIVA);
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("DESCRICAO_HIERARQUIA", hierarquiaNoPeriodo.getDescricao());
            dto.adicionarParametro("ID_UNIDADE", hierarquiaOrganizacional.getSubordinada().getId());
            dto.adicionarParametro("DATAINICIAL", DataUtil.getDataFormatada(dataInicial));
            dto.adicionarParametro("DATAFINAL", DataUtil.getDataFormatada(dataFinal));
            dto.adicionarParametro("tipoRelatorio", tipoRelatorio.getToDto());
            dto.setNomeRelatorio("Relatório Quantitativo de Protocolos " + tipoRelatorio.getDescricao());
            dto.setApi("administrativo/quantitativo-protocolo/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }
}
