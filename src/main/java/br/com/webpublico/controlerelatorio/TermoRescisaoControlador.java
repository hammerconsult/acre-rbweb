/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.ExoneracaoRescisao;
import br.com.webpublico.entidades.FichaFinanceiraFP;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.EntidadeFacade;
import br.com.webpublico.negocios.ExoneracaoRescisaoFacade;
import br.com.webpublico.negocios.FichaFinanceiraFPFacade;
import br.com.webpublico.negocios.VinculoFPFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * @author Leonardo
 */
@ManagedBean(name = "termoRescisaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "termoRescisaoRelatorio", pattern = "/relatorio/termo-de-rescisao/", viewId = "/faces/rh/relatorios/termoRescisao.xhtml")
})
public class TermoRescisaoControlador extends AbstractReport implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private VinculoFP vinculoFP;
    private int mes;
    private int ano;
    private FichaFinanceiraFP fichaFinanceiraFP;
    private FichaFinanceiraFP fichaFinanceiraFPFolhaRescisoria;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private ExoneracaoRescisaoFacade exoneracaoRescisaoFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    private EntidadeFacade entidadeFacade;
    private ConverterAutoComplete converterContratoFP;


    public TermoRescisaoControlador() {
        geraNoDialog = Boolean.TRUE;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public FichaFinanceiraFP getFichaFinanceiraFP() {
        return fichaFinanceiraFP;
    }

    public void setFichaFinanceiraFP(FichaFinanceiraFP fichaFinanceiraFP) {
        this.fichaFinanceiraFP = fichaFinanceiraFP;
    }

    public FichaFinanceiraFP getFichaFinanceiraFPFolhaRescisoria() {
        return fichaFinanceiraFPFolhaRescisoria;
    }

    public void setFichaFinanceiraFPFolhaRescisoria(FichaFinanceiraFP fichaFinanceiraFPFolhaRescisoria) {
        this.fichaFinanceiraFPFolhaRescisoria = fichaFinanceiraFPFolhaRescisoria;
    }

    public List<VinculoFP> completaContratoParaTermoRescisao(String parte) {
        return vinculoFPFacade.listaVinculosExonerados(parte.trim());
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public ConverterAutoComplete getConverterContratoFP() {
        if (converterContratoFP == null) {
            converterContratoFP = new ConverterAutoComplete(VinculoFP.class, vinculoFPFacade);
        }
        return converterContratoFP;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    @URLAction(mappingId = "termoRescisaoRelatorio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoRelatorio() {
        vinculoFP = null;
    }


    public void iniciarParametrosRelatorios() throws JRException, IOException {
        ExoneracaoRescisao ex = exoneracaoRescisaoFacade.recuperaExoneracaoRecisao(vinculoFP);
        mes = DataUtil.getMes(ex.getDataRescisao());
        ano = DataUtil.getAno(ex.getDataRescisao());
        FichaFinanceiraFP ficha = fichaFinanceiraFPFacade.recuperaUltimaFichaFinanceiraFPPorVinculoFPMesAnoTipoFolha(vinculoFP, TipoFolhaDePagamento.NORMAL);
        FichaFinanceiraFP fichaFinanceiraFPFolhaRescisoria = fichaFinanceiraFPFacade.recuperaUltimaFichaFinanceiraFPPorVinculoFPMesAnoTipoFolha(vinculoFP, TipoFolhaDePagamento.RESCISAO);
        if (fichaFinanceiraFPFolhaRescisoria == null) {
            FacesUtil.addError("Atençao", "Não foi encontrado uma ficha de rescisão para " + vinculoFP);
        } else {
            setFichaFinanceiraFP(ficha);
            setFichaFinanceiraFPFolhaRescisoria(fichaFinanceiraFPFolhaRescisoria);
        }
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (vinculoFP == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo servidor é obrigatório.");
        }
        FichaFinanceiraFP fichaFinanceiraFPFolhaRescisoria = fichaFinanceiraFPFacade.recuperaUltimaFichaFinanceiraFPPorVinculoFPMesAnoTipoFolha(vinculoFP, TipoFolhaDePagamento.RESCISAO);
        if (vinculoFP != null && fichaFinanceiraFPFolhaRescisoria == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não há ficha de rescisão para gerar relatório.");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public void limparCampos() {
        vinculoFP = null;
    }

    public void gerarRelatorioTermoRescisao(Integer mes, Integer ano) throws JRException, IOException {
        iniciarParametrosRelatorios();
        this.mes = mes;
        this.ano = ano;
        gerarRelatorio("PDF");
    }

    public void gerarRelatorio(String tipoRelatorio) {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorio));
            dto.setApi("rh/termo-de-rescisao/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
            logger.error("Erro ao tentar gerar relatório: " + e);
        }
    }

    private RelatorioDTO montarRelatorioDTO() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.setNomeParametroBrasao("BRASAO");
        dto.setNomeRelatorio("TERMO DE RESCISÃO");
        dto.setNomeParametroSubreportDir("SUBREPORT_DIR");
        dto.adicionarParametro("CONTRATO_ID", vinculoFP.getId());
        dto.adicionarParametro("ANO", ano);
        dto.adicionarParametro("MES", mes);
        dto.adicionarParametro("ID_FICHA", fichaFinanceiraFP.getId());
        dto.adicionarParametro("ID_FICHA_RESCISAO", fichaFinanceiraFPFolhaRescisoria.getId());
        dto.adicionarParametro("ENTIDADE_ID", entidadeFacade.buscarEntidadePorUnidade(vinculoFP.getUnidadeOrganizacional(), (vinculoFP.getFinalVigencia() != null ? vinculoFP.getFinalVigencia() : sistemaControlador.getDataOperacao())).getId());
        return dto;
    }

}
