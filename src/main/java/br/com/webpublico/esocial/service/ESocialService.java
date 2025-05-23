package br.com.webpublico.esocial.service;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.esocial.comunicacao.enums.TipoArquivoESocial;
import br.com.webpublico.esocial.comunicacao.eventos.iniciais.*;
import br.com.webpublico.esocial.comunicacao.eventos.naoperiodicos.*;
import br.com.webpublico.esocial.comunicacao.eventos.periodicos.*;
import br.com.webpublico.esocial.domain.EmpregadorESocial;
import br.com.webpublico.esocial.dto.BuscarEventoDTO;
import br.com.webpublico.esocial.dto.EventoESocialDTO;
import br.com.webpublico.esocial.enums.SituacaoESocial;
import br.com.webpublico.util.AssistenteBarraProgresso;

import java.util.List;


public interface ESocialService {

    EventoS1000 enviarEventoS1000(EventoS1000 s1000);

    EventoS1010 enviarEventoS1010(EventoS1010 s1010);

    EventoS1020 enviarEventoS1020(EventoS1020 s1020);

    EventoS1005 enviarEventoS1005(EventoS1005 s1005);

    EventoS1030 enviarEventoS1030(EventoS1030 s1030);

    EventoS1035 enviarEventoS1035(EventoS1035 s1035);

    EventoS1040 enviarEventoS1040(EventoS1040 s1040);

    EventoS1050 enviarEventoS1050(EventoS1050 s1050);

    EventoS1070 enviarEventoS1070(EventoS1070 s1070);

    EventoS1300 enviarEventoS1300(EventoS1300 s1300);

    EventoS2190 enviarEventoS2190(EventoS2190 s2190);

    EventoS2200 enviarEventoS2200(EventoS2200 s2200, AssistenteBarraProgresso assistenteBarraProgresso, ContratoFP contratoFP);

    List<EventoESocialDTO> getEventosFolhaByEmpregador(EmpregadorESocial empregadorESocial, TipoArquivoESocial tipoArquivoESocial,
                                                       Integer mes, Integer ano, List<SituacaoESocial> situacoes, String numeroRecibo,
                                                       String idVinculo, String idXml, String cpfVinculo, List<String> idsExoneracao);

    EventoS2220 enviarEventoS2220(EventoS2220 s2220);


    EventoS1200 enviarEventoS1200(EventoS1200 s1200);

    EventoS1207 enviarEventoS1207(EventoS1207 s1207);

    EventoS1299 enviarEventoS1299(EventoS1299 s1299);

    EventoS1202 enviarEventoS1202(EventoS1202 s1202, VinculoFP vinculoFP);

    EventoS1295 enviarEventoS1295(EventoS1295 s1295);

    EventoS1280 enviarEventoS1280(EventoS1280 s1280);

    EventoS1298 enviarEventoS1298(EventoS1298 s1298);

    EventoS1210 enviarEventoS1210(EventoS1210 s1210);

    EventoS2205 enviarEventoS2205(EventoS2205 s2205);

    EventoS2206 enviarEventoS2206(EventoS2206 s2206);

    EventoS2210 enviarEventoS2210(EventoS2210 s2210);


    EventoS2230 enviarEventoS2230(EventoS2230 s2230);

    EventoS2231 enviarEventoS2231(EventoS2231 s2231);

    EventoS2240 enviarEventoS2240(EventoS2240 s2240);

    EventoS2250 enviarEventoS2250(EventoS2250 s2250);

    EventoS2298 enviarEventoS2298(EventoS2298 s2298);

    EventoS2299 enviarEventoS2299(EventoS2299 s2299);

    EventoS2300 enviarEventoS2300(EventoS2300 s2300);

    EventoS2306 enviarEventoS2306(EventoS2306 s2306);

    EventoS2399 enviarEventoS2399(EventoS2399 s2399);

    EventoS2400 enviarEventoS2400(EventoS2400 s2400);
    EventoS2405 enviarEventoS2405(EventoS2405 s2405);

    EventoS2410 enviarEventoS2410(EventoS2410 s2410);

    EventoS2418 enviarEventoS2418(EventoS2418 s2418);

    EventoS2416 enviarEventoS2416(EventoS2416 s2410);

    EventoS2420 enviarEventoS2420(EventoS2420 s2420);

    EventoS3000 enviarEventoS3000(EventoS3000 s3000);

    EventoS3000 enviarEventoS3000Simples(String idXMLEvento);

    void enviarS1000();

    EventoS1000 getEventoS1000(EmpregadorESocial empregador);

