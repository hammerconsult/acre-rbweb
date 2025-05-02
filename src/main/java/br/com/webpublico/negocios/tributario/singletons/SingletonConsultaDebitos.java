package br.com.webpublico.negocios.tributario.singletons;

import br.com.webpublico.controle.ConsultaDebitoControlador;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@AccessTimeout(value = 5000)
public class SingletonConsultaDebitos implements Serializable {

    private final Logger log = LoggerFactory.getLogger(SingletonConsultaDebitos.class);

    private Map<UUID, List<BigDecimal>> cacheCadastros;

    @Lock(LockType.WRITE)
    public void publicarCadastros(UUID uuid, List<BigDecimal> idsCadastros) {
        if (cacheCadastros == null) {
            cacheCadastros = Maps.newHashMap();
        }
        cacheCadastros.put(uuid, idsCadastros);
    }

    @Lock(LockType.WRITE)
    public Integer getTotalCalcular(UUID uuid) {
        if (cacheCadastros != null && cacheCadastros.get(uuid) != null) {
            return cacheCadastros.get(uuid).size();
        }
        return 0;
    }

    @Lock(LockType.WRITE)
    public List<BigDecimal> getProximosCalcular(UUID uuid) {
        if (cacheCadastros != null && cacheCadastros.get(uuid) != null && cacheCadastros.get(uuid).size() > 0) {
            int contador = 0;
            List<BigDecimal> ids = Lists.newArrayList();
            while (contador < 500 && !cacheCadastros.get(uuid).isEmpty()) {
                ids.add(cacheCadastros.get(uuid).remove(0));
                contador++;
            }
            return ids;
        }
        return Lists.newArrayList();
    }
}
