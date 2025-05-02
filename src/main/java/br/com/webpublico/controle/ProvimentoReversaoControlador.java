package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.usertype.ModalidadeContratoFPData;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.rh.TipoAutorizacaoRH;
import br.com.webpublico.enums.rh.TipoPeq;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Claudio
 * Date: 19/07/13
 * Time: 14:39
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "provimentoReversaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoProvimentoReversao", pattern = "/provimentoreversao/novo/", viewId = "/faces/rh/administracaodepagamento/provimentoreversao/edita.xhtml"),
    @URLMapping(id = "editarProvimentoReversao", pattern = "/provimentoreversao/editar/#{provimentoReversaoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/provimentoreversao/edita.xhtml"),
    @URLMapping(id = "listarProvimentoReversao", pattern = "/provimentoreversao/listar/", viewId = "/faces/rh/administracaodepagamento/provimentoreversao/lista.xhtml"),
    @URLMapping(id = "verProvimentoReversao", pattern = "/provimentoreversao/ver/#{provimentoReversaoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/provimentoreversao/edita.xhtml")
})
public class ProvimentoReversaoControlador extends PrettyControlador<ProvimentoReversao> implements Serializable, CRUD {

    @EJB
    private ProvimentoReversaoFacade provimentoReversaoFacade;
    @EJB
    private ProvimentoFPFacade provimentoFPFacade;
    private ConverterAutoComplete converterAposentadoria;
    private Arquivo arquivo;
    private Arquivo marcadoRemover;
    private FileUploadEvent fileUploadEvent;
    private UploadedFile file;
    private UploadedFile fileSelecionado;
    private StreamedContent fileDownload;

    private ProgressaoPCS progressaoPCSPai;
    private CategoriaPCS categoriaPCSfilha;
    private EnquadramentoPCS enquadramentoPCS;
    private PlanoCargosSalarios planoCargosSalarios;
    private ProvimentoFP provimentoFP;
    private ContratoFP contratoFPReativado;
    private final long EM_EXERCICIO = 1L;
    private final long APOSENTADORIA = 7L;
    private boolean podeAlterarRecursoHorarioLotacao;

    private ContratoFPCargo ultimoContratoFPCargo;


    // Representações dos selecionados
    private EnquadramentoFuncional enquadramentoFuncional;
    private ModalidadeContratoFPData modalidadeContratoFPData;
    private PrevidenciaVinculoFP previdenciaVinculoFP;
    private OpcaoValeTransporteFP opcaoValeTransporteFP;
    private HorarioContratoFP horarioContratoFP;
    private LotacaoFuncional lotacaoFuncional;
    private HierarquiaOrganizacional hierarquiaOrganizacionalLotacaoFuncional;
    private SindicatoVinculoFP sindicatoVinculoFP;
    private AssociacaoVinculoFP associacaoVinculoFP;
    private ContratoVinculoDeContrato contratoVinculoDeContrato;
    private RecursoDoVinculoFP recursoDoVinculoFP;
    private ContratoFPCargo contratoFPCargo;

    // Listas
    List<EnquadramentoFuncional> enquadramentos;
    List<ModalidadeContratoFPData> modalidadeContratoFPDatas;
    private List<PrevidenciaVinculoFP> previdencias;
    private List<OpcaoValeTransporteFP> opcoesValeTransporte;
    private List<HorarioContratoFP> horarios;
    private List<SindicatoVinculoFP> sindicatos;
    private List<AssociacaoVinculoFP> associacoes;
    private List<ContratoVinculoDeContrato> vinculoDeContratos;
    private List<RecursoDoVinculoFP> recursoDoVinculoFPs;
    private List<ContratoFPCargo> contratoFPCargos;

