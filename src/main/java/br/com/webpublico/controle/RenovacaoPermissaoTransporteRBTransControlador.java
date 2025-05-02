package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteRenovacaoPermissaoTransporte;
import br.com.webpublico.entidadesauxiliares.FiltroRenovacaoPermissao;
import br.com.webpublico.entidadesauxiliares.VOPermissaoRenovacao;
import br.com.webpublico.enums.TipoPermissaoRBTrans;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.ImprimeDAM;
import br.com.webpublico.negocios.RenovacaoPermissaoFacade;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.util.AsyncExecutor;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by AndreGustavo on 08/09/2014.
 */
@ManagedBean(name = "renovacaoPermissaoTransporteRBTransControlador")
@SessionScoped
@URLMappings(
    mappings = {
        @URLMapping(id = "novaRenovacao", pattern = "/renovacao-permissao-transporte/", viewId = "/faces/tributario/rbtrans/renovacao/edita.xhtml"),
        @URLMapping(id = "acompanhaRenovacao", pattern = "/acomanhamento-renovacao-permissao-transporte/", viewId = "/faces/tributario/rbtrans/renovacao/acompanhamento.xhtml")
    }
)
public class RenovacaoPermissaoTransporteRBTransControlador extends PrettyControlador<RenovacaoPermissao> {
    @EJB
    private RenovacaoPermissaoFacade renovacaoPermissaoFacade;
    private FiltroRenovacaoPermissao filtroRenovacaoPermissao;
    private List<VOPermissaoRenovacao> permissoes;
    private Exercicio anoRenovacao;
    private ParametrosTransitoTransporte parametro;
    private CompletableFuture<List<AssistenteRenovacaoPermissaoTransporte>> futureRenovacoes;
    private boolean futureRenovacoesConcluida;
    private RenovacaoPermissaoFacade.AssistenteCalculoRenovacao assistenteCalculoRenovacao;

    @Override
    public AbstractFacade getFacede() {
        return renovacaoPermissaoFacade;
    }

    @Override
    @URLAction(mappingId = "novaRenovacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtroRenovacaoPermissao = new FiltroRenovacaoPermissao();
        permissoes = new ArrayList<>();
        limparFutureRenovacoes();
    }

