package br.com.webpublico.controle;

import br.com.webpublico.entidades.ArquivoCEDO;
import br.com.webpublico.entidades.DetalheArquivoCEDO;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ArquivoCEDOFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.AsyncExecutor;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoArquivoCEDO",
        pattern = "/tributario/iptu/arquivo-cedo/novo/",
        viewId = "/faces/tributario/iptu/arquivocedo/edita.xhtml"),
    @URLMapping(id = "verArquivoCEDO",
        pattern = "/tributario/iptu/arquivo-cedo/ver/#{arquivoCEDOControlador.id}/",
        viewId = "/faces/tributario/iptu/arquivocedo/visualizar.xhtml"),
    @URLMapping(id = "listarArquivoCEDO",
        pattern = "/tributario/iptu/arquivo-cedo/listar/",
        viewId = "/faces/tributario/iptu/arquivocedo/lista.xhtml")
})
public class ArquivoCEDOControlador extends PrettyControlador<ArquivoCEDO> implements Serializable, CRUD {

    @EJB
    private ArquivoCEDOFacade facade;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private CompletableFuture<ArquivoCEDO> future;
    private LazyDataModel<DetalheArquivoCEDO> detalhesDataModel;

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public ArquivoCEDOControlador() {
        super(ArquivoCEDO.class);
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/iptu/arquivo-cedo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public LazyDataModel<DetalheArquivoCEDO> getDetalhesDataModel() {
        return detalhesDataModel;
    }

    public void setDetalhesDataModel(LazyDataModel<DetalheArquivoCEDO> detalhesDataModel) {
        this.detalhesDataModel = detalhesDataModel;
    }

    @URLAction(mappingId = "novoArquivoCEDO", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataImportacao(new Date());
        selecionado.setUsuarioImportacao(facade.getSistemaFacade().getUsuarioCorrente());
    }

    @URLAction(mappingId = "verArquivoCEDO", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        criarDetalhesDataModel();
    }

    private void criarDetalhesDataModel() {
        detalhesDataModel = new LazyDataModel<DetalheArquivoCEDO>() {
            @Override
            public List<DetalheArquivoCEDO> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
                setRowCount(facade.contarDetalhes(selecionado).intValue());
                return facade.buscarDetalhes(selecionado, first, pageSize);
            }
        };
    }

    @Override
    public void salvar() {
        try {
            selecionado.realizarValidacoes();
            selecionado = facade.salvarRetornando(selecionado);
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
            FacesUtil.addOperacaoRealizada("Arquivo CEDO salvo com sucesso!");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void processar() {
        assistenteBarraProgresso = new AssistenteBarraProgresso(facade.getSistemaFacade().getUsuarioCorrente(),
            "Importando arquivo CEDO", 0);
        future = AsyncExecutor.getInstance().execute(assistenteBarraProgresso,
            ()-> facade.processarArquivo(assistenteBarraProgresso, selecionado));
        FacesUtil.executaJavaScript("iniciarImportacaoArquivo()");
    }

    public void acompanharImportacaoArquivo() {
        if (future != null) {
            if (future.isDone()) {
                FacesUtil.executaJavaScript("finalizarImportacaoArquivo()");
            } else if (future.isCancelled()) {
                FacesUtil.executaJavaScript("abortarImportacaoArquivo()");
                FacesUtil.addOperacaoNaoRealizada("Erro inesperado ao importar o arquivo CEDO.");
            }
        }
    }

    public void finalizarImportacaoArquivo() throws ExecutionException, InterruptedException {
        ArquivoCEDO arquivoCEDO = future.get();
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + arquivoCEDO.getId());
        FacesUtil.addOperacaoRealizada("Arquivo CEDO processado com sucesso!");
    }
}
