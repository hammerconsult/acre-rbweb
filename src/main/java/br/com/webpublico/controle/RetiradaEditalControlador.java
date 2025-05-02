package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SituacaoCadastralPessoa;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.administrativo.RetiradaEditalFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;

/**
 * Created by hudson on 05/10/2015.
 */

@ManagedBean(name = "retiradaEditalControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-retirada-edital", pattern = "/licitacao/retirada/", viewId = "/faces/administrativo/licitacao/retiradaedital/edita.xhtml")
})
public class RetiradaEditalControlador extends PrettyControlador<RetiradaEdital> implements Serializable, CRUD {

    @EJB
    private RetiradaEditalFacade retiradaEditalFacade;
    private RetiradaEdital retiradaEditalSelecionado;
    private List<RetiradaEdital> listaDeRetiradaEdital;
    private List<RetiradaEdital> listaDeRetiradaExcluida;
    private Licitacao licitacaoSelecionado;
    private ConverterAutoComplete converterPessoa;
    private ConverterAutoComplete converterLicitacao;

    public RetiradaEditalControlador() {
        super(RetiradaEdital.class);
    }

    @URLAction(mappingId = "novo-retirada-edital", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        inicializarAtributos();
    }

    @Override
    public void salvar() {
        retiradaEditalFacade.salvar(listaDeRetiradaEdital, listaDeRetiradaExcluida);
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao());
        FacesUtil.addOperacaoRealizada("Salvo com sucesso!");
    }

    @Override
    public void cancelar() {
        FacesUtil.redirecionamentoInterno("/licitacao/retirada");
    }

    // metodos void

    public void inicializarAtributos() {
        licitacaoSelecionado = null;
        retiradaEditalSelecionado = null;
        listaDeRetiradaEdital = null;
    }

    public void carregarLicitacaoSelecionado() {
        licitacaoSelecionado = retiradaEditalFacade.getLicitacaoFacade().recuperar(licitacaoSelecionado.getId());
        listaDeRetiradaEdital = retiradaEditalFacade.buscarRetiradaEdital(licitacaoSelecionado);
        listaDeRetiradaExcluida = Lists.newArrayList();
    }

    public void novoRetiradaEdital() {
        retiradaEditalSelecionado = new RetiradaEdital();
        retiradaEditalSelecionado.setRetiradaEm(UtilRH.getDataOperacao());
    }

    public void removerRetiradaEdital(RetiradaEdital retiradaEdital) {
        if (retiradaEdital != null) {
            listaDeRetiradaEdital.remove(retiradaEdital);
            listaDeRetiradaExcluida.add(retiradaEdital);
        }
    }

    public void validarSeSituacaoCadastralPessoaEValida(SelectEvent evento) {
        if (retiradaEditalSelecionado.getFornecedor() != null && retiradaEditalSelecionado.getFornecedor() instanceof PessoaFisica) {
            retiradaEditalSelecionado.setRepresentante(null);
        }
        Pessoa pessoa = (Pessoa) evento.getObject();
        if (!pessoa.getSituacaoCadastralPessoa().equals(SituacaoCadastralPessoa.ATIVO)) {
            UIComponent campo = evento.getComponent();
            ((UIInput) campo).setValue(null);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Situação Cadastral Inválida!", "A pessoa jurídica selecionada está com situação cadastral '" + pessoa.getSituacaoCadastralPessoa() + "'. Por favor, regularize a situação!"));
        }
    }

    public void adicionarRetiradaEdital() {
        if (isValidaFornecedor()) {
            boolean editandoRetirada = isEstaEditandoRetirada();
            retiradaEditalSelecionado.setLicitacao(licitacaoSelecionado);
            setListaDeRetiradaEdital(Util.adicionarObjetoEmLista(listaDeRetiradaEdital, retiradaEditalSelecionado));
            criarNovaRetirada(editandoRetirada);
        }
    }

    private void criarNovaRetirada(boolean editandoRetirada) {
        if (editandoRetirada) {
            retiradaEditalSelecionado = null;
            FacesUtil.executaJavaScript("dialogFornecedor.hide()");
        } else {
            novoRetiradaEdital();
        }
    }

    public void cancelarRetiradaEdital() {
        this.retiradaEditalSelecionado = null;
    }

    // metodos list

    public List<Licitacao> completarLicitacaoPorAndamento(String parte) {
        return retiradaEditalFacade.buscarLicitacaoPorStatusEmAndamento(parte.trim());
    }

    public List<Pessoa> completarTodasAsPessoas(String parte) {
        return retiradaEditalFacade.getPessoaFacade().listaTodasPessoas(parte.trim());
    }

    public List<Pessoa> completarPessoaFisica(String parte) {
        return retiradaEditalFacade.getPessoaFacade().listaPessoasFisicas(parte.trim());
    }

    // metodos booleanos

    public boolean isVisualizar() {
        if (Operacoes.VER.equals(operacao)) {
            return true;
        }
        return false;
    }

    public boolean isEstaEditandoRetirada() {
        return listaDeRetiradaEdital.contains(retiradaEditalSelecionado);
    }

    public boolean fornecedorEPessoaJuridica() {
        if (retiradaEditalSelecionado != null) {
            if (retiradaEditalSelecionado.getFornecedor() != null) {
                return retiradaEditalSelecionado.getFornecedor() instanceof PessoaJuridica;
            }
        }
        return true;
    }

    public Boolean isValidaFornecedor() {
        if (retiradaEditalSelecionado.getFornecedor() == null) {
            FacesUtil.addCampoObrigatorio("Por favor, selecione um fornecedor para adicioná-lo.");
            return Boolean.FALSE;
        }

        if (retiradaEditalSelecionado.getFornecedor().isPessoaJuridica() && retiradaEditalSelecionado.getRepresentante() == null) {
            FacesUtil.addCampoObrigatorio("Por favor, selecione o representante do fornecedor informado.");
            return Boolean.FALSE;
        }

        for (RetiradaEdital retirada : listaDeRetiradaEdital) {
            if (retiradaEditalSelecionado.getFornecedor().equals(retirada.getFornecedor()) && !listaDeRetiradaEdital.contains(retiradaEditalSelecionado)) {
                FacesUtil.addOperacaoNaoPermitida("O Fornecedor selecionado já foi adicionado a esta licitação.");
                return Boolean.FALSE;
            }
        }

        return Boolean.TRUE;
    }

    // metodos converters

    public ConverterAutoComplete getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, retiradaEditalFacade.getPessoaFacade());
        }
        return converterPessoa;
    }

    public ConverterAutoComplete getConverterLicitacao() {
        if (converterLicitacao == null) {
            converterLicitacao = new ConverterAutoComplete(Licitacao.class, retiradaEditalFacade.getLicitacaoFacade());
        }
        return converterLicitacao;
    }

    // getters e setters

    public RetiradaEditalFacade getRetiradaEditalFacade() {
        return retiradaEditalFacade;
    }

    public void setRetiradaEditalFacade(RetiradaEditalFacade retiradaEditalFacade) {
        this.retiradaEditalFacade = retiradaEditalFacade;
    }

    public RetiradaEdital getRetiradaEditalSelecionado() {
        return retiradaEditalSelecionado;
    }

    public void setRetiradaEditalSelecionado(RetiradaEdital retiradaEditalSelecionado) {
        this.retiradaEditalSelecionado = retiradaEditalSelecionado;
    }

    public Licitacao getLicitacaoSelecionado() {
        return licitacaoSelecionado;
    }

    public void setLicitacaoSelecionado(Licitacao licitacaoSelecionado) {
        this.licitacaoSelecionado = licitacaoSelecionado;
    }

    public List<RetiradaEdital> getListaDeRetiradaEdital() {
        return listaDeRetiradaEdital;
    }

    public void setListaDeRetiradaEdital(List<RetiradaEdital> listaDeRetiradaEdital) {
        this.listaDeRetiradaEdital = listaDeRetiradaEdital;
    }

    public void setConverterPessoa(ConverterAutoComplete converterPessoa) {
        this.converterPessoa = converterPessoa;
    }

    public void setConverterLicitacao(ConverterAutoComplete converterLicitacao) {
        this.converterLicitacao = converterLicitacao;
    }

    @Override
    public AbstractFacade getFacede() {
        return retiradaEditalFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/licitacao/retirada/";
    }

    @Override
    public Object getUrlKeyValue() {
        return null;
    }

}
