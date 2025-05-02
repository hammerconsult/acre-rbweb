/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.CadastroAidfControlador.OrdenarPor;
import br.com.webpublico.entidades.*;
import br.com.webpublico.viewobjects.FiltroAIDF;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author leonardo
 */
@Stateless
public class CadastroAidfFacade extends AbstractFacade<CadastroAidf> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ArquivoFacade arquivoFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CadastroAidfFacade() {
        super(CadastroAidf.class);
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public String getProximoNumeroAIDF() {
        String sql = "select max(cast(numeroaidf as NUMBER(19))) +1 from cadastroaidf";
        Query q = em.createNativeQuery(sql);
        if (q.getResultList().isEmpty()) {
            return String.valueOf(1);
        }
        return String.valueOf((BigDecimal) q.getResultList().get(0));
    }

    public boolean existeEsteNumeroAIDF(CadastroAidf aidf) {
        String sql = "select id from cadastroaidf where numeroaidf = :numero";
        if (aidf.getId() != null) {
            sql += " and id != :id";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("numero", aidf.getNumeroAidf());
        if (aidf.getId() != null) {
            q.setParameter("id", aidf.getId());
        }
        return !q.getResultList().isEmpty();
    }

    public List<CadastroAidf> listaPorCadastroEconomico(CadastroEconomico ce) {
        String hql = "from CadastroAidf ca where ca.cadastroEconomico = :ce order by ca.data desc";
        Query q = em.createQuery(hql);
        q.setParameter("ce", ce);
        return q.getResultList();
    }

    public String ultimoMaisUmNotaFiscalInicial(CadastroEconomico ce, NumeroSerie numeroSerie) {
        if (ce != null && numeroSerie != null) {
            String sql = "select max(cast(ca.notafiscalfinalautorizado as NUMBER(19))) from CadastroAIDF ca "
                    + "where ca.cadastroeconomico_id = :cadastroEconomico and ca.numeroserie_id = :numeroSerie "
                    + "and ca.statusaidf = 'ABERTO'";
            Query q = em.createNativeQuery(sql);
            q.setParameter("cadastroEconomico", ce.getId());
            q.setParameter("numeroSerie", numeroSerie.getId());

            return String.valueOf(q.getResultList().get(0) != null ? (((BigDecimal) q.getResultList().get(0)).intValue() + 1) : 1);
        } else {
            return "";
        }
    }

    public Long verificaUltimoAidfPorPessoa(CadastroAidf selecionado) {
        Query q = em.createNativeQuery("select max(c.id) from cadastroaidf c join cadastroeconomico ce "
                + "on c.cadastroeconomico_id = :cadastroEconomico "
                + "and statusaidf = 'ABERTO'");
        q.setParameter("cadastroEconomico", selecionado.getCadastroEconomico().getId());

        Long idEncontrado = q.getResultList().get(0) != null ? ((BigDecimal) q.getResultList().get(0)).longValue() : 0L;
        return idEncontrado;

    }

    @Override
    public List<CadastroAidf> lista() {
        String hql = "from CadastroAidf obj order by obj.numeroAidf";
        Query q = getEntityManager().createQuery(hql);
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<CadastroAidf> filtrar(List<OrdenarPor> ordenarPor, FiltroAIDF filtro, int maxResults, int inicio) throws IllegalArgumentException, IllegalAccessException {
        String juncao = " where ";
        String hql = "select c from CadastroAidf c ";

        if (filtro.getNumeroAIDFInicial() != null) {
            hql += juncao + " to_number(c.numeroAidf) >= :numeroAIDFInicial ";
            juncao = " and ";
        }
        if (filtro.getNumeroAIDFFinal() != null) {
            hql += juncao + " to_number(c.numeroAidf) <= :numeroAIDFFinal ";
            juncao = " and ";
        }
        if (filtro.getNumeroSerieInicial() != null && !filtro.getNumeroSerieInicial().isEmpty()) {
            hql += juncao + " c.numeroSerie.numeroSerie >= :numeroSerieInicial ";
            juncao = " and ";
        }
        if (filtro.getNumeroSerieFinal() != null && !filtro.getNumeroSerieFinal().isEmpty()) {
            hql += juncao + " c.numeroSerie.numeroSerie >= :numeroSerieFinal ";
            juncao = " and ";
        }
        if (filtro.getDataInicial() != null) {
            //System.out.println("filtro.getDataInicial() " + filtro.getDataInicial());
            hql += juncao + " c.data >= :dataInicial";
            juncao = " and ";
        }
        if (filtro.getDataFinal() != null) {
            hql += juncao + " c.data <= :dataFinal";
            juncao = " and ";
        }
        if (filtro.getCmcInicial() != null) {
            hql += juncao + " c.cadastroEconomico.inscricaoCadastral >= :inscricaoCadastralInicial";
            juncao = " and ";
        }
        if (filtro.getCmcFinal() != null) {
            hql += juncao + " c.cadastroEconomico.inscricaoCadastral <= :inscricaoCadastralFinal";
            juncao = " and ";
        }
        if (filtro.getGrafica() != null) {
            hql += juncao + " c.grafica = :grafica";
            juncao = " and ";
        }
        if (filtro.getPessoa() != null) {
            hql += juncao + " c.cadastroEconomico.pessoa = :pessoa";
            juncao = " and ";
        }

        String orderBy = " order by ";

        for (OrdenarPor ordena : ordenarPor) {
            hql += orderBy + ordena.getOrdenaPor();
            orderBy = ", ";
        }

        Query q = em.createQuery(hql);

        if (filtro.getNumeroAIDFInicial() != null) {
            q.setParameter("numeroAIDFInicial", filtro.getNumeroAIDFInicial());
        }
        if (filtro.getNumeroAIDFFinal() != null) {
            q.setParameter("numeroAIDFFinal", filtro.getNumeroAIDFFinal());
        }
        if (filtro.getNumeroSerieInicial() != null && !filtro.getNumeroSerieInicial().isEmpty()) {
            q.setParameter("numeroSerieInicial", filtro.getNumeroSerieInicial());
        }
        if (filtro.getNumeroSerieFinal() != null && !filtro.getNumeroSerieFinal().isEmpty()) {
            q.setParameter("numeroSerieFinal", filtro.getNumeroSerieFinal());
        }
        if (filtro.getDataInicial() != null) {
            q.setParameter("dataInicial", filtro.getDataInicial());
        }
        if (filtro.getDataFinal() != null) {
            q.setParameter("dataFinal", filtro.getDataFinal());
        }
        if (filtro.getCmcInicial() != null) {
            q.setParameter("inscricaoCadastralInicial", filtro.getCmcInicial().getInscricaoCadastral());
        }
        if (filtro.getCmcFinal() != null) {
            q.setParameter("inscricaoCadastralFinal", filtro.getCmcFinal().getInscricaoCadastral());
        }
        if (filtro.getGrafica() != null) {
            q.setParameter("grafica", filtro.getGrafica());
        }
        if (filtro.getPessoa() != null) {
            q.setParameter("pessoa", filtro.getPessoa());
        }

        q.setMaxResults(maxResults+3);
        q.setFirstResult(inicio);

        return q.getResultList();
    }

    public CadastroAidf recarregar(Long id) {
        CadastroAidf aidf = em.find(CadastroAidf.class, id);
        aidf.getArquivos().size();
        return aidf;
    }

    public List<CadastroAidf> listaPorParteCadastroEconomico(CadastroEconomico cadastroEconomico, String parte) {
        Query q = em.createQuery("select aidf from CadastroAidf aidf where lower(aidf.numeroAidf) like :parte and aidf.cadastroEconomico = :cmc");
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setParameter("cmc", cadastroEconomico);
        return q.getResultList();
    }

    @Override
    public CadastroAidf recuperar(Object id) {
        CadastroAidf aidf = super.recuperar(id);
        aidf.getArquivos().size();
        return aidf;
    }
}
