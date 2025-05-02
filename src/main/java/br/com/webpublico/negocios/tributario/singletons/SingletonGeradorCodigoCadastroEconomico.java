package br.com.webpublico.negocios.tributario.singletons;

import br.com.webpublico.negocios.ExcecaoNegocioGenerica;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;


@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@AccessTimeout(value = 5000)
public class SingletonGeradorCodigoCadastroEconomico implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private Long ultimaInscricao = null;

    @Lock(LockType.WRITE)
    public Long getProximaInscricao() {
        if (ultimaInscricao == null) {
            ultimaInscricao = buscarUltimaInscricao();
        }
        ultimaInscricao = ultimaInscricao + 1;
        return ultimaInscricao;
    }

    private Long buscarUltimaInscricao() {
        try {
            Query consulta = em.createNativeQuery("select to_number(max(inscricaoCadastral)) from cadastroeconomico ce " +
                " inner join pessoafisica pf on pf.id = ce.pessoa_id ");
            BigDecimal resultado = (BigDecimal) consulta.getSingleResult();
            return resultado.longValue();
        } catch (ClassCastException e) {
            throw new ExcecaoNegocioGenerica("Não foi possível converter a inscrição para Long!");
        }
    }

}
