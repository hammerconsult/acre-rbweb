package br.com.webpublico.controle;

import br.com.webpublico.entidades.Capacitacao;
import br.com.webpublico.entidades.InscricaoCapacitacao;
import br.com.webpublico.entidades.ModuloCapacitacao;
import br.com.webpublico.entidades.NotaModulo;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.InscricaoEventoCapacitacaoFacade;
import br.com.webpublico.negocios.ModuloCapacitacaoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by carlos on 21/08/15.
 */
@ManagedBean(name = "notaModuloControlador")
@ViewScoped
@URLMappings(
    mappings = {
        @URLMapping(id = "lancarNota",
            pattern = "/nota/lancar/",
            viewId = "/faces/rh/administracaodepagamento/capacitacaoservidor/nota/lancar.xhtml")
    }
)
public class NotaModuloControlador implements Serializable {
    @EJB
    private InscricaoEventoCapacitacaoFacade inscricaoEventoCapacitacaoFacade;
    @EJB
    private ModuloCapacitacaoFacade moduloCapacitacaoFacade;
    private InscricaoCapacitacao inscricaoCapacitacao;
    private Capacitacao capacitacao;
    private ModuloCapacitacao moduloCapacitacao;
    private List<NotaModulo> notaModuloList = Lists.newArrayList();
    private Double nota;
    private ConverterAutoComplete converterModulo;

    @URLAction(mappingId = "lancarNota", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        nota = null;
    }

    public void salvar() {
        if (validar()) {
            inscricaoEventoCapacitacaoFacade.salvarNota(notaModuloList);
            FacesUtil.redirecionamentoInterno("");
            FacesUtil.addOperacaoRealizada("Presenças salvas com sucesso.");
        }

    }

    public boolean validar() {
        boolean valida = true;
        boolean nota = true;

        if (capacitacao == null) {
            FacesUtil.addCampoObrigatorio("O Evento é um campo obrigátorio.");
            valida = false;
        }

        if (moduloCapacitacao == null) {
            FacesUtil.addCampoObrigatorio("O Módulo é um campo obrigátorio.");
            valida = false;
        }

        for (NotaModulo notaModulo : notaModuloList) {
            if (notaModulo.getNota() == null || notaModulo.getNota() < 0 || notaModulo.getNota() > 10) {
                nota = false;
            }
        }

        if (!nota) {
            FacesUtil.addCampoObrigatorio("As notas devem estar entre 0 e 10.");
            valida = false;
        }

        return valida;
    }

    public void cancelar() {
        FacesUtil.redirecionamentoInterno("/evento-de-capacitacao/listar/");
    }

    public AbstractFacade getFacede() {
        return inscricaoEventoCapacitacaoFacade;
    }

    public List<Capacitacao> completaCapacitacao(String parte) {
        return inscricaoEventoCapacitacaoFacade.getEventoCapacitacaoFacade().listaEventosCapacitacao(parte);
    }

    public List<ModuloCapacitacao> completaModuloEvento(String parte) {
        return inscricaoEventoCapacitacaoFacade.getEventoCapacitacaoFacade().listaModuloCapacitacao(parte, capacitacao);
    }

    public void carregaListaNota() {
        notaModuloList = new ArrayList<NotaModulo>();
        capacitacao = moduloCapacitacaoFacade.recuperarCapacitcao(capacitacao.getId());
        List<NotaModulo> notaModulosRecuperados = inscricaoEventoCapacitacaoFacade.recuperarNota(moduloCapacitacao);
        if (notaModulosRecuperados.size() > 0) {
            this.notaModuloList.addAll(notaModulosRecuperados);
        } else {
            for (InscricaoCapacitacao inscricaoCapacitacao : capacitacao.getInscricoes()) {
                NotaModulo notaModulo = new NotaModulo();
                notaModulo.setModuloCapacitacao(moduloCapacitacao);
                notaModulo.setInscricaoCapacitacao(inscricaoCapacitacao);
                notaModulo.setNota(nota);
                this.notaModuloList.add(notaModulo);
            }
        }
    }

    public Capacitacao getCapacitacao() {
        return capacitacao;
    }

    public void setCapacitacao(Capacitacao capacitacao) {
        this.capacitacao = capacitacao;
    }

    public InscricaoCapacitacao getInscricaoCapacitacao() {
        return inscricaoCapacitacao;
    }

    public void setInscricaoCapacitacao(InscricaoCapacitacao inscricaoCapacitacao) {
        this.inscricaoCapacitacao = inscricaoCapacitacao;
    }

    public InscricaoEventoCapacitacaoFacade getInscricaoEventoCapacitacaoFacade() {
        return inscricaoEventoCapacitacaoFacade;
    }

    public void setInscricaoEventoCapacitacaoFacade(InscricaoEventoCapacitacaoFacade inscricaoEventoCapacitacaoFacade) {
        this.inscricaoEventoCapacitacaoFacade = inscricaoEventoCapacitacaoFacade;
    }

    public ModuloCapacitacao getModuloCapacitacao() {
        return moduloCapacitacao;
    }

    public void setModuloCapacitacao(ModuloCapacitacao moduloCapacitacao) {
        this.moduloCapacitacao = moduloCapacitacao;
    }

    public ModuloCapacitacaoFacade getModuloCapacitacaoFacade() {
        return moduloCapacitacaoFacade;
    }

    public void setModuloCapacitacaoFacade(ModuloCapacitacaoFacade moduloCapacitacaoFacade) {
        this.moduloCapacitacaoFacade = moduloCapacitacaoFacade;
    }

    public List<NotaModulo> getNotaModuloList() {
        return notaModuloList;
    }

    public void setNotaModuloList(List<NotaModulo> notaModuloList) {
        this.notaModuloList = notaModuloList;
    }

    public ConverterAutoComplete getConverterModulo() {
        if (converterModulo == null) {
            converterModulo = new ConverterAutoComplete(ModuloCapacitacao.class, moduloCapacitacaoFacade);
        }
        return converterModulo;
    }
}
