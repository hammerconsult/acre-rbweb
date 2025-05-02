package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoGrupo;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.enums.TipoOperacaoBensImoveis;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.BensImoveisFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Fabio
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-bens-imoveis", pattern = "/contabil/bens-imoveis/novo/", viewId = "/faces/financeiro/orcamentario/bens/bensimoveis/edita.xhtml"),
    @URLMapping(id = "editar-bens-imoveis", pattern = "/contabil/bens-imoveis/editar/#{bensImoveisControlador.id}/", viewId = "/faces/financeiro/orcamentario/bens/bensimoveis/edita.xhtml"),
    @URLMapping(id = "ver-bens-imoveis", pattern = "/contabil/bens-imoveis/ver/#{bensImoveisControlador.id}/", viewId = "/faces/financeiro/orcamentario/bens/bensimoveis/visualizar.xhtml"),
    @URLMapping(id = "listar-bens-imoveis", pattern = "/contabil/bens-imoveis/listar/", viewId = "/faces/financeiro/orcamentario/bens/bensimoveis/lista.xhtml")
})
public class BensImoveisControlador extends PrettyControlador<BensImoveis> implements Serializable, CRUD {

    @EJB
    private BensImoveisFacade facade;

    public BensImoveisControlador() {
        super(BensImoveis.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/contabil/bens-imoveis/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-bens-imoveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setTipoLancamento(TipoLancamento.NORMAL);
        selecionado.setDataBem(facade.getSistemaFacade().getDataOperacao());
        selecionado.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
        selecionado.setUnidadeOrganizacional(facade.getSistemaFacade().getUnidadeOrganizacionalOrcamentoCorrente());
    }

    @URLAction(mappingId = "editar-bens-imoveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "ver-bens-imoveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        try {
            validarSelecionado();
            if (isOperacaoNovo()) {
                facade.salvarNovo(selecionado);
            } else {
                this.facade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    private void validarSelecionado() {
        selecionado.realizarValidacoes();
        validaRegrasEspecifica();
    }

    public HierarquiaOrganizacional getUnidadeAdministrativa() {
        if (selecionado.getUnidadeOrganizacional() != null) {
            HierarquiaOrganizacional hierarquiaOrganizacional = facade.getHierarquiaOrganizacionalFacade().getHierarquiaAdministrativaDaOrcamentaria(selecionado.getUnidadeOrganizacional(), selecionado.getDataBem());
            try {
                return hierarquiaOrganizacional;
            } catch (Exception ex) {
                FacesUtil.addOperacaoNaoRealizada("Hierarquia Administrativa não encontrada para a unidade: " + selecionado.getUnidadeOrganizacional());
            }
        }
        return null;
    }

    private void validaRegrasEspecifica() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getTipoIngresso() == null && isTipoOperacaoParaIngresso()) {
            ve.adicionarMensagemDeCampoObrigatorio(" O campo Tipo de Ingresso deve ser informado.");
        }
        if (selecionado.getTipoBaixaBens() == null && isTipoOperacaoParaBaixa()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Baixa deve ser informado.");
        }
        if (selecionado.getValor() == null || selecionado.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo valor deve ser maior que zero (0).");
        }
        ve.lancarException();
    }

    public Boolean isRegistroEditavel() {
        return isOperacaoEditar();
    }

    public List<TipoLancamento> getTiposLancamento() {
        List<TipoLancamento> lista = new ArrayList<TipoLancamento>();
        for (TipoLancamento tipo : TipoLancamento.values()) {
            lista.add(tipo);
        }
        return lista;
    }

    public List<SelectItem> getListaTipoGrupo() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoGrupo tg : TipoGrupo.values()) {
            if (tg.getClasseDeUtilizacao() == selecionado.getClass()) {
                toReturn.add(new SelectItem(tg, tg.getDescricao()));
            }
        }
        return toReturn;
    }

    private List<TipoOperacaoBensImoveis> excecoesDeItensListados() {
        List<TipoOperacaoBensImoveis> lista = new ArrayList<>();
        lista.add(TipoOperacaoBensImoveis.AQUISICAO_BENS_IMOVEIS);
        lista.add(TipoOperacaoBensImoveis.TRANSFERENCIA_BENS_IMOVEIS_CONCEDIDA);
        lista.add(TipoOperacaoBensImoveis.TRANSFERENCIA_BENS_IMOVEIS_RECEBIDA);
        lista.add(TipoOperacaoBensImoveis.TRANSFERENCIA_BENS_IMOVEIS_AMORTIZACAO_CONCEDIDA);
        lista.add(TipoOperacaoBensImoveis.TRANSFERENCIA_BENS_IMOVEIS_AMORTIZACAO_RECEBIDA);
        lista.add(TipoOperacaoBensImoveis.TRANSFERENCIA_BENS_IMOVEIS_DEPRECIACAO_CONCEDIDA);
        lista.add(TipoOperacaoBensImoveis.TRANSFERENCIA_BENS_IMOVEIS_DEPRECIACAO_RECEBIDA);
        return lista;
    }

    public List<SelectItem> getOperacoesBemImoveis() {
        List<SelectItem> lista = new ArrayList<>();
        lista.add(new SelectItem(null, ""));
        for (TipoOperacaoBensImoveis tipo : TipoOperacaoBensImoveis.values()) {
            if (!excecoesDeItensListados().contains(tipo)) {
                lista.add(new SelectItem(tipo, tipo.getDescricao()));
            }
        }
        return lista;
    }


    public List<TipoIngresso> completarTipoIngresso(String parte) {
        return facade.getTipoIngressoFacade().listaFiltrandoPorTipoBem(parte.trim(), TipoBem.IMOVEIS);
    }

    public List<TipoBaixaBens> completarTipoBaixaBens(String parte) {
        return facade.getTipoBaixaBensFacade().listaFiltrandoPorTipoBem(parte.trim(), TipoBem.IMOVEIS);
    }

    public List<GrupoBem> completarGrupoBem(String parte) {
        return facade.getGrupoBemFacade().buscarGrupoBemPorTipoBem(parte.trim(), TipoBem.IMOVEIS);
    }


    public void setaPessoa(SelectEvent evt) {
        Pessoa p = (Pessoa) evt.getObject();
    }


    public void definirEventoContabil() {
        try {
            atribuirNullAoTipoBaixaTipoIngresso();
            facade.definirEventoContabil(selecionado);
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, " Evento não encontrado! ", ex.getMessage()));
        }
    }

    public boolean renderizarCampoTipoIngresso() {
        return selecionado.getTipoOperacaoBemEstoque() != null && isTipoOperacaoParaIngresso();
    }

    public boolean renderizarCampoTipoBaixa() {
        return selecionado.getTipoOperacaoBemEstoque() != null && isTipoOperacaoParaBaixa();
    }

    public void atribuirNullAoTipoBaixaTipoIngresso() {
        selecionado.setTipoIngresso(null);
        selecionado.setTipoBaixaBens(null);
    }

    private boolean isTipoOperacaoParaBaixa() {
        return TipoOperacaoBensImoveis.DESINCORPORACAO_BENS_IMOVEIS.equals(selecionado.getTipoOperacaoBemEstoque());
    }

    private boolean isTipoOperacaoParaIngresso() {
        return TipoOperacaoBensImoveis.INCORPORACAO_BENS_IMOVEIS.equals(selecionado.getTipoOperacaoBemEstoque());
    }

    public BensImoveis getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(BensImoveis selecionado) {
        this.selecionado = selecionado;
    }
}
