package br.com.webpublico.nfse.controladores;

import br.com.webpublico.entidades.ConfiguracaoAgendamentoTarefa;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ConfiguracaoAgendamentoTarefaFacade;
import br.com.webpublico.negocios.tributario.services.ServiceAgendamentoTarefa;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import org.quartz.CronExpression;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.text.ParseException;
import java.util.List;

@ManagedBean
@ViewScoped
public class ManutencaoAgendamentoControlador {

    private static final Logger logger = LoggerFactory.getLogger(ManutencaoAgendamentoControlador.class);

    @EJB
    private ConfiguracaoAgendamentoTarefaFacade configuracaoAgendamentoTarefaFacade;

    private ConfiguracaoAgendamentoTarefa.TipoTarefaAgendada tipoTarefaAgendada;
    private ConfiguracaoAgendamentoTarefa agendamento;
    private List<ConfiguracaoAgendamentoTarefa> agendamentos;

    public ConfiguracaoAgendamentoTarefa getAgendamento() {
        return agendamento;
    }

    public void setAgendamento(ConfiguracaoAgendamentoTarefa agendamento) {
        this.agendamento = agendamento;
    }

    public List<ConfiguracaoAgendamentoTarefa> getAgendamentos() {
        return agendamentos;
    }

    public void setAgendamentos(List<ConfiguracaoAgendamentoTarefa> agendamentos) {
        this.agendamentos = agendamentos;
    }

    public void init(ConfiguracaoAgendamentoTarefa.TipoTarefaAgendada tipoTarefaAgendada) {
        this.tipoTarefaAgendada = tipoTarefaAgendada;
        buscarAgendamentos();
    }

    private void buscarAgendamentos() {
        agendamentos = configuracaoAgendamentoTarefaFacade.buscarPorTipo(tipoTarefaAgendada);
    }

    private void atualizarAgendamentos() {
        buscarAgendamentos();
        ServiceAgendamentoTarefa serviceAgendamentoTarefa = (ServiceAgendamentoTarefa) Util.getSpringBeanPeloNome("serviceAgendamentoTarefa");
        try {
            serviceAgendamentoTarefa.agendaTarefas();
        } catch (SchedulerException e) {
            logger.error(e.getMessage());
        }
    }

    public void novoAgendamento() {
        agendamento = new ConfiguracaoAgendamentoTarefa();
        agendamento.setTipoTarefaAgendada(tipoTarefaAgendada);
    }

    public void removerAgendamento(ConfiguracaoAgendamentoTarefa agendamento) {
        configuracaoAgendamentoTarefaFacade.remover(agendamento);
        atualizarAgendamentos();
    }

    public void salvarAgendamento() {
        try {
            validarAgendamento();
            configuracaoAgendamentoTarefaFacade.salvar(agendamento);
            atualizarAgendamentos();
            agendamento = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    private void validarAgendamento() {
        ValidacaoException ve = new ValidacaoException();
        try {
            new CronExpression(agendamento.getCron());
        } catch (ParseException e) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Cron inválido.");
        }
        ve.lancarException();
    }
}
