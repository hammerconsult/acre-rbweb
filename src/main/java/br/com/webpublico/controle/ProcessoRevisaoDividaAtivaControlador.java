package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.VOSituacaoParcela;
import br.com.webpublico.enums.SituacaoProcessoRevisaoDividaAtiva;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.enums.TipoParcelaRevisaoDA;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CancelamentoParcelamentoFacade;
import br.com.webpublico.negocios.ConsultaDebitoFacade;
import br.com.webpublico.negocios.ProcessoRevisaoDividaAtivaFacade;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
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
import java.util.*;

@ManagedBean(name = "processoRevisaoDividaAtivaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoProcessoRevisaoDividaAtiva",
        pattern = "/processo-revisao-divida-ativa/novo/",
        viewId = "/faces/tributario/dividaativa/revisao/edita.xhtml"),
    @URLMapping(id = "listarProcessoRevisaoDividaAtiva",
        pattern = "/processo-revisao-divida-ativa/listar/",
        viewId = "/faces/tributario/dividaativa/revisao/lista.xhtml"),
    @URLMapping(id = "verProcessoRevisaoDividaAtiva",
        pattern = "/processo-revisao-divida-ativa/ver/#{processoRevisaoDividaAtivaControlador.id}/",
        viewId = "/faces/tributario/dividaativa/revisao/visualizar.xhtml")
})
public class ProcessoRevisaoDividaAtivaControlador extends PrettyControlador<ProcessoRevisaoDividaAtiva> implements Serializable, CRUD {

    @EJB
    private ProcessoRevisaoDividaAtivaFacade processoRevisaoDividaAtivaFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    @EJB
    private CancelamentoParcelamentoFacade cancelamentoParcelamentoFacade;
    private FiltroDebitos filtroDebitos;
    private List<ParcelaComCda> parcelas;
    private List<ResultadoParcela> parcelasRecuperadas;
    private List<ResultadoParcela> novasParcelas;
    private Exercicio exercicioDebito;
    private List<Integer> exerciciosNaoCalculados;

