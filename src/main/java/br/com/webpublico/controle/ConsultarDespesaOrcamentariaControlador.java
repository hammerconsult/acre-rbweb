/*
 * Codigo gerado automaticamente em Fri Jul 13 15:21:46 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.DespesaORC;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.entidadesauxiliares.DefaultTreeNodeORC;
import br.com.webpublico.entidadesauxiliares.VwHierarquiaDespesaORC;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoContaDespesa;
import br.com.webpublico.negocios.DespesaORCFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.UtilRH;
import com.google.common.collect.Lists;
import org.primefaces.context.RequestContext;
import org.primefaces.model.TreeNode;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
public class ConsultarDespesaOrcamentariaControlador implements Serializable {

    @EJB
    private DespesaORCFacade despesaORCFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private String codigoReduzidoDespesaOrc;
    private DefaultTreeNodeORC raizDespesaOrcamentaria;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private DefaultTreeNodeORC nodeSelecionado;
    private VwHierarquiaDespesaORC vwHierarquiaDespesaORC;
    private DespesaORC despesaORC;
    private String idComponente;
    private Boolean recuperarSomenteUnidadeLogada;
    private Boolean mostrarArvoreExpandida;
    private String filtro;
    private Boolean expansaoFiltro;
    private List<TipoContaDespesa> tiposContaDespesa;

    private UnidadeOrganizacional unidadeOrganizacional;

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

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        if (unidadeOrganizacional != null) {
            this.unidadeOrganizacional = unidadeOrganizacional;
        } else {
            this.unidadeOrganizacional = sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente();
        }
    }

    public List<TipoContaDespesa> getTiposContaDespesa() {
        return tiposContaDespesa;
    }

    public void setTiposContaDespesa(List<TipoContaDespesa> tiposContaDespesa) {
        this.tiposContaDespesa = tiposContaDespesa;
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

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public String getCodigoReduzidoDespesaOrc() {
        return codigoReduzidoDespesaOrc;
    }

    public void setCodigoReduzidoDespesaOrc(String codigoReduzidoDespesaOrc) {
        this.codigoReduzidoDespesaOrc = codigoReduzidoDespesaOrc;
    }

    public DefaultTreeNodeORC montarArvoreDespesaOrcamentaria() {
        raizDespesaOrcamentaria = new DefaultTreeNodeORC(0l, "Despesa Orçamentária", null);

        List<VwHierarquiaDespesaORC> listaHierarquia = buscarDadosParaArvore(recuperarSomenteUnidadeLogada, tiposContaDespesa);
        DefaultTreeNodeORC orgao = null;
        DefaultTreeNodeORC unidade = null;
        DefaultTreeNodeORC subAcao = null;
        DefaultTreeNodeORC conta = null;

        for (VwHierarquiaDespesaORC v : listaHierarquia) {
            orgao = insereNo(raizDespesaOrcamentaria, 0, v.getOrgao(), orgao);
            orgao.setExpanded(mostrarArvoreExpandida);
            marcarNodeSelecionado(orgao);

            unidade = insereNo(orgao, 0, v.getUnidade(), unidade);
            unidade.setExpanded(mostrarArvoreExpandida);
            marcarNodeSelecionado(unidade);

            subAcao = insereNo(unidade, 0, v.getSubAcao(), subAcao);
            subAcao.setExpanded(mostrarArvoreExpandida);
            marcarNodeSelecionado(subAcao);

            conta = insereNo(subAcao, v.getId(), v.getConta(), conta);
            conta.setExpanded(mostrarArvoreExpandida);
            marcarNodeSelecionado(conta);
        }
        raizDespesaOrcamentaria.setExpanded(mostrarArvoreExpandida);
        return raizDespesaOrcamentaria;
    }

    private List<VwHierarquiaDespesaORC> buscarDadosParaArvore(Boolean filtrarUnidade, List<TipoContaDespesa> tiposContaDespesa) {
        Exercicio exercicio = UtilRH.getExercicio();
        if (filtrarUnidade) {
            if (unidadeOrganizacional.getId() != null) {
                List<UnidadeOrganizacional> unidades = Lists.newArrayList();
                unidades.add(unidadeOrganizacional);
                return despesaORCFacade.descricaoDoCodigoComponenteConsulta(UtilRH.getDataOperacao(), unidades, exercicio, filtro, tiposContaDespesa);
            }
        } else {
            List<UnidadeOrganizacional> unidades = Lists.newArrayList();
            List<HierarquiaOrganizacional> orcamentarias = hierarquiaOrganizacionalFacade.listaHierarquiaUsuarioCorrentePorNivel("", getSistemaControlador().getUsuarioCorrente(), getSistemaControlador().getExercicioCorrente(), getSistemaControlador().getDataOperacao(), "ORCAMENTARIA", 3);
            if (orcamentarias != null) {
                for (HierarquiaOrganizacional orcamentaria : orcamentarias) {
                    unidades.add(orcamentaria.getSubordinada());
                }
            }
            return despesaORCFacade.descricaoDoCodigoComponenteConsulta(UtilRH.getDataOperacao(), unidades, exercicio, filtro, tiposContaDespesa);
        }
        return null;
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
                DefaultTreeNodeORC treeNodeORC = (DefaultTreeNodeORC) t;
                desc = (String) t.getData();
                if (desc.equals(descricao) && treeNodeORC.getId() == id) {
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
        this.filtro = null;
        this.raizDespesaOrcamentaria = null;
        this.codigoReduzidoDespesaOrc = null;
        this.despesaORC = null;
        this.vwHierarquiaDespesaORC = null;
        this.nodeSelecionado = null;
    }

    public void confirmarSelecaoDespesaOrcamentaria() {
        if (nodeSelecionado == null) {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Selecione uma dotação orçamentária no último nível para poder continuar.");
            return;
        }

        if (nodeSelecionado.getId() > 0) {
            DespesaORC dorc = despesaORCFacade.recuperar(nodeSelecionado.getId());
            setDespesaORC(dorc);
            setVwHierarquiaDespesaORC(despesaORCFacade.recuperaStrDespesaPorId(getDespesaORC().getId()));
            setCodigoReduzidoDespesaOrc(dorc.getProvisaoPPADespesa().getContaDeDespesa().getCodigo());
            RequestContext.getCurrentInstance().execute("dialogDespesaOrcamentaria.hide()");

            RequestContext.getCurrentInstance().update(idComponente + ":codigo-dotacao-orcamentaria");
            RequestContext.getCurrentInstance().update(idComponente + ":grid-info-dotacao-orcamentaria");
            RequestContext.getCurrentInstance().update(idComponente + ":bt-info-dotacao-orcamentaria");

            RequestContext.getCurrentInstance().execute("setar()");
        } else {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Só é permitido selecionar o último nível da estrutura.");
            nodeSelecionado = null;
        }
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public void mudouTipoBuscaUnidade() {
        raizDespesaOrcamentaria = null;
        montarArvoreDespesaOrcamentaria();
    }

    public void filtrar() {
        raizDespesaOrcamentaria = null;
        montarArvoreDespesaOrcamentaria();
    }

    public void atribuirVariaveisEditando(DespesaORC despesaORC) {
        limparSelecaoDotacaoOrcamentaria();
        if (despesaORC != null) {
            if (despesaORC.getId() != null) {
                setDespesaORC(despesaORC);
                setVwHierarquiaDespesaORC(despesaORCFacade.recuperaStrDespesaPorId(getDespesaORC().getId()));
                setCodigoReduzidoDespesaOrc(despesaORC.getProvisaoPPADespesa().getContaDeDespesa().getCodigo());
            }
        }
    }
}
