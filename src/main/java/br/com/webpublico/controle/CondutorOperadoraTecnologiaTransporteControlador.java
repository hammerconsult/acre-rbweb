package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CondutorOperadoraTecnologiaTransporteFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean(name = "condutorOperadoraTecnologiaTransporteControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoCondutorOperadoraTransporte", pattern = "/condutor-operadora-tecnologia-transporte/novo/",
        viewId = "/faces/tributario/rbtrans/condutoroperadoratransporte/edita.xhtml"),
    @URLMapping(id = "editarCondutorOperadoraTransporte", pattern = "/condutor-operadora-tecnologia-transporte/editar/#{condutorOperadoraTecnologiaTransporteControlador.id}/",
        viewId = "/faces/tributario/rbtrans/condutoroperadoratransporte/edita.xhtml"),
    @URLMapping(id = "listarCondutorOperadoraTransporte", pattern = "/condutor-operadora-tecnologia-transporte/listar/",
        viewId = "/faces/tributario/rbtrans/condutoroperadoratransporte/lista.xhtml"),
    @URLMapping(id = "verCondutorOperadoraTransporte", pattern = "/condutor-operadora-tecnologia-transporte/ver/#{condutorOperadoraTecnologiaTransporteControlador.id}/",
        viewId = "/faces/tributario/rbtrans/condutoroperadoratransporte/visualizar.xhtml")
})
public class CondutorOperadoraTecnologiaTransporteControlador extends PrettyControlador<CondutorOperadoraTecnologiaTransporte> implements Serializable, CRUD {

    private List<CEPUF> ufs;
    @EJB
    private CondutorOperadoraTecnologiaTransporteFacade facade;
    private CondutorOperadoraVeiculoOperadora condutorOperadoraVeiculoOperadora;
    private VeiculoOperadoraTecnologiaTransporte veiculoOperadoraTecnologiaTransporte;
    private CondutorOperadoraDetentorArquivo condutorOperadoraDetentorArquivo;
    private RenovacaoCondutorOTT renovacaoCondutorOTT;
    private List<CondutorOperadoraDetentorArquivo> documentosAvaliacao;
    private List<CondutorRenovacaoDetentorArquivo> documentosAvaliacaoRenovacao;
    private String imgSrc;

