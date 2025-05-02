package br.com.webpublico.controle;

import br.com.webpublico.DateUtils;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoVeiculoOTT;
import br.com.webpublico.enums.StatusDocumentoOtt;
import br.com.webpublico.enums.TipoPermissaoRBTrans;
import br.com.webpublico.enums.VeiculoPoluente;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.VeiculoOperadoraTecnologiaTransporteFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean(name = "veiculoOperadoraTecnologiaTransporteControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoVeiculoOperadoraTransporte", pattern = "/veiculo-operadora-tecnologia-transporte/novo/",
        viewId = "/faces/tributario/rbtrans/veiculooperadoratransporte/edita.xhtml"),
    @URLMapping(id = "editarVeiculoOperadoraTransporte", pattern = "/veiculo-operadora-tecnologia-transporte/editar/#{veiculoOperadoraTecnologiaTransporteControlador.id}/",
        viewId = "/faces/tributario/rbtrans/veiculooperadoratransporte/edita.xhtml"),
    @URLMapping(id = "listarVeiculoOperadoraTransporte", pattern = "/veiculo-operadora-tecnologia-transporte/listar/",
        viewId = "/faces/tributario/rbtrans/veiculooperadoratransporte/lista.xhtml"),
    @URLMapping(id = "verVeiculoOperadoraTransporte", pattern = "/veiculo-operadora-tecnologia-transporte/ver/#{veiculoOperadoraTecnologiaTransporteControlador.id}/",
        viewId = "/faces/tributario/rbtrans/veiculooperadoratransporte/visualizar.xhtml")
})

public class VeiculoOperadoraTecnologiaTransporteControlador extends PrettyControlador<VeiculoOperadoraTecnologiaTransporte> implements Serializable, CRUD {

    @EJB
    private VeiculoOperadoraTecnologiaTransporteFacade facade;
    private VeiculoOperadoraDetentorArquivo veiculoOperadoraDetentorArquivo;
    private List<VeiculoOperadoraDetentorArquivo> documentosAvaliacao;
    private RenovacaoVeiculoOTT renovacaoVeiculoOTT;
    private List<VeiculoRenovacaoDetentorArquivo> documentosAvaliacaoRenovacao;
    private ParametrosTransitoTransporte parametrosTransitoTransporte;

    public VeiculoOperadoraTecnologiaTransporteControlador() {
        super(VeiculoOperadoraTecnologiaTransporte.class);
    }

    @URLAction(mappingId = "novoVeiculoOperadoraTransporte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataCadastro(facade.getSistemaFacade().getDataOperacao());
        selecionado.setSituacaoVeiculoOTT(SituacaoVeiculoOTT.CADASTRADO);
        facade.atribuirDocumentosVeiculo(selecionado);
        recuperarParametrosTransitoTransporte();
    }

    @URLAction(mappingId = "verVeiculoOperadoraTransporte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        facade.salvar(selecionado);
    }

    @URLAction(mappingId = "editarVeiculoOperadoraTransporte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarParametrosTransitoTransporte();
    }

