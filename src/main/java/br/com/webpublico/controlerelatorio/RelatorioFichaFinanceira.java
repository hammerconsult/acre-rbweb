/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
@ManagedBean(name = "relatorioFichaFinanceira")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioFichaFinanceira", pattern = "/relatorio/ficha-financeira/novo/", viewId = "/faces/rh/relatorios/relatoriofichafinanceira.xhtml"),
    @URLMapping(id = "gerarRelatorioFicha", pattern = "/relatorio/fichafinanceira/gerar/", viewId = "/faces/rh/relatorios/relatoriofichafinanceira.xhtml")
})
public class RelatorioFichaFinanceira extends AbstractReport implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioFichaFinanceira.class);

    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    private LotacaoFuncionalFacade lotacaoFuncionalFacade;
    private ContratoFP contratoFPSelecionado;
    private VinculoFP vinculoFPSelecionado;
    private Integer mes;
    private Integer ano;
    private Integer mesFinal;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConverterAutoComplete converterContratoFP;
    private ConverterAutoComplete converterVinculoFP;
    private TipoFolhaDePagamento tipoFolhaDePagamento;
    private List<FichaFinanceiraFP> fichaFinanceiraFP;
    private Integer versao;

    public RelatorioFichaFinanceira() {
        geraNoDialog = Boolean.TRUE;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(Integer mesFinal) {
        this.mesFinal = mesFinal;
    }

    public TipoFolhaDePagamento getTipoFolhaDePagamento() {
        return tipoFolhaDePagamento;
    }

    public void setTipoFolhaDePagamento(TipoFolhaDePagamento tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }

    public VinculoFP getVinculoFPSelecionado() {
        return vinculoFPSelecionado;
    }

    public void setVinculoFPSelecionado(VinculoFP vinculoFPSelecionado) {
        this.vinculoFPSelecionado = vinculoFPSelecionado;
    }

    @URLAction(mappingId = "novoRelatorioFichaFinanceira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoRelatorioFichaFinanceira() {

        limpaCampos();
    }

    @URLAction(mappingId = "novoRelatorioFichaFinanceiraAnual", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoRelatorioFichaFinanceiraAnual() {
        limpaCampos();
    }

    public void limpaCampos() {
        mes = null;
        mesFinal = null;
        ano = null;
        contratoFPSelecionado = null;
        vinculoFPSelecionado = null;
    }

    @URLAction(mappingId = "gerarRelatorioFicha", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void gerarRelatorioAPartirFolha() {
        String contrato = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("contrato");
        String year = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("ano");
        String month = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("mesinicial");
        String monthFinal = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("mesfinal");
        String tipoFolha = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("tipo");
        setGeraNoDialog(false);
        VinculoFP vinculo = vinculoFPFacade.recuperar(Long.parseLong(contrato));
        setVinculoFPSelecionado(vinculo);
        setAno(Integer.parseInt(year));
        setMes(Integer.parseInt(month));
        setMesFinal(Integer.parseInt(monthFinal));
        setTipoFolhaDePagamento(TipoFolhaDePagamento.valueOf(tipoFolha));
        gerarRelatorio("PDF");
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public List<FichaFinanceiraFP> getFichaFinanceiraFP() {
        return fichaFinanceiraFP;
    }

    public void setFichaFinanceiraFP(List<FichaFinanceiraFP> fichaFinanceiraFP) {
        this.fichaFinanceiraFP = fichaFinanceiraFP;
    }

    public List<SelectItem> getTiposFolha() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, " "));
        for (TipoFolhaDePagamento tipo : TipoFolhaDePagamento.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCamposFichaFinanceira();
            buscarAndValidaFichaFinanceira();
            AssistenteRelatorioFichaFinanceira assistente = preencherAssistenteRelatorioFicha();
            List<Long> idsFichas = Lists.newArrayList();
            for (FichaFinanceiraFP ficha : fichaFinanceiraFP) {
                idsFichas.add(ficha.getId());
            }
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", sistemaControlador.getUsuarioCorrente().getLogin(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MODULO", "Recursos Humanos");
            dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE GESTÃO ADMINISTRATIVA\nE TECNOLOGIA DA INFORMAÇÃO " + "\nDEPARTAMENTO DE FOLHA DE PAGAMENTO");
            dto.adicionarParametro("NOMERELATORIO", "");
            dto.adicionarParametro("DATAOPERACAO", UtilRH.getDataOperacao());
            dto.adicionarParametro("assistenteFicha", AssistenteRelatorioFichaFinanceira.assistenteFichaToDto(assistente));
            dto.adicionarParametro("idsFichas", idsFichas);
            dto.setNomeRelatorio("RELATORIO-FICHA-FINANCEIRA");
            dto.setApi("rh/ficha-financeira/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    private AssistenteRelatorioFichaFinanceira preencherAssistenteRelatorioFicha() {
        AssistenteRelatorioFichaFinanceira assistente = new AssistenteRelatorioFichaFinanceira();
        assistente.setCondicoes(montarCondicoes());
        assistente.setDataOperacao(sistemaControlador.getDataOperacao());
        assistente.setOperacaoFormula(OperacaoFormula.ADICAO);
        assistente.setIdVinculo(vinculoFPSelecionado.getId());
        return assistente;
    }

    private String montarCondicoes() {
        return versao != null ? (" and folha.versao = " + versao) : "";
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

    public Date getDataParam() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, ano);
        c.set(Calendar.MONTH, mes - 1);
        c.set(Calendar.DAY_OF_MONTH, 15);
        return c.getTime();

    }

    public List<VinculoFP> completaContrato(String s) {
        return contratoFPFacade.buscaContratoFiltrandoAtributosVinculoMatriculaFP(s.trim());
    }

    public Converter getConverterContratoFP() {
        if (converterContratoFP == null) {
            converterContratoFP = new ConverterAutoComplete(ContratoFP.class, contratoFPFacade);
        }
        return converterContratoFP;
    }

    public Converter getConverterVinculoFP() {
        if (converterVinculoFP == null) {
            converterVinculoFP = new ConverterAutoComplete(VinculoFP.class, vinculoFPFacade);
        }
        return converterVinculoFP;
    }

    public ContratoFP getContratoFPSelecionado() {
        return contratoFPSelecionado;
    }

    public void setContratoFPSelecionado(ContratoFP contratoFPSelecionado) {
        this.contratoFPSelecionado = contratoFPSelecionado;
    }

    private boolean validaCamposFichaAnual() {
        boolean retorno = true;
        if (vinculoFPSelecionado == null) {
            FacesUtil.addError("Impossível gerar o relatório!", "Informe o contrato.");
            retorno = false;
        }
        if (ano == null) {
            FacesUtil.addError("Impossível gerar o relatório!", "Informe o ano.");
            retorno = false;
        }
        return retorno;
    }

    private void validarCamposFichaFinanceira() {
        ValidacaoException ve = new ValidacaoException();

        if (mes == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês Inicial é obrigatório.");
        }
        if (mesFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês Final é obrigatório.");
        }
        if (ano == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano é obrigatório.");
        }
        if (vinculoFPSelecionado == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Servidor é obrigatório.");
        }
        if (mes != null && (mes < 1 || mes > 12)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Favor informar um Mês Inicial entre 01 (Janeiro) e 12 (Dezembro).");
        }
        if (mesFinal != null && (mesFinal < 1 || mesFinal > 12)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Favor informar um Mês Final entre 01 (Janeiro) e 12 (Dezembro).");
        }
        if (mes != null && mesFinal != null && mes > mesFinal) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Mês Inicial deve ser igual ou inferior ao Mês Final.");
        }
        if (tipoFolhaDePagamento == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Folha é obrigatório.");
        }
        ve.lancarException();
    }

    private void buscarAndValidaFichaFinanceira() {
        ValidacaoException ve = new ValidacaoException();
        fichaFinanceiraFP = fichaFinanceiraFPFacade.recuperaFichaFinanceiraFPPorVinculoFPMesesAnos(vinculoFPSelecionado, mes, mesFinal, ano, tipoFolhaDePagamento);
        if (fichaFinanceiraFP == null || fichaFinanceiraFP.isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoRealizada("Não existe ficha financeira para os filtros informados.");
        } else {
            ordenarFichas();
        }
        ve.lancarException();
    }

    private void ordenarFichas() {
        Collections.sort(fichaFinanceiraFP, new Comparator<FichaFinanceiraFP>() {
            @Override
            public int compare(FichaFinanceiraFP o1, FichaFinanceiraFP o2) {
                return ComparisonChain.start().compare(o1.getFolhaDePagamento().getMes(), o2.getFolhaDePagamento().getMes()).
                    compare(o1.getFolhaDePagamento().getVersao(), o2.getFolhaDePagamento().getVersao()).result();
            }
        });
    }

    public List<SelectItem> getVersoes() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, " "));
        if (mes != null && ano != null && tipoFolhaDePagamento != null && mesFinal != null && vinculoFPSelecionado != null) {
            for (Integer versaoFolha : folhaDePagamentoFacade.recuperarVersaoWithIntevaloMes(Mes.getMesToInt(mes), ano, tipoFolhaDePagamento, Mes.getMesToInt(mesFinal), vinculoFPSelecionado)) {
                retorno.add(new SelectItem(versaoFolha, String.valueOf(versaoFolha)));
            }
        }
        return retorno;
    }


    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }
}
