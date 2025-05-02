package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ProcessoCalculoIPTU;
import br.com.webpublico.entidades.ValorDivida;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;

import java.util.List;

public class ExercicioRevisaoIPTU {

    private Exercicio exercicio;
    private List<ResultadoParcela> parcelas;
    private Boolean revisar;
    private Boolean permiteRevisao;
    private String mensagemRevisaoNaoPermitida;
    private ValorDivida valorDividaOriginal;
    private ProcessoCalculoIPTU processoCalculoIPTU;
    private ValorDivida valorDividaOriginado;

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public List<ResultadoParcela> getParcelas() {
        return parcelas;
    }

    public void setParcelas(List<ResultadoParcela> parcelas) {
        this.parcelas = parcelas;
    }

    public Boolean getRevisar() {
        return revisar;
    }

    public void setRevisar(Boolean revisar) {
        this.revisar = revisar;
    }

    public Boolean getPermiteRevisao() {
        return permiteRevisao;
    }

    public void setPermiteRevisao(Boolean permiteRevisao) {
        this.permiteRevisao = permiteRevisao;
    }

    public String getMensagemRevisaoNaoPermitida() {
        return mensagemRevisaoNaoPermitida;
    }

    public void setMensagemRevisaoNaoPermitida(String mensagemRevisaoNaoPermitida) {
        this.mensagemRevisaoNaoPermitida = mensagemRevisaoNaoPermitida;
    }

    public ValorDivida getValorDividaOriginal() {
        return valorDividaOriginal;
    }

    public void setValorDividaOriginal(ValorDivida valorDividaOriginal) {
        this.valorDividaOriginal = valorDividaOriginal;
    }

    public ProcessoCalculoIPTU getProcessoCalculoIPTU() {
        return processoCalculoIPTU;
    }

    public void setProcessoCalculoIPTU(ProcessoCalculoIPTU processoCalculoIPTU) {
        this.processoCalculoIPTU = processoCalculoIPTU;
    }

    public ValorDivida getValorDividaOriginado() {
        return valorDividaOriginado;
    }

    public void setValorDividaOriginado(ValorDivida valorDividaOriginado) {
        this.valorDividaOriginado = valorDividaOriginado;
    }
}
