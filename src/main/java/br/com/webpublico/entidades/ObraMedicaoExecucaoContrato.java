package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by mga on 17/11/2017.
 */

@Entity
@Audited
@GrupoDiagrama(nome = "Contratos")
@Etiqueta("Medição Obra/Execução Contrato")
@Table(name = "OBRAMEDICAOEXECCONTRATO")
public class ObraMedicaoExecucaoContrato extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Medição da Obra")
    private ObraMedicao obraMedicao;

    @ManyToOne
    @Etiqueta("Execução Contrato")
    private ExecucaoContrato execucaoContrato;

    public ObraMedicaoExecucaoContrato() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ObraMedicao getObraMedicao() {
        return obraMedicao;
    }

    public void setObraMedicao(ObraMedicao obraMedicao) {
        this.obraMedicao = obraMedicao;
    }

    public ExecucaoContrato getExecucaoContrato() {
        return execucaoContrato;
    }

    public void setExecucaoContrato(ExecucaoContrato execucaoContrato) {
        this.execucaoContrato = execucaoContrato;
    }

    @Override
    public String toString() {
        try {
            return execucaoContrato.toString();
        } catch (Exception ex) {
            return "";
        }
    }
}
