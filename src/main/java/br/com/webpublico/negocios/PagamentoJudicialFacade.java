package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.SituacaoProcessoDebito;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

@Stateless
public class PagamentoJudicialFacade extends AbstractFacade<PagamentoJudicial> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private CadastroRuralFacade cadastroRuralFacade;
    @EJB
    private GeraValorDividaPagamentoJudicial geraDebito;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PagamentoJudicialFacade() {
        super(PagamentoJudicial.class);
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public DividaFacade getDividaFacade() {
        return dividaFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }


    public AtoLegalFacade getAtoLegalFacade() {
        return atoLegalFacade;
    }

    public void encerraProcesso(PagamentoJudicial processo) {
        processo.setSituacao(SituacaoProcessoDebito.FINALIZADO);
        for (PagamentoJudicialParcela parcela : processo.getParcelas()) {
            ParcelaValorDivida pvd = em.find(ParcelaValorDivida.class, parcela.getParcelaValorDivida().getId());
            setaSituacaoDeAcordoComProcesso(pvd, SituacaoParcela.SUBSTITUIDA_POR_COMPENSACAO);
            try {
                geraDebito.geraDebito(geraProcessoCalculoJudicial(processo, pvd, parcela, SituacaoParcela.EM_ABERTO));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        em.merge(processo);
    }

    private ProcessoCalculoJudicial geraProcessoCalculoJudicial(PagamentoJudicial pagamento, ParcelaValorDivida pvd,
                                                                PagamentoJudicialParcela pagamentoJudicialParcela, SituacaoParcela situacaoParcela) {
        CalculoPagamentoJudicial calculoPagamentoJudicial = new CalculoPagamentoJudicial();
        criarCalculo(calculoPagamentoJudicial, pagamento, pagamentoJudicialParcela, pvd, situacaoParcela);
        ProcessoCalculoJudicial processo = new ProcessoCalculoJudicial();
        calculoPagamentoJudicial.setProcessoCalculo(processo);
        calculoPagamentoJudicial.setProcessoCalculoJudicial(processo);
        criaProcessoPagamentoJudicial(processo, calculoPagamentoJudicial, pagamento, pvd);
        return em.merge(processo);
    }

    private void criaProcessoPagamentoJudicial(ProcessoCalculoJudicial processo, CalculoPagamentoJudicial calculoPagamentoJudicial, PagamentoJudicial pagamento, ParcelaValorDivida pvd) {
        processo.getCalculos().add(calculoPagamentoJudicial);
        processo.setCompleto(Boolean.TRUE);
        processo.setDataLancamento(pagamento.getDataCompensacao());
        processo.setDivida(pvd.getValorDivida().getDivida());
        processo.setExercicio(pagamento.getExercicio());
    }


    private void criarCalculo(CalculoPagamentoJudicial calculoPagamentoJudicial, PagamentoJudicial pagamentoJudicial,
                              PagamentoJudicialParcela pagamentoJudicialParcela,
                              ParcelaValorDivida pvd, SituacaoParcela situacaoParcela) {
        calculoPagamentoJudicial.setPagamentoJudicial(pagamentoJudicial);
        calculoPagamentoJudicial.setDataCalculo(pagamentoJudicial.getDataCompensacao());
        calculoPagamentoJudicial.setSimulacao(Boolean.FALSE);
        calculoPagamentoJudicial.setCadastro(pagamentoJudicial.getCadastro());
        calculoPagamentoJudicial.setTipoCalculo(Calculo.TipoCalculo.PROCESSO_COMPENSACAO);
        calculoPagamentoJudicial.setSituacaoParcelaGerada(situacaoParcela);
        calculoPagamentoJudicial.setVencimentoOriginalParcela(pagamentoJudicialParcela.getVencimento());
        calculoPagamentoJudicial.setVencimentoOriginalParcela(pagamentoJudicialParcela.getVencimento());
        calculoPagamentoJudicial.setSequenciaParcela(pagamentoJudicialParcela.getSequencia());
        calculoPagamentoJudicial.setQuantidadeParcela(pagamentoJudicialParcela.getQuantidadeParcela());
        calculoPagamentoJudicial.setDividaAtiva(pagamentoJudicialParcela.getDividaAtiva());
        calculoPagamentoJudicial.setDividaAtivaAjuizada(pagamentoJudicialParcela.getDividaAtivaAjuizada());
        if (calculoPagamentoJudicial.getSituacaoParcelaGerada().equals(SituacaoParcela.EM_ABERTO)) {
            calculoPagamentoJudicial.setReferencia(pagamentoJudicialParcela.getReferencia() + " Processo: " + pagamentoJudicial.getCodigo() + "/" + pagamentoJudicial.getExercicio().getAno());
            calculoPagamentoJudicial.setValorEfetivo(pagamentoJudicialParcela.getValorResidual());
            calculoPagamentoJudicial.setValorReal(pagamentoJudicialParcela.getValorResidual());
            criaItemDoCalculo(pagamentoJudicialParcela, pvd, calculoPagamentoJudicial);
        } else {
            calculoPagamentoJudicial.setReferencia(pagamentoJudicialParcela.getReferencia());
            calculoPagamentoJudicial.setValorEfetivo(pagamentoJudicialParcela.getValorCompensado());
            calculoPagamentoJudicial.setValorReal(pagamentoJudicialParcela.getValorCompensado());
            criaItemDoCalculoResidualCompensado(pagamentoJudicialParcela, pvd, calculoPagamentoJudicial);
        }
        for (CalculoPessoa cp : pvd.getValorDivida().getCalculo().getPessoas()) {
            CalculoPessoa calculoPessoa = new CalculoPessoa();
            calculoPessoa.setPessoa(cp.getPessoa());
            calculoPessoa.setCalculo(calculoPagamentoJudicial);
            calculoPagamentoJudicial.getPessoas().add(calculoPessoa);
        }
    }

    private void criaItemDoCalculo(PagamentoJudicialParcela parcela, ParcelaValorDivida pvd, CalculoPagamentoJudicial calculoPagamentoJudicial) {
        lancaTributo(parcela, pvd, calculoPagamentoJudicial);
    }

    private void criaItemDoCalculoResidualCompensado(PagamentoJudicialParcela parcela, ParcelaValorDivida pvd, CalculoPagamentoJudicial calculoPagamentoJudicial) {
        lancaItensResidualCompensado(parcela, pvd, calculoPagamentoJudicial);
    }

    private void lancaTributo(PagamentoJudicialParcela parcela, ParcelaValorDivida pvd, CalculoPagamentoJudicial calculoPagamentoJudicial) {
        BigDecimal saldo = parcela.getValorCompensado();
        BigDecimal diferenca = BigDecimal.ZERO;
        if (parcela.getJuros().compareTo(BigDecimal.ZERO) > 0 && saldo.compareTo(parcela.getJuros()) <= 0) {
            saldo = saldo.subtract(parcela.getJuros());
            BigDecimal residualJuros = parcela.getJuros().subtract(parcela.getValorCompensado());

            for (ItemParcelaValorDivida item : pvd.getItensParcelaValorDivida()) {
                if (!temTributoDeTaxaOuImposto(item)) {
                    continue;
                }
                if (pvd.getValorDivida().getItemValorDividas().size() > 1) {
                    if (item.getItemValorDivida().getTributo().getTipoTributo().equals(Tributo.TipoTributo.TAXA)) {
                        BigDecimal porcentagemAbatimento = percentualDeAbatimentoNosTributos(parcela.getTaxa(), parcela);
                        diferenca = diferenca.add((residualJuros.multiply(porcentagemAbatimento)).divide(new BigDecimal(100)));
                        lancaAcrescimoProporcional(calculoPagamentoJudicial, (residualJuros.multiply(porcentagemAbatimento)).
                                divide(new BigDecimal(100), 2, RoundingMode.HALF_UP),
                            item.getItemValorDivida().getTributo().getTributoJuros());

                    } else {
                        BigDecimal porcentagemAbatimento = percentualDeAbatimentoNosTributos(parcela.getImposto(), parcela);
                        diferenca = diferenca.add((residualJuros.multiply(porcentagemAbatimento)).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
                        lancaAcrescimoProporcional(calculoPagamentoJudicial, (residualJuros.multiply(porcentagemAbatimento)).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP),
                            item.getItemValorDivida().getTributo().getTributoJuros());
                    }
                } else {
                    lancaJuros(calculoPagamentoJudicial, item, residualJuros);
                }
            }
            if (diferenca.compareTo(residualJuros) != 0 && pvd.getValorDivida().getItemValorDividas().size() > 1) {
                calculoPagamentoJudicial.getItensCalculoJudicial().get(0).setValor(calculoPagamentoJudicial.getItensCalculoJudicial().get(0).getValor()
                    .add(residualJuros.subtract(diferenca)));
            }
        } else {
            saldo = saldo.subtract(parcela.getJuros());
        }

        //MULTA
        if (parcela.getMulta().compareTo(BigDecimal.ZERO) > 0 && saldo.compareTo(parcela.getMulta()) < 0) {
            if (saldo.compareTo(parcela.getMulta()) <= 0 && saldo.compareTo(BigDecimal.ZERO) > 0) {
                lancaMulta(parcela, pvd, calculoPagamentoJudicial, parcela.getMulta().subtract(saldo));
                saldo = saldo.subtract(parcela.getMulta());
            } else {
                lancaMulta(parcela, pvd, calculoPagamentoJudicial, parcela.getMulta());
                saldo = saldo.subtract(parcela.getMulta());
            }
        } else {
            saldo = saldo.subtract(parcela.getMulta());
        }

        //CORRECAO
        if (parcela.getCorrecao().compareTo(BigDecimal.ZERO) > 0 && saldo.compareTo(parcela.getCorrecao()) < 0) {
            if (saldo.compareTo(parcela.getCorrecao()) <= 0 && saldo.compareTo(BigDecimal.ZERO) > 0) {
                lancaCorrecao(parcela, pvd, calculoPagamentoJudicial, parcela.getCorrecao().subtract(saldo));
                saldo = saldo.subtract(parcela.getCorrecao());
            } else {
                lancaCorrecao(parcela, pvd, calculoPagamentoJudicial, parcela.getCorrecao());
                saldo = saldo.subtract(parcela.getCorrecao());
            }
        } else {
            saldo = saldo.subtract(parcela.getCorrecao());
        }

        //TAXA
        if (parcela.getTaxa().compareTo(BigDecimal.ZERO) > 0 && saldo.compareTo(parcela.getTaxa()) < 0) {
            if (saldo.compareTo(parcela.getTaxa()) <= 0 && saldo.compareTo(BigDecimal.ZERO) > 0) {
                lancaTaxa(parcela, pvd, calculoPagamentoJudicial, parcela.getTaxa().subtract(saldo));
                saldo = saldo.subtract(parcela.getTaxa());
            } else {
                lancaTaxa(parcela, pvd, calculoPagamentoJudicial, parcela.getTaxa());
                saldo = saldo.subtract(parcela.getTaxa());
            }
        } else {
            saldo = saldo.subtract(parcela.getTaxa());
        }

        //IMPOSTO
        if (parcela.getImposto().compareTo(BigDecimal.ZERO) > 0 && saldo.compareTo(parcela.getImposto()) < 0) {
            if (saldo.compareTo(parcela.getImposto()) <= 0 && saldo.compareTo(BigDecimal.ZERO) > 0) {
                lancaImposto(parcela, pvd, calculoPagamentoJudicial, parcela.getImposto().subtract(saldo));
            } else {
                lancaImposto(parcela, pvd, calculoPagamentoJudicial, parcela.getImposto());
            }
        }
    }


    private void lancaMulta(PagamentoJudicialParcela parcela, ParcelaValorDivida pvd, CalculoPagamentoJudicial calculoPagamentoJudicial, BigDecimal saldo) {
        BigDecimal diferencaMulta = BigDecimal.ZERO;
        for (ItemParcelaValorDivida item : pvd.getItensParcelaValorDivida()) {
            if (!temTributoDeTaxaOuImposto(item)) {
                continue;
            }
            if (pvd.getItensParcelaValorDivida().size() > 1) {
                if (item.getItemValorDivida().getTributo().getTipoTributo().equals(Tributo.TipoTributo.TAXA)) {
                    BigDecimal porcentagemAbatimento = percentualDeAbatimentoNosTributos(parcela.getTaxa(), parcela);
                    diferencaMulta = diferencaMulta.add((saldo.multiply(porcentagemAbatimento)).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
                    lancaAcrescimoProporcional(calculoPagamentoJudicial, (saldo.multiply(porcentagemAbatimento)).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP),
                        item.getItemValorDivida().getTributo().getTributoMulta());
                } else {
                    BigDecimal porcentagemAbatimento = percentualDeAbatimentoNosTributos(parcela.getImposto(), parcela);
                    diferencaMulta = diferencaMulta.add((saldo.multiply(porcentagemAbatimento)).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
                    lancaAcrescimoProporcional(calculoPagamentoJudicial, (saldo.multiply(porcentagemAbatimento)).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP),
                        item.getItemValorDivida().getTributo().getTributoMulta());
                }

            } else {
                lancaAcrescimoProporcional(calculoPagamentoJudicial, saldo, item.getItemValorDivida().getTributo().getTributoMulta());
            }
        }
        if (diferencaMulta.compareTo(BigDecimal.ZERO) > 0 && diferencaMulta.compareTo(saldo) != 0 && pvd.getValorDivida().getItemValorDividas().size() > 1) {
            for (ItensCalculoJudicial itensCalculoJudicial : calculoPagamentoJudicial.getItensCalculoJudicial()) {
                if (itensCalculoJudicial.getTributo().getTipoTributo().equals(Tributo.TipoTributo.MULTA)) {
                    itensCalculoJudicial.setValor(itensCalculoJudicial.getValor().add(saldo.subtract(diferencaMulta)));
                    break;
                }
            }
        }
    }

    private void lancaCorrecao(PagamentoJudicialParcela parcela, ParcelaValorDivida pvd, CalculoPagamentoJudicial calculoPagamentoJudicial, BigDecimal saldo) {
        BigDecimal diferencaCorrecao = BigDecimal.ZERO;
        for (ItemParcelaValorDivida item : pvd.getItensParcelaValorDivida()) {
            if (!temTributoDeTaxaOuImposto(item)) {
                continue;
            }
            if (pvd.getItensParcelaValorDivida().size() > 1) {
                if (item.getItemValorDivida().getTributo().getTipoTributo().equals(Tributo.TipoTributo.TAXA)) {
                    BigDecimal porcentagemAbatimento = percentualDeAbatimentoNosTributos(parcela.getTaxa(), parcela);
                    diferencaCorrecao = diferencaCorrecao.add((saldo.multiply(porcentagemAbatimento)).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
                    lancaAcrescimoProporcional(calculoPagamentoJudicial, (saldo.multiply(porcentagemAbatimento)).
                        divide(new BigDecimal(100), 2, RoundingMode.HALF_UP), item.getItemValorDivida().getTributo().getTributoCorrecaoMonetaria());
                } else {
                    BigDecimal porcentagemAbatimento = percentualDeAbatimentoNosTributos(parcela.getImposto(), parcela);
                    diferencaCorrecao = diferencaCorrecao.add((saldo.multiply(porcentagemAbatimento)).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
                    lancaAcrescimoProporcional(calculoPagamentoJudicial, (saldo.multiply(porcentagemAbatimento)).
                        divide(new BigDecimal(100), 2, RoundingMode.HALF_UP), item.getItemValorDivida().getTributo().getTributoCorrecaoMonetaria());
                }
            } else {
                lancaAcrescimoProporcional(calculoPagamentoJudicial, saldo, item.getItemValorDivida().getTributo().getTributoCorrecaoMonetaria());
            }
        }
        if (diferencaCorrecao.compareTo(BigDecimal.ZERO) > 0 && diferencaCorrecao.compareTo(saldo) != 0 && pvd.getValorDivida().getItemValorDividas().size() > 1) {
            for (ItensCalculoJudicial itensCalculoJudicial : calculoPagamentoJudicial.getItensCalculoJudicial()) {
                if (itensCalculoJudicial.getTributo().getTipoTributo().equals(Tributo.TipoTributo.CORRECAO)) {
                    itensCalculoJudicial.setValor(itensCalculoJudicial.getValor().add(saldo.subtract(diferencaCorrecao)));
                    break;
                }
            }
        }
    }

    private void lancaTaxa(PagamentoJudicialParcela parcela, ParcelaValorDivida pvd, CalculoPagamentoJudicial calculoPagamentoJudicial, BigDecimal saldo) {
        BigDecimal diferencaTaxa = BigDecimal.ZERO;
        for (ItemParcelaValorDivida item : pvd.getItensParcelaValorDivida()) {
            if (!temTributoDeTaxaOuImposto(item) || !item.getItemValorDivida().getTributo().getTipoTributo().equals(Tributo.TipoTributo.TAXA)) {
                continue;
            }
            if (pvd.getItensParcelaValorDivida().size() > 1) {
                BigDecimal percentual = (saldo.multiply(new BigDecimal(100))).divide(parcela.getTaxa(), 2, RoundingMode.HALF_UP);
                diferencaTaxa = diferencaTaxa.add((percentual.multiply(item.getValor())).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
                lancaAcrescimoProporcional(calculoPagamentoJudicial, (percentual.multiply(item.getValor())).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP),
                    item.getItemValorDivida().getTributo());
            } else {
                lancaAcrescimoProporcional(calculoPagamentoJudicial, saldo, item.getItemValorDivida().getTributo());
            }
        }
        if (diferencaTaxa.compareTo(BigDecimal.ZERO) > 0 && diferencaTaxa.compareTo(saldo) != 0 && pvd.getValorDivida().getItemValorDividas().size() > 1) {
            for (ItensCalculoJudicial itensCalculoJudicial : calculoPagamentoJudicial.getItensCalculoJudicial()) {
                if (itensCalculoJudicial.getTributo().getTipoTributo().equals(Tributo.TipoTributo.TAXA)) {
                    itensCalculoJudicial.setValor(itensCalculoJudicial.getValor().add(saldo.subtract(diferencaTaxa)));
                    break;
                }
            }
        }
    }

    private void lancaImposto(PagamentoJudicialParcela parcela, ParcelaValorDivida pvd, CalculoPagamentoJudicial calculoPagamentoJudicial, BigDecimal saldo) {
        BigDecimal diferencaImposto = BigDecimal.ZERO;
        for (ItemParcelaValorDivida item : pvd.getItensParcelaValorDivida()) {
            if (!temTributoDeTaxaOuImposto(item) || !item.getItemValorDivida().getTributo().getTipoTributo().equals(Tributo.TipoTributo.IMPOSTO)) {
                continue;
            }
            if (pvd.getItensParcelaValorDivida().size() > 1) {
                BigDecimal percentual = (saldo.multiply(new BigDecimal(100))).divide(parcela.getImposto(), 2, RoundingMode.HALF_UP);
                diferencaImposto = diferencaImposto.add((percentual.multiply(item.getValor())).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
                lancaAcrescimoProporcional(calculoPagamentoJudicial, (percentual.multiply(item.getValor())).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP),
                    item.getItemValorDivida().getTributo());
            } else {
                lancaAcrescimoProporcional(calculoPagamentoJudicial, saldo, item.getItemValorDivida().getTributo());
            }
        }
        if (diferencaImposto.compareTo(BigDecimal.ZERO) > 0 && diferencaImposto.compareTo(saldo) != 0 && pvd.getValorDivida().getItemValorDividas().size() > 1) {
            for (ItensCalculoJudicial itensCalculoJudicial : calculoPagamentoJudicial.getItensCalculoJudicial()) {
                if (itensCalculoJudicial.getTributo().getTipoTributo().equals(Tributo.TipoTributo.IMPOSTO)) {
                    itensCalculoJudicial.setValor(itensCalculoJudicial.getValor().add(saldo.subtract(diferencaImposto)));
                    break;
                }
            }
        }
    }

    private boolean temTributoDeTaxaOuImposto(ItemParcelaValorDivida iten) {
        return iten.getItemValorDivida().getTributo().getTipoTributo().equals(Tributo.TipoTributo.TAXA) || iten.getItemValorDivida().getTributo().getTipoTributo().equals(Tributo.TipoTributo.IMPOSTO);
    }


    private void lancaJuros(CalculoPagamentoJudicial calculoPagamentoJudicial, ItemParcelaValorDivida itens, BigDecimal saldo) {
        ItensCalculoJudicial item = new ItensCalculoJudicial();
        item.setTributo(itens.getItemValorDivida().getTributo().getTributoJuros());
        item.setCalculoPagamentoJudicial(calculoPagamentoJudicial);
        item.setValor(saldo);
        calculoPagamentoJudicial.getItensCalculoJudicial().add(item);
    }

    private void lancaAcrescimoProporcional(CalculoPagamentoJudicial calculoPagamentoJudicial, BigDecimal saldo, Tributo tributo) {
        ItensCalculoJudicial item = new ItensCalculoJudicial();
        item.setTributo(tributo);
        item.setCalculoPagamentoJudicial(calculoPagamentoJudicial);
        item.setValor(saldo);
        calculoPagamentoJudicial.getItensCalculoJudicial().add(item);
    }


    private void lancaItensResidualCompensado(PagamentoJudicialParcela parcela, ParcelaValorDivida pvd, CalculoPagamentoJudicial calculoPagamentoJudicial) {
        BigDecimal saldo = parcela.getValorCompensado();
        for (ItemParcelaValorDivida item : pvd.getItensParcelaValorDivida()) {
            if (parcela.getJuros().compareTo(BigDecimal.ZERO) > 0) {
                if (saldo.compareTo(parcela.getJuros()) > 0) {
                    lancaAcrescimoProporcional(calculoPagamentoJudicial, parcela.getJuros(), item.getItemValorDivida().getTributo().getTributoJuros());
                    saldo = saldo.subtract(parcela.getJuros());
                } else {
                    lancaAcrescimoProporcional(calculoPagamentoJudicial, saldo, item.getItemValorDivida().getTributo().getTributoJuros());
                    saldo = saldo.subtract(parcela.getJuros());
                }
            }
            if (saldo.compareTo(BigDecimal.ZERO) > 0) {
                if (parcela.getMulta().compareTo(BigDecimal.ZERO) > 0) {
                    if (saldo.compareTo(parcela.getMulta()) > 0) {
                        lancaAcrescimoProporcional(calculoPagamentoJudicial, parcela.getMulta(), item.getItemValorDivida().getTributo().getTributoMulta());
                        saldo = saldo.subtract(parcela.getMulta());
                    } else {
                        lancaAcrescimoProporcional(calculoPagamentoJudicial, saldo, item.getItemValorDivida().getTributo().getTributoMulta());
                        saldo = saldo.subtract(parcela.getMulta());
                    }
                }
            }
            if (saldo.compareTo(BigDecimal.ZERO) > 0) {
                if (parcela.getCorrecao().compareTo(BigDecimal.ZERO) > 0) {
                    if (saldo.compareTo(parcela.getCorrecao()) > 0) {
                        lancaAcrescimoProporcional(calculoPagamentoJudicial, parcela.getCorrecao(), item.getItemValorDivida().getTributo().getTributoCorrecaoMonetaria());
                        saldo = saldo.subtract(parcela.getCorrecao());
                    } else {
                        lancaAcrescimoProporcional(calculoPagamentoJudicial, saldo, item.getItemValorDivida().getTributo().getTributoCorrecaoMonetaria());
                        saldo = saldo.subtract(parcela.getCorrecao());
                    }
                }
            }
            if (saldo.compareTo(BigDecimal.ZERO) > 0) {
                if (parcela.getTaxa().compareTo(BigDecimal.ZERO) > 0) {
                    for (ItemParcelaValorDivida itemTaxa : pvd.getItensParcelaValorDivida()) {
                        if (!itemTaxa.getItemValorDivida().getTributo().getTipoTributo().equals(Tributo.TipoTributo.TAXA)) {
                            continue;
                        }
                        if (saldo.compareTo(parcela.getTaxa()) > 0) {
                            lancaAcrescimoProporcional(calculoPagamentoJudicial, parcela.getTaxa(), itemTaxa.getItemValorDivida().getTributo());
                            saldo = saldo.subtract(parcela.getTaxa());
                        } else {
                            lancaAcrescimoProporcional(calculoPagamentoJudicial, saldo, itemTaxa.getItemValorDivida().getTributo());
                            saldo = saldo.subtract(parcela.getTaxa());
                        }
                    }
                }
            }
            if (saldo.compareTo(BigDecimal.ZERO) > 0) {
                if (parcela.getImposto().compareTo(BigDecimal.ZERO) > 0) {
                    for (ItemParcelaValorDivida itemImposto : pvd.getItensParcelaValorDivida()) {
                        if (!itemImposto.getItemValorDivida().getTributo().getTipoTributo().equals(Tributo.TipoTributo.IMPOSTO)) {
                            continue;
                        }
                        if (saldo.compareTo(parcela.getImposto()) > 0) {
                            lancaAcrescimoProporcional(calculoPagamentoJudicial, parcela.getImposto(), itemImposto.getItemValorDivida().getTributo());
                        } else {
                            lancaAcrescimoProporcional(calculoPagamentoJudicial, saldo, itemImposto.getItemValorDivida().getTributo());
                        }
                        break;
                    }
                }
            }
            break;
        }
    }


    private BigDecimal percentualDeAbatimentoNosTributos(BigDecimal valorTributo, PagamentoJudicialParcela parcela) {
        BigDecimal impostoMaisTaxa = parcela.getImposto().add(parcela.getTaxa());
        BigDecimal percentual = impostoMaisTaxa.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
        return valorTributo.divide(percentual, 2, RoundingMode.HALF_UP);
    }

    public void setaSituacaoDeAcordoComProcesso(ParcelaValorDivida parcela, SituacaoParcela situacao) {
        salvaSituacaoParcela(parcela, situacao);
    }


    private void salvaSituacaoParcela(ParcelaValorDivida parcela, SituacaoParcela situacao) {
        SituacaoParcelaValorDivida spvd = new SituacaoParcelaValorDivida();
        spvd.setSaldo(parcela.getUltimaSituacao().getSaldo());
        spvd.setDataLancamento(new Date());
        spvd.setParcela(parcela);
        spvd.setSituacaoParcela(situacao);
        em.persist(spvd);
    }

    public PagamentoJudicial salvarProcesso(PagamentoJudicial pagamento) {
        PagamentoJudicial selecionado = em.merge(pagamento);
        return selecionado;
    }

    public Long recuperaProximoCodigoPorExercicio(Exercicio exercicio) {
        String sql = " select max(coalesce(obj.codigo,0)) from PagamentoJudicial obj "
            + " where obj.exercicio = :exercicio";
        Query query = em.createQuery(sql);
        query.setParameter("exercicio", exercicio);
        query.setMaxResults(1);
        try {
            Long resultado = (Long) query.getSingleResult();
            if (resultado == null) {
                resultado = 0l;
            }

            return resultado + 1;
        } catch (Exception e) {
            return 1l;
        }
    }

    public CadastroRuralFacade getCadastroRuralFacade() {
        return cadastroRuralFacade;
    }

    public CadastroEconomicoFacade getCadastroEconomicoFacade() {
        return cadastroEconomicoFacade;
    }


    public CadastroImobiliarioFacade getCadastroImobiliarioFacade() {
        return cadastroImobiliarioFacade;
    }


    public ParcelaValorDivida recuperaParcelaValorDivida(Long id) {
        String hql = "select pvd from ParcelaValorDivida pvd where pvd.id = :id";
        Query q = em.createQuery(hql, ParcelaValorDivida.class);
        q.setParameter("id", id);
        if (!q.getResultList().isEmpty()) {
            ParcelaValorDivida parcela = (ParcelaValorDivida) q.getSingleResult();
            parcela.getItensParcelaValorDivida().size();
            parcela.getSituacoes().size();
            return parcela;
        } else {
            return null;
        }
    }

    public PagamentoJudicial recuperar(Object id) {
        PagamentoJudicial processo = em.find(PagamentoJudicial.class, id);
        processo.getParcelas().size();
        return processo;
    }

    public void estornar(PagamentoJudicial processo, List<ResultadoParcela> parcelas) {
        processo.setSituacao(SituacaoProcessoDebito.ESTORNADO);
        for (PagamentoJudicialParcela item : processo.getParcelas()) {
            ParcelaValorDivida parcela = em.find(ParcelaValorDivida.class, item.getParcelaValorDivida().getId());
            salvaSituacaoParcela(parcela, SituacaoParcela.EM_ABERTO);
        }
        for (ResultadoParcela parcela : parcelas) {
            ParcelaValorDivida pvd = em.find(ParcelaValorDivida.class, parcela.getIdParcela());
            salvaSituacaoParcela(pvd, SituacaoParcela.CANCELAMENTO);
        }
        em.merge(processo);
    }

    public List<CalculoPagamentoJudicial> recuperarCalculo(PagamentoJudicial pagamentoJudicial) {
        String hql = "select calc from CalculoPagamentoJudicial calc where calc.pagamentoJudicial = :pagamento";
        Query q = em.createQuery(hql);
        q.setParameter("pagamento", pagamentoJudicial);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return null;
    }


    public List<PagamentoJudicial> filtraPagamentoJudicial(Long codigo, Integer ano) {
        StringBuilder hql = new StringBuilder("from PagamentoJudicial p");
        if (codigo != null && !codigo.toString().isEmpty()) {
            hql.append(" where p.codigo like :codigo");
        }
        if (ano != null) {
            hql.append(" and p.exercicio.ano = :ano");
        } else {
            hql.append(" or p.exercicio.ano = :codigo");
        }
        Query q = em.createQuery(hql.toString());
        if (codigo != null && !codigo.toString().isEmpty()) {
            q.setParameter("codigo", codigo);
        }
        if (ano != null) {
            q.setParameter("ano", ano);
        }
        q.setMaxResults(30);
        return q.getResultList();
    }

    private ValorDivida recuperaValorDivida(ParcelaValorDivida parcela) {
        String hql = "select pvd.valorDivida from ParcelaValorDivida pvd where pvd = :parcela";
        Query q = em.createQuery(hql);
        q.setParameter("parcela", parcela);
        if (!q.getResultList().isEmpty()) {
            return (ValorDivida) q.getResultList().get(0);
        }
        return null;
    }

    public Integer recuperaQuantidadeParcela(ParcelaValorDivida parcela) {
        ValorDivida vd = recuperaValorDivida(parcela);
        String hql = "SELECT COUNT(p.id) " +
            "        FROM ParcelaValorDivida P" +
            "        INNER JOIN valordivida V" +
            "        ON V.ID                 = P.VALORDIVIDA_ID " +
            "        WHERE V.ID              = :vd and p.opcaopagamento_id = :opcao";
        Query q = em.createNativeQuery(hql);
        q.setParameter("vd", vd.getId());
        q.setParameter("opcao", parcela.getOpcaoPagamento().getId());
        if (!q.getResultList().isEmpty()) {
            return ((BigDecimal) q.getResultList().get(0)).intValue();
        }
        return null;
    }

    public List<Long> recuperarIDDoPagamentoJudicialDaParcelaOriginal(Long idParcela) {
        String hql = "select calculo.id from CalculoPagamentoJudicial calculo " +
            " join calculo.pagamentoJudicial pagto " +
            " join pagto.parcelas p " +
            " where p.parcelaValorDivida.id = :idParcela ";
        Query q = em.createQuery(hql);
        q.setParameter("idParcela", idParcela);
        return q.getResultList();
    }

}