    private void recuperarParametrosTransitoTransporte() {
        if (parametrosTransitoTransporte == null) {
            parametrosTransitoTransporte = facade.getParametrosTransitoTransporteFacade()
                .recuperarParametroVigentePeloTipo(TipoPermissaoRBTrans.TAXI);
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/veiculo-operadora-tecnologia-transporte/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public VeiculoOperadoraDetentorArquivo getVeiculoOperadoraDetentorArquivo() {
        return veiculoOperadoraDetentorArquivo;
    }

    public void setVeiculoOperadoraDetentorArquivo(VeiculoOperadoraDetentorArquivo veiculoOperadoraDetentorArquivo) {
        this.veiculoOperadoraDetentorArquivo = veiculoOperadoraDetentorArquivo;
    }

    public List<VeiculoOperadoraDetentorArquivo> getDocumentosAvaliacao() {
        return documentosAvaliacao;
    }

    public void setDocumentosAvaliacao(List<VeiculoOperadoraDetentorArquivo> documentosAvaliacao) {
        this.documentosAvaliacao = documentosAvaliacao;
    }

    public RenovacaoVeiculoOTT getRenovacaoVeiculoOTT() {
        return renovacaoVeiculoOTT;
    }

    public void setRenovacaoVeiculoOTT(RenovacaoVeiculoOTT renovacaoVeiculoOTT) {
        this.renovacaoVeiculoOTT = renovacaoVeiculoOTT;
    }

    public List<VeiculoRenovacaoDetentorArquivo> getDocumentosAvaliacaoRenovacao() {
        return documentosAvaliacaoRenovacao;
    }

    public void setDocumentosAvaliacaoRenovacao(List<VeiculoRenovacaoDetentorArquivo> documentosAvaliacaoRenovacao) {
        this.documentosAvaliacaoRenovacao = documentosAvaliacaoRenovacao;
    }

    public List<VeiculoPoluente> getVeiculosPoluentes() {
        return Arrays.asList(VeiculoPoluente.values());
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            validarAnoFabricacao();
            if (isOperacaoNovo()) {
                facade.criarHistoricoSituacoesVeiculo(selecionado, facade.getSistemaFacade().getUsuarioCorrente());
                facade.salvarNovo(selecionado);
            } else {
                facade.salvar(selecionado);
            }
            redireciona();
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public List<CertificadoRenovacaoOTT> certificados() {
        List<CertificadoRenovacaoOTT> retorno = Lists.newArrayList();
        if (selecionado.getCertificadoInscricao() != null) retorno.add(selecionado.getCertificadoInscricao());
        for (RenovacaoVeiculoOTT renovacao : selecionado.getRenovacaoVeiculoOTTS()) {
            if (renovacao.getCertificadoRenovacaoOTT() != null) retorno.add(renovacao.getCertificadoRenovacaoOTT());
        }
        return retorno;
    }

    public boolean habilitarEmissaoCertificado(CertificadoRenovacaoOTT certificado) {
        if (certificado.getDocumentoOficial() != null) return true;
        List<CondutorOperadoraTecnologiaTransporte> condutores = facade.buscarCondutoresPorVeiculo(selecionado);
        if (condutores.isEmpty()) return false;
        CondutorOperadoraTecnologiaTransporte condutor = condutores.get(0);
        for (CertificadoCondutorOTT condutorCertificado : condutor.getCertificados()) {
            if (condutorCertificado.getVeiculoOtt().getId().equals(selecionado.getId())
                && condutorCertificado.getDocumentoOficial() != null
                && condutorCertificado.getVencimento().after(DataUtil.dataSemHorario(new Date()))) {
                return true;
            }
        }
        return false;
    }

    public void emitirCertificado(CertificadoRenovacaoOTT certificado) {
        DocumentoOficial doc = null;
        if (certificado.getDocumentoOficial() != null) doc = certificado.getDocumentoOficial();
        List<CondutorOperadoraTecnologiaTransporte> condutores = facade.buscarCondutoresPorVeiculo(selecionado);
        if (condutores.isEmpty()) return;
        if (doc != null) {
            facade.getDocumentoOficialFacade().emiteDocumentoOficial(doc);
            return;
        }
        CondutorOperadoraTecnologiaTransporte condutor = condutores.get(0);
        for (CertificadoCondutorOTT condutorCertificado : condutor.getCertificados()) {
            if (condutorCertificado.getVeiculoOtt().getId().equals(selecionado.getId())
                && condutorCertificado.getDocumentoOficial() != null
                && condutorCertificado.getVencimento().after(DataUtil.dataSemHorario(new Date()))) {
                doc = condutorCertificado.getDocumentoOficial();
                certificado.setDocumentoOficial(doc);
                facade.salvar(selecionado);
                break;
            }
        }
        if (doc != null) facade.getDocumentoOficialFacade().emiteDocumentoOficial(doc);
    }

    private void validarCampos() {
        selecionado.realizarValidacoes();
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getPlacaVeiculo() != null &&
            selecionado.getOperadoraTransporte() != null &&
            facade.verificarPlacaVeiculoPorOperadora(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe outro veículo com essa Placa já cadastrada!");
        }
        ve.lancarException();
    }

    private void validarAnoFabricacao() {
        if (selecionado.getAnoFabricacaoVeiculo() != null) {
            Integer anoInicial = DateUtils.getAno(new Date()) - parametrosTransitoTransporte.getLimiteIdade();
            if (selecionado.getAnoFabricacaoVeiculo() < anoInicial) {
                throw new ValidacaoException("O ano de fabricação do veículo não pode ser inferior a " + anoInicial);
            }
        }
    }

    public boolean podeAprovar() {
        return SituacaoVeiculoOTT.APROVADO.equals(selecionado.getSituacaoVeiculoOTT());
    }

    public boolean podeGerarVistoria() {
        return facade.recuperarVistoriaVeiculo(selecionado);
    }

    public void gerarVistoria() {
        try {
            facade.getVistoriaVeiculoOttFacade().gerarVistoriaParaVeiculo(selecionado, facade.getSistemaFacade().getUsuarioCorrente());
            facade.aprovarParaVistoriaVeiculoOTT(selecionado);
            FacesUtil.addOperacaoRealizada("Vistoria Gerada com sucesso!");
            FacesUtil.addOperacaoRealizada("Veículo aguardando vistoria!");
            redireciona();
        } catch (Exception e){
            descobrirETratarException(e);
        }
    }

    public void aprovar() {
        try {
            facade.aprovarVeiculoOTT(selecionado);
            FacesUtil.addInfo("Enviado!", facade.enviarEmailAprovacaoVeiculoOtt(selecionado));
            FacesUtil.addOperacaoRealizada("Aprovado com sucesso.");
            redireciona();
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public List<OperadoraTecnologiaTransporte> completaOperadora(String parte) {
        return facade.getOperadoraTecnologiaTransporteFacade().listarOperadoras(parte);
    }

    public void uploadDocumentoVeiculo(FileUploadEvent event) {
        try {
            DetentorArquivoComposicao detentorArquivoComposicao = new DetentorArquivoComposicao();
            detentorArquivoComposicao.setArquivoComposicao(new ArquivoComposicao());
            detentorArquivoComposicao.getArquivoComposicao().setDataUpload(new Date());
            detentorArquivoComposicao.getArquivoComposicao().setUsuario(facade.getSistemaFacade().getUsuarioCorrente());
            Arquivo arquivo = facade.getOperadoraTecnologiaTransporteFacade().getArquivoFacade().criarArquivo(event.getFile());
            detentorArquivoComposicao.getArquivoComposicao().setArquivo(arquivo);
            veiculoOperadoraDetentorArquivo.setDetentorArquivoComposicao(detentorArquivoComposicao);
            FacesUtil.executaJavaScript("dlgUploadDocumentoVeiculo.hide()");
            FacesUtil.atualizarComponente("Formulario:tbView:opAnexos");
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void substituirDocumentoVeiculo(VeiculoOperadoraDetentorArquivo veiculoOperadoraDetentorArquivo) {
        setVeiculoOperadoraDetentorArquivo(veiculoOperadoraDetentorArquivo);
        this.veiculoOperadoraDetentorArquivo.setDetentorArquivoComposicao(null);
        FacesUtil.executaJavaScript("dlgUploadDocumentoVeiculo.show()");
        FacesUtil.atualizarComponente("formularioUploadDocumentoVeiculo");
    }

    public void alterouAlugado() {
        facade.atribuirDocumentosVeiculo(selecionado);
    }

    public void iniciarAvaliacaoDocumentacao() {
        documentosAvaliacao = selecionado.getVeiculoOperadoraDetentorArquivos()
            .stream()
            .filter(doc -> doc.getStatus().equals(StatusDocumentoOtt.AGUARDANDO_AVALIACAO))
            .collect(Collectors.toList());
    }

    public void mudouStatusAvaliacaoDocumento(VeiculoOperadoraDetentorArquivo anexo) {
        anexo.setObservacao(null);
    }

    public void mudouStatusAvaliacaoDocumento(VeiculoRenovacaoDetentorArquivo anexo) {
        anexo.setObservacao(null);
    }

    public void confirmarAvaliacaoDocumentos() {
        try {
            validarAvaliacaoDocumentos();
            facade.confirmarAvaliacaoDocumentos(selecionado, documentosAvaliacao);
            FacesUtil.addOperacaoRealizada("Avaliação de Documentos salva com sucesso!");
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

    public void iniciarAvaliacaoDocumentacaoRenovacao(RenovacaoVeiculoOTT renovacaoVeiculoOTT) {
        this.renovacaoVeiculoOTT = renovacaoVeiculoOTT;
        documentosAvaliacaoRenovacao = this.renovacaoVeiculoOTT.getVeiculoRenovacaoDetentorArquivos()
            .stream()
            .filter(doc -> doc.getStatus().equals(StatusDocumentoOtt.AGUARDANDO_AVALIACAO))
            .collect(Collectors.toList());
    }

    public void confirmarAvaliacaoDocumentosRenovacao() {
        try {
            validarAvaliacaoDocumentosRenovacao();
            facade.confirmarAvaliacaoDocumentosRenovacao(renovacaoVeiculoOTT, documentosAvaliacaoRenovacao);
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

    public void alterouAnoFabricacao() {
        try {
            validarAnoFabricacao();
        } catch (ValidacaoException ve) {
            selecionado.setAnoFabricacaoVeiculo(null);
            FacesUtil.printAllFacesMessages(ve);
        }
    }
}
