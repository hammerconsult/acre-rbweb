/*
 * Codigo gerado automaticamente em Thu May 10 14:10:07 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ProcessoCompensacaoIptuFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "processoCompensacaoIptuControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novaCompensacaoIptu", pattern = "/processo-compensacao-iptu/novo/", viewId = "/faces/tributario/iptu/processo-compensacao/edita.xhtml"),
        @URLMapping(id = "editarCompensacaoIptu", pattern = "/processo-compensacao-iptu/editar/#{processoCompensacaoIptuControlador.id}/", viewId = "/faces/tributario/iptu/processo-compensacao/edita.xhtml"),
        @URLMapping(id = "listarCompensacaoIptu", pattern = "/processo-compensacao-iptu/listar/", viewId = "/faces/tributario/iptu/processo-compensacao/lista.xhtml"),
        @URLMapping(id = "verCompensacaoIptu", pattern = "/processo-compensacao-iptu/ver/#{processoCompensacaoIptuControlador.id}/", viewId = "/faces/tributario/iptu/processo-compensacao/visualizar.xhtml")
})
public class ProcessoCompensacaoIptuControlador extends PrettyControlador<ProcessoCompensacaoIptu> implements Serializable, CRUD {

    @EJB
    private ProcessoCompensacaoIptuFacade processoCompensacaoIptuFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private List<Exercicio> exercicios;
    private Exercicio exercicioDebito;

    public ProcessoCompensacaoIptuControlador() {
        super(ProcessoCompensacaoIptu.class);
    }

    @URLAction(mappingId = "novaCompensacaoIptu", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setExercicio(sistemaFacade.getExercicioCorrente());
        selecionado.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
        selecionado.setLancamento(new Date());
        exercicios = Lists.newArrayList();
        selecionado.setUfmProcesso(processoCompensacaoIptuFacade.buscarValorUfmExercicioProcesso(selecionado.getExercicio()));
    }

    @URLAction(mappingId = "verCompensacaoIptu", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        Collections.sort(selecionado.getItens());
        this.exercicios = Lists.newArrayList();
        for (ProcessoCompensacaoIptuItem item : selecionado.getItensIncorretos()) {
            this.exercicios.add(item.getExercicio());
        }
    }

    @URLAction(mappingId = "editarCompensacaoIptu", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        super.salvar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/processo-compensacao-iptu/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return processoCompensacaoIptuFacade;
    }

    public List<Exercicio> getExercicios() {
        return exercicios;
    }

    public void setExercicios(List<Exercicio> exercicios) {
        this.exercicios = exercicios;
    }

    public Exercicio getExercicioDebito() {
        return exercicioDebito;
    }

    public void setExercicioDebito(Exercicio exercicioDebito) {
        this.exercicioDebito = exercicioDebito;
    }

    public void removerExercicioDebito(Exercicio ex) {
        this.exercicios.remove(ex);
    }

    private void validarExercicioDebito() {
        ValidacaoException ve = new ValidacaoException();
        if (exercicioDebito == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício do Débito deve ser informado.");
        } else {
            if (exercicios != null) {
                for (Exercicio ex : exercicios) {
                    if (ex.equals(exercicioDebito)) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("O Exercício do Débito já está adicionado ao processo de revisão.");
                        break;
                    }
                }
            }
        }
        ve.lancarException();
    }

    public void adicionarExercicioDebito() {
        try {
            validarExercicioDebito();
            if (this.exercicios == null) {
                this.exercicios = Lists.newArrayList();
            }
            this.exercicios.add(exercicioDebito);
            exercicioDebito = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    private void validarBuscaProcesso() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getCadastroImobiliario() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Cadastro Imobiliário deve ser informado.");
        }
        if (exercicios == null || exercicios.isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Ao menos um Exercício do Débito deve ser informado.");
        }
        ve.lancarException();
    }

    public void buscarIptuPorExercicio() {
        try {
            validarBuscaProcesso();
            processoCompensacaoIptuFacade.buscarIptuPorExercicioProcesso(selecionado, exercicios);
            Collections.sort(selecionado.getItens());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    public BigDecimal getTotalImposto(String tipoItem) {
        ProcessoCompensacaoIptuItem.TipoItem tipo = ProcessoCompensacaoIptuItem.TipoItem.valueOf(tipoItem);
        BigDecimal total = BigDecimal.ZERO;
        for (ProcessoCompensacaoIptuItem item : selecionado.getItens()) {
            if (tipo.equals(item.getTipoItem())) {
                total = total.add(item.getValorImposto());
            }
        }
        return total;
    }

    public BigDecimal getTotalTaxa(String tipoItem) {
        ProcessoCompensacaoIptuItem.TipoItem tipo = ProcessoCompensacaoIptuItem.TipoItem.valueOf(tipoItem);
        BigDecimal total = BigDecimal.ZERO;
        for (ProcessoCompensacaoIptuItem item : selecionado.getItens()) {
            if (tipo.equals(item.getTipoItem())) {
                total = total.add(item.getValorTaxa());
            }
        }
        return total;
    }

    public BigDecimal getTotalDesconto(String tipoItem) {
        ProcessoCompensacaoIptuItem.TipoItem tipo = ProcessoCompensacaoIptuItem.TipoItem.valueOf(tipoItem);
        BigDecimal total = BigDecimal.ZERO;
        for (ProcessoCompensacaoIptuItem item : selecionado.getItens()) {
            if (tipo.equals(item.getTipoItem())) {
                total = total.add(item.getValorDesconto());
            }
        }
        return total;
    }

    public BigDecimal getTotalJurosMulta(String tipoItem) {
        ProcessoCompensacaoIptuItem.TipoItem tipo = ProcessoCompensacaoIptuItem.TipoItem.valueOf(tipoItem);
        BigDecimal total = BigDecimal.ZERO;
        for (ProcessoCompensacaoIptuItem item : selecionado.getItens()) {
            if (tipo.equals(item.getTipoItem())) {
                total = total.add(item.getValorJurosMulta());
            }
        }
        return total;
    }

    public BigDecimal getTotalTotal(String tipoItem) {
        ProcessoCompensacaoIptuItem.TipoItem tipo = ProcessoCompensacaoIptuItem.TipoItem.valueOf(tipoItem);
        BigDecimal total = BigDecimal.ZERO;
        for (ProcessoCompensacaoIptuItem item : selecionado.getItens()) {
            if (tipo.equals(item.getTipoItem())) {
                total = total.add(item.getValorTotal());
            }
        }
        return total;
    }

    public BigDecimal getTotalAtualizado(String tipoItem) {
        ProcessoCompensacaoIptuItem.TipoItem tipo = ProcessoCompensacaoIptuItem.TipoItem.valueOf(tipoItem);
        BigDecimal total = BigDecimal.ZERO;
        for (ProcessoCompensacaoIptuItem item : selecionado.getItens()) {
            if (tipo.equals(item.getTipoItem())) {
                total = total.add(item.getValorAtualizado());
            }
        }
        return total;
    }

    public BigDecimal getSaldoAtualizado() {
        return getTotalAtualizado(ProcessoCompensacaoIptuItem.TipoItem.INCORRETO.name()).subtract(getTotalAtualizado(ProcessoCompensacaoIptuItem.TipoItem.CORRETO.name()));
    }

    public void calcularNovoIptu() {
        if (!selecionado.getItens().isEmpty()) {
            selecionado = processoCompensacaoIptuFacade.salvarRetornando(selecionado);
            List<Exercicio> exerciciosEncontrados = Lists.newArrayList();
            for (ProcessoCompensacaoIptuItem item : selecionado.getItensIncorretos()) {
                exerciciosEncontrados.add(item.getExercicio());
            }

            List<ProcessoCalculoIPTU> processos = processoCompensacaoIptuFacade.calcularIPTUCompensacao(selecionado, exerciciosEncontrados);
            for (ProcessoCalculoIPTU processo : processos) {
                if (!processo.getCalculosIPTU().isEmpty()) {
                    ProcessoCompensacaoIptuItem item = new ProcessoCompensacaoIptuItem();
                    item.setProcessoCompensacaoIptu(selecionado);
                    item.setTipoItem(ProcessoCompensacaoIptuItem.TipoItem.CORRETO);
                    item.setExercicio(processo.getExercicio());
                    item.setValorUfm(processoCompensacaoIptuFacade.buscarValorUfmExercicioProcesso(item.getExercicio()));
                    BigDecimal valorImposto = BigDecimal.ZERO;
                    BigDecimal valorTaxa = BigDecimal.ZERO;
                    for (ItemCalculoIPTU itemCalculoIPTU : processo.getCalculosIPTU().get(0).getItensCalculo()) {
                        if (!itemCalculoIPTU.getIsento()) {
                            if (EventoCalculo.TipoLancamento.IMPOSTO.equals(itemCalculoIPTU.getEvento().getEventoCalculo().getTipoLancamento())) {
                                valorImposto = valorImposto.add(itemCalculoIPTU.getValor());
                            } else if (EventoCalculo.TipoLancamento.TAXA.equals(itemCalculoIPTU.getEvento().getEventoCalculo().getTipoLancamento())) {
                                valorTaxa = valorTaxa.add(itemCalculoIPTU.getValor());
                            }
                        }
                    }
                    item.setValorImposto(valorImposto);
                    item.setValorTaxa(valorTaxa);

                    BigDecimal descontoIptu = processoCompensacaoIptuFacade.buscarPercetualDescontoIptu(processo.getDivida(), processo.getExercicio());
                    if (descontoIptu.compareTo(BigDecimal.ZERO) > 0) {
                        item.setValorDesconto(valorImposto.multiply(descontoIptu.divide(CEM, 2, RoundingMode.HALF_UP)));
                    } else {
                        item.setValorDesconto(BigDecimal.ZERO);
                    }
                    item.setValorJuros(BigDecimal.ZERO);
                    item.setValorMulta(BigDecimal.ZERO);

                    selecionado.getItens().add(item);
                }
            }
            selecionado = processoCompensacaoIptuFacade.salvarRetornando(selecionado);
            FacesUtil.addOperacaoRealizada("Cálculos efetivados com sucesso!");
            redirecionarParaVerOrEditar(selecionado.getId(), "ver");
        }
    }
}
