package br.com.webpublico.negocios.tributario.setter;

import br.com.webpublico.entidades.Calculo;
import br.com.webpublico.entidades.InscricaoDividaParcela;
import br.com.webpublico.entidades.ParcelaValorDivida;
import br.com.webpublico.entidades.SituacaoParcelaValorDivida;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.interfaces.GeradorReferenciaParcela;
import br.com.webpublico.negocios.tributario.dao.JdbcDividaAtivaDAO;
import com.google.common.collect.Maps;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class SituacaoParcelaValorDividaSetter implements BatchPreparedStatementSetter {
    private final List<?> parcelas;
    private final List<Long> ids;
    private final SituacaoParcela situacao;
    private final boolean gerarReferencia;
    private final Map<Calculo.TipoCalculo, GeradorReferenciaParcela> geradoresReferencia;
    private GeradorReferenciaParcela geradorReferencia;
    private Long idRevisao;

    public SituacaoParcelaValorDividaSetter(List<?> parcelas, SituacaoParcela situacao, List<Long> ids, boolean gerarReferencia) {
        this.parcelas = parcelas;
        this.situacao = situacao;
        this.ids = ids;
        this.geradoresReferencia = Maps.newHashMap();
        this.gerarReferencia = gerarReferencia;
    }

    public SituacaoParcelaValorDividaSetter(List<?> parcelas, SituacaoParcela situacao, List<Long> ids, boolean gerarReferencia, Long idRevisao) {
        this.parcelas = parcelas;
        this.situacao = situacao;
        this.ids = ids;
        this.geradoresReferencia = Maps.newHashMap();
        this.gerarReferencia = gerarReferencia;
    }

    public SituacaoParcelaValorDividaSetter(List<?> parcelas, SituacaoParcela situacao, List<Long> ids) {
        this.parcelas = parcelas;
        this.situacao = situacao;
        this.ids = ids;
        this.geradoresReferencia = Maps.newHashMap();
        this.gerarReferencia = true;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        try {
            ParcelaValorDivida parcela = getParcelaValorDivida(i);
            SituacaoParcelaValorDivida sit = new SituacaoParcelaValorDivida();
            sit.setId(ids.get(i));
            sit.setParcela(parcela);
            sit.setDataLancamento(new java.util.Date());
            sit.setSituacaoParcela(parcela.getSituacaoAtual() != null ? parcela.getSituacaoAtual().getSituacaoParcela() : situacao);
            sit.setSaldo(parcela.getValor());
            if (gerarReferencia) {
                sit.setReferencia(geradorReferencia.geraReferencia(parcela.getId()));
            }

            ps.setLong(1, sit.getId());
            ps.setDate(2, new Date(sit.getDataLancamento().getTime()));
            ps.setString(3, sit.getSituacaoParcela().name());
            ps.setLong(4, parcela.getId());
            ps.setBigDecimal(5, parcela.getValor());
            ps.setString(6, sit.getReferencia());

            if (idRevisao != null) {
                ps.setLong(7, idRevisao);
                ps.setLong(8, 0L);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ParcelaValorDivida getParcelaValorDivida(int i) throws IllegalAccessException, InstantiationException {
        Object o = parcelas.get(i);
        if (o instanceof ParcelaValorDivida) {
            ParcelaValorDivida parcela = (ParcelaValorDivida) o;
            if (gerarReferencia) {
                defineGeradorReferencia(parcela.getValorDivida().getCalculo().getTipoCalculo());
            }
            return parcela;
        } else if (o instanceof InscricaoDividaParcela) {
            InscricaoDividaParcela inscricao = (InscricaoDividaParcela) o;
            if (gerarReferencia) {
                defineGeradorReferencia(inscricao.getTipoCalculo());
            }
            ParcelaValorDivida parcela = new ParcelaValorDivida();
            parcela.setId(inscricao.getIdParcela());
            parcela.setValor(inscricao.getSaldoParcela());
            return parcela;
        }
        return null;
    }

    private void defineGeradorReferencia(Calculo.TipoCalculo tipoCalculo) throws InstantiationException, IllegalAccessException {
        if (!geradoresReferencia.containsKey(tipoCalculo)) {
            geradoresReferencia.put(tipoCalculo, tipoCalculo.getGeradorReferenciaParcela());
        }
        geradorReferencia = geradoresReferencia.get(tipoCalculo);
    }

    @Override
    public int getBatchSize() {
        return parcelas.size();
    }
}
