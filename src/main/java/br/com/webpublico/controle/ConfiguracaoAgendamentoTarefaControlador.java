package br.com.webpublico.controle;

import br.com.webpublico.entidades.ConfiguracaoAgendamentoTarefa;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfiguracaoAgendamentoTarefaFacade;
import br.com.webpublico.negocios.tributario.services.ServiceAgendamentoTarefa;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

import static br.com.webpublico.negocios.tributario.services.ServiceAgendamentoTarefa.APP_PERFIL_DESATIVAR_ROTINA_AGENDADA;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 10/04/14
 * Time: 18:20
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "editar-configuracao-agendamento-tarefa",
        pattern = "/configuracao-agendamento-tarefas/",
        viewId = "/faces/admin/configuracaoagendamentotarefa/edita.xhtml"),
})
public class ConfiguracaoAgendamentoTarefaControlador extends PrettyControlador<ConfiguracaoAgendamentoTarefa> implements Serializable, CRUD {

    @EJB
    private ConfiguracaoAgendamentoTarefaFacade configuracaoAgendamentoTarefaFacade;
    private List<ConfiguracaoAgendamentoTarefa> agendamentos;
    private List<ConfiguracaoAgendamentoTarefa> removidos;

    public ConfiguracaoAgendamentoTarefaControlador() {
        super(ConfiguracaoAgendamentoTarefa.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-agendamento-tarefas/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<ConfiguracaoAgendamentoTarefa> getAgendamentos() {
        return agendamentos;
    }

    @Override
    public AbstractFacade getFacede() {
        return configuracaoAgendamentoTarefaFacade;
    }

    @Override
    @URLAction(mappingId = "editar-configuracao-agendamento-tarefa", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        removidos = Lists.newArrayList();
        selecionado = new ConfiguracaoAgendamentoTarefa();
        String perfilExecutarRotinaAgendada = System.getenv(APP_PERFIL_DESATIVAR_ROTINA_AGENDADA);
        if (perfilExecutarRotinaAgendada == null) {
            agendamentos = configuracaoAgendamentoTarefaFacade.findAll();
        } else {
            agendamentos = Lists.newArrayList();
        }
    }

    public List<SelectItem> getTipoAgendamento() {
        return Util.getListSelectItem(ConfiguracaoAgendamentoTarefa.TipoTarefaAgendada.values());
    }

    public List<SelectItem> getHoraExecucao() {
        List<SelectItem> retorno = Lists.newArrayList();
        for (int i = 0; i < 24; i++) {
            String s = StringUtil.preencheString(String.valueOf(i), 2, '0');
            retorno.add(new SelectItem(s));
        }
        return retorno;
    }

    public List<SelectItem> getMinutoExecucao() {
        List<SelectItem> retorno = Lists.newArrayList();
        for (int i = 0; i <= 55; i = i + 5) {
            String s = StringUtil.preencheString(String.valueOf(i), 2, '0');
            retorno.add(new SelectItem(s));
        }
        return retorno;
    }


    public void addAgendamento() {
        agendamentos.add(selecionado);
        selecionado = new ConfiguracaoAgendamentoTarefa();
    }

    public void removeAgendamento(ConfiguracaoAgendamentoTarefa selecionado) {
        agendamentos.remove(selecionado);
        removidos.add(selecionado);
    }

    public void rodarAgora(ConfiguracaoAgendamentoTarefa selecionado) {
        try {
            String perfilExecutarRotinaAgendada = System.getenv(APP_PERFIL_DESATIVAR_ROTINA_AGENDADA);
            if (perfilExecutarRotinaAgendada == null) {
                Scheduler scheduler = new StdSchedulerFactory().getScheduler();
                configuracaoAgendamentoTarefaFacade.rodarAgora(selecionado, scheduler);
                FacesUtil.addOperacaoRealizada("O JOB será executado em Segundo Plano");
            } else {
                FacesUtil.addOperacaoNaoPermitida("Desabilitado execução de rotina agendada.");
            }
        } catch (SchedulerException e) {
            logger.error("Erro ao inicializar scheduler. ", e);
        }
    }

    @Override
    public void salvar() {
        configuracaoAgendamentoTarefaFacade.salvar(agendamentos);
        configuracaoAgendamentoTarefaFacade.deletar(removidos);
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        ServiceAgendamentoTarefa serviceAgendamentoTarefa = (ServiceAgendamentoTarefa) ap.getBean("serviceAgendamentoTarefa");
        try {
            serviceAgendamentoTarefa.agendaTarefas();
        } catch (SchedulerException e) {
            logger.error(e.getMessage());
        }
        FacesUtil.addOperacaoRealizada("Agendamentos Salvos");
        FacesUtil.redirecionamentoInterno("/configuracao-agendamento-tarefas/");
    }

}
