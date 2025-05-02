package br.com.webpublico.interfaces;

import br.com.webpublico.entidades.AutoInfracaoFiscal;
import br.com.webpublico.entidades.ProcessoCalculo;

public interface CalculoAutoInfracao {

    public AutoInfracaoFiscal getAutoInfracaoFiscal();

    public ProcessoCalculo getProcessoCalculo();
}
