package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ExercicioRevisaoIPTU;
import br.com.webpublico.enums.TipoVencimentoRevisaoIPTU;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ProcessoRevisaoCalculoIPTUFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "nova-revisao-iptu-lote",
        pattern = "/revisao-calculo-de-iptu-lote/novo/",
        viewId = "/faces/tributario/iptu/revisaoporlote/edita.xhtml"),
    @URLMapping(id = "editar-revisao-iptu-lote",
        pattern = "/revisao-calculo-de-iptu-lote/editar/#{processoRevisaoCalculoIPTUControlador.id}/",
        viewId = "/faces/tributario/iptu/revisaoporlote/edita.xhtml"),
    @URLMapping(id = "ver-revisao-iptu-lote",
        pattern = "/revisao-calculo-de-iptu-lote/ver/#{processoRevisaoCalculoIPTUControlador.id}/",
        viewId = "/faces/tributario/iptu/revisaoporlote/visualizar.xhtml"),
    @URLMapping(id = "listar-revisao-iptu-lote",
        pattern = "/revisao-calculo-de-iptu-lote/listar/",
        viewId = "/faces/tributario/iptu/revisaoporlote/lista.xhtml")
})
public class ProcessoRevisaoCalculoIPTUControlador extends PrettyControlador<ProcessoRevisaoCalculoIPTU> implements Serializable, CRUD {

    @EJB
    private ProcessoRevisaoCalculoIPTUFacade facade;
    private Exercicio exercicio;
    private AssistenteBarraProgresso assistente;
    private Future<Map<CadastroImobiliario, List<ExercicioRevisaoIPTU>>> futureRevisao;
    private Future<ProcessoRevisaoCalculoIPTU> futureEfetivacao;
    private Map<CadastroImobiliario, List<ExercicioRevisaoIPTU>> mapaConsulta;

