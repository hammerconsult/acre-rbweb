package br.com.webpublico.entidades;

import br.com.webpublico.entidades.administrativo.obra.ResponsavelTecnico;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.EntidadeDetendorDocumentoLicitacao;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * @author venom
 */
@Entity
@Audited
@Etiqueta("Fiscal")
public class ResponsavelTecnicoFiscal extends SuperEntidade implements ValidadorEntidade, EntidadeDetendorDocumentoLicitacao {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Tipo do Responsável")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private TipoResponsavelFiscal tipoResponsavel;

    @Etiqueta(" Tipo Responsavel Técnico ")
    @Pesquisavel
    @Tabelavel
    @Enumerated(EnumType.STRING)
    private TipoResponsavelTecnico tipoResponsavelTecnico;

    @Etiqueta("Tipo Contratante")
    @Enumerated(EnumType.STRING)
    private TipoContratante tipoContratante;

    @Etiqueta("Tipo Fiscal")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private TipoFiscalContrato tipoFiscal;

    @Etiqueta("Contrato")
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    private Contrato contrato;
    /**
     * campo destinado a informar o responsavel tecnico
     *
     * @see {@link #tipoResponsavelTecnico}
     */
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Etiqueta(" Responsável Técnico ")
    private ResponsavelTecnico responsavelTecnico;

    @Etiqueta("Servidor")
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    private ContratoFP contratoFP;

    @Etiqueta("CREA/CAU")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    private String creaCau;

    @Etiqueta("Especialidade")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @ManyToOne
    private ProfissionalConfea profissionalConfea;

    @Etiqueta("Atribuição")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private ObraAtribuicao atribuicao;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private DetentorDocumentoLicitacao detentorDocumentoLicitacao;

    public ResponsavelTecnicoFiscal() {
        super();
        this.tipoResponsavel = TipoResponsavelFiscal.FISCAL;
        this.tipoFiscal = TipoFiscalContrato.INTERNO;
        this.responsavelTecnico = new ResponsavelTecnico();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoResponsavelFiscal getTipoResponsavel() {
        return tipoResponsavel;
    }

    public void setTipoResponsavel(TipoResponsavelFiscal tipoResponsavel) {
        this.tipoResponsavel = tipoResponsavel;
    }

    public TipoFiscalContrato getTipoFiscal() {
        return tipoFiscal;
    }

    public void setTipoFiscal(TipoFiscalContrato tipoFiscal) {
        this.tipoFiscal = tipoFiscal;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public String getCreaCau() {
        return creaCau;
    }

    public void setCreaCau(String creaCau) {
        this.creaCau = creaCau;
    }

    public ProfissionalConfea getProfissionalConfea() {
        return profissionalConfea;
    }

    public void setProfissionalConfea(ProfissionalConfea profissionalConfea) {
        this.profissionalConfea = profissionalConfea;
    }

    public ObraAtribuicao getAtribuicao() {
        return atribuicao;
    }

    public void setAtribuicao(ObraAtribuicao atribuicao) {
        this.atribuicao = atribuicao;
    }

    public Boolean isTipoInterno() {
        return TipoFiscalContrato.INTERNO.equals(this.tipoFiscal);
    }

    public Boolean isTipoExterno() {
        return TipoFiscalContrato.EXTERNO.equals(this.tipoFiscal);
    }

    public TipoResponsavelTecnico getTipoResponsavelTecnico() {
        return tipoResponsavelTecnico;
    }

    public void setTipoResponsavelTecnico(TipoResponsavelTecnico tipoResponsavelTecnico) {
        this.tipoResponsavelTecnico = tipoResponsavelTecnico;
    }

    public ResponsavelTecnico getResponsavelTecnico() {
        return responsavelTecnico;
    }

    public void setResponsavelTecnico(ResponsavelTecnico responsavelTecnico) {
        this.responsavelTecnico = responsavelTecnico;
    }

    public TipoContratante getTipoContratante() {
        return tipoContratante;
    }

    public void setTipoContratante(TipoContratante tipoContratante) {
        this.tipoContratante = tipoContratante;
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();
        if (TipoFiscalContrato.EXTERNO.equals(this.tipoFiscal)) {
            if (this.contrato == null) {
                ve.adicionarMensagemError(SummaryMessages.CAMPO_OBRIGATORIO, "Informe o contrato.");
            }
        }
        if (TipoFiscalContrato.INTERNO.equals(this.tipoFiscal)) {
            if (this.contratoFP == null) {
                ve.adicionarMensagemError(SummaryMessages.CAMPO_OBRIGATORIO, "Informe o servidor.");
            }
        }
        ve.lancarException();
    }

    @Override
    public DetentorDocumentoLicitacao getDetentorDocumentoLicitacao() {
        return detentorDocumentoLicitacao;
    }

    @Override
    public void setDetentorDocumentoLicitacao(DetentorDocumentoLicitacao detentorDocumentoLicitacao) {
        this.detentorDocumentoLicitacao = detentorDocumentoLicitacao;
    }

    @Override
    public TipoMovimentoProcessoLicitatorio getTipoAnexo() {
        return TipoMovimentoProcessoLicitatorio.FISCAL;
    }

    @Override
    public String toString() {
        String retorno = "";
        if (contrato != null) {
            retorno = contrato.toString();
        }
        if (contratoFP != null) {
            retorno = contratoFP.toString();
        }

        return retorno + " Tipo: " + tipoResponsavel.toString() + " " + tipoFiscal.toString();
    }

    public String getResponsavel() {
        String retorno = "";
        if (TipoFiscalContrato.INTERNO.equals(this.tipoFiscal)) {
            if (this.contratoFP != null) {
                return this.contratoFP.toString();
            }
        } else {
            if (this.contrato != null) {
                return this.contrato.getContratado().toString();
            }
        }
        return retorno;
    }

    public String getResponsavelTecnicoPorTipoResponsavel() {
        String responsavel = " ";
        if (TipoResponsavelTecnico.CONTRATADO.equals(this.tipoResponsavelTecnico)) {
            if (this.getResponsavelTecnico().getPessoaFisica() != null) {
                responsavel = this.getResponsavelTecnico().getPessoaFisica().toString();
            }
            responsavel = responsavel == null ? " " : responsavel;
            return responsavel;
        }

        if (!TipoResponsavelTecnico.CONTRATADO.equals(this.tipoResponsavelTecnico)) {
            if (this.getContratoFP() != null) {
                responsavel = this.getContratoFP().getMatriculaFP().getPessoa().toString();
            }
            return responsavel == null ? " " : responsavel;
        }

        return responsavel;
    }

    public void setNullPorTipo() {
        if (TipoFiscalContrato.INTERNO.equals(tipoFiscal)) {
            setContrato(null);
        } else {
            setContratoFP(null);
        }
    }
}
