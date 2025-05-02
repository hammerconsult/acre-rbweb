package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.nfse.domain.dtos.AnexoLei1232006FaixaNfseDTO;
import br.com.webpublico.nfse.domain.dtos.AnexoLei1232006NfseDTO;
import br.com.webpublico.nfse.domain.dtos.NfseEntity;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@Etiqueta("Anexo Lei Complementar 123/2006")
public class AnexoLei1232006 extends SuperEntidade implements NfseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Vigente Até")
    @Temporal(TemporalType.DATE)
    private Date vigenteAte;

    @Obrigatorio
    @Etiqueta("IRPJ (%) Exc.")
    private BigDecimal irpjExcedente;

    @Obrigatorio
    @Etiqueta("CSLL (%) Exc.")
    private BigDecimal csllExcedente;

    @Obrigatorio
    @Etiqueta("Cofins (%) Exc.")
    private BigDecimal cofinsExcedente;

    @Obrigatorio
    @Etiqueta("PIS/Pasep (%) Exc.")
    private BigDecimal pisPasepExcedente;

    @Obrigatorio
    @Etiqueta("CPP (%) Exc.")
    private BigDecimal cppExcedente;

    @Obrigatorio
    @Etiqueta("ICMS (%) Exc.")
    private BigDecimal icmsExcedente;

    @Obrigatorio
    @Etiqueta("IPI (%) Exc.")
    private BigDecimal ipiExcedente;

    @OneToMany(mappedBy = "anexoLei1232006", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnexoLei1232006Faixa> faixas;

    private Boolean exibirAlteracaoAnexo;

    public AnexoLei1232006() {
        super();
        faixas = Lists.newArrayList();
        exibirAlteracaoAnexo = Boolean.TRUE;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Date getVigenteAte() {
        return vigenteAte;
    }

    public void setVigenteAte(Date vigenteAte) {
        this.vigenteAte = vigenteAte;
    }

    public List<AnexoLei1232006Faixa> getFaixas() {
        return faixas;
    }

    public void setFaixas(List<AnexoLei1232006Faixa> faixas) {
        this.faixas = faixas;
    }

    public boolean hasFaixa(AnexoLei1232006Faixa faixa) {
        if (faixa == null) {
            for (AnexoLei1232006Faixa faixaAnexo : faixas) {
                if (!faixaAnexo.getId().equals(faixa.getId()) &&
                    faixaAnexo.getValorMaximo().compareTo(faixa.getValorMaximo()) == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public BigDecimal getIrpjExcedente() {
        return irpjExcedente;
    }

    public void setIrpjExcedente(BigDecimal irpjExcedente) {
        this.irpjExcedente = irpjExcedente;
    }

    public BigDecimal getCsllExcedente() {
        return csllExcedente;
    }

    public void setCsllExcedente(BigDecimal csllExcedente) {
        this.csllExcedente = csllExcedente;
    }

    public BigDecimal getCofinsExcedente() {
        return cofinsExcedente;
    }

    public void setCofinsExcedente(BigDecimal cofinsExcedente) {
        this.cofinsExcedente = cofinsExcedente;
    }

    public BigDecimal getPisPasepExcedente() {
        return pisPasepExcedente;
    }

    public void setPisPasepExcedente(BigDecimal pisPasepExcedente) {
        this.pisPasepExcedente = pisPasepExcedente;
    }

    public BigDecimal getCppExcedente() {
        return cppExcedente;
    }

    public void setCppExcedente(BigDecimal cppExcedente) {
        this.cppExcedente = cppExcedente;
    }

    public BigDecimal getIcmsExcedente() {
        return icmsExcedente;
    }

    public void setIcmsExcedente(BigDecimal icmsExcedente) {
        this.icmsExcedente = icmsExcedente;
    }

    public BigDecimal getIpiExcedente() {
        return ipiExcedente;
    }

    public void setIpiExcedente(BigDecimal ipiExcedente) {
        this.ipiExcedente = ipiExcedente;
    }

    public Boolean getExibirAlteracaoAnexo() {
        return exibirAlteracaoAnexo;
    }

    public void setExibirAlteracaoAnexo(Boolean exibirAlteracaoAnexo) {
        this.exibirAlteracaoAnexo = exibirAlteracaoAnexo;
    }

    @Override
    public String toString() {
        return descricao;
    }

    @Override
    public AnexoLei1232006NfseDTO toNfseDto() {
        AnexoLei1232006NfseDTO anexoLei1232006NfseDTO = new AnexoLei1232006NfseDTO(id, descricao, vigenteAte, irpjExcedente,
            csllExcedente, cofinsExcedente, pisPasepExcedente, cppExcedente, icmsExcedente, ipiExcedente);
        anexoLei1232006NfseDTO.setFaixas(new ArrayList());
        for (AnexoLei1232006Faixa faixa : faixas) {
            anexoLei1232006NfseDTO.getFaixas().add((AnexoLei1232006FaixaNfseDTO) faixa.toNfseDto());
        }
        return anexoLei1232006NfseDTO;
    }

    public AnexoLei1232006Faixa getFaixaPorRBT12(BigDecimal rbt12) {
        for (AnexoLei1232006Faixa faixa : getFaixas()) {
            if (rbt12.compareTo(faixa.getValorMaximo()) <= 0) {
                return faixa;
            }
        }
        return null;
    }
}
