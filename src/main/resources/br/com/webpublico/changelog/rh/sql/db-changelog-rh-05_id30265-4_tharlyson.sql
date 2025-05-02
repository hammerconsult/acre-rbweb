declare numid number;
begin
    -- 545171 - RUTHELENE LIMA DE FREITAS
    select hibernate_sequence.nextval into numid from ( select level from dual connect by level <= 1);
    insert into basecargo (id, iniciovigencia, baseperiodoaquisitivo_id, cargo_id)
    values (numid, to_date('01/01/1986', 'dd/MM/yyyy'), 6190048, 638886102);
    insert into periodoaquisitivofl (id, iniciovigencia, finalvigencia, contratofp_id, status, quantidadedias, saldo, saldodedireito, basecargo_id)
    values (hibernate_sequence.nextval, to_date('08/08/2008', 'dd/MM/yyyy'), (add_months(to_date('08/08/2008', 'dd/MM/yyyy'), 60)), 638986265, 'NAO_CONCEDIDO', 1826, 90, 90, numId);
end;
