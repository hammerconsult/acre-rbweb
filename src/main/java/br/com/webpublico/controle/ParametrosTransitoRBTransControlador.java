/*
 * Codigo gerado automaticamente em Mon Oct 31 21:28:23 BRST 2011
 * Gerador de Controlador
 *
 */
package br.com.webpublico.controle;

import br.com.webpublico.controle.forms.FormParametrosTransitoRBTransControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.ModuloTipoDoctoOficial;
import br.com.webpublico.enums.TipoParametroRBTrans;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.hibernate.envers.RevisionType;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.persistence.Transient;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@ManagedBean(name = "parametrosTransitoRBTransControlador")
@ViewScoped
@URLMappings(
    mappings = {
        @URLMapping(id = "criarParametroFiscalizacao",
            pattern = "/rbtrans/parametrotransito/fiscalizacao/novo/",
            viewId = "/faces/tributario/rbtrans/parametros/fiscalizacao/edita.xhtml"),
        @URLMapping(id = "verParametroTermo",
            pattern = "/rbtrans/parametrotransito/termo/historico/",
            viewId = "/faces/tributario/rbtrans/parametros/termo/visualizar-historico.xhtml"),
        @URLMapping(id = "verParametrosOutorga",
            pattern = "/rbtrans/parametrotransito/outorga/historico/",
            viewId = "/faces/tributario/rbtrans/parametros/outorga/visualizar-historico.xhtml"),
        @URLMapping(id = "verParametroFiscalizacao",
            pattern = "/rbtrans/parametrotransito/fiscalizacao/historico/",
            viewId = "/faces/tributario/rbtrans/parametros/fiscalizacao/visualizar-historico.xhtml")
    }
)

public class ParametrosTransitoRBTransControlador extends PrettyControlador<ParametrosTransitoRBTrans> implements Serializable, CRUD {

    @EJB
    private ParametrosTransitoRBTransFacade parametrosTransitoRBTransFacade;
    @EJB
    private TributoFacade tributoFacade;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private TipoDoctoOficialFacade tipoDoctoOficialFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private TipoAutonomoFacade tipoAutonomoFacade;
    @EJB
    private IndiceEconomicoFacade indiceFacade;
    private ConverterAutoComplete converterDivida;
    private ConverterAutoComplete converterTipoDoctoOficial;
    private ConverterAutoComplete converterExercicio;
    private ConverterAutoComplete converterTributo;
    private ConverterAutoComplete converterTipoAutonomo;
    private ConverterGenerico converterIndice;
    private FormParametrosTransitoRBTransControlador form;


    public ParametrosTransitoRBTransControlador() {
        super(ParametrosTransitoRBTrans.class);
    }

    public FormParametrosTransitoRBTransControlador getForm() {
        return form;
    }

    public void setForm(FormParametrosTransitoRBTransControlador form) {
        this.form = form;
    }

    public void inicializacao() {
        form = (FormParametrosTransitoRBTransControlador) Web.pegaDaSessao(FormParametrosTransitoRBTransControlador.class);
        if (form == null) {
            form = new FormParametrosTransitoRBTransControlador();
        }
    }

    public void parametroTermo() {
        inicializacao();
        form.setTipoParametroRBTrans(TipoParametroRBTrans.TERMO);
        novoParametro();
    }

    @URLAction(mappingId = "verParametroTermo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void historicoParametroTermo() {
        parametroTermo();
    }

    public void parametroOutorga() {
        inicializacao();
        form.setTipoParametroRBTrans(TipoParametroRBTrans.OUTORGA);
        novoParametro();
    }

