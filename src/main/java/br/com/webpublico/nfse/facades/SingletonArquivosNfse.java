package br.com.webpublico.nfse.facades;

import br.com.webpublico.nfse.domain.ArquivoNFSETRE;
import br.com.webpublico.util.AssistenteBarraProgresso;
import com.beust.jcommander.internal.Maps;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * Created by hudson on 15/09/15.
 */

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class SingletonArquivosNfse implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    private Map<ArquivoNFSETRE, AcompanhamentoArquivoTRE> arquivosTRE = Maps.newHashMap();

    @Lock(LockType.READ)
    public void iniciarArquivoTRE(ArquivoNFSETRE arquivo, AssistenteBarraProgresso assistente, Future future) {
        if (!arquivosTRE.containsKey(arquivo)) {
            arquivosTRE.put(arquivo, new AcompanhamentoArquivoTRE(assistente, future));
        }
    }

    @Lock(LockType.READ)
    public void finalizarArquivoTRE(ArquivoNFSETRE arquivo) {
        arquivosTRE.remove(arquivo);
    }

    @Lock(LockType.READ)
    public AcompanhamentoArquivoTRE getArquivosTRE(ArquivoNFSETRE arquivo) {
        if (arquivosTRE.containsKey(arquivo)) {
            return arquivosTRE.get(arquivo);
        }
        return null;
    }


    public class AcompanhamentoArquivoTRE {
        AssistenteBarraProgresso assistente;
        Future future;

        public AcompanhamentoArquivoTRE(AssistenteBarraProgresso assistente, Future future) {
            this.assistente = assistente;
            this.future = future;
        }

        public AssistenteBarraProgresso getAssistente() {
            return assistente;
        }

        public Future getFuture() {
            return future;
        }
    }
}
