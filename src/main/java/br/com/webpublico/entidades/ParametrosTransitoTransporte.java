/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoCalculoRBTRans;
import br.com.webpublico.enums.TipoPermissaoRBTrans;
import br.com.webpublico.enums.TipoTermoRBTrans;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author cheles
 */
@Entity

@GrupoDiagrama(nome = "RBTrans")
@Audited
@Etiqueta("Parâmetros de Trânsito Referente ao Transporte")
@Table(name = "PARAMTRANSITOTRANSPORTE")
@Inheritance(strategy = InheritanceType.JOINED)
public class ParametrosTransitoTransporte extends ParametrosTransitoConfiguracao implements Serializable {

    @Etiqueta("Limite de Permissões")
    private Integer limitePermissoes;
    @Etiqueta("Limite de Idade")
    private Integer limiteIdade;
    @OneToOne
    @Etiqueta("Natureza Jurídica")
    private NaturezaJuridica naturezaJuridica;
    @OneToOne
    @Etiqueta("Tipo de Autônomo")
    private TipoAutonomo tipoAutonomo;
    @Enumerated(EnumType.STRING)
    private TipoPermissaoRBTrans tipoPermissao;
    @OneToMany(mappedBy = "parametro", cascade = CascadeType.ALL)
    private List<DigitoVencimento> vencimentos;
    private Boolean gerarOutorga;
    private Integer horaAntecedenciaViagem;

    public ParametrosTransitoTransporte() {
        this.setTaxasTransito(new ArrayList<TaxaTransito>());
        this.setParametrosTermos(new ArrayList<ParametrosTransitoTermos>());
        this.setParametrosValoresTransferencias(new ArrayList<ParametrosValoresTransferencia>());
        this.setVencimentos(new ArrayList<DigitoVencimento>());
        this.gerarOutorga = false;
    }

    public ParametrosTransitoTransporte(TipoPermissaoRBTrans tipoPermissao) {
        this.tipoPermissao = tipoPermissao;
        carregarListaTaxas(tipoPermissao);
        carregarVencimentosLicenciamento();
        carregarVencimentosCredencial();
        carregarListaTermos(tipoPermissao);
        this.setParametrosValoresTransferencias(new ArrayList<ParametrosValoresTransferencia>());
    }

    public void carregarVencimentosLicenciamento() {
        if (this.getVencimentos() == null) {
            this.setVencimentos(new ArrayList<DigitoVencimento>());
        }
        for (int i = 0; i < 10; i++) {
            DigitoVencimento obj = new DigitoVencimento();
            obj.setTipoDigitoVencimento(DigitoVencimento.TipoDigitoVencimento.LICENCIAMENTO);
            obj.setDigito(i);
            obj.setParametro(this);
            this.getVencimentos().add(obj);
        }
    }

    public void carregarVencimentosCredencial() {
        if (this.getVencimentos() == null) {
            this.setVencimentos(new ArrayList<DigitoVencimento>());
        }
        for (int i = 0; i < 10; i++) {
            DigitoVencimento obj = new DigitoVencimento();
            obj.setTipoDigitoVencimento(DigitoVencimento.TipoDigitoVencimento.CREDENCIAL);
            obj.setDigito(i);
            obj.setParametro(this);
            this.getVencimentos().add(obj);
        }
    }

    public void carregarListaTaxas(TipoPermissaoRBTrans tipoPermissao) {
        if (this.getTaxasTransito() == null) {
            this.setTaxasTransito(Lists.newArrayList());
        }
        for (TipoCalculoRBTRans tipo : TipoCalculoRBTRans.values()) {
            if (tipo.contains(tipoPermissao)) {
                adicionarTaxaTransito(tipo);
            }
        }
    }

    public void carregarListaTermos(TipoPermissaoRBTrans tipoPermissao) {
        if (getParametrosTermos() == null || getParametrosTermos().isEmpty()) {
            this.setParametrosTermos(Lists.newArrayList());
            for (TipoTermoRBTrans termo : TipoTermoRBTrans.values()) {
                if (termo.contains(tipoPermissao)) {
                    adicionarTermo(termo);
                }
            }
        } else {
            for (TipoTermoRBTrans termo : TipoTermoRBTrans.values()) {
                if (getParametrosTermos().stream().noneMatch(param -> param.getTipoTermoRBTrans().equals(termo)) && termo.contains(tipoPermissao)) {
                    adicionarTermo(termo);
                }
            }
        }
    }

