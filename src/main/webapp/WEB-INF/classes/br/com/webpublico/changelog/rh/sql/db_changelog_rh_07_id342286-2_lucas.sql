update contratofp cont
set cont.TIPOVINCULOSICAP = (select case
                                        when (moda.codigo = 1 and regime.codigo = 1) then 'EMPREGADO_PUBLICO_CLT'
                                        when (moda.codigo = 1 and regime.codigo = 2) then
                                            coalesce((select 'SERVIDOR_EFETIVO_CARGO_COMISSAO'
                                                      from cargoconfianca cc
                                                      where cc.contratofp_id = cont.id
                                                        and cc.iniciovigencia = (select max(ccfinal.iniciovigencia)
                                                                                 from cargoconfianca ccfinal
                                                                                 where ccfinal.contratofp_id = cont.id)),
                                                     case moda.codigoVinculoSicap
                                                         when 1 then 'SERVIDOR_CARGO_EFETIVO'
                                                         when 2 then 'SERVIDOR_CARGO_TEMPORARIO'
                                                         when 3 then 'SERVIDOR_CARGO_COMISSIONADO'
                                                         when 4 then 'SERVIDOR_EFETIVO_CARGO_COMISSAO'
                                                         when 5 then 'SERVIDOR_EXERCENDO_MANDATO_ELETIVO'
                                                         when 6 then 'EMPREGADO_PUBLICO_CLT'
                                                         when 7 then 'SERVIDOR_ESTAVEL_NAO_EFETIVO_NA_FORMA_DO_ART_19_DO_ADCT'
                                                         when 8 then 'SERVIDOR_EFETIVO_CARGO_COMISSAO_CEDIDO_DE_OUTRO_ORGAO'
                                                         when 9 then 'EMPREGADO_CLT_CONTRATO_DE_GESTAO'
                                                         when 10 then 'ESTAGIARIOS'
                                                         when 11 then 'CONTRIBUINTE_INDIVIDUAL_E_AUTONOMO'
                                                         end)
                                        else case moda.codigoVinculoSicap
                                                 when 1 then 'SERVIDOR_CARGO_EFETIVO'
                                                 when 2 then 'SERVIDOR_CARGO_TEMPORARIO'
                                                 when 3 then 'SERVIDOR_CARGO_COMISSIONADO'
                                                 when 4 then 'SERVIDOR_EFETIVO_CARGO_COMISSAO'
                                                 when 5 then 'SERVIDOR_EXERCENDO_MANDATO_ELETIVO'
                                                 when 6 then 'EMPREGADO_PUBLICO_CLT'
                                                 when 7 then 'SERVIDOR_ESTAVEL_NAO_EFETIVO_NA_FORMA_DO_ART_19_DO_ADCT'
                                                 when 8 then 'SERVIDOR_EFETIVO_CARGO_COMISSAO_CEDIDO_DE_OUTRO_ORGAO'
                                                 when 9 then 'EMPREGADO_CLT_CONTRATO_DE_GESTAO'
                                                 when 10 then 'ESTAGIARIOS'
                                                 when 11 then 'CONTRIBUINTE_INDIVIDUAL_E_AUTONOMO'
                                            end end
                             from CONTRATOFP c
                                      inner join MODALIDADECONTRATOFP moda on
                                 c.MODALIDADECONTRATOFP_ID = moda.ID
                                      inner join tipoRegime regime on c.TIPOREGIME_ID = regime.ID
                             where c.id = cont.id)
where cont.id not in (select e.id from ESTAGIARIO e);

update contratofp cont
set cont.TIPOVINCULOSICAP = 'ESTAGIARIOS'
where cont.id in (select e.id from ESTAGIARIO e);
