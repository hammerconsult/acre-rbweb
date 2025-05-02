package br.com.webpublico.negocios.rh.cadastrofuncional;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.SituacaoContratoFPBkp;
import br.com.webpublico.entidades.rh.cadastrofuncional.ReprocessamentoSituacaoFuncional;
import br.com.webpublico.entidades.rh.cadastrofuncional.ReprocessamentoSituacaoFuncionalVinculo;
import br.com.webpublico.enums.TipoPeriodoAquisitivo;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ReprocessamentoSituacaoFuncionalExecutor implements Callable<AssistenteBarraProgresso> {

    private final Logger logger = LoggerFactory.getLogger(ReprocessamentoSituacaoFuncionalExecutor.class);
    private final SituacaoContratoFPFacade situacaoContratoFPFacade;
    private final ReprocessamentoSituacaoFuncionalFacade reprocessamentoSituacaoFuncionalFacade;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private List<VinculoFP> itemVinculoFP;
    private ReprocessamentoSituacaoFuncional reprocessamento;

    public ReprocessamentoSituacaoFuncionalExecutor(SituacaoContratoFPFacade situacaoContratoFPFacade,
                                                    ReprocessamentoSituacaoFuncionalFacade reprocessamentoSituacaoFuncionalFacade) {
        this.situacaoContratoFPFacade = situacaoContratoFPFacade;
        this.reprocessamentoSituacaoFuncionalFacade = reprocessamentoSituacaoFuncionalFacade;
    }

    public Future<AssistenteBarraProgresso> execute(List<VinculoFP> itemVinculoFP, ReprocessamentoSituacaoFuncional reprocessamento, AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
        this.itemVinculoFP = itemVinculoFP;
        this.reprocessamento = reprocessamento;
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<AssistenteBarraProgresso> submit = executorService.submit(this);
        executorService.shutdown();
        return submit;
    }

    public AssistenteBarraProgresso call() throws Exception {
        try {
            SituacaoFuncional situacaoExonerado = situacaoContratoFPFacade.buscarSituacaoFuncionalPorCodigo(6);
            SituacaoFuncional situacaoCedidoADesposicao = situacaoContratoFPFacade.buscarSituacaoFuncionalPorCodigo(4);
            SituacaoFuncional situacaoAfastado = situacaoContratoFPFacade.buscarSituacaoFuncionalPorCodigo(3);
            SituacaoFuncional situacaoFerias = situacaoContratoFPFacade.buscarSituacaoFuncionalPorCodigo(2);
            SituacaoFuncional situacaoExercicio = situacaoContratoFPFacade.buscarSituacaoFuncionalPorCodigo(1);
            SituacaoFuncional situacaoAposentado = situacaoContratoFPFacade.buscarSituacaoFuncionalPorCodigo(7);

            for (VinculoFP vinculo : itemVinculoFP) {
                ReprocessamentoSituacaoFuncionalVinculo reprocessamentoVinculo = new ReprocessamentoSituacaoFuncionalVinculo();
                reprocessamentoSituacaoFuncionalFacade.reprocessarSituacoesFuncionais(vinculo, situacaoExonerado, situacaoCedidoADesposicao, situacaoAfastado, situacaoFerias, situacaoExercicio, situacaoAposentado);
                reprocessamentoVinculo.setReprocessamento(reprocessamento);
                reprocessamentoVinculo.setVinculoFP(vinculo);
                reprocessamento.getReprocessamentoVinculo().add(reprocessamentoVinculo);
                assistenteBarraProgresso.conta();
            }
            return assistenteBarraProgresso;
        } catch (Exception ex) {
            logger.error("Erro ao reprocessar situações funcionais {}", ex);
        }
        return null;
    }
}