    public ProcessoRevisaoDividaAtivaControlador() {
        super(ProcessoRevisaoDividaAtiva.class);
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public FiltroDebitos getFiltroDebitos() {
        return filtroDebitos;
    }

    public void setFiltroDebitos(FiltroDebitos filtroDebitos) {
        this.filtroDebitos = filtroDebitos;
    }

    public List<ParcelaComCda> getParcelas() {
        return parcelas;
    }

    public void setParcelas(List<ParcelaComCda> parcelas) {
        this.parcelas = parcelas;
    }

    public Exercicio getExercicioDebito() {
        return exercicioDebito;
    }

    public void setExercicioDebito(Exercicio exercicioDebito) {
        this.exercicioDebito = exercicioDebito;
    }

    public List<Integer> getExerciciosNaoCalculados() {
        if (exerciciosNaoCalculados == null) {
            exerciciosNaoCalculados = Lists.newArrayList();
        }
        return exerciciosNaoCalculados;
    }

    public void setExerciciosNaoCalculados(List<Integer> exerciciosNaoCalculados) {
        this.exerciciosNaoCalculados = exerciciosNaoCalculados;
    }

    @Override
    @URLAction(mappingId = "novoProcessoRevisaoDividaAtiva", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setSituacaoProcesso(SituacaoProcessoRevisaoDividaAtiva.EM_ABERTO);
        selecionado.setExercicio(getSistemaControlador().getExercicioCorrente());
        selecionado.setUsuarioSistema(getSistemaControlador().getUsuarioCorrente());
        selecionado.setLancamento(getSistemaControlador().getDataOperacao());

        filtroDebitos = new FiltroDebitos();
        filtroDebitos.limparFiltros();
        parcelas = Lists.newArrayList();
    }

    @URLAction(mappingId = "verProcessoRevisaoDividaAtiva", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/processo-revisao-divida-ativa/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return processoRevisaoDividaAtivaFacade;
    }

    public void limparCadastro() {
        selecionado.setCadastro(null);
        selecionado.setPessoa(null);
        selecionado.setDivida(null);
    }

    public List<SelectItem> getDividas() {
        List<SelectItem> toReturn = new ArrayList<>();
        if (selecionado.getTipoCadastro() != null) {
            List<Divida> dividas = TipoCadastroTributario.PESSOA.equals(selecionado.getTipoCadastro()) ?
                consultaDebitoFacade.getDividaFacade().listaDividasQuePodemSerRevisadas(null) :
                consultaDebitoFacade.getDividaFacade().listaDividasQuePodemSerRevisadas(selecionado.getTipoCadastro());
            for (Divida divida : dividas) {
                toReturn.add(new SelectItem(divida, divida.getDescricao()));
            }
        }
        return toReturn;
    }

    private void validarCamposPesquisa() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getTipoCadastro() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o tipo de cadastro!");
        }
        if (selecionado.getCadastro() == null && selecionado.getPessoa() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o " + (selecionado.getTipoCadastro() == null ? "cadastro ou pessoa!" : selecionado.getTipoCadastro().getDescricaoLonga()));
        }
        if (selecionado.getDivida() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a dívida!");
        }
        if (selecionado.getExerciciosDebitos() == null || selecionado.getExerciciosDebitos().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe ao menos um exercício do débito!");
        }
        ve.lancarException();
    }

    public void consultarParcelas() {
        try {
            validarCamposPesquisa();
            parcelas = converterResultaParcela(processoRevisaoDividaAtivaFacade.buscarParcelasFiltradas(selecionado, filtroDebitos));
            if (parcelas.isEmpty()) {
                FacesUtil.addOperacaoNaoRealizada("Nenhuma parcela de dívida ativa encontrada com os filtros informados!");
            } else {
                validarParcelasComCda(parcelas);
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarParcelasComCda(List<ParcelaComCda> parcelas) {
        ValidacaoException ve = new ValidacaoException();
        for (ParcelaComCda parcela : parcelas) {
            if (parcela.getNumeroCda() == null) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Existe(m) parcela(s) encontrada(s) sem Certidão de Dívida Ativa!");
            }
        }
        ve.lancarException();
    }

    private List<ParcelaComCda> converterResultaParcela(List<ResultadoParcela> resultadoParcelas) {
        List<ParcelaComCda> parcelasComCda = Lists.newArrayList();
        for (ResultadoParcela resultadoParcela : resultadoParcelas) {
            ParcelaComCda parcelaComCda = new ParcelaComCda();
            parcelaComCda.setParcela(resultadoParcela);
            Long idParcela = resultadoParcela.getIdParcela();
            if (resultadoParcela.getTipoCalculoEnumValue().isCancelamentoParcelamento()) {
                idParcela = cancelamentoParcelamentoFacade.buscarIdParcelaOriginalDaParcelaDoCancelamento(resultadoParcela.getIdParcela());
            }
            if (idParcela != null) {
                List<CertidaoDividaAtiva> cdas = processoRevisaoDividaAtivaFacade.buscarCertidoesDividaAtivaDaParcela(idParcela);
                if (!cdas.isEmpty()) {
                    parcelaComCda.setNumeroCda(cdas.get(0).getNumero());
                    parcelaComCda.setAnoCda(cdas.get(0).getExercicio().getAno());
                }
                parcelasComCda.add(parcelaComCda);
            }
        }
        return parcelasComCda;
    }

    public void removerParcelaDaLista(ParcelaComCda parcela) {
        if (parcela != null) {
            parcelas.remove(parcela);
        }
    }

    @Override
    public void salvar() {
        try {
            validarCamposPesquisa();
            validarCamposProcesso();
            validarParcelasComCda(parcelas);
            FacesUtil.executaJavaScript("confirmarProcesso.show()");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void confirmarProcesso() {
        montarItensDoProcessoPelasParcelas();
        if (!getParcelasOriginais().isEmpty()) {
            selecionado.setCodigo(singletonGeradorCodigo.getProximoCodigo(ProcessoRevisaoDividaAtiva.class, "codigo"));
            selecionado = processoRevisaoDividaAtivaFacade.salvarRetornando(selecionado);
            processoRevisaoDividaAtivaFacade.alterarSituacaoParcelas(selecionado);
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        } else {
            FacesUtil.addOperacaoNaoRealizada("Não foi possível encontrar as parcelas de origem da dívida ativa! Certifique-se que essa dívida ativa não é um residual de Cancelamento de Parcelamento!");
        }
    }

    private void montarItensDoProcessoPelasParcelas() {
        if (selecionado.getItens().isEmpty()) {
            for (ParcelaComCda parcela : parcelas) {
                adicionarParcelaAoProcesso(parcela.getParcela().getIdParcela(), TipoParcelaRevisaoDA.DIVIDA_ATIVA_EM_REVISAO);
            }
        }
        montarItensOriginaisDoProcessoPelasParcelas();
    }

    private void compararParcelasCalculoComOsItens(List<ResultadoParcela> parcelas) {
        List<Long> idsCalculo = Lists.newArrayList();
        List<ResultadoParcela> parcelasDoCalculo = Lists.newArrayList();
        List<ResultadoParcela> parcelasParaAdicionar = Lists.newArrayList();
        for (ResultadoParcela parcela : parcelas) {
            if (!idsCalculo.contains(parcela.getIdCalculo())) {
                idsCalculo.add(parcela.getIdCalculo());
                parcelasDoCalculo.clear();
                parcelasDoCalculo = processoRevisaoDividaAtivaFacade.buscarParcelasPeloIdCalculo(parcela.getIdCalculo());
            } else {
                for (ResultadoParcela rp : parcelasDoCalculo) {
                    if (!parcelas.contains(rp) && !parcelasParaAdicionar.contains(rp)) {
                        parcelasParaAdicionar.add(rp);
                    }
                }
            }
        }
        parcelas.addAll(parcelasParaAdicionar);
    }

    private void montarItensOriginaisDoProcessoPelasParcelas() {
        List<ResultadoParcela> parcelasOriginais = processoRevisaoDividaAtivaFacade.buscarParcelasOriginaisDosItensDaRevisao(selecionado.getItens());
        compararParcelasCalculoComOsItens(parcelasOriginais);
        for (ResultadoParcela original : parcelasOriginais) {
            adicionarParcelaAoProcesso(original.getIdParcela(), TipoParcelaRevisaoDA.AGUARDANDO_REVISAO);
        }
    }

    private void adicionarParcelaAoProcesso(Long idParcela, TipoParcelaRevisaoDA tipoParcelaRevisaoDA) {
        ItemProcessoRevisaoDividaAtiva item = new ItemProcessoRevisaoDividaAtiva();
        item.setParcela(new ParcelaValorDivida(idParcela));
        item.setProcessoRevisao(selecionado);
        item.setTipoParcelaRevisaoDA(tipoParcelaRevisaoDA);
        selecionado.getItens().add(item);
    }

    private void validarCamposProcesso() {
        ValidacaoException ve = new ValidacaoException();
        if (parcelas == null || parcelas.isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe ao menos uma parcela de dívida ativa para o processo!");
        }
        if (selecionado.getMotivo() == null || selecionado.getMotivo().trim().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o motivo / fundamentação legal!");
        }
        ve.lancarException();
    }

    public List<ResultadoParcela> getParcelasRecuperadas() {
        if (parcelasRecuperadas == null) {
            List<Long> idsParcelas = Lists.newArrayList();
            for (ItemProcessoRevisaoDividaAtiva item : selecionado.getItens()) {
                idsParcelas.add(item.getParcela().getId());
            }
            ConsultaParcela consulta = new ConsultaParcela();
            consulta.addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IN, idsParcelas);
            parcelasRecuperadas = consulta.executaConsulta().getResultados();
        }
        return parcelasRecuperadas;
    }

    private List<ResultadoParcela> getParcelasPorTipoDeParcela(TipoParcelaRevisaoDA tipoParcelaRevisaoDA) {
        List<ResultadoParcela> parcelas = Lists.newArrayList();
        for (ItemProcessoRevisaoDividaAtiva item : selecionado.getItens()) {
            if (tipoParcelaRevisaoDA.equals(item.getTipoParcelaRevisaoDA())) {
                for (ResultadoParcela resultadoParcela : getParcelasRecuperadas()) {
                    if (item.getParcela().getId().equals(resultadoParcela.getIdParcela())) {
                        parcelas.add(resultadoParcela);
                    }
                }
            }
        }
        ordenarParcelasDoParcelamento(parcelas);
        return parcelas;
    }

    private void ordenarParcelasDoParcelamento(List<ResultadoParcela> parcelas) {
        Comparator<ResultadoParcela> comparator = new Comparator<ResultadoParcela>() {
            @Override
            public int compare(ResultadoParcela um, ResultadoParcela dois) {
                int i = um.getExercicio().compareTo(dois.getExercicio());
                if (i != 0) {
                    return i;
                }
                i = um.getIdValorDivida().compareTo(dois.getIdValorDivida());
                if (i != 0) {
                    return i;
                }
                i = um.getSequenciaParcela().compareTo(dois.getSequenciaParcela());
                if (i != 0) {
                    return i;
                }
                i = um.getVencimento().compareTo(dois.getVencimento());
                if (i != 0) {
                    return i;
                }
                i = um.getSd().compareTo(dois.getSd());
                if (i != 0) {
                    return i;
                }
                i = um.getIdParcela().compareTo(dois.getIdParcela());
                if (i != 0) {
                    return i;
                }
                return 0;
            }
        };
        Collections.sort(parcelas, comparator);
    }

    public List<ResultadoParcela> getParcelasOriginais() {
        return getParcelasPorTipoDeParcela(TipoParcelaRevisaoDA.AGUARDANDO_REVISAO);
    }

    public List<ResultadoParcela> getParcelasDividaAtiva() {
        return getParcelasPorTipoDeParcela(TipoParcelaRevisaoDA.DIVIDA_ATIVA_EM_REVISAO);
    }

    public List<ResultadoParcela> getParcelasEmAberto() {
        if (novasParcelas == null) {
            novasParcelas = processoRevisaoDividaAtivaFacade.buscarParcelasEmAbertoDoNovoCalculo(selecionado, getParcelasOriginais());
        }
        return novasParcelas;
    }

    public boolean getProcessoAberto() {
        return selecionado.getSituacaoProcesso().equals(SituacaoProcessoRevisaoDividaAtiva.EM_ABERTO);
    }

    public String exerciciosNaoCalculados() {
        StringBuilder retorno = new StringBuilder();
        if (!getExerciciosNaoCalculados().isEmpty()) {
            for (Integer exNaoCalculado : getExerciciosNaoCalculados()) {
                if (retorno.toString().isEmpty()) {
                    retorno.append(exNaoCalculado);
                } else {
                    retorno.append(", ").append(exNaoCalculado);
                }
            }
        }
        return retorno.toString();
    }

    public void verificarExerciciosNaoCalculados() {
        List<Integer> exerciciosParcelasEmAberto = Lists.newArrayList();
        getExerciciosNaoCalculados().clear();
        for (ResultadoParcela resultadoParcela : getParcelasEmAberto()) {
            if (!exerciciosParcelasEmAberto.contains(resultadoParcela.getExercicio())) {
                exerciciosParcelasEmAberto.add(resultadoParcela.getExercicio());
            }
        }
        for (ProcessoRevisaoDividaAtivaExercicio exercicioDebito : selecionado.getExerciciosDebitos()) {
            if (!exerciciosParcelasEmAberto.contains(exercicioDebito.getExercicio().getAno())) {
                getExerciciosNaoCalculados().add(exercicioDebito.getExercicio().getAno());
            }
        }
    }

    public void confirmarFinalizacaoProcesso() {
        if (!getParcelasEmAberto().isEmpty()) {
            verificarExerciciosNaoCalculados();
            FacesUtil.executaJavaScript("finalizarProcesso.show()");
        } else {
            FacesUtil.addOperacaoNaoPermitida("Não existem novos débitos lançados passíveis de substituição na Revisão!");
        }
    }

    public void corrigirOrigemParcelas() {
        processoRevisaoDividaAtivaFacade.corrigirParcelasNaInscricaoDeDividaAtiva(selecionado, getParcelasEmAberto());
    }

    private void buscarResultadoParcela() {
        for (ItemProcessoRevisaoDividaAtiva item : selecionado.getItens()) {
            if (item.getParcela().getResultadoParcela() == null) {
                ConsultaParcela consulta = new ConsultaParcela();
                consulta.addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IGUAL, item.getParcela().getId());
                consulta.executaConsulta();
                item.getParcela().setResultadoParcela(consulta.getResultados().get(0));
            }
        }
    }

    public void finalizarProcesso() {
        try {
            selecionado = processoRevisaoDividaAtivaFacade.finalizarProcesso(selecionado, getParcelasEmAberto(), getExerciciosNaoCalculados());
            alterarSituacaoParcelasDAEmRevisao();
            buscarResultadoParcela();
            processoRevisaoDividaAtivaFacade.estornarDebitosExercicioProcesso(selecionado, getExerciciosNaoCalculados());
            try {
                processoRevisaoDividaAtivaFacade.retificarCertidoesDividaAtiva(selecionado);
            } catch (Exception ex) {
                FacesUtil.addAtencao("Não foi possível retificar a CDA dessa dívida ativa!");
                logger.error("Erro ao retificar a CDA: {}", ex.getMessage());
            }

            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
            FacesUtil.addOperacaoRealizada("Processo Finalizado com sucesso!");
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um erro ao finalizar o processo! " + ex.getMessage());
            logger.error(ex.getMessage());
        }
    }

    private void alterarSituacaoParcelasDAEmRevisao() {
        for (VOSituacaoParcela voSituacaoParcela : selecionado.getSituacoesDAEmRevisao()) {
            processoRevisaoDividaAtivaFacade.salvaSituacaoParcela(voSituacaoParcela.getIdParcela(), voSituacaoParcela.getReferencia(),
                voSituacaoParcela.getSituacaoParcela(), voSituacaoParcela.getSaldo());
        }
    }

    public void possivelEstornar() {
        if (getParcelasEmAberto().isEmpty()) {
            FacesUtil.executaJavaScript("estornarProcesso.show()");
        } else {
            FacesUtil.addOperacaoNaoPermitida("Não é possível estornar esse processo de revisão porque já existe novos débitos lançados!");
        }
    }

    public void estornarProcesso() {
        try {
            selecionado = processoRevisaoDividaAtivaFacade.estornarProcesso(selecionado);
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
            FacesUtil.addOperacaoRealizada("Processo Estornado com sucesso!");
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um erro ao estornar o processo! " + ex.getMessage());
            logger.error(ex.getMessage());
        }

    }

    public class ParcelaComCda {
        private ResultadoParcela parcela;
        private Long numeroCda;
        private int anoCda;

        public ResultadoParcela getParcela() {
            return parcela;
        }

        public void setParcela(ResultadoParcela parcela) {
            this.parcela = parcela;
        }

        public Long getNumeroCda() {
            return numeroCda;
        }

        public void setNumeroCda(Long numeroCda) {
            this.numeroCda = numeroCda;
        }

        public int getAnoCda() {
            return anoCda;
        }

        public void setAnoCda(int anoCda) {
            this.anoCda = anoCda;
        }

        public String getNumeroComAno() {
            if (numeroCda != null && anoCda > 0) {
                return numeroCda + "/" + anoCda;
            }
            return "";
        }
    }

    public class FiltroDebitos {
        private Date vencimentoInicial;
        private Date vencimentoFinal;
        private Long numeroCdaInicial;
        private Exercicio exercicioCdaInicial;
        private Long numeroCdaFinal;
        private Exercicio exercicioCdaFinal;

        public void limparFiltros() {
            vencimentoInicial = null;
            vencimentoFinal = null;
            numeroCdaInicial = null;
            exercicioCdaInicial = null;
            numeroCdaFinal = null;
            exercicioCdaFinal = null;
        }

        public Date getVencimentoInicial() {
            return vencimentoInicial;
        }

        public void setVencimentoInicial(Date vencimentoInicial) {
            this.vencimentoInicial = vencimentoInicial;
        }

        public Date getVencimentoFinal() {
            return vencimentoFinal;
        }

        public void setVencimentoFinal(Date vencimentoFinal) {
            this.vencimentoFinal = vencimentoFinal;
        }

        public Long getNumeroCdaInicial() {
            return numeroCdaInicial;
        }

        public void setNumeroCdaInicial(Long numeroCdaInicial) {
            this.numeroCdaInicial = numeroCdaInicial;
        }

        public Long getNumeroCdaFinal() {
            return numeroCdaFinal;
        }

        public void setNumeroCdaFinal(Long numeroCdaFinal) {
            this.numeroCdaFinal = numeroCdaFinal;
        }

        public Exercicio getExercicioCdaInicial() {
            return exercicioCdaInicial;
        }

        public void setExercicioCdaInicial(Exercicio exercicioCdaInicial) {
            this.exercicioCdaInicial = exercicioCdaInicial;
        }

        public Exercicio getExercicioCdaFinal() {
            return exercicioCdaFinal;
        }

        public void setExercicioCdaFinal(Exercicio exercicioCdaFinal) {
            this.exercicioCdaFinal = exercicioCdaFinal;
        }
    }

    public void validarExercicioDebito() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getTipoCadastro() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Cadastro deve ser informado.");
            ve.lancarException();
        }
        if (selecionado.getCadastro() == null && selecionado.getPessoa() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo " + selecionado.getTipoCadastro().getDescricaoLonga() + " deve ser informado.");
        }
        if (selecionado.getDivida() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Dívida deve ser informado.");
        }
        if (exercicioDebito == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício do Débito deve ser informado.");
        } else {
            if (selecionado.getExerciciosDebitos() != null) {
                for (ProcessoRevisaoDividaAtivaExercicio pe : selecionado.getExerciciosDebitos()) {
                    if (pe.getExercicio().equals(exercicioDebito)) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("O Exercício do Débito já está adicionado ao processo de revisão.");
                        break;
                    }
                }
            }
        }
        ve.lancarException();
        if (processoRevisaoDividaAtivaFacade.hasDividaAtivaPagoPorParcelamentoEmAberto(exercicioDebito,
            selecionado.getCadastro(), selecionado.getDivida())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O exercício de " + exercicioDebito +
                " já possui parcelas pagas por parcelamento, não é possível revisar esse exercício.");
        }
        ve.lancarException();
    }

    public void adicionarExercicioDebito() {
        try {
            validarExercicioDebito();
            if (selecionado.getExerciciosDebitos() == null) {
                selecionado.setExerciciosDebitos(new ArrayList<ProcessoRevisaoDividaAtivaExercicio>());
            }
            ProcessoRevisaoDividaAtivaExercicio pe = new ProcessoRevisaoDividaAtivaExercicio();
            pe.setProcessoRevisaoDividaAtiva(selecionado);
            pe.setExercicio(exercicioDebito);
            selecionado.getExerciciosDebitos().add(pe);
            exercicioDebito = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        }
    }

    public void removerExercicioDebito(ProcessoRevisaoDividaAtivaExercicio pe) {
        selecionado.getExerciciosDebitos().remove(pe);
    }
}
