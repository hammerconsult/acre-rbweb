package br.com.webpublico.seguranca.controle.converter;

import static org.junit.Assert.assertEquals;

import javax.faces.convert.ConverterException;

import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.Test;

public class LocalTimeConverterTest {

	private LocalTimeConverter converter;

	@Before
	public void setup() {
		this.converter = new LocalTimeConverter();
	}

	@Test
	public void testGetAsObject() {
		LocalTime localTime = (LocalTime) converter.getAsObject(null, null, "07:00");
		assertEquals(new LocalTime(7, 0), localTime);
	}

	@Test(expected = ConverterException.class)
	public void testInvalidGetAsObject() {
		converter.getAsObject(null, null, "24:33");
	}

	@Test
	public void testGetAsString() {
		String string = converter.getAsString(null, null, new LocalTime(22, 33));
		assertEquals("22:33", string);
	}

}
