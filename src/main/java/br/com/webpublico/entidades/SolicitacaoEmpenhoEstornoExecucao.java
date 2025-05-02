package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="SOLICITACAOEMPENHOESTEXEC")
@Audited
@GrupoDiagrama(nome = "Contratos")
@Etiqueta("Solicitação de Empenho Estorno Execução")
public class SolicitacaoEmpenhoEstornoExecucao extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Número")
    private Long numero;

    @Etiqueta("Data de Lançamento")
    private Date dataLancamento;

    @ManyToOne
    @Etiqueta("Usuário")
    private UsuarioSistema usuarioSistema;

    @ManyToOne
    @Etiqueta("Execução do Contrato")
    private ExecucaoContrato execucaoContrato;

    @ManyToOne(cascade = CascadeType.ALL)
    @Etiqueta("Solicitação Empenho Estorno")
    private SolicitacaoEmpenhoEstorno solicitacaoEmpenhoEstorno;

    public SolicitacaoEmpenhoEstornoExecucao() { }


    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public ExecucaoContrato getExecucaoContrato() {
        return execucaoContrato;
    }

    public void setExecucaoContrato(ExecucaoContrato execucaoContrato) {
        this.execucaoContrato = execucaoContrato;
    }

    public SolicitacaoEmpenhoEstorno getSolicitacaoEmpenhoEstorno() {
        return solicitacaoEmpenhoEstorno;
    }

    public void setSolicitacaoEmpenhoEstorno(SolicitacaoEmpenhoEstorno solicitacaoEmpenhoEstorno) {
        this.solicitacaoEmpenhoEstorno = solicitacaoEmpenhoEstorno;
    }
}
