package br.com.webpublico.controle;

import br.com.webpublico.entidades.BensImoveis;
import br.com.webpublico.entidades.GrupoBem;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.TransfBensImoveis;
import br.com.webpublico.enums.*;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.TransfBensImoveisFacade;
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

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 17/02/14
 * Time: 15:35
 * To change this template use File | Settings | File Templates.
 */


@ManagedBean(name = "transfBensImoveisControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-transf-bens-imoveis", pattern = "/contabil/transferencia-bens-imoveis/novo/", viewId = "/faces/financeiro/orcamentario/bens/transfbensimoveis/edita.xhtml"),
    @URLMapping(id = "editar-transf-bens-imoveis", pattern = "/contabil/transferencia-bens-imoveis/editar/#{transfBensImoveisControlador.id}/", viewId = "/faces/financeiro/orcamentario/bens/transfbensimoveis/edita.xhtml"),
    @URLMapping(id = "ver-transf-bens-imoveis", pattern = "/contabil/transferencia-bens-imoveis/ver/#{transfBensImoveisControlador.id}/", viewId = "/faces/financeiro/orcamentario/bens/transfbensimoveis/visualizar.xhtml"),
    @URLMapping(id = "listar-transf-bens-imoveis", pattern = "/contabil/transferencia-bens-imoveis/listar/", viewId = "/faces/financeiro/orcamentario/bens/transfbensimoveis/lista.xhtml"),
})
public class TransfBensImoveisControlador extends PrettyControlador<TransfBensImoveis> implements Serializable, CRUD {

    @EJB
    private TransfBensImoveisFacade transfBensImoveisFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConverterAutoComplete converterUnidade;
    private ConverterAutoComplete converterGrupoBem;
    private MoneyConverter moneyConverter;
    private HierarquiaOrganizacional hierarquiaOrganizacionalOrigem;
    private HierarquiaOrganizacional hierarquiaOrganizacionalDestino;

    public TransfBensImoveisControlador() {
        super(TransfBensImoveis.class);
    }

