package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.PortalContribunteFacade;
import br.com.webpublico.negocios.UsuarioSaudFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.ws.model.ConsultaCEP;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Usuario
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listarUsuarioSaud",
        pattern = "/tributario/saud/usuario-saud/listar/",
        viewId = "/faces/tributario/saud/usuariosaud/lista.xhtml"),
    @URLMapping(id = "novoUsuarioSaud",
        pattern = "/tributario/saud/usuario-saud/novo/",
        viewId = "/faces/tributario/saud/usuariosaud/edita.xhtml"),
    @URLMapping(id = "editarUsuarioSaud",
        pattern = "/tributario/saud/usuario-saud/editar/#{usuarioSaudControlador.id}/",
        viewId = "/faces/tributario/saud/usuariosaud/edita.xhtml"),
    @URLMapping(id = "verUsuarioSaud",
        pattern = "/tributario/saud/usuario-saud/ver/#{usuarioSaudControlador.id}/",
        viewId = "/faces/tributario/saud/usuariosaud/visualizar.xhtml")
})
public class UsuarioSaudControlador extends PrettyControlador<UsuarioSaud> implements Serializable, CRUD {

    @EJB
    private UsuarioSaudFacade facade;
    @EJB
    private PortalContribunteFacade portalContribunteFacade;
    private UsuarioSaudDocumento usuarioSaudDocumento;
    private ParametroSaudDocumento documentoParametroSelecionado;
    private List<ParametroSaudDocumento> documentosObrigatorios;
    private ConverterAutoComplete converterDocumentoParametro;

    public UsuarioSaudControlador() {
        super(UsuarioSaud.class);
    }

    @Override
    public UsuarioSaudFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/saud/usuario-saud/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public UsuarioSaudDocumento getUsuarioSaudDocumento() {
        return usuarioSaudDocumento;
    }

    public void setUsuarioSaudDocumento(UsuarioSaudDocumento usuarioSaudDocumento) {
        this.usuarioSaudDocumento = usuarioSaudDocumento;
    }

    public List<ParametroSaudDocumento> getDocumentosObrigatorios() {
        if (documentosObrigatorios == null) {
            documentosObrigatorios = Lists.newArrayList();
        }
        return documentosObrigatorios;
    }

    public void setDocumentosObrigatorios(List<ParametroSaudDocumento> documentosObrigatorios) {
        this.documentosObrigatorios = documentosObrigatorios;
    }

    public ParametroSaudDocumento getDocumentoParametroSelecionado() {
        return documentoParametroSelecionado;
    }

    public void setDocumentoParametroSelecionado(ParametroSaudDocumento documentoParametroSelecionado) {
        this.documentoParametroSelecionado = documentoParametroSelecionado;
    }

    @URLAction(mappingId = "novoUsuarioSaud", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        facade.inicializarDocumentosObrigatorios(selecionado);
        carregarDocumentosObrigatorios();
        atualizarDescricaoDocumento();
    }


    @URLAction(mappingId = "editarUsuarioSaud", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        if (!UsuarioSaud.Status.CADASTRADO.equals(selecionado.getStatus())) {
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
            return;
        }
        carregarFoto();
        carregarDocumentosObrigatorios();
        atualizarDescricaoDocumento();
    }

    @URLAction(mappingId = "verUsuarioSaud", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
        carregarFoto();
        atualizarDescricaoDocumento();
    }

