/*
 * Codigo gerado automaticamente em Tue Mar 27 16:21:22 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.AtoLegal;
import br.com.webpublico.entidades.DiariaDeCampo;
import br.com.webpublico.entidades.PropostaConcessaoDiaria;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AtoLegalFacade;
import br.com.webpublico.negocios.DiariaDeCampoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "diariaDeCampoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-diaria-campo", pattern = "/diaria-campo/novo/", viewId = "/faces/financeiro/concessaodediarias/diariadecampo/edita.xhtml"),
    @URLMapping(id = "editar-diaria-campo", pattern = "/diaria-campo/editar/#{diariaDeCampoControlador.id}/", viewId = "/faces/financeiro/concessaodediarias/diariadecampo/edita.xhtml"),
    @URLMapping(id = "ver-diaria-campo", pattern = "/diaria-campo/ver/#{diariaDeCampoControlador.id}/", viewId = "/faces/financeiro/concessaodediarias/diariadecampo/visualizar.xhtml"),
    @URLMapping(id = "listar-diaria-campo", pattern = "/diaria-campo/listar/", viewId = "/faces/financeiro/concessaodediarias/diariadecampo/lista.xhtml")
})
public class DiariaDeCampoControlador extends PrettyControlador<DiariaDeCampo> implements Serializable, CRUD {

    @EJB
    private DiariaDeCampoFacade diariaDeCampoFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private List<PropostaConcessaoDiaria> listaDePropostasDisponiveis;
    private ConverterAutoComplete converterAtoLegal;

    public DiariaDeCampoControlador() {
        super(DiariaDeCampo.class);
    }

    public DiariaDeCampoFacade getFacade() {
        return diariaDeCampoFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return diariaDeCampoFacade;
    }

    @URLAction(mappingId = "novo-diaria-campo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataDiaria(new Date());
        listaDePropostasDisponiveis = diariaDeCampoFacade.listaDiariaDeCampo();
    }

    @URLAction(mappingId = "ver-diaria-campo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperarEditarVer();
    }

    @URLAction(mappingId = "editar-diaria-campo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarEditarVer();
    }

    public void recuperarEditarVer() {
        selecionado = diariaDeCampoFacade.recarregar(selecionado);
        selecionado.setDataDiaria(new Date());
        listaDePropostasDisponiveis = diariaDeCampoFacade.propostaDisponiveis(sistemaControlador.getExercicioCorrente());
    }

    public Boolean validaCampos() {
        DiariaDeCampo d = (DiariaDeCampo) selecionado;
        boolean valida = true;

        int qtd = d.getPorpostasConcoesDiarias().size();
        if (qtd == 0) {
            valida = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Informe uma Diária para salvar!", ""));
        }
        if (d.getDescricao().trim().length() <= 0) {
            valida = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "O campo Descrição é obrigatório!", ""));
        }
        valida = valida && qtd > 0;
        return valida;
    }

    @Override
    public void salvar() {
        DiariaDeCampo diaria = (DiariaDeCampo) selecionado;
        if (diaria.getId() == null) {
            diaria.setCodigo(calculaCodigo());
        }
        if (validaCampos() == true) {
            try {
                if (operacao == Operacoes.NOVO) {
                    diariaDeCampoFacade.salvarNovo(diaria);
                } else {
                    diariaDeCampoFacade.salvar(diaria);
                }
//                lista = null;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Salvo com Sucesso", ""));
                redireciona();
                return;

            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Exceção do sistema!", e.getMessage()));
                return;
            }
        } else {
            return;
        }
    }

    public void removerPropostas(ActionEvent evento) {
        DiariaDeCampo diariaDeCampo = (DiariaDeCampo) selecionado;
        PropostaConcessaoDiaria p = (PropostaConcessaoDiaria) evento.getComponent().getAttributes().get("objeto");
        diariaDeCampo.getPorpostasConcoesDiarias().remove(p);
        listaDePropostasDisponiveis.add(p);
    }

    public void addPropostas(ActionEvent evento) {
        DiariaDeCampo diariaDeCampo = (DiariaDeCampo) selecionado;
        PropostaConcessaoDiaria p = (PropostaConcessaoDiaria) evento.getComponent().getAttributes().get("objeto");
        diariaDeCampo.getPorpostasConcoesDiarias().add(p);
        listaDePropostasDisponiveis.remove(p);
    }

    public List<PropostaConcessaoDiaria> propostaConcessaoDiariasAdicionadas() {

        DiariaDeCampo d = (DiariaDeCampo) selecionado;
        if (d != null) {

            if (d.getPorpostasConcoesDiarias() != null) {
                return d.getPorpostasConcoesDiarias();

            }

        }
        return new ArrayList<PropostaConcessaoDiaria>();

    }

    public Converter getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, atoLegalFacade);
        }
        return converterAtoLegal;
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return atoLegalFacade.listaFiltrandoNomeNumero(parte.trim());
    }

    public String calculaCodigo() {
        Integer numero = diariaDeCampoFacade.getCodigoSequencial(sistemaControlador.getExercicioCorrente());
        return numero.toString();
    }

    public List<PropostaConcessaoDiaria> getListaDePropostasDisponiveis() {
        return listaDePropostasDisponiveis;
    }

    public void setListaDePropostasDisponiveis(List<PropostaConcessaoDiaria> listaDePropostasDisponiveis) {
        this.listaDePropostasDisponiveis = listaDePropostasDisponiveis;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/diaria-campo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