    public CondutorOperadoraTecnologiaTransporteControlador() {
        super(CondutorOperadoraTecnologiaTransporte.class);
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public RenovacaoCondutorOTT getRenovacaoCondutorOTT() {
        return renovacaoCondutorOTT;
    }

    public void setRenovacaoCondutorOTT(RenovacaoCondutorOTT renovacaoCondutorOTT) {
        this.renovacaoCondutorOTT = renovacaoCondutorOTT;
    }

    public List<CondutorOperadoraDetentorArquivo> getDocumentosAvaliacao() {
        return documentosAvaliacao;
    }

    public void setDocumentosAvaliacao(List<CondutorOperadoraDetentorArquivo> documentosAvaliacao) {
        this.documentosAvaliacao = documentosAvaliacao;
    }

    public List<CondutorRenovacaoDetentorArquivo> getDocumentosAvaliacaoRenovacao() {
        return documentosAvaliacaoRenovacao;
    }

    public void setDocumentosAvaliacaoRenovacao(List<CondutorRenovacaoDetentorArquivo> documentosAvaliacaoRenovacao) {
        this.documentosAvaliacaoRenovacao = documentosAvaliacaoRenovacao;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    @URLAction(mappingId = "novoCondutorOperadoraTransporte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataCadastro(new Date());
        selecionado.setSituacaoCondutorOTT(SituacaoCondutorOTT.CADASTRADO);
        condutorOperadoraVeiculoOperadora = new CondutorOperadoraVeiculoOperadora();
        veiculoOperadoraTecnologiaTransporte = null;
        facade.atribuirDocumentosCondutor(selecionado);
    }

    @URLAction(mappingId = "verCondutorOperadoraTransporte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarCondutorOperadoraTransporte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        condutorOperadoraVeiculoOperadora = new CondutorOperadoraVeiculoOperadora();
        veiculoOperadoraTecnologiaTransporte = null;
        condutorOperadoraDetentorArquivo = new CondutorOperadoraDetentorArquivo(selecionado);
        carregarFoto();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/condutor-operadora-tecnologia-transporte/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public CondutorOperadoraVeiculoOperadora getCondutorOperadoraVeiculoOperadora() {
        return condutorOperadoraVeiculoOperadora;
    }

    public void setCondutorOperadoraVeiculoOperadora(CondutorOperadoraVeiculoOperadora condutorOperadoraVeiculoOperadora) {
        this.condutorOperadoraVeiculoOperadora = condutorOperadoraVeiculoOperadora;
    }

    public VeiculoOperadoraTecnologiaTransporte getVeiculoOperadoraTecnologiaTransporte() {
        return veiculoOperadoraTecnologiaTransporte;
    }

    public void setVeiculoOperadoraTecnologiaTransporte(VeiculoOperadoraTecnologiaTransporte veiculoOperadoraTecnologiaTransporte) {
        this.veiculoOperadoraTecnologiaTransporte = veiculoOperadoraTecnologiaTransporte;
    }

    public CondutorOperadoraDetentorArquivo getCondutorOperadoraDetentorArquivo() {
        return condutorOperadoraDetentorArquivo;
    }

    public void setCondutorOperadoraDetentorArquivo(CondutorOperadoraDetentorArquivo condutorOperadoraDetentorArquivo) {
        this.condutorOperadoraDetentorArquivo = condutorOperadoraDetentorArquivo;
    }

    public List<CEPUF> getUfs() {
        return ufs;
    }

    public void setUfs(List<CEPUF> ufs) {
        this.ufs = ufs;
    }

    public List<String> completaCEP(String parte) {
        return facade.getPessoaFacade().getConsultaCepFacade().listaCEPString(parte.trim());
    }

    public List<EquipamentoCondutor> getEquipamentosCondutor() {
        return Arrays.asList(EquipamentoCondutor.values());
    }

    public List<Sexo> getSexo() {
        return Arrays.asList(Sexo.values());
    }

    public List<String> completaEndereco(String parte) {
        String l = "";
        if (selecionado.getEnderecoLogradouro() != null) {
            l = selecionado.getEnderecoLogradouro();
        }
        return facade.getPessoaFacade().getConsultaCepFacade().listaLogradourosString(l, parte.trim());
    }

    public List<String> completaBairro(String parte) {
        String l = "";
        if (selecionado.getBairro() != null) {
            l = selecionado.getBairro();
        }
        return facade.getPessoaFacade().getConsultaCepFacade().listaBairrosString(l, parte.trim());
    }

    public List<String> completaCidade(String parte) {
        String l = "";
        if (selecionado.getCidade() != null) {
            l = selecionado.getCidade();
        }
        return facade.getPessoaFacade().getConsultaCepFacade().listaLocalidadesString(l, parte.trim());
    }

    public List<CEPUF> getListaUF() {
        if (ufs == null) {
            ufs = facade.getPessoaFacade().getConsultaCepFacade().listaUf();
        }
        return ufs;
    }

    public void atualizaLogradouros() {
        List<CEPLogradouro> logradouros = facade.getPessoaFacade().getConsultaCepFacade().consultaLogradouroPorCEP(selecionado.getCep());
        if (!logradouros.isEmpty()) {
            CEPLogradouro logradouro = logradouros.get(0);
            selecionado.setBairro(logradouro.getBairro().getNome());
            selecionado.setEnderecoLogradouro(logradouro.getNomeCompleto());
            selecionado.setUf(logradouro.getLocalidade().getCepuf().getSigla());
            selecionado.setCidade(logradouro.getLocalidade().getNome());
        } else {
            FacesUtil.executaJavaScript("alert('Nenhum endereço foi encontrado para o CEP informado');");
        }
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            if (isOperacaoNovo()) {
                facade.criarHistoricoSituacoesCondutor(selecionado);
                facade.salvarNovo(selecionado);
                facade.gerarCertificadoVeiculo(selecionado);
            } else {
                facade.salvar(selecionado);
                facade.gerarCertificadoVeiculo(selecionado);
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
        if (facade.verificarCpfJaExiste(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("CPF já cadastrado pela operadora informada!");
        }
        ve.lancarException();
    }

    public void adicionarVeiculoOperadoraTransporte() {
        try {
            validarVeiculo();
            condutorOperadoraVeiculoOperadora.setCondutorOperaTransporte(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getCondutorOperadoraVeiculoOperadoras(), condutorOperadoraVeiculoOperadora);
            condutorOperadoraVeiculoOperadora = new CondutorOperadoraVeiculoOperadora();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarVeiculo() {
        ValidacaoException ve = new ValidacaoException();
        if (condutorOperadoraVeiculoOperadora == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o veículo de Transporte!");
        } else if (condutorOperadoraVeiculoOperadora.getVeiculoOTTransporte() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o veículo de Transporte!");
        } else {
            for (CondutorOperadoraVeiculoOperadora veiculoCondutor : selecionado.getCondutorOperadoraVeiculoOperadoras()) {
                if (veiculoCondutor.getVeiculoOTTransporte().equals(condutorOperadoraVeiculoOperadora.getVeiculoOTTransporte())) {
                    ve.adicionarMensagemDeCampoObrigatorio("Veículo de transporte já existente.");
                    break;
                }
            }
        }
        ve.lancarException();
    }

    public void excluirVeiculoOperadoraTransporte(CondutorOperadoraVeiculoOperadora condutorOperadoraVeiculoOperadora) {
        selecionado.getCondutorOperadoraVeiculoOperadoras().remove(condutorOperadoraVeiculoOperadora);
    }

    public List<OperadoraTecnologiaTransporte> completaOperadora(String parte) {
        return facade.getOperadoraTecnologiaTransporteFacade().listarOperadoras(parte);
    }

    public List<VeiculoOperadoraTecnologiaTransporte> completarVeiculos(String parte) {
        if (selecionado.getOperadoraTecTransporte() != null) {
            return facade.getVeiculoOperadoraTecnologiaTransporteFacade().listarVeiculosPorOtt(parte, selecionado.getOperadoraTecTransporte());
        }
        return Lists.newArrayList();
    }

    public void emitirCertificado(CertificadoCondutorOTT certificado) {
        if (certificado != null && certificado.getDocumentoOficial() != null) {
            facade.getDocumentoOficialFacade().emiteDocumentoOficial(certificado.getDocumentoOficial());
        }
    }

    private void validarNovoCertificado() {
        ValidacaoException ve = new ValidacaoException();
        if (veiculoOperadoraTecnologiaTransporte == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o veículo do condutor!");
        } else {
            for (CertificadoCondutorOTT certificado : selecionado.getCertificados()) {
                if (certificado.getVeiculoOtt().equals(veiculoOperadoraTecnologiaTransporte) &&
                    (certificado.getVencimento().after(DataUtil.dataSemHorario(new Date())) || certificado.getVencimento().equals(DataUtil.dataSemHorario(new Date())))) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Ainda existe um certificado não vencido para esse veículo!");
                    break;
                }
            }
        }
        ve.lancarException();
    }

    public void gerarNovoCertificado() {
        try {
            validarNovoCertificado();
            CondutorOperadoraTecnologiaTransporte condutor = facade.gerarNovoCertificado(selecionado, veiculoOperadoraTecnologiaTransporte, selecionado.getDataCadastro());
            if (condutor != null) {
                selecionado = condutor;
                FacesUtil.executaJavaScript("dlgNovoCertificado.hide()");
                veiculoOperadoraTecnologiaTransporte = null;
            } else {
                FacesUtil.addOperacaoNaoRealizada("Não foi encontrado o tipo de documento configurado para a certificado de autorização do condutor!");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public List<SelectItem> getVeiculosDoCondutor() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, ""));
        for (CondutorOperadoraVeiculoOperadora veiculo : selecionado.getCondutorOperadoraVeiculoOperadoras()) {
            retorno.add(new SelectItem(veiculo.getVeiculoOTTransporte(), veiculo.getVeiculoOTTransporte().toString()));
        }
        return retorno;
    }

    public void gerarCertificadoRenovacao(RenovacaoCondutorOTT renovacaoCondutorOTT) {
        try {
            VeiculoOperadoraTecnologiaTransporte veiculo = new VeiculoOperadoraTecnologiaTransporte();
            CondutorOperadoraTecnologiaTransporte condutorOperadora = facade.recuperar(selecionado.getId());
            veiculo = retornarVeiculoOttAprovado(veiculo, condutorOperadora);

            CondutorOperadoraTecnologiaTransporte condutor = facade.gerarNovoCertificado(renovacaoCondutorOTT.getCondutorOtt(), veiculo, renovacaoCondutorOTT.getDataRenovacao());
            if (condutor == null) {
                FacesUtil.addWarn("Atenção", "Não foi encontrado o tipo de documento configurado para a certificado de renovação de autorização");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private VeiculoOperadoraTecnologiaTransporte retornarVeiculoOttAprovado(VeiculoOperadoraTecnologiaTransporte veiculo, CondutorOperadoraTecnologiaTransporte condutorOperadora) {
        for (CondutorOperadoraVeiculoOperadora operadoraVeiculoOperadora : condutorOperadora.getCondutorOperadoraVeiculoOperadoras()) {
            if (SituacaoVeiculoOTT.APROVADO.equals(operadoraVeiculoOperadora.getVeiculoOTTransporte().getSituacaoVeiculoOTT())
                && operadoraVeiculoOperadora.getVeiculoOTTransporte().getCertificadoInscricao() != null
                && operadoraVeiculoOperadora.getVeiculoOTTransporte().getCertificadoInscricao().getVencimento().after(new Date())) {
                veiculo = operadoraVeiculoOperadora.getVeiculoOTTransporte();
                return veiculo;
            }
            for (RenovacaoVeiculoOTT renovacaoVeiculoOTT : operadoraVeiculoOperadora.getVeiculoOTTransporte().getRenovacaoVeiculoOTTS()) {
                if (SituacaoRenovacaoOTT.APROVADO.equals(renovacaoVeiculoOTT.getSituacaoRenovacao())) {
                    veiculo = renovacaoVeiculoOTT.getVeiculoOtt();
                    break;
                }
            }
        }
        return veiculo;
    }

    public void uploadDocumentoCondutor(FileUploadEvent event) {
        try {
            DetentorArquivoComposicao detentorArquivoComposicao = new DetentorArquivoComposicao();
            detentorArquivoComposicao.setArquivoComposicao(new ArquivoComposicao());
            detentorArquivoComposicao.getArquivoComposicao().setDataUpload(new Date());
            detentorArquivoComposicao.getArquivoComposicao().setUsuario(facade.getSistemaFacade().getUsuarioCorrente());
            Arquivo arquivo = facade.getOperadoraTecnologiaTransporteFacade().getArquivoFacade().criarArquivo(event.getFile());
            detentorArquivoComposicao.getArquivoComposicao().setArquivo(arquivo);
            condutorOperadoraDetentorArquivo.setDetentorArquivoComposicao(detentorArquivoComposicao);
            FacesUtil.executaJavaScript("dlgUploadDocumentoCondutor.hide()");
            FacesUtil.atualizarComponente("Formulario:tbView:opAnexos");
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void substituirDocumentoCondutor(CondutorOperadoraDetentorArquivo condutorOperadoraDetentorArquivo) {
        setCondutorOperadoraDetentorArquivo(condutorOperadoraDetentorArquivo);
        this.condutorOperadoraDetentorArquivo.setDetentorArquivoComposicao(null);
        FacesUtil.executaJavaScript("dlgUploadDocumentoCondutor.show()");
        FacesUtil.atualizarComponente("formularioUploadDocumentoCondutor");
    }

    public void alterouServidorPublico() {
        facade.atribuirDocumentosCondutor(selecionado);
    }

    public void iniciarAvaliacaoDocumentacao() {
        documentosAvaliacao = selecionado.getCondutorOperadoraDetentorArquivos()
            .stream()
            .filter(doc -> doc.getStatus().equals(StatusDocumentoOtt.AGUARDANDO_AVALIACAO))
            .collect(Collectors.toList());
    }

    public void mudouStatusAvaliacaoDocumento(CondutorOperadoraDetentorArquivo anexo) {
        anexo.setObservacao(null);
    }

    public void mudouStatusAvaliacaoDocumento(CondutorRenovacaoDetentorArquivo anexo) {
        anexo.setObservacao(null);
    }

    public void confirmarAvaliacaoDocumentos() {
        try {
            validarAvaliacaoDocumentos();
            facade.confirmarAvaliacaoDocumentos(selecionado, documentosAvaliacao, retornarVeiculoOttAprovado(new VeiculoOperadoraTecnologiaTransporte(), selecionado));
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

    public void iniciarAvaliacaoDocumentacaoRenovacao(RenovacaoCondutorOTT renovacaoCondutorOTT) {
        this.renovacaoCondutorOTT = renovacaoCondutorOTT;
        documentosAvaliacaoRenovacao = this.renovacaoCondutorOTT.getCondutorRenovacaoDetentorArquivos()
            .stream()
            .filter(doc -> doc.getStatus().equals(StatusDocumentoOtt.AGUARDANDO_AVALIACAO))
            .collect(Collectors.toList());
    }

    public void confirmarAvaliacaoDocumentosRenovacao() {
        try {
            validarAvaliacaoDocumentosRenovacao();
            facade.confirmarAvaliacaoDocumentosRenovacao(renovacaoCondutorOTT, documentosAvaliacaoRenovacao);
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

    public void uploadFoto(FileUploadEvent event) {
        try {
            Arquivo arquivo = facade.getArquivoFacade().criarArquivo(event.getFile());
            selecionado.setFoto(arquivo);
            carregarFoto();
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    private void carregarFoto() {
        imgSrc = null;
        if (selecionado.getFoto() == null) return;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            for (ArquivoParte a : selecionado.getFoto().getPartes()) {
                buffer.write(a.getDados());
            }
            String base64 = Util.getBase64Encode(buffer.toByteArray());
            imgSrc = "data:image/png;base64, " + base64;
        } catch (Exception ex) {
            logger.error("Erro: ", ex);
        }
    }

    public void trocarFoto() {
        selecionado.setFoto(null);
    }
}