    private void carregarDocumentosObrigatorios() {
        ParametroSaud parametroSaud = facade.getParametroSaudFacade().recuperarUltimo();
        if (parametroSaud != null) {
            documentosObrigatorios = parametroSaud.getDocumentos();
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

    private void atualizarDescricaoDocumento() {
        for (UsuarioSaudDocumento documento : selecionado.getDocumentos()) {
            if (documento.getParametroSaudDocumento() != null) {
                documento.setDescricao(documento.getParametroSaudDocumento().getDescricao());
            }
        }
    }

    public List<SelectItem> selectItemDocumentosObrigatorios() {
        return Util.getListSelectItem(getDocumentosObrigatorios());
    }

    public ConverterAutoComplete getConverterDocumentoParametro() {
        if (converterDocumentoParametro == null) {
            converterDocumentoParametro = new ConverterAutoComplete(ParametroSaudDocumento.class, facade.getParametroSaudFacade());
        }
        return converterDocumentoParametro;
    }

    private void carregarFoto() {
        if (selecionado.getFoto() == null) return;
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            for (ArquivoParte a : selecionado.getFoto().getPartes()) {
                buffer.write(a.getDados());
            }
            InputStream inputStream = new ByteArrayInputStream(buffer.toByteArray());
            DefaultStreamedContent imagemFoto = new DefaultStreamedContent(inputStream, selecionado.getFoto().getMimeType(), selecionado.getFoto().getNome());
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("imagem-foto", imagemFoto);
        } catch (Exception ex) {
            logger.error("Erro: ", ex);
        }
    }

    public void trocarFoto() {
        selecionado.setFoto(null);
    }

    public void mudouUf() {
        selecionado.getEndereco().setCidade(null);
    }

    public List<Cidade> completarCidade(String parte) {
        if (selecionado.getEndereco().getUf() == null) return Lists.newArrayList();
        return facade.getCidadeFacade().consultaCidade(selecionado.getEndereco().getUf().getUf(), parte);
    }

    public List<UsuarioSaud> completarUsuario(String parte) {
        return facade.listaFiltrando(parte, "nome", "cpf");
    }

    public void selecionarDocumento(UsuarioSaudDocumento usuarioSaudDocumento) {
        this.usuarioSaudDocumento = usuarioSaudDocumento;
        if (!documentoDoParametro()) {
            for (ParametroSaudDocumento documentoObrigatorios : documentosObrigatorios) {
                if (usuarioSaudDocumento.getDescricao().equals(documentoObrigatorios.getDescricao())) {
                    documentoParametroSelecionado = documentoObrigatorios;
                    break;
                }
            }
        }
    }

    public void uploadDocumento(FileUploadEvent event) {
        try {
            ValidacaoException ve = new ValidacaoException();
            if (!documentoDoParametro() && documentoParametroSelecionado == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo documento deve ser informado.");
                ve.lancarException();
            }
            if (documentoDoParametro()) {
                usuarioSaudDocumento.setDescricao(usuarioSaudDocumento.getDescricao());
            } else {
                usuarioSaudDocumento.setDescricao(documentoParametroSelecionado.getDescricao());
            }
            Arquivo arquivo = facade.getArquivoFacade().criarArquivo(event.getFile());
            usuarioSaudDocumento.setDocumento(arquivo);
            boolean docJaNaLista = false;
            for (int i = 0; i < selecionado.getDocumentos().size(); i++) {
                if (usuarioSaudDocumento.hashCode() == selecionado.getDocumentos().get(i).hashCode()) {
                    selecionado.getDocumentos().set(i, usuarioSaudDocumento);
                    docJaNaLista = true;
                    break;
                }
            }
            if (!docJaNaLista) selecionado.getDocumentos().add(usuarioSaudDocumento);
            FacesUtil.executaJavaScript("dlgUploadDocumento.hide()");
            FacesUtil.atualizarComponente("form:tbView:opDocumentos");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void adicionarDocumento() {
        usuarioSaudDocumento = new UsuarioSaudDocumento(selecionado, null);
        FacesUtil.executaJavaScript("dlgUploadDocumento.show()");
        FacesUtil.atualizarComponente("formUploadDocumento");
    }

    public void removerDocumento(UsuarioSaudDocumento usuarioSaudDocumento) {
        selecionado.getDocumentos().remove(usuarioSaudDocumento);
        FacesUtil.atualizarComponente("formUploadDocumento");
    }

    public void substituirDocumento(UsuarioSaudDocumento usuarioSaudDocumento) {
        selecionarDocumento(usuarioSaudDocumento);
        this.usuarioSaudDocumento.setDocumento(null);
        FacesUtil.executaJavaScript("dlgUploadDocumento.show()");
        FacesUtil.atualizarComponente("formUploadDocumento");
    }

    public boolean documentoDoParametro() {
        return usuarioSaudDocumento != null && usuarioSaudDocumento.getParametroSaudDocumento() != null;
    }

    public void ativar() {
        facade.ativarUsuario(selecionado);
        selecionado = facade.recuperar(selecionado.getId());
        if (selecionado.getUsuarioWeb() != null) {
            portalContribunteFacade.getUsuarioWebFacade().criarNovaSenhaeEnviarPorEmail(selecionado.getUsuarioWeb());
        }
        FacesUtil.addOperacaoRealizada("Usuário ativado com sucesso!");
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
    }

    public void inativar() {
        facade.inativarUsuario(selecionado);
        FacesUtil.addOperacaoRealizada("Usuário inativado com sucesso!");
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
    }

    public StreamedContent baixarTodosDocumentos() throws IOException {
        List<Arquivo> arquivos = selecionado.getDocumentos().stream()
            .map(UsuarioSaudDocumento::getDocumento).collect(Collectors.toList());
        return facade.getArquivoFacade().ziparArquivos("Documentos anexados", arquivos);
    }

    public void emitirCarteira() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("idUsuario", selecionado.getId());
            dto.setNomeRelatorio("EMISSAO-CARTEIRA-USUARIO-SAUD-" + selecionado.getId());
            dto.setApi("tributario/carteira-usuario-saud/");
            ReportService.getInstance().gerarRelatorio(facade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    public List<SelectItem> getStatusAvaliacaoDocumento() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        toReturn.add(new SelectItem(UsuarioSaudDocumento.Status.APROVADO, UsuarioSaudDocumento.Status.APROVADO.getDescricao()));
        toReturn.add(new SelectItem(UsuarioSaudDocumento.Status.REJEITADO, UsuarioSaudDocumento.Status.REJEITADO.getDescricao()));
        return toReturn;
    }

    public void mudouStatusAvaliacaoDocumento(UsuarioSaudDocumento usuarioSaudDocumento) {
        usuarioSaudDocumento.setObservacao(null);
    }

    public void confirmarAvaliacaoDocumentos() {
        try {
            validarAvaliacaoDocumentos();
            facade.confirmarAvaliacaoDocumentos(selecionado);
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    private void validarAvaliacaoDocumentos() {
        boolean semStatus = false;
        boolean semObservacao = false;
        for (UsuarioSaudDocumento documento : selecionado.getDocumentos()) {
            if (documento.getStatus() == null) {
                semStatus = true;
            }
            if (UsuarioSaudDocumento.Status.REJEITADO.equals(documento.getStatus()) &&
                Strings.isNullOrEmpty(documento.getObservacao())) {
                semObservacao = true;
            }
        }
        if (semStatus || semObservacao) {
            throw new ValidacaoException("Defina o status de todos os documentos, " +
                "caso esteja rejeitado descreva o motivo da rejeição.");
        }
    }

    public Boolean podeAprovar() {
        return !UsuarioSaud.Status.ATIVO.equals(selecionado.getStatus())
            && selecionado.getDocumentacaoAprovada();
    }

    public void consultarCEP() {
        if (Strings.isNullOrEmpty(selecionado.getEndereco().getCep()) ||
            selecionado.getEndereco().getCep().length() < 8) return;
        selecionado.getEndereco().setBairro("");
        selecionado.getEndereco().setLogradouro("");
        selecionado.getEndereco().setCidade(null);
        selecionado.getEndereco().setUf(null);
        ConsultaCEP consultaCEP = facade.getConsultaCepFacade().buscarCEP(selecionado.getEndereco().getCep());
        if (consultaCEP != null) {
            selecionado.getEndereco().setBairro(consultaCEP.getBairro());
            selecionado.getEndereco().setLogradouro(consultaCEP.getLogradouro());
            Cidade cidade = facade.getCidadeFacade().recuperaCidadePorNomeEEstado(consultaCEP.getLocalidade(),
                consultaCEP.getUf());
            if (cidade != null) {
                selecionado.getEndereco().setCidade(cidade);
                selecionado.getEndereco().setUf(cidade.getUf());

            }
        }
    }

    public String buscarExtensoesPermitidas() {
        String extensoesPermitidas = "";
        if (documentoDoParametro()) {
            extensoesPermitidas = usuarioSaudDocumento.getParametroSaudDocumento().getExtensoesPermitidas();
        } else if (documentoParametroSelecionado != null) {
            extensoesPermitidas = documentoParametroSelecionado.getExtensoesPermitidas();
        }
        extensoesPermitidas = extensoesPermitidas.replace(".", "");
        extensoesPermitidas = extensoesPermitidas.replace(",", "|");
        return "/(\\.|\\/)(" + extensoesPermitidas + ")$/";
    }

    public String buscarMensagemExtensoesPermitidas() {
        String extensoesPermitidas = "";
        if (documentoDoParametro()) {
            extensoesPermitidas = usuarioSaudDocumento.getParametroSaudDocumento().getExtensoesPermitidas();
        } else if (documentoParametroSelecionado != null) {
            extensoesPermitidas = documentoParametroSelecionado.getExtensoesPermitidas();
        }
        if (Strings.isNullOrEmpty(extensoesPermitidas)) return "";
        return "Extensão invalida, apenas as extenções (" + extensoesPermitidas + ") são permitidas.";
    }
}
