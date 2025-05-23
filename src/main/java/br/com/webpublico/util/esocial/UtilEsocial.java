package br.com.webpublico.util.esocial;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.HistoricoEnvioEsocial;
import br.com.webpublico.entidades.rh.esocial.ItemHistoricoEnvioEsocial;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.rh.esocial.TipoClasseESocial;
import br.com.webpublico.esocial.enums.ClasseWP;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

public class UtilEsocial {

    public static HistoricoEnvioEsocial criarHistoricoEsocial(ConfiguracaoEmpregadorESocial configuracaoEmpregadorESocial,
                                                              Date dataEnvio, UsuarioSistema usuario,
                                                              TipoClasseESocial tipoClasseESocial,
                                                              Mes mes, Exercicio exercicio, String motivo) {

        HistoricoEnvioEsocial historico = new HistoricoEnvioEsocial();
        historico.setEmpregador(configuracaoEmpregadorESocial);
        historico.setDataEnvio(dataEnvio);
        historico.setUsuarioSistema(usuario);
        historico.setTipoClasseESocial(tipoClasseESocial);
        historico.setMes(mes);
        historico.setExercicio(exercicio);
        historico.setMotivo(motivo);
        return historico;
    }

    public static ItemHistoricoEnvioEsocial criarItemHistoricoEnvioEsocial(HistoricoEnvioEsocial historico, String descricao,
                                                                           String identificador, ClasseWP classeWP) {
        ItemHistoricoEnvioEsocial item = new ItemHistoricoEnvioEsocial();
        item.setHistoricoEnvioEsocial(historico);
        item.setDescricao(descricao);
        item.setIdentificador(identificador);
        item.setClasseWP(classeWP);
        return item;
    }

    public static Boolean isEventoPagamento(TipoClasseESocial tipoClasseESocial) {
        return tipoClasseESocial.equals(TipoClasseESocial.S1200) || tipoClasseESocial.equals(TipoClasseESocial.S1202)
            || tipoClasseESocial.equals(TipoClasseESocial.S1207) || tipoClasseESocial.equals(TipoClasseESocial.S1210);
    }

    public static List<TipoClasseESocial> getTipoEventoPagamento() {
        List<TipoClasseESocial> eventos = Lists.newArrayList();
        eventos.add(TipoClasseESocial.S1200);
        eventos.add(TipoClasseESocial.S1202);
        eventos.add(TipoClasseESocial.S1207);
        eventos.add(TipoClasseESocial.S1210);
        return eventos;
    }
}
