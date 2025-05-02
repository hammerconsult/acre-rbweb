package br.com.webpublico.negocios.tributario.consultaparcela;

import br.com.webpublico.entidades.ConfiguracaoAcrescimos;
import br.com.webpublico.entidades.ParcelaValorDivida;
import br.com.webpublico.tributario.consultadebitos.calculadores.CalculadorHonorarios;
import br.com.webpublico.tributario.consultadebitos.calculadores.CalculadorJuros;
import br.com.webpublico.tributario.consultadebitos.calculadores.CalculadorMulta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

@Deprecated
public class CalculadorAcrescimos {

    private static final Logger log = LoggerFactory.getLogger(CalculadorAcrescimos.class);

    @Deprecated
    public static ParcelaValorDivida calculaAcrescimo(ParcelaValorDivida parcela, Date dataReferencia, BigDecimal saldo, ConfiguracaoAcrescimos acrescimo) {
        Calendar vencimento = Calendar.getInstance();
        vencimento.setTime(parcela.getVencimento());
        int anoParcela = vencimento.get(Calendar.YEAR);
        int anoAtual = Calendar.getInstance().get(Calendar.YEAR);
        boolean calculaDividaAtiva = anoParcela < anoAtual;
        parcela.zeraValores();
        BigDecimal totalMulta = BigDecimal.ZERO;
        BigDecimal totalJuros = BigDecimal.ZERO;
        totalMulta = totalMulta.add(CalculadorMulta.calculaMulta(acrescimo.toDto(), parcela.getVencimento(), dataReferencia, saldo, calculaDividaAtiva));
        totalJuros = totalJuros.add(CalculadorJuros.calculaJuros(acrescimo.toDto(), parcela.getVencimento(), dataReferencia, saldo, calculaDividaAtiva));
        parcela.setValorSaldo(saldo);
        parcela.setValorMulta(totalMulta);
        parcela.setValorJuros(totalJuros);
        if (parcela.isDividaAtivaAjuizada()) {
            parcela.setValorHonorarios(CalculadorHonorarios.calculaHonorarios(acrescimo.toDto(), dataReferencia, parcela.getValorSaldo()));
        }
        parcela.setValorTotal(parcela.getValorHonorarios().add(parcela.getValorSaldo().add(parcela.getValorMulta()).add(parcela.getValorJuros())));
        return parcela;
    }
}