    @URLAction(mappingId = "verParametrosOutorga", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void historicoParametroOutorga() {
        parametroOutorga();
    }

    @URLAction(mappingId = "criarParametroFiscalizacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void parametroFiscalizacao() {
        inicializacao();
        form.setTipoParametroRBTrans(TipoParametroRBTrans.FISCALIZACAO);
        novoParametro();
    }

    @URLAction(mappingId = "verParametroFiscalizacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void historicoParametroFiscalizacao() {
        parametroFiscalizacao();
    }

    public void definirTipoParametrosTransporte(String tipo) {
        //this.listaHistoricoGeral = null;
        if (TipoParametroRBTrans.valueOf(tipo).getDescricao().trim().length() > 0) {
            //  this.tipoParametroRBTrans = TipoParametroRBTrans.valueOf(tipo);
            novoParametro();
            return;
        }
        //this.tipoParametroRBTrans = null;
    }

    public String retornaCaminhoPadraoPorTipoParametro() {
        StringBuilder caminho = new StringBuilder("");
        caminho.append("/rbtrans/parametrotransito/");
        switch (form.getTipoParametroRBTrans()) {
            case FISCALIZACAO:
                caminho.append("fiscalizacao/");
                break;
            case OUTORGA:
                caminho.append("outorga/");
                break;
            case VENCIMENTO:
                caminho.append("vencimentolicenciamento/");
                break;
            case TERMO:
                caminho.append("termo/");
                break;
        }
        caminho.append("novo/");

        return caminho.toString();
    }

    @Override
    public String getCaminhoPadrao() {
        return retornaCaminhoPadraoPorTipoParametro();
    }

    @Override
    public Object getUrlKeyValue() {
        return form.getParametrosTransitoSelecionado().getId();
    }

    @Override
    public void salvar() {
        try {
            ResultadoValidacao resultado = null;
            form.getParametrosTransitoSelecionado().setInicioVigencia(new Date());
            resultado = parametrosTransitoRBTransFacade.salvarParametro(form.getParametrosTransitoSelecionado());
            FacesUtil.printAllMessages(resultado.getMensagens());
            if (resultado.isValidado()) {
                form.setListaHistoricoGeral(null);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

    }


    public ConverterAutoComplete getConverterTipoDoctoOficial() {
        if (converterTipoDoctoOficial == null) {
            converterTipoDoctoOficial = new ConverterAutoComplete(TipoDoctoOficial.class, tipoDoctoOficialFacade);
        }
        return converterTipoDoctoOficial;
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

    public List<Divida> completaDivida(String parte) {
        return dividaFacade.listaFiltrando(parte.trim(), "descricao");
    }

    public List<TipoDoctoOficial> completaTipoDoctoOficialFiscalizacao(String parte) {
        return tipoDoctoOficialFacade.recuperaTiposDoctoOficialPorModulo(ModuloTipoDoctoOficial.FISCALIZACAORBTRANS, parte.trim());
    }

    public List<TipoDoctoOficial> completaTipoDoctoOficialRBTrans(String parte) {
        return tipoDoctoOficialFacade.recuperaTiposDoctoOficialPorModulo(ModuloTipoDoctoOficial.RBTRANS, parte.trim());
    }


    public List<Exercicio> completaExercicio(String parte) {
        return exercicioFacade.listaFiltrandoEspecial(parte.trim());
    }

    @Override
    public AbstractFacade getFacede() {
        return parametrosTransitoRBTransFacade;
    }

    public ConverterAutoComplete getConverterTributo() {
        if (converterTributo == null) {
            converterTributo = new ConverterAutoComplete(Tributo.class, tributoFacade);
        }
        return converterTributo;
    }

    public List<Tributo> completarTributos(String parte) {
        return tributoFacade.listaFiltrando(parte.trim(), "descricao");
    }

    public void novoParametro() {
        if (Objects.requireNonNull(form.getTipoParametroRBTrans()) == TipoParametroRBTrans.FISCALIZACAO) {
            executarParametrosFiscalizacaoRBTrans();
        }
    }

    private void executarParametrosFiscalizacaoRBTrans() {
        form.setParametrosTransitoSelecionado(parametrosTransitoRBTransFacade.getParametrosFiscalizacaoRBTransVigente());
        criarNovoParametroFiscalizacaoRBTrans();
    }

    private void criarNovoParametroFiscalizacaoRBTrans() {
        if (form.getParametrosTransitoSelecionado() != null) {
            return;
        } else {
            form.setParametrosTransitoSelecionado(new ParametrosFiscalizacaoRBTrans());
        }
        getEsteSelecionadoFiscalizacao().setAlteradoPor(((SistemaControlador) Util.getControladorPeloNome("sistemaControlador")).getUsuarioCorrente());
    }

    public ParametrosTransitoTransporte esteSelecionadoFrete() {
        return (ParametrosTransitoTransporte) form.getParametrosTransitoSelecionado();
    }

    public ParametrosTransitoTransporte esteSelecionadoMotoTaxi() {
        return (ParametrosTransitoTransporte) form.getParametrosTransitoSelecionado();
    }

    public ParametrosTransitoTransporte esteSelecionadoTaxi() {
        return (ParametrosTransitoTransporte) form.getParametrosTransitoSelecionado();
    }

    public ParametrosFiscalizacaoRBTrans getEsteSelecionadoFiscalizacao() {
        return (ParametrosFiscalizacaoRBTrans) form.getParametrosTransitoSelecionado();
    }


    public List<Object[]> getListaHistoricoGeral() throws IllegalArgumentException, IllegalAccessException {
        if (form.getListaHistoricoGeral() == null || form.getListaHistoricoGeral().isEmpty()) {
            switch (form.getTipoParametroRBTrans()) {
                case TERMO:
                    form.setListaHistoricoGeral(parametrosTransitoRBTransFacade.recuperarHistoricoAlteracoes(ParametrosTransitoTermos.class));
                    break;
                case OUTORGA:
                    form.setListaHistoricoGeral(parametrosTransitoRBTransFacade.recuperarHistoricoAlteracoes(ParametrosOutorgaRBTrans.class));
                    break;
                case FISCALIZACAO:
                    form.setListaHistoricoGeral(parametrosTransitoRBTransFacade.recuperarHistoricoAlteracoes(ParametrosFiscalizacaoRBTrans.class));
                    break;
            }
        }
        return form.getListaHistoricoGeral();
    }

    private RevisionType tipoRevisao(Object[] tipo) {
        try {
            return (RevisionType) tipo[2];
        } catch (Exception e) {
            return null;
        }
    }

    public String recuperarStringInsercao(Object[] obj) {
        String retorno = "";
        if (!RevisionType.ADD.equals(tipoRevisao(obj))) {
            return retorno;
        }

        retorno = "Inserção do(a) " + obj[0].getClass().getAnnotation(Etiqueta.class).value() + ".<br />";

        return retorno;
    }

    public String recuperarStringRemocao(Object[] obj) {
        String retorno = "";
        if (!tipoRevisao(obj).equals(RevisionType.ADD)) {
            return retorno;
        }

        retorno = "Removido o(a) " + obj[0].getClass().getAnnotation(Etiqueta.class).value() + ".<br />";

        return retorno;
    }

    public String recuperarStringAlteracao(Object[] obj) {
        String retorno = "";
        if (!tipoRevisao(obj).equals(RevisionType.MOD)) {
            return retorno;
        }

        Object objAtual = obj[0];
        Object objAnterior = null;
        try {
            objAnterior = form.getListaHistoricoGeral().get(form.getListaHistoricoGeral().indexOf(obj) + 1)[0];
        } catch (Exception e) {
            objAnterior = "";
        }

        for (Field field : Persistencia.getAtributos(objAtual.getClass())) {
            field.setAccessible(true);
            if (!field.isAnnotationPresent(Transient.class)) {
                try {
                    if (!Persistencia.getAttributeValue(objAtual, field.getName()).toString().equals(Persistencia.getAttributeValue(objAnterior, field.getName()).toString())) {
                        retorno += "<tr>";
                        retorno += "<td></td>"; // Icone
                        String nomeCampo = field.isAnnotationPresent(Etiqueta.class) ? field.getAnnotation(Etiqueta.class).value() : field.getName();
                        String de = recuperarDescricaoCampo(objAnterior, field);
                        String para = recuperarDescricaoCampo(objAtual, field);
                        retorno += "<td>" + nomeCampo + "</td>";
                        retorno += "<td>" + de + "</td>";
                        retorno += "<td>" + para + "</td>";
                        retorno += "</tr>";
                    }
                } catch (Exception e) {
                    retorno += "";
                }
            }
        }

        return retorno;
    }

    public String recuperarDescricaoCampo(Object obj, Field campo) {
        AtributoMetadata amd = new AtributoMetadata(campo);
        return amd.getString(obj);
    }

    public ConverterAutoComplete getConverterTipoAutonomo() {
        if (converterTipoAutonomo == null) {
            converterTipoAutonomo = new ConverterAutoComplete(TipoAutonomo.class, tipoAutonomoFacade);
        }
        return converterTipoAutonomo;
    }

    public void setConverterTipoAutonomo(ConverterAutoComplete converterTipoAutonomo) {
        this.converterTipoAutonomo = converterTipoAutonomo;
    }


    public List<TipoAutonomo> autoCompletarTipoAutonomo(String parte) {
        return tipoAutonomoFacade.listaFiltrando(parte.trim(), "descricao");
    }

    public List<SelectItem> getIndices() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
//        toReturn.add(new SelectItem(null, ""));
        for (IndiceEconomico object : indiceFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

}
