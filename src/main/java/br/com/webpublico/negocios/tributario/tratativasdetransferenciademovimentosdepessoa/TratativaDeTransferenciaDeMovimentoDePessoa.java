package br.com.webpublico.negocios.tributario.tratativasdetransferenciademovimentosdepessoa;

import br.com.webpublico.entidades.Calculo;
import br.com.webpublico.seguranca.service.UsuarioSistemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.persistence.EntityManager;

/**
 * Created by tributario on 27/10/2015.
 */
public abstract class TratativaDeTransferenciaDeMovimentoDePessoa<T> {
    public T calculo;

    public TratativaDeTransferenciaDeMovimentoDePessoa() {
        dependenciasSpring();
    }

    public void dependenciasSpring() {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    public abstract Object recuperarMovimentoCalculo(Calculo calculo);
}
