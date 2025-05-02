/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.test.integracao;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ReferenciaFPFuncao;
import br.com.webpublico.negocios.FuncoesFolhaFacade;
import org.junit.Test;

import javax.naming.NamingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author gecen
 * @author Mailson Isaque Cardoso Peixe
 */
public class FuncoesFolhaFacadeIT extends DataBaseIT {

    private static VinculoFP vinculo;
    private static FuncoesFolhaFacade funcoesFacade;

    @Test
    public void testObterRefereciaFP() throws NamingException {
        //System.out.println();
        ReferenciaFPFuncao obterReferenciaValorFP = funcoesFacade.obterReferenciaValorFP(vinculo, "3", 11, 2011);
        assertEquals(-1.0, obterReferenciaValorFP.getPercentual(), 0.0);
        assertEquals(157.47, obterReferenciaValorFP.getValor(), 0.0);
        assertNotNull(funcoesFacade);


    }

    @Test
    public void testSalarioBase() throws NamingException {
        Double valor = funcoesFacade.salarioBase(vinculo);
        //System.out.println("Valor para o Vinculo " + vinculo.getDescricao() + " : " + valor);
        assertNotNull(funcoesFacade);
        assertNotNull(valor);
        assertEquals(1800.0D, valor, 0.0);
    }

    @Test
    public void testObterModalidadeContratoFP() throws NamingException {
        ModalidadeContratoFP modalidade = funcoesFacade.obterModalidadeContratoFP(vinculo);
        //System.out.println("Modalidade para o ContratoFP " + vinculo.getDescricao() + " : " + modalidade);
        assertNotNull(funcoesFacade);

        assertEquals("CONTRATO TEMPORÁRIO", modalidade.getDescricao());
    }

    @Test
    public void testObterTipoRegime() throws NamingException {
        TipoRegime tipoRegime = funcoesFacade.obterTipoRegime(vinculo);
        //System.out.println("Tipo de Regime " + vinculo.getDescricao() + " : " + tipoRegime);
        assertEquals("ESTATUTÁRIO", tipoRegime.getDescricao());
        assertNotNull(funcoesFacade);
    }

    //    @Test
//    public void testObterModalidadeBeneficioFP() throws NamingException {
//        //System.out.println();
//        getVinculo();
//        FuncoesFolhaFacade funcoesFacade = findReference(FuncoesFolhaFacade.class);
//        ModalidadeBeneficioFP modalidadeBeneficio = funcoesFacade.obterModalidadeBeneficioFP(vinculo);
//        //System.out.println("Modalidade para o Beneficio " + vinculo.getDescricao() + " : " + modalidadeBeneficio);
//        assertNotNull(funcoesFacade);
//        //System.out.println();
//    }
//    @Test
//    public void testObterValorBeneficioFP() throws NamingException {
//        //System.out.println();
//        getVinculo();
//        FuncoesFolhaFacade funcoesFacade = findReference(FuncoesFolhaFacade.class);
//        Double valorBeneficio = funcoesFacade.obterValorBeneficioFP(vinculo);
//        //System.out.println("Valor Beneficio " + vinculo.getDescricao() + " : " + valorBeneficio);
//        assertNotNull(funcoesFacade);
//        //System.out.println();
//    }
    @Test
    public void testDiasTrabalhados() throws NamingException {
        Double diasTrabalhados = funcoesFacade.diasTrabalhados(vinculo, 10, 2011, TipoDiasTrabalhados.NORMAL, null, null, null);
        //System.out.println("Dias Trabalhados " + vinculo.getDescricao() + " : " + diasTrabalhados);
        assertEquals(19.0, diasTrabalhados, 0.0);
        assertNotNull(funcoesFacade);
    }

    @Test
    public void testObterItensBaseFP() throws NamingException {
        List<ItemBaseFP> itemBase = funcoesFacade.obterItensBaseFP("1001");
        assertEquals("1001", itemBase.get(0).getBaseFP().getCodigo());
        assertNotNull(funcoesFacade);
    }

    @Test
    public void testObterTipoPrevidenciaFP() throws NamingException {
        String tipoPrevidencia = funcoesFacade.obterTipoPrevidenciaFP(vinculo, 11, 2011);
        //System.out.println("Tipo Previdência" + vinculo.getDescricao() + " : " + tipoPrevidencia);
        assertEquals("2", tipoPrevidencia);
        assertNotNull(funcoesFacade);
    }

    @Test
    public void testObterReferenciaFaixaFP() throws NamingException {
        ReferenciaFPFuncao referenciaFpFuncao = funcoesFacade.obterReferenciaFaixaFP(vinculo, "3", 20.0, 11, 2011);
        //System.out.println("RefefrenciaFP Funcao Percentual: " + referenciaFpFuncao.getPercentual());
        //System.out.println("RefefrenciaFP Funcao Valor: " + referenciaFpFuncao.getValor());

        assertNotNull(funcoesFacade);
        assertEquals(-1.0, referenciaFpFuncao.getPercentual(), 0.0);
        assertEquals(-1.0, referenciaFpFuncao.getValor(), 0.0);
    }

