/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gustavo
 */

//
//


@ManagedBean(name = "segundaViaCarneControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoEmissaodeCarne", pattern = "/tributario/atendimento/emissao-de-carnes/", viewId = "/faces/tributario/contacorrente/segundaviacarne/consulta.xhtml")
})

public class SegundaViaCarneControlador extends AbstractReport {

    private TipoCadastroTributario tipoCadastro;
    private Exercicio exercicio;
    private Divida divida;
    private Cadastro cadastro;
    @EJB
    private CadastroFacade cadastroFacade;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private LoteFacade loteFacade;
    private List<ValorDivida> lista;
    private ConverterAutoComplete converterCadastroImobiliario;
    private ConverterAutoComplete converterCadastroEconomico;
    private ConverterAutoComplete converterCadastroRendasPatrimoniais;
    private ConverterAutoComplete converterCadastroRural;
    private ConverterAutoComplete converterExercicio;
    private ConverterAutoComplete converterDivida;
    private ValorDivida valorDividaSelecionado;
    private String mensagemCarne;
    private Boolean somenteParcExercCorrente;


    @URLAction(mappingId = "novoEmissaodeCarne", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        exercicio = new Exercicio();
        cadastro = null;
        divida = new Divida();
        tipoCadastro = TipoCadastroTributario.IMOBILIARIO;
        lista = new ArrayList<ValorDivida>();
        somenteParcExercCorrente = Boolean.FALSE;
    }

    public Boolean getSomenteParcExercCorrente() {
        return somenteParcExercCorrente;
    }

    public void setSomenteParcExercCorrente(Boolean somenteParcExercCorrente) {
        this.somenteParcExercCorrente = somenteParcExercCorrente;
    }

    public Cadastro getCadastro() {
        return cadastro;
    }

