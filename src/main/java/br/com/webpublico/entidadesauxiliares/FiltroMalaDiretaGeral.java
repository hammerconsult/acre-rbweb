package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Divida;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.MalaDiretaGeral;
import br.com.webpublico.enums.DetalhadoResumido;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class FiltroMalaDiretaGeral {

    private Exercicio exercicioInicial;
    private Exercicio exercicioFinal;
    private Date vencimentoInicial;
    private Date vencimentoFinal;
    private Date lancamentoInicial;
    private Date lancamentoFinal;
    private Date pagamentoInicial;
    private Date pagamentoFinal;
    private Date movimentoInicial;
    private Date movimentoFinal;
    private Date notificacaoInicial;
    private Date notificacaoFinal;
    private BigDecimal valorInicial;
    private BigDecimal valorFinal;
    private List<Divida> dividas;
    private Divida divida;
    private Boolean debitosExercicio;
    private Boolean debitosDividaAtiva;
    private Boolean debitosDividaAtivaAjuizada;
    private Boolean debitosParcelamentosDividaAtiva;
    private TipoCadastroTributario tipoCadastroTributario;
    private DetalhadoResumido tipoRelatorio;
    private Boolean temDividaDeDividaAtiva;
    private Boolean temDividaDoExercicio;
    private Boolean temDividaDeParcelamento;
    private FiltroMalaDiretaGeralImovel filtroImovel;
    private FiltroMalaDiretaGeralEconomico filtroEconomico;
    private FiltroMalaDiretaGeralPessoa filtroPessoa;
    private FiltroMalaDiretaGeralRural filtroRural;
    private AssistenteGeracaoRelatorio assistenteGeracaoRelatorio;
    private MalaDiretaGeral malaDiretaGeral;
    private String texto;
    private String filtro;
    private SituacaoParcela[] situacoes;

    public FiltroMalaDiretaGeral() {
        debitosExercicio = true;
        debitosDividaAtiva = false;
        debitosDividaAtivaAjuizada = false;
        debitosParcelamentosDividaAtiva = false;
        temDividaDeDividaAtiva = false;
        temDividaDeParcelamento = false;
        temDividaDoExercicio = false;
        tipoRelatorio = DetalhadoResumido.DETALHADO;
        dividas = Lists.newArrayList();
        filtroImovel = new FiltroMalaDiretaGeralImovel();
        filtroEconomico = new FiltroMalaDiretaGeralEconomico();
        filtroPessoa = new FiltroMalaDiretaGeralPessoa();
        filtroRural = new FiltroMalaDiretaGeralRural();
        assistenteGeracaoRelatorio = new AssistenteGeracaoRelatorio();
        situacoes = new SituacaoParcela[]{SituacaoParcela.EM_ABERTO};
    }

    public AssistenteGeracaoRelatorio getAssistenteGeracaoRelatorio() {
        return assistenteGeracaoRelatorio;
    }

    public void setAssistenteGeracaoRelatorio(AssistenteGeracaoRelatorio assistenteGeracaoRelatorio) {
        this.assistenteGeracaoRelatorio = assistenteGeracaoRelatorio;
    }

    public SituacaoParcela[] getSituacoes() {
        return situacoes;
    }

    public void setSituacoes(SituacaoParcela[] situacoes) {
        this.situacoes = situacoes;
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

    public Date getLancamentoInicial() {
        return lancamentoInicial;
    }

    public void setLancamentoInicial(Date lancamentoInicial) {
        this.lancamentoInicial = lancamentoInicial;
    }

    public Date getLancamentoFinal() {
        return lancamentoFinal;
    }

    public void setLancamentoFinal(Date lancamentoFinal) {
        this.lancamentoFinal = lancamentoFinal;
    }

    public Date getPagamentoInicial() {
        return pagamentoInicial;
    }

    public void setPagamentoInicial(Date pagamentoInicial) {
        this.pagamentoInicial = pagamentoInicial;
    }

    public Date getPagamentoFinal() {
        return pagamentoFinal;
    }

    public void setPagamentoFinal(Date pagamentoFinal) {
        this.pagamentoFinal = pagamentoFinal;
    }

    public Date getMovimentoInicial() {
        return movimentoInicial;
    }

    public void setMovimentoInicial(Date movimentoInicial) {
        this.movimentoInicial = movimentoInicial;
    }

    public Date getMovimentoFinal() {
        return movimentoFinal;
    }

    public void setMovimentoFinal(Date movimentoFinal) {
        this.movimentoFinal = movimentoFinal;
    }

    public Date getNotificacaoInicial() {
        return notificacaoInicial;
    }

    public void setNotificacaoInicial(Date notificacaoInicial) {
        this.notificacaoInicial = notificacaoInicial;
    }

    public Date getNotificacaoFinal() {
        return notificacaoFinal;
    }

    public void setNotificacaoFinal(Date notificacaoFinal) {
        this.notificacaoFinal = notificacaoFinal;
    }

    public BigDecimal getValorInicial() {
        return valorInicial;
    }

    public void setValorInicial(BigDecimal valorInicial) {
        this.valorInicial = valorInicial;
    }

    public BigDecimal getValorFinal() {
        return valorFinal;
    }

    public void setValorFinal(BigDecimal valorFinal) {
        this.valorFinal = valorFinal;
    }

    public List<Divida> getDividas() {
        return dividas;
    }

    public void setDividas(List<Divida> dividas) {
        this.dividas = dividas;
    }

    public Boolean getDebitosExercicio() {
        return debitosExercicio;
    }

    public void setDebitosExercicio(Boolean debitosExercicio) {
        this.debitosExercicio = debitosExercicio;
    }

    public Boolean getDebitosDividaAtiva() {
        return debitosDividaAtiva;
    }

    public void setDebitosDividaAtiva(Boolean debitosDividaAtiva) {
        this.debitosDividaAtiva = debitosDividaAtiva;
    }

    public Boolean getDebitosDividaAtivaAjuizada() {
        return debitosDividaAtivaAjuizada;
    }

    public void setDebitosDividaAtivaAjuizada(Boolean debitosDividaAtivaAjuizada) {
        this.debitosDividaAtivaAjuizada = debitosDividaAtivaAjuizada;
    }

    public Boolean getDebitosParcelamentosDividaAtiva() {
        return debitosParcelamentosDividaAtiva;
    }

    public void setDebitosParcelamentosDividaAtiva(Boolean debitosParcelamentosDividaAtiva) {
        this.debitosParcelamentosDividaAtiva = debitosParcelamentosDividaAtiva;
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    public DetalhadoResumido getTipoRelatorio() {
        return tipoRelatorio;
    }

    public void setTipoRelatorio(DetalhadoResumido tipoRelatorio) {
        this.tipoRelatorio = tipoRelatorio;
    }

    public Boolean getTemDividaDeDividaAtiva() {
        return temDividaDeDividaAtiva;
    }

    public Boolean getTemDividaDoExercicio() {
        return temDividaDoExercicio;
    }

    public Boolean getTemDividaDeParcelamento() {
        return temDividaDeParcelamento;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public FiltroMalaDiretaGeralImovel getFiltroImovel() {
        return filtroImovel;
    }

    public void setFiltroImovel(FiltroMalaDiretaGeralImovel filtroImovel) {
        this.filtroImovel = filtroImovel;
    }

    public MalaDiretaGeral getMalaDiretaGeral() {
        return malaDiretaGeral;
    }

    public void setMalaDiretaGeral(MalaDiretaGeral malaDiretaGeral) {
        this.malaDiretaGeral = malaDiretaGeral;
    }

    public FiltroMalaDiretaGeralEconomico getFiltroEconomico() {
        return filtroEconomico;
    }

    public void setFiltroEconomico(FiltroMalaDiretaGeralEconomico filtroEconomico) {
        this.filtroEconomico = filtroEconomico;
    }

    public FiltroMalaDiretaGeralRural getFiltroRural() {
        return filtroRural;
    }

    public void setFiltroRural(FiltroMalaDiretaGeralRural filtroRural) {
        this.filtroRural = filtroRural;
    }

    public FiltroMalaDiretaGeralPessoa getFiltroPessoa() {
        return filtroPessoa;
    }

    public void setFiltroPessoa(FiltroMalaDiretaGeralPessoa filtroPessoa) {
        this.filtroPessoa = filtroPessoa;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public void removerDivida(Divida divida) {
        if (getDividas().contains(divida)) {
            getDividas().remove(divida);
        }
    }

    public void adicionarDivida() {
        try {
            validarDivida();
            dividas.add(divida);
            divida = new Divida();
            ajustarTiposDeDividas();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    public void ajustarTiposDeDividas() {
        temDividaDeDividaAtiva = false;
        temDividaDoExercicio = false;
        temDividaDeParcelamento = false;
        for (Divida dv : dividas) {
            if (dv.getIsDividaAtiva()) {
                temDividaDeDividaAtiva = true;
            } else {
                temDividaDoExercicio = true;
            }
        }
        if (temDividaDeDividaAtiva) {
            debitosDividaAtiva = false;
            debitosDividaAtivaAjuizada = false;
        }
        if (!temDividaDeDividaAtiva && !temDividaDeParcelamento) {
            debitosExercicio = false;
        }
        if (temDividaDoExercicio) {
            debitosExercicio = false;
        }
        if ((!debitosDividaAtiva && !debitosDividaAtivaAjuizada && !temDividaDeDividaAtiva) || temDividaDeParcelamento) {
            debitosParcelamentosDividaAtiva = false;
        }
    }

    private void validarDivida() {
        Divida.validarDividaParaAdicaoEmLista(divida, dividas);
    }

    public void desmarcaParcelamentoOriginados() {
        if (!debitosDividaAtiva && !debitosDividaAtivaAjuizada) {
            debitosParcelamentosDividaAtiva = false;
        }
    }

    public List<SituacaoParcela> getTodasAsSituacoesParcela() {
        return SituacaoParcela.getValues();
    }
}
