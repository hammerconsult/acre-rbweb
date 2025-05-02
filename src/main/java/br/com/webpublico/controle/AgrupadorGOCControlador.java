package br.com.webpublico.controle;

import br.com.webpublico.entidades.AgrupadorGOC;
import br.com.webpublico.entidades.AgrupadorGOCGrupo;
import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.GrupoObjetoCompra;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AgrupadorGOCFacade;
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
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-agrupador-goc", pattern = "/agrupador-goc/novo/", viewId = "/faces/administrativo/licitacao/agrupador-goc/edita.xhtml"),
    @URLMapping(id = "editar-agrupador-goc", pattern = "/agrupador-goc/editar/#{agrupadorGOCControlador.id}/", viewId = "/faces/administrativo/licitacao/agrupador-goc/edita.xhtml"),
    @URLMapping(id = "ver-agrupador-goc", pattern = "/agrupador-goc/ver/#{agrupadorGOCControlador.id}/", viewId = "/faces/administrativo/licitacao/agrupador-goc/visualizar.xhtml"),
    @URLMapping(id = "listar-agrupador-goc", pattern = "/agrupador-goc/listar/", viewId = "/faces/administrativo/licitacao/agrupador-goc/lista.xhtml")
})
public class AgrupadorGOCControlador extends PrettyControlador<AgrupadorGOC> implements Serializable, CRUD {

    @EJB
    private AgrupadorGOCFacade facade;
    private AgrupadorGOCGrupo grupo;
    private String palavra;
    private FileUploadEvent fileUploadEvent;
    private List<GrupoObjetoCompra> gruposObjetoCompraDisponiveis;

    public AgrupadorGOCControlador() {
        super(AgrupadorGOC.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "novo-agrupador-goc", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        fileUploadEvent = null;
        setPalavra(null);
        novoGOC();
    }

    @URLAction(mappingId = "ver-agrupador-goc", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-agrupador-goc", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        fileUploadEvent = null;
        setPalavra(null);
        carregarImagemGrupo();
        novoGOC();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/agrupador-goc/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void salvar() {
        try {
            validarRegrasSalvar();
            facade.salvar(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public void buscarGrupoObjetoCompraSemAgrupador() {
        gruposObjetoCompraDisponiveis = facade.getGrupoObjetoCompraFacade().buscarGrupoObjetoCompraSemAgrupador(palavra);
        FacesUtil.atualizarComponente("Formulario:goc-nao-agrupados");
    }

    private void validarRegrasSalvar() {
        Util.validarCampos(selecionado);
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getGrupos().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Para continuar é necessário adicionar ao menos um grupo objeto de compra.");
        }
        ve.lancarException();
    }

    public List<GrupoObjetoCompra> completarGrupoObjetoCompra(String parte) {
        return facade.getGrupoObjetoCompraFacade().buscarGrupoObjetoCompraPorCodigoOrDescricaoNaoAgrupado(parte.trim());
    }

    public void novoGOC() {
        grupo = new AgrupadorGOCGrupo();
        FacesUtil.executaJavaScript("setaFoco('Formulario:ac-goc_input')");
    }

    public boolean isGOCUtilizado(GrupoObjetoCompra grupo) {
        for (AgrupadorGOCGrupo agrupador : selecionado.getGrupos()) {
            if (agrupador.getGrupoObjetoCompra().equals(grupo)) {
                return true;
            }
        }
        return false;
    }

    public void adicionarGOCDisponivel(GrupoObjetoCompra grupoObjetoCompra) {
        grupo.setGrupoObjetoCompra(grupoObjetoCompra);
        adicionarGOC();
        if (!Strings.isNullOrEmpty(getPalavra())) {
            setPalavra(null);
            buscarGrupoObjetoCompraSemAgrupador();
        }
    }

    public void adicionarGOC() {
        try {
            validarGOC();
            grupo.setAgrupadorGOC(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getGrupos(), grupo);
            novoGOC();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removerGrupo(AgrupadorGOCGrupo grupo) {
        selecionado.getGrupos().remove(grupo);
    }

    public void validarGOC() {
        Util.validarCampos(grupo);
        ValidacaoException ve = new ValidacaoException();
        for (AgrupadorGOCGrupo grupo : selecionado.getGrupos()) {
            if (!grupo.equals(this.grupo) && grupo.getGrupoObjetoCompra().equals(this.grupo.getGrupoObjetoCompra())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O grupo " + grupo.getGrupoObjetoCompra() + " já foi adicionado na lista.");
            }
        }
        ve.lancarException();
    }

    public boolean hasImagemGrupo() {
        try {
            return selecionado.getArquivo() != null;
        } catch (Exception e) {
            return true;
        }
    }

    public void uploadArquivo(FileUploadEvent event) throws Exception {
        try {
            fileUploadEvent = event;
            novoArquivo(event);
            DefaultStreamedContent imagem = new DefaultStreamedContent(fileUploadEvent.getFile().getInputstream(), "image/png", fileUploadEvent.getFile().getFileName());
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("imagem-grupo", imagem);
        } catch (IOException ex) {
            logger.error("Erro: ", ex);
        }
    }

    private void novoArquivo(FileUploadEvent event) throws Exception {
        Arquivo arquivo = new Arquivo();
        arquivo.setMimeType(fileUploadEvent.getFile().getContentType());
        arquivo.setNome(fileUploadEvent.getFile().getFileName());
        arquivo.setTamanho(fileUploadEvent.getFile().getSize());
        arquivo.setDescricao(fileUploadEvent.getFile().getFileName());
        arquivo = facade.getArquivoFacade().novoArquivoMemoria(arquivo, event.getFile().getInputstream());
        selecionado.setArquivo(arquivo);
    }

    private void carregarImagemGrupo() {
        DefaultStreamedContent imagemGrupo = facade.carregarImagemGrupo(selecionado);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("imagem-grupo", imagemGrupo);
    }

    public AgrupadorGOCGrupo getGrupo() {
        return grupo;
    }

    public void setGrupo(AgrupadorGOCGrupo grupo) {
        this.grupo = grupo;
    }

    public FileUploadEvent getFileUploadEvent() {
        return fileUploadEvent;
    }

    public void setFileUploadEvent(FileUploadEvent fileUploadEvent) {
        this.fileUploadEvent = fileUploadEvent;
    }

    public List<GrupoObjetoCompra> getGruposObjetoCompraDisponiveis() {
        return gruposObjetoCompraDisponiveis;
    }

    public void setGruposObjetoCompraDisponiveis(List<GrupoObjetoCompra> gruposObjetoCompraDisponiveis) {
        this.gruposObjetoCompraDisponiveis = gruposObjetoCompraDisponiveis;
    }

    public String getPalavra() {
        return palavra;
    }

    public void setPalavra(String palavra) {
        this.palavra = palavra;
    }
}
