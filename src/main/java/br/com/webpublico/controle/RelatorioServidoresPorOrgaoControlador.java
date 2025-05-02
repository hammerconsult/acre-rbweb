/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.SituacaoFuncional;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SituacaoFuncionalFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author peixe
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioServidoresPorOrgao", pattern = "/relatorio/servidores-por-orgao/novo/", viewId = "/faces/rh/relatorios/relatorioservidoresporunidade.xhtml")
})
public class RelatorioServidoresPorOrgaoControlador extends AbstractReport implements Serializable {

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SituacaoFuncionalFacade situacaoFuncionalFacade;
    private Date dataReferencia;
    private HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada;
    private SituacaoFuncional situacaoFuncional;

    public RelatorioServidoresPorOrgaoControlador() {
        geraNoDialog = Boolean.TRUE;
    }

    @URLAction(mappingId = "novoRelatorioServidoresPorOrgao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        hierarquiaOrganizacionalSelecionada = null;
        situacaoFuncional = null;
        dataReferencia = new Date();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("dataReferencia", dataReferencia);
            dto.adicionarParametro("situacao", situacaoFuncional != null ? " and situacao.id = " + situacaoFuncional.getId() : "");
            dto.adicionarParametro("REFERENCIA", DataUtil.getDataFormatada(dataReferencia));
            dto.adicionarParametro("LOTACAO", hierarquiaOrganizacionalSelecionada.getCodigoSemZerosFinais() + "%");
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome());
            dto.adicionarParametro("MODULO", "Recursos Humanos");
            dto.setNomeRelatorio("RELATÓRIO-SERVIDORES-POR-ORGAO");
            dto.setApi("rh/servidores-por-orgao/");
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

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (dataReferencia == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data de referência deve ser informado.");
        }

        if (hierarquiaOrganizacionalSelecionada == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo órgão deve ser informado.");
        }
        ve.lancarException();
    }

    public Date getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(Date dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalSelecionada() {
        return hierarquiaOrganizacionalSelecionada;
    }

    public void setHierarquiaOrganizacionalSelecionada(HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada) {
        this.hierarquiaOrganizacionalSelecionada = hierarquiaOrganizacionalSelecionada;
    }

    public List<HierarquiaOrganizacional> completarHierarquias(String parte) {
        return hierarquiaOrganizacionalFacade.filtraNivelDoisEComRaiz(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public SituacaoFuncional getSituacaoFuncional() {
        return situacaoFuncional;
    }

    public void setSituacaoFuncional(SituacaoFuncional situacaoFuncional) {
        this.situacaoFuncional = situacaoFuncional;
    }

    public List<SelectItem> getBuscarSituacaoFuncional() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, "Todos"));
        for (SituacaoFuncional situacao : situacaoFuncionalFacade.buscarSituacaoFuncional()) {
            toReturn.add(new SelectItem(situacao, situacao.toString()));
        }
        return toReturn;
    }

}
