package br.com.webpublico.controle;

import br.com.webpublico.entidades.BensMoveis;
import br.com.webpublico.entidades.GrupoBem;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.TransfBensMoveis;
import br.com.webpublico.enums.*;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.TransfBensMoveisFacade;
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


@ManagedBean(name = "transfBensMoveisControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-transf-bens-moveis", pattern = "/contabil/transferencia-bens-moveis/novo/", viewId = "/faces/financeiro/orcamentario/bens/transfbensmoveis/edita.xhtml"),
    @URLMapping(id = "editar-transf-bens-moveis", pattern = "/contabil/transferencia-bens-moveis/editar/#{transfBensMoveisControlador.id}/", viewId = "/faces/financeiro/orcamentario/bens/transfbensmoveis/edita.xhtml"),
    @URLMapping(id = "ver-transf-bens-moveis", pattern = "/contabil/transferencia-bens-moveis/ver/#{transfBensMoveisControlador.id}/", viewId = "/faces/financeiro/orcamentario/bens/transfbensmoveis/visualizar.xhtml"),
    @URLMapping(id = "listar-transf-bens-moveis", pattern = "/contabil/transferencia-bens-moveis/listar/", viewId = "/faces/financeiro/orcamentario/bens/transfbensmoveis/lista.xhtml"),
})
public class TransfBensMoveisControlador extends PrettyControlador<TransfBensMoveis> implements Serializable, CRUD {

    @EJB
    private TransfBensMoveisFacade transfBensMoveisFacade;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;
    private ConverterAutoComplete converterUnidade;
    private ConverterAutoComplete converterGrupoBem;
    private MoneyConverter moneyConverter;
    private HierarquiaOrganizacional hierarquiaOrganizacionalOrigem;
    private HierarquiaOrganizacional hierarquiaOrganizacionalDestino;

    public TransfBensMoveisControlador() {
        super(TransfBensMoveis.class);
    }

