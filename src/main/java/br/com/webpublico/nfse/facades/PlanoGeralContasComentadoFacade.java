package br.com.webpublico.nfse.facades;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.PlanoGeralContasComentado;
import br.com.webpublico.nfse.domain.PlanoGeralContasComentadoProdutoServico;
import br.com.webpublico.nfse.domain.PlanoGeralContasComentadoTarifaBancaria;
import br.com.webpublico.util.AssistenteBarraProgresso;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class PlanoGeralContasComentadoFacade extends AbstractFacade<PlanoGeralContasComentado> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PlanoGeralContasComentadoFacade() {
        super(PlanoGeralContasComentado.class);
    }

    @Override
    public PlanoGeralContasComentado recuperar(Object id) {
        PlanoGeralContasComentado plano = super.recuperar(id);
        Hibernate.initialize(plano.getCadastroEconomico().getEnquadramentos());
        return plano;
    }

    public List<PlanoGeralContasComentado> buscarContasVigentesPorCadastroEconomico(CadastroEconomico cadastroEconomico) {

        return em.createNativeQuery(" select pgcc.*" +
                        "   from pgcc " +
                        " where pgcc.cadastroeconomico_id = :cadastro " +
                        "   and trunc(current_date) between trunc(pgcc.iniciovigencia) and trunc(pgcc.fimvigencia) " +
                        " order by pgcc.conta ", PlanoGeralContasComentado.class)
                .setParameter("cadastro", cadastroEconomico.getId())
                .getResultList();
    }

    public List<PlanoGeralContasComentado> buscarContasRootVigentes(CadastroEconomico cadastroEconomico) {
        return em.createNativeQuery(" select pgcc.*" +
                                "   from pgcc " +
                                " where pgcc.cadastroeconomico_id = :cadastro " +
                                "   and trunc(current_date) between trunc(pgcc.iniciovigencia) and trunc(pgcc.fimvigencia) " +
                                "   and pgcc.contasuperior_id is null ",
                        PlanoGeralContasComentado.class)
                .setParameter("cadastro", cadastroEconomico.getId())
                .getResultList();
    }

    public List<PlanoGeralContasComentado> buscarContasFilhasVigentes(PlanoGeralContasComentado conta) {

        return em.createNativeQuery(" select pgcc.*" +
                                "   from pgcc " +
                                " where trunc(current_date) between trunc(pgcc.iniciovigencia) and trunc(pgcc.fimvigencia) " +
                                "   and pgcc.contasuperior_id = :conta_id ",
                        PlanoGeralContasComentado.class)
                .setParameter("conta_id", conta.getId())
                .getResultList();
    }

    public void updateContaTributavel(PlanoGeralContasComentado planoGeralContasComentado) {
        em.createNativeQuery(" update pgcc set contatributavel = :conta_tributavel where id = :id ")
                .setParameter("conta_tributavel", planoGeralContasComentado.getContaTributavel())
                .setParameter("id", planoGeralContasComentado.getId())
                .executeUpdate();
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future copiarPgcc(AssistenteBarraProgresso assistente,
                             CadastroEconomico cadastroEconomico,
                             CadastroEconomico cadastroEconomicoDestino) {
        assistente.setDescricaoProcesso("Buscando contas inicias vigentes de " + cadastroEconomico);
        List<PlanoGeralContasComentado> contas = buscarContasRootVigentes(cadastroEconomico);
        if (contas != null && !contas.isEmpty()) {
            for (PlanoGeralContasComentado conta : contas) {
                buscarContasFilhas(conta);
            }
            Integer quantidadeContas = contarQuantidadeContas(0, contas);
            assistente.setDescricaoProcesso("Copiando contas vigentes de " + cadastroEconomico +
                    " para " + cadastroEconomicoDestino);
            assistente.setTotal(quantidadeContas);
            copiarContas(assistente, null, contas, cadastroEconomicoDestino);
        }
        return new AsyncResult(null);
    }

    private Integer contarQuantidadeContas(Integer quantidadeContas, List<PlanoGeralContasComentado> contas) {
        if (contas != null && !contas.isEmpty()) {
            quantidadeContas = quantidadeContas + contas.size();
            for (PlanoGeralContasComentado conta : contas) {
                quantidadeContas = contarQuantidadeContas(quantidadeContas, conta.getContasFilhas());
            }
        }
        return quantidadeContas;
    }

    private void buscarContasFilhas(PlanoGeralContasComentado conta) {
        conta.setContasFilhas(buscarContasFilhasVigentes(conta));
        if (conta.getContasFilhas() != null && !conta.getContasFilhas().isEmpty()) {
            for (PlanoGeralContasComentado contaFilha : conta.getContasFilhas()) {
                buscarContasFilhas(contaFilha);
            }
        }
    }

    private void copiarContas(AssistenteBarraProgresso assistenteBarraProgresso,
                              PlanoGeralContasComentado contaSuperior,
                              List<PlanoGeralContasComentado> contas,
                              CadastroEconomico cadastroEconomicoDestino) {
        if (contas != null) {
            for (PlanoGeralContasComentado conta : contas) {
                PlanoGeralContasComentado contaCopiada = copiarConta(contaSuperior, conta, cadastroEconomicoDestino);
                assistenteBarraProgresso.conta();
                copiarContas(assistenteBarraProgresso, contaCopiada, conta.getContasFilhas(), cadastroEconomicoDestino);
            }
        }
    }

    private PlanoGeralContasComentado copiarConta(PlanoGeralContasComentado contaSuperior,
                                                  PlanoGeralContasComentado conta,
                                                  CadastroEconomico cadastroEconomico) {
        PlanoGeralContasComentado novaConta = new PlanoGeralContasComentado();
        novaConta.setCadastroEconomico(cadastroEconomico);
        novaConta.setContaSuperior(contaSuperior);
        novaConta.setCosif(conta.getCosif());
        novaConta.setConta(conta.getConta());
        novaConta.setDesdobramento(conta.getDesdobramento());
        novaConta.setNome(conta.getNome());
        novaConta.setDescricaoDetalhada(conta.getDescricaoDetalhada());
        novaConta.setInicioVigencia(conta.getInicioVigencia());
        novaConta.setFimVigencia(conta.getFimVigencia());
        novaConta.setServico(conta.getServico());
        novaConta.setCodigoTributacao(conta.getCodigoTributacao());
        novaConta.setContaTributavel(conta.getContaTributavel());

        if (conta.getProdutoServico() != null) {
            PlanoGeralContasComentadoProdutoServico novoProdutoServico = new PlanoGeralContasComentadoProdutoServico();
            novoProdutoServico.setProdutoServicoBancario(conta.getProdutoServico().getProdutoServicoBancario());
            novoProdutoServico.setDescricaoComplementar(conta.getProdutoServico().getDescricaoComplementar());
            novaConta.setProdutoServico(em.merge(novoProdutoServico));
        }
        if (conta.getTarifaBancaria() != null) {
            PlanoGeralContasComentadoTarifaBancaria novaTarifaBancaria = new PlanoGeralContasComentadoTarifaBancaria();
            novaTarifaBancaria.setTarifaBancaria(conta.getTarifaBancaria().getTarifaBancaria());
            novaTarifaBancaria.setInicioVigencia(conta.getTarifaBancaria().getInicioVigencia());
            novaTarifaBancaria.setValorPercentual(conta.getTarifaBancaria().getValorPercentual());
            novaTarifaBancaria.setValorUnitario(conta.getTarifaBancaria().getValorUnitario());
            novaConta.setTarifaBancaria(em.merge(novaTarifaBancaria));
        }
        return em.merge(novaConta);
    }
}
