/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.DefaultTreeNodeORC;
import br.com.webpublico.entidadesauxiliares.VwHierarquiaDespesaORC;
import br.com.webpublico.enums.TipoContaDespesa;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.FacesUtil;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.TreeNode;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;

/**
 * @author reidocrime
 */
@SessionScoped
@ManagedBean(name = "componente")
public class ComponenteTreeDespesaORC implements Serializable {

    @EJB
    private DespesaORCFacade despesaORCFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private DefaultTreeNodeORC root;
    private DefaultTreeNodeORC noSelecionado;
    private DefaultTreeNodeORC orgao;
    private DefaultTreeNodeORC unidade;
    private DefaultTreeNodeORC subAcao;
    private DefaultTreeNodeORC conta;
    private DespesaORC despesaORCSelecionada;
    private String despesaSTR;
    private String codigo;
    private boolean abrePainel;
    private Boolean filtrarTipoPelasFilhas;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private HierarquiaOrganizacional hieUnidade;
    private HierarquiaOrganizacional hieOrgao;
    private VwHierarquiaDespesaORC vwHierarquiaDespesaORCTemp;
    private String hoOrgao;
    private String hoUnidade;
    private String funional;
    private String despesa;

    public ComponenteTreeDespesaORC() {
        despesaSTR = " ";
        abrePainel = false;
    }

