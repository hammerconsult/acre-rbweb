package br.com.webpublico.controle;

import br.com.webpublico.negocios.AssincronoFacade;
import br.com.webpublico.util.FacesUtil;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@ManagedBean
@SessionScoped
public class AssincronoControle implements Serializable {
    @EJB
    private AssincronoFacade assincronoFacade;
    private Future<String> future;
    private Integer quantidade;

    public void executa() {
        future = assincronoFacade.processar(quantidade);
    }

    public Future<String> getFuture() {
        return future;
    }

    public boolean getExecutando() {
        return future != null && future.isDone();
    }

    public void cancela() {
        future.cancel(true);
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}