    @Test
    public void testContaLancamento() throws NamingException {
        List<VinculoFP> vinculos = em.createQuery("from VinculoFP").getResultList();
        for (VinculoFP vincu : vinculos) {
            Integer lancamentos = funcoesFacade.contaLancamento(vincu, "101", 11, 2011);
            //System.out.println("Numero Lancamento: " + lancamentos);
            assertNotNull(funcoesFacade);
            assertEquals(0, lancamentos, 0);
        }
    }

    @Test
    public void testRecuperaQuantificacaoLancamentoTipoReferencia() throws NamingException {
        Double tipoReferencia = funcoesFacade.recuperaQuantificacaoLancamentoTipoReferencia(vinculo, "101", 11, 2011);
        //System.out.println("Tipo Referencia: " + tipoReferencia);
        assertNotNull(funcoesFacade);
        assertEquals(-1, tipoReferencia, 0);
    }

    @Test
    public void testRecuperaQuantificacaoLancamentoTipoValor() throws NamingException {
        Double tipoValor = funcoesFacade.recuperaQuantificacaoLancamentoTipoValor(vinculo, "101", 11, 2011);
        //System.out.println("Tipo Valor: " + tipoValor);
        assertNotNull(funcoesFacade);
        assertEquals(-1, tipoValor, 0);
    }

    @Test
    public void testObterHoraMensal() throws NamingException {
        Double horaMensal = funcoesFacade.obterHoraMensal(vinculo);
        //System.out.println("Quantidade Horas Mensal: " + horaMensal);
        assertNotNull(funcoesFacade);
        assertEquals(160.0, horaMensal, 0);
    }
//
//    @Test
//    public void testTemSindicato() throws NamingException {
//        boolean temSindicato = funcoesFacade.temSindicato(1l, vinculo);
//        //System.out.println("Tem Sindicato ? : " + temSindicato);
//        assertNotNull(funcoesFacade);
//        assertFalse(temSindicato);
//    }

    @Test
    public void testObterNumeroDependentes() throws NamingException {
        List<VinculoFP> vinculos = em.createQuery("from VinculoFP").getResultList();
        List<TipoDependente> tipos = em.createQuery("from TipoDependente").getResultList();
        for (VinculoFP v : vinculos) {
            for (TipoDependente t : tipos) {
                Integer quantidade = funcoesFacade.obterNumeroDependentes(t.getCodigo(), v);
                //System.out.println("Tipo: " + t.getDescricao() + " - " + t.getCodigo() + " VinculoFP: " + v.getId() + " Quantidade Dependentes: " + quantidade);
                assertNotNull(funcoesFacade);
                assertEquals(0, quantidade, 0);
            }
        }

    }

    @Test
    public void testTemFeriasConcedidas() throws NamingException {
        //System.out.println();
        boolean temFerias = funcoesFacade.temFeriasConcedidas(vinculo);
        //System.out.println("Tem Ferias Concedidas: " + temFerias);
        assertNotNull(funcoesFacade);
        assertFalse(temFerias);
        //System.out.println();
    }

    @Test
    public void testRecuperarPercentualNaturezaAtividade() throws NamingException {
        //System.out.println();
        Double percentual = funcoesFacade.recuperarPercentualPorTipoNaturezaAtividade(vinculo, 11, 2011, 1);
        //System.out.println("Percentual Natureza Atividade: " + percentual);
        assertNotNull(funcoesFacade);
        assertEquals(0, percentual, 0);
        //System.out.println();
    }

    @Test
    public void testRecuperaValorItemSindicato() throws NamingException {
        BigDecimal valor = funcoesFacade.recuperaValorItemSindicato(vinculo, 1);
        //System.out.println("Valor : " + valor);
        assertEquals(new BigDecimal(BigInteger.ZERO), valor);
        assertNotNull(funcoesFacade);
    }

    @Test
    public void testaRetornoMenorSalario() {
        BigDecimal menorSalario = funcoesFacade.retornaMenorSalario();
        assertEquals(new BigDecimal(1000), menorSalario);
        assertNotNull(funcoesFacade);
    }

    @Test
    public void testRecuperaValorItemAssociacao() throws NamingException {
        BigDecimal valor = funcoesFacade.recuperaValorItemAssociacao(vinculo, 1);
        //System.out.println("Valor : " + valor);
        assertEquals(new BigDecimal(BigInteger.ZERO), valor);
        assertNotNull(funcoesFacade);
    }

    @Test
    public void testIdentificaAposentado() throws NamingException {
        boolean aposentado = funcoesFacade.identificaAposentado(vinculo);
        //System.out.println("É Aposentado : " + aposentado);
        assertNotNull(funcoesFacade);
        assertEquals(false, aposentado);
    }

    @Test
    public void testObterValorAposentadoria() throws NamingException {
        String[] eventos = {"101", "360"};
        Double valorAposentadoria = funcoesFacade.obterValorAposentadoria(vinculo, eventos);
        //System.out.println("Valor da Aposentadoria: " + valorAposentadoria);
        assertNotNull(funcoesFacade);
        assertEquals(0, valorAposentadoria, 0);
    }

