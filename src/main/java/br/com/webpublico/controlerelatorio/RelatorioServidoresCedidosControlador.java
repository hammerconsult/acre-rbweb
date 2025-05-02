package br.com.webpublico.controlerelatorio;

import br.com.webpublico.enums.TipoCedenciaContratoFP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.CedenciaContratoFPFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by carlos on 21/05/15.
 */
@ManagedBean(name = "relatorioServidoresCedidosControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorioServidoresCedidos", pattern = "/rh/servidores-cedidos", viewId = "/faces/rh/relatorios/relatorioservidorescedidos.xhtml")
})
public class RelatorioServidoresCedidosControlador implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(RelatorioServidoresCedidosControlador.class);
    private Date dataInicial;
    private Date dataFinal;
    private TipoCedenciaContratoFP tipoCedenciaContratoFP;
    private String filtros;
    @EJB
    private CedenciaContratoFPFacade cedenciaContratoFPFacade;

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

    public TipoCedenciaContratoFP getTipoCedenciaContratoFP() {
        return tipoCedenciaContratoFP;
    }

    public void setTipoCedenciaContratoFP(TipoCedenciaContratoFP tipoCedenciaContratoFP) {
        this.tipoCedenciaContratoFP = tipoCedenciaContratoFP;
    }

    public String getFiltros() {
        return filtros;
    }

    public void setFiltros(String filtros) {
        this.filtros = filtros;
    }

    @URLAction(mappingId = "relatorioServidoresCedidos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        dataInicial = null;
        dataFinal = null;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setNomeParametroBrasao("BRASAO");
            dto.setNomeRelatorio("Servidores Cedidos");
            dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DE PROVIMENTOS");
            dto.adicionarParametro("WHERE", isCriterio());
            dto.adicionarParametro("FILTROS", filtros);
            dto.adicionarParametro("USUARIO", cedenciaContratoFPFacade.getSistemaFacade().getUsuarioCorrente().getLogin(), false);
            dto.adicionarParametro("MODULO", "Recursos Humano");
            dto.adicionarParametro("TIPO_CEDENCIA", tipoCedenciaContratoFP != null ? tipoCedenciaContratoFP.getToDto() : null);
            dto.adicionarParametro("DATA_INICIAL", DataUtil.getDataFormatada(dataInicial));
            dto.adicionarParametro("DATA_FINAL", DataUtil.getDataFormatada(dataFinal));
            dto.adicionarParametro("DATA_OPERACAO", cedenciaContratoFPFacade.getSistemaFacade().getDataOperacao().getTime());
            dto.setApi("rh/relatorio-servidores-cedidos/");
            ReportService.getInstance().gerarRelatorio(cedenciaContratoFPFacade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
            logger.error("Erro ao tentar gerar relatório. ", e);
        }
    }

    public List<SelectItem> itensTipoCedencia() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (TipoCedenciaContratoFP tipo : TipoCedenciaContratoFP.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (dataInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O período inicial deve ser preenchido.");
        }
        if (dataFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O período final deve ser preenchido.");
        }
        if (dataInicial != null && dataFinal != null) {
            if (dataInicial.after(dataFinal)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O período inicial não pode ser posterior ao período final.");
            }
        }
        ve.lancarException();
    }

    public String isCriterio() {
        StringBuilder sb = new StringBuilder(" WHERE 1 = 1 ");
        String juncao = " and ";
        filtros = "";

        if (tipoCedenciaContratoFP != null) {
            if (this.tipoCedenciaContratoFP.equals(tipoCedenciaContratoFP.COM_ONUS)) {
                sb.append(juncao).append("CCFP.TIPOCONTRATOCEDENCIAFP = ").append(" 'COM_ONUS' ").append(" ");
                filtros += " Tipo Solicitacao: " + tipoCedenciaContratoFP.COM_ONUS + " ";
            }
            if (this.tipoCedenciaContratoFP.equals(tipoCedenciaContratoFP.SEM_ONUS)) {
                sb.append(juncao).append("CCFP.TIPOCONTRATOCEDENCIAFP = ").append(" 'SEM_ONUS' ").append(" ");
                filtros += " Tipo Solicitacao: " + tipoCedenciaContratoFP.SEM_ONUS + " ";
            }

        }
        if (dataInicial != null) {
            filtros += " Período Inicial:  " + DataUtil.getDataFormatada(dataInicial) +";";
        }
        if (dataFinal != null) {
            filtros += " Período Final:  " + DataUtil.getDataFormatada(dataFinal) +";";
        }
        return sb.toString();
    }

}
