package br.com.webpublico.controlerelatorio.contabil.emenda;

import br.com.webpublico.entidades.Emenda;
import br.com.webpublico.entidades.Vereador;
import br.com.webpublico.enums.ModalidadeEmenda;
import br.com.webpublico.enums.StatusEmenda;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.VereadorFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
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

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-emenda-individual", pattern = "/relatorio/emenda-individual/", viewId = "/faces/financeiro/relatorio/relatorio-emenda-individual.xhtml")
})
public class RelatorioEmendaIndividualControlador implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private VereadorFacade vereadorFacade;
    private StatusEmenda statusPrefeitura;
    private StatusEmenda statusCamara;
    private Vereador vereador;
    private Date dataInicial;
    private Date dataFinal;
    private ModalidadeEmenda modalidadeEmenda;

    @URLAction(mappingId = "relatorio-emenda-individual", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        vereador = null;
        dataInicial = Util.montarData(1, 1, sistemaFacade.getExercicioCorrente().getAno());
        dataFinal = sistemaFacade.getDataOperacao();
        statusCamara = null;
        statusPrefeitura = null;
    }

    public List<SelectItem> getStatus() {
        return Util.getListSelectItem(StatusEmenda.values());
    }

    public void gerarRelatorio(Emenda em) {
        try {
            gerarRelatorio(" where em.id = " + em.getId());
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    public void gerarRelatorio() {
        try {
            validarCampos();
            gerarRelatorio(montarCondicao());
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    private void gerarRelatorio(String condicao) {
        RelatorioDTO dto = new RelatorioDTO();
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("MUNICIPIO", "CÂMARA MUNICIPAL DE RIO BRANCO");
        dto.adicionarParametro("condicao", condicao);
        dto.setNomeRelatorio(getNomeRelatorio());
        dto.setApi("contabil/emenda/");
        ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
        FacesUtil.addMensagemRelatorioSegundoPlano();
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (dataInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Inicial deve ser informado.");
        }
        if (dataFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Final deve ser informado.");
        }
        ve.lancarException();
        if (dataInicial.after(dataFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Data Final deve ser superior ao campo Data Inicial.");
        }
        ve.lancarException();
    }

    private String montarCondicao() {
        String condicao = " where trunc(em.dataEmenda) between to_date('" + DataUtil.getDataFormatada(dataInicial) + "', 'dd/MM/yyyy') and to_date('" + DataUtil.getDataFormatada(dataFinal) + "', 'dd/MM/yyyy') ";
        if (vereador != null) {
            condicao += " and em.VEREADOR_ID = " + vereador.getId();
        }
        if (statusCamara != null) {
            condicao += " and em.statuscamara = '" + statusCamara.name() + "' ";
        }
        if (statusPrefeitura != null) {
            condicao += " and em.statusPrefeitura = '" + statusPrefeitura.name() + "' ";
        }
        if (modalidadeEmenda != null) {
            condicao += " and em.modalidadeEmenda = '" + modalidadeEmenda.name() + "' ";
        }
        return condicao;
    }

    public String getNomeRelatorio() {
        return "RELATORIO-EMENDA";
    }

    public List<Vereador> completarVereadores(String parte) {
        return vereadorFacade.listaVereadorPorExercicio(parte.trim(), sistemaFacade.getExercicioCorrente());
    }

    public List<SelectItem> getModalidades() {
        return Util.getListSelectItem(ModalidadeEmenda.values());
    }

    public Vereador getVereador() {
        return vereador;
    }

    public void setVereador(Vereador vereador) {
        this.vereador = vereador;
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

    public StatusEmenda getStatusPrefeitura() {
        return statusPrefeitura;
    }

    public void setStatusPrefeitura(StatusEmenda statusPrefeitura) {
        this.statusPrefeitura = statusPrefeitura;
    }

    public StatusEmenda getStatusCamara() {
        return statusCamara;
    }

    public void setStatusCamara(StatusEmenda statusCamara) {
        this.statusCamara = statusCamara;
    }

    public ModalidadeEmenda getModalidadeEmenda() {
        return modalidadeEmenda;
    }

    public void setModalidadeEmenda(ModalidadeEmenda modalidadeEmenda) {
        this.modalidadeEmenda = modalidadeEmenda;
    }
}
