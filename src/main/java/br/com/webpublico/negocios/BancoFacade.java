/*
 * Codigo gerado automaticamente em Fri Feb 11 08:42:00 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Banco;
import br.com.webpublico.enums.Situacao;
import br.com.webpublico.util.StringUtil;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class BancoFacade extends AbstractFacade<Banco> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public BancoFacade() {
        super(Banco.class);
    }

    public boolean verificarNumeroBanco(String numero) {
        String sql = "select * from banco b where b.numeroBanco = :numeroBanco ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("numeroBanco", numero);
        if (!q.getResultList().isEmpty()) {
            return true;
        }
        return false;
    }

    public Banco retornaBancoPorNumero(String numero) {
        String hql = "from Banco b where trim(b.numeroBanco) = :numeroParametro";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("numeroParametro", numero.trim());
        if(!q.getResultList().isEmpty()){
            return (Banco) q.getResultList().get(0);
        }
        return null;
    }

    public Banco buscarBancoPorIspb(String ispb) {
        String hql = "from Banco b where trim(b.ispb) = :ispb";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("ispb", ispb.trim());
        if(!q.getResultList().isEmpty()){
            return (Banco) q.getResultList().get(0);
        }
        return null;
    }

    public Banco buscarBancoPorCNPJ(String cnpj) {
        String hql = "select b from Banco b " +
            " join b.pessoaJuridica pj " +
            " where replace(replace(replace(pj.cnpj, '.', ''), '/', ''), '-', '') = :cnpj ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("cnpj", StringUtil.retornaApenasNumeros(cnpj));
        List resultList = q.getResultList();
        return !resultList.isEmpty() ? (Banco) resultList.get(0) : null;
    }

    public List<Banco> listaBancoPorCodigoOuNome(String texto) {
        String hql = "select b from Banco b "
                + " where (b.numeroBanco = :numero "
                + " or lower(b.descricao) like :descricao) "
                + " and b.situacao = :situacao ";
        Query q = em.createQuery(hql);
        q.setParameter("numero", texto);
        q.setParameter("situacao", Situacao.ATIVO);
        q.setParameter("descricao", "%" + texto.toLowerCase() + "%");
        q.setMaxResults(10);
        List<Banco> bancos = q.getResultList();

        if (bancos == null || bancos.isEmpty()) {
            return new ArrayList<Banco>();
        }

        return bancos;
    }

    public List<Banco> listaBancoPorCodigoOuNomeNoBordero(String texto) {
        String sql = " select b.* from banco b " +
                " inner join bancoobn ban on ban.numerodobanco = b.numerobanco " +
                " where (b.numerobanco = :numero or lower(b.descricao) like :descricao ) ";
        Query q = em.createNativeQuery(sql,Banco.class);
        q.setParameter("numero", texto);
        q.setParameter("descricao", "%" + texto.toLowerCase() + "%");
        q.setMaxResults(10);
        List<Banco> bancos = q.getResultList();

        if (bancos == null || bancos.isEmpty()) {
            return new ArrayList<Banco>();
        }

        return bancos;
    }

    public List<Banco> bancosDasContasCorrenteDosVinculos() {
        String hql = "select distinct banco from VinculoFP vinculo " +
                " inner join vinculo.contaCorrente.agencia.banco banco " +
                " order by banco.numeroBanco";

        Query q = em.createQuery(hql);
        return q.getResultList();
    }

    public boolean isBancoCaixaEconomica(Banco banco) {
        return banco.getNumeroBanco().equals("104");
    }
}
