/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.base.Strings;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author claudio
 */
@GrupoDiagrama(nome = "CadastroRural")
@Entity
@Audited
@Etiqueta("Imóvel Rural")
public class CadastroRural extends Cadastro implements Serializable {

    private static final long serialVersionUID = 1L;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "imovel", orphanRemoval = true)
    private List<PropriedadeRural> propriedade;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Código")
    private Long codigo;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Nome da Propriedade")
    private String nomePropriedade;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Localização")
    private String localizacaoLote;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Área")
    private String areaLote;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "CR_ValorAtributos")
    @Etiqueta("Atributos")
    private Map<Atributo, ValorAtributo> atributos;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Tipo de Área Rural")
    private TipoAreaRural tipoAreaRural;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("INCRA")
    private String numeroIncra;
    @Tabelavel
    @Monetario
    @Etiqueta("Valor Negociado")
    private BigDecimal valorVenalLote;
    @Transient
    private Long criadoEm;
//    @Invisivel
//    @Transient
//    private List<PropriedadeRural> propriedadesAtuais;
    @Invisivel
    @Transient
    private List<PropriedadeRural> propriedadesDoHistorico;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Endereço Fiscal")
    private String enderecoFiscal;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Via de Acesso")
    private String viaAcesso;
    @Tabelavel
    @Etiqueta("Distância da Sede do Município (Km)")
    private BigDecimal distanciaSede;
    @Pesquisavel(labelBooleanVerdadeiro = "Ativo", labelBooleanFalso = "Inativo")
    @Tabelavel
    @Etiqueta("Ativo")
    private Boolean ativo;

    public CadastroRural() {
        criadoEm = System.nanoTime();
        propriedade = new ArrayList<PropriedadeRural>();
        this.ativo = true;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Map<Atributo, ValorAtributo> getAtributos() {
        return atributos;
    }

    public void setAtributos(Map<Atributo, ValorAtributo> atributos) {
        this.atributos = atributos;
    }

    public String getAreaLote() {
        return areaLote;
    }

    public void setAreaLote(String areaLote) {
        this.areaLote = areaLote;
    }

    public String getLocalizacaoLote() {
        return localizacaoLote != null ? localizacaoLote.trim() : null;
    }

    public void setLocalizacaoLote(String localizacaoLote) {
        this.localizacaoLote = localizacaoLote;
    }

    public List<PropriedadeRural> getPropriedade() {
        return propriedade;
    }

    public void setPropriedade(List<PropriedadeRural> propriedade) {
        this.propriedade = propriedade;
    }

    public String getNomePropriedade() {
        return nomePropriedade != null ? nomePropriedade.trim() : null;
    }

    public void setNomePropriedade(String nomePropriedade) {
        this.nomePropriedade = nomePropriedade;
    }

    public String getNumeroIncra() {
        return numeroIncra;
    }

    public void setNumeroIncra(String numeroIncra) {
        this.numeroIncra = numeroIncra;
    }

    public TipoAreaRural getTipoAreaRural() {
        return tipoAreaRural;
    }

    public void setTipoAreaRural(TipoAreaRural tipoArea) {
        this.tipoAreaRural = tipoArea;
    }

    public BigDecimal getValorVenalLote() {
        return valorVenalLote;
    }

    public void setValorVenalLote(BigDecimal valorVenalLote) {
        this.valorVenalLote = valorVenalLote;
    }

    public String getEnderecoFiscal() {
        return enderecoFiscal;
    }

    public void setEnderecoFiscal(String enderecoFiscal) {
        this.enderecoFiscal = enderecoFiscal;
    }

    public String getViaAcesso() {
        return viaAcesso;
    }

    public void setViaAcesso(String viaAcesso) {
        this.viaAcesso = viaAcesso;
    }

    public BigDecimal getDistanciaSede() {
        return distanciaSede;
    }

    public void setDistanciaSede(BigDecimal distanciaSede) {
        this.distanciaSede = distanciaSede;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public String getDescricao() {
        StringBuilder retorno = new StringBuilder("");
        if (codigo != null) {
            retorno.append(codigo);
        }
        if (nomePropriedade != null) {
            if (!retorno.toString().isEmpty()) {
                retorno.append(" - ");
            }
            retorno.append(nomePropriedade);
        }
        return retorno.toString();
    }

    @Override
    public String getNumeroCadastro() {
        return codigo.toString();
    }

    @Override
    public String getCadastroResponsavel() {
        return getDescricao();
    }

    public List<PropriedadeRural> getPropriedadesAtuais() {
        Date dataOperacao = new Date();
        List<PropriedadeRural> vigentes = new ArrayList<>();
        try {
            for (PropriedadeRural atual : propriedade) {
                if (atual.getFinalVigencia() == null || atual.getFinalVigencia().after(dataOperacao)) {
                    vigentes.add(atual);
                }
            }
        } catch (java.lang.NullPointerException ex) {
        }
        return vigentes;
    }

    public PropriedadeRural getPropriedade(Pessoa pessoa) {
        Date dataOperacao = new Date();
        try {
            for (PropriedadeRural atual : propriedade) {
                if ((atual.getPessoa().equals(pessoa)) && (atual.getFinalVigencia() == null || atual.getFinalVigencia().after(dataOperacao))) {
                    return atual;
                }
            }
        } catch (java.lang.NullPointerException ex) {
        }
        return null;
    }

    public List<PropriedadeRural> getPropriedadesVigenteNaData(Date data) {
        List<PropriedadeRural> vigentes = new ArrayList<>();
        try {
            for (PropriedadeRural prop : propriedade) {
                if ((prop.getFinalVigencia() == null || prop.getFinalVigencia().after(data)) && prop.getInicioVigencia().before(data)) {
                    vigentes.add(prop);
                }
            }
        } catch (java.lang.NullPointerException ex) {
        }
        return vigentes;
    }

    public List<PropriedadeRural> getPropriedadesDoHistorico() {
        if (propriedadesDoHistorico != null) {
            return propriedadesDoHistorico;
        }

        propriedadesDoHistorico = new ArrayList<>();
        List<PropriedadeRural> atuais = getPropriedadesAtuais();

        for (PropriedadeRural pr : propriedade) {
            if (!atuais.contains(pr)) {
                propriedadesDoHistorico.add(pr);
            }
        }
        return propriedadesDoHistorico;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (codigo != null) {
            sb.append(codigo);
        }
        if (!Strings.isNullOrEmpty(getNomePropriedade())) {
            sb.append(" - ").append(getNomePropriedade());
        }
        if (!Strings.isNullOrEmpty(getLocalizacaoLote())) {
            sb.append(" - ").append(getLocalizacaoLote());
        }
        return sb.toString();
    }

    public static class EnderecoCadastroStrings {
        String nomePropriedade;
        String localizacao;

        public EnderecoCadastroStrings() {
        }

        public void setNomePropriedade(String nomePropriedade) {
            this.nomePropriedade = nomePropriedade;
        }

        public void setLocalizacao(String localizacao) {
            this.localizacao = localizacao;
        }

        public String getEnderecoCompleto() {
            StringBuilder sb = new StringBuilder();
            if (nomePropriedade != null) {
                sb.append("Nome da Propriedade: ").append(nomePropriedade);
            }
            if (localizacao != null) {
                sb.append(" - Localização: ").append(localizacao);
            }
            return sb.toString();
        }
    }
}