    public List<SelectItem> getTiposPermissao() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoPermissaoRBTrans tipo : TipoPermissaoRBTrans.values()) {
            toReturn.add(new SelectItem(tipo.name(), tipo.getDescricao()));
        }
        return toReturn;
    }

    public void pesquisar() {
        if (validaConsulta()) {
            permissoes = renovacaoPermissaoFacade.consultaPermissoesParaRenovacao(filtroRenovacaoPermissao);
        }
    }

    public boolean validaConsulta() {
        boolean valida = true;
        if (filtroRenovacaoPermissao.getTipoPermissaoRBTrans() == null) {
            FacesUtil.addCampoObrigatorio("O Tipo de Permissão é um campo obrigatório.");
            valida = false;
        }

        return valida;
    }

    public FiltroRenovacaoPermissao getFiltroRenovacaoPermissao() {
        return filtroRenovacaoPermissao;
    }

    public void setFiltroRenovacaoPermissao(FiltroRenovacaoPermissao filtroRenovacaoPermissao) {
        this.filtroRenovacaoPermissao = filtroRenovacaoPermissao;
    }

    public List<VOPermissaoRenovacao> getPermissoes() {
        return permissoes;
    }

    public void setPermissoes(List<VOPermissaoRenovacao> permissoes) {
        this.permissoes = permissoes;
    }

    public Exercicio getAnoRenovacao() {
        return anoRenovacao;
    }

    public void setAnoRenovacao(Exercicio anoRenovacao) {
        this.anoRenovacao = anoRenovacao;
    }

    public ParametrosTransitoTransporte getParametro() {
        return parametro;
    }

    public void setParametro(ParametrosTransitoTransporte parametro) {
        this.parametro = parametro;
    }

    public void renovar() {
        if (filtroRenovacaoPermissao.getTipoPermissaoRBTrans() == null) {
            FacesUtil.addCampoObrigatorio("Selecione o Tipo de Permissão.");
            return;
        }
        ParametrosTransitoTransporte parametro = renovacaoPermissaoFacade.getPermissaoTransporteFacade().getParametrosTransitoTransporteFacade().recuperarParametroVigentePeloTipo(filtroRenovacaoPermissao.getTipoPermissaoRBTrans());
        if (parametro == null) {
            FacesUtil.addOperacaoNaoRealizada("Não existe parâmetros de trânsito e transportes cadastrados.");
            return;
        }

        try {
            assistenteCalculoRenovacao = new RenovacaoPermissaoFacade.AssistenteCalculoRenovacao(
                permissoes.size(),
                renovacaoPermissaoFacade.getSistemaFacade().getUsuarioCorrente(),
                "Renovação de permissão de transporte");
            Date dataOperacao = renovacaoPermissaoFacade.getSistemaFacade().getDataOperacao();
            futureRenovacoes = AsyncExecutor.getInstance().execute(assistenteCalculoRenovacao, () -> renovacaoPermissaoFacade.renovar(assistenteCalculoRenovacao,
                dataOperacao, permissoes,
                anoRenovacao.getAno(), parametro));
            FacesUtil.redirecionamentoInterno("/acomanhamento-renovacao-permissao-transporte/");
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoRealizada("O sistema não pôde executar a renovação.");
        }
    }

    public void gerarDamnsDasRenovacoes() throws ExecutionException, InterruptedException {
        for (AssistenteRenovacaoPermissaoTransporte renovacao : futureRenovacoes.get()) {
            for (CalculoRBTrans calculoRBTrans : renovacao.getCalculosGerados()) {
                try {
                    renovacaoPermissaoFacade.getPermissaoTransporteFacade().getCalculoRBTransFacade().gerarDAM(calculoRBTrans);
                } catch (Exception e) {
                    logger.debug("ERRO NA GERACAO DO DAM DE RENOVACAO DE PERMISSAO DE TRANSPORTE" + e.getMessage());
                }
            }
        }
    }

    public List<DAM> buscarDamsDasRenovacoes() throws ExecutionException, InterruptedException {
        List<DAM> damsDasRenovacoes = Lists.newArrayList();
        for (AssistenteRenovacaoPermissaoTransporte renovacao : futureRenovacoes.get()) {
            for (CalculoRBTrans calculoRBTrans : renovacao.getCalculosGerados()) {
                ConsultaParcela consultaParcela = new ConsultaParcela();
                consultaParcela.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, calculoRBTrans.getId());
                consultaParcela.executaConsulta();
                damsDasRenovacoes.add(renovacaoPermissaoFacade.getPermissaoTransporteFacade().getDamFacade().recuperaDAMPeloIdParcela(consultaParcela.getResultados().get(0).getIdParcela()));
            }
        }
        return damsDasRenovacoes;
    }

    public void emitirRenovacoes() {
        try {
            gerarDamnsDasRenovacoes();
            List<DAM> dams = buscarDamsDasRenovacoes();

            ImprimeDAM imprimeDAM = new ImprimeDAM();
            imprimeDAM.setGeraNoDialog(true);
            imprimeDAM.imprimirDamUnicoViaApi(dams);
        } catch (Exception e) {
            logger.error(e.getMessage());
            FacesUtil.addErroAoGerarRelatorio("Ocorreu um erro ao emitir os documentos de arrecadação");
        }
    }

    public void limparFutureRenovacoes() {
        futureRenovacoes = null;
        futureRenovacoesConcluida = false;
    }


    public Future<List<AssistenteRenovacaoPermissaoTransporte>> getFutureRenovacoes() {
        return futureRenovacoes;
    }

    public void setFutureRenovacoes(CompletableFuture<List<AssistenteRenovacaoPermissaoTransporte>> futureRenovacoes) {
        this.futureRenovacoes = futureRenovacoes;
    }

    public boolean isFutureRenovacoesConcluida() {
        if (futureRenovacoes == null) {
            return false;
        }

        return futureRenovacoes.isDone();
    }

    public void setFutureRenovacoesConcluida(boolean futureRenovacoesConcluida) {
        this.futureRenovacoesConcluida = futureRenovacoesConcluida;
    }

    public RenovacaoPermissaoFacade.AssistenteCalculoRenovacao getAssistenteCalculoRenovacao() {
        return assistenteCalculoRenovacao;
    }

    public void setAssistenteCalculoRenovacao(RenovacaoPermissaoFacade.AssistenteCalculoRenovacao assistenteCalculoRenovacao) {
        this.assistenteCalculoRenovacao = assistenteCalculoRenovacao;
    }

    public void abrirConfirmacaoRenovacao() {
        if (permissoes != null && !permissoes.isEmpty()) {
            FacesUtil.executaJavaScript("confirmaRenovacao.show()");
        } else {
            FacesUtil.addOperacaoNaoPermitida("Não foi encontrado nenhuma permissão pesquisada para renovação!");
        }
    }
}