    @Test
    public void testIdentificaPensionista() throws NamingException {
        boolean pensionista = funcoesFacade.identificaPensionista(vinculo);
        //System.out.println("É Pensionista : " + pensionista);
        assertNotNull(funcoesFacade);
        assertEquals(false, pensionista);
    }

    @Test
    public void testObterValorPensionista() throws NamingException {
        Double valorPensionista = funcoesFacade.obterValorPensionista(vinculo, null, false);
        //System.out.println("Valor do Pensionista : " + valorPensionista);
        assertNotNull(funcoesFacade);
        assertEquals(0, valorPensionista, 0);
    }

    @Test
    public void testTemOpcaoValeTransporte() throws NamingException {
        boolean temOpcaoValeTransporte = funcoesFacade.temOpcaoValeTransporte(vinculo);
        //System.out.println("Tem Opcao Vale Transporte ? : " + temOpcaoValeTransporte);
        assertNotNull(funcoesFacade);
        assertFalse(temOpcaoValeTransporte);
    }

    public FuncoesFolhaFacadeIT() throws NamingException {
        vinculo = (VinculoFP) em.createQuery("from VinculoFP where id = :param").setParameter("param", 1979458L).getSingleResult();
        funcoesFacade = findReference(FuncoesFolhaFacade.class);
    }

    @Test
    public void testTemFuncaoGratificada() throws NamingException {
        List<VinculoFP> vinculos = em.createQuery("from VinculoFP").getResultList();
        for (VinculoFP v : vinculos) {
            boolean retorno = funcoesFacade.temFuncaoGratificada(v);
            //System.out.println("Tem Funcao Gratificada: " + retorno);
            assertNotNull(funcoesFacade);
            assertNotNull(retorno);
        }
    }

    @Test
    public void testObterValorFuncaoGratificada() throws NamingException {
        List<VinculoFP> vinculos = em.createQuery("from VinculoFP").getResultList();
        for (VinculoFP v : vinculos) {
            if (funcoesFacade.temFuncaoGratificada(v)) {
                Double retorno = funcoesFacade.obterValorFuncaoGratificada(v);
                //System.out.println("Obter Valor Funcao Gratificada: " + retorno);
                assertNotNull(funcoesFacade);
                assertEquals(500, retorno, 0.0);
            }
        }
    }

    @Test
    public void testTemLocalPerigo() throws NamingException {
        List<VinculoFP> vinculos = em.createQuery("from VinculoFP").getResultList();
        for (VinculoFP v : vinculos) {
            boolean retorno = funcoesFacade.temLocalPenoso(v);
            //System.out.println("Tem Local Pegigoso: " + retorno);
            assertNotNull(funcoesFacade);
            assertFalse(retorno);
        }
    }

    @Test
    public void testObterValorLocalPerigo() throws NamingException {
        List<VinculoFP> vinculos = em.createQuery("from VinculoFP").getResultList();
        for (VinculoFP v : vinculos) {
            if (funcoesFacade.temLocalPenoso(v)) {
                Double valor = funcoesFacade.obterValorLocalPenosidade(v);
                //System.out.println("Valor Local Pegigoso: " + valor);
                assertNotNull(funcoesFacade);
                assertEquals(500.0, valor, 0.0);
            }
        }
    }

    @Test
    public void testRecuperaTipoReajusteAposentadoria() throws NamingException {
        String tipo = funcoesFacade.recuperaTipoReajusteAposentadoria(vinculo);
        //System.out.println("Tipo: " + tipo);
        assertEquals("MEDIA", tipo);
        assertNotNull(funcoesFacade);
    }

    @Test
    public void testTemPensaoJudicial() throws NamingException {
        List<VinculoFP> vinculos = em.createQuery("from VinculoFP").getResultList();
        for (VinculoFP v : vinculos) {
            if (funcoesFacade.temPensaoJudicial(v)) {
                Double valor = funcoesFacade.obterParametroPensaoJudicial(v);
                //System.out.println("Parametro Pensao Judicial: " + valor);
                assertNotNull(funcoesFacade);
                assertEquals(500.0, valor, 0.0);
            }
        }
    }

    @Test
    public void testTemFaltas() throws NamingException {
        boolean temFaltas = funcoesFacade.temFaltas(vinculo, " ", null);
        //System.out.println("Tem Faltas ? : " + temFaltas);
        assertNotNull(funcoesFacade);
        assertFalse(temFaltas);
    }

    @Test
    public void testRecuperaNumeroFaltas() throws NamingException {
        List<VinculoFP> vinculos = em.createQuery("from VinculoFP").getResultList();
        for (VinculoFP v : vinculos) {
            if (funcoesFacade.temFaltas(v, " ", null)) {
                Double faltas = funcoesFacade.recuperaNumeroFaltas(v, "58777", null);
                //System.out.println("Faltas: " + faltas);
                assertNotNull(funcoesFacade);
                assertEquals(1.0, faltas, 0.0);
            }
        }
    }
}
