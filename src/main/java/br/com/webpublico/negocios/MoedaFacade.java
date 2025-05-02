/*
 * Codigo gerado automaticamente em Fri Feb 11 13:51:59 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.IndiceEconomico;
import br.com.webpublico.entidades.Moeda;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.singletons.CacheTributario;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Stateless
public class MoedaFacade extends AbstractFacade<Moeda> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private CacheTributario cacheTributario;

    public MoedaFacade() {
        super(Moeda.class);
    }

    @Override
    public void salvar(Moeda entity) {
        super.salvar(entity);
        cacheTributario.init();
    }

    @Override
    public void salvarNovo(Moeda entity) {
        super.salvarNovo(entity);
        cacheTributario.init();
    }

    private static BigDecimal converterToReal(BigDecimal valueUFM, BigDecimal valorReal) {
        return valueUFM.multiply(valorReal);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public boolean existeMoeda(Moeda moeda) {
        Query q = em.createQuery("from Moeda moeda where " +
                " moeda.ano = :ano and moeda.mes = :mes " +
                " and moeda.indiceEconomico = :indice " +
                " and trim(lower(moeda.descricao)) = trim(lower(:descricao))" +
                " and moeda.id <> :id ");
        q.setParameter("mes", moeda.getMes());
        q.setParameter("ano", moeda.getAno());
        q.setParameter("indice", moeda.getIndiceEconomico());
        q.setParameter("descricao", moeda.getDescricao());
        q.setParameter("id", moeda.getId());
        return !q.getResultList().isEmpty();
    }

    public Moeda getMoedaPorIndiceData(IndiceEconomico indice, Date data) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        //mes vai de 0-11, por isso o +1
        return getMoedaPorIndiceAnoMes(indice, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1);
    }

    public Moeda getMoedaPorIndiceAnoMes(IndiceEconomico indice, Integer ano, Integer mes) {
        Query q = em.createQuery("from Moeda m where m.indiceEconomico = :indice and "
            + "m.ano = :ano and m.mes = :mes");
        q.setParameter("indice", indice);
        q.setParameter("ano", ano);
        q.setParameter("mes", mes);
        List<Moeda> resultados = q.getResultList();
        //retorna o 1o resultado, pois não deveria ter + de 1, e o singleResult retorna exception se tiver + de 1 ou nenhum
        if (!resultados.isEmpty()) {
            return resultados.get(0);
        } else {
            return null;
        }
    }

    public Moeda getMoedaPorIndiceAno(IndiceEconomico indice, Integer ano) {
        Query q = em.createQuery("from Moeda m where m.indiceEconomico = :indice and "
            + "m.ano = :ano");
        q.setParameter("indice", indice);
        q.setParameter("ano", ano);
        List<Moeda> resultados = q.getResultList();
        if (!resultados.isEmpty()) {
            return resultados.get(0);
        } else {
            return null;
        }
    }

    public BigDecimal recuperaValorVigenteUFM() {
        Calendar calendario = Calendar.getInstance();
        Query q = em.createQuery("select m.valor from Moeda m where m.indiceEconomico.descricao = 'UFM' and "
                + "m.ano = :ano and m.mes = :mes");
        q.setParameter("ano", calendario.get(Calendar.YEAR));
        q.setParameter("mes", calendario.get(Calendar.MONTH) + 1);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (BigDecimal) q.getSingleResult();
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal recuperaValorUFMParaData(Date data) {
        if (data != null) {
            Calendar calendario = Calendar.getInstance();
            calendario.setTime(data);
            Query q = em.createQuery("select m.valor from Moeda m where m.indiceEconomico.descricao = 'UFM' and "
                + "m.ano = :ano and m.mes = :mes");
            q.setParameter("ano", calendario.get(Calendar.YEAR));
            q.setParameter("mes", (calendario.get(Calendar.MONTH) + 1));
            List resultList = q.getResultList();
            if (!resultList.isEmpty()) {
                return (BigDecimal) resultList.get(0);
            }
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal converterToUFMVigente(BigDecimal value) throws UFMException {
        BigDecimal valorUFMVigente = recuperaValorVigenteUFM();
        if (!valorUFMVigente.equals(BigDecimal.ZERO)) {
            return value.divide(valorUFMVigente, 6, RoundingMode.HALF_UP);
        } else {
            throw new UFMException(" Atenção! Não foi encontrado valor UFM para a data " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        }
    }

    public BigDecimal converterToUFMVigente(BigDecimal value, BigDecimal valorUFMVigente) throws UFMException {
        if (!valorUFMVigente.equals(BigDecimal.ZERO)) {
            return value.divide(valorUFMVigente, 4, RoundingMode.HALF_EVEN);
        } else {
            throw new UFMException(" Atenção! Não foi encontrado valor UFM para a data " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        }
    }

    public BigDecimal converterToUFMComData(BigDecimal value, Date date) throws UFMException {
        BigDecimal valorUFMVigente = recuperaValorUFMParaData(date);
        if (!valorUFMVigente.equals(BigDecimal.ZERO)) {
            return value.divide(valorUFMVigente, 4, RoundingMode.HALF_EVEN);
        } else {
            throw new UFMException(" Atenção! Não foi encontrado valor UFM para a data " + new SimpleDateFormat("dd/MM/yyyy").format(date));
        }
    }

    public BigDecimal converterToReal(BigDecimal valueUFM) throws UFMException {
        BigDecimal valorUFMVigente = this.recuperaValorVigenteUFM();
        if (valorUFMVigente.equals(BigDecimal.ZERO)) {
            throw new UFMException(" Atenção! Não foi encontrado valor UFM para a data " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        } else {
            return valorUFMVigente.multiply(valueUFM).setScale(2, RoundingMode.HALF_EVEN);
        }
    }

    public BigDecimal converterToRealPorExercicio(BigDecimal valueUFM, Exercicio exercicio) throws UFMException {
        BigDecimal valorUFMVigente = this.recuperaValorUFMPorExercicio(exercicio.getAno());
        if (valorUFMVigente.equals(BigDecimal.ZERO)) {
            throw new UFMException(" Atenção! Não foi encontrado valor UFM para o exercício " + exercicio.getAno());
        } else {
            return valorUFMVigente.multiply(valueUFM).setScale(2, RoundingMode.HALF_EVEN);
        }
    }

    public BigDecimal recuperaValorUFMPorExercicio(Integer ano) {
        Calendar calendario = Calendar.getInstance();
        Query q = em.createQuery("select m.valor from Moeda m where m.indiceEconomico.descricao = 'UFM' and "
                + "m.ano = :ano and m.mes = :mes");
        q.setParameter("ano", ano);
        q.setParameter("mes", calendario.get(Calendar.MONTH) + 1);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (BigDecimal) q.getSingleResult();
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal recuperaValorPorExercicio(Integer ano, Integer mes) {
        Calendar calendario = Calendar.getInstance();
        Query q = em.createQuery("select m.valor from Moeda m "
                + " where m.ano = :ano and m.mes = :mes");
        q.setParameter("ano", ano);
        q.setParameter("mes", mes);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (BigDecimal) q.getSingleResult();
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal buscarValorUFMParaAno(Integer ano) {
        Calendar calendario = Calendar.getInstance();
        calendario.set(Calendar.YEAR, ano);
        calendario.set(Calendar.MONTH, 0);

        Query q = em.createQuery("select m.valor from Moeda m where m.indiceEconomico.descricao = 'UFM' and "
            + "m.ano = :ano and m.mes = :mes");
        q.setParameter("ano", calendario.get(Calendar.YEAR));
        q.setParameter("mes", (calendario.get(Calendar.MONTH) + 1));
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return (BigDecimal) q.getSingleResult();
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal converterToUFMParaExercicio(BigDecimal value, Exercicio exercicio) throws UFMException {
        if (exercicio != null) {
            BigDecimal valorUFMVigente = recuperaValorUFMPorExercicio(exercicio.getAno());
            if (!valorUFMVigente.equals(BigDecimal.ZERO)) {
                BigDecimal divide = value.divide(valorUFMVigente, 4, RoundingMode.HALF_EVEN);
                return divide;
            } else {
                throw new UFMException(" Atenção! Não foi encontrado valor UFM para a data o exercicío de " + exercicio.getAno());
            }
        }
        return new BigDecimal(BigInteger.ZERO);
    }

    public BigDecimal calcularIndiceAtualizaoDataAteHoje(Date dataBase) {
        BigDecimal ufmDataBase = recuperaValorUFMParaData(dataBase);
        BigDecimal ufmHoje = recuperaValorVigenteUFM();
        BigDecimal indice = (ufmDataBase != null && ufmDataBase.compareTo(BigDecimal.ZERO) > 0) &&
            (ufmHoje != null && ufmHoje.compareTo(BigDecimal.ZERO) > 0) ?
            ufmHoje.divide(ufmDataBase, 4, BigDecimal.ROUND_HALF_UP) : BigDecimal.ONE;
        return indice;
    }
}
