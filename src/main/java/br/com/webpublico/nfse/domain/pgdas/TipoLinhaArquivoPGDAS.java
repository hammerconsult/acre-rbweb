package br.com.webpublico.nfse.domain.pgdas;


public enum TipoLinhaArquivoPGDAS {
    PGAAAAA("AAAAA"),
    PG00000("00000"),
    PG00001("00001"),
    PG01000("01000"),
    PG01500("01500"),
    PG01501("01501"),
    PG01502("01502"),
    PG02000("02000"),
    PG03000("03000"),
    PG03100("03100"),
    PG03110("03110"),
    PG03111("03111"),
    PG03112("03112"),
    PG03120("03120"),
    PG03121("03121"),
    PG03122("03122"),
    PG03130("03130"),
    PG03131("03131"),
    PG03132("03132"),
    PG03500("03500"),
    PG04000("04000"),
    PG99999("99999"),
    PGZZZZZ("ZZZZZ");

    private String codigo;

    TipoLinhaArquivoPGDAS(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
