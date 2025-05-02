package br.com.webpublico.controle;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.negocios.SingletonHierarquiaOrganizacional;
import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.TreeNode;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;


@ViewScoped
@ManagedBean
public class ArvoreHierarquiaAdministrativaControlador implements Serializable {
    @EJB
    private SingletonHierarquiaOrganizacional singletonHO;

    private TreeNode rootAdm;
    private TreeNode[] selectedNodes;
    private List<HierarquiaOrganizacional> hoSelecionadas;

    public void carregarArvore(Boolean marcarTodosAoAtualizar) {
        rootAdm = singletonHO.getArvoreAdministrativa(null);
        if (marcarTodosAoAtualizar) {
            selecionarTodosTreeNodes(true);
        } else if (hoSelecionadas.isEmpty()) {
            selecionarTodosTreeNodes(false);
        }
    }

    public TreeNode getRootAdm() {
        return rootAdm;
    }

    public void setRootAdm(TreeNode rootAdm) {
        this.rootAdm = rootAdm;
    }

    public TreeNode[] getSelectedNodes() {
        return selectedNodes;
    }

    public void setSelectedNodes(TreeNode[] selectedNodes) {
        this.selectedNodes = selectedNodes;
    }

    public void carregarListaDeSelecionados() {
        hoSelecionadas.clear();
        atualizarHoSelecionadas(hoSelecionadas);
    }

    private void atualizarHoSelecionadas(List<HierarquiaOrganizacional> hoSelecionadas) {
        for (TreeNode node : selectedNodes) {
            hoSelecionadas.add((HierarquiaOrganizacional) node.getData());
        }
    }

    private void selecionarTodosTreeNodes(Boolean valor) {
        setSelectedNodes(new TreeNode[]{rootAdm});
        rootAdm.setSelected(valor);
        selecionarFilhos(rootAdm, valor);
        RequestContext.getCurrentInstance().update(":hierarquiaOrganizacionalTree");
    }

    private void selecionarFilhos(TreeNode pai,Boolean valor) {
        if (pai.getChildCount() != 0) {
            pai.getChildren().forEach(child -> {
                selecionarFilhos(child, valor);
                child.setSelected(valor) ;
            });
        }
    }

    public void setAttributos(List<HierarquiaOrganizacional> hoSelecionadas) {
        setHoSelecionadas(hoSelecionadas);
    }

    public List<HierarquiaOrganizacional> getHoSelecionadas() {
        return hoSelecionadas;
    }

    public void setHoSelecionadas(List<HierarquiaOrganizacional> hoSelecionadas) {
        this.hoSelecionadas = hoSelecionadas;
    }
}
