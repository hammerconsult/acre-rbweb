/*
 * Codigo gerado automaticamente em Wed Sep 12 14:06:38 GMT-03:00 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.GrupoMaterial;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.TransferenciaBensEstoque;
import br.com.webpublico.enums.*;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.TransferenciaBensEstoqueFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.MoneyConverter;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "transferenciaBensEstoqueControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-transf-bens-estoque", pattern = "/contabil/transferencia-bens-estoque/novo/", viewId = "/faces/financeiro/orcamentario/bens/transferenciabensestoque/edita.xhtml"),
    @URLMapping(id = "editar-transf-bens-estoque", pattern = "/contabil/transferencia-bens-estoque/editar/#{transferenciaBensEstoqueControlador.id}/", viewId = "/faces/financeiro/orcamentario/bens/transferenciabensestoque/edita.xhtml"),
    @URLMapping(id = "ver-transf-bens-estoque", pattern = "/contabil/transferencia-bens-estoque/ver/#{transferenciaBensEstoqueControlador.id}/", viewId = "/faces/financeiro/orcamentario/bens/transferenciabensestoque/visualizar.xhtml"),
    @URLMapping(id = "listar-transf-bens-estoque", pattern = "/contabil/transferencia-bens-estoque/listar/", viewId = "/faces/financeiro/orcamentario/bens/transferenciabensestoque/lista.xhtml"),})

public class TransferenciaBensEstoqueControlador extends PrettyControlador<TransferenciaBensEstoque> implements Serializable, CRUD {

    @EJB
    private TransferenciaBensEstoqueFacade transferenciaBensEstoqueFacade;
    private ConverterAutoComplete converterGrupoMaterial;
    private ConverterAutoComplete converterUnidade;
    private MoneyConverter moneyConverter;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private HierarquiaOrganizacional hierarquiaOrganizacionalOrigem;
    private HierarquiaOrganizacional hierarquiaOrganizacionalDestino;

    public TransferenciaBensEstoqueControlador() {
        super(TransferenciaBensEstoque.class);
    }

    public TransferenciaBensEstoqueFacade getFacade() {
        return transferenciaBensEstoqueFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return transferenciaBensEstoqueFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/contabil/transferencia-bens-estoque/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-transf-bens-estoque", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataTransferencia(sistemaControlador.getDataOperacao());
        selecionado.setTipoLancamento(TipoLancamento.NORMAL);
        selecionado.setExercicio(sistemaControlador.getExercicioCorrente());
        if (transferenciaBensEstoqueFacade.getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
            FacesUtil.addWarn("Aviso! ", transferenciaBensEstoqueFacade.getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
        }
    }

    @URLAction(mappingId = "ver-transf-bens-estoque", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditarVer();
    }

    @URLAction(mappingId = "editar-transf-bens-estoque", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditarVer();
    }

    public void recuperaEditarVer() {
        selecionado = transferenciaBensEstoqueFacade.recuperar(selecionado.getId());
        if (selecionado.getUnidadeOrgOrigem() != null) {
            hierarquiaOrganizacionalOrigem = transferenciaBensEstoqueFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(selecionado.getDataTransferencia(), selecionado.getUnidadeOrgOrigem(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
        }
        if (selecionado.getUnidadeOrgDestino() != null) {
            hierarquiaOrganizacionalDestino = transferenciaBensEstoqueFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(selecionado.getDataTransferencia(), selecionado.getUnidadeOrgDestino(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
        }
    }

    public void salvar() {
        try {
            if (Util.validaCampos(selecionado) && verificaEvento()) {
                definirUnidadeOrigem();
                definirUnidadeDestino();
                if (operacao.equals(Operacoes.NOVO)) {
                    transferenciaBensEstoqueFacade.salvarNovo(selecionado);
                    FacesUtil.addOperacaoRealizada(" Registro salvo com sucesso. ");
                } else {
                    transferenciaBensEstoqueFacade.salvar(selecionado);
                    FacesUtil.addOperacaoRealizada(" Registro alterado com sucesso. ");
                }
                redireciona();
            }
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(" Não foi possível salvar. Detalhes do erro: " + ex.getMessage() + ". Contate o Suporte.");
        }
    }

    public void definirOperacaoRecuperandoEvento() {
        if (selecionado.getOperacaoOrigem() != null) {
            if (TipoOperacaoBensEstoque.TRANSFERENCIA_BENS_ESTOQUE_CONCEDIDA.equals(selecionado.getOperacaoOrigem())) {
                selecionado.setOperacaoDestino(TipoOperacaoBensEstoque.TRANSFERENCIA_BENS_ESTOQUE_RECEBIDA);
                selecionado.setEventoContabilDestino(null);
                selecionado.setEventoContabilOrigem(null);
                definirEventoOrigem();
                definirEventoDestino();
            }
        }
    }

    public void definirEventoDestino() {
        try {
            transferenciaBensEstoqueFacade.buscarEventoDestino(selecionado);
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    private void definirEventoOrigem() {
        try {
            transferenciaBensEstoqueFacade.buscarEventoOrigem(selecionado);
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public void definirUnidadeOrigem() {
        if (hierarquiaOrganizacionalOrigem.getSubordinada() != null) {
            selecionado.setUnidadeOrgOrigem(hierarquiaOrganizacionalOrigem.getSubordinada());
        }
    }

    public void definirUnidadeDestino() {
        if (hierarquiaOrganizacionalDestino.getSubordinada() != null) {
            selecionado.setUnidadeOrgDestino(hierarquiaOrganizacionalDestino.getSubordinada());
        }
    }

    public boolean getVerificaEditar() {
        if (operacao.equals(Operacoes.EDITAR)) {
            return true;
        } else {
            return false;
        }
    }

    public List<SelectItem> getListaTipoLancamento() {
        List<SelectItem> lista = new ArrayList<>();
        for (TipoLancamento lanc : TipoLancamento.values()) {
            lista.add(new SelectItem(lanc, lanc.getDescricao()));
        }
        return lista;
    }

    public List<SelectItem> getListaTipoEstoque() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoEstoque object : TipoEstoque.values()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getOperacoesBensEstoqueOrigem() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        toReturn.add(new SelectItem(TipoOperacaoBensEstoque.TRANSFERENCIA_BENS_ESTOQUE_CONCEDIDA, TipoOperacaoBensEstoque.TRANSFERENCIA_BENS_ESTOQUE_CONCEDIDA.getDescricao()));
        return Util.ordenaSelectItem(toReturn);
    }


    public List<SelectItem> getOperacoesBensEstoqueDestino() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(TipoOperacaoBensEstoque.TRANSFERENCIA_BENS_ESTOQUE_RECEBIDA, TipoOperacaoBensEstoque.TRANSFERENCIA_BENS_ESTOQUE_RECEBIDA.getDescricao()));
        return Util.ordenaSelectItem(toReturn);
    }

    public List<HierarquiaOrganizacional> completaUnidadeOrganizacional(String parte) {
        try {
            return transferenciaBensEstoqueFacade.getHierarquiaOrganizacionalFacade().filtraPorNivel(parte.trim(), "3", "ORCAMENTARIA", sistemaControlador.getDataOperacao());
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public List<GrupoMaterial> completaGrupoMaterial(String parte) {
        return transferenciaBensEstoqueFacade.getGrupoMaterialFacade().listaFiltrandoGrupoDeMaterialAtivo(parte.trim());
    }

    public boolean verificaEvento() {
        if (hierarquiaOrganizacionalOrigem == null) {
            FacesUtil.addCampoObrigatorio("O campo Unidade Organizacional de Origem deve ser informado.");
            return false;
        }
        if (hierarquiaOrganizacionalDestino == null) {
            FacesUtil.addCampoObrigatorio("O campo Unidade Organizacional de Destino deve ser informado.");
            return false;
        }
        if (selecionado.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            FacesUtil.addOperacaoNaoPermitida(" O campo valor deve ser maior que zero(0). ");
            return false;
        }
        if (selecionado.getEventoContabilOrigem() == null) {
            FacesUtil.addOperacaoNaoPermitida(" Não foi encontrado nenhum evento contábil de origem para a transferência.");
            return false;
        }
        if (selecionado.getEventoContabilDestino() == null) {
            FacesUtil.addOperacaoNaoPermitida(" Não foi encontrado nenhum evento contábil de destino para a transferência.");
            return false;
        }
        return true;
    }

    public ConverterAutoComplete getConverterUnidade() {
        if (converterUnidade == null) {
            return converterUnidade = new ConverterAutoComplete(HierarquiaOrganizacional.class, transferenciaBensEstoqueFacade.getHierarquiaOrganizacionalFacade());
        }
        return converterUnidade;
    }

    public ConverterAutoComplete getConverterGrupoMaterial() {
        if (converterGrupoMaterial == null) {
            return converterGrupoMaterial = new ConverterAutoComplete(GrupoMaterial.class, transferenciaBensEstoqueFacade.getGrupoMaterialFacade());
        }
        return converterGrupoMaterial;
    }

    public MoneyConverter getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverter();
        }
        return moneyConverter;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalOrigem() {
        return hierarquiaOrganizacionalOrigem;
    }

    public void setHierarquiaOrganizacionalOrigem(HierarquiaOrganizacional hierarquiaOrganizacionalOrigem) {
        this.hierarquiaOrganizacionalOrigem = hierarquiaOrganizacionalOrigem;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalDestino() {
        return hierarquiaOrganizacionalDestino;
    }

    public void setHierarquiaOrganizacionalDestino(HierarquiaOrganizacional hierarquiaOrganizacionalDestino) {
        this.hierarquiaOrganizacionalDestino = hierarquiaOrganizacionalDestino;
    }
}
