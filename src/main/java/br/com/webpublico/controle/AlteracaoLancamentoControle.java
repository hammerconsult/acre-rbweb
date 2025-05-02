package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.PropositoAtoLegal;
import br.com.webpublico.enums.TipoLancamentoFP;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

@ManagedBean
@SessionScoped
public class AlteracaoLancamentoControle extends SuperControladorCRUD implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(AlteracaoLancamentoControle.class);

    @EJB
    private LancamentoFPFacade lancamentoFPFacade;
    @EJB
    private SituacaoFuncionalFacade situacaoFuncionalFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    private ConverterAutoComplete converterVinculoFP;
    @EJB
    private EventoFPFacade eventoFPFacade;
    private ConverterAutoComplete converterEventoFP;
    private ConverterGenerico converterTipoLancamentoFP;
    private MoneyConverter moneyConverter;
    private ConverterAutoComplete converterAtoLegal;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    private ConverterAutoComplete converterContratoFP;
    private ContratoFP contratoFPFiltro;
    private EventoFP eventoFPFiltro;
    private Date mesAnoFiltro;
    private LancamentoFP original;

    public AlteracaoLancamentoControle() {
        metadata = new EntidadeMetaData(LancamentoFP.class);
    }

    public LancamentoFPFacade getFacade() {
        return lancamentoFPFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return lancamentoFPFacade;
    }

    public List<VinculoFP> vinculoFPs(String parte) {
        return vinculoFPFacade.listaTodosVinculosVigentes(parte.trim());
    }

    @Override
    public void selecionar(ActionEvent evento) {
        operacao = Operacoes.EDITAR;
        selecionado = (LancamentoFP) evento.getComponent().getAttributes().get("objeto");
        selecionado = lancamentoFPFacade.recuperar(((LancamentoFP) selecionado).getId());
        original = (LancamentoFP) Util.clonarObjeto(selecionado);
    }

    @Override
    public String salvar() {
        if (validaCampos()) {
            try {
                lancamentoFPFacade.salvarAlteracaoLancamentoFp(original, (LancamentoFP) selecionado);
                FacesUtil.addMessageInfo("", "Registro salvo com sucesso.");
                return "visualizar";
            } catch (Exception e) {
                FacesUtil.addMessageError("Exeção", "Houve um problema não identificado ao salvar. Erro: " + e);
                return "edita";
            }
//            return super.salvar();
//            if (lancamentoFPFacade.podeSalvarLancamento((LancamentoFP) selecionado)) {
//                return super.salvar();
//            } else {
//                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "Já existe uma Lançamento salva com a mesma vigência!"));
//            }
        }
        return "";
    }

    public Converter getMoneyConverter() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String str) {
                if (str == null || str.toString().trim().equals("")) {
                    return new BigDecimal("0.00");
                } else {
                    str = str.replaceAll(Pattern.quote("."), "");
                    try {
                        str = str.replaceAll(Pattern.quote(","), ".");
                        BigDecimal valor = new BigDecimal(str);
                        return valor;
                    } catch (Exception e) {
                        return new BigDecimal("0.00");
                    }
                }
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object obj) {
                String s = null;
                if (obj == null || obj.toString().trim().equals("")) {
                    return "0,00";
                } else {
                    NumberFormat nf = NumberFormat.getCurrencyInstance();
                    nf.setMinimumFractionDigits(2);
                    nf.setMaximumFractionDigits(3);
                    String saida = String.valueOf(obj).replaceAll("\\.", ",");
                    return saida;
                }
            }
        };
    }

    public ConverterAutoComplete getConverterVinculoFP() {
        if (converterVinculoFP == null) {
            converterVinculoFP = new ConverterAutoComplete(VinculoFP.class, vinculoFPFacade);
        }
        return converterVinculoFP;
    }

    public List<EventoFP> eventoFPs(String parte) {
        return eventoFPFacade.listaFiltrando(parte.trim(), "codigo", "descricao");
    }

    public ConverterAutoComplete getConverterEventoFP() {
        if (converterEventoFP == null) {
            converterEventoFP = new ConverterAutoComplete(EventoFP.class, eventoFPFacade);
        }
        return converterEventoFP;
    }

    public List<SelectItem> getTipoLancamentoFP() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, " "));
        for (TipoLancamentoFP object : TipoLancamentoFP.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public Converter getConverterMesAnoFinal() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                try {
                    if (value != null && !value.equals("")) {

                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        Date dataMesAnoFinal = new Date();
                        value = "01/" + value;
                        dataMesAnoFinal = sdf.parse(value);
                        Calendar c = GregorianCalendar.getInstance();
                        c.setTime(dataMesAnoFinal);
                        Integer ultimoDiaMes = c.getActualMaximum(Calendar.DAY_OF_MONTH);
                        c.set(Calendar.DAY_OF_MONTH, ultimoDiaMes);
                        return c.getTime();
                    }
                } catch (Exception ex) {
                    logger.error("{}", ex);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro do conversão de data, favor insira um valor aceitável"));
                    return null;
                }
                return null;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String dataMesAnoFinal = sdf.format(value);
                StringBuilder builder = new StringBuilder(dataMesAnoFinal);
                builder.replace(0, 3, "");
                return builder.toString();
            }
        };
    }

    public Converter getConverterMesAnoInicial() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                try {
                    if (value != null && !value.equals("")) {

                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        Date dataMesAnoFinal = new Date();
                        value = "01/" + value;
                        dataMesAnoFinal = sdf.parse(value);
                        Calendar c = GregorianCalendar.getInstance();
                        c.setTime(dataMesAnoFinal);
                        Integer ultimoDiaMes = c.getActualMinimum(Calendar.DAY_OF_MONTH);
                        c.set(Calendar.DAY_OF_MONTH, ultimoDiaMes);
                        return c.getTime();
                    }
                } catch (Exception ex) {
                    logger.error("{}", ex);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro do conversão de data, favor insira um valor aceitável"));
                    return null;
                }
                return null;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                String dataMesAnoFinal = sdf.format(value);
                StringBuilder builder = new StringBuilder(dataMesAnoFinal);
                builder.replace(0, 3, "");
                return builder.toString();
            }
        };
    }

    @Override
    public Boolean validaCampos() {
        if (super.validaCampos()) {
            LancamentoFP l = ((LancamentoFP) selecionado);

            if (verificaExistente(l)) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    private boolean verificaExistente(LancamentoFP l) throws NumberFormatException {
        if (!l.getMesAnoInicial().equals("") && !l.getMesAnoFinal().equals("") && !l.getMesAnoInicioCalculo().equals("")) {
            if (l.getQuantificacao() != null && l.getQuantificacao().compareTo(BigDecimal.ZERO) <= 0) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "A quantificação deve ser um número positivo");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return true;
            }
            if (l.getMesAnoInicial().after(l.getMesAnoFinal())) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "A data Inicial é maior que a data final");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return true;
            }
            if (l.getMesAnoInicioCalculo().after(l.getMesAnoInicial())) {
                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "A data Inicio de cálculo é maior que a data inicial da vigência");
                FacesContext.getCurrentInstance().addMessage(null, message);
                return true;
            }
