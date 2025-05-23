package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AlvaraProcessoSuspensaoCassacao;
import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.ProcessoSuspensaoCassacaoAlvara;
import br.com.webpublico.entidadesauxiliares.VOAlvara;
import br.com.webpublico.enums.SituacaoProcessoSuspensaoCassacaoAlvara;
import br.com.webpublico.enums.TipoProcessoSuspensaoCassacaoAlvara;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

@Stateless
public class ProcessoSuspensaoCassacaoAlvaraFacade extends AbstractFacade<ProcessoSuspensaoCassacaoAlvara> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private CalculoAlvaraFacade calculoAlvaraFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;

    public ProcessoSuspensaoCassacaoAlvaraFacade() {
        super(ProcessoSuspensaoCassacaoAlvara.class);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public CalculoAlvaraFacade getCalculoAlvaraFacade() {
        return calculoAlvaraFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    @Override
    public void salvarNovo(ProcessoSuspensaoCassacaoAlvara entity) {
        if (entity.getId() == null) entity.setCodigo(buscarProximoCodigo());
        super.salvar(entity);
    }

    @Override
    public ProcessoSuspensaoCassacaoAlvara salvarRetornando(ProcessoSuspensaoCassacaoAlvara entity) {
        if (entity.getId() == null) entity.setCodigo(buscarProximoCodigo());
        return super.salvarRetornando(entity);
    }

    public String sqlEmissaoAlvaraSuspensa(String idCmc) {
        return " FROM ALVARAPROCESSOSUSPENSAOCASSACAO ALVARAPROC " +
            "   INNER JOIN PROCESSOSUSPENSAOCASSACAOALVARA PROC ON ALVARAPROC.PROCESSO_ID = PROC.ID " +
            "   INNER JOIN ALVARA A ON ALVARAPROC.ALVARA_ID = A.ID " +
            "   WHERE A.CADASTROECONOMICO_ID = " + idCmc +
            "   AND PROC.TIPOPROCESSO = '" + TipoProcessoSuspensaoCassacaoAlvara.SUSPENSAO.name() + "'" +
            "   AND PROC.SITUACAO = '" + SituacaoProcessoSuspensaoCassacaoAlvara.EFETIVADO.name() + "'";
    }

    public String sqlAlvaraCassado(String idAlvara) {
        return " FROM ALVARAPROCESSOSUSPENSAOCASSACAO ALVARAPROC " +
            "                      INNER JOIN PROCESSOSUSPENSAOCASSACAOALVARA PROC ON ALVARAPROC.PROCESSO_ID = PROC.ID " +
            "                      INNER JOIN ALVARA A ON ALVARAPROC.ALVARA_ID = A.ID " +
            "                      WHERE PROC.SITUACAO =  '" + SituacaoProcessoSuspensaoCassacaoAlvara.ENCERRADO.name() + "'" +
            "                      AND PROC.TIPOPROCESSO = '" + TipoProcessoSuspensaoCassacaoAlvara.CASSACAO.name() + "'" +
            "                      AND A.ID = " + idAlvara;
    }

    @Override
    public ProcessoSuspensaoCassacaoAlvara recuperar(Object id) {
        ProcessoSuspensaoCassacaoAlvara processo = super.recuperar(id);
        Hibernate.initialize(processo.getAlvaras());
        if (processo.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(processo.getDetentorArquivoComposicao().getArquivosComposicao());
        }
        for (AlvaraProcessoSuspensaoCassacao suspensaoAlvara : processo.getAlvaras()) {
            if (suspensaoAlvara.getAlvara() != null) {
                VOAlvara vo = calculoAlvaraFacade.preencherVOAlvaraPorIdAlvara(suspensaoAlvara.getAlvara().getId());
                vo.setExercicio(exercicioFacade.recuperarExercicioPeloAno(vo.getAno()));
                calculoAlvaraFacade.definirPermissaoImpressao(vo, configuracaoTributarioFacade.retornaUltimo(), null, null);
                suspensaoAlvara.setVoAlvara(vo);
            }
        }
        return processo;
    }

    public void efetivarProcesso(ProcessoSuspensaoCassacaoAlvara processo) {
        if (TipoProcessoSuspensaoCassacaoAlvara.CASSACAO.equals(processo.getTipoProcesso())) {
            encerrarProcesso(processo);
        } else {
            processo.setSituacao(SituacaoProcessoSuspensaoCassacaoAlvara.EFETIVADO);
            em.merge(processo);
        }
    }

    public void encerrarProcesso(ProcessoSuspensaoCassacaoAlvara processo) {
        processo.setSituacao(SituacaoProcessoSuspensaoCassacaoAlvara.ENCERRADO);
        em.merge(processo);
    }

    public List<CadastroEconomico> buscarCmcPorRazaoSocialAndCnpj(String parte) {
        return cadastroEconomicoFacade.buscarCMCsPorNomeRazaoSocialOrCpfCnpjOrSituacao(parte);
    }

    private Long buscarProximoCodigo() {
        String sql = " SELECT (COALESCE(MAX(CODIGO), 0) + 1) AS CODIGO FROM PROCESSOSUSPENSAOCASSACAOALVARA ";
        Query q = em.createNativeQuery(sql);
        BigDecimal result = (BigDecimal) q.getSingleResult();
        return result.longValue();
    }

    public boolean alvaraCassado(Long idAlvara) {
        String sql = " SELECT COUNT(PROC.ID) " + sqlAlvaraCassado(idAlvara.toString());
        Query q = em.createNativeQuery(sql);
        BigDecimal result = (BigDecimal) q.getSingleResult();
        return result.longValue() != 0;
    }

    public boolean emissaoAlvaraSuspensa(Long idCmc) {
        String sql = " SELECT COUNT(PROC.ID) " + sqlEmissaoAlvaraSuspensa(idCmc.toString());
        Query q = em.createNativeQuery(sql);
        BigDecimal result = (BigDecimal) q.getSingleResult();
        return result.longValue() != 0;
    }

    public boolean verificarSituacaoProcesso(Long idProcesso, SituacaoProcessoSuspensaoCassacaoAlvara situacao) {
        String sql = " SELECT * FROM PROCESSOSUSPENSAOCASSACAOALVARA WHERE ID = :idProcesso ";
        Query q = em.createNativeQuery(sql, ProcessoSuspensaoCassacaoAlvara.class);
        q.setParameter("idProcesso", idProcesso);
        List<ProcessoSuspensaoCassacaoAlvara> result = q.getResultList();
        if (result.isEmpty()) return false;
        return situacao.equals(result.get(0).getSituacao());
    }
}
