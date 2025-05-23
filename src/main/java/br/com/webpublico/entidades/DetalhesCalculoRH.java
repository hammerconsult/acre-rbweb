package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 05/09/13
 * Time: 14:33
 * To change this template use File | Settings | File Templates
 */
@GrupoDiagrama(nome = "RecursosHumanos")
@Entity
@Audited
@Etiqueta("Detalhes do Calculo Folha RH")
public class DetalhesCalculoRH implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Integer totalServidores;
    private Integer totalNaoCalculados;
    private Integer totalComProblemasNoCalculo;
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @ManyToOne
    private FolhaDePagamento folhaDePagamento;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCalculo;
    @Transient
    private String descricao;
    private Integer mesesRetroacao;

//    private Integer totalPensionistas;
//    private Integer totalAposentados;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "detalhesCalculoRH", orphanRemoval = true)
    private List<ItensDetalhesErrosCalculo> itensDetalhesErrosCalculos;

    public DetalhesCalculoRH() {
        totalServidores = 0;
        totalComProblemasNoCalculo = 0;
        totalNaoCalculados = 0;
        itensDetalhesErrosCalculos = new ArrayList<>();
        dataCalculo = new Date();
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public synchronized void contaTotalComProblemasNoCalculo() {
        totalComProblemasNoCalculo++;
    }

    public Date getDataCalculo() {
        return dataCalculo;
    }

    public void setDataCalculo(Date dataCalculo) {
        this.dataCalculo = dataCalculo;
    }

    public FolhaDePagamento getFolhaDePagamento() {
        return folhaDePagamento;
    }

    public void setFolhaDePagamento(FolhaDePagamento folhaDePagamento) {
        this.folhaDePagamento = folhaDePagamento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTotalServidores() {
        return totalServidores;
    }

    public void setTotalServidores(Integer totalServidores) {
        this.totalServidores = totalServidores;
    }

    public Integer getTotalNaoCalculados() {
        return totalNaoCalculados;
    }

    public void setTotalNaoCalculados(Integer totalNaoCalculados) {
        this.totalNaoCalculados = totalNaoCalculados;
    }

    public Integer getTotalComProblemasNoCalculo() {
        return totalComProblemasNoCalculo;
    }

    public void setTotalComProblemasNoCalculo(Integer totalComProblemasNoCalculo) {
        this.totalComProblemasNoCalculo = totalComProblemasNoCalculo;
    }

    public List<ItensDetalhesErrosCalculo> getItensDetalhesErrosCalculos() {
        return itensDetalhesErrosCalculos;
    }

    public void setItensDetalhesErrosCalculos(List<ItensDetalhesErrosCalculo> itensDetalhesErrosCalculos) {
        this.itensDetalhesErrosCalculos = itensDetalhesErrosCalculos;
    }

    public Integer getMesesRetroacao() {
        return mesesRetroacao;
    }

    public void setMesesRetroacao(Integer mesesRetroacao) {
        this.mesesRetroacao = mesesRetroacao;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public void incrementaNaoCalculado() {
        totalNaoCalculados++;
    }

    public void incrementaServidorComProblema() {
        totalComProblemasNoCalculo++;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        return "DetalhesCalculoRH{" +
                "totalServidores=" + totalServidores +
                ", totalNaoCalculados=" + totalNaoCalculados +
                ", totalComProblemasNoCalculo=" + totalComProblemasNoCalculo +
                '}';
    }
}
