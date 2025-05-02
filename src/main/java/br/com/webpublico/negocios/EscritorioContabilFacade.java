/*
 * Codigo gerado automaticamente em Mon Feb 14 10:35:08 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.EscritorioContabil;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.nfse.domain.dtos.DadosPessoaisNfseDTO;
import br.com.webpublico.nfse.domain.dtos.EscritorioContabilNfseDTO;
import com.google.common.collect.Lists;
import org.springframework.data.domain.Pageable;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class EscritorioContabilFacade extends AbstractFacade<EscritorioContabil> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EscritorioContabilFacade() {
        super(EscritorioContabil.class);
    }

    public boolean jaExisteCodigo(EscritorioContabil escritorioContabil) {
        if (escritorioContabil == null || escritorioContabil.getCodigo() == null) {
            return false;
        }
        String hql = "from EscritorioContabil ec where ec.codigo = :codigo";
        if (escritorioContabil.getId() != null) {
            hql += " and ec <> :escritorioContabil";
        }
        Query q = em.createQuery(hql);
        q.setParameter("codigo", escritorioContabil.getCodigo());
        if (escritorioContabil.getId() != null) {
            q.setParameter("escritorioContabil", escritorioContabil);
        }
        return !q.getResultList().isEmpty();
    }

    public boolean jaExistePessoa(EscritorioContabil escritorioContabil) {
        if (escritorioContabil == null || escritorioContabil.getPessoa() == null) {
            return false;
        }
        String hql = "from EscritorioContabil ec where ec.pessoa = :pessoa";
        if (escritorioContabil.getId() != null) {
            hql += " and ec <> :escritorioContabil";
        }
        Query q = em.createQuery(hql);
        q.setParameter("pessoa", escritorioContabil.getPessoa());
        if (escritorioContabil.getId() != null) {
            q.setParameter("escritorioContabil", escritorioContabil);
        }
        return !q.getResultList().isEmpty();
    }

    public boolean jaExisteCRC(EscritorioContabil escritorioContabil) {
        if (escritorioContabil == null || escritorioContabil.getCrc() == null || escritorioContabil.getCrc().isEmpty()) {
            return false;
        }
        String hql = "from EscritorioContabil ec where ec.crc = :crc";
        if (escritorioContabil.getId() != null) {
            hql += " and ec <> :escritorioContabil";
        }
        Query q = em.createQuery(hql);
        q.setParameter("crc", escritorioContabil.getCrc());
        if (escritorioContabil.getId() != null) {
            q.setParameter("escritorioContabil", escritorioContabil);
        }
        return !q.getResultList().isEmpty();
    }

    public boolean usadoEmBCE(EscritorioContabil selecionado) {
        boolean toReturn;
        if (selecionado == null || selecionado.getId() == null) {
            toReturn = false;
        }
        Query q = em.createNativeQuery("select c.migracaochave, ce.* from cadastro c"
            + " inner join cadastroeconomico ce on c.id = ce.id"
            + " where ce.escritoriocontabil_id =:escritorioContabil ", CadastroEconomico.class);
        q.setParameter("escritorioContabil", selecionado.getId());
        q.setMaxResults(1);
        toReturn = !q.getResultList().isEmpty();
        //System.out.println("RETORNO" + toReturn);

        return toReturn;
    }

    public List<Pessoa> listaPessoasSemEscritorioContabil(String filtro) {
        Query q;
        ArrayList<Pessoa> retorno = new ArrayList<Pessoa>();
        q = getEntityManager().createQuery("from PessoaFisica obj where not exists(select pessoa from EscritorioContabil ec where ec.pessoa = obj) and lower(obj.nome) like :filtro or lower(obj.cpf) like :filtro");
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        q.setMaxResults(50);
        retorno.addAll(q.getResultList());
        q = getEntityManager().createQuery("from PessoaJuridica obj where not exists(select pessoa from EscritorioContabil ec where ec.pessoa = obj) and lower(obj.razaoSocial) like :filtro or lower(obj.cnpj) like :filtro or lower(obj.nomeFantasia) like :filtro");
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        q.setMaxResults(50);
        retorno.addAll(q.getResultList());
        return retorno;
    }


    public List<EscritorioContabil> recuperarEscritoriosOrderByNome() {
        String hql = "select new EscritorioContabil(ec.id, ec.codigo, ec.crcEscritorio, ec.crc, pj.nomeFantasia, resp.nome) from EscritorioContabil ec left join ec.pessoa as pj left join ec.responsavel as resp order by ec.codigo asc";
        Query q = getEntityManager().createQuery(hql);
        return q.getResultList();
    }


    public List<EscritorioContabilNfseDTO> listaEscritorioPorNomeRazaoSocialCPFCNPJ(String parte, Pageable pageable) {

        String hql = " SELECT " +
            "  es.id, " +
            "  es.CODIGO, " +
            "  es.crc            crcContador, " +
            "  es.CRCESCRITORIO  crcEscritorio, " +
            "  pj.RAZAOSOCIAL AS nomeEscritorio, " +
            "  pj.CNPJ        AS cnpjEscritorio, " +
            "  pf.nome        AS nomeContador, " +
            "  pf.cpf         AS cpfContador " +
            "FROM " +
            "  ESCRITORIOCONTABIL es " +
            "  LEFT JOIN PESSOAJURIDICA pj ON pj.id = es.PESSOA_ID " +
            "  LEFT JOIN PESSOAfisica pf ON pf.id = es.RESPONSAVEL_ID " +
            " WHERE replace(replace(replace(pj.cnpj, '.', ''), '-', ''), '/', '') LIKE replace(replace(replace(:parte, '.', ''), '-', ''), '/', '') " +
            "      or lower(pj.razaoSocial) LIKE :parte " +
            " or replace(replace(replace(pf.cpf, '.', ''), '-', ''), '/', '') LIKE replace(replace(replace(:parte, '.', ''), '-', ''), '/', '') " +
            "      or lower(pf.nome) LIKE :parte ";
        Query q = em.createNativeQuery(hql);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");

        q.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        q.setMaxResults(pageable.getPageSize());
        List<Object[]> objs = q.getResultList();
        List<EscritorioContabilNfseDTO> retorno = Lists.newLinkedList();
        for (Object[] obj : objs) {
            EscritorioContabilNfseDTO dto = new EscritorioContabilNfseDTO();
            dto.setId(((Number) obj[0]).longValue());
            dto.setCodigo(((Number) obj[1]).longValue());
            if (obj[2] != null)
                dto.setCrcContador(((String) obj[2]));
            if (obj[3] != null)
                dto.setCrcEscritorio(((String) obj[3]));
            if (obj[4] != null && obj[5] != null)
                dto.setDadosPessoais(new DadosPessoaisNfseDTO(((String) obj[5]), ((String) obj[4]), null, null, null));
            if (obj[6] != null && obj[7] != null)
                dto.setResponsavel(new DadosPessoaisNfseDTO(((String) obj[7]), ((String) obj[6]), null, null, null));
            retorno.add(dto);
        }
        return retorno;
    }

    public Integer contarEscritorioPorNomeRazaoSocialCPFCNPJ(String parte) {

        String hql = " SELECT count(es.id) " +
            "FROM " +
            "  ESCRITORIOCONTABIL es " +
            "  LEFT JOIN PESSOAJURIDICA pj ON pj.id = es.PESSOA_ID " +
            "  LEFT JOIN PESSOAfisica pf ON pf.id = es.RESPONSAVEL_ID " +
            " WHERE replace(replace(replace(pj.cnpj, '.', ''), '-', ''), '/', '') LIKE replace(replace(replace(:parte, '.', ''), '-', ''), '/', '') " +
            "      or lower(pj.razaoSocial) LIKE :parte " +
            " or replace(replace(replace(pf.cpf, '.', ''), '-', ''), '/', '') LIKE replace(replace(replace(:parte, '.', ''), '-', ''), '/', '') " +
            "      or lower(pf.nome) LIKE :parte ";
        Query q = em.createNativeQuery(hql);
        q.setParameter("parte", "%" + parte + "%");

        return ((BigDecimal) q.getSingleResult()).intValue();
    }
}
