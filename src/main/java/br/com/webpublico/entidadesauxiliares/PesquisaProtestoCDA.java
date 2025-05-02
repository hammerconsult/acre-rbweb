package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.CertidaoDividaAtiva;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class PesquisaProtestoCDA implements Serializable {

    private CertidaoDividaAtiva certidaoDividaAtiva;
    private Date dataPrescricao;
    private List<ResultadoParcela> parcelas;

    public PesquisaProtestoCDA() {
    }

    public CertidaoDividaAtiva getCertidaoDividaAtiva() {
        return certidaoDividaAtiva;
    }

    public void setCertidaoDividaAtiva(CertidaoDividaAtiva certidaoDividaAtiva) {
        this.certidaoDividaAtiva = certidaoDividaAtiva;
    }

    public Date getDataPrescricao() {
        return dataPrescricao;
    }

    public void setDataPrescricao(Date dataPrescricao) {
        this.dataPrescricao = dataPrescricao;
    }

    public List<ResultadoParcela> getParcelas() {
        return parcelas;
    }

    public void setParcelas(List<ResultadoParcela> parcelas) {
        this.parcelas = parcelas;
    }
}
