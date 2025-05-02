/*
 * Codigo gerado automaticamente em Mon Oct 31 21:28:23 BRST 2011
 * Gerador de Controlador
 *
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
public class AutoCompleteCadastrosControlador implements Serializable {

    private ConverterAutoComplete converterCadastroImobiliario;
    private ConverterAutoComplete converterCadastroEconomico;
    private ConverterAutoComplete converterCadastroRendasPatrimoniais;
    private ConverterAutoComplete converterCadastroRural;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private CadastroRuralFacade cadastroRuralFacade;
    @EJB
    private ContratoRendasPatrimoniaisFacade contratoRendasPatrimoniaisFacade;
    @EJB
    private LoteFacade loteFacade;
    private Cadastro cadastro;

    public List<CadastroImobiliario> completaCadastroImobiliario(String parte) {
//        return processoParcelamentoFacade.completaCadastroImobiliario(parte.trim());
        return cadastroImobiliarioFacade.listaFiltrando(parte.trim(), "inscricaoCadastral");
    }

    public ConverterAutoComplete getConverterCadastroImobiliario() {
        if (converterCadastroImobiliario == null) {
            converterCadastroImobiliario = new ConverterAutoComplete(CadastroImobiliario.class, cadastroImobiliarioFacade);
        }
        return converterCadastroImobiliario;
    }

    public List<CadastroEconomico> completaCadastroEconomico(String parte) {
//        return processoParcelamentoFacade.completaCadastroEconomico(parte.trim());
        return cadastroEconomicoFacade.buscarCadastrosPorInscricaoOrCpfCnpjOrNome(parte.trim());
    }

    public ConverterAutoComplete getConverterCadastroEconomico() {
        if (converterCadastroEconomico == null) {
            converterCadastroEconomico = new ConverterAutoComplete(CadastroEconomico.class, cadastroEconomicoFacade);
        }
        return converterCadastroEconomico;
    }

    public List<CadastroRural> completaCadastroRural(String parte) {
        return cadastroRuralFacade.listaFiltrandoPorCodigo(parte.trim());
    }

    public ConverterAutoComplete getConverterCadastroRural() {
        if (converterCadastroRural == null) {
            converterCadastroRural = new ConverterAutoComplete(CadastroRural.class, cadastroRuralFacade);
        }
        return converterCadastroRural;
    }

    public List<ContratoRendasPatrimoniais> completaContratolRendasPatrimonial(String parte) {
        return contratoRendasPatrimoniaisFacade.listaFiltrando(parte.trim(), "numeroContrato");
    }

    public ConverterAutoComplete getConverterContratoRendasPatrimoniais() {
        if (converterCadastroRendasPatrimoniais == null) {
            converterCadastroRendasPatrimoniais = new ConverterAutoComplete(ContratoRendasPatrimoniais.class, contratoRendasPatrimoniaisFacade);
        }
        return converterCadastroRendasPatrimoniais;
    }

    public Cadastro getCadastro() {
        return cadastro;
    }

    public void setCadastro(Cadastro cadastro) {
        this.cadastro = cadastro;
    }

    public void setaCadastro(SelectEvent e) {
        cadastro = (Cadastro) e.getObject();
    }

    public void setaCadastro(Cadastro c) {
        cadastro = c;
    }

    public SituacaoCadastroEconomico getSituacaoCadastroEconomico() {
        if (cadastro != null) {
            return cadastroEconomicoFacade.recuperarUltimaSituacaoDoCadastroEconomico((CadastroEconomico) cadastro);
        } else {
            return new SituacaoCadastroEconomico();
        }
    }

    public Testada getTestadaPrincipal() {
        if (cadastro != null) {
            return loteFacade.recuperarTestadaPrincipal(((CadastroImobiliario) cadastro).getLote());
        } else {
            return new Testada();
        }
    }
}
