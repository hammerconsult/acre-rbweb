/*
 * Codigo gerado automaticamente em Tue May 24 14:14:23 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SituacaoInventarioEstoque;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.Util;
import org.primefaces.context.RequestContext;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "itemInventarioEstoqueControlador")
@ViewScoped
public class ItemInventarioEstoqueControlador extends PrettyControlador<ItemInventarioEstoque> implements Serializable, CRUD {

    @EJB
    private ItemInventarioEstoqueFacade itemInventarioEstoqueFacade;
    @EJB
    private InventarioEstoqueFacade inventarioEstoqueFacade;
    @EJB
    private MaterialFacade materialFacade;
    @EJB
    private EntradaMaterialFacade entradaMaterialFacade;
    @EJB
    private SaidaMaterialFacade saidaMaterialFacade;
    @EJB
    private LocalEstoqueFacade localEstoqueFacade;
    @EJB
    private LoteMaterialFacade loteMaterialFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    private InventarioEstoque inventarioEstoqueSelecionado;
    private ItemInventarioEstoque itemInventarioEstoqueSelecionado;
    private Integer indiceSelecionado;
    private ItemInventarioEstoque itemInventarioEstoqueEncontrado;
    private EntradaInventarioMaterial entradaInventarioMaterial;
    private SaidaInventarioMaterial saidaInventarioMaterial;
    private List<ItemInventarioItemEntrada> itensInventarioItemEntrada;
    private List<ItemInventarioItemSaida> itensInventarioItemSaida;
    private LoteMaterial loteMaterialDoItemInventarioEncontrado;

    public ItemInventarioEstoqueControlador() {
        metadata = new EntidadeMetaData(ItemInventarioEstoque.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/inventario-estoque/";
    }

    @Override
    public Object getUrlKeyValue() {
        return inventarioEstoqueSelecionado.getId();
    }

    @Override
    public void salvar() {
        try {
            inventarioEstoqueFacade.salvar(inventarioEstoqueSelecionado);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Salvo com Sucesso", ""));
            redireciona();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public List<LoteMaterial> completarLoteMaterialEncontrado(String parte) {
        return loteMaterialFacade.buscarFiltrandoPorMaterial(parte.trim(), itemInventarioEstoqueEncontrado.getMaterial());
    }

    public LoteMaterial getLoteMaterialDoItemInventarioEncontrado() {
        return loteMaterialDoItemInventarioEncontrado;
    }

    public void setLoteMaterialDoItemInventarioEncontrado(LoteMaterial loteMaterialDoItemInventarioEncontrado) {
        this.loteMaterialDoItemInventarioEncontrado = loteMaterialDoItemInventarioEncontrado;
    }

    public Integer getIndiceSelecionado() {
        return indiceSelecionado;
    }

    public ItemInventarioEstoque getItemInventarioEstoqueEncontrado() {
        return itemInventarioEstoqueEncontrado;
    }

    public void setItemInventarioEstoqueEncontrado(ItemInventarioEstoque itemInventarioEstoqueEncontrado) {
        this.itemInventarioEstoqueEncontrado = itemInventarioEstoqueEncontrado;
    }

    public void setIndiceSelecionado(Integer indiceSelecionado) {
        this.indiceSelecionado = indiceSelecionado;
    }

    public InventarioEstoque getInventarioEstoqueSelecionado() {
        return inventarioEstoqueSelecionado;
    }

    public void setInventarioEstoqueSelecionado(InventarioEstoque inventarioEstoqueSelecionado) {
        this.inventarioEstoqueSelecionado = inventarioEstoqueSelecionado;
    }

    public ItemInventarioEstoque getItemInventarioEstoqueSelecionado() {
        return itemInventarioEstoqueSelecionado;
    }

    public void setItemInventarioEstoqueSelecionado(ItemInventarioEstoque itemInventarioEstoqueSelecionado) {
        this.itemInventarioEstoqueSelecionado = itemInventarioEstoqueSelecionado;
    }

    @Override
    public AbstractFacade getFacede() {
        return this.itemInventarioEstoqueFacade;
    }

    public void inventariar(InventarioEstoque ie) {
        operacao = Operacoes.EDITAR;
        inventarioEstoqueSelecionado = ie;
        carregarListasDependentes();
        if (inventarioEstoqueSelecionado.getItensInventarioEstoque() != null && !inventarioEstoqueSelecionado.getItensInventarioEstoque().isEmpty()) {
            itemInventarioEstoqueSelecionado = inventarioEstoqueSelecionado.getItensInventarioEstoque().get(0);
        }
        itemInventarioEstoqueEncontrado = null;
        indiceSelecionado = 1;
        entradaInventarioMaterial = null;
        saidaInventarioMaterial = null;
    }

    public void navegarParaItem() {
        itemInventarioEstoqueSelecionado = inventarioEstoqueSelecionado.getItensInventarioEstoque().get(this.indiceSelecionado - 1);
    }

    public void criarNovoItemInventarioEstoqueEncontrado() {
        this.itemInventarioEstoqueEncontrado = new ItemInventarioEstoque();
        this.itemInventarioEstoqueEncontrado.setInventarioEstoque(inventarioEstoqueSelecionado);
        this.itemInventarioEstoqueEncontrado.setQuantidadeEsperada(BigDecimal.ZERO);
        this.itemInventarioEstoqueEncontrado.setEncontrado(Boolean.TRUE);
        this.itemInventarioEstoqueEncontrado.setAjustado(Boolean.FALSE);
        this.itemInventarioEstoqueEncontrado.setDivergente(Boolean.FALSE);
    }

    public void adicionarMaterialEncontrado() {
        if (this.itemInventarioEstoqueEncontrado == null) {
            return;
        }

        if (!podeAdicionarItemInventario()) {
            return;
        }

        this.inventarioEstoqueSelecionado.getItensInventarioEstoque().add(this.itemInventarioEstoqueEncontrado);
        this.indiceSelecionado = this.inventarioEstoqueSelecionado.getItensInventarioEstoque().indexOf(this.itemInventarioEstoqueEncontrado) + 1;
        this.itemInventarioEstoqueSelecionado = this.itemInventarioEstoqueEncontrado;
        if (itemInventarioEstoqueEncontrado.getMaterial().getControleDeLote()) {
            LoteItemInventario lii = new LoteItemInventario();
            lii.setItemInventarioEstoque(itemInventarioEstoqueEncontrado);
            lii.setLoteMaterial(loteMaterialDoItemInventarioEncontrado);
            lii.setQuantidadeEsperada(BigDecimal.ZERO);
            lii.setQuantidadeConstatada(itemInventarioEstoqueEncontrado.getQuantidadeConstatada());
            itemInventarioEstoqueEncontrado.setLotesItemInventario(Util.adicionarObjetoEmLista(itemInventarioEstoqueEncontrado.getLotesItemInventario(), lii));
        }
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Material Adicionado.", "O material selecionado foi adicionado com sucesso na lista de materiais deste inventário."));
        itemInventarioEstoqueEncontradoToNull();
    }

    private boolean podeAdicionarItemInventario() {
        if (itemInventarioEstoqueEncontrado.getMaterial() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Material Não Informado.", "Por favor informe um material para prosseguir com a operação."));
            return false;
        }

        if (itemInventarioEstoqueEncontrado.getQuantidadeConstatada() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório não informado.", "Por favor, informe a quantidade encontrada."));
            return false;
        }

        if (itemInventarioEstoqueEncontrado.getFinanceiro() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório não informado.", "Por favor, informe o valor financeiro."));
            return false;
        }

        if (!localEstoqueFacade.materialPossuiVinculoComHierarquiaDoLocalEstoque(itemInventarioEstoqueEncontrado.getMaterial(), inventarioEstoqueSelecionado.getLocalEstoque())) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Erro de vinculo e permissão do material no Local de Estoque!",
                "Não é permitido realizar movimentações do material " + itemInventarioEstoqueEncontrado.getMaterial().getDescricao().toUpperCase() + " no local de estoque " + inventarioEstoqueSelecionado.getLocalEstoque().getDescricao().toUpperCase()));
            return false;
        }

        if (materialSelecionadoJaEstaNaLista()) {
            return false;
        }

        if (itemInventarioEstoqueEncontrado.getMaterial().getControleDeLote()) {
            if (loteMaterialDoItemInventarioEncontrado == null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Material requer controle de lote!",
                    "Por favor informe o lote do material para poder adicioná-lo."));
                return false;
            }

            if (Util.getDataHoraMinutoSegundoZerado(loteMaterialDoItemInventarioEncontrado.getValidade()).compareTo(Util.getDataHoraMinutoSegundoZerado(new Date())) < 0) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Lote vencido.",
                    "Não será possível adicionar o material neste lote pois o mesmo já está vencido."));
                return false;
            }
        }

        return true;
    }

    private boolean materialSelecionadoJaEstaNaLista() {
        if (itemInventarioEstoqueEncontrado == null || inventarioEstoqueSelecionado.getItensInventarioEstoque() == null) {
            return false;
        }

        for (ItemInventarioEstoque itemInventarioEstoque : inventarioEstoqueSelecionado.getItensInventarioEstoque()) {
            if (itemInventarioEstoque.getMaterial().equals(itemInventarioEstoqueEncontrado.getMaterial())) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Material Repetido.", "O material que você está tentando adicionar já está na lista para inventariar."));
                return true;
            }
        }
        return false;
    }

    public void itemInventarioEstoqueEncontradoToNull() {
        this.itemInventarioEstoqueEncontrado = null;
    }

    private ItemInventarioEstoque recuperarProximoItemSemQuantidadeConstatada() {
        for (ItemInventarioEstoque itemInventarioEstoque : inventarioEstoqueSelecionado.getItensInventarioEstoque()) {
            if (itemInventarioEstoque.getQuantidadeConstatada() == null || itemInventarioEstoque.getQuantidadeConstatada().compareTo(BigDecimal.ZERO) < 0 || itemInventarioEstoque.getQuantidadeConstatada().toString().trim().length() <= 0) {
                return itemInventarioEstoque;
            }
        }
        return null;
    }

    private void notificarUsuarioSobreErroQuantidadeConstatada() {
        this.indiceSelecionado = inventarioEstoqueSelecionado.getItensInventarioEstoque().indexOf(itemInventarioEstoqueSelecionado) + 1;

        if (itemInventarioEstoqueSelecionado.getMaterial().getControleDeLote()) {
            for (LoteItemInventario loteItemInventario : itemInventarioEstoqueSelecionado.getLotesItemInventario()) {
                if (loteItemInventario.getQuantidadeConstatada() == null || loteItemInventario.getQuantidadeConstatada().toString().trim().length() <= 0) {
                    FacesContext.getCurrentInstance().addMessage("Formulario:tabela-lote-itens-inventario", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "Por favor, informe a quantidade constatada de todos os lotes."));
                    break;
                }
            }
        } else {
            FacesContext.getCurrentInstance().addMessage("Formulario:quantidade-constatada", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "O campo quantidade constatada é obrigatório e deve ser maior ou igual a zero."));
            RequestContext.getCurrentInstance().execute("setaFoco('Formulario:tabelaItensInventario:" + (this.indiceSelecionado - 1) + ":quantidade-constatada');");
        }
        RequestContext.getCurrentInstance().update("Formulario:painel-itens-do-inventario");
    }

    private boolean validarTodosItensInventariados() {
        ItemInventarioEstoque itemInventarioEstoqueParaValidar = recuperarProximoItemSemQuantidadeConstatada();
        if (itemInventarioEstoqueParaValidar != null) {
            itemInventarioEstoqueSelecionado = itemInventarioEstoqueParaValidar;
            notificarUsuarioSobreErroQuantidadeConstatada();
            return false;
        }
        return true;
    }

    public void validarItensParaAjustesInventario() {
        if (!validarTodosItensInventariados()) {
            return;
        }
        RequestContext.getCurrentInstance().execute("ajusteDialog.show()");
    }

    public void ajustarItensDoInventario() {
        inventarioEstoqueSelecionado.setSituacaoInventario(SituacaoInventarioEstoque.FINALIZADO);
        if (inventarioEstoqueSelecionado.getEncerradoEm() == null) {
            inventarioEstoqueSelecionado.setEncerradoEm(new Date());
            inventarioEstoqueFacade.desbloquearEstoque(inventarioEstoqueSelecionado);
            inventarioEstoqueFacade.salvar(inventarioEstoqueSelecionado);
        }
        RequestContext.getCurrentInstance().execute("ajusteDialog.hide()");
        redireciona();
    }

    public void consolidarQuantidadeConstatada() {
        BigDecimal acumulado = BigDecimal.ZERO;
        for (LoteItemInventario lote : itemInventarioEstoqueSelecionado.getLotesItemInventario()) {
            if (lote.getQuantidadeConstatada() == null) {
                acumulado = acumulado.add(BigDecimal.ZERO);
            } else {
                acumulado = acumulado.add(lote.getQuantidadeConstatada());
            }
        }
        itemInventarioEstoqueSelecionado.setQuantidadeConstatada(acumulado);
    }

    public BigDecimal calcularValorFinanceiroConstatado(ItemInventarioEstoque itemInventarioEstoque) {
        return itemInventarioEstoqueFacade.calcularValorFinanceiroConstatado(itemInventarioEstoque);
    }

    public BigDecimal calcularValorFinanceiroEsperado(ItemInventarioEstoque itemInventarioEstoque) {
        return itemInventarioEstoqueFacade.calcularValorFinanceiroEsperado(itemInventarioEstoque);
    }

    private void carregarListasDependentes() {
        inventarioEstoqueSelecionado.setItensInventarioEstoque(inventarioEstoqueFacade.recuperarItensInventario(inventarioEstoqueSelecionado));
        for (ItemInventarioEstoque itemInventarioEstoque : inventarioEstoqueSelecionado.getItensInventarioEstoque()) {
            itemInventarioEstoque.setLotesItemInventario(inventarioEstoqueFacade.recuperarLotesItemInventario(itemInventarioEstoque));
        }
    }

    public void loteMaterialDoItemInventarioEncontradoToNull() {
        loteMaterialDoItemInventarioEncontrado = null;
    }

    public void atribuirItemTabelaParaItemSelecionado(ItemInventarioEstoque itemInventarioEstoque) {
        this.itemInventarioEstoqueSelecionado = itemInventarioEstoque;
    }
}