    public TransfBensMoveisFacade getFacade() {
        return transfBensMoveisFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return transfBensMoveisFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/contabil/transferencia-bens-moveis/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-transf-bens-moveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setDataTransferencia(sistemaControlador.getDataOperacao());
        selecionado.setExercicio(sistemaControlador.getExercicioCorrente());
        if (transfBensMoveisFacade.getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
            FacesUtil.addWarn("Aviso! ", transfBensMoveisFacade.getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
        }
    }

    @URLAction(mappingId = "ver-transf-bens-moveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditarVer();
    }

    @URLAction(mappingId = "editar-transf-bens-moveis", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditarVer();
    }

    public void recuperaEditarVer() {
        selecionado = transfBensMoveisFacade.recuperar(selecionado.getId());
        if (selecionado.getUnidadeOrigem() != null) {
            hierarquiaOrganizacionalOrigem = transfBensMoveisFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(selecionado.getDataTransferencia(), selecionado.getUnidadeOrigem(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
        }
        if (selecionado.getUnidadeDestino() != null) {
            hierarquiaOrganizacionalDestino = transfBensMoveisFacade.getHierarquiaOrganizacionalFacade().getHierarquiaOrganizacionalPorUnidade(selecionado.getDataTransferencia(), selecionado.getUnidadeDestino(), TipoHierarquiaOrganizacional.ORCAMENTARIA);
        }
    }

    @Override
    public void salvar() {
        try {
            if (Util.validaCampos(selecionado) && validaCampos()) {
                definirUnidadeOrigem();
                definirUnidadeDestino();
                if (operacao.equals(Operacoes.NOVO)) {
                    transfBensMoveisFacade.salvarNovo(selecionado);
                    FacesUtil.addOperacaoRealizada(" Registro salvo com sucesso. ");
                } else {
                    transfBensMoveisFacade.salvar(selecionado);
                    FacesUtil.addOperacaoRealizada(" Registro alterado com sucesso. ");
                }
                redireciona();
            }
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoPermitida(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            FacesUtil.addOperacaoNaoRealizada(" Não foi possível salvar. Detalhes do erro: " + ex.getMessage() + ". Contate o Suporte.");
        }
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

    public List<SelectItem> getOperacoesBensMoveisOrigem() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        for (TipoOperacaoBensMoveis tipoOperacaoBensMoveis : TipoOperacaoBensMoveis.getOperacoesTransferenciaConcedida()) {
            toReturn.add(new SelectItem(tipoOperacaoBensMoveis, tipoOperacaoBensMoveis.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getOperacoesBensMoveisDestino() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, " "));
        for (TipoOperacaoBensMoveis tipoOperacaoBensMoveis : TipoOperacaoBensMoveis.getOperacoesTransferenciaRecebida()) {
            toReturn.add(new SelectItem(tipoOperacaoBensMoveis, tipoOperacaoBensMoveis.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getListaTipoGrupoBem() {
        BensMoveis bensMoveis = new BensMoveis();
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoGrupo object : TipoGrupo.values()) {
            if (object.getClasseDeUtilizacao() == bensMoveis.getClass()) {
                toReturn.add(new SelectItem(object, object.getDescricao()));
            }
        }
        return Util.ordenaSelectItem(toReturn);
    }

    public List<HierarquiaOrganizacional> completarUnidadeOrganizacional(String parte) {
        try {
            return transfBensMoveisFacade.getHierarquiaOrganizacionalFacade().filtraPorNivel(parte.trim(), "3", "ORCAMENTARIA", sistemaControlador.getDataOperacao());
        } catch (Exception ex) {
            return new ArrayList<>();
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

    public List<GrupoBem> completaGrupoBemMoveis(String parte) {
        return transfBensMoveisFacade.getGrupoBemFacade().buscarGrupoBemPorTipoBem(parte, TipoBem.MOVEIS);
    }

    public void definirOperacaoRecuperandoEvento() {
        if (selecionado.getOperacaoOrigem() != null) {
            switch (selecionado.getOperacaoOrigem()) {
                case TRANFERENCIABENS_MOVEIS_CONCEDIDA:
                    selecionado.setOperacaoDestino(TipoOperacaoBensMoveis.TRANFERENCIA_BENS_MOVEIS_RECEBIDA);
                    break;
                case TRANFERENCIABENS_MOVEIS_DEPRECIACAO_CONCEDIDA:
                    selecionado.setOperacaoDestino(TipoOperacaoBensMoveis.TRANFERENCIABENS_MOVEIS_DEPRECIACAO_RECEBIDA);
                    break;
                case TRANFERENCIABENS_MOVEIS_AMORTIZACAO_CONCEDIDA:
                    selecionado.setOperacaoDestino(TipoOperacaoBensMoveis.TRANFERENCIABENS_MOVEIS_AMORTIZACAO_RECEBIDA);
                    break;
                case TRANFERENCIABENS_MOVEIS_EXAUSTAO_CONCEDIDA:
                    selecionado.setOperacaoDestino(TipoOperacaoBensMoveis.TRANFERENCIABENS_MOVEIS_EXAUSTAO_RECEBIDA);
                    break;
                case TRANFERENCIABENS_MOVEIS_REDUCAO_CONCEDIDA:
                    selecionado.setOperacaoDestino(TipoOperacaoBensMoveis.TRANFERENCIABENS_MOVEIS_REDUCAO_RECEBIDA);
                    break;
            }
            selecionado.setEventoContabilDestino(null);
            selecionado.setEventoContabilOrigem(null);
            definirEventoOrigem();
            definirEventoDestino();
        }
    }

    public void definirEventoDestino() {
        try {
            transfBensMoveisFacade.recuperaEventoDestino(selecionado);
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    private void definirEventoOrigem() {
        try {
            transfBensMoveisFacade.recuperaEventoOrigem(selecionado);
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public boolean validaCampos() {
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
            return converterUnidade = new ConverterAutoComplete(HierarquiaOrganizacional.class, transfBensMoveisFacade.getHierarquiaOrganizacionalFacade());
        }
        return converterUnidade;
    }

    public ConverterAutoComplete getConverterGrupoBem() {
        if (converterGrupoBem == null) {
            return converterGrupoBem = new ConverterAutoComplete(GrupoBem.class, transfBensMoveisFacade.getGrupoBemFacade());
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
