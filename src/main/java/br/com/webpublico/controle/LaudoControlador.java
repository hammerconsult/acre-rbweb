/*
 * Codigo gerado automaticamente em Wed Feb 08 11:13:38 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.TipoLaudo;
import br.com.webpublico.enums.TipoNaturezaAtividadeFP;
import br.com.webpublico.enums.TipoResponsavel;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.PercentualConverter;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.context.RequestContext;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "laudoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoLaudo", pattern = "/rh/laudo/novo/", viewId = "/faces/rh/administracaodepagamento/laudo/edita.xhtml"),
        @URLMapping(id = "listaLaudo", pattern = "/rh/laudo/listar/", viewId = "/faces/rh/administracaodepagamento/laudo/lista.xhtml"),
        @URLMapping(id = "verLaudo", pattern = "/rh/laudo/ver/#{laudoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/laudo/visualizar.xhtml"),
        @URLMapping(id = "editarLaudo", pattern = "/rh/laudo/editar/#{laudoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/laudo/edita.xhtml"),
})
public class LaudoControlador extends PrettyControlador<Laudo> implements Serializable, CRUD {

    @EJB
    private LaudoFacade laudoFacade;
    @EJB
    private NaturezaAtividadeFPFacade naturezaAtividadeFPFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    private ConverterAutoComplete converterPessoaFisica;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private ItemLaudo itemLaudo;
    private ItemLaudo itemLaudoSelecionado;
    private ValorUnidadeOrganizacional itemValorUnidadeOrganizacional;
    @EJB
    private ItemLaudoFacade itemLaudoFacade;
    private PercentualConverter percentualConverter;

    public LaudoControlador() {
        super(Laudo.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return laudoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/rh/laudo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public List<SelectItem> getTiposLaudo() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoLaudo object : TipoLaudo.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoResponsaveis() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoResponsavel object : TipoResponsavel.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTipoNaturezaAtividade() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (TipoNaturezaAtividadeFP object : TipoNaturezaAtividadeFP.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    @URLAction(mappingId = "novoLaudo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        try {
            super.novo();
            itemLaudo = new ItemLaudo();
            itemValorUnidadeOrganizacional = new ValorUnidadeOrganizacional();

            if (isSessao()) {
                Web.pegaTodosFieldsNaSessao(this);
            }
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage());
        }
    }

    @URLAction(mappingId = "editarLaudo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        if (itemLaudo == null) {
            itemLaudo = new ItemLaudo();
        }
        if (itemValorUnidadeOrganizacional == null) {
            itemValorUnidadeOrganizacional = new ValorUnidadeOrganizacional();
        }
    }

    @URLAction(mappingId = "verLaudo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public void addValorUnidadeOrganizacional() {
        if (hierarquiaOrganizacional == null) {
            FacesUtil.addWarn("Atenção!", "Por favor, selecione uma unidade organizacional!");
            return;
        }
        if (itemValorUnidadeOrganizacional.getValor() != null && itemValorUnidadeOrganizacional.getTipoNaturezaAtividadeFP() != null) {
            if ((itemValorUnidadeOrganizacional.getValor().compareTo(BigDecimal.ZERO)) == 0) {
                FacesUtil.addWarn("Atenção!", "Por favor, informe um valor maior que zero!");
                return;
            }

            itemValorUnidadeOrganizacional.setUnidadeOrganizacional(hierarquiaOrganizacional.getSubordinada());
            itemValorUnidadeOrganizacional.setDataRegistro(new Date());
            if (naoPossuiMesmaNaturezaAtividade(itemLaudoSelecionado, itemValorUnidadeOrganizacional)) {
                itemLaudoSelecionado.getValoresUnidadesOrganizacionals().add(itemValorUnidadeOrganizacional);
                itemValorUnidadeOrganizacional = new ValorUnidadeOrganizacional();
                hierarquiaOrganizacional = new HierarquiaOrganizacional();
            } else {
                FacesUtil.addWarn("Atenção!", "Já existe um tipo de natureza de atividade salvo para a uma mesma unidade organizacional.");
                return;
            }
        } else {
            FacesUtil.addWarn("Atenção!", "Há campos obrigatórios sem preenchimento.");
            return;
        }
        RequestContext.getCurrentInstance().execute("dialogo.hide()");
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public Boolean validaCampos() {
        boolean valida = true;

        for (ItemLaudo item : selecionado.getItensLaudos()) {
            if (item.getValoresUnidadesOrganizacionals().isEmpty()) {
                FacesUtil.addWarn("Atenção !", "O Responsável " + item + " não possui nenhum valor informado ! ");
                valida = false;
            }
        }

        if (selecionado.getInicioVigencia() != null && selecionado.getFimVigencia() != null) {
            if (selecionado.getInicioVigencia().after(selecionado.getFimVigencia())) {
                FacesUtil.addWarn("Atenção", "Data de Início da Vigência maior que data final da vigência.");
                valida = false;
            }
        }

        return valida;
    }

    public boolean temVigenciaAberta() {
        Laudo laudo = laudoFacade.find(selecionado.getNumeroLaudo());
        if (laudo == null) {
            return true;
        }
        if (selecionado.getInicioVigencia().before(laudo.getFimVigencia())) {
            return false;
        }
        return true;
    }

    @Override
    public void salvar() {
        if (validaCampos() == true) {
            if (operacao == Operacoes.NOVO) {
                if (!temVigenciaAberta()) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "Já existe um laudo vigente com o mesmo numero de identificação (Número do Laudo)."));
                    return;
                }
            }
            super.salvar();
        }
    }

    public void addItemLaudo() {
        if (validaCamposItemLaudo()) {
            for (ItemLaudo item : selecionado.getItensLaudos()) {
                if ((item.getTipoLaudo() == itemLaudo.getTipoLaudo())) {
                    FacesUtil.addWarn("Atenção !", "Já existe um item do laudo com o mesmo Tipo !");
                    return;
                }
            }

            itemLaudo.setLaudo(selecionado);
            selecionado.getItensLaudos().add(itemLaudo);
            instanciaItemLaudo();
        }
    }

    public void removeItemLaudo(ActionEvent e) {
        itemLaudo = (ItemLaudo) e.getComponent().getAttributes().get("obj");
        selecionado.getItensLaudos().remove(itemLaudo);
        instanciaItemLaudo();
    }

    private void instanciaItemLaudo() {
        itemLaudo = new ItemLaudo();
        itemLaudo.setValoresUnidadesOrganizacionals(new ArrayList<ValorUnidadeOrganizacional>());
    }

    public List<PessoaFisica> completaPessoaFisica(String parte) {
        return pessoaFisicaFacade.listaFiltrando(parte.trim(), "nome", "cpf");
    }

    public Converter getConverterPessoaFisica() {
        if (converterPessoaFisica == null) {
            converterPessoaFisica = new ConverterAutoComplete(PessoaFisica.class, pessoaFisicaFacade);
        }
        return converterPessoaFisica;
    }

    public ItemLaudo getItemLaudo() {
        return itemLaudo;
    }

    public void setItemLaudo(ItemLaudo itemLaudo) {
        this.itemLaudo = itemLaudo;
    }

    public ValorUnidadeOrganizacional getItemValorUnidadeOrganizacional() {
        return itemValorUnidadeOrganizacional;
    }

    public void setItemValorUnidadeOrganizacional(ValorUnidadeOrganizacional itemValorUnidadeOrganizacional) {
        this.itemValorUnidadeOrganizacional = itemValorUnidadeOrganizacional;
    }

    public List<ValorUnidadeOrganizacional> valoresUnidadesOrganizacionais(ItemLaudo itemLaudo) {
        itemLaudoSelecionado = itemLaudo;
        return itemLaudo.getValoresUnidadesOrganizacionals();
    }

    public void removeValorUnidadeOrganizacional(ItemLaudo item, ValorUnidadeOrganizacional valor) {
        item.getValoresUnidadesOrganizacionals().remove(valor);

    }

    public ItemLaudo getItemLaudoSelecionado() {
        return itemLaudoSelecionado;
    }

    public void setItemLaudoSelecionado(ItemLaudo itemLaudoSelecionado) {
        this.itemLaudoSelecionado = itemLaudoSelecionado;
    }

    public boolean naoPossuiMesmaNaturezaAtividade(ItemLaudo itemLaudoSelecionado, ValorUnidadeOrganizacional itemValorUnidadeOrganizacional) {
        for (ValorUnidadeOrganizacional valor : itemLaudoSelecionado.getValoresUnidadesOrganizacionals()) {
            if ((valor.getTipoNaturezaAtividadeFP().equals(itemValorUnidadeOrganizacional.getTipoNaturezaAtividadeFP()))
                    && (valor.getUnidadeOrganizacional().equals(itemValorUnidadeOrganizacional.getUnidadeOrganizacional()))) {
                return false;
            }
        }
        return true;
    }

    public void associa(ItemLaudo i) {
        itemLaudoSelecionado = i;
        hierarquiaOrganizacional = new HierarquiaOrganizacional();
        itemValorUnidadeOrganizacional.setItemLaudo(i);
        hierarquiaOrganizacional = new HierarquiaOrganizacional();
        hierarquiaOrganizacional.setExercicio(getSistemaControlador().getExercicioCorrente());
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    private boolean validaCamposItemLaudo() {
        boolean valida = true;

        if (itemLaudo.getDescricao().trim().isEmpty()) {
            FacesUtil.addWarn("Atenção!", "O Campo descrição é obrigatório !");
            valida = false;
        }

        if (itemLaudo.getResponsavelLaudo() == null) {
            FacesUtil.addWarn("Atenção!", "O Campo responsável pelo laudo é obrigatório !");
            valida = false;
        }
        return valida;
    }

    public PercentualConverter getPercentualConverter() {
        if (percentualConverter == null) {
            percentualConverter = new PercentualConverter();
        }
        return percentualConverter;
    }

    public void poeNaSessao() {
        Web.poeNaSessao(selecionado);
        Web.poeTodosFieldsNaSessao(this);
    }
}
