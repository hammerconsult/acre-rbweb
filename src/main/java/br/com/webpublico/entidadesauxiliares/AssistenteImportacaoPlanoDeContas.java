package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.PlanoDeContas;
import br.com.webpublico.entidades.SaldoContaContabil;
import br.com.webpublico.entidades.TipoConta;
import br.com.webpublico.enums.SituacaoImportarPlanoDeContas;
import br.com.webpublico.util.AssistenteBarraProgresso;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

public class AssistenteImportacaoPlanoDeContas extends AssistenteBarraProgresso {

    private Exercicio exercicio;
    private Exercicio exercicioAnterior;
    private Date dataOperacao;
    private TipoConta tipoConta;
    private BarraProgressoItens barraProgressoItens;
    private PlanoDeContas planoDeContas;
    private PlanoDeContas planoDeContasExercicioAnterior;
    private List<String> mensagens = Lists.newArrayList();
    private Integer posicaoColunaCodigoConta;
    private Integer posicaoColunaDescricaoConta;
    private Integer linhaPrimeiroRegistro;
    private Integer posicaoColunaCodigoContaDepara;
    private Integer posicaoColunaNaturezaSaldo;
    private Integer posicaoColunaDescricaoContaDepara;
    private Integer posicaoColunaNaturezaDaConta;
    private Integer posicaoColunaNaturezaInformacao;
    private Integer posicaoColunaCategoriaConta;
    private Integer posicaoColunaSubsistema;
    private Integer posicaoColunaOperacao;
    private Integer posicaoColunaCodigoSiconfi;
    private Integer posicaoColunaCodigoTCE;
    private String mensagemErro;
    private List<ContaVO> todasContas;
    private List<ContaVO> contasImportadas;
    private List<ContaVO> contasAlteradas;
    private List<ContaVO> contasExcluidas;
    private List<DeparaContaVO> deparaContas;
    private SituacaoImportarPlanoDeContas situacao;
    private List<SaldoContaContabil> saldoContabeis;

    private Boolean mostrarContasNaoEncontrada;

    private List<br.com.webpublico.entidades.Conta> contasNaoEncontradaTCE;

    public AssistenteImportacaoPlanoDeContas() {
        todasContas = Lists.newArrayList();
        contasImportadas = Lists.newArrayList();
        contasAlteradas = Lists.newArrayList();
        contasExcluidas = Lists.newArrayList();
        deparaContas = Lists.newArrayList();
    }

    public void definirPosicaoExcelTCE() {
        if (tipoConta != null && tipoConta.getClasseDaConta().isClasseContabil()) {
            posicaoColunaCodigoConta = 7;
            posicaoColunaDescricaoConta = 8;
            posicaoColunaNaturezaSaldo = 10;
            posicaoColunaNaturezaInformacao = 12;
            posicaoColunaCategoriaConta = 13;
            posicaoColunaSubsistema = 14;
            posicaoColunaOperacao = 17;
        } else {
            posicaoColunaCodigoConta = 7;
            posicaoColunaDescricaoConta = 8;
        }

        posicaoColunaCodigoContaDepara = 18;
        posicaoColunaDescricaoContaDepara = 19;
        posicaoColunaNaturezaDaConta = 20;
        posicaoColunaCodigoSiconfi = 21;
        posicaoColunaCodigoTCE = 22;

        linhaPrimeiroRegistro = 2;
    }

    public boolean isSituacao(SituacaoImportarPlanoDeContas situacao) {
        return situacao.equals(this.situacao);
    }

    public Boolean getImportado() {
        return isSituacao(SituacaoImportarPlanoDeContas.IMPORTADO);
    }

    public Boolean getFinalizado() {
        return isSituacao(SituacaoImportarPlanoDeContas.FINALIZADO);
    }

