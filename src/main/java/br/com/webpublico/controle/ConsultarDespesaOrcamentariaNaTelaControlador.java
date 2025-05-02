/*
 * Codigo gerado automaticamente em Fri Jul 13 15:21:46 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.DefaultTreeNodeORC;
import br.com.webpublico.entidadesauxiliares.VwHierarquiaDespesaORC;
import br.com.webpublico.enums.SubTipoObjetoCompra;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoObjetoCompra;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.primefaces.context.RequestContext;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
public class ConsultarDespesaOrcamentariaNaTelaControlador implements Serializable {

    @EJB
    private DespesaORCFacade despesaORCFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ConfigTipoObjetoCompraFacade configTipoObjetoCompraFacade;
    private String codigoReduzidoDespesaOrc;
    private DefaultTreeNodeORC raizDespesaOrcamentaria;
    private DefaultTreeNodeORC nodeSelecionado;
    private VwHierarquiaDespesaORC vwHierarquiaDespesaORC;
    private DespesaORC despesaORC;
    private String idComponente;
    private Boolean recuperarSomenteUnidadeLogada;
    private Boolean mostrarArvoreExpandida;
    private String filtro;
    private Boolean expansaoFiltro;
    private HierarquiaOrganizacional hierarquiaAdministrativa;
    private HierarquiaOrganizacional hierarquiaOrcamenataria;
    private List<HierarquiaOrganizacional> hierarquiasOrcamentarias;
    private List<TipoObjetoCompra> tiposObjetoCompra;
    private SubTipoObjetoCompra subTipoObjetoCompra;
    private String filtroElemento;

    protected static final Logger logger = LoggerFactory.getLogger(ConsultarDespesaOrcamentariaNaTelaControlador.class);

    public DefaultTreeNodeORC montarArvoreDespesaOrcamentaria() {
        raizDespesaOrcamentaria = new DefaultTreeNodeORC(0L, "Despesa Orçamentária", null);

        List<VwHierarquiaDespesaORC> hierarquias = buscaDadosParaArvore(recuperarSomenteUnidadeLogada, hierarquiaOrcamenataria.getSubordinada());

        DefaultTreeNodeORC orgao = null;
        DefaultTreeNodeORC unidade = null;
        DefaultTreeNodeORC subAcao = null;
        DefaultTreeNodeORC conta = null;

        for (VwHierarquiaDespesaORC vwDespesaOrc : hierarquias) {
            orgao = insereNo(raizDespesaOrcamentaria, 0, vwDespesaOrc.getOrgao(), orgao);
            orgao.setExpanded(mostrarArvoreExpandida);
            marcarNodeSelecionado(orgao);

            unidade = insereNo(orgao, 0, vwDespesaOrc.getUnidade(), unidade);
            unidade.setExpanded(mostrarArvoreExpandida);
            marcarNodeSelecionado(unidade);

            subAcao = insereNo(unidade, 0, vwDespesaOrc.getSubAcao(), subAcao);
            subAcao.setExpanded(mostrarArvoreExpandida);
            marcarNodeSelecionado(subAcao);

            conta = insereNo(subAcao, vwDespesaOrc.getId(), vwDespesaOrc.getConta(), conta);
            conta.setExpanded(mostrarArvoreExpandida);
            marcarNodeSelecionado(conta);
        }
        raizDespesaOrcamentaria.setExpanded(mostrarArvoreExpandida);
        return raizDespesaOrcamentaria;
    }

    private List<VwHierarquiaDespesaORC> buscaDadosParaArvore(Boolean filtrarSomentePorUnidadeLogada, UnidadeOrganizacional unidadeOrc) {
        Exercicio exercicio = sistemaFacade.getExercicioCorrente();
        List<UnidadeOrganizacional> unidades = Lists.newArrayList();
        definirUnidades(unidadeOrc, unidades, filtrarSomentePorUnidadeLogada);
        if (!Util.isListNullOrEmpty(tiposObjetoCompra) && subTipoObjetoCompra != null) {
            List<Long> idsConfig = getIdsConfigTipoObjetoCompraContaDespesa();
            return despesaORCFacade.buscarVwHierarquiaDespesaOrcPorConfiguracao(sistemaFacade.getDataOperacao(), unidades, exercicio, filtro, idsConfig, filtroElemento);
        }
        return despesaORCFacade.buscarVwHierarquiaDespesaOrc(unidades, filtro, filtroElemento);
    }

    private List<Long> getIdsConfigTipoObjetoCompraContaDespesa() {
        List<Long> idsConfig = Lists.newArrayList();
        for (TipoObjetoCompra tipo : tiposObjetoCompra) {
            ConfigTipoObjetoCompra config = configTipoObjetoCompraFacade.recuperarConfiguracaoVigente(tipo, subTipoObjetoCompra, sistemaFacade.getDataOperacao());
            if (config != null) {
                idsConfig.add(config.getId());
            }
        }
        return idsConfig;
    }

    private void definirUnidades(UnidadeOrganizacional unidadeOrganizacional, List<UnidadeOrganizacional> unidades, Boolean filtrarSomentePorUnidadeLogada) {
        if (filtrarSomentePorUnidadeLogada) {
            if (unidadeOrganizacional != null) {
                unidades.add(this.hierarquiaOrcamenataria.getSubordinada());
            } else {
                unidades.add(sistemaFacade.getUnidadeOrganizacionalOrcamentoCorrente());
            }
        } else {
            List<HierarquiaOrganizacional> orcamentariasUsuarioLogado = hierarquiaOrganizacionalFacade.listaHierarquiaUsuarioCorrentePorNivel(
                "",
                sistemaFacade.getUsuarioCorrente(),
                sistemaFacade.getExercicioCorrente(),
                sistemaFacade.getDataOperacao(), TipoHierarquiaOrganizacional.ORCAMENTARIA.name(), 3);
            if (orcamentariasUsuarioLogado != null) {
                for (HierarquiaOrganizacional orcamentaria : orcamentariasUsuarioLogado) {
                    unidades.add(orcamentaria.getSubordinada());
                }
            }
        }
    }

    private DefaultTreeNodeORC insereNo(DefaultTreeNodeORC noPai, long id, String descricao, DefaultTreeNodeORC noFilho) {
        boolean vaiAdicionar = false;
        DefaultTreeNodeORC pai = noPai;
        DefaultTreeNodeORC filho = noFilho;
        if (pai.getChildCount() == 0) {//se não tiver filhos adiciona
            vaiAdicionar = true;
        } else {//se tiver filhos entra aqui
            String desc;
            for (TreeNode t : pai.getChildren()) {
                desc = (String) t.getData();
                if (desc.equals(descricao)) {
                    vaiAdicionar = false;
                    break;//se achou um igual sai do for
                } else {
                    vaiAdicionar = true;
                }
            }
        }
        if (vaiAdicionar) {
            filho = new DefaultTreeNodeORC(id, descricao, pai);
        }
        return filho;
    }

    private void marcarNodeSelecionado(DefaultTreeNodeORC node) {
        if (nodeSelecionado != null) {
            if (nodeSelecionado.getId() == node.getId()) {
                node.setSelected(true);
            }
        }
    }

    public void limparSelecaoDotacaoOrcamentaria() {
        try{
        montarArvoreDespesaOrcamentaria();
        this.filtro = null;
        this.codigoReduzidoDespesaOrc = null;
        this.despesaORC = null;
        this.vwHierarquiaDespesaORC = null;
        this.nodeSelecionado = null;
        } catch (ValidacaoException ex) {
            logger.error("Erro de Operação::: ConsultarDespesaOrcamentariaNaTelaControlador ::: limparSelecaoDotacaoOrcamentaria: ", ex);
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    public void confirmarSelecaoDespesaOrcamentaria() {
        if (nodeSelecionado == null) {
            FacesUtil.addOperacaoNaoPermitida("Selecione uma dotação orçamentária no último nível para continuar.");
            return;
        }
        if (nodeSelecionado.getId() > 0) {
            DespesaORC dorc = despesaORCFacade.recuperar(nodeSelecionado.getId());
            setDespesaORC(dorc);
            setVwHierarquiaDespesaORC(despesaORCFacade.recuperaStrDespesaPorId(getDespesaORC().getId()));
            setCodigoReduzidoDespesaOrc(dorc.getProvisaoPPADespesa().getContaDeDespesa().getCodigo());

            RequestContext.getCurrentInstance().update(idComponente + ":codigo-dotacao-orcamentaria");
            RequestContext.getCurrentInstance().update(idComponente + ":grid-info-dotacao-orcamentaria");
            RequestContext.getCurrentInstance().update(idComponente + ":bt-info-dotacao-orcamentaria");

            RequestContext.getCurrentInstance().execute("setar()");
        } else {
            FacesUtil.addOperacaoNaoPermitida("Só é permitido selecionar o último nível da estrutura.");
            nodeSelecionado = null;
        }
    }

    public void filtrar() {
        try {
            if (this.hierarquiaAdministrativa.getSubordinada() != null && this.hierarquiaOrcamenataria.getSubordinada() == null) {
                FacesUtil.addOperacaoNaoPermitida("A Unidade Orçamentária deve ser selecionada.");
                return;
            }
            raizDespesaOrcamentaria = null;
            montarArvoreDespesaOrcamentaria();
        } catch (ValidacaoException ex) {
            logger.error("Erro de Operação::: ConsultarDespesaOrcamentariaNaTelaControlador ::: filtrar: ", ex);
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    public void atribuirVariaveisEditando(DespesaORC despesaORC) {
        try {
            limparSelecaoDotacaoOrcamentaria();
            if (despesaORC != null) {
                if (despesaORC.getId() != null) {
                    setDespesaORC(despesaORC);
                    setVwHierarquiaDespesaORC(despesaORCFacade.recuperaStrDespesaPorId(getDespesaORC().getId()));
                    setCodigoReduzidoDespesaOrc(despesaORC.getProvisaoPPADespesa().getContaDeDespesa().getCodigo());
                }
            }
        } catch (ValidacaoException ex) {
            logger.error("Erro de Operação::: ConsultarDespesaOrcamentariaNaTelaControlador ::: atribuirVariaveisEditando: ", ex);
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        }
    }

    public List<TipoObjetoCompra> tipoObjetoCompraPorDescricao(String descricao) {
        return Lists.newArrayList(TipoObjetoCompra.valueOf(descricao));
    }

    public List<TipoObjetoCompra> getTiposObjetoCompra() {
        return tiposObjetoCompra;
    }

    public void setTiposObjetoCompra(List<TipoObjetoCompra> tiposObjetoCompra) {
        this.tiposObjetoCompra = tiposObjetoCompra;
    }

    public SubTipoObjetoCompra getSubTipoObjetoCompra() {
        return subTipoObjetoCompra;
    }

    public void setSubTipoObjetoCompra(SubTipoObjetoCompra subTipoObjetoCompra) {
        this.subTipoObjetoCompra = subTipoObjetoCompra;
    }

    public String getDescricaoUnidadeOrcamentariaLogada() {
        return hierarquiaOrganizacionalFacade.buscarCodigoDescricaoHierarquia(
            TipoHierarquiaOrganizacional.ORCAMENTARIA.name(),
            sistemaFacade.getUnidadeOrganizacionalOrcamentoCorrente(),
            sistemaFacade.getDataOperacao());
    }

    public Boolean getMostrarArvoreExpandida() {
        return mostrarArvoreExpandida;
    }

    public void setMostrarArvoreExpandida(Boolean mostrarArvoreExpandida) {
        this.mostrarArvoreExpandida = mostrarArvoreExpandida;
    }

    public String getIdComponente() {
        return idComponente;
    }

    public void setIdComponente(String idComponente) {
        this.idComponente = idComponente;
    }

    public DespesaORC getDespesaORC() {
        return despesaORC;
    }

    public void setDespesaORC(DespesaORC despesaORC) {
        this.despesaORC = despesaORC;
    }

    public VwHierarquiaDespesaORC getVwHierarquiaDespesaORC() {
        return vwHierarquiaDespesaORC;
    }

    public void setVwHierarquiaDespesaORC(VwHierarquiaDespesaORC vwHierarquiaDespesaORC) {
        this.vwHierarquiaDespesaORC = vwHierarquiaDespesaORC;
    }

    public DefaultTreeNodeORC getNodeSelecionado() {
        return nodeSelecionado;
    }

    public void setNodeSelecionado(DefaultTreeNodeORC nodeSelecionado) {
        this.nodeSelecionado = nodeSelecionado;
    }

    public DefaultTreeNodeORC getRaizDespesaOrcamentaria() {
        return raizDespesaOrcamentaria;
    }

    public void setRaizDespesaOrcamentaria(DefaultTreeNodeORC raizDespesaOrcamentaria) {
        this.raizDespesaOrcamentaria = raizDespesaOrcamentaria;
    }

    public String getCodigoReduzidoDespesaOrc() {
        return codigoReduzidoDespesaOrc;
    }

    public void setCodigoReduzidoDespesaOrc(String codigoReduzidoDespesaOrc) {
        this.codigoReduzidoDespesaOrc = codigoReduzidoDespesaOrc;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public HierarquiaOrganizacional getHierarquiaAdministrativa() {
        return hierarquiaAdministrativa;
    }

    public void setHierarquiaAdministrativa(HierarquiaOrganizacional hierarquiaAdministrativa) {
        this.hierarquiaAdministrativa = hierarquiaAdministrativa;
    }

    public List<HierarquiaOrganizacional> getHierarquiasOrcamentarias() {
        return hierarquiasOrcamentarias;
    }

    public void setHierarquiasOrcamentarias(List<HierarquiaOrganizacional> hierarquiasOrcamentarias) {
        this.hierarquiasOrcamentarias = hierarquiasOrcamentarias;
    }

    public HierarquiaOrganizacional getHierarquiaOrcamenataria() {
        return hierarquiaOrcamenataria;
    }

    public void setHierarquiaOrcamenataria(HierarquiaOrganizacional hierarquiaOrcamenataria) {
        this.hierarquiaOrcamenataria = hierarquiaOrcamenataria;
    }

    public Boolean getExpansaoFiltro() {
        return expansaoFiltro;
    }

    public void setExpansaoFiltro(Boolean expansaoFiltro) {
        this.expansaoFiltro = expansaoFiltro;
    }

    public Boolean getRecuperarSomenteUnidadeLogada() {
        return recuperarSomenteUnidadeLogada;
    }

    public void setRecuperarSomenteUnidadeLogada(Boolean recuperarSomenteUnidadeLogada) {
        this.expansaoFiltro = recuperarSomenteUnidadeLogada;
        this.recuperarSomenteUnidadeLogada = recuperarSomenteUnidadeLogada;
    }

    public String getFiltroElemento() {
        return filtroElemento;
    }

    public void setFiltroElemento(String filtroElemento) {
        this.filtroElemento = filtroElemento;
    }

    public void setRecuperarPorUnidadeOrganizacionalEspecifica(UnidadeOrganizacional unidadeAdministrativa, UnidadeOrganizacional unidadeOrcamentaria) {
        if (unidadeAdministrativa != null) {
            this.hierarquiaAdministrativa = hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(
                TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
                unidadeAdministrativa,
                sistemaFacade.getDataOperacao());

            if (this.hierarquiaAdministrativa != null) {
                if (unidadeOrcamentaria != null) {
                    HierarquiaOrganizacional hierarquiaDaUnidade = hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(
                        TipoHierarquiaOrganizacional.ORCAMENTARIA.name(),
                        unidadeOrcamentaria,
                        sistemaFacade.getDataOperacao());
                    hierarquiasOrcamentarias = Lists.newArrayList(hierarquiaDaUnidade);
                } else {
                    this.hierarquiasOrcamentarias = hierarquiaOrganizacionalFacade.retornaHierarquiaOrcamentariaPorUnidadeAdministrativa(
                        this.hierarquiaAdministrativa.getSubordinada(),
                        sistemaFacade.getDataOperacao());
                }
                if (this.hierarquiasOrcamentarias != null && !this.hierarquiasOrcamentarias.isEmpty()) {
                    hierarquiaOrcamenataria = new HierarquiaOrganizacional();
                    this.hierarquiaOrcamenataria = hierarquiasOrcamentarias.get(0);
                }
            }
        }
    }
}
