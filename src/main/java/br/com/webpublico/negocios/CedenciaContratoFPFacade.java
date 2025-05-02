/*
 * Codigo gerado automaticamente em Mon Oct 17 08:48:31 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.enums.FinalidadeCedenciaContratoFP;
import br.com.webpublico.enums.TipoCedenciaContratoFP;
import br.com.webpublico.enums.rh.esocial.TipoCessao2231;
import br.com.webpublico.esocial.comunicacao.enums.TipoArquivoESocial;
import br.com.webpublico.esocial.service.S2231Service;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.rh.esocial.RegistroESocialFacade;
import br.com.webpublico.util.NossaInputStream;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static br.com.webpublico.util.DataUtil.localDateToDate;

@Stateless
public class CedenciaContratoFPFacade extends AbstractFacade<CedenciaContratoFP> {

    private static final Logger logger = LoggerFactory.getLogger(CedenciaContratoFPFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private LotacaoFuncionalFacade lotacaoFuncionalFacade;
    @EJB
    private SituacaoFuncionalFacade situacaoFuncionalFacade;
    @EJB
    private SituacaoContratoFPFacade situacaoContratoFPFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private RegistroESocialFacade registroESocialFacade;
    private S2231Service s2231Service;

    public CedenciaContratoFPFacade() {
        super(CedenciaContratoFP.class);
        s2231Service = (S2231Service) Util.getSpringBeanPeloNome("s2231Service");
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SituacaoContratoFPFacade getSituacaoContratoFPFacade() {
        return situacaoContratoFPFacade;
    }

    public SituacaoFuncionalFacade getSituacaoFuncionalFacade() {
        return situacaoFuncionalFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public List<FichaFinanceiraFP> buscaFichaFinaceiraFPPorVigenciaCedenciaContratoFP(CedenciaContratoFP cedenciaContratoFP) {
        String sql = "SELECT fffp.* FROM FichaFinanceiraFP fffp "
            + " INNER JOIN VinculoFP vfp ON fffp.vinculoFP_id = vfp.id "
            + " INNER JOIN FolhaDePagamento fdp ON fffp.folhaDePagamento_id = fdp.id "
            + " INNER JOIN ContratoFP cfp ON vfp.id = cfp.id "
            + " INNER JOIN CedenciaContratoFP ccfp ON ccfp.contratoFP_id = cfp.id "
            + " WHERE (fdp.mes >= extract(MONTH FROM ccfp.dataCessao) "
            + " AND fdp.ano >= extract(YEAR FROM ccfp.dataCessao))"
            + " AND  (fdp.mes <= extract(MONTH FROM ccfp.dataRetorno) "
            + " AND fdp.ano <= extract(YEAR FROM ccfp.dataRetorno))"
            + " AND ccfp.id = :codigo ";

        Query q = em.createNativeQuery(sql, FichaFinanceiraFP.class);
        q.setParameter("codigo", cedenciaContratoFP.getId());
        try {
            return q.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean podeSalvarCedencia(CedenciaContratoFP c) {
        Query q = em.createQuery("from CedenciaContratoFP c where ((:dataInicio "
            + "between c.dataCessao and coalesce(c.dataRetorno,:dataInicio)) "
            + "or  (:dataFim between c.dataCessao and coalesce(c.dataRetorno,:dataFim))) and c.contratoFP = :contratoFP");
        q.setParameter("contratoFP", c.getContratoFP());
        q.setParameter("dataInicio", c.getDataCessao());
        q.setParameter("dataFim", retornaData(c.getDataRetorno()));

        return q.getResultList().isEmpty();
    }

    public Date retornaData(Date c) {
        if (c != null) {
            return c;
        } else {
            return Util.getDataHoraMinutoSegundoZerado(new Date());
        }
    }

    @Override
    public void salvar(CedenciaContratoFP cedenciaContratoFP) {
        if (cedenciaContratoFP.getAnexo() != null) {
            try {
                arquivoFacade.novoArquivo(cedenciaContratoFP.getAnexo(), new NossaInputStream(cedenciaContratoFP.getAnexo()));
            } catch (Exception ex) {
                logger.error("Erro:", ex);
            }
        }
        super.salvar(cedenciaContratoFP);
    }

    public void salvarApenasDados(CedenciaContratoFP cedenciaContratoFP) {
        try {
            CedenciaContratoFP entidadeExistente = getEntityManager().find(CedenciaContratoFP.class, cedenciaContratoFP.getId());
            if (entidadeExistente != null) {
                cedenciaContratoFP.setAnexo(entidadeExistente.getAnexo());
            }
            super.salvar(cedenciaContratoFP);
        } catch (Exception ex) {
            logger.error("Erro ao salvar dados da cedência:", ex);
            throw new EJBException("Erro ao salvar dados da cedência", ex);
        }
    }

    public void salvarNovoCedencia(CedenciaContratoFP cedenciaContratoFP) throws Exception {
        if (cedenciaContratoFP.getAnexo() != null) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            for (ArquivoParte a : cedenciaContratoFP.getAnexo().getPartes()) {
                try {
                    buffer.write(a.getDados());
                } catch (IOException ex) {
                    logger.error("Erro:", ex);
                }
            }
            InputStream is = new ByteArrayInputStream(buffer.toByteArray());
            arquivoFacade.novoArquivo(cedenciaContratoFP.getAnexo(), is);
        }

        salvarLotacaoFuncional(cedenciaContratoFP);
        cedenciaContratoFP = getEntityManager().merge(cedenciaContratoFP);

        //Funcionário Cedido
        if (cedenciaContratoFP.getCessionario() != null) {
            SituacaoContratoFP situacaoContratoFPRecuperada = recuperaSituacaoContratoFPVigente(cedenciaContratoFP.getContratoFP());
            if (situacaoContratoFPRecuperada != null) {
                situacaoContratoFPRecuperada.setFinalVigencia(cedenciaContratoFP.getDataCessao());
                em.merge(situacaoContratoFPRecuperada);
            }
        }

        ContratoFP contratoFP = contratoFPFacade.recuperar(cedenciaContratoFP.getContratoFP().getId());
        boolean temFichaFinanceira = folhaDePagamentoFacade.existeFolhaPorContratoData(contratoFP, SistemaFacade.getDataCorrente());
        SituacaoContratoFP situacaoContratoFPAnterior = null;
        if (temFichaFinanceira) {
            situacaoContratoFPAnterior = recuperaSituacaoContratoFPVigente(contratoFP);
            situacaoContratoFPAnterior.setFinalVigencia(cedenciaContratoFP.getDataCessao());
            em.merge(situacaoContratoFPAnterior);

            //pego a data da cessao e aumento 1 dia
            //para ser o inicio da vigencia da nova situação
            Calendar calendario = Calendar.getInstance();
            calendario.setTime(cedenciaContratoFP.getDataCessao());
            calendario.add(Calendar.DATE, 1);

            //criar a nova situacaoContratoFP vigente
            SituacaoContratoFP situacaoContratoFP = new SituacaoContratoFP();
            situacaoContratoFP.setInicioVigencia(calendario.getTime());
            situacaoContratoFP.setContratoFP(cedenciaContratoFP.getContratoFP());
            //recuperar a situacao Funcional para cedencia
            SituacaoFuncional situacaoFuncional = null;
            if (cedenciaContratoFP.getCedente() == null) {
                situacaoFuncional = situacaoFuncionalFacade.recuperaCodigo(SituacaoFuncional.A_DISPOSICAO);
            } else {
                situacaoFuncional = situacaoFuncionalFacade.recuperaCodigo(SituacaoFuncional.A_NOSSA_DISPOSICAO);
            }
            situacaoContratoFP.setSituacaoFuncional(situacaoFuncional);
            em.persist(situacaoContratoFP);
        } else {
            //altera situação funcional vigente para A NOSSA DISPOSIÇÃO
            situacaoContratoFPAnterior = recuperaSituacaoContratoFPVigente(contratoFP);
            situacaoContratoFPAnterior.setSituacaoFuncional(situacaoFuncionalFacade.recuperaCodigo(SituacaoFuncional.A_NOSSA_DISPOSICAO));
            em.merge(situacaoContratoFPAnterior);
        }

    }

    private SituacaoContratoFP recuperaSituacaoContratoFPVigente(ContratoFP contratoFP) {
        return situacaoContratoFPFacade.ultimaSituacaoContratoFP(contratoFP);
    }

    private void salvarLotacaoFuncional(CedenciaContratoFP cedenciaContratoFP) {
        List<LotacaoFuncional> listaLotacaoFuncionals = lotacaoFuncionalFacade.recuperaLotacaoFuncionalPorContratoFPComparandoDataCessao(cedenciaContratoFP.getContratoFP(), cedenciaContratoFP.getDataCessao());
        if (!TipoCedenciaContratoFP.COM_ONUS.equals(cedenciaContratoFP.getTipoContratoCedenciaFP())) {
            for (LotacaoFuncional item : listaLotacaoFuncionals) {
                item.getHorarioContratoFP().setFinalVigencia(cedenciaContratoFP.getDataCessao());
                item.setFinalVigencia(cedenciaContratoFP.getDataCessao());
                getEntityManager().persist(item);
            }
        }
    }

    public CedenciaContratoFP recuperaCedenciaPrefeituraVigentePorContratoFP(ContratoFP contratoFP) {
        try {
            Query q = em.createQuery(" from CedenciaContratoFP cedencia "
                + " where cedencia.cedente is not null and cedencia.contratoFP = :contrato "
                + " and :dataVigencia >= cedencia.dataCessao and :dataVigencia <= coalesce(cedencia.dataRetorno,:dataVigencia)");
            q.setParameter("contrato", contratoFP);
            q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(new Date()));
            q.setMaxResults(1);

            return (CedenciaContratoFP) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public CedenciaContratoFP recuperaCedenciaUnidadeExternaVigentePorContratoFP(ContratoFP contratoFP, Date data) {
        try {
            Query q = em.createQuery(" from CedenciaContratoFP cedencia "
                + " where cedencia.cessionario is not null and cedencia.contratoFP = :contrato "
                + " and :dataVigencia >= cedencia.dataCessao and :dataVigencia <= coalesce(cedencia.dataRetorno,:dataVigencia)");
            q.setParameter("contrato", contratoFP);
            q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(data));
            q.setMaxResults(1);

            return (CedenciaContratoFP) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public CedenciaContratoFP recuperaCedenciaUnidadeExternaVigentePorContratoFP(ContratoFP contratoFP) {
        return recuperaCedenciaUnidadeExternaVigentePorContratoFP(contratoFP, new Date());
    }

    public CedenciaContratoFP recuperaCedenciaVigentePorContratoFP(ContratoFP contratoFP, Date data) {
        Query q = em.createQuery(" from CedenciaContratoFP cedencia "
            + " where cedencia.contratoFP = :contrato "
            + " and :dataVigencia >= cedencia.dataCessao and :dataVigencia <= coalesce(cedencia.dataRetorno,:dataVigencia)");
        q.setParameter("contrato", contratoFP);
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(data));
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (CedenciaContratoFP) q.getResultList().get(0);
        }
        return null;
    }

    public Object[] recuperaCedenciaVigentePorContratoFPAtuarial(ContratoFP contratoFP, Date data) {
        Query q = em.createQuery(" select cedencia.tipoContratoCedenciaFP, cedencia.finalidadeCedenciaContratoFP from CedenciaContratoFP cedencia "
            + " where cedencia.contratoFP = :contrato "
            + "  AND :dataVigencia >= cedencia.dataCessao "
            + "  AND :dataVigencia <= COALESCE(cedencia.dataRetorno, :dataVigencia)");
        q.setParameter("contrato", contratoFP);
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(Util.recuperaDataUltimoDiaDoMes(data)));
        q.setMaxResults(1);
        try {
            return (Object[]) q.getSingleResult();
        } catch (NoResultException nr) {
            return null;
        }
    }


    public CedenciaContratoFP recuperaUltimaCedenciaUnidadeExternaPorContratoFP(ContratoFP contratoFP) {
        try {
            Query q = em.createQuery(" from CedenciaContratoFP cedencia "
                + " where cedencia.cessionario is not null and cedencia.contratoFP = :contrato "
                + " and cedencia.dataCessao = (select max(c.dataCessao) from CedenciaContratoFP c "
                + " where c.cessionario is not null and c.contratoFP = :contrato) ");
            q.setParameter("contrato", contratoFP);
            q.setMaxResults(1);

            return (CedenciaContratoFP) q.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public CedenciaContratoFP recuperaCedenciaByServidorAndPeriodo(ContratoFP contratoFP, Date inicio, Date fim) {
        Query q = em.createQuery("from CedenciaContratoFP af where af.contratoFP = :contratoFP"
            + " and ((:dataInicio between af.dataCessao and coalesce(af.dataRetorno,:dataInicio)) " +
            "           or (:dataFim between af.dataCessao and coalesce(af.dataRetorno,:dataFim))"
            + "         or (af.dataCessao between :dataInicio and :dataFim) " +
            "           or (af.dataRetorno between :dataInicio and :dataFim))");
        q.setParameter("contratoFP", contratoFP);
        q.setParameter("dataInicio", inicio);
        q.setParameter("dataFim", fim);
        q.setMaxResults(1);
        try {
            return (CedenciaContratoFP) q.getSingleResult();
        } catch (NoResultException nr) {
            return null;
        }
    }

    public List<CedenciaContratoFP> buscarCedenciaPorPeriodoAndTipo(Long contratoID, Date inicio, Date fim) {
        Query q = em.createQuery("from CedenciaContratoFP af where af.contratoFP.id = :contratoID and af.tipoContratoCedenciaFP = :tipo "
            + " and ((:dataInicio between af.dataCessao and coalesce(af.dataRetorno,:dataInicio)) or (:dataFim between af.dataCessao and coalesce(af.dataRetorno,:dataFim))"
            + " or (af.dataCessao between :dataInicio and :dataFim) or (af.dataRetorno between :dataInicio and :dataFim))");

        q.setParameter("contratoID", contratoID);
        q.setParameter("tipo", TipoCedenciaContratoFP.SEM_ONUS);
        q.setParameter("dataInicio", inicio);
        q.setParameter("dataFim", fim);
        return q.getResultList();
    }

    public Date recuperaDataCessao(VinculoFP vinculoFP, LocalDate dataReferencia) {
        String hql = "select max(cedencia.dataCessao) from CedenciaContratoFP cedencia where cedencia.contratoFP.id = :vinculoFP " +
            " and cedencia.tipoContratoCedenciaFP = :tipo" +
            " and (extract(month from cedencia.dataCessao))= (extract(month from :dataParam)) " +
            "and (extract(year from cedencia.dataCessao))= (extract(year from :dataParam))";
        Query q = em.createQuery(hql);
        q.setParameter("vinculoFP", vinculoFP.getId());
        q.setParameter("tipo", TipoCedenciaContratoFP.SEM_ONUS);
        q.setParameter("dataParam", localDateToDate(dataReferencia));
        q.setMaxResults(1);
        try {
            return (Date) q.getSingleResult();

        } catch (NoResultException nr) {
            logger.debug("Nenhum resultado encontrado para recuperaDataCessao " + nr.getMessage());
            return null;

        } catch (Exception nr) {
            nr.printStackTrace();
            return null;
        }
    }

    public void excluirCedenciaUnidadeExterna(CedenciaContratoFP cedenciaContratoFP, SituacaoContratoFP situacaoContratoFP) {
        situacaoContratoFPFacade.remover(situacaoContratoFP);
        SituacaoContratoFP sit = situacaoContratoFPFacade.ultimaSituacaoContratoFP(cedenciaContratoFP.getContratoFP());
        if (sit != null) {
            sit.setFinalVigencia(null);
            situacaoContratoFPFacade.salvar(sit);
        }
        LotacaoFuncional lot = lotacaoFuncionalFacade.buscarUltimaLotacaoVigentePorVinculoFP(cedenciaContratoFP.getContratoFP());
        lot.setFinalVigencia(null);
        lot.getHorarioContratoFP().setFinalVigencia(null);
        lotacaoFuncionalFacade.salvar(lot);
        remover(cedenciaContratoFP);
    }

    public void excluirCedenciaPrefeitura(CedenciaContratoFP cedenciaContratoFP) {
        ContratoFP contrato = contratoFPFacade.recuperar(cedenciaContratoFP.getContratoFP().getId());
        ContratoFP contratoARemover = (ContratoFP) Util.clonarObjeto(contrato);

        cedenciaContratoFP.setContratoFP(null);
        salvar(cedenciaContratoFP);
        contratoFPFacade.remover(contratoARemover);

        cedenciaContratoFP = recuperar(cedenciaContratoFP.getId());
        remover(cedenciaContratoFP);
    }

    public Integer buscarQuantidadeDeServidoresComCedencia(Date inicio) {
        Integer total = 0;
        String hql = "select count(acesso) from CedenciaContratoFP acesso where acesso.dataCadastro = :data ";
        Query q = em.createQuery(hql);
        q.setParameter("data", Util.getDataHoraMinutoSegundoZerado(inicio));
        if (q.getResultList().isEmpty()) {
            return total;
        }
        Long bg = (Long) q.getSingleResult();
        total = bg.intValue();
        return total;
    }

    public List<CedenciaContratoFP> buscarCedenciaContratoFP(Long id) {
        String sql = "select * from CedenciaContratoFP where CONTRATOFP_ID = :idVinculo ";
        Query q = em.createNativeQuery(sql, CedenciaContratoFP.class);
        q.setParameter("idVinculo", id);
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

    public void enviarS2231ESocial(ConfiguracaoEmpregadorESocial empregador, CedenciaContratoFP cedencia, TipoCessao2231 tipoCessao2231) throws ValidacaoException {
        s2231Service.enviarS2231(empregador, cedencia, sistemaFacade.getDataOperacao(), tipoCessao2231);
        if (registroESocialFacade.hasEventoEnviado(TipoArquivoESocial.S2231, cedencia.getId().toString(), empregador)) {
            cedencia.setEnviadoIniCessaoESocial(Boolean.TRUE);
            if (TipoCessao2231.FIM_CESSAO.equals(tipoCessao2231)) {
                cedencia.setEnviadoFimCessaoESocial(Boolean.TRUE);
            }
            em.merge(cedencia);
        }
    }

    public List<CedenciaContratoFP> buscarCedenciasSemRetornoDeCedencia(String filtro) {
        String sql = "  select distinct cedencia.id from lotacaofuncional lotacao  " +
            "inner join vinculofp vinculo on vinculo.id = lotacao.vinculofp_id   " +
            "inner join contratofp contrato on contrato.id = vinculo.id  " +
            "inner join cedenciacontratofp cedencia on cedencia.contratofp_id = contrato.id " +
            "inner join matriculafp matricula on matricula.id = vinculo.matriculafp_id  " +
            "inner join pessoafisica pessoa on matricula.pessoa_id = pessoa.id  " +
            "where (lower(pessoa.nome) like :filtro  " +
            "       or lower(pessoa.cpf) like :filtro  " +
            "       or lower(matricula.matricula) like :filtro)  " +
            "  and contrato.id in (  " +
            "    select ced.contratofp_id  " +
            "    from cedenciacontratofp ced  " +
            "    where contrato.id = vinculo.id  )  " +
            "  and pessoa.id not in ( select obito.pessoafisica_id  from registrodeobito obito) " +
            " and not exists (select 1 from retornocedenciacontratofp r where r.cedenciacontratofp_id = cedencia.id ) ";


        Query q = em.createNativeQuery(sql);
        q.setMaxResults(10);
        q.setParameter("filtro", "%" + filtro.toLowerCase() + "%");
        List resultList = q.getResultList();
        List<CedenciaContratoFP> cedencias = com.google.common.collect.Lists.newArrayList();

        if (!resultList.isEmpty()) {
            for (BigDecimal id : (List<BigDecimal>) q.getResultList()) {
                cedencias.add(em.find(CedenciaContratoFP.class, id.longValue()));
            }
            return cedencias;
        }
        return null;
    }

    public List<Long> buscarIdsCedenciasPorContratoFP(ContratoFP contratoFP) {
        String sql = " select cedencia.id " +
            " from cedenciacontratofp cedencia " +
            " where cedencia.contratofp_id = :contrato " +
            "  and cedencia.finalidadecedenciacontratofp = :finalidade " +
            " order by cedencia.id desc ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("contrato", contratoFP.getId());
        q.setParameter("finalidade", FinalidadeCedenciaContratoFP.EXTERNA.name());
        List<BigDecimal> resultList = q.getResultList();
        List<Long> retorno = Lists.newArrayList();
        if (resultList != null && !resultList.isEmpty()) {
            for (BigDecimal resultado : resultList) {
                retorno.add(resultado.longValue());
            }
        }
        return retorno;
    }
}
