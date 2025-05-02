package br.com.webpublico.negocios.rh.executores;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoPeriodoAquisitivo;
import br.com.webpublico.exception.rh.SemBasePeriodoAquisitivoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.AssistenteBarraProgresso;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PeriodoAquisitivoFLExecutor implements Callable<AssistenteBarraProgresso> {

    private final Logger logger = LoggerFactory.getLogger(PeriodoAquisitivoFLExecutor.class);
    private final PeriodoAquisitivoFLFacade periodoAquisitivoFLFacade;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private List<ContratoFP> contratoFPs;
    private List<String> erros;
    private Date dataOperacao;
    private TipoPeriodoAquisitivo tipoPeriodoAquisitivo;

    public PeriodoAquisitivoFLExecutor(PeriodoAquisitivoFLFacade periodoAquisitivoFLFacade) {
        this.periodoAquisitivoFLFacade = periodoAquisitivoFLFacade;
    }

    public Future<AssistenteBarraProgresso> execute(List<ContratoFP> contratoFPs, AssistenteBarraProgresso assistenteBarraProgresso, Date dataOperacao, TipoPeriodoAquisitivo tipoPeriodoAquisitivo) {
        this.contratoFPs = contratoFPs;
        this.assistenteBarraProgresso = assistenteBarraProgresso;
        this.dataOperacao = dataOperacao;
        this.erros = Lists.newArrayList();
        this.tipoPeriodoAquisitivo = tipoPeriodoAquisitivo;
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<AssistenteBarraProgresso> submit = executorService.submit(this);
        executorService.shutdown();
        return submit;
    }

    public AssistenteBarraProgresso call() throws Exception {
        try {
            for (ContratoFP contratoFP : contratoFPs) {
                try {
                    periodoAquisitivoFLFacade.gerarPeriodosAquisitivos(contratoFP, dataOperacao, tipoPeriodoAquisitivo);
                    assistenteBarraProgresso.conta();
                } catch (SemBasePeriodoAquisitivoException sbex) {
                    logger.error("Erro ao gerar perídos aquisitivos {}", sbex);
                    erros.add(sbex.getMessage());
                } catch (ExcecaoNegocioGenerica ex) {
                    erros.add(ex.getMessage());
                }
            }
            assistenteBarraProgresso.setSelecionado(erros);
            return assistenteBarraProgresso;
        } catch (Exception ex) {
            logger.error("Erro ao gerar/atualizar períodos aquisitivos {}", ex);
        }
        return null;
    }
}
