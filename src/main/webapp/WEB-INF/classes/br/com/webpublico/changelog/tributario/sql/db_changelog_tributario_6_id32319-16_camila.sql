declare
    cursor cnaes is select *
                    from cnae;
    cursor sessaoatividade is select *
                              from sessaoatividade
                              order by sessao;
BEGIN
    for sessao in sessaoatividade
        loop
            for cnae in cnaes
                loop
                    if sessao.sessao = 'A' and (substr(cnae.CODIGOCNAE, 0, 2) in ('01', '02', '03')) then
                        insert into sessaoatividadecnae
                        values (HIBERNATE_SEQUENCE.nextval, sessao.id, cnae.ID);
                    elsif sessao.sessao = 'B' and (substr(cnae.CODIGOCNAE, 0, 2) in ('05', '06', '07', '08', '09')) then
                        insert into sessaoatividadecnae
                        values (HIBERNATE_SEQUENCE.nextval, sessao.id, cnae.ID);
                    elsif sessao.sessao = 'C' and (substr(cnae.CODIGOCNAE, 0, 2) in
                                                   ('10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20',
                                                    '21', '22', '23', '24', '25', '26', '27', '27', '27', '28', '29', '30',
                                                    '31', '32', '33')) then
                        insert into sessaoatividadecnae
                        values (HIBERNATE_SEQUENCE.nextval, sessao.id, cnae.ID);
                    elsif sessao.sessao = 'D' and (substr(cnae.CODIGOCNAE, 0, 2) in ('35')) then
                        insert into sessaoatividadecnae
                        values (HIBERNATE_SEQUENCE.nextval, sessao.id, cnae.ID);
                    elsif sessao.sessao = 'E' and (substr(cnae.CODIGOCNAE, 0, 2) in ('36', '37', '38', '39')) then
                        insert into sessaoatividadecnae
                        values (HIBERNATE_SEQUENCE.nextval, sessao.id, cnae.ID);
                    elsif sessao.sessao = 'F' and (substr(cnae.CODIGOCNAE, 0, 2) in ('41', '42', '43')) then
                        insert into sessaoatividadecnae
                        values (HIBERNATE_SEQUENCE.nextval, sessao.id, cnae.ID);
                    elsif sessao.sessao = 'G' and (substr(cnae.CODIGOCNAE, 0, 2) in ('45', '46', '47')) then
                        insert into sessaoatividadecnae
                        values (HIBERNATE_SEQUENCE.nextval, sessao.id, cnae.ID);
                    elsif sessao.sessao = 'H' and (substr(cnae.CODIGOCNAE, 0, 2) in ('49', '50', '51', '52', '53')) then
                        insert into sessaoatividadecnae
                        values (HIBERNATE_SEQUENCE.nextval, sessao.id, cnae.ID);
                    elsif sessao.sessao = 'I' and (substr(cnae.CODIGOCNAE, 0, 2) in ('55', '56')) then
                        insert into sessaoatividadecnae
                        values (HIBERNATE_SEQUENCE.nextval, sessao.id, cnae.ID);
                    elsif sessao.sessao = 'J' and
                          (substr(cnae.CODIGOCNAE, 0, 2) in ('58', '59', '60', '61', '62', '63')) then
                        insert into sessaoatividadecnae
                        values (HIBERNATE_SEQUENCE.nextval, sessao.id, cnae.ID);
                    elsif sessao.sessao = 'K' and (substr(cnae.CODIGOCNAE, 0, 2) in ('64', '65', '66')) then
                        insert into sessaoatividadecnae
                        values (HIBERNATE_SEQUENCE.nextval, sessao.id, cnae.ID);
                    elsif sessao.sessao = 'L' and (substr(cnae.CODIGOCNAE, 0, 2) in ('68')) then
                        insert into sessaoatividadecnae
                        values (HIBERNATE_SEQUENCE.nextval, sessao.id, cnae.ID);
                    elsif sessao.sessao = 'M' and
                          (substr(cnae.CODIGOCNAE, 0, 2) in ('69', '70', '71', '72', '73', '74', '75')) then
                        insert into sessaoatividadecnae
                        values (HIBERNATE_SEQUENCE.nextval, sessao.id, cnae.ID);
                    elsif sessao.sessao = 'N' and
                          (substr(cnae.CODIGOCNAE, 0, 2) in ('77', '78', '79', '80', '81', '82')) then
                        insert into sessaoatividadecnae
                        values (HIBERNATE_SEQUENCE.nextval, sessao.id, cnae.ID);
                    elsif sessao.sessao = 'O' and (substr(cnae.CODIGOCNAE, 0, 2) in ('84')) then
                        insert into sessaoatividadecnae
                        values (HIBERNATE_SEQUENCE.nextval, sessao.id, cnae.ID);
                    elsif sessao.sessao = 'P' and (substr(cnae.CODIGOCNAE, 0, 2) in ('85')) then
                        insert into sessaoatividadecnae
                        values (HIBERNATE_SEQUENCE.nextval, sessao.id, cnae.ID);
                    elsif sessao.sessao = 'Q' and (substr(cnae.CODIGOCNAE, 0, 2) in ('86', '87', '88')) then
                        insert into sessaoatividadecnae
                        values (HIBERNATE_SEQUENCE.nextval, sessao.id, cnae.ID);
                    elsif sessao.sessao = 'R' and (substr(cnae.CODIGOCNAE, 0, 2) in ('90', '91', '92', '93')) then
                        insert into sessaoatividadecnae
                        values (HIBERNATE_SEQUENCE.nextval, sessao.id, cnae.ID);
                    elsif sessao.sessao = 'S' and (substr(cnae.CODIGOCNAE, 0, 2) in ('94', '95', '96')) then
                        insert into sessaoatividadecnae
                        values (HIBERNATE_SEQUENCE.nextval, sessao.id, cnae.ID);
                    elsif sessao.sessao = 'T' and (substr(cnae.CODIGOCNAE, 0, 2) in ('97')) then
                        insert into sessaoatividadecnae
                        values (HIBERNATE_SEQUENCE.nextval, sessao.id, cnae.ID);
                    elsif sessao.sessao = 'U' and (substr(cnae.CODIGOCNAE, 0, 2) in ('99')) then
                        insert into sessaoatividadecnae
                        values (HIBERNATE_SEQUENCE.nextval, sessao.id, cnae.ID);
                    end if;
                end loop;
        end loop;
END;
