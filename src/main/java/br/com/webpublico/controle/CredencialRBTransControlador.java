/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.CredencialRBTrans;
import br.com.webpublico.entidades.CredencialTransporte;
import br.com.webpublico.entidades.FiltrosConsultaCredenciaisRBTrans;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidadesauxiliares.FiltroGeracaoCredencialRBTrans;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


@ManagedBean(name = "credencialRBTransControlador")
@ViewScoped
@URLMappings(mappings = {

    @URLMapping(id = "criarCredencial",
        pattern = "/geracao-de-credenciais/novo/",
        viewId = "/faces/tributario/rbtrans/geracaodecredenciais/edita.xhtml"),
    @URLMapping(id = "verCredencial",
        pattern = "/geracao-de-credenciais/ver/#{credencialRBTransControlador.id}/",
        viewId = "/faces/tributario/rbtrans/geracaodecredenciais/visualizar.xhtml"),
    @URLMapping(id = "emitirCredencial",
        pattern = "/rbtrans/emissao-de-credenciais/novo/",
        viewId = "/faces/tributario/rbtrans/emissaodecredenciais/edita.xhtml")
})

public class CredencialRBTransControlador extends PrettyControlador<CredencialRBTrans> implements Serializable, CRUD {

    @EJB
    private CredencialRBTransFacade credencialRBTransFacade;
    private FiltrosConsultaCredenciaisRBTrans filtros;
    private Future<List<CredencialRBTrans>> listaDeCredenciais;
    private Date dataGeracao;
    private FiltroGeracaoCredencialRBTrans filtroGeracao;
    private AssistenteBarraProgresso assistente;

    public CredencialRBTransControlador() {
        super(CredencialRBTrans.class);
    }

    public void parametrosIniciaisDaEmissao() {
        filtros = new FiltrosConsultaCredenciaisRBTrans();
        filtroGeracao = new FiltroGeracaoCredencialRBTrans();
    }

    @URLAction(mappingId = "emitirCredencial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void parametrosIniciaisDaEmissaodeCredencial() {
        parametrosIniciaisDaEmissao();
    }


    @Override
    public String getCaminhoPadrao() {
        return "/geracao-de-credenciais/";
    }

    @Override
    public Object getUrlKeyValue() {
        return getId();
    }

    public AssistenteBarraProgresso getAssistente() {
        return assistente;
    }

    public void setAssistente(AssistenteBarraProgresso assistente) {
        this.assistente = assistente;
    }

    @Override
    @URLAction(mappingId = "criarCredencial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        operacao = Operacoes.NOVO;
        parametrosIniciaisDaGeracao();
        filtroGeracao = new FiltroGeracaoCredencialRBTrans();
    }

    @Override
    public AbstractFacade getFacede() {
        return credencialRBTransFacade;
    }

    @Override
    public void salvar() {
        for (CredencialRBTrans c : getListaDeCredenciais()) {
            c.setNumero(credencialRBTransFacade.gerarNumero());
            credencialRBTransFacade.salvar(c);
        }

        FacesUtil.addOperacaoRealizada("Registro inserido com sucesso!");
        FacesUtil.redirecionamentoInterno("/geracao-de-credenciais/novo/");
    }

    @Override
    @URLAction(mappingId = "verCredencial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    public List getLista() {
        return credencialRBTransFacade.recuperarTodasCredenciais();
    }

    public void parametrosIniciaisDaGeracao() {
        selecionado = new CredencialTransporte();
        dataGeracao = credencialRBTransFacade.getSistemaFacade().getDataOperacao();
    }

    public FiltroGeracaoCredencialRBTrans getFiltroGeracao() {
        return filtroGeracao;
    }

    public void setFiltroGeracao(FiltroGeracaoCredencialRBTrans filtroGeracao) {
        this.filtroGeracao = filtroGeracao;
    }


    public CredencialRBTrans esteSelecionado() {
        return selecionado;
    }


    public String recuperarAnoDaData(Date data) {
        return Util.getAnoDaData(data);
    }

    public String formataData(Date data) {
        return Util.dateToString(data);
    }

    private void validarPesquisa() {
        ValidacaoException ve = new ValidacaoException();

        if (filtroGeracao.getNumeroPermissaoInicial() != null) {
            if (filtroGeracao.getNumeroPermissaoFinal() != null) {
                if (filtroGeracao.getNumeroPermissaoInicial() > filtroGeracao.getNumeroPermissaoFinal()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O número inicial do intervalo de permissões deve ser menor que o número final do intervalo.");
                }
            } else {
                ve.adicionarMensagemDeCampoObrigatorio("É necessário informar o final do intervalo do número da permissão.");
            }
        }
        if (filtroGeracao.getFinalPermissaoInicial() != null) {
            if (filtroGeracao.getFinalPermissaoFinal() != null) {
                if (filtroGeracao.getFinalPermissaoInicial() > filtroGeracao.getFinalPermissaoFinal()) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O número inicial do intervalo deve ser menor que o número final do intervalo.");
                }
            } else {
                ve.adicionarMensagemDeCampoObrigatorio("É necessário informar o fim do intervalo de final de permissão.");
            }
        }
        if (filtroGeracao.getTipoPermissaoRBTrans() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O tipo de transporte deve ser informado.");
        }
        if (filtroGeracao.getAnoValidade() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O Ano de Validade deve ser informado.");
        }