    public ProvimentoReversaoControlador() {
        super(ProvimentoReversao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return provimentoReversaoFacade;
    }

    public ContratoFP getContratoFPReativado() {
        return contratoFPReativado;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public UploadedFile getFileSelecionado() {
        return fileSelecionado;
    }

    public void setFileSelecionado(UploadedFile fileSelecionado) {
        this.fileSelecionado = fileSelecionado;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public StreamedContent getFileDownload() {
        return fileDownload;
    }

    public void removerArquivo() {
        if (selecionado != null) {
            this.arquivo = null;
            this.selecionado.getLaudoProvimentoReversao().setArquivo(null);
            this.selecionado.getLaudoProvimentoReversao().setDescricao(null);
            fileSelecionado = null;
            fileDownload = null;
        }
    }

    public void setFileDownload(StreamedContent fileDownload) {
        this.fileDownload = fileDownload;
    }

    @URLAction(mappingId = "novoProvimentoReversao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        fileUploadEvent = null;
        arquivo = new Arquivo();
        marcadoRemover = null;
        fileDownload = null;

        progressaoPCSPai = null;
        categoriaPCSfilha = null;
        enquadramentoPCS = new EnquadramentoPCS();
        planoCargosSalarios = new PlanoCargosSalarios();
        enquadramentos = new ArrayList<>();
        podeAlterarRecursoHorarioLotacao = provimentoReversaoFacade.hasAutorizacaoEspecialRH(provimentoReversaoFacade.getSistemaFacade().getUsuarioCorrente(), TipoAutorizacaoRH.PERMITIR_ALTERAR_EXCLUIR_RECURSOFP_HORARIO_LOTACAO);
    }

    @URLAction(mappingId = "verProvimentoReversao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        contratoFPReativado = provimentoReversaoFacade.getContratoFPFacade().recuperar(selecionado.getContratoFP().getId());
        recuperaTipoAposentadoria();
        arquivo = new Arquivo();
        marcadoRemover = null;
        fileUploadEvent = null;
        fileDownload = null;
        if (selecionado.getLaudoProvimentoReversao().getArquivo() != null) {
            carregaArquivo();
        }
        carregarObjetosParaVerOuEditar();
    }

    @URLAction(mappingId = "editarProvimentoReversao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        contratoFPReativado = provimentoReversaoFacade.getContratoFPFacade().recuperar(selecionado.getContratoFP().getId());
        recuperaTipoAposentadoria();
        arquivo = new Arquivo();
        marcadoRemover = null;
        fileUploadEvent = null;
        fileDownload = null;
        podeAlterarRecursoHorarioLotacao = provimentoReversaoFacade.hasAutorizacaoEspecialRH(
            provimentoReversaoFacade.getSistemaFacade().getUsuarioCorrente(),
            TipoAutorizacaoRH.PERMITIR_ALTERAR_EXCLUIR_RECURSOFP_HORARIO_LOTACAO
        );
        if (selecionado.getLaudoProvimentoReversao().getArquivo() != null) {
            carregaArquivo();
        }
        carregarObjetosParaVerOuEditar();
        ordenarCargos();
    }

    public void validarAposentadoriaPosteriorAoProvimentoReversao(){
        ValidacaoException ve = new ValidacaoException();
        if (provimentoReversaoFacade.getAposentadoriaFacade().aposentadoriaPosteriorAoProvimentoReversao(selecionado.getAposentadoria())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível excluir este registro, pois há uma aposentadoria posterior.");
        }
        ve.lancarException();
    }

    public void validarRegistroPosteriorParaExcluir() {
        ValidacaoException ve = new ValidacaoException();
        ordenarListasContratoSelecionado();
        if (selecionado.getEnquadramentoFuncional() != null && selecionado.getEnquadramentoFuncional().getInicioVigencia().before(enquadramentos.get(0).getInicioVigencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível excluir a reversão, pois existem registros posteriores de 'Enquadramento funcional' que impedem a exclusão.");
        }
        if (selecionado.getModalidadeContratoFPData() != null && selecionado.getModalidadeContratoFPData().getInicioVigencia().before(modalidadeContratoFPDatas.get(0).getInicioVigencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível excluir a reversão, pois existem registros posteriores de 'Modalidade de Contrato' que impedem a exclusão.");
        }
        if (selecionado.getPrevidenciaVinculoFP() != null && selecionado.getPrevidenciaVinculoFP().getInicioVigencia().before(previdencias.get(0).getInicioVigencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível excluir a reversão, pois existem registros posteriores de 'Previdência' que impedem a exclusão.");
        }
        if (selecionado.getOpcaoValeTransporteFP() != null && selecionado.getOpcaoValeTransporteFP().getInicioVigencia().before(opcoesValeTransporte.get(0).getInicioVigencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível excluir a reversão, pois existem registros posteriores de 'Vale Transporte' que impedem a exclusão.");
        }

        HorarioContratoFP horarioContratoUltimo = horarios.get(0);
        ordenarListaDescendentemente(horarioContratoUltimo.getLocalTrabalho());
        LotacaoFuncional ultimaLotacao = horarioContratoUltimo.getLocalTrabalho().isEmpty() ? null : horarioContratoUltimo.getLocalTrabalho().get(0);
        if (ultimaLotacao != null && selecionado.getLotacaoFuncional() != null && selecionado.getLotacaoFuncional().getInicioVigencia().before(ultimaLotacao.getInicioVigencia())) {
            ve.adicionarMensagemDeCampoObrigatorio("Não é possível excluir a reversão, pois existem registros posteriores de local de trabalho (Lotação Funcional), que impedem a exclusão..");
        }


        if (selecionado.getHorarioContratoFP() != null && selecionado.getHorarioContratoFP().getInicioVigencia().before(horarios.get(0).getInicioVigencia())) {
            ve.adicionarMensagemDeCampoObrigatorio("Não é possível excluir a reversão, pois existem registros posteriores de horário de trabalho (Lotação Funcional), que impedem a exclusão..");
        }
        if (selecionado.getSindicatoVinculoFP() != null && selecionado.getSindicatoVinculoFP().getInicioVigencia().before(sindicatos.get(0).getInicioVigencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível excluir a reversão, pois existem registros posteriores de 'Sindicato' que impedem a exclusão.");
        }
        if (selecionado.getAssociacaoVinculoFP() != null && selecionado.getAssociacaoVinculoFP().getInicioVigencia().before(sindicatos.get(0).getInicioVigencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível excluir a reversão, pois existem registros posteriores de 'Assosiação' que impedem a exclusão.");
        }
        if (selecionado.getContratoVinculoDeContrato() != null && selecionado.getContratoVinculoDeContrato().getInicioVigencia().before(sindicatos.get(0).getInicioVigencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível excluir a reversão, pois existem registros posteriores de 'Vínculo (E-Consig)' que impedem a exclusão.");
        }
        if (selecionado.getRecursoDoVinculoFP() != null && selecionado.getRecursoDoVinculoFP().getInicioVigencia().before(sindicatos.get(0).getInicioVigencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível excluir a reversão, pois existem registros posteriores de 'Recursos de Folha de Pagamento' que impedem a exclusão.");
        }
        if (selecionado.getContratoFPCargo() != null && selecionado.getContratoFPCargo().getInicioVigencia().before(sindicatos.get(0).getInicioVigencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é possível excluir a reversão, pois existem registros posteriores de 'Cargos' que impedem a exclusão.");
        }
        ve.lancarException();
    }

    @Override
    public void excluir() {

        recuperarObjeto();
                try {
                    if (provimentoReversaoFacade.getContratoFPFacade().temFichaNaCompetencia(selecionado.getContratoFP(), selecionado.getInicioVigencia())) {
                        FacesUtil.addWarn("Atenção!", "Este registro não pode ser excluído porque há ficha financeira calculada para o contrato FP durante o período de vigência desta reversão.");
                        return;
                    }
                    if (provimentoReversaoFacade.getContratoFPFacade().temFichaNaCompetencia(selecionado.getAposentadoria(), selecionado.getInicioVigencia())) {
                        FacesUtil.addWarn("Atenção!", "Este registro não pode ser excluído porque há ficha financeira calculada para a aposentadoria durante o período de vigência desta reversão.");
                        return;
                    }

                    validarAposentadoriaPosteriorAoProvimentoReversao();
                    validarRegistroPosteriorParaExcluir();
                    permitirManipularRegistro();
                    carregarObjetosParaVerOuEditar();
                    ordenarListasContratoSelecionado();
                    atribuirFimDeVigenciaDoContratoNaLotacaoFuncionalAnterior();

                    selecionado.setEnquadramentoFuncionalAnterior((EnquadramentoFuncional) alterarFinalDeVigenciaDoRegistroAnteriorAoAdicionadoPelaReversao(selecionado.getEnquadramentoFuncional(), enquadramentos, selecionado.getFimVigenciaAnterior()));
                    selecionado.setModalidadeContratoFPDataAnterior((ModalidadeContratoFPData) alterarFinalDeVigenciaDoRegistroAnteriorAoAdicionadoPelaReversao(selecionado.getModalidadeContratoFPData(), modalidadeContratoFPDatas, selecionado.getFimVigenciaAnterior()));
                    selecionado.setPrevidenciaAnterior((PrevidenciaVinculoFP) alterarFinalDeVigenciaDoRegistroAnteriorAoAdicionadoPelaReversao(selecionado.getPrevidenciaVinculoFP(), previdencias, selecionado.getFimVigenciaAnterior()));
                    selecionado.setOpcaoValeTransporteAnterior((OpcaoValeTransporteFP) alterarFinalDeVigenciaDoRegistroAnteriorAoAdicionadoPelaReversao(selecionado.getOpcaoValeTransporteFP(), opcoesValeTransporte, selecionado.getFimVigenciaAnterior()));
                    selecionado.setHorarioAnterior((HorarioContratoFP) alterarFinalDeVigenciaDoRegistroAnteriorAoAdicionadoPelaReversao(selecionado.getHorarioContratoFP(), horarios, selecionado.getFimVigenciaAnterior()));
                    selecionado.setSindicatoAnterior((SindicatoVinculoFP) alterarFinalDeVigenciaDoRegistroAnteriorAoAdicionadoPelaReversao(selecionado.getSindicatoVinculoFP(), sindicatos, selecionado.getFimVigenciaAnterior()));
                    selecionado.setAssociacaoAnterior((AssociacaoVinculoFP) alterarFinalDeVigenciaDoRegistroAnteriorAoAdicionadoPelaReversao(selecionado.getAssociacaoVinculoFP(), associacoes, selecionado.getFimVigenciaAnterior()));
                    selecionado.setContratoVinculoDeContratoAnterior((ContratoVinculoDeContrato) alterarFinalDeVigenciaDoRegistroAnteriorAoAdicionadoPelaReversao(selecionado.getContratoVinculoDeContrato(), associacoes, selecionado.getFimVigenciaAnterior()));
                    selecionado.setRecursoDoVinculoFPAnterior((RecursoDoVinculoFP) alterarFinalDeVigenciaDoRegistroAnteriorAoAdicionadoPelaReversao(selecionado.getRecursoDoVinculoFP(), recursoDoVinculoFPs, selecionado.getFimVigenciaAnterior()));

                    selecionado.setContratoFPCargoAnterior((ContratoFPCargo) alterarFinalDeVigenciaCargoDoRegistroAnteriorAoAdicionadoPelaReversao(selecionado.getContratoFPCargo(), contratoFPCargos, null));
                    ultimoContratoFPCargo = (ContratoFPCargo) alterarFinalDeVigenciaCargoDoRegistroAnteriorAoAdicionadoPelaReversao(selecionado.getContratoFPCargo(), contratoFPCargos, null);

                    excluirRevertendoProvimentoReversao(selecionado, contratoFPReativado, ultimoContratoFPCargo);
                } catch (ValidacaoException ve) {
                    FacesUtil.printAllFacesMessages(ve.getMensagens());
                } catch (ExcecaoNegocioGenerica e) {
                    FacesUtil.addError(e.getMessage(), "Houve um erro ao tentar excluir o Provimento de Reversão. Entre em contato com o Administrador.");
                }
    }


    public void excluirRevertendoProvimentoReversao(ProvimentoReversao provimentoReversao, ContratoFP contratoFPReativado, ContratoFPCargo ultimoContratoFPCargo) throws ExcecaoNegocioGenerica {

        try {
            TipoProvimento tipoProvimento = provimentoReversaoFacade.getEnquadramentoFuncionalFacade().getTipoProvimentoFacade().recuperaTipoProvimentoPorCodigo(TipoProvimento.REVERSAO);

            ProvimentoFP provimentoFP = provimentoFPFacade.recuperaProvimento(contratoFPReativado, tipoProvimento, provimentoReversao.getInicioVigencia());
            if (provimentoFP != null) {
                provimentoFP = provimentoFPFacade.recuperar(provimentoFP.getId());
                contratoFPReativado.setProvimentoFP(null);
                provimentoReversaoFacade.getContratoFPFacade().salvar(contratoFPReativado);
                provimentoFPFacade.remover(provimentoFP);
            }

            Aposentadoria aposentadoria = provimentoReversaoFacade.getAposentadoriaFacade().recuperar(provimentoReversao.getAposentadoria().getId());
            List<SituacaoContratoFP> situacoes = provimentoReversaoFacade.getSituacaoContratoFPFacade().recuperarSituacoesContratoFP(selecionado.getContratoFP());
            if (situacoes.size() > 1) {
                ordenarListaDescendentemente(situacoes);

                //recupera ultima situacaofuncional igual a aposentadoria
                SituacaoContratoFP ultimaAposentadoria = null;
                for (int i = situacoes.size() - 1; i >= 0; i--) {
                    if (situacoes.get(i).getSituacaoFuncional().getCodigo() == APOSENTADORIA &&
                        situacoes.get(i).getFinalVigencia().compareTo(DataUtil.getDataDiaAnterior(selecionado.getInicioVigencia()))==0) {
                        ultimaAposentadoria = situacoes.get(i);
                        break;
                    }
                }
                //volta final vigencia do contrato, para situacão anterior.
                contratoFPReativado.setFinalVigencia(DataUtil.getDataDiaAnterior(ultimaAposentadoria.getInicioVigencia()));
                provimentoReversaoFacade.getContratoFPFacade().salvar(contratoFPReativado);

                //reabre aposentadoria
                aposentadoria.setFinalVigencia(null);
                provimentoReversaoFacade.getAposentadoriaFacade().salvarAposentadoria(aposentadoria);
            }

            provimentoReversaoFacade.remover(provimentoReversao, ultimoContratoFPCargo);
            provimentoReversaoFacade.getConcessaoFeriasLicencaFacade().reprocessarSituacoesContrato(contratoFPReativado);

            FacesUtil.addInfo("", "Registro excluído com sucesso!");
            redireciona();
        } catch (ExcecaoNegocioGenerica ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }


    @Override
    public void salvar() {
        if (validaCampos()) {
            try {
                validarCamposObrigatoriosProvimentoReversao();
                temReversaoVigenteParaOContratoFP();

                if (operacao == Operacoes.NOVO) {
                    setarDataFinalVigenciaNoContrato(contratoFPReativado, null);

                    geraNovoProvimentoFP(contratoFPReativado, selecionado, TipoProvimento.REVERSAO, selecionado.getInicioVigencia());
                    validarAposentadoriaPosterior(contratoFPReativado, selecionado.getAposentadoria(), selecionado);
                    setarContratoNaReversao(selecionado, contratoFPReativado);
                    encerrarVigenciaAposentadoria();
                    //Salvar provimento do Enquadramento, e Enquadramento
                    if(selecionado.getEnquadramentoFuncional() != null && provimentoFP != null) {
                        provimentoReversaoFacade.getEnquadramentoFuncionalFacade().salvarNovo(selecionado.getEnquadramentoFuncional(), selecionado.getEnquadramentoFuncionalAnterior(), provimentoFP);
                    }

                    provimentoReversaoFacade.salvarNovo(selecionado, arquivo, fileUploadEvent);
                    contratoFPReativado = provimentoReversaoFacade.getContratoFPFacade().salvarRetornando(contratoFPReativado);
                    provimentoReversaoFacade.getConcessaoFeriasLicencaFacade().reprocessarSituacoesContrato(contratoFPReativado);

                } else {
                    validarAposentadoriaPosterior(contratoFPReativado, selecionado.getAposentadoria(), selecionado);
                    //Salvar provimento do Enquadramento, e Enquadramento
                    if (selecionado.getEnquadramentoFuncional() != null && provimentoFP != null) {
                        provimentoReversaoFacade.getEnquadramentoFuncionalFacade().salvarNovo(selecionado.getEnquadramentoFuncional(), selecionado.getEnquadramentoFuncionalAnterior(), provimentoFP);
                    }
                    setarContratoNaReversao(selecionado, contratoFPReativado);
                    provimentoReversaoFacade.salvar(selecionado, arquivo, fileUploadEvent, marcadoRemover, ultimoContratoFPCargo);
                    provimentoReversaoFacade.getConcessaoFeriasLicencaFacade().reprocessarSituacoesContrato(contratoFPReativado);
                }

            } catch (ExcecaoNegocioGenerica e) {
                FacesUtil.addError("Entre em contato com o Administrador.", "Ocorreu um erro ao tentar salvar o provimento de reversão.");
                return;
            } catch (ValidacaoException ve) {
                FacesUtil.printAllFacesMessages(ve.getMensagens());
                return;
            } catch (Exception e) {
                logger.error("Erro ao salvar Provimento reversao: " + e.getMessage());
                logger.debug("Detalhes do erro ao salvar provimento reversão. ", e);
                FacesUtil.addOperacaoNaoRealizada("Erro ao salvar provimento reversão. Detalhes: " + e.getMessage());
                redireciona();
                return;
            }

            if (operacao == Operacoes.NOVO) {
                FacesUtil.addInfo("", "Salvo com sucesso!");
                RequestContext.getCurrentInstance().update("FormularioDialog");
                RequestContext.getCurrentInstance().execute("dialogo.show();");
            } else {
                FacesUtil.addInfo("", "Salvo com sucesso!");
                redireciona();
            }
        }
    }

    public void validarAposentadoriaPosterior(ContratoFP contratoFP, Aposentadoria aposentadoria, ProvimentoReversao provimentoReversao) {
        ValidacaoException ve = new ValidacaoException();
        Date dataInicioAposentadoria = provimentoReversaoFacade.getAposentadoriaFacade().aposentadoriaPosterior(contratoFP, aposentadoria);
        if(dataInicioAposentadoria != null){
            if(provimentoReversao.getFinalVigencia() == null ||(DataUtil.getDataDiaAnterior(dataInicioAposentadoria).compareTo(provimentoReversao.getFinalVigencia())<=0)){
                ve.adicionarMensagemDeCampoObrigatorio("O registro de reversão não pode ser concomitante com a nova aposentadoria existente.");
            }
        }
        ve.lancarException();
    }

    public void encerrarVigenciaAposentadoria() {
        Aposentadoria aposentadoria = provimentoReversaoFacade.getAposentadoriaFacade().recuperar(selecionado.getAposentadoria().getId());

        ValidacaoException ve = new ValidacaoException();

        if (selecionado.getInicioVigencia().compareTo(aposentadoria.getInicioVigencia())<=0) {
            ve.adicionarMensagemDeCampoObrigatorio("A data de inicio de vigência da reversão deve ser posterior ao inicio de vigência da aposentadoria.");
        } else {
            aposentadoria.setFinalVigencia(DataUtil.getDataDiaAnterior(selecionado.getInicioVigencia()));
            provimentoReversaoFacade.getAposentadoriaFacade().salvarAposentadoria(aposentadoria);
        }

        ve.lancarException();
    }

    public void temReversaoVigenteParaOContratoFP() {
        ValidacaoException ve = new ValidacaoException();
            if (provimentoReversaoFacade.temReversaoVigenteParaOContratoFP(selecionado, selecionado.getInicioVigencia(), selecionado.getFinalVigencia())) {
                ve.adicionarMensagemDeCampoObrigatorio("Já existe uma reversão vigente para esse contrato.");
            }
        ve.lancarException();
    }

    public boolean validaCampos() {

        boolean valido = Util.validaCampos(selecionado);
        if (valido) {
            if (selecionado.getFinalVigencia() != null && selecionado.getFinalVigencia().before(selecionado.getInicioVigencia())) {
                FacesUtil.addError("", "O Final de Vigência não pode ser anterior ao Início de Vigência!");
                valido = false;
            }

            if (selecionado.getTipoAposentadoria().getCodigo().equals("3")) {
                if (selecionado.getLaudoProvimentoReversao().getDataLaudo() == null) {
                    FacesUtil.addError("", "O campo Data do Laudo é obrigatório!");
                    valido = false;
                }
                if (selecionado.getLaudoProvimentoReversao().getDescricao() == null || selecionado.getLaudoProvimentoReversao().getDescricao().trim().isEmpty()) {
                    FacesUtil.addError("", "O campo Descrição do Laudo é obrigatório!");
                    valido = false;
                }

                if (operacao == Operacoes.EDITAR) {
                    if (selecionado.getLaudoProvimentoReversao().getArquivo() == null) {
                        FacesUtil.addError("", "O Arquivo do laudo é obrigatorio!");
                        valido = false;
                    }
                } else {
                    if (file == null) {
                        FacesUtil.addError("", "O Arquivo do laudo é obrigatorio!");
                        valido = false;
                    }
                }


            } else {
                if (selecionado.getLaudoProvimentoReversao().getMotivo() == null || selecionado.getLaudoProvimentoReversao().getMotivo().trim().isEmpty()) {
                    FacesUtil.addError("", "O campo Motivo é obrigatório!");
                    valido = false;
                }
                if (arquivo == null) {
                    FacesUtil.addError("", "O Arquivo referente ao motivo é obrigatorio!");
                    valido = false;
                }
            }
        }

        return valido;
    }

    public void validarCamposObrigatoriosProvimentoReversao() {
        ValidacaoException ve = new ValidacaoException();

        if (modalidadeContratoFPDatas.isEmpty() || selecionado.getModalidadeContratoFPData() == null && modalidadeContratoFPDatas.get(0).getFinalVigencia() != null && modalidadeContratoFPDatas.get(0).getFinalVigencia().before(selecionado.getInicioVigencia())) {
            ve.adicionarMensagemDeCampoObrigatorio("É obrigatório informar uma nova Modalidade de Contrato vigente, para o servidor.");
        }
        if (previdencias.isEmpty() || selecionado.getPrevidenciaVinculoFP() == null && previdencias.get(0).getFinalVigencia() != null && previdencias.get(0).getFinalVigencia().before(selecionado.getInicioVigencia())) {
            ve.adicionarMensagemDeCampoObrigatorio("É obrigatório informar uma nova opção de previdência vigente, para o servidor.");
        }

        //Validar se tem lotacao funcional vigente
        HorarioContratoFP horarioContratoUltimo = horarios.get(0);
        ordenarListaDescendentemente(horarioContratoUltimo.getLocalTrabalho());
        LotacaoFuncional ultimaLotacao = horarioContratoUltimo.getLocalTrabalho().isEmpty() ? null : horarioContratoUltimo.getLocalTrabalho().get(0);
        if (ultimaLotacao == null || selecionado.getLotacaoFuncional() == null && ultimaLotacao.getFinalVigencia() != null && ultimaLotacao.getFinalVigencia().before(selecionado.getInicioVigencia())) {
            ve.adicionarMensagemDeCampoObrigatorio("É obrigatório informar um local de trabalho (lotação funcional) vigente, para o novo horário de trabalho do servidor.");
        }
        if (horarios.isEmpty() || selecionado.getHorarioContratoFP() == null && horarios.get(0).getFinalVigencia() != null && horarios.get(0).getFinalVigencia().before(selecionado.getInicioVigencia())) {
            ve.adicionarMensagemDeCampoObrigatorio("É obrigatório informar um novo horário de trabalho vigente, para o servidor.");
        }

        if (vinculoDeContratos.isEmpty() || selecionado.getContratoVinculoDeContrato() == null && vinculoDeContratos.get(0).getFinalVigencia() != null && vinculoDeContratos.get(0).getFinalVigencia().before(selecionado.getInicioVigencia())) {
            ve.adicionarMensagemDeCampoObrigatorio("É obrigatório informar uma novo Vínculo (E-Consig) vigente, para o servidor.");
        }

        if (contratoFPCargos.isEmpty() || selecionado.getContratoFPCargo() == null && contratoFPCargos.get(0).getFimVigencia() != null && contratoFPCargos.get(0).getFimVigencia().before(selecionado.getInicioVigencia())) {
            ve.adicionarMensagemDeCampoObrigatorio("É obrigatório informar um cargo vigente, para o servidor.");
        }

        ve.lancarException();
    }

    public void validarInicioVigenciaProvimentoReversao() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("É obrigatório informar o campo Início de Vigência da Reversão na aba Dados Gerais.");
        }
        ve.lancarException();
    }

    public List<Aposentadoria> completaAposentadoriaInvalidez(String parte) {
        return provimentoReversaoFacade.getAposentadoriaFacade().recuperaAposentadoInvalidez(parte.trim());
    }

    public Converter getConverterAposentadoria() {
        if (converterAposentadoria == null) {
            converterAposentadoria = new ConverterAutoComplete(Aposentadoria.class, provimentoReversaoFacade.getAposentadoriaFacade());
        }
        return converterAposentadoria;
    }

    public void uploadArquivo(FileUploadEvent evento) {
        fileUploadEvent = evento;
    }

    public void handleFileUpload(FileUploadEvent event) throws IOException {
        fileUploadEvent = event;
        file = fileUploadEvent.getFile();
        fileSelecionado = file;
        if (selecionado.getLaudoProvimentoReversao().getArquivo() != null) {
            marcadoRemover = selecionado.getLaudoProvimentoReversao().getArquivo();
        }
        arquivo = new Arquivo();
        fileDownload = new DefaultStreamedContent(event.getFile().getInputstream(), event.getFile().getContentType(), event.getFile().getFileName());
        arquivo.setDescricao(file.getFileName());
        this.selecionado.getLaudoProvimentoReversao().setArquivo(arquivo);
    }

    public void carregaArquivo() {
        Arquivo arq = selecionado.getLaudoProvimentoReversao().getArquivo();
        arquivo = selecionado.getLaudoProvimentoReversao().getArquivo();
        if (arq != null) {
            try {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                for (ArquivoParte a : provimentoReversaoFacade.getArquivoFacade().recuperaDependencias(arq.getId()).getPartes()) {
                    buffer.write(a.getDados());
                }

                InputStream inputStream = new ByteArrayInputStream(buffer.toByteArray());
                fileDownload = new DefaultStreamedContent(inputStream, arq.getNome(), arq.getMimeType());
            } catch (Exception ex) {
                logger.error("Erro: ", ex);
            }
        }
    }

    public void recuperaTipoAposentadoria() {
        if (isAposentadoriaValida(selecionado.getAposentadoria())) {
            if (selecionado.getAposentadoria() != null && ((ProvimentoReversao) selecionado).getAposentadoria().getId() != null) {
                selecionado.setTipoAposentadoria(provimentoReversaoFacade.recuperaTipoAposentadoria(((ProvimentoReversao) selecionado).getAposentadoria()));
            } else {
                selecionado.setTipoAposentadoria(null);
            }
        }
    }

    private boolean isAposentadoriaValida(Aposentadoria aposentadoria) {
        try {
            int IDADE_MAX_PROVIMENTO_REVERSAO = 70;
            int IDADE_ATUAL = Integer.parseInt(DataUtil.getIdade(aposentadoria.getDataNascimento()));

            if (IDADE_ATUAL >= IDADE_MAX_PROVIMENTO_REVERSAO) {
                FacesUtil.addOperacaoNaoPermitida("Provimento de reversão só é permitido para servidores com menos de " + IDADE_MAX_PROVIMENTO_REVERSAO + " anos de idade. Idade atual do servidor selecionado: " + IDADE_ATUAL + " anos.");
                cancelarAposentadoria();
                return false;
            }
        } catch (NullPointerException npe) {
            FacesUtil.addOperacaoNaoRealizada("Erro ao recuperar a data de nascimento deste servidor.");
            cancelarAposentadoria();
            return false;
        }
        return true;
    }

    private void cancelarAposentadoria() {
        selecionado.setAposentadoria(null);
    }


    public void setarContratoNaReversao(ProvimentoReversao provimentoReversao, ContratoFP contratoFP) throws ExcecaoNegocioGenerica {
        provimentoReversao.setContratoFP(contratoFP);
    }

    public void setarDataFinalVigenciaNoContrato(ContratoFP contratoFP, Date dataFinal) {
        contratoFP.setFinalVigencia(dataFinal);
    }

    private void geraNovoProvimentoFP(ContratoFP contratoFP, ProvimentoReversao provimentoReversao, int tipoProvimento, Date dataProvimento) {
        ProvimentoFP provimentoFP = new ProvimentoFP();
        provimentoFP.setVinculoFP(contratoFP);
        provimentoFP.setTipoProvimento(provimentoReversaoFacade.getEnquadramentoFuncionalFacade().getTipoProvimentoFacade().recuperaTipoProvimentoPorCodigo(tipoProvimento));

        provimentoFP.setDataProvimento(dataProvimento);
        provimentoFP.setAtoLegal(provimentoReversao.getAtoLegal());
        provimentoFP.setSequencia(provimentoFPFacade.geraSequenciaPorVinculoFP(contratoFP));
        provimentoFPFacade.salvarNovo(provimentoFP);

        provimentoFP = (ProvimentoFP) provimentoFPFacade.recuperar(ProvimentoFP.class, provimentoFP.getId());
        contratoFP.setProvimentoFP(provimentoFP);

    }

    public void cancelarItensAbas() {
        cancelarEnquadramentoFuncional();
        cancelarModalidade();
        cancelarPrevidencia();
        cancelarLotacaoFuncional();
        cancelarHorarioContratoFP();
        cancelarSindicatoVinculoFP();
        cancelarAssociacaoVinculoFP();
        cancelarVinculoEConsig();
        cancelarRecurso();
        cancelarContratoFPCargo();


        if (selecionado.getEnquadramentoFuncional() != null || selecionado.getModalidadeContratoFPData() != null
            || selecionado.getPrevidenciaVinculoFP() != null || selecionado.getOpcaoValeTransporteFP() != null
            || selecionado.getLotacaoFuncional() != null || selecionado.getHorarioContratoFP() != null
            || selecionado.getSindicatoVinculoFP() != null || selecionado.getAssociacaoVinculoFP() != null
            || selecionado.getContratoVinculoDeContrato() != null || selecionado.getRecursoDoVinculoFP() != null
            || selecionado.getContratoFPCargo() != null) {
            selecionado = null;
            novo();
        }
        if (!provimentoReversaoFacade.getContratoFPFacade().podeExcluir(selecionado.getContratoFP(), selecionado.getInicioVigencia())) {
            FacesUtil.addWarn("Atenção!", "Existe folha calculada para este contrato!");
        }
        if(!provimentoReversaoFacade.podeExcluir(selecionado.getAposentadoria(), selecionado.getInicioVigencia())){
            FacesUtil.addWarn("Atenção!", "Existe folha calculada para esta Aposentadoria!");
        }

    }


    @Override
    public String getCaminhoPadrao() {
        return "/provimentoreversao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void validarServidorSelecionado(SelectEvent ev) {
        try {

            if (selecionado.getAposentadoria() == null) {
                return;
            }

            contratoFPReativado = provimentoReversaoFacade.getContratoFPFacade().recuperar(selecionado.getAposentadoria().getContratoFP().getId());

            if (contratoFPReativado == null) {
                return;
            }

            if (!provimentoReversaoFacade.isVinculoFPVivo(contratoFPReativado.getId())) {
                FacesUtil.addOperacaoNaoPermitida("O servidor selecionado possui registro de óbito. Por favor, selecione um servidor vivo.");
                FacesUtil.executaJavaScript("limparCamposDoAutoComplete('" + ev.getComponent().getClientId() + "')");
                selecionado.setContratoFP(null);
                return;
            }


            selecionado.setContratoFP(contratoFPReativado);
            recuperaTipoAposentadoria();

            atribuirListasDoContratoParaListasControlador();
            ordenarListasContratoSelecionado();

        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }


    public void validarDataProvimentoReversao(Date InicioVigenciaAtual, Date InicioVigenciaDoAnterior, String campo) {
        ValidacaoException ve = new ValidacaoException();
        if (InicioVigenciaDoAnterior != null) {
            if (InicioVigenciaAtual.compareTo(InicioVigenciaDoAnterior) <= 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Reversão deve ser superior ao Início de Vigência (" +
                    DataUtil.getDataFormatada(InicioVigenciaDoAnterior) + ") de " + campo + "  anterior.");
            }

            if (ve.temMensagens()) {
                throw ve;
            }
        }
    }

    public void limpar() {
        cancelarEnquadramentoFuncional();
    }

    public void carregarObjetosParaVerOuEditar() {
        selecionado.setContratoFP(provimentoReversaoFacade.getContratoFPFacade().recuperarParaReversao(contratoFPReativado.getId()));
        atribuirListasDoContratoParaListasControlador();
        ordenarListasContratoSelecionado();
    }

    private void atribuirListasDoContratoParaListasControlador() {
        enquadramentos = selecionado.getContratoFP().getEnquadramentos();
        if (enquadramentos.isEmpty()) {
            ValidacaoException ve = new ValidacaoException();
            ve.adicionarMensagemDeOperacaoNaoRealizada("O Servidor não possui um Enquadramento Funcional em vigência");
            ve.lancarException();
        }

        modalidadeContratoFPDatas = selecionado.getContratoFP().getModalidadeContratoFPDatas();
        previdencias = selecionado.getContratoFP().getPrevidenciaVinculoFPs();
        opcoesValeTransporte = selecionado.getContratoFP().getOpcaoValeTransporteFPs();
        recuperarHorariosDoContrato();
        sindicatos = selecionado.getContratoFP().getSindicatosVinculosFPs();
        associacoes = selecionado.getContratoFP().getAssociacaosVinculosFPs();
        vinculoDeContratos = selecionado.getContratoFP().getContratoVinculoDeContratos();
        recursoDoVinculoFPs = selecionado.getContratoFP().getRecursosDoVinculoFP();
        contratoFPCargos = selecionado.getContratoFP().getCargos();

    }

    public void ordenarListasContratoSelecionado() {
        ordenarListaDescendentemente(enquadramentos);
        ordenarListaDescendentemente(modalidadeContratoFPDatas);
        ordenarListaDescendentemente(previdencias);
        ordenarListaDescendentemente(opcoesValeTransporte);
        ordenarListaDescendentemente(horarios);
        ordenarListaDescendentemente(sindicatos);
        ordenarListaDescendentemente(associacoes);
        ordenarListaDescendentemente(vinculoDeContratos);
        ordenarListaDescendentemente(recursoDoVinculoFPs);
        ordenarListaDescendentemente(contratoFPCargos);
    }

    public boolean validarConfirmacao(ValidadorEntidade obj) {
        if (!Util.validaCampos(obj)) {
            return false;
        }
        try {
            obj.validarConfirmacao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            return false;
        }
        return true;
    }


    public void ordenarListaDescendentemente(List<?> lista) {
        if (lista.isEmpty() || !(lista.get(0) instanceof ContratoFPCargo)) {
            Collections.sort((List) lista, Collections.reverseOrder());
        }
    }

    private <T extends ValidadorVigencia> void alterarVigenciaUltimoRegistroLista(T vv, List<T> lista) {
        try {
            ordenarListaDescendentemente(lista);
            ValidadorVigencia antigo = lista.get(0); // Devem vir ordenadas decrescentemente
            if (antigo.getFimVigencia() == null) {
                antigo.setFimVigencia(DataUtil.getDataDiaAnterior(vv.getInicioVigencia()));
                lista = Util.adicionarObjetoEmLista(lista, (T) antigo);
            }
        } catch (IndexOutOfBoundsException ex) {
        }
    }

    private boolean jaExisteRegistroAdicionadoPeloProvimentoReversao(Object obj, String alias) {
        if (obj != null) {
            FacesUtil.addOperacaoNaoPermitida("Já existe um registro de " + alias + " adicionado a partir desta reversão. Verifique as informações e tente novamente.");
            return true;
        }
        return false;
    }

    public String getMensagemObservacaoDoRegistro(ValidadorVigencia objDaReversao, ValidadorVigencia objDaVez, List<? extends ValidadorVigencia> lista) {
        try {
            if (objDaReversao.equals(objDaVez)) {
                return "Registro adicionado por reversão.";
            }
        } catch (NullPointerException npe) {
        }


        try {
            int idx = lista.indexOf(objDaReversao);
            ValidadorVigencia objAnterior = lista.get(idx + 1);
            if (!objAnterior.equals(objDaVez)) {
                return "";
            }
            if (DataUtil.getDataDiaAnterior(objDaReversao.getInicioVigencia()).compareTo(objAnterior.getFimVigencia()) == 0) {
                return "Registro alterado automaticamente por esta reversão";
            }
        } catch (NullPointerException | IndexOutOfBoundsException e) {
        }

        return "";
    }

    public String getMensagemObservacaoDoVinculoEConsig(ContratoVinculoDeContrato objDaReversao, ContratoVinculoDeContrato objDaVez, List<ContratoVinculoDeContrato> lista) {
        try {
            if (objDaReversao.equalsID(objDaVez)) {
                return "Registro adicionado por reversão.";
            }
        } catch (NullPointerException npe) {
        }

        try {
            int idx = lista.indexOf(objDaReversao);

            if (objDaReversao.getId() == null) {
                ContratoVinculoDeContrato anterior = lista.get(idx + 1);
                if (!anterior.equals(objDaVez)) {
                    return "";
                }
                if (DataUtil.getDataDiaAnterior(objDaReversao.getInicioVigencia()).compareTo(anterior.getFimVigencia()) == 0) {
                    return "Registro alterado automaticamente por esta reversão";
                }
            }

            ContratoVinculoDeContrato objAnterior = lista.get(idx + 2);

            if (!objAnterior.equalsID(objDaVez)) {
                return "";
            }

            if (DataUtil.getDataDiaAnterior(objDaReversao.getInicioVigencia()).compareTo(objAnterior.getFimVigencia()) == 0) {
                return "Registro alterado automaticamente por esta reversão";
            }
        } catch (NullPointerException | IndexOutOfBoundsException e) {
        }

        return "";
    }


    private boolean validarVigenciaNovoObjetoAdicionadoEmLista(ValidadorVigencia vv, List<? extends ValidadorVigencia> objs) {
        if (objs == null || objs.isEmpty()) {
            return true;
        }
        ValidadorVigencia ultimo = objs.get(0);
        if (ultimo == null) {
            return true;
        }

        if (ultimo.getFimVigencia() != null) {
            if (vv.getInicioVigencia().compareTo(ultimo.getFimVigencia()) <= 0) {
                String dataNova = Util.formatterDiaMesAno.format(vv.getInicioVigencia());
                String dataAntiga = Util.formatterDiaMesAno.format(ultimo.getFimVigencia());
                FacesUtil.addOperacaoNaoPermitida("A data de inicio de vigência (" + dataNova + ") deste registro deve ser posterior a data de final de vigência (" + dataAntiga + ") do último registro já existente.");
                return false;
            }
            return true;
        }

        if (ultimo.getFimVigencia() == null && ultimo.getInicioVigencia() != null) {
            if (vv.getInicioVigencia().compareTo(ultimo.getInicioVigencia()) <= 0) {
                String dataNova = Util.formatterDiaMesAno.format(vv.getInicioVigencia());
                String dataAntiga = Util.formatterDiaMesAno.format(ultimo.getInicioVigencia());
                FacesUtil.addOperacaoNaoPermitida("A data de inicio de vigência (" + dataNova + ") deste registro deve ser posterior a data de inicio de vigência (" + dataAntiga + ") do último registro já existente.");
                return false;
            }
            return true;
        }

        return true;
    }


    public ValidadorVigencia alterarFinalDeVigenciaDoRegistroAnteriorAoAdicionadoPelaReversao(ValidadorVigencia objDaReversao, List<? extends ValidadorVigencia> lista, Date valorParaSetar) {
        try {
            int idx = lista.indexOf(objDaReversao);
            ValidadorVigencia objAnterior = lista.get(idx + 1);
            if (DataUtil.getDataDiaAnterior(objDaReversao.getInicioVigencia()).compareTo(objAnterior.getFimVigencia()) == 0) {
                objAnterior.setFimVigencia(valorParaSetar);
                return objAnterior;
            }
        } catch (NullPointerException | IndexOutOfBoundsException e) {
        }
        return null;
    }

    public ValidadorVigencia alterarFinalDeVigenciaCargoDoRegistroAnteriorAoAdicionadoPelaReversao(ValidadorVigencia objDaReversao, List<? extends ValidadorVigencia> lista, Date valorParaSetar) {
        try {
            int idx = lista.indexOf(objDaReversao);
            ValidadorVigencia objAnterior = lista.get(idx + 1);
            objAnterior.setFimVigencia(valorParaSetar);
            return objAnterior;
        } catch (NullPointerException | IndexOutOfBoundsException e) {
        }
        return null;
    }

    private void permitirManipularRegistro() {
        ValidacaoException ve = new ValidacaoException();
        if (provimentoReversaoFacade.existeFolhaProcessadaParaContratoDepoisDe(contratoFPReativado, selecionado.getInicioVigencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não é permitido manipular as informações desta reversão, já foi processada folha de pagamento após a realização desta reversão.");
        }
        ve.lancarException();
    }

    private boolean existeServidorSelecionado() {
        if (selecionado.getContratoFP() == null) {
            FacesUtil.addOperacaoNaoPermitida("Para adicionar informações a esta reversão é necessário selecionar um servidor.");
            FacesUtil.addOperacaoNaoPermitida("Por favor, selecione um servidor para poder continuar.");
            return false;
        }
        return true;
    }


    // ************************
    // ENQUADRAMENTO FUNCIONAL
    // ************************


    public void limpa() {
        if (enquadramentoFuncional.getInicioVigencia() == null) {
            planoCargosSalarios = null;
        }
        if (planoCargosSalarios == null) {
            categoriaPCSfilha = null;
        }
        if (categoriaPCSfilha == null) {
            progressaoPCSPai = null;
        }
        if (progressaoPCSPai == null) {
            enquadramentoFuncional.setProgressaoPCS(null);
            enquadramentoFuncional.setCategoriaPCS(null);
        }
        if (enquadramentoFuncional.getProgressaoPCS() == null) {
            enquadramentoPCS.setVencimentoBase(BigDecimal.ZERO);
        }
    }

    private void definirProgressaoCategoria() {
        if (enquadramentoPCS != null) {
            getEnquadramentoFuncional().setProgressaoPCS(enquadramentoPCS.getProgressaoPCS());
            getEnquadramentoFuncional().setCategoriaPCS(enquadramentoPCS.getCategoriaPCS());
        } else {
            FacesUtil.addError("Enquadramento de Plano de Cargos e Trabalhos não selecionado!", "Por favor selecione.");
        }
    }

    public List<SelectItem> getPlanos() {

        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        if (enquadramentoFuncional != null && enquadramentoFuncional.getInicioVigencia() != null) {
            for (PlanoCargosSalarios object : provimentoReversaoFacade.getPlanosPorQuadro(enquadramentoFuncional.getInicioVigencia())) {
                toReturn.add(new SelectItem(object, object.toString()));
            }
        }
        return toReturn;
    }

    public EnquadramentoFuncional getEnquadramentoFuncional() {
        return enquadramentoFuncional;
    }

    public void setEnquadramentoFuncional(EnquadramentoFuncional enquadramentoFuncional) {
        this.enquadramentoFuncional = enquadramentoFuncional;
    }

    public PlanoCargosSalarios getPlanoCargosSalarios() {
        return planoCargosSalarios;
    }

    public void setPlanoCargosSalarios(PlanoCargosSalarios planoCargosSalarios) {
        this.planoCargosSalarios = planoCargosSalarios;
    }

    public CategoriaPCS getCategoriaPCSfilha() {
        return categoriaPCSfilha;
    }

    public void setCategoriaPCSfilha(CategoriaPCS categoriaPCSfilha) {
        this.categoriaPCSfilha = categoriaPCSfilha;
    }

    public List<EnquadramentoFuncional> getEnquadramentos() {
        return enquadramentos;
    }

    public void setEnquadramentos(List<EnquadramentoFuncional> enquadramentos) {
        this.enquadramentos = enquadramentos;
    }

    public List<SelectItem> getCategoriaPCS() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        if (enquadramentoFuncional != null && enquadramentoFuncional.getInicioVigencia() != null && planoCargosSalarios != null) {
            if (planoCargosSalarios.getId() != null) {
                for (CategoriaPCS object : provimentoReversaoFacade.recuperaCategoriasNoEnquadramentoFuncionalVigente(planoCargosSalarios, enquadramentoFuncional.getInicioVigencia())) {

                    CategoriaPCS cat = new CategoriaPCS();
                    String nome = "";
                    cat = provimentoReversaoFacade.recuperarCategoriaPCS(object.getId());
                    nome = cat.getDescricao();
                    if (cat.getFilhos().isEmpty()) {
                        while ((cat.getSuperior() != null) && (!cat.equals(cat.getSuperior()))) {
                            cat = cat.getSuperior();
                            nome = cat.getDescricao() + "/" + object.getCodigo() + " - " + nome;
                        }
                        toReturn.add(new SelectItem(object, nome));
                    }
                }
            }
        }

        return Util.ordenaSelectItem(toReturn);
    }

    public ProgressaoPCS getProgressaoPCSPai() {
        return progressaoPCSPai;
    }

    public void setProgressaoPCSPai(ProgressaoPCS progressaoPCSPai) {
        this.progressaoPCSPai = progressaoPCSPai;
    }

    public List<SelectItem> getProgressaoPCS() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        if (enquadramentoFuncional != null && enquadramentoFuncional.getInicioVigencia() != null && planoCargosSalarios != null && categoriaPCSfilha != null) {
            if (planoCargosSalarios.getId() != null) {
                List<ProgressaoPCS> listaProgressao = provimentoReversaoFacade.getRaizPorPlanoECategoriaVigenteNoEnquadramento(planoCargosSalarios, categoriaPCSfilha, enquadramentoFuncional.getInicioVigencia());
                if (listaProgressao != null && !listaProgressao.isEmpty()) {
                    UtilRH.ordenarProgressoes(listaProgressao);
                    for (ProgressaoPCS object : listaProgressao) {
                        ProgressaoPCS prog = provimentoReversaoFacade.recuperarProgressaoPCS(object.getId());
                        toReturn.add(new SelectItem(prog, (prog.getCodigo() != null ? prog.getCodigo() + " - " : "") + prog.getDescricao()));
                    }
                }
            }
        }
        return toReturn;
    }

    public List<SelectItem> getProgrecaoPCSApenasFilhos() {

        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        if (progressaoPCSPai != null && planoCargosSalarios != null) {
            if (progressaoPCSPai.getId() != null && planoCargosSalarios.getId() != null) {
                List<ProgressaoPCS> listaProgressao = provimentoReversaoFacade.getFilhosDe(progressaoPCSPai, planoCargosSalarios);
                UtilRH.ordenarProgressoes(listaProgressao);
                for (ProgressaoPCS object : listaProgressao) {
                    String nome = "";
                    ProgressaoPCS prog = provimentoReversaoFacade.recuperarProgressaoPCS(object.getId());
                    nome = prog.getDescricao();
                    toReturn.add(new SelectItem(object, nome));
                }
            }
        }
        return toReturn;
    }

    public ProvimentoFP getProvimentoFP() {
        if (provimentoFP == null) {
            provimentoFP = new ProvimentoFP();
        }
        return provimentoFP;
    }


    public void setProvimentoFP(ProvimentoFP provimentoFP) {
        this.provimentoFP = provimentoFP;
    }

    public List<SelectItem> tiposProvimentos() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        List<TipoProvimento> progressaoAndPromocao = provimentoReversaoFacade.getEnquadramentoFuncionalFacade().getTipoProvimentoFacade().buscarProvimentosProgressaoAndPromocao();
        for (TipoProvimento tipoProvimento : progressaoAndPromocao) {
            toReturn.add(new SelectItem(tipoProvimento, tipoProvimento.getDescricao()));
        }
        return toReturn;
    }

    public EnquadramentoPCS getEnquadramento() {
        if (enquadramentoFuncional != null) {
            if (categoriaPCSfilha != null && getEnquadramentoFuncional().getProgressaoPCS() != null) {
                Date dataParametro = enquadramentoFuncional.getInicioVigencia() == null ? new Date() : enquadramentoFuncional.getInicioVigencia();
                enquadramentoPCS = provimentoReversaoFacade.recuperaValor(categoriaPCSfilha, getEnquadramentoFuncional().getProgressaoPCS(), dataParametro);
                if (enquadramentoPCS != null && enquadramentoPCS.getId() != null) {
                    getEnquadramentoFuncional().setVencimentoBase(enquadramentoPCS.getVencimentoBase());
                }
            }
        }

        if (enquadramentoPCS == null || enquadramentoPCS.getId() == null) {
            enquadramentoPCS = new EnquadramentoPCS();
            enquadramentoPCS.setVencimentoBase(BigDecimal.ZERO);
        }

        if (enquadramentoFuncional == null || enquadramentoFuncional.getProgressaoPCS() == null) {
            enquadramentoPCS.setVencimentoBase(BigDecimal.ZERO);
        }

        return enquadramentoPCS;
    }

    public BigDecimal recuperarValorEnquadramento(EnquadramentoFuncional ef) {
        if (ef != null) {
            if (ef.getCategoriaPCS() != null & ef.getProgressaoPCS() != null) {
                return provimentoReversaoFacade.getEnquadramentoFuncionalFacade().getValorEnquadramentoFuncional(
                    ef.getCategoriaPCS().getId(),
                    ef.getProgressaoPCS().getId(),
                    ef.getFinalVigencia() != null ? ef.getFinalVigencia() : new Date()
                );
            } else {
                return BigDecimal.ZERO;
            }
        } else {
            return BigDecimal.ZERO;
        }
    }

    public boolean validaCamposEnquadramento() {

        if (enquadramentoFuncional.getContratoServidor() == null) {
            FacesUtil.addCampoObrigatorio("O campo Aposentadoria é obrigatório.");
            return false;
        }
        if (enquadramentoFuncional.getInicioVigencia() == null) {
            FacesUtil.addCampoObrigatorio("Preencha o inicio da vigência corretamente.");
            return false;
        }

        if (planoCargosSalarios == null) {
            FacesUtil.addCampoObrigatorio("O campo Plano de Cargos e Salários é obrigatório.");
            return false;
        }

        if (categoriaPCSfilha == null) {
            FacesUtil.addCampoObrigatorio("O campo Categoria de Plano de Cargos e Salários é obrigatório.");
            return false;
        }

        if (progressaoPCSPai == null) {
            FacesUtil.addCampoObrigatorio("O campo Progressão de Plano de Cargos e Salários é obrigatório.");
            return false;
        }

        if (enquadramentoFuncional.getProgressaoPCS() == null) {
            FacesUtil.addCampoObrigatorio("Selecione uma referência(Letra da Progressão)");
            return false;
        }

        if (enquadramentoFuncional.getFinalVigencia() != null) {
            if (enquadramentoFuncional.getFinalVigencia().before(enquadramentoFuncional.getInicioVigencia())) {
                FacesUtil.addOperacaoNaoPermitida("O Fim da Vigência não pode ser menor que Início da Vigência.");
                return false;
            }
        }

        if (enquadramentoFuncional.getCategoriaPCS() == null) {
            FacesUtil.addCampoObrigatorio("Selecione uma categoria PCCR.");
            return false;
        }

        if (getEnquadramentoAtual().compareTo(BigDecimal.ZERO) <= 0) {
            FacesUtil.addOperacaoNaoPermitida("O valor da referência deve ser maior que 0 (zero).");
            return false;
        }
        return true;
    }

    public BigDecimal getEnquadramentoAtual() {
        BigDecimal valorAtual = BigDecimal.ZERO;
        if (enquadramentoFuncional.getCategoriaPCS() != null && enquadramentoFuncional.getProgressaoPCS() != null && enquadramentoFuncional.getInicioVigencia() != null) {
            EnquadramentoPCS valor = provimentoReversaoFacade.recuperaValor(enquadramentoFuncional.getCategoriaPCS(), enquadramentoFuncional.getProgressaoPCS(), UtilRH.getDataOperacao());
            if (valor != null) {
                valorAtual = valor.getVencimentoBase();
            }
        }
        return valorAtual;
    }

    public boolean isVisualizar() {
        return Operacoes.VER.equals(operacao);
    }


    public void cancelarEnquadramentoFuncional() {
        enquadramentoFuncional = null;
        progressaoPCSPai = null;
        categoriaPCSfilha = null;
        enquadramentoPCS = null;
        planoCargosSalarios = null;
    }


    public void novoEnquadramentoFuncional() {
        try {

            if (!existeServidorSelecionado()) {
                return;
            }

            if (jaExisteRegistroAdicionadoPeloProvimentoReversao(selecionado.getEnquadramentoFuncional(), "enquadramento funcional")) {
                return;
            }

            enquadramentoFuncional = new EnquadramentoFuncional();
            enquadramentoFuncional.setContratoServidor(contratoFPReativado);
            enquadramentoFuncional.setInicioVigencia(selecionado.getInicioVigencia());

            //Setar Dados Provimento
            getProvimentoFP().setSequencia(provimentoReversaoFacade.getEnquadramentoFuncionalFacade().getProvimentoFPFacade().geraSequenciaPorVinculoFP(selecionado.getContratoFP()));
            provimentoFP.setVinculoFP(selecionado.getContratoFP());
            provimentoFP.setDataProvimento(new Date());
            getProvimentoFP().setTipoProvimento(provimentoReversaoFacade.getEnquadramentoFuncionalFacade().getTipoProvimentoFacade().recuperaTipoProvimentoPorCodigo(TipoProvimento.NOMEACAO));


        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void confirmarEnquadramentoFuncional() {
        try {

            definirProgressaoCategoria();
            if (!podeConfirmarEnquadramentoFuncional()) {
                return;
            }
            selecionado.setEnquadramentoFuncionalAnterior(enquadramentos == null || enquadramentos.isEmpty() ? null : enquadramentos.get(0));
            EnquadramentoFuncional enquadramentoAntigo = enquadramentos.get(0);
            validarDataProvimentoReversao(enquadramentoFuncional.getInicioVigencia(), enquadramentoAntigo.getInicioVigencia(), "Enquadramento Funcional");

            //seta valor para mostrar na tela, seta valor para salvar em enquadramentoFuncionalFacade.salvarNovo
            if (enquadramentoAntigo != null) {
                enquadramentoAntigo.setFinalVigencia(DataUtil.getDataDiaAnterior(enquadramentoFuncional.getInicioVigencia()));
            }

            enquadramentoFuncional.setConsideraParaProgAutomat(Boolean.TRUE);

            enquadramentos = Util.adicionarObjetoEmLista(enquadramentos, enquadramentoFuncional);

            selecionado.setEnquadramentoFuncional(enquadramentoFuncional);
            selecionado.setEnquadramentoFuncionalAnterior(enquadramentoAntigo);

            //Setar ProvimentoFP no enquadramento
            if (provimentoFP.getTipoProvimento() != null) {
                enquadramentoFuncional.setProvimentoFP(provimentoFP);
            }

            cancelarEnquadramentoFuncional();
            ordenarListaDescendentemente(enquadramentos);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private boolean podeConfirmarEnquadramentoFuncional() {
        if (!validaCamposEnquadramento()) {
            return false;
        }

        if (!validarVigenciaNovoObjetoAdicionadoEmLista(enquadramentoFuncional, enquadramentos)) {
            return false;
        }

        return true;
    }

    public void removerEnquadramento(EnquadramentoFuncional enquadramentoFuncional) {
        try {
            permitirManipularRegistro();
            provimentoFP = enquadramentoFuncional.getProvimentoFP();

            if (provimentoFP != null && provimentoFP.getId() != null) {
                enquadramentoFuncional.setProvimentoFP(null);
            }
            enquadramentos.remove(enquadramentoFuncional);

            selecionado.setEnquadramentoFuncional(null);

            provimentoFP = null;
            if (enquadramentoFuncional.getId() == null) {
                carregarObjetosParaVerOuEditar();
            }

            limpar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }


    // ************************
    // MODADALIDADE DE CONTRATO
    // ************************


    public void novaModalidadeDeContrato() {
        try {
            if (!existeServidorSelecionado()) {
                return;
            }
            validarInicioVigenciaProvimentoReversao();
            if (jaExisteRegistroAdicionadoPeloProvimentoReversao(selecionado.getModalidadeContratoFPData(), "modalidade de contrato")) {
                return;
            }
            modalidadeContratoFPData = new ModalidadeContratoFPData();
            modalidadeContratoFPData.setInicioVigencia(selecionado.getInicioVigencia());

        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void cancelarModalidade() {
        modalidadeContratoFPData = null;
    }

    public void confirmarModalidade() {
        try {
            if (!podeConfirmarModalidade()) {
                return;
            }
            selecionado.setModalidadeContratoFPDataAnterior(modalidadeContratoFPDatas == null || modalidadeContratoFPDatas.isEmpty() ? null : modalidadeContratoFPDatas.get(0));
            ModalidadeContratoFPData modalidadeContratoAntigo = modalidadeContratoFPDatas.get(0);
            validarModalidadeContratoFP();
            validarDataProvimentoReversao(modalidadeContratoFPData.getInicioVigencia(), modalidadeContratoAntigo.getInicioVigencia(), "Modalidade de Contrato");

            alterarVigenciaUltimoRegistroLista(modalidadeContratoFPData, modalidadeContratoFPDatas);

            modalidadeContratoFPData.setContratoFP(selecionado.getContratoFP());

            modalidadeContratoFPDatas = Util.adicionarObjetoEmLista(modalidadeContratoFPDatas, modalidadeContratoFPData);

            selecionado.setModalidadeContratoFPData(modalidadeContratoFPData);

            cancelarModalidade();
            ordenarListaDescendentemente(modalidadeContratoFPDatas);

        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private boolean podeConfirmarModalidade() {
        return validarVigenciaNovoObjetoAdicionadoEmLista(modalidadeContratoFPData, modalidadeContratoFPDatas);
    }


    public void removerModalidade(ModalidadeContratoFPData modalidadeContratoFPData) {
        try {

            modalidadeContratoFPDatas.remove(modalidadeContratoFPData);
            selecionado.setModalidadeContratoFPData(null);
            cancelarModalidade();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public boolean isHabilitaTipoPEQ() {
        boolean retorno = false;
        if (selecionado.getContratoFP().getUnidadeOrganizacional() != null && selecionado.getContratoFP().getUnidadeOrganizacional().getHabilitaTipoPeq()) {
            if (selecionado.getContratoFP().getModalidadeContratoFP() != null && ModalidadeContratoFP.CONCURSADOS.equals(selecionado.getContratoFP().getModalidadeContratoFP().getCodigo())) {
                retorno = true;
            }
        }
        return retorno;
    }

    public List<SelectItem> getTipoPeq() {
        return Util.getListSelectItem(TipoPeq.values(), false);
    }

    private void validarModalidadeContratoFP() {
        ValidacaoException ve = new ValidacaoException();
        if (modalidadeContratoFPData.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Atenção - Insira pelo menos o inicio da vigência!");
        }
        if (modalidadeContratoFPData.getModalidadeContratoFP() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Atenção - Selecione uma Modalidade de Contrato para adicionar");
        }
        if (modalidadeContratoFPData.getAtoLegal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Atenção - Selecione um Ato Legal para adicionar");
        }

        if (modalidadeContratoFPData.getInicioVigencia() != null && modalidadeContratoFPData.getFinalVigencia() != null) {
            if (modalidadeContratoFPData.getInicioVigencia().after(modalidadeContratoFPData.getFinalVigencia())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Atenção - A data Final da vigência não pode ser inferior a data Inicial da vigência!");
            }
        }
        ve.lancarException();
    }

    public ModalidadeContratoFPData getModalidadeContratoFPData() {
        return modalidadeContratoFPData;
    }

    public void setModalidadeContratoFPData(ModalidadeContratoFPData modalidadeContratoFPData) {
        this.modalidadeContratoFPData = modalidadeContratoFPData;
    }

    public List<ModalidadeContratoFPData> getModalidadeContratoFPDatas() {
        return modalidadeContratoFPDatas;
    }

    public void setModalidadeContratoFPDatas(List<ModalidadeContratoFPData> modalidadeContratoFPDatas) {
        this.modalidadeContratoFPDatas = modalidadeContratoFPDatas;
    }

    // ************************
    // PREVIDÊNCIA
    // ************************
    public void novaPrevidencia() {
        try {
            if (!existeServidorSelecionado()) {
                return;
            }
            validarInicioVigenciaProvimentoReversao();
            if (jaExisteRegistroAdicionadoPeloProvimentoReversao(selecionado.getPrevidenciaVinculoFP(), "previdência")) {
                return;
            }
            previdenciaVinculoFP = new PrevidenciaVinculoFP();
            previdenciaVinculoFP.setContratoFP(selecionado.getContratoFP());
            previdenciaVinculoFP.setInicioVigencia(selecionado.getInicioVigencia());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void cancelarPrevidencia() {
        previdenciaVinculoFP = null;
    }

    public void confirmarPrevidencia() {
        if (!podeConfirmarPrevidencia()) {
            return;
        }
        alterarVigenciaUltimoRegistroLista(previdenciaVinculoFP, previdencias);
        selecionado.setPrevidenciaAnterior(previdencias == null || previdencias.isEmpty() ? null : previdencias.get(0));
        selecionado.setPrevidenciaVinculoFP(previdenciaVinculoFP);

        previdencias = Util.adicionarObjetoEmLista(previdencias, previdenciaVinculoFP);
        cancelarPrevidencia();
        ordenarListaDescendentemente(previdencias);
    }

    private boolean podeConfirmarPrevidencia() {
        if (!validarConfirmacao(previdenciaVinculoFP)) {
            return false;
        }

        if (!validarVigenciaNovoObjetoAdicionadoEmLista(previdenciaVinculoFP, previdencias)) {
            return false;
        }

        return true;
    }

    public void removerPrevidencia(PrevidenciaVinculoFP prev) {
        try {
            permitirManipularRegistro();
            previdencias.remove(prev);
            selecionado.setPrevidenciaVinculoFP(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public PrevidenciaVinculoFP getPrevidenciaVinculoFP() {
        return previdenciaVinculoFP;
    }

    public void setPrevidenciaVinculoFP(PrevidenciaVinculoFP previdenciaVinculoFP) {
        this.previdenciaVinculoFP = previdenciaVinculoFP;
    }

    public List<PrevidenciaVinculoFP> getPrevidencias() {
        return previdencias;
    }

    public void setPrevidencias(List<PrevidenciaVinculoFP> previdencias) {
        this.previdencias = previdencias;
    }

    // ************************
    // VALE TRANSPORTE
    // ************************
    public void novoOpcaoValeTransporteFP() {
        try {
            if (!existeServidorSelecionado()) {
                return;
            }
            validarInicioVigenciaProvimentoReversao();
            if (jaExisteRegistroAdicionadoPeloProvimentoReversao(selecionado.getOpcaoValeTransporteFP(), "vale transporte")) {
                return;
            }
            opcaoValeTransporteFP = new OpcaoValeTransporteFP();
            opcaoValeTransporteFP.setContratoFP(selecionado.getContratoFP());
            opcaoValeTransporteFP.setInicioVigencia(selecionado.getInicioVigencia());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void confirmarOpcaoValeTransporteFP() {
        if (!podeConfirmarOpcaoValeTransporte()) {
            return;
        }
        alterarVigenciaUltimoRegistroLista(opcaoValeTransporteFP, opcoesValeTransporte);
        selecionado.setOpcaoValeTransporteAnterior(opcoesValeTransporte == null || opcoesValeTransporte.isEmpty() ? null : opcoesValeTransporte.get(0));

        opcoesValeTransporte = Util.adicionarObjetoEmLista(opcoesValeTransporte, opcaoValeTransporteFP);
        selecionado.setOpcaoValeTransporteFP(opcaoValeTransporteFP);
        cancelarOpcaoValeTransporteFP();
        ordenarListaDescendentemente(opcoesValeTransporte);
    }

    public boolean podeConfirmarOpcaoValeTransporte() {
        if (!validarConfirmacao(opcaoValeTransporteFP)) {
            return false;
        }

        if (!validarVigenciaNovoObjetoAdicionadoEmLista(opcaoValeTransporteFP, opcoesValeTransporte)) {
            return false;
        }

        return true;
    }

    public void cancelarOpcaoValeTransporteFP() {
        opcaoValeTransporteFP = null;
    }

    public void removerOpcaoValeTransporteFP(OpcaoValeTransporteFP valeTransporteFP) {
        try {
            permitirManipularRegistro();
            opcoesValeTransporte.remove(valeTransporteFP);
            selecionado.setOpcaoValeTransporteFP(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public List<OpcaoValeTransporteFP> getOpcoesValeTransporte() {
        return opcoesValeTransporte;
    }

    public void setOpcoesValeTransporte(List<OpcaoValeTransporteFP> opcoesValeTransporte) {
        this.opcoesValeTransporte = opcoesValeTransporte;
    }

    public OpcaoValeTransporteFP getOpcaoValeTransporteFP() {
        return opcaoValeTransporteFP;
    }

    public void setOpcaoValeTransporteFP(OpcaoValeTransporteFP opcaoValeTransporteFP) {
        this.opcaoValeTransporteFP = opcaoValeTransporteFP;
    }

    // ************************
    // HORÁRIO DE TRABALHO
    // ************************
    public void novoHorarioTrabalhoServidor() {
        try {
            if (!existeServidorSelecionado()) {
                return;
            }
            validarInicioVigenciaProvimentoReversao();
            if (jaExisteRegistroAdicionadoPeloProvimentoReversao(selecionado.getHorarioContratoFP(), "horário")) {
                return;
            }
            horarioContratoFP = new HorarioContratoFP();
            horarioContratoFP.setInicioVigencia(selecionado.getInicioVigencia());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void cancelarHorarioContratoFP() {
        horarioContratoFP = null;
    }

    public void confirmarHorarioContratoFP() {
        if (!podeConfirmarHorarioContratoFP()) {
            return;
        }
        alterarVigenciaUltimoRegistroLista(horarioContratoFP, horarios);
        selecionado.setHorarioAnterior(horarios == null || horarios.isEmpty() ? null : horarios.get(0));


        selecionado.setHorarioContratoFP(horarioContratoFP);

        horarios = Util.adicionarObjetoEmLista(horarios, horarioContratoFP);
        cancelarHorarioContratoFP();
        ordenarListaDescendentemente(horarios);
    }

    public boolean podeConfirmarHorarioContratoFP() {
        if (!validarConfirmacao(horarioContratoFP)) {
            return false;
        }

        if (!validarVigenciaNovoObjetoAdicionadoEmLista(horarioContratoFP, horarios)) {
            return false;
        }

        return true;
    }

    public void removeHorarioCotratoFP(HorarioContratoFP h) {
        try {
            permitirManipularRegistro();
            horarios.remove(h);
            selecionado.setHorarioContratoFP(null);
            selecionado.setLotacaoFuncional(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void informarHorarioTrabalhoServidor(HorarioContratoFP horarioContrato) {
        horarioContratoFP = horarioContrato;
    }

    public void selecionarHorarioTrabalhoServidor(HorarioContratoFP horarioContrato) {
        horarioContratoFP = horarioContrato;
    }

    public void cancelarLotacaoFuncional() {
        hierarquiaOrganizacionalLotacaoFuncional = null;
        lotacaoFuncional = null;
    }

    public void novoLotacaoFuncional() {
        try {
            permitirManipularRegistro();
            validarInicioVigenciaProvimentoReversao();
            if (jaExisteRegistroAdicionadoPeloProvimentoReversao(selecionado.getLotacaoFuncional(), "lotação funcional")) {
                return;
            }
            lotacaoFuncional = new LotacaoFuncional();
            lotacaoFuncional.setInicioVigencia(selecionado.getInicioVigencia());
            lotacaoFuncional.setHorarioContratoFP(horarioContratoFP);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void confirmarLotacaoFuncional() {
        if (!validarCamposObrigatoriosLotacaoFuncional()) {
            return;
        }


        alterarUltimaLotacaoFuncional();
        lotacaoFuncional.setUnidadeOrganizacional(hierarquiaOrganizacionalLotacaoFuncional.getSubordinada());
        lotacaoFuncional.setVinculoFP(selecionado.getContratoFP());
        horarioContratoFP.setLocalTrabalho(Util.adicionarObjetoEmLista(horarioContratoFP.getLocalTrabalho(), lotacaoFuncional));
        selecionado.setLotacaoFuncional(lotacaoFuncional);
        cancelarLotacaoFuncional();
    }

    private boolean validarCamposObrigatoriosLotacaoFuncional() {
        boolean validou = true;
        if (hierarquiaOrganizacionalLotacaoFuncional == null) {
            FacesUtil.addCampoObrigatorio("É obrigatório informar uma unidade organizacional.");
            validou = false;
        }

        if (lotacaoFuncional.getInicioVigencia() == null) {
            FacesUtil.addCampoObrigatorio("É obrigatório informar o início de vigência.");
            validou = false;
        }
        return validou;
    }

    private void alterarUltimaLotacaoFuncional() {
        try {
            ordenarListaDescendentemente(horarios);
            HorarioContratoFP horarioAnterior = horarios.get(1); // Pegar o segundo horário, pois o primeiro é o que foi recentemente adicionado
            ordenarListaDescendentemente(horarioAnterior.getLocalTrabalho());
            LotacaoFuncional lfAnterior = horarioAnterior.getLocalTrabalho().get(0);
            selecionado.setLotacaoFuncionalAnterior(lfAnterior);
            lfAnterior.setFinalVigencia(DataUtil.getDataDiaAnterior(selecionado.getInicioVigencia()));
            horarioAnterior.setLocalTrabalho(Util.adicionarObjetoEmLista(horarioAnterior.getLocalTrabalho(), lfAnterior));
            selecionado.getContratoFP().setHorarioContratoFPs(Util.adicionarObjetoEmLista(horarios, horarioAnterior));
            selecionado.getContratoFP().setLotacaoFuncionals(Util.adicionarObjetoEmLista(selecionado.getContratoFP().getLotacaoFuncionals(), lfAnterior));
        } catch (IndexOutOfBoundsException ex) {
            return;
        }
    }

    private void recuperarHorariosDoContrato() {
        horarios = new ArrayList<>();
        for (LotacaoFuncional lotacaoFuncional : selecionado.getContratoFP().getLotacaoFuncionals()) {
            if (!horarios.contains(lotacaoFuncional.getHorarioContratoFP())) {
                horarios.add(provimentoReversaoFacade.getHorarioContratoFPFacade().recuperar(lotacaoFuncional.getHorarioContratoFP().getId()));
            }
        }
    }

    private void atribuirFimDeVigenciaDoContratoNaLotacaoFuncionalAnterior() {
        try {
            ordenarListaDescendentemente(horarios);
            HorarioContratoFP horarioAnterior = horarios.get(1); // Pegar o segundo horário, pois o primeiro é o que foi adicionado pela reversao
            ordenarListaDescendentemente(horarioAnterior.getLocalTrabalho());
            LotacaoFuncional lfAnterior = horarioAnterior.getLocalTrabalho().get(0);
            selecionado.setLotacaoFuncionalAnterior(lfAnterior);
            lfAnterior.setFinalVigencia(selecionado.getFimVigenciaAnterior());
            horarioAnterior.setLocalTrabalho(Util.adicionarObjetoEmLista(horarioAnterior.getLocalTrabalho(), lfAnterior));
            selecionado.getContratoFP().setHorarioContratoFPs(Util.adicionarObjetoEmLista(horarios, horarioAnterior));
        } catch (IndexOutOfBoundsException ex) {
            return;
        }
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacional(String parte) {
        return provimentoReversaoFacade.getHierarquiaOrganizacionalFacadeNovo().filtrandoHierarquiaOrganizacionalTipo(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), selecionado.getInicioVigencia());
    }

    public HorarioContratoFP getHorarioContratoFP() {
        return horarioContratoFP;
    }

    public void setHorarioContratoFP(HorarioContratoFP horarioContratoFP) {
        this.horarioContratoFP = horarioContratoFP;
    }

    public List<HorarioContratoFP> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<HorarioContratoFP> horarios) {
        this.horarios = horarios;
    }

    public LotacaoFuncional getLotacaoFuncional() {
        return lotacaoFuncional;
    }

    public void setLotacaoFuncional(LotacaoFuncional lotacaoFuncional) {
        this.lotacaoFuncional = lotacaoFuncional;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalLotacaoFuncional() {
        return hierarquiaOrganizacionalLotacaoFuncional;
    }

    public void setHierarquiaOrganizacionalLotacaoFuncional(HierarquiaOrganizacional hierarquiaOrganizacionalLotacaoFuncional) {
        this.hierarquiaOrganizacionalLotacaoFuncional = hierarquiaOrganizacionalLotacaoFuncional;
    }

    // ************************
    // SINDICATO
    // ************************
    public void novoSindicato() {
        try {
            if (!existeServidorSelecionado()) {
                return;
            }
            validarInicioVigenciaProvimentoReversao();
            if (jaExisteRegistroAdicionadoPeloProvimentoReversao(selecionado.getSindicatoVinculoFP(), "sindicato")) {
                return;
            }
            sindicatoVinculoFP = new SindicatoVinculoFP();
            sindicatoVinculoFP.setVinculoFP(selecionado.getContratoFP());
            sindicatoVinculoFP.setInicioVigencia(selecionado.getInicioVigencia());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void confirmarSindicatoVinculoFP() {
        if (!podeConfirmarSindicatoVinculoFP()) {
            return;
        }

        alterarVigenciaUltimoRegistroLista(sindicatoVinculoFP, sindicatos);
        selecionado.setSindicatoAnterior(sindicatos == null || sindicatos.isEmpty() ? null : sindicatos.get(0));

        selecionado.setSindicatoVinculoFP(sindicatoVinculoFP);
        sindicatos = Util.adicionarObjetoEmLista(sindicatos, sindicatoVinculoFP);
        cancelarSindicatoVinculoFP();
    }

    public boolean podeConfirmarSindicatoVinculoFP() {
        if (!validarConfirmacao(sindicatoVinculoFP)) {
            return false;
        }
        if (!validarVigenciaNovoObjetoAdicionadoEmLista(sindicatoVinculoFP, sindicatos)) {
            return false;
        }
        return true;
    }

    public void cancelarSindicatoVinculoFP() {
        sindicatoVinculoFP = null;
    }

    public void removerSindicatoVinculoFP(SindicatoVinculoFP e) {
        try {
            permitirManipularRegistro();
            sindicatos.remove(e);
            selecionado.setSindicatoVinculoFP(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public SindicatoVinculoFP getSindicatoVinculoFP() {
        return sindicatoVinculoFP;
    }

    public void setSindicatoVinculoFP(SindicatoVinculoFP sindicatoVinculoFP) {
        this.sindicatoVinculoFP = sindicatoVinculoFP;
    }

    public List<SindicatoVinculoFP> getSindicatos() {
        return sindicatos;
    }

    public void setSindicatos(List<SindicatoVinculoFP> sindicatos) {
        this.sindicatos = sindicatos;
    }

    // ************************
    // ASSOCIAÇÃO
    // ************************
    public void novaAssociacaoVinculoFP() {
        try {
            if (!existeServidorSelecionado()) {
                return;
            }
            validarInicioVigenciaProvimentoReversao();
            if (jaExisteRegistroAdicionadoPeloProvimentoReversao(selecionado.getAssociacaoVinculoFP(), "associação")) {
                return;
            }
            associacaoVinculoFP = new AssociacaoVinculoFP();
            associacaoVinculoFP.setVinculoFP(selecionado.getContratoFP());
            associacaoVinculoFP.setInicioVigencia(selecionado.getInicioVigencia());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }

    }

    public void confirmarAssociacaoVinculoFP() {
        if (!podeConfirmarAssociacaoVinculoFP()) {
            return;
        }

        alterarVigenciaUltimoRegistroLista(associacaoVinculoFP, associacoes);
        selecionado.setAssociacaoAnterior(associacoes == null || associacoes.isEmpty() ? null : associacoes.get(0));

        selecionado.setAssociacaoVinculoFP(associacaoVinculoFP);
        associacoes = Util.adicionarObjetoEmLista(associacoes, associacaoVinculoFP);
        cancelarAssociacaoVinculoFP();
        ordenarListaDescendentemente(associacoes);
    }

    public boolean podeConfirmarAssociacaoVinculoFP() {
        if (!validarConfirmacao(associacaoVinculoFP)) {
            return false;
        }
        if (!validarVigenciaNovoObjetoAdicionadoEmLista(associacaoVinculoFP, associacoes)) {
            return false;
        }


        return true;
    }

    public void cancelarAssociacaoVinculoFP() {
        associacaoVinculoFP = null;
    }

    public void removerAssociacaoVinculoFP(AssociacaoVinculoFP assVFP) {
        try {
            permitirManipularRegistro();
            associacoes.remove(assVFP);
            selecionado.setAssociacaoVinculoFP(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public List<SelectItem> getEventosFP() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        for (EventoFP eventoFP : provimentoReversaoFacade.getContratoFPFacade().getEventoFPFacade().lista()) {
            toReturn.add(new SelectItem(eventoFP, eventoFP.toString()));
        }
        return toReturn;
    }

    public AssociacaoVinculoFP getAssociacaoVinculoFP() {
        return associacaoVinculoFP;
    }

    public void setAssociacaoVinculoFP(AssociacaoVinculoFP associacaoVinculoFP) {
        this.associacaoVinculoFP = associacaoVinculoFP;
    }

    public List<AssociacaoVinculoFP> getAssociacoes() {
        return associacoes;
    }

    public void setAssociacoes(List<AssociacaoVinculoFP> associacoes) {
        this.associacoes = associacoes;
    }

    // ************************
    // Vínculo (E-Consig)
    // ************************

    public void novoVinculoEConsig() {
        try {
            if (!existeServidorSelecionado()) {
                return;
            }
            validarInicioVigenciaProvimentoReversao();
            if (jaExisteRegistroAdicionadoPeloProvimentoReversao(selecionado.getContratoVinculoDeContrato(), "Vínculo (E-Consig)")) {
                return;
            }
            contratoVinculoDeContrato = new ContratoVinculoDeContrato();
            contratoVinculoDeContrato.setContratoFP(selecionado.getContratoFP());
            contratoVinculoDeContrato.setInicioVigencia(selecionado.getInicioVigencia());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void confirmarVinculoEConsig() {
        if (!podeConfirmarVinculoEConsig()) {
            return;
        }

        try {
            alterarVigenciaUltimoRegistroLista(contratoVinculoDeContrato, vinculoDeContratos);
            validarVinculoDeContratoFP();
            selecionado.setContratoVinculoDeContratoAnterior(vinculoDeContratos == null || vinculoDeContratos.isEmpty() ? null : vinculoDeContratos.get(0));

            selecionado.setContratoVinculoDeContrato(contratoVinculoDeContrato);
            vinculoDeContratos = Util.adicionarObjetoEmLista(vinculoDeContratos, contratoVinculoDeContrato);
            ordenarListaDescendentemente(vinculoDeContratos);
            cancelarVinculoEConsig();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarVinculoDeContratoFP() {
        ValidacaoException ve = new ValidacaoException();
        if (contratoVinculoDeContrato.getVinculoDeContratoFP() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Vínculo é Obrigatório");
        }

        if (contratoVinculoDeContrato.getInicioVigencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Início de Vigência é Obrigatório");
        } else if (contratoVinculoDeContrato.getFinalVigencia() != null && contratoVinculoDeContrato.getInicioVigencia().after(contratoVinculoDeContrato.getFinalVigencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O final de Vigência não pode ser menor que o inicio da vigência.");
        }

        for (ContratoVinculoDeContrato c : vinculoDeContratos) {
            if (c.getFinalVigencia() != null && contratoVinculoDeContrato.getInicioVigencia() != null) {
                if (c.getFinalVigencia().after(contratoVinculoDeContrato.getInicioVigencia()) && (!c.equals(contratoVinculoDeContrato))) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A data Final da vigência anterior não pode ser inferior a data inicial da nova vigência!");
                }
            } else if (!c.equals(contratoVinculoDeContrato)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A vigência da lista contem registro(s) com vigência aberta!");
            }
        }
        ve.lancarException();
    }

    public boolean podeConfirmarVinculoEConsig() {

        if (!validarVigenciaNovoObjetoAdicionadoEmLista(contratoVinculoDeContrato, vinculoDeContratos)) {
            return false;
        }
        return true;
    }

    public void cancelarVinculoEConsig() {
        contratoVinculoDeContrato = null;
    }

    public void removerVinculoEConsig(ContratoVinculoDeContrato e) {
        try {
            permitirManipularRegistro();
            vinculoDeContratos.remove(e);
            selecionado.setContratoVinculoDeContrato(null);

        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public ContratoVinculoDeContrato getContratoVinculoDeContrato() {
        return contratoVinculoDeContrato;
    }

    public void setContratoVinculoDeContrato(ContratoVinculoDeContrato contratoVinculoDeContrato) {
        this.contratoVinculoDeContrato = contratoVinculoDeContrato;
    }

    public List<ContratoVinculoDeContrato> getVinculoDeContratos() {
        return vinculoDeContratos;
    }

    public void setVinculoDeContratos(List<ContratoVinculoDeContrato> vinculoDeContratos) {
        this.vinculoDeContratos = vinculoDeContratos;
    }

    // ************************
    // Recurso de Folha de Pagamento
    // ************************

    public void novoRecurso() {
        try {
            if (!existeServidorSelecionado()) {
                return;
            }
            validarInicioVigenciaProvimentoReversao();
            if (jaExisteRegistroAdicionadoPeloProvimentoReversao(selecionado.getRecursoDoVinculoFP(), "recurso do vínculo")) {
                return;
            }
            recursoDoVinculoFP = new RecursoDoVinculoFP();
            recursoDoVinculoFP.setVinculoFP(selecionado.getContratoFP());
            recursoDoVinculoFP.setInicioVigencia(selecionado.getInicioVigencia());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void confirmarRecurso() {
        if (!podeConfirmarRecurso()) {
            return;
        }

        try {
            alterarVigenciaUltimoRegistroLista(recursoDoVinculoFP, recursoDoVinculoFPs);
            validarRecursosIguais();
            selecionado.setRecursoDoVinculoFPAnterior(recursoDoVinculoFPs == null || recursoDoVinculoFPs.isEmpty() ? null : recursoDoVinculoFPs.get(0));

            selecionado.setRecursoDoVinculoFP(recursoDoVinculoFP);
            recursoDoVinculoFPs = Util.adicionarObjetoEmLista(recursoDoVinculoFPs, recursoDoVinculoFP);
            ordenarListaDescendentemente(recursoDoVinculoFPs);
            cancelarRecurso();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }


    public boolean podeConfirmarRecurso() {

        if (!validarConfirmacao(recursoDoVinculoFP)) {
            return false;
        }

        if (!validarVigenciaNovoObjetoAdicionadoEmLista(recursoDoVinculoFP, recursoDoVinculoFPs)) {
            return false;
        }
        return true;
    }

    public void cancelarRecurso() {
        recursoDoVinculoFP = null;
    }

    public void removerRecurso(RecursoDoVinculoFP e) {
        try {
            permitirManipularRegistro();
            recursoDoVinculoFPs.remove(e);
            selecionado.setRecursoDoVinculoFP(null);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }


    private void validarRecursosIguais() {
        if (!recursoDoVinculoFP.temId() && !podeAlterarRecursoHorarioLotacao) {
            ValidacaoException ve = new ValidacaoException();
            VinculoFP vinculo = provimentoReversaoFacade.recuperarVinculoFPComDependenciaLotacaoFuncional(selecionado.getContratoFP().getId());
            if (recursoDoVinculoFP.getRecursoFP().equals(vinculo.getRecursoDoVinculoFPVigente().getRecursoFP())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O servidor já se encontra no Recurso FP informado.");
            }
            ve.lancarException();
        }
    }

    public RecursoDoVinculoFP getRecursoDoVinculoFP() {
        return recursoDoVinculoFP;
    }

    public void setRecursoDoVinculoFP(RecursoDoVinculoFP recursoDoVinculoFP) {
        this.recursoDoVinculoFP = recursoDoVinculoFP;
    }

    public List<RecursoDoVinculoFP> getRecursoDoVinculoFPs() {
        return recursoDoVinculoFPs;
    }

    public void setRecursoDoVinculoFPs(List<RecursoDoVinculoFP> recursoDoVinculoFPs) {
        this.recursoDoVinculoFPs = recursoDoVinculoFPs;
    }

    // ************************
    // Cargos
    // ************************

    public void novoContratoFPCargo() {
        if (!existeServidorSelecionado()) {
            return;
        }
        validarInicioVigenciaProvimentoReversao();
        if (jaExisteRegistroAdicionadoPeloProvimentoReversao(selecionado.getContratoFPCargo(), "cargo")) {
            return;
        }

        contratoFPCargo = new ContratoFPCargo();
        contratoFPCargo.setInicioVigencia(selecionado.getInicioVigencia());
        contratoFPCargo.setContratoFP(selecionado.getContratoFP());

    }

    public void confirmarContratoFPCargo() {
        try {
            if (!podeConfirmarContratoFPCargo()) {
                return;
            }
            Util.validarCampos(contratoFPCargo);

            alterarVigenciaUltimoRegistroLista(contratoFPCargo, contratoFPCargos);
            selecionado.setContratoFPCargoAnterior(contratoFPCargos == null || contratoFPCargos.isEmpty() ? null : contratoFPCargos.get(0));

            ultimoContratoFPCargo = contratoFPCargos == null || contratoFPCargos.isEmpty() ? null : contratoFPCargos.get(0);

            selecionado.setContratoFPCargo(contratoFPCargo);
            contratoFPCargos = Util.adicionarObjetoEmLista(contratoFPCargos, contratoFPCargo);
            cancelarContratoFPCargo();
            ordenarCargos();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void ordenarCargos() {
        if (!isCargosEmpty())
            Collections.sort(contratoFPCargos);
    }

    private boolean isCargosEmpty() {
        return (contratoFPCargos == null) || contratoFPCargos.isEmpty();
    }


    public boolean podeConfirmarContratoFPCargo() {

        if (!validarVigenciaNovoObjetoAdicionadoEmLista(contratoFPCargo, contratoFPCargos)) {
            return false;
        }
        return true;
    }

    public void cancelarContratoFPCargo() {
        contratoFPCargo = null;
    }

    public void removerContratoFPCargo(ContratoFPCargo cc) {

        if (provimentoReversaoFacade.existeFolhaProcessadaParaContratoDepoisDe(cc.getContratoFP(), cc.getInicioVigencia())) {
            String infoString = Util.formatterDiaMesAno.format(cc.getInicioVigencia());
            FacesUtil.addOperacaoNaoPermitida("Já existem dados financeiros para este registro após seu início de vigência <b>'" + infoString + "'</b>.");
            return;
        }

        contratoFPCargo = null;
        selecionado.setContratoFPCargo(null);

        selecionado.getContratoFP().getCargos().remove(cc);

    }

    public boolean existeFolhadePagamentoParaOCargo() {
        if (provimentoReversaoFacade.existeFolhaProcessadaParaContratoDepoisDe(contratoFPCargo.getContratoFP(), contratoFPCargo.getInicioVigencia())) {
            return true;
        }
        return false;
    }

    public void limparCbo() {
        contratoFPCargo.setCbo(null);
    }

    public List<Cargo> completaCargosContrato(String parte) {
        HierarquiaOrganizacional hierarquia = provimentoReversaoFacade.getHierarquiaOrganizacionalFacadeNovo().getHierarquiaOrganizacionalPorUnidade(UtilRH.getDataOperacao(), selecionado.getContratoFP().getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ADMINISTRATIVA);
        selecionado.getContratoFP().setHierarquiaOrganizacional(hierarquia);

        return provimentoReversaoFacade.getCargoFacade().buscarCargosVigentesPorUnidadesUsuarioAndUnidadeOrganizacional(parte.trim(), selecionado.getContratoFP() != null
            && selecionado.getContratoFP().temHierarquiaOrganizacional() ?
            selecionado.getContratoFP().getHierarquiaOrganizacional().getSubordinada().getId() : null, null);
    }

    public List<CBO> completaCbo(String parte) {
        if (contratoFPCargo.getCargo() == null) {
            FacesUtil.addOperacaoNaoPermitida("É necessário informar um cargo no campo anterior para poder listar os CBOs.");
            return null;
        }
        return provimentoReversaoFacade.listarFiltrandoPorCargo(parte.trim(), contratoFPCargo.getCargo());
    }

    public ContratoFPCargo getContratoFPCargo() {
        return contratoFPCargo;
    }

    public void setContratoFPCargo(ContratoFPCargo contratoFPCargo) {
        this.contratoFPCargo = contratoFPCargo;
    }

    public List<ContratoFPCargo> getContratoFPCargos() {
        return contratoFPCargos;
    }

    public void setContratoFPCargos(List<ContratoFPCargo> contratoFPCargos) {
        this.contratoFPCargos = contratoFPCargos;
    }
}
