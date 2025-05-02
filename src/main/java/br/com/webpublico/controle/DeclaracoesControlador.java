package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.TipoCAGED;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 30/11/13
 * Time: 15:16
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoDeclaracao", pattern = "/declaracoes/novo/", viewId = "/faces/rh/administracaodepagamento/declaracoes/edita.xhtml"),
    @URLMapping(id = "editarDeclaracao", pattern = "/declaracoes/editar/#{declaracoesControlador.id}/", viewId = "/faces/rh/administracaodepagamento/declaracoes/edita.xhtml"),
    @URLMapping(id = "listarDeclaracao", pattern = "/declaracoes/listar/", viewId = "/faces/rh/administracaodepagamento/declaracoes/lista.xhtml"),
    @URLMapping(id = "verDeclaracao", pattern = "/declaracoes/ver/#{declaracoesControlador.id}/", viewId = "/faces/rh/administracaodepagamento/declaracoes/visualizar.xhtml"),
})
public class DeclaracoesControlador extends PrettyControlador<ContratoFP> implements Serializable, CRUD {

    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private CategoriaSEFIPFacade categoriaSEFIPFacade;
    @EJB
    private TipoAdmissaoFGTSFacade tipoAdmissaoFGTSFacade;
    @EJB
    private TipoAdmissaoSEFIPFacade tipoAdmissaoSEFIPFacade;
    @EJB
    private OcorrenciaSEFIPFacade ocorrenciaSEFIPFacade;
    @EJB
    private MovimentoCAGEDFacade movimentoCAGEDFacade;
    @EJB
    private TipoAdmissaoRAISFacade tipoAdmissaoRAISFacade;
    @EJB
    private RetencaoIRRFFacade retencaoIRRFFacade;
    @EJB
    private VinculoEmpregaticioFacade vinculoEmpregaticioFacade;
    @EJB
    private NaturezaRendimentoFacade naturezaRendimentoFacade;
    private ConverterGenerico converterCategoriaSEFIP;
    private ConverterGenerico converterTipoAdmissaoFGTS;
    private ConverterGenerico converterTipoAdmissaoSEFIP;
    private ConverterGenerico converterMovimentoCAGED;
    private ConverterGenerico converterTipoAdmissaoRAIS;
    private ConverterGenerico converterRetencaoIRRF;
    private ConverterGenerico converterVinculoEmpregaticio;
    private ConverterGenerico converterOcorrenciaSEFIP;
    private ConverterGenerico converterNaturezaRendimento;
    private ContratoFP contratoFPSelecionado;

    public DeclaracoesControlador() {
        super(ContratoFP.class);
    }

    @URLAction(mappingId = "novoDeclaracao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        operacao = Operacoes.EDITAR;
    }

