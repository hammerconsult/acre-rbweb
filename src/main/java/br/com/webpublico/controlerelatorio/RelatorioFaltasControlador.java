/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.UtilRH;
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

@ManagedBean(name = "relatorioFaltasControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorioFaltas", pattern = "/relatorio-de-faltas/", viewId = "/faces/rh/relatorios/relatoriofaltas.xhtml")
})
public class RelatorioFaltasControlador extends AbstractReport implements Serializable {

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacadeNovo;
    private HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada;
    private Date dataInicial;
    private Date dataFinal;
    private VinculoFP vinculoFP;
    private String tipoDeFiltroSelecionado;

    public String getTipoDeFiltroSelecionado() {
        return tipoDeFiltroSelecionado;
    }

    public void setTipoDeFiltroSelecionado(String tipoDeFiltroSelecionado) {
        this.tipoDeFiltroSelecionado = tipoDeFiltroSelecionado;
    }

    public RelatorioFaltasControlador() {
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalSelecionada() {
        return hierarquiaOrganizacionalSelecionada;
    }

    public void setHierarquiaOrganizacionalSelecionada(HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada) {
        this.hierarquiaOrganizacionalSelecionada = hierarquiaOrganizacionalSelecionada;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrganizacional(String parte) {
        return hierarquiaOrganizacionalFacadeNovo.filtrandoHierarquiaOrganizacionalTipo(parte.trim(), "ADMINISTRATIVA", getSistemaFacade().getDataOperacao());
    }

    @URLAction(mappingId = "relatorioFaltas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
        hierarquiaOrganizacionalSelecionada = null;
        dataInicial = null;
        dataFinal = null;
        tipoDeFiltroSelecionado = "ORGAO";
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (tipoDeFiltroSelecionado.equals("ORGAO") && hierarquiaOrganizacionalSelecionada == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Órgão deve ser informado.");
        }
        if (tipoDeFiltroSelecionado.equals("SERVIDOR") && vinculoFP == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Servidor deve ser informado.");
        }
        if (dataInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Inicial deve ser informado.");
        }
        if (dataFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Final deve ser informado.");
        }
        ve.lancarException();
        if (dataFinal.before(dataInicial)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo data inicial deve ser anterior ao campo data final.");
        }
        ve.lancarException();
    }

    public void gerarRelatorio(String tipoRelatorio) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorio));
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "Recursos Humanos");
            dto.adicionarParametro("SECRETARIA", "DEPARTAMENTO DE RECURSOS HUMANOS");
            dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DE FALTAS");
            dto.adicionarParametro("DATAINICIAL", DataUtil.getDataFormatada(dataInicial));
            dto.adicionarParametro("DATAFINAL", DataUtil.getDataFormatada(dataFinal));
            dto.adicionarParametro("DATAOPERACAO", DataUtil.getDataFormatada(UtilRH.getDataOperacao()));
            dto.adicionarParametro("CODIGO_HO_SERVIDOR", getComplementoQuery());
            dto.setNomeRelatorio("RELATÓRIO-DE-FALTAS");
            dto.setApi("rh/faltas/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
            logger.error("Erro ao gerar relatório de Faltas: {} ", ex);
        }
    }

    private String getComplementoQuery() {
        String complementoQuery = "";
        complementoQuery += hierarquiaOrganizacionalSelecionada != null ? " and ho.codigo like '" + hierarquiaOrganizacionalSelecionada.getCodigoSemZerosFinais() + "%' " : "";
        complementoQuery += vinculoFP != null ? " and contrato.id = " + vinculoFP.getId() + " " : "";
        return complementoQuery;
    }

    public void tornarNullFiltrosDaPesquisa() {
        this.hierarquiaOrganizacionalSelecionada = null;
        vinculoFP = null;
    }
}
