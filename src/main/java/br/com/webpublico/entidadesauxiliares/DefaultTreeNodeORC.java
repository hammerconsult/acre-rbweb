/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 * @author reidocrime
 */
public class DefaultTreeNodeORC extends DefaultTreeNode {

    private long id;

    public DefaultTreeNodeORC() {
        super();
    }

    public DefaultTreeNodeORC(String type, Object data, TreeNode parent) {
        super(type, data, parent);
    }

    public DefaultTreeNodeORC(Long id, Object data, TreeNode parent) {
        super(data, parent);
        this.id = id;
    }

    public DefaultTreeNodeORC(Object data, TreeNode parent) {
        super(data, parent);
    }

    public long getId() {
        return id;
    }

    public void setId(long idObject) {
        this.id = idObject;
    }
}
