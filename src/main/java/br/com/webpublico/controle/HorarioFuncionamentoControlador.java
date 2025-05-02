/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.enums.DiaSemana;
import br.com.webpublico.entidades.HorarioFuncionamento;
import br.com.webpublico.entidades.HorarioFuncionamentoItem;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.HorarioFuncionamentoFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.DateTimeFieldType;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Gustavo
 */
@ManagedBean(name = "horarioFuncionamentoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoHorarioFuncionamento", pattern = "/horario-de-funcionamento/novo/", viewId = "/faces/tributario/cadastromunicipal/horariofuncionamento/edita.xhtml"),
        @URLMapping(id = "editarHorarioFuncionamento", pattern = "/horario-de-funcionamento/editar/#{horarioFuncionamentoControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/horariofuncionamento/edita.xhtml"),
        @URLMapping(id = "listarHorarioFuncionamento", pattern = "/horario-de-funcionamento/listar/", viewId = "/faces/tributario/cadastromunicipal/horariofuncionamento/lista.xhtml"),
        @URLMapping(id = "verHorarioFuncionamento", pattern = "/horario-de-funcionamento/ver/#{horarioFuncionamentoControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/horariofuncionamento/visualizar.xhtml")
})
public class HorarioFuncionamentoControlador extends PrettyControlador<HorarioFuncionamento> implements Serializable, CRUD {

    @EJB
    private HorarioFuncionamentoFacade horarioFuncionamentoFacade;
    private HorarioFuncionamentoItem item;

    public HorarioFuncionamentoControlador() {
        super(HorarioFuncionamento.class);
    }

    public HorarioFuncionamentoItem getItem() {
        return item;
    }

    public void setItem(HorarioFuncionamentoItem item) {
        this.item = item;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/horario-de-funcionamento/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novoHorarioFuncionamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        item = new HorarioFuncionamentoItem();
        getSelecionado().setCodigo(horarioFuncionamentoFacade.retornaUltimoCodigoLong());
    }

    @Override
    @URLAction(mappingId = "editarHorarioFuncionamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        item = new HorarioFuncionamentoItem();
    }

    @Override
    @URLAction(mappingId = "verHorarioFuncionamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }


    @Override
    public AbstractFacade getFacede() {
        return horarioFuncionamentoFacade;
    }

    public void addItem() {
        if (validaItem()) {
            if (getSelecionado().getItens().contains(item)) {
                getSelecionado().getItens().set(getSelecionado().getItens().indexOf(item), item);
            } else {
                item.setHorarioFuncionamento(getSelecionado());
                getSelecionado().addItem(item);
            }
            item = new HorarioFuncionamentoItem();
        }
    }

    public Boolean validaCampos() {
        boolean valida = true;
        if (getSelecionado().getDescricao().isEmpty()) {
            valida = false;
            FacesUtil.addError("Campo Obrigatório!", "Informe o a descrição.");
        }
        if (getSelecionado().getItens().isEmpty()) {
            valida = false;
            FacesUtil.addError("Campo Obrigatório!", "Informe o ao menos um horário de funcionamento.");
        }
        return valida;
    }

    public void alteraItem(HorarioFuncionamentoItem item) {
        this.item = item;
        getSelecionado().getItens().remove(item);
    }

    public void removeItem(HorarioFuncionamentoItem item) {
        getSelecionado().getItens().remove(item);
    }

    private boolean validaItem() {
        boolean valida = true;
        if (item.getDiaEntrada() == null) {
            valida = false;
            FacesUtil.addError("Campo Obrigatório!", "Informe o dia da semana (Entrada).");
        }
        if (item.getDiaSaida() == null) {
            valida = false;
            FacesUtil.addError("Campo Obrigatório!", "Informe o dia da semana (Saída).");
        }
        if (item.getHorarioEntrada() == null) {
            valida = false;
            FacesUtil.addError("Campo Obrigatório!", "Informe o horário de entrada.");
        }
        if (item.getHorarioSaida() == null) {
            valida = false;
            FacesUtil.addError("Campo Obrigatório!", "Informe o horário de saída.");
        }
        if (item.getHorarioSaida() != null && item.getHorarioEntrada() != null &&
                item.getDiaEntrada() != null && item.getDiaEntrada() != null &&
                item.getDiaEntrada() == item.getDiaSaida() &&
                item.getHorarioEntrada().after(item.getHorarioSaida())) {
            valida = false;
            FacesUtil.addError("Atenção!", "O horário de entrada deve ser menor que o horário de saída.");
        }
        if (item.getHorarioSaida() != null && item.getHorarioEntrada() != null &&
                item.getDiaEntrada() != null && item.getDiaSaida() != null && choqueHorario()) {
            valida = false;
            FacesUtil.addError("Atenção!", "Houve um conflito de horários. Uma faixa de horário não pode estar contida em outra faixa para o mesmo dia.");
        }
        return valida;
    }

    public List<SelectItem> getDias() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, " "));
        for (DiaSemana dia : DiaSemana.values()) {
            retorno.add(new SelectItem(dia, dia.getDescricao()));
        }
        return retorno;
    }

    private boolean choqueHorario() {
        for (HorarioFuncionamentoItem horario : getSelecionado().getItens()) {
            int diaEntradaNovo = item.getDiaEntrada().getDiaDaSemana();
            int diaEntradaVelho = horario.getDiaEntrada().getDiaDaSemana();
            int diaSaidaNovo = item.getDiaSaida().getDiaDaSemana();
            int diaSaidaVelho = horario.getDiaSaida().getDiaDaSemana();
            long inicioNovo = item.getHorarioEntrada().getTime();
            long inicioVelho = horario.getHorarioEntrada().getTime();
            long fimNovo = item.getHorarioSaida().getTime();
            long fimVelho = horario.getHorarioSaida().getTime();

            if(diaEntradaNovo >= diaEntradaVelho && diaEntradaNovo <= diaSaidaVelho &&
                    inicioNovo >= inicioVelho && inicioNovo <= fimVelho){
                return true;
            }

            if(diaSaidaNovo >= diaEntradaVelho && diaSaidaNovo <= diaSaidaVelho &&
                    fimNovo >= inicioVelho && fimNovo <= fimVelho){
                return true;
            }

            if(diaEntradaNovo <= diaEntradaVelho && diaSaidaNovo >= diaSaidaVelho &&
                    inicioNovo <= inicioVelho && fimNovo >= fimVelho){
                return true;
            }

        }
        return false;
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            super.salvar();
        }
    }
}