        ve.lancarException();
    }

    public void buscarPermissoesECriarCredenciais() {
        try {
            validarPesquisa();
            assistente = new AssistenteBarraProgresso();
            assistente.setExecutando(true);
            assistente.setUsuarioSistema(credencialRBTransFacade.getSistemaFacade().getUsuarioCorrente());
            assistente.setDataAtual(credencialRBTransFacade.getSistemaFacade().getDataOperacao());
            assistente.setDescricaoProcesso("Geração de credenciais de transporte");
            assistente.setFiltro(filtroGeracao);

            listaDeCredenciais = credencialRBTransFacade.geraCredenciais(assistente);

            FacesUtil.executaJavaScript("iniciaPesquisa()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void recuperarCredenciaisDeAcordoComFiltros() {

        ResultadoValidacao resultado = filtros.validaFiltros();

        if (resultado.isValidado()) {
            listaDeCredenciais = credencialRBTransFacade.recuperarCredenciaisPorFiltro(filtros);
        } else {
            exibeMensagensDoResultadoDaValidacao(resultado);
        }
    }


    public Date getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(Date dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    private void exibeMensagensDoResultadoDaValidacao(ResultadoValidacao resultado) {
        for (MensagemValidacao mv : resultado.getMensagens()) {
            FacesUtil.addError(mv.getSummary(), mv.getDetail() + mv.getClientId());
        }
    }

    private void exibeMensagemRequerenteNaoPossuiCNH(PessoaFisica pessoa) {
        FacesUtil.addError("Requerente sem registro!", "O requerente " + pessoa.getNome() + " não possui registro de cnh.");
    }

    public FiltrosConsultaCredenciaisRBTrans getFiltros() {
        return filtros;
    }

    public void setFiltros(FiltrosConsultaCredenciaisRBTrans filtros) {
        this.filtros = filtros;
    }

    public List<CredencialRBTrans> getListaDeCredenciais() {
        if (listaDeCredenciais != null && listaDeCredenciais.isDone()) {
            try {
                return listaDeCredenciais.get();
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public List<SelectItem> getItensTipoTransporte() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoPermissaoRBTrans object : TipoPermissaoRBTrans.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getItensTipoRequerente() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, "Ambos"));
        for (TipoRequerenteCredencialRBTrans object : TipoRequerenteCredencialRBTrans.values()) {
            if (filtroGeracao.getTipoPermissaoRBTrans() != null) {
                if (!(filtroGeracao.getTipoPermissaoRBTrans().equals(TipoPermissaoRBTrans.FRETE)
                    && object.equals(TipoRequerenteCredencialRBTrans.MOTORISTA))) {
                    toReturn.add(new SelectItem(object, object.getDescricao()));
                }
            }
        }
        return toReturn;
    }

    public List<SelectItem> getItensTipoRequerenteEmissao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));

        for (TipoRequerenteCredencialRBTrans object : TipoRequerenteCredencialRBTrans.values()) {
            if (filtros.getTipoTransporte() != null) {
                if (!(filtros.getTipoTransporte().equals(TipoPermissaoRBTrans.MOTO_TAXI) && object.equals(TipoRequerenteCredencialRBTrans.MOTORISTA))) {
                    toReturn.add(new SelectItem(object, object.getDescricao()));
                }
            }
        }
        return toReturn;
    }

    public List<SelectItem> getItensTipoCredencial() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, "Ambos"));
        for (TipoCredencialRBTrans object : TipoCredencialRBTrans.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getItensTipoPermissaoRBTrans() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoPermissaoRBTrans object : TipoPermissaoRBTrans.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getItensTipoFiltroCredencial() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoFiltroCredencialRBTrans object : TipoFiltroCredencialRBTrans.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getItensTipoPagamentoCredencial() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoPagamentoCredencialRBTrans object : TipoPagamentoCredencialRBTrans.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getItensTipoValidadeCredencial() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoValidadeCredencialRBTrans object : TipoValidadeCredencialRBTrans.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getItensTipoEmissaoCredencial() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoStatusEmissaoCredencialRBTrans object : TipoStatusEmissaoCredencialRBTrans.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public boolean mostraFiltroPermissao() {
        if (filtros == null) {
            return false;
        }

        return filtros.getTipoFiltro() == null ? false : filtros.getTipoFiltro().equals(TipoFiltroCredencialRBTrans.PERMISSAO);
    }

    public boolean mostraFiltroCMC() {
        if (filtros == null) {
            return false;
        }
        return filtros.getTipoFiltro() == null ? false : filtros.getTipoFiltro().equals(TipoFiltroCredencialRBTrans.CMC);
    }


    public void posPesquisa() throws ExecutionException, InterruptedException {
        if (listaDeCredenciais != null && listaDeCredenciais.isDone()) {
            assistente.setExecutando(false);
            FacesUtil.executaJavaScript("terminaPesquisa();");
            if ((listaDeCredenciais.get() != null) && listaDeCredenciais.get().isEmpty()) {
                FacesUtil.executaJavaScript("$('#rodape-erro').fadeIn('slow');");
            } else {
                FacesUtil.executaJavaScript("$('#rodape-continuar').fadeIn('slow');");
            }
        }
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public boolean validarSeUsuarioTemPermissaoAlterarData() {
        return credencialRBTransFacade.getUsuarioSistemaFacade().validaAutorizacaoUsuario(getSistemaControlador().getUsuarioCorrente(), AutorizacaoTributario.PERMITIR_INFORMAR_DATA_VENCIMENTO_CREDENCIAL_RBTRANS);
    }
}
