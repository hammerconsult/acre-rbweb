/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoHierarquia;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.pessoa.dto.HierarquiaOrganizacionalDTO;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@GrupoDiagrama(nome = "Administrativo")
@Entity

@Audited
@Etiqueta("Esfera de Governo")
/**
 * Determina a estrutura organizacional gerada a partir do relacionamento entre
 * as Unidades Organizacionais superiores e subordinadas. Representa o
 * organograma estrutural.
 */
public class HierarquiaOrganizacional implements Serializable, Comparable<HierarquiaOrganizacional> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Tabelavel
    @Etiqueta("Código")
    @Invisivel
    private Long id;
    /**
     * Código da Unidade Organizacional, determinado pela máscara da primeira
     * Entidade da hierarquia organizacional na qual esta unidade está
     * vinculada. A máscara de eventuais entidades em níveis mais baixos da
     * hierarquia será desconsiderada.
     */
    @Tabelavel
    private String codigo;
    /**
     * Nível gerado a partir do relacionamento estabelecido na hierarquia,
     * iniciando em 1 no primeiro nível.
     */
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta(value = "Descrição")
    private String descricao;
    private Integer nivelNaEntidade;
    @ManyToOne
    private UnidadeOrganizacional superior;
    @Obrigatorio
    @ManyToOne
    private UnidadeOrganizacional subordinada;
    @Obrigatorio
    @ManyToOne
    private Exercicio exercicio;
    @Enumerated(EnumType.STRING)
    private TipoHierarquiaOrganizacional tipoHierarquiaOrganizacional;
    private Integer indiceDoNo;
    private BigDecimal valorEstimado;
    private String codigoNo;
    @Enumerated(EnumType.STRING)
    private TipoHierarquia tipoHierarquia;
    @JoinColumn(name = "HIERARQUIAORC_ID")
    @ManyToOne
    private HierarquiaOrganizacional hierarquiaOrganizacionalOrcamentaria;
    @OneToMany(mappedBy = "hierarquiaOrganizacionalAdm", cascade = CascadeType.ALL)
    private List<HierarquiaOrganizacionalResponsavel> hierarquiaOrganizacionalResponsavels;
    @OneToMany(mappedBy = "hierarquiaOrganizacionalOrc", cascade = CascadeType.ALL)
    private List<HierarquiaOrganizacionalCorrespondente> hierarquiaOrganizacionalCorrespondentes;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date inicioVigencia;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fimVigencia;
    @Transient
    private boolean gerenciaValorEstimando;
    @Transient
    private Long criadoEm;
    @Transient
    @Invisivel
    private boolean selecionado;

    public HierarquiaOrganizacional(Long id) {
        this.id = id;
        this.valorEstimado = BigDecimal.ZERO;
        this.tipoHierarquia = TipoHierarquia.OUTROS;
        criadoEm = System.nanoTime();
        this.hierarquiaOrganizacionalCorrespondentes = new ArrayList<>();
        this.hierarquiaOrganizacionalResponsavels = new ArrayList<>();
    }

    public HierarquiaOrganizacional(Long id, Long IdUnidade, String codigo, String descricao) {
        this.id = id;
        this.subordinada = new UnidadeOrganizacional();
        this.subordinada.setId(IdUnidade);
        this.codigo = codigo;
        this.setDescricao(descricao);
        this.hierarquiaOrganizacionalCorrespondentes = new ArrayList<>();
        this.hierarquiaOrganizacionalResponsavels = new ArrayList<>();

        criadoEm = System.nanoTime();
    }

    public HierarquiaOrganizacional() {
        this.valorEstimado = BigDecimal.ZERO;
        this.tipoHierarquia = TipoHierarquia.OUTROS;
        criadoEm = System.nanoTime();
        this.hierarquiaOrganizacionalCorrespondentes = new ArrayList<>();
        this.hierarquiaOrganizacionalResponsavels = new ArrayList<>();
    }

    public static List<HierarquiaOrganizacionalDTO> toHierarquiaOrganizacionalDTOs(List<HierarquiaOrganizacional> hierarquias) {
        if (hierarquias != null && !hierarquias.isEmpty()) {
            List<HierarquiaOrganizacionalDTO> dtos = Lists.newLinkedList();
            hierarquias.forEach(ho -> dtos.add(toHierarquiaOrganizacionalDTO(ho)));
            return dtos;
        }
        return null;
    }

    public static HierarquiaOrganizacionalDTO toHierarquiaOrganizacionalDTO(HierarquiaOrganizacional ho) {
        if (ho == null) {
            return null;
        }
        return new HierarquiaOrganizacionalDTO(ho.getSubordinada().getId(), ho.getCodigo(), ho.getDescricao());
    }

    public TipoHierarquia getTipoHierarquia() {
        return tipoHierarquia;
    }

    public void setTipoHierarquia(TipoHierarquia tipoHierarquia) {
        this.tipoHierarquia = tipoHierarquia;
    }

    public UnidadeOrganizacional getSubordinada() {
        return subordinada;
    }

    public void setSubordinada(UnidadeOrganizacional subordinada) {
        if(subordinada != null){
            this.descricao = subordinada.getDescricao();
        }
        this.subordinada = subordinada;
    }

    public UnidadeOrganizacional getSuperior() {
        return superior;
    }

    public void setSuperior(UnidadeOrganizacional superior) {
        this.superior = superior;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNivelNaEntidade() {
        return nivelNaEntidade;
    }

    public void setNivelNaEntidade(Integer nivelNaEntidade) {
        this.nivelNaEntidade = nivelNaEntidade;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public TipoHierarquiaOrganizacional getTipoHierarquiaOrganizacional() {
        return tipoHierarquiaOrganizacional;
    }

    public void setTipoHierarquiaOrganizacional(TipoHierarquiaOrganizacional tipoHierarquiaOrganizacional) {
        this.tipoHierarquiaOrganizacional = tipoHierarquiaOrganizacional;
    }

    public Integer getIndiceDoNo() {
        return indiceDoNo;
    }

    public void setIndiceDoNo(Integer indiceDoNo) {
        this.indiceDoNo = indiceDoNo;
    }

    public BigDecimal getValorEstimado() {
        return valorEstimado;
    }

    public void setValorEstimado(BigDecimal valorEstimado) {
        this.valorEstimado = valorEstimado;
    }

    public String getCodigoNo() {
        return codigoNo;
    }

    public void setCodigoNo(String codigoNo) {
        this.codigoNo = codigoNo;
    }

    public boolean isGerenciaValorEstimando() {
        return (this.indiceDoNo > 2);
    }

    public void setGerenciaValorEstimando(boolean gerenciaValorEstimando) {
        this.gerenciaValorEstimando = gerenciaValorEstimando;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalOrcamentaria() {
        return hierarquiaOrganizacionalOrcamentaria;
    }

    public void setHierarquiaOrganizacionalOrcamentaria(HierarquiaOrganizacional hierarquiaOrganizacionalOrcamentaria) {
        this.hierarquiaOrganizacionalOrcamentaria = hierarquiaOrganizacionalOrcamentaria;
    }

    public List<HierarquiaOrganizacionalResponsavel> getHierarquiaOrganizacionalResponsavels() {
        return hierarquiaOrganizacionalResponsavels;
    }

    public void setHierarquiaOrganizacionalResponsavels(List<HierarquiaOrganizacionalResponsavel> hierarquiaOrganizacionalResponsavels) {
        this.hierarquiaOrganizacionalResponsavels = hierarquiaOrganizacionalResponsavels;
    }

    public List<HierarquiaOrganizacionalCorrespondente> getHierarquiaOrganizacionalCorrespondentes() {
        return hierarquiaOrganizacionalCorrespondentes;
    }

    public void setHierarquiaOrganizacionalCorrespondentes(List<HierarquiaOrganizacionalCorrespondente> hierarquiaOrganizacionalCorrespondentes) {
        this.hierarquiaOrganizacionalCorrespondentes = hierarquiaOrganizacionalCorrespondentes;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        if (codigo != null && descricao != null ) {
            return codigo + " - " + descricao;
        }
        return "";
    }


    public String getCodigoSemZerosFinais() {
        //Com o código = "01.02.000.00.000.0000.000.00";
        //vai retornar "01.02.", com o qual faremos um like...;
        boolean tudoZero = true;
        int i = codigo.length();
        int ultimoPonto = codigo.length();
        while (i > 0 && tudoZero) {
            char c = codigo.charAt(i - 1);
            if (c == '.') {
                ultimoPonto = i;
            }
            if (c != '.' && c != '0') {
                tudoZero = false;
            }
            i--;
        }
        return codigo.substring(0, ultimoPonto);
    }

    public String getCodigoDo2NivelDeHierarquia() {
        //Com o código = "01.02.001.00.000.0000.000.00";
        //vai retornar "02", com o qual faremos um like...;
        return codigo.split("\\.")[1];
    }

    public Integer getNivelPorCodigo() {
        //retorna o nivel confer o código
        String[] quebrado = codigo.split("\\.");
        int numero = 0;
        for (String parte : quebrado) {
            if (!parte.replaceAll("0", "").trim().isEmpty()) {
                numero++;
            }
        }
        return numero;
    }

    public String getDescricaoSubordinadaConcatenada() {
        String retorno = StringUtil.cortaOuCompletaEsquerda(this.getDescricao(), this.getDescricao().length() + getNivelPorCodigo() * 5, " ");
        return retorno;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public int compareTo(HierarquiaOrganizacional o) {
        try {
            return this.codigo.compareTo(o.getCodigo());
        } catch (Exception e) {
            return Integer.MAX_VALUE;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HierarquiaOrganizacional)) return false;

        HierarquiaOrganizacional other = (HierarquiaOrganizacional) o;

        if (id == null || other.id == null) { //se o ID de um dos dois for null
            return this.criadoEm != null ? criadoEm.equals(other.criadoEm) : other.criadoEm != null; //se o criadoEm desse for diferente de nulo, retorna o equals dele
        } else {                                                                                     //senão, retorna o criadoEm do outro igual a nulo também
            return this.id != null ? this.id.equals(other.id) : other.id != null; //igual o criadoEm, mas com id
        }
    }

    @Override
    public int hashCode() {
        if (id == null) { //se o ID de um dos dois for null
            return this.criadoEm.hashCode(); //se o criadoEm desse for diferente de nulo, retorna o equals dele
        } else {                                                                                     //senão, retorna o criadoEm do outro igual a nulo também
            return this.id.hashCode();
        }
    }

    public boolean isSelecionado() {
        return selecionado;
    }

    public void setSelecionado(boolean selecionado) {
        this.selecionado = selecionado;
    }

    public String getDescricaoHierarquia() {
        try {
            String descricao = this.toString();
            Integer indiceDoNo = this.getIndiceDoNo();
            if (indiceDoNo > 2) {
                StringBuilder sb = new StringBuilder();
                String espaco = "   ";
                for (int i = 0; i < indiceDoNo - 2; i++) {
                    sb.append(espaco).append(espaco).append(espaco);
                }
                sb.append(descricao);
                return sb.toString();
            }
            return descricao;
        } catch (NullPointerException npe) {
            return "";
        }
    }
}
