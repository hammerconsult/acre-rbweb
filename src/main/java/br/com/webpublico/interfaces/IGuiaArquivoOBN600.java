package br.com.webpublico.interfaces;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoDocPagto;
import br.com.webpublico.enums.TipoIdentificacaoGuia;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by RenatoRomanini on 18/05/2015.
 */
public interface IGuiaArquivoOBN600 {
    public String getNumero();

    public TipoDocPagto getTipoDocumento();

    public GuiaFatura getGuiaFatura();

    public GuiaConvenio getGuiaConvenio();

    public GuiaGPS getGuiaGPS();

    public GuiaDARF getGuiaDARF();

    public GuiaDARFSimples getGuiaDARFSimples();

    public GuiaGRU getGuiaGRU();

    public BigDecimal getValorTotalPorGuia();

    public Pessoa getPessoa();

    public TipoIdentificacaoGuia getTipoIdentificacaoGuia();

    public String getCodigoIdentificacao();

    public Date getDataPagamento();

}