    public void setCadastro(Cadastro cadastro) {
        this.cadastro = cadastro;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public TipoCadastroTributario getTipoCadastro() {
        return tipoCadastro;
    }

    public void setTipoCadastro(TipoCadastroTributario tipoCadastro) {
        this.tipoCadastro = tipoCadastro;
    }

    public List<CadastroImobiliario> completaCadastroImobiliario(String parte) {
        return cadastroFacade.completaCadastroImobiliario(parte.trim());
    }

    public List<CadastroRural> completaCadastroRural(String parte) {
        return cadastroFacade.completaCadastroRural(parte.trim());
    }

    public List<CadastroEconomico> completaCadastroEconomico(String parte) {
        return cadastroFacade.completaCadastroEconomico(parte.trim());
    }

    public List<ContratoRendasPatrimoniais> completaContratolRendasPatrimonial(String parte) {
        return cadastroFacade.completaContratoRendasPatrimoniais(parte.trim());
    }

    public List<Divida> completaDivida(String parte) {
        return dividaFacade.listaFiltrando(parte.trim(), "descricao");
    }

    public List<Exercicio> completaExercicio(String parte) {
        return exercicioFacade.listaFiltrandoEspecial(parte.trim());
    }

    public List<ValorDivida> getLista() {
        return lista;
    }

    public void setLista(List<ValorDivida> lista) {
        this.lista = lista;
    }

    public void iniciaBusca() {
        //System.out.println("entrou no inicia busca");
        if (validaBusca()) {
            //System.out.println("boolean = " + somenteParcExercCorrente);
            if (somenteParcExercCorrente) {
                //System.out.println("buscaDividasCarneAno");
                lista = consultaDebitoFacade.buscaDividasCarneAno(divida, cadastro, exercicio);
            } else {
                lista = consultaDebitoFacade.buscaDividasCarne(divida, cadastro, exercicio);
            }
            //System.out.println("tamanho da lista = " + lista.size());
        }
    }

    public String retornaNumeroCadastro(ValorDivida valorDivida) {
        try {
            return consultaDebitoFacade.recuperaNumerocadastroPorValorDivida(valorDivida);
        } catch (NullPointerException e) {
            return " - ";
        } catch (Exception e) {
            return "Houve um erro ao recuperar as informações!";
        }
    }

    public boolean validaBusca() {
        boolean retorno = true;
        //System.out.println("entrou no valida busca");
        if (divida == null || divida.getId() == null) {
            FacesUtil.addError("Não foi possível continuar!", "Informe a Dívida desejada.");
            retorno = false;
        }
        if (cadastro == null || cadastro.getId() == null) {
            FacesUtil.addError("Não foi possível continuar!", "Informe o Cadastro desejado!");
            retorno = false;
        }
        if (exercicio == null || exercicio.getId() == null) {
            FacesUtil.addError("Não foi possível continuar!", "Informe o Exercício desejado!");
            retorno = false;
        }

        return retorno;
    }

    public ConverterAutoComplete getConverterCadastroImobiliario() {
        if (converterCadastroImobiliario == null) {
            converterCadastroImobiliario = new ConverterAutoComplete(CadastroImobiliario.class, cadastroFacade.getCadastroImobiliarioFacade());
        }
        return converterCadastroImobiliario;
    }

    public ConverterAutoComplete getConverterDivida() {
        if (converterDivida == null) {
            converterDivida = new ConverterAutoComplete(Divida.class, dividaFacade);
        }
        return converterDivida;
    }

    public ConverterAutoComplete getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterAutoComplete(Exercicio.class, exercicioFacade);
        }
        return converterExercicio;
    }

    public ConverterAutoComplete getConverterCadastroEconomico() {
        if (converterCadastroEconomico == null) {
            converterCadastroEconomico = new ConverterAutoComplete(CadastroEconomico.class, cadastroFacade.getCadastroEconomicoFacade());
        }
        return converterCadastroEconomico;
    }

    public ConverterAutoComplete getConverterCadastroRural() {
        if (converterCadastroRural == null) {
            converterCadastroRural = new ConverterAutoComplete(CadastroRural.class, cadastroFacade.getCadastroRuralFacade());
        }
        return converterCadastroRural;
    }

    public ConverterAutoComplete getConverterContratoRendasPatrimoniais() {
        if (converterCadastroRendasPatrimoniais == null) {
            converterCadastroRendasPatrimoniais = new ConverterAutoComplete(ContratoRendasPatrimoniais.class, cadastroFacade.getContratoRendasPatrimoniaisFacade());
        }
        return converterCadastroRendasPatrimoniais;
    }

    public List<SelectItem> getTiposCadastros() {
        List<SelectItem> retorno = new ArrayList<>();
        for (TipoCadastroTributario tipo : TipoCadastroTributario.values()) {
            if (!tipo.equals(TipoCadastroTributario.PESSOA)) {
                retorno.add(new SelectItem(tipo, tipo.getDescricao()));
            }
        }
        return retorno;
    }

    public void imprimeCarne() throws JRException, IOException {
        if (valorDividaSelecionado != null) {
            if (somenteParcExercCorrente) {
                //System.out.println("imprimecarne->somenteParcExercCorrente");
                consultaDebitoFacade.geraCarne(valorDividaSelecionado, exercicio, " and to_char(pvd.vencimento, 'yyyy') = " + exercicio.getAno().toString());
            } else {
                consultaDebitoFacade.geraCarne(valorDividaSelecionado, exercicio);
            }
        }
    }

    public void selecionaValorDivida(ValorDivida valor) {
        valorDividaSelecionado = valor;
        if (somenteParcExercCorrente) {
            mensagemCarne = consultaDebitoFacade.montaMensagemCarne(valorDividaSelecionado, exercicio.getAno());
        } else {
            mensagemCarne = consultaDebitoFacade.montaMensagemCarne(valorDividaSelecionado);
        }
    }

    public String getMensagemCarne() {
        return mensagemCarne;
    }

    public void setMensagemCarne(String mensagemCarne) {
        this.mensagemCarne = mensagemCarne;
    }

    public void limpaCadastro() {
        cadastro = null;
    }

    public SituacaoCadastroEconomico getSituacaoCadastroEconomico() {
        if (cadastro != null) {
            return cadastroFacade.getCadastroEconomicoFacade().recuperarUltimaSituacaoDoCadastroEconomico((CadastroEconomico) cadastro);
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

    public List<Pessoa> recuperaPessoasCadastro() {
        List<Pessoa> retorno = new ArrayList<Pessoa>();
        if (cadastro instanceof CadastroImobiliario) {
            CadastroImobiliario cadastroIm = cadastroFacade.getCadastroImobiliarioFacade().recuperar(cadastro.getId());
            for (Propriedade p : cadastroIm.getPropriedade()) {
                retorno.add(p.getPessoa());
            }
        }
        if (cadastro instanceof CadastroRural) {
            CadastroRural cadastroRu = cadastroFacade.getCadastroRuralFacade().recuperar(cadastro.getId());
            for (PropriedadeRural p : cadastroRu.getPropriedade()) {
                retorno.add(p.getPessoa());
            }
        }
        if (cadastro instanceof CadastroEconomico) {
            CadastroEconomico cadastroEco = cadastroFacade.getCadastroEconomicoFacade().recuperar(cadastro.getId());
            for (SociedadeCadastroEconomico sociedadeCadastroEconomico : cadastroEco.getSociedadeCadastroEconomico()) {
                retorno.add(sociedadeCadastroEconomico.getSocio());
            }
        }
        return retorno;
    }
}
