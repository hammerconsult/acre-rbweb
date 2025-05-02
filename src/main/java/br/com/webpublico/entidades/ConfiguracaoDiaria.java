/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author reidocrime
 */
@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity

@Etiqueta("Tabela de Diárias")
public class ConfiguracaoDiaria extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Base Legal")
    @OneToOne()
    @Pesquisavel
    @Tabelavel
    private AtoLegal lei;
    @Etiqueta("Ato Legal Complementar")
    @OneToOne()
    @Pesquisavel
    @Tabelavel
    private AtoLegal atoLegal;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Início de Vigência")
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date inicioVigencia;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Fim de Vigência")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fimVigencia;
    @Etiqueta("Classes")
    @OneToMany(mappedBy = "configuracaoDiaria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClasseDiaria> classeDiarias;
    @Obrigatorio
    @Etiqueta("Quantidade de Dias Corridos para Bloqueio")
    private Integer quantidadeDiasCorridos;
    @Obrigatorio
    @Etiqueta("Quantidade de Dias Úteis para Bloqueio")
    private Integer quantidadeDiasUteis;

    public ConfiguracaoDiaria() {
        classeDiarias = new ArrayList<ClasseDiaria>();
        this.criadoEm = System.nanoTime();
    }

    public ConfiguracaoDiaria(AtoLegal lei, AtoLegal atoLegal, Date inicioVigencia, Date fimVigencia, List<ClasseDiaria> classeDiarias) {
        this.lei = lei;
        this.atoLegal = atoLegal;
        this.inicioVigencia = inicioVigencia;
        this.fimVigencia = fimVigencia;
        this.classeDiarias = classeDiarias;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public List<ClasseDiaria> getClasseDiarias() {
        Collections.sort(this.classeDiarias, new Comparator<ClasseDiaria>() {
            @Override
            public int compare(ClasseDiaria o1, ClasseDiaria o2) {
                String i1 = o1.getCodigo();
                String i2 = o2.getCodigo();
                return i1.compareTo(i2);
            }
        });
        return classeDiarias;
    }

    public void setClasseDiarias(List<ClasseDiaria> classeDiarias) {
        this.classeDiarias = classeDiarias;
    }

    public AtoLegal getLei() {
        return lei;
    }

    public void setLei(AtoLegal lei) {
        this.lei = lei;
    }

    public Integer getQuantidadeDiasCorridos() {
        return quantidadeDiasCorridos;
    }

    public void setQuantidadeDiasCorridos(Integer quantidadeDiasCorridos) {
        this.quantidadeDiasCorridos = quantidadeDiasCorridos;
    }

    public Integer getQuantidadeDiasUteis() {
        return quantidadeDiasUteis;
    }

    public void setQuantidadeDiasUteis(Integer quantidadeDiasUteis) {
        this.quantidadeDiasUteis = quantidadeDiasUteis;
    }

    @Override
    public String toString() {
        return "Lei " + lei.toString() + " Inicio de Vigência : " + new SimpleDateFormat("dd/MM/yyyy").format(inicioVigencia);
    }
}
