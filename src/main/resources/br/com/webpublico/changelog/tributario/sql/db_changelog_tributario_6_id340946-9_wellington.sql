create or replace procedure proc_atualiza_situacao_valordivida (p_id_parcela in number)
as
    n_id_valordivida number;
cursor c_situacoes is (select spvd.situacaoparcela
                           from situacaoparcelavalordivida spvd
                                    inner join parcelavalordivida pvd on pvd.situacaoatual_id = spvd.id
                           where pvd.valordivida_id = n_id_valordivida);
    r_situacao c_situacoes%rowtype;
    b_situacao_pago int;
    b_situacao_aberto int;
    outra_situacao varchar(100);
    situacao_debito varchar(100);
begin
    b_situacao_pago := 0;
    b_situacao_aberto := 0;

select pvd.valordivida_id into n_id_valordivida
from parcelavalordivida pvd
where pvd.id = p_id_parcela;

open c_situacoes;
loop
fetch c_situacoes into r_situacao;
        exit when c_situacoes%notfound;

        if (r_situacao.situacaoparcela in ('BAIXADO', 'PAGO', 'PAGO_PARCELAMENTO', 'PAGO_REFIS', 'PAGO_SUBVENCAO',
                                           'BAIXADO_OUTRA_OPCAO')) then
            b_situacao_pago := 1;
else
            if (r_situacao.situacaoparcela = 'EM_ABERTO') then
                b_situacao_aberto := 1;
else
                outra_situacao := r_situacao.situacaoparcela;
end if;
end if;
end loop;
close c_situacoes;

if (b_situacao_pago = 1 and b_situacao_aberto = 0) then
        situacao_debito := 'PAGO';
else
        if (b_situacao_aberto = 1) then
            situacao_debito := 'EM_ABERTO';
else
            situacao_debito := outra_situacao;
end if;
end if;

update valordivida set situacao = situacao_debito where id = n_id_valordivida;
end;
