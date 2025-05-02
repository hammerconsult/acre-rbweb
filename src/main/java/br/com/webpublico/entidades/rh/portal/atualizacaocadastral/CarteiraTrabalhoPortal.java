package br.com.webpublico.entidades.rh.portal.atualizacaocadastral;

import br.com.webpublico.entidades.Banco;
import br.com.webpublico.entidades.UF;
import br.com.webpublico.pessoa.dto.CarteiraTrabalhoDTO;
import br.com.webpublico.util.DataUtil;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Created by peixe on 29/08/17.
 */
@Entity
public class CarteiraTrabalhoPortal implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String numero;
    private String serie;
    private Date dataEmissao;
    @ManyToOne
    private UF uf;
    private String pisPasep;
    private String orgaoExpedidor;
    @ManyToOne
    private Banco bancoPisPasep;
    private Date dataEmissaoPisPasep;
    private Integer anoPrimeiroEmprego;

    public CarteiraTrabalhoPortal() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public UF getUf() {
        return uf;
    }

    public void setUf(UF uf) {
        this.uf = uf;
    }

    public String getPisPasep() {
        return pisPasep;
    }

    public void setPisPasep(String pisPasep) {
        this.pisPasep = pisPasep;
    }

    public String getOrgaoExpedidor() {
        return orgaoExpedidor;
    }

    public void setOrgaoExpedidor(String orgaoExpedidor) {
        this.orgaoExpedidor = orgaoExpedidor;
    }

    public Banco getBancoPisPasep() {
        return bancoPisPasep;
    }

    public void setBancoPisPasep(Banco bancoPisPasep) {
        this.bancoPisPasep = bancoPisPasep;
    }

    public Date getDataEmissaoPisPasep() {
        return dataEmissaoPisPasep;
    }

    public void setDataEmissaoPisPasep(Date dataEmissaoPisPasep) {
        this.dataEmissaoPisPasep = dataEmissaoPisPasep;
    }

    public Integer getAnoPrimeiroEmprego() {
        return anoPrimeiroEmprego;
    }

    public void setAnoPrimeiroEmprego(Integer anoPrimeiroEmprego) {
        this.anoPrimeiroEmprego = anoPrimeiroEmprego;
    }

    public static CarteiraTrabalhoPortal dtoToCarteiraTrabalhoPortal(CarteiraTrabalhoDTO dto) {
        CarteiraTrabalhoPortal c = new CarteiraTrabalhoPortal();
        c.setAnoPrimeiroEmprego(dto.getAnoPrimeiroEmprego());
        c.setBancoPisPasep(dto.getBancoPisPasep() != null ? Banco.dtoToBanco(dto.getBancoPisPasep()): null);
        c.setDataEmissao(dto.getDataEmissaoCarteiraTrabalho());
        c.setDataEmissaoPisPasep(dto.getDataEmissaoPisPasep());
        c.setNumero(dto.getNumeroCarteiraTrabalho());
        c.setOrgaoExpedidor(dto.getOrgaoExpedidor());
        c.setPisPasep(dto.getPisPasep());
        c.setSerie(dto.getSerie());
        c.setUf(UF.dtoToUF(dto.getUfCarteiraTrabalho()));
        return c;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarteiraTrabalhoPortal that = (CarteiraTrabalhoPortal) o;
        return Objects.equals(numero != null ? numero.trim() : null, that.numero != null ? that.numero.trim() : null)
            && Objects.equals(serie != null ? serie.trim() : null, that.serie != null ? that.serie.trim() : null)
            && Objects.equals(DataUtil.getDataFormatada(dataEmissao), DataUtil.getDataFormatada(that.dataEmissao))
            && Objects.equals(uf, that.uf)
            && Objects.equals(pisPasep != null ? pisPasep.trim() : null, that.pisPasep != null ? that.pisPasep.trim() : null)
            && Objects.equals(bancoPisPasep, that.bancoPisPasep)
            && Objects.equals(DataUtil.getDataFormatada(dataEmissaoPisPasep), DataUtil.getDataFormatada(that.dataEmissaoPisPasep))
            && Objects.equals(anoPrimeiroEmprego, that.anoPrimeiroEmprego);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numero, serie, dataEmissao, uf, pisPasep, bancoPisPasep, dataEmissaoPisPasep, anoPrimeiroEmprego);
    }
}
