package br.com.webpublico.entidades.usertype;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Set;

import org.hibernate.HibernateException;
import org.junit.Test;

import br.com.webpublico.enums.Direito;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class DireitosUserTypeTest {

	@Test
	public void testNullSafeGet() throws SQLException {
		DireitosUserType userType = new DireitosUserType();
		ResultSet rs = mock(ResultSet.class);
		when(rs.getInt(anyString())).thenReturn(0, 5, 4, 0);
		when(rs.wasNull()).thenReturn(true, false);
		String[] names = new String[] { "a" };
		assertTrue(Sets.difference(ImmutableSet.of(), (Set<Direito>) userType.nullSafeGet(rs, names, null, null)).isEmpty());
		assertTrue(Sets.difference(ImmutableSet.of(Direito.LEITURA, Direito.EXCLUSAO),
				(Set<Direito>) userType.nullSafeGet(rs, names, null, null)).isEmpty());
		assertTrue(Sets.difference(ImmutableSet.of(Direito.LEITURA),
				(Set<Direito>) userType.nullSafeGet(rs, names, null, null)).isEmpty());
		assertTrue(Sets.difference(ImmutableSet.of(), (Set<Direito>) userType.nullSafeGet(rs, names, null, null)).isEmpty());
	}

	@Test
	public void testNullSafeSet() throws HibernateException, SQLException {
		DireitosUserType userType = new DireitosUserType();
		PreparedStatement st = mock(PreparedStatement.class);
		userType.nullSafeSet(st, null, 0, null);
		userType.nullSafeSet(st, ImmutableSet.of(Direito.LEITURA, Direito.EXCLUSAO), 1, null);
		userType.nullSafeSet(st, ImmutableSet.of(Direito.ESCRITA), 1, null);
		userType.nullSafeSet(st, ImmutableSet.of(), 1, null);
		verify(st).setNull(0, Types.SMALLINT);
		verify(st).setInt(1, 5);
		verify(st).setInt(1, 2);
		verify(st).setInt(1, 0);
	}
}
