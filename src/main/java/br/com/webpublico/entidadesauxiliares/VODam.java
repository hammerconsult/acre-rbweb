package br.com.webpublico.entidadesauxiliares;

import java.util.Date;

/**
 * Created by Fabio on 22/04/2019.
 */
public class VODam implements Comparable<VODam> {

    private Long idDam;
    private Long idParcela;
    private String codigoBarras;
    private Date vencimento;
    private String codigoBarrasCotaUnica;

    public Long getIdDam() {
        return idDam;
    }

    public void setIdDam(Long idDam) {
        this.idDam = idDam;
    }

    public Long getIdParcela() {
        return idParcela;
    }

    public void setIdParcela(Long idParcela) {
        this.idParcela = idParcela;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public String getCodigoBarrasCotaUnica() {
        return codigoBarrasCotaUnica;
    }

    public void setCodigoBarrasCotaUnica(String codigoBarrasCotaUnica) {
        this.codigoBarrasCotaUnica = codigoBarrasCotaUnica;
    }

    @Override
    public int compareTo(VODam o) {
        return this.getIdDam().compareTo(o.getIdDam());
    }
}
