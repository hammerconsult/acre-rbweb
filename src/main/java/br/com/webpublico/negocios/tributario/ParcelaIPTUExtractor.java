package br.com.webpublico.negocios.tributario;

import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ParcelaIPTUExtractor implements ResultSetExtractor<List<VOParcelaExportacaoDebitosIPTU>> {
    private static final Logger logger = LoggerFactory.getLogger(ParcelaIPTUExtractor.class);

    private final AssistenteExportacaoDebitosIPTU assistente;

    public ParcelaIPTUExtractor(AssistenteExportacaoDebitosIPTU assistente) {
        this.assistente = assistente;
    }

    @Override
    public List<VOParcelaExportacaoDebitosIPTU> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<VOParcelaExportacaoDebitosIPTU> parcelasIPTU = Lists.newArrayList();
        Long totalRegistros = 0L;
        while (rs.next()) {
            if (totalRegistros.equals(0L)) {
                totalRegistros = rs.getLong("total");
                assistente.setTotal(totalRegistros.intValue());
            }

            VOParcelaExportacaoDebitosIPTU parcelaIPTU = new VOParcelaExportacaoDebitosIPTU();

            ResultadoParcela parcela = new ResultadoParcela();
            parcela.setIdCadastro(rs.getLong("idCadastro"));
            parcela.setIdParcela(rs.getLong("idParcela"));
            parcela.setDivida(rs.getString("divida") != null ? rs.getString("divida") : "");
            parcela.setExercicio(rs.getInt("exercicio"));
            parcela.setValorImposto(rs.getBigDecimal("valorparcela") != null ? rs.getBigDecimal("valorparcela") : BigDecimal.ZERO);
            parcela.setCotaUnica(rs.getBoolean("cotaunica"));
            parcela.setParcela(rs.getString("sequenciaparcela") != null ? rs.getString("sequenciaparcela") : "");
            parcela.setIdPessoa(rs.getLong("idPessoa"));
            parcela.setVencimento(rs.getDate("vencimento"));
            parcela.setIdValorDivida(rs.getLong("valordivida"));
            parcelaIPTU.setParcela(parcela);

            adicionarCadastroImobiliario(rs, parcelaIPTU);
            adicionarPessoa(rs, parcelaIPTU, parcela);

            boolean hasDAM = rs.getBoolean("hasdam");
            if (hasDAM) {
                VODam dam = new VODam();
                dam.setIdDam(rs.getLong("id_dam"));
                dam.setCodigoBarras(rs.getString("cod_barras"));
                dam.setVencimento(rs.getDate("vencimento"));
                dam.setCodigoBarrasCotaUnica(rs.getString("cod_barras_cota_unica"));
                parcelaIPTU.setDam(dam);

                parcelasIPTU.add(parcelaIPTU);

                assistente.conta();
            } else {
                if (!assistente.getParcelasPorValorDivida().containsKey(parcela.getIdValorDivida())) {
                    assistente.getParcelasPorValorDivida().put(parcela.getIdValorDivida(), Lists.newArrayList(parcelaIPTU));
                } else {
                    assistente.getParcelasPorValorDivida().get(parcela.getIdValorDivida()).add(parcelaIPTU);
                }
            }
        }
        return parcelasIPTU;
    }

    private void adicionarPessoa(ResultSet rs, VOParcelaExportacaoDebitosIPTU parcelaIPTU, ResultadoParcela parcela) throws SQLException {
        String dadosPessoa = rs.getString("dadosPessoa");
        if (dadosPessoa != null) {
            VOPessoa pessoa = new VOPessoa();
            pessoa.setId(parcela.getIdParcela());
            pessoa.setCpfCnpj(StringUtils.substringBefore(dadosPessoa, ";"));
            pessoa.setTipoPessoa(StringUtils.substringAfter(dadosPessoa, ";"));

            parcelaIPTU.setPessoa(pessoa);
        }
    }

    private void adicionarCadastroImobiliario(ResultSet rs, VOParcelaExportacaoDebitosIPTU parcelaIPTU) throws SQLException {
        String dadosCadastro = rs.getString("dadosCadastro");
        if (dadosCadastro != null) {
            VOCadastroImobiliario cadastroImobiliario = new VOCadastroImobiliario();
            cadastroImobiliario.setInscricaoCadastral(StringUtils.substringBefore(dadosCadastro, ";"));
            cadastroImobiliario.setValorVenal(!StringUtils.substringAfter(dadosCadastro, ";").isEmpty() ?
                new BigDecimal(StringUtils.substringAfter(dadosCadastro, ";").replace(",", ".")) : BigDecimal.ZERO);
            parcelaIPTU.setCadastroImobiliario(cadastroImobiliario);
        }
    }
}
