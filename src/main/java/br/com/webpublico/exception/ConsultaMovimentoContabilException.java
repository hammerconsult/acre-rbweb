package br.com.webpublico.exception;

import br.com.webpublico.negocios.contabil.reprocessamento.consulta.ConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.FiltroConsultaMovimentoContabil;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class ConsultaMovimentoContabilException extends RuntimeException {

    public ConsultaMovimentoContabilException(ConsultaMovimentoContabil consultaMovimentoContabil) {
        super("Erro ao realizar a consulta " + consultaMovimentoContabil.toString() + ".");
    }

    public ConsultaMovimentoContabilException(FiltroConsultaMovimentoContabil filtroConsultaMovimentoContabil, Class classe) {
        super("Erro ao realizar a consulta, o filtro " + filtroConsultaMovimentoContabil + " não esta configurado adequadamente para " + classe.getSimpleName() + ".");
    }

    public ConsultaMovimentoContabilException(FiltroConsultaMovimentoContabil filtroConsultaMovimentoContabil, ConsultaMovimentoContabil.Campo campo, Class classe) {
        super("Erro ao realizar a consulta, o filtro " + filtroConsultaMovimentoContabil + " não esta configurado adequadamente para " + classe.getSimpleName() + ". O Tipo de objeto deve ser da classe " + campo.getTipo().getSimpleName() + ".");
    }

}
