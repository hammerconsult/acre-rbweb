package br.com.webpublico.entidades;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
@Etiqueta("Unidade da Licitação")
public class UnidadeLicitacao extends SuperEntidade implements Comparable<UnidadeLicitacao> {

    private static final Logger logger = LoggerFactory.getLogger(UnidadeLicitacao.class);
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Licitação")
    private Licitacao licitacao;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Unidade Administrativa")
    private UnidadeOrganizacional unidadeAdministrativa;

    @Etiqueta("Unidade Administrativa")
    @Transient
    private HierarquiaOrganizacional hierarquiaAdministrativa;

    @Obrigatorio
    @Temporal(TemporalType.DATE)
    @Etiqueta("Início de Vigência")
    private Date inicioVigencia;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Fim de Vigência")
    private Date fimVigencia;

    public UnidadeLicitacao() {
        super();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Licitacao getLicitacao() {
        return licitacao;
    }

    public void setLicitacao(Licitacao licitacao) {
        this.licitacao = licitacao;
    }

    public UnidadeOrganizacional getUnidadeAdministrativa() {
        return unidadeAdministrativa;
    }

    public void setUnidadeAdministrativa(UnidadeOrganizacional unidadeAdministrativa) {
        this.unidadeAdministrativa = unidadeAdministrativa;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public HierarquiaOrganizacional getHierarquiaAdministrativa() {
        return hierarquiaAdministrativa;
    }

    public void setHierarquiaAdministrativa(HierarquiaOrganizacional hierarquiaAdministrativa) {
        if (hierarquiaAdministrativa !=null){
            unidadeAdministrativa = hierarquiaAdministrativa.getSubordinada();
        }

        this.hierarquiaAdministrativa = hierarquiaAdministrativa;
    }

    public void validarVigencia(UnidadeLicitacao novaUnidade) {
        ValidacaoException ve = new ValidacaoException();

        if (this.temFimDeVigencia() && novaUnidade.getInicioVigencia().compareTo(this.getFimVigencia()) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O novo registro não poderá ser adicionado pois já existe um registro vigênte em: " + DataUtil.getDataFormatada(getInicioVigencia()));
            ve.adicionarMensagemDeOperacaoNaoPermitida("Detalhes do registro já vigente: " + getHierarquiaAdministrativa() + " - Inicio Vigência: " + DataUtil.getDataFormatada(this.getInicioVigencia()) + " - Fim Vigência: " + DataUtil.getDataFormatada(this.getFimVigencia()));
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

    @Override
    public int compareTo(UnidadeLicitacao o) {
        if (o.getInicioVigencia() != null && getInicioVigencia() != null) {
            return o.getInicioVigencia().compareTo(getInicioVigencia());
        }
        return 0;
    }
}
