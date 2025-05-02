/*
 * Codigo gerado automaticamente em Fri Feb 11 13:51:59 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.DiretorSecretarioSeker;
import br.com.webpublico.entidades.UsuarioAlvaraSeker;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class DiretorSecretarioSekerFacade extends AbstractFacade<DiretorSecretarioSeker> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public DiretorSecretarioSekerFacade() {
        super(DiretorSecretarioSeker.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }


    public List<DiretorSecretarioSeker> buscarDiretorSecretarioSeker() {
        return em.createNativeQuery("select * from DIRETORSECRETARIOSEKER", DiretorSecretarioSeker.class).getResultList();
    }
}
