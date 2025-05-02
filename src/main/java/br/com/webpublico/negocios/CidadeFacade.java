/*
 * Codigo gerado automaticamente em Wed Feb 09 11:02:22 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.nfse.util.PesquisaGenericaNfseUtil;
import br.com.webpublico.singletons.CacheRH;
import com.google.common.base.Strings;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Stateless
public class CidadeFacade extends AbstractFacade<Cidade> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private BairroFacade bairroFacade;
    @EJB
    private DistritoFacade distritoFacade;
    @EJB
    private UFFacade uFFacade;
    @EJB
    private CacheRH cacheRH;


    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void salvarNovo(Cidade entity) {
        super.salvarNovo(entity);
        Bairro b = new Bairro("Centro");
        bairroFacade.salvarNovo(b);
        initializeCache();
    }

    @Override
    public void salvar(Cidade entity) {
        super.salvar(entity);
        initializeCache();
    }

    private void initializeCache() {
        cacheRH.setCidades(null);
        cacheRH.setCidadesPorEstado(new HashMap<Long, List<Cidade>>());
    }

    public CidadeFacade() {
        super(Cidade.class);
    }

    public List<Cidade> listaCidade(UF uf) {
        String hql = "from Cidade c where c.uf =:parametro";
        Query q = em.createQuery(hql);
        q.setParameter("parametro", uf);
        return q.getResultList();
    }

    public List<Cidade> buscarCidadesPorEstado(Long idEstado) {
        String hql = "from Cidade c where c.uf.id = :parametro order by c.nome";
        Query q = em.createQuery(hql);
        q.setParameter("parametro", idEstado);
        return q.getResultList();
    }

    public boolean cidadeJaExiste(Cidade cidade) {
        String hql = "from Cidade c where c.uf= :uf and lower(c.nome) = :nome";
        if (cidade.getId() != null) {
            hql += " and c != :cidade";
        }
        Query q = em.createQuery(hql);
        q.setParameter("uf", cidade.getUf());
        q.setParameter("nome", cidade.getNome().toLowerCase().trim());
        if (cidade.getId() != null) {
            q.setParameter("cidade", cidade);
        }
        return !q.getResultList().isEmpty();
    }

    public List<Cidade> listaFiltrandoCodigoDescricao(String s) {
        Query q = em.createNativeQuery(" SELECT c.* FROM Cidade c WHERE (lower(cast(c.codigo AS VARCHAR(10))) LIKE :filtro"
            + " OR lower(c.nome) LIKE :filtro) AND rownum < 11 ", Cidade.class);
        q.setParameter("filtro", "%" + s.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public List<Cidade> consultaCidade(String uf, String parte) {
        Query q = em.createQuery("select c FROM Cidade c, UF estado where lower(estado.uf) like :uf and c.uf = estado AND lower(c.nome) like :parte");
        q.setParameter("uf", "%" + uf.toLowerCase() + "%");
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setMaxResults(50);
        return q.getResultList();
    }

    public Cidade recuperaCidadeDaLotacao(LotacaoFuncional lotacaoFuncional) {
        if (lotacaoFuncional.getUnidadeOrganizacional().getCidade() != null && lotacaoFuncional.getUnidadeOrganizacional().getUf() != null) {
            return recuperaCidadePorNomeEEstado(lotacaoFuncional.getUnidadeOrganizacional().getCidade(), lotacaoFuncional.getUnidadeOrganizacional().getUf());
        }
        return null;
    }

    public Cidade recuperaCidadePorNomeEEstado(String cidade, String uf) {
        if (Strings.isNullOrEmpty(cidade) || Strings.isNullOrEmpty(uf)) return null;

        String hql = "select c from Cidade c" +
            " inner join c.uf uf " +
            " where lower(c.nome) like :cidade " +
            " and lower(uf.uf) like :uf";

        Query q = em.createQuery(hql);
        q.setParameter("cidade", "%" + cidade.toLowerCase().trim() + "%");
        q.setParameter("uf", "%" + uf.toLowerCase().trim() + "%");
        q.setMaxResults(1);
        try {
            return (Cidade) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<Cidade> listaFiltrandoCidade(String parte) {
        String sql = " SELECT C.* FROM CIDADE C " +
            "      INNER JOIN UF ON UF.ID = C.UF_ID " +
            "      WHERE (LOWER(C.NOME) LIKE :param ) ";
        Query q = em.createNativeQuery(sql, Cidade.class);
        q.setParameter("param", "%" + parte.toLowerCase().trim() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }


    public List<Cidade> listaCidadesPorUF(String uf) {
        String sql = "select c from Cidade c"
            + " inner join  c.uf uf"
            + " where upper(uf.uf) = upper(:param) "
            + " order by c.nome ";
        Query q = getEntityManager().createQuery(sql);
        q.setParameter("param", uf);
        return q.getResultList();
    }

    public BairroFacade getBairroFacade() {
        return bairroFacade;
    }

    public DistritoFacade getDistritoFacade() {
        return distritoFacade;
    }

    public UFFacade getuFFacade() {
        return uFFacade;
    }

    public Cidade buscarPorCodigo(String codigo) {
        String hql = "from Cidade c where c.codigo = :codigo";
        Query q = em.createQuery(hql);
        q.setParameter("codigo", Integer.valueOf(codigo));
        if (!q.getResultList().isEmpty()) {
            return (Cidade) q.getResultList().get(0);
        }
        return null;
    }

    public Cidade buscarPorCodigoIBGE(String codigo) {
        String hql = "from Cidade c where c.codigoIBGE = :codigo";
        Query q = em.createQuery(hql);
        q.setParameter("codigo", Integer.valueOf(codigo));
        if (!q.getResultList().isEmpty()) {
            return (Cidade) q.getResultList().get(0);
        }
        return null;
    }

    public Page<Cidade> buscarListarApenasComCodigo(Pageable pageable, String param) {
        String hql = "from Cidade c ";
        String where = " where c.codigoIBGE is not null ";
        String hqlCount = "select count(id) " + hql;
        if (!Strings.isNullOrEmpty(param)) {
            where += "and ( LOWER(c.nome) LIKE :param or LOWER(to_char(c.codigoIBGE)) LIKE :param)";
        }
        Query q = em.createQuery(hql + where);
        Query qCount = em.createQuery(hqlCount + where);
        q = PesquisaGenericaNfseUtil.atribuirPaginacao(q, pageable);

        if (!Strings.isNullOrEmpty(param)) {
            q.setParameter("param", "%" + param.toLowerCase().trim() + "%");
            qCount.setParameter("param", "%" + param.toLowerCase().trim() + "%");
        }
        return new PageImpl<>(q.getResultList(), pageable, ((Number) qCount.getSingleResult()).intValue());
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public void importarPlanilhaCidadesIBGE(InputStream inputStream) throws IOException, InvalidFormatException {
        Workbook workbook = WorkbookFactory.create(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = sheet.iterator();
        //desconsidera o cabeçalho
        if (iterator.hasNext()) {
            iterator.next();
        }
        logger.debug("Buscando estados...");
        List<UF> estados = uFFacade.lista();
        logger.debug("Buscando cidades...");
        List<Cidade> cidades = lista();
        while (iterator.hasNext()) {
            Row row = iterator.next();
            Integer ufCodigoIBGE = new Double(row.getCell(0).getStringCellValue()).intValue();
            Integer cidadeCodigoIBGE = new Double(row.getCell(1).getStringCellValue()).intValue();
            String cidadeNome = row.getCell(2).getStringCellValue();

            logger.debug("Inserindo " + row.getRowNum() + " {} - {}", cidadeCodigoIBGE, cidadeNome);
            Cidade cidade = buscarCidadePorCodigoIBGE(cidades, cidadeCodigoIBGE);
            if (cidade == null) {
                UF uf = buscarUFPorCodigoIBGE(estados, ufCodigoIBGE);
                if (uf != null) {
                    cidade = new Cidade();
                    cidade.setUf(uf);
                    cidade.setCodigoIBGE(cidadeCodigoIBGE);
                    cidade.setCodigo(cidadeCodigoIBGE);
                    cidade.setNome(cidadeNome);
                    salvarNovo(cidade);
                } else {
                    logger.debug("UF não encontrada {}", ufCodigoIBGE);
                }
            }
        }
    }

    public UF buscarUFPorCodigoIBGE(List<UF> estados, Integer codigoIBGE) {
        if (estados == null || estados.isEmpty())
            return null;
        for (UF estado : estados) {
            if (estado.getCodigoIBGE() != null && estado.getCodigoIBGE().equals(codigoIBGE))
                return estado;
        }
        return null;
    }

    public Cidade buscarCidadePorCodigoIBGE(List<Cidade> cidades, Integer codigoIBGE) {
        if (cidades == null || cidades.isEmpty())
            return null;
        for (Cidade cidade : cidades) {
            if (cidade.getCodigoIBGE() != null && cidade.getCodigoIBGE().equals(codigoIBGE))
                return cidade;
        }
        return null;
    }

    public Cidade buscarPorCodigoIbge(String codigoIbge) {
        String hql = "from Cidade c where c.codigoIBGE = :codigoIbge ";
        Query q = em.createQuery(hql);
        q.setParameter("codigoIbge", Integer.valueOf(codigoIbge));
        if (!q.getResultList().isEmpty()) {
            return (Cidade) q.getResultList().get(0);
        }
        return null;
    }
}
