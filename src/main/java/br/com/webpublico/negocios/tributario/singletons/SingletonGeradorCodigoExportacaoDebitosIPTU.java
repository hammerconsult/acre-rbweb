package br.com.webpublico.negocios.tributario.singletons;

import br.com.webpublico.controle.ExportacaoIPTUControlador;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.Processo;
import br.com.webpublico.entidades.ProcessoExportacaoIPTU;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.SistemaFacade;
import com.google.common.collect.Maps;
import org.primefaces.model.StreamedContent;

import javax.ejb.AccessTimeout;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * @author octavio
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@AccessTimeout(5000)
public class SingletonGeradorCodigoExportacaoDebitosIPTU implements Serializable {

    @PersistenceContext(name = "webpublicoPU")
    private EntityManager em;

    private Map<ExportacaoIPTUControlador.AssistenteBarraProgressoExportacaoIPTU, Future<ProcessoExportacaoIPTU>> processoExpotacaoEmExecucao;

    private Map<SistemaFacade.PerfilApp, Map<Exercicio, Long>> mapa = Maps.newHashMap();


    public Long getProximoCodigo(SistemaFacade.PerfilApp tipo, Exercicio exercicio) {

        if (!mapa.containsKey(tipo)) {
            Map<Exercicio, Long> novoMapa = Maps.newHashMap();
            novoMapa.put(exercicio, buscarUltimoCodigoExercicio(exercicio));
            mapa.put(tipo, novoMapa);
        }
        if (!mapa.get(tipo).containsKey(exercicio)) {
            mapa.get(tipo).put(exercicio, buscarUltimoCodigoExercicio(exercicio));
        }
        mapa.get(tipo).put(exercicio, mapa.get(tipo).get(exercicio) + 1);

        return mapa.get(tipo).get(exercicio);
    }

    private Long buscarUltimoCodigoExercicio(Exercicio ex) {
        StringBuilder sql = new StringBuilder();

        sql.append(" select coalesce(max(numeroRemessa), 0) as numero  ")
            .append(" from ExportacaoDebitosIPTU EXP ")
            .append(" inner join exercicio ex on ex.id = exp.exercicio_id ")
            .append(" where ex.ano = :ano ");

        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("ano", ex.getAno());
        if (!q.getResultList().isEmpty()) {
            return ((Number) q.getResultList().get(0)).longValue();
        }
        return 0L;
    }

    public Map<ExportacaoIPTUControlador.AssistenteBarraProgressoExportacaoIPTU, Future<ProcessoExportacaoIPTU>> getProcessoExpotacaoEmExecucao() {
        return processoExpotacaoEmExecucao;
    }

    public void limparProcessoEmExecucao() {
        this.processoExpotacaoEmExecucao = null;
    }

    public void setProcessoExpotacaoEmExecucao(ExportacaoIPTUControlador.AssistenteBarraProgressoExportacaoIPTU processoExpotacaoEmExecucao,
                                              Future<ProcessoExportacaoIPTU> future) {
        if (this.processoExpotacaoEmExecucao != null) {
            throw new ValidacaoException("Já existem um processo em execução");
        }
        this.processoExpotacaoEmExecucao = Maps.newHashMap();
        this.processoExpotacaoEmExecucao.put(processoExpotacaoEmExecucao, future);
    }
}
