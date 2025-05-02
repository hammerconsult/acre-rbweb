/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.TextoFixo;
import br.com.webpublico.enums.TipoCadastroTributario;
import com.google.common.collect.Lists;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * @author Renato
 */
@Stateless
public class TextoFixoFacade extends AbstractFacade<TextoFixo> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TextoFixoFacade() {
        super(TextoFixo.class);
    }

    public List<TextoFixo> buscarTextoFixoDoTipoDeCadastro(TipoCadastroTributario tipoCadastroTributario) {
        try {
            if (tipoCadastroTributario != null) {
                Query consulta = em.createNativeQuery("select * from TextoFixo where tipoCadastroTributario = :tipo", TextoFixo.class);
                consulta.setParameter("tipo", tipoCadastroTributario.name());
                return consulta.getResultList();
            }
        } catch (Exception ex) {
            logger.error("Erro ao buscar o Texto Fixo: ", ex);
        }
        return Lists.newArrayList();
    }
}
