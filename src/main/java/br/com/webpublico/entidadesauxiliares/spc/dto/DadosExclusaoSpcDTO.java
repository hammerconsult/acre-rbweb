package br.com.webpublico.entidadesauxiliares.spc.dto;

import br.com.webpublico.entidadesauxiliares.spc.enums.TipoPessoa;

import java.util.Date;

public class DadosExclusaoSpcDTO {

    private String numeroContrato;
    private TipoPessoa tipoPessoa;
    private String cpfCnpj;
    private Date dataVencimento;
    private Long idMotivoExclusao;

    public DadosExclusaoSpcDTO() {
    }

    public String getNumeroContrato() {
        return numeroContrato;
    }

    public void setNumeroContrato(String numeroContrato) {
        this.numeroContrato = numeroContrato;
    }

    public TipoPessoa getTipoPessoa() {
        return tipoPessoa;
    }

    public void setTipoPessoa(TipoPessoa tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public Date getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(Date dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public Long getIdMotivoExclusao() {
        return idMotivoExclusao;
    }

    public void setIdMotivoExclusao(Long idMotivoExclusao) {
        this.idMotivoExclusao = idMotivoExclusao;
    }
}
