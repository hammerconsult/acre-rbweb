/*
 * Codigo gerado automaticamente em Fri Mar 04 09:44:14 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.DescansoSemanal;
import br.com.webpublico.enums.SituacaoVinculo;
import br.com.webpublico.enums.TipoCAGED;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConfiguracaoModalidadeContratoFPFacade;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.interfaces.CRUD;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ManagedBean(name = "configuracaoModalidadeContratoFPControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "configuraModalidade", pattern = "/configuracao-modalidade-contrato/", viewId = "/faces/rh/administracaodepagamento/configuracaomodalidadecontratofp/edita.xhtml")
})
public class ConfiguracaoModalidadeContratoFPControlador extends PrettyControlador<ConfiguracaoModalidadeContratoFP> implements Serializable, CRUD {

    @EJB
    private ConfiguracaoModalidadeContratoFPFacade configuracaoModalidadeContratoFPFacade;
    private List<ModalidadeContratoFP> modalidades;
    private Map<ModalidadeContratoFP, ConfiguracaoModalidadeContratoFP> mapa;
    private ConverterGenerico converterJornadaDeTrabalho;
    private ConverterGenerico converterTipoRegime;
    private ConverterGenerico converterTipoPrevidencia;
    private ConverterGenerico converterCategoriaSEFIP;
    private ConverterGenerico converterTipoAdmissaoFGTS;
    private ConverterGenerico converterTipoAdmissaoSEFIP;
    private ConverterGenerico converterTipoAdmissaoRAIS;
    private ConverterGenerico converterMovimentoCAGED;
    private ConverterGenerico converterOcorrenciaSEFIP;
    private ConverterGenerico converterRetencaoIRRF;
    private ConverterGenerico converterVinculoEmpregaticio;
    private ConverterGenerico converterNaturezaRendimento;
    private ConverterGenerico converterHorarioDeTrabalho;


    public ConfiguracaoModalidadeContratoFPControlador() {
        super(ConfiguracaoModalidadeContratoFP.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return configuracaoModalidadeContratoFPFacade;
    }

    public Map<ModalidadeContratoFP, ConfiguracaoModalidadeContratoFP> getMapa() {
        return mapa;
    }

    public List<ModalidadeContratoFP> getModalidades() {
        return modalidades;
    }

    public void setModalidades(List<ModalidadeContratoFP> modalidades) {
        this.modalidades = modalidades;
    }

    @URLAction(mappingId = "configuraModalidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        modalidades = configuracaoModalidadeContratoFPFacade.getModalidadeContratoFPFacade().modalidadesAtivas();
        mapa = Maps.newHashMap();
        for (ModalidadeContratoFP m : modalidades) {
            ConfiguracaoModalidadeContratoFP config = configuracaoModalidadeContratoFPFacade.recuperarPelaModalidade(m);
            if (config == null) {
                config = new ConfiguracaoModalidadeContratoFP(m);
            }
            config.setConfiguracaoPrevidencia(new ConfiguracaoPrevidenciaVinculoFP());
            mapa.put(m, config);
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/configuracao-modalidade-contrato/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void salvar() {
        configuracaoModalidadeContratoFPFacade.salvar(mapa);
        FacesUtil.addInfo("Salvo com sucesso!", "");
    }

    public ConverterGenerico getConverterJornadaDeTrabalho() {
        if (converterJornadaDeTrabalho == null) {
            converterJornadaDeTrabalho = new ConverterGenerico(JornadaDeTrabalho.class, configuracaoModalidadeContratoFPFacade.getJornadaDeTrabalhoFacade());
        }
        return converterJornadaDeTrabalho;
    }

    public ConverterGenerico getConverterTipoRegime() {
        if (converterTipoRegime == null) {
            converterTipoRegime = new ConverterGenerico(TipoRegime.class, configuracaoModalidadeContratoFPFacade.getTipoRegimeFacade());
        }
        return converterTipoRegime;
    }

    public ConverterGenerico getConverterTipoPrevidencia() {
        if (converterTipoPrevidencia == null) {
            converterTipoPrevidencia = new ConverterGenerico(TipoPrevidenciaFP.class, configuracaoModalidadeContratoFPFacade.getTipoPrevidenciaFPFacade());
        }
        return converterTipoPrevidencia;
    }

    public ConverterGenerico getConverterCategoriaSEFIP() {
        if (converterCategoriaSEFIP == null) {
            converterCategoriaSEFIP = new ConverterGenerico(CategoriaSEFIP.class, configuracaoModalidadeContratoFPFacade.getCategoriaSEFIPFacade());
        }
        return converterCategoriaSEFIP;
    }

    public ConverterGenerico getConverterTipoAdmissaoFGTS() {
        if (converterTipoAdmissaoFGTS == null) {
            converterTipoAdmissaoFGTS = new ConverterGenerico(TipoAdmissaoFGTS.class, configuracaoModalidadeContratoFPFacade.getTipoAdmissaoFGTSFacade());
        }
        return converterTipoAdmissaoFGTS;
    }

    public ConverterGenerico getConverterTipoAdmissaoSEFIP() {
        if (converterTipoAdmissaoSEFIP == null) {
            converterTipoAdmissaoSEFIP = new ConverterGenerico(TipoAdmissaoSEFIP.class, configuracaoModalidadeContratoFPFacade.getTipoAdmissaoSEFIPFacade());
        }
        return converterTipoAdmissaoSEFIP;
    }

    public ConverterGenerico getConverterTipoAdmissaoRAIS() {
        if (converterTipoAdmissaoRAIS == null) {
            converterTipoAdmissaoRAIS = new ConverterGenerico(TipoAdmissaoRAIS.class, configuracaoModalidadeContratoFPFacade.getTipoAdmissaoRAISFacade());
        }
        return converterTipoAdmissaoRAIS;
    }

    public ConverterGenerico getConverterMovimentoCAGED() {
        if (converterMovimentoCAGED == null) {
            converterMovimentoCAGED = new ConverterGenerico(MovimentoCAGED.class, configuracaoModalidadeContratoFPFacade.getMovimentoCAGEDFacade());
        }
        return converterMovimentoCAGED;
    }

    public ConverterGenerico getConverterOcorrenciaSEFIP() {
        if (converterOcorrenciaSEFIP == null) {
            converterOcorrenciaSEFIP = new ConverterGenerico(OcorrenciaSEFIP.class, configuracaoModalidadeContratoFPFacade.getOcorrenciaSEFIPFacade());
        }
        return converterOcorrenciaSEFIP;
    }

    public ConverterGenerico getConverterRetencaoIRRF() {
        if (converterRetencaoIRRF == null) {
            converterRetencaoIRRF = new ConverterGenerico(RetencaoIRRF.class, configuracaoModalidadeContratoFPFacade.getRetencaoIRRFFacade());
        }
        return converterRetencaoIRRF;
    }

    public ConverterGenerico getConverterVinculoEmpregaticio() {
        if (converterVinculoEmpregaticio == null) {
            converterVinculoEmpregaticio = new ConverterGenerico(VinculoEmpregaticio.class, configuracaoModalidadeContratoFPFacade.getVinculoEmpregaticioFacade());
        }
        return converterVinculoEmpregaticio;
    }

    public ConverterGenerico getConverterNaturezaRendimento() {
        if (converterNaturezaRendimento == null) {
            converterNaturezaRendimento = new ConverterGenerico(NaturezaRendimento.class, configuracaoModalidadeContratoFPFacade.getNaturezaRendimentoFacade());
        }
        return converterNaturezaRendimento;
    }

    public ConverterGenerico getConverterHorarioDeTrabalho() {
        if (converterHorarioDeTrabalho == null) {
            converterHorarioDeTrabalho = new ConverterGenerico(HorarioDeTrabalho.class, configuracaoModalidadeContratoFPFacade.getHorarioDeTrabalhoFacade());
        }
        return converterHorarioDeTrabalho;
    }

    public List<SelectItem> getJornadaDeTrabalho() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (JornadaDeTrabalho object : configuracaoModalidadeContratoFPFacade.getJornadaDeTrabalhoFacade().lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getDescansoSemanals() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(""));
        for (DescansoSemanal obj : DescansoSemanal.values()) {
            toReturn.add(new SelectItem(obj, obj.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoRegime() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoRegime object : configuracaoModalidadeContratoFPFacade.getTipoRegimeFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getSituacoes() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (SituacaoVinculo object : SituacaoVinculo.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoPrevidenciaFp() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoPrevidenciaFP object : configuracaoModalidadeContratoFPFacade.getTipoPrevidenciaFPFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getCategoriaSEFIP() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (CategoriaSEFIP object : configuracaoModalidadeContratoFPFacade.getCategoriaSEFIPFacade().lista()) {
            String valor = object.toString();
            if (valor.length() > 70) {
                valor = valor.substring(0, 69);
            }
            toReturn.add(new SelectItem(object, valor));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoAdmissaoFGTS() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoAdmissaoFGTS object : configuracaoModalidadeContratoFPFacade.getTipoAdmissaoFGTSFacade().lista()) {
            String valor = object.toString();
            if (valor.length() > 70) {
                valor = valor.substring(0, 69);
            }
            toReturn.add(new SelectItem(object, valor));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoAdmissaoSEFIPs() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoAdmissaoSEFIP object : configuracaoModalidadeContratoFPFacade.getTipoAdmissaoSEFIPFacade().lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoAdmissaoRAIS() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoAdmissaoRAIS object : configuracaoModalidadeContratoFPFacade.getTipoAdmissaoRAISFacade().lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getMovimentosCAGEDs() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (MovimentoCAGED object : configuracaoModalidadeContratoFPFacade.getMovimentoCAGEDFacade().listaMovimentoPorTipo(TipoCAGED.ADMISSAO)) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getOcorrenciasSEFIP() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (OcorrenciaSEFIP obj : configuracaoModalidadeContratoFPFacade.getOcorrenciaSEFIPFacade().lista()) {
            toReturn.add(new SelectItem(obj, obj.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getRetencaoIRRF() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (RetencaoIRRF object : configuracaoModalidadeContratoFPFacade.getRetencaoIRRFFacade().lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getVinculosEmpregaticios() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (VinculoEmpregaticio object : configuracaoModalidadeContratoFPFacade.getVinculoEmpregaticioFacade().lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getNaturezaRendimentos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (NaturezaRendimento object : configuracaoModalidadeContratoFPFacade.getNaturezaRendimentoFacade().lista()) {
            String valor = object.toString();
            if (valor.length() > 70) {
                valor = valor.substring(0, 69);
            }
            toReturn.add(new SelectItem(object, valor));
        }
        return toReturn;
    }

    public List<SelectItem> getHorarioDeTrabalho() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (HorarioDeTrabalho object : configuracaoModalidadeContratoFPFacade.getHorarioDeTrabalhoFacade().lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public boolean validaCamposPrevidencia(ConfiguracaoPrevidenciaVinculoFP previdenciaVinculoFP) {
        boolean retorno = true;
        if (previdenciaVinculoFP.getTipoPrevidenciaFP() == null) {
            FacesUtil.addWarn("Atenção", "O tipo de previdência é obrigatório.");
            retorno = false;
        }
        return retorno;
    }

    public void addPrevidencia(ConfiguracaoModalidadeContratoFP configuracaoModalidadeContratoFP) {
        if (validaCamposPrevidencia(configuracaoModalidadeContratoFP.getConfiguracaoPrevidencia())) {
            configuracaoModalidadeContratoFP.getConfiguracaoPrevidencia().setConfModalidadeContratoFP(configuracaoModalidadeContratoFP);
            configuracaoModalidadeContratoFP.addConfiguracaoPrevidencia(configuracaoModalidadeContratoFP.getConfiguracaoPrevidencia());
        }
    }

    public void removePrevidencia(ConfiguracaoModalidadeContratoFP modalidadeContratoFP, ConfiguracaoPrevidenciaVinculoFP previdenciaVinculoFP) {
        modalidadeContratoFP.getConfiguracaoPrevidenciaVinculoFPs().remove(previdenciaVinculoFP);
    }
}
