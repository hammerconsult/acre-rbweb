/*
 * Codigo gerado automaticamente em Mon Sep 05 15:28:59 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.ItemEnvioDadosRBPonto;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.enums.ClasseAfastamento;
import br.com.webpublico.enums.TipoPeriodoAquisitivo;
import br.com.webpublico.enums.rh.TipoInformacaoEnvioRBPonto;
import br.com.webpublico.esocial.dto.OcorrenciaESocialDTO;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.esocial.ConfiguracaoEmpregadorESocialFacade;
import br.com.webpublico.negocios.rh.integracaoponto.EnvioDadosRBPontoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.beust.jcommander.internal.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.*;

@ManagedBean(name = "afastamentoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoAfastamento", pattern = "/afastamento/novo/", viewId = "/faces/rh/administracaodepagamento/afastamento/edita.xhtml"),
    @URLMapping(id = "editarAfastamento", pattern = "/afastamento/editar/#{afastamentoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/afastamento/edita.xhtml"),
    @URLMapping(id = "listarAfastamento", pattern = "/afastamento/listar/", viewId = "/faces/rh/administracaodepagamento/afastamento/lista.xhtml"),
    @URLMapping(id = "verAfastamento", pattern = "/afastamento/ver/#{afastamentoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/afastamento/visualizar.xhtml")
})
public class AfastamentoControlador extends PrettyControlador<Afastamento> implements Serializable, CRUD {

    @EJB
    private AfastamentoFacade afastamentoFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    private ConverterAutoComplete converterContratoFP;
    @EJB
    private TipoAfastamentoFacade tipoAfastamentoFacade;
    private ConverterAutoComplete converterTipoAfastamento;
    @EJB
    private CIDFacade CIDFacade;
    private ConverterAutoComplete converterCid;
    private Afastamento afastamentoVigente;
    @EJB
    private MedicoFacade medicoFacade;
    private ConverterAutoComplete converterMedico;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private CargoConfiancaFacade cargoConfiancaFacade;
    @EJB
    private FuncaoGratificadaFacade funcaoGratificadaFacade;
    @EJB
    private ConfiguracaoEmpregadorESocialFacade configuracaoEmpregadorESocialFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private EnvioDadosRBPontoFacade envioDadosRBPontoFacade;
    @EJB
    private PeriodoAquisitivoFLFacade periodoAquisitivoFLFacade;
    private List<OcorrenciaESocialDTO> ocorrencias;
    private String xml;
    private List<ItemEnvioDadosRBPonto> itensEnvioDadosRBPonto;
    private List<TipoAfastamentoTipoPrevidenciaFP> tipoAfastamentoTipoPrevidenciaFPList;

    public AfastamentoControlador() {
        super(Afastamento.class);
    }

    public AfastamentoFacade getFacade() {
        return afastamentoFacade;
    }

    public List<OcorrenciaESocialDTO> getOcorrencias() {
        return ocorrencias;
    }

    public void setOcorrencias(Set<OcorrenciaESocialDTO> ocorrencias) {
        this.ocorrencias = Lists.newArrayList(ocorrencias);
    }

    @Override
    public AbstractFacade getFacede() {
        return afastamentoFacade;
    }

    public List<SelectItem> getContratoFP() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (ContratoFP object : contratoFPFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public ConverterAutoComplete getConverterContratoFP() {
        if (converterContratoFP == null) {
            converterContratoFP = new ConverterAutoComplete(ContratoFP.class, contratoFPFacade);
        }
        return converterContratoFP;
    }

    public List<SelectItem> getTipoAfastamento() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoAfastamento object : tipoAfastamentoFacade.tiposAfastamentosAtivos()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public Converter getConverterTipoAfastamento() {
        if (converterTipoAfastamento == null) {
            converterTipoAfastamento = new ConverterAutoComplete(TipoAfastamento.class, tipoAfastamentoFacade);
        }
        return converterTipoAfastamento;
    }

    public Converter getConverterCid() {
        if (converterCid == null) {
            converterCid = new ConverterAutoComplete(CID.class, CIDFacade);
        }
        return converterCid;
    }

    public Converter getConverterMedico() {
        if (converterMedico == null) {
            converterMedico = new ConverterAutoComplete(Medico.class, medicoFacade);
        }

        return converterMedico;
    }

    public AfastamentoFacade getAfastamentoFacade() {
        return afastamentoFacade;
    }

    public void setAfastamentoFacade(AfastamentoFacade afastamentoFacade) {
        this.afastamentoFacade = afastamentoFacade;
    }

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    public void setContratoFPFacade(ContratoFPFacade contratoFPFacade) {
        this.contratoFPFacade = contratoFPFacade;
    }

    public TipoAfastamentoFacade getTipoAfastamentoFacade() {
        return tipoAfastamentoFacade;
    }

    public void setTipoAfastamentoFacade(TipoAfastamentoFacade tipoAfastamentoFacade) {
        this.tipoAfastamentoFacade = tipoAfastamentoFacade;
    }

    public List<ContratoFP> completaContratoFP(String parte) {
        return afastamentoFacade.listaFiltrandoContratoFPVigente(parte.trim());
    }

    public List<CID> completaCids(String parte) {
        return CIDFacade.listaFiltrando(parte.trim(), "descricao", "codigoDaCid");
    }

    public List<TipoAfastamento> completaTipoAfastamento(String parte) {
        return tipoAfastamentoFacade.buscarTiposAfastamentoPorDescricaoCodigoAtivo(parte.trim());
    }

    public List<ItemEnvioDadosRBPonto> getItensEnvioDadosRBPonto() {
        return itensEnvioDadosRBPonto;
    }

    public void setItensEnvioDadosRBPonto(List<ItemEnvioDadosRBPonto> itensEnvioDadosRBPonto) {
        this.itensEnvioDadosRBPonto = itensEnvioDadosRBPonto;
    }

    public List<Medico> completaMedico(String parte) {
        return medicoFacade.listaFiltrandoMedico(parte);
    }

    public void preencherCarencia(SelectEvent a) {
        if (a.getObject() != null) {
            TipoAfastamento t = (TipoAfastamento) a.getObject();
            preencherCarencia(t);
        }
    }

    public void preencherCarencia(TipoAfastamento t) {
        if (t != null) {
            if (t.getClasseAfastamento().equals(ClasseAfastamento.AFASTAMENTO)) {
                selecionado.setDiasMaximoPermitido(t.getDiasMaximoPermitido());

                if (t.getCarencia() != null) {
                    ((Afastamento) selecionado).setCarencia(t.getCarencia().intValue());
                } else {
                    ((Afastamento) selecionado).setCarencia(null);
                }
            } else {
                ((Afastamento) selecionado).setCarencia(null);
            }
            atualizarCampos();
        }
    }

    @URLAction(mappingId = "verAfastamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        getEventoPorIdentificador();
        carregarEnvioDadosPonto();
    }

    @URLAction(mappingId = "editarAfastamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        selecionado.setDiasMaximoPermitido(selecionado.getTipoAfastamento().getDiasMaximoPermitido());
        carregarEnvioDadosPonto();
    }

    @URLAction(mappingId = "novoAfastamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataCadastro(new Date());
    }

    public void concluir() {
        if (Util.validaCampos(selecionado)) {
            try {
                validarTipoPrevidenciaParaTipoAfastamento();
                afastamentoFacade.validarCampos(selecionado);
                Afastamento afastamentoProrrogacao = afastamentoFacade.buscarAfastamentoProrrogacao(selecionado);
                if (afastamentoProrrogacao != null && selecionado.getCarencia() != null && selecionado.getCarencia() > 0) {
                    selecionado.setCarencia(0);
                    FacesUtil.executaJavaScript("dlgConcluir.show()");
                } else {
                    salvar();
                }
            } catch (ValidacaoException ve) {
                FacesUtil.printAllFacesMessages(ve.getMensagens());
            } catch (Exception e) {
                logger.error("Erro ao salvar o afastamento {}", e);
            }
        }
    }

    @Override
    public void salvar() {
        try {
            selecionado.setQuantidadeDias(selecionado.getTotalDias());
            if (selecionado.getRetornoInformado() && selecionado.getDataRetornoInformado() == null) {
                selecionado.setDataRetornoInformado(new Date());
            }
            if (isOperacaoNovo()) {
                afastamentoFacade.salvarNovo(selecionado);
            } else {
                getFacede().salvar(selecionado);
            }
            processarPA();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Salvo com sucesso!", ""));
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Ocorreu um erro: ", e.getMessage()));
        }
    }

    public void processarPA() {
        if (selecionado.getTipoAfastamento().getAlongarPeriodoAquisitivo()) {
            excluirPA();
            periodoAquisitivoFLFacade.gerarPeriodosAquisitivos(selecionado.getContratoFP(), SistemaFacade.getDataCorrente(), null);
        }
    }

    public void excluirPA() {

        //esse método vai recuperar o ultimo período concedido, ou com terço de férias automático, usar a data inicio como referencia e excluir, dali pra cima,
        //se não tiver nenhum concedido ou terço, vai excluir todos, não concedidos.

        PeriodoAquisitivoFL ultimoPeriodoConcedidoLicenca = periodoAquisitivoFLFacade.
            recuperaUltimoPeriodoConcedidoPorContratoETipoPA(selecionado.getContratoFP(), TipoPeriodoAquisitivo.LICENCA);

        PeriodoAquisitivoFL ultimoPeriodoConcedidoFerias = periodoAquisitivoFLFacade.
            recuperaUltimoPeriodoConcedidoPorContratoETipoPA(selecionado.getContratoFP(), TipoPeriodoAquisitivo.FERIAS);

        PeriodoAquisitivoFL ultimoPeriodoComTercoAutomatico = periodoAquisitivoFLFacade.
            recuperaUltimoPANaoCondedidoComTercoAutomaticoPorContrato(selecionado.getContratoFP());

        Date dataReferenciaLicenca = null;

        if (ultimoPeriodoConcedidoLicenca != null) {
            dataReferenciaLicenca = ultimoPeriodoConcedidoLicenca.getInicioVigencia();
        }

        Date dataReferenciaFerias = null;

        if (ultimoPeriodoConcedidoFerias != null) {
            if (ultimoPeriodoComTercoAutomatico != null) {
                if (ultimoPeriodoComTercoAutomatico.getInicioVigencia().after(ultimoPeriodoConcedidoFerias.getInicioVigencia())) {
                    dataReferenciaFerias = ultimoPeriodoComTercoAutomatico.getInicioVigencia();
                } else {
                    dataReferenciaFerias = ultimoPeriodoConcedidoFerias.getInicioVigencia();
                }
            } else {
                dataReferenciaFerias = ultimoPeriodoConcedidoFerias.getInicioVigencia();
            }
        }

        List<PeriodoAquisitivoFL> periodosNaoConcedidosParaExcluirLicenca = periodoAquisitivoFLFacade.recuperaPeriodosNaoConcedidosPorContratoETipoPA(
            selecionado.getContratoFP(), dataReferenciaLicenca, TipoPeriodoAquisitivo.LICENCA);

        List<PeriodoAquisitivoFL> periodosNaoConcedidosParaExcluirFerias = periodoAquisitivoFLFacade.recuperaPeriodosNaoConcedidosPorContratoETipoPA(
            selecionado.getContratoFP(), dataReferenciaFerias, TipoPeriodoAquisitivo.FERIAS);


        if (periodosNaoConcedidosParaExcluirLicenca != null) {
            selecionado.getContratoFP().getPeriodosAquisitivosFLs().removeAll(periodosNaoConcedidosParaExcluirLicenca);
            for (PeriodoAquisitivoFL paNaoConcedidosLicenca : periodosNaoConcedidosParaExcluirLicenca) {
                periodoAquisitivoFLFacade.remover(paNaoConcedidosLicenca);
            }
        }

        if (periodosNaoConcedidosParaExcluirFerias != null) {
            selecionado.getContratoFP().getPeriodosAquisitivosFLs().removeAll(periodosNaoConcedidosParaExcluirFerias);
            for (PeriodoAquisitivoFL paNaoConcedidosFerias : periodosNaoConcedidosParaExcluirFerias) {
                periodoAquisitivoFLFacade.remover(paNaoConcedidosFerias);
            }
        }
    }

    private void validarTipoPrevidenciaParaTipoAfastamento() {
        ValidacaoException ve = new ValidacaoException();

        tipoAfastamentoTipoPrevidenciaFPList =  tipoAfastamentoFacade.recuperarTipoAfastamentoTipoPrevidenciaFPPorTipoAfastamento(selecionado.getTipoAfastamento().getId());

        PrevidenciaVinculoFP previdenciaVinculoFP = selecionado.getContratoFP().getPrevidenciaVinculoFPVigente();
        TipoPrevidenciaFP tipoPrevidenciaFP = previdenciaVinculoFP.getTipoPrevidenciaFP();

        if (tipoAfastamentoTipoPrevidenciaFPList != null && tipoPrevidenciaFP != null && !tipoAfastamentoTipoPrevidenciaFPList.isEmpty()) {
            boolean afastamentoPermitido = tipoAfastamentoTipoPrevidenciaFPList.stream()
                .anyMatch(tipoAfast -> tipoPrevidenciaFP.getId().equals(tipoAfast.getTipoPrevidenciaFP().getId()));

            if (!afastamentoPermitido) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Afastamento não permitido para o tipo de previdência do servidor.");
                ve.lancarException();
            }
        }

    }

    public void informarCargoComissaoOuFuncaoGratificada() {
        if (selecionado.getTermino() != null) {
            CargoConfianca cargoConfianca = cargoConfiancaFacade.recuperaCargoConfiancaVigente(selecionado.getContratoFP(), selecionado.getTermino());
            FuncaoGratificada funcaoGratificada = funcaoGratificadaFacade.recuperaFuncaoGratificadaVigente(selecionado.getContratoFP(), selecionado.getTermino());

            if (cargoConfianca != null && cargoConfianca.getId() != null) {
                FacesUtil.addAtencao("Este servidor possui Acesso a Cargo em Comissão neste contrato.");
            }
            if (funcaoGratificada != null && funcaoGratificada.getId() != null) {
                FacesUtil.addAtencao("Este servidor possui Acesso a Função Gratificada neste contrato.");
            }
        }
    }

    private boolean estaEntreDatas(Date dataBase, Date inicio, Date termino) {
        return dataBase.after(inicio) && dataBase.before(termino);
    }

    public void sugeriDataFinal() {
        if (((Afastamento) selecionado).getInicio() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(((Afastamento) selecionado).getInicio());
            if (selecionado.getDiasMaximoPermitido() != null && selecionado.getDiasMaximoPermitido() > 0) {
                calendar.add(Calendar.DAY_OF_MONTH, selecionado.getDiasMaximoPermitido() - 1);
                ((Afastamento) selecionado).setTermino(calendar.getTime());
            }
        }
        informarCargoComissaoOuFuncaoGratificada();
    }

    public void validaData() {
        if (((Afastamento) selecionado).getInicio() != null && ((Afastamento) selecionado).getTermino() != null) {
            if (((Afastamento) selecionado).getInicio().after(((Afastamento) selecionado).getTermino())) {
                FacesUtil.addWarn("Atenção!", "A Data de Término não pode ser anterior a Data de Início.");
                sugeriDataFinal();
            } else {
                if (quantidadeDiasEntreDatas().compareTo(selecionado.getDiasMaximoPermitido() != null ? selecionado.getDiasMaximoPermitido() : 0) > 0) {
                    FacesUtil.addWarn("Atenção!", "O intervalo entre a data de início e término ultrapassa o número máximo de dias permitido.");
                    sugeriDataFinal();
                }
            }
        }
    }

    private Integer quantidadeDiasEntreDatas() {
        DateTime inicio = new DateTime(((Afastamento) selecionado).getInicio());
        DateTime fim = new DateTime(((Afastamento) selecionado).getTermino());

        return Days.daysBetween(inicio, fim).getDays();
    }

    public List<AtoLegal> completarAtosLegais(String parte) {
        return atoLegalFacade.listaFiltrandoAtoLegal(parte);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/afastamento/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public boolean isExigeSindicato() {
        return selecionado.getTipoAfastamento() != null && (selecionado.getTipoAfastamento().getExigeSindicato() != null ? selecionado.getTipoAfastamento().getExigeSindicato() : false);
    }

    public void enviarEventoEsocial() {
        try {
            ConfiguracaoEmpregadorESocial configuracao = configuracaoEmpregadorESocialFacade.buscarEmpregadorPorVinculoFP(selecionado.getContratoFP());
            validarEnvioEventoEsocial(configuracao);
            PessoaFisica pessoaFisica = selecionado.getContratoFP().getMatriculaFP().getPessoa();
            selecionado.getContratoFP().getMatriculaFP().setPessoa(pessoaFisicaFacade.recuperarComDocumentos(pessoaFisica.getId()));
            afastamentoFacade.enviarS2230ESocial(configuracao, selecionado);
            redireciona();
            FacesUtil.addOperacaoRealizada("Evento enviado com sucesso! Aguarde seu processamento para receber a situação.");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            FacesUtil.executaJavaScript("aguarde.hide()");
        }
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    private void validarEnvioEventoEsocial(ConfiguracaoEmpregadorESocial configuracao) {
        ValidacaoException ve = new ValidacaoException();
        if (configuracao == null || configuracao.getCertificado() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado configuração do empregador do servidor.");
        }
        ve.lancarException();
    }

    public boolean isProrrogacaoSetarCarenciaNulo() {
        if(selecionado == null || selecionado.getContratoFP() == null || selecionado.getTipoAfastamento() == null || selecionado.getInicio() == null){
            return false ;
        }

        Long id = selecionado.getContratoFP().getId();
        int intervaloDias = selecionado.getTipoAfastamento().getIntervaloProrrogacaoDias();
        Date dataIntervaloSubtraida = DataUtil.removerDias(selecionado.getInicio(), intervaloDias);
        Date dataIntervaloSomada = null;
        if(selecionado.getTermino() != null){
             dataIntervaloSomada = DataUtil.adicionaDias(selecionado.getTermino(), intervaloDias);
        }
        String codigo = String.valueOf(selecionado.getTipoAfastamento().getCodigo());
        boolean afastamentoProrrogacao = afastamentoFacade.isAfastamentoProrrogacao(id, dataIntervaloSubtraida, dataIntervaloSomada, selecionado.getInicio(), selecionado.getTermino(), codigo);
        if (afastamentoProrrogacao) {
            selecionado.setCarencia(null);
            return true;
        } else {
            if(selecionado.getTermino() == null){
                preencherCarencia(selecionado.getTipoAfastamento());
            }
            return false;
        }
    }

    private void getEventoPorIdentificador() {
        selecionado.setEventosEsocial(configuracaoEmpregadorESocialFacade.getRegistroESocialFacade().getEventoPorIdentificador(selecionado.getId()));
    }

    private void atualizarCampos() {
        selecionado.setTermino(null);
    }

    private void carregarEnvioDadosPonto(){
        itensEnvioDadosRBPonto = envioDadosRBPontoFacade.buscarItensEnvioDadosPontoPorIdentificador(selecionado.getId(), TipoInformacaoEnvioRBPonto.AFASTAMENTO);
    }
}
