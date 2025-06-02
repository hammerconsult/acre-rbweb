/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Munif
 */
@GrupoDiagrama(nome = "CadastroImobiliario")
@Entity
@Audited
public class Construcao implements Serializable, Comparable<Construcao> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código Interno")
    private Long id;
    @Tabelavel
    @Column(length = 70)
    @Size(max = 70)
    private String descricao;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "construcao", orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CaracteristicasConstrucao> caracteristicasConstrucao;
    @Transient
    private Map<Atributo, ValorAtributo> atributos;
    @ManyToOne
    private CadastroImobiliario imovel;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date anoConstrucao;
    @ManyToOne
    private Construcao englobado;
    @Tabelavel
    private Double areaConstruida;
    @Transient
    private Double areaTotalConstruida;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @Transient
    private boolean dummy;
    @Transient
    private Long criadoEm;
    @Tabelavel
    @Obrigatorio
    private Integer codigo;
    private Integer quantidadePavimentos;
    private String migracaoChave;
    private String habitese;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataHabitese;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataProtocolo;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataAlvara;
    private String numeroAlvara;
    private String tipoAlvara;
    private String numeroProtocolo;
    private String tipoProtocolo;
    private String folhaDocumento;
    private String livroDocumento;
    @OneToMany(mappedBy = "construcao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventoCalculoConstrucao> eventos;
    @Transient
    private Map<GrupoAtributo, List<EventoCalculoConstrucao>> mapEventoCalculoConstrucaoPorGrupoAtributo;
    private Boolean cancelada;

    public Construcao(Long id, String migracaoChave) {
        this.id = id;
        this.migracaoChave = migracaoChave;
    }

    public Double getAreaTotalConstruida() {
        return areaTotalConstruida != null ? areaTotalConstruida : 0.0;
    }

    public void setAreaTotalConstruida(Double areaTotalConstruida) {
        this.areaTotalConstruida = areaTotalConstruida;
    }

    public Construcao() {
        construtor();
    }

    public Construcao(Long id, Construcao englobado) {
        this.id = id;
        this.englobado = englobado;
        construtor();
    }

    public Construcao(Long id) {
        this.id = id;
        construtor();
    }

    public List<EventoCalculoConstrucao> getEventos() {
        return eventos;
    }

    public void setEventos(List<EventoCalculoConstrucao> eventos) {
        this.eventos = eventos;
    }

    public String getHabitese() {
        return habitese;
    }

    public void setHabitese(String habitese) {
        this.habitese = habitese;
    }

    public Date getDataHabitese() {
        return dataHabitese;
    }

    public void setDataHabitese(Date dataHabitese) {
        this.dataHabitese = dataHabitese;
    }

    public Date getDataProtocolo() {
        return dataProtocolo;
    }

    public void setDataProtocolo(Date dataProtocolo) {
        this.dataProtocolo = dataProtocolo;
    }

    public Date getDataAlvara() {
        return dataAlvara;
    }

    public void setDataAlvara(Date dataAlvara) {
        this.dataAlvara = dataAlvara;
    }

    public String getNumeroAlvara() {
        return numeroAlvara;
    }

    public void setNumeroAlvara(String numeroAlvara) {
        this.numeroAlvara = numeroAlvara;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public boolean isDummy() {
        return dummy;
    }

    public void setDummy(boolean dummy) {
        this.dummy = dummy;
    }

    public Double getAreaConstruida() {
        return areaConstruida != null ? areaConstruida : Double.valueOf(0);
    }

    public void setAreaConstruida(Double areaConstruida) {
        this.areaConstruida = areaConstruida;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getAnoConstrucao() {
        return anoConstrucao;
    }

    public Integer getAnoDaConstrucao() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(anoConstrucao);
        return calendar.get(Calendar.YEAR);
    }

    public void setAnoConstrucao(Date anoConstrucao) {
        this.anoConstrucao = anoConstrucao;
    }

    public Map<Atributo, ValorAtributo> getAtributos() {
        if (atributos == null) {
            popularAtributos(Lists.newArrayList());
        }
        return atributos;
    }

    public void popularAtributos(List<Atributo> atributosPorClasse) {
        atributos = new HashMap();
        if (caracteristicasConstrucao != null) {
            for (CaracteristicasConstrucao carac : caracteristicasConstrucao) {
                atributos.put(carac.getAtributo(), carac.getValorAtributo());
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

    public void setAtributos(Map<Atributo, ValorAtributo> atributos) {
        this.atributos = atributos;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Construcao getEnglobado() {
        return englobado;
    }

    public void setEnglobado(Construcao englobado) {
        this.englobado = englobado;
    }

    public CadastroImobiliario getImovel() {
        return imovel;
    }

    public void setImovel(CadastroImobiliario imovel) {
        this.imovel = imovel;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }


    public Integer getQuantidadePavimentos() {
        return quantidadePavimentos;
    }

    public void setQuantidadePavimentos(Integer quantidadePavimentos) {
        this.quantidadePavimentos = quantidadePavimentos;
    }

    public String getMigracaoChave() {
        return migracaoChave;
    }

    public void setMigracaoChave(String migracaoChave) {
        this.migracaoChave = migracaoChave;
    }

    public Boolean getCancelada() {
        return cancelada != null ? cancelada : false;
    }

    public void setCancelada(Boolean cancelada) {
        this.cancelada = cancelada;
    }

    public String getTipoAlvara() {
        return tipoAlvara;
    }

    public void setTipoAlvara(String tipoAlvara) {
        this.tipoAlvara = tipoAlvara;
    }

    public String getTipoProtocolo() {
        return tipoProtocolo;
    }

    public void setTipoProtocolo(String tipoProtocolo) {
        this.tipoProtocolo = tipoProtocolo;
    }

    public String getFolhaDocumento() {
        return folhaDocumento;
    }

    public void setFolhaDocumento(String folhaDocumento) {
        this.folhaDocumento = folhaDocumento;
    }

    public String getLivroDocumento() {
        return livroDocumento;
    }

    public void setLivroDocumento(String livroDocumento) {
        this.livroDocumento = livroDocumento;
    }

    public List<GrupoAtributo> initEventosCalculadosConstrucao() {
        if (mapEventoCalculoConstrucaoPorGrupoAtributo == null) {
            mapEventoCalculoConstrucaoPorGrupoAtributo = Maps.newHashMap();
            for (EventoCalculoConstrucao evento : eventos) {
                mapEventoCalculoConstrucaoPorGrupoAtributo.computeIfAbsent(evento.getEventoCalculo().getGrupoAtributo(), k -> Lists.newArrayList());
                mapEventoCalculoConstrucaoPorGrupoAtributo.get(evento.getEventoCalculo().getGrupoAtributo()).add(evento);
            }
        }
        return mapEventoCalculoConstrucaoPorGrupoAtributo.keySet().stream()
            .sorted(Comparator.comparing(GrupoAtributo::getCodigo))
            .collect(Collectors.toList());
    }

    public Map<GrupoAtributo, List<EventoCalculoConstrucao>> getMapEventoCalculoConstrucaoPorGrupoAtributo() {
        return mapEventoCalculoConstrucaoPorGrupoAtributo;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        if (codigo != null && (!descricao.trim().equals("") || descricao != null)) {
            sb.append(codigo).append(" - ").append(descricao);
        }
        return sb.toString();
    }

    @Override
    public int compareTo(Construcao o) {
        try {
            int i = this.getCodigo().compareTo(o.getCodigo());
            if (i == 0) {
                i = this.getCancelada().compareTo(o.getCancelada());
            }
            return i;
        } catch (Exception e) {
            return 0;
        }

    }

    public BigDecimal getValorVenal() {
        for (EventoCalculoConstrucao evento : eventos) {
            if (EventoConfiguradoBCI.Identificacao.VALOR_VENAL.equals(evento.getEventoCalculo().getIdentificacao())) {
                return evento.getValor();
            }
        }
        return BigDecimal.ZERO;
    }

    public static Construcao getConstrucaoDummy(CadastroImobiliario cadastro) {
        Construcao construcao = new Construcao();
        construcao.setDummy(true);
        construcao.setAtributos(new HashMap());
        construcao.setAnoConstrucao(new Date());
        construcao.setAreaConstruida(0.0);
        construcao.setDataRegistro(new Date());
        construcao.setId(Long.MIN_VALUE);
        construcao.setImovel(cadastro);
        return construcao;
    }


    private void construtor() {
        atributos = new HashMap<>();
        criadoEm = System.nanoTime();
        dataRegistro = new Date();
        dummy = false;
        eventos = Lists.newArrayList();
    }

    public List<CaracteristicasConstrucao> getCaracteristicasConstrucao() {
        if (caracteristicasConstrucao == null) {
            caracteristicasConstrucao = Lists.newArrayList();
        }
        return caracteristicasConstrucao;
    }

    public void setCaracteristicasConstrucao(List<CaracteristicasConstrucao> caracteristicasConstrucao) {
        this.caracteristicasConstrucao = caracteristicasConstrucao;
    }

    public void popularCaracteristicas() {
        if (atributos != null) {
            if (caracteristicasConstrucao == null) {
                caracteristicasConstrucao = new ArrayList();
            }
            CaracteristicasConstrucao caracteristicasConstrucao;
            boolean registrado = false;
            for (Atributo atributo : atributos.keySet()) {
                registrado = false;
                for (CaracteristicasConstrucao c : this.caracteristicasConstrucao) {
                    if (c.getAtributo().equals(atributo)) {
                        c.setValorAtributo(atributos.get(atributo));
                        registrado = true;
                        break;
                    }
                }
                if (registrado) {
                    continue;
                }
                caracteristicasConstrucao = new CaracteristicasConstrucao();
                caracteristicasConstrucao.setAtributo(atributo);
                caracteristicasConstrucao.setValorAtributo(atributos.get(atributo));
                caracteristicasConstrucao.setConstrucao(this);
                this.caracteristicasConstrucao.add(caracteristicasConstrucao);
            }
        }
    }
}
