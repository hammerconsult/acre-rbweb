/*
 * Codigo gerado automaticamente em Tue Feb 07 08:22:09 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.RelatorioFichaFinanceira;
import br.com.webpublico.controlerelatorio.TermoRescisaoControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.cadastrofuncional.AvisoPrevio;
import br.com.webpublico.entidades.rh.configuracao.ConfiguracaoRH;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.rh.TipoAvisoPrevio;
import br.com.webpublico.enums.rh.esocial.TipoMotivoDesligamentoESocial;
import br.com.webpublico.esocial.dto.OcorrenciaESocialDTO;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.rh.SemBasePeriodoAquisitivoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.cadastrofuncional.AvisoPrevioFacade;
import br.com.webpublico.negocios.rh.esocial.ConfiguracaoEmpregadorESocialFacade;
import br.com.webpublico.util.*;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "exoneracaoRescisaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoExoneracaoRescisao", pattern = "/exoneracaorescisao/novo/", viewId = "/faces/rh/administracaodepagamento/exoneracaorescisao/edita.xhtml"),
    @URLMapping(id = "editarExoneracaoRescisao", pattern = "/exoneracaorescisao/editar/#{exoneracaoRescisaoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/exoneracaorescisao/edita.xhtml"),
    @URLMapping(id = "listarExoneracaoRescisao", pattern = "/exoneracaorescisao/listar/", viewId = "/faces/rh/administracaodepagamento/exoneracaorescisao/lista.xhtml"),
    @URLMapping(id = "verExoneracaoRescisao", pattern = "/exoneracaorescisao/ver/#{exoneracaoRescisaoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/exoneracaorescisao/visualizar.xhtml")
})
public class ExoneracaoRescisaoControlador extends PrettyControlador<ExoneracaoRescisao> implements Serializable, CRUD {

    public static final String MSG_ERRO_CONFIGURACAO = "Não foi possível encontrar configuração vigênte, por favor tente novamente, se o erro persistir verifique suas configurações em ";
    @EJB
    private ExoneracaoRescisaoFacade exoneracaoRescisaoFacade;
    @EJB
    private MotivoExoneracaoRescisaoFacade motivoExoneracaoRescisaoFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private AvisoPrevioFacade avisoPrevioFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private SituacaoFuncionalFacade situacaoFuncionalFacade;
    @EJB
    private TipoProvimentoFacade tipoProvimentoFacade;
    @EJB
    private MovimentoSEFIPFacade movimentoSEFIPFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    private FuncoesFolhaFacade funcoesFolhaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private CompetenciaFPFacade competenciaFPFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    private ConfiguracaoEmpregadorESocialFacade configuracaoEmpregadorESocialFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    @ManagedProperty(name = "relatorioFichaFinanceira", value = "#{relatorioFichaFinanceira}")
    private RelatorioFichaFinanceira relatorioFichaFinanceira;
    @ManagedProperty(name = "termoRescisaoControlador", value = "#{termoRescisaoControlador}")
    private TermoRescisaoControlador termoRescisaoControlador;

    private ConverterAutoComplete converterContratoFP;
    private ConverterAutoComplete converterMotivoExoneracaoRescisao;
    private ConverterAutoComplete converterAtoLegal;
    private ConverterAutoComplete converterMovimentoSEFIP;
    private ConverterAutoComplete converterAtoLegalAlteracao;
    private List<OcorrenciaESocialDTO> ocorrencias;
    private String xml;

    public ExoneracaoRescisaoControlador() {
        super(ExoneracaoRescisao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return exoneracaoRescisaoFacade;
    }

    public List<SelectItem> getTiposAvisoPrevio() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoAvisoPrevio object : TipoAvisoPrevio.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public boolean isDesabilitarFGTS() {
        if (selecionado.getVinculoFP() != null) {
            try {
                if (exoneracaoRescisaoFacade.isContratoFP(selecionado.getVinculoFP().getId())) {
                    ContratoFP c = selecionado.getVinculoFP().getContratoFP();
                    if (TipoRegime.ESTATUTARIO.equals(c.getTipoRegime().getCodigo())) return true;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public List<OcorrenciaESocialDTO> getOcorrencias() {
        return ocorrencias;
    }

    public void setOcorrencias(List<OcorrenciaESocialDTO> ocorrencias) {
        this.ocorrencias = ocorrencias;
    }

    public ConverterAutoComplete getConverterContratoFP() {
        if (converterContratoFP == null) {
            converterContratoFP = new ConverterAutoComplete(VinculoFP.class, vinculoFPFacade);
        }
        return converterContratoFP;
    }

    public List<VinculoFP> completarContratoFP(String parte) {
        return vinculoFPFacade.buscarContratosQueNaoEstaoRescisos(parte.trim());
    }

    public List<SelectItem> getAvisoPrevio() {
        List<SelectItem> retorno = Lists.newArrayList();
        if (selecionado.getVinculoFP() == null) {
            return retorno;
        }
        List<AvisoPrevio> avisosPrevio = avisoPrevioFacade.getAvisoPrevioPorContratoFP((ContratoFP) selecionado.getVinculoFP());
        if (avisosPrevio == null || avisosPrevio.isEmpty()) {
            return retorno;
        }
        retorno.add(new SelectItem(null, ""));
        for (AvisoPrevio avisoPrevio : avisosPrevio) {
            retorno.add(new SelectItem(avisoPrevio, "Data do Aviso Prévio: " + DataUtil.getDataFormatada(avisoPrevio.getDataAviso()) + "; Data do Desligamento: " +
                DataUtil.getDataFormatada(avisoPrevio.getDataDesligamento()) + " Servidor: " + avisoPrevio.getContratoFP()));
        }
        return retorno;
    }

    public ConverterAutoComplete getConverterMotivoExoneracaoRescisao() {
        if (converterMotivoExoneracaoRescisao == null) {
            converterMotivoExoneracaoRescisao = new ConverterAutoComplete(MotivoExoneracaoRescisao.class, motivoExoneracaoRescisaoFacade);
        }
        return converterMotivoExoneracaoRescisao;
    }

    public List<MotivoExoneracaoRescisao> completarMotivoExoneracaoRescisao(String parte) {
        return motivoExoneracaoRescisaoFacade.listaFiltrandoCodigoDescricao(parte);
    }

    public ConverterAutoComplete getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, atoLegalFacade);
        }
        return converterAtoLegal;
    }

    public ConverterAutoComplete getConverterAtoLegalAlteracao() {
        if (converterAtoLegalAlteracao == null) {
            converterAtoLegalAlteracao = new ConverterAutoComplete(AtoLegal.class, atoLegalFacade);
        }
        return converterAtoLegalAlteracao;
    }

    public List<AtoLegal> completarAtoLegal(String parte) {
        return atoLegalFacade.listaFiltrandoParteEPropositoAtoLegal(parte, PropositoAtoLegal.ATO_DE_PESSOAL, "numero", "nome");
    }

    public List<AtoLegal> completarAtoLegalAlteracao(String parte) {
        return atoLegalFacade.listaFiltrandoParteEPropositoAtoLegal(parte, PropositoAtoLegal.ATO_DE_PESSOAL, "numero", "nome");
    }

    @URLAction(mappingId = "novoExoneracaoRescisao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        if (situacaoFuncionalFacade.recuperaCodigo(SituacaoFuncional.EXONERADO_RESCISO) == null) {
            FacesUtil.addAtencao("A Situação Funcional para Exoneração/Rescisão ainda não foi cadastrada !");
        }

        if ((tipoProvimentoFacade.recuperaTipoProvimentoPorCodigo(TipoProvimento.EXONERACAORESCISAO_CARREIRA) == null)
            || (tipoProvimentoFacade.recuperaTipoProvimentoPorCodigo(TipoProvimento.EXONERACAORESCISAO_COMISSAO) == null)) {
            FacesUtil.addAtencao("Os Tipos de Provimentos para Exonerações/Rescisões não foram cadastrados !");
        }
    }

    @URLAction(mappingId = "verExoneracaoRescisao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        getEventoPorIdentificador();
    }

    @URLAction(mappingId = "editarExoneracaoRescisao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        AtoLegal ato = (AtoLegal) Web.pegaDaSessao(AtoLegal.class);
        MotivoExoneracaoRescisao motivoExoneracaoRescisao = (MotivoExoneracaoRescisao) Web.pegaDaSessao(MotivoExoneracaoRescisao.class);
        MovimentoSEFIP movimentoSEFIP = (MovimentoSEFIP) Web.pegaDaSessao(MovimentoSEFIP.class);
        super.editar();
        if (selecionado.getAtoLegalAlteracao() == null) {
            selecionado.setAtoLegalAlteracao(ato);
        }
        if (selecionado.getMotivoExoneracaoRescisao() == null) {
            selecionado.setMotivoExoneracaoRescisao(motivoExoneracaoRescisao);
        }
        if (selecionado.getMovimentoSEFIP() == null) {
            selecionado.setMovimentoSEFIP(movimentoSEFIP);
        }
        getEventoPorIdentificador();
    }

    private void validarAfastamentoAndConcessaoFerias() {
        ValidacaoException ve = new ValidacaoException();
        List<Afastamento> afastamentos = exoneracaoRescisaoFacade.hasAfastamentoDataExoneracao(selecionado.getVinculoFP(), selecionado.getDataRescisao());
        List<ConcessaoFeriasLicenca> concessao = exoneracaoRescisaoFacade.hasConcessaoDataExoneracao(selecionado.getVinculoFP(), selecionado.getDataRescisao());
        if (afastamentos != null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O servidor(a) tem afastamento à vencer na data <b>" + DataUtil.getDataFormatada(afastamentos.get(0).getTermino()) + "</b>" + " não podendo ser exonerado.");
        }
        if (concessao != null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O servidor(a) tem concessão de férias à vencer na data <b>" + DataUtil.getDataFormatada(concessao.get(0).getDataFinal()) + "</b>" + " não podendo ser exonerado.");
        }
        ve.lancarException();
    }

    public String mensagemFichaFinanceiraAberta() {
        return (selecionado.getVinculoFP() != null && selecionado.getDataRescisao() != null && exoneracaoRescisaoFacade.buscarFichaFinanceiraParaExclusao(selecionado.getVinculoFP(), selecionado.getDataRescisao()) != null) ? "if (!confirm('ATENÇÃO! Foi encontrada ficha financeira em aberto processada em Folha Normal na competência da exoneração. Essa ficha financeira será excluída.')) return false;" : "aguarde.show()";
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            validarAfastamentoAndConcessaoFerias();
            Date dataFinalVigenciaContratoFP = getFinalVigenciaContratoFP();
            if (isOperacaoNovo()) {
                exoneracaoRescisaoFacade.salvarNovo(selecionado, dataFinalVigenciaContratoFP);
            } else {
                exoneracaoRescisaoFacade.salvar(selecionado);
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoSalvar()));
        } catch (SemBasePeriodoAquisitivoException semBaseEx) {
            String url = "<b><a href='" + FacesUtil.getRequestContextPath() + "/cargo/editar/" + selecionado.getVinculoFP().getContratoFP().getCargo().getId() + "' target='_blank'>Cadastro de Cargos</a></b>";
            FacesUtil.addOperacaoNaoRealizada("O cargo <b>" + selecionado.getVinculoFP().getContratoFP().getCargo() + "</b> não possui bases de período aquisitivo vigentes em <b>" + Util.formatterDiaMesAno.format(UtilRH.getDataOperacao()) + "</b>.<br />Verifique as configurações no " + url);
            return;
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
            return;
        } catch (Exception e) {
            FacesUtil.addAtencao("Não foi passivel salvar a exoneração/rescisão: " + e.getMessage());
            descobrirETratarException(e);
        }
        redireciona();
    }


    @Override
    public void excluir() {
        if (validaRegrasParaExcluir()) {
            try {
                ContratoFP contratoFP = selecionado.getVinculoFP().getContratoFP();
                voltarVigenciasContratoFPAndAssociacoes(contratoFP);
                excluirPeriodoAquisitivoFLCriadoAoSalvarExoneracao(contratoFP);
                criarNovoProvimentoFPEstornoExoneracao();
                reabrirVigenciaPrevidenciaComplementar();
                getFacede().remover(selecionado);
                redireciona();
                FacesUtil.addOperacaoRealizada(getMensagemSucessoAoExcluir());
            } catch (Exception e) {
                descobrirETratarException(e);
            }
        }
    }

    public void criarNovoProvimentoFPEstornoExoneracao() {
        exoneracaoRescisaoFacade.criarNovoProvimentoFPEstornoExoneracao(selecionado);
    }

    public void excluirPeriodoAquisitivoFLCriadoAoSalvarExoneracao(ContratoFP contratoFP) {
        PeriodoAquisitivoFL periodoAquisitivoFL = exoneracaoRescisaoFacade.getPeriodoAquisitivoFLFacade().buscarPeriodoAquisitivoFLPorContratoFPAndFinalVigencia(contratoFP, selecionado.getDataRescisao());
        if (periodoAquisitivoFL != null) {
            exoneracaoRescisaoFacade.getPeriodoAquisitivoFLFacade().remover(periodoAquisitivoFL);
        }
    }

    public void voltarVigenciasContratoFPAndAssociacoes(ContratoFP contratoFP) {
        exoneracaoRescisaoFacade.getContratoFPFacade().voltarVigenciasContratoFPAndAssociacoes(contratoFP);
    }

    public void reabrirVigenciaPrevidenciaComplementar() {
        exoneracaoRescisaoFacade.reabrirVigenciaPrevidenciaComplementar(selecionado);
    }

    public Date simularFinalVigencia() {
        if (!isOperacaoNovo())
            return selecionado.getDataRescisao();

        ValidacaoException ve = new ValidacaoException();
        try {
            ConfiguracaoRH configuracaoRH = exoneracaoRescisaoFacade.getConfiguracaoRHFacade().recuperarConfiguracaoRHVigente(sistemaControlador.getDataOperacao());
            validarConfiguracaoVigenteRh(configuracaoRH, ve);
            if (configuracaoRH.getConfiguracaoRescisao().getControleVigenciaFinalViculoFP()) {
                return DataUtil.getDataDiaAnterior(selecionado.getDataRescisao());
            }
            return selecionado.getDataRescisao();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
            return selecionado.getDataRescisao();
        }
    }


    private void validarConfiguracaoVigenteRh(ConfiguracaoRH configuracaoRH, ValidacaoException ex) {
        if (configuracaoRH == null || configuracaoRH.getConfiguracaoRescisao() == null)
            ex.adicionarMensagemDeOperacaoNaoRealizada(MSG_ERRO_CONFIGURACAO + getMontarUrlInfo());

        if (ex.temMensagens())
            throw ex;
    }

    private String getMontarUrlInfo() {
        return "<a href='" + FacesUtil.getRequestContextPath() + "/configuracao/rh/listar/' target='_blank'>Configuração Recursos Humanos</a>";
    }

    public Date getFinalVigenciaContratoFP() {
        ValidacaoException ex = new ValidacaoException();
        try {
            ConfiguracaoRH configuracaoRH = exoneracaoRescisaoFacade.getConfiguracaoRHFacade().recuperarConfiguracaoRHVigente(sistemaControlador.getDataOperacao());
            validarConfiguracaoVigenteRh(configuracaoRH, ex);
            if (configuracaoRH.getConfiguracaoRescisao().getControleVigenciaFinalViculoFP()) {
                return DataUtil.getDataDiaAnterior(selecionado.getDataRescisao());
            }
            return selecionado.getDataRescisao();
        } catch (ValidacaoException ve) {
            return selecionado.getDataRescisao();
        }
    }

    @Override
    public boolean validaRegrasParaExcluir() {
        try {
            int mes = DataUtil.getMes(selecionado.getDataRescisao());
            int ano = DataUtil.getAno(selecionado.getDataRescisao());
            FolhaDePagamento folhaDePagamento = getFolhaPagamentoPorVinculoFPAndMesAndAno(selecionado.getVinculoFP(), mes, ano);
            CompetenciaFP competenciaFP = getCompetenciaPorFolhaPagamento(folhaDePagamento);

            if (!competenciaFP.isStatusCompetenciaAberta()) {
                String mensagem = "Foi encontrado folha processada em <b>"
                    + Mes.getMesToInt(mes).getDescricao() + "/" + ano + "</b> - <b>"
                    + competenciaFP.getStatusCompetencia().getDescricao() + "</b> para esta exoneração/rescisão. É impossível excluir este registro!";
                FacesUtil.addOperacaoNaoPermitida(mensagem);
                return false;
            }
            return true;
        } catch (ExcecaoNegocioGenerica nge) {
            logger.debug(nge.getMessage());
            return true;
        } catch (Exception ex) {
            logger.error("Erro ao tentar excluir uma exoneração ", ex);
            return false;
        }
    }

    private CompetenciaFP getCompetenciaPorFolhaPagamento(FolhaDePagamento folhaDePagamento) {
        try {
            return folhaDePagamento.getCompetenciaFP();
        } catch (NullPointerException npe) {
            throw new ExcecaoNegocioGenerica("Folha de pagamento" + folhaDePagamento + " sem competência!");
        }
    }

    public FolhaDePagamento getFolhaPagamentoPorVinculoFPAndMesAndAno(VinculoFP vinculoFP, int mes, int ano) {
        FichaFinanceiraFP fichaFinanceiraFP = fichaFinanceiraFPFacade.recuperaFichaFinanceiraFPPorVinculoFPMesAnoTipoFolha(vinculoFP, ano, mes, TipoFolhaDePagamento.RESCISAO);
        try {
            return fichaFinanceiraFP.getFolhaDePagamento();
        } catch (NullPointerException npe) {
            throw new ExcecaoNegocioGenerica("Nenhuma folha processada em " + mes + "/" + ano + " para o servidor " + vinculoFP.getContratoFP());
        }
    }

    public void gerarRelatorio(ExoneracaoRescisao exoneracao) throws JRException, IOException {
        exoneracao = exoneracaoRescisaoFacade.recuperar(exoneracao.getId());
        relatorioFichaFinanceira.setVinculoFPSelecionado(exoneracao.getVinculoFP());
        relatorioFichaFinanceira.setAno(FolhaDePagamentoFacade.getAno(exoneracao.getDataRescisao()));
        relatorioFichaFinanceira.setMes(FolhaDePagamentoFacade.getMes(exoneracao.getDataRescisao()));
        relatorioFichaFinanceira.setMesFinal(FolhaDePagamentoFacade.getMes(exoneracao.getDataRescisao()));
        relatorioFichaFinanceira.setTipoFolhaDePagamento(TipoFolhaDePagamento.RESCISAO);
        relatorioFichaFinanceira.gerarRelatorio("PDF");
    }

    public void gerarRelatorioTermoRescisao() throws JRException, IOException {
        termoRescisaoControlador.setVinculoFP(selecionado.getVinculoFP());
        int mes = DataUtil.getMes(selecionado.getDataRescisao());
        int ano = DataUtil.getAno(selecionado.getDataRescisao());
        FichaFinanceiraFP ficha = fichaFinanceiraFPFacade.recuperaFichaFinanceiraFPPorVinculoFPMesAnoTipoFolha(selecionado.getVinculoFP(), mes, ano, TipoFolhaDePagamento.RESCISAO);
        termoRescisaoControlador.setFichaFinanceiraFP(ficha);
        termoRescisaoControlador.gerarRelatorioTermoRescisao(mes, ano);
    }


    public void gerarFichaFinanceira() throws JRException, IOException {
        gerarRelatorio((ExoneracaoRescisao) selecionado);
    }

    public CompetenciaFP getCompetenciaAberta(FolhaDePagamento folha) {
        CompetenciaFP comp = folhaDePagamentoFacade.verificaCompetenciaAberta(folha);
        if (comp == null) {
            comp = new CompetenciaFP();
            comp.setExercicio(sistemaControlador.getExercicioCorrente());
            comp.setMes(Mes.getMesToInt(FolhaDePagamentoFacade.getMes(new Date())));
            comp.setStatusCompetencia(StatusCompetencia.ABERTA);
            comp.setTipoFolhaDePagamento(TipoFolhaDePagamento.RESCISAO);
            competenciaFPFacade.salvarNovo(comp);
        }
        return comp;
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getVinculoFP() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Servidor deve ser informado!");
            ve.lancarException();
        }
        if (selecionado.getDataRescisao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("A Data da Rescisão deve ser informado!");
            ve.lancarException();
        }
        if (selecionado.getMotivoExoneracaoRescisao().getTipoMotivoDesligamentoESocial() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Motivo da Exoneração/Rescisão selecionado não possui um 'Motivo Exoneração/Rescisão E-Social' cadastrado!<br>" +
                "  Vá em /motivo-exoneracao-rescisao/listar/ , ache o Motivo da Exoneração/Rescisão que esta tentando usar e cadastre um 'Motivo Exoneração/Rescisão E-Social' para ele.");
            ve.lancarException();
        }
        if (selecionado.getMotivoExoneracaoRescisao() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Motivo da Exoneração/Rescisão deve ser informado!");
        }
        if (selecionado.getAtoLegal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Ato Legal deve ser informado!");
        }
        if (situacaoFuncionalFacade.recuperaCodigo(SituacaoFuncional.EXONERADO_RESCISO) == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Situação Funcional para Exoneração/Rescisão ainda não foi cadastrada!");
        }

        if ((tipoProvimentoFacade.recuperaTipoProvimentoPorCodigo(TipoProvimento.EXONERACAORESCISAO_CARREIRA) == null)
            || (tipoProvimentoFacade.recuperaTipoProvimentoPorCodigo(TipoProvimento.EXONERACAORESCISAO_COMISSAO) == null)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Os Tipos de Provimentos para Exonerações/Rescisões não foram cadastrados!");
        }

        if (selecionado.getVinculoFP().getInicioVigencia().after(selecionado.getDataRescisao())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Rescisão não pode ser inferior ao início de vigência do contrato do servidor!");
        }
        if (isOperacaoNovo()) {
            if (exoneracaoRescisaoFacade.existeExoneracaoRescisaoPorVinculoFP(selecionado.getVinculoFP())) {
                Reintegracao reintegracao = exoneracaoRescisaoFacade.getReintegracaoFacade().recuperarUltimaReintegracao(selecionado.getVinculoFP());
                if (reintegracao == null || (reintegracao.getDataReintegracao().compareTo(selecionado.getDataRescisao()) >= 0)) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O Servidor selecionado já foi exonerado!");
                }
            }
        }
        if (isAvisoPrevioObrigatorio() && selecionado.getAvisoPrevio() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Para esse Servidor é obrigatório informar o Aviso Prévio!");
        }
        if (isInformarNumeroCertidaoObito() && Strings.isNullOrEmpty(selecionado.getNumeroCertidaoObito())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Para servidor que tem motivo de desligamento por óbito é necessário informar o Número da Certidão de óbito válida!");
        }
        ve.lancarException();
    }

    public Converter getConverterMovimentoSEFIP() {
        if (converterMovimentoSEFIP == null) {
            converterMovimentoSEFIP = new ConverterAutoComplete(MovimentoSEFIP.class, movimentoSEFIPFacade);
        }
        return converterMovimentoSEFIP;
    }

    public List<MovimentoSEFIP> completarMovimentosSEFIP(String parte) {
        List<MovimentoSEFIP> toReturn = new ArrayList<>();
        for (MovimentoSEFIP movimento : movimentoSEFIPFacade.listaFiltrando(parte.trim(), "codigo", "descricao")) {
            if (Arrays.asList("H", "I1", "I2", "I3", "I4", "J", "K", "L", "S2", "S3", "U1", "U3").contains(movimento.getCodigo())) {
                toReturn.add(movimento);
            }
        }
        return toReturn;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public RelatorioFichaFinanceira getRelatorioFichaFinanceira() {
        return relatorioFichaFinanceira;
    }

    public void setRelatorioFichaFinanceira(RelatorioFichaFinanceira relatorioFichaFinanceira) {
        this.relatorioFichaFinanceira = relatorioFichaFinanceira;
    }

    public TermoRescisaoControlador getTermoRescisaoControlador() {
        return termoRescisaoControlador;
    }

    public void setTermoRescisaoControlador(TermoRescisaoControlador termoRescisaoControlador) {
        this.termoRescisaoControlador = termoRescisaoControlador;
    }

    public void excluirSelecionado() {
        FacesUtil.addWarn("Atenção", "O registro não foi excluído. Função ainda não implementada.");
    }

    @Override
    public String getCaminhoPadrao() {
        return "/exoneracaorescisao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }


    public String getMensagem() {
        String mensagem = "";
        if (selecionado.getVinculoFP() != null && selecionado.getDataRescisao() != null) {
            if (selecionado.getVinculoFP().getFinalVigencia() != null && selecionado.getDataRescisao().after(selecionado.getVinculoFP().getFinalVigencia())) {
                mensagem = "O servidor " + selecionado.getVinculoFP() + " está com o final de vigência preenchido(" + Util.dateToString(selecionado.getVinculoFP().getFinalVigencia()) + ") e a data de rescisão é superior a data de final de vigência. Ao salvar o sistema irá atualizar o final de vigência do vinculo pela data de rescisão.";
            }
        }
        return mensagem;
    }

    public boolean isAvisoPrevioObrigatorio() {
        try {
            if (selecionado.getVinculoFP() == null) {
                return false;
            }
            ConfiguracaoEmpregadorESocial empregador = contratoFPFacade.buscarEmpregadorPorVinculoFP((ContratoFP) selecionado.getVinculoFP());
            if (empregador != null) {
                return empregador.getEmiteAvisoPrevio();
            }
            return false;
        } catch (Exception e) {
            logger.debug("Não foi possível determinar o Aviso Prévio.");
            return false;
        }
    }

    public boolean isInformarNumeroCertidaoObito() {
        if (selecionado.getMotivoExoneracaoRescisao() != null) {
            return TipoMotivoDesligamentoESocial.RESCISAO_OPCAO_EMPREGADO_VIRTUDE_FALECIMENTO_EMPREGADOR_INDIVIDUAL_OU_DOCUMESTICO.equals(selecionado.getMotivoExoneracaoRescisao().getTipoMotivoDesligamentoESocial())
                || TipoMotivoDesligamentoESocial.RESCISAO_FALECIMENTO_EMPREGADO.equals(selecionado.getMotivoExoneracaoRescisao().getTipoMotivoDesligamentoESocial());
        }
        return false;
    }

    public void enviarEventoEsocial() {
        try {
            ConfiguracaoEmpregadorESocial configuracao = configuracaoEmpregadorESocialFacade.buscarEmpregadorPorVinculoFP(selecionado.getVinculoFP());
            validarEnvioEventoEsocial(configuracao);
            PessoaFisica pessoaFisica = selecionado.getVinculoFP().getMatriculaFP().getPessoa();
            selecionado.getVinculoFP().getMatriculaFP().setPessoa(pessoaFisicaFacade.recuperarComDocumentos(pessoaFisica.getId()));
            exoneracaoRescisaoFacade.enviarS2299ESocial(configuracao, selecionado);
            redireciona();

            FacesUtil.addOperacaoRealizada("Evento enviado com sucesso! Aguarde seu processamento para receber a situação.");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            FacesUtil.executaJavaScript("aguarde.hide()");
        }
    }

    private void validarEnvioEventoEsocial(ConfiguracaoEmpregadorESocial configuracao) {
        ValidacaoException ve = new ValidacaoException();
        if (configuracao == null || configuracao.getCertificado() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado configuração do empregador do servidor.");
        }
        ve.lancarException();
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    private void getEventoPorIdentificador() {
        selecionado.setEventosEsocial(exoneracaoRescisaoFacade.getRegistroESocialFacade().getEventoPorIdentificador(selecionado.getId()));
    }
}
