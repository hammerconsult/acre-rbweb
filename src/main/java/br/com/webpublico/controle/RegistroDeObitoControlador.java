/*
 * Codigo gerado automaticamente em Thu Mar 22 09:22:05 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.ArquivoRegistroDeObito;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.RegistroDeObito;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.RegistroDeObitoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "registroDeObitoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRegistroDeObito", pattern = "/registro-obito/novo/", viewId = "/faces/rh/administracaodepagamento/registrodeobito/edita.xhtml"),
    @URLMapping(id = "editarRegistroDeObito", pattern = "/registro-obito/editar/#{registroDeObitoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/registrodeobito/edita.xhtml"),
    @URLMapping(id = "listarRegistroDeObito", pattern = "/registro-obito/listar/", viewId = "/faces/rh/administracaodepagamento/registrodeobito/lista.xhtml"),
    @URLMapping(id = "verRegistroDeObito", pattern = "/registro-obito/ver/#{registroDeObitoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/registrodeobito/visualizar.xhtml")
})
public class RegistroDeObitoControlador extends PrettyControlador<RegistroDeObito> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(RegistroDeObitoControlador.class);

    @EJB
    private RegistroDeObitoFacade registroDeObitoFacade;
    private FileUploadEvent fileUploadEvent;
    private UploadedFile file;
    private ArquivoRegistroDeObito arquivoRegistroDeObito;
    private List<Arquivo> arquivosParaRemover;

    public RegistroDeObitoControlador() {
        super(RegistroDeObito.class);
    }

    public RegistroDeObitoFacade getFacade() {
        return registroDeObitoFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return registroDeObitoFacade;
    }

    public ConverterAutoComplete getConverterPessoaFisica() {
        return new ConverterAutoComplete(PessoaFisica.class, registroDeObitoFacade.getPessoaFisicaFacade());
    }

    public List<PessoaFisica> completaPessoaFisica(String parte) {
        return registroDeObitoFacade.listaMatriculaFPSemObito(parte.trim());
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (registroDeObitoFacade.existeRegristroDeObitoPorPessoaFisica(selecionado)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A pessoa selecionada já possui um registro de óbito cadastrado!");
        }

        if (selecionado.getFePublica()) {
            if (selecionado.getDataFalecimento() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("A data de falecimento é um campo obrigatório");
            }

            if (selecionado.getPessoaFisica() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O Servidor é um campo obrigatório");
            }

            ve.lancarException();
            if (selecionado.getDataFalecimento().after(new Date())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data de óbito não pode ser maior que a data corrente.");
            }
        } else {
            if (selecionado.getDataFalecimento() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("A data de falecimento é um campo obrigatório");
            }

            if (selecionado.getPessoaFisica() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O Servidor é um campo obrigatório");
            }

            if (selecionado.getMatriculaCertidao().equals("")) {
                ve.adicionarMensagemDeCampoObrigatorio("A Matrícula da Certidão de Óbito é um campo obrigatório");
            }

            if (selecionado.getNumeroObito().equals("")) {
                ve.adicionarMensagemDeCampoObrigatorio("O Documento de Identificação é um campo obrigatório");
            }

            if (Strings.isNullOrEmpty(selecionado.getCartorio())) {
                ve.adicionarMensagemDeCampoObrigatorio("O Cartório é um campo obrigatório");
            }

            ve.lancarException();
            if (selecionado.getDataFalecimento().after(new Date())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data de óbito não pode ser maior que a data corrente.");
            }
        }

        if (selecionado.getArquivoRegistroDeObitos() == null || selecionado.getArquivoRegistroDeObitos().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A inclusão do arquivo da Certidão de Óbito é obrigatória.");
        } else {
            boolean hasArquivoCertidaoObito = false;
            for (ArquivoRegistroDeObito ardo : selecionado.getArquivoRegistroDeObitos()) {
                if (ardo.getCertidaoObito()) {
                    hasArquivoCertidaoObito = true;
                    break;
                }
            }
            if (!hasArquivoCertidaoObito) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A inclusão do arquivo da Certidão de Óbito é obrigatória.");
            }
        }
        ve.lancarException();
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            if (operacao == Operacoes.NOVO) {
                registroDeObitoFacade.salvarNovo(selecionado);
            } else {
                registroDeObitoFacade.salvar(selecionado, arquivosParaRemover);
            }
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    @URLAction(mappingId = "editarRegistroDeObito", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        selecionado = registroDeObitoFacade.recuperar(selecionado.getId());
        fileUploadEvent = null;
        arquivoRegistroDeObito = new ArquivoRegistroDeObito();
        arquivosParaRemover = Lists.newArrayList();
    }

    @URLAction(mappingId = "verRegistroDeObito", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public String getCaminhoPadrao() {
        return "/registro-obito/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoRegistroDeObito", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        fileUploadEvent = null;
        selecionado.setFePublica(Boolean.FALSE);
        selecionado.setDataFalecimento(new Date());
        arquivoRegistroDeObito = new ArquivoRegistroDeObito();
    }


    public void excluirSelecionado() {
        try {
            if (registroDeObitoFacade.podeExcluir((RegistroDeObito) selecionado)) {
                getFacede().remover(selecionado);
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro.", "Este é um registro de óbito de um Instituidor de pensão! É impossivel excluí-lo!"));
                return;
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Excluído com sucesso!", "O registro selecionado foi excluído com sucesso!"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Exceção do sistema!", e.getMessage()));
        }
    }

    public void handleFileUpload(FileUploadEvent event) {
        try {
            fileUploadEvent = event;
            file = event.getFile();
            Arquivo arquivo = new Arquivo();

            arquivo.setNome(file.getFileName());
            arquivo.setMimeType(file.getContentType());
            arquivo.setTamanho(file.getSize());
            arquivo.setInputStream(file.getInputstream());
            arquivo = registroDeObitoFacade.novoArquivoMemoria(arquivo);
            arquivoRegistroDeObito.setArquivo(arquivo);
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao() + " Erro ao fazer o upload do arquivo.");
        }
    }

    public ArquivoRegistroDeObito getArquivoRegistroDeObito() {
        return arquivoRegistroDeObito;
    }

    public void setArquivoRegistroDeObito(ArquivoRegistroDeObito arquivoRegistroDeObito) {
        this.arquivoRegistroDeObito = arquivoRegistroDeObito;
    }

    public void adicionarArquivoRegistro() {
        try {
            validarArquivoRegisro();

            arquivoRegistroDeObito.setRegistroDeObito(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getArquivoRegistroDeObitos(), arquivoRegistroDeObito);
            arquivoRegistroDeObito = new ArquivoRegistroDeObito();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao() + " Erro ao adicionar o arquivo.");
        }
    }

    public void validarArquivoRegisro() {
        ValidacaoException ve = new ValidacaoException();
        if (arquivoRegistroDeObito.getArquivo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Deve ser selecionado um arquivo para adicionar um anexo.");
        }

        if (Strings.isNullOrEmpty(arquivoRegistroDeObito.getDescricao())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Descrição do Arquivo deve ser preenchido.");
        }
        ve.lancarException();
    }

    public void editarArquivoRegistro(ArquivoRegistroDeObito arq) {
        arquivoRegistroDeObito = (ArquivoRegistroDeObito) Util.clonarObjeto(arq);
    }

    public void removerArquivoRegistro(ArquivoRegistroDeObito arq) {
        selecionado.getArquivoRegistroDeObitos().remove(arq);
        if (arq.getArquivo().getId() != null) {
            arquivosParaRemover.add(arq.getArquivo());
        }
        arquivoRegistroDeObito = new ArquivoRegistroDeObito();
    }

    public StreamedContent recuperarArquivoParaDownload(Arquivo arq) {
        try {
            return registroDeObitoFacade.montarArquivoParaDownloadPorArquivo(arq);
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
            return null;
        }
    }

    public void cancelarEditarArquivo() {
        arquivoRegistroDeObito = new ArquivoRegistroDeObito();
    }

    public void atualizarDescricaoArquivo() {
        if (arquivoRegistroDeObito != null && arquivoRegistroDeObito.getCertidaoObito() &&
            (arquivoRegistroDeObito.getDescricao() == null || arquivoRegistroDeObito.getDescricao().isEmpty())) {
            arquivoRegistroDeObito.setDescricao("Certidão de Óbito");
        }
    }

    public void uploadArquivo(FileUploadEvent evento) {
        fileUploadEvent = evento;
    }

    public FileUploadEvent getFileUploadEvent() {
        return fileUploadEvent;
    }

    public void setFileUploadEvent(FileUploadEvent fileUploadEvent) {
        this.fileUploadEvent = fileUploadEvent;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public List<Arquivo> getArquivosParaRemover() {
        return arquivosParaRemover;
    }

    public void setArquivosParaRemover(List<Arquivo> arquivosParaRemover) {
        this.arquivosParaRemover = arquivosParaRemover;
    }
}
