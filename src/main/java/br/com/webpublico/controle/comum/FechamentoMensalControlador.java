package br.com.webpublico.controle.comum;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.RevisaoAuditoria;
import br.com.webpublico.entidades.comum.FechamentoMensal;
import br.com.webpublico.entidades.comum.FechamentoMensalMes;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.SituacaoFechamentoMensal;
import br.com.webpublico.enums.TipoSituacaoAgendamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.comum.FechamentoMensalFacade;
import br.com.webpublico.negocios.tributario.services.ServiceAgendamentoTarefa;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.DateTime;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-fechamento-mensal", pattern = "/fechamento-mensal/novo/", viewId = "/faces/comum/fechamento-mensal/edita.xhtml"),
    @URLMapping(id = "edita-fechamento-mensal", pattern = "/fechamento-mensal/editar/#{fechamentoMensalControlador.id}/", viewId = "/faces/comum/fechamento-mensal/edita.xhtml"),
    @URLMapping(id = "ver-fechamento-mensal", pattern = "/fechamento-mensal/ver/#{fechamentoMensalControlador.id}/", viewId = "/faces/comum/fechamento-mensal/visualizar.xhtml"),
    @URLMapping(id = "listar-fechamento-mensal", pattern = "/fechamento-mensal/listar/", viewId = "/faces/comum/fechamento-mensal/lista.xhtml")
})
public class FechamentoMensalControlador extends PrettyControlador<FechamentoMensal> implements Serializable, CRUD {

    @EJB
    private FechamentoMensalFacade facade;
    private FechamentoMensalMes fechamentoMensalMes;

    public FechamentoMensalControlador() {
        super(FechamentoMensal.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/fechamento-mensal/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-fechamento-mensal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        cancelarFechamentoMes();
        FechamentoMensal fechamentoMensal = facade.buscarFechamentoPorExercicio(facade.getSistemaFacade().getExercicioCorrente().getId());
        if (fechamentoMensal != null) {
            selecionado = fechamentoMensal;
            FacesUtil.redirecionamentoInterno("/fechamento-mensal/editar/" + fechamentoMensal.getId() + "/");
        } else {
            inicializarAtributos();
        }
    }

    public void cancelarFechamentoMes() {
        fechamentoMensalMes = null;
    }

    @URLAction(mappingId = "edita-fechamento-mensal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        ordenar();
        cancelarFechamentoMes();
    }

    public void ordenar() {
        Collections.sort(selecionado.getMeses(), new Comparator<FechamentoMensalMes>() {
            @Override
            public int compare(FechamentoMensalMes o1, FechamentoMensalMes o2) {
                return o1.getMes().getNumeroMes().compareTo(o2.getMes().getNumeroMes());
            }
        });
    }

    @URLAction(mappingId = "ver-fechamento-mensal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        ordenar();
        cancelarFechamentoMes();
    }

    @Override
    public void salvar() {
        super.salvar();
    }

    private void agendarReprocessamentos() throws SchedulerException {
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        ServiceAgendamentoTarefa serviceAgendamentoTarefa = (ServiceAgendamentoTarefa) ap.getBean("serviceAgendamentoTarefa");
        serviceAgendamentoTarefa.agendaTarefas();
    }

    public List<SelectItem> getSituacoes() {
        return Util.getListSelectItemSemCampoVazio(SituacaoFechamentoMensal.values());
    }

    private void inicializarAtributos() {
        selecionado.setExercicio(facade.getSistemaFacade().getExercicioCorrente());
        selecionado.setMeses(Lists.<FechamentoMensalMes>newArrayList());
        for (Mes mes : Mes.values()) {
            selecionado.getMeses().add(new FechamentoMensalMes(mes, SituacaoFechamentoMensal.ABERTO, selecionado));
        }
    }


    @Override
    public RevisaoAuditoria getUltimaRevisao() {
        if (ultimaRevisao == null) {
            ultimaRevisao = buscarUltimaAuditoria();
            if (selecionado.getMeses() != null) {
                for(FechamentoMensalMes fechamento : selecionado.getMeses()) {
                    RevisaoAuditoria revisaoAuditoria = buscarUltimaAuditoria(FechamentoMensalMes.class, fechamento.getId());
                    if (ultimaRevisao == null || (revisaoAuditoria != null && revisaoAuditoria.getDataHora().after(ultimaRevisao.getDataHora()))) {
                        ultimaRevisao = revisaoAuditoria;
                    }
                }
            }
        }
        return ultimaRevisao;
    }

    public void atribuirFechamentoMes(FechamentoMensalMes fechamentoMes) {
        fechamentoMensalMes = (FechamentoMensalMes) Util.clonarObjeto(fechamentoMes);
        FacesUtil.executaJavaScript("dialogNovoAgendamento.show();");
    }

    public void salvarEAgendarFechamentoMes() {
        try {
            validarDataAgendamento();
            montarCronComDataAgendamentoEAtualizarSituacao();
            Util.adicionarObjetoEmLista(selecionado.getMeses(), fechamentoMensalMes);
            facade.salvarFechamentoMensalMes(fechamentoMensalMes);
            cancelarFechamentoMes();
            FacesUtil.executaJavaScript("dialogNovoAgendamento.hide()");
            agendarReprocessamentos();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (SchedulerException e) {
            logger.error(e.getMessage());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public void removerAgendamentoFechamentoMes() {
        try {
            fechamentoMensalMes.setDataAgendamento(null);
            fechamentoMensalMes.setCron(null);
            fechamentoMensalMes.setTipoSituacaoAgendamento(null);
            Util.adicionarObjetoEmLista(selecionado.getMeses(), fechamentoMensalMes);
            facade.salvarFechamentoMensalMes(fechamentoMensalMes);
            cancelarFechamentoMes();
            FacesUtil.executaJavaScript("dialogNovoAgendamento.hide()");
            agendarReprocessamentos();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (SchedulerException e) {
            logger.error(e.getMessage());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    private void validarDataAgendamento() {
        ValidacaoException ve = new ValidacaoException();
        if (fechamentoMensalMes.getDataAgendamento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data do Agendamento deve ser informado.");
        }
        ve.lancarException();
        if (new DateTime(fechamentoMensalMes.getDataAgendamento()).getHourOfDay() < 18) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Por favor, selecione a hora entre 18 e 23.");
        }
        if (fechamentoMensalMes.getDataAgendamento().before(new Date())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Data do Agendamento deve ser igual ou superior a data atual.");
        }
        ve.lancarException();
    }

    private void montarCronComDataAgendamentoEAtualizarSituacao() {
        fechamentoMensalMes.setTipoSituacaoAgendamento(TipoSituacaoAgendamento.AGUARDANDO);
        DateTime dataAgendamento = new DateTime(fechamentoMensalMes.getDataAgendamento());
        fechamentoMensalMes.setCron(
            "0 " + dataAgendamento.getMinuteOfHour() +
                " " + dataAgendamento.getHourOfDay() +
                " " + dataAgendamento.getDayOfMonth() +
                " " + dataAgendamento.getMonthOfYear() +
                " ?"
        );
    }

    public FechamentoMensalMes getFechamentoMensalMes() {
        return fechamentoMensalMes;
    }

    public void setFechamentoMensalMes(FechamentoMensalMes fechamentoMensalMes) {
        this.fechamentoMensalMes = fechamentoMensalMes;
    }
}

