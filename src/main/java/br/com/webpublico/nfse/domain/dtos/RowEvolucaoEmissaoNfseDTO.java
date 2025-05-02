package br.com.webpublico.nfse.domain.dtos;

public class RowEvolucaoEmissaoNfseDTO {

    private Integer ano;
    private Integer mes;
    private Long quantidadeUsuarios;
    private Long quantidadeNotas;

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Long getQuantidadeUsuarios() {
        return quantidadeUsuarios;
    }

    public void setQuantidadeUsuarios(Long quantidadeUsuarios) {
        this.quantidadeUsuarios = quantidadeUsuarios;
    }

    public Long getQuantidadeNotas() {
        return quantidadeNotas;
    }

    public void setQuantidadeNotas(Long quantidadeNotas) {
        this.quantidadeNotas = quantidadeNotas;
    }
}