    public Boolean getValidado() {
        return isSituacao(SituacaoImportarPlanoDeContas.VALIDADO);
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Exercicio getExercicioAnterior() {
        return exercicioAnterior;
    }

    public void setExercicioAnterior(Exercicio exercicioAnterior) {
        this.exercicioAnterior = exercicioAnterior;
    }

    public Date getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public TipoConta getTipoConta() {
        return tipoConta;
    }

    public void setTipoConta(TipoConta tipoConta) {
        this.tipoConta = tipoConta;
    }

    public PlanoDeContas getPlanoDeContas() {
        return planoDeContas;
    }

    public void setPlanoDeContas(PlanoDeContas planoDeContas) {
        this.planoDeContas = planoDeContas;
    }

    public List<String> getMensagens() {
        return mensagens;
    }

    public void setMensagens(List<String> mensagens) {
        this.mensagens = mensagens;
    }

    public Integer getPosicaoColunaCodigoConta() {
        return posicaoColunaCodigoConta;
    }

    public void setPosicaoColunaCodigoConta(Integer posicaoColunaCodigoConta) {
        this.posicaoColunaCodigoConta = posicaoColunaCodigoConta;
    }

    public Integer getPosicaoColunaDescricaoConta() {
        return posicaoColunaDescricaoConta;
    }

    public void setPosicaoColunaDescricaoConta(Integer posicaoColunaDescricaoConta) {
        this.posicaoColunaDescricaoConta = posicaoColunaDescricaoConta;
    }

    public List<ContaVO> getTodasContas() {
        return todasContas;
    }

    public void setTodasContas(List<ContaVO> todasContas) {
        this.todasContas = todasContas;
    }

    public List<ContaVO> getContasImportadas() {
        return contasImportadas;
    }

    public void setContasImportadas(List<ContaVO> contasImportadas) {
        this.contasImportadas = contasImportadas;
    }

    public List<ContaVO> getContasAlteradas() {
        return contasAlteradas;
    }

    public void setContasAlteradas(List<ContaVO> contasAlteradas) {
        this.contasAlteradas = contasAlteradas;
    }

    public List<ContaVO> getContasExcluidas() {
        return contasExcluidas;
    }

    public void setContasExcluidas(List<ContaVO> contasExcluidas) {
        this.contasExcluidas = contasExcluidas;
    }

    public List<DeparaContaVO> getDeparaContas() {
        return deparaContas;
    }

    public void setDeparaContas(List<DeparaContaVO> deparaContas) {
        this.deparaContas = deparaContas;
    }

    public Boolean getMostrarContasNaoEncontrada() {
        return mostrarContasNaoEncontrada;
    }

    public void setMostrarContasNaoEncontrada(Boolean mostrarContasNaoEncontrada) {
        this.mostrarContasNaoEncontrada = mostrarContasNaoEncontrada;
    }

    public List<br.com.webpublico.entidades.Conta> getContasNaoEncontradaTCE() {
        return contasNaoEncontradaTCE;
    }

    public void setContasNaoEncontradaTCE(List<br.com.webpublico.entidades.Conta> contasNaoEncontradaTCE) {
        this.contasNaoEncontradaTCE = contasNaoEncontradaTCE;
    }

    public BarraProgressoItens getBarraProgressoItens() {
        return barraProgressoItens;
    }

    public void setBarraProgressoItens(BarraProgressoItens barraProgressoItens) {
        this.barraProgressoItens = barraProgressoItens;
    }

    public Integer getLinhaPrimeiroRegistro() {
        return linhaPrimeiroRegistro;
    }

    public void setLinhaPrimeiroRegistro(Integer linhaPrimeiroRegistro) {
        this.linhaPrimeiroRegistro = linhaPrimeiroRegistro;
    }

    public Integer getPosicaoColunaCodigoContaDepara() {
        return posicaoColunaCodigoContaDepara;
    }

    public void setPosicaoColunaCodigoContaDepara(Integer posicaoColunaCodigoContaDepara) {
        this.posicaoColunaCodigoContaDepara = posicaoColunaCodigoContaDepara;
    }

    public Integer getPosicaoColunaDescricaoContaDepara() {
        return posicaoColunaDescricaoContaDepara;
    }

    public void setPosicaoColunaDescricaoContaDepara(Integer posicaoColunaDescricaoContaDepara) {
        this.posicaoColunaDescricaoContaDepara = posicaoColunaDescricaoContaDepara;
    }

    public Integer getPosicaoColunaNaturezaDaConta() {
        return posicaoColunaNaturezaDaConta;
    }

    public void setPosicaoColunaNaturezaDaConta(Integer posicaoColunaNaturezaDaConta) {
        this.posicaoColunaNaturezaDaConta = posicaoColunaNaturezaDaConta;
    }

    public Integer getPosicaoColunaNaturezaInformacao() {
        return posicaoColunaNaturezaInformacao;
    }

    public void setPosicaoColunaNaturezaInformacao(Integer posicaoColunaNaturezaInformacao) {
        this.posicaoColunaNaturezaInformacao = posicaoColunaNaturezaInformacao;
    }

    public Integer getPosicaoColunaNaturezaSaldo() {
        return posicaoColunaNaturezaSaldo;
    }

    public void setPosicaoColunaNaturezaSaldo(Integer posicaoColunaNaturezaSaldo) {
        this.posicaoColunaNaturezaSaldo = posicaoColunaNaturezaSaldo;
    }

    public SituacaoImportarPlanoDeContas getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoImportarPlanoDeContas situacao) {
        this.situacao = situacao;
    }

    public String getMensagemErro() {
        return mensagemErro;
    }

    public void setMensagemErro(String mensagemErro) {
        this.mensagemErro = mensagemErro;
    }

    public PlanoDeContas getPlanoDeContasExercicioAnterior() {
        return planoDeContasExercicioAnterior;
    }

    public void setPlanoDeContasExercicioAnterior(PlanoDeContas planoDeContasExercicioAnterior) {
        this.planoDeContasExercicioAnterior = planoDeContasExercicioAnterior;
    }

    public List<SaldoContaContabil> getSaldoContabeis() {
        return saldoContabeis;
    }

    public void setSaldoContabeis(List<SaldoContaContabil> saldoContabeis) {
        this.saldoContabeis = saldoContabeis;
    }

    public Integer getPosicaoColunaSubsistema() {
        return posicaoColunaSubsistema;
    }

    public void setPosicaoColunaSubsistema(Integer posicaoColunaSubsistema) {
        this.posicaoColunaSubsistema = posicaoColunaSubsistema;
    }

    public Integer getPosicaoColunaOperacao() {
        return posicaoColunaOperacao;
    }

    public void setPosicaoColunaOperacao(Integer posicaoColunaOperacao) {
        this.posicaoColunaOperacao = posicaoColunaOperacao;
    }

    public Integer getPosicaoColunaCodigoSiconfi() {
        return posicaoColunaCodigoSiconfi;
    }

    public void setPosicaoColunaCodigoSiconfi(Integer posicaoColunaCodigoSiconfi) {
        this.posicaoColunaCodigoSiconfi = posicaoColunaCodigoSiconfi;
    }

    public Integer getPosicaoColunaCodigoTCE() {
        return posicaoColunaCodigoTCE;
    }

    public void setPosicaoColunaCodigoTCE(Integer posicaoColunaCodigoTCE) {
        this.posicaoColunaCodigoTCE = posicaoColunaCodigoTCE;
    }

    public Integer getPosicaoColunaCategoriaConta() {
        return posicaoColunaCategoriaConta;
    }

    public void setPosicaoColunaCategoriaConta(Integer posicaoColunaCategoriaConta) {
        this.posicaoColunaCategoriaConta = posicaoColunaCategoriaConta;
    }
}
