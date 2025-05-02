package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.ContaReceita;
import br.com.webpublico.entidades.FonteDeRecursos;
import br.com.webpublico.entidades.Tributo;

import java.math.BigDecimal;
import java.util.Date;


public class MapaArrecadacao implements Comparable<MapaArrecadacao> {

    private Tributo tributo;
    private String fonteDeRecursos;
    private ContaReceita conta;
    private BigDecimal valor;
    private Date pagamento;
    private String agenteArrecadador;

    public MapaArrecadacao() {
    }

    public Tributo getTributo() {
        return tributo;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
    }

    public ContaReceita getConta() {
        return conta;
    }

    public void setConta(ContaReceita conta) {
        this.conta = conta;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Date getPagamento() {
        return pagamento;
    }

    public void setPagamento(Date pagamento) {
        this.pagamento = pagamento;
    }

    public String getAgenteArrecadador() {
        return agenteArrecadador;
    }

    public void setAgenteArrecadador(String agenteArrecadador) {
        this.agenteArrecadador = agenteArrecadador;
    }

//    @Override
//    public boolean equals(Object obj) {
//        if (obj instanceof MapaArrecadacao) {
//            MapaArrecadacao mapa = (MapaArrecadacao) obj;
//            if (mapa.tributo.getId().compareTo(tributo.getId()) == 0) {
//                if (mapa.conta == null && conta == null) {
//                    return true;
//                } else {
//                    if ((mapa.conta == null && conta != null) || (mapa.conta != null && conta == null)) {
//                        return false;
//                    } else {
//                        if (mapa.conta.getId().compareTo(conta.getId()) == 0) {
//                            if (mapa.pagamento == null && pagamento == null) {
//                                return true;
//                            } else if ((mapa.pagamento == null && pagamento != null) || (mapa.pagamento != null && pagamento == null)) {
//                                return false;
//                            } else {
//                                if (mapa.pagamento.compareTo(pagamento) == 0) {
//                                    if (mapa.agenteArrecadador == null && agenteArrecadador == null) {
//                                        return true;
//                                    } else if ((mapa.agenteArrecadador == null && agenteArrecadador != null) || (mapa.agenteArrecadador != null && agenteArrecadador == null)) {
//                                        return false;
//                                    } else {
//                                        return (mapa.agenteArrecadador.compareTo(agenteArrecadador) == 0);
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return false;
//    }

    public String getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(String fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapaArrecadacao that = (MapaArrecadacao) o;

        if (tributo != null && tributo.getId() != null ? !tributo.getId().equals(that.tributo.getId()) : that.tributo != null && that.tributo.getId() != null) return false;
        if (fonteDeRecursos != null ? !fonteDeRecursos.equals(that.fonteDeRecursos) : that.fonteDeRecursos != null)
            return false;
        return !(conta != null && conta.getId() != null ? !conta.getId().equals(that.conta.getId()) : that.conta != null && that.conta.getId() != null);
    }

    @Override
    public int hashCode() {
        int result = tributo != null && tributo.getId() != null ? tributo.getId().hashCode() : 0;
        result = 31 * result + (fonteDeRecursos != null ? fonteDeRecursos.hashCode() : 0);
        result = 31 * result + (conta != null && conta.getId() != null ? conta.getId().hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(MapaArrecadacao o) {
        if (pagamento != null && o.getPagamento() != null) {
            int i = pagamento.compareTo(o.pagamento);
            if (i > 0) {
                return 1;
            }
            if (i < 0) {
                return -1;
            }
        }

        return tributo.getDescricao().compareTo(o.getTributo().getDescricao());
    }
}
