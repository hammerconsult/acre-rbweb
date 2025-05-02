package br.com.webpublico.negocios;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.concurrent.Future;

@Stateless
public class AssincronoFacade {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Asynchronous
    public Future<String> processar(int quantidade) {
        for (int x = 1; x <= quantidade; x++) {
            try {
                //System.out.println("Sleep ");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return new AsyncResult<String>("Fail ");
            }
        }
        return new AsyncResult<String>("Success ");
    }
}
