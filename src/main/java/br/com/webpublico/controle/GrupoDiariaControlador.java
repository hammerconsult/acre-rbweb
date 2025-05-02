/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ClasseCredor;
import br.com.webpublico.entidades.ContaCorrenteBancPessoa;
import br.com.webpublico.entidades.GrupoDiaria;
import br.com.webpublico.entidades.GrupoPessoasDiarias;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.enums.TipoClasseCredor;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.GrupoDiariaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.util.Util;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

/**
 * @author Usuario
 */
@ManagedBean(name = "grupoDiariaControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novogrupodiaria", pattern = "/grupo-pessoa-diaria/novo/", viewId = "/faces/financeiro/concessaodediarias/grupopessoadiaria/edita.xhtml"),
        @URLMapping(id = "editargrupodiaria", pattern = "/grupo-pessoa-diaria/editar/#{grupoDiariaControlador.id}/", viewId = "/faces/financeiro/concessaodediarias/grupopessoadiaria/edita.xhtml"),
        @URLMapping(id = "vergrupodiaria", pattern = "/grupo-pessoa-diaria/ver/#{grupoDiariaControlador.id}/", viewId = "/faces/financeiro/concessaodediarias/grupopessoadiaria/visualizar.xhtml"),
        @URLMapping(id = "listargrupodiaria", pattern = "/grupo-pessoa-diaria/listar/", viewId = "/faces/financeiro/concessaodediarias/grupopessoadiaria/lista.xhtml")
})
public class GrupoDiariaControlador extends PrettyControlador<GrupoDiaria> implements Serializable, CRUD {

    @EJB
    private GrupoDiariaFacade grupoDiariaFacade;
    private ConverterAutoComplete converterPessoa;
    private ConverterAutoComplete converterContaCorrente;
    private ConverterAutoComplete converterClasseCredor;
    private GrupoPessoasDiarias grupoPessoasDiarias;

    @Override
    public AbstractFacade getFacede() {
        return grupoDiariaFacade;
    }

    public GrupoDiariaControlador() {
        super(GrupoDiaria.class);
    }

    @URLAction(mappingId = "novogrupodiaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        grupoPessoasDiarias = new GrupoPessoasDiarias();
    }

    @URLAction(mappingId = "vergrupodiaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editargrupodiaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        grupoPessoasDiarias = new GrupoPessoasDiarias();
    }

    @Override
    public void excluir() {
        super.excluir();
    }

    @Override
    public void salvar() {
        if (selecionado.getId() == null) {
            selecionado.setCodigo(grupoDiariaFacade.getUltimoNumero());
        }
        try {
            if (Util.validaCampos(selecionado)) {
                if (selecionado.getGrupoPessoasDiarias().isEmpty()) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Operação não Realizada! ", " Adicione uma pessoa para esse grupo de diária."));
                } else {
                    if (operacao.equals(Operacoes.NOVO)) {
                        this.getFacede().salvarNovo(selecionado);
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação Realizada! ", " Registro salvo com sucesso."));
                    } else {
                        this.getFacede().salvar(selecionado);
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operação Realizada! ", " Registro alterado com sucesso."));
                    }
                    redireciona();
                }
            }
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Operação não Realizada! ", "Erro ao salvar: " + ex.getMessage()));
        }

    }

    public boolean validaAdicionarPessoa() {
        boolean controle = true;
        if (grupoPessoasDiarias.getPessoa() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório! ", " O campo pessoa é obrigatório."));
            controle = false;
        }
        if (grupoPessoasDiarias.getClasseCredor() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório! ", " O campo classe é obrigatório. "));
            controle = false;
        }
        if (grupoPessoasDiarias.getContaCorrenteBanc() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório!  ", " O campo conta corrente é obrigatório. "));
            controle = false;
        }
        for (GrupoPessoasDiarias g : selecionado.getGrupoPessoasDiarias()) {
            if (g.getPessoa().equals(grupoPessoasDiarias.getPessoa())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro ao adicionar! ", " A Pessoa " + g.getPessoa() + " já foi adicionada"));
                controle = false;
            }
        }
        return controle;
    }

    public void adicionaPessoa() {
        if (validaAdicionarPessoa()) {
            grupoPessoasDiarias.setGrupoDiaria(selecionado);
//            selecionado.getGrupoPessoasDiarias().add(grupoPessoasDiarias);
            selecionado.setGrupoPessoasDiarias(Util.adicionarObjetoEmLista(selecionado.getGrupoPessoasDiarias(), grupoPessoasDiarias));
            grupoPessoasDiarias = new GrupoPessoasDiarias();
        }
    }

    public void excluiPessoa(ActionEvent evento) {
        GrupoPessoasDiarias g = (GrupoPessoasDiarias) evento.getComponent().getAttributes().get("ob");
        selecionado.getGrupoPessoasDiarias().remove(g);
    }

    public List<PessoaFisica> completaPessoaFisica(String parte) {
        return grupoDiariaFacade.getPessoaFacade().listaFiltrandoPessoaComVinculoVigenteEPorTipoClasse(parte.trim(), TipoClasseCredor.DIARIA_CAMPO);
    }

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, grupoDiariaFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public List<ContaCorrenteBancPessoa> completaContaCorrente(String parte) {
        return grupoDiariaFacade.getContaCorrenteBancPessoaFacade().listaContasBancariasPorPessoa(grupoPessoasDiarias.getPessoa(), parte.trim());
    }

    public ConverterAutoComplete getConverterContaCorrente() {
        if (converterContaCorrente == null) {
            converterContaCorrente = new ConverterAutoComplete(ContaCorrenteBancPessoa.class, grupoDiariaFacade.getContaCorrenteBancPessoaFacade());
        }
        return converterContaCorrente;
    }

    public List<ClasseCredor> completaClasseCredor(String parte) {
        return grupoDiariaFacade.getClasseCredorFacade().listaFiltrandoPorPessoaPorTipoClasse(parte.trim(), grupoPessoasDiarias.getPessoa(), TipoClasseCredor.DIARIA_CAMPO);
    }

    public ConverterAutoComplete getConverterClasseCredor() {
        if (converterClasseCredor == null) {
            converterClasseCredor = new ConverterAutoComplete(ClasseCredor.class, grupoDiariaFacade.getClasseCredorFacade());
        }
        return converterClasseCredor;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/grupo-pessoa-diaria/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public GrupoPessoasDiarias getGrupoPessoasDiarias() {
        return grupoPessoasDiarias;
    }

    public void setGrupoPessoasDiarias(GrupoPessoasDiarias grupoPessoasDiarias) {
        this.grupoPessoasDiarias = grupoPessoasDiarias;
    }
}
