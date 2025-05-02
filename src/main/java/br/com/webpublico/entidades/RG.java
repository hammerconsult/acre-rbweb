/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.rh.portal.atualizacaocadastral.RGPortal;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.pessoa.dto.RGDTO;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import java.util.Date;

/**
 * @author Munif
 */
@GrupoDiagrama(nome = "CadastroUnico")
@Entity
@Audited

public class RG extends DocumentoPessoal {

    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data de Emissão")
    private Date dataemissao;
    @Column
    @Etiqueta("Número")
    @Obrigatorio
    @Pesquisavel
    private String numero;
    @Column
    @Etiqueta("Órgão Emissor")
    @Obrigatorio
    private String orgaoEmissao;
    @ManyToOne
    @Etiqueta("U.F.")
    private UF uf;

    public RG() {
    }

    public RG(PessoaFisica pessoaFisica, String numero, String orgaoEmissao, UF uf, Date emissao) {
        this.setPessoaFisica(pessoaFisica);
        this.numero = numero;
        this.orgaoEmissao = orgaoEmissao;
        this.uf = uf;
        this.dataemissao = emissao;
    }

    public Date getDataemissao() {
        return dataemissao;
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
        return orgaoEmissao != null ? orgaoEmissao.toUpperCase() : null;
    }

    public void setOrgaoEmissao(String orgaoEmissao) {
        this.orgaoEmissao = orgaoEmissao != null ? orgaoEmissao.toUpperCase() : orgaoEmissao;
    }

    public UF getUf() {
        return uf;
    }

    public void setUf(UF uf) {
        this.uf = uf;
    }

    @Override
    public String toString() {
        return numero != null ? numero : "";
    }

    public String getToStringNumeroOrgaoEmissor() {
        StringBuilder sb;
        if (numero != null) {
            sb = new StringBuilder(getNumero());
        } else {
            sb = new StringBuilder("");
        }

        if (orgaoEmissao != null
            && !orgaoEmissao.isEmpty()) {
            sb.append(" - ").append(orgaoEmissao);
        }
        return sb.toString();
    }

    public static RGDTO toRGDTO(RG rg) {
        if (rg == null) {
            return null;
        }
        RGDTO dto = new RGDTO();
        dto.setId(rg.getId());
        dto.setDataEmissaoRG(rg.getDataemissao());
        dto.setNumeroRG(rg.getNumero());
        dto.setOrgaoEmissaoRG(rg.getOrgaoEmissao());
        dto.setUfRG(UF.toUfDTO(rg.getUf()));
        return dto;
    }

    public static RG rgPortalToRG(PessoaFisica pf, RGPortal rg) {
        if (rg == null) {
            return null;
        }
        RG dto = new RG();
        dto.setDataemissao(rg.getDataemissao());
        dto.setNumero(rg.getNumero());
        dto.setPessoaFisica(pf);
        dto.setOrgaoEmissao(rg.getOrgaoEmissao());
        dto.setUf(rg.getUf() != null && rg.getUf().getId() != null ? UF.createUF(rg.getUf().getId()) : null);
        return dto;
    }

    public static RGDTO toRGPortalDTO(RGPortal rg) {
        if (rg == null) {
            return null;
        }
        RGDTO dto = new RGDTO();
        dto.setId(rg.getId());
        dto.setDataEmissaoRG(rg.getDataemissao());
        dto.setNumeroRG(rg.getNumero());
        dto.setOrgaoEmissaoRG(rg.getOrgaoEmissao());
        dto.setUfRG(UF.toUfDTO(rg.getUf()));
        return dto;
    }
}
