package br.com.webpublico.negocios.tributario.consultaparcela;


import br.com.webpublico.negocios.tributario.dao.JdbcParcelaValorDividaDAO;
import br.com.webpublico.tributario.consultadebitos.services.ConsultaDebitosService;
import br.com.webpublico.util.DataUtil;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.Date;

public class ConsultaParcela extends br.com.webpublico.tributario.consultadebitos.ConsultaParcela {

    public ConsultaParcela(Date referencia) {
        this();
        try {
            this.referencia = LocalDateTime.fromDateFields(referencia);
        } catch (IllegalArgumentException ie) {
            this.referencia = LocalDateTime.fromDateFields(DataUtil.adicionaHoras(referencia, 23));
        }
        this.criadoEm = LocalDateTime.now();
    }

    public ConsultaParcela(Date referencia, Date dataPagamentoInicial, Date dataPagamentoFinal) {
        this(referencia);
        this.dataPagamentoInicial = LocalDate.fromDateFields(dataPagamentoInicial);
        this.dataPagamentoFinal = LocalDate.fromDateFields(dataPagamentoFinal);
    }

    public ConsultaParcela() {
        super(ConsultaDebitosService.buildFromJdbcTemplate(JdbcParcelaValorDividaDAO.getInstance().getJdbcTemplate()));
        limpaConsulta();
        this.criadoEm = LocalDateTime.now();
        this.referencia = LocalDateTime.now();
        this.referenciaMulta = null;
        this.referenciaHonorarios = null;
        this.bloqueio = null;
    }

    public ConsultaDebitosService getServiceConsultaDebitos() {
        return super.serviceConsultaDebitos;
    }


}
