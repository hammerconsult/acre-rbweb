package br.com.webpublico.controle;

import br.com.webpublico.entidades.Capacitacao;
import br.com.webpublico.entidades.InscricaoCapacitacao;
import br.com.webpublico.entidades.ModuloCapacitacao;
import br.com.webpublico.entidades.PresencaModulo;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.InscricaoEventoCapacitacaoFacade;
import br.com.webpublico.negocios.ModuloCapacitacaoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by carlos on 11/06/15.
 */
@ManagedBean(name = "presencaModuloControlador")
@ViewScoped
@URLMappings(
    mappings = {
        @URLMapping(id = "lancarPresenca",
            pattern = "/presenca/lancar/",
            viewId = "/faces/rh/administracaodepagamento/capacitacaoservidor/presenca/lancar.xhtml")
    }
)
public class PresencaModuloControlador implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(PresencaModuloControlador.class);
    @EJB
    private InscricaoEventoCapacitacaoFacade inscricaoEventoCapacitacaoFacade;
    @EJB
    private ModuloCapacitacaoFacade moduloCapacitacaoFacade;
    private InscricaoCapacitacao inscricaoCapacitacao;
    private Capacitacao capacitacao;
    private ModuloCapacitacao moduloCapacitacao;
    private List<PresencaModulo> presencaModulos;
    private Date dataPresenca;
    private List<Date> dias;
    private ConverterAutoComplete converterModulo;

    @URLAction(mappingId = "lancarPresenca", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        dataPresenca = null;
    }

    public void salvar() {
        if (valida()) {
            inscricaoEventoCapacitacaoFacade.salvar(presencaModulos);
            FacesUtil.redirecionamentoInterno("");
            FacesUtil.addOperacaoRealizada("Presenças salvas com sucesso.");
        }

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
        System.out.println(inscricaoEventoCapacitacaoFacade.getEventoCapacitacaoFacade().listaModuloCapacitacao(parte, capacitacao).size());
        return inscricaoEventoCapacitacaoFacade.getEventoCapacitacaoFacade().listaModuloCapacitacao(parte, capacitacao);
    }

    public void recuperarDatas() {
        if (moduloCapacitacao != null) {
            this.dias = DataUtil.pegaDiasPorDiaDaSemana(moduloCapacitacao.getDataInicioModulo(), moduloCapacitacao.getDataFinalModulo(), Calendar.MONDAY,
                Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY);
            moduloCapacitacao = moduloCapacitacaoFacade.recuperar(moduloCapacitacao.getId());

        }
    }

    public List<SelectItem> diasSelectMenu() {
        List<SelectItem> datas = new ArrayList<>();
        datas.add(new SelectItem(null, ""));
        if (dias != null) {
            for (Date dia : dias) {
                datas.add(new SelectItem(DataUtil.getDataFormatada(dia), DataUtil.getDataFormatada(dia)));
            }
        }
        return datas;

    }

    public void carregaListaPresenca() {
        presencaModulos = new ArrayList<PresencaModulo>();
        capacitacao = moduloCapacitacaoFacade.recuperarCapacitcao(capacitacao.getId());

        if (dataPresenca == null) {
            FacesUtil.addOperacaoNaoPermitida("Selecione uma data");
        } else {
            List<PresencaModulo> presencaModulosRecuperados = inscricaoEventoCapacitacaoFacade.recuperarPresencaPorDia(dataPresenca, moduloCapacitacao);
            if (presencaModulosRecuperados.size() > 0) {
                this.presencaModulos.addAll(presencaModulosRecuperados);
            } else {
                for (InscricaoCapacitacao inscricaoCapacitacao : capacitacao.getInscricoes()) {
                    PresencaModulo presencaModulo = new PresencaModulo();
                    presencaModulo.setModuloCapacitacao(moduloCapacitacao);
                    presencaModulo.setInscricaoCapacitacao(inscricaoCapacitacao);
                    presencaModulo.setDia(dataPresenca);
                    this.presencaModulos.add(presencaModulo);
                }
            }
        }


    }

    public Converter getconverterData() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {
                try {
                    if (value != null && !value.equals("")) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        Date dataDoCarlos = sdf.parse(value);

                        return dataDoCarlos;

                    }
                } catch (Exception ex) {
                    logger.error("Erro: ", ex);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Erro do conversão de data, favor insira um valor aceitável" + ex.getMessage()));
                    return null;
                }
                return null;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
//                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                if (value != null) {
                    return value.toString();
                } else {
                    return null;
                }
            }
        };
    }

    public boolean valida() {
        boolean valida = true;

        if (capacitacao == null) {
            FacesUtil.addCampoObrigatorio("O Evento é um campo obrigátorio.");
            valida = false;
        }

        if (moduloCapacitacao == null) {
            FacesUtil.addCampoObrigatorio("O Módulo é um campo obrigátorio.");
            valida = false;
        }

        return valida;
    }


//    ===================================================== Getters and Setters ========================================


    public InscricaoCapacitacao getInscricaoCapacitacao() {
        return inscricaoCapacitacao;
    }

    public void setInscricaoCapacitacao(InscricaoCapacitacao inscricaoCapacitacao) {
        this.inscricaoCapacitacao = inscricaoCapacitacao;
    }

    public Capacitacao getCapacitacao() {
        return capacitacao;
    }

    public void setCapacitacao(Capacitacao capacitacao) {
        this.capacitacao = capacitacao;
    }

    public ModuloCapacitacao getModuloCapacitacao() {
        return moduloCapacitacao;
    }

    public void setModuloCapacitacao(ModuloCapacitacao moduloCapacitacao) {
        this.moduloCapacitacao = moduloCapacitacao;
    }

    public Date getDataPresenca() {
        return dataPresenca;
    }

    public void setDataPresenca(Date dataPresenca) {
        this.dataPresenca = dataPresenca;
    }

    public List<Date> getDias() {
        return dias;
    }

    public void setDias(List<Date> dias) {
        this.dias = dias;
    }

    public List<PresencaModulo> getPresencaModulos() {
        return presencaModulos;
    }

    public void setPresencaModulos(List<PresencaModulo> presencaModulos) {
        this.presencaModulos = presencaModulos;
    }

    public ConverterAutoComplete getConverterModulo() {
        if (converterModulo == null) {
            converterModulo = new ConverterAutoComplete(ModuloCapacitacao.class, moduloCapacitacaoFacade);
        }
        return converterModulo;
    }
}