    @URLAction(mappingId = "verDeclaracao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarDeclaracao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        setContratoFPSelecionado(selecionado);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/declaracoes/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return contratoFPFacade;
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            super.salvar();
        }
    }

    public boolean validaCampos() {
        boolean retorno = true;

        if (contratoFPSelecionado == null) {
            FacesUtil.addCampoObrigatorio("Informar o Servidor.");
            return false;
        }
        if (!selecionado.temCategoriaSEFIP()) {
            FacesUtil.addCampoObrigatorio("Informar a categoria SEFIP.");
            retorno = false;
        }
        if (!selecionado.temTipoAdmissaoFGTS()) {
            FacesUtil.addCampoObrigatorio("Informar o tipo de Admissão FGTS.");
            retorno = false;
        }
        if (!selecionado.temTipoAdmissaoSEFIP()) {
            FacesUtil.addCampoObrigatorio("Informar o tipo de Admissão SEFIP.");
            retorno = false;
        }
        if (!selecionado.temTipoAdmissaoRAIS()) {
            FacesUtil.addCampoObrigatorio("Informar o tipo de Admissão RAIS.");
            retorno = false;
        }
        if (!selecionado.temMovimentoCAGED()) {
            FacesUtil.addCampoObrigatorio("Informar o movimento CAGED.");
            retorno = false;
        }
        if (!selecionado.temTipoAdmissaoRAIS()) {
            FacesUtil.addCampoObrigatorio("Informar o tipo de Admissão RAIS.");
            retorno = false;
        }
        if (!selecionado.temTipoOcorrenciaSEFIP()) {
            FacesUtil.addCampoObrigatorio("Informar o tipo de Ocorrência SEFIP.");
            retorno = false;
        }
        if (!selecionado.temRetencaoIRRF()) {
            FacesUtil.addCampoObrigatorio("Informar a retenção SEFIP.");
            retorno = false;
        }
        if (!selecionado.temVinculoEmpregaticio()) {
            FacesUtil.addCampoObrigatorio("Informar o Vínculo Empregatício.");
            retorno = false;
        }
        if (!selecionado.temNaturezaRendimentoDirf()) {
            FacesUtil.addCampoObrigatorio("Informe Natureza de Rendimento DIRF.");
            retorno = false;
        }
        return retorno;
    }

    public List<ContratoFP> completaContratoServidor(String parte) {
        return contratoFPFacade.recuperaContrato(parte.trim());
    }

    public ConverterAutoComplete getConverterContratoServidor() {
        return new ConverterAutoComplete(ContratoFP.class, contratoFPFacade);
    }

    public List<SelectItem> getCategoriaSEFIP() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (CategoriaSEFIP object : categoriaSEFIPFacade.lista()) {
            String valor = object.toString();
            if (valor.length() > 70) {
                valor = valor.substring(0, 69);
            }
            toReturn.add(new SelectItem(object, valor));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterCategoriaSEFIP() {
        if (converterCategoriaSEFIP == null) {
            converterCategoriaSEFIP = new ConverterGenerico(CategoriaSEFIP.class, categoriaSEFIPFacade);
        }
        return converterCategoriaSEFIP;
    }

    public List<SelectItem> getTipoAdmissaoFGTS() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoAdmissaoFGTS object : tipoAdmissaoFGTSFacade.lista()) {
            String valor = object.toString();
            if (valor.length() > 70) {
                valor = valor.substring(0, 69);
            }
            toReturn.add(new SelectItem(object, valor));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterTipoAdmissaoFGTS() {
        if (converterTipoAdmissaoFGTS == null) {
            converterTipoAdmissaoFGTS = new ConverterGenerico(TipoAdmissaoFGTS.class, tipoAdmissaoFGTSFacade);
        }
        return converterTipoAdmissaoFGTS;
    }

    public List<SelectItem> getTipoAdmissaoSEFIPs() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoAdmissaoSEFIP object : tipoAdmissaoSEFIPFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getOcorrenciasSEFIP() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (OcorrenciaSEFIP obj : ocorrenciaSEFIPFacade.lista()) {
            toReturn.add(new SelectItem(obj, obj.toString()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterTipoAdmissaoSEFIP() {
        if (converterTipoAdmissaoSEFIP == null) {
            converterTipoAdmissaoSEFIP = new ConverterGenerico(TipoAdmissaoSEFIP.class, tipoAdmissaoSEFIPFacade);
        }
        return converterTipoAdmissaoSEFIP;
    }

    public List<SelectItem> getMovimentosCAGEDs() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (MovimentoCAGED object : movimentoCAGEDFacade.listaMovimentoPorTipo(TipoCAGED.ADMISSAO)) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterMovimentoCAGED() {
        if (converterMovimentoCAGED == null) {
            converterMovimentoCAGED = new ConverterGenerico(MovimentoCAGED.class, movimentoCAGEDFacade);
        }
        return converterMovimentoCAGED;
    }

    public List<SelectItem> getTipoAdmissaoRAIS() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoAdmissaoRAIS object : tipoAdmissaoRAISFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterTipoAdmissaoRAIS() {
        if (converterTipoAdmissaoRAIS == null) {
            converterTipoAdmissaoRAIS = new ConverterGenerico(TipoAdmissaoRAIS.class, tipoAdmissaoRAISFacade);
        }
        return converterTipoAdmissaoRAIS;
    }

    public List<SelectItem> getRetencaoIRRF() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (RetencaoIRRF object : retencaoIRRFFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterRetencaoIRRF() {
        if (converterRetencaoIRRF == null) {
            converterRetencaoIRRF = new ConverterGenerico(RetencaoIRRF.class, retencaoIRRFFacade);
        }
        return converterRetencaoIRRF;
    }

    public List<SelectItem> getVinculosEmpregaticios() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (VinculoEmpregaticio object : vinculoEmpregaticioFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterVinculoEmpregaticio() {
        if (converterVinculoEmpregaticio == null) {
            converterVinculoEmpregaticio = new ConverterGenerico(VinculoEmpregaticio.class, vinculoEmpregaticioFacade);
        }
        return converterVinculoEmpregaticio;
    }

    public ConverterGenerico getConverterOcorrenciaSEFIP() {
        if (converterOcorrenciaSEFIP == null) {
            converterOcorrenciaSEFIP = new ConverterGenerico(OcorrenciaSEFIP.class, ocorrenciaSEFIPFacade);
        }
        return converterOcorrenciaSEFIP;
    }

    public Converter getConverterNaturezaRendimento() {
        if (converterNaturezaRendimento == null) {
            converterNaturezaRendimento = new ConverterGenerico(NaturezaRendimento.class, naturezaRendimentoFacade);
        }
        return converterNaturezaRendimento;
    }

    public List<SelectItem> listaNaturezaRendimento() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (NaturezaRendimento object : naturezaRendimentoFacade.lista()) {
            String valor = object.toString();
            if (valor.length() > 70) {
                valor = valor.substring(0, 69);
            }
            toReturn.add(new SelectItem(object, valor));
        }
        return toReturn;
    }

    public void processarServidorSelecionado() {
        selecionado = contratoFPSelecionado;
    }

    public ContratoFP getContratoFPSelecionado() {
        return contratoFPSelecionado;
    }

    public void setContratoFPSelecionado(ContratoFP contratoFPSelecionado) {
        this.contratoFPSelecionado = contratoFPSelecionado;
    }
}
