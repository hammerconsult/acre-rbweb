package br.com.webpublico.entidades.rh.avaliacao;


import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@GrupoDiagrama(nome = "RecursosHumanos - Avaliações")
@Audited
@Etiqueta("Avaliação do Servidor")
public class AvaliacaoVinculoFP extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    private Long id;
    @ManyToOne
    private AvaliacaoRH avaliacaoRH;
    @ManyToOne
    private VinculoFP vinculoFP;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataInicioExecucao;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataTerminoExecucao;

    @OneToMany(mappedBy = "avaliacaoVinculoFP")
    private List<RespostaAvaliacao> respostas;

    public AvaliacaoVinculoFP() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AvaliacaoRH getAvaliacaoRH() {
        return avaliacaoRH;
    }

    public void setAvaliacaoRH(AvaliacaoRH avaliacaoRH) {
        this.avaliacaoRH = avaliacaoRH;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public Date getDataInicioExecucao() {
        return dataInicioExecucao;
    }

    public void setDataInicioExecucao(Date dataInicioExecucao) {
        this.dataInicioExecucao = dataInicioExecucao;
    }

    public Date getDataTerminoExecucao() {
        return dataTerminoExecucao;
    }

    public void setDataTerminoExecucao(Date dataTerminoExecucao) {
        this.dataTerminoExecucao = dataTerminoExecucao;
    }

    public List<RespostaAvaliacao> getRespostas() {
        return respostas;
    }

    public void setRespostas(List<RespostaAvaliacao> respostas) {
        this.respostas = respostas;
    }
}
