package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroHistoricoProcessoLicitatorio;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.TipoMovimentoProcessoLicitatorio;
import br.com.webpublico.enums.TipoParecerLicitacao;
import br.com.webpublico.exception.StatusLicitacaoException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ParecerLicitacaoFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.TabChangeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: JULIO CESAR-MGA
 * Date: 19/03/14
 * Time: 14:40
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "parecerLicitacaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoParecerLicitacao", pattern = "/parecer-licitacao/novo/", viewId = "/faces/administrativo/licitacao/parecerlicitacao/edita.xhtml"),
    @URLMapping(id = "editarParecerLicitacao", pattern = "/parecer-licitacao/editar/#{parecerLicitacaoControlador.id}/", viewId = "/faces/administrativo/licitacao/parecerlicitacao/edita.xhtml"),
    @URLMapping(id = "listarParecerLicitacao", pattern = "/parecer-licitacao/listar/", viewId = "/faces/administrativo/licitacao/parecerlicitacao/lista.xhtml"),
    @URLMapping(id = "verParecerLicitacao", pattern = "/parecer-licitacao/ver/#{parecerLicitacaoControlador.id}/", viewId = "/faces/administrativo/licitacao/parecerlicitacao/visualizar.xhtml")
})
public class ParecerLicitacaoControlador extends PrettyControlador<ParecerLicitacao> implements Serializable, CRUD {

    protected static final Logger logger = LoggerFactory.getLogger(ParecerLicitacaoControlador.class);

    @EJB
    private ParecerLicitacaoFacade parecerLicitacaoFacade;

    private FiltroHistoricoProcessoLicitatorio filtroHistoricoProcesso;

