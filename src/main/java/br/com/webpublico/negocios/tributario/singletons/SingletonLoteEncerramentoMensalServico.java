package br.com.webpublico.negocios.tributario.singletons;

import br.com.webpublico.nfse.domain.LoteDeclaracaoMensalServico;
import br.com.webpublico.util.AssistenteBarraProgresso;
import com.google.common.collect.Maps;

import javax.ejb.*;
import java.io.Serializable;
import java.util.Map;


@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@AccessTimeout(value = 20000)
public class SingletonLoteEncerramentoMensalServico implements Serializable {

    private Map<Long, AssistenteBarraProgresso> progressoLotes = Maps.newHashMap();

    @Lock(LockType.WRITE)
    public void registrar(AssistenteBarraProgresso assistente,
                          LoteDeclaracaoMensalServico lancamento) {
        progressoLotes.put(lancamento.getId(), assistente);
    }

    @Lock(LockType.WRITE)
    public void finalizar(LoteDeclaracaoMensalServico lancamento) {
        progressoLotes.remove(lancamento.getId());
    }

    @Lock(LockType.WRITE)
    public AssistenteBarraProgresso getAndamento(Long id) {
        if (progressoLotes.get(id) != null) {
            return progressoLotes.get(id);
        }
        return null;
    }
}
