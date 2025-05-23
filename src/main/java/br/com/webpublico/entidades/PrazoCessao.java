package br.com.webpublico.entidades;

import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 08/05/14
 * Time: 16:04
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta("Prazo de Cessão")
public class PrazoCessao extends SuperEntidade implements Comparable<PrazoCessao> {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Inicio do Prazo")
    @Temporal(TemporalType.DATE)
    private Date inicioDoPrazo;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Fim do Prazo")
    @Temporal(TemporalType.DATE)
    private Date fimDoPrazo;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Cessão")
    @ManyToOne(cascade = CascadeType.ALL)
    private LoteCessao loteCessao;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Prorrogação da Cessão")
    @OneToOne
    private ProrrogacaoCessao prorrogacaoCessao;

    public PrazoCessao() {
        super();
    }

    public PrazoCessao(LoteCessao loteCessao) {
        super();
        this.loteCessao = loteCessao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getInicioDoPrazo() {
        return inicioDoPrazo;
    }

    public void setInicioDoPrazo(Date inicioDoPrazo) {
        this.inicioDoPrazo = inicioDoPrazo != null ? DataUtil.dataSemHorario(inicioDoPrazo) : null;
    }

    public Date getFimDoPrazo() {
        return fimDoPrazo;
    }

    public void setFimDoPrazo(Date fimDoPrazo) {
        this.fimDoPrazo = fimDoPrazo != null ? DataUtil.dataSemHorario(fimDoPrazo) : null;
    }

    public LoteCessao getLoteCessao() {
        return loteCessao;
    }

    public void setLoteCessao(LoteCessao loteCessao) {
        this.loteCessao = loteCessao;
    }

    public ProrrogacaoCessao getProrrogacaoCessao() {
        return prorrogacaoCessao;
    }

    public void setProrrogacaoCessao(ProrrogacaoCessao prorrogacaoCessao) {
        this.prorrogacaoCessao = prorrogacaoCessao;
    }

    public void validarNegocio(Date dataAtual) {
        ValidacaoException ve = new ValidacaoException();
        if (this.getInicioDoPrazo().compareTo(dataAtual) < 0) {
            ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "A data de início do prazo deve ser igual ou posterior a data atual.");
        }
        if (this.getFimDoPrazo().compareTo(dataAtual) < 0) {
            ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "A data de fim do prazo deve ser igual ou posterior a data atual.");
        }
        if (this.getFimDoPrazo().compareTo(this.inicioDoPrazo) < 0) {
            ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "A data de fim do prazo deve ser posterior a data inicial.");
        }
        ve.lancarException();
    }

    @Override
    public int compareTo(PrazoCessao pc) {
        return this.inicioDoPrazo.compareTo(pc.getInicioDoPrazo());
    }
}
