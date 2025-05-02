package br.com.webpublico.controle;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.PortalTransparencia;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.PortalTipoAnexo;
import br.com.webpublico.enums.PortalTransparenciaSituacao;
import br.com.webpublico.enums.PortalTransparenciaTipo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.PortalTransparenciaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Buzatto
 * Date: 30/12/13
 * Time: 11:05
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "portalTransparenciaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoPortalTransparencia", pattern = "/anexo-portal-transparencia/novo/", viewId = "/faces/financeiro/portaldatransparencia/edita.xhtml"),
    @URLMapping(id = "editarPortalTransparencia", pattern = "/anexo-portal-transparencia/editar/#{portalTransparenciaControlador.id}/", viewId = "/faces/financeiro/portaldatransparencia/edita.xhtml"),
    @URLMapping(id = "listarPortalTransparencia", pattern = "/anexo-portal-transparencia/listar/", viewId = "/faces/financeiro/portaldatransparencia/lista.xhtml"),
    @URLMapping(id = "verPortalTransparencia", pattern = "/anexo-portal-transparencia/ver/#{portalTransparenciaControlador.id}/", viewId = "/faces/financeiro/portaldatransparencia/visualizar.xhtml")
})
public class PortalTransparenciaControlador extends PrettyControlador<PortalTransparencia> implements Serializable, CRUD {

    @EJB
    private PortalTransparenciaFacade portalTransparenciaFacade;
    private Arquivo arquivo;
    private FileUploadEvent fileUploadEvent;
    private ConverterAutoComplete converterExercicio;

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return portalTransparenciaFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/anexo-portal-transparencia/";
    }


    @URLAction(mappingId = "novoPortalTransparencia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        inicializar();
    }

    @URLAction(mappingId = "verPortalTransparencia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        inicializarObjetosAoVerEditar();
    }

    @URLAction(mappingId = "editarPortalTransparencia", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        inicializarObjetosAoVerEditar();
    }

    private void inicializar() {
        arquivo = null;
        fileUploadEvent = null;
        selecionado.setExercicio(portalTransparenciaFacade.getSistemaFacade().getExercicioCorrente());
        selecionado.setUsuarioSistema(portalTransparenciaFacade.getSistemaFacade().getUsuarioCorrente());
    }

    @Override
    public void salvar() {
        try {
            Util.validarCampos(selecionado);
            validarCamposAdicionais();
            if (isOperacaoNovo()) {
                validarAnexoJaCadastrado();
                portalTransparenciaFacade.salvarNovo(selecionado, arquivo, fileUploadEvent);
            } else {
                portalTransparenciaFacade.salvar(selecionado, arquivo, fileUploadEvent);
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Operação Realizada!", " Registro salvo com sucesso."));
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addError(null, ex.getMessage());
        }
    }

    private void validarCamposAdicionais() {
        ValidacaoException ve = new ValidacaoException();
        if ((isOperacaoNovo() && fileUploadEvent == null) || (isOperacaoEditar() && arquivo == null)) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Arquivo deve ser informado.");
        }
        if (selecionado.getTipoAnexo() == null && (selecionado.getTipo().isTipoAnexoLRF() || selecionado.getTipo().isTipoAnexoLei4320())) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo Anexo deve ser informado.");
        }
        ve.lancarException();
    }

    private void validarAnexoJaCadastrado() {
        ValidacaoException ve = new ValidacaoException();
        PortalTransparencia anexoPortalTransparencia = portalTransparenciaFacade.buscarAnexoPorExercicioTipoTipoAnexoEMes(selecionado.getExercicio(), selecionado.getTipoAnexo(), selecionado.getMes());
        if (anexoPortalTransparencia != null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não deve existir mais que um registro com o mesmo Exercício: <b>" + selecionado.getExercicio() +
                "</b>, Tipo: <b>" + selecionado.getTipoAnexo().getTipo().getDescricao() + " - " + selecionado.getTipoAnexo().getDescricao() + "</b>" +
                (selecionado.getMes() != null ? " e Mês: <b>" + selecionado.getMes().getDescricao() + "</b>" : ""));
        }
        ve.lancarException();
    }

    private void inicializarObjetosAoVerEditar() {
        arquivo = new Arquivo();
        fileUploadEvent = null;

        if (selecionado.getArquivo() != null) {
            arquivo = selecionado.getArquivo();
        }
    }

    public void apagaArquivo() {
        selecionado.setArquivo(null);
        arquivo = null;
        fileUploadEvent = null;
    }

    public void uploadArquivo(FileUploadEvent evento) {
        fileUploadEvent = evento;
        arquivo = new Arquivo();
        if (evento != null) {
            arquivo.setDescricao(evento.getFile().getFileName());
        }
    }

    public ConverterAutoComplete getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterAutoComplete(Exercicio.class, portalTransparenciaFacade.getExercicioFacade());
        }
        return converterExercicio;
    }

    public List<Exercicio> completaExercicio(String parte) {
        return portalTransparenciaFacade.getExercicioFacade().listaFiltrandoEspecial(parte.trim());
    }

    public List<SelectItem> getMes() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (Mes object : Mes.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getSituacao() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (PortalTransparenciaSituacao object : PortalTransparenciaSituacao.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipos() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (PortalTransparenciaTipo object : PortalTransparenciaTipo.values()) {
            if (!PortalTransparenciaTipo.GERAL.equals(object)) {
                toReturn.add(new SelectItem(object, object.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getTiposAnexos() {
        return Util.getListSelectItem(PortalTipoAnexo.buscarAnexosPorTipo(selecionado.getTipo()), false);
    }

    public void limparTipoAnexo() {
        selecionado.setTipoAnexo(null);
    }

    public PortalTransparenciaControlador() {
        super(PortalTransparencia.class);
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public FileUploadEvent getFileUploadEvent() {
        return fileUploadEvent;
    }

    public void setFileUploadEvent(FileUploadEvent fileUploadEvent) {
        this.fileUploadEvent = fileUploadEvent;
    }
}
