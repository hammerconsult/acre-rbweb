package br.com.webpublico.negocios.tributario.geradoresreferencaparcela;

import br.com.webpublico.entidades.CalculoCompensacao;
import br.com.webpublico.entidades.ItemCompensacao;

import java.math.BigDecimal;


public class GeraReferenciaProcessoCompensacao extends GeraReferencia<CalculoCompensacao> {

    @Override
    protected String getReferencia() {
        for (ItemCompensacao itemCompensacao : ((CalculoCompensacao) calculo).getCompensacao().getItens()) {
            if (itemCompensacao.getValorCompensado().compareTo(BigDecimal.ZERO) > 0) {
                return itemCompensacao.getParcela().getSituacaoAtual().getReferencia();
            }
        }
        return "Exercício: " + calculo.getProcessoCalculo().getExercicio().getAno();
    }

}
