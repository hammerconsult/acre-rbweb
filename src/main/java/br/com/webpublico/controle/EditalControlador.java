package br.com.webpublico.controle;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.Edital;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ArquivoFacade;
import br.com.webpublico.negocios.EditalFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Preconditions;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 20/08/15
 * Time: 14:42
 * To change this template use File | Settings | File Templates.
 */

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoEdital", pattern = "/edital/novo/", viewId = "/faces/edital/edita.xhtml"),
        @URLMapping(id = "listarEditais", pattern = "/edital/listar/", viewId = "/faces/edital/lista.xhtml"),
        @URLMapping(id = "editarEdital", pattern = "/edital/editar/#{editalControlador.id}/", viewId = "/faces/edital/edita.xhtml"),
        @URLMapping(id = "verEdital", pattern = "/edital/ver/#{editalControlador.id}/", viewId = "/faces/edital/visualizar.xhtml"),
})
public class EditalControlador extends PrettyControlador<Edital> implements Serializable, CRUD {

    @EJB
    private EditalFacade editalFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private FileUploadEvent fileUploadEvent;

    public EditalControlador() {
        super(Edital.class);
    }

    @URLAction(mappingId = "novoEdital", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        inicializar();
    }

    @URLAction(mappingId = "verEdital", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarEdital", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    private void inicializar() {
        selecionado.setDataPublicacao(sistemaControlador.getDataOperacao());
        selecionado.setExercicio(sistemaControlador.getExercicioCorrente());
    }

    public void apagaArquivo() {
        selecionado.setArquivo(null);
    }

    public void uploadArquivo(FileUploadEvent evento) {
        fileUploadEvent = evento;
        try {
            Preconditions.checkNotNull(fileUploadEvent, "Evento de upload do arquivo esta vazio entre em contato com o Suporte!");
            UploadedFile arquivoRecebido = fileUploadEvent.getFile();
            Arquivo arquivo = new Arquivo();
            arquivo.setDescricao(arquivoRecebido.getFileName());
            arquivo.setNome(arquivoRecebido.getFileName());
            arquivo.setMimeType(arquivoRecebido.getContentType());
            arquivo.setTamanho(arquivoRecebido.getSize());
            selecionado.setArquivo(arquivoFacade.novoArquivoMemoria(arquivo, evento.getFile().getInputstream()));
        } catch (Exception e) {
            FacesUtil.addErrorGenerico(e);
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/edital/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return editalFacade;
    }

    public FileUploadEvent getFileUploadEvent() {
        return fileUploadEvent;
    }

    public void setFileUploadEvent(FileUploadEvent fileUploadEvent) {
        this.fileUploadEvent = fileUploadEvent;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    protected boolean validaRegrasParaSalvar() {
        if (!Util.validaCampos(selecionado)) {
            return false;
        }

        return true;
    }

    @Override
    public void salvar() {
        if (validaRegrasParaSalvar()) {
            try {
                selecionado = editalFacade.salvarRetornando(selecionado);
                String edital = " Edital Nº " + selecionado.getNumero() + " de " + selecionado.getExercicio().getAno();
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoSalvar() + edital));
            } catch (ValidacaoException ex) {
                FacesUtil.printAllFacesMessages(ex.getMensagens());
                return;
            } catch (Exception e) {
                descobrirETratarException(e);
            }
            redireciona();
        }
    }
}
