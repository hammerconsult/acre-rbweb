package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.DataTableConsultaDebito;
import br.com.webpublico.enums.SituacaoLancamentoDesconto;
import br.com.webpublico.negocios.tributario.services.ServiceConsultaDebitos;
import br.com.webpublico.tributario.consultadebitos.calculadores.CalculadorHonorarios;
import br.com.webpublico.util.Persistencia;
import com.google.common.collect.Lists;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class LancamentoDescontoFacade extends AbstractFacade<LancamentoDesconto> {

    protected final BigDecimal CEM = new BigDecimal("100");
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private DividaFacade dividaFacade;


    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LancamentoDescontoFacade() {
        super(LancamentoDesconto.class);
    }

    @Override
    public LancamentoDesconto recuperar(Object id) {
        LancamentoDesconto ld = em.find(LancamentoDesconto.class, id);
        ld.getParcelas().size();
        for (LancamentoDescontoParcela lancamentoDescontoParcela : ld.getParcelas()) {
            lancamentoDescontoParcela.getDescontos().size();
        }
        return ld;
    }

    public AtoLegalFacade getAtoLegalFacade() {
        return atoLegalFacade;
    }

    public DividaFacade getDividaFacade() {
        return dividaFacade;
    }


    public List<ItemParcelaValorDivida> recuperaItensParcelaValorDivida(ParcelaValorDivida parcelaValorDivida) {
        Query consulta = em.createQuery("select item from ItemParcelaValorDivida item"
            + " inner join item.parcelaValorDivida par"
            + " inner join par.valorDivida vd"
            + " inner join vd.divida div"
            + " where par = :param");
        consulta.setParameter("param", parcelaValorDivida);
        return (List<ItemParcelaValorDivida>) consulta.getResultList();
    }

    public void salvarDescontoItemParcela(DescontoItemParcela descontoItemParcela) {
        em.persist(descontoItemParcela);
    }

    public Calculo calculoDaParcela(ParcelaValorDivida parcela) {
        Query consulta = em.createQuery("select c from ParcelaValorDivida par "
            + " inner join par.valorDivida vd "
            + " inner join vd.calculo c "
            + " where par = :param");
        consulta.setParameter("param", parcela);
        List<Calculo> listaCalculos = consulta.getResultList();
        if (listaCalculos.isEmpty()) {
            return null;
        } else {
            return listaCalculos.get(0);
        }
    }

    public List<DataTableConsultaDebito> listaParcelaCalculandoDebitoDoLancamentoDesconto(LancamentoDesconto lancamentoDesconto) {
        String hql = "select distinct"
            + " new br.com.webpublico.entidadesauxiliares.DataTableConsultaDebito("
            + " pv,"
            + " cal.cadastro,"
            + " vd.divida,"
            + " vd.exercicio,"
            + " case when pv.opcaoPagamento.promocional = 1 then (pv.sequenciaParcela || '/0') else (pv.sequenciaParcela||'/' ||to_char( (select count(parcela.id) from ParcelaValorDivida parcela where parcela.valorDivida = pv.valorDivida and parcela.opcaoPagamento = pv.opcaoPagamento)  )) end,"
            + " cal.subDivida,"
            + " coalesce("
            + " (select processo.numero || '/' || processo.exercicio.ano "
            + " from ProcessoParcelamento processo "
            + " join processo.itensProcessoParcelamento item "
            + " where item.parcelaValorDivida = pv)"
            + " , (select processo.numero || '/' || processo.exercicio.ano "
            + " from ProcessoParcelamento processo where processo = cal)), "
            + " pv.vencimento,"
            + " situacao,"
            + " pv.valor/coalesce((select m.valor from Moeda m where m.mes = TO_NUMBER(TO_CHAR(pv.dataRegistro,'MM')) and m.ano = vd.exercicio.ano and m.indiceEconomico.descricao = 'UFM'),1),"
            + " case when situacao.situacaoParcela != 'PAGO' then"
            + " ((situacao.saldo/coalesce((select m.valor from Moeda m where m.mes = TO_NUMBER(TO_CHAR(pv.dataRegistro,'MM')) and m.ano = vd.exercicio.ano and m.indiceEconomico.descricao = 'UFM'),1)) "
            + " * coalesce((select m.valor from Moeda m where m.mes = TO_NUMBER(TO_CHAR(current_date,'MM')) and m.ano = TO_NUMBER(TO_CHAR(current_date,'yyyy')) and m.indiceEconomico.descricao = 'UFM'),1))"
            + " else ((select sum(itemLoteParcela.itemLoteBaixa.valorPago) from ItemLoteBaixaParcela itemLoteParcela where itemLoteParcela.itemDam.parcela = pv) - 0) "
            + " end, "
            + " (select c.acrescimo from DividaAcrescimo c where c.divida = vd.divida and c.inicioVigencia <= CURRENT_DATE and (c.finalVigencia is null or c.finalVigencia >= CURRENT_DATE)),"
            + " (select coalesce(sum((desconto.itemParcelaValorDivida.valor * (desconto.porcentagemDesconto/100))),0) from DescontoItemParcela desconto where desconto.itemParcelaValorDivida.parcelaValorDivida = pv),"
            + " (select coalesce(sum(item.valor),0) from ItemParcelaValorDivida item where item.parcelaValorDivida = pv and item.itemValorDivida.tributo.tipoTributo = 'IMPOSTO'),"
            + " (select coalesce(sum(item.valor),0) from ItemParcelaValorDivida item where item.parcelaValorDivida = pv and item.itemValorDivida.tributo.tipoTributo = 'TAXA'))"
            + " from ParcelaValorDivida pv "
            + " join pv.situacoes situacao "
            + " join pv.valorDivida vd"
            + " join vd.calculo cal"
            + " left join cal.pessoas pessoa"
            + " where pv in (select l.parcela from LancamentoDescontoParcela l where l.lancamentoDesconto = :lancamento)";

        Query q = em.createQuery(hql.toString());
        q.setParameter("lancamento", lancamentoDesconto);
        q.setMaxResults(100);
        return q.getResultList();
    }

    public List<DescontoItemParcela> listaDescontoItemParcelaLancamento(LancamentoDesconto lancamentoDesconto) {
        String sql = " select dip.* from DescontoItemParcela dip "
            + " inner join ItemLancamentoDesconto ild on ild.descontoitemparcela_id = dip.id "
            + " inner join TributoDesconto td on ild.tributodesconto_id = td.id "
            + " where td.lancamentodesconto_id = :lancamento";
        Query q = em.createNativeQuery(sql.toString(), DescontoItemParcela.class);
        q.setParameter("lancamento", lancamentoDesconto.getId());
        return q.getResultList();
    }

    public List<ItemLancamentoDesconto> listaItemLancamentoDescontoLancamento(LancamentoDesconto lancamentoDesconto) {
        String sql = "select ild.* from ItemLancamentoDesconto ild "
            + " inner join TributoDesconto td on ild.tributodesconto_id = td.id "
            + " where td.lancamentodesconto_id = :lancamento";
        Query q = em.createNativeQuery(sql.toString(), ItemLancamentoDesconto.class);
        q.setParameter("lancamento", lancamentoDesconto.getId());
        return q.getResultList();
    }

    public ItemLancamentoDesconto recuperarItemLancamentoDesconto(Object id) {
        return em.find(ItemLancamentoDesconto.class, id);
    }

    public DescontoItemParcela recuperarDescontoItemParcela(Object id) {
        return em.find(DescontoItemParcela.class, id);
    }

    public void removerItemLancamentoDesconto(ItemLancamentoDesconto ild) {
        Object chave = Persistencia.getId(ild);
        Object obj = recuperarItemLancamentoDesconto(chave);
        if (obj != null) {
            getEntityManager().remove(obj);
        }
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

    private ItemParcelaValorDivida getItemParcelaValorDividaPorTipoTributo(List<ItemParcelaValorDivida> itens, Tributo.TipoTributo tipoTributo) {
        for (ItemParcelaValorDivida itemParcelaValorDivida : itens) {
            if (itemParcelaValorDivida.getTributo().getTipoTributo().equals(tipoTributo)) {
                return itemParcelaValorDivida;
            }
        }
        return null;
    }

    private LancamentoDescontoParcelaTributo criarLancamentoDescontoParcelaTributo(LancamentoDescontoParcela lancamentoDescontoParcela,
                                                                                   ItemParcelaValorDivida itemParcelaValorDivida,
                                                                                   BigDecimal desconto, Tributo.TipoTributo tipoTributo) {
        LancamentoDescontoParcelaTributo lancamentoDescontoParcelaTributo = new LancamentoDescontoParcelaTributo();
        lancamentoDescontoParcelaTributo.setLancamentoDescontoParcela(lancamentoDescontoParcela);
        lancamentoDescontoParcelaTributo.setItemParcelaValorDivida(itemParcelaValorDivida);
        lancamentoDescontoParcelaTributo.setTipoTributo(tipoTributo);
        lancamentoDescontoParcelaTributo.setDesconto(desconto);
        return lancamentoDescontoParcelaTributo;
    }

    public void calcularDescontosDebitos(LancamentoDesconto lancamentoDesconto) {
        if (lancamentoDesconto.getParcelas() == null || lancamentoDesconto.getParcelas().isEmpty()) {
            return;
        }

        for (LancamentoDescontoParcela lancamentoDescontoParcela : lancamentoDesconto.getParcelas()) {
            lancamentoDescontoParcela.setDescontos(new ArrayList());

            BigDecimal descontoImposto = BigDecimal.ZERO;
            BigDecimal descontoTaxa = BigDecimal.ZERO;
            BigDecimal descontoJuros = BigDecimal.ZERO;
            BigDecimal descontoMulta = BigDecimal.ZERO;
            BigDecimal descontoCorrecao = BigDecimal.ZERO;
            BigDecimal descontoHonorarios = BigDecimal.ZERO;

            if (LancamentoDesconto.TipoDesconto.FIXO.equals(lancamentoDesconto.getTipoDesconto())) {
                if (lancamentoDescontoParcela.getImposto().compareTo(BigDecimal.ZERO) > 0) {
                    descontoImposto = lancamentoDesconto.getValorDescontoImposto();
                }
                if (lancamentoDescontoParcela.getTaxa().compareTo(BigDecimal.ZERO) > 0) {
                    descontoTaxa = lancamentoDesconto.getValorDescontoTaxa();
                }

                if (lancamentoDescontoParcela.getJuros().compareTo(BigDecimal.ZERO) > 0) {
                    descontoJuros = lancamentoDesconto.getValorDescontoJuros();
                }
                if (lancamentoDescontoParcela.getMulta().compareTo(BigDecimal.ZERO) > 0) {
                    descontoMulta = lancamentoDesconto.getValorDescontoMulta();
                }
                if (lancamentoDescontoParcela.getCorrecao().compareTo(BigDecimal.ZERO) > 0) {
                    descontoCorrecao = lancamentoDesconto.getValorDescontoCorrecao();
                }
                if (lancamentoDescontoParcela.getHonorarios().compareTo(BigDecimal.ZERO) > 0) {
                    descontoHonorarios = lancamentoDesconto.getValorDescontoHonorarios();
                }
            } else if (LancamentoDesconto.TipoDesconto.PERCENTUAL.equals(lancamentoDesconto.getTipoDesconto())) {
                descontoImposto = lancamentoDescontoParcela.getImposto().multiply(lancamentoDesconto.getPercentualDescontoImposto().divide(CEM, 2, BigDecimal.ROUND_HALF_UP));
                descontoTaxa = lancamentoDescontoParcela.getTaxa().multiply(lancamentoDesconto.getPercentualDescontoTaxa().divide(CEM, 2, BigDecimal.ROUND_HALF_UP));
                descontoJuros = lancamentoDescontoParcela.getJuros().multiply(lancamentoDesconto.getPercentualDescontoJuros().divide(CEM, 2, BigDecimal.ROUND_HALF_UP));
                descontoMulta = lancamentoDescontoParcela.getMulta().multiply(lancamentoDesconto.getPercentualDescontoMulta().divide(CEM, 2, BigDecimal.ROUND_HALF_UP));
                descontoCorrecao = lancamentoDescontoParcela.getCorrecao().multiply(lancamentoDesconto.getPercentualDescontoCorrecao().divide(CEM, 2, BigDecimal.ROUND_HALF_UP));
                descontoHonorarios = lancamentoDescontoParcela.getHonorarios().multiply(lancamentoDesconto.getPercentualDescontoHonorarios().divide(CEM, 2, BigDecimal.ROUND_HALF_UP));
            }

            ParcelaValorDivida parcelaValorDivida = em.find(ParcelaValorDivida.class, lancamentoDescontoParcela.getParcela().getId());

            if (descontoImposto.compareTo(BigDecimal.ZERO) > 0) {
                calcularDescontoPorTipoTributo(parcelaValorDivida, lancamentoDescontoParcela,
                    descontoImposto, Tributo.TipoTributo.IMPOSTO);
            }
            if (descontoTaxa.compareTo(BigDecimal.ZERO) > 0) {
                calcularDescontoPorTipoTributo(parcelaValorDivida, lancamentoDescontoParcela,
                    descontoTaxa, Tributo.TipoTributo.TAXA);
            }
            if (descontoJuros.compareTo(BigDecimal.ZERO) > 0) {
                calcularDescontoPorTipoTributo(parcelaValorDivida, lancamentoDescontoParcela,
                    descontoJuros, Tributo.TipoTributo.JUROS);
            }
            if (descontoMulta.compareTo(BigDecimal.ZERO) > 0) {
                calcularDescontoPorTipoTributo(parcelaValorDivida, lancamentoDescontoParcela,
                    descontoMulta, Tributo.TipoTributo.MULTA);
            }
            if (descontoCorrecao.compareTo(BigDecimal.ZERO) > 0) {
                calcularDescontoPorTipoTributo(parcelaValorDivida, lancamentoDescontoParcela,
                    descontoCorrecao, Tributo.TipoTributo.CORRECAO);
            }
            if (descontoHonorarios.compareTo(BigDecimal.ZERO) > 0) {
                calcularDescontoPorTipoTributo(parcelaValorDivida, lancamentoDescontoParcela,
                    descontoHonorarios, Tributo.TipoTributo.HONORARIOS);
            }
            if (lancamentoDescontoParcela.getHonorarios().compareTo(BigDecimal.ZERO) > 0) {
                ConfiguracaoAcrescimos configuracaoAcrescimos = dividaFacade.configuracaoVigente(lancamentoDescontoParcela.getParcela().getValorDivida().getDivida());

                BigDecimal honorariosCalculado = CalculadorHonorarios.calculaHonorarios(configuracaoAcrescimos.toDto(), new Date(),
                    lancamentoDescontoParcela.getImposto().add(lancamentoDescontoParcela.getTaxa())
                        .add(lancamentoDescontoParcela.getJuros()).add(lancamentoDescontoParcela.getMulta())
                        .add(lancamentoDescontoParcela.getCorrecao()).subtract(lancamentoDescontoParcela.getDesconto())
                        .subtract(lancamentoDescontoParcela.getTotalDesconto()));
                lancamentoDescontoParcela.setHonorariosAtualizados(honorariosCalculado);
            }
        }
    }

    private void calcularDescontoPorTipoTributo(ParcelaValorDivida parcelaValorDivida,
                                                LancamentoDescontoParcela lancamentoDescontoParcela,
                                                BigDecimal descontoPorValor,
                                                Tributo.TipoTributo tipoTributo) {
        ItemParcelaValorDivida itemDoTipoTributo = getItemParcelaValorDividaPorTipoTributo(parcelaValorDivida.getItensParcelaValorDivida(), tipoTributo);
        if (descontoPorValor.compareTo(BigDecimal.ZERO) > 0) {
            lancamentoDescontoParcela.getDescontos().add(criarLancamentoDescontoParcelaTributo(lancamentoDescontoParcela,
                itemDoTipoTributo, descontoPorValor, tipoTributo));
        }
    }

    public void efetivarLancamento(LancamentoDesconto lancamentoDesconto) {
        for (LancamentoDescontoParcela lancamentoDescontoParcela : lancamentoDesconto.getParcelas()) {
            Tributo tributoImpostoTaxa = getTributoDoImpostoOuTaxa(lancamentoDescontoParcela.getParcela());
            if (tributoImpostoTaxa != null) {
                for (LancamentoDescontoParcelaTributo lancamentoDescontoParcelaTributo : lancamentoDescontoParcela.getDescontos()) {
                    criarDescontoItemParcela(lancamentoDescontoParcela, lancamentoDescontoParcelaTributo, tributoImpostoTaxa);
                }
            }
        }
        lancamentoDesconto.setSituacao(SituacaoLancamentoDesconto.EFETIVADO);
        em.merge(lancamentoDesconto);
    }

    private Tributo getTributoDoImpostoOuTaxa(ParcelaValorDivida parcelaValorDivida) {
        List<ItemParcelaValorDivida> itens = buscarItensParcelaValorDivida(parcelaValorDivida);
        for (ItemParcelaValorDivida item : itens) {
            if (item.getItemValorDivida().getTributo().getTipoTributo().isImpostoTaxa()) {
                return item.getItemValorDivida().getTributo();
            }
        }
        return null;
    }

    private void criarDescontoItemParcela(LancamentoDescontoParcela lancamentoDescontoParcela, LancamentoDescontoParcelaTributo lancamentoDescontoParcelaTributo, Tributo tributoImpostoTaxa) {
        ItemParcelaValorDivida itemParcelaValorDivida = lancamentoDescontoParcelaTributo.getItemParcelaValorDivida();
        if (itemParcelaValorDivida == null) {
            itemParcelaValorDivida = criarItemValorDividaEItemParcelaValorDividaZeradosComDesconto(lancamentoDescontoParcela.getParcela(), lancamentoDescontoParcelaTributo, tributoImpostoTaxa);
        }

        DescontoItemParcela descontoItemParcela = new DescontoItemParcela();
        descontoItemParcela.setItemParcelaValorDivida(itemParcelaValorDivida);
        descontoItemParcela.setAtoLegal(lancamentoDescontoParcela.getLancamentoDesconto().getAtoLegal());
        descontoItemParcela.setMotivo(lancamentoDescontoParcela.getLancamentoDesconto().getMotivo());
        descontoItemParcela.setTipo(DescontoItemParcela.Tipo.VALOR);
        descontoItemParcela.setInicio(lancamentoDescontoParcela.getLancamentoDesconto().getInicio());
        descontoItemParcela.setFim(lancamentoDescontoParcela.getLancamentoDesconto().getFim());
        descontoItemParcela.setDesconto(lancamentoDescontoParcelaTributo.getDesconto());
        descontoItemParcela.setOrigem(DescontoItemParcela.Origem.LANCAMENTO_DESCONTO);
        lancamentoDescontoParcelaTributo.setDescontoItemParcela(descontoItemParcela);
    }

    private ItemParcelaValorDivida criarItemValorDividaEItemParcelaValorDividaZeradosComDesconto(ParcelaValorDivida parcelaValorDivida, LancamentoDescontoParcelaTributo lancamentoDescontoParcelaTributo, Tributo tributoImpostoTaxa) {
        ItemValorDivida itemValorDivida = new ItemValorDivida();
        itemValorDivida.setValorDivida(parcelaValorDivida.getValorDivida());
        if (Tributo.TipoTributo.JUROS.equals(lancamentoDescontoParcelaTributo.getTipoTributo())) {
            itemValorDivida.setTributo(tributoImpostoTaxa.getTributoJuros());
        } else if (Tributo.TipoTributo.MULTA.equals(lancamentoDescontoParcelaTributo.getTipoTributo())) {
            itemValorDivida.setTributo(tributoImpostoTaxa.getTributoMulta());
        } else if (Tributo.TipoTributo.CORRECAO.equals(lancamentoDescontoParcelaTributo.getTipoTributo())) {
            itemValorDivida.setTributo(tributoImpostoTaxa.getTributoCorrecaoMonetaria());
        } else if (Tributo.TipoTributo.HONORARIOS.equals(lancamentoDescontoParcelaTributo.getTipoTributo())) {
            itemValorDivida.setTributo(tributoImpostoTaxa.getTributoHonorarios());
        }
        itemValorDivida.setValor(BigDecimal.ZERO);
        itemValorDivida.setIsento(false);

        itemValorDivida = em.merge(itemValorDivida);

        ItemParcelaValorDivida itemParcelaValorDivida = new ItemParcelaValorDivida();
        itemParcelaValorDivida.setItemValorDivida(itemValorDivida);
        itemParcelaValorDivida.setParcelaValorDivida(parcelaValorDivida);
        itemParcelaValorDivida.setValor(BigDecimal.ZERO);

        return em.merge(itemParcelaValorDivida);
    }

    public List<DescontoItemParcela> desvincularDescontoItemParcela(LancamentoDesconto lancamentoDesconto) {
        List<DescontoItemParcela> descontosItemParcela = Lists.newArrayList();
        for (LancamentoDescontoParcela lancamentoDescontoParcela : lancamentoDesconto.getParcelas()) {
            for (LancamentoDescontoParcelaTributo lancamentoDescontoParcelaTributo : lancamentoDescontoParcela.getDescontos()) {
                if (lancamentoDescontoParcelaTributo.getDescontoItemParcela() != null) {
                    DescontoItemParcela descontoItemParcela = em.find(DescontoItemParcela.class,
                        lancamentoDescontoParcelaTributo.getDescontoItemParcela().getId());
                    lancamentoDescontoParcelaTributo.setDescontoItemParcela(null);
                    descontosItemParcela.add(descontoItemParcela);
                }
            }
        }
        return descontosItemParcela;
    }

    public void excluirDescontosItemParcela(List<DescontoItemParcela> descontosItemParcela) {
        for (DescontoItemParcela descontoItemParcela : descontosItemParcela) {
            em.remove(em.find(DescontoItemParcela.class, descontoItemParcela.getId()));
        }
    }

    public void alterarSituacaoParaEstornado(LancamentoDesconto lancamentoDesconto) {
        lancamentoDesconto.setSituacao(SituacaoLancamentoDesconto.ESTORNADO);
        em.merge(lancamentoDesconto);
    }

    private List<ItemParcelaValorDivida> buscarItensParcelaValorDivida(ParcelaValorDivida parcelaValorDivida) {
        String sql = "select ipvd.* from ItemParcelaValorDivida ipvd where ipvd.parcelaValorDivida_id = :idParcela";
        Query q = em.createNativeQuery(sql, ItemParcelaValorDivida.class);
        q.setParameter("idParcela", parcelaValorDivida.getId());
        return q.getResultList();
    }

    public LancamentoDesconto buscarUltimoProcessoDescontoPorParcelaValorDivida(Long idParcela) {
        String sql = "select ld.*" +
            "           from LancamentoDescontoParcela ldp " +
            "          inner join LancamentoDesconto ld on ld.id = ldp.lancamentoDesconto_id " +
            "         where ldp.parcela_id = :idParcela " +
            "           and (trunc(:dataAtual) between trunc(ld.inicio) and trunc(ld.fim)) " +
            "           and ld.situacao = :situacao " +
            "           order by ld.id desc";
        Query q = em.createNativeQuery(sql, LancamentoDesconto.class);
        try {
            q.setMaxResults(1);
            q.setParameter("idParcela", idParcela);
            q.setParameter("dataAtual", new Date());
            q.setParameter("situacao", SituacaoLancamentoDesconto.EFETIVADO.name());
            return (LancamentoDesconto) q.getSingleResult();
        } catch (Exception no) {
            return null;
        }
    }
}
