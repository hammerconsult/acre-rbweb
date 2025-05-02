/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util.obn600;

import br.com.webpublico.entidades.ArquivoRemBancoBordero;
import br.com.webpublico.entidades.ArquivoRemessaBanco;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.DataUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author reidocrime
 */
public class LayoutObn600 {

    private HeaderObn600 headerObn600;
    private List<AgrupaRegistros> listaRegistroAgrupa;
    private AgrupaRegistros agrupaRegistros;
    private TrailerObn600 trailerObn600;
    private ArquivoRemessaBanco arb;

    public LayoutObn600() {
        throw new ExcecaoNegocioGenerica("Construtor não suportado para a classe: LayoutObn600!");
    }

    public LayoutObn600(ArquivoRemessaBanco arquivoRemessaBanco) {
        this.arb = arquivoRemessaBanco;
        listaRegistroAgrupa = new ArrayList<>();
    }

    private String horaDaData(Date data) {
        SimpleDateFormat s = new SimpleDateFormat("HHmmss");
        return s.format(data);
    }

    private void percorreBordero() {
        Date data = new Date();
        headerObn600 = new HeaderObn600("0", DataUtil.getDataFormatada(data), horaDaData(data), "1", "10E001", "", arb.getNumero(), "1");
        for (ArquivoRemBancoBordero a : arb.getArquivoRemBancoBorderos()) {
            agrupaRegistros = new AgrupaRegistros(a.getBordero());
            listaRegistroAgrupa.add(agrupaRegistros);

        }
        trailerObn600 = new TrailerObn600("9", " ",arb.getValor().toString(),arb.getQntdDocumento().toString());
    }

    public HeaderObn600 getHeaderObn600() {
        return headerObn600;
    }

    public void setHeaderObn600(HeaderObn600 headerObn600) {
        this.headerObn600 = headerObn600;
    }

    public List<AgrupaRegistros> getListaRegistroAgrupa() {
        return listaRegistroAgrupa;
    }

    public void setListaRegistroAgrupa(List<AgrupaRegistros> listaRegistroAgrupa) {
        this.listaRegistroAgrupa = listaRegistroAgrupa;
    }

    public TrailerObn600 getTrailerObn600() {
        return trailerObn600;
    }

    public void setTrailerObn600(TrailerObn600 trailerObn600) {
        this.trailerObn600 = trailerObn600;
    }


}
