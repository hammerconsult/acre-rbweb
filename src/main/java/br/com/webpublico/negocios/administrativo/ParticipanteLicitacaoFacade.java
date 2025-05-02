package br.com.webpublico.negocios.administrativo;

import br.com.webpublico.entidades.Licitacao;
import br.com.webpublico.entidades.LicitacaoFornecedor;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.LicitacaoFacade;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ParticipanteLicitacaoFacade extends AbstractFacade<LicitacaoFornecedor> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private LicitacaoFacade licitacaoFacade;
    @EJB
    private RetiradaEditalFacade retiradaEditalFacade;

    public ParticipanteLicitacaoFacade() {
        super(LicitacaoFornecedor.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LicitacaoFacade getLicitacaoFacade() {
        return licitacaoFacade;
    }

    public RetiradaEditalFacade getRetiradaEditalFacade() {
        return retiradaEditalFacade;
    }

    @Override
    public LicitacaoFornecedor recuperar(Object id) {
        LicitacaoFornecedor entity = super.recuperar(id);
        Hibernate.initialize(entity.getDocumentosFornecedor());
        Hibernate.initialize(entity.getListaDeStatus());
        return entity;
    }

    public LicitacaoFornecedor buscarLicitacaoFornecedor(Licitacao licitacao, Pessoa fornecedor) {
        String sql = " select lf.* from licitacaofornecedor lf where lf.licitacao_id = :idLicitacao and lf.empresa_id = :idFornecedor ";
        Query q = em.createNativeQuery(sql, LicitacaoFornecedor.class);
        q.setParameter("idLicitacao", licitacao.getId());
        q.setParameter("idFornecedor", fornecedor.getId());
        q.setMaxResults(1);
        try {
            return (LicitacaoFornecedor) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<LicitacaoFornecedor> buscarPorLicitacao(Long idLicitacao, String filtro) {
        String sql =
            "  select lf.* from licitacaofornecedor lf " +
                " inner join pessoa p on p.id = lf.empresa_id " +
                " left join pessoajuridica pj on pj.id = p.id" +
                " left join pessoafisica pf on pf.id = p.id" +
                " where (lower(translate(pj.razaoSocial,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:filtro,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')" +
                "    or lower(translate(pj.nomeFantasia,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:filtro,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')" +
                "    or replace(replace(replace(pj.cnpj,'.',''),'-',''),'/','') like :filtro " +
                "    or lower(translate(pf.nome,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:filtro,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')" +
                "    or replace(replace(replace(pf.cpf,'.',''),'-',''),'/','') like :filtro) " +
                " and lf.licitacao_id = :idLicitacao " +
                " order by lf.codigo ";
        Query q = em.createNativeQuery(sql, LicitacaoFornecedor.class);
        q.setParameter("idLicitacao", idLicitacao);
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public Integer getMaiorCodigoLicitacaoFornecedor(List<LicitacaoFornecedor> participantes) {
        Integer maiorCodigo = 0;
        for (LicitacaoFornecedor lf : participantes) {
            if (lf.getCodigo() > maiorCodigo) {
                maiorCodigo = lf.getCodigo();
            }
        }
        return maiorCodigo + 1;
    }
}
