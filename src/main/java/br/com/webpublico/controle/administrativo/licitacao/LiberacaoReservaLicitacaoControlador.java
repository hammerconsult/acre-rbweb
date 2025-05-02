package br.com.webpublico.controle.administrativo.licitacao;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.Licitacao;
import br.com.webpublico.entidades.administrativo.licitacao.LiberacaoReservaLicitacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.administrativo.licitacao.LiberacaoReservaLicitacaoFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Date;
import java.util.List;

/**
 * Created by wellington on 24/10/17.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-liberacao-reserva-licitacao", pattern = "/licitacao/liberacao-reserva/novo/",
        viewId = "/faces/administrativo/licitacao/liberacaoreserva/edita.xhtml"),
    @URLMapping(id = "ver-liberacao-reserva-licitacao", pattern = "/licitacao/liberacao-reserva/ver/#{liberacaoReservaLicitacaoControlador.id}/",
        viewId = "/faces/administrativo/licitacao/liberacaoreserva/visualizar.xhtml"),
    @URLMapping(id = "listar-liberacao-reserva-licitacao", pattern = "/licitacao/liberacao-reserva/listar/",
        viewId = "/faces/administrativo/licitacao/liberacaoreserva/lista.xhtml")
})
public class LiberacaoReservaLicitacaoControlador extends PrettyControlador<LiberacaoReservaLicitacao> implements CRUD {

    @EJB
    private LiberacaoReservaLicitacaoFacade liberacaoReservaLicitacaoFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public LiberacaoReservaLicitacaoControlador() {
        super(LiberacaoReservaLicitacao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return liberacaoReservaLicitacaoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/licitacao/liberacao-reserva/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "nova-liberacao-reserva-licitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setLiberadoEm(new Date());
        selecionado.setResponsavel(sistemaFacade.getUsuarioCorrente());
    }

    @URLAction(mappingId = "ver-liberacao-reserva-licitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        try {
            Util.validarCampos(selecionado);
            if (isOperacaoNovo()) {
                liberacaoReservaLicitacaoFacade.salvarNovo(selecionado);
            } else {
                liberacaoReservaLicitacaoFacade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();

        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    public List<Licitacao> completarLicitacao(String parte) {
        return liberacaoReservaLicitacaoFacade.buscarLicitacoesParaLiberacao(parte);
    }

    public void popularFontesDespesaOrcLiberacaoSaldo() {
        if (selecionado.getLicitacao() != null) {
            liberacaoReservaLicitacaoFacade.popularFontesDespesaOrcLiberacaoSaldo(selecionado);
        }
    }
}
