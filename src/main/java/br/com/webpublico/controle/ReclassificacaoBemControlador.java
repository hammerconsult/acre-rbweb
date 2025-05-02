package br.com.webpublico.controle;

import br.com.webpublico.entidades.GrupoBem;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.LoteReclassificacaoBem;
import br.com.webpublico.entidades.ReclassificacaoBem;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoGrupo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.administrativo.pesquisabem.PesquisaBem;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 30/01/14
 * Time: 19:15
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "reclassificacaoBemControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novaReclassificacaoDeGrupo", pattern = "/reclassificacao-de-grupo/novo/", viewId = "/faces/administrativo/patrimonio/reclassificacaobem/edita.xhtml"),
        @URLMapping(id = "editarReclassificacaoDeGrupo", pattern = "/reclassificacao-de-grupo/editar/#{reclassificacaoBemControlador.id}/", viewId = "/faces/administrativo/patrimonio/reclassificacaobem/edita.xhtml"),
        @URLMapping(id = "listarReclassificacaoDeGrupo", pattern = "/reclassificacao-de-grupo/listar/", viewId = "/faces/administrativo/patrimonio/reclassificacaobem/lista.xhtml"),
        @URLMapping(id = "verReclassificacaoDeGrupo", pattern = "/reclassificacao-de-grupo/ver/#{reclassificacaoBemControlador.id}/", viewId = "/faces/administrativo/patrimonio/reclassificacaobem/visualizar.xhtml")
})
public class ReclassificacaoBemControlador extends PrettyControlador<LoteReclassificacaoBem> implements CRUD {

    @EJB
    private ReclassificacaoBemFacade reclassificacaoBemFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private GrupoBemFacade grupoBemFacade;
    @EJB
    private GrupoObjetoCompraGrupoBemFacade grupoObjetoCompraGrupoBemFacade;

    private PesquisaBem pesquisaBem;
    private ManipuladorDaReclassificacao manipuladorDaReclassificacao;
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    public ReclassificacaoBemControlador() {
        super(LoteReclassificacaoBem.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/reclassificacao-de-grupo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return reclassificacaoBemFacade;
    }

    @URLAction(mappingId = "novaReclassificacaoDeGrupo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setCodigo(singletonGeradorCodigo.getProximoCodigo(LoteReclassificacaoBem.class, "codigo"));
        selecionado.setDataHoraCriacao(sistemaFacade.getDataOperacao());
        selecionado.setUsuario(sistemaFacade.getUsuarioCorrente());
        inicializarAtributos();
    }

    @URLAction(mappingId = "verReclassificacaoDeGrupo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarReclassificacaoDeGrupo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        try {
            if (podeSalvar()) {
                try {
                    reclassificacaoBemFacade.salvar(selecionado);
                    FacesUtil.addOperacaoRealizada("Reclassificação realizada com sucesso.");
                    redireciona();
                } catch (ExcecaoNegocioGenerica eng) {
                    FacesUtil.addError(SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), eng.getMessage());
                } catch (Exception throwable) {
                    logger.error(throwable.getMessage());
                }
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    @Override
    public void redireciona() {
        if (!isSessao()) {
            Web.limpaNavegacao();
        }
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "listar/");
    }

    @Override
    public void cancelar() {
        if (operacao.equals(Operacoes.NOVO) &&
                selecionado.getId() != null &&
                selecionado.getReclassificacoes().isEmpty()) {
            reclassificacaoBemFacade.remover(selecionado);
        }
        super.cancelar();
    }

    private void inicializarAtributos() {
        manipuladorDaReclassificacao = new ManipuladorDaReclassificacao();
        pesquisaBem = new PesquisaBem(bemFacade,
                ReclassificacaoBem.class, "loteReclassificacaoBem_id", idDoSelecionadoEmString(), manipuladorDaReclassificacao);
    }

    private String idDoSelecionadoEmString() {
        return selecionado.getId() != null ? selecionado.getId().toString() : "null";
    }

    private boolean podeSalvar() {
        Boolean retorno = Util.validaCampos(selecionado);

        if (selecionado.getReclassificacoes().isEmpty()) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Informe os bens a serem reclassificados.");
            retorno = false;
        }

        if (selecionado.getGrupoObjetoCompraGrupoBem() != null && selecionado.getGrupoObjetoCompraGrupoBem().getGrupoBem() == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "O grupo objeto de compra não está associado com nenhum grupo patrimonial.");
            retorno = false;
        }

