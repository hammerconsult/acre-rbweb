/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.rh.portal.atualizacaocadastral.CarteiraTrabalhoPortal;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.pessoa.dto.CarteiraTrabalhoDTO;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * @author Munif
 */
@GrupoDiagrama(nome = "CadastroUnico")
@Entity

@Audited
public class CarteiraTrabalho extends DocumentoPessoal {

    @Tabelavel
    @Column(length = 20)
    @Size(max = 20)
    private String numero;
    @Tabelavel
    @Column(length = 20)
    @Size(max = 20)
    private String serie;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataEmissao;
    @ManyToOne
    private UF uf;
    @Tabelavel
    private String pisPasep;
    @Tabelavel
    private String orgaoExpedidor;
    @ManyToOne
    private Banco bancoPisPasep;
    @Temporal(TemporalType.DATE)
    private Date dataEmissaoPisPasep;
    private Integer anoPrimeiroEmprego;
    @Etiqueta("CTPS Digital")
    private Boolean ctpsDigital;

    public CarteiraTrabalho() {
    }

    public CarteiraTrabalho(PessoaFisica pessoaFisica, String pisPasep, UF uf) {
        this.setPessoaFisica(pessoaFisica);
        this.uf = uf;
        this.pisPasep = pisPasep;
    }

    public Integer getAnoPrimeiroEmprego() {
        return anoPrimeiroEmprego;
    }

    public void setAnoPrimeiroEmprego(Integer anoPrimeiroEmprego) {
        this.anoPrimeiroEmprego = anoPrimeiroEmprego;
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

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getPisPasep() {
        return pisPasep;
    }

    public void setPisPasep(String pisPasep) {
        this.pisPasep = pisPasep;
    }

    public String getSerie() {
        return serie != null ? serie.toUpperCase() : null;
    }

    public void setSerie(String serie) {
        this.serie = serie != null ? serie.toUpperCase() : serie;
    }

    public UF getUf() {
        return uf;
    }

    public void setUf(UF uf) {
        this.uf = uf;
    }

    public String getOrgaoExpedidor() {
        return orgaoExpedidor;
    }

    public void setOrgaoExpedidor(String orgaoExpedidor) {
        this.orgaoExpedidor = orgaoExpedidor;
    }

    @Override
    public String toString() {
        return numero;
    }

    public static CarteiraTrabalhoDTO toCarteiraTrabalhoDTO(CarteiraTrabalho carteira) {
        if (carteira == null) {
            return null;
        }
        CarteiraTrabalhoDTO dto = new CarteiraTrabalhoDTO();
        dto.setId(carteira.getId());
        dto.setAnoPrimeiroEmprego(carteira.getAnoPrimeiroEmprego());
        dto.setBancoPisPasep(Banco.toBancoDTO(carteira.getBancoPisPasep()));
        dto.setDataEmissaoCarteiraTrabalho(carteira.getDataEmissao());
        dto.setDataEmissaoPisPasep(carteira.getDataEmissaoPisPasep());
        dto.setNumeroCarteiraTrabalho(carteira.getNumero());
        dto.setOrgaoExpedidor(carteira.getOrgaoExpedidor());
        dto.setPisPasep(carteira.getPisPasep());
        dto.setSerie(carteira.getSerie());
        dto.setUfCarteiraTrabalho(UF.toUfDTO(carteira.getUf()));
        return dto;
    }

    public static CarteiraTrabalho toCarteiraTrabalho(PessoaFisica pessoaFisica, CarteiraTrabalhoPortal carteira) {
        if (carteira == null) {
            return null;
        }
        CarteiraTrabalho dto = new CarteiraTrabalho();
        dto.setPessoaFisica(pessoaFisica);
        dto.setAnoPrimeiroEmprego(carteira.getAnoPrimeiroEmprego());
        dto.setBancoPisPasep(carteira.getBancoPisPasep());
        dto.setDataEmissao(carteira.getDataEmissao());
        dto.setDataEmissaoPisPasep(carteira.getDataEmissaoPisPasep());
        dto.setNumero(carteira.getNumero());
        dto.setOrgaoExpedidor(carteira.getOrgaoExpedidor());
        dto.setPisPasep(carteira.getPisPasep());
        dto.setSerie(carteira.getSerie());
        dto.setUf(carteira.getUf());
        return dto;
    }

    public static CarteiraTrabalhoDTO toCarteiraTrabalhoPortalDTO(CarteiraTrabalhoPortal carteira) {
        if (carteira == null) {
            return null;
        }
        CarteiraTrabalhoDTO dto = new CarteiraTrabalhoDTO();
        dto.setId(carteira.getId());
        dto.setAnoPrimeiroEmprego(carteira.getAnoPrimeiroEmprego());
        dto.setBancoPisPasep(Banco.toBancoDTO(carteira.getBancoPisPasep()));
        dto.setDataEmissaoCarteiraTrabalho(carteira.getDataEmissao());
        dto.setDataEmissaoPisPasep(carteira.getDataEmissaoPisPasep());
        dto.setNumeroCarteiraTrabalho(carteira.getNumero());
        dto.setOrgaoExpedidor(carteira.getOrgaoExpedidor());
        dto.setPisPasep(carteira.getPisPasep());
        dto.setSerie(carteira.getSerie());
        dto.setUfCarteiraTrabalho(UF.toUfDTO(carteira.getUf()));
        return dto;
    }

    public Boolean getCtpsDigital() {
        return ctpsDigital == null ? Boolean.FALSE : ctpsDigital;
    }

    public void setCtpsDigital(Boolean ctpsDigital) {
        this.ctpsDigital = ctpsDigital;
    }
}
