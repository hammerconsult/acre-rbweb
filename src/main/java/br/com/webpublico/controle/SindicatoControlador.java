/*
 * Codigo gerado automaticamente em Thu Aug 04 08:42:59 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SituacaoCadastralPessoa;
import br.com.webpublico.enums.TipoItemSindicato;
import br.com.webpublico.enums.TipoValorItemSindicato;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.PessoaFacade;
import br.com.webpublico.negocios.SindicatoFacade;
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
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "sindicatoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoSindicato", pattern = "/sindicato/novo/", viewId = "/faces/rh/administracaodepagamento/sindicato/edita.xhtml"),
    @URLMapping(id = "listaSindicato", pattern = "/sindicato/listar/", viewId = "/faces/rh/administracaodepagamento/sindicato/lista.xhtml"),
    @URLMapping(id = "verSindicato", pattern = "/sindicato/ver/#{sindicatoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/sindicato/visualizar.xhtml"),
    @URLMapping(id = "editarSindicato", pattern = "/sindicato/editar/#{sindicatoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/sindicato/edita.xhtml"),
})
public class SindicatoControlador extends PrettyControlador<Sindicato> implements Serializable, CRUD {

    @EJB
    private SindicatoFacade sindicatoFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    private ConverterAutoComplete converterPessoaJuridica;
    private ItemSindicato itemSindicato;
    private ConverterGenerico converterTipoItemSindicato;
    private ConverterGenerico converterTipoValorItemSindicato;

    public SindicatoControlador() {
        super(Sindicato.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return sindicatoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/sindicato/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<SelectItem> getPessoaJuridica() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (PessoaJuridica object : sindicatoFacade.getPessoaJuridicaFacade().lista()) {
            toReturn.add(new SelectItem(object, object.getNome()));
        }
        return toReturn;
    }

    public Converter getConverterPessoaJuridica() {
        if (converterPessoaJuridica == null) {
            converterPessoaJuridica = new ConverterAutoComplete(PessoaJuridica.class, sindicatoFacade.getPessoaJuridicaFacade());
        }
        return converterPessoaJuridica;
    }

    public List<Pessoa> completarPessoasJuridicas(String parte) {
        return pessoaFacade.listaPessoasJuridicas(parte, SituacaoCadastralPessoa.ATIVO);
    }

    public List<EventoFP> completarEventos(String filtro) {
        return sindicatoFacade.getEventoFPFacade().buscarFiltrandoEventosAtivosOrdenadoPorCodigo(filtro);
    }

    public List<Sindicato> completarSindicato(String filtro) {
        return sindicatoFacade.buscarSindicatoCNPJRazao(filtro);
    }

    @URLAction(mappingId = "novoSindicato", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        getSelecionado().setCodigo(sindicatoFacade.retornaUltimoCodigoLong());
        getSelecionado().setItensSindicatos(new ArrayList<ItemSindicato>());
        itemSindicato = new ItemSindicato();
    }

    @URLAction(mappingId = "verSindicato", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            if (operacao == Operacoes.NOVO) {
                Long novoCodigo = sindicatoFacade.retornaUltimoCodigoLong();
                if (!novoCodigo.equals(getSelecionado().getCodigo())) {
                    getSelecionado().setCodigo(novoCodigo);
                    FacesUtil.addInfo("Salvo com Sucesso", "O Código " + getSelecionado().getCodigo() + " já está sendo usado e foi gerado o novo código " + novoCodigo + " !");
                }
            }
            super.salvar();
        }
    }

    public ItemSindicato getItemSindicato() {
        return itemSindicato;
    }

    public void setItemSindicato(ItemSindicato itemSindicato) {
        this.itemSindicato = itemSindicato;
    }

    @URLAction(mappingId = "editarSindicato", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        itemSindicato = new ItemSindicato();
    }

    public void adicionarItemSindicato() {
        if (validaCamposItem()) {
            Sindicato sindicatoSelecionado = getSelecionado();
            itemSindicato.setSindicato(sindicatoSelecionado);

            if (itemSindicato.getTipoValorItemSindicato() == TipoValorItemSindicato.VALOR_DIA_TRABALHADO) {
                itemSindicato.setValor(null);
            }

            sindicatoSelecionado.getItensSindicatos().add(itemSindicato);
            itemSindicato = new ItemSindicato();
        }
    }

    public void removerItemSindicato(ActionEvent e) {
        itemSindicato = (ItemSindicato) e.getComponent().getAttributes().get("objeto");
        if (itemSindicato.getId() == null || itemSindicato.equals(sindicatoFacade.getItemSindicatoFacade().recuperaItemSindicatoVigente(getSelecionado()))) {
            getSelecionado().getItensSindicatos().remove(itemSindicato);
        } else {
            FacesUtil.addWarn("Atenção", " Não é possível excluir um item não vigente !");
        }
        itemSindicato = new ItemSindicato();
    }

    public ConverterGenerico getConverterTipoItemSindicato() {
        return converterTipoItemSindicato;
    }

    public void setConverterTipoItemSindicato(ConverterGenerico converterTipoItemSindicato) {
        this.converterTipoItemSindicato = converterTipoItemSindicato;
    }

    public ConverterGenerico getConverterTipoValorItemSindicato() {
        return converterTipoValorItemSindicato;
    }

    public void setConverterTipoValorItemSindicato(ConverterGenerico converterTipoValorItemSindicato) {
        this.converterTipoValorItemSindicato = converterTipoValorItemSindicato;
    }

    public List<SelectItem> getTipoItemSindicato() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoItemSindicato object : TipoItemSindicato.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoValorItemSindicato() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoValorItemSindicato object : TipoValorItemSindicato.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public boolean validaCamposItem() {
        if (itemSindicato.getTipoItemSindicato() == null) {
            FacesUtil.addError("Atenção!", "O Tipo de sindicato deve ser informado!");
            return false;
        }

        if (itemSindicato.getTipoValorItemSindicato() == null) {
            FacesUtil.addError("Atenção!", "O Tipo de valor do sindicato deve ser informado!");
            return false;
        }

        if (itemSindicato.getInicioVigencia() == null) {
            FacesUtil.addError("Atenção !", "A Data inicial da vigência deve ser informada !");
            return false;
        }

        if (itemSindicato.getFinalVigencia() != null) {
            if (itemSindicato.getInicioVigencia().after(itemSindicato.getFinalVigencia())) {
                FacesUtil.addError("Atenção !", "A Data inicial da vigência não pode ser superior a data final de vigência");
                return false;
            }
        }

        boolean validou = true;
        for (ItemSindicato item : selecionado.getItensSindicatos()) {
            if (((item.getTipoItemSindicato() == itemSindicato.getTipoItemSindicato())) && (item.getTipoValorItemSindicato() == itemSindicato.getTipoValorItemSindicato())) {
                if (item.getFinalVigencia() != null) {
                    if (itemSindicato.getInicioVigencia().before(item.getFinalVigencia())
                        || itemSindicato.getInicioVigencia().before(item.getInicioVigencia())) {
                        FacesUtil.addError("Atenção", " A Data de Início da Vigência é inválida para este item !");
                        validou = false;
                    }

                    if (itemSindicato.getFinalVigencia() != null) {
                        if (itemSindicato.getFinalVigencia().before(item.getFinalVigencia())) {
                            FacesUtil.addError("Atenção", " A Data Final de Vigência é inválida para este item !");
                            validou = false;
                        }
                    }
                }
            }
        }

        return validou;
    }

    public Boolean validaCampos() {
        if (selecionado.getMesDataBase() == 2) {
            if (selecionado.getDiaDataBase() < 1 || selecionado.getDiaDataBase() > 29) {
                FacesUtil.addError("Atenção", " O Dia Data Base deve estar entre 1 e 29 !");
                return false;
            }
        } else if (selecionado.getDiaDataBase() < 1 || selecionado.getDiaDataBase() > 31) {
            FacesUtil.addError("Atenção", " O Dia Data Base deve estar entre 1 e 31 !");
            return false;
        }
        if (selecionado.getMesDataBase() < 1 || selecionado.getMesDataBase() > 12) {
            FacesUtil.addError("Atenção", " O Mês Data Base deve estar entre 1 e 12 !");
            return false;
        }
        return true;
    }
}
