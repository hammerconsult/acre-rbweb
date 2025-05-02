package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.pncp.entidadeauxiliar.EventoPncpVO;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@GrupoDiagrama(nome = "Licitações")
public class ExecucaoProcessoEmpenho extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Execução Processo")
    private ExecucaoProcesso execucaoProcesso;

    @ManyToOne
    @Etiqueta("Empenho")
    private Empenho empenho;

    @OneToOne(cascade = CascadeType.ALL)
    @Etiqueta("Solicitação de Empenho")
    private SolicitacaoEmpenho solicitacaoEmpenho;

    @Etiqueta("Sequencial Criado pelo PNCP ao Realizar o Envio do Empenho")
    @Tabelavel(campoSelecionado = false)
    private String sequencialIdPncp;

    @Etiqueta("Id Criado pelo PNCP ao Realizar o Envio do Empenho")
    @Tabelavel(campoSelecionado = false)
    private String idPncp;

    @Transient
    private EventoPncpVO eventoPncpVO;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ExecucaoProcesso getExecucaoProcesso() {
        return execucaoProcesso;
    }

    public void setExecucaoProcesso(ExecucaoProcesso execucaoProcesso) {
        this.execucaoProcesso = execucaoProcesso;
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

    public String getSequencialIdPncp() {
        return sequencialIdPncp;
    }

    public void setSequencialIdPncp(String sequencialIdPncp) {
        this.sequencialIdPncp = sequencialIdPncp;
    }

    public String getIdPncp() {
        return idPncp;
    }

    public void setIdPncp(String idPncp) {
        this.idPncp = idPncp;
    }

    public EventoPncpVO getEventoPncpVO() {
        return eventoPncpVO;
    }

    public void setEventoPncpVO(EventoPncpVO eventoPncpVO) {
        this.eventoPncpVO = eventoPncpVO;
    }

    public String toStringEmpenho() {
        try {
            if (empenho != null) {
                return "<b>Empenho: </b>" + empenho;
            }
            return "<b>Solicitação de Empenho: </b>" + solicitacaoEmpenho + ". Empenho ainda não executado.";
        } catch (NullPointerException ex) {
            return "";
        }
    }
}
