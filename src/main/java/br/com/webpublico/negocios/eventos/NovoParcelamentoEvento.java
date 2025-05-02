package br.com.webpublico.negocios.eventos;

import br.com.webpublico.entidades.ProcessoParcelamento;

public class NovoParcelamentoEvento {

    private final ProcessoParcelamento processoParcelamento;

    public NovoParcelamentoEvento(ProcessoParcelamento processoParcelamento) {
        this.processoParcelamento = processoParcelamento;
    }

    public ProcessoParcelamento getProcessoParcelamento() {
        return processoParcelamento;
    }
}
