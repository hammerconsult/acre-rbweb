/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle.tributario;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.ArquivoParte;
import br.com.webpublico.entidades.tributario.ParametroLicenciamentoAmbiental;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.tributario.ParametroLicenciamentoAmbientalFacade;
import br.com.webpublico.util.FacesUtil;
import com.google.common.base.Strings;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.*;

/**
 * @author Pedro
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listarParametroLicenciamentoAmbiental", pattern = "/tributario/licenciamento-ambiental/parametro/listar/",
        viewId = "/faces/tributario/licenciamentoambiental/parametro/lista.xhtml"),
    @URLMapping(id = "editarParametroLicenciamentoAmbiental", pattern = "/tributario/licenciamento-ambiental/parametro/editar/#{parametroLicenciamentoAmbientalControlador.id}/",
        viewId = "/faces/tributario/licenciamentoambiental/parametro/edita.xhtml"),
    @URLMapping(id = "verParametroLicenciamentoAmbiental", pattern = "/tributario/licenciamento-ambiental/parametro/ver/#{parametroLicenciamentoAmbientalControlador.id}/",
        viewId = "/faces/tributario/licenciamentoambiental/parametro/visualizar.xhtml"),
    @URLMapping(id = "novoParametroLicenciamentoAmbiental", pattern = "/tributario/licenciamento-ambiental/parametro/novo/",
        viewId = "/faces/tributario/licenciamentoambiental/parametro/edita.xhtml")
})
public class ParametroLicenciamentoAmbientalControlador extends PrettyControlador<ParametroLicenciamentoAmbiental> implements Serializable, CRUD {

    @EJB
    private ParametroLicenciamentoAmbientalFacade facade;

    public ParametroLicenciamentoAmbientalControlador() {
        super(ParametroLicenciamentoAmbiental.class);
    }

    @Override
    public ParametroLicenciamentoAmbientalFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/licenciamento-ambiental/parametro/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoParametroLicenciamentoAmbiental", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editarParametroLicenciamentoAmbiental", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        carregarFotoSecretario();
        carregarFotoSecretarioAdj();
        carregarFotoDiretor();
    }

    @URLAction(mappingId = "verParametroLicenciamentoAmbiental", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
        carregarFotoSecretario();
        carregarFotoSecretarioAdj();
        carregarFotoDiretor();
    }

    public void uploadFotoSecretario(FileUploadEvent event) {
        try {
            Arquivo arquivo = facade.getArquivoFacade().criarArquivo(event.getFile());
            selecionado.getSecretario().setArquivo(arquivo);
            carregarFotoSecretario();
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void uploadFotoSecretarioAdj(FileUploadEvent event) {
        try {
            Arquivo arquivo = facade.getArquivoFacade().criarArquivo(event.getFile());
            selecionado.getSecretarioAdjunto().setArquivo(arquivo);
            carregarFotoSecretarioAdj();
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void uploadFotoDiretor(FileUploadEvent event) {
        try {
            Arquivo arquivo = facade.getArquivoFacade().criarArquivo(event.getFile());
            selecionado.getDiretor().setArquivo(arquivo);
            carregarFotoDiretor();
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public void trocarFotoSecretario() {
        selecionado.getSecretario().setArquivo(null);
    }

    public void trocarFotoSecretarioAdj() {
        selecionado.getSecretarioAdjunto().setArquivo(null);
    }

    public void trocarFotoDiretor() {
        selecionado.getDiretor().setArquivo(null);
    }

    private void carregarFotoSecretario() {
        if (selecionado.getSecretario().getArquivo() == null) return;
        try {
            colocarArquivoNoMapDaSessao(selecionado.getSecretario().getArquivo(), "imagem-secretario-licenciamento-ambiental");
        } catch (Exception ex) {
            logger.error("Erro: ", ex);
        }
    }

    private void carregarFotoSecretarioAdj() {
        if (selecionado.getSecretarioAdjunto().getArquivo() == null) return;
        try {
            colocarArquivoNoMapDaSessao(selecionado.getSecretarioAdjunto().getArquivo(), "imagem-secretario-adj-licenciamento-ambiental");
        } catch (Exception ex) {
            logger.error("Erro: ", ex);
        }
    }

    private void carregarFotoDiretor() {
        if (selecionado.getDiretor().getArquivo() == null) return;
        try {
            colocarArquivoNoMapDaSessao(selecionado.getDiretor().getArquivo(), "imagem-diretor-licenciamento-ambiental");
        } catch (Exception ex) {
            logger.error("Erro: ", ex);
        }
    }

    private void colocarArquivoNoMapDaSessao(Arquivo arq, String keyMap) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        if (arq.getId() != null) arq = facade.getArquivoFacade().recuperaDependencias(arq.getId());
        for (ArquivoParte a : arq.getPartes()) {
            buffer.write(a.getDados());
        }
        InputStream inputStream = new ByteArrayInputStream(buffer.toByteArray());
        DefaultStreamedContent imagemFoto = new DefaultStreamedContent(inputStream, arq.getMimeType(), arq.getNome());
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(keyMap, imagemFoto);
    }

    @Override
    public boolean validaRegrasEspecificas() {
        if (selecionado.getExercicio() != null && facade.existeParametroComMesmoExercicio(selecionado.getId(), selecionado.getExercicio().getAno())) {
            FacesUtil.addOperacaoNaoPermitida("Ja existe um parâmetro com o exercício de " + selecionado.getExercicio().getAno());
            return false;
        }
        return true;
    }

    private void validarCamposSecretarios() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getSecretario().getSecretario() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Secretário.");
        }
        if (selecionado.getSecretario().getArquivo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Assinatura Digital do Secretário.");
        }
        if (Strings.isNullOrEmpty(selecionado.getSecretario().getDecretoNomeacao())) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Decreto de Nomeação do Secretário.");
        }
        if (selecionado.getDiretor().getSecretario() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Diretor.");
        }
        if (selecionado.getDiretor().getArquivo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Assinatura Digital do Diretor.");
        }
        if (Strings.isNullOrEmpty(selecionado.getDiretor().getDecretoNomeacao())) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Decreto de Nomeação do Diretor.");
        }
        ve.lancarException();
    }

    @Override
    public void salvar() {
        try {
            validarCamposSecretarios();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }
}