    public void getHierarquias() {
        if (despesaORCSelecionada != null) {
            if (despesaORCSelecionada.getId() != null) {
                UnidadeOrganizacional uni = despesaORCSelecionada.getProvisaoPPADespesa().getUnidadeOrganizacional();
                hieUnidade = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(sistemaFacade.getDataOperacao(), uni, TipoHierarquiaOrganizacional.ORCAMENTARIA);
                if (hieUnidade != null) {
                    hieOrgao = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(sistemaFacade.getDataOperacao(), hieUnidade.getSuperior(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
                }
            }
        } else {
            hieUnidade = new HierarquiaOrganizacional();
            hieOrgao = new HierarquiaOrganizacional();
        }
    }

    public String getFuncional() {
        if (despesaORCSelecionada != null && despesaORCSelecionada.getId() != null) {
            VwHierarquiaDespesaORC vw = despesaORCFacade.recuperaStrDespesaPorId(despesaORCSelecionada.getId());
            return vw.getSubAcao();
        } else {
            return "";
        }
    }

    public HierarquiaOrganizacional getUnidade() {
        hieUnidade = new HierarquiaOrganizacional();
        getHierarquias();
        if (hieUnidade != null) {
            HierarquiaOrganizacional hieUni = hieUnidade;
            return hieUni;
        } else {
            return new HierarquiaOrganizacional();
        }
    }

    public HierarquiaOrganizacional getOrgao() {
        hieOrgao = new HierarquiaOrganizacional();
        getHierarquias();
        if (hieOrgao != null) {
            HierarquiaOrganizacional hieOrg = hieOrgao;
            return hieOrg;
        } else {
            return new HierarquiaOrganizacional();
        }
    }

    public Boolean getLiberaView() {
        if (despesaORCSelecionada != null && despesaORCSelecionada.getId() != null) {
            return true;
        } else {
            abrePainel = false;
            return false;
        }
    }

    public DefaultTreeNodeORC getNoSelecionado() {
        return noSelecionado;
    }

    public void setNoSelecionado(DefaultTreeNodeORC noSelecionado) {
        this.noSelecionado = noSelecionado;
    }

    private void adicionaNoOrgao(VwHierarquiaDespesaORC v) {
        orgao = insereNo(root, 0, v.getOrgao(), orgao);
    }

    private void adiconaNoUnidade(VwHierarquiaDespesaORC v) {
        unidade = insereNo(orgao, 0, v.getUnidade(), unidade);
    }

    private void adiconaNOsubAcaoSemUnidade(VwHierarquiaDespesaORC v) {
        subAcao = insereNo(root, 0, v.getSubAcao(), subAcao);
    }

    private void adiconaNOsubAcao(VwHierarquiaDespesaORC v) {
        subAcao = insereNo(unidade, 0, v.getSubAcao(), subAcao);
    }

    private void adiconaNoConta(VwHierarquiaDespesaORC v) {
        conta = insereNo(subAcao, v.getId(), v.getConta(), conta);
    }

    private void adiconaNoContaSozinha(VwHierarquiaDespesaORC v) {
        conta = insereNo(root, v.getId(), v.getConta(), conta);
    }

    public DespesaORC getDespesaORCSelecionada() {
        return despesaORCSelecionada;
    }

    public void setDespesaORCSelecionada(DespesaORC despesaORCSelecionada) {
        this.despesaORCSelecionada = despesaORCSelecionada;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
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

    public String getDespesaSTR() {
        return despesaSTR;
    }

    public void setDespesaSTR(String despesaSTR) {
        this.despesaSTR = despesaSTR;
    }

    public void setaPainel() {
        abrePainel = true;
        root = null;
    }

    public void getSelecionaNo(NodeSelectEvent e) {
        noSelecionado = (DefaultTreeNodeORC) e.getTreeNode();
        if (noSelecionado != null) {
            if (noSelecionado.getId() > 0) {
                despesaORCSelecionada = despesaORCFacade.recuperar(noSelecionado.getId());
                codigo = despesaORCSelecionada.getProvisaoPPADespesa().getContaDeDespesa().getCodigo();
            } else {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Nó selecionado inválido", "Só é possivel selecionar o último nível da Árvore");
                FacesContext.getCurrentInstance().addMessage("Formulario:idArvore", msg);
            }
        }
        despesaSTR = noSelecionado.getData().toString();
        abrePainel = false;
        getHierarquias();
    }

    public DefaultTreeNodeORC getRoot() {
        return root;
    }

    public void setRoot(DefaultTreeNodeORC root) {
        this.root = root;
    }

    public TreeNode montaArvore(TipoContaDespesa tipo, Boolean filtrarUnidade, Boolean filtrarTipoPelasFilhas) {
        this.filtrarTipoPelasFilhas = filtrarTipoPelasFilhas;
        try {
            if (root == null) {
                List<VwHierarquiaDespesaORC> listaHierarquia = buscaDadosParaArvore(filtrarUnidade, tipo);
                if (listaHierarquia.isEmpty()) {
                    return root;
                }
                noSelecionado = new DefaultTreeNodeORC();
                root = new DefaultTreeNodeORC(0l, "Despesas Orçamentaria", null);
                for (VwHierarquiaDespesaORC v : listaHierarquia) {
                    if (!filtrarUnidade) {
                        adicionaNoOrgao(v);
                        adiconaNoUnidade(v);
                        adiconaNOsubAcao(v);
                        adiconaNoConta(v);
                    } else {
                        if (listaHierarquia.size() == 1) {
                            adiconaNoContaSozinha(v);
                        } else {
                            adiconaNOsubAcaoSemUnidade(v);
                            adiconaNoConta(v);
                        }
                    }
                }
                root.setExpanded(false);
            }
            return root;
        } catch (Exception e) {
            FacesUtil.addError("Erro! ", e.getMessage());
        }

        return root;
    }

    private List<VwHierarquiaDespesaORC> buscaDadosParaArvore(Boolean filtrarUnidade, TipoContaDespesa tipo) {
        try {
            Exercicio exercicio = sistemaControlador.getExercicioCorrente();

            if (filtrarUnidade) {
                UnidadeOrganizacional und = sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente();
                if (und.getId() != null) {
                    return despesaORCFacade.descricaoDoCodigo(sistemaControlador.getDataOperacao(), tipo, und, exercicio, codigo, filtrarTipoPelasFilhas);
                }
            } else {
                return despesaORCFacade.descricaoDoCodigo(sistemaControlador.getDataOperacao(), tipo, null, exercicio, codigo, filtrarTipoPelasFilhas);
            }
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Erro ao tentar recuperar a hierarquia. " + e.getMessage());
        }
        return null;
    }

    public void recuperaDespesaORCPorCodigo(TipoContaDespesa tipo, Boolean filtrarUnidade) {
        codigo = codigo.trim();
        if (!codigo.equals("")) {
            VwHierarquiaDespesaORC vw = null;
            if (filtrarUnidade) {
                vw = despesaORCFacade.recuperaStrDespesa(codigo, tipo, sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente());
            } else {
                vw = despesaORCFacade.recuperaStrDespesa(codigo, tipo, null);
            }
            if (vw != null && vw.getId() != null) {
                despesaSTR = vw.getConta();
                despesaORCSelecionada = despesaORCFacade.recuperar(vw.getId());
            } else {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Código Reduzido não encontrado", "");
                FacesContext.getCurrentInstance().addMessage("Formulario:idString", msg);
            }
        }
    }

    public boolean isAbrePainel() {
        return abrePainel;
    }

    public void setAbrePainel(boolean abrePainel) {
        this.abrePainel = abrePainel;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public HierarquiaOrganizacional getHieOrgao() {
        return hieOrgao;
    }

    public void setHieOrgao(HierarquiaOrganizacional hieOrgao) {
        this.hieOrgao = hieOrgao;
    }

    public HierarquiaOrganizacional getHieUnidade() {
        return hieUnidade;
    }

    public void setHieUnidade(HierarquiaOrganizacional hieUnidade) {
        this.hieUnidade = hieUnidade;
    }

    public void recuperarValores(DespesaORC despesa) throws ExcecaoNegocioGenerica {
        if (despesa != null) {
            if (vwHierarquiaDespesaORCTemp == null) {
                vwHierarquiaDespesaORCTemp = despesaORCFacade.recuperaVwDespesaOrc(despesa, sistemaControlador.getDataOperacao());
            }
        }
    }

    public String getFunionalStr() {
        if (vwHierarquiaDespesaORCTemp != null) {
            String toReturn = vwHierarquiaDespesaORCTemp.getSubAcao();
            return toReturn;
        }
        return null;
    }

    public String getHoOrgao() {
        if (vwHierarquiaDespesaORCTemp != null) {
            String toReturn = vwHierarquiaDespesaORCTemp.getOrgao();
            return toReturn;
        }
        return null;
    }

    public String getHoUnidade() {
        if (vwHierarquiaDespesaORCTemp != null) {
            String toReturn = vwHierarquiaDespesaORCTemp.getUnidade();
            return toReturn;
        }
        return null;
    }

    public String getDespesa() {
        if (vwHierarquiaDespesaORCTemp != null) {
            String toReturn = vwHierarquiaDespesaORCTemp.getConta();
            return toReturn;
        }
        return null;
    }

    public String recuperaMsg(TipoContaDespesa tipo, Boolean filtrarUnidade) {
        String retorno = "Não foi encontrado nenhum registro para o exercício " + sistemaControlador.getExercicioCorrenteAsInteger();
        if (tipo != null) {
            retorno += " e para o Tipo de Conta de Despesa " + tipo.getDescricao();
        }
        if (filtrarUnidade) {
            retorno += " e para a Unidade Organizacional " + sistemaControlador.getUnidadeOrganizacionalOrcamentoCorrente();
        }
        return retorno;
    }

    public void setaNullRoot() {
        root = null;
    }

    public Boolean getFiltrarTipoPelasFilhas() {
        return filtrarTipoPelasFilhas;
    }

    public void setFiltrarTipoPelasFilhas(Boolean filtrarTipoPelasFilhas) {
        this.filtrarTipoPelasFilhas = filtrarTipoPelasFilhas;
    }
}