    EventoS1010 getEventoS1010(EmpregadorESocial empregador);

    EventoS1020 getEventoS1020(EmpregadorESocial empregador);

    EventoS1005 getEventoS1005(EmpregadorESocial empregador);

    EventoS1030 getEventoS1030(EmpregadorESocial empregador);

    EventoS1035 getEventoS1035(EmpregadorESocial empregador);

    EventoS1040 getEventoS1040(EmpregadorESocial empregador);

    EventoS1050 getEventoS1050(EmpregadorESocial empregador);

    EventoS1070 getEventoS1070(EmpregadorESocial empregador);

    EventoS1298 getEventoS1298(EmpregadorESocial empregador);

    EventoS1300 getEventoS1300(EmpregadorESocial empregador);

    EventoS2190 getEventoS2190(EmpregadorESocial empregador);

    EventoS2200 getEventoS2200(EmpregadorESocial empregador);

    EventoS2220 getEventoS2220(EmpregadorESocial empregador);

    EventoS1200 getEventoS1200(EmpregadorESocial empregador);
    EventoS1207 getEventoS1207(EmpregadorESocial empregador);

    EventoS1280 getEventoS1280(EmpregadorESocial empregador);

    EventoS1299 getEventoS1299(EmpregadorESocial empregador);

    EventoS1202 getEventoS1202(EmpregadorESocial empregador);

    EventoS1295 getEventoS1295(EmpregadorESocial empregador);

    EventoS1210 getEventoS1210(EmpregadorESocial empregador);

    EventoS2205 getEventoS2205(EmpregadorESocial empregador);

    EventoS2206 getEventoS2206(EmpregadorESocial empregador);

    EventoS2210 getEventoS2210(EmpregadorESocial empregador);

    EventoS2230 getEventoS2230(EmpregadorESocial empregador);

    EventoS2231 getEventoS2231(EmpregadorESocial empregador);

    EventoS2240 getEventoS2240(EmpregadorESocial empregador);
    EventoS2250 getEventoS2250(EmpregadorESocial empregador);

    EventoS2298 getEventoS2298(EmpregadorESocial empregador);

    EventoS2299 getEventoS2299(EmpregadorESocial empregador);

    EventoS2300 getEventoS2300(EmpregadorESocial empregador);

    EventoS2306 getEventoS2306(EmpregadorESocial empregador);

    EventoS2399 getEventoS2399(EmpregadorESocial empregador);

    EventoS2400 getEventoS2400(EmpregadorESocial empregador);

    EventoS2405 getEventoS2405(EmpregadorESocial empregador);

    EventoS2410 getEventoS2410(EmpregadorESocial empregador);

    EventoS2418 getEventoS2418(EmpregadorESocial empregador);

    EventoS2416 getEventoS2416(EmpregadorESocial empregador);

    EventoS2420 getEventoS2420(EmpregadorESocial empregador);

    EventoS3000 getEventoS3000(String idXMLEvento);

    EmpregadorESocial enviarEmpregador(EmpregadorESocial empregador);

    void enviarEventosIntegrados(List<Long> ids);

    EmpregadorESocial enviarEmpregador();

    EmpregadorESocial getEmpregadorPorCnpj(String cnpj);

    String getBaseUrl();

    EmpregadorESocial getNovoEmpregador();

    List<EventoESocialDTO> getEventosPorEmpregador(EmpregadorESocial empregadorESocial);

    List<EventoESocialDTO> getEventosPorEmpregadorAndTipoArquivo(EmpregadorESocial empregadorESocial, TipoArquivoESocial tipoArquivoESocial, SituacaoESocial situacao, int page, int pageSize);

    Integer getQuantidadeEventosPorEmpregadorAndTipoArquivo(EmpregadorESocial empregadorESocial, TipoArquivoESocial tipoArquivoESocial, SituacaoESocial situacao);

    List<EventoESocialDTO> getEventosPorIdEsocial(String idEsocial);

    List<EventoESocialDTO> getEventosPorEmpregadorAndIdEsocial(EmpregadorESocial empregadorESocial, String idEsocial);

    List<EventoESocialDTO> buscarEventosPorIdESocial(String idESocial, Boolean logESocial);

    List<EventoESocialDTO> buscarEventosESocialNaoSincronizados(Boolean logESocial, String cnpj) throws Exception;

    void enviarEventosAtualizarDescricao(List<EventoESocialDTO> eventos);

    List<EventoESocialDTO> getEventosPorIdEsocialOrCPF(BuscarEventoDTO dto);
}
