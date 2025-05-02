/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.RevisaoAuditoria;
import org.hibernate.envers.RevisionListener;

/**
 * @author Munif
 */
public class OuvinteAuditoria implements RevisionListener {

    @Override
    public void newRevision(Object revisaoAuditoria) {
        RevisaoAuditoria ra = (RevisaoAuditoria) revisaoAuditoria;
        ra.setIp(SistemaFacade.obtemIp());
        ra.setUsuario(SistemaFacade.obtemLogin());
    }
}
