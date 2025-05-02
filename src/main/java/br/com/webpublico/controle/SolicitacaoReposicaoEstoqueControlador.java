/*
 * Codigo gerado automaticamente em Wed Apr 11 19:47:48 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.StatusMaterial;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.OperacaoEstoqueException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

@ManagedBean(name = "solicitacaoReposicaoEstoqueControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaSolicitacaoDeReposicao", pattern = "/solicitacao-de-reposicao-de-estoque/novo/", viewId = "/faces/administrativo/materiais/solicitacaoreposicaoestoque/edita.xhtml"),
    @URLMapping(id = "editarSolicitacaoDeReposicao", pattern = "/solicitacao-de-reposicao-de-estoque/editar/#{solicitacaoReposicaoEstoqueControlador.id}/", viewId = "/faces/administrativo/materiais/solicitacaoreposicaoestoque/edita.xhtml"),
    @URLMapping(id = "listarSolicitacaoDeReposicao", pattern = "/solicitacao-de-reposicao-de-estoque/listar/", viewId = "/faces/administrativo/materiais/solicitacaoreposicaoestoque/lista.xhtml"),
    @URLMapping(id = "verSolicitacaoDeReposicao", pattern = "/solicitacao-de-reposicao-de-estoque/ver/#{solicitacaoReposicaoEstoqueControlador.id}/", viewId = "/faces/administrativo/materiais/solicitacaoreposicaoestoque/visualizar.xhtml")
})
public class SolicitacaoReposicaoEstoqueControlador extends PrettyControlador<SolicitacaoReposicaoEstoque> implements Serializable, CRUD {

    @EJB
    private SolicitacaoReposicaoEstoqueFacade solicitacaoReposicaoEstoqueFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private ProcessoDeCompraFacade processoDeCompraFacade;
    @EJB
    private MaterialFacade materialFacade;
    @EJB
    private PoliticaDeEstoqueFacade politicaDeEstoqueFacade;
    @EJB
    private EstoqueFacade estoqueFacade;
    @EJB
    private LocalEstoqueFacade localEstoqueFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SolicitacaoMaterialFacade solicitacaoMaterialFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private LocalEstoque localEstoqueSelecionado;
    private List<LocalEstoque> locaisEncontrados;
    private Map<LocalEstoque, List<ItemSolicitacaoReposicaoEstoque>> mapaLocalEItens;

    public SolicitacaoReposicaoEstoqueControlador() {
        metadata = new EntidadeMetaData(SolicitacaoReposicaoEstoque.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/solicitacao-de-reposicao-de-estoque/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novaSolicitacaoDeReposicao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        SolicitacaoReposicaoEstoque solicitacaoDaSessao = (SolicitacaoReposicaoEstoque) Web.pegaDaSessao(SolicitacaoReposicaoEstoque.class);
        if (solicitacaoDaSessao != null) {
            selecionado = solicitacaoDaSessao;
            novoCadastroVindoDaSessao();
        } else {
            novoCadastroDoZero();
        }
    }

    private void novoCadastroVindoDaSessao() {
        hierarquiaOrganizacional = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(selecionado.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ADMINISTRATIVA);
        preencherMapa();
    }

    private void novoCadastroDoZero() {
        super.novo();
        hierarquiaOrganizacional = null;
        localEstoqueSelecionado = null;
        selecionado.setDataSolicitacao(sistemaFacade.getDataOperacao());
        selecionado.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
    }

    @Override
    @URLAction(mappingId = "editarSolicitacaoDeReposicao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        selecionado.setItensSolicitados(solicitacaoReposicaoEstoqueFacade.recuperaItensDaSolicitacaoReposicaoEstoque(selecionado));
        hierarquiaOrganizacional = hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), selecionado.getUnidadeOrganizacional(), sistemaFacade.getDataOperacao());
    }

    @Override
    @URLAction(mappingId = "verSolicitacaoDeReposicao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    public void excluir() {
        try {
            super.excluir();
        } catch (ValidacaoException vex) {
            FacesUtil.printAllFacesMessages(vex.getMensagens());
        }
    }

    @Override
    public void salvar() {
        selecionado.setNumero(singletonGeradorCodigo.getProximoCodigo(SolicitacaoReposicaoEstoque.class, "numero"));
        super.salvar();
    }

    @Override
    public AbstractFacade getFacede() {
        return solicitacaoReposicaoEstoqueFacade;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public LocalEstoque getLocalEstoqueSelecionado() {
        return localEstoqueSelecionado;
    }

    public void setLocalEstoqueSelecionado(LocalEstoque localEstoqueSelecionado) {
        if (localEstoqueSelecionado != null) {
            this.localEstoqueSelecionado = localEstoqueSelecionado;
            FacesUtil.atualizarComponente("Formulario");
        }
    }

    public List<ItemSolicitacaoReposicaoEstoque> getItensDoLocalSelecionado() {
        return mapaLocalEItens.get(localEstoqueSelecionado);
    }

    public List<LocalEstoque> getLocaisEncontrados() {
        return locaisEncontrados;
    }

    public void setLocaisEncontrados(List<LocalEstoque> locaisEncontrados) {
        this.locaisEncontrados = locaisEncontrados;
    }

    public boolean disableBotoesVisualizarEGerarSolicitacao() {
        return selecionado.getItensSolicitados() == null || selecionado.getItensSolicitados().isEmpty();
    }

    public void adicionarItemNaListaDoSelecionado(ItemSolicitacaoReposicaoEstoque itemSolicitacaoReposicaoEstoque) {
        selecionado.getItensSolicitados().add(itemSolicitacaoReposicaoEstoque);
    }

    public void removerItemNaListaDoSelecionado(ItemSolicitacaoReposicaoEstoque itemSolicitacaoReposicaoEstoque) {
        selecionado.getItensSolicitados().remove(itemSolicitacaoReposicaoEstoque);
    }

    public boolean itemEstaNaListaDoSelecionado(ItemSolicitacaoReposicaoEstoque itemSolicitacaoReposicaoEstoque) {
        if (selecionado.getItensSolicitados() == null) {
            return false;
        }

        if (selecionado.getItensSolicitados().contains(itemSolicitacaoReposicaoEstoque)) {
            return true;
        }

        return false;
    }

    public void preencherMapa() {
        try {
            if (validarPreenchimento()) {
                selecionado.setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
                locaisEncontrados = localEstoqueFacade.completarLocaisEstoquePrimeiroNivel("", selecionado.getUnidadeOrganizacional());
                mapaLocalEItens = new HashMap<>();
                List<ItemSolicitacaoReposicaoEstoque> itens = null;

                for (LocalEstoque localEstoque : locaisEncontrados) {
                    itens = new ArrayList<>();
                    localEstoque = localEstoqueFacade.recarregar(localEstoque);
                    Set<Material> setMaterial = new HashSet<>();
                    setMaterial.addAll(materialFacade.completarMaterialNaHierarquiaDoLocalEstoquePorDataAndComEstoque(localEstoque, null, "", StatusMaterial.getSituacoesDeferidoInativo()));
                    ConsolidadorDeEstoque novoConsolidador = localEstoqueFacade.getNovoConsolidadorComReservaEstoque(localEstoque, setMaterial, selecionado.getDataSolicitacao());
                    for (Material material : setMaterial) {
                        ItemSolicitacaoReposicaoEstoque isre = criarNovoItem(material, novoConsolidador, localEstoque);

                        if (isre != null) {
                            itens.add(isre);
                        }

                    }

                    if (!itens.isEmpty()) {
                        mapaLocalEItens.put(localEstoque, itens);
                    }
                }
            }
        } catch (OperacaoEstoqueException op) {
            FacesUtil.addOperacaoRealizada(op.getMessage());
        }
    }

    private boolean validarPreenchimento() {
        boolean validou = true;

        if (hierarquiaOrganizacional == null) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Informe uma Unidade Organizacional.");
            validou = false;
        }

        if (selecionado.getDataSolicitacao() == null) {
            FacesUtil.addError(SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), "Informe a data.");
            validou = false;
        }

        return validou;
    }

    public ItemSolicitacaoReposicaoEstoque criarNovoItem(Material material, ConsolidadorDeEstoque consolidadorDeEstoque, LocalEstoque localEstoque) throws OperacaoEstoqueException {
        BigDecimal estoqueFisico = null;
        if (consolidadorDeEstoque != null) {
            estoqueFisico = consolidadorDeEstoque.getFisicoConsolidado(material);
        }

        if (estoqueFisico == null) {
            return null;
        }

        PoliticaDeEstoque politica = politicaDeEstoqueFacade.recuperarPoliticaDeEstoqueComBaseNoMaterialEUnidade(material, localEstoque.getUnidadeOrganizacional());

        if (politica == null) {
            return null;
        }

        if (politica.getPontoDeReposicao() != null && estoqueFisico.compareTo(politica.getPontoDeReposicao()) <= 0) {
            BigDecimal quantidade = solicitacaoMaterialFacade.recuperarItemSolicitacaoPorMaterialEUnidade(material, selecionado.getUnidadeOrganizacional());

            BigDecimal qtdePDC = BigDecimal.ZERO;
            if (quantidade != null) {
                qtdePDC = quantidade;
            }

            return new ItemSolicitacaoReposicaoEstoque(selecionado, estoqueFisico, material, politica, qtdePDC, localEstoque);
        }

        return null;
    }

    public void novoFormularioDeCotacaoReporEstoque() {
        if (validaItens()) {
            Web.poeNaSessao(selecionado);
            FacesUtil.redirecionamentoInterno("/formulario-cotacao/reposicao-de-estoque/");
        }
    }

    private boolean validaItens() {
        boolean validou = true;

        if (verificaSeExistemItensDoSelecionadoComQuantidadeAcimaDoEstoqueMaximoQuandoHouver()) {
            validou = false;
        }

        return validou;
    }

    private boolean verificaSeExistemItensDoSelecionadoComQuantidadeAcimaDoEstoqueMaximoQuandoHouver() {
        boolean pobrema = false;

        for (ItemSolicitacaoReposicaoEstoque isre : selecionado.getItensSolicitados()) {
            if (isre.getPoliticaDeEstoque().getEstoqueMaximo() != null) {
                if (isre.getQuantidadeParaComprar().add(isre.getEstoqueFisico()).compareTo(isre.getPoliticaDeEstoque().getEstoqueMaximo()) > 0) {
                    pobrema = true;
                    exibirMensagemItemComQuantidadeInválida("A quantidade do material " + isre.getEstoque().getMaterial().getDescricao().toUpperCase()
                        + ", do local de estoque " + isre.getEstoque().getLocalEstoque().getDescricao().toUpperCase()
                        + ", somada com a quantidade em estoque, não pode ser superior ao seu estoque máximo!");
                }
            }
        }

        return pobrema;
    }

    private void exibirMensagemItemComQuantidadeInválida(String mensagem) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
            "Quantidade inválida!", mensagem));
    }
}
