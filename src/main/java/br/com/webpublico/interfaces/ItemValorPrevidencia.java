package br.com.webpublico.interfaces;

import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidades.rh.configuracao.ReajusteMediaAposentadoria;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author peixe on 19/01/2016  13:55.
 */
public interface ItemValorPrevidencia {
    Date getInicioVigencia();

    Date getFinalVigencia();

    VinculoFP getVinculoFP();

    EventoFP getEventoFP();

    ReajusteMediaAposentadoria getReajusteRecebido();

    BigDecimal getValor();

    Date getDataRegistro();

    void setInicioVigencia(Date inicioVigencia);

    void setFinalVigencia(Date finalVigencia);

    void setVinculoFP(VinculoFP vinculoFP);

    void setEventoFP(EventoFP eventoFP);

    void setReajusteRecebido(ReajusteMediaAposentadoria reajusteRecebido);

    void setValor(BigDecimal valor);

    void setDataRegistro(Date dataRegistro);

}
