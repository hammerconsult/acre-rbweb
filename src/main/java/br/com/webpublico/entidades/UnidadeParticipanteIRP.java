package br.com.webpublico.entidades;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
@Etiqueta("Unidade Participante Intenção Registro de Preço")
public class UnidadeParticipanteIRP extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Participante IRP")
    private ParticipanteIRP participanteIRP;

    @ManyToOne
    @Etiqueta("Unidade Participante")
    private UnidadeOrganizacional unidadeParticipante;

    @Obrigatorio
    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Início de Vigência")
    private Date inicioVigencia;

    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Fim de Vigência")
    private Date fimVigencia;

    @Transient
    @Obrigatorio
    @Etiqueta("Unidade Participante")
    private HierarquiaOrganizacional hierarquiaParticipante;

    public UnidadeParticipanteIRP() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ParticipanteIRP getParticipanteIRP() {
        return participanteIRP;
    }

    public void setParticipanteIRP(ParticipanteIRP participanteIRP) {
        this.participanteIRP = participanteIRP;
    }

    public UnidadeOrganizacional getUnidadeParticipante() {
        return unidadeParticipante;
    }

    public void setUnidadeParticipante(UnidadeOrganizacional unidadeParticipante) {
        this.unidadeParticipante = unidadeParticipante;
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

    public HierarquiaOrganizacional getHierarquiaParticipante() {
        return hierarquiaParticipante;
    }

    public void setHierarquiaParticipante(HierarquiaOrganizacional hierarquiaParticipante) {
        if (hierarquiaParticipante != null) {
            this.setUnidadeParticipante(hierarquiaParticipante.getSubordinada());
        }
        this.hierarquiaParticipante = hierarquiaParticipante;
    }

    public void validarVigencia(UnidadeParticipanteIRP novaUnidade) {
        ValidacaoException ve = new ValidacaoException();

        if (this.temFimDeVigencia() && novaUnidade.getInicioVigencia().compareTo(this.getFimVigencia()) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O novo registro não poderá ser adicionado pois já existe um registro vigênte em: " + DataUtil.getDataFormatada(novaUnidade.getInicioVigencia()));
            ve.adicionarMensagemDeOperacaoNaoPermitida("Detalhes do registro já vigente: " + getHierarquiaParticipante() + " - Inicio Vigência: " + DataUtil.getDataFormatada(this.getInicioVigencia()) + " - Fim Vigência: " + DataUtil.getDataFormatada(this.getFimVigencia()));
        }
        if (novaUnidade.getInicioVigencia().compareTo(this.getInicioVigencia()) == 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data início de vigência deve ser posterior a " + DataUtil.getDataFormatada(this.getInicioVigencia()));
        }
        if (getFimVigencia() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O novo registro não poderá ser adicionado pois já existe um registro vigênte em: " + DataUtil.getDataFormatada(getInicioVigencia()));
        }
        if ((novaUnidade.temFimDeVigencia() && this.temFimDeVigencia()) &&
            novaUnidade.getFimVigencia().compareTo(this.getFimVigencia()) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de fim de vigência deve ser diferente de " + DataUtil.getDataFormatada(this.getFimVigencia()));
        }
        if ((novaUnidade.temFimDeVigencia() && this.temFimDeVigencia()) &&
            novaUnidade.getInicioVigencia().compareTo(this.getFimVigencia()) <= 0 &&
            novaUnidade.getInicioVigencia().compareTo(this.getInicioVigencia()) >= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de início de vigência está entre uma vigência já estabelecida: de " + DataUtil.getDataFormatada(this.getInicioVigencia()) + " à " + DataUtil.getDataFormatada(this.getFimVigencia()));
        }
        if ((novaUnidade.temFimDeVigencia() && this.temFimDeVigencia()) &&
            novaUnidade.getFimVigencia().compareTo(this.getInicioVigencia()) >= 0 &&
            novaUnidade.getFimVigencia().compareTo(this.getFimVigencia()) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de fim de vigência está entre uma vigência já estabelecida: de " + DataUtil.getDataFormatada(this.getInicioVigencia()) + " à " + DataUtil.getDataFormatada(this.getFimVigencia()));
        }
        if ((novaUnidade.temFimDeVigencia() && this.temFimDeVigencia()) &&
            novaUnidade.getInicioVigencia().compareTo(this.getInicioVigencia()) <= 0 &&
            novaUnidade.getFimVigencia().compareTo(this.getFimVigencia()) >= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A vigência informada está abrangindo uma vigência já estabelecida: de " + DataUtil.getDataFormatada(this.getInicioVigencia()) + " à " + DataUtil.getDataFormatada(this.getFimVigencia()));
        }
        ve.lancarException();
    }

    private boolean temFimDeVigencia() {
        return fimVigencia != null;
    }
}