    public ProcessoRevisaoCalculoIPTUControlador() {
        super(ProcessoRevisaoCalculoIPTU.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/revisao-calculo-de-iptu-lote/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public void setAssistente(AssistenteBarraProgresso assistente) {
        this.assistente = assistente;
    }

    public AssistenteBarraProgresso getAssistente() {
        return assistente;
    }

    public Map<CadastroImobiliario, List<ExercicioRevisaoIPTU>> getMapaConsulta() {
        return mapaConsulta;
    }

    public void setMapaConsulta(Map<CadastroImobiliario, List<ExercicioRevisaoIPTU>> mapaConsulta) {
        this.mapaConsulta = mapaConsulta;
    }

    public List<CadastroImobiliario> getCadastrosImobiliario() {
        if (this.mapaConsulta != null) {
            return Lists.newArrayList(this.mapaConsulta.keySet());
        }
        return null;
    }

    public List<ExercicioRevisaoIPTU> getExerciciosRevisaoIPTU(CadastroImobiliario cadastroImobiliario) {
        if (this.mapaConsulta != null && this.mapaConsulta.get(cadastroImobiliario) != null) {
            return this.mapaConsulta.get(cadastroImobiliario);
        }
        return null;
    }

    @URLAction(mappingId = "nova-revisao-iptu-lote", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataProcesso(new Date());
        selecionado.setUsuario(facade.getSistemaFacade().getUsuarioCorrente());
    }

    @URLAction(mappingId = "editar-revisao-iptu-lote", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "ver-revisao-iptu-lote", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    public void removerProcessoExercicio(ProcessoRevisaoCalculoIPTUExercicio processoExercicio) {
        selecionado.getExercicios().remove(processoExercicio);
    }

    public void validarExercicioProcesso() {
        ValidacaoException ve = new ValidacaoException();
        if (exercicio == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício deve ser informado.");
        } else {
            if (selecionado.getExercicios() != null && !selecionado.getExercicios().isEmpty()) {
                for (ProcessoRevisaoCalculoIPTUExercicio processoExercicio : selecionado.getExercicios()) {
                    if (processoExercicio.getExercicio().equals(exercicio)) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("O Exercício já está adicionado ao processo.");
                        break;
                    }
                }
            }
        }
        ve.lancarException();
    }

    public void adicionarExercicioProcesso() {
        try {
            validarExercicioProcesso();
            ProcessoRevisaoCalculoIPTUExercicio processoExercicio = new ProcessoRevisaoCalculoIPTUExercicio();
            processoExercicio.setProcessoRevisaoCalculoIPTU(selecionado);
            processoExercicio.setExercicio(exercicio);
            if (selecionado.getExercicios() == null) {
                selecionado.setExercicios(new ArrayList());
            }
            selecionado.getExercicios().add(processoExercicio);
            exercicio = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    public void validarDadosProcesso() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getInscricaoInicial().trim().equals("") || selecionado.getInscricaoFinal().trim().equals("")) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a Inscrição Cadastral Inicial e Inscrição Cadastral Final para Revisão de Cálculo de IPTU.");
        }
        if (selecionado.getExercicios() == null || selecionado.getExercicios().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe ao menos um Exercício para Revisão de Cálculo de IPTU.");
        }
        if (selecionado.getTipoVencimentoRevisaoIPTU() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Vencimento deve ser informado.");
        }
        ve.lancarException();
    }

    public void revisarIPTU() {
        try {
            preRevisaoIPTU();
            validarDadosProcesso();
            mapaConsulta = null;
            assistente = new AssistenteBarraProgresso();
            futureRevisao = facade.revisarIPTU(assistente, selecionado, facade.getSistemaFacade().getExercicioCorrente());
            FacesUtil.executaJavaScript("iniciarRevisaoIPTU()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    private void preRevisaoIPTU() {
        if (exercicio != null && (selecionado.getExercicios() == null || selecionado.getExercicios().isEmpty())) {
            adicionarExercicioProcesso();
        }
    }

    public void acompanharRevisaoIPTU() throws ExecutionException, InterruptedException {
        if (futureRevisao != null && futureRevisao.isDone()) {
            FacesUtil.executaJavaScript("pararTimer()");
            mapaConsulta = futureRevisao.get();
            FacesUtil.atualizarComponente("Formulario");
            FacesUtil.executaJavaScript("closeDialog(dialogAcompanhamento)");
            if (mapaConsulta.keySet().isEmpty()) {
                FacesUtil.addAtencao("Nenhum cadastro econtrado para os filtros utilizados.");
            } else {
                FacesUtil.addOperacaoRealizada("A Revisão dos cálculos de I.P.T.U foram realizadas, por favor confira os novos valores " +
                    "e clique em Efetivar, caso deseja concluir o processo.");
            }
        }
    }

    public boolean canEfetivarRevisaoIPTU() {
        if (mapaConsulta != null && !mapaConsulta.keySet().isEmpty()) {
            for (CadastroImobiliario cadastroImobiliario : mapaConsulta.keySet()) {
                for (ExercicioRevisaoIPTU exercicioRevisaoIPTU : mapaConsulta.get(cadastroImobiliario)) {
                    if (exercicioRevisaoIPTU.getPermiteRevisao() && exercicioRevisaoIPTU.getValorDividaOriginado() != null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void efetivarRevisarIPTU() {
        try {
            assistente = new AssistenteBarraProgresso();
            futureEfetivacao = facade.efetivarRevisarIPTU(assistente, selecionado, mapaConsulta);
            FacesUtil.executaJavaScript("iniciarEfetivacaoRevisaoIPTU()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void acompanharEfetivacaoRevisaoIPTU() throws ExecutionException, InterruptedException {
        if (futureEfetivacao != null && futureEfetivacao.isDone()) {
            selecionado = futureEfetivacao.get();
            FacesUtil.executaJavaScript("pararTimer()");

            FacesUtil.addOperacaoRealizada("Efetivação de Revisão dos cálculos de I.P.T.U realizada com sucesso.");
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
        }
    }

    public void visualizarRevisaoIPTU(RevisaoCalculoIPTU revisaoCalculoIPTU) {
        FacesUtil.redirecionamentoInterno("/revisao-calculo-de-iptu/ver/" + revisaoCalculoIPTU.getId());
    }

    public List<SelectItem> getTiposVencimento() {
        return Util.getListSelectItem(TipoVencimentoRevisaoIPTU.values());
    }
}
