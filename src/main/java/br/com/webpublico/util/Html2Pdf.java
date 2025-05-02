
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util;

import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author renato
 */
public class Html2Pdf {

    public static void convert(String input, OutputStream out) throws Exception {
        convert(new ByteArrayInputStream(input.getBytes("iso-8859-1")), out);
    }

    private static void convert(InputStream input, OutputStream out) throws Exception {
        Tidy tidy = new Tidy();
        tidy.setQuiet(true);//Foi incluido para não aparecer nenhuma mensagem de LOG
        tidy.setInputEncoding("iso-8859-1");
        tidy.setOutputEncoding("iso-8859-1");
        tidy.setWord2000(true);
        tidy.setDropEmptyParas(true);
        Document doc = tidy.parseDOM(input, null);
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocument(doc, null);
        renderer.layout();
        renderer.createPDF(out);


    }
}
