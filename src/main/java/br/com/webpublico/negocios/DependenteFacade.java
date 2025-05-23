/*
 * Codigo gerado automaticamente em Thu Aug 04 08:07:43 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Dependente;
import br.com.webpublico.entidades.DependenteVinculoFP;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.rh.portal.atualizacaocadastral.CamposAlteradosPortal;
import br.com.webpublico.entidadesauxiliares.rh.sig.DependenteSigDTO;
import br.com.webpublico.negocios.rh.portal.CamposAlteradosPortalFacade;
import com.beust.jcommander.internal.Lists;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Sets;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.*;

@Stateless
public class DependenteFacade extends AbstractFacade<Dependente> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private CamposAlteradosPortalFacade camposAlteradosPortalFacade;

    public DependenteFacade() {
        super(Dependente.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public Dependente recuperar(Object id) {
        Dependente dp = em.find(Dependente.class, id);
        dp.getDependentesVinculosFPs().size();
        if (dp.getDetentorArquivoComposicao() != null) {
            dp.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        return dp;
    }

    @Override
    public void remover(Dependente entity) {
        List<CamposAlteradosPortal> campos = camposAlteradosPortalFacade.buscarCamposAlteradosPortalPorDependente(entity);
        if (campos != null && !campos.isEmpty()) {
            for (CamposAlteradosPortal campo : campos) {
                campo.setDependente(null);
                camposAlteradosPortalFacade.salvar(campo);
            }
        }
        super.remover(entity);
    }

    public List<PessoaFisica> listaDependentesPorResponsavel(Pessoa responsavel) {
        try {
            Query q = em.createQuery(" select dependentes from Dependente d "
                + " inner join d.dependente dependentes "
                + " where d.responsavel = :parametro ");
            q.setParameter("parametro", responsavel);
            return q.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<DependenteSigDTO> dependentesPorResponsavelFP(Pessoa responsavel) {
        String sql = "select  d.id," +
            "                pf.NOME," +
            "                pf.DATANASCIMENTO," +
            "                pf.cpf from dependente d " +
            "                inner join pessoafisica pf on d.DEPENDENTE_ID = pf.id " +
            " where d.RESPONSAVEL_ID = :idResponsavel";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idResponsavel", responsavel.getId());
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return getItemDependenteSigDTO(resultList);
        }
        return resultList;
    }

    private List<DependenteSigDTO> getItemDependenteSigDTO(List<Object[]> resultado) {
        Set<DependenteSigDTO> retorno = Sets.newHashSet();
        for (Object[] obj : resultado) {
            DependenteSigDTO dependenteSigDTO = new DependenteSigDTO();
            dependenteSigDTO.setId(Long.parseLong(obj[0].toString()));
            dependenteSigDTO.setNome((String) obj[1]);
            dependenteSigDTO.setDataNascimento((Date) obj[2]);
            dependenteSigDTO.setCpf((String) obj[3]);
            retorno.add(dependenteSigDTO);
        }
        return Lists.newArrayList(retorno);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<Dependente> dependentesPorResponsavelNative(Pessoa responsavel) {
        Query q = em.createNativeQuery(" select distinct d.* from Dependente d "
            + " where d.responsavel_id = :parametro ", Dependente.class);
        q.setParameter("parametro", responsavel.getId());
        List<Dependente> resultList = q.getResultList();
        for (Dependente dependente : resultList) {
            Hibernate.initialize(dependente.getDependentesVinculosFPs());
            Hibernate.initialize(dependente.getDependente().getDocumentosPessoais());
        }
        return resultList;
    }


    public List<Dependente> dependentesPorResponsavel(Pessoa responsavel) {
        Query q = em.createQuery(" select distinct d from Dependente d "
            + " inner join d.dependente dependentes "
            + " where d.responsavel = :parametro ");
        q.setParameter("parametro", responsavel);
        return q.getResultList();
    }

    public List<Dependente> buscarDependentesPorDependente(Pessoa dependente) {
        Query q = em.createQuery(" select distinct d from Dependente d "
            + " where d.dependente = :parametro ");
        q.setParameter("parametro", dependente);
        return q.getResultList();
    }

    public List<Dependente> listaFiltrandoX(String s, int inicio, int max) {
        String hql = " select distinct dependente from Dependente dependente "
            + " inner join dependente.responsavel pessoaResponsavel "
            + " inner join dependente.dependente pessoaDependente "
            + " inner join dependente.grauDeParentesco grau "
            + " left join dependente.dependentesVinculosFPs depvinc "
            + " left join depvinc.vinculoFP vinc "
            + " left join vinc.matriculaFP matr "
            + " where (lower(pessoaResponsavel.nome) like :filtro) "
            + " or (lower(pessoaDependente.nome) like :filtro) "
            + " or (lower(grau.descricao) like :filtro) "
            + " or (lower(matr.matricula) like :filtro)";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(max + 1);
        q.setFirstResult(inicio);
        return q.getResultList();
    }

    public List<Dependente> getDependentesDe(Pessoa responsavel, Date dataReferencia) {
        String hql = "select d                            " +
            "       from Dependente d                 " +
            " inner join d.dependentesVinculosFPs dvs " +
            "      where d.responsavel = :parametro   " +
            "        and :dataReferencia between dvs.inicioVigencia and coalesce(dvs.finalVigencia, :dataReferencia) order by dvs.inicioVigencia asc";
        Query q = em.createQuery(hql);
        q.setParameter("parametro", responsavel);
        q.setParameter("dataReferencia", dataReferencia);
        List<Dependente> deps = q.getResultList();
        for (Dependente d : deps) {
            d.getDependentesVinculosFPs().size();
        }
        return deps;
    }

    public List<Date> getDependentesDePessoaParaBBAtuarial(Pessoa responsavel, Date dataReferencia) {
        String hql = "select pf.dataNascimento            " +
            "       from Dependente d                 " +
            " inner join d.dependente pf              " +
            "      where d.responsavel = :parametro   " +
            "        and pf.dataNascimento <= :dataReferencia" +
            "        and trunc((months_between(:dataReferencia, to_char(pf.dataNascimento, 'dd/mm/yyyy')))/12) < 18" + // Somente dependentes menores de 18 anos
            " order by pf.dataNascimento desc";
        Query q = em.createQuery(hql);
        q.setParameter("parametro", responsavel);
        q.setParameter("dataReferencia", dataReferencia);
        return q.getResultList();
    }

    public Integer getNumeroDeDependentesDaDirf(Pessoa responsavel, Date dataReferencia) {
        String hql = "select count(d)                     " +
            "       from Dependente d                 " +
            " inner join d.dependentesVinculosFPs dvs " +
            " inner join dvs.tipoDependente tpDep" +
            "      where d.responsavel = :parametro   " +
            "        and ltrim(rtrim(tpDep.codigo)) in ('2','3','4','10')" +
            "        and :dataReferencia between dvs.inicioVigencia and coalesce(dvs.finalVigencia, :dataReferencia)" +
            "            and extract(year from :dataReferencia) >= extract (year from (dvs.inicioVigencia)) " +
            "            and extract(year from :dataReferencia) <= extract (year from coalesce(dvs.finalVigencia, :dataReferencia))";
        Query q = em.createQuery(hql);
        q.setParameter("parametro", responsavel);
        q.setParameter("dataReferencia", dataReferencia);
        q.setMaxResults(1);
        Integer resultado = Integer.parseInt("" + q.getSingleResult());
        return resultado;
    }

    public List<Dependente> buscarDependentesVinculoFP(Pessoa responsavel, Date dataReferencia) {
        String sql = "select distinct dep.* " +
            "               from dependente dep " +
            "         inner join dependentevinculofp vinculo on dep.id = vinculo.dependente_id " +
            "         inner join pessoafisica pf on dep.dependente_id = pf.id " +
            "              where dep.responsavel_id = :responsavel " +
            "                and to_date(:dataReferencia, 'dd/mm/yyyy') between vinculo.iniciovigencia " +
            "                and coalesce(vinculo.finalvigencia, to_date(:dataReferencia, 'dd/mm/yyyy')) ";

        Query q = em.createNativeQuery(sql, Dependente.class);
        q.setParameter("dataReferencia", DataUtil.getDataFormatada(dataReferencia));
        q.setParameter("responsavel", responsavel.getId());

        List<Dependente> deps = q.getResultList();
        if (!deps.isEmpty()) {
            for (Dependente dependente : deps) {
                dependente.getDependentesVinculosFPs().size();
            }
            return deps;
        }
        return Lists.newArrayList();
    }

    public boolean hasDependenteBaseIRRF(Date dataParametro, Long idDependente, List<String> tiposRenda) {
        String sql = " select pf.nome, tipo.idademaxima from dependente dep " +
            " inner join dependentevinculofp dpvinculo on dep.id = dpvinculo.dependente_id " +
            " inner join pessoafisica pf on dep.dependente_id = pf.id " +
            " inner join tipodependente tipo on dpvinculo.tipodependente_id = tipo.id " +
            " where dep.dependente_id = :idDependente " +
            " and to_date(:dataParametro, 'dd/MM/yyyy') between dpvinculo.iniciovigencia " +
            "    and coalesce(dpvinculo.finalvigencia, to_date(:dataParametro, 'dd/MM/yyyy')) " +
            " and tipo.idademaxima > (extract(year from to_date(:dataParametro, 'dd/MM/yyyy')) - (extract(year from to_date(pf.datanascimento)))) " +
            " and tipo.codigo in :tiposRenda ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("dataParametro", DataUtil.getDataFormatada(dataParametro));
        q.setParameter("tiposRenda", tiposRenda);
        q.setParameter("idDependente", idDependente);


        return !q.getResultList().isEmpty();
    }

    public List<Long> buscarDependentesVinculoFPArquivoAtuarial(Pessoa responsavel, Date dataReferencia) {
        String sql = "select distinct dep.id " +
            "               from dependente dep " +
            "         inner join dependentevinculofp vinculo on dep.id = vinculo.dependente_id " +
            "         inner join pessoafisica pf on dep.dependente_id = pf.id " +
            "              where dep.responsavel_id = :responsavel " +
            "                and to_date(:dataReferencia, 'dd/mm/yyyy') between vinculo.iniciovigencia  " +
            "                and coalesce(vinculo.finalvigencia, to_date(:dataReferencia, 'dd/mm/yyyy')) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataReferencia", DataUtil.getDataFormatada(dataReferencia));
        q.setParameter("responsavel", responsavel.getId());
        Set<Long> ids = new HashSet<>();
        for (Object o : q.getResultList()) {
            ids.add(((BigDecimal) o).longValue());
        }
        return new ArrayList<>(ids);
    }

    public DependenteVinculoFP buscarDependenteVinculoFPPorTipo(Dependente dependente, String tipoDependente, Date dataReferencia) {
        String hql = " select depVinculo from DependenteVinculoFP depVinculo " +
            " inner join depVinculo.tipoDependente tipo " +
            " where :dataReferencia between depVinculo.inicioVigencia and coalesce(depVinculo.finalVigencia, :dataReferencia) " +
            "  and tipo.codigo = :tipoDependente " +
            "  and depVinculo.dependente = :dependente ";
        Query q = em.createQuery(hql);
        q.setParameter("dependente", dependente);
        q.setParameter("dataReferencia", dataReferencia);
        q.setParameter("tipoDependente", tipoDependente);
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return (DependenteVinculoFP) resultList.get(0);
        }
        return null;
    }
}
