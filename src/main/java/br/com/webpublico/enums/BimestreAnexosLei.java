package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.contabil.BimestreAnexosLeiDTO;

public enum BimestreAnexosLei {
    PRIMEIRO_BIMESTRE("Jan - Fev", Mes.JANEIRO, Mes.FEVEREIRO, BimestreAnexosLeiDTO.PRIMEIRO_BIMESTRE),
    SEGUNDO_BIMESTRE("Mar - Abr", Mes.MARCO, Mes.ABRIL, BimestreAnexosLeiDTO.SEGUNDO_BIMESTRE),
    TERCEIRO_BIMESTRE("Mai - Jun", Mes.MAIO, Mes.JUNHO, BimestreAnexosLeiDTO.TERCEIRO_BIMESTRE),
    QUARTO_BIMESTRE("Jul - Ago", Mes.JULHO, Mes.AGOSTO, BimestreAnexosLeiDTO.QUARTO_BIMESTRE),
    QUINTO_BIMESTRE("Set - Out", Mes.SETEMBRO, Mes.OUTUBRO, BimestreAnexosLeiDTO.QUINTO_BIMESTRE),
    SEXTO_BIMESTRE("Nov - Dez", Mes.NOVEMBRO, Mes.DEZEMBRO, BimestreAnexosLeiDTO.SEXTO_BIMESTRE);

    private String descricao;
    private Mes mesFinal;
    private Mes mesInicial;
    private BimestreAnexosLeiDTO toDto;

    BimestreAnexosLei(String descricao, Mes mesInicial, Mes mesFinal, BimestreAnexosLeiDTO toDto) {
        this.descricao = descricao;
        this.mesFinal = mesFinal;
        this.mesInicial = mesInicial;
        this.toDto = toDto;
    }

    public String getDescricao() {
        return descricao;
    }

    public Mes getMesFinal() {
        return mesFinal;
    }

    public Mes getMesInicial() {
        return mesInicial;
    }

    public BimestreAnexosLeiDTO getToDto() {
        return toDto;
    }

    public boolean isUltimoBimestre() {
        return SEXTO_BIMESTRE.equals(this);
    }

    @Override
    public String toString() {
        return descricao;
    }
}
