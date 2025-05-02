/*
 * Codigo gerado automaticamente em Wed Feb 08 17:21:31 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Associacao;
import br.com.webpublico.entidades.ItemValorAssociacao;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.PessoaJuridica;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.TipoAssociacao;
import br.com.webpublico.enums.TipoValorAssociacao;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AssociacaoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "associacaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaAssociacao", pattern = "/associacao/novo/", viewId = "/faces/rh/administracaodepagamento/associacao/edita.xhtml"),
    @URLMapping(id = "editarAssociacao", pattern = "/associacao/editar/#{associacaoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/associacao/edita.xhtml"),
    @URLMapping(id = "listarAssociacao", pattern = "/associacao/listar/", viewId = "/faces/rh/administracaodepagamento/associacao/lista.xhtml"),
    @URLMapping(id = "verAssociacao", pattern = "/associacao/ver/#{associacaoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/associacao/visualizar.xhtml")
})
public class AssociacaoControlador extends PrettyControlador<Associacao> implements Serializable, CRUD {

    @EJB
    private AssociacaoFacade associacaoFacade;

    private ConverterAutoComplete converterPessoa;
    private ItemValorAssociacao itemValorAssociacaoSelecionado;

    public AssociacaoControlador() {
        super(Associacao.class);
    }

    public AssociacaoFacade getFacade() {
        return associacaoFacade;
    }

    public ItemValorAssociacao getItemValorAssociacaoSelecionado() {
        return itemValorAssociacaoSelecionado;
    }

    public void setItemValorAssociacaoSelecionado(ItemValorAssociacao itemValorAssociacaoSelecionado) {
        this.itemValorAssociacaoSelecionado = itemValorAssociacaoSelecionado;
    }

    @Override
    public AbstractFacade getFacede() {
        return associacaoFacade;
    }

    public Converter getConverterPessoa() {
        if (converterPessoa == null) {
            converterPessoa = new ConverterAutoComplete(Pessoa.class, associacaoFacade.getPessoaJuridicaFacade());
        }
        return converterPessoa;
    }

    public List<PessoaJuridica> completaPessoa(String parte) {
        return associacaoFacade.getPessoaJuridicaFacade().listaFiltrando(parte.trim(), "nomeFantasia");
    }

    public List<SelectItem> getTiposAssociacao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoAssociacao obj : TipoAssociacao.values()) {
            toReturn.add(new SelectItem(obj, obj.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposValorAssociacao() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoValorAssociacao obj : TipoValorAssociacao.values()) {
            toReturn.add(new SelectItem(obj, obj.getDescricao()));
        }
        return toReturn;
    }

    @Override
    public void salvar() {
        if (podeSalvar()) {
            super.salvar();
        }
    }

    @URLAction(mappingId = "novaAssociacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verAssociacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarAssociacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    public Boolean podeSalvar() {
        if (!Util.validaCampos(selecionado)) {
            return false;
        }
        if (!selecionado.temPeloMenosItemValorAdicionado()) {
            FacesUtil.addOperacaoNaoPermitida("É obrigatório adicionar pelo menos um item valor!");
            return false;
        }
        return true;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/associacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public void novoItemValor() {
        itemValorAssociacaoSelecionado = new ItemValorAssociacao(selecionado);
    }

    public void confirmarItemValor() {
        if (podeConfirmarItemValor()) {
            adicionarItemValor();
            setarNullItemValor();
        }
    }

    public void adicionarItemValor() {
        selecionado.setItensValoresAssociacoes(Util.adicionarObjetoEmLista(selecionado.getItensValoresAssociacoes(), itemValorAssociacaoSelecionado));
    }

    private boolean podeConfirmarItemValor() {
        if (!Util.validaCampos(itemValorAssociacaoSelecionado)) {
            return false;
        }
        if (!DataUtil.isVigenciaValida(itemValorAssociacaoSelecionado, selecionado.getItensValoresAssociacoes())) {
            return false;
        }
        if (!isValorValido()) {
            return false;
        }
        return true;
    }

    public void cancelarItemValor() {
        if (itemValorAssociacaoSelecionado.estaEditando()) {
            adicionarItemValor();
        }
        setarNullItemValor();
    }

    private void setarNullItemValor() {
        itemValorAssociacaoSelecionado = null;
    }

    public void selecionarItemValor(ItemValorAssociacao itemValor) {
        itemValorAssociacaoSelecionado = (ItemValorAssociacao) Util.clonarObjeto(itemValor);
        itemValorAssociacaoSelecionado.setOperacao(Operacoes.EDITAR);
        removerItemValor(itemValor);
    }

    public void removerItemValor(ItemValorAssociacao itemValor) {
        selecionado.getItensValoresAssociacoes().remove(itemValor);
    }

    public boolean isValorValido() {
        if (itemValorAssociacaoSelecionado.isTipoValorFixo()) {
            if (isValorMenorQueZero()) {
                FacesUtil.addOperacaoNaoPermitida("O valor deve ser maior que zero (0)!");
                setarValorZero();
                return false;
            }
        }
        if (itemValorAssociacaoSelecionado.isTipoValorPercentual()) {
            if (isValorMenorQueZero() || isValorMaiorQueCem()) {
                FacesUtil.addOperacaoNaoPermitida("O valor percentual deve ser entre zero (0) e  cem (100)!");
                setarValorZero();
                return false;
            }
        }
        return true;
    }

    public void setarValorZero() {
        itemValorAssociacaoSelecionado.setValor(BigDecimal.ZERO);
    }

    public boolean isValorMenorQueZero() {
        return BigDecimal.ZERO.compareTo(itemValorAssociacaoSelecionado.getValor()) >= 0;
    }

    public boolean isValorMaiorQueCem() {
        return new BigDecimal(100).compareTo(itemValorAssociacaoSelecionado.getValor()) < 0;
    }
}
