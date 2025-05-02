package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.FichaFinanceiraFP;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidadesauxiliares.rh.relatorio.AssistenteRelatorioFichaFinanceira;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.OperacaoFormula;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.UtilRH;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.*;

/**
 * @author leonardo
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioFichaFinanceiraAnual", pattern = "/relatorio/ficha-financeira-anual/novo/", viewId = "/faces/rh/relatorios/relatoriofichafinanceiraanual.xhtml")
})
public class RelatorioFichaFinanceiraAnual extends AbstractReport implements Serializable{

    private static final Logger logger = LoggerFactory.getLogger(RelatorioFichaFinanceira.class);

    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private LotacaoFuncionalFacade lotacaoFuncionalFacade;
    private VinculoFP vinculoFPSelecionado;
    private Integer ano;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    public RelatorioFichaFinanceiraAnual() {
        geraNoDialog = Boolean.TRUE;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public VinculoFP getVinculoFPSelecionado() {
        return vinculoFPSelecionado;
    }

    public void setVinculoFPSelecionado(VinculoFP vinculoFPSelecionado) {
        this.vinculoFPSelecionado = vinculoFPSelecionado;
    }


    @URLAction(mappingId = "novoRelatorioFichaFinanceiraAnual", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoRelatorioFichaFinanceiraAnual() {
        limpaCampos();
    }

    public void limpaCampos() {
        ano = null;
        vinculoFPSelecionado = null;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public void gerarRelatorio(String tipoRelatorio) {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorio));
            dto.setApi("rh/relatorio-ficha-financeira-anual/");
            ReportService.getInstance().gerarRelatorio(sistemaControlador.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
            logger.error("Erro ao gerar relatório de Ficha Financeira Anual. ", e);
        }
    }

    private RelatorioDTO montarRelatorioDTO() {
        RelatorioDTO dto = new RelatorioDTO();

        dto.setNomeParametroBrasao("BRASAO");
        dto.setNomeRelatorio("RELATÓRIO DE FICHA FINANCEIRA ANUAL");
        dto.adicionarParametro("MODULO", "Recursos Humanos");
        dto.adicionarParametro("SECRETARIA", "DEPARTAMENTO DE GESTÃO DE PESSOAS");
        dto.adicionarParametro("NOMERELATORIO", "FICHA FINANCEIRA ANUAL");
        dto.adicionarParametro("USUARIO", sistemaControlador.getUsuarioCorrente().getLogin(), false);

        Date ultima = DataUtil.montaData(31, Calendar.DECEMBER, ano).getTime();
        Date dataLotacao = ultima;
        if (vinculoFPSelecionado.getFinalVigencia() != null) {
            if (ultima.after(vinculoFPSelecionado.getFinalVigencia() != null ? vinculoFPSelecionado.getFinalVigencia() : new Date())) {
                ultima = vinculoFPSelecionado.getFinalVigencia();
                dataLotacao = lotacaoFuncionalFacade.buscarDataUltimaLotacaoFuncionalPorDataEVinculoFP(ultima, vinculoFPSelecionado);
            }
        }

        dto.adicionarParametro("ANO", ano.intValue());
        dto.adicionarParametro("DATA", DataUtil.getDataFormatada(ultima));
        dto.adicionarParametro("DATALOTACAO", DataUtil.getDataFormatada(dataLotacao));
        dto.adicionarParametro("CONTRATO_ID", vinculoFPSelecionado.getId());
        return dto;
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (ano == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O ano é um campo obrigatório!");
        }
        if (vinculoFPSelecionado == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Servidor é um campo obrigatório!");
        }
        ve.lancarException();
    }

    public List<VinculoFP> completaContrato(String s) {
        return contratoFPFacade.buscaContratoFiltrandoAtributosVinculoMatriculaFP(s.trim());
    }
}
