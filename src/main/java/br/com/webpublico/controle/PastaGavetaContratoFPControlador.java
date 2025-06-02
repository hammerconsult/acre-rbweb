/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.PastaGavetaContratoFPFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alex
 * @since 23/03/2017 09:40
 */
@ManagedBean(name = "pastaGavetaContratoFPControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoFicharioContrato", pattern = "/servidor-fichario/novo/", viewId = "/faces/rh/administracaodepagamento/fichariocontrato/edita.xhtml"),
        @URLMapping(id = "editarFicharioContrato", pattern = "/servidor-fichario/editar/#{pastaGavetaContratoFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/fichariocontrato/edita.xhtml"),
        @URLMapping(id = "listarFicharioContrato", pattern = "/servidor-fichario/listar/", viewId = "/faces/rh/administracaodepagamento/fichariocontrato/lista.xhtml"),
        @URLMapping(id = "verFicharioContrato", pattern = "/servidor-fichario/ver/#{pastaGavetaContratoFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/fichariocontrato/visualizar.xhtml")
})
public class PastaGavetaContratoFPControlador extends PrettyControlador<PastaGavetaContratoFP> implements Serializable, CRUD {

    @EJB
    private PastaGavetaContratoFPFacade pastaGavetaContratoFPFacade;
    private GavetaFichario gavetaFicharioSelecionado;
    private ConverterAutoComplete converterGavetaFichario;
    private PastaGaveta pastaGavetaSelecionado;
    private ConverterAutoComplete converterPastaGaveta;
    private ContratoFP contratoFPSelecionado;
    private Fichario ficharioSelecionado;

