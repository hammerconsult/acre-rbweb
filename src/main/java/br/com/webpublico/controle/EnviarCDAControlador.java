package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoCertidaoDA;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.ConverterExercicio;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import org.primefaces.context.RequestContext;
import org.primefaces.model.StreamedContent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@ManagedBean(name = "enviarCDAControlador")
@ViewScoped
//@URLMappings(mappings = {
//        @URLMapping(id = "novoEnvioCertidaoDA", pattern = "/enviar-certicao-de-divida-ativa/", viewId = "/faces/tributario/dividaativa/certidaodividaativa/enviarcda.xhtml")
//})
public class EnviarCDAControlador extends PrettyControlador<CertidaoDividaAtiva> implements Serializable {
    private TipoCadastroTributario tipoCadastro;
    private Divida divida;
    private Exercicio exercicioInicial;
    private Exercicio exercicioFinal;
    private String cadastroInicial;
    private String cadastroFinal;
    private Pessoa pessoa;
    private List<CertidaoDividaAtiva> certidoesDividaAtiva;
    private CertidaoDividaAtiva[] certidoesSelecionadas;
    private List<InscricaoDividaParcela> listaParcelas;
    private String nrProtocolo;
    private String motivo;
    private ConverterAutoComplete dividaConverter;
    private ConverterExercicio converterExercicio;
    @EJB
    private CertidaoDividaAtivaFacade certidaoDividaAtivaFacade;
    @EJB
    private AlterarSituacaoCDAFacade alterarSituacaoCDAFacade;
    @EJB
    private ComunicaSofPlanFacade comunicaSofPlanFacade;
    @EJB
    private ProcessoParcelamentoFacade processoParcelamentoFacade;
    private Long numeroCdaInicial;
    private Long numeroCdaFinal;
    private Boolean pesquisaApenasValidas;
    private Future future;
    private SituacaoEnvio situacaoEnvio;
    private int maxResult, inicio;
    private Future<StreamedContent> futureArquivo;

