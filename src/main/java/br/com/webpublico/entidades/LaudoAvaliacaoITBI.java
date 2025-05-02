package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 15/06/15
 * Time: 14:17
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
public class LaudoAvaliacaoITBI extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    private CalculoITBI calculoITBI;
    @ManyToOne
    private ParametrosFuncionarios respComissaoAvaliadora;
    @ManyToOne
    private ParametrosFuncionarios diretorChefeDeparTributo;
    @Temporal(TemporalType.DATE)
    private Date dataImpressaoLaudo;
    @Temporal(TemporalType.DATE)
    private Date dataImpressao2Via;
    @Temporal(TemporalType.DATE)
    private Date dataVencimento;
    private Boolean laudoImpresso = false;
    private Boolean laudoSegundaViaImpresso = false;
    @ManyToOne
    private UsuarioSistema usuarioImpressaoLaudo;
    @ManyToOne
    private ProcessoCalculoITBI processoCalculoITBI;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CalculoITBI getCalculoITBI() {
        return calculoITBI;
    }

    public void setCalculoITBI(CalculoITBI calculoITBI) {
        this.calculoITBI = calculoITBI;
    }

    public ParametrosFuncionarios getRespComissaoAvaliadora() {
        return respComissaoAvaliadora;
    }

    public void setRespComissaoAvaliadora(ParametrosFuncionarios respComissaoAvaliadora) {
        this.respComissaoAvaliadora = respComissaoAvaliadora;
    }

    public ParametrosFuncionarios getDiretorChefeDeparTributo() {
        return diretorChefeDeparTributo;
    }

    public void setDiretorChefeDeparTributo(ParametrosFuncionarios diretorChefeDeparTributo) {
        this.diretorChefeDeparTributo = diretorChefeDeparTributo;
    }

    public Boolean getLaudoImpresso() {
        return laudoImpresso;
    }

    public void setLaudoImpresso(Boolean laudoImpresso) {
        this.laudoImpresso = laudoImpresso;
    }

    public Date getDataImpressaoLaudo() {
        return dataImpressaoLaudo;
    }

    public void setDataImpressaoLaudo(Date dataImpressaoLaudo) {
        this.dataImpressaoLaudo = dataImpressaoLaudo;
    }

    public Date getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(Date dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public UsuarioSistema getUsuarioImpressaoLaudo() {
        return usuarioImpressaoLaudo;
    }

    public void setUsuarioImpressaoLaudo(UsuarioSistema usuarioImpressaoLaudo) {
        this.usuarioImpressaoLaudo = usuarioImpressaoLaudo;
    }

    public Boolean getLaudoSegundaViaImpresso() {
        return laudoSegundaViaImpresso != null ? laudoSegundaViaImpresso : false;
    }

    public void setLaudoSegundaViaImpresso(Boolean laudoSegundaViaImpresso) {
        this.laudoSegundaViaImpresso = laudoSegundaViaImpresso;
    }

    public Date getDataImpressao2Via() {
        return dataImpressao2Via;
    }

    public void setDataImpressao2Via(Date dataImpressao2Via) {
        this.dataImpressao2Via = dataImpressao2Via;
    }

    public ProcessoCalculoITBI getProcessoCalculoITBI() {
        return processoCalculoITBI;
    }

    public void setProcessoCalculoITBI(ProcessoCalculoITBI processoCalculoITBI) {
        this.processoCalculoITBI = processoCalculoITBI;
    }
}