    private void adicionarTaxaTransito(TipoCalculoRBTRans tipo) {
        TaxaTransito taxa = new TaxaTransito();
        taxa.setTipoCalculoRBTRans(tipo);
        taxa.setParametrosTransitoConfiguracao(this);
        taxa.setValor(BigDecimal.ZERO);
        if (!this.getTaxasTransito().contains(taxa)) {
            this.getTaxasTransito().add(taxa);
        }
    }


    private void adicionarTermo(TipoTermoRBTrans tipo) {
        ParametrosTransitoTermos termo = new ParametrosTransitoTermos();
        termo.setTipoTermoRBTrans(tipo);
        termo.setParametrosTransitoConfiguracao(this);
        this.getParametrosTermos().add(termo);
    }

    public ParametrosTransitoTermos recuperarParametroTermos(TipoTermoRBTrans tipoTermo) {
        for (ParametrosTransitoTermos param : getParametrosTermos()) {
            if (param.getTipoTermoRBTrans().equals(tipoTermo)) {
                return param;
            }
        }
        return null;
    }

    public DigitoVencimento recuperarVencimentoPorDigitoTipo(Integer digito, DigitoVencimento.TipoDigitoVencimento tipoDigitoVencimento) {
        for (DigitoVencimento obj : vencimentos) {
            if (obj.getDigito() == digito && tipoDigitoVencimento.equals(obj.getTipoDigitoVencimento())) {
                return obj;
            }
        }
        return null;
    }

    public Integer getLimiteIdade() {
        return limiteIdade;
    }

    public void setLimiteIdade(Integer limiteIdade) {
        this.limiteIdade = limiteIdade;
    }

    public Integer getLimitePermissoes() {
        return limitePermissoes;
    }

    public void setLimitePermissoes(Integer limitePermissoes) {
        this.limitePermissoes = limitePermissoes;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " " + this.getId();
    }

    public NaturezaJuridica getNaturezaJuridica() {
        return naturezaJuridica;
    }

    public void setNaturezaJuridica(NaturezaJuridica naturezaJuridica) {
        this.naturezaJuridica = naturezaJuridica;
    }

    public TipoAutonomo getTipoAutonomo() {
        return tipoAutonomo;
    }

    public void setTipoAutonomo(TipoAutonomo tipoAutonomo) {
        this.tipoAutonomo = tipoAutonomo;
    }

    public TipoPermissaoRBTrans getTipoPermissao() {
        return tipoPermissao;
    }

    public void setTipoPermissao(TipoPermissaoRBTrans tipoPermissao) {
        this.tipoPermissao = tipoPermissao;
    }

    public List<DigitoVencimento> getVencimentosLicenciamento() {
        List<DigitoVencimento> retorno = Lists.newArrayList();
        for (DigitoVencimento obj : vencimentos) {
            if (DigitoVencimento.TipoDigitoVencimento.LICENCIAMENTO.equals(obj.getTipoDigitoVencimento())) {
                retorno.add(obj);
            }
        }
        return retorno;
    }

    public List<DigitoVencimento> getVencimentosCredencial() {
        List<DigitoVencimento> retorno = Lists.newArrayList();
        for (DigitoVencimento obj : vencimentos) {
            if (DigitoVencimento.TipoDigitoVencimento.CREDENCIAL.equals(obj.getTipoDigitoVencimento())) {
                retorno.add(obj);
            }
        }
        return retorno;
    }

    public List<DigitoVencimento> getVencimentos() {
        return vencimentos;
    }

    public void setVencimentos(List<DigitoVencimento> vencimentos) {
        this.vencimentos = vencimentos;
    }

    public Boolean getGerarOutorga() {
        return gerarOutorga != null ? gerarOutorga : false;
    }

    public void setGerarOutorga(Boolean gerarOutorga) {
        this.gerarOutorga = gerarOutorga;
    }

    public Integer getHoraAntecedenciaViagem() {
        return horaAntecedenciaViagem;
    }

    public void setHoraAntecedenciaViagem(Integer horaAntecedenciaViagem) {
        this.horaAntecedenciaViagem = horaAntecedenciaViagem;
    }
}
