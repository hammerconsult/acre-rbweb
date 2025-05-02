package br.com.webpublico.enums;

public enum LeiLicitacao {

    LEI_8666("Leis nº 8.666/1993, nº 10.520/2002 e nº 12.462/2011"),
    LEI_14133("Lei nº 14.133/21"),
    LEI_14284("Lei nº 14.284/21"),
    LEI_9636("Lei nº 9.636/98");

    private String descricao;

    LeiLicitacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public boolean isLei8666(){
        return LEI_8666.equals(this);
    }

    public boolean isLei14133(){
        return LEI_14133.equals(this);
    }
}
