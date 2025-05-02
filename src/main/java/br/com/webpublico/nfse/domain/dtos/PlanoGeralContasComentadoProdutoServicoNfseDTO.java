package br.com.webpublico.nfse.domain.dtos;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PlanoGeralContasComentadoProdutoServicoNfseDTO implements BatchPreparedStatementSetter {

    private Long id;
    private ProdutoServicoBancarioNfseDTO produtoServico;
    private String descricaoComplementar;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProdutoServicoBancarioNfseDTO getProdutoServico() {
        return produtoServico;
    }

    public void setProdutoServico(ProdutoServicoBancarioNfseDTO produtoServico) {
        this.produtoServico = produtoServico;
    }

    public String getDescricaoComplementar() {
        return descricaoComplementar;
    }

    public void setDescricaoComplementar(String descricaoComplementar) {
        this.descricaoComplementar = descricaoComplementar;
    }

    @Override
    public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
        preparedStatement.setLong(1, id);
        preparedStatement.setLong(2, produtoServico.getId());
        preparedStatement.setString(3, descricaoComplementar);
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
