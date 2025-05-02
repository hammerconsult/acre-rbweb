/*
 * Codigo gerado automaticamente em Fri Jul 13 15:21:46 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios.rh.avaliacao;

import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidades.rh.avaliacao.AvaliacaoRH;
import br.com.webpublico.entidades.rh.avaliacao.AvaliacaoVinculoFP;
import br.com.webpublico.entidades.rh.avaliacao.RespostaAvaliacao;
import br.com.webpublico.entidades.rh.avaliacao.ResumoRespostaDTO;
import br.com.webpublico.negocios.AbstractFacade;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Stateless
public class AvaliacaoRHFacade extends AbstractFacade<AvaliacaoRH> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public void salvar(AvaliacaoRH entity) {
        super.salvar(entity);
    }

    @Override
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public void salvarNovo(AvaliacaoRH entity) {
        super.salvarNovo(entity);
    }

    public AvaliacaoRHFacade() {
        super(AvaliacaoRH.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public AvaliacaoRH recuperar(Object id) {
        AvaliacaoRH recuperar = super.recuperar(id);
        return recuperar;
    }

    public List<AvaliacaoVinculoFP> buscarAvaliacaoVinculoDTO(AvaliacaoRH avaliacao) {
        String sql = "select av.id, v.id as idVinculo, mat.MATRICULA||'/'||v.numero||' - '||pf.nome, av.DATAINICIOEXECUCAO, av.DATATERMINOEXECUCAO " +
            "from vinculofp v inner join AvaliacaoVinculoFP av on av.vinculofp_id = v.id " +
            "                 inner join matriculafp mat on mat.id = v.matriculafp_id " +
            "                 inner join pessoafisica pf on pf.id = mat.pessoa_id " +
            "                 inner join avaliacaorh avaliacao on avaliacao.id = av.avaliacaoRH_id " +
            "where av.AVALIACAORH_ID = :idAvaliacao " +
            "order by pf.nome ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idAvaliacao", avaliacao.getId());
        List<AvaliacaoVinculoFP> resumos = new ArrayList<>();
        for (Object[] obj : (List<Object[]>) q.getResultList()) {
            AvaliacaoVinculoFP domain = new AvaliacaoVinculoFP();
            domain.setId(((BigDecimal) obj[0]).longValue());
            Long idVinculo = ((BigDecimal) obj[1]).longValue();
            String descricaoVinculo = (String) obj[2];
            VinculoFP v = new VinculoFP(idVinculo, descricaoVinculo);
            domain.setVinculoFP(v);
            domain.setDataInicioExecucao(((Date) obj[3]));
            domain.setDataTerminoExecucao(((Date) obj[4]));
            domain.setAvaliacaoRH(avaliacao);
            resumos.add(domain);
        }
        return resumos;
    }

    public AvaliacaoVinculoFP recuperarAvaliacaoVinculo(Object id) {
        AvaliacaoVinculoFP avaliacaoVinculoFP = em.find(AvaliacaoVinculoFP.class, id);
        Hibernate.initialize(avaliacaoVinculoFP.getRespostas());
        return avaliacaoVinculoFP;
    }

    public RespostaAvaliacao gravarResposta(RespostaAvaliacao respostaAvaliacao) {
        if (buscarRespostaPorQuestaoAndNivelResposta(respostaAvaliacao.getAvaliacaoVinculoFP().getId(), respostaAvaliacao.getQuestaoAvaliacao().getId(), respostaAvaliacao.getNivelResposta().getId())) {
            em.merge(respostaAvaliacao);
        }
        return respostaAvaliacao;
    }

    public Boolean buscarRespostaPorQuestaoAndNivelResposta(Long avaliacoVinculoFP, Long questao, Long nivelResposta) {
        String sql = "select id from RESPOSTAAVALIACAO where AVALIACAOVINCULOFP_ID = :avaliacaoVinculo and QUESTAOAVALIACAO_ID = :questao and NIVELRESPOSTA_ID = :nivel ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("avaliacaoVinculo", avaliacoVinculoFP);
        q.setParameter("questao", questao);
        q.setParameter("nivel", nivelResposta);
        return q.getResultList().isEmpty();
    }

    public List<AvaliacaoVinculoFP> buscarAvaliacaoVinculo(Long idAvaliacao) {
        String sql = "select av.* " +
            " from vinculofp v inner join AvaliacaoVinculoFP av on av.vinculofp_id = v.id" +
            " inner join avaliacaorh avaliacao on avaliacao.id = av.avaliacaoRH_id " +
            " where av.id = :idAvaliacao and current_date between avaliacao.vigenciaInicial and coalesce(avaliacao.vigenciaFinal, current_date) ";
        Query q = em.createNativeQuery(sql, AvaliacaoVinculoFP.class);
        q.setParameter("idAvaliacao", idAvaliacao);
        return q.getResultList();
    }

    public List<ResumoRespostaDTO> buscarResumoRespostas(Long idAvaliacao) {
        String sql = "select questao.QUESTAO, qa.DESCRICAO as resposta, count(av.ID) as quantidade, " +
            " (count(av.ID) * 100) / (select count(id) from AvaliacaoVinculoFP where avaliacaorh_id = :idAvaliacao) " +
            "from avaliacaorh a " +
            "         inner join AvaliacaoVinculoFP av on av.avaliacaorh_id = a.id " +
            "         inner join RespostaAvaliacao ra on ra.avaliacaovinculofp_id = av.id " +
            "         inner join NIVELRESPOSTA nivelResposta on ra.NIVELRESPOSTA_ID = nivelResposta.ID " +
            "         inner join questaoAvaliacao questao on questao.id = ra.QUESTAOAVALIACAO_ID " +
            "         inner join QUESTAOALTERNATIVA qa " +
            "                    on nivelResposta.ID = qa.NIVELRESPOSTA_ID and questao.ID = qa.QUESTAOAVALIACAO_ID " +
            "                   where a.id = :idAvaliacao " +
            "group by questao.QUESTAO, qa.DESCRICAO ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idAvaliacao", idAvaliacao);
        List<ResumoRespostaDTO> resumos = new ArrayList<>();
        for (Object[] obj : (List<Object[]>) q.getResultList()) {
            ResumoRespostaDTO dto = new ResumoRespostaDTO();
            dto.setQuestao((String) obj[0]);
            dto.setResposta((String) obj[1]);
            dto.setQuantidade(((BigDecimal) obj[2]).intValue());
            dto.setPercentual((BigDecimal) obj[3]);
            resumos.add(dto);
        }
        return resumos;
    }

    public List<AvaliacaoVinculoFP> buscarAvaliacaoVinculos(Long idPessoa) {
        String sql = "select av.* " +
            " from vinculofp v inner join AvaliacaoVinculoFP av on av.vinculofp_id = v.id" +
            " inner join avaliacaorh avaliacao on avaliacao.id = av.avaliacaoRH_id " +
            " inner join matriculaFP mat on mat.id = v.matriculaFP_id " +
            " where mat.pessoa_id = :idPessoa and current_date between avaliacao.vigenciaInicial and coalesce(avaliacao.vigenciaFinal, current_date) ";
        Query q = em.createNativeQuery(sql, AvaliacaoVinculoFP.class);
        q.setParameter("idPessoa", idPessoa);
        return q.getResultList();
    }

    public Boolean verificarAvaliacaoRespondida(AvaliacaoVinculoFP avaliacao) {
        String sql = "select av.* " +
            " from vinculofp v inner join AvaliacaoVinculoFP av on av.vinculofp_id = v.id" +
            " inner join avaliacaorh avaliacao on avaliacao.id = av.avaliacaoRH_id " +
            "   inner join RespostaAvaliacao ra on ra.avaliacaovinculofp_id = av.id " +
            " where av.id = :idAvaliacao ";
        Query q = em.createNativeQuery(sql, AvaliacaoVinculoFP.class);
        q.setParameter("idAvaliacao", avaliacao.getId());
        return !q.getResultList().isEmpty();
    }

}
