package br.com.webpublico.negocios.tributario.geradoresreferencaparcela;

import br.com.webpublico.entidades.CalculoLicenciamentoAmbiental;
import br.com.webpublico.entidades.ProcessoCalculoLicenciamentoAmbiental;

public class GeraReferenciaLicenciamentoAmbiental extends GeraReferencia<CalculoLicenciamentoAmbiental> {
    @Override
    protected String getReferencia() {
        ProcessoCalculoLicenciamentoAmbiental processoCalculoLicenciamentoAmbiental = (ProcessoCalculoLicenciamentoAmbiental) calculo.getProcessoCalculo();
        return processoCalculoLicenciamentoAmbiental.getDescricao();
    }
}
