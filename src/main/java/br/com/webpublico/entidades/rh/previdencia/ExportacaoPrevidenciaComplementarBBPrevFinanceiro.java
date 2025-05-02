package br.com.webpublico.entidades.rh.previdencia;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.ItemEntidadeDPContas;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@GrupoDiagrama(nome = "RecursosHumanos")
@Audited
@Entity
@Table(name = "EXPPREVCOMPBBPREVFINANC")
public class ExportacaoPrevidenciaComplementarBBPrevFinanceiro extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta("Patrocinador")
    private ItemEntidadeDPContas patrocinador;
    @Etiqueta("Ano")
    private Integer ano;
    @Etiqueta("Mês")
    private Integer mes;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data Geração")
    private Date dataGeracao;
    @OneToOne(cascade = CascadeType.ALL)
    private Arquivo arquivo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemEntidadeDPContas getPatrocinador() {
        return patrocinador;
    }

    public void setPatrocinador(ItemEntidadeDPContas patrocinador) {
        this.patrocinador = patrocinador;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Date getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(Date dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }
}
