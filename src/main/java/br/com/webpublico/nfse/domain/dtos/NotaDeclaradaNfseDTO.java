package br.com.webpublico.nfse.domain.dtos;

public class NotaDeclaradaNfseDTO implements NfseDTO {

    private DeclaracaoPrestacaoServicoNfseDTO declaracaoPrestacaoServico;

    public NotaDeclaradaNfseDTO() {
    }

    public DeclaracaoPrestacaoServicoNfseDTO getDeclaracaoPrestacaoServico() {
        return declaracaoPrestacaoServico;
    }

    public void setDeclaracaoPrestacaoServico(DeclaracaoPrestacaoServicoNfseDTO declaracaoPrestacaoServico) {
        this.declaracaoPrestacaoServico = declaracaoPrestacaoServico;
    }
}
