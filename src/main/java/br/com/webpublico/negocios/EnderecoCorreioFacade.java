package br.com.webpublico.negocios;

import br.com.webpublico.entidades.EnderecoCorreio;
import br.com.webpublico.entidades.Pessoa;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author daniel
 */
@Stateless
public class EnderecoCorreioFacade extends AbstractFacade<EnderecoCorreio> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public EnderecoCorreioFacade() {
        super(EnderecoCorreio.class);

    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<EnderecoCorreio> retornaEnderecoCorreioPorCep(String parte) {
        Query q = em.createQuery("from EnderecoCorreio e where e.cep like :cep");
        q.setParameter("cep", parte.trim() + "%");
        return q.getResultList();
    }

    public List<EnderecoCorreio> recuperaEnderecosPessoa(Pessoa pessoa) {
        List<EnderecoCorreio> toReturn = new ArrayList<>();
        String hql = "select endereco from Pessoa pessoa"
            + " inner join pessoa.enderecoscorreio endereco"
            + " where pessoa = :pessoa";
        Query q = em.createQuery(hql);
        q.setParameter("pessoa", pessoa);

        if (!q.getResultList().isEmpty()) {
            toReturn = q.getResultList();
        }

        return toReturn;
    }

    public List<String> listaFiltrandoPorBairro(String s) {
        String hql = "select distinct bairro from enderecocorreio "
            + " where lower(bairro) like :filtro and lower(localidade) like '%rio branco%' ";
        Query q = getEntityManager().createNativeQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<String> listaFiltrandoPorEndereco(String s) {
        String hql = "select distinct logradouro from enderecocorreio "
            + " where lower(logradouro) like :filtro and lower(localidade) like '%rio branco%' ";
        Query q = getEntityManager().createNativeQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<String> listaFiltrandoPorLogradouro(String s) {
        String hql = "select distinct logradouro from enderecocorreio "
            + " where lower(logradouro) like :filtro and lower(localidade) like '%rio branco%' ";
        Query q = getEntityManager().createNativeQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public BigDecimal getCodigoIBGECidade(String cidade, String uf) {
        String sql = "select cidade.codigoIBGE from cidade " +
            "inner join uf on cidade.UF_ID = uf.id " +
            "where upper(cidade.nome) = :cidade and upper(uf.UF) = :uf";
        Query q = em.createNativeQuery(sql);
        q.setParameter("cidade", cidade.trim().toUpperCase());
        q.setParameter("uf", uf.trim().toUpperCase());
        if (!q.getResultList().isEmpty()) {
            return (BigDecimal) q.getResultList().get(0);
        }
        return null;
    }

    public EnderecoCorreio buscarUltimoEnderecoDaPessoa(Long idPessoa, boolean principal) {
        String sql = "select e.* " +
            " from pessoa p " +
            "         inner join pessoa_enderecocorreio pe on p.id = pe.pessoa_id " +
            "         inner join enderecocorreio e on pe.enderecoscorreio_id = e.id " +
            " where e.principal = :principal " +
            "   and p.id = :idPessoa " +
            " order by e.id desc";
        Query q = em.createNativeQuery(sql, EnderecoCorreio.class);
        q.setParameter("idPessoa", idPessoa);
        q.setParameter("principal", principal);

        List<EnderecoCorreio> resultado = q.getResultList();
        if (resultado != null && !resultado.isEmpty()) {
            return resultado.get(0);
        }
        return null;
    }
}