    public EnviarCDAControlador() {
        super(CertidaoDividaAtiva.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return certidaoDividaAtivaFacade;
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public List<SelectItem> getTiposCadastroTributario() {
        return TipoCadastroTributario.asSelectItemList();
    }

    public TipoCadastroTributario getTipoCadastro() {
        return tipoCadastro;
    }

    public void setTipoCadastro(TipoCadastroTributario tipoCadastro) {
        this.tipoCadastro = tipoCadastro;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public Exercicio getExercicioInicial() {
        return exercicioInicial;
    }

    public void setExercicioInicial(Exercicio exercicioInicial) {
        this.exercicioInicial = exercicioInicial;
    }

    public Exercicio getExercicioFinal() {
        return exercicioFinal;
    }

    public void setExercicioFinal(Exercicio exercicioFinal) {
        this.exercicioFinal = exercicioFinal;
    }

    public String getCadastroInicial() {
        return cadastroInicial;
    }

    public void setCadastroInicial(String cadastroInicial) {
        this.cadastroInicial = cadastroInicial;
    }

    public String getCadastroFinal() {
        return cadastroFinal;
    }

    public void setCadastroFinal(String cadastroFinal) {
        this.cadastroFinal = cadastroFinal;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public CertidaoDividaAtiva[] getCertidoesSelecionadas() {
        return certidoesSelecionadas;
    }

    public void setCertidoesSelecionadas(CertidaoDividaAtiva[] certidoesSelecionadas) {
        this.certidoesSelecionadas = certidoesSelecionadas;
    }

    public List<CertidaoDividaAtiva> getCertidoesDividaAtiva() {
        return certidoesDividaAtiva;
    }

    public void setCertidoesDividaAtiva(List<CertidaoDividaAtiva> certidoesDividaAtiva) {
        this.certidoesDividaAtiva = certidoesDividaAtiva;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public List<InscricaoDividaParcela> getListaParcelas() {
        return listaParcelas;
    }

    public void setListaParcelas(List<InscricaoDividaParcela> listaParcelas) {
        this.listaParcelas = listaParcelas;
    }

    public Long getNumeroCdaFinal() {
        return numeroCdaFinal;
    }

    public void setNumeroCdaFinal(Long numeroCdaFinal) {
        this.numeroCdaFinal = numeroCdaFinal;
    }

    public Long getNumeroCdaInicial() {
        return numeroCdaInicial;
    }

    public void setNumeroCdaInicial(Long numeroCdaInicial) {
        this.numeroCdaInicial = numeroCdaInicial;
    }

    @URLAction(mappingId = "novoEnvioCertidaoDA", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        limparDados();
        numeroCdaInicial = null;
        numeroCdaFinal = null;
        situacaoEnvio = SituacaoEnvio.AGUARDANDO;
        inicio = 0;
        maxResult = 10;
        pesquisaApenasValidas = true;
    }

    public ConverterExercicio getConverterExercicio() {
        if (converterExercicio == null) {
            converterExercicio = new ConverterExercicio(certidaoDividaAtivaFacade.getExercicioFacade());
        }
        return converterExercicio;
    }

    public Converter getDividaConverter() {
        if (dividaConverter == null) {
            dividaConverter = new ConverterAutoComplete(Divida.class, certidaoDividaAtivaFacade.getDividaFacade());
        }
        return dividaConverter;
    }

    public List<SelectItem> getTiposDividas() {
        List<SelectItem> lista = new ArrayList<>();
        if (tipoCadastro != null) {
            lista.add(new SelectItem(null, ""));
            for (Divida divida : certidaoDividaAtivaFacade.getInscricaoDividaAtivaFacade().buscarDividasPorTipoCadastroTributario(tipoCadastro)) {
                lista.add(new SelectItem(divida, divida.getDescricao()));
            }
        }
        return lista;
    }

    public void pesquisar() {
        certidoesDividaAtiva = certidaoDividaAtivaFacade.buscarCDAs(tipoCadastro, cadastroInicial, cadastroFinal, exercicioInicial, exercicioFinal, numeroCdaInicial, numeroCdaFinal, divida, pessoa, Lists.newArrayList(SituacaoCertidaoDA.ABERTA), inicio, maxResult, pesquisaApenasValidas, true, null);
    }

    public String retornaInscricaoDoCadastro(Cadastro cadastro) {
        return certidaoDividaAtivaFacade.retornaInscricaoDoCadastro(cadastro);
    }

    public BigDecimal valorDacertidao(CertidaoDividaAtiva certidao) {
        return certidaoDividaAtivaFacade.valorDaCertidao(certidao);
    }

    public String recuperaUltimaSituacaoDaParcela(ParcelaValorDivida parcelaValorDivida) {
        return certidaoDividaAtivaFacade.getInscricaoDividaAtivaFacade().retornaUltimaSituacaoDaParcela(parcelaValorDivida);
    }

    public void validaCertidoesSelecionadas() {
        if (certidoesSelecionadas != null && certidoesSelecionadas.length > 0) {
            RequestContext.getCurrentInstance().execute("dialogEnviar.show()");
        } else {
            FacesUtil.addError("Atenção", "Nenhum Certidão foi selecionada!");
        }
    }


    public void enviaTodosParcelamentos() {
        comunicaSofPlanFacade.enviarTodosParcelamentos(comunicaSofPlanFacade.getSistemaFacade().getUsuarioCorrente());
    }

    public void enviaParcelamentosEstornados() {
        comunicaSofPlanFacade.enviarParcelamentosEstornados(comunicaSofPlanFacade.getSistemaFacade().getUsuarioCorrente());
    }


    public void limparDados() {
        this.tipoCadastro = null;
        this.certidoesDividaAtiva = new ArrayList<>();
        this.certidoesSelecionadas = null;
        this.listaParcelas = new ArrayList<>();
        this.exercicioInicial = null;
        this.exercicioFinal = null;
        this.nrProtocolo = "";
        this.motivo = "";
        this.limparFiltro();
    }

    public void limparFiltro() {
        this.divida = null;
        this.cadastroInicial = "1";
        this.cadastroFinal = "999999999999999999";
        this.pessoa = null;
    }

    public void consultaEnvio() {
        if (situacaoEnvio.equals(SituacaoEnvio.ENVIANDO) && future != null && future.isDone()) {
            situacaoEnvio = SituacaoEnvio.ENVIADO;
        }
    }

    public SituacaoEnvio getSituacaoEnvio() {
        return situacaoEnvio;
    }

    public void imprimirRelatorioOcorrencia() {
        new ImprimeRelatorio().imprime();
    }

    public void proximaPagina() {
        inicio += maxResult;
        pesquisar();
    }

    public void paginaAnterior() {
        inicio -= maxResult;
        pesquisar();
    }

    public Integer getMaxResult() {
        return maxResult;
    }

    public void setMaxResult(int maxResult) {
        this.maxResult = maxResult;
    }

    public void setMaxResult(Integer maxResult) {
        this.maxResult = maxResult;
    }

    public Integer getInicio() {
        return inicio;
    }

    public void setInicio(int inicio) {
        this.inicio = inicio;
    }

    public Boolean getPesquisaApenasValidas() {
        return pesquisaApenasValidas;
    }

    public void setPesquisaApenasValidas(Boolean pesquisaApenasValidas) {
        this.pesquisaApenasValidas = pesquisaApenasValidas;
    }

    public void gerarArquivo() {
        futureArquivo = null;
        try {
            if (certidoesSelecionadas != null && certidoesSelecionadas.length > 0) {
//                futureArquivo = comunicaSofPlanFacade.geraAquivoSofPlan(Arrays.asList(certidoesSelecionadas));
            } else {
//                futureArquivo = comunicaSofPlanFacade.geraAquivoSofPlanTodasCdasComunicadas();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public boolean getGerandoArquivo() {
        return !(futureArquivo != null && futureArquivo.isDone());
    }

    public void deletaArquivo() {
        futureArquivo = null;
    }

    public StreamedContent getDownload() {
        try {
            return futureArquivo.get();
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        } catch (ExecutionException e) {
            logger.error(e.getMessage());
        }
        return null;
    }


    public static enum SituacaoEnvio {AGUARDANDO, ENVIANDO, ENVIADO}

    public class ImprimeRelatorio extends AbstractReport {

        public void imprime() {
            HashMap parameters = new HashMap();
            parameters.put("BRASAO", getCaminhoImagem());
            parameters.put("USUARIO", SistemaFacade.obtemLogin());
            try {
                gerarRelatorio("cdaInconsistente.jasper", parameters);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }
}
