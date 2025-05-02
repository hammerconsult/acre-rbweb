package br.com.webpublico.controle;

import br.com.webpublico.entidades.GrupoBem;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.entidadesauxiliares.AssistenteConsultaMovimentacaoBemContabil;
import br.com.webpublico.entidadesauxiliares.MovimentacaoBemContabilGrupo;
import br.com.webpublico.entidadesauxiliares.MovimentacaoBemContabilMovimento;
import br.com.webpublico.entidadesauxiliares.MovimentacaoBemContabilUnidade;
import br.com.webpublico.enums.NaturezaTipoGrupoBem;
import br.com.webpublico.enums.TipoBem;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ConsultaMovimentacaoBemContabilFacade;
import br.com.webpublico.util.AsyncExecutor;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "mov-bem-cont", pattern = "/consulta-movimentacao-bem-contabil/", viewId = "/faces/administrativo/patrimonio/consulta-mov-bem-contabil/edita.xhtml")
})
public class ConsultaMovimentacaoBemContabilControlador implements Serializable {

    @EJB
    private ConsultaMovimentacaoBemContabilFacade facade;
    private AssistenteConsultaMovimentacaoBemContabil assistente;
    private CompletableFuture<AssistenteConsultaMovimentacaoBemContabil> futureConsulta;


