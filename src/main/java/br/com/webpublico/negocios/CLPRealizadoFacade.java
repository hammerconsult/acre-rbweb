/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CLP;
import br.com.webpublico.entidades.CLPRealizado;
import br.com.webpublico.entidades.TagOCC;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * @author venon
 */
@Stateless
public class CLPRealizadoFacade extends AbstractFacade<CLPRealizado> {

    @EJB
    private SaldoContaContabilFacade saldoContaContabilFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CLPRealizadoFacade() {
        super(CLPRealizado.class);
    }

    public void geraContabilizacao(CLPRealizado cr) throws ExcecaoNegocioGenerica {
//        try {
//            List<LancamentoContabil> listaLanc = new ArrayList<LancamentoContabil>();
//            Boolean execObrigatorio = Boolean.FALSE;
//            BigDecimal debito = new BigDecimal(BigInteger.ZERO);
//            BigDecimal credito = new BigDecimal(BigInteger.ZERO);
//            String erro;
//            Conta contaCredito = null;
//            Conta contaDebito = null;
//            for (LCP cci : cr.getClp().getLcps()) {
//                execObrigatorio = Boolean.FALSE;
////                if (cci.getcLPTag() == null) {
////                   throw new ExcecaoNegocioGenerica("Erro: Lançamento Padronizado sem Tag definida");
////                }
//                for (CLPConfiguracaoParametro ccp : cr.getcLPConfiguracaoParametro()) {
//                    if (ccp.getcLPTag() == null) {
//                        throw new ExcecaoNegocioGenerica("Erro: Lançamento Padronizado sem Tag definida");
//                    }
//                    if (ccp.getcLPTag().equals(cci.getcLPTag())) {
//                        if (cci.getContaCredito() != null) {
//                            contaCredito = cci.getContaCredito();
//                        } else if (ccp.getContaCredito() != null) {
//                            contaCredito = ccp.getContaCredito();
//                        } else {
//                            throw new ExcecaoNegocioGenerica("Erro: Existem TAGs sem conta contábil prevista!");
//                        }
//                        if (cci.getContaDebito() != null) {
//                            contaDebito = cci.getContaDebito();
//                        } else if (ccp.getContaDebito() != null) {
//                            contaDebito = ccp.getContaDebito();
//                        } else {
//                            throw new ExcecaoNegocioGenerica("Erro: Existem TAGs sem conta contábil prevista!");
//                        }
//                        execObrigatorio = Boolean.TRUE;
//                        credito = credito.add(ccp.getValor());
//                        debito = debito.add(ccp.getValor());
//                        listaLanc.add(new LancamentoContabil(cr.getDataCLPRealizado(), ccp.getValor(), contaCredito, TipoLancamentoContabil.CREDITO, null, ccp.getComplementoHistorico(), ccp.getUnidadeOrganizacional(), contaDebito, ccp, cci));
//                        listaLanc.add(new LancamentoContabil(cr.getDataCLPRealizado(), ccp.getValor(), contaDebito, TipoLancamentoContabil.DEBITO, null, ccp.getComplementoHistorico(), ccp.getUnidadeOrganizacional(), contaCredito, ccp, cci));
//                        //System.out.println("Débito conta: " + contaDebito + " - Crédito Conta: " + contaCredito + " valor: " + ccp.getValor());
//
//                    }
//                }
//                if (cci.getObrigatorio() == true && execObrigatorio == false) {
//                    erro = "Erro: existem TAGs obrigatórias que não foram executadas!";
//                    throw new ExcecaoNegocioGenerica(erro);
//                }
//            }
//            if (debito.subtract(credito).compareTo(BigDecimal.ZERO) != 0) {
//                erro = "Erro: Os saldos de crédito e débito não fecham nos lançamentos contábeis!";
//                throw new ExcecaoNegocioGenerica(erro);
//            }
//            em.persist(cr);
//            for (LancamentoContabil lcont : listaLanc) {
//                em.persist(lcont);
//                saldoContaContabilFacade.geraSaldoContaContabil(lcont.getDataLancamento(), lcont.getContaContabil(), lcont.getUnidadeOrganizacional(), lcont.getTipoLancamentoContabil(), lcont.getValor());
//            }
//        } catch (ExcecaoNegocioGenerica ex) {
//            throw new ExcecaoNegocioGenerica(ex.getMessage());
//        }
    }

    public CLP getClpPorEvento(String e) {
        Query q = em.createNativeQuery("SELECT cc.* FROM clpconfiguracao cc "
                + "INNER JOIN eventocontabil ec ON cc.eventocontabil_id = ec.id "
                + "WHERE ec.descricao = :param OR ec.codigo = :param", CLP.class);
        q.setParameter("param", e.trim());
        CLP cc = new CLP();
        cc = (CLP) q.getSingleResult();
        if (cc.getId() != null) {
            cc.getLcps().size();
            return cc;
        } else {
            //erro... nenhuma CLP encontrada para este evendo contábil.
            return new CLP();
        }
    }

    public TagOCC getRecuperaClpTagPorCodigoDescricao(String e) {
        Query q = em.createNativeQuery("SELECT TAG.* "
                + " FROM CLPCONFIGURACAO CC"
                + " INNER JOIN CLPCONFIGURACAOITEM CCI ON CCI.CLPCONFIGURACAO_ID = CC.ID"
                + " INNER JOIN CLPTAG TAG ON CCI.CLPTAG_ID = TAG.ID"
                + " WHERE TAG.CODIGO = :param OR TAG.DESCRICAO = :param", TagOCC.class);
        q.setParameter("param", e.trim());
        TagOCC tag = new TagOCC();
        tag = (TagOCC) q.getSingleResult();
        if (tag.getId() != null) {
            return tag;
        } else {
            return new TagOCC();
        }
    }

    public SaldoContaContabilFacade getSaldoContaContabilFacade() {
        return saldoContaContabilFacade;
    }
}
