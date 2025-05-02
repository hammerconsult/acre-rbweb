package br.com.webpublico.entidades;


import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
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
@Etiqueta("Configuração de Movimentação de Bens Móveis")
public class ConfigMovimentacaoBem extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Operação")
    @Enumerated(EnumType.STRING)
    private OperacaoMovimentacaoBem operacaoMovimentacaoBem;

    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    @Temporal(TemporalType.DATE)
    @Etiqueta("Inicio de Vigência")
    private Date inicioVigencia;

    @Pesquisavel
    @Tabelavel
    @Temporal(TemporalType.DATE)
    @Etiqueta("Fim de Vigência")
    private Date fimVigencia;

    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Configurado por")
    private UsuarioSistema usuarioSistema;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Validar Movimento Retroativo")
    private Boolean validarMovimentoRetroativo;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Iniciar operação datas = data do servidor")
    private Boolean dataLancIgualDataOperacao;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Concluir operação datas <= data do servido")
    private Boolean dataLancMenorIgualDataOperacao;

    @OneToOne(cascade = CascadeType.ALL)
    private ConfigMovimentacaoBemPesquisa pesquisa;

    @OneToOne(cascade = CascadeType.ALL)
    private ConfigMovimentacaoBemBloqueio bloqueio;

    public ConfigMovimentacaoBem() {
        validarMovimentoRetroativo = Boolean.FALSE;
        dataLancIgualDataOperacao = Boolean.FALSE;
        dataLancMenorIgualDataOperacao = Boolean.FALSE;
    }

    public ConfigMovimentacaoBemPesquisa getPesquisa() {
        return pesquisa;
    }

    public void setPesquisa(ConfigMovimentacaoBemPesquisa pesquisa) {
        this.pesquisa = pesquisa;
    }

    public ConfigMovimentacaoBemBloqueio getBloqueio() {
        return bloqueio;
    }

    public void setBloqueio(ConfigMovimentacaoBemBloqueio bloqueio) {
        this.bloqueio = bloqueio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Boolean getValidarMovimentoRetroativo() {
        return validarMovimentoRetroativo == null ? Boolean.FALSE : validarMovimentoRetroativo;
    }

    public void setValidarMovimentoRetroativo(Boolean validarMovimentoRetroativo) {
        this.validarMovimentoRetroativo = validarMovimentoRetroativo;
    }

    public Boolean getDataLancIgualDataOperacao() {
        return dataLancIgualDataOperacao == null ? Boolean.FALSE : dataLancIgualDataOperacao;
    }

    public void setDataLancIgualDataOperacao(Boolean dataLancIgualDataOperacao) {
        this.dataLancIgualDataOperacao = dataLancIgualDataOperacao;
    }

    public OperacaoMovimentacaoBem getOperacaoMovimentacaoBem() {
        return operacaoMovimentacaoBem;
    }

    public void setOperacaoMovimentacaoBem(OperacaoMovimentacaoBem operacaoMovimentacaoBem) {
        this.operacaoMovimentacaoBem = operacaoMovimentacaoBem;
    }

    public Boolean getDataLancMenorIgualDataOperacao() {
        return dataLancMenorIgualDataOperacao;
    }

    public void setDataLancMenorIgualDataOperacao(Boolean dataLancMenorIgualDataOperacao) {
        this.dataLancMenorIgualDataOperacao = dataLancMenorIgualDataOperacao;
    }

    public void validarDataLancamentoDiferenteDataOperacao(Date dataLancemnto) {
        ValidacaoException ve = new ValidacaoException();
        dataLancemnto = DataUtil.dataSemHorario(dataLancemnto);
        Date dataServidor = DataUtil.dataSemHorario(new Date());
        if (getDataLancIgualDataOperacao() && dataLancemnto.compareTo(dataServidor) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Esta operação não pode ser realizada, pois a data de operação e a data de lançamento devem ser iguais " +
                "à data do servidor do sistema: " + DataUtil.getDataFormatada(dataServidor));
        }
        ve.lancarException();
    }

    public void validarDataLancamentoMenorIgualDataOperacao(Date dataLancemnto, Date dataOperacao) {
        ValidacaoException ve = new ValidacaoException();
        dataLancemnto = DataUtil.dataSemHorario(dataLancemnto);
        dataOperacao = DataUtil.dataSemHorario(dataOperacao);
        Date dataServidor = DataUtil.dataSemHorario(new Date());
        if (getDataLancMenorIgualDataOperacao()
            && (dataLancemnto.compareTo(dataServidor) > 0 || dataOperacao.compareTo(dataServidor) > 0)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Esta operação não pode ser realizada, pois a data de operação e a data de lançamento devem ser menores ou iguais " +
                "à data do servidor do sistema: " + DataUtil.getDataFormatada(dataServidor));
        }
        ve.lancarException();
    }

    public void validarDatasMovimentacao(Date dataLancemnto, Date dataOperacao, Operacoes operacao) {
        ValidacaoException ve = new ValidacaoException();
        dataLancemnto = DataUtil.dataSemHorario(dataLancemnto);
        dataOperacao = DataUtil.dataSemHorario(dataOperacao);
        if (Operacoes.NOVO.equals(operacao)) {
            validarDataLancamentoDiferenteDataOperacao(dataLancemnto);
        } else {
            validarDataLancamentoMenorIgualDataOperacao(dataLancemnto, dataOperacao);
        }
        ve.lancarException();
    }
}
