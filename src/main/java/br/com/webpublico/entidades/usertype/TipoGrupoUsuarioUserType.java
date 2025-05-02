package br.com.webpublico.entidades.usertype;

import br.com.webpublico.enums.TipoGrupoUsuario;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class TipoGrupoUsuarioUserType extends ImmutableUserType {

    private static final long serialVersionUID = 1L;

    private static final BiMap<TipoGrupoUsuario, String> values = ImmutableBiMap.of(TipoGrupoUsuario.AUTORIZACAO, "A", TipoGrupoUsuario.NEGACAO, "N");

    @Override
    public int[] sqlTypes() {
        return new int[]{Types.CHAR};
    }

    @Override
    public Class<?> returnedClass() {
        return TipoGrupoUsuario.class;
    }

    @Override
    public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws HibernateException, SQLException {
        String value = rs.getString(names[0]);
        if (rs.wasNull()) {
            return null;
        } else {
            TipoGrupoUsuario tipo = values.inverse().get(value);
            if (tipo != null) {
                return tipo;
            } else {
                throw new IllegalStateException(String.format("Tipo não encontrado para valor '%s'", value));
            }
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor sharedSessionContractImplementor) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.CHAR);
        } else {
            TipoGrupoUsuario tipo = (TipoGrupoUsuario) value;
            st.setString(index, values.get(tipo));
        }
    }

}
