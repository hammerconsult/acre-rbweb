package br.com.webpublico.negocios;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.controle.Web;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.enums.TipoDesconto;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterExercicio;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.faces.bean.ViewScoped;

/**
 * @author fabio
 */
@ManagedBean(name = "parametroFiscalizacaoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoParametroFiscalizacaoISSQN", pattern = "/parametro-de-fiscalizacao/novo/", viewId = "/faces/tributario/fiscalizacao/parametro/edita.xhtml"),
        @URLMapping(id = "listarParametroFiscalizacaoISSQN", pattern = "/parametro-de-fiscalizacao/listar/", viewId = "/faces/tributario/fiscalizacao/parametro/lista.xhtml"),
        @URLMapping(id = "verParametroFiscalizacaoISSQN", pattern = "/parametro-de-fiscalizacao/ver/#{parametroFiscalizacaoControlador.id}/", viewId = "/faces/tributario/fiscalizacao/parametro/visualizar.xhtml"),
        @URLMapping(id = "editarParametroFiscalizacaoISSQN", pattern = "/parametro-de-fiscalizacao/editar/#{parametroFiscalizacaoControlador.id}/", viewId = "/faces/tributario/fiscalizacao/parametro/edita.xhtml")
})
public class ParametroFiscalizacaoControlador extends PrettyControlador<ParametroFiscalizacao> implements Serializable, CRUD {

    @EJB
    private ParametroFiscalizacaoFacade parametroFiscalizacaoFacade;
    private Converter converterTipoDoctoOficial;
    private Converter converterExercicio;
    private ConverterAutoComplete converterEntidade;
    private ConverterAutoComplete converterIndiceEconomico;
    private ConverterAutoComplete converterPessoa;
    private AtributoPesquisavel atributoPesquisavel;
    private Divida dividaIssqn;

    public ParametroFiscalizacaoControlador() {
        super(ParametroFiscalizacao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return parametroFiscalizacaoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/parametro-de-fiscalizacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novoParametroFiscalizacaoISSQN", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        if (this.selecionado.getVencimentoAutoInfracao() == null) {
            this.selecionado.setVencimentoAutoInfracao(0);
        }
        if (this.selecionado.getPrazoFiscalizacao() == null) {
            this.selecionado.setPrazoFiscalizacao(0);
        }
        if (this.selecionado.getPrazoApresentacaoDocto() == null) {
            this.selecionado.setPrazoApresentacaoDocto(0);
        }
        if (this.selecionado.getUltimoNumAutoInfracao() == null) {
            this.selecionado.setUltimoNumAutoInfracao(0);
        }
        if (this.selecionado.getUltimoNumHomologacao() == null) {
            this.selecionado.setUltimoNumHomologacao(0);
        }
        if (this.selecionado.getUltimoNumLevantamento() == null) {
            this.selecionado.setUltimoNumLevantamento(0);
        }
        if (this.selecionado.getUltimoNumOrdemServico() == null) {
            this.selecionado.setUltimoNumOrdemServico(0);
        }
        if (this.selecionado.getUltimoNumProgramacao() == null) {
            this.selecionado.setUltimoNumProgramacao(0);
        }
        if (this.selecionado.getUltimoNumTermoInicio() == null) {
            this.selecionado.setUltimoNumTermoInicio(0);
        }
        if (this.selecionado.getUltimoNumTermoFim() == null) {
            this.selecionado.setUltimoNumTermoFim(0);
        }
        atributoPesquisavel = AtributoPesquisavel.SECRETARIA;
        selecionado.setDividasIssqn(new ArrayList<ParametroFiscalizacaoDivida>());
    }

    public String getOrigem() {
        if (operacao != null && operacao.equals(Operacoes.EDITAR)) {
            return getCaminhoPadrao() + "editar/" + selecionado.getId() + "/";
        }
        return getCaminhoPadrao() + "novo/";
    }

    public void novoTipoDoctoTermoInicio() {
        Web.navegacao(selecionado, getOrigem(), "/tipo-de-documento-oficial/novo/", "ParametroFiscalizacao.tipoDoctoTermoInicio");
    }

    public void novoTipoDoctoTermoFim() {
        Web.navegacao(selecionado, getOrigem(), "/tipo-de-documento-oficial/novo/", "ParametroFiscalizacao.tipoDoctoTermoFim");
    }

    public void novoTipoDoctoTermoHomologacao() {
        Web.navegacao(selecionado, getOrigem(), "/tipo-de-documento-oficial/novo/", "ParametroFiscalizacao.tipoDoctoTermoHomologacao");
    }

