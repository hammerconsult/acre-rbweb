package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoOTT;
import br.com.webpublico.enums.StatusDocumentoOtt;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean(name = "operadoraTecnologiaTransporteControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaOperadoraTransporte", pattern = "/operadora-tecnologia-transporte/novo/",
        viewId = "/faces/tributario/rbtrans/operadoratransporte/edita.xhtml"),
    @URLMapping(id = "editarOperadoraTransporte", pattern = "/operadora-tecnologia-transporte/editar/#{operadoraTecnologiaTransporteControlador.id}/",
        viewId = "/faces/tributario/rbtrans/operadoratransporte/edita.xhtml"),
    @URLMapping(id = "listarOperadoraTransporte", pattern = "/operadora-tecnologia-transporte/listar/",
        viewId = "/faces/tributario/rbtrans/operadoratransporte/lista.xhtml"),
    @URLMapping(id = "verOperadoraTransporte", pattern = "/operadora-tecnologia-transporte/ver/#{operadoraTecnologiaTransporteControlador.id}/",
        viewId = "/faces/tributario/rbtrans/operadoratransporte/visualizar.xhtml")
})

public class OperadoraTecnologiaTransporteControlador extends PrettyControlador<OperadoraTecnologiaTransporte> implements Serializable, CRUD {

    private List<CEPUF> ufs;
    @EJB
    private OperadoraTecnologiaTransporteFacade operadoraTecnologiaTransporteFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ParametrosOttFacade parametrosOttFacade;
    private OperadoraTransporteDetentorArquivo operadoraTransporteDetentorArquivo;
    private OperadoraRenovacaoDetentorArquivo operadoraRenovacaoDetentorArquivo;
    private DefaultStreamedContent fileDownload;
    private Exercicio exercicioCertificado;
    private RenovacaoOperadoraOTT renovacaoOperadoraOTT;
    private List<OperadoraTransporteDetentorArquivo> documentosAvaliacao;
    private List<OperadoraRenovacaoDetentorArquivo> documentosAvaliacaoRenovacao;

    public OperadoraTecnologiaTransporteControlador() {
        super(OperadoraTecnologiaTransporte.class);
    }

    public Exercicio getExercicioCertificado() {
        return exercicioCertificado;
    }

    public void setExercicioCertificado(Exercicio exercicioCertificado) {
        this.exercicioCertificado = exercicioCertificado;
    }

    public List<OperadoraTransporteDetentorArquivo> getDocumentosAvaliacao() {
        return documentosAvaliacao;
    }

    public void setDocumentosAvaliacao(List<OperadoraTransporteDetentorArquivo> documentosAvaliacao) {
        this.documentosAvaliacao = documentosAvaliacao;
    }

    public List<OperadoraRenovacaoDetentorArquivo> getDocumentosAvaliacaoRenovacao() {
        return documentosAvaliacaoRenovacao;
    }

    public void setDocumentosAvaliacaoRenovacao(List<OperadoraRenovacaoDetentorArquivo> documentosAvaliacaoRenovacao) {
        this.documentosAvaliacaoRenovacao = documentosAvaliacaoRenovacao;
    }

    @URLAction(mappingId = "novaOperadoraTransporte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        operadoraTransporteDetentorArquivo = new OperadoraTransporteDetentorArquivo(selecionado);
        selecionado.setSituacao(SituacaoOTT.CADASTRADO);
        operadoraTecnologiaTransporteFacade.atribuirDocumentosObrigatoriosCredenciamento(selecionado);
    }

    @URLAction(mappingId = "verOperadoraTransporte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        exercicioCertificado = null;
    }

    @URLAction(mappingId = "editarOperadoraTransporte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        operadoraTransporteDetentorArquivo = new OperadoraTransporteDetentorArquivo(selecionado);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/operadora-tecnologia-transporte/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return operadoraTecnologiaTransporteFacade;
    }

    public OperadoraTransporteDetentorArquivo getOperadoraTransporteDetentorArquivo() {
        return operadoraTransporteDetentorArquivo;
    }

    public void setOperadoraTransporteDetentorArquivo(OperadoraTransporteDetentorArquivo operadoraTransporteDetentorArquivo) {
        this.operadoraTransporteDetentorArquivo = operadoraTransporteDetentorArquivo;
    }

    public DefaultStreamedContent getFileDownload() {
        return fileDownload;
    }

    public void setFileDownload(DefaultStreamedContent fileDownload) {
        this.fileDownload = fileDownload;
    }

    public List<String> completaCEP(String parte) {
        return pessoaFacade.getConsultaCepFacade().listaCEPString(parte.trim());
    }

    public List<String> completaEndereco(String parte) {
        String l = "";
        if (selecionado.getEnderecoComercial() != null) {
            l = selecionado.getEnderecoComercial();
        }
        return pessoaFacade.getConsultaCepFacade().listaLogradourosString(l, parte.trim());
    }

