package br.com.webpublico.negocios.tributario.services;

import br.com.webpublico.entidades.EmpresaIrregular;
import br.com.webpublico.entidades.MalaDiretaMonitoramentoFiscal;
import br.com.webpublico.entidades.MonitoramentoFiscal;
import br.com.webpublico.entidades.SituacaoEmpresaIrregular;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.MonitoramentoFiscalFacade;
import br.com.webpublico.util.DataUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ServiceMonitoramentoFiscal {

    private static final Logger logger = LoggerFactory.getLogger(ServiceMonitoramentoFiscal.class);
    private MonitoramentoFiscalFacade monitoramentoFiscalFacade;
    @PersistenceContext
    protected transient EntityManager em;

    @PostConstruct
    private void init() {
        try {
            monitoramentoFiscalFacade = (MonitoramentoFiscalFacade) new InitialContext().lookup("java:module/MonitoramentoFiscalFacade");
        } catch (NamingException e) {
            logger.error("{]", e);
        }
    }

    public void processarVerificacaoROL() {
        List<MonitoramentoFiscal> monitoramentos = monitoramentoFiscalFacade.buscarMonitoramentosComunicadosOuFinalizados();
        for (MonitoramentoFiscal monitoramento : monitoramentos) {
            for (MalaDiretaMonitoramentoFiscal malaDiretaMonitoramentoFiscal : monitoramento.getMalasDiretasMonitoramentoFiscal()) {
                if (DataUtil.dataSemHorario(new Date()).compareTo(DataUtil.dataSemHorario(malaDiretaMonitoramentoFiscal.getDataRegularizacao())) >= 0) {
                    try {
                        EmpresaIrregular empresaIrregular = new EmpresaIrregular();
                        empresaIrregular.setMonitoramentoFiscal(monitoramento);
                        if (monitoramento.getEmpresaIrregular() == null || !monitoramento.getEmpresaIrregular()) {
                            empresaIrregular.setSituacao(SituacaoEmpresaIrregular.Situacao.RETIRADA);
                        } else {
                            empresaIrregular.setSituacao(SituacaoEmpresaIrregular.Situacao.INSERIDA);
                        }
                        empresaIrregular.setCadastroEconomico(monitoramento.getCadastroEconomico());
                        empresaIrregular.setJsonCadastroEconomicoDTO(empresaIrregular.getJsonCadastroEconomicoDTO());
                        empresaIrregular.setIrregularidade(monitoramento.getIrregularidade());
                        empresaIrregular.setObservacao(monitoramento.getObservacaoIrregularidade());
                        empresaIrregular.setUsuarioSistema(monitoramento.getUsuarioIrregularidade());
                        empresaIrregular.setExercicio(monitoramentoFiscalFacade.getExercicioFacade().recuperarExercicioPeloAno(DataUtil.getAno(monitoramento.getOrdemGeralMonitoramento().getDataInicial())));
                        monitoramentoFiscalFacade.getEmpresaIrregularFacade().salvar(empresaIrregular, false);
                    } catch (ValidacaoException ve) {
                        //ignora o erro
                    }
                }
            }
        }
    }

}
