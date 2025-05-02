/*
 * Codigo gerado automaticamente em Tue Feb 07 08:22:09 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.esocial.service.S2298Service;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Stateless
public class ReintegracaoFacade extends AbstractFacade<Reintegracao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ContratoFPFacade contratoFPFacade;

    private S2298Service s2298Service;

    public ReintegracaoFacade() {
        super(Reintegracao.class);
    }

    @PostConstruct
    public void init() {
        s2298Service = (S2298Service) Util.getSpringBeanPeloNome("s2298Service");
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    private void mergeObject(Object obj) {
        if (obj != null) {
            em.merge(obj);
        }
    }

    private void salvarRegistrosAteriores(Reintegracao r) {
        mergeObject(r.getEnquadramentoAnterior());
        mergeObject(r.getPrevidenciaAnterior());
        mergeObject(r.getOpcaoValeTransporteAnterior());
        mergeObject(r.getHorarioAnterior());
        mergeObject(r.getSindicatoAnterior());
        mergeObject(r.getAssociacaoAnterior());
        mergeObject(r.getSituacaoAnterior());
        mergeObject(r.getLotacaoFuncionalAnterior());
    }

    @Override
    public void salvar(Reintegracao reintegracaor) {
        salvarRegistrosAteriores(reintegracaor);
        super.salvar(reintegracaor);
    }

    @Override
    public void salvarNovo(Reintegracao reintegracaor) {
        tornarFimVigenciaContratoFPNull(reintegracaor);
        reintegracaor.setContratoFP(tornarFimVigenciaContratoFPNull(reintegracaor));
        salvarRegistrosAteriores(reintegracaor);
        super.salvarNovo(reintegracaor);
        ConfiguracaoEmpregadorESocial empregador = contratoFPFacade.buscarEmpregadorPorVinculoFP(reintegracaor.getContratoFP());
    }

    private ContratoFP tornarFimVigenciaContratoFPNull(Reintegracao r) {
        ContratoFP c = em.find(r.getContratoFP().getClass(), r.getContratoFP().getId());
        c.setFinalVigencia(null);
        return c;
    }

    private void reverterFimDeVigenciaContratoFP(ContratoFP c, Date dataReferencia) {
        c.setFinalVigencia(dataReferencia);
        em.merge(c);
    }

    private void removerObjeto(Object obj, Long id) {
        if (obj == null || id == null) {
            return;
        }
        obj = em.find(obj.getClass(), id);
        em.remove(obj);
    }

    private void removerObjetoEspecial(Object obj, Long id) {
        System.out.println("OBJ..: " + obj);
        System.out.println("ID...: " + id);
        if (obj == null || id == null) {
            return;
        }
        obj = em.find(obj.getClass(), id);
        System.out.println("OBJ ENCONTRADO...: " + obj);
        em.remove(obj);
    }

    private Reintegracao removerRegistrosRelacionadosComReintegracao(Reintegracao r) {
        r.setEnquadramentoFuncional(null);
        r.setPrevidenciaVinculoFP(null);
        r.setOpcaoValeTransporteFP(null);
        r.setHorarioContratoFP(null);
        r.setSindicatoVinculoFP(null);
        r.setAssociacaoVinculoFP(null);
        r.setLotacaoFuncional(null);
        return em.merge(r);
    }

    @Override
    public void remover(Reintegracao r) {
        ContratoFP c = em.find(r.getContratoFP().getClass(), r.getContratoFP().getId());
        c.setFinalVigencia(r.getFimVigenciaAnterior());
        r.setContratoFP(c);
        salvarRegistrosAteriores(r);
        r = em.find(r.getClass(), r.getId());
        em.remove(r);
    }

    public List<ContratoFP> listaFiltrandoContratosRescindidosExonerados(String s, Long... modalidades) {
        Query q = em.createQuery(" select contrato from ContratoFP contrato "
            + " inner join contrato.matriculaFP matricula "
            + " inner join contrato.situacaoFuncionals sit "
            + " where (sit.situacaoFuncional.codigo = :situacaoExoneracaoRescisao or sit.situacaoFuncional.codigo = :situacaoAposentado)" // OK
            + " and contrato.matriculaFP.pessoa not in (select ro.pessoaFisica from RegistroDeObito ro) " // OK
            + " and contrato in( select vinculoFP from ProvimentoFP exonerado "
            + " inner join exonerado.vinculoFP vinculoFP "
            + " inner join exonerado.tipoProvimento tipo "
            + " where (tipo.codigo = :exoneracaoCarreira or tipo.codigo = :exoneracaoComissao)"
            + " and vinculoFP = contrato and exonerado.sequencia > "
            + " coalesce((select max(reintegrado.sequencia) from ProvimentoFP reintegrado "
            + " inner join reintegrado.vinculoFP vinculoFP "
            + " inner join reintegrado.tipoProvimento tipo "
            + " where tipo.codigo = :provimentoReintegracao and vinculoFP = contrato),0) "
            + " and contrato.modalidadeContratoFP.codigo in :modalidades "
            + " and ((lower(matricula.pessoa.nome) like :filtro) "
            + " OR (lower(matricula.pessoa.cpf) like :filtro) "
            + " OR (lower(matricula.matricula) like :filtro)))");

        q.setParameter("modalidades", Arrays.asList(modalidades));
        q.setParameter("situacaoExoneracaoRescisao", SituacaoFuncional.EXONERADO_RESCISO);
        q.setParameter("situacaoAposentado", SituacaoFuncional.APOSENTADO);
        q.setParameter("provimentoReintegracao", TipoProvimento.REINTEGRACAO);
        q.setParameter("exoneracaoCarreira", TipoProvimento.EXONERACAORESCISAO_CARREIRA);
        q.setParameter("exoneracaoComissao", TipoProvimento.EXONERACAORESCISAO_COMISSAO);
        q.setParameter("filtro", "%" + s.toLowerCase().trim() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public void enviarS2298ESocial(ConfiguracaoEmpregadorESocial empregador, Reintegracao reintegracao) throws ValidacaoException {
        s2298Service.enviarS2298(empregador, reintegracao);
    }

    public List<SituacaoContratoFP> getSituacoesExoneradoAntesDaReintegracao(VinculoFP vinculo) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select situacao.* ");
        sql.append(" FROM SITUACAOCONTRATOFP situacao ");
        sql.append(" INNER JOIN SITUACAOFUNCIONAL situacaofuncional ");
        sql.append(" ON situacao.SITUACAOFUNCIONAL_ID = situacaofuncional.id ");
        sql.append(" INNER JOIN vinculofp vinculo ON vinculo.id                = situacao.CONTRATOFP_ID ");
        sql.append(" inner join REINTEGRACAO reintegracao on reintegracao.CONTRATOFP_ID = vinculo.id ");
        sql.append(" WHERE vinculo.id             = :vinculoFP and vinculo.FINALVIGENCIA is null ");
        sql.append(" AND situacaofuncional.codigo = :exonerado");
        Query q = em.createNativeQuery(sql.toString(), SituacaoContratoFP.class);
        q.setParameter("vinculoFP", vinculo.getId());
        q.setParameter("exonerado", SituacaoFuncional.EXONERADO_RESCISO);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return null;
    }

    public Reintegracao recuperarUltimaReintegracao(VinculoFP vinculo) {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from REINTEGRACAO where CONTRATOFP_ID = :idVinculo order by DATAREINTEGRACAO desc");
        Query q = em.createNativeQuery(sql.toString(), Reintegracao.class);
        q.setParameter("idVinculo", vinculo.getId());
        if (!q.getResultList().isEmpty()) {
            return (Reintegracao) q.getResultList().get(0);
        }
        return null;
    }

    public boolean podeExcluir(ContratoFP contrato, Date data) {
        if (contrato == null || contrato.getId() == null) {
            return true;
        } else {
            Query query = em.createQuery("select folha from FolhaDePagamento folha inner join folha.fichaFinanceiraFPs ficha "
                + " where ficha.vinculoFP = :vinculo and "
                + " folha.calculadaEm >= :param");
            query.setParameter("param", data);
            query.setParameter("vinculo", contrato);
            query.setMaxResults(1);
            if (!query.getResultList().isEmpty()) {
                FolhaDePagamento folha = (FolhaDePagamento) query.getResultList().get(0);
                if (folha.getMes().getNumeroMes() == DataUtil.getMes(data) && folha.getAno() == DataUtil.getAno(data)) {
                    return false;
                }
            }
            return true;
        }
    }

    public Reintegracao buscarReintegracaoParaContrato(Long contratoId, Long rescisaoId) {
        String hql = " from Reintegracao r " +
            " where r.contratoFP.id = :contratoId " +
            " and r.exoneracaoRescisao.id = :rescisaoId " +
            " order by r.dataReintegracao";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("contratoId", contratoId);
        q.setParameter("rescisaoId", rescisaoId);

        List<Reintegracao> lista = q.getResultList();
        if (lista != null && !lista.isEmpty()) {
            return lista.get(0);
        }
        return null;
    }

    public List<Reintegracao> buscarReintegracoesPorContrato(ContratoFP contrato) {
        String sql = " select r.* from REINTEGRACAO r " +
            " where r.CONTRATOFP_ID = :contrato ";
        Query q = em.createNativeQuery(sql, Reintegracao.class);
        q.setParameter("contrato", contrato.getId());
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

    public Reintegracao buscarReintegracaoParaContratoNaCompetencia(Long contratoId, Integer mes, Integer ano) {
        String hql = " from Reintegracao r " +
            " where r.contratoFP.id = :contratoId " +
            " and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') = to_date(to_char(r.dataReintegracao,'mm/yyyy'),'mm/yyyy') " +
            " order by r.dataReintegracao desc ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("contratoId", contratoId);
        q.setParameter("dataVigencia", DataUtil.criarDataPrimeiroDiaMes(mes, ano));

        List<Reintegracao> lista = q.getResultList();
        if (lista != null && !lista.isEmpty()) {
            return lista.get(0);
        }
        return null;
    }
}
