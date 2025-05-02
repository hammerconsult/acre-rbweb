package br.com.webpublico.entidades;

import br.com.webpublico.entidades.comum.SolicitacaoCadastroPessoa;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import static org.hibernate.envers.RelationTargetAuditMode.NOT_AUDITED;

@Entity
@Audited
@Etiqueta("Cálculo de Solicitação de Cadastro de Credor")
@Table(name = "CALCULOSOLICITACAOCADASTC")
public class CalculoSolicitacaoCadastroCredor extends Calculo {

    @Audited(targetAuditMode = NOT_AUDITED)
    @ManyToOne
    private SolicitacaoCadastroPessoa solicitacaoCadastroPessoa;
    @ManyToOne
    private ProcessoCalculoSolicitacaoCadastroCredor processoCalculoSolicitacao;

    public CalculoSolicitacaoCadastroCredor() {
        setTipoCalculo(TipoCalculo.SOLICITACAO_CADASTRO_CREDOR);
    }

    public SolicitacaoCadastroPessoa getSolicitacaoCadastroPessoa() {
        return solicitacaoCadastroPessoa;
    }

    public void setSolicitacaoCadastroPessoa(SolicitacaoCadastroPessoa solicitacaoCadastroPessoa) {
        this.solicitacaoCadastroPessoa = solicitacaoCadastroPessoa;
    }

    public ProcessoCalculoSolicitacaoCadastroCredor getProcessoCalculoSolicitacao() {
        return processoCalculoSolicitacao;
    }

    public void setProcessoCalculoSolicitacao(ProcessoCalculoSolicitacaoCadastroCredor processoCalculoSolicitacao) {
        this.processoCalculoSolicitacao = processoCalculoSolicitacao;
    }
}
