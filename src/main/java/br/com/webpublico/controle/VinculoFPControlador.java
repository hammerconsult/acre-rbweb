/*
 * Codigo gerado automaticamente em Thu Aug 04 09:41:08 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.UtilRH;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@SessionScoped
public class VinculoFPControlador extends SuperControladorCRUD implements Serializable {

    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    private ConverterGenerico converterUnidadeOrganizacional;
    @EJB
    private MatriculaFPFacade matriculaFPFacade;
    private ConverterGenerico converterMatriculaFP;
    @EJB
    private ContaCorrenteFacade contaCorrenteFacade;
    private ConverterAutoComplete converterContaCorrente;
    @EJB
    private RecursoDoVinculoFPFacade recursoDoVinculoFPFacade;
    private ConverterGenerico converterRecursoDoVinculo;
    private ConverterAutoComplete converterVinculoFP;

    private Long numeroSelecionado;

    public VinculoFPControlador() {
        metadata = new EntidadeMetaData(VinculoFP.class);
    }

    public VinculoFPFacade getFacade() {
        return vinculoFPFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return vinculoFPFacade;
    }

    public List<SelectItem> getUnidadeOrganizacional() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (UnidadeOrganizacional object : unidadeOrganizacionalFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterUnidadeOrganizacional() {
        if (converterUnidadeOrganizacional == null) {
            converterUnidadeOrganizacional = new ConverterGenerico(UnidadeOrganizacional.class, unidadeOrganizacionalFacade);
        }
        return converterUnidadeOrganizacional;
    }

    public List<SelectItem> getMatriculaFP() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, "Selecione"));
        for (MatriculaFP object : matriculaFPFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getMatricula()));
        }
        return toReturn;
    }

    @Override
    public void selecionar(ActionEvent evento) {
        super.selecionar(evento);
        numeroSelecionado = ((VinculoFP) selecionado).getMatriculaFP().getId();
    }

    public String getNumero() {
        String numero = "";
        if (((VinculoFP) selecionado).getId() == null && ((VinculoFP) selecionado).getMatriculaFP() != null) {
            numero = vinculoFPFacade.retornaCodigo(((VinculoFP) selecionado).getMatriculaFP());
        } else if (!operacao.equals(Operacoes.NOVO)) {
            numero = ((VinculoFP) selecionado).getNumero();
            if (!numeroSelecionado.equals(((VinculoFP) selecionado).getMatriculaFP().getId())) {
                numero = vinculoFPFacade.retornaCodigo(((VinculoFP) selecionado).getMatriculaFP());
            }
        }

        return numero;
    }

    @Override
    public String salvar() {
        ((VinculoFP) selecionado).setNumero(getNumero());
        if (Operacoes.NOVO.equals(operacao)) {
            ((VinculoFP) selecionado).setDataRegistro(UtilRH.getDataOperacao());
        } else {
            ((VinculoFP) selecionado).setDataAlteracao(UtilRH.getDataOperacao());
        }
        return super.salvar();
    }

    public List<SelectItem> getRecursoDoVinculo() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (RecursoDoVinculoFP object : recursoDoVinculoFPFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getDataRegistro() + ""));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterMatriculaFP() {
        if (converterMatriculaFP == null) {
            converterMatriculaFP = new ConverterGenerico(MatriculaFP.class, matriculaFPFacade);
        }
        return converterMatriculaFP;
    }

    public ConverterGenerico getConverterRecursoDoVinculo() {
        if (converterRecursoDoVinculo == null) {
            converterRecursoDoVinculo = new ConverterGenerico(RecursoDoVinculoFP.class, recursoDoVinculoFPFacade);
        }
        return converterRecursoDoVinculo;
    }

    public Converter getConverterConta() {
        if (converterContaCorrente == null) {
            converterContaCorrente = new ConverterAutoComplete(ContaCorrenteBancaria.class, contaCorrenteFacade);
        }
        return converterContaCorrente;
    }

    public List<ContaCorrenteBancaria> completaContaCorrente(String parte) {
        return contaCorrenteFacade.listaFiltrando(parte.trim(), "digitoVerificador", "numeroConta");
    }

    public ConverterAutoComplete getConverterVinculoFP() {
        if (converterVinculoFP == null) {
            converterVinculoFP = new ConverterAutoComplete(VinculoFP.class, vinculoFPFacade);
        }
        return converterVinculoFP;
    }
}
