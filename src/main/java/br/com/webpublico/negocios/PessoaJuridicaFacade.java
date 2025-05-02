/*
 * Codigo gerado automaticamente em Tue Feb 15 14:19:22 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.PessoaJuridica;
import br.com.webpublico.enums.PerfilEnum;
import br.com.webpublico.enums.TipoItemSindicato;
import br.com.webpublico.nfse.domain.dtos.ConsultaReceita;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.StringUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.RequestParam;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;

@Stateless
public class PessoaJuridicaFacade extends AbstractFacade<PessoaJuridica> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private PessoaFacade pessoaFacade;

    public PessoaJuridicaFacade() {
        super(PessoaJuridica.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    @Override
    public PessoaJuridica recuperar(Object id) {
        PessoaJuridica pf = em.find(PessoaJuridica.class, id);
        pf.getTelefones().size();
        pf.getEnderecos().size();
        pf.getContaCorrenteBancPessoas().size();
        pf.getPessoaCNAE().size();
        pf.getSociedadePessoaJuridica().size();
        return pf;
    }

    public boolean hasCnpj(String cnpj) {
        String hql = "from PessoaJuridica pj where replace(replace(replace(pj.cnpj,'.',''),'-',''),'/','') = :cnpj";

        Query q = em.createQuery(hql);
        q.setParameter("cnpj", cnpj);
        return !q.getResultList().isEmpty();

    }

    public List<PessoaJuridica> listaFiltrandoRazaoSocialCNPJ(String s) {
        Query q = em.createQuery(" from PessoaJuridica pj"
            + " where (lower(pj.razaoSocial) like :filtro) "
            + " or (pj.cnpj like :filtro) "
            + " or (replace(replace(replace(pj.cnpj,'.',''),'-',''),'/','') like :filtro) ");
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(10);

        return q.getResultList();
    }

    public List<PessoaJuridica> listaFiltrandoPorPerfil(PerfilEnum perfil, String s) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select pj from PessoaJuridica pj");
        sql.append("  inner join pj.perfis pfl");
        sql.append(" where pfl = :perfil");
        sql.append("   and ((lower(pj.razaoSocial) like :filtro) or (pj.cnpj like :filtro) or (replace(replace(replace(pj.cnpj,'.',''),'-',''),'/','') like :filtro))");

        Query q = em.createQuery(sql.toString());
        q.setParameter("perfil", perfil);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");

        return q.getResultList();
    }

    public PessoaJuridica recuperaPessoaSindicato(ContratoFP contratoFP, TipoItemSindicato tipoItemSindicato) {
        try {
            String hql = "select pj from SindicatoVinculoFP sv "
                + " inner join sv.vinculoFP v "
                + " inner join sv.sindicato s "
                + " inner join s.pessoaJuridica pj "
                + " inner join s.itensSindicatos item "
                + " where v = :contratoFP "
                + " and item.tipoItemSindicato = :tipoItemSindicato ";

            Query q = em.createQuery(hql);
            q.setParameter("contratoFP", contratoFP);
            q.setParameter("tipoItemSindicato", tipoItemSindicato);

            PessoaJuridica pj = (PessoaJuridica) q.getSingleResult();
            return pj;
        } catch (NoResultException nre) {
            return new PessoaJuridica();
        }
    }

    public PessoaJuridica recuperaPessoaSindicatoAno(ContratoFP contratoFP, TipoItemSindicato tipoItemSindicato, Integer ano) {
        try {
            Date dataFinal = DataUtil.montaData(31, 11, ano).getTime();

            String hql = "select pj from SindicatoVinculoFP sv "
                + " inner join sv.vinculoFP v "
                + " inner join sv.sindicato s "
                + " inner join s.pessoaJuridica pj "
                + " inner join s.itensSindicatos item "
                + " where v = :contratoFP "
                + " and item.tipoItemSindicato = :tipoItemSindicato "
                + " and (extract(year from sv.inicioVigencia) <= :ano and extract(year from coalesce(sv.finalVigencia, :dataFinal)) >= :ano) "
                + " order by sv.inicioVigencia desc";

            Query q = em.createQuery(hql);
            q.setParameter("contratoFP", contratoFP);
            q.setParameter("tipoItemSindicato", tipoItemSindicato);
            q.setParameter("ano", ano);
            q.setParameter("dataFinal", dataFinal);
            q.setMaxResults(1);

            PessoaJuridica pj = (PessoaJuridica) q.getSingleResult();
            return pj;
        } catch (NoResultException nre) {
            return null;
        }
    }

    public PessoaJuridica recuperaPessoaJuridicaPorCNPJ(String cnpj) {
        String hql = " from PessoaJuridica pj " +
            "  where (replace(replace(replace(pj.cnpj,'.',''),'-',''),'/','')) = :cnpj";
        Query q = em.createQuery(hql);
        q.setParameter("cnpj", StringUtil.retornaApenasNumeros(cnpj.trim()));
        q.setMaxResults(1);

        if (q.getResultList().isEmpty()) {
            return null;
        } else {
            return (PessoaJuridica) q.getSingleResult();
        }
    }


    public ConsultaReceita buscarPessoaJuridicaNaReceita(@RequestParam(value = "cnpj") String cnpj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            cnpj = cnpj.replaceAll("\\.", "").replaceAll("\\/", "").replaceAll("\\-", "");
            if (cnpj.length() != 14) {
                logger.debug("CNPJ menor que o esperado " + cnpj);
                return null;
            }
            String url = "http://www.receitaws.com.br/v1/cnpj/" + cnpj;
            logger.debug("Vai buscar CNPJ no WS: " + url);
            String html = getHTML(url);
            return mapper.readValue(html, ConsultaReceita.class);

        } catch (Exception e) {
            logger.error("Erro ao consultar na receita ", e);
            return null;
        }
    }

    private String getHTML(String urlToRead) {
        String line;
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(urlToRead);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}
