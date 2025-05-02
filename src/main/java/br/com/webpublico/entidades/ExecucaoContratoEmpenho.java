/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.List;

/**
 * @author Wellingto Abdo
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Contratos")
@Etiqueta("Empenho(s) da Execução de Contrato")
public class ExecucaoContratoEmpenho extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Execução de Contrato")
    private ExecucaoContrato execucaoContrato;

    @ManyToOne
    @Etiqueta("Empenho")
    private Empenho empenho;

    @OneToOne(cascade = CascadeType.PERSIST)
    @Etiqueta("Solicitação de Empenho")
    private SolicitacaoEmpenho solicitacaoEmpenho;

    @Invisivel
    @Transient
    private List<EmpenhoEstorno> estornosEmpenho;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ExecucaoContrato getExecucaoContrato() {
        return execucaoContrato;
    }

    public void setExecucaoContrato(ExecucaoContrato execucaoContrato) {
        this.execucaoContrato = execucaoContrato;
    }

    public Empenho getEmpenho() {
        return empenho;
    }

    public void setEmpenho(Empenho empenho) {
        this.empenho = empenho;
    }

    public SolicitacaoEmpenho getSolicitacaoEmpenho() {
        return solicitacaoEmpenho;
    }

    public void setSolicitacaoEmpenho(SolicitacaoEmpenho solicitacaoEmpenho) {
        this.solicitacaoEmpenho = solicitacaoEmpenho;
    }

    public List<EmpenhoEstorno> getEstornosEmpenho() {
        return estornosEmpenho;
    }

    public void setEstornosEmpenho(List<EmpenhoEstorno> estornosEmpenho) {
        this.estornosEmpenho = estornosEmpenho;
    }

    public String toStringEmpenho() {
        try {
            if (empenho != null) {
                return "<b>Empenho: </b>" + empenho.toString();
            } else {
                return "<b>Solicitação de Empenho: </b>" +
                    getSolicitacaoEmpenho().toString() + ". Empenho ainda não executado.";
            }
        } catch (NullPointerException ex) {
            return "";
        }
    }
}
