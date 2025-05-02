package br.com.webpublico.entidades;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
@Etiqueta("Unidade Objeto Frota")
public class UnidadeObjetoFrota extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Objeto Frota")
    private ObjetoFrota objetoFrota;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Unidade Organizacional")
    private UnidadeOrganizacional unidadeOrganizacional;

    @Obrigatorio
    @Temporal(TemporalType.DATE)
    @Etiqueta("Início de Vigência")
    private Date inicioVigencia;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Fim de Vigência")
    private Date fimVigencia;

    @Transient
    @Obrigatorio
    @Etiqueta("Unidade Organizacional")
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    public UnidadeObjetoFrota() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ObjetoFrota getObjetoFrota() {
        return objetoFrota;
    }

    public void setObjetoFrota(ObjetoFrota objetoFrota) {
        this.objetoFrota = objetoFrota;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        if (hierarquiaOrganizacional != null) {
            this.unidadeOrganizacional = hierarquiaOrganizacional.getSubordinada();
        }
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
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

    public void validarVigencia(UnidadeObjetoFrota novaUnidade) {
        ValidacaoException ve = new ValidacaoException();

        if (this.temFimDeVigencia() && novaUnidade.getInicioVigencia().compareTo(this.getFimVigencia()) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O novo registro não poderá ser adicionado pois já existe um registro vigênte em: " + DataUtil.getDataFormatada(novaUnidade.getInicioVigencia()));
            ve.adicionarMensagemDeOperacaoNaoPermitida("Detalhes do registro já vigente: " + this.getHierarquiaOrganizacional() + " - Inicio Vigência: " + DataUtil.getDataFormatada(this.getInicioVigencia()) + " - Fim Vigência: " + DataUtil.getDataFormatada(this.getFimVigencia()));
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
