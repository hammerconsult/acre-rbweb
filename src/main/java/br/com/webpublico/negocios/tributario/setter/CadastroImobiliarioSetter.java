package br.com.webpublico.negocios.tributario.setter;

import br.com.webpublico.entidades.CadastroImobiliario;
import br.com.webpublico.util.Persistencia;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;

public class CadastroImobiliarioSetter implements BatchPreparedStatementSetter {

    private final Long idRevisao;
    private final CadastroImobiliario cadastroImobiliario;

    public CadastroImobiliarioSetter(Long idRevisao, CadastroImobiliario cadastroImobiliario) {
        this.idRevisao = idRevisao;
        this.cadastroImobiliario = cadastroImobiliario;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        adicionarAtributoId(ps, 1, cadastroImobiliario.getId());
        adicionarAtributo(ps, 2, idRevisao);
        adicionarAtributo(ps, 3, 1);
        adicionarAtributo(ps, 4, cadastroImobiliario.getCodigo());
        adicionarAtributo(ps, 5, cadastroImobiliario.getFolhaRegistro());
        adicionarAtributo(ps, 6, cadastroImobiliario.getInscricaoCadastral());
        adicionarAtributo(ps, 7, cadastroImobiliario.getLivroRegistro());
        adicionarAtributo(ps, 8, cadastroImobiliario.getMatriculaRegistro());
        adicionarAtributoId(ps, 9, cadastroImobiliario.getAtoLegalIsencao());
        adicionarAtributoId(ps, 10, cadastroImobiliario.getCartorio());
        adicionarAtributoId(ps, 11, cadastroImobiliario.getLote());
        adicionarAtributoId(ps, 12, cadastroImobiliario.getObservacoes());
        adicionarAtributoId(ps, 13, cadastroImobiliario.getProcesso());
        adicionarAtributo(ps, 14, cadastroImobiliario.getAtivo());
        adicionarAtributo(ps, 15, cadastroImobiliario.getDataInativacao());
        adicionarAtributo(ps, 16, cadastroImobiliario.getMotivoInativacao());
        adicionarAtributo(ps, 17, cadastroImobiliario.getAreaTotalConstruida());
        adicionarAtributo(ps, 18, cadastroImobiliario.getDataIsencao());
        adicionarAtributoId(ps, 19, cadastroImobiliario.getTipoIsencao());
        adicionarAtributo(ps, 20, cadastroImobiliario.getComplementoEndereco());
        adicionarAtributo(ps, 21, cadastroImobiliario.isImune());
        adicionarAtributo(ps, 22, cadastroImobiliario.getTipoMatricula());
        adicionarAtributo(ps, 23, cadastroImobiliario.getDataRegistro());
        adicionarAtributo(ps, 24, cadastroImobiliario.getDataTitulo());
        adicionarAtributo(ps, 25, cadastroImobiliario.getNumeroDoTitulo());
        adicionarAtributoId(ps, 26, cadastroImobiliario.getUnidadeExterna());
        adicionarAtributoId(ps, 27, cadastroImobiliario.getUnidadeOrganizacional());
        adicionarAtributo(ps, 28, cadastroImobiliario.getIdentificacaoPatrimonial());
        adicionarAtributo(ps, 29, cadastroImobiliario.getAutonoma());
        adicionarAtributo(ps, 30, cadastroImobiliario.getNumero());
    }

    @Override
    public int getBatchSize() {
        return 1;
    }

    public void adicionarAtributoId(PreparedStatement ps, int index, Object valor) throws SQLException {
        try {
            if (valor != null) {
                ps.setLong(index, (Long) Persistencia.getId(valor));
            } else {
                ps.setNull(index, Types.NULL);
            }
        } catch (Exception e) {
            ps.setNull(index, Types.NULL);
        }
    }

    public <T> void adicionarAtributo(PreparedStatement ps, int index, T valor) throws SQLException {
        try {
            if (valor != null) {
                if (valor instanceof Long) {
                    ps.setLong(index, (Long) valor);
                } else if (valor instanceof BigDecimal) {
                    ps.setBigDecimal(index, (BigDecimal) valor);
                } else if (valor instanceof String) {
                    ps.setString(index, (String) valor);
                } else if (valor instanceof Boolean) {
                    ps.setBoolean(index, (Boolean) valor);
                } else if (valor instanceof Integer) {
                    ps.setInt(index, (Integer) valor);
                } else if (valor instanceof Date) {
                    ps.setDate(index, new java.sql.Date(((Date) valor).getTime()));
                } else if (valor instanceof Enum) {
                    ps.setString(index, ((Enum<?>) valor).name());
                }
            } else {
                ps.setNull(index, Types.NULL);
            }
        } catch (Exception e) {
            ps.setNull(index, Types.NULL);
        }
    }
}
