/*
 * Codigo gerado automaticamente em Thu Oct 06 17:09:26 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.rh.portal.FuncaoGratificadaPortal;
import br.com.webpublico.entidadesauxiliares.rh.portal.VinculoServidorPortal;
import br.com.webpublico.enums.rh.TipoFuncaoGratificada;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.rh.parametro.cargocomissaofuncaogratificada.ParametroControleCargoFGFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class FuncaoGratificadaFacade extends AbstractFacade<FuncaoGratificada> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private TipoProvimentoFacade tipoProvimentoFacade;
    @EJB
    private EnquadramentoPCSFacade enquadramentoPCSFacade;
    @EJB
    private CargoConfiancaFacade cargoConfiancaFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ParametroControleCargoFGFacade parametroControleCargoFGFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;

    public FuncaoGratificadaFacade() {
        super(FuncaoGratificada.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CargoConfiancaFacade getCargoConfiancaFacade() {
        return cargoConfiancaFacade;
    }

    public FichaFinanceiraFPFacade getFichaFinanceiraFPFacade() {
        return fichaFinanceiraFPFacade;
    }

    @Override
    public FuncaoGratificada recuperar(Object id) {
        FuncaoGratificada fg = em.find(FuncaoGratificada.class, id);
        fg.getEnquadramentoFGs().size();
        return fg;
    }

    public List<FuncaoGratificada> recuperaFuncaoGratificadaContrato(ContratoFP contratoFP, Date dataParametro) {
        Query q = em.createQuery(" select funcao from FuncaoGratificada funcao "
            + " inner join funcao.contratoFP contrato "
            + " where contrato = :parametroContrato "
            + " and :dataVigencia >= funcao.inicioVigencia "
            + " and :dataVigencia <= coalesce(funcao.finalVigencia,:dataVigencia)  ");

        q.setParameter("parametroContrato", contratoFP);
        q.setParameter("dataVigencia", dataParametro);
        return q.getResultList();

    }

    public List<FuncaoGratificada> recuperaFuncaoGratificadaContratoSemVigencia(ContratoFP contratoFP) {
        Query q = em.createQuery(" select funcao from FuncaoGratificada funcao "
            + " inner join funcao.contratoFP contrato "
            + " where contrato = :parametroContrato ");
        q.setParameter("parametroContrato", contratoFP);
        return q.getResultList();

    }

    public List<FuncaoGratificada> listaFuncoesGratificadasTodas(ContratoFP contratoFP) {
        Query q = em.createQuery(" select funcao from FuncaoGratificada funcao "
            + " inner join funcao.contratoFP contrato "
            + " where contrato = :parametroContrato ");
        q.setParameter("parametroContrato", contratoFP);
        return q.getResultList();

    }

    public FuncaoGratificada recuperaFuncaoGratificada(ContratoFP contratoFP) {
        Query q = em.createQuery(" select funcao from FuncaoGratificada funcao "
            + " inner join funcao.contratoFP contrato "
            + " where contrato = :parametroContrato "
            + " and :dataVigencia >= funcao.inicioVigencia "
            + " and :dataVigencia <= coalesce(funcao.finalVigencia,:dataVigencia)  ");
        q.setParameter("parametroContrato", contratoFP);
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(new Date()));
        q.setMaxResults(1);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (FuncaoGratificada) q.getSingleResult();

    }

    public boolean isContratoComFPVigente(FuncaoGratificada funcaoGratificada) {
        Query q = em.createQuery(" select funcao from FuncaoGratificada funcao "
            + " inner join funcao.contratoFP contrato "
            + " where contrato = :parametroContrato "
            + " and :dataVigencia >= funcao.inicioVigencia "
            + " and :dataVigencia <= coalesce(funcao.finalVigencia,:dataVigencia)  ");
        q.setMaxResults(1);
        q.setParameter("parametroContrato", funcaoGratificada.getContratoFP());
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(new Date()));

        if (!q.getResultList().isEmpty()) {
            if (!((FuncaoGratificada) q.getSingleResult()).equals(funcaoGratificada)) {
                return true;
            }
        }

        return false;
    }


    public void salvarFuncaoGratificada(FuncaoGratificada funcaoGratificada, ProvimentoFP provimentoFP) {
        ValidacaoException ve = new ValidacaoException();
        if (provimentoFP.getId() == null) {
            ve.lancarException();
            getEntityManager().persist(provimentoFP);
        } else {
            getEntityManager().merge(provimentoFP);
        }

        funcaoGratificada.setProvimentoFP(provimentoFP);

        if (funcaoGratificada.getId() == null) {
            getEntityManager().persist(funcaoGratificada);
        } else {
            getEntityManager().merge(funcaoGratificada);
        }
    }

    public List<FuncaoGratificada> listaFiltrandoX(String s, int inicio, int max) {
        String hql = " select funcao from FuncaoGratificada funcao "
            + " inner join funcao.contratoFP contrato"
            + " inner join contrato.matriculaFP matricula "
            + " inner join matricula.pessoa pf "
            + " where (lower(matricula.matricula) like :filtro) "
            + " or (lower(pf.nome) like :filtro) "
            + " or (lower(funcao.descricao) like :filtro) "
            + " or (to_char(funcao.inicioVigencia,'dd/MM/yyyy') like :filtro) "
            + " or (to_char(funcao.finalVigencia,'dd/MM/yyyy') like :filtro) "
            + " or (to_char(funcao.dataNomeacao,'dd/MM/yyyy') like :filtro) ";

        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");

        q.setMaxResults(max + 1);
        q.setFirstResult(inicio);
        return q.getResultList();
    }

    public List<CargoConfianca> listaFiltrandoXRetorno(String s, int inicio, int max) {
        String hql = " select retorno from RetornoFuncaoGratificada retorno "
            + " inner join retorno.contratoFP contrato "
            + " inner join contrato.matriculaFP matricula "
            + " inner join matricula.pessoa pf "
            + " inner join retorno.provimentoFP provimento "
            + " where provimento.tipoProvimento =:prov "
            + " and  ((lower(matricula.matricula) like :filtro) "
            + " or (lower(pf.nome) like :filtro) "
            + " or (to_char(retorno.dataRetorno,'dd/MM/yyyy') like :filtro)) ";

        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("prov", tipoProvimentoFacade.recuperaTipoProvimentoPorCodigo(TipoProvimento.EXONERACAO_DE_FUNCAO_GRATIFICADA));

        q.setMaxResults(max + 1);
        q.setFirstResult(inicio);
        return q.getResultList();
    }

    public List<FuncaoGratificada> recuperaFuncaoGratificadaProvimento(ContratoFP contratoFP, Date dataProvimento) {
        Query q = em.createQuery(" select funcao from FuncaoGratificada funcao "
            + " inner join funcao.contratoFP contrato "
            + " where contrato = :parametroContrato "
            + " and :dataProvimento >= funcao.inicioVigencia "
            + " order by funcao.inicioVigencia");

        q.setParameter("parametroContrato", contratoFP);
        q.setParameter("dataProvimento", dataProvimento);

        List<FuncaoGratificada> listaFG = q.getResultList();

        for (FuncaoGratificada fg : listaFG) {
            fg.getEnquadramentoFGs().size();

            if (dataProvimento.getTime() < fg.getInicioVigencia().getTime()) {
                continue;
            }

            for (EnquadramentoFG efg : fg.getEnquadramentoFGs()) {
                EnquadramentoPCS enquadramentoPCS = enquadramentoPCSFacade.recuperaUltimoValor(efg.getCategoriaPCS(), efg.getProgressaoPCS(), efg.getInicioVigencia(), efg.getInicioVigencia());
                if (enquadramentoPCS != null) {
                    efg.setVencimentoBase(enquadramentoPCS.getVencimentoBase());
                }
            }
        }
        return listaFG;

    }

    public EnquadramentoFG recuperaEnquadramentoFGVigente(ContratoFP contratoFP, Date dataProvimento) {
        StringBuilder hql = new StringBuilder();
        hql.append("select fg from FuncaoGratificada funcaoGratificada inner join funcaoGratificada.enquadramentoFGs fg ");
        hql.append(" where funcaoGratificada.contratoFP = :contrato ");
        hql.append(" and :dataProvimento >= funcaoGratificada.inicioVigencia ");
        hql.append(" and :dataProvimento <= coalesce(funcaoGratificada.finalVigencia, :dataProvimento) ");

        hql.append(" and :dataProvimento >= fg.inicioVigencia ");
        hql.append(" and :dataProvimento <= coalesce(fg.finalVigencia, :dataProvimento) ");

        Query q = em.createQuery(hql.toString());
        q.setParameter("contrato", contratoFP);
        q.setParameter("dataProvimento", dataProvimento);

        List<EnquadramentoFG> lista = q.getResultList();

        if (lista == null || lista.isEmpty()) {
            return null;
        }
        return lista.get(0);
    }

    public FuncaoGratificada recuperaFuncaoGratificadaVigente(ContratoFP contratoFP, Date dataProvimento) {
        StringBuilder hql = new StringBuilder();
        hql.append(" from FuncaoGratificada funcaoGratificada ");
        hql.append(" where funcaoGratificada.contratoFP = :contrato ");
        hql.append(" and :dataProvimento >= funcaoGratificada.inicioVigencia ");
        hql.append(" and :dataProvimento <= coalesce(funcaoGratificada.finalVigencia, :dataProvimento) ");

        Query q = em.createQuery(hql.toString());
        q.setParameter("contrato", contratoFP);
        q.setParameter("dataProvimento", dataProvimento);

        List<FuncaoGratificada> lista = q.getResultList();

        if (lista == null || lista.isEmpty()) {
            return new FuncaoGratificada();
        }
        return lista.get(0);
    }

    public Integer buscarQuantidadeDeServidoresNomeadosFG(Date inicio) {
        Integer total = 0;
        String hql = "select count(acesso) from FuncaoGratificada acesso where to_date(to_char(acesso.dataRegistro,'dd/MM/yyyy'),'dd/MM/yyyy') = :data ";
        Query q = em.createQuery(hql);
        q.setParameter("data", Util.getDataHoraMinutoSegundoZerado(inicio));
        if (q.getResultList().isEmpty()) {
            return total;
        }
        Long bg = (Long) q.getSingleResult();
        total = bg.intValue();
        return total;
    }

    public List<FuncaoGratificadaPortal> buscarFuncaoGratificadaPortal(List<VinculoServidorPortal> vinculos, Date data) {
        if (vinculos.isEmpty()) {
            return Lists.newArrayList();
        }
        List<Long> ids = new ArrayList<>();
        for (VinculoServidorPortal vinculo : vinculos) {
            ids.add(vinculo.getVinculoFP().getId());
        }

        String sql = "select  distinct v.id as vinculo_id,  " +
            " mat.matricula, " +
            " v.numero,  " +
            " pf.nome,  " +
            " fg.INICIOVIGENCIA,  " +
            " fg.FINALVIGENCIA,  " +
            " fg.DESCRICAO,  " +
            " cat.DESCRICAO as categoria,  " +
            " pcs.VENCIMENTOBASE    " +
            " from FUNCAOGRATIFICADA fg " +
            "   inner join vinculofp v on v.id = fg.CONTRATOFP_ID " +
            "   inner join ENQUADRAMENTOFG efg on efg.FUNCAOGRATIFICADA_ID = fg.id " +
            "   inner join CategoriaPCS cat on cat.id = efg.CATEGORIAPCS_ID " +
            "   inner join progressaopcs prog on prog.id = efg.PROGRESSAOPCS_ID " +
            "   inner join enquadramentopcs pcs on pcs.CATEGORIAPCS_ID = cat.id and pcs.PROGRESSAOPCS_ID = prog.id " +
            "   inner join MatriculaFP mat on mat.id = v.MATRICULAFP_ID " +
            "   inner join pessoaFisica pf on pf.id = mat.PESSOA_ID " +
            " where to_date(:dataOperacao,'dd/MM/yyyy') between fg.INICIOVIGENCIA and coalesce(fg.FINALVIGENCIA, to_date(:dataOperacao,'dd/MM/yyyy')) " +
            "  and to_date(:dataOperacao,'dd/MM/yyyy') between v.INICIOVIGENCIA and coalesce(v.FINALVIGENCIA, to_date(:dataOperacao,'dd/MM/yyyy')) " +
            "  and to_date(:dataOperacao,'dd/MM/yyyy') between efg.INICIOVIGENCIA and coalesce(efg.FINALVIGENCIA, to_date(:dataOperacao,'dd/MM/yyyy')) " +
            "  and to_date(:dataOperacao,'dd/MM/yyyy') between pcs.INICIOVIGENCIA and coalesce(pcs.FINALVIGENCIA, to_date(:dataOperacao,'dd/MM/yyyy')) " +
            "  and v.id in (:idVinculos)";

        Query q = em.createNativeQuery(sql);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(data));
        q.setParameter("idVinculos", ids);
        List resultados = q.getResultList();
        List<FuncaoGratificadaPortal> funcoes = Lists.newLinkedList();
        for (Object resultado : resultados) {
            Object[] objeto = (Object[]) resultado;
            FuncaoGratificadaPortal funcao = criarFuncaoPortal(objeto);
            if (funcao != null) {
                funcoes.add(funcao);
            }
        }
        return funcoes;
    }

    private FuncaoGratificadaPortal criarFuncaoPortal(Object[] objeto) {
        FuncaoGratificadaPortal funcaoPortal = new FuncaoGratificadaPortal();
        funcaoPortal.setVinculoFPId((BigDecimal) objeto[0]);
        funcaoPortal.setInicioVigencia((Date) objeto[4]);
        funcaoPortal.setFinalVigencia((Date) objeto[5]);
        funcaoPortal.setDescricao((String) objeto[6]);
        funcaoPortal.setCategoria((String) objeto[7]);
        funcaoPortal.setValor((BigDecimal) objeto[8]);
        return funcaoPortal;
    }

    public List<FuncaoGratificada> buscarFuncoesGratificadasPorContratoFP(FuncaoGratificada funcaoGratificada) {
        String sql = "SELECT * " +
            "         FROM FuncaoGratificada " +
            "         WHERE CONTRATOFP_ID = :contrato_id";
        Query q = em.createNativeQuery(sql, FuncaoGratificada.class);
        q.setParameter("contrato_id", funcaoGratificada.getContratoFP());
        return q.getResultList().isEmpty() ? Lists.<FuncaoGratificada>newArrayList() : q.getResultList();
    }

    public FuncaoGratificada recuperarEnquadramentos (Object id) {
        FuncaoGratificada funcaoGratificada = em.find(FuncaoGratificada.class, id);
        Hibernate.initialize(funcaoGratificada.getEnquadramentoFGs());
        return funcaoGratificada;
    }

    public FuncaoGratificada buscarFuncaoGratificadaVigente(TipoFuncaoGratificada tipo, VinculoFP vinculoFP, Date dataOperacao) {
        String sql = " select * from funcaogratificada fg " +
            "where fg.contratofp_id = :contratoFPId " +
            "and to_date(:dataOperacao, 'dd/mm/yyyy') between fg.iniciovigencia and coalesce(fg.finalvigencia, to_date(:dataOperacao, 'dd/mm/yyyy')) " +
            "and fg.tipofuncaogratificada = :tipo ";
        Query q = em.createNativeQuery(sql, FuncaoGratificada.class);
        q.setParameter("contratoFPId", vinculoFP.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        q.setParameter("tipo", tipo.name());
        List<FuncaoGratificada> resultado = q.getResultList();
        if (resultado == null || resultado.isEmpty()) {
            return null;
        }
        return resultado.get(0);
    }
}
