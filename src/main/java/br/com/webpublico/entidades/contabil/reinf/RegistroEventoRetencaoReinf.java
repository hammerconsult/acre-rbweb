package br.com.webpublico.entidades.contabil.reinf;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.esocial.domain.OcorrenciaESocial;
import br.com.webpublico.esocial.enums.SituacaoESocial;
import br.com.webpublico.reinf.eventos.EventoReinfDTO;
import br.com.webpublico.reinf.eventos.TipoArquivoReinf;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.util.Strings;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
public class RegistroEventoRetencaoReinf extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Pessoa pessoa;
    @OneToMany(mappedBy = "registro", cascade = CascadeType.ALL)
    private List<PagamentoReinf> pagamentos;
    @OneToMany(mappedBy = "registro", cascade = CascadeType.ALL)
    private List<ReceitaExtraReinf> receitas;
    @OneToMany(mappedBy = "registro", cascade = CascadeType.ALL)
    private List<NotaReinf> notas;
    @Temporal(TemporalType.DATE)
    private Date data;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @Enumerated(EnumType.STRING)
    private Mes mes;
    @ManyToOne
    private Exercicio exercicio;
    @Enumerated(EnumType.STRING)
    private TipoArquivoReinf tipoArquivo;
    private BigDecimal valorTotalBruto;
    private BigDecimal valorTotalRetencao;
    private BigDecimal valorTotalLiquido;
    private BigDecimal valorTotalBaseCalculo;
    private String mensagem;

    private Boolean retificacao;
    private String numeroReciboOrigem;
    @ManyToOne
    private ConfiguracaoEmpregadorESocial empregador;
    @OneToMany(mappedBy = "registro", cascade = CascadeType.ALL)
    private List<EventoRegistroReinfRetencao> eventos;
    @Transient
    private Boolean marcarPraEnviar;
    @Transient
    private EventoReinfDTO eventoAtual;

    public RegistroEventoRetencaoReinf() {
        dataRegistro = new Date();
        receitas = Lists.newArrayList();
        pagamentos = Lists.newArrayList();
        notas = Lists.newArrayList();
        valorTotalBruto = BigDecimal.ZERO;
        valorTotalRetencao = BigDecimal.ZERO;
        valorTotalLiquido = BigDecimal.ZERO;
        valorTotalBaseCalculo = BigDecimal.ZERO;
        mensagem = "";
        marcarPraEnviar = Boolean.TRUE;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public List<PagamentoReinf> getPagamentos() {
        return pagamentos;
    }

    public void setPagamentos(List<PagamentoReinf> pagamentos) {
        this.pagamentos = pagamentos;
    }

    public List<ReceitaExtraReinf> getReceitas() {
        return receitas;
    }

    public void setReceitas(List<ReceitaExtraReinf> receitas) {
        this.receitas = receitas;
    }

    public List<NotaReinf> getNotas() {
        return notas;
    }

    public void setNotas(List<NotaReinf> notas) {
        this.notas = notas;
    }

    public BigDecimal getValorTotalBruto() {
        return valorTotalBruto;
    }

    public void setValorTotalBruto(BigDecimal valorTotalBruto) {
        this.valorTotalBruto = valorTotalBruto;
    }

    public BigDecimal getValorTotalBaseCalculo() {
        return valorTotalBaseCalculo;
    }

    public void setValorTotalBaseCalculo(BigDecimal valorTotalBaseCalculo) {
        this.valorTotalBaseCalculo = valorTotalBaseCalculo;
    }

    public BigDecimal getValorTotalRetencao() {
        return valorTotalRetencao;
    }

    public void setValorTotalRetencao(BigDecimal valorTotalRetencao) {
        this.valorTotalRetencao = valorTotalRetencao;
    }

    public BigDecimal getValorTotalLiquido() {
        return valorTotalLiquido;
    }

    public void setValorTotalLiquido(BigDecimal valorTotalLiquido) {
        this.valorTotalLiquido = valorTotalLiquido;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public TipoArquivoReinf getTipoArquivo() {
        return tipoArquivo;
    }

    public void setTipoArquivo(TipoArquivoReinf tipoArquivo) {
        this.tipoArquivo = tipoArquivo;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Boolean getRetificacao() {
        if (retificacao == null) {
            retificacao = Boolean.FALSE;
        }
        return retificacao;
    }

    public void setRetificacao(Boolean retificacao) {
        this.retificacao = retificacao;
    }

    public String getNumeroReciboOrigem() {
        return numeroReciboOrigem;
    }

    public void setNumeroReciboOrigem(String numeroReciboOrigem) {
        this.numeroReciboOrigem = numeroReciboOrigem;
    }

    public ConfiguracaoEmpregadorESocial getEmpregador() {
        return empregador;
    }

    public void setEmpregador(ConfiguracaoEmpregadorESocial empregador) {
        this.empregador = empregador;
    }

    public List<EventoRegistroReinfRetencao> getEventos() {
        return eventos;
    }

    public void setEventos(List<EventoRegistroReinfRetencao> eventos) {
        this.eventos = eventos;
    }

    public EventoReinfDTO getEventoAtual() {
        return eventoAtual;
    }

    public void setEventoAtual(EventoReinfDTO eventoAtual) {
        this.eventoAtual = eventoAtual;
    }

    public Boolean getMarcarPraEnviar() {
        if (marcarPraEnviar == null) {
            marcarPraEnviar = Boolean.FALSE;
        }
        return marcarPraEnviar;
    }

    public void setMarcarPraEnviar(Boolean marcarPraEnviar) {
        this.marcarPraEnviar = marcarPraEnviar;
    }

    public Boolean isValido() {
        return Strings.isEmpty(this.getMensagem()) && this.getValorTotalLiquido().compareTo(BigDecimal.ZERO) != 0;
    }

    public Boolean isValidoTodasNotas() {
        Boolean valido = Boolean.TRUE;
        for (NotaReinf nota : getNotas()) {
            if (!nota.isValido()) {
                valido = Boolean.FALSE;
            }
        }
        return valido;
    }

    public String getStringMensagemNotasInvalidas() {
        return "Existe(m) notas com percentual maior que 11%.";
    }

    public String getStyleTitulo() {
        if (id != null) {
            if (eventoAtual != null) {
                if (eventoAtual.getLoteESocial() != null &&
                    SituacaoESocial.REJEITADO.equals(eventoAtual.getLoteESocial().getSituacao())) {
                    return "vermelhonegrito";
                }
                if (SituacaoESocial.PROCESSADO_COM_SUCESSO.equals(eventoAtual.getSituacao())) {
                    return "verdenegrito";
                }
                if (SituacaoESocial.PROCESSADO_COM_ERRO.equals(eventoAtual.getSituacao())) {
                    return "laranaescuro";
                }
                return "amarelonegrito";
            }
            return "verdenegrito";
        }
        if (!this.isValido()) {
            return "vermelhonegrito";
        }
        return "preformatted-fonte-padrao";
    }

    public Boolean isDesabilitaEnviar() {
        if (id == null && this.isValido()) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    public String getStyleMensagem() {
        if (id != null) {
            if (eventoAtual != null) {
                if (SituacaoESocial.PROCESSADO_COM_SUCESSO.equals(eventoAtual.getSituacao())) {
                    return "alert alert-success mbot02";
                }
                if (SituacaoESocial.PROCESSADO_COM_ERRO.equals(eventoAtual.getSituacao()) ||
                    (eventoAtual.getLoteESocial() != null &&
                        SituacaoESocial.REJEITADO.equals(eventoAtual.getLoteESocial().getSituacao()))) {
                    return "alert alert-danger mbot02";
                }
                return "alert alert-warning mbot02";
            }
            return "alert alert-success mbot02";
        }
        if (!this.isValido()) {
            return "alert alert-danger mbot02";
        }
        return "";
    }

    public String getTituloMensagem() {
        if (id != null) {
            if (eventoAtual != null && SituacaoESocial.PROCESSADO_COM_ERRO.equals(eventoAtual.getSituacao()) ||
                (eventoAtual != null && eventoAtual.getLoteESocial() != null &&
                    SituacaoESocial.REJEITADO.equals(eventoAtual.getLoteESocial().getSituacao()))) {
                return "Erro";
            }
            return "Informação";
        }
        if (!this.isValido()) {
            return "Erro";
        }
        return "Informação";
    }

    public BigDecimal getValorRetencao(LiquidacaoDoctoFiscal nota) {
        BigDecimal totalDeReceita = BigDecimal.ZERO;
        for (ReceitaExtraReinf receita : this.getReceitas()) {
            if (receita.getReceitaExtra().getRetencaoPgto().getPagamento().getLiquidacao().getId().equals(nota.getLiquidacao().getId())) {
                totalDeReceita = totalDeReceita.add(receita.getReceitaExtra().getValor());
            }
        }
        return totalDeReceita;
    }

    public void calcularValores() {
        valorTotalBruto = BigDecimal.ZERO;
        valorTotalRetencao = BigDecimal.ZERO;
        valorTotalLiquido = BigDecimal.ZERO;
        valorTotalBaseCalculo = BigDecimal.ZERO;
        for (ReceitaExtraReinf receita : this.getReceitas()) {
            Pagamento pagamento = receita.getReceitaExtra().getRetencaoPgto().getPagamento();
            Boolean encontrou = Boolean.FALSE;
            for (PagamentoReinf registroPagamento : this.getPagamentos()) {
                if (pagamento.getId().equals(registroPagamento.getPagamento().getId())) {
                    encontrou = Boolean.TRUE;
                }
            }

            if (!encontrou) {
                this.getPagamentos().add(new PagamentoReinf(pagamento, this));
            }
        }
        for (NotaReinf nota : this.getNotas()) {
            this.setValorTotalBruto(this.getValorTotalBruto().add(nota.getNota().getValorLiquidado()));
            this.setValorTotalBaseCalculo(this.getValorTotalBaseCalculo().add(TipoArquivoReinf.R4020.equals(this.getTipoArquivo()) ? nota.getNota().getValorBaseCalculoIrrf() : nota.getNota().getValorBaseCalculo()));
            nota.setValorRetido(this.getValorRetencao(nota.getNota()));
            if (!nota.isValido()) {
                this.setMarcarPraEnviar(Boolean.FALSE);
                this.setMensagem(this.getMensagem() + "<br> </br> ");
            }
        }
        for (ReceitaExtraReinf receita : this.getReceitas()) {
            this.setValorTotalRetencao(this.getValorTotalRetencao().add(receita.getReceitaExtra().getValor()));
        }
        this.setValorTotalLiquido(this.getValorTotalBruto().subtract(this.getValorTotalRetencao()));
    }

    public void calcularValoresSemNota() {
        valorTotalBruto = BigDecimal.ZERO;
        valorTotalRetencao = BigDecimal.ZERO;
        valorTotalLiquido = BigDecimal.ZERO;
        valorTotalBaseCalculo = BigDecimal.ZERO;
        for (ReceitaExtraReinf receita : this.getReceitas()) {
            Pagamento pagamento = receita.getReceitaExtra().getRetencaoPgto().getPagamento();
            boolean encontrou = false;
            for (PagamentoReinf registroPagamento : this.getPagamentos()) {
                if (pagamento.getId().equals(registroPagamento.getPagamento().getId())) {
                    encontrou = true;
                    break;
                }
            }

            if (!encontrou) {
                this.getPagamentos().add(new PagamentoReinf(pagamento, this));
            }
        }
        for (ReceitaExtraReinf receita : this.getReceitas()) {
            this.setValorTotalRetencao(this.getValorTotalRetencao().add(receita.getReceitaExtra().getValor()));
        }
        for (PagamentoReinf pagamento : this.getPagamentos()) {
            this.setValorTotalBruto(this.getValorTotalBruto().add(pagamento.getPagamento().getValor()));
        }
        this.setValorTotalLiquido(this.getValorTotalBruto().subtract(this.getValorTotalRetencao()));
    }

    public void atualizarMensagemComEventoAtual() {
        mensagem = getMensagemAtualizada();
    }

    public String getMensagemAtualizada() {
        if (id != null && eventoAtual != null) {
            if (eventoAtual.getLoteESocial() != null &&
                SituacaoESocial.REJEITADO.equals(eventoAtual.getLoteESocial().getSituacao())) {
                return eventoAtual.getLoteESocial().getSituacao().getDescricao() + ": " + eventoAtual.getLoteESocial().getCodigoResposta() + " - " + eventoAtual.getLoteESocial().getDescricaoResposta();
            }
            if (SituacaoESocial.PROCESSADO_COM_ERRO.equals(eventoAtual.getSituacao())) {
                String mensagemAtualizada = "";
                int contador = 1;
                for (OcorrenciaESocial ocorrencia : eventoAtual.getOcorrencias()) {
                    mensagemAtualizada += (contador > 1) ? " </br>" : "";
                    mensagemAtualizada += ocorrencia.getCodigo() + " - " + ocorrencia.getDescricao();
                    contador++;
                }
                return mensagemAtualizada;
            }
            if (SituacaoESocial.PRONTO_PARA_ENVIO.equals(eventoAtual.getSituacao())) {
                return "Aguardando para ser enviado aos servidores da REINF.";
            }
            if (SituacaoESocial.ENVIADO_AGUARDANDO_PROCESSAMENTO.equals(eventoAtual.getSituacao())) {
                return "Enviado e aguardando processamento.";
            }
            if (eventoAtual.getSituacao() != null) {
                return eventoAtual.getSituacao().getDescricao();
            }
        }
        return mensagem;
    }
}
