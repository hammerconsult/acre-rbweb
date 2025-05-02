package br.com.webpublico.singletons;

import br.com.webpublico.enums.SituacaoParcela;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by clovis on 29/06/2016.
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class SingletonConferenciaRelatorio implements Serializable {

    private static Logger logger = LoggerFactory.getLogger(SingletonConferenciaRelatorio.class);
    private Map<Long, BigDecimal> cacheParcelas;
    private List<SituacaoParcela> listaSituacoes;

    @PostConstruct
    private void init() {
        cacheParcelas = Maps.newHashMap();
    }

    @Lock(LockType.WRITE)
    public void publicar(Long cadastro, BigDecimal valor) {
        if (cacheParcelas.get(cadastro) != null) {
            valor = valor.add(cacheParcelas.get(cadastro));
        }
        cacheParcelas.put(cadastro, valor);
    }

    @Lock(LockType.WRITE)
    public void limparCache() {
        cacheParcelas = Maps.newHashMap();
    }

    @Lock(LockType.WRITE)
    public void limparSituacoes() {
        listaSituacoes = Lists.newArrayList();
    }

    public void compararCadastro(Long cadastroComparar, BigDecimal valorComparar) {
        if (cacheParcelas.get(cadastroComparar) != null) {
            BigDecimal valor = cacheParcelas.get(cadastroComparar);
            if (valor.compareTo(valorComparar) != 0) {
                logger.debug("Diferença - Valor Dívida: " + cadastroComparar + " Valor Singleton: " + valor + " Valor Comparar: " + valorComparar + " Dif: " + valor.subtract(valorComparar));
            }
            cacheParcelas.remove(cadastroComparar);
        } else {
            logger.debug("Cadastro Não Encontrado: " + cadastroComparar);
        }
    }

    public void imprimirResiduais() {
        if (cacheParcelas != null) {
            for (Long s : cacheParcelas.keySet()) {
                logger.debug("Residual {}", s);
            }
        }
    }

    public void adicionarSituacao(SituacaoParcela situacaoParcela) {
        if (!listaSituacoes.contains(situacaoParcela)) {
            listaSituacoes.add(situacaoParcela);
        }
    }

    public void imprimirSituacoes(String origem) {
        if (listaSituacoes != null) {
            logger.debug(origem + ": Situacoes: {}", listaSituacoes);
        }
    }


}
