package br.com.webpublico.controle;

import br.com.webpublico.negocios.CidadeFacade;
import br.com.webpublico.util.FacesUtil;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class PesquisaCidadeControlador extends ComponentePesquisaGenerico {

    @EJB
    private CidadeFacade cidadeFacade;

    public void uploadArquivo(FileUploadEvent event) {
        try {
            cidadeFacade.importarPlanilhaCidadesIBGE(event.getFile().getInputstream());
            FacesUtil.addOperacaoRealizada("Arquivo importado com sucesso!");
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Falha ao importar a planilha de cidades. " + ex.getMessage());
            logger.error("Falha ao importar a planilha de cidades. ", ex);
        }
    }
}
