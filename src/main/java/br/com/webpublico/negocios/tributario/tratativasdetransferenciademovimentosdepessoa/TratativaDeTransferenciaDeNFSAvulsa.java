package br.com.webpublico.negocios.tributario.tratativasdetransferenciademovimentosdepessoa;

import br.com.webpublico.entidades.Calculo;
import br.com.webpublico.entidades.CalculoNFSAvulsa;
import br.com.webpublico.entidades.NFSAvulsa;
import br.com.webpublico.negocios.tributario.services.ServiceTratativaDeTransferenciaDeMovimentoDePessoa;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by tributario on 27/10/2015.
 */
public class TratativaDeTransferenciaDeNFSAvulsa extends TratativaDeTransferenciaDeMovimentoDePessoa<CalculoNFSAvulsa> {
    @Autowired
    private ServiceTratativaDeTransferenciaDeMovimentoDePessoa service;
    @Override
    public Object recuperarMovimentoCalculo(Calculo calculo) {
        return service.recuperarNFSAvulsaPeloCalculo(calculo);
    }
}