    public List<String> completaBairro(String parte) {
        String l = "";
        if (selecionado.getBairro() != null) {
            l = selecionado.getBairro();
        }
        return pessoaFacade.getConsultaCepFacade().listaBairrosString(l, parte.trim());
    }

    public List<String> completaCidade(String parte) {
        String l = "";
        if (selecionado.getCidade() != null) {
            l = selecionado.getCidade();
        }
        return pessoaFacade.getConsultaCepFacade().listaLocalidadesString(l, parte.trim());
    }

    public List<CEPUF> getListaUF() {
        if (ufs == null) {
            ufs = pessoaFacade.getConsultaCepFacade().listaUf();
        }
        return ufs;
    }

    public void atualizarLogradouros() {
        EnderecoCorreio enderecoCorreio = new EnderecoCorreio();
        enderecoCorreio.setCep(selecionado.getCep());
        pessoaFacade.getConsultaCepFacade().atualizarLogradouros(enderecoCorreio);

        selecionado.setBairro(enderecoCorreio.getBairro());
        selecionado.setEnderecoComercial(enderecoCorreio.getLogradouro());
        selecionado.setUf(enderecoCorreio.getUf());
        selecionado.setCidade(enderecoCorreio.getLocalidade());
        selecionado.setComplemento(enderecoCorreio.getComplemento());
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            if (isOperacaoNovo()) {
                operadoraTecnologiaTransporteFacade.criarHistoricoSituacaoOTT(selecionado, sistemaFacade.getUsuarioCorrente());
                operadoraTecnologiaTransporteFacade.salvarNovo(selecionado);
            } else {
                selecionado.setSituacao(SituacaoOTT.APROVADO);
                operadoraTecnologiaTransporteFacade.criarHistoricoSituacaoOTT(selecionado, sistemaFacade.getUsuarioCorrente());
                operadoraTecnologiaTransporteFacade.salvar(selecionado);
            }
            redireciona();
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());

        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void validarCampos() {
        selecionado.realizarValidacoes();
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getCnpj() != null && operadoraTecnologiaTransporteFacade.verificarCnpjJaExistente(selecionado.getId(), selecionado.getCnpj())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O CNPJ já está cadastrado em outra Operadora de Transporte!");
        }
        ve.lancarException();
        validarAtividadesCnpj();
    }

    public void iniciarAvaliacaoDocumentacao() {
        documentosAvaliacao = selecionado.getDetentorArquivoComposicao()
            .stream()
            .filter(doc -> StatusDocumentoOtt.AGUARDANDO_AVALIACAO.equals(doc.getStatus()))
            .collect(Collectors.toList());
    }

    private void validarNovoCertificado() {
        ValidacaoException ve = new ValidacaoException();
        if (exercicioCertificado == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o ano do Certificado!");
        } else {
            for (CertificadoAnualOTT certificado : selecionado.getCertificados()) {
                if (certificado.getExercicio().equals(exercicioCertificado) &&
                    (certificado.getVencimento().after(DataUtil.dataSemHorario(new Date())) || certificado.getVencimento().equals(DataUtil.dataSemHorario(new Date())))) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um certificado para o ano de " + exercicioCertificado.getAno() + "!");
                    break;
                }
            }
        }
        ve.lancarException();
    }

    public void gerarNovoCertificado() {
        try {
            validarNovoCertificado();
            Date vencimento = DataUtil.acrescentarUmAno(selecionado.getDataCadastro());
            if (vencimento.before(DataUtil.dataSemHorario(new Date()))) {
                vencimento = DataUtil.acrescentarUmAno(new Date());
            }
            ParametrosOtt parametrosOtt = parametrosOttFacade.retornarParametroOtt();
            if (parametrosOtt != null && parametrosOtt.getCertificadoCredenciamento() != null) {
                CertificadoAnualOTT certificado = operadoraTecnologiaTransporteFacade.gerarNovoCertificado(selecionado, exercicioCertificado, vencimento, sistemaFacade.getUsuarioCorrente(), parametrosOtt);
                if (certificado != null) {
                    FacesUtil.addOperacaoRealizada("Certificado de " + exercicioCertificado + " gerado com sucesso!");
                    FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
                } else {
                    FacesUtil.addOperacaoNaoRealizada("Não foi encontrado o tipo de documento configurado para a certidão anual da OTT!");
                }
            } else {
                FacesUtil.addOperacaoNaoRealizada("Não foi encontrado parâmetro configurado para a certidão anual da OTT!");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void emitirCertificado(CertificadoAnualOTT certificadoAnualOTT) {
        if (certificadoAnualOTT.getDocumentoOficial() != null) {
            operadoraTecnologiaTransporteFacade.getDocumentoOficialFacade().emiteDocumentoOficial(certificadoAnualOTT.getDocumentoOficial());
        }
    }

    public RenovacaoOperadoraOTT getRenovacaoOperadoraOTT() {
        return renovacaoOperadoraOTT;
    }

    public void setRenovacaoOperadoraOTT(RenovacaoOperadoraOTT renovacaoOperadoraOTT) {
        this.renovacaoOperadoraOTT = renovacaoOperadoraOTT;
    }

    public void iniciarRenovacao() {
        renovacaoOperadoraOTT = new RenovacaoOperadoraOTT();
        renovacaoOperadoraOTT.setExercicioRenovacao(sistemaFacade.getExercicioCorrente());
        operadoraTecnologiaTransporteFacade.atribuirDocumentosObrigatoriosCredenciamento(renovacaoOperadoraOTT);
    }

    public void salvarRenovacaoAutorizacaoOperadora() {
        try {
            renovacaoOperadoraOTT.realizarValidacoes();
            validarRenovacaoPorOperadora();
            renovacaoOperadoraOTT.setOperadora(selecionado);
            RenovacaoOperadoraOTT renovacao = operadoraTecnologiaTransporteFacade.novaRenovacaoOperadora(renovacaoOperadoraOTT);
            Util.adicionarObjetoEmLista(selecionado.getRenovacaoOperadoraOTTS(), renovacao);
            OperadoraTecnologiaTransporte operadora = operadoraTecnologiaTransporteFacade.recuperar(selecionado.getId());
            operadoraTecnologiaTransporteFacade.salvar(operadora);
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
            FacesUtil.addOperacaoRealizada("Renovação de Autorização salvo com sucesso.");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarRenovacaoPorOperadora() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getRenovacaoOperadoraOTTS() != null && !selecionado.getRenovacaoOperadoraOTTS().isEmpty()) {
            for (RenovacaoOperadoraOTT renovacao : selecionado.getRenovacaoOperadoraOTTS()) {
                if (renovacao.getExercicioRenovacao().equals(renovacaoOperadoraOTT.getExercicioRenovacao())) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe uma Renovação de Autorização para a Operadora " + selecionado.getNome() + " no exercício: " + renovacao.getExercicioRenovacao()
                        + ", " + renovacao.getSituacaoRenovacao().getDescricao() + ".");
                    break;
                }
            }
            ve.lancarException();
        }
    }

    public void cancelarRenovacao() {
        operadoraRenovacaoDetentorArquivo = null;
        renovacaoOperadoraOTT = null;
    }

    public OperadoraRenovacaoDetentorArquivo getOperadoraRenovacaoDetentorArquivo() {
        return operadoraRenovacaoDetentorArquivo;
    }

    public void setOperadoraRenovacaoDetentorArquivo(OperadoraRenovacaoDetentorArquivo operadoraRenovacaoDetentorArquivo) {
        this.operadoraRenovacaoDetentorArquivo = operadoraRenovacaoDetentorArquivo;
    }

    public void uploadDocumentoCredenciamento(FileUploadEvent event) {
        try {
            DetentorArquivoComposicao detentorArquivoComposicao = new DetentorArquivoComposicao();
            detentorArquivoComposicao.setArquivoComposicao(new ArquivoComposicao());
            detentorArquivoComposicao.getArquivoComposicao().setDataUpload(new Date());
            detentorArquivoComposicao.getArquivoComposicao().setUsuario(sistemaFacade.getUsuarioCorrente());
            Arquivo arquivo = operadoraTecnologiaTransporteFacade.getArquivoFacade().criarArquivo(event.getFile());
            detentorArquivoComposicao.getArquivoComposicao().setArquivo(arquivo);
            operadoraTransporteDetentorArquivo.setDetentorArquivoComposicao(detentorArquivoComposicao);
            FacesUtil.atualizarComponente("Formulario:tabViewOperadora:opAnexosCadastro");
            FacesUtil.executaJavaScript("dlgUploadDocumentoCredenciamento.hide()");
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void substituirDocumentoCredenciamento(OperadoraTransporteDetentorArquivo operadoraTransporteDetentorArquivo) {
        setOperadoraTransporteDetentorArquivo(operadoraTransporteDetentorArquivo);
        this.operadoraTransporteDetentorArquivo.setDetentorArquivoComposicao(null);
        FacesUtil.atualizarComponente("formularioUploadDocumentoCredenciamento");
        FacesUtil.executaJavaScript("dlgUploadDocumentoCredenciamento.show()");
    }

    public void uploadDocumentoCredenciamentoRenovacao(FileUploadEvent event) {
        try {
            DetentorArquivoComposicao detentorArquivoComposicao = new DetentorArquivoComposicao();
            detentorArquivoComposicao.setArquivoComposicao(new ArquivoComposicao());
            detentorArquivoComposicao.getArquivoComposicao().setDataUpload(new Date());
            detentorArquivoComposicao.getArquivoComposicao().setUsuario(sistemaFacade.getUsuarioCorrente());
            Arquivo arquivo = operadoraTecnologiaTransporteFacade.getArquivoFacade().criarArquivo(event.getFile());
            detentorArquivoComposicao.getArquivoComposicao().setArquivo(arquivo);
            operadoraRenovacaoDetentorArquivo.setDetentorArquivoComposicao(detentorArquivoComposicao);
            FacesUtil.atualizarComponente("formOperadoraPortal");
            FacesUtil.executaJavaScript("dlgUploadDocumentoCredenciamento.hide()");
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void substituirDocumentoCredenciamento(OperadoraRenovacaoDetentorArquivo operadoraRenovacaoDetentorArquivo) {
        setOperadoraRenovacaoDetentorArquivo(operadoraRenovacaoDetentorArquivo);
        this.operadoraRenovacaoDetentorArquivo.setDetentorArquivoComposicao(null);
        FacesUtil.atualizarComponente("formularioUploadDocumentoCredenciamento");
        FacesUtil.executaJavaScript("dlgUploadDocumentoCredenciamento.show()");
    }

    public List<SelectItem> getStatusAvaliacaoDocumento() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        toReturn.add(new SelectItem(StatusDocumentoOtt.APROVADO, StatusDocumentoOtt.APROVADO.getDescricao()));
        toReturn.add(new SelectItem(StatusDocumentoOtt.REJEITADO, StatusDocumentoOtt.REJEITADO.getDescricao()));
        return toReturn;
    }

    public void mudouStatusAvaliacaoDocumento(OperadoraTransporteDetentorArquivo anexo) {
        anexo.setObservacao(null);
    }

    public void confirmarAvaliacaoDocumentos() {
        try {
            validarAvaliacaoDocumentos();
            operadoraTecnologiaTransporteFacade.confirmarAvaliacaoDocumentos(selecionado, documentosAvaliacao);
            FacesUtil.addOperacaoRealizada("Avaliação de Documentação salva com sucesso!");
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    private void validarAvaliacaoDocumentos() {
        if (documentosAvaliacao.stream()
            .anyMatch(arq -> arq.getStatus() == null ||
                (arq.getStatus().equals(StatusDocumentoOtt.REJEITADO) && Strings.isNullOrEmpty(arq.getObservacao())))) {
            throw new ValidacaoException("Defina o status de todos os documentos, " +
                "caso esteja rejeitado descreva o motivo da rejeição.");
        }
    }

    public void iniciarAvaliacaoDocumentacaoRenovacao(RenovacaoOperadoraOTT renovacaoOperadoraOTT) {
        this.renovacaoOperadoraOTT = renovacaoOperadoraOTT;
        documentosAvaliacaoRenovacao = this.renovacaoOperadoraOTT.getOperadoraRenovacaoDetentorArquivos()
            .stream()
            .filter(doc -> StatusDocumentoOtt.AGUARDANDO_AVALIACAO.equals(doc.getStatus()))
            .collect(Collectors.toList());
    }

    public void mudouStatusAvaliacaoDocumento(OperadoraRenovacaoDetentorArquivo anexo) {
        anexo.setObservacao(null);
    }

    public void confirmarAvaliacaoDocumentosRenovacao() {
        try {
            validarAvaliacaoDocumentosRenovacao();
            operadoraTecnologiaTransporteFacade.confirmarAvaliacaoDocumentosRenovacao(renovacaoOperadoraOTT, documentosAvaliacaoRenovacao);
            FacesUtil.addOperacaoRealizada("Avaliação de Documentação de Renovação salva com sucesso!");
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    private void validarAvaliacaoDocumentosRenovacao() {
        if (documentosAvaliacaoRenovacao.stream()
            .anyMatch(arq -> arq.getStatus() == null ||
                (arq.getStatus().equals(StatusDocumentoOtt.REJEITADO) && Strings.isNullOrEmpty(arq.getObservacao())))) {
            throw new ValidacaoException("Defina o status de todos os documentos, " +
                "caso esteja rejeitado descreva o motivo da rejeição.");
        }
    }

    public void alterouCnpj() {
        try {
            validarAtividadesCnpj();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    private void validarAtividadesCnpj() {
        if (Strings.isNullOrEmpty(selecionado.getCnpj())) return;
        if (!operadoraTecnologiaTransporteFacade.cnpjPermiteCadastroOTT(selecionado.getCnpj())) {
            throw new ValidacaoException("O CNPJ informado não contém atividade permitida para o cadastro de ott.");
        }
    }
}




