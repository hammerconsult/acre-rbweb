package br.com.webpublico.exception;

import br.com.webpublico.enums.SummaryMessages;

import javax.faces.application.FacesMessage;

public class ValidacaoExceptionComCodigo extends ValidacaoException {

    private boolean lancarSempreAoAdicionar;

    public ValidacaoExceptionComCodigo(boolean lancarSempreAoAdicionar) {
        this.lancarSempreAoAdicionar = lancarSempreAoAdicionar;
    }

    public ValidacaoExceptionComCodigo() {
    }

    public ValidacaoExceptionComCodigo adicionarMensagemDeCampoObrigatorio(String detail, int codigo) {
        getMensagens().add(new ValidacaoComCodigo(FacesMessage.SEVERITY_ERROR, SummaryMessages.CAMPO_OBRIGATORIO.getDescricao(), detail, codigo));
        if(lancarSempreAoAdicionar){
            lancarException();
        }
        return this;
    }

    public ValidacaoExceptionComCodigo adicionarMensagemDeOperacaoNaoPermitida(String detail, int codigo) {
        getMensagens().add(new ValidacaoComCodigo(FacesMessage.SEVERITY_ERROR, SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), detail, codigo));
        if(lancarSempreAoAdicionar){
            lancarException();
        }
        return this;
    }

    public ValidacaoExceptionComCodigo adicionarMensagemDeOperacaoNaoRealizada(String detail, int codigo) {
        getMensagens().add(new ValidacaoComCodigo(FacesMessage.SEVERITY_ERROR, SummaryMessages.OPERACAO_NAO_REALIZADA.getDescricao(), detail, codigo));
        if(lancarSempreAoAdicionar){
            lancarException();
        }
        return this;
    }

    @Override
    public ValidacaoExceptionComCodigo adicionarMensagemDeCampoObrigatorio(String detail) {
        return adicionarMensagemDeCampoObrigatorio(detail, -1);
    }

    @Override
    public ValidacaoExceptionComCodigo adicionarMensagemDeOperacaoNaoPermitida(String detail) {
        return adicionarMensagemDeOperacaoNaoPermitida(detail, -1);

    }

    @Override
    public ValidacaoExceptionComCodigo adicionarMensagemDeOperacaoNaoRealizada(String detail) {
        return adicionarMensagemDeOperacaoNaoRealizada(detail, -1);
    }

    public class ValidacaoComCodigo extends FacesMessage {
        private int codigo;

        public ValidacaoComCodigo(Severity severity, String summary, String detail, int codigo) {
            super(severity, summary, detail);
            this.codigo = codigo;
        }

        public int getCodigo() {
            return codigo;
        }
    }
}