    @URLAction(mappingId = "mov-bem-cont", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novaConsulta() {
        assistente = new AssistenteConsultaMovimentacaoBemContabil();
        assistente.setDataFinal(facade.getSistemaFacade().getDataOperacao());
        assistente.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
    }

    public void consultar() {
        try {
            validarConsulta();
            assistente.zerarContadoresProcesso();
            if (assistente.getDataInicial() == null) {
                assistente.setDataInicial(facade.getBemFacade().getDataPrimeiraAquisicao());
            }
            assistente.setMovimentacoes(Lists.newArrayList());
            futureConsulta = AsyncExecutor.getInstance().execute(assistente, () -> facade.buscarMovimentacaoUnidade(assistente));
            FacesUtil.executaJavaScript("iniciarFuture()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void finalizarFuture() {
        FacesUtil.atualizarComponente("Formulario");
    }

    public void acompanharFuture() throws ExecutionException, InterruptedException {
        if (futureConsulta != null && futureConsulta.isDone()) {
            assistente = futureConsulta.get();
            futureConsulta = null;
            if (assistente.getSomenteUnidadeComDiferenca()) {
                assistente.getMovimentacoes().removeIf(MovimentacaoBemContabilUnidade::isUnidadeConciliada);
            }
            FacesUtil.executaJavaScript("terminarFuture()");
        }
    }

    private void validarConsulta() {
        ValidacaoException ve = new ValidacaoException();
        if (assistente.getDataFinal() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data final/referência deve ser informado.");
        }
        if (assistente.getDataInicial() != null && assistente.getDataInicial().after(assistente.getDataFinal())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo data inicial deve anterior a data final/referência.");
        }
        ve.lancarException();
    }

    public List<GrupoBem> completarGrupoBem(String parte) {
        return facade.getGrupoBemFacade().buscarGrupoBemPorTipoBem(parte.trim(), TipoBem.MOVEIS);
    }

    public List<HierarquiaOrganizacional> completarHierarquiaAdministrativa(String parte) {
        return facade.getHierarquiaOrganizacionalFacade().buscarHierarquiaUsuarioPorTipoData(
            parte.trim(),
            facade.getSistemaFacade().getUsuarioCorrente(),
            facade.getSistemaFacade().getDataOperacao(),
            TipoHierarquiaOrganizacional.ADMINISTRATIVA);
    }

    public List<HierarquiaOrganizacional> completarHierarquiaOrcamentaria(String parte) {
        if (assistente.getHierarquiaAdministrativa() != null) {
            return facade.getHierarquiaOrganizacionalFacade().retornaHierarquiaOrcamentariaPorUnidadeAdministrativa(assistente.getHierarquiaAdministrativa().getSubordinada(), assistente.getDataFinal());
        }
        return facade.getHierarquiaOrganizacionalFacade().buscarHierarquiaUsuarioPorTipoData(
            parte.trim(),
            facade.getSistemaFacade().getUsuarioCorrente(),
            facade.getSistemaFacade().getDataOperacao(),
            TipoHierarquiaOrganizacional.ORCAMENTARIA);
    }

    public void listenerTipoConsulta() {
        if (assistente.getTipoConsulta().isDetalhado()) {
            assistente.setDataInicial(null);
        }
        if (hasMovimentacao()) {
            consultar();
        }
    }

    public void listenerUnidadeAdm(){
        assistente.setHierarquiaOrcamentaria(null);
    }

    public List<SelectItem> getTiposMovimentosGrupo() {
        return Util.getListSelectItemSemCampoVazio(AssistenteConsultaMovimentacaoBemContabil.TipoConsulta.values(), false);
    }

    public void expandirGrupo(MovimentacaoBemContabilUnidade mov, boolean expandir) {
        assistente.zerarContadoresProcesso();
        mov.setExpandirGrupo(expandir);
        if (mov.getExpandirGrupo() && !mov.hasGrupos()) {
            if (assistente.getDataInicial() == null) {
                assistente.setDataInicial(facade.getBemFacade().getDataPrimeiraAquisicao());
            }
            futureConsulta = AsyncExecutor.getInstance().execute(assistente, () -> facade.buscarMovimentacaoGrupo(assistente, mov));
            FacesUtil.executaJavaScript("iniciarFuture()");
        }
    }

    public void selecionarMovGrupoOriginalContabil(MovimentacaoBemContabilGrupo movGrupo, MovimentacaoBemContabilUnidade movUnid) {
        assistente.setMovGrupoSelecionado(movGrupo);
        assistente.getMovGrupoSelecionado().setTipoOperacao(MovimentacaoBemContabilGrupo.TipoOperacao.CONTABIL_ORIGINAL);
        movGrupo.setMovimentos(facade.buscarMovimentosGrupoOriginalContabil(assistente, movUnid.getHierarquiaOrcamentaria().getSubordinada()));
        movGrupo.setSaldoGrupo(buscarSaldoGrupo(movGrupo, NaturezaTipoGrupoBem.ORIGINAL, movUnid.getHierarquiaOrcamentaria().getSubordinada()));
        FacesUtil.executaJavaScript("dlgMovGrupo.show()");
        FacesUtil.atualizarComponente("formMovGrupo");

    }

    public void selecionarMovGrupoAjusteContabil(MovimentacaoBemContabilGrupo movGrupo, MovimentacaoBemContabilUnidade movUnid) {
        assistente.setMovGrupoSelecionado(movGrupo);
        assistente.getMovGrupoSelecionado().setTipoOperacao(MovimentacaoBemContabilGrupo.TipoOperacao.CONTABIL_AJUSTE);
        movGrupo.setMovimentos(facade.buscarMovimentosGrupoAjusteContabil(assistente, movUnid.getHierarquiaOrcamentaria().getSubordinada()));
        movGrupo.setSaldoGrupo(buscarSaldoGrupo(movGrupo, NaturezaTipoGrupoBem.DEPRECIACAO, movUnid.getHierarquiaOrcamentaria().getSubordinada()));
        FacesUtil.executaJavaScript("dlgMovGrupo.show()");
        FacesUtil.atualizarComponente("formMovGrupo");
    }

    public void selecionarMovAtualContabil(MovimentacaoBemContabilGrupo movGrupo, MovimentacaoBemContabilUnidade movUnid) {
        assistente.setMovGrupoSelecionado(movGrupo);
        assistente.getMovGrupoSelecionado().setTipoOperacao(MovimentacaoBemContabilGrupo.TipoOperacao.CONTABIL_CONCILIACAO);
        movGrupo.setMovimentos(Lists.newArrayList());

        movGrupo.getMovimentos().addAll(facade.buscarMovimentosGrupoOriginalContabil(assistente, movUnid.getHierarquiaOrcamentaria().getSubordinada()));
        movGrupo.getMovimentos().addAll(facade.buscarMovimentosGrupoAjusteContabil(assistente, movUnid.getHierarquiaOrcamentaria().getSubordinada()));

        BigDecimal credito = buscarSaldoGrupo(movGrupo, NaturezaTipoGrupoBem.DEPRECIACAO, movUnid.getHierarquiaOrcamentaria().getSubordinada());
        BigDecimal debito = buscarSaldoGrupo(movGrupo, NaturezaTipoGrupoBem.ORIGINAL, movUnid.getHierarquiaOrcamentaria().getSubordinada());
        movGrupo.setSaldoGrupo(credito.subtract(debito).abs());

        FacesUtil.executaJavaScript("dlgMovGrupo.show()");
        FacesUtil.atualizarComponente("formMovGrupo");
    }

    public void selecionarMovOriginalAdm(MovimentacaoBemContabilGrupo movGrupo, MovimentacaoBemContabilUnidade movUnid) {
        assistente.setMovGrupoSelecionado(movGrupo);
        assistente.getMovGrupoSelecionado().setTipoOperacao(MovimentacaoBemContabilGrupo.TipoOperacao.ADMINISTRATIVO_ORIGINAL);
        movGrupo.setMovimentos(facade.buscarMovimentosGrupoOriginalAdm(assistente, movUnid.getHierarquiaOrcamentaria().getSubordinada()));
        movGrupo.setSaldoGrupo(buscarSaldoGrupo(movGrupo, NaturezaTipoGrupoBem.ORIGINAL, movUnid.getHierarquiaOrcamentaria().getSubordinada()));
        FacesUtil.executaJavaScript("dlgMovGrupo.show()");
        FacesUtil.atualizarComponente("formMovGrupo");
    }

    public void selecionarMovAjusteAdm(MovimentacaoBemContabilGrupo movGrupo, MovimentacaoBemContabilUnidade movUnid) {
        assistente.setMovGrupoSelecionado(movGrupo);
        assistente.getMovGrupoSelecionado().setTipoOperacao(MovimentacaoBemContabilGrupo.TipoOperacao.ADMINISTRATIVO_AJUSTE);
        movGrupo.setMovimentos(facade.buscarMovimentosGrupoAjusteAdm(assistente, movUnid.getHierarquiaOrcamentaria().getSubordinada()));
        movGrupo.setSaldoGrupo(buscarSaldoGrupo(movGrupo, NaturezaTipoGrupoBem.DEPRECIACAO, movUnid.getHierarquiaOrcamentaria().getSubordinada()));
        FacesUtil.atualizarComponente("formMovGrupo");
        FacesUtil.executaJavaScript("dlgMovGrupo.show()");
    }

    public void selecionarMovAtualAdm(MovimentacaoBemContabilGrupo movGrupo, MovimentacaoBemContabilUnidade movUnid) {
        assistente.setMovGrupoSelecionado(movGrupo);
        assistente.getMovGrupoSelecionado().setTipoOperacao(MovimentacaoBemContabilGrupo.TipoOperacao.ADMINISTRATIVO_CONCILIACAO);
        movGrupo.setMovimentos(Lists.newArrayList());

        List<MovimentacaoBemContabilMovimento> movOriginal = facade.buscarMovimentosGrupoOriginalAdm(assistente, movUnid.getHierarquiaOrcamentaria().getSubordinada());
        movGrupo.getMovimentos().addAll(movOriginal);

        List<MovimentacaoBemContabilMovimento> movAjuste = facade.buscarMovimentosGrupoAjusteAdm(assistente, movUnid.getHierarquiaOrcamentaria().getSubordinada());
        movGrupo.getMovimentos().addAll(movAjuste);

        BigDecimal credito = buscarSaldoGrupo(movGrupo, NaturezaTipoGrupoBem.DEPRECIACAO, movUnid.getHierarquiaOrcamentaria().getSubordinada());
        BigDecimal debito = buscarSaldoGrupo(movGrupo, NaturezaTipoGrupoBem.ORIGINAL, movUnid.getHierarquiaOrcamentaria().getSubordinada());
        movGrupo.setSaldoGrupo(credito.subtract(debito).abs());

        FacesUtil.executaJavaScript("dlgMovGrupo.show()");
        FacesUtil.atualizarComponente("formMovGrupo");
    }

    private BigDecimal buscarSaldoGrupo(MovimentacaoBemContabilGrupo mov, NaturezaTipoGrupoBem natureza, UnidadeOrganizacional unidadeOrc) {
        return facade.buscarSaldoGrupo(mov.getGrupoBem(), natureza, unidadeOrc, assistente.getDataFinal());
    }

    public boolean hasMovimentacao() {
        return assistente != null && !Util.isListNullOrEmpty(assistente.getMovimentacoes());
    }

    public BigDecimal getValorTotalOriginalContabil() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasMovimentacao()) {
            for (MovimentacaoBemContabilUnidade mov : assistente.getMovimentacoes()) {
                total = total.add(mov.getValorOriginalContabil());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalAjusteContabil() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasMovimentacao()) {
            for (MovimentacaoBemContabilUnidade mov : assistente.getMovimentacoes()) {
                total = total.add(mov.getValorAjusteContabil());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalAtualContabil() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasMovimentacao()) {
            for (MovimentacaoBemContabilUnidade mov : assistente.getMovimentacoes()) {
                total = total.add(mov.getValorAtualContabil());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalOriginalAdm() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasMovimentacao()) {
            for (MovimentacaoBemContabilUnidade mov : assistente.getMovimentacoes()) {
                total = total.add(mov.getValorOriginalAdm());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalAjusteAdm() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasMovimentacao()) {
            for (MovimentacaoBemContabilUnidade mov : assistente.getMovimentacoes()) {
                total = total.add(mov.getValorAjusteAdm());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalAtualAdm() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasMovimentacao()) {
            for (MovimentacaoBemContabilUnidade mov : assistente.getMovimentacoes()) {
                total = total.add(mov.getValorAtualAdm());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalOriginalConciliacao() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasMovimentacao()) {
            for (MovimentacaoBemContabilUnidade mov : assistente.getMovimentacoes()) {
                total = total.add(mov.getValorOriginalConciliacao());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalAjusteConciliacao() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasMovimentacao()) {
            for (MovimentacaoBemContabilUnidade mov : assistente.getMovimentacoes()) {
                total = total.add(mov.getValorAjusteConciliacao());
            }
        }
        return total;
    }

    public BigDecimal getValorTotalAtualConciliacao() {
        BigDecimal total = BigDecimal.ZERO;
        if (hasMovimentacao()) {
            for (MovimentacaoBemContabilUnidade mov : assistente.getMovimentacoes()) {
                total = total.add(mov.getValorAtualConciliacao());
            }
        }
        return total;
    }

    public AssistenteConsultaMovimentacaoBemContabil getAssistente() {
        return assistente;
    }

    public void setAssistente(AssistenteConsultaMovimentacaoBemContabil assistente) {
        this.assistente = assistente;
    }
}