    public void novoTipoDoctoAutoInfracao() {
        Web.navegacao(selecionado, getOrigem(), "/tipo-de-documento-oficial/novo/", "ParametroFiscalizacao.tipoDoctoAutoInfracao");
    }

    public void novoTipoDoctoOrdemServico() {
        Web.navegacao(selecionado, getOrigem(), "/tipo-de-documento-oficial/novo/", "ParametroFiscalizacao.tipoDoctoOrdemServico");
    }

    public void novoTipoDoctoRelatorioFiscal() {
        Web.navegacao(selecionado, getOrigem(), "/tipo-de-documento-oficial/novo/", "ParametroFiscalizacao.tipoDoctoRelatorioFiscal");
    }

    @Override
    @URLAction(mappingId = "verParametroFiscalizacaoISSQN", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarParametroFiscalizacaoISSQN", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        atributoPesquisavel = AtributoPesquisavel.SECRETARIA;
    }

    private boolean validaCampos() {
        boolean retorno = true;
        if ((selecionado.getId() == null) && (selecionado.getExercicio() != null) && (parametroFiscalizacaoFacade.exercicioJaExiste(selecionado.getExercicio()))) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Já existe um parâmetro cadastrado para esse exercício."));
            retorno = false;
        }
        return retorno;
    }

    @Override
    public void salvar() {
        if (!validaCampos()) {
            return;
        }
        super.salvar();
    }

    public Converter getConverterTipoDoctoOficial() {
        if (converterTipoDoctoOficial == null) {
            converterTipoDoctoOficial = new ConverterAutoComplete(TipoDoctoOficial.class, parametroFiscalizacaoFacade.getTipoDoctoOficialFacade());
        }
        return converterTipoDoctoOficial;
    }

    public ConverterAutoComplete getConverterEntidade() {
        if (converterEntidade == null) {
            converterEntidade = new ConverterAutoComplete(Entidade.class, parametroFiscalizacaoFacade.getEntidadeFacade());
        }
        return converterEntidade;
    }

    public Converter getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterExercicio(parametroFiscalizacaoFacade.getExercicioFacade());
        }
        return converterExercicio;
    }

    public List<TipoDoctoOficial> completaTipoDoctoOficial(String parte) {
        return parametroFiscalizacaoFacade.getTipoDoctoOficialFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public List<IndiceEconomico> completaIndiceEconomico(String parte) {
        return parametroFiscalizacaoFacade.getIndiceEconomicoFacade().listaFiltrando(parte.trim(), "descricao");
    }

    public List<Pessoa> completaDiretor(String parte) {
        return parametroFiscalizacaoFacade.getPessoaFacade().listaTodasPessoas(parte);
    }

    public List<Divida> completarDividaISSQN(String parte){
        return parametroFiscalizacaoFacade.getDividaFacade().buscarDividasTipoCadastroPorCodigoAndDescricao(parte, TipoCadastroTributario.ECONOMICO);
    }

    public Converter getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, parametroFiscalizacaoFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public Converter getConverterIndiceEconomico() {
        if (converterIndiceEconomico == null) {
            converterIndiceEconomico = new ConverterAutoComplete(IndiceEconomico.class, parametroFiscalizacaoFacade.getIndiceEconomicoFacade());
        }
        return converterIndiceEconomico;
    }

    public List<Entidade> completaEntidade(String parte) {
        try {
            return parametroFiscalizacaoFacade.getEntidadeFacade().listaEntidades(parte.trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void recuperaTipoDoctoTermoInicio(SelectEvent evento) {
        selecionado.setTipoDoctoTermoInicio((TipoDoctoOficial) evento.getObject());
    }

    public void recuperaTipoDoctoTermoFim(SelectEvent evento) {
        selecionado.setTipoDoctoTermoFim((TipoDoctoOficial) evento.getObject());
    }

    public void recuperaTipoDoctoTermoHomologacao(SelectEvent evento) {
        selecionado.setTipoDoctoTermoHomologacao((TipoDoctoOficial) evento.getObject());
    }

    public void recuperaTipoDoctoAutoInfracao(SelectEvent evento) {
        selecionado.setTipoDoctoAutoInfracao((TipoDoctoOficial) evento.getObject());
    }

    public AtributoPesquisavel getAtributoPesquisavel() {
        return atributoPesquisavel;
    }

    public List<SelectItem> getExercicios() {
        List<SelectItem> lista = new ArrayList();
        lista.add(new SelectItem(null, " "));
        for (Exercicio ex : parametroFiscalizacaoFacade.getExercicioFacade().listaExerciciosAtualFuturos()) {
            lista.add(new SelectItem(ex, ex.getAno().toString()));
        }
        return lista;
    }

    public List<SelectItem> getTipoDesconto() {
        List<SelectItem> lista = new ArrayList();
        lista.add(new SelectItem(null, " "));
        for (TipoDesconto tipo : TipoDesconto.values()) {
            lista.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return lista;
    }

    public void iniciaPesquisa(String nome) {
        atributoPesquisavel = Enum.valueOf(AtributoPesquisavel.class, nome);
    }

    public void selecionarObjetoPesquisaGenerico(ActionEvent event) {
        Object obj = event.getComponent().getAttributes().get("objeto");
        switch (atributoPesquisavel) {
            case DOCUMENTO_AUTO_INFRACAO:
                selecionado.setTipoDoctoAutoInfracao((TipoDoctoOficial) obj);
                break;
            case DOCUMENTO_ORDEM_SERVICO:
                selecionado.setTipoDoctoOrdemServico((TipoDoctoOficial) obj);
                break;
            case DOCUMENTO_RELATORIO_FISCAL:
                selecionado.setTipoDoctoRelatorioFiscal((TipoDoctoOficial) obj);
                break;
            case DOCUMENTO_TERMO_FIM:
                selecionado.setTipoDoctoTermoFim((TipoDoctoOficial) obj);
                break;
            case DOCUMENTO_TERMO_HOMOLOGACAO:
                selecionado.setTipoDoctoTermoHomologacao((TipoDoctoOficial) obj);
                break;
            case DOCUMENTO_TERMO_INICIO:
                selecionado.setTipoDoctoTermoInicio((TipoDoctoOficial) obj);
                break;
            case SECRETARIA:
                selecionado.setSecretaria((Entidade) obj);
                break;
            case DIRETOR:
                selecionado.setDiretor((Pessoa) obj);
                break;
        }
    }

    public enum AtributoPesquisavel {
        DOCUMENTO_TERMO_INICIO("TipoDoctoOficial"),
        DOCUMENTO_TERMO_FIM("TipoDoctoOficial"),
        DOCUMENTO_TERMO_HOMOLOGACAO("TipoDoctoOficial"),
        DOCUMENTO_AUTO_INFRACAO("TipoDoctoOficial"),
        DOCUMENTO_ORDEM_SERVICO("TipoDoctoOficial"),
        DOCUMENTO_RELATORIO_FISCAL("TipoDoctoOficial"),
        SECRETARIA("Entidade"),
        DIRETOR("Pessoa");
        private String classe;

        private AtributoPesquisavel(String classe) {
            this.classe = classe;
        }

        public String getClasse() {
            return classe;
        }
    }

    public List<ParametroFiscalizacaoDivida> dividasIssqnOrdenadas (){
        ordenarDividasIssqn();
        return selecionado.getDividasIssqn();
    }

    private void validarDividaIssqn(){
        ValidacaoException ve = new ValidacaoException();
        if (dividaIssqn == null){
            ve.adicionarMensagemDeCampoObrigatorio("Informe o campo Dívida de ISSQN");
        } else {
            for (ParametroFiscalizacaoDivida d : selecionado.getDividasIssqn()) {
                if (d.getDivida().getId().compareTo(dividaIssqn.getId()) == 0){
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Dívida: " + dividaIssqn.getDescricao() + " ja foi adicionada na lista.");
                    break;
                }
            }
        }
        ve.lancarException();
    }

    public void adicionarDividaIssqn() {
        try {
            validarDividaIssqn();
            ParametroFiscalizacaoDivida pfd = new ParametroFiscalizacaoDivida();
            pfd.setDivida(dividaIssqn);
            pfd.setParametroFiscalizacao(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getDividasIssqn(), pfd);
            dividaIssqn = new Divida();
            ordenarDividasIssqn();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void limparDividaIssqn(){
        dividaIssqn = new Divida();
    }

    public void removerDividaIssqn(ParametroFiscalizacaoDivida p){
        selecionado.getDividasIssqn().remove(p);
    }

    public Divida getDividaIssqn() {
        return dividaIssqn;
    }

    public void setDividaIssqn(Divida dividaIssqn) {
        this.dividaIssqn = dividaIssqn;
    }

    private void ordenarDividasIssqn() {
        Collections.sort(selecionado.getDividasIssqn(), new Comparator<ParametroFiscalizacaoDivida>() {
            @Override
            public int compare(ParametroFiscalizacaoDivida o1, ParametroFiscalizacaoDivida o2) {
                Integer i1 = o1.getDivida().getCodigo();
                Integer i2 = o2.getDivida().getCodigo();
                return i1.compareTo(i2);
            }
        });
    }
}