    public TransfBensImoveisFacade getFacade() {
        return transfBensImoveisFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return transfBensImoveisFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/contabil/transferencia-bens-imoveis/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-transf-bens-imoveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataTransferencia(sistemaControlador.getDataOperacao());
        getSelecionado().setExercicio(sistemaControlador.getExercicioCorrente());
        if (transfBensImoveisFacade.getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
            FacesUtil.addWarn("Aviso! ", transfBensImoveisFacade.getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
        }
    }

    @URLAction(mappingId = "ver-transf-bens-imoveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditarVer();
    }

    @URLAction(mappingId = "editar-transf-bens-imoveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditarVer();
    }

    public void recuperaEditarVer() {
        selecionado = transfBensImoveisFacade.recuperar(selecionado.getId());
        if (selecionado.getUnidadeOrigem() != null) {
            hierarquiaOrganizacionalOrigem = transfBensImoveisFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(selecionado.getDataTransferencia(), selecionado.getUnidadeOrigem(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
        }
        if (selecionado.getUnidadeDestino() != null) {
            hierarquiaOrganizacionalDestino = transfBensImoveisFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(selecionado.getDataTransferencia(), selecionado.getUnidadeDestino(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
        }
    }

    @Override
    public void salvar() {
        try {
            if (Util.validaCampos(selecionado) && validarCampos()) {
                definirUnidadeOrigem();
                definirUnidadeDestino();
                if (operacao.equals(Operacoes.NOVO)) {
                    transfBensImoveisFacade.salvarNovo(selecionado);
                    FacesUtil.addOperacaoRealizada(" Registro salvo com sucesso. ");
                } else {
                    transfBensImoveisFacade.salvar(selecionado);
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
            if (TipoOperacaoBensImoveis.TRANSFERENCIA_BENS_IMOVEIS_CONCEDIDA.equals(selecionado.getOperacaoOrigem())) {
                selecionado.setOperacaoDestino(TipoOperacaoBensImoveis.TRANSFERENCIA_BENS_IMOVEIS_RECEBIDA);
            } else if (TipoOperacaoBensImoveis.TRANSFERENCIA_BENS_IMOVEIS_DEPRECIACAO_CONCEDIDA.equals(selecionado.getOperacaoOrigem())) {
                selecionado.setOperacaoDestino(TipoOperacaoBensImoveis.TRANSFERENCIA_BENS_IMOVEIS_DEPRECIACAO_RECEBIDA);
            } else if (TipoOperacaoBensImoveis.TRANSFERENCIA_BENS_IMOVEIS_AMORTIZACAO_CONCEDIDA.equals(selecionado.getOperacaoOrigem())) {
                selecionado.setOperacaoDestino(TipoOperacaoBensImoveis.TRANSFERENCIA_BENS_IMOVEIS_AMORTIZACAO_RECEBIDA);
            }
            selecionado.setEventoContabilDestino(null);
            selecionado.setEventoContabilOrigem(null);
            definirEventoOrigem();
            definirEventoDestino();
        }
    }

    public void definirEventoDestino() {
        try {
            transfBensImoveisFacade.buscarEventoDestino(selecionado);
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    private void definirEventoOrigem() {
        try {
            transfBensImoveisFacade.buscarEventoOrigem(selecionado);
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public void definirUnidadeOrigem() {
        if (hierarquiaOrganizacionalOrigem.getSubordinada() != null) {
            selecionado.setUnidadeOrigem(hierarquiaOrganizacionalOrigem.getSubordinada());
        }
    }

    public void definirUnidadeDestino() {
        if (hierarquiaOrganizacionalDestino.getSubordinada() != null) {
            selecionado.setUnidadeDestino(hierarquiaOrganizacionalDestino.getSubordinada());
        }
    }

    public boolean validarCampos() {
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

    public boolean getVerificaEditar() {
        if (operacao.equals(Operacoes.EDITAR)) {
            return true;
        }
        return false;
    }

    public List<SelectItem> getListaTipoLancamento() {
        List<SelectItem> lista = new ArrayList<>();
        for (TipoLancamento lanc : TipoLancamento.values()) {
            lista.add(new SelectItem(lanc, lanc.getDescricao()));
        }
        return lista;
    }

    public List<SelectItem> getListaTipoGrupoBem() {
        BensImoveis bensImoveis = new BensImoveis();
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoGrupo object : TipoGrupo.values()) {
            if (object.getClasseDeUtilizacao() == bensImoveis.getClass()) {
                toReturn.add(new SelectItem(object, object.getDescricao()));
            }
        }
        return toReturn;
    }

    public List<SelectItem> getOperacoesBensImoveisOrigem() {
        return montarSelectPelosEnums(TipoOperacaoBensImoveis.TRANSFERENCIA_BENS_IMOVEIS_CONCEDIDA,
            TipoOperacaoBensImoveis.TRANSFERENCIA_BENS_IMOVEIS_DEPRECIACAO_CONCEDIDA,
            TipoOperacaoBensImoveis.TRANSFERENCIA_BENS_IMOVEIS_AMORTIZACAO_CONCEDIDA);
    }


    public List<SelectItem> getOperacoesBensImoveisDestino() {
        return montarSelectPelosEnums(TipoOperacaoBensImoveis.TRANSFERENCIA_BENS_IMOVEIS_RECEBIDA,
            TipoOperacaoBensImoveis.TRANSFERENCIA_BENS_IMOVEIS_DEPRECIACAO_RECEBIDA,
            TipoOperacaoBensImoveis.TRANSFERENCIA_BENS_IMOVEIS_AMORTIZACAO_RECEBIDA);
    }

    private List<SelectItem> montarSelectPelosEnums(TipoOperacaoBensImoveis... tipos) {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        for (TipoOperacaoBensImoveis tipo : tipos) {
            toReturn.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return toReturn;
    }

    public List<HierarquiaOrganizacional> completaUnidadeOrganizacional(String parte) {
        try {
            return transfBensImoveisFacade.getHierarquiaOrganizacionalFacade().filtraPorNivel(parte.trim(), "3", "ORCAMENTARIA", sistemaControlador.getDataOperacao());
        } catch (Exception ex) {
            return new ArrayList<>();
        }
    }

    public List<GrupoBem> completaGrupoBemImoveis(String parte) {
        return transfBensImoveisFacade.getGrupoBemFacade().buscarGrupoBemPorTipoBem(parte, TipoBem.IMOVEIS);
    }


    public ConverterAutoComplete getConverterUnidade() {
        if (converterUnidade == null) {
            return converterUnidade = new ConverterAutoComplete(HierarquiaOrganizacional.class, transfBensImoveisFacade.getHierarquiaOrganizacionalFacade());
        }
        return converterUnidade;
    }

    public ConverterAutoComplete getConverterGrupoBem() {
        if (converterGrupoBem == null) {
            return converterGrupoBem = new ConverterAutoComplete(GrupoBem.class, transfBensImoveisFacade.getGrupoBemFacade());
        }
        return converterGrupoBem;
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
