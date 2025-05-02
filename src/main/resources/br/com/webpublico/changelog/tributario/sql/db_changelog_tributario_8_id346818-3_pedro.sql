declare
    idparametrott number(19);
begin

    select id into idparametrott from parametrosott order by id desc fetch first 1 rows only;

    insert into digitovencimento (id, dia, mes, parametro_id, digito, tipodigitovencimento, parametroott_id)
    VALUES (HIBERNATE_SEQUENCE.nextval, 31, 10, null, 0, 'CERTIFICADO_VEICULO_OTT', idparametrott);
    insert into digitovencimento (id, dia, mes, parametro_id, digito, tipodigitovencimento, parametroott_id)
    VALUES (HIBERNATE_SEQUENCE.nextval, 27, 3, null, 1, 'CERTIFICADO_VEICULO_OTT', idparametrott);
    insert into digitovencimento (id, dia, mes, parametro_id, digito, tipodigitovencimento, parametroott_id)
    VALUES (HIBERNATE_SEQUENCE.nextval, 27, 3, null, 2, 'CERTIFICADO_VEICULO_OTT', idparametrott);
    insert into digitovencimento (id, dia, mes, parametro_id, digito, tipodigitovencimento, parametroott_id)
    VALUES (HIBERNATE_SEQUENCE.nextval, 30, 4, null, 3, 'CERTIFICADO_VEICULO_OTT', idparametrott);
    insert into digitovencimento (id, dia, mes, parametro_id, digito, tipodigitovencimento, parametroott_id)
    VALUES (HIBERNATE_SEQUENCE.nextval, 30, 4, null, 4, 'CERTIFICADO_VEICULO_OTT', idparametrott);
    insert into digitovencimento (id, dia, mes, parametro_id, digito, tipodigitovencimento, parametroott_id)
    VALUES (HIBERNATE_SEQUENCE.nextval, 29, 5, null, 5, 'CERTIFICADO_VEICULO_OTT', idparametrott);
    insert into digitovencimento (id, dia, mes, parametro_id, digito, tipodigitovencimento, parametroott_id)
    VALUES (HIBERNATE_SEQUENCE.nextval, 28, 6, null, 6, 'CERTIFICADO_VEICULO_OTT', idparametrott);
    insert into digitovencimento (id, dia, mes, parametro_id, digito, tipodigitovencimento, parametroott_id)
    VALUES (HIBERNATE_SEQUENCE.nextval, 31, 7, null, 7, 'CERTIFICADO_VEICULO_OTT', idparametrott);
    insert into digitovencimento (id, dia, mes, parametro_id, digito, tipodigitovencimento, parametroott_id)
    VALUES (HIBERNATE_SEQUENCE.nextval, 30, 8, null, 8, 'CERTIFICADO_VEICULO_OTT', idparametrott);
    insert into digitovencimento (id, dia, mes, parametro_id, digito, tipodigitovencimento, parametroott_id)
    VALUES (HIBERNATE_SEQUENCE.nextval, 30, 9, null, 9, 'CERTIFICADO_VEICULO_OTT', idparametrott);

    insert into digitovencimento (id, dia, mes, parametro_id, digito, tipodigitovencimento, parametroott_id)
    VALUES (HIBERNATE_SEQUENCE.nextval, 31, 10, null, 0, 'VISTORIA_VEICULO_OTT', idparametrott);
    insert into digitovencimento (id, dia, mes, parametro_id, digito, tipodigitovencimento, parametroott_id)
    VALUES (HIBERNATE_SEQUENCE.nextval, 27, 3, null, 1, 'VISTORIA_VEICULO_OTT', idparametrott);
    insert into digitovencimento (id, dia, mes, parametro_id, digito, tipodigitovencimento, parametroott_id)
    VALUES (HIBERNATE_SEQUENCE.nextval, 27, 3, null, 2, 'VISTORIA_VEICULO_OTT', idparametrott);
    insert into digitovencimento (id, dia, mes, parametro_id, digito, tipodigitovencimento, parametroott_id)
    VALUES (HIBERNATE_SEQUENCE.nextval, 30, 4, null, 3, 'VISTORIA_VEICULO_OTT', idparametrott);
    insert into digitovencimento (id, dia, mes, parametro_id, digito, tipodigitovencimento, parametroott_id)
    VALUES (HIBERNATE_SEQUENCE.nextval, 30, 4, null, 4, 'VISTORIA_VEICULO_OTT', idparametrott);
    insert into digitovencimento (id, dia, mes, parametro_id, digito, tipodigitovencimento, parametroott_id)
    VALUES (HIBERNATE_SEQUENCE.nextval, 29, 5, null, 5, 'VISTORIA_VEICULO_OTT', idparametrott);
    insert into digitovencimento (id, dia, mes, parametro_id, digito, tipodigitovencimento, parametroott_id)
    VALUES (HIBERNATE_SEQUENCE.nextval, 28, 6, null, 6, 'VISTORIA_VEICULO_OTT', idparametrott);
    insert into digitovencimento (id, dia, mes, parametro_id, digito, tipodigitovencimento, parametroott_id)
    VALUES (HIBERNATE_SEQUENCE.nextval, 31, 7, null, 7, 'VISTORIA_VEICULO_OTT', idparametrott);
    insert into digitovencimento (id, dia, mes, parametro_id, digito, tipodigitovencimento, parametroott_id)
    VALUES (HIBERNATE_SEQUENCE.nextval, 30, 8, null, 8, 'VISTORIA_VEICULO_OTT', idparametrott);
    insert into digitovencimento (id, dia, mes, parametro_id, digito, tipodigitovencimento, parametroott_id)
    VALUES (HIBERNATE_SEQUENCE.nextval, 30, 9, null, 9, 'VISTORIA_VEICULO_OTT', idparametrott);

end;
