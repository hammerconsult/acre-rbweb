package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.DateUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class PlanoGeralContasComentadoTarifaBancariaNfseDTO implements BatchPreparedStatementSetter {

    private Long id;
    private TarifaBancariaNfseDTO tarifaBancaria;
    private Date inicioVigencia;
    private BigDecimal valorUnitario;
    private BigDecimal valorPercentual;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TarifaBancariaNfseDTO getTarifaBancaria() {
        return tarifaBancaria;
    }

    public void setTarifaBancaria(TarifaBancariaNfseDTO tarifaBancaria) {
        this.tarifaBancaria = tarifaBancaria;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public BigDecimal getValorPercentual() {
        return valorPercentual;
    }

    public void setValorPercentual(BigDecimal valorPercentual) {
        this.valorPercentual = valorPercentual;
    }

    @Override
    public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
        preparedStatement.setLong(1, id);
        preparedStatement.setLong(2, tarifaBancaria.getId());
        preparedStatement.setDate(3, DateUtils.toSQLDate(inicioVigencia));
        preparedStatement.setBigDecimal(4, valorUnitario);
        preparedStatement.setBigDecimal(5, valorPercentual);
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
