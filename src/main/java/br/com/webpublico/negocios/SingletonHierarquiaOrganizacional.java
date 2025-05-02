package br.com.webpublico.negocios;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import java.util.*;

/**
 * @author Daniel
 *         Date:   07/11/13 08:44
 */

@Singleton
public class SingletonHierarquiaOrganizacional {

    private Map<Date, TreeNode> arvoresAdministrativas = new HashMap<>();
    private Map<Date, TreeNode> arvoresOrcamentarias = new HashMap<>();
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private Logger logger = LoggerFactory.getLogger(SingletonHierarquiaOrganizacional.class);
    private Calendar cal = Calendar.getInstance();

    public TreeNode getArvoreAdministrativa(Date data) {
        return getArvore(TipoHierarquiaOrganizacional.ADMINISTRATIVA, data);
    }

    public TreeNode getArvoreOrcamentaria(Date data) {
        return getArvore(TipoHierarquiaOrganizacional.ORCAMENTARIA, data);
    }

    private TreeNode getArvore(TipoHierarquiaOrganizacional tipo, Date data) {
        Map<Date, TreeNode> mapa = getMap(tipo);
        Date dataSemHorario = removeHorarioDe(data == null ? sistemaFacade.getDataOperacao() : data);
        TreeNode retorno = mapa.get(dataSemHorario);
        if (retorno == null) {
            retorno = montaArvore(dataSemHorario, tipo);
            mapa.put(dataSemHorario, retorno);
        }
        return retorno;
    }

    private TreeNode montaArvore(Date dataFiltro, TipoHierarquiaOrganizacional tipo) {
        TreeNode raiz = new DefaultTreeNode(" ", null);
        Date data = dataFiltro == null ? sistemaFacade.getDataOperacao() : dataFiltro;
        try {
            List<HierarquiaOrganizacional> listaHO = hierarquiaOrganizacionalFacade.listaOrdenadaPorIndiceDoNo(tipo, data);
            if (!listaHO.isEmpty()) {
                arvoreMontada(raiz, listaHO);
            }
            raiz.isExpanded();
        } catch (ExcecaoNegocioGenerica e) {
            logger.error("Erro Montando Arvore de HierarquiaOrganizacional " + tipo + " com Data [" + data + "]: ", e);
        }
        return raiz;
    }

    public void arvoreMontada(TreeNode raiz, List<HierarquiaOrganizacional> listaHO) {
        TreeNode no = null;
        for (HierarquiaOrganizacional hierarquiaOrganizacional : listaHO) {
            if (hierarquiaOrganizacional.getSuperior() == null) {
                TreeNode treeNode = new DefaultTreeNode(hierarquiaOrganizacional, raiz);
                treeNode.isExpanded();
                no = treeNode;
            } else {
                TreeNode treeNode = new DefaultTreeNode(hierarquiaOrganizacional, getPai(hierarquiaOrganizacional, no));
                treeNode.isExpanded();
            }
        }
    }

    public TreeNode getPai(HierarquiaOrganizacional ho, TreeNode root) {
        HierarquiaOrganizacional no = (HierarquiaOrganizacional) root.getData();
        if (no.getSubordinada().equals(ho.getSuperior())) {
            return root;
        }
        for (TreeNode treeNodeDaVez : root.getChildren()) {
            TreeNode treeNode = getPai(ho, treeNodeDaVez);
            HierarquiaOrganizacional hoRecuparado = (HierarquiaOrganizacional) treeNode.getData();
            if (hoRecuparado.getSubordinada().equals(ho.getSuperior())) {
                return treeNode;
            }
        }
        return root;
    }

    private Date removeHorarioDe(Date data) {
        cal.setTime(data);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private Map<Date, TreeNode> getMap(TipoHierarquiaOrganizacional tipo) {
        return tipo == TipoHierarquiaOrganizacional.ADMINISTRATIVA ? arvoresAdministrativas : arvoresOrcamentarias;
    }

    public void atualizaArvore(TipoHierarquiaOrganizacional tipo, Date data) {
        Date dataSemHorario = removeHorarioDe(data);
        getMap(tipo).put(removeHorarioDe(dataSemHorario), montaArvore(dataSemHorario, tipo));
    }

    public void invalidaCache(TipoHierarquiaOrganizacional tipo) {
        getMap(tipo).clear();
    }

}
