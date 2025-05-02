/*
 * Codigo gerado automaticamente em Tue Feb 22 08:55:15 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoEndereco;
import br.com.webpublico.util.StringUtil;
import org.apache.commons.lang.StringUtils;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class EnderecoFacade extends AbstractFacade<Endereco> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EnderecoFacade() {
        super(Endereco.class);
    }

    public List<EnderecoCorreio> retornaDoisPrincipais(Pessoa p) {
        String consulta = "";
        StringBuilder hql = new StringBuilder("SELECT E.* FROM ENDERECOCORREIO E INNER JOIN ")
                .append("PESSOA_ENDERECOCORREIO PE ON E.ID = PE.ENDERECOSCORREIO_ID INNER JOIN PESSOA P ")
                .append("ON PE.PESSOA_ID = P.ID WHERE PE.PESSOA_ID = :id ");
        if ((p instanceof PessoaJuridica)) {
            consulta = "COMERCIAL";
        } else {
            consulta = "RESIDENCIAL";
        }
        hql.append(" AND E.TIPOENDERECO IN ('").append(consulta).append("','CORRESPONDENCIA') ");
        Query q = em.createNativeQuery(hql.toString(), EnderecoCorreio.class);
        q.setParameter("id", p.getId());
        return q.getResultList();

        //para montar a clausula where fazer o sql que esta no relatorio, que mergeEntity os enderecos
        // que são correspondecias, caso essa query não seja vazia, passar a clausula where com o tipo CORRESPONDECIA
        //, do contrario não filtrar pelo tipo de endereço
    }

    public List<EnderecoCorreio> retornaEnderecoCorreioDaPessoa(Pessoa p) {
        List<EnderecoCorreio> enderecos = retornaPrincipalEPorTipoEndereco(p);
        if (!enderecos.isEmpty()) {
            return enderecos;
        }
        enderecos.clear();
        enderecos = retornaPorTipoEndereco(p);
        if (!enderecos.isEmpty()) {
            return enderecos;
        }
        enderecos.clear();
        enderecos = retornaEnderecoCorreioPrincipal(p);
        if (!enderecos.isEmpty()) {
            return enderecos;
        }
        enderecos.clear();
        enderecos = retornaPrimeiroQueEncontrar(p);
        if (!enderecos.isEmpty()) {
            return enderecos;
        }
        return new ArrayList<>();
    }

    public List<EnderecoCorreio> retornaPrincipalEPorTipoEndereco(Pessoa p) {
        String consulta = "";
        StringBuilder sql = new StringBuilder("SELECT E.* FROM ENDERECOCORREIO E INNER JOIN ")
                .append("PESSOA_ENDERECOCORREIO PE ON E.ID = PE.ENDERECOSCORREIO_ID INNER JOIN PESSOA P ")
                .append("ON PE.PESSOA_ID = P.ID WHERE PE.PESSOA_ID = :id ");
        if ((p instanceof PessoaJuridica)) {
            consulta = "COMERCIAL";
        } else {
            consulta = "RESIDENCIAL";
        }
        sql.append(" AND E.TIPOENDERECO IN ('").append(consulta).append("') ")
                .append(" AND E.PRINCIPAL = 1");
        Query q = em.createNativeQuery(sql.toString(), EnderecoCorreio.class);
        q.setParameter("id", p.getId());
        return q.getResultList();
    }

    public List<EnderecoCorreio> retornaPorTipoEndereco(Pessoa p) {
        String consulta = "";
        StringBuilder sql = new StringBuilder("SELECT E.* FROM ENDERECOCORREIO E INNER JOIN ")
                .append("PESSOA_ENDERECOCORREIO PE ON E.ID = PE.ENDERECOSCORREIO_ID INNER JOIN PESSOA P ")
                .append("ON PE.PESSOA_ID = P.ID WHERE PE.PESSOA_ID = :id ");
        if ((p instanceof PessoaJuridica)) {
            consulta = "COMERCIAL";
        } else {
            consulta = "RESIDENCIAL";
        }
        sql.append(" AND E.TIPOENDERECO IN ('").append(consulta).append("')");
        Query q = em.createNativeQuery(sql.toString(), EnderecoCorreio.class);
        q.setParameter("id", p.getId());
        return q.getResultList();
    }

    public List<EnderecoCorreio> retornaEnderecoCorreioPrincipal(Pessoa p) {
        StringBuilder sql = new StringBuilder("SELECT E.* FROM ENDERECOCORREIO E INNER JOIN ")
                .append("PESSOA_ENDERECOCORREIO PE ON E.ID = PE.ENDERECOSCORREIO_ID INNER JOIN PESSOA P ")
                .append("ON PE.PESSOA_ID = P.ID WHERE PE.PESSOA_ID = :id ")
                .append(" AND E.PRINCIPAL = 1");
        Query q = em.createNativeQuery(sql.toString(), EnderecoCorreio.class);
        q.setParameter("id", p.getId());
        return q.getResultList();
    }

    public List<EnderecoCorreio> retornaPrimeiroQueEncontrar(Pessoa p) {
        StringBuilder sql = new StringBuilder("SELECT E.* FROM ENDERECOCORREIO E INNER JOIN ")
                .append("PESSOA_ENDERECOCORREIO PE ON E.ID = PE.ENDERECOSCORREIO_ID INNER JOIN PESSOA P ")
                .append("ON PE.PESSOA_ID = P.ID WHERE PE.PESSOA_ID = :id ");
        Query q = em.createNativeQuery(sql.toString(), EnderecoCorreio.class);
        q.setParameter("id", p.getId());
        return q.getResultList();
    }

    public Endereco retornaPrincipal(Pessoa p) {
        StringBuilder hql = new StringBuilder("SELECT E.ID FROM ENDERECO E INNER JOIN ")
                .append("PESSOA_ENDERECO PE ON E.ID = PE.ENDERECOS_ID INNER JOIN PESSOA P ")
                .append("ON PE.PESSOA_ID = P.ID WHERE PE.PESSOA_ID = :id ")
                .append(" AND E.TIPOENDERECO = 'CORRESPONDENCIA' ");

        Query q = em.createNativeQuery(hql.toString());
        q.setParameter("id", p.getId());
        q.setMaxResults(1);

        if (!q.getResultList().isEmpty()) {
            List<BigDecimal> ends = q.getResultList();
            for (BigDecimal i : ends) {
                return em.find(Endereco.class, i.longValue());
            }
        } else {
            Query q2 = em.createNativeQuery(hql.toString());
            q2.setParameter("id", p.getId());
            q2.setMaxResults(1);
            List<BigDecimal> ends = q2.getResultList();
            for (BigDecimal i : ends) {
                return em.find(Endereco.class, i.longValue());
            }
        }
        return null;
    }

    public EnderecoCorreio retornaEnderecoPrincipalCorreio(Pessoa p) {
        StringBuilder hql = new StringBuilder("SELECT E.ID FROM ENDERECO E INNER JOIN ")
                .append("PESSOA_ENDERECOCORREIO PE ON E.ID = PE.ENDERECOSCORREIO_ID INNER JOIN PESSOA P ")
                .append("ON PE.PESSOA_ID = P.ID WHERE PE.PESSOA_ID = :id  AND E.TIPOENDERECO = 'CORRESPONDENCIA' ");

        Query q = em.createNativeQuery(hql.toString());
        q.setParameter("id", p.getId());
        q.setMaxResults(1);

        if (!q.getResultList().isEmpty()) {
            List<BigDecimal> ends = q.getResultList();
            for (BigDecimal i : ends) {
                return em.find(EnderecoCorreio.class, i.longValue());
            }
        } else {
            Query q2 = em.createNativeQuery(hql.toString());
            q2.setParameter("id", p.getId());
            q2.setMaxResults(1);
            List<BigDecimal> ends = q2.getResultList();
            for (BigDecimal i : ends) {
                return em.find(EnderecoCorreio.class, i.longValue());
            }
        }
        return null;
    }

    public EnderecoCorreio retornaPrimeiroEnderecoCorreioValido(Pessoa p) {
        return retornaPrimeiroEnderecoCorreioValido(p.getId());
    }

    public EnderecoCorreio retornaPrimeiroEnderecoCorreioValido(Long idPessoa) {
        StringBuilder hql = new StringBuilder("SELECT E.ID FROM ENDERECOCORREIO E INNER JOIN ")
                .append("PESSOA_ENDERECOCORREIO PE ON E.ID = PE.ENDERECOSCORREIO_ID INNER JOIN PESSOA P ")
                .append("ON PE.PESSOA_ID = P.ID WHERE PE.PESSOA_ID = :id  AND (E.TIPOENDERECO = 'CORRESPONDENCIA' OR E.TIPOENDERECO = 'RESIDENCIAL' OR E.TIPOENDERECO = 'COMERCIAL') ");
        Query q = em.createNativeQuery(hql.toString());
        q.setParameter("id", idPessoa);
        q.setMaxResults(1);

        if (!q.getResultList().isEmpty()) {
            List<BigDecimal> ends = q.getResultList();
            for (BigDecimal i : ends) {
                return em.find(EnderecoCorreio.class, i.longValue());
            }
        } else {
            Query q2 = em.createNativeQuery(hql.toString());
            q2.setParameter("id", idPessoa);
            q2.setMaxResults(1);
            List<BigDecimal> ends = q2.getResultList();
            for (BigDecimal i : ends) {
                return em.find(EnderecoCorreio.class, i.longValue());
            }
        }
        return new EnderecoCorreio();

    }

    public Endereco umEndereco() {
        Query q = em.createQuery("from Endereco order by id");
        q.setMaxResults(1);
        //System.out.println(q.getSingleResult());
        return (Endereco) q.getSingleResult();
    }

    public List<EnderecoCorreio> enderecoPorPessoa(Pessoa pessoa) {
        if (pessoa == null || pessoa.getId() == null) {
            return new ArrayList<EnderecoCorreio>();
        }
        String hql = "select e from Pessoa p join p.enderecoscorreio e where p = :pessoa";
        Query q = em.createQuery(hql);
        q.setParameter("pessoa", pessoa);
        return q.getResultList();
    }

    public String recuperarEnderecoPessoaCMC(String atributo, CadastroEconomico ce) {
        String sql = " select " + atributo + " from vwenderecopessoa vw " +
            " where vw.pessoa_id = :idPessoa ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idPessoa", ce.getPessoa().getId());

        List<String> atributos = q.getResultList();
        return (atributos != null && !atributos.isEmpty()) ? atributos.get(0) : "";
    }

    public String montarEnderecoCadastroImobiliarioIntegracaoBB(Long idCadastro) {
        String sql = "select vw.logradouro, vw.numero, vw.bairro, 'Setor ' || vw.setor || ', Quadra ' || vw.quadra || ', Lote ' || vw.lote " +
            " from cadastroimobiliario ci " +
            " inner join vwenderecobci vw on vw.cadastroimobiliario_id = ci.id " +
            " and ci.id = :idImovel";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idImovel", idCadastro);

        List<Object[]> camposEndereco = q.getResultList();
        String enderecoBB = "";
        if (camposEndereco != null && !camposEndereco.isEmpty()) {
            Object[] campos = camposEndereco.get(0);
            if (campos != null) {
                if (campos[0] != null) {
                    enderecoBB += campos[0];
                    if (campos[1] != null && !StringUtils.isBlank((String) campos[1])) {
                        enderecoBB += (", " + campos[1]);
                    }
                    enderecoBB = StringUtil.cortaDireita(39, enderecoBB) + ";";
                }
                if (campos[2] != null) {
                    enderecoBB += StringUtil.cortaDireita(39, (String) campos[2]) + ";";
                }
                if (campos[3] != null) {
                    enderecoBB += StringUtil.cortaDireita(39, (String) campos[3]) + ";";
                }
            }
        }

        return enderecoBB;
    }
}
