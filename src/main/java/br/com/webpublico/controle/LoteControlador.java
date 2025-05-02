/*
 * Codigo gerado automaticamente em Tue Mar 01 17:36:11 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.ClasseDoAtributo;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.LoteFacade;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "loteControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoLote", pattern = "/tributario/lote/novo/",
        viewId = "/faces/tributario/cadastromunicipal/lotes/edita.xhtml"),
    @URLMapping(id = "editarLote", pattern = "/tributario/lote/editar/#{loteControlador.id}/",
        viewId = "/faces/tributario/cadastromunicipal/lotes/edita.xhtml"),
    @URLMapping(id = "listarLote", pattern = "/tributario/lote/listar/",
        viewId = "/faces/tributario/cadastromunicipal/lotes/lista.xhtml"),
    @URLMapping(id = "verLote", pattern = "/tributario/lote/ver/#{loteControlador.id}/",
        viewId = "/faces/tributario/cadastromunicipal/lotes/visualizar.xhtml")
})
public class LoteControlador extends PrettyControlador<Lote>
    implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(LoteControlador.class);
    @EJB
    private LoteFacade loteFacade;
    private ConverterAutoComplete converterLogradouro;
    private ConverterAutoComplete converterQuadra;
    private Converter converterSetor;
    private ConverterAutoComplete converterLoteamento;
    private ConverterGenerico converterFace;
    private Converter converterBairro;
    private ConverterAutoComplete converterLote;
    private ConfiguracaoTributario configuracaoTributario;
    private Loteamento loteamento;
    private Bairro bairroLogradouro;
    private Logradouro logradouroPrincipal;
    private List<SelectItem> faces;
    private Testada testada;
    private Logradouro logradouroFace;

    public ConfiguracaoTributario getConfiguracaoTributario() {
        return configuracaoTributario;
    }

    public void setConfiguracaoTributario(ConfiguracaoTributario configuracaoTributario) {
        this.configuracaoTributario = configuracaoTributario;
    }

    public String getUrlAtual() {
        return PrettyContext.getCurrentInstance().getRequestURL().toString();
    }

    public boolean getListaDeTestadasPreenchida() {
        return !selecionado.getTestadas().isEmpty();
    }

    public Converter getConverterBairro() {
        if (converterBairro == null) {
            converterBairro = new ConverterAutoComplete(Bairro.class, loteFacade.getBairroFacade());
        }
        return converterBairro;
    }

    public LoteControlador() {
        super(Lote.class);
    }

    public void atribuiQuadra() {
        selecionado.setQuadra(null);
    }

    public LoteFacade getFacade() {
        return loteFacade;
    }

    public Bairro getBairroLogradouro() {
        return bairroLogradouro;
    }

    public void setBairroLogradouro(Bairro bairroLogradouro) {
        this.bairroLogradouro = bairroLogradouro;
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    private void limparCampos() {
        loteamento = new Loteamento();
        logradouroPrincipal = new Logradouro();
        faces = new ArrayList<>();
        testada = new Testada();
    }

    @URLAction(mappingId = "novoLote", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        configuracaoTributario = loteFacade.getConfiguracaoTributarioFacade().retornaUltimo();
        if (configuracaoTributario != null) {
            selecionado = (Lote) Web.pegaDaSessao(Lote.class);
            if (selecionado == null) {
                super.novo();
                selecionado.setAtributos(loteFacade.getAtributoFacade().novoMapaAtributoValorAtributo(ClasseDoAtributo.LOTE));
                limparCampos();
            }
        } else {
            cancelar();
            FacesUtil.addWarn("Configuração não encontrada!", "É necessário cadastrar uma Configuração do Tributário.");
        }
    }

    @URLAction(mappingId = "verLote", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        editar();
    }

    @URLAction(mappingId = "editarLote", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        configuracaoTributario = loteFacade.getConfiguracaoTributarioFacade().retornaUltimo();
        if (configuracaoTributario != null) {
            selecionado = ((Lote) Web.pegaDaSessao(Lote.class));
            if (selecionado == null) {
                super.editar();
                loteFacade.getAtributoFacade().completaAtributos(selecionado.getAtributos(), ClasseDoAtributo.LOTE);
                if (selecionado.getQuadra() != null) {
                    setLoteamento(selecionado.getQuadra().getLoteamento());
                }
                for (Testada t : selecionado.getTestadas()) {
                    if (t.getPrincipalString().equals("Sim")) {
                        if (t.getFace() != null) {
                            Face f = loteFacade.getFaceFacade().recuperar(t.getFace().getId());
                            setLogradouroPrincipal(f.getLogradouroBairro().getLogradouro());
                        }
                    }
                }

                setLogradouroFace(new Logradouro());
                atualizarFaces();
                if (!selecionado.getTestadas().isEmpty()) {
                    bairroLogradouro = loteFacade.getBairroFacade().recuperaBairroPorTestada(selecionado.getTestadaPrincipal());
                }
            }
            limparCampos();
        } else {
            cancelar();
            FacesUtil.addWarn("Configuração não encontrada!", "É necessário cadastrar uma Configuração do Tributário.");
        }
    }

    public Loteamento getLoteamento() {
        return loteamento;
    }

    public void setLoteamento(Loteamento loteamento) {
        loteamento = loteamento;
    }

    public Converter getConverterLoteamento() {
        if (converterLoteamento == null) {
            converterLoteamento = new ConverterAutoComplete(Loteamento.class, loteFacade.getLoteamentoFacade());
        }
        return converterLoteamento;
    }

    public Logradouro getLogradouroPrincipal() {
        return logradouroPrincipal;
    }

    public void setLogradouroPrincipal(Logradouro logradouro) {
        logradouroPrincipal = logradouro;
    }

    public Logradouro getLogradouroFace() {
        return logradouroFace;
    }

    public void setLogradouroFace(Logradouro logradouroFace) {
        logradouroFace = logradouroFace;
    }

    public Converter getConverterFace() {
        if (converterFace == null) {
            converterFace = new ConverterGenerico(Face.class, loteFacade.getFaceFacade());
        }
        return converterFace;
    }

    public Converter getConverterSetor() {
        if (converterSetor == null) {
            converterSetor = new ConverterGenerico(Setor.class, loteFacade.getSetorFacade());
        }
        return converterSetor;
    }

    public List<SelectItem> getFaces() {
        atualizarFaces();
        return faces;
    }

    public List<SelectItem> getSetores() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (Setor obj : loteFacade.getSetorFacade().listaSetoresOrdenadosPorCodigo()) {
            toReturn.add(new SelectItem(obj, obj.toString()));
        }

        return toReturn;
    }

    public List<Quadra> completarQuadra(String parte) {
        if (selecionado.getSetor() != null) {
            return loteFacade.getQuadraFacade().buscarPorSetor(selecionado.getSetor(), parte.trim());
        }
        return Lists.newArrayList();
    }

    public List<Bairro> completaBairro(String parte) {
        return loteFacade.getBairroFacade().completaBairro(parte);
    }

    public Converter getConverterQuadra() {
        if (converterQuadra == null) {
            converterQuadra = new ConverterAutoComplete(Quadra.class, loteFacade.getQuadraFacade());
        }
        return converterQuadra;
    }

    public Converter getConverterLogradouro() {
        if (converterLogradouro == null) {
            converterLogradouro = new ConverterAutoComplete(Logradouro.class, loteFacade.getLogradouroFacade());
        }
        return converterLogradouro;
    }

    public Testada getTestada() {
        return testada;
    }

    public void setTestada(Testada testada) {
        testada = testada;
    }

    public List<Logradouro> completaLogradouros(String parte) {
        return loteFacade.getLogradouroFacade().completaLogradourosPorBairro(parte.trim(), bairroLogradouro);
    }

    public void handleSelect(SelectEvent event) {
        Logradouro l = (Logradouro) event.getObject();
        logradouroFace = loteFacade.getLogradouroFacade().recuperar(l.getId());
        atualizarFaces();
    }

    public void atualizarFaces() {
        faces = Lists.newArrayList();
        if (logradouroFace != null && logradouroFace.getId() != null) {
            for (Face f : loteFacade.getFaceFacade().recuperarPorLogradouro(logradouroFace)) {
                faces.add(new SelectItem(f, f.getCodigoFace() + " - " + f.getLado()));
            }
        }
    }

    public void novaTestada() {
        if (validatestada(testada)) {
            testada.setLote(selecionado);
            testada.setPrincipal(selecionado.getTestadas().isEmpty());
            testada.setDataRegistro(new Date());
            selecionado.getTestadas().add(testada);
            testada = new Testada();
            logradouroFace = null;
        }
    }

    public boolean testadaJaExiste(Testada testadaVerificada) {
        for (Testada t : selecionado.getTestadas()) {
            if (t.getFace().equals(testadaVerificada.getFace())) {
                return true;
            }
        }
        return false;
    }

    public void removeTestada(ActionEvent evento) {
        selecionado.getTestadas().remove((Testada) evento.getComponent().getAttributes().get("removeTestada"));
    }

    public List<Setor> completaSetor(String parte) {
        return loteFacade.getSetorFacade().listaFiltrando(parte.trim(), "nome");
    }

    public List<Loteamento> completaLoteamento(String parte) {
        return loteFacade.getLoteamentoFacade().listaFiltrando(parte.trim(), "nome");
    }

    public void excluirSelecionado() {
        loteFacade.remover(selecionado);
    }

    public void redireciona() {
        CRUD c = (CRUD) this;
        FacesUtil.navegaEmbora(selecionado, c.getCaminhoPadrao());
    }

    @Override
    public void salvar() {
        if (validarCampos() && ValidaAtributosGenericos.validaAtributosdGenericos(selecionado.getAtributos())) {
            if (Util.validaCampos(selecionado)) {
                selecionado.popularCaracteristicas();
                try {
                    getFacede().salvar(selecionado);
                } catch (ValidacaoException ex) {
                    FacesUtil.printAllFacesMessages(ex.getMensagens());
                    return;
                } catch (Exception e) {
                    logger.error(e.getMessage());

                }
                redireciona();
            }
        }
    }

    public ConverterAutoComplete getConverterLote() {
        if (converterLote == null) {
            converterLote = new ConverterAutoComplete(Lote.class, loteFacade);
        }
        return converterLote;
    }

    @Override
    public void cancelar() {
        Web.getEsperaRetorno();
        FacesUtil.navegaEmbora(selecionado, getCaminhoPadrao());
    }

    @Override
    public void excluir() {
        try {
            getFacede().remover(selecionado);
            Web.getEsperaRetorno();
            FacesUtil.navegaEmbora(selecionado, getCaminhoPadrao());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), "Registro excluído com sucesso."));
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private boolean validarCampos() {
        boolean retorno = true;
        if (selecionado.getSetor() == null) {
            retorno = false;
            FacesUtil.addWarn("Atenção!", "O Setor é um campo obrigatório.");
        }
        if (selecionado.getQuadra() == null) {
            FacesUtil.addWarn("Atenção!", "A Quadra é um campo obrigatório.");
            retorno = false;
        } else {
            if (loteFacade.codigoJaExisteNaQuadra(selecionado)) {
                FacesUtil.addWarn("Atenção", "Já existe um lote cadastrado para a quadra informada com este código");
                retorno = false;
            }
        }
        if (selecionado.getCodigoLote() == null || selecionado.getCodigoLote().trim().equals("")) {
            FacesUtil.addWarn("Atenção!", "O Código do lote é um campo obrigatório.");
            retorno = false;
        } else if (selecionado.getCodigoLote().replaceAll(" ", "").length() < configuracaoTributario.getNumDigitosLote()) {
            FacesUtil.addWarn("Código inválido.", "A configuração vigente exige um código com " + configuracaoTributario.getNumDigitosLote() + " dígitos.");
            retorno = false;
        }
        if (selecionado.getAreaLote() == null || selecionado.getAreaLote().doubleValue() < 0.0) {
            FacesUtil.addWarn("Atenção!", "A Área do lote é um campo obrigatório e deve ser maior que zero.");
            retorno = false;
        }
        if (selecionado.getTestadas().isEmpty()) {
            FacesUtil.addWarn("Atenção!", "O lote deve possuir ao menos uma testada.");
            retorno = false;
        } else {
            int qtdTestadasPrincipais = 0;
            for (Testada testadaSelecionado : selecionado.getTestadas()) {
                if (testadaSelecionado.getPrincipal()) {
                    qtdTestadasPrincipais++;
                }
            }
            if (qtdTestadasPrincipais == 0) {
                retorno = false;
                FacesUtil.addWarn("Atenção!", "O lote deve possuir ao menos uma testada principal.");
            }
            if (qtdTestadasPrincipais > 1) {
                retorno = false;
                FacesUtil.addWarn("Atenção!", "O lote deve possuir apenas uma testada principal.");
            }
        }

        return retorno;
    }

    private boolean validatestada(Testada testada) {
        boolean resultado = true;
        if (bairroLogradouro == null) {
            resultado = false;
            FacesUtil.addWarn("Atenção!", "O Bairro é um campo obrigatório.");
        }
        if (logradouroFace == null) {
            resultado = false;
            FacesUtil.addWarn("Atenção!", "O Logradouro é um campo obrigatório.");
        }
        if (testada.getFace() == null) {
            resultado = false;
            FacesUtil.addWarn("Atenção!", "A Face é um campo obrigatório.");
        } else if (testadaJaExiste(testada)) {
            resultado = false;
            FacesUtil.addWarn("Atenção!", "Testada já adicionada");
        }
        if (testada.getTamanho() == null || testada.getTamanho().doubleValue() <= 0.0) {
            resultado = false;
            FacesUtil.addWarn("Atenção!", "O tamanho por metro linear da testada é um campo obrigatório e deve ser maior que zero");
        }
        return resultado;
    }

    public AbstractFacade getFacede() {
        return loteFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/lote/";
    }
}
