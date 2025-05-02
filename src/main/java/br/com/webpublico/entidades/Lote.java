/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.Patrimonio;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Munif
 */
@GrupoDiagrama(nome = "CadastroImobiliario")
@Entity
@Etiqueta("Lote")
@Audited
public class Lote implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Lote (Município)")
    private String codigoLote;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    private Setor setor;
    @Tabelavel
    @ManyToOne
    @Pesquisavel
    private Quadra quadra;
    @Etiqueta("Lote (Loteamento)")
    @Pesquisavel
    @Tabelavel
    private String descricaoLoteamento;
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Obrigatorio
    @Pesquisavel
    @Numerico
    @Etiqueta("Área do Lote")
    private Double areaLote;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número")
    private String numeroCorreio;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Complemento")
    private String complemento;
    @Etiqueta("Código Anterior")
    private String codigoAnterior;
    @OneToMany(mappedBy = "lote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Testada> testadas = new ArrayList<Testada>();
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "lote", fetch = FetchType.EAGER)
    private List<CaracteristicasLote> caracteristicasLote;
    @Transient
    private Map<Atributo, ValorAtributo> atributos;
    @OneToMany(mappedBy = "lote", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<LoteCondominio> lotesCondominio;
    @OneToMany(mappedBy = "originario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoteOriginario> originarios;
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    private Patrimonio patrimonio;
    private String migracaoChave;
    @ManyToOne
    private Zona zona;
    private Boolean vulnerabilidadeAmbiental;
    @ManyToOne
    private ZonaFiscal zonaFiscal;
    @Transient
    @Tabelavel
    @Etiqueta("Logradouro Principal")
    private Logradouro logradouroPrincipal;
    @Transient
    private String codigoQuadra;
    @Transient
    private String codigoSetor;

    public Lote(Long id, String codigoLote, Setor setor, Quadra quadra, String descricaoLoteamento, Double areaLote, String numeroCorreio, String complemento, Logradouro logradouroPrincipal) {
        this.id = id;
        this.codigoLote = codigoLote;
        this.setor = setor;
        this.quadra = quadra;
        this.descricaoLoteamento = descricaoLoteamento;
        this.areaLote = areaLote;
        this.numeroCorreio = numeroCorreio;
        this.complemento = complemento;
        this.logradouroPrincipal = logradouroPrincipal;
    }

    public Lote() {
        this.vulnerabilidadeAmbiental = Boolean.FALSE;
    }

    public Logradouro getLogradouroPrincipal() {
        return logradouroPrincipal;
    }

    public void setLogradouroPrincipal(Logradouro logradouroPrincipal) {
        this.logradouroPrincipal = logradouroPrincipal;
    }

    public String getMigracaoChave() {
        return migracaoChave;
    }

    public void setMigracaoChave(String migracaoChave) {
        this.migracaoChave = migracaoChave;
    }

    public Zona getZona() {
        return zona;
    }

    public void setZona(Zona zona) {
        this.zona = zona;
    }

    public Double getAreaLote() {
        return areaLote != null ? areaLote : 0D;
    }

    public void setAreaLote(Double areaLote) {
        this.areaLote = areaLote;
    }

    public String getCodigoLote() {
        return codigoLote;
    }

    public void setCodigoLote(String codigoLote) {
        this.codigoLote = codigoLote;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public Setor getSetor() {
        return setor;
    }

    public void setSetor(Setor setor) {
        this.setor = setor;
    }

    public Map<Atributo, ValorAtributo> getAtributos() {
        if (atributos == null) {
            popularAtributos(Lists.newArrayList());
        }
        return atributos;
    }

    public void popularAtributos(List<Atributo> atributosPorClasse) {
        atributos = new HashMap();
        if (caracteristicasLote != null) {
            for (CaracteristicasLote carac : caracteristicasLote) {
                if (carac.getValorAtributo().getValorDiscreto() == null) {
                    inserirValorAtributoPadrao(carac);
                } else {
                    atributos.put(carac.getAtributo(), carac.getValorAtributo());
                }
            }
        }
        if (atributosPorClasse != null) {
            for (Atributo atributo : atributosPorClasse) {
                if (!atributos.containsKey(atributo)) {
                    atributos.put(atributo, new ValorAtributo(atributo));
                }
            }
        }
    }

    public void inserirValorAtributoPadrao(CaracteristicasLote carac) {
        if (!carac.getAtributo().getValoresPossiveis().isEmpty()) {
            for (ValorPossivel valorPossivel : carac.getAtributo().getValoresPossiveis()) {
                if (valorPossivel.isValorPadrao()) {
                    carac.getValorAtributo().setValorDiscreto(valorPossivel);
                    break;
                }
            }
        }
        atributos.put(carac.getAtributo(), carac.getValorAtributo());
    }

    public void setAtributos(Map<Atributo, ValorAtributo> atributos) {
        this.atributos = atributos;
    }

    public List<Testada> getTestadas() {
        return testadas;
    }

    public void setTestadas(List<Testada> testadas) {
        this.testadas = testadas;
    }

    public Quadra getQuadra() {
        return quadra;
    }

    public void setQuadra(Quadra quadra) {
        this.quadra = quadra;
    }

    public String getCodigoAnterior() {
        return codigoAnterior;
    }

    public void setCodigoAnterior(String codigoAnterior) {
        this.codigoAnterior = codigoAnterior;
    }

    @Deprecated
    public String getNumeroCorreio() {
        return numeroCorreio;
    }

    @Deprecated
    public void setNumeroCorreio(String numeroCorreio) {
        this.numeroCorreio = numeroCorreio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<LoteOriginario> getOriginarios() {
        return originarios;
    }

    public void setOriginarios(List<LoteOriginario> originarios) {
        this.originarios = originarios;
    }

    public List<LoteCondominio> getLotesCondominio() {
        return lotesCondominio;
    }

    public void setLotesCondominio(List<LoteCondominio> lotesCondominio) {
        this.lotesCondominio = lotesCondominio;
    }

    public String getDescricaoLoteamento() {
        return descricaoLoteamento;
    }

    public void setDescricaoLoteamento(String descricaoLoteamento) {
        this.descricaoLoteamento = descricaoLoteamento;
    }

    public Patrimonio getPatrimonio() {
        return patrimonio;
    }

    public void setPatrimonio(Patrimonio patrimonio) {
        this.patrimonio = patrimonio;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Lote)) {
            return false;
        }
        Lote other = (Lote) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String retorno = "";
        if (codigoLote != null) {
            retorno += "Lote: " + codigoLote;
        }
        if (quadra != null && quadra.getDescricao() != null && setor != null) {
            retorno += ", Quadra:" + quadra.getDescricao();
        }
        if (setor != null && setor.getNome() != null) {
            retorno += ", Setor: " + setor.getNome();
        }
        return retorno;
    }

    public String getRetornaToStringModificado() {
        String retorno = "";
        if (codigoLote != null) {
            retorno += "Lote(Município): " + codigoLote;
        }
        if (quadra != null && quadra.getDescricao() != null && setor != null) {
            retorno += ", Quadra(Município): " + quadra.getDescricao();
        }
        if (descricaoLoteamento != null) {
            retorno += ", Lote(Loteamento): " + descricaoLoteamento;
        }
        if (quadra != null && quadra.getDescricaoLoteamento() != null && setor != null) {
            retorno += ", Quadra(Município):" + quadra.getDescricaoLoteamento();
        }
        if (setor != null && setor.getNome() != null) {
            retorno += ", Setor: " + setor.getNome();
        }
        return retorno;
    }

    public Testada getTestadaPrincipal() {
        if (this.testadas == null || this.testadas.isEmpty()) {
            return null;
        } else {
            for (Testada testada : this.testadas) {
                if (testada.getPrincipal() != null && testada.getPrincipal()) {
                    return testada;
                }
            }
            return testadas.get(0);
        }
    }

    public String getEndereco() {
        StringBuilder sb = new StringBuilder();

        if (getTestadaPrincipal() != null) {
            if (getTestadaPrincipal().getFace() != null) {
                if (getTestadaPrincipal().getFace().getLogradouroBairro() != null) {
                    if (getTestadaPrincipal().getFace().getLogradouroBairro().getLogradouro() != null && getTestadaPrincipal().getFace().getLogradouroBairro().getLogradouro().getTipoLogradouro() != null) {
                        sb.append(getTestadaPrincipal().getFace().getLogradouroBairro().getLogradouro().getTipoLogradouro().getDescricao()).append(" ");
                    }
                    if (getTestadaPrincipal().getFace().getLogradouroBairro().getLogradouro() != null) {
                        sb.append(getTestadaPrincipal().getFace().getLogradouroBairro().getLogradouro().getNome()).append(", ");
                    }

                    if (getTestadaPrincipal().getFace().getLogradouroBairro().getBairro() != null) {
                        sb.append(getTestadaPrincipal().getFace().getLogradouroBairro().getBairro().getDescricao()).append(", ");
                    }
                    //if (getTestadaPrincipal().getFace().getLogradouroBairro().getCep() != null) {
                    //    sb.append(getTestadaPrincipal().getFace().getLogradouroBairro().getCep()).append(" ");
                    //}
                    if (getTestadaPrincipal().getFace().getCep() != null) {
                        sb.append(getTestadaPrincipal().getFace().getCep()).append(" ");
                    }

                }
            }
        }


        return sb.toString();
    }

    public void setCodigoQuadra(String codigoQuadra) {
        this.codigoQuadra = codigoQuadra;
    }

    public String getCodigoQuadra() {
        return codigoQuadra;
    }

    public void setCodigoSetor(String codigoSetor) {
        this.codigoSetor = codigoSetor;
    }

    public String getCodigoSetor() {
        return codigoSetor;
    }

    public Testada getTestada(String codigoFace) {
        for (Testada testada : testadas) {
            if (testada.getFace().getCodigoFace().equals(codigoFace)) {
                return testada;
            }
        }
        return null;
    }

    public List<CaracteristicasLote> getCaracteristicasLote() {
        return caracteristicasLote;
    }

    public void setCaracteristicasLote(List<CaracteristicasLote> caracteristicasLote) {
        this.caracteristicasLote = caracteristicasLote;
    }

    public void popularCaracteristicas() {
        if (atributos != null) {
            if (caracteristicasLote == null) {
                caracteristicasLote = new ArrayList();
            }
            CaracteristicasLote caracteristica;
            boolean registrado = false;
            for (Atributo atributo : atributos.keySet()) {
                registrado = false;
                for (CaracteristicasLote c : this.caracteristicasLote) {
                    if (c.getAtributo().equals(atributo)) {
                        c.setValorAtributo(atributos.get(atributo));
                        registrado = true;
                        break;
                    }
                }
                if (registrado) {
                    continue;
                }
                caracteristica = new CaracteristicasLote();
                caracteristica.setAtributo(atributo);
                caracteristica.setValorAtributo(atributos.get(atributo));
                caracteristica.setLote(this);
                this.caracteristicasLote.add(caracteristica);
            }
        }
    }

    public Boolean getVulnerabilidadeAmbiental() {
        return vulnerabilidadeAmbiental;
    }

    public void setVulnerabilidadeAmbiental(Boolean vulnerabilidadeAmbiental) {
        this.vulnerabilidadeAmbiental = vulnerabilidadeAmbiental;
    }

    public ZonaFiscal getZonaFiscal() {
        return zonaFiscal;
    }

    public void setZonaFiscal(ZonaFiscal zonaFiscal) {
        this.zonaFiscal = zonaFiscal;
    }
}
