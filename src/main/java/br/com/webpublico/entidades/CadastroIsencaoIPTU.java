package br.com.webpublico.entidades;

import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Etiqueta("Solicitação de Processo de Isenção")
public class CadastroIsencaoIPTU extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ProcessoIsencaoIPTU processo;
    @ManyToOne
    private CadastroImobiliario cadastro;
    private Boolean enquadra;

    @Pesquisavel
    @Etiqueta("Código")
    @Transient
    @Tabelavel
    private Long codigoProcesso;
    @Pesquisavel
    @Etiqueta("Exercício de Referência")
    @Transient
    @Tabelavel
    private Exercicio exercicioProcesso;
    @Etiqueta("Início de vigência")
    @Transient
    @Tabelavel
    private String inicioVigenciaString;
    @Etiqueta("Fim de vigência")
    @Transient
    @Tabelavel
    private String fimVigenciaString;
    @Pesquisavel
    @Etiqueta("Protocolo")
    @Transient
    @Tabelavel
    private String protocoloProcesso;
    @Pesquisavel
    @Etiqueta("Situação do Processo")
    @Transient
    @Tabelavel
    private ProcessoIsencaoIPTU.Situacao situacaoProcesso;
    @Tabelavel
    @Etiqueta("Cadastro Imobiliário")
    @Transient
    private String cadastroImobiliarioString;
    @Tabelavel
    @Etiqueta("Categoria")
    @Transient
    private String categoriaString;
    @Tabelavel
    @Etiqueta("Tipo de Lançamento")
    @Transient
    private String tipoLancamentoString;

    public CadastroIsencaoIPTU(CadastroIsencaoIPTU obj) {
        this.id = obj.getId();
        this.codigoProcesso = obj.getProcesso().getNumero();
        this.exercicioProcesso = obj.getProcesso().getExercicioReferencia();
        this.protocoloProcesso = obj.getProcesso().getNumeroProtocolo() + "/" + obj.getProcesso().getAnoProtocoloString();
        this.situacaoProcesso = obj.getProcesso().getSituacao();
        this.cadastro = obj.getCadastro();
        this.cadastroImobiliarioString = obj.getCadastro().toString();
        if (!obj.getCadastro().getPropriedadeVigente().isEmpty()) {
            this.cadastroImobiliarioString += " - " + obj.getCadastro().getPropriedadeVigente().get(0).getPessoa().getNomeCpfCnpj();
        }
        this.categoriaString = obj.getProcesso().getCategoriaIsencaoIPTU().toString();
        this.tipoLancamentoString = obj.getProcesso().getCategoriaIsencaoIPTU().getTipoLancamentoIsencaoIPTU().getDescricao();
        if (obj.getProcesso().getDataLancamento() != null) {
            this.inicioVigenciaString = DataUtil.getDataFormatada(obj.getProcesso().getDataLancamento());
        } else {
            this.inicioVigenciaString = "";
        }
        if (obj.getProcesso().getValidade() != null) {
            this.fimVigenciaString = DataUtil.getDataFormatada(obj.getProcesso().getValidade());
        } else {
            this.fimVigenciaString = "";
        }
    }

    public CadastroIsencaoIPTU() {
    }

    public ProcessoIsencaoIPTU getProcesso() {
        return processo;
    }

    public void setProcesso(ProcessoIsencaoIPTU processo) {
        this.processo = processo;
    }

    public CadastroImobiliario getCadastro() {
        return cadastro;
    }

    public void setCadastro(CadastroImobiliario cadastro) {
        this.cadastro = cadastro;
    }

    public Boolean getEnquadra() {
        return enquadra;
    }

    public void setEnquadra(Boolean enquadra) {
        this.enquadra = enquadra;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Long getCodigoProcesso() {
        return codigoProcesso;
    }

    public void setCodigoProcesso(Long codigoProcesso) {
        this.codigoProcesso = codigoProcesso;
    }

    public Exercicio getExercicioProcesso() {
        return exercicioProcesso;
    }

    public void setExercicioProcesso(Exercicio exercicioProcesso) {
        this.exercicioProcesso = exercicioProcesso;
    }

    public String getInicioVigenciaString() {
        return inicioVigenciaString;
    }

    public void setInicioVigenciaString(String inicioVigenciaString) {
        this.inicioVigenciaString = inicioVigenciaString;
    }

    public String getFimVigenciaString() {
        return fimVigenciaString;
    }

    public void setFimVigenciaString(String fimVigenciaString) {
        this.fimVigenciaString = fimVigenciaString;
    }

    public String getProtocoloProcesso() {
        return protocoloProcesso;
    }

    public void setProtocoloProcesso(String protocoloProcesso) {
        this.protocoloProcesso = protocoloProcesso;
    }

    public ProcessoIsencaoIPTU.Situacao getSituacaoProcesso() {
        return situacaoProcesso;
    }

    public void setSituacaoProcesso(ProcessoIsencaoIPTU.Situacao situacaoProcesso) {
        this.situacaoProcesso = situacaoProcesso;
    }

    public String getCadastroImobiliarioString() {
        return cadastroImobiliarioString;
    }

    public void setCadastroImobiliarioString(String cadastroImobiliarioString) {
        this.cadastroImobiliarioString = cadastroImobiliarioString;
    }

    public String getCategoriaString() {
        return categoriaString;
    }

    public void setCategoriaString(String categoriaString) {
        this.categoriaString = categoriaString;
    }

    public String getTipoLancamentoString() {
        return tipoLancamentoString;
    }

    public void setTipoLancamentoString(String tipoLancamentoString) {
        this.tipoLancamentoString = tipoLancamentoString;
    }
}
