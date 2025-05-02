package br.com.webpublico.controle;

import br.com.webpublico.entidades.ObjetoCompra;
import br.com.webpublico.entidades.ObjetoDeCompraEspecificacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ObjetoCompraFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean
@ViewScoped
public class TabelaEspecificacaoObjetoCompraControlador implements Serializable {

    @EJB
    private ObjetoCompraFacade objetoCompraFacade;
    private ObjetoCompra objetoCompra;
    private ObjetoDeCompraEspecificacao objetoDeCompraEspecificacao;

    public void recuperarObjetoCompra(ObjetoCompra objetoCompra) {
        if (objetoCompra != null) {
            this.objetoCompra = objetoCompraFacade.recuperar(objetoCompra.getId());
        }
        cancelarEspecificacao();
        FacesUtil.executaJavaScript("$('#modalTabelaEspecificacao').modal('show')");
        FacesUtil.atualizarComponente("formTabelaEspecificacao");
    }

    public void novaEspecificacao() {
        objetoDeCompraEspecificacao = new ObjetoDeCompraEspecificacao(true, objetoCompra);
    }

    public void removerEspecificacao(ObjetoDeCompraEspecificacao esp) {
        objetoCompra.getEspecificacaoes().remove(esp);
        objetoCompra = objetoCompraFacade.salvarRetornando(objetoCompra);
    }

    public void adicionarEspecificacao() {
        try {
            validarEspecificacao(objetoDeCompraEspecificacao);
            objetoDeCompraEspecificacao = objetoCompraFacade.salvarEspecificacao(objetoDeCompraEspecificacao);
            Util.adicionarObjetoEmLista(objetoCompra.getEspecificacaoes(), objetoDeCompraEspecificacao);
            cancelarEspecificacao();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void cancelarEspecificacao() {
        objetoDeCompraEspecificacao = null;
    }

    private void validarEspecificacao(ObjetoDeCompraEspecificacao esp) {
        ValidacaoException ve = new ValidacaoException();
        if (Util.isStringNulaOuVazia(esp.getTexto())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo texto da especificação deve ser informado.");
        } else if (objetoCompra.getEspecificacaoes().stream()
            .anyMatch(espList -> !espList.equals(esp) && Util.isStringIgual(espList.getTexto(), esp.getTexto()))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O texto informado já está cadastrado para outra especificação deste objeto de compra.");
        }
        ve.lancarException();
    }

    public void editarEspecificacao(ObjetoDeCompraEspecificacao esp) {
        esp.setSelecionada(true);
    }

    public void confirmarEdicaoEspecificacao(ObjetoDeCompraEspecificacao esp) {
        objetoCompraFacade.salvarEspecificacao(esp);
        esp.setSelecionada(false);
    }

    public ObjetoCompra getObjetoCompra() {
        return objetoCompra;
    }

    public void setObjetoCompra(ObjetoCompra objetoCompra) {
        this.objetoCompra = objetoCompra;
    }

    public ObjetoDeCompraEspecificacao getObjetoDeCompraEspecificacao() {
        return objetoDeCompraEspecificacao;
    }

    public void setObjetoDeCompraEspecificacao(ObjetoDeCompraEspecificacao objetoDeCompraEspecificacao) {
        this.objetoDeCompraEspecificacao = objetoDeCompraEspecificacao;
    }
}
