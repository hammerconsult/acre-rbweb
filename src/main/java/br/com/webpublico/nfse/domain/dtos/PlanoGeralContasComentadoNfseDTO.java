package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.DateUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

public class PlanoGeralContasComentadoNfseDTO implements RowMapper<PlanoGeralContasComentadoNfseDTO>, BatchPreparedStatementSetter {

    private Long id;
    private PrestadorServicoNfseDTO prestador;
    private Date inicioVigencia;
    private Date fimVigencia;
    private String conta;
    private String desdobramento;
    private String nome;
    private String descricaoDetalhada;
    private Long idContaSuperior;
    private String contaSuperior;
    private CosifNfseDTO cosif;
    private ServicoNfseDTO servico;
    private CodigoTributacaoNfseDTO codigoTributacao;
    private PlanoGeralContasComentadoTarifaBancariaNfseDTO tarifaBancaria;
    private PlanoGeralContasComentadoProdutoServicoNfseDTO produtoServico;
    private Boolean update;

    public PlanoGeralContasComentadoNfseDTO() {
        this.update = Boolean.FALSE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PrestadorServicoNfseDTO getPrestador() {
        return prestador;
    }

    public void setPrestador(PrestadorServicoNfseDTO prestador) {
        this.prestador = prestador;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public String getDesdobramento() {
        return desdobramento;
    }

    public void setDesdobramento(String desdobramento) {
        this.desdobramento = desdobramento;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricaoDetalhada() {
        return descricaoDetalhada;
    }

    public void setDescricaoDetalhada(String descricaoDetalhada) {
        this.descricaoDetalhada = descricaoDetalhada;
    }

    public Long getIdContaSuperior() {
        return idContaSuperior;
    }

    public void setIdContaSuperior(Long idContaSuperior) {
        this.idContaSuperior = idContaSuperior;
    }

    public String getContaSuperior() {
        return contaSuperior;
    }

    public void setContaSuperior(String contaSuperior) {
        this.contaSuperior = contaSuperior;
    }

    public CosifNfseDTO getCosif() {
        return cosif;
    }

    public void setCosif(CosifNfseDTO cosif) {
        this.cosif = cosif;
    }

    public ServicoNfseDTO getServico() {
        return servico;
    }

    public void setServico(ServicoNfseDTO servico) {
        this.servico = servico;
    }

    public CodigoTributacaoNfseDTO getCodigoTributacao() {
        return codigoTributacao;
    }

    public void setCodigoTributacao(CodigoTributacaoNfseDTO codigoTributacao) {
        this.codigoTributacao = codigoTributacao;
    }

    public PlanoGeralContasComentadoTarifaBancariaNfseDTO getTarifaBancaria() {
        return tarifaBancaria;
    }

    public void setTarifaBancaria(PlanoGeralContasComentadoTarifaBancariaNfseDTO tarifaBancaria) {
        this.tarifaBancaria = tarifaBancaria;
    }

    public PlanoGeralContasComentadoProdutoServicoNfseDTO getProdutoServico() {
        return produtoServico;
    }

    public void setProdutoServico(PlanoGeralContasComentadoProdutoServicoNfseDTO produtoServico) {
        this.produtoServico = produtoServico;
    }

    public Boolean getUpdate() {
        return update;
    }

    public void setUpdate(Boolean update) {
        this.update = update;
    }

    @Override
    public PlanoGeralContasComentadoNfseDTO mapRow(ResultSet resultSet, int i) throws SQLException {
        PlanoGeralContasComentadoNfseDTO dto = new PlanoGeralContasComentadoNfseDTO();
        dto.setId(resultSet.getLong("id"));
        dto.setPrestador(new PrestadorServicoNfseDTO());
        dto.getPrestador().setId(resultSet.getLong("cadastroeconomico_id"));
        dto.setConta(resultSet.getString("conta"));
        dto.setDesdobramento(resultSet.getString("desdobramento"));
        dto.setNome(resultSet.getString("nome"));
        dto.setDescricaoDetalhada(resultSet.getString("descricaodetalhada"));
        dto.setInicioVigencia(resultSet.getDate("iniciovigencia"));
        dto.setFimVigencia(resultSet.getDate("fimvigencia"));
        if (resultSet.getLong("id_superior") != 0) {
            dto.setIdContaSuperior(resultSet.getLong("id_superior"));
            dto.setContaSuperior(resultSet.getString("conta_superior"));
        }
        if (resultSet.getLong("cosif_id") != 0) {
            dto.setCosif(new CosifNfseDTO());
            dto.getCosif().setId(resultSet.getLong("cosif_id"));
        }
        if (resultSet.getLong("codigotributacao_id") != 0) {
            dto.setCodigoTributacao(new CodigoTributacaoNfseDTO());
            dto.getCodigoTributacao().setId(resultSet.getLong("codigotributacao_id"));
        }
        if (resultSet.getLong("id_rel_tarifa") != 0) {
            dto.setTarifaBancaria(new PlanoGeralContasComentadoTarifaBancariaNfseDTO());
            dto.getTarifaBancaria().setId(resultSet.getLong("id_rel_tarifa"));
            dto.getTarifaBancaria().setTarifaBancaria(new TarifaBancariaNfseDTO());
            dto.getTarifaBancaria().getTarifaBancaria().setId(resultSet.getLong("id_tarifa"));
            dto.getTarifaBancaria().setInicioVigencia(resultSet.getDate("iniciovigencia_tarifa"));
            dto.getTarifaBancaria().setValorUnitario(resultSet.getBigDecimal("valorunitario_tarifa"));
            dto.getTarifaBancaria().setValorPercentual(resultSet.getBigDecimal("valorpercentual_tarifa"));
        }
        if (resultSet.getLong("id_rel_prodserv") != 0) {
            dto.setProdutoServico(new PlanoGeralContasComentadoProdutoServicoNfseDTO());
            dto.getProdutoServico().setId(resultSet.getLong("id_rel_prodserv"));
            dto.getProdutoServico().setProdutoServico(new ProdutoServicoBancarioNfseDTO());
            dto.getProdutoServico().getProdutoServico().setId(resultSet.getLong("id_prodserv"));
            dto.getProdutoServico().setDescricaoComplementar(resultSet.getString("descricaocomplementar_prodserv"));
        }
        return dto;
    }

    @Override
    public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
        if (!update) {
            preparedStatement.setLong(1, id);
            preparedStatement.setLong(2, prestador.getId());
            preparedStatement.setDate(3, DateUtils.toSQLDate(inicioVigencia));
            preparedStatement.setDate(4, DateUtils.toSQLDate(fimVigencia));
            preparedStatement.setString(5, conta);
            preparedStatement.setString(6, desdobramento);
            preparedStatement.setString(7, nome);
            preparedStatement.setString(8, descricaoDetalhada);
            if (cosif != null) {
                preparedStatement.setLong(9, cosif.getId());
            } else {
                preparedStatement.setNull(9, Types.NULL);
            }
            if (codigoTributacao != null) {
                preparedStatement.setLong(10, codigoTributacao.getId());
            } else {
                preparedStatement.setNull(10, Types.NULL);
            }
            if (tarifaBancaria != null) {
                preparedStatement.setLong(11, tarifaBancaria.getId());
            } else {
                preparedStatement.setNull(11, Types.NULL);
            }
            if (produtoServico != null) {
                preparedStatement.setLong(12, produtoServico.getId());
            } else {
                preparedStatement.setNull(12, Types.NULL);
            }
            if (idContaSuperior != null) {
                preparedStatement.setLong(13, idContaSuperior);
            } else {
                preparedStatement.setNull(13, Types.NULL);
            }
        } else {
            preparedStatement.setString(1, desdobramento);
            preparedStatement.setString(2, nome);
            preparedStatement.setString(3, descricaoDetalhada);
            if (cosif != null) {
                preparedStatement.setLong(4, cosif.getId());
            } else {
                preparedStatement.setNull(4, Types.NULL);
            }
            if (codigoTributacao != null) {
                preparedStatement.setLong(5, codigoTributacao.getId());
            } else {
                preparedStatement.setNull(5, Types.NULL);
            }
            if (tarifaBancaria != null) {
                preparedStatement.setLong(6, tarifaBancaria.getId());
            } else {
                preparedStatement.setNull(6, Types.NULL);
            }
            if (produtoServico != null) {
                preparedStatement.setLong(7, produtoServico.getId());
            } else {
                preparedStatement.setNull(7, Types.NULL);
            }
            if (idContaSuperior != null) {
                preparedStatement.setLong(8, idContaSuperior);
            } else {
                preparedStatement.setNull(8, Types.NULL);
            }
            preparedStatement.setLong(9, id);
        }
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
