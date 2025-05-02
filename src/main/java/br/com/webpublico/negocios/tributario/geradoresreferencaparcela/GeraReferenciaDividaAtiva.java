package br.com.webpublico.negocios.tributario.geradoresreferencaparcela;

import br.com.webpublico.entidades.Calculo;
import br.com.webpublico.entidades.ItemInscricaoDividaAtiva;
import br.com.webpublico.entidades.SituacaoParcelaValorDivida;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.negocios.tributario.rowmapper.IdETipoCalculoParcelaOriginalRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Query;


public class GeraReferenciaDividaAtiva extends GeraReferencia<ItemInscricaoDividaAtiva> {

    private static final Logger logger = LoggerFactory.getLogger(GeraReferenciaDividaAtiva.class);

    private Long idParcelaOriginal;
    private Long idParcelaOriginada;
    private Calculo.TipoCalculo tipoCalculoOriginal;
    private SituacaoParcela situacaoParcela;

    @Override
    public void geraReferencia(SituacaoParcelaValorDivida spvd) {
        this.idParcelaOriginada = spvd.getParcela().getId();
        this.situacaoParcela = spvd.getSituacaoParcela();
        recuperaIdParcelaOriginalETipoCalculo();
        spvd.setReferencia(getReferencia());
    }

    @Override
    public String geraReferencia(Long idParcela) {
        this.idParcelaOriginada = idParcela;
        recuperaIdParcelaOriginalETipoCalculo();
        return getReferencia();
    }

    protected String getReferencia() {
        try {
            String retorno = "";
            if (tipoCalculoOriginal != null) {
                retorno = tipoCalculoOriginal.getGeradorReferenciaParcela().geraReferencia(idParcelaOriginal);
            } else {
                retorno = recuperaExercicioValorDividaDividaAtiva();
            }

            if (!retorno.toLowerCase().contains("parcelamento") && situacaoParcela != null) {
                if (situacaoParcela.isParcelado()) {
                    retorno = retorno + recuperaNumeroDoParcelamento();
                } else if (situacaoParcela.isSoPago()) {
                    retorno = retorno + buscarDosParcelamentoPagos(idParcelaOriginada);
                } else if (situacaoParcela.isPago()) {
                    retorno = retorno + buscarDeTodosParcelamentos(idParcelaOriginada);
                }
            }
            return retorno;
        } catch (Exception e) {
            logger.error("Erro ao gerar a referencia: {}", e);
            return " ";
        }
    }

    private void recuperaIdParcelaOriginalETipoCalculo() {
        try {
            IdETipoCalculoParcelaOriginalRowMapper mapper = dao.recuperaIdParcelaOriginalETipoCalculo(idParcelaOriginada);
            idParcelaOriginal = mapper.getIdParcelaOriginal();
            tipoCalculoOriginal = mapper.getTipoCalculoOriginal();
        } catch (Exception e) {
            idParcelaOriginal = null;
            tipoCalculoOriginal = null;
        }
    }

    private String recuperaNumeroDoParcelamento() {
        String sql = "select parcelamento.numero || '/' || exercicio.ano " +
                " from processoparcelamento parcelamento\n" +
                " inner join itemprocessoparcelamento it on it.processoparcelamento_id = parcelamento.id\n" +
                " inner join exercicio on exercicio.id = parcelamento.exercicio_id\n" +
                " where it.parcelavalordivida_id = :parcela " +
                " and (parcelamento.situacaoParcelamento = 'FINALIZADO'" +
                " or parcelamento.situacaoParcelamento = 'PAGO')" +
                " order by parcelamento.id desc";
        Query q = getEm().createNativeQuery(sql);
        q.setParameter("parcela", idParcelaOriginada);
        q.setMaxResults(1);
        String retorno = "";
        if (!q.getResultList().isEmpty()) {
            retorno = " Parcelamento: " + q.getResultList().get(0);
        }
        return retorno;
    }

    private String recuperaExercicioValorDividaDividaAtiva() {
        String sql = "select ex.ano from ValorDivida vd " +
                " inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id" +
                " inner join exercicio ex on ex.id = vd.exercicio_id" +
                " where pvd.id = :parcela";
        Query q = getEm().createNativeQuery(sql);
        q.setParameter("parcela", idParcelaOriginada);
        q.setMaxResults(1);
        String retorno = "";
        if (!q.getResultList().isEmpty()) {
            retorno = "Exercício: " + q.getResultList().get(0);
        }
        return retorno;
    }

}
