/*
 * Codigo gerado automaticamente em Wed Jun 29 14:21:41 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ReferenciaFPFuncao;
import br.com.webpublico.enums.TipoEmpenhoFP;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.EntidadeMetaData;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.persistence.NoResultException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@SessionScoped
public class TesteFuncoesFacadeControlador extends SuperControladorCRUD implements Serializable {

    @EJB
    private FuncoesFolhaFacade funcoesFolhaFacade;
    @EJB
    private EventoFPFacade eventoFPFacade;
    private PessoaFisica pessoaFisica;
    @EJB
    private FormulaPadraoFPFacade formulaPadraoFPFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    private ConverterAutoComplete converterVinculos;
    private VinculoFP vinculoFPSalarioBase;
    private VinculoFP vinculoFPObterModalidade;
    private VinculoFP vinculoFPTipoRegime;
    private VinculoFP vinculoFPObterDependentes;
    private VinculoFP vinculoFPObterModalidadeBeneficio;
    private VinculoFP vinculoFPObterItensBaseFP;
    private BaseFP baseFP;
    @EJB
    private BaseFPFacade baseFPFacade;
    private TipoDependente tipoDependente;
    @EJB
    private TipoDependenteFacade tipoDependenteFacade;
    private ConverterGenerico converterTipoDependente;
    private ConverterGenerico converterBaseFP;
    private VinculoFP vinculoFPTipoPrevidencia;
    private VinculoFP vinculoFPContaLancemto;
    private VinculoFP vinculoFPQuantificacao;
    private VinculoFP vinculoFPQuantificacaoValor;
    private VinculoFP vinculoFPObterHoraMensal;
    private VinculoFP vinculoFPObterPercentualNatureza;
    private EventoFP eventoFPContaLancamento;
    private EventoFP eventoFPQuantificacao;
    private EventoFP eventoFPQuantificacaoValor;
    private Double valor;
    private ReferenciaFP referenciaFP;
    private ReferenciaFP referenciaFPValor;
    private ConverterGenerico converterReferenciaFP;
    @EJB
    private ReferenciaFPFacade referenciaFPFacade;
    private ConverterAutoComplete converterEventos;
    private Sindicato sindicato;
    @EJB
    private SindicatoFacade sindicatoFacade;
    private VinculoFP vinculoFPSindicato;
    private VinculoFP vinculoFPTemFeriasConcedidas;
    private ConverterGenerico converterSindicatos;

    public TesteFuncoesFacadeControlador() {
        metadata = new EntidadeMetaData(FolhaDePagamento.class);
    }

    public FuncoesFolhaFacade getFacade() {
        return funcoesFolhaFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return funcoesFolhaFacade;
    }

    public List<SelectItem> getTiposEmpenhos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoEmpenhoFP object : TipoEmpenhoFP.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getSindicatos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (Sindicato object : sindicatoFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public Converter getConverterVinculos() {
        if (converterVinculos == null) {
            converterVinculos = new ConverterAutoComplete(VinculoFP.class, vinculoFPFacade);
        }
        return converterVinculos;
    }

    public Converter getConverterSindicatos() {
        if (converterSindicatos == null) {
            converterSindicatos = new ConverterGenerico(Sindicato.class, sindicatoFacade);
        }
        return converterSindicatos;
    }

    public Converter getConverterEventos() {
        if (converterEventos == null) {
            converterEventos = new ConverterAutoComplete(EventoFP.class, eventoFPFacade);
        }
        return converterEventos;
    }

    public ConverterGenerico getConverterReferenciaFP() {
        if (converterReferenciaFP == null) {
            converterReferenciaFP = new ConverterGenerico(ReferenciaFP.class, referenciaFPFacade);
        }
        return converterReferenciaFP;
    }

    public ConverterGenerico getConverterTipoDependentes() {
        if (converterTipoDependente == null) {
            converterTipoDependente = new ConverterGenerico(TipoDependente.class, tipoDependenteFacade);
        }
        return converterTipoDependente;
    }

    public ConverterGenerico getConverterBaseFps() {
        if (converterBaseFP == null) {
            converterBaseFP = new ConverterGenerico(BaseFP.class, baseFPFacade);
        }
        return converterBaseFP;
    }

    public List<SelectItem> getTipoFolhaDePagamento() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoFolhaDePagamento object : TipoFolhaDePagamento.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getReferenciaFPs() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (ReferenciaFP object : referenciaFPFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoDependentes() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoDependente object : tipoDependenteFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getBaseFPs() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (BaseFP object : baseFPFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<VinculoFP> completaVinculoFPs(String parte) {
        return vinculoFPFacade.listaTodosVinculos(parte.trim());
    }

    public List<EventoFP> completaEventoFPs(String parte) {
        return eventoFPFacade.listaFiltrando(parte.trim(), "descricao");
    }

    public Double getConsultaSalarioBase() {
        if (vinculoFPSalarioBase != null) {
            return funcoesFolhaFacade.salarioBase(vinculoFPSalarioBase).doubleValue();
        }
        return 0.0;
    }

    public ModalidadeContratoFP getConsultaObterModalideContratoFP() {
        if (vinculoFPObterModalidade != null) {
            return funcoesFolhaFacade.obterModalidadeContratoFP(vinculoFPObterModalidade);
        }
        return null;
    }

    public TipoRegime getConsultaObterTipoRegime() {
        if (vinculoFPTipoRegime != null) {
            return funcoesFolhaFacade.obterTipoRegime(vinculoFPTipoRegime);
        }
        return null;
    }

    public Integer getConsultaObterNumeroDependentes() {
        if (vinculoFPObterDependentes != null && tipoDependente != null) {
            return funcoesFolhaFacade.obterNumeroDependentes(tipoDependente.getCodigo(), vinculoFPObterDependentes);
        }
        return null;
    }

    public ModalidadeBeneficioFP getConsultaObterModalideBeneficio() {

        if (vinculoFPObterModalidadeBeneficio != null) {
            try {
                return funcoesFolhaFacade.obterModalidadeBeneficioFP(vinculoFPObterModalidadeBeneficio);
            } catch (NoResultException ex) {
                ModalidadeBeneficioFP m = new ModalidadeBeneficioFP();
                m.setDescricao("Nenhum Resultado Para a Consulta");
                return m;
            } catch (Exception e) {
                //System.out.println("OpsProblemas...." + e);
            }

        }
        return null;
    }

    public List<ItemBaseFP> getConsultaObterItensBase() {
        if (baseFP != null) {
            try {
                return funcoesFolhaFacade.obterItensBaseFP(baseFP.getCodigo());
            } catch (Exception e) {
                logger.error("Erro: ", e);
            }

        }
        return null;
    }

    public String getConsultaObterTipoPredencia() {

        if (vinculoFPTipoPrevidencia != null) {
            try {
                return funcoesFolhaFacade.obterTipoPrevidenciaFP(vinculoFPTipoPrevidencia, 11, 2011);
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Error:" + e));
                //System.out.println("OpsProblemas...." + e);
            }

        }
        return null;
    }

    public ReferenciaFPFuncao getConsultaObterReferenciaFaixaFP() {

        if (referenciaFP != null && valor != null) {
            try {
                return funcoesFolhaFacade.obterReferenciaFaixaFP(vinculoFPContaLancemto, referenciaFP.getCodigo(), valor, 11, 2011);
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage("msgs", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Error:" + e));
                //System.out.println("OpsProblemas...." + e);
            }

        }
        return null;
    }

    public ReferenciaFPFuncao getConsultaObterReferenciaValorFP() {

        if (referenciaFPValor != null) {
            try {
                return funcoesFolhaFacade.obterReferenciaValorFP(vinculoFPContaLancemto, referenciaFPValor.getCodigo(), 11, 2011);
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage("msgs", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Error:" + e));
                //System.out.println("OpsProblemas...." + e);
            }
        }
        return null;
    }

    public Integer getConsultaContaLancamento() {

        if (vinculoFPContaLancemto != null && eventoFPContaLancamento != null) {
            try {
                return funcoesFolhaFacade.contaLancamento(vinculoFPContaLancemto, eventoFPContaLancamento.getCodigo(), 11, 2011);
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage("msgs", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Error:" + e));
                //System.out.println("OpsProblemas...." + e);
            }

        }
        return null;
    }

    public Double getConsultaQuantificacao() {

        if (vinculoFPQuantificacao != null && eventoFPQuantificacao != null) {
            try {
                return funcoesFolhaFacade.recuperaQuantificacaoLancamentoTipoReferencia(vinculoFPQuantificacao, eventoFPQuantificacao.getCodigo(), 11, 2011);
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage("msgs", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Error:" + e));
                //System.out.println("OpsProblemas...." + e);
            }

        }
        return null;
    }

    public Double getConsultaQuantificacaoValor() {

        if (vinculoFPQuantificacaoValor != null && eventoFPQuantificacaoValor != null) {
            try {
                return funcoesFolhaFacade.recuperaQuantificacaoLancamentoTipoValor(vinculoFPQuantificacaoValor, eventoFPQuantificacaoValor.getCodigo(), 11, 2011);
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage("msgs", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Error:" + e));
                //System.out.println("OpsProblemas...." + e);
            }

        }
        return null;
    }

    public Double getConsultaObterHoraMensal() {

        if (vinculoFPObterHoraMensal != null) {
            try {
                return funcoesFolhaFacade.obterHoraMensal(vinculoFPObterHoraMensal);
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage("msgs", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Error:" + e));
                //System.out.println("OpsProblemas...." + e);
            }

        }
        return null;
    }

    public Double getConsultaObterPercentualNaturezaAtividade() {

        if (vinculoFPObterPercentualNatureza != null) {
            try {
                return funcoesFolhaFacade.recuperarPercentualPorTipoNaturezaAtividade(vinculoFPObterPercentualNatureza, 11, 2011, 1);
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage("msgs", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Error:" + e));
                //System.out.println("OpsProblemas...." + e);
            }

        }
        return null;
    }


    public String getConsultaTemFeriasConcedidas() {

        if (vinculoFPTemFeriasConcedidas != null) {
            try {
                if (funcoesFolhaFacade.temFeriasConcedidas(vinculoFPTemFeriasConcedidas)) {
                    return "SIM";
                } else {
                    return "NÃO";
                }
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage("msgs", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Error:" + e));
                //System.out.println("OpsProblemas...." + e);
            }

        }
        return "";
    }

    public VinculoFP getVinculoFPObterModalidade() {
        return vinculoFPObterModalidade;
    }

    public void setVinculoFPObterModalidade(VinculoFP vinculoFPObterModalidade) {
        this.vinculoFPObterModalidade = vinculoFPObterModalidade;
    }

    public VinculoFP getVinculoFPSalarioBase() {
        return vinculoFPSalarioBase;
    }

    public void setVinculoFPSalarioBase(VinculoFP vinculoFPSalarioBase) {
        this.vinculoFPSalarioBase = vinculoFPSalarioBase;
    }

    public VinculoFP getVinculoFPTipoRegime() {
        return vinculoFPTipoRegime;
    }

    public void setVinculoFPTipoRegime(VinculoFP vinculoFPTipoRegime) {
        this.vinculoFPTipoRegime = vinculoFPTipoRegime;
    }

    public TipoDependente getTipoDependente() {
        return tipoDependente;
    }

    public void setTipoDependente(TipoDependente tipoDependente) {
        this.tipoDependente = tipoDependente;
    }

    public VinculoFP getVinculoFPObterDependentes() {
        return vinculoFPObterDependentes;
    }

    public void setVinculoFPObterDependentes(VinculoFP vinculoFPObterDependentes) {
        this.vinculoFPObterDependentes = vinculoFPObterDependentes;
    }

    public VinculoFP getVinculoFPObterModalidadeBeneficio() {
        return vinculoFPObterModalidadeBeneficio;
    }

    public void setVinculoFPObterModalidadeBeneficio(VinculoFP vinculoFPObterModalidadeBeneficio) {
        this.vinculoFPObterModalidadeBeneficio = vinculoFPObterModalidadeBeneficio;
    }

    public VinculoFP getVinculoFPObterItensBaseFP() {
        return vinculoFPObterItensBaseFP;
    }

    public void setVinculoFPObterItensBaseFP(VinculoFP vinculoFPObterItensBaseFP) {
        this.vinculoFPObterItensBaseFP = vinculoFPObterItensBaseFP;
    }

    public BaseFP getBaseFP() {
        return baseFP;
    }

    public void setBaseFP(BaseFP baseFP) {
        this.baseFP = baseFP;
    }

    public VinculoFP getVinculoFPTipoPrevidencia() {
        return vinculoFPTipoPrevidencia;
    }

    public void setVinculoFPTipoPrevidencia(VinculoFP vinculoFPTipoPrevidencia) {
        this.vinculoFPTipoPrevidencia = vinculoFPTipoPrevidencia;
    }

    public ReferenciaFP getReferenciaFP() {
        return referenciaFP;
    }

    public void setReferenciaFP(ReferenciaFP referenciaFP) {
        this.referenciaFP = referenciaFP;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public ReferenciaFP getReferenciaFPValor() {
        return referenciaFPValor;
    }

    public void setReferenciaFPValor(ReferenciaFP referenciaFPValor) {
        this.referenciaFPValor = referenciaFPValor;
    }

    public EventoFP getEventoFPContaLancamento() {
        return eventoFPContaLancamento;
    }

    public void setEventoFPContaLancamento(EventoFP eventoFPContaLancamento) {
        this.eventoFPContaLancamento = eventoFPContaLancamento;
    }

    public VinculoFP getVinculoFPContaLancemto() {
        return vinculoFPContaLancemto;
    }

    public void setVinculoFPContaLancemto(VinculoFP vinculoFPContaLancemto) {
        this.vinculoFPContaLancemto = vinculoFPContaLancemto;
    }

    public EventoFP getEventoFPQuantificacao() {
        return eventoFPQuantificacao;
    }

    public void setEventoFPQuantificacao(EventoFP eventoFPQuantificacao) {
        this.eventoFPQuantificacao = eventoFPQuantificacao;
    }

    public VinculoFP getVinculoFPQuantificacao() {
        return vinculoFPQuantificacao;
    }

    public void setVinculoFPQuantificacao(VinculoFP vinculoFPQuantificacao) {
        this.vinculoFPQuantificacao = vinculoFPQuantificacao;
    }

    public EventoFP getEventoFPQuantificacaoValor() {
        return eventoFPQuantificacaoValor;
    }

    public void setEventoFPQuantificacaoValor(EventoFP eventoFPQuantificacaoValor) {
        this.eventoFPQuantificacaoValor = eventoFPQuantificacaoValor;
    }

    public VinculoFP getVinculoFPQuantificacaoValor() {
        return vinculoFPQuantificacaoValor;
    }

    public void setVinculoFPQuantificacaoValor(VinculoFP vinculoFPQuantificacaoValor) {
        this.vinculoFPQuantificacaoValor = vinculoFPQuantificacaoValor;
    }

    public VinculoFP getVinculoFPObterHoraMensal() {
        return vinculoFPObterHoraMensal;
    }

    public void setVinculoFPObterHoraMensal(VinculoFP vinculoFPObterHoraMensal) {
        this.vinculoFPObterHoraMensal = vinculoFPObterHoraMensal;
    }

    public Sindicato getSindicato() {
        return sindicato;
    }

    public void setSindicato(Sindicato sindicato) {
        this.sindicato = sindicato;
    }

    public VinculoFP getVinculoFPSindicato() {
        return vinculoFPSindicato;
    }

    public void setVinculoFPSindicato(VinculoFP vinculoFPSindicato) {
        this.vinculoFPSindicato = vinculoFPSindicato;
    }

    public VinculoFP getVinculoFPTemFeriasConcedidas() {
        return vinculoFPTemFeriasConcedidas;
    }

    public void setVinculoFPTemFeriasConcedidas(VinculoFP vinculoFPTemFeriasConcedidas) {
        this.vinculoFPTemFeriasConcedidas = vinculoFPTemFeriasConcedidas;
    }

    public VinculoFP getVinculoFPObterPercentualNatureza() {
        return vinculoFPObterPercentualNatureza;
    }

    public void setVinculoFPObterPercentualNatureza(VinculoFP vinculoFPObterPercentualNatureza) {
        this.vinculoFPObterPercentualNatureza = vinculoFPObterPercentualNatureza;
    }
}
