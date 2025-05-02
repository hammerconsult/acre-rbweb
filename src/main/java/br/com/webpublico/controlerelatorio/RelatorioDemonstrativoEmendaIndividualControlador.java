package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Vereador;
import br.com.webpublico.enums.ModalidadeEmenda;
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

@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-demonstrativo-emenda-individual", pattern = "/relatorio-demonstrativo-emenda-individual/", viewId = "/faces/financeiro/relatorio/relatorio-demonstrativo-emenda-individual.xhtml")
})
@ManagedBean
public class RelatorioDemonstrativoEmendaIndividualControlador implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private VereadorFacade vereadorFacade;
    private Vereador vereador;
    private ModalidadeEmenda modalidadeEmenda;
    private String filtros;
    private Date dataInicial;
    private Date dataFinal;

    @URLAction(mappingId = "relatorio-demonstrativo-emenda-individual", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        dataInicial = new Date();
        dataFinal = new Date();
        vereador = null;
        modalidadeEmenda = null;
        filtros = "";
    }

    public List<Vereador> completarVereadores(String filtro) {
        return vereadorFacade.listaVereadorPorExercicio(filtro, sistemaFacade.getExercicioCorrente());
    }

    public List<SelectItem> getModalidades() {
        return Util.getListSelectItem(ModalidadeEmenda.values());
    }

    public void gerarRelatorio() {
        try {
            validarDatas();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.adicionarParametro("MUNICIPIO", "CÂMARA MUNICIPAL DE RIO BRANCO");
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("complemento", montarParametros());
            dto.adicionarParametro("FILTROS", filtros);
            dto.setNomeRelatorio("RELATORIO-DEMONSTRATIVO-EMENDA-INDIVIDUAL");
            dto.setApi("contabil/demonstrativo-emenda-individual/");
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

    private void validarDatas() {
        ValidacaoException ve = new ValidacaoException();
        if (dataInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Inicial deve ser informado.");
        }
        if (dataFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data Final deve ser informado.");
        }
        ve.lancarException();
        if (dataInicial.after(dataFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data Inicial deve ser inferior ou igual à Data Final.");
        }
        ve.lancarException();
    }

    private String montarParametros() {
        String retorno = " and trunc(em.dataEmenda) between to_date('" + DataUtil.getDataFormatada(dataInicial) + "', 'dd/MM/yyyy') and to_date('" + DataUtil.getDataFormatada(dataFinal) + "', 'dd/MM/yyyy') ";
        filtros = " Período: " + DataUtil.getDataFormatada(dataInicial) + " à " + DataUtil.getDataFormatada(dataFinal) + " -";
        if (vereador != null) {
            retorno += " and ve.id = " + vereador.getId();
            filtros += " Vereador: " + vereador.getPessoa().getNome() + " -";
        }
        if (modalidadeEmenda != null) {
            retorno += " and em.modalidadeEmenda = '" + modalidadeEmenda.name() + "' ";
            filtros += " Modalidade da Emenda: " + modalidadeEmenda.getDescricao() + " -";
        }
        filtros = filtros.substring(0, filtros.length() - 1);
        return retorno;
    }

    public Vereador getVereador() {
        return vereador;
    }

    public void setVereador(Vereador vereador) {
        this.vereador = vereador;
    }

    public ModalidadeEmenda getModalidadeEmenda() {
        return modalidadeEmenda;
    }

    public void setModalidadeEmenda(ModalidadeEmenda modalidadeEmenda) {
        this.modalidadeEmenda = modalidadeEmenda;
    }

    public String getFiltros() {
        return filtros;
    }

    public void setFiltros(String filtros) {
        this.filtros = filtros;
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
}
