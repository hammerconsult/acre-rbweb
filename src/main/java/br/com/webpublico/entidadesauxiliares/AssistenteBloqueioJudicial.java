package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.AssistenteBarraProgresso;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class AssistenteBloqueioJudicial extends AssistenteBarraProgresso implements Serializable {
    private Integer totalParcelasResiduais;
    private Date vencimentoDam;
    private String mensagem;
    private List<ResultadoParcela> parcelasOriginais;
    private List<ResultadoParcela> parcelasOriginadas;

    public AssistenteBloqueioJudicial() {
        super();
        this.parcelasOriginais = Lists.newArrayList();
        this.parcelasOriginadas = Lists.newArrayList();
    }

    public Integer getTotalParcelasResiduais() {
        return totalParcelasResiduais;
    }

    public void setTotalParcelasResiduais(Integer totalParcelasResiduais) {
        this.totalParcelasResiduais = totalParcelasResiduais;
    }

    public Date getVencimentoDam() {
        return vencimentoDam;
    }

    public void setVencimentoDam(Date vencimentoDam) {
        this.vencimentoDam = vencimentoDam;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public List<ResultadoParcela> getParcelasOriginais() {
        return parcelasOriginais;
    }

    public void setParcelasOriginais(List<ResultadoParcela> parcelasOriginais) {
        this.parcelasOriginais = parcelasOriginais;
    }

    public List<ResultadoParcela> getParcelasOriginadas() {
        return parcelasOriginadas;
    }

    public void setParcelasOriginadas(List<ResultadoParcela> parcelasOriginadas) {
        this.parcelasOriginadas = parcelasOriginadas;
    }
}
