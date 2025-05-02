package br.com.webpublico.entidades;

import static org.junit.Assert.*;

import br.com.webpublico.enums.Direito;
import org.junit.Test;

public class DireitoTest {

	@Test
	public void testMatches() {
		assertTrue(Direito.LEITURA.matches("/faces/cep/ceplocalidade/lista.xhtml"));
		assertTrue(Direito.LEITURA.matches("/faces/cep/ceplocalidade/visualizar.xhtml"));
		assertTrue(Direito.LEITURA.matches("/faces/cep/ceplocalidade/lista.jsf"));
		assertTrue(Direito.LEITURA.matches("/faces/cep/ceplocalidade/visualizar.jsf"));
		assertFalse(Direito.LEITURA.matches("/faces/cep/ceplocalidade/edita.jsf"));
		assertTrue(Direito.ESCRITA.matches("/faces/cep/cepuf/edita.xhtml"));
		assertTrue(Direito.ESCRITA.matches("/faces/cep/cepuf/edita.jsf"));
		assertFalse(Direito.EXCLUSAO.matches("/faces/cep/cepuf/lista.xhtml"));
	}

}
