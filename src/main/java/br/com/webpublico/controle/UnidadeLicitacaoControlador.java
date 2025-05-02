package br.com.webpublico.controle;

import br.com.webpublico.entidades.Licitacao;
import br.com.webpublico.entidades.UnidadeLicitacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.LicitacaoFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "unidadeLicitacaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-unidade-licitacao", pattern = "/unidade-licitacao/", viewId = "/faces/administrativo/licitacao/unidade-licitacao/edita.xhtml")
})
public class UnidadeLicitacaoControlador implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(UnidadeLicitacaoControlador.class);
    @EJB
    private LicitacaoFacade facade;
    private Licitacao licitacao;
    private UnidadeLicitacao unidadeLicitacao;

    @URLAction(mappingId = "nova-unidade-licitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {

    }

    public List<Licitacao> completarLicitacao(String parte) {
        return facade.buscarLicitacaoPorNumeroModalidadeOrNumeroLicitacao(parte.trim());
    }

    public void recuperarLicitacao() {
        licitacao = facade.recuperarSomenteUnidades(licitacao.getId());
    }

    public void novaUnidade() {
        try {
            validarSalvar();
            unidadeLicitacao = new UnidadeLicitacao();
            unidadeLicitacao.setLicitacao(licitacao);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public String styleLicitacao() {
        if (unidadeLicitacao != null) {
            return "margin-left: 87px";
        }
        if (licitacao == null) {
            return "";
        }
        return "";
    }

    public void cancelarUnidade() {
        unidadeLicitacao = null;
    }

    public void removerUnidade(UnidadeLicitacao obj) {
        licitacao.getUnidades().remove(obj);
    }

    public void editarUnidade(UnidadeLicitacao obj) {
        unidadeLicitacao = obj;
    }

    public void adicionarUnidade() {
        try {
            validarAdicionarUnidade();
            Util.adicionarObjetoEmLista(licitacao.getUnidades(), unidadeLicitacao);
            cancelarUnidade();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarAdicionarUnidade() {
        ValidacaoException ve = new ValidacaoException();
        Util.validarCampos(unidadeLicitacao);
        for (UnidadeLicitacao unidadeAdicionada : licitacao.getUnidades()) {
            if (!unidadeAdicionada.equals(unidadeLicitacao)) {
                unidadeAdicionada.validarVigencia(unidadeLicitacao);
            }
        }
        ve.lancarException();
    }

    public void salvarUnidade() {
        try {
            validarSalvar();
            Licitacao licitacaoSalva = facade.salvarRetornando(this.licitacao);
            FacesUtil.redirecionamentoInterno("/licitacao/ver/" + licitacaoSalva.getId() + "/");
            FacesUtil.addOperacaoRealizada("Troca de unidade realizada com sucesso!");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
            FacesUtil.executaJavaScript("aguarde.hide()");
        } catch (Exception e) {
            FacesUtil.executaJavaScript("aguarde.hide()");
            logger.error("Salvar troca de unidade da licitação {}", e);
        }
    }

    private void validarSalvar() {
        ValidacaoException ve = new ValidacaoException();
        if (licitacao == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo licitação deve ser informado.");
        }
        if (unidadeLicitacao != null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Finalize as alterações na vigência da unidade para continuar.");
        }
        ve.lancarException();
    }

    public void cancelarProcesso() {
        FacesUtil.redirecionamentoInterno("/licitacao/listar/");
    }

    public void limparDadosLicitacao() {
        setLicitacao(null);
        setUnidadeLicitacao(null);
    }

    public Licitacao getLicitacao() {
        return licitacao;
    }

    public void setLicitacao(Licitacao licitacao) {
        this.licitacao = licitacao;
    }

    public UnidadeLicitacao getUnidadeLicitacao() {
        return unidadeLicitacao;
    }

    public void setUnidadeLicitacao(UnidadeLicitacao unidadeLicitacao) {
        this.unidadeLicitacao = unidadeLicitacao;
    }
}
