/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SistemaFacade;
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

/**
 * @author leonardo
 */
@ManagedBean(name = "relatorioTempoServicoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "relatorioTempoServico", pattern = "/rh/relatorio-de-tempo-de-contribuicao/", viewId = "/faces/rh/relatorios/relatoriotemposervico.xhtml")
})
public class RelatorioTempoServicoControlador implements Serializable {

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private Integer numeroMeses;
    private Date dataFinal;
    private HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada;

    public RelatorioTempoServicoControlador() {
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setNomeRelatorio("RELATORIO DE TEMPO DE CONTRIBUIÇÃO");
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "Recursos Humanos");
            dto.adicionarParametro("DATAFINAL", DataUtil.getDataFormatada(dataFinal));
            dto.adicionarParametro("LOTACAO", hierarquiaOrganizacionalSelecionada.getCodigoSemZerosFinais() + "%");
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getLogin());
            dto.adicionarParametro("DESCRICAO", hierarquiaOrganizacionalSelecionada.getSubordinada().getDescricao());
            dto.adicionarParametro("NUMEROMESES", numeroMeses);
            dto.setApi("rh/relatorio-de-tempo-de-contribuicao/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (hierarquiaOrganizacionalSelecionada == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Órgão deve ser informado.");
        }
        if (numeroMeses < 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Número de Meses deve ser maior que zero (0).");
        }
        if (numeroMeses == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Número de Meses deve ser informado");
        }
        if (dataFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data deve ser informado.");
        }
        ve.lancarException();
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalSelecionada() {
        return hierarquiaOrganizacionalSelecionada;
    }

    public void setHierarquiaOrganizacionalSelecionada(HierarquiaOrganizacional hierarquiaOrganizacionalSelecionada) {
        this.hierarquiaOrganizacionalSelecionada = hierarquiaOrganizacionalSelecionada;
    }

    public List<HierarquiaOrganizacional> completarHierarquias(String parte) {
        return hierarquiaOrganizacionalFacade.filtraPorNivelSemView(parte.trim(), "2", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public Integer getNumeroMeses() {
        return numeroMeses;
    }

    public void setNumeroMeses(Integer numeroMeses) {
        this.numeroMeses = numeroMeses;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    @URLAction(mappingId = "relatorioTempoServico", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        hierarquiaOrganizacionalSelecionada = null;
        numeroMeses = 0;
        dataFinal = sistemaFacade.getDataOperacao();
    }
}
