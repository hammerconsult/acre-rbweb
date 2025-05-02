package br.com.webpublico.controle.tributario;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.AnexoControleLicenciamentoAmbiental;
import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.tributario.ControleLicenciamentoAmbiental;
import br.com.webpublico.entidades.tributario.ProcessoLicenciamentoAmbiental;
import br.com.webpublico.enums.tributario.StatusLicenciamentoAmbiental;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.tributario.ControleLicenciamentoAmbientalFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoControleLicenciamentoAmbiental",
        pattern = "/tributario/licenciamento-ambiental/controle/novo/",
        viewId = "/faces/tributario/licenciamentoambiental/controle/edita.xhtml"),
    @URLMapping(id = "editarControleLicenciamentoAmbiental",
        pattern = "/tributario/licenciamento-ambiental/controle/editar/#{controleLicenciamentoAmbientalControlador.id}/",
        viewId = "/faces/tributario/licenciamentoambiental/controle/edita.xhtml"),
    @URLMapping(id = "verControleLicenciamentoAmbiental",
        pattern = "/tributario/licenciamento-ambiental/controle/ver/#{controleLicenciamentoAmbientalControlador.id}/",
        viewId = "/faces/tributario/licenciamentoambiental/controle/visualizar.xhtml"),
    @URLMapping(id = "listarControleLicenciamentoAmbiental",
        pattern = "/tributario/licenciamento-ambiental/controle/listar/",
        viewId = "/faces/tributario/licenciamentoambiental/controle/lista.xhtml")
})
public class ControleLicenciamentoAmbientalControlador extends PrettyControlador<ControleLicenciamentoAmbiental> implements CRUD, Serializable {

    @EJB
    private ControleLicenciamentoAmbientalFacade facade;
    private List<ProcessoLicenciamentoAmbiental> processos;
    private AnexoControleLicenciamentoAmbiental anexoSelecionado;

    @Override
    public ControleLicenciamentoAmbientalFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/licenciamento-ambiental/controle/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public ControleLicenciamentoAmbientalControlador() {
        super(ControleLicenciamentoAmbiental.class);
    }

    public List<ProcessoLicenciamentoAmbiental> getProcessos() {
        return processos;
    }

    public void setProcessos(List<ProcessoLicenciamentoAmbiental> processos) {
        this.processos = processos;
    }

    @URLAction(mappingId = "verControleLicenciamentoAmbiental", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarControleLicenciamentoAmbiental", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        novoAnexoSelecionado();
    }

    @URLAction(mappingId = "novoControleLicenciamentoAmbiental", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataHora(new Date());
        novoAnexoSelecionado();
    }

    public AnexoControleLicenciamentoAmbiental getAnexoSelecionado() {
        return anexoSelecionado;
    }

    public void setAnexoSelecionado(AnexoControleLicenciamentoAmbiental anexoSelecionado) {
        this.anexoSelecionado = anexoSelecionado;
    }

    public void recuperarTecnico() {
        if (selecionado.getTecnico() != null) {
            selecionado.setTecnico(facade.getTecnicoLicenciamentoAmbientalFacade()
                .recuperar(selecionado.getTecnico().getId()));
            this.processos = facade.buscarProcessosDoTecnico(selecionado.getTecnico());
        }
    }

    @Override
    public void depoisDeSalvar() {
        ProcessoLicenciamentoAmbiental proc = facade.getProcessoLicenciamentoAmbientalFacade().recuperar(selecionado.getProcesso().getId());
        try {
            facade.getProcessoLicenciamentoAmbientalFacade().gerarDebitoParecer(proc, facade.getSistemaFacade().getUsuarioCorrente());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            logger.error("Erro ao gerar débito do processo " + proc.getSequencial() + " com o assunto " + proc.getAssuntoLicenciamentoAmbiental().getDescricao() + ".", e);
        }
    }

    public List<SelectItem> getListaStatus() {
        return Util.getListSelectItem(StatusLicenciamentoAmbiental.values());
    }

    private void validarAnexo() {
        ValidacaoException ve = new ValidacaoException();
        if (anexoSelecionado.getArquivo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe um arquivo.");
        }
        for (AnexoControleLicenciamentoAmbiental anexo : selecionado.getAnexos()) {
            if (anexo.getArquivo().getNome().equals(anexoSelecionado.getArquivo().getNome())) {
                ve.adicionarMensagemDeCampoObrigatorio("Arquivo " + anexo.getArquivo().getNome() + " já foi adicionado.");
                break;
            }
        }
        ve.lancarException();
    }

    public void uploadAnexo(FileUploadEvent event) {
        try {
            Arquivo arquivo = facade.getArquivoFacade().criarArquivo(event.getFile());
            anexoSelecionado.setArquivo(arquivo);
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void adicionarAnexo() {
        try {
            validarAnexo();
            Util.adicionarObjetoEmLista(selecionado.getAnexos(), anexoSelecionado);
            novoAnexoSelecionado();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void removerAnexo(ActionEvent evento) {
        AnexoControleLicenciamentoAmbiental anexo = (AnexoControleLicenciamentoAmbiental) evento.getComponent().getAttributes().get("vAnexo");
        selecionado.getAnexos().remove(anexo);
    }

    public StreamedContent baixarArquivo(Arquivo arq) {
        if (arq == null) return null;
        return facade.getArquivoFacade().montarArquivoParaDownloadPorArquivo(arq);
    }

    public void editarAnexo(AnexoControleLicenciamentoAmbiental anexo) {
        anexoSelecionado = (AnexoControleLicenciamentoAmbiental) Util.clonarObjeto(anexo);
    }

    public void novoAnexoSelecionado() {
        AnexoControleLicenciamentoAmbiental novoAnexo = new AnexoControleLicenciamentoAmbiental();
        novoAnexo.setControleLicenciamentoAmbiental(selecionado);
        if (selecionado.getProcesso() != null) {
            novoAnexo.setMostrarNoPortalContribuinte(selecionado.getProcesso().getAssuntoLicenciamentoAmbiental().getMostrarAnexosPortal());
        }
        setAnexoSelecionado(novoAnexo);
    }
}
