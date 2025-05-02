package br.com.webpublico.entidades.rh.portal.atualizacaocadastral;

import br.com.webpublico.entidades.UF;
import br.com.webpublico.pessoa.dto.RGDTO;
import br.com.webpublico.util.DataUtil;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Created by peixe on 29/08/17.
 */
@Entity
public class RGPortal implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Date dataemissao;
    private String numero;
    private String orgaoEmissao;
    @ManyToOne
    private UF uf;

    public RGPortal() {
    }

    public RGPortal(Long id, Date dataemissao, String numero, String orgaoEmissao, UF uf) {
        this.id = id;
        this.dataemissao = dataemissao;
        this.numero = numero;
        this.orgaoEmissao = orgaoEmissao;
        this.uf = uf;
    }

    public UF getUf() {
        return uf;
    }

    public void setUf(UF uf) {
        this.uf = uf;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataemissao() {
        return dataemissao != null ? DataUtil.dataSemHorario(dataemissao) : null;
    }

    public void setDataemissao(Date dataemissao) {
        this.dataemissao = dataemissao;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getOrgaoEmissao() {
        return orgaoEmissao;
    }

    public void setOrgaoEmissao(String orgaoEmissao) {
        this.orgaoEmissao = orgaoEmissao;
    }

    public static RGPortal dtoToRgPortal(RGDTO dto) {
        RGPortal rg = new RGPortal();
        rg.setDataemissao(dto.getDataEmissaoRG());
        rg.setNumero(dto.getNumeroRG());
        rg.setOrgaoEmissao(dto.getOrgaoEmissaoRG());
        rg.setUf(UF.dtoToUF(dto.getUfRG()));
        return rg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RGPortal rgPortal = (RGPortal) o;
        return Objects.equals(DataUtil.getDataFormatada(dataemissao), DataUtil.getDataFormatada(rgPortal.dataemissao))
            && Objects.equals(numero != null ? numero.trim() : null, rgPortal.numero != null ? rgPortal.numero : null)
            && Objects.equals(orgaoEmissao != null ? orgaoEmissao.toUpperCase().trim() : null, rgPortal.orgaoEmissao != null ? rgPortal.orgaoEmissao.toUpperCase().trim() : null)
            && Objects.equals(uf, rgPortal.uf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataemissao, numero, orgaoEmissao, uf);
    }
}
