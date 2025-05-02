package br.com.webpublico.enums;

import br.com.webpublico.entidades.EventoBem;
import br.com.webpublico.webreportdto.dto.administrativo.TipoReducaoValorBemDTO;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 03/10/14
 * Time: 10:39
 * To change this template use File | Settings | File Templates.
 */
public enum TipoReducaoValorBem {

    AMORTIZACAO("Amortização", "Amortizar", "Amortizado", "Amortizáveis", TipoEventoBem.AMORTIZACAO, TipoReducaoValorBemDTO.AMORTIZACAO),
    DEPRECIACAO("Depreciação", "Depreciar", "Depreciado", "Depreciáveis", TipoEventoBem.DEPRECIACAO, TipoReducaoValorBemDTO.DEPRECIACAO),
    EXAUSTAO("Exaustão", "Exaurir", "Exaurido", "Exauríveis", TipoEventoBem.EXAUSTAO, TipoReducaoValorBemDTO.EXAUSTAO),
    NAO_APLICAVEL("Não aplicável", "", "", "", null, TipoReducaoValorBemDTO.NAO_APLICAVEL);

    private String descricao;
    private String segundaDescricao;
    private String terceiraDescricao;
    private String quartaDescricao;
    private TipoEventoBem tipoEventoBem;
    private TipoReducaoValorBemDTO toDto;

    TipoReducaoValorBem(String descricao, String segundaDescricao, String terceiraDescricao, String quartaDescricao, TipoEventoBem tipoEventoBem, TipoReducaoValorBemDTO toDto) {
        this.descricao = descricao;
        this.segundaDescricao = segundaDescricao;
        this.terceiraDescricao = terceiraDescricao;
        this.quartaDescricao = quartaDescricao;
        this.tipoEventoBem = tipoEventoBem;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getSegundaDescricao() {
        return segundaDescricao;
    }

    public String getTerceiraDescricao() {
        return terceiraDescricao;
    }

    public String getQuartaDescricao() {
        return quartaDescricao;
    }

    public TipoEventoBem getTipoEventoBem() {
        return tipoEventoBem;
    }

    public TipoReducaoValorBemDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