//            if (lancamentoFPFacade.validaLancamento(((LancamentoFP) selecionado))) {
//                FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Já existe um lançamento com os mesmos dados abaixo!");
//                FacesContext.getCurrentInstance().addMessage(null, message);
//                return true;
//            }
        } else {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Existem campos sem preenchimento.");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return true;
        }
        return false;
    }

    @Override
    public void novo() {
        super.novo();
        ((LancamentoFP) selecionado).setDataCadastro(new Date());
    }

    public Converter getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, atoLegalFacade);
        }
        return converterAtoLegal;
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return atoLegalFacade.listaFiltrandoParteEPropositoAtoLegal(parte.trim(), PropositoAtoLegal.ATO_DE_PESSOAL, "numero", "nome");
    }

    private String filtro = "";
    private int inicio = 0;
    private int maximoRegistrosTabela = 10;

    @Override
    public List listaFiltrandoX() {
//        List<AtributoMetadata> atributosTabelaveis = getMetadata().getAtributosTabelaveis();
//        Field atributos[] = new Field[atributosTabelaveis.size()];
//        for (int i = 0; i < atributos.length; i++) {
//            atributos[i] = atributosTabelaveis.get(i).getAtributo();
//        }
        lista = lancamentoFPFacade.listaFiltrandoX(filtro, inicio, maximoRegistrosTabela, contratoFPFiltro, mesAnoFiltro, eventoFPFiltro);
        return lista;
    }

    @Override
    public void proximos() {
        inicio += maximoRegistrosTabela;
        listaFiltrandoX();
    }

    @Override
    public void anteriores() {
        inicio -= maximoRegistrosTabela;
        if (inicio < 0) {
            inicio = 0;
        }
        listaFiltrandoX();
    }

    @Override
    public boolean isTemMaisResultados() {
        if (lista == null) {
            lista = listaFiltrandoX();
        }
        return lista.size() >= maximoRegistrosTabela;
    }

    @Override
    public boolean isTemAnterior() {
        return inicio > 0;
    }

    @Override
    public List acaoBotaoFiltrar() {
        inicio = 0;
        return listaFiltrandoX();
    }

    @Override
    public int getMaxRegistrosNaLista() {
        return AbstractFacade.MAX_RESULTADOS_NA_CONSULTA - 1;
    }

    @Override
    public int getMaximoRegistrosTabela() {
        return maximoRegistrosTabela;
    }

    @Override
    public void setMaximoRegistrosTabela(int maximoRegistrosTabela) {
        this.maximoRegistrosTabela = maximoRegistrosTabela;
    }

    @Override
    public String getFiltro() {
        return filtro;
    }

    @Override
    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public List<VinculoFP> completaContratoFiltro(String s) {
        return contratoFPFacade.buscaContratoFiltrandoAtributosMatriculaFP(s.trim());
    }

    public Converter getConverterContratoFP() {
        if (converterContratoFP == null) {
            converterContratoFP = new ConverterAutoComplete(ContratoFP.class, contratoFPFacade);
        }
        return converterContratoFP;
    }

    public ContratoFP getContratoFPFiltro() {
        return contratoFPFiltro;
    }

    public void setContratoFPFiltro(ContratoFP contratoFPFiltro) {
        this.contratoFPFiltro = contratoFPFiltro;
    }

    public EventoFP getEventoFPFiltro() {
        return eventoFPFiltro;
    }

    public void setEventoFPFiltro(EventoFP eventoFPFiltro) {
        this.eventoFPFiltro = eventoFPFiltro;
    }

    public List<EventoFP> completaEventoFP(String parte) {
        return eventoFPFacade.listaFiltrandoAutoComplete(parte.trim(), "codigo", "descricao");
    }

    public void limpaCampos() {
        filtro = "";
        eventoFPFiltro = null;
        contratoFPFiltro = null;
        mesAnoFiltro = null;
    }

    public Date getMesAnoFiltro() {
        return mesAnoFiltro;
    }

    public void setMesAnoFiltro(Date mesAnoFiltro) {
        this.mesAnoFiltro = mesAnoFiltro;
    }

    public void alteraEventoFP(SelectEvent event) {
        EventoFP eventoFP = (EventoFP) event.getObject();
        LancamentoFP lancamentoFP = (LancamentoFP) selecionado;

        if (eventoFP != null) {
            lancamentoFP.setTipoLancamentoFP(eventoFP.getTipoLancamentoFP());
            lancamentoFP.setQuantificacao(eventoFP.getQuantificacao());
        } else {
            lancamentoFP.setTipoLancamentoFP(null);
            lancamentoFP.setQuantificacao(null);
        }
        lancamentoFPFacade.definirDatasNovoLancamento(lancamentoFP);
    }

    public void atribuiMesAnoInicioCalculo() {
        LancamentoFP lancamentoFP = (LancamentoFP) selecionado;
        lancamentoFP.setMesAnoInicioCalculo(lancamentoFP.getMesAnoInicial());
    }

    public String statusServidor() {
        if (original != null) {
            SituacaoContratoFP situacao = situacaoFuncionalFacade.recuperaSituacaoFuncionalVigente(original.getVinculoFP());
            if (situacao == null) {
                return "Não Disponível";
            }
            return situacao.getSituacaoFuncional().getDescricao();
        }
        return "";
    }

    @Override
    public void cancelar() {

    }
}
