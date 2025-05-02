package br.com.webpublico.negocios;

import br.com.webpublico.entidades.DispensaDeLicitacao;
import br.com.webpublico.entidades.FornecedorDispensaDeLicitacao;
import org.hibernate.Hibernate;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class FornecedorDispensaDeLicitacaoFacade extends AbstractFacade<FornecedorDispensaDeLicitacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public FornecedorDispensaDeLicitacaoFacade() { super(FornecedorDispensaDeLicitacao.class); }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public FornecedorDispensaDeLicitacao recuperar(Object id) {
        FornecedorDispensaDeLicitacao f = super.recuperar(id);
        Hibernate.initialize(f.getDocumentos());
        return f;
    }

    public List<FornecedorDispensaDeLicitacao> buscarFornecedoresDispensaLicitacao(DispensaDeLicitacao dispensa, String filtro) {
        String sql = "select forn.* from DISPENSADELICITACAO DL  " +
            "    inner join FORNECEDORDISPLIC forn on DL.ID = forn.DISPENSADELICITACAO_ID  " +
            "    inner join pessoa p on forn.PESSOA_ID = p.ID  " +
            "    left join pessoafisica pf on p.ID = pf.ID  " +
            "    left join pessoajuridica pj on p.ID = pj.ID  " +
            " where (lower(translate(pj.razaoSocial,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:filtro,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')" +
            "    or lower(translate(pj.nomeFantasia,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:filtro,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')" +
            "    or replace(replace(replace(pj.cnpj,'.',''),'-',''),'/','') like :filtro" +
            "    or (lower(translate(pf.nome,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:filtro,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')" +
            "    or replace(replace(replace(pf.cpf,'.',''),'-',''),'/','') like :filtro))" +
            " and DL.ID = :idDispensa ";
        Query q = em.createNativeQuery(sql, FornecedorDispensaDeLicitacao.class);
        q.setParameter("idDispensa", dispensa.getId());
        q.setParameter("filtro", "%" + filtro.toLowerCase() + "%");
        return q.getResultList();
    }

}
