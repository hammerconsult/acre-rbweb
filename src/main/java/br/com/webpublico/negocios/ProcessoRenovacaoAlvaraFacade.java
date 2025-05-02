package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.SituacaoCalculoAlvara;
import br.com.webpublico.exception.ValidacaoException;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ProcessoRenovacaoAlvaraFacade {

    protected static final Logger logger = LoggerFactory.getLogger(ProcessoRenovacaoAlvaraFacade.class);
    @EJB
    private CalculoAlvaraFacade calculoAlvaraFacade;
    @EJB
    private CNAEFacade cnaeFacade;
    @Resource
    private SessionContext context;
    @Resource
    private UserTransaction userTransaction;

    public ConfiguracaoTributario getConfiguracaoTributario() {
        return calculoAlvaraFacade.buscarConfiguracaoTriutarioVigente();
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<AssistenteRenovacaoAlvara> gerarAlvarasNovo(AssistenteRenovacaoAlvara assistente) {

        userTransaction = context.getUserTransaction();
        List<ProcessoCalculoAlvara> processos = Lists.newArrayList();
        if (assistente.getExercicio() != null) {
            for (CadastroEconomico cadastro : assistente.getCadastros()) {
                if (cadastro.getAreaUtilizacao() > 0 && cadastro.getAbertura() != null) {
                    try {
                        userTransaction.begin();
                        ProcessoCalculoAlvara processoCalculoAlvara = criarProcesoCalculoAlvara(assistente.getExercicio(), assistente.getUsuarioSistema(), cadastro);
                        criarCnaesProcessoCalculoAlvara(cadastro, processoCalculoAlvara);

                        ValidacaoException ve = new ValidacaoException();
                        calculoAlvaraFacade.validarCalculo(processoCalculoAlvara, ve, false);

                        if (!ve.getMensagens().isEmpty()) {
                            if (!assistente.getMapaInconsistencia().containsKey(cadastro)) {
                                assistente.getMapaInconsistencia().put(cadastro, Lists.newArrayList(ve.getMensagens()));
                            }
                        } else {
                            processoCalculoAlvara = calculoAlvaraFacade.calcularAlvara(processoCalculoAlvara, assistente.getExercicio(), false, null, assistente.getConfiguracaoTributario());
                            processoCalculoAlvara = calculoAlvaraFacade.efetivarCalculo(processoCalculoAlvara, assistente.getExercicio());
                            processos.add(processoCalculoAlvara);
                        }
                        userTransaction.commit();
                    } catch (Exception e) {
                        try {
                            userTransaction.rollback();
                        } catch (SystemException se) {
                            logger.error("Erro: ", se);
                        }
                    }
                }
                assistente.conta();
            }
        }
        return new AsyncResult<>(assistente);
    }

    private void criarCnaesProcessoCalculoAlvara(CadastroEconomico cadastro, ProcessoCalculoAlvara processoCalculoAlvara) {
        for (EconomicoCNAE economicoCNAE : calculoAlvaraFacade.buscarCnaesVigentesCMC(cadastro.getId())) {
            CNAEProcessoCalculoAlvara cnaeProcesso = new CNAEProcessoCalculoAlvara();
            cnaeProcesso.setProcessoCalculoAlvara(processoCalculoAlvara);
            cnaeProcesso.setCnae(economicoCNAE.getCnae());
            cnaeProcesso.setTipoCnae(economicoCNAE.getTipo());
            cnaeProcesso.setHorarioFuncionamento(economicoCNAE.getHorarioFuncionamento());
            processoCalculoAlvara.getCnaes().add(cnaeProcesso);
        }
    }

    private ProcessoCalculoAlvara criarProcesoCalculoAlvara(Exercicio exercicioCorrente, UsuarioSistema usuarioCorrente, CadastroEconomico cadastro) {
        ProcessoCalculoAlvara processoCalculoAlvara = new ProcessoCalculoAlvara();
        processoCalculoAlvara.setCadastroEconomico(cadastro);
        processoCalculoAlvara.setSituacaoCalculoAlvara(SituacaoCalculoAlvara.EM_ABERTO);
        processoCalculoAlvara.setExercicio(exercicioCorrente);
        processoCalculoAlvara.setUsuario(usuarioCorrente);
        processoCalculoAlvara.setDataLancamento(new Date());
        processoCalculoAlvara.setCodigo(calculoAlvaraFacade.montarProximoCodigoPorExercicio(exercicioCorrente));
        if (calculoAlvaraFacade.hasAlvaraEfetivadoNoExercicio(cadastro.getId(), exercicioCorrente.getId())) {
            processoCalculoAlvara.setRenovacao(true);
        }
        return processoCalculoAlvara;
    }

    public List<VOCnae> buscarCnaesAtivos() {
        return cnaeFacade.buscarCnaesAtivosToVO();
    }
}
