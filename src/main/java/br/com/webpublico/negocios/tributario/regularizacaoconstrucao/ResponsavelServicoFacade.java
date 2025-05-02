package br.com.webpublico.negocios.tributario.regularizacaoconstrucao;

import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.ResponsavelServico;
import br.com.webpublico.negocios.AbstractFacade;
import org.hibernate.Hibernate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class ResponsavelServicoFacade extends AbstractFacade<ResponsavelServico> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public ResponsavelServicoFacade() {
        super(ResponsavelServico.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void preSave(ResponsavelServico entidade) {
        entidade.realizarValidacoes();
    }

    public List<ResponsavelServico> buscarResponsavelServicoPorPessoaAndArt(String parte) {
        return em.createNativeQuery(" select rs.* " +
                "   from responsavelservico rs " +
                "  left join pessoafisica pf on pf.id = rs.pessoa_id " +
                "  left join pessoajuridica pj on pj.id = rs.pessoa_id " +
                " where lower(trim(coalesce(pf.cpf, pj.cnpj))) like :parte " +
                "    or lower(trim(coalesce(pf.nome, pj.razaosocial))) like :parte ",
                ResponsavelServico.class)
            .setParameter("parte", "%" + parte.trim().toLowerCase())
            .getResultList();
    }
}
