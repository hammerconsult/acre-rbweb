package br.com.webpublico.entidadesauxiliares;

public class VOTributoBi implements Comparable<VOTributoBi> {

    private Long codigo;
    private String descricao;

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public int compareTo(VOTributoBi o) {
        return this.codigo.compareTo(o.getCodigo());
    }
}
