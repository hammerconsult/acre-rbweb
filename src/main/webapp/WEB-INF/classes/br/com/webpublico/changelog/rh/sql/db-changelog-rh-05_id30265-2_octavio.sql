declare numid number;
begin
    -- 545067 - ADELAIDE HERCULANO DOS SANTOS
    select hibernate_sequence.nextval into numid from ( select level from dual connect by level <= 1);
    insert into basecargo (id, iniciovigencia, baseperiodoaquisitivo_id, cargo_id)
    values (numid, to_date('01/01/1986', 'dd/MM/yyyy'), 6190048, 638886102);
    insert into periodoaquisitivofl (id, iniciovigencia, finalvigencia, contratofp_id, status, quantidadedias, saldo, saldodedireito, basecargo_id)
    values (hibernate_sequence.nextval, to_date('30/05/2008', 'dd/MM/yyyy'), (add_months(to_date('30/05/2008', 'dd/MM/yyyy'), 60)), 638985357, 'NAO_CONCEDIDO', 1826, 90, 90, numId);

    -- 545237 - CLENILDE SILVA DOS SANTOS
    select hibernate_sequence.nextval into numid from ( select level from dual connect by level <= 1);
    insert into basecargo (id, iniciovigencia, baseperiodoaquisitivo_id, cargo_id)
    values (numid, to_date('01/01/1986', 'dd/MM/yyyy'), 6190048, 638886102);
    insert into periodoaquisitivofl (id, iniciovigencia, finalvigencia, contratofp_id, status, quantidadedias, saldo, saldodedireito, basecargo_id)
    values (hibernate_sequence.nextval, to_date('30/05/2008', 'dd/MM/yyyy'), (add_months(to_date('30/05/2008', 'dd/MM/yyyy'), 60)), 638986809, 'NAO_CONCEDIDO', 1826, 90, 90, numid);

    -- 545159 - ANTONIO CARLOS DOS SANTOS
    select hibernate_sequence.nextval into numid from ( select level from dual connect by level <= 1);
    insert into basecargo (id, iniciovigencia, baseperiodoaquisitivo_id, cargo_id)
    values (numid, to_date('01/01/1986', 'dd/MM/yyyy'), 6190048, 693784505);
    insert into periodoaquisitivofl (id, iniciovigencia, finalvigencia, contratofp_id, status, quantidadedias, saldo, saldodedireito, basecargo_id)
    values (hibernate_sequence.nextval, to_date('11/07/2008', 'dd/MM/yyyy'), (add_months(to_date('11/07/2008', 'dd/MM/yyyy'), 60)), 694223661, 'NAO_CONCEDIDO', 1826, 90, 90, numid);

    -- 545207 - PATRICIA DE SOUZA RUFINO
    select hibernate_sequence.nextval into numid from ( select level from dual connect by level <= 1);
    insert into basecargo (id, iniciovigencia, baseperiodoaquisitivo_id, cargo_id)
    values (numid, to_date('01/01/1986', 'dd/MM/yyyy'), 6190048, 638884662);
    insert into periodoaquisitivofl (id, iniciovigencia, finalvigencia, contratofp_id, status, quantidadedias, saldo, saldodedireito, basecargo_id)
    values (hibernate_sequence.nextval, to_date('05/08/2008', 'dd/MM/yyyy'), (add_months(to_date('05/08/2008', 'dd/MM/yyyy'), 60)), 638986559, 'NAO_CONCEDIDO', 1826, 90, 90, numid);

    -- 545210 - POLIANA LIMA LEMOS
    select hibernate_sequence.nextval into numid from ( select level from dual connect by level <= 1);
    insert into basecargo (id, iniciovigencia, baseperiodoaquisitivo_id, cargo_id)
    values (numid, to_date('01/01/1986', 'dd/MM/yyyy'), 6190048, 638886102);
    insert into periodoaquisitivofl (id, iniciovigencia, finalvigencia, contratofp_id, status, quantidadedias, saldo, saldodedireito, basecargo_id)
    values (hibernate_sequence.nextval, to_date('08/08/2008', 'dd/MM/yyyy'), (add_months(to_date('08/08/2008', 'dd/MM/yyyy'), 60)), 638986587, 'NAO_CONCEDIDO', 1826, 90, 90, numid);

    -- 545168 - RECILENE SANTOS DO NASCIMENTO
    select hibernate_sequence.nextval into numid from ( select level from dual connect by level <= 1);
    insert into basecargo (id, iniciovigencia, baseperiodoaquisitivo_id, cargo_id)
    values (numid, to_date('01/01/1986', 'dd/MM/yyyy'), 6190048, 638886102);
    insert into periodoaquisitivofl (id, iniciovigencia, finalvigencia, contratofp_id, status, quantidadedias, saldo, saldodedireito, basecargo_id)
    values (hibernate_sequence.nextval, to_date('08/08/2008', 'dd/MM/yyyy'), (add_months(to_date('08/08/2008', 'dd/MM/yyyy'), 60)), 638986241, 'NAO_CONCEDIDO', 1826, 90, 90, numid);

    -- 545209 - PAULO RENATO NORONHA DANTAS
    select hibernate_sequence.nextval into numid from ( select level from dual connect by level <= 1);
    insert into basecargo (id, iniciovigencia, baseperiodoaquisitivo_id, cargo_id)
    values (numid, to_date('01/01/1986', 'dd/MM/yyyy'), 6190048, 638884362);
    insert into periodoaquisitivofl (id, iniciovigencia, finalvigencia, contratofp_id, status, quantidadedias, saldo, saldodedireito, basecargo_id)
    values (hibernate_sequence.nextval, to_date('08/08/2008', 'dd/MM/yyyy'), (add_months(to_date('08/08/2008', 'dd/MM/yyyy'), 60)), 638986580, 'NAO_CONCEDIDO', 1826, 90, 90, numid);

    -- 545174 - RAMIRO DA SILVA LUNA
    select hibernate_sequence.nextval into numid from ( select level from dual connect by level <= 1);
    insert into basecargo (id, iniciovigencia, baseperiodoaquisitivo_id, cargo_id)
    values (numid, to_date('01/01/1986', 'dd/MM/yyyy'), 6190048, 638886102);
    insert into periodoaquisitivofl (id, iniciovigencia, finalvigencia, contratofp_id, status, quantidadedias, saldo, saldodedireito, basecargo_id)
    values (hibernate_sequence.nextval, to_date('08/08/2008', 'dd/MM/yyyy'), (add_months(to_date('08/08/2008', 'dd/MM/yyyy'), 60)), 638986289, 'NAO_CONCEDIDO', 1826, 90, 90, numid);

    -- 545221 - EZINETE ALNERT
    select hibernate_sequence.nextval into numid from ( select level from dual connect by level <= 1);
    insert into basecargo (id, iniciovigencia, baseperiodoaquisitivo_id, cargo_id)
    values (numid, to_date('01/01/1986', 'dd/MM/yyyy'), 6190048, 638886102);
    insert into periodoaquisitivofl (id, iniciovigencia, finalvigencia, contratofp_id, status, quantidadedias, saldo, saldodedireito, basecargo_id)
    values (hibernate_sequence.nextval, to_date('15/08/2008', 'dd/MM/yyyy'), (add_months(to_date('15/08/2008', 'dd/MM/yyyy'), 60)), 638986673, 'NAO_CONCEDIDO', 1826, 90, 90, numid);

    -- 537923 - RICHARLE AGUIAR DA SILVA
    select hibernate_sequence.nextval into numid from ( select level from dual connect by level <= 1);
    insert into basecargo (id, iniciovigencia, baseperiodoaquisitivo_id, cargo_id)
    values (numid, to_date('01/01/1986', 'dd/MM/yyyy'), 6190048, 638886102);
    insert into periodoaquisitivofl (id, iniciovigencia, finalvigencia, contratofp_id, status, quantidadedias, saldo, saldodedireito, basecargo_id)
    values (hibernate_sequence.nextval, to_date('28/08/2008', 'dd/MM/yyyy'), (add_months(to_date('28/08/2008', 'dd/MM/yyyy'), 60)), 638940911, 'NAO_CONCEDIDO', 1826, 90, 90, numid);

    -- 545117 - VIVIANE DA CONCEIÇAO MACEDO
    select hibernate_sequence.nextval into numid from ( select level from dual connect by level <= 1);
    insert into basecargo (id, iniciovigencia, baseperiodoaquisitivo_id, cargo_id)
    values (numid, to_date('01/01/1986', 'dd/MM/yyyy'), 6190048, 638886102);
    insert into periodoaquisitivofl (id, iniciovigencia, finalvigencia, contratofp_id, status, quantidadedias, saldo, saldodedireito, basecargo_id)
    values (hibernate_sequence.nextval, to_date('23/10/2008', 'dd/MM/yyyy'), (add_months(to_date('23/10/2008', 'dd/MM/yyyy'), 60)), 638985755, 'NAO_CONCEDIDO', 1826, 90, 90, numid);

    -- 543330 - FRANCISCO BARROS DE ARAGAO
    select hibernate_sequence.nextval into numid from ( select level from dual connect by level <= 1);
    insert into basecargo (id, iniciovigencia, baseperiodoaquisitivo_id, cargo_id)
    values (numid, to_date('01/01/1986', 'dd/MM/yyyy'), 6190048, 638886102);
    insert into periodoaquisitivofl (id, iniciovigencia, finalvigencia, contratofp_id, status, quantidadedias, saldo, saldodedireito, basecargo_id)
    values (hibernate_sequence.nextval, to_date('09/08/2010', 'dd/MM/yyyy'), (add_months(to_date('09/08/2010', 'dd/MM/yyyy'), 60)), 638966933, 'NAO_CONCEDIDO', 1826, 90, 90, numid);

    -- 545084 - NICELLE KAREN ARAUJO MACAMBIRA
    select hibernate_sequence.nextval into numid from ( select level from dual connect by level <= 1);
    insert into basecargo (id, iniciovigencia, baseperiodoaquisitivo_id, cargo_id)
    values (numid, to_date('01/01/1986', 'dd/MM/yyyy'), 6190048, 638886102);
    insert into periodoaquisitivofl (id, iniciovigencia, finalvigencia, contratofp_id, status, quantidadedias, saldo, saldodedireito, basecargo_id)
    values (hibernate_sequence.nextval, to_date('09/07/2010', 'dd/MM/yyyy'), (add_months(to_date('09/07/2010', 'dd/MM/yyyy'), 60)), 638985495, 'NAO_CONCEDIDO', 1826, 90, 90, numid);

    -- 543285 - NADEZIR DA SILVA DE LIMA
    select hibernate_sequence.nextval into numid from ( select level from dual connect by level <= 1);
    insert into basecargo (id, iniciovigencia, baseperiodoaquisitivo_id, cargo_id)
    values (numid, to_date('01/01/1986', 'dd/MM/yyyy'), 6190048, 638886102);
    insert into periodoaquisitivofl (id, iniciovigencia, finalvigencia, contratofp_id, status, quantidadedias, saldo, saldodedireito, basecargo_id)
    values (hibernate_sequence.nextval, to_date('09/08/2010', 'dd/MM/yyyy'), (add_months(to_date('09/08/2010', 'dd/MM/yyyy'), 60)), 638966302, 'NAO_CONCEDIDO', 1826, 90, 90, numid);

    -- 543338 - MARIA CACILDA DUARTE
    select hibernate_sequence.nextval into numid from ( select level from dual connect by level <= 1);
    insert into basecargo (id, iniciovigencia, baseperiodoaquisitivo_id, cargo_id)
    values (numid, to_date('01/01/1986', 'dd/MM/yyyy'), 6190048, 638886102);
    insert into periodoaquisitivofl (id, iniciovigencia, finalvigencia, contratofp_id, status, quantidadedias, saldo, saldodedireito, basecargo_id)
    values (hibernate_sequence.nextval, to_date('17/08/2010', 'dd/MM/yyyy'), (add_months(to_date('17/08/2010', 'dd/MM/yyyy'), 60)), 638967023, 'NAO_CONCEDIDO', 1826, 90, 90, numid);

    -- 545114 - ERLENILCE LOPES FERREIRA
    select hibernate_sequence.nextval into numid from ( select level from dual connect by level <= 1);
    insert into basecargo (id, iniciovigencia, baseperiodoaquisitivo_id, cargo_id)
    values (numid, to_date('01/01/1986', 'dd/MM/yyyy'), 6190048, 638886102);
    insert into periodoaquisitivofl (id, iniciovigencia, finalvigencia, contratofp_id, status, quantidadedias, saldo, saldodedireito, basecargo_id)
    values (hibernate_sequence.nextval, to_date('21/09/2010', 'dd/MM/yyyy'), (add_months(to_date('21/09/2010', 'dd/MM/yyyy'), 60)), 638985732, 'NAO_CONCEDIDO', 1826, 90, 90, numid);

    -- 537303 - MARIA DE NAZARE NASCIMENTO DOS SANTOS
    select hibernate_sequence.nextval into numid from ( select level from dual connect by level <= 1);
    insert into basecargo (id, iniciovigencia, baseperiodoaquisitivo_id, cargo_id)
    values (numid, to_date('01/01/1986', 'dd/MM/yyyy'), 6190048, 638886872);
    insert into periodoaquisitivofl (id, iniciovigencia, finalvigencia, contratofp_id, status, quantidadedias, saldo, saldodedireito, basecargo_id)
    values (hibernate_sequence.nextval, to_date('24/06/2015', 'dd/MM/yyyy'), (add_months(to_date('24/06/2015', 'dd/MM/yyyy'), 60)), 638938387, 'NAO_CONCEDIDO', 1826, 90, 90, numid);

    -- 543423 - MARIA EDITE CAVALCANTE DA SILVA
    select hibernate_sequence.nextval into numid from ( select level from dual connect by level <= 1);
    insert into basecargo (id, iniciovigencia, baseperiodoaquisitivo_id, cargo_id)
    values (numid, to_date('01/01/1986', 'dd/MM/yyyy'), 6190048, 638886087);
    insert into periodoaquisitivofl (id, iniciovigencia, finalvigencia, contratofp_id, status, quantidadedias, saldo, saldodedireito, basecargo_id)
    values (hibernate_sequence.nextval, to_date('16/08/2010', 'dd/MM/yyyy'), (add_months(to_date('16/08/2010', 'dd/MM/yyyy'), 60)), 638968140, 'NAO_CONCEDIDO', 1826, 90, 90, numid);

    -- 271527 - DENISE PINTO DE LIMA
    select hibernate_sequence.nextval into numid from ( select level from dual connect by level <= 1);
    insert into basecargo (id, iniciovigencia, baseperiodoaquisitivo_id, cargo_id)
    values (numid, to_date('01/01/1986', 'dd/MM/yyyy'), 6190048, 638886087);
    insert into periodoaquisitivofl (id, iniciovigencia, finalvigencia, contratofp_id, status, quantidadedias, saldo, saldodedireito, basecargo_id)
    values (hibernate_sequence.nextval, to_date('20/08/2010', 'dd/MM/yyyy'), (add_months(to_date('20/08/2010', 'dd/MM/yyyy'), 60)), 638930552, 'NAO_CONCEDIDO', 1826, 90, 90, numid);
end;