    public ParecerLicitacaoControlador() {
        super(ParecerLicitacao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return parecerLicitacaoFacade;
    }

    @URLAction(mappingId = "novoParecerLicitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verParecerLicitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
    }

    @URLAction(mappingId = "editarParecerLicitacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        try {
            parecerLicitacaoFacade.getLicitacaoFacade().verificarStatusLicitacao(selecionado.getLicitacao());
        } catch (StatusLicitacaoException se) {
            FacesUtil.printAllFacesMessages(se.getMensagens());
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/parecer-licitacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void salvar() {
        try {
            if (!Util.validaCampos(selecionado) || !validarDataParecer()) {
                return;
            }
            parecerLicitacaoFacade.getConfiguracaoLicitacaoFacade().validarAnexoObrigatorio(selecionado.getDetentorDocumentoLicitacao(), selecionado.getTipoAnexo());
            parecerLicitacaoFacade.getLicitacaoFacade().verificarStatusLicitacao(selecionado.getLicitacao());
            selecionado.setNumero(parecerLicitacaoFacade.recuperaUltimoNumero());
            selecionado = parecerLicitacaoFacade.salvarRetornando(selecionado);
            if (selecionado.getId() != null) {
                FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
                FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
            } else {
                super.salvar();
            }
        } catch (StatusLicitacaoException se) {
            FacesUtil.printAllFacesMessages(se.getMensagens());
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        }
    }

    public boolean validarDataParecer() {
        SolicitacaoMaterial solicitacao = parecerLicitacaoFacade.getLicitacaoFacade().getProcessoDeCompraFacade().recuperarSolicitacaoMaterial(selecionado.getLicitacao().getProcessoDeCompra());
        boolean retorno = true;
        selecionado.setLicitacao(parecerLicitacaoFacade.getLicitacaoFacade().recuperar(selecionado.getLicitacao().getId()));
        if (TipoParecerLicitacao.JURIDICO_EDITAL.equals(selecionado.getTipoParecerLicitacao())) {
            SolicitacaoMaterial solicitacaoMaterial = parecerLicitacaoFacade.getLicitacaoFacade().getProcessoDeCompraFacade().recuperarSolicitacaoMaterial(selecionado.getLicitacao().getProcessoDeCompra());
            if (!solicitacao.isExercicioProcessoDiferenteExercicioAtual(solicitacao.getDataSolicitacao()) && selecionado.getDataParecer().compareTo(solicitacaoMaterial.getCotacao().getDataCotacao()) < 0) {
                FacesUtil.addOperacaoNaoPermitida("A data do parecer não pode ser anterior a data de validade da cotação (" + DataUtil.getDataFormatada(solicitacaoMaterial.getCotacao().getDataCotacao()) + ")");
                retorno = false;
            }

            if (selecionado.getLicitacao().getPublicacoes() != null) {
                for (PublicacaoLicitacao publicacaoLicitacao : selecionado.getLicitacao().getPublicacoes()) {
                    if (publicacaoLicitacao.isAviso() && selecionado.getDataParecer().compareTo(publicacaoLicitacao.getDataPublicacao()) > 0) {
                        FacesUtil.addOperacaoNaoPermitida("A Data do Parecer não pode ser superior a Data de Publicação do Edital.");
                        retorno = false;
                    }

                    if (publicacaoLicitacao.isAviso() && selecionado.getDataParecer().compareTo(solicitacao.getCotacao().getDataCotacao()) < 0) {
                        FacesUtil.addOperacaoNaoPermitida("A Data do Parecer não pode ser inferior a Data da Cotação.");
                        retorno = false;
                    }
                }
            }
        }

        if (TipoParecerLicitacao.JURIDICO_JULGAMENTO.equals(selecionado.getTipoParecerLicitacao())) {
            if (selecionado.getLicitacao().getPublicacoes().isEmpty()) {
                FacesUtil.addOperacaoNaoPermitida("Não existe nenhuma publicação para a licitação.");
                retorno = false;
            }
        }
        return retorno;
    }


    public boolean isLicitacaoModalidadeDispensa() {
        return selecionado.getLicitacao().getModalidadeLicitacao() != null && selecionado.getLicitacao().getModalidadeLicitacao().isDispensaLicitacao();
    }

    public boolean isLicitacaoModalidadeInexigibilidade() {
        return selecionado.getLicitacao().getModalidadeLicitacao() != null && selecionado.getLicitacao().getModalidadeLicitacao().isInexigibilidade();
    }

    public List<SelectItem> getTipoParecerLicitacao() {
        if (selecionado.getLicitacao() == null) {
            List<SelectItem> toReturn = new ArrayList<SelectItem>();
            toReturn.add(new SelectItem(null, "Selecione a Licitação"));
            return toReturn;
        } else {
            List<SelectItem> toReturn = new ArrayList<SelectItem>();
            toReturn.add(new SelectItem(null, ""));
            if (isLicitacaoModalidadeDispensa()) {
                toReturn.add(new SelectItem(TipoParecerLicitacao.JURIDICO_DISPENSA, TipoParecerLicitacao.JURIDICO_DISPENSA.getDescricao()));
                toReturn.add(new SelectItem(TipoParecerLicitacao.JURIDICO_OUTRO, TipoParecerLicitacao.JURIDICO_OUTRO.getDescricao()));
            }

            if (isLicitacaoModalidadeInexigibilidade()) {
                toReturn.add(new SelectItem(TipoParecerLicitacao.JURIDICO_INEXIGIBILIDADE, TipoParecerLicitacao.JURIDICO_INEXIGIBILIDADE.getDescricao()));
                toReturn.add(new SelectItem(TipoParecerLicitacao.JURIDICO_OUTRO, TipoParecerLicitacao.JURIDICO_OUTRO.getDescricao()));
            }

            if (!isLicitacaoModalidadeDispensa() && !isLicitacaoModalidadeInexigibilidade()) {
                for (TipoParecerLicitacao object : TipoParecerLicitacao.values()) {
                    if (!object.equals(TipoParecerLicitacao.JURIDICO_DISPENSA) && !object.equals(TipoParecerLicitacao.JURIDICO_INEXIGIBILIDADE)) {
                        toReturn.add(new SelectItem(object, object.getDescricao()));
                    }
                }
            }
            return toReturn;
        }
    }

    public void carregarListas() {
        selecionado.getLicitacao().setPareceres(parecerLicitacaoFacade.getLicitacaoFacade().recuperarListaDeParecer(selecionado.getLicitacao()));
        FacesUtil.atualizarComponente("Formulario");
    }

    private void novoFiltroHistoricoProcesso() {
        filtroHistoricoProcesso = new FiltroHistoricoProcessoLicitatorio(selecionado.getId(), TipoMovimentoProcessoLicitatorio.PARECER_LICITACAO);
    }

    public void onTabChange(TabChangeEvent event) {
        String tab = event.getTab().getId();
        if ("tab-historico".equals(tab)) {
            novoFiltroHistoricoProcesso();
        }
    }

    public FiltroHistoricoProcessoLicitatorio getFiltroHistoricoProcesso() {
        return filtroHistoricoProcesso;
    }

    public void setFiltroHistoricoProcesso(FiltroHistoricoProcessoLicitatorio filtroHistoricoProcesso) {
        this.filtroHistoricoProcesso = filtroHistoricoProcesso;
    }
}
