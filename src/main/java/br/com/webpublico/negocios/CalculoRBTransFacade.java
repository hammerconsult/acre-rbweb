package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoCalculoRBTRans;
import br.com.webpublico.enums.TipoPermissaoRBTrans;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Stateless
public class CalculoRBTransFacade extends AbstractFacade<CalculoRBTrans> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ParametrosTransitoTransporteFacade parametrosTransitoTransporteFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private MoedaFacade moedaFacade;
    @EJB
    private GeraValorDividaRBTrans geraValorDividaRBTrans;
    @EJB
    private PermissaoTransporteFacade permissaoTransporteFacade;
    @EJB
    private CalculaISSFacade calculaISSFacade;
    @EJB
    private GeraValorDividaISS geraDebito;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private GeraValorDividaISSRbtrans geraDebitoRbtrans;

    public CalculoRBTransFacade() {
        super(CalculoRBTrans.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CalculoRBTrans recuperar(Object id) {
        CalculoRBTrans c = em.find(CalculoRBTrans.class, id);
        c.getItensCalculo().size();
        c.getPessoas().size();
        return c;
    }

    public GeraValorDividaISS getGeraDebito() {
        return geraDebito;
    }

    public GeraValorDividaISSRbtrans getGeraDebitoRbtrans() {
        return geraDebitoRbtrans;
    }

    private CalculoRBTrans calcula(CadastroEconomico cadastro, TipoPermissaoRBTrans tipoPermissao, TipoCalculoRBTRans tipoCalculo, PermissaoTransporte permissao) throws ExcecaoNegocioGenerica {
        return calcula(sistemaFacade.getDataOperacao(), sistemaFacade.getExercicioCorrente(), cadastro, tipoPermissao, tipoCalculo, permissao, permissao.getFinalVigencia());
    }

    public CalculoRBTrans calcular(PermissaoTransporte permissaoTransporte, TipoCalculoRBTRans tipoCalculo) {
        return calcula(permissaoTransporte.getPermissionarioVigente().getCadastroEconomico(), permissaoTransporte.getTipoPermissaoRBTrans(), tipoCalculo);
    }

    private CalculoRBTrans calcula(CadastroEconomico cadastro, TipoPermissaoRBTrans tipoPermissao, TipoCalculoRBTRans tipoCalculo) throws ExcecaoNegocioGenerica {
        return calcula(sistemaFacade.getDataOperacao(), sistemaFacade.getExercicioCorrente(), cadastro, tipoPermissao, tipoCalculo, null, null);
    }

    private CalculoRBTrans calcula(Date dataOperacao, Exercicio exercicio, CadastroEconomico cadastro, TipoPermissaoRBTrans tipoPermissao,
                                   TipoCalculoRBTRans tipoCalculo) throws ExcecaoNegocioGenerica {
        return calcula(dataOperacao, exercicio, cadastro, tipoPermissao, tipoCalculo, null, null);
    }

    private CalculoRBTrans calcula(Date dataOperacao,
                                   Exercicio exercicio,
                                   CadastroEconomico cadastro,
                                   TipoPermissaoRBTrans tipoPermissao,
                                   TipoCalculoRBTRans tipoCalculo,
                                   PermissaoTransporte permissao,
                                   Date vencimento) throws ExcecaoNegocioGenerica {

        ProcessoCalculoRBTrans processo = new ProcessoCalculoRBTrans();
        CalculoRBTrans calculo = new CalculoRBTrans();
        calculo.setTipoCalculoRBTRans(tipoCalculo);
        calculo.setNumeroLancamento(singletonGeradorCodigo.getProximoCodigo(CalculoRBTrans.class, "numeroLancamento").intValue());
        calculo.setVencimento(vencimento);

        ParametrosTransitoTransporte param = parametrosTransitoTransporteFacade.recuperarParametroVigentePeloTipo(tipoPermissao);
        if (param != null) {
            List<TaxaTransito> taxas = Lists.newArrayList();
            if (permissao != null) {
                if (TipoCalculoRBTRans.TRANSFERENCIA_PERMISSAO.equals(tipoCalculo) && permissao.isMotoTaxi()) {
                    int quantidade = permissaoTransporteFacade.quantidadeTransferencias(permissao);
                    taxas = parametrosTransitoTransporteFacade.recuperarTaxaTransitoTransferenciaMotoTaxi(param, quantidade + 1);
                    calculo.setVencimento(null);
                }
            }
            if (taxas.isEmpty()) {
                taxas = parametrosTransitoTransporteFacade.recuperarTaxaTransitoPeloTipo(param, tipoCalculo);
            }
            if (taxas != null && !taxas.isEmpty()) {
                try {
                    calculo.setCadastro(cadastro);
                    calculo.setDataCalculo(dataOperacao);
                    calculo.setProcessoCalculo(processo);
                    BigDecimal total = BigDecimal.ZERO;
                    for (TaxaTransito taxa : taxas) {
                        BigDecimal valor = moedaFacade.converterToReal(taxa.getValor());
                        ItemCalculoRBTrans item = new ItemCalculoRBTrans();
                        item.setCalculoRBTrans(calculo);
                        item.setTributo(taxa.getTributo());
                        item.setValor(valor);
                        calculo.getItensCalculo().add(item);
                        total = total.add(valor);
                    }
                    calculo.setValorEfetivo(total);
                    calculo.setValorReal(total);
                    calculo.setSimulacao(false);

                    processo.setDivida(taxas.get(0).getDivida());
                    processo.setParametroTransito(param);
                    processo.setExercicio(exercicio);
                    processo.setDataLancamento(calculo.getDataCalculo());

                    CalculoPessoa cp = new CalculoPessoa();
                    cp.setCalculo(calculo);
                    cp.setPessoa(cadastro.getPessoa());
                    calculo.getPessoas().add(cp);

                    processo.getCalculos().add(calculo);
                    processo = em.merge(processo);
                    return processo.getCalculos().get(0);

                } catch (UFMException ex) {
                    throw new ExcecaoNegocioGenerica("Impossível converter moeda ao gerar o débito");
                } catch (Exception ex) {
                    throw new ExcecaoNegocioGenerica("Impossível calcular: " + ex.getMessage());
                }
            } else {
                String mensagem = "Não foi possivel fazer o calculo porque não possui taxa cadastrada nos parâmetros RBTRANS para "
                    + tipoCalculo.getDescricao() + " na categoria de " + tipoPermissao.getDescricao() + ".";
                throw new ExcecaoNegocioGenerica(mensagem);
            }
        }
        return null;
    }

    public ProcessoCalculoISS gerarIss(CadastroEconomico cadastro) {
        return calculaISSFacade.calcularIssFixo(cadastro, sistemaFacade.getExercicioCorrente(), null);
    }

    public void gerarDebito(CalculoRBTrans calculo) {
        try {
            geraValorDividaRBTrans.geraDebito(calculo.getProcessoCalculo());
        } catch (UFMException ex) {
            throw new ExcecaoNegocioGenerica("Impossível converter moeda ao gerar o débito");
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Impossível calcular: " + ex.getMessage());
        }
    }

    public void gerarDAM(CalculoRBTrans calculo) {
        geraValorDividaRBTrans.geraDam(calculo);
    }


    public CalculoRBTrans calculaBaixaVeiculo(CadastroEconomico cadastroEconomico, TipoPermissaoRBTrans tipoPermissaoRBTrans) throws ExcecaoNegocioGenerica {
        return calcula(cadastroEconomico, tipoPermissaoRBTrans, TipoCalculoRBTRans.BAIXA_VEICULO);
    }

    public CalculoRBTrans calculaInsercaoVeiculo(CadastroEconomico cadastroEconomico, TipoPermissaoRBTrans tipoPermissaoRBTrans) throws ExcecaoNegocioGenerica {
        return calcula(cadastroEconomico, tipoPermissaoRBTrans, TipoCalculoRBTRans.INSERCAO_VEICULO);
    }

    public CalculoRBTrans calculaBaixaMotorista(CadastroEconomico cadastroEconomico, TipoPermissaoRBTrans tipoPermissaoRBTrans) throws ExcecaoNegocioGenerica {
        return calcula(cadastroEconomico, tipoPermissaoRBTrans, TipoCalculoRBTRans.BAIXA_MOTORISTA_AUXILIAR);
    }

    public CalculoRBTrans calculaInsercaoMotorista(CadastroEconomico cadastroEconomico, TipoPermissaoRBTrans tipoPermissaoRBTrans) throws ExcecaoNegocioGenerica {
        return calcula(cadastroEconomico, tipoPermissaoRBTrans, TipoCalculoRBTRans.INSERCAO_MOTORISTA_AUXILIAR);
    }

    public CalculoRBTrans calculaTransferenciaPermissao(CadastroEconomico cadastroEconomico, TipoPermissaoRBTrans tipoPermissaoRBTrans) throws ExcecaoNegocioGenerica {
        return calcula(cadastroEconomico, tipoPermissaoRBTrans, TipoCalculoRBTRans.TRANSFERENCIA_PERMISSAO);
    }

    public CalculoRBTrans calculaTransferenciaPermissaoMotoTaxi(CadastroEconomico cadastroEconomico, PermissaoTransporte permissao) throws ExcecaoNegocioGenerica {
        return calcula(cadastroEconomico, TipoPermissaoRBTrans.MOTO_TAXI, TipoCalculoRBTRans.TRANSFERENCIA_PERMISSAO, permissao);
    }

    public CalculoRBTrans calculaVistoriaVeiculo(CadastroEconomico cadastroEconomico) throws ExcecaoNegocioGenerica {
        return calcula(sistemaFacade.getDataOperacao(), sistemaFacade.getExercicioCorrente(), cadastroEconomico, TipoPermissaoRBTrans.TAXI, TipoCalculoRBTRans.VISTORIA_VEICULO);
    }

    public CalculoRBTrans calculaVistoriaVeiculo(Date dataOperacao, Exercicio exercicio, CadastroEconomico cadastroEconomico, Date vencimento) throws ExcecaoNegocioGenerica {
        return calcula(dataOperacao, exercicio, cadastroEconomico, TipoPermissaoRBTrans.TAXI, TipoCalculoRBTRans.VISTORIA_VEICULO, null, vencimento);
    }

    public CalculoRBTrans calculaAutorizacaoFuncionamento(CadastroEconomico cadastroEconomico) throws ExcecaoNegocioGenerica {
        return calcula(cadastroEconomico, TipoPermissaoRBTrans.FRETE, TipoCalculoRBTRans.AUTORIZACAO_FUNCIONAMENTO);
    }

    public CalculoRBTrans calculaRenovacaoPermissao(Date dataOperacao, Exercicio exercicio, CadastroEconomico cadastroEconomico, TipoPermissaoRBTrans tipoPermissaoRBTrans, PermissaoTransporte permissaoTransporte) throws ExcecaoNegocioGenerica {
        return calcula(dataOperacao, exercicio, cadastroEconomico, tipoPermissaoRBTrans, TipoCalculoRBTRans.RENOVACAO_PERMISSAO, permissaoTransporte, permissaoTransporte.getFinalVigencia());
    }

    public CalculoRBTrans calculaSegundaViaCredencialTransporte(CadastroEconomico cadastroEconomico, TipoPermissaoRBTrans tipoPermissaoRBTrans) throws ExcecaoNegocioGenerica {
        return calcula(cadastroEconomico, tipoPermissaoRBTrans, TipoCalculoRBTRans.SEGUNDA_VIA_CREDENCIAL_TRANSPORTE);
    }

    public CalculoRBTrans calculaSegundaViaCredencialTrafego(CadastroEconomico cadastroEconomico, TipoPermissaoRBTrans tipoPermissaoRBTrans) throws ExcecaoNegocioGenerica {
        return calcula(cadastroEconomico, tipoPermissaoRBTrans, TipoCalculoRBTRans.SEGUNDA_VIA_CREDENCIAL_TRAFEGO);
    }

    public CalculoRBTrans calculaCadastroAutonomo(CadastroEconomico cadastroEconomico, TipoPermissaoRBTrans tipoPermissao) throws ExcecaoNegocioGenerica {
        return calcula(cadastroEconomico, tipoPermissao, TipoCalculoRBTRans.CADASTRO_AUTONOMO);
    }

    public CalculoRBTrans calculaRequerimento(CadastroEconomico cadastroEconomico, TipoPermissaoRBTrans tipoPermissao) throws ExcecaoNegocioGenerica {
        return calcula(cadastroEconomico, tipoPermissao, TipoCalculoRBTRans.REQUERIMENTO);
    }

    public CalculoRBTrans calculaOutorga(Date dataOperacao, Exercicio exercicio, CadastroEconomico cadastroEconomico, Date vencimento) throws ExcecaoNegocioGenerica {
        return calcula(dataOperacao, exercicio, cadastroEconomico, TipoPermissaoRBTrans.MOTO_TAXI, TipoCalculoRBTRans.OUTORGA, null, vencimento);
    }

    public CalculoRBTrans calculaOutorga(CadastroEconomico cadastroEconomico) throws ExcecaoNegocioGenerica {
        return calcula(cadastroEconomico, TipoPermissaoRBTrans.MOTO_TAXI, TipoCalculoRBTRans.OUTORGA);
    }

    public DAM recuperaDAM(Long parcela) {
        return recuperaDAMPeloIdParcela(parcela);
    }

    public DAM recuperaDAMPeloIdParcela(Long idParcela) {
        StringBuilder hql = new StringBuilder("select distinct dam from DAM dam ")
            .append(" join dam.itens item ")
            .append(" where item.parcela.id = :idParcela")
            .append(" order by dam.vencimento");
        Query q = em.createQuery(hql.toString(), DAM.class);
        q.setParameter("idParcela", idParcela);
        if (!q.getResultList().isEmpty()) {
            DAM dam = (DAM) q.getResultList().get(0);
            dam.getItens().size();
            for (ItemDAM item : dam.getItens()) {
                item.getParcela().getValorDivida().getCalculo().getTipoCalculo();
            }
            return dam;
        }
        return null;
    }
}
