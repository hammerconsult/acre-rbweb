/*
 * Codigo gerado automaticamente em Sat Jul 02 11:00:55 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CategoriaPCS;
import br.com.webpublico.entidades.EnquadramentoPCS;
import br.com.webpublico.entidades.ProgressaoPCS;
import br.com.webpublico.entidadesauxiliares.EstruturaPCS;
import br.com.webpublico.entidadesauxiliares.FiltroPCS;
import br.com.webpublico.enums.TipoPCS;
import br.com.webpublico.exception.FuncoesFolhaFacadeException;
import br.com.webpublico.interfaces.EntidadePagavelRH;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;

@Stateless
public class EnquadramentoPCSFacade extends AbstractFacade<EnquadramentoPCS> {

    private static final Logger logger = LoggerFactory.getLogger(EnquadramentoPCSFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private CategoriaPCSFacade categoriaPCSFacade;
    @EJB
    private ProgressaoPCSFacade progressaoPCSFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EnquadramentoPCSFacade() {
        super(EnquadramentoPCS.class);
    }

    public EnquadramentoPCS recuperaValor(CategoriaPCS c, ProgressaoPCS p, Date vigencia) {
        EnquadramentoPCS e = null;
        Query q = em.createQuery("from EnquadramentoPCS e where e.categoriaPCS = :cat "
            + " and e.progressaoPCS = :pro "
            + " and :dataVigencia >= e.inicioVigencia "
            + " and :dataVigencia <= coalesce(e.finalVigencia,:dataVigencia) "
            + " order by e.vencimentoBase desc ");
        q.setParameter("cat", c);
        q.setParameter("pro", p);
        q.setParameter("dataVigencia", vigencia == null ? Util.getDataHoraMinutoSegundoZerado(new Date()) : vigencia);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            e = (EnquadramentoPCS) q.getSingleResult();
        }
        return e;
    }

    public BigDecimal recuperaValorDecimal(CategoriaPCS c, ProgressaoPCS p, Date vigencia) {
        Query q = em.createQuery("select e.vencimentoBase from EnquadramentoPCS e where e.categoriaPCS = :cat "
            + " and e.progressaoPCS = :pro and e.progressaoPCS.planoCargosSalarios = e.categoriaPCS.planoCargosSalarios "
            + " and :dataVigencia >= e.inicioVigencia "
            + " and :dataVigencia <= coalesce(e.finalVigencia,:dataVigencia) "
            + " order by e.vencimentoBase desc ");
        q.setParameter("cat", c);
        q.setParameter("pro", p);
        q.setParameter("dataVigencia", vigencia == null ? Util.getDataHoraMinutoSegundoZerado(new Date()) : vigencia);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (BigDecimal) q.getResultList().get(0);
        }
        return null;
    }

    public List<EnquadramentoPCS> recuperaTodosEnquadramentos(CategoriaPCS c, ProgressaoPCS p) {
        Query q = em.createQuery("select e from EnquadramentoPCS e where e.categoriaPCS = :cat "
            + " and e.progressaoPCS = :pro and e.progressaoPCS.planoCargosSalarios = e.categoriaPCS.planoCargosSalarios "

            + " order by e.vencimentoBase desc ");
        q.setParameter("cat", c);
        q.setParameter("pro", p);

        return q.getResultList();
    }

    public List<EnquadramentoPCS> lista() {
        return super.lista();
    }

    public List<EnquadramentoPCS> listaEnquadramentosVigentes(Date dataVigencia) {
        Query q = em.createQuery(" from EnquadramentoPCS e "
            + " where :dataVigencia >= e.inicioVigencia "
            + " and :dataVigencia <= coalesce(e.finalVigencia,:dataVigencia) ");
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(dataVigencia));
        return q.getResultList();
    }

    public List<EnquadramentoPCS> listaEnquadramentosVigentesTipoQuadroEfetivo(Date dataVigencia) {
        Query q = em.createQuery(" from EnquadramentoPCS e "
            + " where e.categoriaPCS.planoCargosSalarios.tipoPCS = :tipoPCS " +
            "   and e.progressaoPCS.planoCargosSalarios.tipoPCS = :tipoPCS " +
            "   and  :dataVigencia >= e.inicioVigencia "
            + " and :dataVigencia <= coalesce(e.finalVigencia,:dataVigencia) ");
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(dataVigencia));
        q.setParameter("tipoPCS", TipoPCS.QUADRO_EFETIVO);
        return q.getResultList();
    }


    public EnquadramentoPCS recuperaUltimoValor(CategoriaPCS c, ProgressaoPCS p, Date inicio, Date fim) {
        Calendar dataBusca = Calendar.getInstance();
        if (fim == null) {
            dataBusca.setTime(new Date());
        } else {
            dataBusca.setTime(fim);
        }
        dataBusca.add(Calendar.DAY_OF_YEAR, -1);
        Query q = em.createQuery("from EnquadramentoPCS e where e.categoriaPCS = :cat "
            + " and e.progressaoPCS = :pro  and "
            + " :dataVigencia >= e.inicioVigencia "
            + " and :dataVigenciaFinal <= coalesce(e.finalVigencia,:dataVigenciaFinal) "
            + " order by e.inicioVigencia desc, e.vencimentoBase desc ");
        q.setParameter("cat", c);
        q.setParameter("pro", p);
        q.setParameter("dataVigencia", dataBusca.getTime());
        q.setParameter("dataVigenciaFinal", dataBusca.getTime());
        q.setMaxResults(1);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (EnquadramentoPCS) q.getResultList().get(0);
    }


    /**
     * daqui para baixo apenas para filtro.
     */
    public List<FiltroPCS> filtraEnquadramento(ProgressaoPCS pai, CategoriaPCS categoria) {
        StringBuilder sb = new StringBuilder();
        Class clazz = FiltroPCS.class;
        sb.append("select new ").append(clazz.getCanonicalName()).append("(categoria, progressao, enquadramento)");
        sb.append(" from EnquadramentoPCS enquadramento inner join enquadramento.progressaoPCS progressao ");
        sb.append(" inner join enquadramento.categoriaPCS categoria where categoria = :cat and progressao.superior = :prog ");
        sb.append("order by categoria.descricao, progressao.descricao ");

        Query q = em.createQuery(sb.toString());
        q.setParameter("cat", categoria);
        q.setParameter("prog", pai);
        return q.getResultList();

        //        sb.append("select categoria.DESCRICAO as categoria, "
        //        pai.DESCRICAO as categoria_pai,
        //        progressao.DESCRICAO as progressao,
        //        pai_pro.DESCRICAO as progressao_pai,
        //        enquadramento.INICIOVIGENCIA,
        //        enquadramento.FINALVIGENCIA,
        //        enquadramento.VENCIMENTOBASE as valor
        //
        //  from ENQUADRAMENTOPCS enquadramento inner join PROGRESSAOPCS progressao on progressao.ID = enquadramento.PROGRESSAOPCS_ID
        //inner join CATEGORIAPCS categoria on categoria.id = enquadramento.CATEGORIAPCS_ID
        //inner join CATEGORIAPCS pai on categoria.SUPERIOR_ID = pai.ID
        //INNER JOIN PROGRESSAOPCS pai_pro on pai_pro.ID = progressao.SUPERIOR_ID
        //
        //where categoria.DESCRICAO like '%ESPECIALISTAS HABILITADOS%' and
        //    pai_pro.DESCRICAO like '%PP002 - Classe-2%'
        //    ORDER by 5,6


    }

    public EnquadramentoPCS salvarComRetorno(EnquadramentoPCS entity) {
        try {
            return getEntityManager().merge(entity);
        } catch (Exception ex) {
            logger.error("Problema ao alterar", ex);
        }
        return null;
    }

    public EnquadramentoPCS filtraEnquadramentoVigente(ProgressaoPCS progressao, CategoriaPCS categoria) {
        try {
            String sql = "select * from enquadramentopcs pcs inner join categoriapcs cat on cat.id = pcs.categoriapcs_id " +
                "            inner join progressaopcs prog on prog.id = pcs.progressaopcs_id " +
                "            INNER JOIN planocargossalarios plano on (plano.id = cat.planocargossalarios_id and plano.id = prog.planocargossalarios_id) " +
                "            where cat.id = :categoria AND prog.superior_id = :progressaoSuperior " +
                "            and prog.descricao = (select p.descricao from ProgressaoPCS p where p.id = :progressao) " +
                "            and :dataVigencia between pcs.iniciovigencia and coalesce(pcs.finalVigencia, :dataVigencia) " +
                "            order by pcs.finalvigencia desc";

            Query q = em.createNativeQuery(sql, EnquadramentoPCS.class);
            q.setParameter("categoria", categoria.getId());
            q.setParameter("progressao", progressao.getId());
            q.setParameter("progressaoSuperior", progressao.getSuperior().getId());
            q.setParameter("dataVigencia", new Date());
            q.setMaxResults(1);
            return (EnquadramentoPCS) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public Double salarioBaseContratoServidor(EntidadePagavelRH ep, Date dataReferencia) {
        String sql = " SELECT coalesce(c.vencimentoBase,0) FROM EnquadramentoFuncional a "
            + " INNER JOIN VinculoFP b ON a.contratoservidor_id = b.id "
            + " INNER JOIN EnquadramentoPCS c ON a.categoriaPCS_id = c.categoriaPCS_id"
            + " AND a.progressaoPCS_id = c.progressaoPCS_id "
            + " WHERE b.id = :parametro "
            + " AND :dataVigencia between a.inicioVigencia AND coalesce(a.finalVigencia,:dataVigencia) "
            + " AND :dataVigencia between c.inicioVigencia AND coalesce(c.finalVigencia,:dataVigencia) ";
        double salarioBase = 0;
        try {
            Query q = em.createNativeQuery(sql);
            q.setParameter("parametro", ep.getIdCalculo());
            q.setParameter("dataVigencia", dataReferencia, TemporalType.DATE);
            q.setMaxResults(1);
            List resultList = q.getResultList();
            if (resultList.isEmpty() || (resultList.get(0) == null)) {
                return 0.0;
            }
            salarioBase = ((BigDecimal) resultList.get(0)).doubleValue();
        } catch (RuntimeException re) {
            throw new FuncoesFolhaFacadeException("Erro ao tentar executar método salarioBase", re);
        }
        ////System.out.println("Salario Base: " + salarioBase);
        return salarioBase;
    }


    public Double salarioBaseContratoServidorUltimoSalario(EntidadePagavelRH ep, Date dataReferencia) {
        String sql = " SELECT coalesce(c.vencimentoBase,0) FROM EnquadramentoFuncional a "
            + " INNER JOIN VinculoFP b ON a.contratoservidor_id = b.id "
            + " INNER JOIN EnquadramentoPCS c ON a.categoriaPCS_id = c.categoriaPCS_id"
            + " AND a.progressaoPCS_id = c.progressaoPCS_id "
            + " WHERE b.id = :parametro "
            + "  and a.iniciovigencia = (select max(c.iniciovigencia) from EnquadramentoFuncional c where c.contratoservidor_id = b.id)  "
            + " AND :dataVigencia between c.inicioVigencia AND coalesce(c.finalVigencia,:dataVigencia) ";

//        sb.append("  and cargo.iniciovigencia = (select max(c.iniciovigencia) from CargoConfianca c where c.contratofp_id = vinculo.id) ");
//        sb.append("  and enquadramento.iniciovigencia = (select max(enq.iniciovigencia) from enquadramentocc enq where enq.cargoConfianca_id = cargo.id ) ");

        double salarioBase = 0;
        try {
            Query q = em.createNativeQuery(sql);
            q.setParameter("parametro", ep.getIdCalculo());
            q.setParameter("dataVigencia", dataReferencia, TemporalType.DATE);
            q.setMaxResults(1);
            List resultList = q.getResultList();
            if (resultList.isEmpty() || (resultList.get(0) == null)) {
                return 0.0;
            }
            salarioBase = ((BigDecimal) resultList.get(0)).doubleValue();
        } catch (RuntimeException re) {
            throw new FuncoesFolhaFacadeException("Erro ao tentar executar método salarioBase", re);
        }
        ////System.out.println("Salario Base: " + salarioBase);
        return salarioBase;
    }


    public BigDecimal salarioBaseContratoServidorAno(EntidadePagavelRH ep, Date finalVigencia) {
        String sql = " SELECT coalesce(c.vencimentoBase,0) FROM EnquadramentoFuncional a "
            + " INNER JOIN VinculoFP b ON a.contratoservidor_id = b.id "
            + " INNER JOIN EnquadramentoPCS c ON a.categoriaPCS_id = c.categoriaPCS_id"
            + " AND a.progressaoPCS_id = c.progressaoPCS_id "
            + " WHERE b.id = :vinculo "
            + " and extract(year from :dataFinal) between "
            + "                                     extract(year from a.inicioVigencia) "
            + "                                     and extract(year from :dataFinal) "
            + " and extract(year from :dataFinal) between "
            + "                                     extract(year from c.inicioVigencia) "
            + "                                     and extract(year from :dataFinal) "
            + " order by c.inicioVigencia desc, a.inicioVigencia desc ";

        BigDecimal salarioBase = BigDecimal.ZERO;
        try {
            Query q = em.createNativeQuery(sql);
            q.setParameter("vinculo", ep.getIdCalculo());
            q.setParameter("dataFinal", finalVigencia, TemporalType.DATE);
            q.setMaxResults(1);
            List resultList = q.getResultList();
            if (resultList.isEmpty() || (resultList.get(0) == null)) {
                return BigDecimal.ZERO;
            }
            salarioBase = ((BigDecimal) resultList.get(0));
        } catch (RuntimeException re) {
            throw new FuncoesFolhaFacadeException("Erro ao tentar executar método salarioBase", re);
        }
        ////System.out.println("Salario Base: " + salarioBase);
        return salarioBase;
    }

    public List<EstruturaPCS> buscarEstruturaCompletaPCS(CategoriaPCS selecionadoCategoriaPCS, ProgressaoPCS selecionadoProgressaoPCS) {
        List<EstruturaPCS> retorno = new LinkedList<>();
        if (selecionadoCategoriaPCS == null || selecionadoProgressaoPCS == null) return retorno;
        selecionadoCategoriaPCS = categoriaPCSFacade.recuperar(selecionadoCategoriaPCS.getId());
        selecionadoProgressaoPCS = progressaoPCSFacade.recuperar(selecionadoProgressaoPCS.getId());
        retorno = recuperarEstruturaPcsDaCategoriaEProgressao(selecionadoCategoriaPCS, selecionadoProgressaoPCS);
        if (retorno != null) {
            for (EstruturaPCS estruturaPCS : retorno) {
                List<EnquadramentoPCS> itens = listaEnquadramentosVigentes(estruturaPCS.getInicioVigencia());
                estruturaPCS.setValores(inicializaValores(selecionadoCategoriaPCS.getFilhos(), selecionadoProgressaoPCS.getFilhos(), itens));
                estruturaPCS.setCategoriaPCSList(selecionadoCategoriaPCS.getFilhos());
                estruturaPCS.setProgressaoPCSList(selecionadoProgressaoPCS.getFilhos());
            }
        }
        return retorno;
    }

    public List<EstruturaPCS> buscarEstruturaCompletaPCSVigente(CategoriaPCS selecionadoCategoriaPCS, ProgressaoPCS selecionadoProgressaoPCS, Date dataOperacao) {
        List<EstruturaPCS> retorno = new LinkedList<>();
        if (selecionadoCategoriaPCS == null || selecionadoProgressaoPCS == null) return retorno;
        selecionadoCategoriaPCS = categoriaPCSFacade.recuperar(selecionadoCategoriaPCS.getId());
        selecionadoProgressaoPCS = progressaoPCSFacade.recuperar(selecionadoProgressaoPCS.getId());
        retorno = recuperarEstruturaPcsDaCategoriaEProgressao(selecionadoCategoriaPCS, selecionadoProgressaoPCS, dataOperacao);
        if (retorno != null) {
            for (EstruturaPCS estruturaPCS : retorno) {
                List<EnquadramentoPCS> itens = listaEnquadramentosVigentes(estruturaPCS.getInicioVigencia());
                estruturaPCS.setValores(inicializaValores(selecionadoCategoriaPCS.getFilhos(), selecionadoProgressaoPCS.getFilhos(), itens));
                estruturaPCS.setCategoriaPCSList(selecionadoCategoriaPCS.getFilhos());
                estruturaPCS.setProgressaoPCSList(selecionadoProgressaoPCS.getFilhos());
            }
        }
        return retorno;
    }

    public List<EnquadramentoPCS> buscarEnquadramentoVigentePorCategoria(CategoriaPCS categoriaPCSSuperior, ProgressaoPCS progressaoPCSSuperior, Date dataOperacao) {
        Query q = em.createQuery("select pcs from EnquadramentoPCS pcs" +
            " where pcs.categoriaPCS.superior  = :superiorCat and pcs.progressaoPCS.superior = :superiorProg " +
            " and :dataOperacao between pcs.inicioVigencia and coalesce(pcs.finalVigencia, :dataOperacao)" +
            " order by pcs.categoriaPCS.descricao, pcs.progressaoPCS.descricao asc");
        q.setParameter("superiorCat", categoriaPCSSuperior);
        q.setParameter("superiorProg", progressaoPCSSuperior);
        q.setParameter("dataOperacao", dataOperacao);
        return q.getResultList();
    }

    public List<EstruturaPCS> recuperarEstruturaPcsDaCategoriaEProgressao(CategoriaPCS selecionadoCategoriaPCS, ProgressaoPCS selecionadoProgressaoPCS) {
        Query q = em.createQuery("select new br.com.webpublico.entidadesauxiliares.EstruturaPCS(pcs.categoriaPCS.superior, pcs.progressaoPCS.superior,pcs.inicioVigencia, pcs.finalVigencia) from EnquadramentoPCS pcs" +
            " where pcs.categoriaPCS.superior  = :superiorCat and pcs.progressaoPCS.superior = :superiorProg " +
            " group by pcs.categoriaPCS.superior, pcs.progressaoPCS.superior,pcs.inicioVigencia, pcs.finalVigencia order by inicioVigencia asc");
        q.setParameter("superiorCat", selecionadoCategoriaPCS);
        q.setParameter("superiorProg", selecionadoProgressaoPCS);
        return q.getResultList();
    }

    public List<EstruturaPCS> recuperarEstruturaPcsDaCategoriaEProgressao(CategoriaPCS selecionadoCategoriaPCS, ProgressaoPCS selecionadoProgressaoPCS, Date dataOperacao) {
        Query q = em.createQuery("select new br.com.webpublico.entidadesauxiliares.EstruturaPCS(pcs.categoriaPCS.superior, pcs.progressaoPCS.superior,pcs.inicioVigencia, pcs.finalVigencia) from EnquadramentoPCS pcs" +
            " where pcs.categoriaPCS.superior  = :superiorCat and pcs.progressaoPCS.superior = :superiorProg " +
            " and :dataOperacao between pcs.inicioVigencia and coalesce(pcs.finalVigencia, :dataOperacao)" +
            " group by pcs.categoriaPCS.superior, pcs.progressaoPCS.superior,pcs.inicioVigencia, pcs.finalVigencia order by inicioVigencia asc");
        q.setParameter("superiorCat", selecionadoCategoriaPCS);
        q.setParameter("superiorProg", selecionadoProgressaoPCS);
        q.setParameter("dataOperacao", dataOperacao);
        return q.getResultList();
    }

    private Map<CategoriaPCS, Map<ProgressaoPCS, EnquadramentoPCS>> inicializaValores(
        List<CategoriaPCS> pCategorias, List<ProgressaoPCS> pProgressoes, List<EnquadramentoPCS> itens) {
        Map<CategoriaPCS, Map<ProgressaoPCS, EnquadramentoPCS>> retorno = new HashMap<CategoriaPCS, Map<ProgressaoPCS, EnquadramentoPCS>>();
//        boolean encontrouEnquadramento = false;

        for (CategoriaPCS c : pCategorias) {
            Map<ProgressaoPCS, EnquadramentoPCS> v = new HashMap<ProgressaoPCS, EnquadramentoPCS>();
            for (ProgressaoPCS p : pProgressoes) {

                for (EnquadramentoPCS ePCS : itens) {
                    if ((ePCS.getCategoriaPCS().equals(c)) && (ePCS.getProgressaoPCS().equals(p))) {
                        v.put(p, ePCS);
//                        encontrouEnquadramento = true;
                    }
                }

//                if (!encontrouEnquadramento) {
//                    v.put(p, new EnquadramentoPCS(dataVigencia, dataFinalVigencia, p, c, BigDecimal.ZERO));
//                }
//                encontrouEnquadramento = false;
            }
            retorno.put(c, v);
        }
        return retorno;
    }

    public List<EnquadramentoPCS> buscarEnquadramentosPorCategoria(ProgressaoPCS progressao, CategoriaPCS categoria, Date dataOperacao) {
        String sql = "select e.* from EnquadramentoPCS e " +
            " inner join progressaopcs prog on E.PROGRESSAOPCS_ID = prog.id " +
            " inner join progressaopcs progPai on prog.superior_id = progPai.id " +
            " inner join categoriapcs cat on cat.id = E.CATEGORIAPCS_ID " +
            " inner join categoriapcs catPai on catPai.id = cat.superior_id " +
            " where e.categoriaPCS_id = :cat " +
            " and progPai.id = :pro " +
            " and e.vencimentobase > 0 " +
            " and PROG.PLANOCARGOSSALARIOS_ID = CAT.PLANOCARGOSSALARIOS_ID  " +
            " and to_date(:dataOperacao, 'dd/mm/yyyy') between e.inicioVigencia and coalesce(e.finalVigencia, to_date(:dataOperacao, 'dd/mm/yyyy')) " +
            " order by e.vencimentoBase desc ";
        Query q = em.createNativeQuery(sql, EnquadramentoPCS.class);
        q.setParameter("cat", categoria.getId());
        q.setParameter("pro", progressao.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        return q.getResultList();
    }

    public EnquadramentoPCS buscarEnquadramentoPorProgressaoCategoriaEVigencia(ProgressaoPCS progressao, CategoriaPCS categoria, Date dataOperacao) {
        String sql = "select e.* from EnquadramentoPCS e " +
            " where e.categoriaPCS_id = :cat " +
            " and e.progressaoPcs_id = :pro " +
            " and to_date(:dataOperacao, 'dd/mm/yyyy') between e.inicioVigencia and coalesce(e.finalVigencia, to_date(:dataOperacao, 'dd/mm/yyyy')) " +
            " order by e.vencimentoBase desc ";
        Query q = em.createNativeQuery(sql, EnquadramentoPCS.class);
        q.setParameter("cat", categoria.getId());
        q.setParameter("pro", progressao.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return (EnquadramentoPCS) resultList.get(0);
        }
        return null;
    }

    public void reverterEnquadramentosPcs(List<EnquadramentoPCS> enquadramentos) {
        for (EnquadramentoPCS enquadramentoASerRemovido : enquadramentos) {
            Date dataReferencia = new DateTime(enquadramentoASerRemovido.getInicioVigencia()).minusDays(1).toDate();
            EnquadramentoPCS enquadramentoAnterior = buscarEnquadramentoPorProgressaoCategoriaEVigencia(enquadramentoASerRemovido.getProgressaoPCS(), enquadramentoASerRemovido.getCategoriaPCS(), dataReferencia);
            if (enquadramentoAnterior != null) {
                enquadramentoAnterior.setFinalVigencia(null);
                salvar(enquadramentoAnterior);
            }
            remover(enquadramentoASerRemovido);
        }
    }


}
