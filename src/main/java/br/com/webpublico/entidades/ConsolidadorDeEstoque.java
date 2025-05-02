package br.com.webpublico.entidades;

import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.Util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 04/04/14
 * Time: 11:18
 * To change this template use File | Settings | File Templates.
 */
public class ConsolidadorDeEstoque {
    private Map<Material, Consolidacao> consolidacoes;

    public ConsolidadorDeEstoque() {
        consolidacoes = new HashMap<>();
    }

    public void consolidar(Material m, List<Estoque> estoques, List<ReservaEstoque> reservas) {
        consolidacoes.put(m, new Consolidacao(estoques, reservas));
    }

    public void validarItensDaAprovacao(Map<Material, BigDecimal> materiais) throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();

        for (Map.Entry<Material, BigDecimal> entry : materiais.entrySet()) {
            BigDecimal fisicoConsolidado = getFisicoConsolidado(entry.getKey());
            if (fisicoConsolidado == null) {
                ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_REALIZADA, "O local de estoque de origem não possui o material " + entry.getKey().getDescricao().toString().toUpperCase() + " em estoque.");
            } else if (fisicoConsolidado.compareTo(entry.getValue()) < 0) {
                ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "Não existe a quantidade necessária em estoque para atender o item "
                        + entry.getKey().toString().toUpperCase() + ". Quantidade em estoque: " + Util.formataQuandoDecimal(fisicoConsolidado, entry.getKey().getMascaraQuantidade()));
            }
        }
        ve.lancarException();
    }

    public BigDecimal getFisicoConsolidado(Material m) {
        return consolidacoes.get(m).fisicoConsolidado;
    }

    public BigDecimal getFinanceiro(Material m) {
        return consolidacoes.get(m).financeiro;
    }

    public BigDecimal getCalculaMateriais() {
        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<Material, Consolidacao> entry : consolidacoes.entrySet()) {
            total = total.add(entry.getValue().financeiro);
        }
        return total;
    }

    public BigDecimal getFisicoReservado(Material m) {
        return consolidacoes.get(m).fisicoReservado;
    }

    public BigDecimal getFisico(Material m) {
        return consolidacoes.get(m).fisico;
    }

    public BigDecimal getValorUnitarioConsolidado(Material m) {
        if (getFinanceiro(m).compareTo(BigDecimal.ZERO) > 0 && getFisicoConsolidado(m).compareTo(BigDecimal.ZERO) > 0) {
            return getFinanceiro(m).divide(getFisicoConsolidado(m), 8, RoundingMode.HALF_EVEN).setScale(4, RoundingMode.HALF_EVEN);
        }
        return BigDecimal.ZERO;
    }

    public Estoque getEstoque(Material m, LocalEstoqueOrcamentario leo) {
        Estoque e = consolidacoes.get(m).mapaLocalEEstoque.get(leo);
        if (e == null) {
            throw new ExcecaoNegocioGenerica("Não existe estoque do material " + m + ", no local de estoque " + leo.getLocalEstoque() + ", contabilizado na unidade orçamentária " + leo.getUnidadeOrcamentaria() + ".");
        }
        return e;
    }

    public List<Estoque> getEstoques(Material m) {
        return consolidacoes.get(m).estoques;
    }

    public List<EstoqueLoteMaterial> getLotes(Material m) {
        return consolidacoes.get(m).lotes;
    }

    public EstoqueLoteMaterial getEstoqueLoteMaterial(Estoque e, LoteMaterial loteMaterial) {
        for (EstoqueLoteMaterial elm : e.getLotesMaterial()) {
            if (elm.getLoteMaterial().equals(loteMaterial)) {
                return elm;
            }
        }

        return null;
    }

    private class Consolidacao {
        /*Total físico descontando as reservas*/
        public BigDecimal fisicoConsolidado;
        private BigDecimal fisico;
        /*Total físico reservado*/
        private BigDecimal fisicoReservado;
        private BigDecimal financeiro;
        private Map<LocalEstoqueOrcamentario, Estoque> mapaLocalEEstoque;
        private List<Estoque> estoques;
        private List<EstoqueLoteMaterial> lotes;
        private List<ReservaEstoque> reservas;

        public Consolidacao(List<Estoque> estoques, List<ReservaEstoque> reservas) {
            this.estoques = estoques;
            this.reservas = reservas;
            this.fisicoConsolidado = BigDecimal.ZERO;
            this.fisicoReservado = BigDecimal.ZERO;
            preencherMapaELista();
            consolidar();
        }

        private BigDecimal calcularFinanceiro() {
            BigDecimal retorno = BigDecimal.ZERO;
            for (Estoque e : estoques) {
                retorno = retorno.add(e.getFinanceiro());
            }
            return retorno;
        }

        private void preencherMapaELista() {
            mapaLocalEEstoque = new TreeMap<>();
            lotes = new ArrayList<>();
            for (Estoque e : estoques) {
                mapaLocalEEstoque.put(e.getLocalEstoqueOrcamentario(), e);
                lotes.addAll(e.getLotesMaterial());
            }
            Collections.sort(lotes);
        }

        /*A ordem dos cálculos altera os resultados*/
        private void consolidar() {
            fisicoReservado = calcularFisicoReservado();
            fisico = calcularFisicoConsolidado();
            fisicoConsolidado = fisico.subtract(fisicoReservado);
            financeiro = calcularFinanceiro();
        }

        private BigDecimal calcularFisicoReservado() {
            BigDecimal reservado = BigDecimal.ZERO;
            for (ReservaEstoque re : reservas) {
                reservado = reservado.add(re.getQuantidadeReservada());
            }
            return reservado;
        }

        private BigDecimal calcularFisicoConsolidado() {
            BigDecimal fisico = BigDecimal.ZERO;
            for (Map.Entry<LocalEstoqueOrcamentario, Estoque> entry : mapaLocalEEstoque.entrySet()) {
                fisico = fisico.add(entry.getValue().getFisico());
            }
            return fisico;
        }
    }
}
