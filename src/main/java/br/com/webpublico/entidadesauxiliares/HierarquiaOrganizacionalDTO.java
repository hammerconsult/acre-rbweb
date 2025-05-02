package br.com.webpublico.entidadesauxiliares;

import java.util.Objects;

public class HierarquiaOrganizacionalDTO {
    private Long id;
    private Long subordinadaId;
    private String codigo;
    private String descricao;

    public HierarquiaOrganizacionalDTO() {
    }

    public HierarquiaOrganizacionalDTO(Long id, Long subordinadaId, String codigo, String descricao) {
        this.id = id;
        this.subordinadaId = subordinadaId;
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSubordinadaId() {
        return subordinadaId;
    }

    public void setSubordinadaId(Long subordinadaId) {
        this.subordinadaId = subordinadaId;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HierarquiaOrganizacionalDTO that = (HierarquiaOrganizacionalDTO) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(subordinadaId, that.subordinadaId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, subordinadaId);
    }

    public String getCodigoSemZerosFinais() {
        boolean tudoZero = true;
        int i = codigo.length();
        int ultimoPonto = codigo.length();
        while (i > 0 && tudoZero) {
            char c = codigo.charAt(i - 1);
            if (c == '.') {
                ultimoPonto = i;
            }
            if (c != '.' && c != '0') {
                tudoZero = false;
            }
            i--;
        }
        return codigo.substring(0, ultimoPonto);
    }

    @Override
    public String toString() {
        if (codigo != null && descricao != null ) {
            return codigo + " - " + descricao;
        }
        return "";
    }
}
