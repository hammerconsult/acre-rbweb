package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ArquivoPisPasep;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * @author Claudio
 */
@Stateless
public class ArquivoPisPasepFacade extends AbstractFacade<ArquivoPisPasep> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private static int DIGIT_COUNT = 11; //170.33259.50-4 numero do pis/pasep
    @EJB
    private PessoaFacade pessoaFacade;

    public ArquivoPisPasepFacade() {
        super(ArquivoPisPasep.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public Long getUltimaSequenciaMaisUm() {
        String hql = "select max(arq.sequencia)+1 as ultimaSequencia from ArquivoPisPasep arq ";
        Query q = em.createQuery(hql);

        Long ultimaSequencia = (Long) q.getSingleResult();

        if (ultimaSequencia == null) {
            ultimaSequencia = 1L;
        }

        return ultimaSequencia;


    }

    public static boolean isValid(String pisOrPasep) {
        if (pisOrPasep == null) {
            return false;
        }
        String n = pisOrPasep.replaceAll("[^0-9]*", "");
        //boolean isPis = n.length() == PIS_DIGITS;
        //boolean isPasep = n.length() == PASEP_DIGITS;
        if (n.length() != DIGIT_COUNT) {
            return false;
        }
        int i;          // just count
        int digit;      // A number digit
        int coeficient; // A coeficient
        int sum;        // The sum of (Digit * Coeficient)
        int foundDv;    // The found Dv (Chek Digit)
        int dv = Integer.parseInt(String.valueOf(n.charAt(n.length() - 1)));

        sum = 0;
        coeficient = 2;
        for (i = n.length() - 2; i >= 0; i--) {
            digit = Integer.parseInt(String.valueOf(n.charAt(i)));

            sum += digit * coeficient;
            coeficient++;
            if (coeficient > 9) {
                coeficient = 2;
            }
        }
        foundDv = 11 - sum % 11;
        if (foundDv >= 10) {
            foundDv = 0;
        }
        return dv == foundDv;
    }


    public static boolean identificaNIT(String numero) {
        //as iniciais do NIT são de 109 a 119
        int iniciaisNIT = Integer.parseInt("109");
        int iniciaisASeremValidadas = Integer.parseInt(numero.substring(0, 2));
        for (int i = 0; i <= 10; i++) {
            if (iniciaisASeremValidadas == iniciaisNIT) {
                return true;
            }
            iniciaisNIT++;
        }

        if ((iniciaisNIT + 48) == 167) {
            return true;
        }

        if ((iniciaisNIT + 49) == 168) {
            return true;
        }

        if ((iniciaisNIT + 148) == 267) {
            return true;
        }

        return false;
    }
}