        return retorno;
    }

    public List<SelectItem> getTiposDeGrupoBem() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (TipoGrupo tipoGrupo : TipoGrupo.values()) {
            if (TipoGrupo.BEM_MOVEL_PRINCIPAL.equals(tipoGrupo)
                    || TipoGrupo.BEM_MOVEL_INSERVIVEL.equals(tipoGrupo)
                    || TipoGrupo.BEM_MOVEL_ALIENAR.equals(tipoGrupo)
                    || TipoGrupo.BEM_IMOVEL_PRINCIPAL.equals(tipoGrupo)
                    || TipoGrupo.BEM_IMOVEL_ALIENAR.equals(tipoGrupo)) {
                retorno.add(new SelectItem(tipoGrupo, tipoGrupo.getDescricao()));
            }
        }
        return retorno;
    }

    public class ManipuladorDaReclassificacao extends ManipuladorDeBemImpl {

        public ManipuladorDaReclassificacao() {
            super(bemFacade);
        }

        @Override
        public void atualizar() {
            selecionado.setReclassificacoes(getListaDeEventosSelecionados());
        }

        @Override
        protected boolean validarCampos() {
            return Util.validaCampos(selecionado);
        }

        @Override
        public void pesquisar() {
            setListaEncontradosNaPesquisa(reclassificacaoBemFacade.criarListaReclassificacaoPesquisaDeBens(pesquisaBem.pesquisar(idDoSelecionadoEmString()), selecionado));
            setListaDeEventosSelecionados(selecionado.getReclassificacoes());
        }
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
        if (hierarquiaOrganizacional != null) {
            pesquisaBem.setHierarquiaOrganizacionalPesquisa(hierarquiaOrganizacional);
        }
    }

    public ManipuladorDaReclassificacao getManipuladorDaReclassificacao() {
        return manipuladorDaReclassificacao;
    }

    public void setManipuladorDaReclassificacao(ManipuladorDaReclassificacao manipuladorDaReclassificacao) {
        this.manipuladorDaReclassificacao = manipuladorDaReclassificacao;
    }

    public PesquisaBem getPesquisaBem() {
        return pesquisaBem;
    }

    public void setPesquisaBem(PesquisaBem pesquisaBem) {
        this.pesquisaBem = pesquisaBem;
    }

    public List<GrupoBem> completaGrupoBem(String parte) {
        if (selecionado.getTipoBem() != null) {
            return grupoBemFacade.buscarGrupoBemPorTipoBem(parte, selecionado.getTipoBem());
        }
        return new ArrayList();
    }

    public void processaMudancaTipoBem() {
        pesquisaBem.setTipoBem(selecionado.getTipoBem());
        selecionado.setNovoGrupoObjetoCompra(null);
    }

    public void recuperarAssociacaoDeGrupos() {
        try {
            if (selecionado.getNovoGrupoObjetoCompra() != null) {
                selecionado.setGrupoObjetoCompraGrupoBem(grupoObjetoCompraGrupoBemFacade.recuperarAssociacaoDoGrupoObjetoCompra(selecionado.getNovoGrupoObjetoCompra(), sistemaFacade.getDataOperacao()));
            }
        } catch (Exception ex) {
            FacesUtil.addError(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), ex.getMessage());
        }
    }

    public List<SelectItem> getTiposBens() {
        return Util.getListSelectItem(Arrays.asList(TipoBem.MOVEIS));
    }
}
