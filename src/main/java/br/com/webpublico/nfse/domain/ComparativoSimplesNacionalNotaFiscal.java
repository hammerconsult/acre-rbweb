package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.DetentorArquivoComposicao;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class ComparativoSimplesNacionalNotaFiscal extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    @Enumerated(EnumType.STRING)
    private Mes mesInicial;
    private Integer anoInicial;
    @Enumerated(EnumType.STRING)
    private Mes mesFinal;
    private Integer anoFinal;
    @ManyToOne
    private CadastroEconomico cadastroEconomico;
    @OneToOne(cascade = CascadeType.ALL)
    private Arquivo arquivo;


    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Mes getMesInicial() {
        return mesInicial;
    }

    public void setMesInicial(Mes mesInicial) {
        this.mesInicial = mesInicial;
    }

    public Integer getAnoInicial() {
        return anoInicial;
    }

    public void setAnoInicial(Integer anoInicial) {
        this.anoInicial = anoInicial;
    }

    public Mes getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(Mes mesFinal) {
        this.mesFinal = mesFinal;
    }

    public Integer getAnoFinal() {
        return anoFinal;
    }

    public void setAnoFinal(Integer anoFinal) {
        this.anoFinal = anoFinal;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    @Override
    public void validarCamposObrigatorios() {
        super.validarCamposObrigatorios();
        ValidacaoException ve = new ValidacaoException();
        if (mesInicial == null || anoInicial == null ||
            mesFinal == null || anoFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Competência deve ser informado.");
        }
        ve.lancarException();
    }
}
