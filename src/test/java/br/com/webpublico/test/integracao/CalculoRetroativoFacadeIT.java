/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.test.integracao;

import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidadesauxiliares.ResultadoCalculoRetroativoFP;
import br.com.webpublico.interfaces.CalculoRetroativoFacade;

import javax.naming.NamingException;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author peixe
 */
public class CalculoRetroativoFacadeIT extends DataBaseIT {

    private static VinculoFP vinculo;
    private static CalculoRetroativoFacade calculoRetroativoFacade;

    public CalculoRetroativoFacadeIT() throws NamingException {
        vinculo = (VinculoFP) em.createQuery("from VinculoFP where id = :param").setParameter("param", 1979458L).getSingleResult();
        calculoRetroativoFacade = findReference(CalculoRetroativoFacade.class);
    }

//    @BeforeClass
//    public static void setUpClass() throws Exception {
//    }
//
//    @AfterClass
//    public static void tearDownClass() throws Exception {
//    }
//
//    @Before
//    public void setUp() {
//    }
//
//    @After
//    public void tearDown() {
//    }
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void verifica() {
        ResultadoCalculoRetroativoFP rcrfp = calculoRetroativoFacade.verifica(vinculo);

        assertFalse(rcrfp.temCalculoRetroativo());
    }
}
