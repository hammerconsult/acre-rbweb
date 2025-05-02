/*
 * Codigo gerado automaticamente em Thu Nov 22 14:40:47 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.AcaoPrincipalFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.*;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "execucao-meta", pattern = "/ldo/execucao-meta/", viewId = "/faces/financeiro/ppa/acao/execucao_meta.xhtml"),
})
public class ExecucaoDaMetaControlador implements Serializable {

    @EJB
    private AcaoPrincipalFacade acaoPrincipalFacade;
    private ConverterAutoComplete converterPrograma;
    private ConverterAutoComplete converterAcaoPPA;
    private MoneyConverter moneyConverter;
    private ProgramaPPA programaPPA;
    private AcaoPrincipal acaoPPA;
    private ProdutoPPA produtoPPA;

    public ExecucaoDaMetaControlador() {
    }

    private SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public void novoLancamento() {
        this.programaPPA = null;
        this.acaoPPA = null;
        this.produtoPPA = null;
        FacesUtil.executaJavaScript("setaFoco('Formulario:programaPPA_input')");
    }

    @URLAction(mappingId = "execucao-meta", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
    }


    public void salvar() {
        try {
            for (ProvisaoPPA provisaoPPA : produtoPPA.getProvisoes()) {
                acaoPrincipalFacade.getProvisaoPPAFacade().salvar(provisaoPPA);
            }
            FacesUtil.addOperacaoRealizada("Execução da Meta realizada com sucesso.");
            FacesUtil.executaJavaScript("dialogFinalizar.show()");
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public void redirecionarParaLista() {
        FacesUtil.redirecionamentoInterno("/acao-ppa/listar/");
    }

    public List<ProgramaPPA> completaProgramaPPA(String parte) {
        try {
            return acaoPrincipalFacade.getProgramaPPAFacade().listaFiltrandoProgramasPorExercicio(parte.trim(), getSistemaControlador().getExercicioCorrente());
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public List<AcaoPrincipal> completaAcaoPPA(String parte) {
        try {
            if (programaPPA != null) {
                return acaoPrincipalFacade.listaAcaoPPAPorProgramaPPA(programaPPA, parte.trim());
            }
        } catch (Exception ex) {
        }
        return new ArrayList<>();
    }

    public void desatribuirProdutoPPASelecionado() {
        this.produtoPPA = null;
    }

    public void recuperarAcaoPPA() {
        acaoPPA = acaoPrincipalFacade.recuperar(acaoPPA.getId());
        for (ProdutoPPA prod : acaoPPA.getProdutoPPAs()) {
            produtoPPA = acaoPrincipalFacade.getSubAcaoPPAFacade().recuperar(prod.getId());
        }
        iniciarValoresExecucao();
    }

    public void recuperarPrograma() {
        programaPPA = acaoPrincipalFacade.getProgramaPPAFacade().recuperar(programaPPA.getId());
    }


    public void atribuirProdutoPPASelecionado(ProdutoPPA prodPPA) {
        produtoPPA = acaoPrincipalFacade.getSubAcaoPPAFacade().recuperar(prodPPA.getId());

        iniciarValoresExecucao();

        if (produtoPPA.getProvisoes().isEmpty()) {
            preencherProvisoes(produtoPPA);
        }
    }

    private void iniciarValoresExecucao() {
        for (ProvisaoPPA provisaoPPA : produtoPPA.getProvisoes()) {
            if (provisaoPPA.getMetaFinancCapitalExecutada() == null) {
                provisaoPPA.setMetaFinancCapitalExecutada(BigDecimal.ZERO);
            }
            if (provisaoPPA.getMetaFinancCorrenteExecutada() == null) {
                provisaoPPA.setMetaFinancCorrenteExecutada(BigDecimal.ZERO);
            }
            if (provisaoPPA.getMetaFisicaExecutada() == null) {
                provisaoPPA.setMetaFisicaExecutada(BigDecimal.ZERO);
            }
        }
    }

    public void duplicar(ProvisaoPPA provisao) {
        ProvisaoPPA nova = new ProvisaoPPA(provisao.getExercicio(), provisao.getProdutoPPA(), provisao.getMetaFisica(), provisao.getMetaFinanceiraCorrente(), provisao.getMetaFinanceiraCapital(), provisao.getDataRegistro());
        nova.setMetaFinancCapitalExecutada(BigDecimal.ZERO);
        nova.setMetaFinancCorrenteExecutada(BigDecimal.ZERO);
        nova.setMetaFisicaExecutada(BigDecimal.ZERO);
        produtoPPA.getProvisoes().add(nova);
        Collections.sort(produtoPPA.getProvisoes(), new Comparator<ProvisaoPPA>() {
            @Override
            public int compare(ProvisaoPPA o1, ProvisaoPPA o2) {
                return o1.getExercicio().getAno().compareTo(o2.getExercicio().getAno());
            }
        });
    }

    public void preencherProvisoes(ProdutoPPA produtoPPA) {
        Calendar cinicio = new GregorianCalendar();
        Calendar cfim = new GregorianCalendar();
        if (programaPPA != null) {

            if (programaPPA.getInicio() != null) {
                cinicio.setTime(programaPPA.getInicio());
            }
            if (programaPPA.getFim() != null) {
                cfim.setTime(programaPPA.getFim());
            }
            try {
                validarItemPlanejamentoEstrategico();
                PlanejamentoEstrategico pe = acaoPrincipalFacade.getPlanejamentoEstrategicoFacade().recuperar(programaPPA.getItemPlanejamentoEstrategico().getPlanejamentoEstrategico().getId());
                for (ExercicioPlanoEstrategico epe : pe.getExerciciosPlanoEstrategico()) {
                    ProvisaoPPA provisaoPPASelecionado = new ProvisaoPPA(epe.getExercicio(), produtoPPA, new BigDecimal(BigInteger.ZERO), new BigDecimal(BigInteger.ZERO), new BigDecimal(BigInteger.ZERO), SistemaFacade.getDataCorrente());
                    produtoPPA.getProvisoes().add(provisaoPPASelecionado);
                }
            } catch (ValidacaoException ve) {
                FacesUtil.printAllFacesMessages(ve.getMensagens());
            } catch (Exception ex) {
                FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
            }
        }
    }


    private void validarItemPlanejamentoEstrategico() {
        ValidacaoException ve = new ValidacaoException();
        PlanejamentoEstrategico pe = acaoPrincipalFacade.getPlanejamentoEstrategicoFacade().recuperar(programaPPA.getItemPlanejamentoEstrategico().getPlanejamentoEstrategico().getId());
        if (pe == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O programa " + programaPPA + " não possui um Item Planejamento Estratégico.");
        }
        if (programaPPA.getItemPlanejamentoEstrategico() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não existem Objetivos dos Eixos cadastrados.");
        }
        ve.lancarException();
    }

    public Boolean getSelecionarProduto() {
        if (produtoPPA == null) {
            return false;
        } else {
            produtoPPA.validaSomaFinanceiraProvisoes(produtoPPA);
            produtoPPA.validaSomaFisicaProvisoes(produtoPPA);
            return true;
        }
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public ConverterAutoComplete getConverterPrograma() {
        if (converterPrograma == null) {
            converterPrograma = new ConverterAutoComplete(ProgramaPPA.class, acaoPrincipalFacade.getProgramaPPAFacade());
        }
        return converterPrograma;
    }

    public ConverterAutoComplete getConverterAcaoPPA() {
        if (converterAcaoPPA == null) {
            converterAcaoPPA = new ConverterAutoComplete(AcaoPrincipal.class, acaoPrincipalFacade);
        }
        return converterAcaoPPA;
    }

    public AcaoPrincipalFacade getAcaoPrincipalFacade() {
        return acaoPrincipalFacade;
    }

    public void setAcaoPrincipalFacade(AcaoPrincipalFacade acaoPrincipalFacade) {
        this.acaoPrincipalFacade = acaoPrincipalFacade;
    }


    public ProgramaPPA getProgramaPPA() {
        return programaPPA;
    }

    public void setProgramaPPA(ProgramaPPA programaPPA) {
        this.programaPPA = programaPPA;
    }


    public ProdutoPPA getProdutoPPA() {
        return produtoPPA;
    }

    public void setProdutoPPA(ProdutoPPA produtoPPA) {
        this.produtoPPA = produtoPPA;
    }

    public AcaoPrincipal getAcaoPPA() {
        return acaoPPA;
    }

    public void setAcaoPPA(AcaoPrincipal acaoPPA) {
        this.acaoPPA = acaoPPA;
    }
}