    public PastaGavetaContratoFPControlador() {
        super(PastaGavetaContratoFP.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return pastaGavetaContratoFPFacade;
    }

    @URLAction(mappingId = "novoFicharioContrato", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        gavetaFicharioSelecionado = new GavetaFichario();
    }

    @URLAction(mappingId = "editarFicharioContrato", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        inicializarVariaveis();
    }

    @URLAction(mappingId = "verFicharioContrato", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/servidor-fichario/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<ContratoFP> buscarServidores(String parte) {
        return pastaGavetaContratoFPFacade.getContratoFPFacade().recuperaContratoMatricula(parte.trim());
    }

    public void iniciarQuandoNaoForTelaFichaContratoFP(PastaGavetaContratoFP pasta) {
        selecionado = pasta;
        inicializarVariaveis();
    }

    public void inicializarVariaveis() {
        if (selecionado != null) {
            contratoFPSelecionado = selecionado.getContratoFP();
            pastaGavetaSelecionado = selecionado.getPastaGaveta();
            if (selecionado.getPastaGaveta() != null) {
                gavetaFicharioSelecionado = selecionado.getPastaGaveta().getGavetaFichario();
                ficharioSelecionado = selecionado.getPastaGaveta().getGavetaFichario().getFichario();
            } else {
                gavetaFicharioSelecionado = null;
                ficharioSelecionado = null;
            }
        }
    }

    public List<Fichario> buscarFicharios(String parte) {
        return pastaGavetaContratoFPFacade.getFicharioFacade().listaFiltrandoCodigoDescricao(parte.trim());
    }

    public ConverterAutoComplete getConverterGavetaFichario() {
        if (converterGavetaFichario == null) {
            converterGavetaFichario = new ConverterAutoComplete(GavetaFichario.class, pastaGavetaContratoFPFacade.getGavetaFicharioFacade());
        }
        return converterGavetaFichario;
    }

    public List<GavetaFichario> buscarGavetaFichario(String parte) {
        if (ficharioSelecionado != null) {
            return pastaGavetaContratoFPFacade.getGavetaFicharioFacade().completaGavetasPorFichario(parte.trim(), ficharioSelecionado);
        } else {
            return new ArrayList<GavetaFichario>();
        }
    }

    public ConverterAutoComplete getConverterPastaGaveta() {
        if (converterPastaGaveta == null) {
            converterPastaGaveta = new ConverterAutoComplete(PastaGaveta.class, pastaGavetaContratoFPFacade.getPastaGavetaFacade());
        }
        return converterPastaGaveta;
    }

    public List<PastaGaveta> buscarPastaGaveta(String parte) {
        if (gavetaFicharioSelecionado != null) {
            List<PastaGaveta> lista = new ArrayList<PastaGaveta>();
            if (pastaGavetaSelecionado != null) {
                lista.add(pastaGavetaSelecionado);
            }
            lista.addAll(pastaGavetaContratoFPFacade.getPastaGavetaFacade().listaFiltrandoPastasDisponiveisPorGaveta(parte.trim(), gavetaFicharioSelecionado));

            if (lista.isEmpty()) {
                FacesUtil.addInfo("Atenção !", "Não Existem Pastas Disponíveis nesta Gaveta !");
            }

            return lista;
        } else {
            return new ArrayList<>();
        }
    }

    public void trocarFichario() {
        gavetaFicharioSelecionado = null;
        pastaGavetaSelecionado = null;
    }

    public void trocarGaveta() {
        pastaGavetaSelecionado = null;
    }
    public void atribuirPasta() {
        selecionado.setPastaGaveta(pastaGavetaSelecionado);
    }

    @Override
    public void salvar() {
        try {
            selecionado.setContratoFP(contratoFPSelecionado);
            selecionado.setPastaGaveta(pastaGavetaSelecionado);
            validarCamposObrigatorios();
            if (validarVigencias()){
                return;
            }
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private boolean validarVigencias() {
        contratoFPSelecionado = pastaGavetaContratoFPFacade.getContratoFPFacade().recuperar(contratoFPSelecionado.getId());
        if (!DataUtil.isVigenciaValida(selecionado, contratoFPSelecionado.getPastasContratosFP())) {
            return true;
        }
        return false;
    }

    @Override
    public void excluir() {
        pastaGavetaContratoFPFacade.excluirPastaDoContratoFP(selecionado);
        FacesUtil.addOperacaoRealizada("Registro excluído com sucesso.");
        redireciona();
    }

    private void validarCamposObrigatorios() {
        ValidacaoException ve = new ValidacaoException();

        if (ficharioSelecionado == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Fichário é obrigatório.");

        }

        if (gavetaFicharioSelecionado == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Gaveta é obrigatório.");
        }

        if (pastaGavetaSelecionado == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Pasta é obrigatório.");
        }

        if (selecionado.getInicioVigencia() == null){
            ve.adicionarMensagemDeCampoObrigatorio("O campo Inicio de Vigência é obrigatório.");
        }

        if (contratoFPSelecionado == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Servidor é obrigatório.");
        }

        ve.lancarException();
    }

    public void buscarPastaDoContratoAnterior(){
        if (contratoFPSelecionado != null) {
            PastaGavetaContratoFP pastaDoContratoAnterior = pastaGavetaContratoFPFacade.buscarPastaDoContratoAnterior(contratoFPSelecionado);
            if (pastaDoContratoAnterior != null) {
                pastaGavetaSelecionado = pastaDoContratoAnterior.getPastaGaveta();
                gavetaFicharioSelecionado = pastaDoContratoAnterior.getPastaGaveta().getGavetaFichario();
                ficharioSelecionado = pastaDoContratoAnterior.getPastaGaveta().getGavetaFichario().getFichario();
                selecionado.setPastaGaveta(pastaDoContratoAnterior.getPastaGaveta());
                selecionado.setInicioVigencia(null);
                selecionado.setFinalVigencia(null);
            }
        }
        selecionado.setContratoFP(contratoFPSelecionado);
    }


    public PastaGaveta getPastaGavetaSelecionado() {
        return pastaGavetaSelecionado;
    }

    public void setPastaGavetaSelecionado(PastaGaveta pastaGavetaSelecionado) {
        this.pastaGavetaSelecionado = pastaGavetaSelecionado;
    }

    public ContratoFP getContratoFPSelecionado() {
        return contratoFPSelecionado;
    }

    public void setContratoFPSelecionado(ContratoFP contratoFPSelecionado) {
        this.contratoFPSelecionado = contratoFPSelecionado;
    }

    public Fichario getFicharioSelecionado() {
        return ficharioSelecionado;
    }

    public void setFicharioSelecionado(Fichario ficharioSelecionado) {
        this.ficharioSelecionado = ficharioSelecionado;
    }

    public GavetaFichario getGavetaFicharioSelecionado() {
        return gavetaFicharioSelecionado;
    }

    public void setGavetaFicharioSelecionado(GavetaFichario gavetaFicharioSelecionado) {
        this.gavetaFicharioSelecionado = gavetaFicharioSelecionado;
    }
}
