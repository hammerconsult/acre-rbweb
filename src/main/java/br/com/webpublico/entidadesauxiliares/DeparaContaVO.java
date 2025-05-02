package br.com.webpublico.entidadesauxiliares;

public class DeparaContaVO {

    private ContaVO contaOrigem;
    private ContaVO contaDestino;

    public DeparaContaVO() {
    }

    public ContaVO getContaOrigem() {
        return contaOrigem;
    }

    public void setContaOrigem(ContaVO contaOrigem) {
        this.contaOrigem = contaOrigem;
    }

    public ContaVO getContaDestino() {
        return contaDestino;
    }

    public void setContaDestino(ContaVO contaDestino) {
        this.contaDestino = contaDestino;
    }
}
