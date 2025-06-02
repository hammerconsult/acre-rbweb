package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.VinculoFP;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Author peixe on 25/11/2015  13:49.
 */
public class PensaoJudicialRetidoFonte implements Serializable {
    private String tipo;
    private String cpf;
    private String descricao;
    private Date nascimento;
    private PessoaFisica pessoaFisica;
    private VinculoFP instituidor;
    private BigDecimal valor = BigDecimal.ZERO;
    private List<EventoFP> eventosFps;

    public PensaoJudicialRetidoFonte() {
    this.eventosFps = Lists.newArrayList();
    }


    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public VinculoFP getInstituidor() {
        return instituidor;
    }

    public void setInstituidor(VinculoFP instituidor) {
        this.instituidor = instituidor;
    }

    public List<EventoFP> getEventosFps() {
        return eventosFps;
    }

    public void setEventosFps(List<EventoFP> eventosFps) {
        this.eventosFps = eventosFps;
    }

    public Date getNascimento() {
        return nascimento;
    }

    public void setNascimento(Date nascimento) {
        this.nascimento = nascimento;
    }

    public void adicionarEvento(EventoFP evento) {
        if (this.getEventosFps() == null) {
            this.eventosFps = Lists.newArrayList();
        }

        if (!this.getEventosFps().contains(evento)) {
            this.getEventosFps().add(evento);
        }
    }

    public void adicionarEventos(List<EventoFP> eventos) {
        List<EventoFP> filhos = Lists.newLinkedList();
        if (eventos == null || eventos.isEmpty()) {
            return;
        }

        if (!this.getEventosFps().isEmpty()) {
            for (EventoFP selecionado : this.eventosFps) {
                for (EventoFP filho : eventos) {
                    if (!selecionado.equals(filho)) {
                        filhos.add(filho);
                    }
                }
            }
        }
        if (!filhos.isEmpty()) {
            this.getEventosFps().addAll(filhos);
        }

    }

    public Boolean hasEventoFP(List<EventoFP> eventos) {
        if (eventos == null || eventos.isEmpty()) {
            return false;
        }
        if (!this.getEventosFps().isEmpty()) {
            for (EventoFP selecionado : this.eventosFps) {
                for (EventoFP filho : eventos) {
                    if (!selecionado.equals(filho)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return descricao + " - " + valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PensaoJudicialRetidoFonte that = (PensaoJudicialRetidoFonte) o;

        if (pessoaFisica != null ? !pessoaFisica.equals(that.pessoaFisica) : that.pessoaFisica != null) return false;
        return eventosFps != null ? eventosFps.equals(that.eventosFps) : that.eventosFps == null;

    }

    @Override
    public int hashCode() {
        int result = pessoaFisica != null ? pessoaFisica.hashCode() : 0;
        result = 31 * result + (eventosFps != null ? eventosFps.hashCode() : 0);
        return result;
    }
}
