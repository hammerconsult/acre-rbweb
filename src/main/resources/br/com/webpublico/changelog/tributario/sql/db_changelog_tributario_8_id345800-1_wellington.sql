    declare
    id_nota numeric;
        id_ultima_situacao_em_aberto numeric;
        pagou_nota int;
    cursor debitos_duplicados is select spvd.id, spvd.parcela_id, spvd.situacaoparcela, spvd.referencia
                                 from nfsavulsa nfa
                                          inner join exercicio e on e.id = nfa.exercicio_id
                                          inner join calculonfsavulsa cnfa on cnfa.nfsavulsa_id = nfa.id
                                          inner join valordivida vd on vd.calculo_id = cnfa.id
                                          inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id
                                          inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id
                                 where nfa.id = id_nota
                                 order by nfa.id, pvd.id;
    r_debitos_duplicados debitos_duplicados%ROWTYPE;
    begin
    for notas_duplicadas in (select nfa.id, e.ano, nfa.numero
                                    from nfsavulsa nfa
                                   inner join exercicio e on e.id = nfa.exercicio_id
                                 where (select count(1)
                                        from calculonfsavulsa scnfa
                                        where scnfa.nfsavulsa_id = nfa.id) > 1
                                   and exists (select 1
                                               from parcelavalordivida pvd2
                                                        inner join situacaoparcelavalordivida spvd2 on spvd2.id = pvd2.situacaoatual_id
                                                        inner join valordivida vd2 on vd2.id = pvd2.valordivida_id
                                                        inner join calculonfsavulsa cnfa2 on cnfa2.id = vd2.calculo_id
                                               where cnfa2.nfsavulsa_id = nfa.id
                                                 and spvd2.situacaoparcela = 'EM_ABERTO'))
            loop
                id_nota := notas_duplicadas.id;
                pagou_nota := 0;
                id_ultima_situacao_em_aberto := 0;
    open debitos_duplicados;
    loop
    fetch debitos_duplicados into r_debitos_duplicados;
                    exit when debitos_duplicados%notfound;
                    if (r_debitos_duplicados.situacaoparcela = 'PAGO') then
                        pagou_nota := 1;
    end if;
                    if (r_debitos_duplicados.situacaoparcela = 'EM_ABERTO' and r_debitos_duplicados.id > id_ultima_situacao_em_aberto) then
                        id_ultima_situacao_em_aberto := r_debitos_duplicados.id;
    end if;
    end loop;
    close debitos_duplicados;
    if (pagou_nota = 1) then
                    id_ultima_situacao_em_aberto := 0;
    end if;
    open debitos_duplicados;
    loop
    fetch debitos_duplicados into r_debitos_duplicados;
                    exit when debitos_duplicados%notfound;
    update situacaoparcelavalordivida set referencia = 'Exercício: '||notas_duplicadas.ano
        ||' Nota: '||lpad(notas_duplicadas.numero, 8, '0')
    where id = r_debitos_duplicados.id;
    if (r_debitos_duplicados.situacaoparcela = 'EM_ABERTO' and
                        r_debitos_duplicados.id != id_ultima_situacao_em_aberto) then
                        insert into situacaoparcelavalordivida (id, datalancamento, situacaoparcela, parcela_id,
                                                                saldo, referencia)
                        values (hibernate_sequence.nextval, current_date, 'CANCELAMENTO', r_debitos_duplicados.parcela_id,
                                0, r_debitos_duplicados.referencia);
    end if;
    end loop;
    close debitos_duplicados;
    end loop;
    end;
