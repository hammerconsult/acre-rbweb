package br.com.webpublico.util;

import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class HtmlToText extends HTMLEditorKit.ParserCallback {

    private StringBuilder s;

    public HtmlToText() {
    }

    public String parse(String texto) throws Exception {
        byte[] dados = texto.getBytes();
        ByteArrayInputStream bais = new ByteArrayInputStream(dados);
        Reader reader = new InputStreamReader(bais);
        return parse(reader);
    }

    public String parse(Reader in) throws IOException {
        s = new StringBuilder();
        ParserDelegator delegator = new ParserDelegator();
        delegator.parse(in, this, Boolean.TRUE);
        return getText();
    }

    @Override
    public void handleText(char[] text, int pos) {
        s.append(text);
        s.append(" ");
    }

    public String getText() {
        return s.toString();
    }
}
